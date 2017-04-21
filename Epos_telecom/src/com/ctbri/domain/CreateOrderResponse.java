package com.ctbri.domain;

public class CreateOrderResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = 7811513862791244451L;
	
	private String orderNumber;  //支付订单号
	private String signName;      //支付签名
	
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getSignName() {
		return signName;
	}
	public void setSignName(String signName) {
		this.signName = signName;
	}
}
