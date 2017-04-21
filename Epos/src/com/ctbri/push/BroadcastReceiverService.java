package com.ctbri.push;

import com.ctbri.utils.ElecLog;
import com.yfcomm.pos.utils.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 交易状态接收
 * @author qin
 * 
 * 2012-11-30
 */
public abstract class BroadcastReceiverService extends BroadcastReceiver {
	
	public final Context context;
	private boolean register = false;
	
	public BroadcastReceiverService(Context context){
		this.context = context;
	}
	
	public void registerReceiver(){
		// 注册
		if(register)
			return;
		
        IntentFilter filter = new IntentFilter(NotificationService.SERVICE_STATE_CHANGE);
        context.registerReceiver(this, filter);
        register = true;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		ElecLog.d(BroadcastReceiverService.class, action);
		//交易状态变更
		if(action.equals(NotificationService.SERVICE_STATE_CHANGE)){
			int state = intent.getIntExtra(NotificationService.SERVICE_STATE, NotificationService.STATE_SERVICE_START);
			String what = intent.getStringExtra(NotificationService.SERVICE_TYPE);
			if(StringUtils.isEmpty(what)) {
				onServiceChanged(what,state);
			}
		}
	}
	
	/**
	 * 业务状态变更事件
	 * @param message  附加信息
	 */
	protected abstract void onServiceChanged(String what,int state);
	
	
	public void unregisterReceiver(){
		if(register)
			 context.unregisterReceiver(this);
		register = false;
	}
}
