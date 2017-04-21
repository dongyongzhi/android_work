package com.yifengcom.yfpos.model;

/**
 * 打印类型
 * @author zzc
 *
 */
public enum PrintType {
	
	
	SUCCE((byte)0x00,"打印成功"),
 
	PRE_AUTHORIZATION((byte)0x01,"打印机缺纸"),
 
	Error((byte)0x02,"打印机异常"),
	
	UNKNOWN((byte)0xFF,"未知指令");
	
	private final byte value;
	private final String messsage;
	
	PrintType(byte value,String messsage) {
		this.value = value;
		this.messsage = messsage;
	}

	public byte getValue() {
		return value;
	}

	public String getMesssage() {
		return messsage;
	}
	
	
	public static  PrintType convert(int type) {
		for(PrintType trxType : PrintType.values()) {
			if(trxType.value == (byte)type) {
				return trxType;
			}
		}
		return PrintType.UNKNOWN;
		
	}
	
}
