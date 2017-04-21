package com.yfcomm.mpos.model;

/**
 * 读取模式
 * @author qc
 *
 */
public enum ReadCardModel {

	/**
	 * 磁条卡
	 */
	MAGNETIC_CARD((byte)0x01), 
	
	/**
	 * ic 卡
	 */
	IC_CARD((byte)0x02), 
	
	/**
	 * 磁条卡和ic卡
	 */
	MAGNETIC_CARD_IC_CARD((byte)0x03),
	
	/**
	 * 开启非接读卡器
	 */
	NO_CONTACT_CARD((byte)0x04),
	
	/**
	 * 所有
	 */
	ALL((byte)0x05);
	
	private final byte value;
	
	ReadCardModel(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
	
}
