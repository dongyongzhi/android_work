package com.yifengcom.yfposdemo;

import com.yifengcom.yfpos.service.IService;
import com.yifengcom.yfposdemo.activity.TerminalActivity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class YFApp extends Application {

	public IService iService;
	public Myconn myconn;
	public boolean isBind = false;
	private static YFApp instance;
	private TerminalActivity IdentityActivity;

	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		bind();
	}
	
	public void SetIdentityActivity(TerminalActivity activity){
		this.IdentityActivity=activity;
	}
	public TerminalActivity getIdentityActivity(){
		return this.IdentityActivity;
	}
	

	public static YFApp getApp() {
		return instance;
	}

	private class Myconn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iService = IService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iService = null;
		}

	}

	/** 绑定 */
	public void bind() {
		Intent intent = new Intent();
		intent.setPackage("com.yifengcom.yfpos");
		intent.setAction("com.yifeng.aidl");
		myconn = new Myconn();
		isBind = bindService(intent, myconn, BIND_AUTO_CREATE);
		if (isBind) {
			System.out.println("绑定成功");
		} else {
			System.out.println("绑定失败");
		}
	}

	/** 解绑 */
	public void unBind() {
		if (myconn != null) {
			try {
				unbindService(myconn);
				System.out.println("解除绑定");
				isBind = false;
				myconn = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	
}
