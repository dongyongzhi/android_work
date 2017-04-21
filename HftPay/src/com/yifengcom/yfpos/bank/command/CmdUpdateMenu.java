package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdUpdateMenu extends EposCommand{
	private int Len;
	private Charset charset = Charset.forName("GB2312");
	 
	private int ParaCount; 
	private ArrayList<String> Paraisused;
	private ArrayList<String> Paralevel;
	private ArrayList<String> Parabusicode;
	private ArrayList<String> ParafunID;
	private ArrayList<String> ParaCodeNo;
	private ArrayList<String> Parastr;
	private String value;
	private String appver="";
	public CmdUpdateMenu(){
		command = EposProtocol.CMD2_UPDATE_MENU;
		}
	public void setappver(String sver){
		appver=sver;
	}
	public String getupdate()
	{
		return value;
	}
	public int decodeFromBuffer(IoBuffer in) {
		
		 
		try
		{
		value=in.getString(2, charset.newDecoder());
	    } catch (CharacterCodingException e) {
		 
		   e.printStackTrace();
	    }
		return 0;
	}
	
	
	@Override
	public byte[] getFieldData() {
        ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		 
		
		stream.write((byte)Len);//总长度
		
		try {
			if(appver.length()!=8)
			  appver="11111112";
			stream.write(StringUtil.str2Bcd(appver));
			stream.write(("0").getBytes(charset));  //处理模式
			stream.write((byte)ParaCount);//总条数
			
			for(int i=0;i<ParaCount;i++) {
				int bt= Integer.parseInt(Paraisused.get(i));
				stream.write((bt+"").getBytes(charset)); 
				
				stream.write(StringUtil.str2Bcd(Paralevel.get(i)));
				
			 
				stream.write(Parabusicode.get(i).getBytes(charset)); 
				
				stream.write(("0").getBytes(charset));//冲正代码 
				
				stream.write((byte)Integer.parseInt(ParafunID.get(i)));
				
				stream.write(("0").getBytes(charset));//中心代码 				
				 
				int plen=ParaCodeNo.get(i).length()/2; //流程代码的长度
				stream.write(plen); 
				if (plen>0)
				stream.write(StringUtil.hexStringToByte(ParaCodeNo.get(i)));//数据
				
			    plen=Parastr.get(i).getBytes(charset).length; //当前记录的长度
				stream.write(plen);   
				stream.write(Parastr.get(i).getBytes(charset));//数据
				 } 			 
				
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	
	//paraCount本次要更新的菜单记录条数
	//paraisused本条菜单是否可能 0可用
	//paralevel菜单级别
	//parabusicode交易代码
	//parafunID功能提示索引
	//paraCodeNo流程代码
	 
	//parastr菜单内容
	public void setPara(int paraCount,
			ArrayList<String> paraisused,
			ArrayList<String> paralevel,
			ArrayList<String> parabusicode,
			ArrayList<String> parafunID,
			ArrayList<String> paraCodeNo,
			ArrayList<String> parastr) {
		Len=4+1+1; 
		 
		ParaCount=paraCount;
		for(int i=0;i<paraCount;i++) {
		  Len=Len+9;
		  Len=Len+1;
		  Len=Len+paraCodeNo.get(i).length()/2; 
		  Len=Len+1;
		  Len=Len+parastr.get(i).getBytes(charset).length;
		  
		 }
		 Paraisused=paraisused;
		 Paralevel=paralevel;
		 Parabusicode=parabusicode;
		 ParafunID=parafunID;
		 ParaCodeNo=paraCodeNo;
		 Parastr=parastr;
		 
	}
	
}
