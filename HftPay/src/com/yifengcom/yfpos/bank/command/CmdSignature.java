package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;


import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;
import net.mfs.util.StringUtil;
public class CmdSignature extends EposCommand{
	private Charset charset = Charset.forName("GB2312");
	private String value; 
	private byte Index;
	private int ParaLen;
	private String SMAK,SPIK,STDK;
	
	public CmdSignature(){
		command = EposProtocol.CMD2_SIGNATURE;
	}
	
	public int decodeFromBuffer(IoBuffer in){
	
	try
		{
		value=in.getString(2, charset.newDecoder());
	    } catch (CharacterCodingException e) {
		 
		   e.printStackTrace();
	    }
		return 0;
	}
	public String getValue() {
		return value;
	}
	@Override
	public byte[] getFieldData() {
		// TODO Auto-generated method stub
		 
		ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		 
		
		stream.write((byte)ParaLen);
		try {
			stream.write(Index);			 
			 
			stream.write(StringUtil.hexStringToByte(SPIK));
			 
			stream.write(StringUtil.hexStringToByte(SMAK));
			stream.write(StringUtil.hexStringToByte(STDK));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	
	
	 
	
	public void setPIK(String sPIK) {
		if (sPIK.length()!=16){
			
			 
		}
		this.SPIK="2910"+sPIK;
		ParaLen=ParaLen+18;
	}
	public void setParaindex(byte index) {
		 this.Index=index;
		 ParaLen=0;
		 ParaLen=ParaLen+1;
	}
	 
	
	public void setMAK(String sMAK) {
		if (sMAK.length()!=16){
			
			 
		}
		this.SMAK="2A10"+sMAK;
		ParaLen=ParaLen+18;
		 
	}
	public void setTDK(String sTDK) {
		if (sTDK.length()!=16){
			
			 
		}
		this.STDK="2B10"+sTDK;
		ParaLen=ParaLen+18;
		 
	}
}
