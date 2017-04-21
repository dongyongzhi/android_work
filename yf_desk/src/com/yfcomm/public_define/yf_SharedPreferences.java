package com.yfcomm.public_define;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class yf_SharedPreferences{
	
	public static void write_conf(Context context,String label, String content)//写文件
	{
		SharedPreferences settings = context.getSharedPreferences(
						label, 0);
		Editor editor = settings.edit();
		editor.putString(label,content);
		editor.commit();
		
	}
	
	public static String Read_conf(Context context,String label){
		//读文件
		SharedPreferences settings = context.getSharedPreferences(
						label, 0);
		
		return settings.getString(label,"");
	}
	

	
}