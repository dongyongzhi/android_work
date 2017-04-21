package com.ctbri.domain;

import com.ctbri.ElecObject;

public class ElecResponse extends ElecObject {

	/**  */
	private static final long serialVersionUID = 4947950477666148058L;

	private String errCode = "00";
	private String errMsg;
	

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
	/**
	 * 是否成功
	 * @return
	 */
	public boolean isSuccess() {
		return ResponseCode.SUCCESS.getCode().equals(errCode);
	}

	public static ElecResponse getErrorResponse(ResponseCode errCode) {
		ElecResponse resp = new ElecResponse();
		resp.setErrCode(errCode.getCode());
		resp.setErrMsg(errCode.getMessage());
		return resp;
	}

	public static <T extends ElecResponse> T getErrorResponse(Class<T> cls,ResponseCode errCode) {
		T t;
		try {
			t = cls.newInstance();
			t.errCode = errCode.getCode();
			t.errMsg = errCode.getMessage();
			return t;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T extends ElecResponse> T  getErrorResponse(Class<T> cls,String code, String msg) {
		T t;
		try {
			t = cls.newInstance();
			t.errCode = code;
			t.errMsg = msg;
			return t;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T extends ElecResponse> T  getErrorResponse(Class<T> cls,ElecResponse resp) {
		T t;
		try {
			t = cls.newInstance();
			t.errCode = resp.errCode;
			t.errMsg = resp.errMsg;
			return t;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
