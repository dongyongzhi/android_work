package com.yifengcom.yfpos.exception;

import com.yifengcom.yfpos.DeviceContext;
import com.yifengcom.yfpos.ErrorCode;

public class ConnectionException extends MPOSException {
 
	private static final long serialVersionUID = 2936202912185445061L;

	public ConnectionException(DeviceContext dc) {
		super(ErrorCode.DEVICE_CLOSE.getCode(), dc.getErrorMessage(ErrorCode.DEVICE_CLOSE));
	}
}
