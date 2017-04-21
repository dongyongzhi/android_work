package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;



import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdDisplay extends EposCommand{
	private Charset charset = Charset.forName("GB2312");
	private String displayInfo;
	private String returnCode;
	
	public CmdDisplay(){
		command = EposProtocol.CMD2_DISPLAY;
	}
	
	public int decodeFromBuffer(IoBuffer in){
		
		byte len = in.get();		//结果信息长度	N1	HEX	指明结果信息的字节数
		@SuppressWarnings("unused")
		byte typ = in.get();		//刷新方式	N1	ASC	指明自动返回待机状态或等待接收下一报文时屏幕的刷新方式，0表示不刷新不显示首页信息，1表示刷新后显示首页信息或提示信息。
		byte tim = in.get();        //应答码	AN2	ASC	中心返回的处理代码
		try {
			
			setReturnCode(in.getString(2, charset.newDecoder()));		//应答码	AN2	ASC	中心返回的处理代码
			setDisplayInfo(in.getString(len-4, charset.newDecoder()));	//应答信息	VAR	ASC		
		} catch (CharacterCodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public byte[] getFieldData() {
		// TODO Auto-generated method stub
		int len = 3;
		String sCode = String.format("%1$02d", returnCode);
		ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		if (displayInfo != null) 
			len += displayInfo.getBytes(charset).length;
		
		stream.write((byte)len);
		try {
			stream.write(0x30);
			if (sCode != null)
				stream.write(sCode.getBytes(charset));
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
	public String getReturnCode() {
		return returnCode;
	}
	
	public void setReturnCode(String string) {
		this.returnCode = string;
	}
}
