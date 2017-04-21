package com.ctbri.domain;

public class LoginResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = -2577290048136897570L;
	
	private boolean islogin;

	public void setIslogin(boolean islogin) {
		this.islogin = islogin;
	}

	public boolean isIslogin() {
		return islogin;
	}
}
