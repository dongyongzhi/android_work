package com.ctbri.net;

public final class MessageType {
	/**签到*/
	public final static int SIGN= 0x00;
	/**查询*/
	public final static int QUERYBALANCE = 0x01;
	/**预授权*/
	public final static int PRE_AUTHORIZE = 0x10;
	/**追加预授权*/
	public final static int ADD_PRE_AUTHORIZE = 0x10;
	/**预授权撤销*/
	public final static int CANCEL_PRE_AUTHORIZE = 0x11;
	/**消费*/
	public final static int PURCHASE = 0x22;
	/**消费撤销*/
	public final static int CANCEL_PURCHASE = 0x23;
	/**退货*/
	public final static int RETURNS = 0x25;
	/**退签*/
	public final static int SIGNOFF = 0xE0;
	/**更新参数*/
	public final static int UPDATEPARAMETER = 0xE1;
	/**冲正*/
	public final static int REVERSAL = 0xE2;
	
	/**读取参数*/
	public final static int READPARAMS = 0xE3;
 
}
