package com.yfcomm.mpos.model;

import java.io.Serializable;

/**
 * 
 * @author qc
 * 
 */
public class CardModel implements Serializable {

	private static final long serialVersionUID = -675574697592171143L;

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
	
}
