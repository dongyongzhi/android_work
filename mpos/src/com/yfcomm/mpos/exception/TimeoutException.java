package com.yfcomm.mpos.exception;

import com.yfcomm.mpos.DeviceContext;
import com.yfcomm.mpos.ErrorCode;

public class TimeoutException extends MPOSException {

	private static final long serialVersionUID = -4836551583610686707L;
	
	public TimeoutException() {
		super(ErrorCode.TIMEOUT.getCode(),ErrorCode.TIMEOUT.getDefaultMessage());
	}
	
	public TimeoutException(DeviceContext dc) {
		super(ErrorCode.TIMEOUT.getCode(), dc.getErrorMessage(ErrorCode.TIMEOUT));
	}
	
}
