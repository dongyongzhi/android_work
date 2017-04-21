package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;


import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;
import net.mfs.util.StringUtil;
public class CmdSeleMenu extends EposCommand{
	private Charset charset = Charset.forName("GB2312");
	private ArrayList<String> Para;
	private byte iCount;
	private int ParaLen,index;
	private String Title;
	public int getseleceindex(){
		return index;
	}
	
	public CmdSeleMenu(){
		command = EposProtocol.CMD2_SELE_MENU;
	}
	
	public int decodeFromBuffer(IoBuffer in){
		
		 
		index= in.get();
		return 0;
	}
	
	@Override
	public byte[] getFieldData() {
		// TODO Auto-generated method stub
		 
		ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		 
		byte of=(byte)255;
		stream.write((byte)ParaLen);
		try {
			stream.write(Title.getBytes(charset));
			stream.write(of);
			stream.write(iCount); 
			stream.write(of);
			for(int i=0;i<iCount;i++)
			{
				stream.write(Para.get(i).getBytes(charset));
				stream.write(of);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	 
	
	public void setMenu(String title,int icount,ArrayList<String> menustr) {
		Title=title;
		ParaLen=title.getBytes(charset).length+3;
		iCount=(byte)icount;
		Para=menustr;
		for(int i=0;i<icount;i++)
		{
			ParaLen=ParaLen+menustr.get(i).getBytes(charset).length+1;
		}
		 
	}
	 
}
