package com.yifengcom.yfpos.bank.command;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdCalcMac extends EposCommand{
	String mac;
	
	public CmdCalcMac(){
		command = EposProtocol.CMD2_CALC_MAC;
	}
	@Override
	public byte[] getFieldData() {
		if ((mac == null) || (mac.length() != 16))
			mac = "0000000000000000";
		byte[] id = StringUtil.hexStringToByte(mac);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		
		byte[] id = new byte[8];
		in.get(id);	 	//��ȡ8�ֽڵı���
		mac = StringUtil.bytesToHexString(id);
		return 0;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String smac) {
		this.mac = smac;
	}


}