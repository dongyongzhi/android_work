package com.yifengcom.yfpos.model;

public enum Action {

	CLEAR((byte) 0x01), ADD((byte) 0x02), DELETE((byte) 0x03);

	private final byte value;

	Action(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}

}
