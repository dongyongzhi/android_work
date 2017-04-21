package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdUpdatePrint extends EposCommand{
	private int Len;
	private Charset charset = Charset.forName("GB2312");
	private ArrayList<String> ParaList = new ArrayList<String>();
	private int ParaCount; 
	private ArrayList<String> ParaNoList = new ArrayList<String>();
	private String value;
	private String appver="";
	public void setappver(String sver){
		appver=sver;
	}
	public CmdUpdatePrint(){
		command = EposProtocol.CMD2_UPDATE_PRINT;
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
			//	stream.write(StringUtil.str2Bcd(appver));
			stream.write((byte)ParaCount);//总条数
			
			for(int i=0;i<ParaCount;i++) {
				 stream.write(Integer.parseInt(ParaNoList.get(i)));  //记录号
				 /*
				 int plen=ParaList.get(i).getBytes(charset).length; //当前记录的长度
					stream.write(plen);   
					stream.write(ParaList.get(i).getBytes(charset));//数据
					*/
				int	 plen=ParaList.get(i).length()/2; //当前记录的长度
						stream.write(plen);   
						stream.write(StringUtil.hexStringToByte(ParaList.get(i)));//数据  
				 } 			 
				
			 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stream.toByteArray();
	}
	
	//paraCount本次要更新的打印模板条数
	//paraNo更新的打印模板编号
	//parastr更新的打印模板内容
	public void setPara(int paraCount,ArrayList<String> parastr,ArrayList<String> paraNo) {
		Len=1; 
		 
		ParaCount=paraCount;
		for(int i=0;i<paraCount;i++) {
		  Len++;
		  Len++;
		   
		  Len=Len+parastr.get(i).length()/2;
		 }
		ParaList=parastr;
		ParaNoList=paraNo;
		 
	}
	
}
