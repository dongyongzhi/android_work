package com.yifengcom.yfpos.tlv.support;

import java.io.IOException;
import java.io.OutputStream;

import com.yifengcom.yfpos.tlv.TLV;

/**
 * BER-TLV  ISO 
 * @author qc
 *
 */
public class BERTLV extends TLV<byte[]> {
 
	private static final long serialVersionUID = -9096452242637643256L;

	private byte[] value;
	private int length;
	private int tag;
	
	public BERTLV(){}
	
	public BERTLV(int tag,byte...  value){
		this.tag = tag;
		this.setTag(this.tag);
		this.setValue(value);
		
		this.value = value;
		
		this.length  = value==null ? 0 : value.length;
	}
	
	@Override
	public byte[] getBytes() {
		return value;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public void setValue(byte[] data, int offset, int len) {
		this.value = new byte[len] ;
		this.length = len;
		if(len>0) {
			System.arraycopy(data, offset, value, 0, len);
			this.setValue(value);
		}
	}
	
	
	/**
	 * 读取数据
	 */
	public int read(byte[] data,int offset) {
		int count = data.length;
		
		//tag 和 len 不能解析   tag(1b)+ len(1b)
		if(count - offset < 2) {
			return 0;
		}
		
		int temp = offset;
		int start = offset;
		
		//解析tag
		int tag;
		if((data[temp] & 0x1F) == 0x1F) {
			//扩展至2个字节的tag
			tag = (data[temp] & 0xFF) <<8  | (data[++temp] & 0xFF); 
		} else {
			tag = (data[temp] & 0xFF); 
		}
		
		//长度
		int len = 0; 
		
		byte lenBytes = data[++temp];
		if((lenBytes & 0x80) == 0x80) {
			//字节数表示长度
			int lenSize = lenBytes & 0x7F;
			for(int i=0;i<lenSize ;i++) {
				len |= (data[++temp] << (i * 8)) & 0xFF; //计算Length域的长度
			}
		}else {
			len = data[++temp]  & 0xFF;
		}
		
		if( (temp + len) <= (count-1) ) {
			//解析value
			this.setValue(data, ++temp, len);
			this.setTag(tag);
			
			return temp - start +1 ;
			
		} else {
			//数据长度不正确
			return 0;
		}
	}
	
	/**
	 * 获取 TLV 转换成的byte
	 * @return
	 */
	public  void getTLVBytes(OutputStream os) {
		int len = this.length();
		
		try {
			//tag 最多长度为2字节
			if(tag>0xFF) {
				os.write(tag>>8);
				os.write(tag);
			} else {
				os.write(tag);
			}
			
			//length 长度
			int lengthSize=1;
			if(len >= 0x80) {
				lengthSize = Double.valueOf(Math.ceil((len*1.0)/0XFF)).intValue();
				os.write( 0x80 | (lengthSize-1));
				
				for(int i=0;i<lengthSize ;i++) {
					os.write(len >> (i*8));
				}
				
			}else {
				os.write(len);
			}
			//内容
			os.write(this.getBytes());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
