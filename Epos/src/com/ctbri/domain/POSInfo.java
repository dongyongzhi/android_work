package com.ctbri.domain;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * pos 机信息
 * @author qin
 * 
 * 2012-12-2
 */
public class POSInfo extends ElecResponse implements Parcelable {

	private static final long serialVersionUID = -8291432250244067134L;
	
	/** 商户号*/
	private String customerNumber;
	/** 商户简称*/
	private String customerName;
	/**终端序列号*/
	private String serialNumber;
    /**POS中心标识码*/
	private String posNumber;
	/**操作员号*/
	private String operator;
	/**收单行标识码*/
	private String acquirerCode;
	/**收单行名称*/
	private String acquirerName;
	/**pos 机地址*/
	private String address;
	/**pos设备名称*/
	private String posName; 
	
	private String commType; //通信方式
	
	private String remark;  //其它信息
	
	private String softVersion;  //软件版本
	private String paramVersion; //参数版本号
	
	/** 只写区域***/
	private byte[] mainkey;// 终端主密钥密文
	private String initSeriNo;//POS初始流水号
	private String initBathNo;//POS初始批次号
	
	private String paramsPwd;//参数设置密码
	private String securityPwd;//安全设置密码
	
	private int maxReversalTime;//最大冲正次数
	private String TPDU;//
	 
	
	
	private boolean supportPrint;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(customerNumber);
		dest.writeString(customerName);
		dest.writeString(serialNumber);
		dest.writeString(posNumber);
		dest.writeString(operator);
		dest.writeString(acquirerCode);
		dest.writeString(acquirerName);
		dest.writeString(address);
		dest.writeString(posName);
		dest.writeString(commType);
		dest.writeString(remark);
		dest.writeString(softVersion);
		dest.writeString(paramVersion);
		
		dest.writeByte(supportPrint == true ? (byte)1 :  (byte)0);
	}

	public static final Parcelable.Creator<POSInfo> CREATOR = new Parcelable.Creator<POSInfo>() {
		@Override
		public POSInfo createFromParcel(Parcel source) {
			POSInfo pos = new POSInfo();
			//pos.resultCode  = ResponseCode.convert(source.readInt());
			pos.customerNumber = source.readString();
			pos.customerName = source.readString();
			pos.serialNumber = source.readString();
			pos.posNumber = source.readString();
			pos.operator = source.readString();
			pos.acquirerCode = source.readString();
			pos.acquirerName = source.readString();
			pos.address = source.readString();
			pos.posName = source.readString();
			pos.commType = source.readString();
			pos.remark = source.readString();
			pos.softVersion = source.readString();
			pos.paramVersion = source.readString();
			
			pos.supportPrint = source.readByte() == 1 ? true : false;
			return pos;
		}

		@Override
		public POSInfo[] newArray(int arg0) {
			return new POSInfo[arg0];
		}
	};
	
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

	public String getPosNumber() {
		return posNumber;
	}

	public void setPosNumber(String posNumber) {
		this.posNumber = posNumber;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getAcquirerCode() {
		return acquirerCode;
	}

	public void setAcquirerCode(String acquirerCode) {
		this.acquirerCode = acquirerCode;
	}

	public String getAcquirerName() {
		return acquirerName;
	}

	public void setAcquirerName(String acquirerName) {
		this.acquirerName = acquirerName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public String getPosName() {
		return posName;
	}

	public void setPosName(String posName) {
		this.posName = posName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setCommType(String commType) {
		this.commType = commType;
	}

	public String getCommType() {
		return commType;
	}

	public void setSupportPrint(boolean supportPrint) {
		this.supportPrint = supportPrint;
	}

	public boolean isSupportPrint() {
		return supportPrint;
	}

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

	public String getParamVersion() {
		return paramVersion;
	}

	public void setParamVersion(String paramVersion) {
		this.paramVersion = paramVersion;
	}

	public String getInitSeriNo() {
		return initSeriNo;
	}

	public void setInitSeriNo(String initSeriNo) {
		this.initSeriNo = initSeriNo;
	}

	public String getInitBathNo() {
		return initBathNo;
	}

	public void setInitBathNo(String initBathNo) {
		this.initBathNo = initBathNo;
	}

	public String getParamsPwd() {
		return paramsPwd;
	}

	public void setParamsPwd(String paramsPwd) {
		this.paramsPwd = paramsPwd;
	}

	public String getSecurityPwd() {
		return securityPwd;
	}

	public void setSecurityPwd(String securityPwd) {
		this.securityPwd = securityPwd;
	}

	public int getMaxReversalTime() {
		return maxReversalTime;
	}

	public void setMaxReversalTime(int maxReversalTime) {
		this.maxReversalTime = maxReversalTime;
	}

	public String getTPDU() {
		return TPDU;
	}

	public void setTPDU(String tPDU) {
		TPDU = tPDU;
	}

	public void setMainkey(byte[] mainkey) {
		this.mainkey = mainkey;
	}

	public byte[] getMainkey() {
		return mainkey;
	}
}
