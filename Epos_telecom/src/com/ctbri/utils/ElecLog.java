package com.ctbri.utils;

import android.util.Log;

import com.ctbri.Constants;

public class ElecLog {
	private final static String APP = "elec_";
	
	public static void d(Class<?> cls,CharSequence message){
		if(Constants.DEBUG && message!=null)
			Log.d(String.format("%s%s", APP,cls.getName()), message.toString());
	}
	
	public static void i(Class<?> cls,CharSequence message){
		if(message!=null)
			Log.i(String.format("%s%s", APP,cls.getName()), message.toString());
	}
	
	public static void e(Class<?> cls,CharSequence message){
		if(message!=null)
			Log.e(String.format("%s%s", APP,cls.getName()), message.toString());
	}
	
	public static void e(Class<?> cls,CharSequence message,Throwable e){
		if(message!=null)
			Log.e(String.format("%s%s", APP,cls.getName()), message.toString(),e);
	}
	
	public static void w(Class<?> cls,CharSequence message){
		if(message!=null)
			Log.e(String.format("%s%s", APP,cls.getName()), message.toString());
	}
	
	public static void w(Class<?> cls,Throwable e){
		 Log.w(String.format("%s%s", APP),e);
	}
}
