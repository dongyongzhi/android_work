package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdICControl extends EposCommand{
	private int  Len;
	
	private ArrayList<String> ParaList = new ArrayList<String>();
	private int ParaCount; 
	private ArrayList<String> ParaNoList = new ArrayList<String>();
	private Charset charset = Charset.forName("GB2312");
	public CmdICControl(){
		command = EposProtocol.CMD2_IC_CONTROL;
		}
	
	public int getICCommandCount()
	{
	   return ParaCount;
	   
	}
	public ArrayList<String> getICCommand()
	{
		return ParaList;
	}
	public int decodeFromBuffer(IoBuffer in) {
		short len=in.getShort();
		byte re=in.get();
		if (re!=0)
		{
			return 0;
		}
		ParaCount=in.get(); 
		ParaList.clear();
		for(byte i=0;i<ParaCount;i++)
		{
			short slen=in.getShort();
			byte[] s1 = new byte[slen];
			in.get(s1);
			ParaList.add(StringUtil.bytesToHexString(s1));
		}
		return 0;
	}
	//可以放到公共类里用
	public static byte[] shortToBytes(short n) {
		byte[] b = new byte[2];
		b[1] = (byte) (n & 0xff);
		b[0] = (byte) ((n >> 8) & 0xff);
		return b;
		}
	
	@Override
	public byte[] getFieldData() {
        ByteArrayOutputStream stream = new  ByteArrayOutputStream();
		 
		byte[] lent=shortToBytes((short)Len);		 
        
		
		try {
			stream.write(lent);//总长度
			stream.write(ParaCount);//总条数
			
			for(int i=0;i<ParaCount;i++) {
				int bt= Integer.parseInt(ParaNoList.get(i));
				stream.write((bt+"").getBytes(charset)); //指令类型
				  
				 if(bt==2)
				 {
				  int plen=ParaList.get(i).length()/2; //当前记录的长度
				  lent=shortToBytes((short)plen);
				  stream.write(lent);   
				  stream.write(StringUtil.hexStringToByte(ParaList.get(i)));//指令数据
				 } 		
			}
				
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	public void setPara(int paraCount,ArrayList<String> parastr,ArrayList<String> paraNo) {
		 //投置指令
		ParaCount=ParaCount+ paraCount;
		for(int i=0;i<paraCount;i++) {
		  Len++;
		  if (paraNo.get(i).equals("2"))
		  {
			 Len=Len+2;
		     Len=Len+parastr.get(i).length()/2;
		  }
		 }
		ParaList.addAll(parastr);
		ParaNoList.addAll(paraNo);
		 
	}
	public void initIC()//初始化
	{
		Len=1;
		ParaCount=0;
		
	}
	public void Poweron()   //上电
	{
		ParaCount++;
		Len++;
		ParaNoList.add("0");
		ParaList.add("");
	}
	public void Poweroff()   //下电
	{
		ParaCount++;
		Len++;
		ParaNoList.add("1");
		ParaList.add("");
	}
	
}
