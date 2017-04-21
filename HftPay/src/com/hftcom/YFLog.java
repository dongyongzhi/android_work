package com.hftcom;

import android.util.Log;

public class YFLog {

	public static final boolean DEBUG = false;
	
	private static final String APP = "YF";
	
	private String className = APP;
	
	private YFLog(Class<?> cls) {
		if(cls!=null) {
			this.className = cls.getName();
		}  
	}
	
	public static YFLog getLog(Class<?> cls) {
		return new YFLog(cls);
	}
	
	public void d(CharSequence message){
		if(DEBUG && message!=null) {
			Log.d(String.format("%s", className), message.toString());
		}
	}
	
	public void d(String message,Object... args) {
		if(message!=null) {
			Log.i(String.format("%s",className), String.format(message.toString(), args));
		}
	}
	
	public  void i(CharSequence message){
		if(message!=null) {
			Log.i(String.format("%s",className), message.toString());
		}
	}
	
	public void e(CharSequence message){
		if(message!=null) {
			Log.e(String.format("%s", className), message.toString());
		}
	}
	
	public void e(CharSequence message,Throwable e){
		if(message!=null) {
			Log.e(String.format("%s", className), message.toString(),e);
		}
	}
	
	public  void w(CharSequence message){
		if(message!=null) {
			Log.e(String.format("%s", className), message.toString());
		}
	}
	
	public  void w(Throwable e){
		 Log.w(String.format("%s", className),e);
	}
}
