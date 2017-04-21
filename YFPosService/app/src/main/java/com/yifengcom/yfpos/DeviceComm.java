package com.yifengcom.yfpos;

import java.io.Serializable;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.listener.ExecuteListener;
import com.yifengcom.yfpos.listener.ConnectionStateListener;

/**
 * 设置通信接口
 * @author qc
 *
 */
public interface DeviceComm {
	
	
	/**
	 * 连接设备
	 * @param deviceInfo  设备信息
	 */
	void connect(DeviceInfo deviceInfo,long timeout,ConnectionStateListener listener);	 
	
	/**
	 * 写入数据
	 * @param pack  数据包
	 * @exception  超时:TimeoutException , 连接:ConnectionException
	 */
	void write(DevicePackage pack);
	
	/**
	 * 接收数据
	 * @param timeout 超时时长
	 * @return 收到的数据包
	 * @exception  超时:TimeoutException , 连接:ConnectionException
	 */
	DevicePackage recv(Serializable sequence,int timeout);
	
	/**
	 * 接收数据
	 * @param cmd    包指令
	 * @param index  包序号
	 * @return  收到的数据包
	 */
	DevicePackage recv(Serializable sequence);
	
	/**
	 * 执行命令
	 * @param cmd  命令包
	 * @return  应答包
	 */
	DevicePackage execute(DevicePackage cmd);
	
	/**
	 *  执行命令
	 * @param cmd   命令包
	 * @param timeout  超时时长
	 * @return  应答包
	 */
	DevicePackage execute(DevicePackage cmd , int timeout);
	
	/**
	 * 执行命令
	 * @param cmd       命令包
	 * @param listener  回调
	 * @return
	 */
	void execute(DevicePackage cmd,ExecuteListener listener);
	
	/**
	 * 设备是否存在
	 * @return
	 */
	boolean connected();
	
	/**
	 * 关闭设备
	 */
	void close();
	
	/**
	 * 获取设备信息
	 * @return
	 */
	DeviceInfo getDeviceInfo();
	
	/**
	 * 取消指令
	 */
	void cancel();
}
