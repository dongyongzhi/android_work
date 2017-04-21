package com.ctbri.net.yeepay;

import org.json.JSONObject;

import com.ctbri.domain.ElecResponse;

/**
 * 易宝 接口返回  （json）
 * @author qin
 * 
 * 2012-12-7
 */
public class MPOSPResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = -478383539485782069L;
	
	private JSONObject  result;

	public void setResult(JSONObject result) {
		this.result = result;
	}

	public JSONObject getResult() {
		return result;
	}

}
