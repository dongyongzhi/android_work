package com.yifengcom.yfpos.bank.mina;

public class EposMessageType {
	public final static int MESSAGE_RECEIVED 			= 1;
	public final static int SESSION_CLOSED				= 2;
	public final static int SESSION_OPENED				= 3;
	public final static int EXCEPTION_CAUGHT			= 4;
	public final static int MESSAGE_SENT				= 5;
	public final static int SESSION_CREATED				= 6;
	public final static int SESSION_IDLE				= 7;
	public final static int MESSAGE_TIMER 				= 8;
	public final static int MESSAGE_LOGIN				= 9;
	
	//custom
	public final static int REMOTE_ERROR = 501;  //错误
	public final static int SWIPERSUCCESS = 502;  //刷卡成功
	public final static int MACSUCCESS = 503;  //计算成功
	public final static int PRINTSUCCESS = 504;  //打印成功
	public final static int RESULT_DATA = 505;  //返回数据处理
}
