package com.yfcomm.mpos.listener;

/**
 * 错误监听
 * @author qc
 *
 */
public interface ErrorListener {

	/**
	 * 出错
	 * @param errorCode    错误代码
	 * @param errorMessage 错误信息描述
	 */
	void onError(int errorCode,String errorMessage);
	
}
