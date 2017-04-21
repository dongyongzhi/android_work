package com.ctbri.domain;

/**
 * posp 平台返回信息
 * @author qc
 *
 */
public class POSPResponse extends MessageResponse {
	
	public static final String SUCCESS = "0";
 
	public POSPResponse(String errCode, String errMsg) {
		super(errCode, errMsg);
	}

	private static final long serialVersionUID = 3174770385220581889L;

	 
	@Override
	public boolean success() {
		return this.getErrCode()!=null && this.getErrCode().equals(SUCCESS);
	}

}
