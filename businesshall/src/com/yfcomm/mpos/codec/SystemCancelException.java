package com.yfcomm.mpos.codec;

import com.yfcomm.mpos.model.syn.ErrorCode;

public class SystemCancelException  extends MPOSException {

	 
	private static final long serialVersionUID = 2580915020006805140L;

	public SystemCancelException() {
		super(ErrorCode.CANCEL.getCode(),ErrorCode.CANCEL.getDefaultMessage());
	}
}
