package com.yifengcom.yfpos.model.syn;

/**
 * 交易类型
 * @author qc
 *
 */
public enum TrxType {
	
	/**
	 * 查询
	 */
	QUERY((byte)0x01,"余额查询"),
	
	/**
	 * 预授权
	 */
	PRE_AUTHORIZATION((byte)0x10,"预授权"),
	
	/**
	 * 追加预授权
	 */
	ADD_PRE_AUTHORIZATION((byte)0x10,"追加预授权"),
	
	/**
	 * 预授权撤销
	 */
	PRE_AUTHORIZATION_REVOKE((byte)0x11,"预授权撤销"),
	
	/**
	 * 预授权完成（联机）
	 */
	PRE_AUTHORIZATION_ONLINE_COMPLETE((byte)0x20,"预授权完成"),
	
	/**
	 * 预授权完成撤销
	 */
	PRE_AUTHORIZATION_COMPLETE_REVOKE((byte)0x21,"完成撤销"),
	
	
	/**
	 * 预授权完成（离线）
	 */
	PRE_AUTHORIZATION_OFF_COMPLETE((byte)0x24,"预授权完成"),

	/**
	 * 消费
	 */
	PURCHASE((byte)0x22,"消费"),
	
	/**
	 * 消费撤销
	 */
	REVOKE((byte)0x23,"消费撤销"),
	
	/**
	 * 退货
	 */
	REFUND((byte)0x25,"退款"),
	
	/**
	 * 结算
	 */
	SETTLEMENT((byte)0x30,"结算"),
	
	/**
	 * 结算调整
	 */
	SETTLEMENT_ADJUSTMENT((byte)0x32,"结算调整"),
	
	/**
	 * 结算调整（追加小费）
	 */
	SETTLEMENT_ADJUSTMENT_TIP((byte)0x34,"调整追加小费"),
	
	/**
	 * 离线消费
	 */
	OFF_PURCHASE((byte)0x36,"消费"),
	
	/**
	 * 基于PBOC 电子钱包的IC 卡指定账户圈存
	 */
	IC_STORE((byte)0x40,"账户圈存");
	
	
	private final byte value;
	private final String messsage;
	
	TrxType(byte value,String messsage) {
		this.value = value;
		this.messsage = messsage;
	}

	public byte getValue() {
		return value;
	}

	public String getMesssage() {
		return messsage;
	}
	
	
	public static  TrxType convert(int type) {
		for(TrxType trxType : TrxType.values()) {
			if(trxType.value == (byte)type) {
				return trxType;
			}
		}
		return TrxType.QUERY;
		
	}
	
}
