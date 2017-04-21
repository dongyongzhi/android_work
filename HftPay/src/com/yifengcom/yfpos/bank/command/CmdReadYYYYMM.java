package com.yifengcom.yfpos.bank.command;
//��ȡ��������
//yhb

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadYYYYMM extends EposCommand{
	
	private String value;
	public CmdReadYYYYMM(){
		command = EposProtocol.CMD2_READ_YYYYMM;
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
		byte[] id = new byte[3];
		in.get(id);	 	//��ȡ3�ֽڵı���
		
		value = StringUtil.bcd2Str(id);
		
		return 0;
	}

}