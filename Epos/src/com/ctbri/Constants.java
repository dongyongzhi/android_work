package com.ctbri;

public final class Constants {
	
	/** ctbai 调试开关 */
	public  static boolean DEBUG = true;
	/**pos机输出缓存*/
	public final static int OUT_BUFFER = 1200;
	/** 编码解码方式 */
	public static String CHARSET = "gb2312";
	/** 包文内容编码  目前是以 json 方式 gb2312*/
	public final static String PACK_CHARSET = "gb2312";
 
	/** 怡丰pos 型号*/
	public final static String YF_POSE_MODE = "YFPOS";
	/**实达 pos 型号**/
	public final static String START_POS_MODE = "START WP-5C";
	
	/** posp通信时 json  key**/
	public final static String POSP_TRANS_DATA_REQUEST_KEY = "paydata";
	/** posp通信时 json  key**/
	public final static String POSP_TRANS_DATA_RESPONSE_KEY = "paydata";
	
	public final static String LOGIN_ERROR_MSG = "非中国电信的手机不能使用！";
	
	//=====版本及软件信息====///
	public final static String SOFT_TYPE = "APK";
	 
	
	/**客服电话*/
	public final static String CUSTOMER_SERVICE_PHONE = "0514-85115182";
	
	/**交易成功过后发送给客户的信息内容 */
	public final static String SEND_MESSAGE="贵卡尾号为%s的卡于%s月%s日在商户%s%s了%s，查询交易详情 %s ，客服"+CUSTOMER_SERVICE_PHONE+"。";
	
	public final static String POS_COMM_TYPE = "蓝牙外设";
	
}
