package com.yfcomm.mpos.listener;

/**
 * 打开设备回调
 * @author qc
 *
 */
public interface ConnectionStateListener {

	/**
	 * 连接成功
	 */
	void onConnected();
	
	/**
	 * 断开连接
	 */
	void onDisconnect();
	
	/**
	 * 打开失败
	 * @param errCode  错误编码
	 * @param errDesc  错误信息
	 */
	void onError(int errCode,String errDesc);
}
