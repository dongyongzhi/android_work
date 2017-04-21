package com.yfcomm.mpos;

import com.yfcomm.mpos.listener.DeviceSearchListener;

/**
 * 设备检测
 * @author qc
 *
 */
public interface DeviceDetect {
	
	/**
	 * 查找设备
	 * @param timeout 超时时长
	 * @param listener
	 */
	void startSearchDevice(int timeout,DeviceSearchListener listener);
	
	/**
	 * 停止查找设备
	 */
	void stopSearchDevice();
}
