package com.yfcomm.mpos.adapter.bt;

import com.yfcomm.mpos.DeviceInfo;

public class BluetoothDeviceInfo extends DeviceInfo {
 
	private static final long serialVersionUID = 8410776258212849533L;
	
	public BluetoothDeviceInfo(String name,String address) {
		super(name,address);
		this.setDeviceChannel(DeviceInfo.DEVICECHANNEL_BLUETOOTH);
	}
}
