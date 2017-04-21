package com.yifeng.skzs.entity;

/**
 * comment:存放用户信息
 * 
 * @author:ZhangYan Date:2012-8-17
 */
public class User {
	private int state;// 查询状态 -1服务器异常，0找不到，1加载成功，2数据解析异常
	private String imsi;
	private String key;
	private String mobileNo;// 手机号码
	private String publicKey;// 公钥
	private String userId;// 用户Id（登录名）
	private String userName;// 用户名
	private String userPwd;// 密码
	private boolean rememberPwd = false;// 是否记住密码
	private String companyId;// 公司Id
	private String companyName;// 公司名称

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

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public boolean isRememberPwd() {
		return rememberPwd;
	}

	public void setRememberPwd(boolean rememberPwd) {
		this.rememberPwd = rememberPwd;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
}
