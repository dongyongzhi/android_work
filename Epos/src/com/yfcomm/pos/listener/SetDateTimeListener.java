package com.yfcomm.pos.listener;

/**
 * 设置时间回调
 * @author qc
 *
 */
public interface SetDateTimeListener {

	void onSetDateTimeSuccess();
	
	void onError(String code,String message);
	
}
