package com.yfcomm.mpos.adapter.bt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.yfcomm.mpos.DeviceDetect;
import com.yfcomm.mpos.YFLog;
import com.yfcomm.mpos.listener.DeviceSearchListener;
import com.yfcomm.mpos.utils.BlueUtils;

/**
 * 蓝牙设备查找
 * @author qc
 *
 */
public class BluetoothDeviceDetect extends BroadcastReceiver implements DeviceDetect {

	private final static YFLog logger = YFLog.getLog(BluetoothDeviceDetect.class);
	
	private boolean registerReceiver = false;
	private final Context context;
	private final BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
	private DeviceSearchListener listener;
	
	
	public BluetoothDeviceDetect(Context context) {
		this.context = context;
	}
	
	@Override
	public void startSearchDevice(int timeout,DeviceSearchListener listener) {
		registerReceiver();
		BlueUtils.setDiscoverableTimeout(bt, timeout);
		this.listener = listener;
		//打开设备
		if(bt!=null && !bt.isEnabled()) {
			bt.enable();
			
		} else if(bt.isEnabled()) {
			//连接设备
			bt.startDiscovery();
		}
	}

	@Override
	public void stopSearchDevice() {
		if(bt!=null) {
			bt.cancelDiscovery();
		}
	}
	
	private void registerReceiver() {
		// 注册查找 蓝牙设备的intent 并返回结果
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(this, filter);
        this.registerReceiver = true;
	}
	
	private void unregisterReceiver() {
		if(this.registerReceiver) {
			context.unregisterReceiver(this);
			this.registerReceiver = false;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) { 
			//蓝牙状态变更，并连接己配对的设备
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
			switch (state){
			case BluetoothAdapter.STATE_OFF:
				unregisterReceiver();
 				logger.d("BluetoothAdapter.STATE_OFF");
				break;
				
			case BluetoothAdapter.STATE_ON:
				bt.startDiscovery();
				break;
			}
			
		} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			//发现设备
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			logger.d("found:%s %s",device.getName(),device.getAddress());
			this.listener.foundOneDevice(new BluetoothDeviceInfo(device.getName(),device.getAddress()));
			
		} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			unregisterReceiver();
			this.listener.discoveryFinished();
		}
	}
}
