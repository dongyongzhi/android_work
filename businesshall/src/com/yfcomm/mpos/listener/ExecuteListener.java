package com.yfcomm.mpos.listener;

import com.yfcomm.mpos.codec.DevicePackage;

/**
 * 执行指令回调
 * @author qc
 *
 */
public interface ExecuteListener  extends ErrorListener{

	/**
	 * 接收到数据包
	 * @param pack
	 */
	void onRecv(DevicePackage pack);
	
}
