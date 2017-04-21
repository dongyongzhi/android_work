package com.ctbri.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class ValidateOrderRequest extends ElecRequest {

	/**  */
	private static final long serialVersionUID = -1689300483163464526L;

	private String tradeType;    //交易类型 01：收款，02：撤销，03：查询余额，04：订单支付
	private String tradeSerialNum;  //流水号
	private String companyName; //商户名称
	private String companyType; //商户类型
	private String orderNum;        //订单号  平台生成
	private String orderExplain;    //订单说明
	private String phoneIMSINum;   //手持设备号
	private String operatorNum;   //操作工号
	private String orderCode;    //  订单号    易宝平台返回
	private String signName;//签名
	
	
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	public String getTradeSerialNum() {
		return tradeSerialNum;
	}
	public void setTradeSerialNum(String tradeSerialNum) {
		this.tradeSerialNum = tradeSerialNum;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyType() {
		return companyType;
	}
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}
	public String getOrderNum() {
		return orderNum;
	}
	public void setOrderNum(String orderNum) {
		this.orderNum = orderNum;
	}
	public String getOrderExplain() {
		return orderExplain;
	}
	public void setOrderExplain(String orderExplain) {
		this.orderExplain = orderExplain;
	}
	public String getPhoneIMSINum() {
		return phoneIMSINum;
	}
	public void setPhoneIMSINum(String phoneIMSINum) {
		this.phoneIMSINum = phoneIMSINum;
	}
	public String getOperatorNum() {
		return operatorNum;
	}
	public void setOperatorNum(String operatorNum) {
		this.operatorNum = operatorNum;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getSignName() {
		return signName;
	}
	public void setSignName(String signName) {
		this.signName = signName;
	}
	public String getPayData() {
		JSONObject json = new JSONObject();
		try {
			json.put("tradeType", tradeType);
			json.put("tradeSerialNum", tradeSerialNum);
			json.put("companyName", companyName);
			json.put("companyType", companyType);
			json.put("orderNum", orderNum);   //平台订单号
			json.put("orderExplain", orderExplain);
			json.put("phoneIMSINum", phoneIMSINum);
			json.put("operatorNum", operatorNum);
			json.put("ordercode", orderCode);
			json.put("signname", signName);
		} catch (JSONException e) {
		}
		return json.toString();
	}
}
