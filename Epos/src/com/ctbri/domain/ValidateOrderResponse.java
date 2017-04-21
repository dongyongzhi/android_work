package com.ctbri.domain;

public class ValidateOrderResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = 2285962731146962066L;

	private boolean isValidate;

	public void setValidate(boolean isValidate) {
		this.isValidate = isValidate;
	}

	public boolean isValidate() {
		return isValidate;
	}
	
}
