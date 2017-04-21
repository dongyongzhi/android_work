package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdUpdateTerm extends EposCommand{
	private int Len;
	private Charset charset = Charset.forName("GB2312");
	private ArrayList<String> ParaList = new ArrayList<String>();
	private int ParaCount; 
	private ArrayList<String> ParaNoList = new ArrayList<String>();
	
	public CmdUpdateTerm(){
		command = EposProtocol.CMD2_UPDATE_TERM;
		}
	
	
	public int decodeFromBuffer(IoBuffer in) {
		
		 
		return 0;
	}
	
	
	@Override
	public byte[] getFieldData() {
        ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		 
		
		stream.write((byte)Len);//总长度
		try {
			stream.write(ParaCount);//总条数
			
			for(int i=0;i<ParaCount;i++) {
				 stream.write(Integer.parseInt(ParaNoList.get(i)));  //记录号
				  int plen=ParaList.get(i).length(); //当前记录的长度
				  stream.write(plen);   
				  stream.write(StringUtil.hexStringToByte(ParaList.get(i)));//数据
				 } 			 
				
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	public void setPara(int paraCount,ArrayList<String> parastr,ArrayList<String> paraNo) {
		Len=1; 
		if (paraCount>32){
			return;
		}
		ParaCount=paraCount;
		for(int i=0;i<paraCount;i++) {
		  Len++;
		  Len++;
		  Len=Len+parastr.get(i).length();
		 }
		ParaList=parastr;
		ParaNoList=paraNo;
		 
	}
	
}
