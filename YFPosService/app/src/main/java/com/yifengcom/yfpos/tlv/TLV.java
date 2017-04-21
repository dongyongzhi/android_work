package com.yifengcom.yfpos.tlv;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

import com.yifengcom.yfpos.YFLog;

public abstract class TLV<V extends Serializable> implements Serializable {
	
	protected final YFLog logger = YFLog.getLog(getClass());
 
	private static final long serialVersionUID = 5941502245502818169L;
	
	private int tag;
	
	private V value;
	
	/***
	 * 转换成byte
	 * @return
	 */
	public abstract byte[] getBytes();
	
	/**
	 * 获取 TLV 转换成的byte
	 * @return
	 */
	public  void getTLVBytes(OutputStream os) {
		try {
			//长度为2
			os.write(this.tag>>8);
			os.write(this.tag);
			
			//长度
			os.write(this.length());
			
			//内容
			if(this.length()>0) {
				os.write(this.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 数据长度
	 * @return
	 */
	public abstract int length();
	
	/**
	 * 设置tlv 值 
	 * @param data   待解析的数据
	 * @param offset  
	 * @param len
	 */
	public abstract void setValue(byte[] data,int offset,int len);
	
	/**
	 * 读取数据
	 * <b> tag(2b)+ len(1b)+ data(n)</b>
	 * @param data
	 * @param offset  起始位置
	 * @return  读取个数 
	 */
	public int read(byte[] data,int offset) {
		int count = data.length;
		//tag 和 len 不能解析   tag(2b)+ len(1b)
		if(count - offset < 3) {
			return 0;
		}
		
		int temp = offset;
		//tag
		int tag = (data[temp] & 0xFF) <<8  | (data[++temp] & 0xFF); 
		//长度
		int len = data[++temp] & 0xFF; 
		
		if( (temp + len) <= (count-1) ) {
			//解析value
			this.setValue(data, ++temp, len);
			this.setTag(tag);
			return 3 + len;
			
		} else {
			//数据长度不正确
			return 0;
		}
	}
	
	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}


	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}
