package com.yifengcom.yfpos.bank.command;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;


//����Ӧ�ú���
public class CmdReadCustom extends EposCommand{
	private Charset charset = Charset.forName("GB2312");
	private String value;
	public CmdReadCustom(){
		command = EposProtocol.CMD2_READ_CUSTOM;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public byte[] getFieldData() {
		byte[] kk = new byte[value.length()+1];
		kk[0] = (byte) value.length();
		if (value.length() > 0)
			System.arraycopy(value.getBytes(),   0,   kk,   1,   value.length());	 
		return kk.clone();
	}
	@Override
	public int decodeFromBuffer(IoBuffer in) {
		int len = in.get()&0xFF;
		try {
			value = in.getString(len, charset.newDecoder());
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
