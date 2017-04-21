package com.yifengcom.yfpos.bank.mina;

public class EposBusiContext {
	String phoneNumber;
    int Nextrecord;
    int Msgtype;
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public void setreadxmlinfo(int nextrecord,int msgtype)
	{
		this.Nextrecord=nextrecord;
		this.Msgtype=msgtype;
	}
	public int getNextrecord(){
		return Nextrecord;
	}
	public int getMsgtype(){
		return Msgtype;
	}
}
