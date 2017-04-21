package com.yifengcom.yfpos.bank.command;
//读取交易数量
//yhb

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadBusiAmount extends EposCommand{
	
	private long value;
	public CmdReadBusiAmount(){
		command = EposProtocol.CMD2_READ_AMOUNT;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	
	@Override
	public byte[] getFieldData() {
		byte[] id = StringUtil.str2Bcd(String.format("%012d", value));
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		byte[] id = new byte[6];
		in.get(id);	 	//读取12字节的编码
		String s = StringUtil.bcd2Str(id);
		setValue(Integer.parseInt(s));
		
		return 0;
	}

}