package com.ctbri.domain;

import org.json.JSONException;
import org.json.JSONObject;

import com.ctbri.utils.ElecLog;

public class QueryNoticeRequest extends ElecRequest {

	/**  */
	private static final long serialVersionUID = 4422459297650371006L;

	private String posCati;//终端号
	private String customerNumber; //商户号
	private String noticeType;//公告类型  (0-一般公告，1-紧急公告)
	
	@Override
	public String getPayData() {
		JSONObject json = new JSONObject();
		try {
			json.put("posCati", posCati);
			json.put("customerNumber", customerNumber);
			json.put("noticeType", noticeType);
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(),e);
		}
		return json.toString();
	}

	public String getPosCati() {
		return posCati;
	}

	public void setPosCati(String posCati) {
		this.posCati = posCati;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

}
