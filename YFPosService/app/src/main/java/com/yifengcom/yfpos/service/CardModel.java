package com.yifengcom.yfpos.service;

import android.os.Parcel;
import android.os.Parcelable;

public class CardModel implements Parcelable {

	private String pan;

	private String expireDate;
	private boolean isIc;

	private int encryTrack2Len;
	private int encryTrack3Len;
	private String encryTrack2;
	private String encryTrack3;
	
	private int track2Len;
	private int track3Len;
	private String track2;
	private String track3;

	private String icData;

	private String mac;
	private String icSeq;
	private String random;
	private String pinBlock;
	
	private String serialNo;
	private String batchNo;
	
	
	public CardModel() { 
		
    }
	
    public CardModel(Parcel in)  
    {  
    	readFromParcel(in); 
    }  

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

	public boolean isIc() {
		return isIc;
	}

	public void setIc(boolean isIc) {
		this.isIc = isIc;
	}

	public String getEncryTrack2() {
		return encryTrack2;
	}

	public void setEncryTrack2(String encryTrack2) {
		this.encryTrack2 = encryTrack2;
	}

	public String getEncryTrack3() {
		return encryTrack3;
	}

	public void setEncryTrack3(String encryTrack3) {
		this.encryTrack3 = encryTrack3;
	}

	public String getIcData() {
		return icData;
	}

	public void setIcData(String icData) {
		this.icData = icData;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIcSeq() {
		return icSeq;
	}

	public void setIcSeq(String icSeq) {
		this.icSeq = icSeq;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getPinBlock() {
		return pinBlock;
	}

	public void setPinBlock(String pinBlock) {
		this.pinBlock = pinBlock;
	}

	public int getTrack2Len() {
		return track2Len;
	}

	public void setTrack2Len(int track2Len) {
		this.track2Len = track2Len;
	}

	public int getTrack3Len() {
		return track3Len;
	}

	public void setTrack3Len(int track3Len) {
		this.track3Len = track3Len;
	}

	public int getEncryTrack2Len() {
		return encryTrack2Len;
	}

	public void setEncryTrack2Len(int encryTrack2Len) {
		this.encryTrack2Len = encryTrack2Len;
	}

	public int getEncryTrack3Len() {
		return encryTrack3Len;
	}

	public void setEncryTrack3Len(int encryTrack3Len) {
		this.encryTrack3Len = encryTrack3Len;
	}

	public String getTrack2() {
		return track2;
	}

	public void setTrack2(String track2) {
		this.track2 = track2;
	}

	public String getTrack3() {
		return track3;
	}

	public void setTrack3(String track3) {
		this.track3 = track3;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(pan);
		dest.writeString(expireDate);
    	dest.writeByte((byte)(isIc ? 1 : 0));
    	dest.writeInt(encryTrack2Len);
    	dest.writeInt(encryTrack3Len);
    	dest.writeString(encryTrack2);
    	dest.writeString(encryTrack3);
    	dest.writeInt(track2Len);
    	dest.writeInt(track3Len);
    	dest.writeString(track2);
    	dest.writeString(track3);
    	dest.writeString(icData);
    	dest.writeString(mac);
    	dest.writeString(icSeq);
    	dest.writeString(random);
    	dest.writeString(pinBlock);
    	dest.writeString(serialNo);
    	dest.writeString(batchNo);
	}

	
	/** 
     * 实例化静态内部对象CREATOR实现接口Parcelable.Creator 
     * public static final一个都不能少，内部对象CREATOR的名称也不能改变，必须全部大写 
     */  
    public static final Parcelable.Creator<CardModel> CREATOR = new Creator<CardModel>(){  
      //将Parcel对象反序列化为HarlanInfo     
        @Override  
        public CardModel createFromParcel(Parcel source)  
        {  
            return new CardModel(source);  
        }  
  
        @Override  
        public CardModel[] newArray(int size)  
        {  
            return new CardModel[size];  
        }  
    };


	public void readFromParcel(Parcel in) {
		pan = in.readString();
    	expireDate = in.readString();
    	isIc = in.readByte() != 0;
    	encryTrack2Len = in.readInt();
    	encryTrack3Len = in.readInt();
    	encryTrack2 = in.readString();
    	encryTrack3 = in.readString();
    	track2Len = in.readInt();
    	track3Len = in.readInt();
    	track2 = in.readString();
    	track3 = in.readString();
    	icData = in.readString();
    	mac = in.readString();
    	icSeq = in.readString();
    	random = in.readString();
    	pinBlock = in.readString();
    	serialNo = in.readString();
    	batchNo = in.readString();
	}  
	
}
