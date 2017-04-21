package com.hftcom;

import com.hftcom.utils.Config;
import com.yifengcom.yfpos.callback.CallBackListener;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class PsamService extends Service {
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		try {
			YFApp.getApp().iService.getPsamInfo(mCallBack);
		}catch(Exception e){
			e.printStackTrace();
			handler.obtainMessage(11).sendToTarget();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	CallBackListener mCallBack = new CallBackListener() {
		@Override
		public void onError(final int errorCode, final String errorMessage) {
			handler.obtainMessage(11).sendToTarget();
		}
		
		@Override
		public void onReadPsamNo(final String num) {
			handler.obtainMessage(10, num).sendToTarget();
		};
	};
	
	private Handler handler = new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 if(msg.what == 10){
				 String num  = (String) msg.obj;
				 YFApp.getApp().writeSDCard(Config.psam_text+num, Config.pName);
			 }
			 stopSelf();
		 };
	};
}
