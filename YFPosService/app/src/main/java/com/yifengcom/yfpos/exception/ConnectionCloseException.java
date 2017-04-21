package com.yifengcom.yfpos.exception;

import com.yifengcom.yfpos.DeviceContext;
import com.yifengcom.yfpos.ErrorCode;

public class ConnectionCloseException  extends MPOSException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1412189300166495570L;

	public ConnectionCloseException(DeviceContext dc) {
		super(ErrorCode.DEVICE_CLOSE.getCode(), dc.getErrorMessage(ErrorCode.DEVICE_CLOSE));
	}
}
