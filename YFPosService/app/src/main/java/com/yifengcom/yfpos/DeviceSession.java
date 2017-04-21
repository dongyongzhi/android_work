package com.yifengcom.yfpos;

import com.yifengcom.yfpos.DeviceInfo;
import com.yifengcom.yfpos.listener.DeviceStateChangeListener;
import com.yifengcom.yfpos.listener.ConnectionStateListener;

/**
 * 设备会话
 * @author qc
 *
 */
public interface DeviceSession {

	void setOnDeviceStateChangeListener(DeviceStateChangeListener listener);
	/**
	 * 打开设备
	 */
	void connect(DeviceInfo deviceInfo,ConnectionStateListener openDeviceListener);
	
	
	void connect(DeviceInfo deviceInfo,long timeout,ConnectionStateListener openDeviceListener);
	
	/**
	 * 是否连接
	 * @return
	 */
	boolean connected();
	
	/**
	 * 关闭连接
	 */
	void close();
	
	/**
	 * 写入数据
	 * @param data  数据内容
	 * @return  是否成功
	 */
	boolean write(byte[] data);
	
	/**
	 * 写入数据
	 * @param data  数据内容
	 * @param offset  
	 * @param count 
	 * @return 是否成功
	 */
	boolean write(byte[] data,int offset,int count);
}
