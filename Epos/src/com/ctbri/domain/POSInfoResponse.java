package com.ctbri.domain;



public class POSInfoResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = -2115869148825497527L;
	/** 商户号*/
	private String customerNumber;
	/** 商户简称*/
	private String customerName;
	/**终端序列号*/
	private String serialNumber;
	
	public String getCustomerNumber() {
		return customerNumber;
	}
	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}
