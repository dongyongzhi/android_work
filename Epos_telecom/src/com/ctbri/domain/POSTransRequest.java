package com.ctbri.domain;

import android.os.Parcel;
import android.os.Parcelable;


public class POSTransRequest  implements Parcelable {
	private int messageType;   //交易类型
	private long money;        //交易金额
	private String orderCode;  //订单号
	private String originalSerialNumber;  //原流水号 /凭证号

	private String originalBatchNumber;   //原批次号    (原交易日期)
	private String referenceNumber;       //原参考号
	private String signName; //签名
	private String authNo;//授权码
	private String originalTransDate;//原交易日期  (MMDD) 格式 
	
	private String customerNumber;  //商户编号
	private String posNumber;  //终端编号
	
	private PayRequest  payRequest;  //插件调用信息内容
	 
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(messageType);
		dest.writeLong(money);
		dest.writeString(orderCode);
		dest.writeString(originalSerialNumber);
		dest.writeString(originalBatchNumber);
		dest.writeString(referenceNumber);
		dest.writeString(signName);
		dest.writeString(authNo);
		dest.writeString(originalTransDate);
		dest.writeString(customerNumber);
		dest.writeString(posNumber);
		
		dest.writeParcelable(payRequest, flags);
	}
	
	public static final Parcelable.Creator<POSTransRequest> CREATOR = new Parcelable.Creator<POSTransRequest>() {

		@Override
		public POSTransRequest createFromParcel(Parcel source) {
			POSTransRequest rep = new POSTransRequest();
			rep.messageType = source.readInt();
			rep.money = source.readLong();
			rep.orderCode = source.readString();
			rep.originalSerialNumber = source.readString();
			rep.originalBatchNumber = source.readString();
			rep.referenceNumber = source.readString();
			rep.signName = source.readString();
			rep.authNo = source.readString();
			rep.originalTransDate = source.readString(); //原交易日期
			rep.customerNumber = source.readString();
			rep.posNumber = source.readString();
			
			rep.payRequest = source.readParcelable(PayRequest.class.getClassLoader());
			
			
			return rep;
		}

		@Override
		public POSTransRequest[] newArray(int arg0) {
			return new POSTransRequest[arg0];
		}
	};
	
	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOriginalSerialNumber() {
		return originalSerialNumber;
	}

	public void setOriginalSerialNumber(String originalSerialNumber) {
		this.originalSerialNumber = originalSerialNumber;
	}

	public String getOriginalBatchNumber() {
		return originalBatchNumber;
	}

	public void setOriginalBatchNumber(String originalBatchNumber) {
		this.originalBatchNumber = originalBatchNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public void setSignName(String signName) {
		this.signName = signName;
	}

	public String getSignName() {
		return signName;
	}

	public void setPayRequest(PayRequest payRequest) {
		this.payRequest = payRequest;
	}

	public PayRequest getPayRequest() {
		return payRequest;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}

	public String getAuthNo() {
		return authNo;
	}
	
	
	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getPosNumber() {
		return posNumber;
	}

	public void setPosNumber(String posNumber) {
		this.posNumber = posNumber;
	}

	/**
	 * 格式MMDD
	 * @param originalTransDate
	 */
	public void setOriginalTransDate(String originalTransDate) {
		this.originalTransDate = originalTransDate;
	}

	public String getOriginalTransDate() {
		return originalTransDate;
	}

}
