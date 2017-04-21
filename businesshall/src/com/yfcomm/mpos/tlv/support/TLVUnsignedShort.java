package com.yfcomm.mpos.tlv.support;

import com.yfcomm.businesshall.ByteUtils;
import com.yfcomm.mpos.tlv.TLV;

public class TLVUnsignedShort extends TLV<Integer> {
 
	private static final long serialVersionUID = 1L;

	@Override
	public byte[] getBytes() {
		if(this.getValue() == null) {
			return new byte[2];
		}
		return ByteUtils.unsignedShort(this.getValue());
	}

	@Override
	public int length() {
		return 2;
	}

	@Override
	public void setValue(byte[] data, int offset, int len) {
		if(len == 2) {
			this.setValue(ByteUtils.byteToInt(data[offset], data[offset+1]));
		}
	}
}
