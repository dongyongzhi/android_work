package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;

public class PrintRecord {
	
	private Charset charset = Charset.forName("GB2312");
	
	public String printControl;
	public byte printIndex;
	public String printInfo;

	
	public int loadFromBuffer(IoBuffer in) throws CharacterCodingException{
		@SuppressWarnings("unused")
		String s = in.getString(3, charset.newDecoder());
		printIndex = (byte)Integer.parseInt(in.getString(1, charset.newDecoder()));
		StringBuffer sb = new StringBuffer();
		while (in.get() != 0){
			sb.append(in.getChar()); 
		}
		printInfo = sb.toString();
		return 0;
	}
	public byte[] getprintbag()  throws CharacterCodingException{
		 
		ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		try {
		   stream.write(printControl.getBytes(charset));
		   stream.write(printIndex);
		   stream.write(printInfo.getBytes(charset));
		   byte b=0;
		   stream.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return  stream.toByteArray();
	}
}
