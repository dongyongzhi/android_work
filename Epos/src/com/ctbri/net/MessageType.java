package com.ctbri.net;

public final class MessageType {
	/**签到*/
	public final static String SIGN= "00";
	
	/**消费*/
	public final static String PURCHASE = "01";
	

	/**冲正*/
	public final static String REVERSAL = "02";
	
	
	/**消费撤销*/
	public final static String CANCEL_PURCHASE = "03";
	
	
	/**查询*/
	public final static String QUERYBALANCE = "04";

	/**退货*/
	public final static String RETURNS = "05";
	
	/**退签*/
	public final static String SIGNOFF = "06";
	

	/**读取参数*/
	public final static String READPARAMS = "F0";
	

	/**更新参数*/
	public final static String UPDATEPARAMETER = "F1";
	
	
	/**预授权*/
	public final static String PRE_AUTHORIZE = "";
	/**追加预授权*/
	public final static String ADD_PRE_AUTHORIZE = "";
	/**预授权撤销*/
	public final static String CANCEL_PRE_AUTHORIZE = "";
	

	
	
 
}
