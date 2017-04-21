package com.ctbri.net;

import java.util.Random;


public final class POSPSetting {
	
	/**
	 * 易宝 posp 接口
	 *
	 * <li> 测试地址  http://219.142.69.135:8000/testTrx/   </li>
	 * 
	 * <li> 生产地址 http://219.142.69.135:8000/trx/       </li>
	 * 
	 * <li>广东生产地址 http://183.63.191.46:8000/trx/</li>
	 * 
	 */  
	public final static String URL ="http://222.189.216.110:8200/posp/";//"http://222.189.216.110:9300/"; 
	
	public final static String CONSULTKEY ="9DUjuiLx86KGdt4G0TeaBvVn"; //协商的key
	public final static String CONSULTDATA ="KEYREQUEST"; //协商数据
	public final static String KYE_DATA = "data";
	
	/**协商接口*/
	public final static String METHOD_CONSULT = String.format("%s%s", URL,"Consult");
	
	/**交易接口**/
	public final static String METHOD_TRANS = String.format("%s%s", URL,"Business");
	
	
	
	/**订单验证*/
	public final static String METHOD_VALIDATEORDER = String.format("%s%s", URL,"ValidateOrder");
	/**创建订单*/
	public final static String METHOD_CREATEORDER =  String.format("%s%s", URL,"CreateOrder");
	/**查询订单*/
	public final static String METHOD_QUERYPOSORDER = String.format("%s%s", URL,"QueryPosOrder");
	/**查询订单 (易宝优化接口v1.2)  参数相同*/
	public final static String METHOD_QUERYPOSORDERS = String.format("%s%s", URL,"QueryPosOrders");
	/**版本更新检查*/
	public final static String METHOD_CHECKDOWNLOAD = String.format("%s%s", URL,"CheckDownload");
	/**查询商户号和终端号*/
	public final static String METHOD_QUERYPOSINFO = String.format("%s%s", URL,"QueryPosInfo"); 
	/**电子签名*/
	public final static String METHOD_ELECTRONICSIGN = String.format("%s%s", URL,"ElectronicSign"); 
	/**登录*/
	public final static String METHOD_LOGIN = String.format("%s%s", URL,"login"); 
	/**签到*/
	public final static String METHOD_SIGNIN = String.format("%s%s", URL,"Business"); 
	/**退签*/
	public final static String METHOD_SIGNOFF = String.format("%s%s", URL,"Business"); 
	/**冲正*/
	public final static String METHOD_REVERSAL = String.format("%s%s", URL,"Business"); 
	/**消费*/
	public final static String METHOD_PURCHASE = String.format("%s%s", URL,"Business");
	/**消费退款*/
	public final static String METHOD_PURCHASEREFUND = String.format("%s%s", URL,"Business");
	/**撤销*/
	public final static String METHOD_REVOKE = String.format("%s%s", URL,"Business");
	/**查询余额*/
	public final static String METHOD_QUERYBALANCE = String.format("%s%s", URL,"Business");
	/**更新参数*/
	public final static String METHOD_UPDATEPARAMETER = String.format("%s%s", URL,"Business");
	/**获取广告图片和公告*/
	public final static String METHOD_QUERYNOTICE  = String.format("%s%s", URL,"QueryNotice");
	/**短信或邮件下发的接口.*/
	public final static String METHOD_MESSAGESEND = String.format("%s%s", URL,"MessagesAndEmail");
	
	/** =========== Exception ==============**/
	public final static String POSP_CONNECTION_FAIL = "连接失败,请检查网络";
	public final static String POSP_CLIENT_FAIL = "中心处理失败";
	public final static String SYSTEM_ERROR = "系统出错,请重试！";
	public final static String SYSTEM_DES3_ENCRYPTO_ERROR = "系统加密出错,请重试！"; //3des不能加密
	public final static String SYSTEM_DES3_DECRYPTE_ERROR = "中心返回数据错误,请重试！"; //3des 不能解密
	public final static String POSP_NET_BAD = "中心返回数据错误！";
	public final static String POSP_RESPONSE_404 = "中心故障，通讯失败！";
	public final static String POSP_RESPONSE_500 = "中心故障，通讯失败！";
	
	
	
	private final static char[] ITEMS={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
		     					'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
		     					'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
		     					'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
		     					'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
		     					'X', 'Y', 'Z'};
	
	private final static int ITEMS_LEN = ITEMS.length;
	private final static int KEY_LEN = 24;//密钥长度
	
	/**
	 * 随机生成密钥
	 * @return
	 */
	public static String generateKey(){
		Random random = new Random();
		StringBuilder sb = new StringBuilder(KEY_LEN);
		for(int i=0;i<KEY_LEN;i++){
			sb.append(ITEMS[random.nextInt(ITEMS_LEN)]);
		}
		return sb.toString();
	}
}
