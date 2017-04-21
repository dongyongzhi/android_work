package com.ctbri.net.yeepay.tms;

import com.ctbri.domain.ElecResponse;

public class TMSDownResponse extends ElecResponse {

	private static final long serialVersionUID = -6466736262771051387L;
	
	private int length;
	private byte[] data;

	public void setLength(int length) {
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}
}
