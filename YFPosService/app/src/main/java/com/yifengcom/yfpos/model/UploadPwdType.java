package com.yifengcom.yfpos.model;

/**
 * 上报输密码结果
 * @author zzc
 *
 */
public enum UploadPwdType {
	
	
	SUCCE((byte)0x00,"输密完成"),
 
	NULL((byte)0x01,"空密码"),
 
	CANCEL((byte)0x02,"取消输密"),
	
	TIMEOUT((byte)0x03,"输密超时"),
	
	UNKNOWN((byte)0xFF,"未知指令");
	
	private final byte value;
	private final String messsage;
	
	UploadPwdType(byte value,String messsage) {
		this.value = value;
		this.messsage = messsage;
	}

	public byte getValue() {
		return value;
	}

	public String getMesssage() {
		return messsage;
	}
	
	
	public static  UploadPwdType convert(int type) {
		for(UploadPwdType trxType : UploadPwdType.values()) {
			if(trxType.value == (byte)type) {
				return trxType;
			}
		}
		return UploadPwdType.UNKNOWN;
		
	}
	
}
