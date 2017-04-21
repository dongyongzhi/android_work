package com.yifengcom.yfpos.bank.command;


import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdICResponse extends EposCommand{
	
	private String value;
	public CmdICResponse(){
		command = EposProtocol.CMD2_READ_CARDID;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public byte[] getFieldData() {
		byte[] id = StringUtil.str2Bcd(value);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		byte[] blen = new byte[2];
		in.get(blen);
		int len = (blen[0] & 0X00FF) * 256 + (blen[1]&0xFF);
		byte[] data = new byte[len];
		in.get(data);	 	//��ȡ12�ֽڵı���
		
		value = StringUtil.bcd2Str(data);
		return 0;
	}

}
