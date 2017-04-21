package com.yifeng.iso8583.parse;

import java.util.Iterator;
import java.util.Map;

import com.yifeng.iso8583.IsoConfig;
import com.yifeng.iso8583.IsoType;
import com.yifeng.iso8583.StringUtils;
import com.yifeng.iso8583.schema.Field;

/**
 * 解析 iso 8583 包
 * @author qin
 * 
 * 2012-11-14
 */
public class IsoParse {
	private Map<Integer,Field> config = IsoConfig.getConfig();  //iso 配置文件信息
	
	private final byte[] iso;
	private int type = 0;
	private IsoField[] fields = new IsoField[129];
	private boolean forceb2 = false;
	private int start = 13; //从报文的13个byte开始解析
	
	public IsoParse(byte[] iso){
		this.iso = iso;
		parse();
	}
	public IsoParse(byte[] iso,boolean forceb2){
		this(iso);
		this.forceb2 = forceb2;
	}
	
	/**
	 * 解析
	 */
	private void parse(){
		if(iso==null || iso.length<2)
			return;
		type = (iso[start] << 8) | iso[start+1]; //信息类型
		
		if((forceb2 && iso.length<18) || (!forceb2 && iso.length<10))
			return;
		
		int offset = start+2;
		//解析位图
		int bitMapLen = forceb2 ? 16 :  8;
		int[] map = new int[forceb2 ?  128 : 64];
		int b=2;
		for(int i=0;i<bitMapLen;i++){
			b=2;
			for(int j=0;j<8;j++){
				if(j>1) b *=2;
				if(((j==0 ? 1 : b) & iso[offset+i])>>j==1){
					map[(8-j) + i*8-1] = 1;
				}
			}
		}
		offset+= bitMapLen;
		//解析域
		IsoField isoField = null;
		for(int i=0;i<map.length;i++){
			if(map[i]==1){  //域存在
				isoField    =  getField(config.get(i+1),i+1, offset,iso);
				fields[i+1] = isoField;
				if(isoField!=null)
					offset = isoField.offset;
			}
		}
	}
	
	public IsoField get(int index){
		if(index>fields.length)
			return null;
		return fields[index];
	}
	/**
	 * 取出域
	 * @param index   域
	 * @param offset  偏移量
	 * @return
	 */
	private  IsoField getField(Field fieldConfig,int index,int offset,byte[] iso){
		if(offset>iso.length-1)
			return null;
		if(fieldConfig==null)
			return null;
		IsoType type = fieldConfig.getType();
		
		int len = 0;
		
		switch(type){
		case ALPHA_LLVAR:
		case LLBIN:
		case N_LLVAR:
		case Z_LLVAR:
			if(offset+1>iso.length)  //长度不够时
				return null;
			//取出一个字节的长度
			len = (iso[offset]>>4) * 10 + (iso[offset++] & 0x0F);
			break;
		case ALPHA_LLLVAR:
		case LLLBIN:
		case N_LLLVAR:
		case Z_LLLVAR:
			if(offset + 2 > iso.length)
				return null;
			len = (iso[offset]>>4) * 1000 + (iso[offset] & 0x0F) * 100+ (iso[++offset]>>4) * 10 + (iso[offset++] & 0x0F);
			break;
			
		default:  //定长型
			len = fieldConfig.getLength();
			break;
		}
		if(len==0)
			return null;
		else if(offset + len >iso.length)
			return null;
		
		//取出数据
		byte[] desk = null;
		String value = null;
		
		switch(type){
		case N:   //bcd 类型 
		case N_LLLVAR:
		case N_LLVAR:
		case AMOUNT:
		case DATE10:
		case DATE4:
		case DATE_EXP:
		case TIME:
			int deskLen = len==1 ? 1 : len /2 + len %2;
			if(offset+deskLen > iso.length)
				return null;
			desk = new byte[deskLen];
			System.arraycopy(iso, offset, desk, 0, deskLen);
			value = StringUtils.byteToHex(desk);  //bcd 转成字符串
			if(deskLen!=len)
				value = value.substring(0,len);
			if(type==IsoType.AMOUNT)
				value =String.valueOf(Long.parseLong(value));  
			len = deskLen;
			break;
		default:
			if(offset+len > iso.length)
				return null;
			desk = new byte[len];
			System.arraycopy(iso, offset, desk, 0, len);
			//字符串则直接转换
			if(type==IsoType.ALPHA ||type==IsoType.ALPHA_LLLVAR || type==IsoType.ALPHA_LLVAR){
				value = new String(desk);
				
			}else
				value = StringUtils.byteToHex(desk);
			break;
		}
		offset += len;
		IsoField isoField = new IsoField(index,desk,value);
		isoField.offset = offset;
		
		 //如果有子域,则获取子域的值
		if(fieldConfig.getFields().size()>0){ 
			isoField.setFields(new IsoField[fieldConfig.getFields().size()+1]);
			
			//类型为bcd 码
			if(type==IsoType.N_LLLVAR || type==IsoType.N_LLVAR 
					|| type == IsoType.Z_LLLVAR || type==IsoType.Z_LLVAR){
				
				parseBCDChildField(fieldConfig,isoField,value);
				
			}else{
			
				int i=1,childOffset = 0;
				IsoField childField = null;
				for(Iterator<Field> it = fieldConfig.getFields().iterator(); it.hasNext();){
					Field field = it.next();
					//递归获取子集
					childField  = getField(field,i++,childOffset,desk);
					isoField.fields[field.getIndex()] = childField;  //计算子域值
					childOffset = childField !=null ? childField.offset : childOffset;
				}
			}
		}
		return isoField;
	}
	
	/**
	 * 获取bcd为子域的数据 
	 * @param fieldConfig
	 * @param index
	 * @param offset
	 * @param iso
	 * @return
	 */
	private void parseBCDChildField(Field fieldConfig,IsoField parentField,String source){
		int i=1,offset = 0;
		for(Iterator<Field> it = fieldConfig.getFields().iterator(); it.hasNext();){
			 
			Field field = it.next();
			
			//获取数据长度
			int len=0;
			switch(field.getType()){
			case N:    //定长
				len = field.getLength();
				break;
				
			case N_LLVAR:  //变长数据
			case Z_LLVAR:
				if(offset+1>source.length())  //长度不够时
					return;
				//取出长度
				len = Integer.parseInt(source.substring(offset,++offset));
				break;
			case N_LLLVAR:
			case Z_LLLVAR:
				if(offset+2>source.length())  //长度不够时
					return;
				//取出长度
				len = Integer.parseInt(source.substring(offset,offset+2));
				offset = offset +2;
				break;
			}
			if(len==0)
				break;
			
			if(offset + len>source.length())
				return;
			
			String value = source.substring(offset,offset + len);
			offset +=len;
			parentField.fields[i] = new IsoField(i,null,value);
			i++;
		}
	}
	
	/** 信息类型*/
	public int getMessageType() {
		return type;
	}
	
	public  class IsoField implements java.io.Serializable{
		/**  */
		private static final long serialVersionUID = 6312716291375889584L;
		private int index;
		private byte[]  bytes;
		private String value;
		private IsoField[] fields = null; //子域
		private int offset;
		
		public IsoField(int index, byte[] bytes,String value) {
			this.index = index;
			this.bytes = bytes;
			this.value = value;
		}
		public int getIndex() {
			return index;
		}
		public byte[] getBytes() {
			return bytes;
		}
		public String getValue() {
			return value;
		}
		public void setFields(IsoField[] fields) {
			this.fields = fields;
		}
		
		public IsoField get(Integer index){
			if(fields==null)
				return null;
			else if(index>fields.length-1)
				return null;
			else
				return this.fields[index];
		}
	}
}
