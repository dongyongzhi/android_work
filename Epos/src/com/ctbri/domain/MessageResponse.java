package com.ctbri.domain;

import com.ctbri.ElecObject;

/**
 * 返回信息定义
 * @author qc
 *
 */
public abstract class MessageResponse extends ElecObject{
 
	private static final long serialVersionUID = -2040194788283586221L;
	private final String errCode;
	private final String errMsg;
	

	public MessageResponse(String errCode,String errMsg) {
		this.errCode = errCode;
		this.errMsg = errMsg;
	}
	
	/**
	 * 是否成功
	 * @return
	 */
	public abstract boolean  success();

	public String getErrCode() {
		return errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}
 
}
