package com.yf.sevice;

import com.yifengcom.yfpos.service.DeviceModel;
import com.yifengcom.yfpos.service.IService;
import com.yifengcom.yfpos.service.listener.CallBackListener;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.YFComm;
import android.util.IYFSevice;
import android.util.Log;
import android.util.YFCommReadSnCallBack;

public class SeviceForYFComm extends Service {

	private IService iService = null;
	private Boolean isBind = false;
	private Myconn myconn;
	private final static String TAG = "SeviceForYFComm";
	private YFCommReadSnCallBack yfcallback = null;

	@Override
	public IBinder onBind(Intent intent) {
		return new MyServiceImpl();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		final Intent intent = new Intent();
		intent.setAction("com.yifeng.aidl");
		intent.setPackage("com.yifengcom.yfpos");
		startService(intent);
		Log.e(TAG, "startService com.yifengcom.yfpos...");
	}

	public class MyServiceImpl extends IYFSevice.Stub {
		@Override
		public void ReadSn_Ver(YFCommReadSnCallBack callback) throws RemoteException {
			yfcallback = callback;
			// yfcallback.ReadSn_Ver(1, "执行成功!");
			if (!isBind || iService == null) {
				bind();
			} else {
				try {
					iService.getPsamInfo(mCallBack);
					iService.onGetDeviceInfo(mCallBack);
				} catch (RemoteException e) {
					Log.e(TAG, "MyServiceImpl=" + e.getMessage());
				}
			}
		}
	}

	public void bind() {
		final Intent intent = new Intent();
		intent.setAction("com.yifeng.aidl");
		intent.setPackage("com.yifengcom.yfpos");
		myconn = new Myconn();
		isBind = bindService(intent, myconn, Context.BIND_AUTO_CREATE);
		if (isBind) {
			Log.e(TAG, "Bind RemoteSevice Succ");
		} else {
			Log.e(TAG, "Bind RemoteSevice failed");
		}
	}

	private class Myconn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iService = IService.Stub.asInterface(service);

			if (iService != null)
				try {
					iService.getPsamInfo(mCallBack);
				} catch (RemoteException e) {
					Log.e(TAG, "onServiceConnected:" + e.getMessage().toString());
				}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iService = null;
		}
	}

	private CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int errorCode, final String errorMessage) throws RemoteException {
			try {
				if (yfcallback != null)
					yfcallback.ReadSn_Ver(0, "写入超时!");
			} catch (Exception e) {
				Log.e(TAG, "onError yfcallback is changed:" + e.getMessage().toString());
			}

		}

		@Override
		public void onGetDeviceInfoSuccess(DeviceModel deviceModel) throws RemoteException {

			String sn = deviceModel.getSn();
			String ver = deviceModel.getTerVersion();

			Log.i(TAG, "Remote Result: sn=" + sn + " ver=" + ver);
			try {
				YFComm.setCoCpuVersion(ver, SeviceForYFComm.this);
				if (YFComm.setSnCode(sn, SeviceForYFComm.this)) {
					if (yfcallback != null)
						yfcallback.ReadSn_Ver(1, "执行成功!");
				} else {
					if (yfcallback != null)
						yfcallback.ReadSn_Ver(0, "写入失败!");
				}
				Log.e(TAG, "SeviceForYFComm onGetDeviceInfoSuccess succ");

			} catch (RuntimeException e) {

				if (YFComm.snCode().equals(sn)) {
					if (yfcallback != null)
						yfcallback.ReadSn_Ver(1, "执行成功!");
				} else {
					if (yfcallback != null)
						yfcallback.ReadSn_Ver(0, "写入异常!");
				}
				Log.e(TAG, "SeviceForYFComm onGetDeviceInfoSuccess failed");

			} catch (Exception e) {
				Log.e(TAG, "onGetDeviceInfoSuccess yfcallback is changed:" + e.getMessage().toString());
			}
		}

		@Override
		public void onReadPsamNo(final String num) {
			Log.i(TAG, "Remote onReadPsamNo=" + num);
			YFComm.setPsamInfo(num, SeviceForYFComm.this);

			/*
			 * Timer timer = new Timer(); timer.schedule(new TimerTask(){
			 * 
			 * @Override public void run() { if(iService != null){ try {
			 * iService.onGetDeviceInfo(mCallBack); } catch (RemoteException e)
			 * { Log.e(TAG, e.getMessage().toString()); } }
			 * 
			 * } },10);
			 */
		};
	};

	public void unBind() {
		if (myconn != null) {
			try {
				unbindService(myconn);
				isBind = false;
				myconn = null;
				iService = null;
			} catch (Exception e) {
				Log.e(TAG, "unBind failed");
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unBind();
	}

}