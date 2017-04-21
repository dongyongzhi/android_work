package com.yifengcom.yfpos.bank.command;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadPin  extends EposCommand{
	String password="";
	String passwordMac="";
	public CmdReadPin(){
		command = EposProtocol.CMD2_READ_PIN;
	}
	@Override
	public byte[] getFieldData() {
		byte[] id = StringUtil.hexStringToByte(password+passwordMac);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		byte[] id = new byte[8];
		in.get(id);	 	//��ȡ8�ֽڵı���
		byte[] mac = new byte[4];
		in.get(mac);
		password = StringUtil.bytesToHexString(id);
		passwordMac = StringUtil.bytesToHexString(mac);
		return 0;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setPassword(byte[] password){
		this.password = StringUtil.bytesToHexString(password);
	}
	
	public String getPasswordMac() {
		return passwordMac;
	}
	
	
	public void setPasswordMac(String passwordMac) {
		this.passwordMac = passwordMac;
	}
	
	public void setPasswordMac(byte[] passwordMac){
		this.passwordMac = StringUtil.bytesToHexString(passwordMac);
	}

}
