package com.yifengcom.yfpos.listener;

public interface CalculateMacListener extends ErrorListener{

	/**
	 * 计算成功返回
	 * @param mac
	 */
	void onCalculateMacSuccess(byte[] mac);
}
