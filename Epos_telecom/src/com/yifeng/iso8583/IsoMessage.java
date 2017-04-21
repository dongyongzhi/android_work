 
package com.yifeng.iso8583;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.yifeng.iso8583.schema.Field;

/**
 * is 8583 协议包
 * @author qin
 * 
 * 2012-11-13
 */
public class IsoMessage {

	private final static int MABLEN = 8;
	/** 信息类型 */
    private int type;
   
    /** 128个位 */
    @SuppressWarnings("rawtypes")
	private IsoValue[] fields = new IsoValue[129];
    private String isoHeader;
    private boolean forceb2;
    
    private final Map<Integer,Field> config = IsoConfig.getConfig();  //获取 iso 配置文件
    
    /**参与mac计算的域配置信息 */
    private final Map<Integer,Integer> macConfig = IsoConfig.getMacConfig(); 
    
    /**，从消息类型（MTI）到63域之间的部分构成MAC ELEMEMENT BLOCK（MAB）。*/
    private byte[][] MAB = new byte[macConfig.size()+1][8];  //位图要参数运算
    /**参与mac 计算域  异域结果*/
    private byte[] macBlock = new byte[MABLEN];

    public IsoMessage() {
    	forceb2 = false;
    }

    IsoMessage(String header) {
    	isoHeader = header;
    }

    IsoMessage(String header,int type){
    	this.isoHeader = header;
    	this.type = type;
    }
    
    public void setForceSecondaryBitmap(boolean flag) {
    	forceb2 = flag;
    }
    public boolean getForceSecondaryBitmap() {
    	return forceb2;
    }

    public void setIsoHeader(String value) {
    	isoHeader = value;
    }
    
    public String getIsoHeader() {
    	return isoHeader;
    }

    /**设置 iso 信息类型   0x200, 0x210, 0x400, 0x410, 0x800, 0x810. */
    public void setType(int value) {
    	type = value;
    }
    /** Returns the ISO message type. */
    public int getType() {
    	return type;
    }

    /**
     * 获取域返回值
     * @param <T>     返回类型  
     * @param field   2-128 个域
     * @return
     */
    public <T> T getObjectValue(int field) {
    	@SuppressWarnings("unchecked")
    	IsoValue<T> v = fields[field];
    	return v == null ? null : v.getValue();
    }

    /** 返回域信息  第一个域 为 2 */
	@SuppressWarnings("unchecked")
    public <T> IsoValue<T> getField(int field) {
    	return fields[field];
    }

    /**
     * 添加域
     * @param index   域索引
     * @param field   域信息 
     * @return
     */
    public IsoMessage setField(int index, IsoValue<?> field) {
    	if (index < 2 || index > 128) {
    		throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
    	}
    	fields[index] = field;
    	return this;
    }

    public IsoMessage setFields(Map<Integer, IsoValue<?>> values) {
    	for (Map.Entry<Integer, IsoValue<?>> e : values.entrySet()) {
    		setField(e.getKey(), e.getValue());
    	}
    	return this;
    }
    /**
     * 设置域
     * @param <T>     值类型
     * @param index   域
     * @param value   值
     * @return
     */
    public <T> IsoMessage setValue(int index,T value){
    	Field field = config.get(index);
    	if(field==null)
    		throw new IllegalArgumentException(String.format("is8583.xml not config ",index));
    	return setValue(index,value,null,field.getType(),field.getLength());
    }
    
    /**
     * 设置域 
     * @param <T>    值类型
     * @param index  域
     * @param value  值 byte[]  
     * @param length byte[] 长度
     * @return
     */
    public <T> IsoMessage setValue(int index,byte[] value,int length){
    	Field field = config.get(index);
    	if(field==null)
    		throw new IllegalArgumentException(String.format("is8583.xml not config ",index));
    	
    	return setValue(index,value,null,field.getType(),length);
    }
    
    public <T> IsoMessage setValue(int index, T value, IsoType t) {
    	return setValue(index, value, null, t, 0);
    }
    
    
    public <T> IsoMessage setValue(int index, byte[] value, IsoType t, int length){
    	return setValue(index,value,t,length);
    }

    /** 添加域
     * @param index     域索引 (2 to 128)
     * @param value     域值.
     * @param t         域类型 .
     * @param length    长度
     * @return The receiver (useful for setting several values in sequence). */
    public <T> IsoMessage setValue(int index, T value, IsoType t, int length) {
    	return setValue(index, value, null, t, length);
    }

    /**
     * 添加域
     * @param <T>        值类型
     * @param index      域索引 (2 to 128) 
     * @param value      域值.
     * @param encoder    自定义格式化
     * @param t          域类型 .   
     * @param length     长度
     * @return
     */
    public <T> IsoMessage setValue(int index, T value, CustomField<T> encoder, IsoType t, int length) {
    	if (index < 2 || index > 128) {
    		throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
    	}
    	if (value == null) {
    		fields[index] = null;
    	} else {
     		fields[index] = new IsoValue<T>(t, value, length, encoder);
    	}
    	return this;
    }

    /**
     * 是否存在该域
     * @param idx
     * @return
     */
    public boolean hasField(int idx) {
    	return fields[idx] != null;
    }
    
    /**
     * 写入数据
     * @return
     */
    public byte[] writeData() {
    	ByteArrayOutputStream bout = new ByteArrayOutputStream();
    	if (isoHeader != null) {
    		try {
    			bout.write(isoHeader.getBytes());
    		} catch (IOException ex) {
    			//should never happen, writing to a ByteArrayOutputStream
    		}
    	}
    	//Message Type
    	bout.write((type & 0xff00) >> 8);
        bout.write(type & 0xff);
        
        //************** step 1  计算位图******************//
    	//Bitmap  位
        try {
			bout.write(genBitMap());
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //************** step 2  计算域******************//
    	//Fields
    	for (int i = 2; i < 129; i++) {
    		IsoValue<?> v = fields[i];
    		if (v != null) {
        		try {
        			v.write(bout);
        		} catch (IOException ex) {
        			//should never happen, writing to a ByteArrayOutputStream
        		}
    		}
    	}
    	return bout.toByteArray();
    }
    
    /**
     * 生成位图
     * @return
     */
    private  byte[] genBitMap(){
    	  byte[] map = new byte[forceb2 ? 16 : 8];
          int offset = 8,j=0;
          byte bit = 0;
          for(int i=1;i<129;i++){
          	bit = (byte)(bit | (fields[i]==null ?  0 : 1)<<(--offset));
          	if(offset==0){
          		map[j] = bit;
          		offset = 8;
          		j++;
          		bit = 0;
          	}
          	if(j>=map.length)
          		break;
          }
          return  map;
    }
    /**
     * 生成 mac MAB
     */
    private void genMAB(){
   	 	//位图保存到MAB
        System.arraycopy(genBitMap(), 0, MAB[0], 0, MABLEN);
        int mabOffset = 1; //mab 偏移
        
         //Fields
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] data;
    	for (int i = 2; i < 129; i++) {
    		IsoValue<?> v = fields[i];
    		if (v != null) {
        		try {
        			v.write(bout);
        			if(macConfig.containsKey(i)){ //保存参与mac 计算域信息
        				data = bout.toByteArray();
        				System.arraycopy(data, 0, MAB[mabOffset++], 0, data.length> MABLEN ? MABLEN : data.length);
        			}
        		} catch (IOException ex) {
        		}finally{
        			bout.reset();
        		}
    		}
    		if(mabOffset>MAB.length-1)
    			break;
    	}
    	//计算
    	for(int i=0;i<MAB.length;i++){
    		for(int j=0;j<MABLEN;j++){
    			macBlock[j] ^=MAB[i][j];
    		}
    	}
    }
    
    /**获取mac 计算域  异域结果*/
    public byte[] getMacBlock() {
    	genMAB();
		return macBlock;
	}
}
