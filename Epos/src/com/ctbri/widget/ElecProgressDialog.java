package com.ctbri.widget;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.net.MessageType;
import com.ctbri.pos.ElecPosService;
import com.ctbri.push.BroadcastReceiverService;
import com.ctbri.push.NotificationService;

/**
 * 进度处理状态显示 
 * @author qin
 * 
 * 2012-12-8
 */
public class ElecProgressDialog implements OnCancelListener {
	
	private final BroadcastReceiverService  receiver; 
	private ElecPosService  pos;
	private TextView mMessage,mTitle;
	private Dialog dialog;
	private String message;
	
	public ElecProgressDialog(Context context) {
		//AlertDialog.Builder builder = new AlertDialog.Builder(context);
		dialog = new Dialog(context,R.style.dialog);
		dialog.setCancelable(false);
		dialog.setOnCancelListener(this);
		//builder.setCancelable(false);
		//builder.setOnCancelListener(this);
		//dialog = builder.create();
		
		//添加接收信息广播
		this.receiver = new BroadcastReceiverService(context){
			@Override
			protected void onServiceChanged(String what,int state) {
				switch(state){
				
				case NotificationService.STATE_SERVICE_START:
					if(what.equals(MessageType.SIGN)) {
						ElecProgressDialog.this.setMessage("正在签到,请在POS机上操作");
					} else {
						ElecProgressDialog.this.setMessage("开始交易...");
					}
					break;
					
				case NotificationService.STATE_SERVICE_COMPLETE:
					ElecProgressDialog.this.setMessage("交易完成");
					break;
					
				case NotificationService.STATE_REQUEST_POS:
					if(what.equals(MessageType.PURCHASE) || what.equals(MessageType.QUERYBALANCE)){
						ElecProgressDialog.this.setMessage("请刷卡或输入密码...");
					}else
						ElecProgressDialog.this.setMessage("正在获取POS终端数据...");
					ElecProgressDialog.this.setCancelable(true); //可以取消对话框
					break;
					
				case NotificationService.STATE_REQUEST_POSP:
					ElecProgressDialog.this.setMessage("正在发送数据...");
					ElecProgressDialog.this.setCancelable(false);
					break;
					
				case NotificationService.STATE_POSP_RESPONSE:
					ElecProgressDialog.this.setMessage("正在接收数据...");
					ElecProgressDialog.this.setCancelable(false);
					break;
					
				case NotificationService.STATE_SIGN:
					ElecProgressDialog.this.setMessage("正在签到,请在POS机上操作");
					ElecProgressDialog.this.setCancelable(false);
					break;
					
				case NotificationService.STATE_REVERSAL:
					ElecProgressDialog.this.setMessage("正在冲正...");
					ElecProgressDialog.this.setCancelable(false);
					break;
				}
			}
		};
	}
	
	public void setCancelable(boolean cancelable){
		this.dialog.setCancelable(cancelable);
	}
	
	public boolean isShowing(){
		return this.dialog.isShowing();
	}
	
	public void setMessage(CharSequence message){
		if(!this.dialog.isShowing())
			this.message = message.toString();
		else
			mMessage.setText(message);
	}
	
	public void setPOS(ElecPosService pos){
		this.pos = pos;
	}
	
	public void setTitle(String title){
		mTitle.setText("正在处理");
	}
 
	public void show(){
		if(!dialog.isShowing()){
			dialog.show();
			
			dialog.setContentView(R.layout.progress_dialog);
			mMessage = (TextView)dialog.findViewById(R.id.mTextMessage);
			mTitle = (TextView)dialog.findViewById(R.id.pTitle);
			mTitle.setText("正在处理");
			if(message!=null)
				setMessage(message);
			
		    this.receiver.registerReceiver();
		}
	}
	 
	
	public void dismiss(){
		dialog.dismiss();
		this.receiver.unregisterReceiver();
	}

	@Override
	public void onCancel(DialogInterface arg0) {
		if(pos!=null){  //结束终端指令
			pos.endCmd();
		}
		this.receiver.unregisterReceiver();
	}
}
