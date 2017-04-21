package com.yifengcom.yfpos.tlv.support;

import com.yifengcom.yfpos.tlv.TLV;

public class TLVByteArray extends TLV<byte[]> {
 
	private static final long serialVersionUID = 1323571070383753031L;

	private byte[] value;
	private int length;
	
	public TLVByteArray(){}
	
	public TLVByteArray(int tag,byte[] value){
		this.setTag(tag);
		this.setValue(value);
		this.value = value;
		this.length  = value.length;
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
		System.arraycopy(data, offset, value, 0, len);
		this.setValue(value);
	}

}
