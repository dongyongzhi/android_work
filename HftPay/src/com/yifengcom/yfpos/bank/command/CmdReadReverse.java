package com.yifengcom.yfpos.bank.command;


import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadReverse extends EposCommand{
	
	private String reverseInfo="0000000000000000000000";//11BYTE
	public CmdReadReverse(){
		command = EposProtocol.CMD2_READ_REVERSE;
	}
	public String getValue() {
		return reverseInfo;
	}
	public void setValue(String value) {
		this.reverseInfo = value;
	}
	
	@Override
	public byte[] getFieldData() {
		byte[] id = StringUtil.str2Bcd(reverseInfo);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		byte[] id = new byte[11];
		in.get(id);	 	//��ȡ12�ֽڵı���
		
		reverseInfo = StringUtil.bcd2Str(id);
		
		return 0;
	}

}
