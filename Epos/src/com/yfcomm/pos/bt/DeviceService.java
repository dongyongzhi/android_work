package com.yfcomm.pos.bt;

import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.bt.device.DeviceComm;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * device 服务\android 服务
 * @author qc
 *
 */
public class DeviceService extends Service {

	private final static YFLog logger = YFLog.getLog(DeviceService.class);
	
	private DeviceComm comm;
	
	@Override
	public IBinder onBind(Intent intent) {
		return new LocalBinder();
	}
	
	/**
	 * 创建服务
	 */
	public void onCreate() {
		logger.d(" onDestroy ");
		comm = new DeviceComm(this);
	}
	
	
	public void onDestroy() {
		super.onDestroy();
		logger.d(" onDestroy ");
		
		if(comm!=null) {
			comm.close();
		}
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		if(bt!=null) {
			bt.disable();
		}
	}
	
	public class LocalBinder extends Binder {
		
		public DeviceComm getService(){
			return DeviceService.this.comm;
		}
	}
}
