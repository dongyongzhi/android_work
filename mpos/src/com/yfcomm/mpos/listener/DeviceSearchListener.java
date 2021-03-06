package com.yfcomm.mpos.listener;

import com.yfcomm.mpos.DeviceInfo;

/**
 * 查找设备监听
 * @author qc
 *
 */
public interface DeviceSearchListener {
	
	/**
	 * 查找完成
	 */
	void discoveryFinished();
	
	/**
	 * 找到一个设备
	 * @param deviceInfo 设备详细信息
	 */
	void foundOneDevice(DeviceInfo deviceInfo);
}
