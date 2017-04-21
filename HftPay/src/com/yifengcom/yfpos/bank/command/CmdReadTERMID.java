package com.yifengcom.yfpos.bank.command;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadTERMID extends EposCommand{
	String termID;
	
	public CmdReadTERMID(){
		command = EposProtocol.CMD2_READ_TERMID;
	}
	@Override
	public byte[] getFieldData() {
		if ((termID == null) || (termID.length() != 20))
			termID = "00000000000000000000";
		byte[] id = StringUtil.hexStringToByte(termID);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		
		byte[] id = new byte[10];
		in.get(id);	 	//��ȡ10�ֽڵı���
		termID = StringUtil.bytesToHexString(id);
		return 0;
	}
	public String getTermID() {
		return termID;
	}
	public void setTermID(String termID) {
		this.termID = termID;
	}


}