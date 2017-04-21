package com.ctbri.domain;

public class PrintResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = -4595596263483044492L;

	private boolean success;

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}

}
