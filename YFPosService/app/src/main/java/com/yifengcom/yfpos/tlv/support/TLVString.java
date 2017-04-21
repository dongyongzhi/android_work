package com.yifengcom.yfpos.tlv.support;

import java.io.UnsupportedEncodingException;

import com.yifengcom.yfpos.tlv.TLV;
import com.yifengcom.yfpos.utils.StringUtils;

public class TLVString extends TLV<String> {
	 
	private static final long serialVersionUID = 3218792744955683322L;
	
	//编码为国标gb2312
	private final static String CHARSET = "gb2312";
	
	private byte[] data = null;
	private int length;
	
	public TLVString() {
		
	}
	
	public TLVString(int tag,String value) {
		this.setTag(tag);
		this.setValue(value);
	}

	@Override
	public byte[] getBytes() {
		return this.data;
	}
	
	public void setValue(String value) {
		super.setValue(value);
		
		//获取 byte[]
		if(StringUtils.isEmpty(this.getValue())) {
			this.data = new byte[0];
		} else {
			try {
				this.data = this.getValue().getBytes(CHARSET);
			} catch (UnsupportedEncodingException e) {
				logger.e(e.getMessage(),e);
				this.data = new byte[0];
			}
		}
		this.length = this.data.length;
	}

	@Override
	public void setValue(byte[] data, int offset, int len) {
		try {
			this.setValue(data!=null && data.length>0 ? new String(data,offset,len,CHARSET) : null);
			this.length = len;
			
		} catch (UnsupportedEncodingException e) {
			logger.e(e.getMessage(),e);
		}
	}

	@Override
	public int length() {
		return  this.length;
	}
}
