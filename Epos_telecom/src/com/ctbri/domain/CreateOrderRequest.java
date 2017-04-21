package com.ctbri.domain;


import org.json.JSONException;
import org.json.JSONObject;

import com.ctbri.utils.ElecLog;

public class CreateOrderRequest extends ElecRequest {

	/**  */
	private static final long serialVersionUID = 1498782272065852301L;
	private String cutomerId; // 商户号
	private String cutomerOrderNumber; // 商户订单号
	private long trxAmount;// 交易额度
	private String customerSingName;
	
	public String getCutomerId() {
		return cutomerId;
	}

	public void setCutomerId(String cutomerId) {
		this.cutomerId = cutomerId;
	}

	public String getCutomerOrderNumber() {
		return cutomerOrderNumber;
	}

	public void setCutomerOrderNumber(String cutomerOrderNumber) {
		this.cutomerOrderNumber = cutomerOrderNumber;
	}

	public long getTrxAmount() {
		return trxAmount;
	}

	public void setTrxAmount(long trxAmount) {
		this.trxAmount = trxAmount;
	}
 

	@Override
	public String getPayData() {
		JSONObject json = new JSONObject();
		try {
			json.put("cutomerId", cutomerId);
			json.put("cutomerOrderNumber", cutomerOrderNumber);
			json.put("TrxAmount", String.valueOf(trxAmount));
			json.put("customerSingName", customerSingName); //用户签名
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(),e);
		}
		return json.toString();
	}

	public void setCustomerSingName(String customerSingName) {
		this.customerSingName = customerSingName;
	}

	public String getCustomerSingName() {
		return customerSingName;
	}

}
