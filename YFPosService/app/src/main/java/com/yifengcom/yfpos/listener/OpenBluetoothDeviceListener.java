package com.yifengcom.yfpos.listener;

/**
 * 打开蓝牙设备
 * @author qc
 *
 */
public interface OpenBluetoothDeviceListener extends ConnectionStateListener {

	/**
	  * 蓝牙绑定成功
	  */
	 void onBluetoothBounded();
	 
	 /**
	  * 正在绑定蓝牙
	  */
	 void onBluetoothBounding();
	 
	 /**
	  * 没有蓝牙设备
	  */
	 void onDetectNoBlueTooth(); 
}
