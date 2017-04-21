package com.yfcomm.mpos.adapter;

import android.content.Context;

import com.yfcomm.mpos.DeviceInfo;
import com.yfcomm.mpos.DeviceSession;
import com.yfcomm.mpos.adapter.bt.BluetoothManager;
import com.yfcomm.mpos.codec.DeviceDecoder;

/**
 * 设备管理类
 * @author qc
 *
 */ 
public class DeviceSessionFactory {

	private static DeviceSession session;
	private static DeviceInfo original;
	
	public static DeviceSession getSession(Context context,DeviceInfo deviceInfo,DeviceDecoder decoder) {
		if(session == null) {
			session = newSession(context,deviceInfo,decoder);
		} else if(session!=null && original.getDeviceChannel()!= deviceInfo.getDeviceChannel()) {
			session.close();
			session = newSession(context,deviceInfo,decoder);
		}
		original = deviceInfo;
		return session;
	}
	
	private static DeviceSession newSession(Context context,DeviceInfo deviceInfo,DeviceDecoder decoder) {
		if(deviceInfo.getDeviceChannel() == DeviceInfo.DEVICECHANNEL_BLUETOOTH) {
			return new BluetoothManager(context, decoder);
		} else {
			return null;
		}
	}
}
