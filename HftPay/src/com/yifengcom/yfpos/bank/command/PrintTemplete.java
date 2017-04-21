package com.yifengcom.yfpos.bank.command;

import java.util.ArrayList;

public class PrintTemplete {
	
	private String data="";
	private ArrayList<String> printInfo = new ArrayList<String>();
	public void setData(String info){
		ArrayList<String> list = decode(info);
		if (list != null){
			data = info;
			printInfo = list;
		}
	}
	
	public ArrayList<String> getPrintInfo(){
		return printInfo;
	}
	
	private ArrayList<String> decode(String info){
		return null;
	}
}
