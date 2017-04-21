package com.yfcomm.mpos.listener.support;

import com.yfcomm.mpos.ErrorCode;
import com.yfcomm.mpos.YFLog;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.listener.ErrorListener;
import com.yfcomm.mpos.listener.ExecuteListener;
import com.yfcomm.mpos.listener.TimeoutListener;

/**
 * 应答监听
 * @author qc
 *
 * @param <Ack>
 */
public abstract class AckListener<Listener> implements ExecuteListener {
	
	private final static YFLog logger = YFLog.getLog(AckListener.class);
	
	private final Listener listener;
	
	public AckListener(Listener listener) {
		this.listener = listener;
	}
	
	@Override
	public void onError(int errorCode, String errorMessage) {
		//分发错误信息
		if(this.listener==null) {
			return;
		}
		
		if(errorCode == ErrorCode.TIMEOUT.getCode()) {
			//有超时错误时则分发到超时
			if(this.listener instanceof TimeoutListener) {
				((TimeoutListener) this.listener).onTimeout();
				
			} else if(this.listener instanceof ErrorListener) {
				((ErrorListener) this.listener).onError(errorCode, errorMessage);
			}
			
		} else if(this.listener instanceof ErrorListener) {
			//以错误返回
			((ErrorListener) this.listener).onError(errorCode, errorMessage);
		}
	}

	@Override
	public void onRecv(DevicePackage pack) {
		try {
			doCallback(listener,pack);
		} catch(Exception ex) {
			 if(this.listener instanceof ErrorListener) {
				 ((ErrorListener) this.listener).onError(ErrorCode.UNKNOWN.getCode(), ex.getMessage());
			 }
			 logger.e(ex.getMessage(), ex);
		}
	}
	
	public abstract void doCallback(Listener listener,DevicePackage pack);
	 
}
