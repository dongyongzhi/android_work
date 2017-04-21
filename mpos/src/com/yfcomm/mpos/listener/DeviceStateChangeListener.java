package com.yfcomm.mpos.listener;

public interface DeviceStateChangeListener extends ConnectionStateListener {
 	
	/**
	 * 写入数据
	 * @param data 数据内容
	 */
	void onWriteData(byte[] data);
	
	/**
	 * 接收到数据
	 * @param data  数据内容
	 * @param count  总个数
	 */
	void onRecvData(byte[] data, int count);
}
