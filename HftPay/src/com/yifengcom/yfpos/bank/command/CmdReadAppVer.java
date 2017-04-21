package com.yifengcom.yfpos.bank.command;
//��ȡ��������
//yhb

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadAppVer extends EposCommand{
	
	private String value;
	public CmdReadAppVer(){
		command = EposProtocol.CMD2_READ_APP_VER;
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
		byte[] id = new byte[4];
		in.get(id);	 	// 
		
		value = StringUtil.bcd2Str(id);
		
		return 0;
	}

}
