package com.yifengcom.yfpos.service;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceModel implements Parcelable{
	
	private String customerNo; //商户号
	private String termNo; //终端号
	private String batchNo; //批次号
	private int existsMainKey; //是否已罐装主密钥
	private String sn; 
	private String terVersion; //终端应用版本号
	private String softVersion; //软件版本号
	
	public DeviceModel() { 
	
    }
	
    public DeviceModel(Parcel in)  
    {  
    	customerNo = in.readString();  
    	termNo = in.readString();
    	batchNo = in.readString();  
    	existsMainKey =  in.readInt();
    	sn = in.readString();  
    	terVersion = in.readString();  
    	softVersion = in.readString();  
    }  

	public String getTerVersion() {
		return terVersion;
	}

	public void setTerVersion(String terVersion) {
		this.terVersion = terVersion;
	}

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

	public int getExistsMainKey() {
		return existsMainKey;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public int isExistsMainKey() {
		return existsMainKey;
	}

	public void setExistsMainKey(int existsMainKey) {
		this.existsMainKey = existsMainKey;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(customerNo);
		dest.writeString(termNo);
		dest.writeString(batchNo);
    	dest.writeInt(existsMainKey);
    	dest.writeString(sn);
    	dest.writeString(terVersion);
    	dest.writeString(softVersion);
	}

	
	/** 
     * 实例化静态内部对象CREATOR实现接口Parcelable.Creator 
     * public static final一个都不能少，内部对象CREATOR的名称也不能改变，必须全部大写 
     */  
    public static final Parcelable.Creator<DeviceModel> CREATOR = new Creator<DeviceModel>(){  
      //将Parcel对象反序列化为HarlanInfo     
        @Override  
        public DeviceModel createFromParcel(Parcel source)  
        {  
        	DeviceModel hlInfo = new DeviceModel(source);  
            return hlInfo;  
        }  
  
        @Override  
        public DeviceModel[] newArray(int size)  
        {  
            return new DeviceModel[size];  
        }  
    };  
}
