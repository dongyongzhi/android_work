package com.yifeng.skzs.entity;

/**用户登录
 * comment:存放用户信息
 * 
 */
public class User {
	
	public String RespCode="";//	"RespCode":"000" 应答码	定长3位	必须	000表示成功，其余表示失败	详细解释请见附录5.1	   
	private String RespDesc;	//"RespDesc":"成功" 应答码描述	变长	最大128位	必须		   
	   
	private static String SessionId;	//"SessionId":"UKu5vp5ykRWAYHdviEHPoJhcaeJHRN3e" 会话id	最大30位	必须	后续使用其他需要验证session的接口时使用的会话id	 
	/**
	 * 绑定
	 */
	private String BindStat;
	 
//	private static String MtId = "003010000029";	//移动终端设备号	固定12位	必须		   
	private static String MtId = "001000000050";	//移动终端设备号	固定12位	必须		   
//	private static String MtId = "003010000030";	//移动终端设备号	固定12位	必须		   
	private static String custId;	//"CustId":"6000060000007692" 用户客户号	最大16位	必须	登录号对应的用户客户号，后续接口都需要上送客户号	   
	private static String UserName;	//"UserName":"ceshu" 用户姓名	最大50位	必须		
	private static String name;
	private static String pwd;
	/**
	 * 账户查询
	 */
	private String TotalOrdCnt;	//当日累计交易笔数	最大3位	必须		   
	private String TotalOrdAmt;	//当日累计交易金额	变长	最大14位	必须	小数点后保留2位	   
	private String CashCardNo;	//取现银行卡卡号	最大25位	必须	卡号形式前6后4，中间数字以*替代	   
	private String BankId;		//取现银行卡所属银行	定长2位	必须	详细解释请见附录5.2	   
	private String BindedMtId;	//绑定的设备号	定长12位	可选	绑定的设备号	   
	private String AvailCashAmt;//可取现金额	变长	最大14位	必须	小数点后保留2位	   
	private String NeedLiqAmt;	//待结算余额	变长	最大14位	必须	小数点后保留2位，收款金额可以T+N日再进行结算	 

	
	
	private int state;// 查询状态 -1服务器异常，0找不到，1加载成功，2数据解析异常
	private String imsi;
	private String key;
	private boolean rememberPwd = false;// 是否记住密码

	
	public static String getName() {
		return name;
	}
	public static void setName(String value) {
		name = value;
	}
	public static String getPwd() {
		return pwd;
	}
	public static void setPwd(String value) {
		pwd = value;
	}
	public String getRespCode() {
		return RespCode;
	}
	public void setRespCode(String respCode) {
		RespCode = respCode;
	}
	public String getRespDesc() {
		return RespDesc;
	}
	public void setRespDesc(String respDesc) {
		RespDesc = respDesc;
	}
	 
	public static String getCustId() {
		return custId;
	}
	public static void setCustId(String value) {
		custId = value;
	}
	public static String getUserName() {
		return UserName;
	}
	public static  void setUserName(String userName) {
		UserName = userName;
	}
	 
	
	
	
	public static String getSessionId() {
		return SessionId;
	}
	public static void setSessionId(String sessionId) {
		SessionId = sessionId;
	}
	public String getBindStat() {
		return BindStat;
	}
	public void setBindStat(String bindStat) {
		BindStat = bindStat;
	}
	 
	public static String getMtId() {
		return MtId;
	}
	public static void setMtId(String mtId) {
		MtId = mtId;
	}
	public String getTotalOrdCnt() {
		return TotalOrdCnt;
	}
	public void setTotalOrdCnt(String totalOrdCnt) {
		TotalOrdCnt = totalOrdCnt;
	}
	public String getTotalOrdAmt() {
		return TotalOrdAmt;
	}
	public void setTotalOrdAmt(String totalOrdAmt) {
		TotalOrdAmt = totalOrdAmt;
	}
	public String getCashCardNo() {
		return CashCardNo;
	}
	public void setCashCardNo(String cashCardNo) {
		CashCardNo = cashCardNo;
	}
	public String getBankId() {
		return BankId;
	}
	public void setBankId(String bankId) {
		BankId = bankId;
	}
	public String getBindedMtId() {
		return BindedMtId;
	}
	public void setBindedMtId(String bindedMtId) {
		BindedMtId = bindedMtId;
	}
	public String getAvailCashAmt() {
		return AvailCashAmt;
	}
	public void setAvailCashAmt(String availCashAmt) {
		AvailCashAmt = availCashAmt;
	}
	public String getNeedLiqAmt() {
		return NeedLiqAmt;
	}
	public void setNeedLiqAmt(String needLiqAmt) {
		NeedLiqAmt = needLiqAmt;
	}






//
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
//
//	public String getMobileNo() {
//		return mobileNo;
//	}
//
//	public void setMobileNo(String mobileNo) {
//		this.mobileNo = mobileNo;
//	}
//
//	public String getPublicKey() {
//		return publicKey;
//	}
//
//	public void setPublicKey(String publicKey) {
//		this.publicKey = publicKey;
//	}
//
//	public String getUserId() {
//		return userId;
//	}
//
//	public void setUserId(String userId) {
//		this.userId = userId;
//	}
//
//	public String getUserName() {
//		return userName;
//	}
//
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
//
//	public String getUserPwd() {
//		return userPwd;
//	}
//
//	public void setUserPwd(String userPwd) {
//		this.userPwd = userPwd;
//	}
//
	public boolean isRememberPwd() {
		return rememberPwd;
	}

	public void setRememberPwd(boolean rememberPwd) {
		this.rememberPwd = rememberPwd;
	}
//
//	public String getCompanyId() {
//		return companyId;
//	}
//
//	public void setCompanyId(String companyId) {
//		this.companyId = companyId;
//	}
//
//	public String getCompanyName() {
//		return companyName;
//	}
//
//	public void setCompanyName(String companyName) {
//		this.companyName = companyName;
//	}
}
