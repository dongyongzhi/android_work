package com.yifengcom.yfpos.service;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceInfo implements Parcelable{
	
	private String customerNo;
	private String termNo;
	private String batchNo;
	private int existsMainKey;
	private String sn;
	private String version;
	
	public DeviceInfo() { 
	
    }
	
    public DeviceInfo(Parcel in)  
    {  
    	customerNo = in.readString();  
    	termNo = in.readString();
    	batchNo = in.readString();  
    	existsMainKey =  in.readInt();
    	sn = in.readString();  
    	version = in.readString();  
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
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
    	dest.writeString(version);
	}

	
    public static final Parcelable.Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>(){  
        @Override  
        public DeviceInfo createFromParcel(Parcel source)  
        {  
        	DeviceInfo hlInfo = new DeviceInfo(source);  
            return hlInfo;  
        }  
  
        @Override  
        public DeviceInfo[] newArray(int size)  
        {  
            return new DeviceInfo[size];  
        }  
    };  
}
