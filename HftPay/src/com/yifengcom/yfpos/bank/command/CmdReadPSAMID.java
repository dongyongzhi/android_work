package com.yifengcom.yfpos.bank.command;

import java.nio.charset.CharacterCodingException;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadPSAMID extends EposCommand{
	String PSAMID;
	
	public CmdReadPSAMID(){
		command = EposProtocol.CMD2_READ_PSAMID;
	}
	@Override
	public byte[] getFieldData() {
		if ((PSAMID == null) || (PSAMID.length() != 16))
			PSAMID = "0000000000000000";
		byte[] id = StringUtil.hexStringToByte(PSAMID);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		
		byte[] id = new byte[8];
		in.get(id);	 	//��ȡ8�ֽڵı���
		
		PSAMID = StringUtil.bytesToHexString(id);
		return 0;
	}
	public String getPSAMID() {
		return PSAMID;
	}
	
	public void setPSAMID(String pSAMID) {
		PSAMID = pSAMID;
	}

}
