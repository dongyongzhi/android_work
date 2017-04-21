package com.yifengcom.yfpos.exception;

import com.yifengcom.yfpos.DeviceContext;
import com.yifengcom.yfpos.ErrorCode;

public class MPOSException extends RuntimeException {

	private static final long serialVersionUID = 7352797896808431093L;

	private int errorCode;

	private String errorMessage;
	
	/**
	 * 未知错误
	 * @param ex
	 */
	public MPOSException(Exception ex) {
		this.errorCode = ErrorCode.UNKNOWN.getCode();
		this.errorMessage = ex.getMessage();
	}
	
	public MPOSException(ErrorCode errorCode,DeviceContext context) {
		this.errorCode = errorCode.getCode();
		this.errorMessage = context.getErrorMessage(errorCode);
	}
	
	public MPOSException(int errorCode,String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return this.errorMessage;
	}
}
