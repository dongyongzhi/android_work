package com.yifengcom.yfpos.adapter.bt;

import com.yifengcom.yfpos.DeviceInfo;

public class BluetoothDeviceInfo extends DeviceInfo {
 
	private static final long serialVersionUID = 8410776258212849533L;
	
	public BluetoothDeviceInfo(String name,String address) {
		super(name,address);
		this.setDeviceChannel(DeviceInfo.DEVICECHANNEL_BLUETOOTH);
	}
}
