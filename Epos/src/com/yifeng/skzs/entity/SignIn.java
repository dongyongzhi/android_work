package com.yifeng.skzs.entity;
/**
 * 签到
 * 
 */
public class SignIn {
	
	private String RespCode="";//应答码	定长3位	必须	000表示成功，其余表示失败	详细解释请见附录5.1	   
	private String RespDesc;//应答码描述	变长	最大128位	必须		   
	private String OrdId;	//订单号	最大20位	必须		   
	private String PinKey;	//Pinkey密文	固定32位	必须		   
	private String PinKvc;	//Pinkey校验值	固定16位	必须		   
	private String PackKey;	//整包key密文	固定32位	必须		   
	private String PackKvc;	//整包key校验值	固定16位	必须		   
	private String Md5Key;	//Md5密钥	固定32位	必须		 	说明：
	
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
	 
	public  String getOrdId() {
		return OrdId;
	}
	public  void setOrdId(String ordId) {
		OrdId = ordId;
	}
	public String getPinKey() {
		return PinKey;
	}
	public void setPinKey(String pinKey) {
		PinKey = pinKey;
	}
	public String getPinKvc() {
		return PinKvc;
	}
	public void setPinKvc(String pinKvc) {
		PinKvc = pinKvc;
	}
	public String getPackKey() {
		return PackKey;
	}
	public void setPackKey(String packKey) {
		PackKey = packKey;
	}
	public String getPackKvc() {
		return PackKvc;
	}
	public void setPackKvc(String packKvc) {
		PackKvc = packKvc;
	}
	public String getMd5Key() {
		return Md5Key;
	}
	public void setMd5Key(String md5Key) {
		Md5Key = md5Key;
	}

	 
	
}
