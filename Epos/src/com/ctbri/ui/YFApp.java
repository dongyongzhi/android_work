package com.ctbri.ui;


import com.yifeng.skzs.util.AppContext;

import android.app.Application;
import android.content.res.Configuration;

public class YFApp extends Application{
	public static final String	TAG	= "YFApp";
	@Override
	public void onCreate()
	{
		super.onCreate();

		AppContext.init(this);

		
	}
	@Override
	public void onTerminate()
	{
     
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{

	}

}
