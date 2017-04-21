package com.yfcomm.pos.ui;

import com.yfcomm.pos.bt.DeviceService;
import com.yfcomm.pos.bt.device.BluetoothSession;
import com.yfcomm.pos.bt.device.DeviceComm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/**
 * device 帮助类
 * @author qc
 *
 */
public class DeviceServiceActivity extends Activity {
	
	private DeviceComm device;
	private DeviceBroadcastReceiver deviceBroadcastReceiver;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//绑定 device 服务
		final Intent deviceIntent = new Intent(this,DeviceService.class); 
		this.bindService(deviceIntent,deviceServiceConnection, Context.BIND_AUTO_CREATE);
		
		//注册接收广播事件
		deviceBroadcastReceiver = new DeviceBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(DeviceComm.CONNECT_CHANGE_MESSAGE);  
		registerReceiver(deviceBroadcastReceiver, intentFilter);
	}
	
	/**
	 * 连接设备事件
	 */
	protected void onConnected() {
		
	}
	
	/**
	 * 连接设备失败
	 */
	protected void onConnectFailed() {
		
	}
	
	/**
	 * 服务己启动
	 */
	protected void onDeviceReady(DeviceComm device) {
		
	}
	
	/**
	 * 设备断开事件
	 */
	protected void onDisconnected() {
		
	}

	public final DeviceComm getDevice() {
		return device;
	}
	
	protected void onDestroy() { 
		super.onDestroy();
		this.unbindService(deviceServiceConnection);
		unregisterReceiver(deviceBroadcastReceiver);
	}
	
	private  ServiceConnection deviceServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			device = ((DeviceService.LocalBinder)service).getService();
			onDeviceReady(device);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			device = null;
		}
	};
	
	/**
	 * 接收设备连接状态广播信息
	 * @author qc
	 *
	 */
	private class DeviceBroadcastReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context ctx, Intent intent) {
			String action = intent.getAction();
			
			//设备连接状态改变
			if(action.equals(DeviceComm.CONNECT_CHANGE_MESSAGE)) {
				int code = intent.getIntExtra(DeviceComm.CONNECT_STATE, BluetoothSession.MESSAGE_CONNECT_FAILED);
				if(code == BluetoothSession.MESSAGE_CONNECT_SUCCESS) {
					onConnected();
				} else if(code == BluetoothSession.MESSAGE_CONNECT_FAILED) {
					onConnectFailed();
				} else if(code == BluetoothSession.MESSAGE_CONNECT_CLOSE) {
					onDisconnected();
				}
			}
		}
	}
}
