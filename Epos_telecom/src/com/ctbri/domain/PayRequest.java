package com.ctbri.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class PayRequest implements Parcelable {

	private String tradeType;
	private String tradeSerialNum;
	private String companyName;
	private String companyType;
	private String orderNum;   //商户订单号
	private String orderExplain;
	private String phoneIMSINum;
	private String operatorNum;
	private String payMoney;
	private String extensionField;
	private String checkDigit;
	private String orderCode; //支付订单号
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(tradeType);
		dest.writeString(tradeSerialNum);
		dest.writeString(companyName);
		dest.writeString(companyType);
		dest.writeString(orderNum);
		dest.writeString(orderExplain);
		dest.writeString(phoneIMSINum);
		dest.writeString(operatorNum);
		dest.writeString(payMoney);
		dest.writeString(extensionField);
		dest.writeString(checkDigit);
		dest.writeString(orderCode);
	}
	
	public static final Parcelable.Creator<PayRequest> CREATOR = new Parcelable.Creator<PayRequest>() {

		@Override
		public PayRequest createFromParcel(Parcel source) {
			PayRequest request = new PayRequest();
			request.tradeType = source.readString();
			request.tradeSerialNum = source.readString();
			request.companyName = source.readString();
			request.companyType = source.readString();
			request.orderNum = source.readString();
			request.orderExplain = source.readString();
			request.phoneIMSINum = source.readString();
			request.operatorNum = source.readString();
			request.payMoney = source.readString();
			request.extensionField = source.readString();
			request.checkDigit = source.readString();
			request.orderCode = source.readString();
			return request;
		}

		@Override
		public PayRequest[] newArray(int n) {
			return new PayRequest[n];
		}
		
	};


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

	public String getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public String getExtensionField() {
		return extensionField;
	}

	public void setExtensionField(String extensionField) {
		this.extensionField = extensionField;
	}

	public String getCheckDigit() {
		return checkDigit;
	}

	public void setCheckDigit(String checkDigit) {
		this.checkDigit = checkDigit;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

}
