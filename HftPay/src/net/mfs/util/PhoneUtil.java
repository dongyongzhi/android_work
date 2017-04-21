package net.mfs.util;

//using System.Text.RegularExpressions;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {
 
	 public static ArrayList<String> AnalyzePhoneNumber(String strData){
		 ArrayList<String> sl = new ArrayList<String>(); 
		 //Pattern p = Pattern.compile("([0-9]{3}-[0-9]{8}|[0-9]{4}-[0-9]{8})|(((\\([0-9]{3}\\))|([0-9]{3}\\-))?13\\d{9}|15[0-9]{9}|18[0-9]{9})");
		 //Pattern p = Pattern.compile("(([0-9]{3,4}([-| ]{1}))[0-9]{7,8})|(((\\([0-9]{3}\\))|([0-9]{3}\\-))|13\\d{9}|15[0-9]{9}|18[0-9]{9})");
		 //Pattern p = Pattern.compile("[1]{1}[3,5,8,6]{1}[0-9]{9}");
		 Pattern p = Pattern.compile("(1[3,5,8]{1}[0-9]{9})|((0[0-9]{2,3}[-| |)|\\uFF09|\\u3000]*[0-9]{7,8})|([2-9]{1}[0-9]{6,7}))");
		 
		 Matcher matcher = p.matcher(strData);
		 while (matcher.find()) {
		 	String all = matcher.group(0);
		 	all = Pattern.compile("[-| |(|)|\\uFF09|\\u3000]*").matcher(all).replaceAll("");
		 	sl.add(all);
		 }
		 return sl;
	 }
	 
	
	public static void main(String[] args){
			String strData = "我的电话010-1234578 ,手机号13812345678 010-12345678 0515 87206900 15305273036 13305273036 (0514)    87216185 051487657623 0523）78765554";
			 ArrayList<String> sl = PhoneUtil.AnalyzePhoneNumber(strData);
			 for(String ss: sl){
				 System.out.println(ss);
			 }
	}

}

