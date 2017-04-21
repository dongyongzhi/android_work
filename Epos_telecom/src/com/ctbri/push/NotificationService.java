package com.ctbri.push;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

/**
 * 通知服务
 * @author qin
 * 
 * 2012-11-30
 */
public final class NotificationService {
	/**服务类*/
	public final static String SERVICE_STATE_CHANGE = "com.elec.android.push.SERVICE_STATE_CHANGE";
	public final static String SERVICE_STATE_CHANGE_MESSAGE = "SERVICE_STATE_CHANGE_MESSAGE";
	public final static String SERVICE_RESULT = "TRANS_RESULT";
	public final static String SERVICE_STATE = "SERVICE_STATE";
	public final static String SERVICE_TYPE = "SERVICE_TYPE";
	
	/**服务状态*/
	public final static int STATE_SERVICE_START = 0;     //服务开始
	public final static int STATE_SERVICE_COMPLETE = 2;  //服务完成
	
	public final static int STATE_REQUEST_POS   = 3;//请求pos机交易
	public final static int STATE_REQUEST_POSP  = 4;//请求posp平台交易
	public final static int STATE_POSP_RESPONSE = 5;//posp平台返回交易
	public final static int STATE_SIGN          = 6;//签到
	public final static int STATE_REVERSAL      = 7;//冲正
	
	
	private final  Context  context;
	
	public  NotificationService(Context context){
		this.context = context;
	}
	
	/**
	 * 服务状态变更通知
	 * @param what    业务类型
	 * @param state   正在处理的业务状态
	 */
	public void notificationTransStateChange(int what,int state){
		if(context==null)
			return;
		Intent intent = new Intent(SERVICE_STATE_CHANGE);
		intent.putExtra(SERVICE_STATE, state);
		intent.putExtra(SERVICE_TYPE, what);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 服务完成通知
	 * @param parcelable  返回内容
	 */
	public void notificationTransResult(Parcelable parcelable){
		if(context==null)
			return;
		
		Intent intent = new Intent(SERVICE_STATE_CHANGE);
		intent.putExtra(SERVICE_STATE, STATE_SERVICE_COMPLETE);
		intent.putExtra(SERVICE_RESULT,parcelable);
		context.sendBroadcast(intent);
	}
	/**
	 * 按og
	 * @param state
	 * @return
	 */
	public static String getMessage(int state){
		return"";
	}
}
