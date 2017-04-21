package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;


import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdUpdate_Caption extends EposCommand{
	private Charset charset = Charset.forName("GB2312");
	private String displayInfo;
	 
	
	public CmdUpdate_Caption(){
		command = EposProtocol.CMD2_UPDATE_CAPTION;
	}
	
	public int decodeFromBuffer(IoBuffer in){
		
		return 0; 
	}
	
	@Override
	public byte[] getFieldData() {
		// TODO Auto-generated method stub
		int len=0; 
		ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		if (displayInfo != null) 
			len = displayInfo.getBytes(charset).length;
		
		stream.write((byte)len);
		try {
			 
			if (displayInfo != null)
				stream.write(displayInfo.getBytes(charset));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	
	
	public String getDisplayInfo() {
		return displayInfo;
	}
	
	public void setDisplayInfo(String displayInfo) {
		if (displayInfo.length()<253)
			this.displayInfo = displayInfo;
		else
			displayInfo.substring(0, 254);
	}
	 
 
}
