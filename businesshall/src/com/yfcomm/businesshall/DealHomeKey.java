package com.yfcomm.businesshall;

import com.yfcomm.homekey.HomeWatcher;
import com.yfcomm.homekey.OnHomePressedListener;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.public_define.public_define;

import android.app.Application;
import android.util.Log;

public class DealHomeKey extends Application{
	
	private HomeWatcher mHomeWatcher;
	public final static String TAG = "DealHomeKey";
	
	@Override
	public void onCreate() {
		super.onCreate();
		WatchHomeKEY();
		Log.e(TAG, "Regeist Home Key");
		//byte[] sendbuf=new  byte[1024];
		
	}
	
	public void WatchHomeKEY() {
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {

			@Override
			public void onHomePressed() {
				Log.e(TAG, "退出");
				Release();
			}

			@Override
			public void onHomeLongPressed() {
				Log.e(TAG, "onHomeLongPressed");
			}
		});
		mHomeWatcher.startWatch();
	}

	public void onDestroy(){
	//	Release();
		/*
		if (mHomeWatcher != null)
			mHomeWatcher.stopWatch();*/	
	}
	private void Release() {
		byte[] data = PackageBuilder.syn(PackageBuilder.CMD_CANCEL, null).getPackData();
		if (data != null && public_define.serialport != null) {
			public_define.serialport.getOutputStream(data);
			Log.i(TAG, "发送串口终端数据:" + ByteUtils.printBytes(data, 0, data.length));
		}
		public_define.close();
	}
	
	
	
}
