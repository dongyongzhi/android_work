package com.hftcom.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hftcom.R;

/**
 * comment:弹出框类
 * 
 * @author:ZhangYan Date:2012-7-24
 */
public class DialogUtil {
	private Activity context;

	public DialogUtil(Activity context) {
		this.context = context;
	}

	/**
	 * 自定义退出程序
	 */
	public void doAdvanceExit() {
		final Dialog builder = new Dialog(context, R.style.dialog);
		builder.setContentView(R.layout.confirm_dialog);
		TextView ptitle = (TextView) builder.findViewById(R.id.pTitle);
		TextView pMsg = (TextView) builder.findViewById(R.id.pMsg);
		ptitle.setText(context.getString(R.string.alert_dialog_finish_title));
		pMsg.setText(context.getString(R.string.alert_dialog_finish));
		final Button confirm_btn = (Button) builder.findViewById(R.id.confirm_btn);
		Button cancel_btn = (Button) builder.findViewById(R.id.cancel_btn);
		confirm_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				builder.dismiss();
				ActivityStackControlUtil.finishProgram();// 退出
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		});

		cancel_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
		builder.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域关闭Dialog
		builder.show();
	}
	
	public void doExitProcess() {
		ActivityStackControlUtil.finishProgram();// 退出
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}
	
	/**
	 * 对话框
	 * @param title    标题
	 * @param message  显示信息
	 */
	public static void dialog(Context ctx,String title,String message,final DialogInterface.OnClickListener onClickListener,final int dialogId){
		final Dialog builder = new Dialog(ctx, R.style.dialog);
		builder.setContentView(R.layout.dialog);
		
		((TextView) builder.findViewById(R.id.pTitle)).setText(title);//标题
		((TextView) builder.findViewById(R.id.pMsg)).setText(message);//显示信息
		
		((Button) builder.findViewById(R.id.pBtn)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				onClickListener.onClick(builder,dialogId);
			}
		});
		builder.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域关闭Dialog
		builder.show();
	}
	
	/**
	 * 系统自带弹出框
	 */
	public void doExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(R.string.alert_dialog_finish_title);
		builder.setMessage(R.string.alert_dialog_finish);
		builder.setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// CommonUtil.doLogOut();
						// exit();
					}
				});
		builder.setNegativeButton(R.string.alert_dialog_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		builder.show();
	}

	/**
	 * 
	 * @param 信息提示长时间显示
	 */
	public void showToast(String context) {
		Toast.makeText(this.context, context, Toast.LENGTH_LONG).show();
	}

	/**
	 * 
	 * @param 信息提示短时间显示
	 */
	public void shortToast(String context) {
		Toast.makeText(this.context, context, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 提示对话框
	 * 
	 * @param context
	 * @return
	 */
	public void showMsg(String title, String msg) {
		final Dialog builder = new Dialog(context, R.style.dialog);
		builder.setContentView(R.layout.dialog);
		TextView ptitle = (TextView) builder.findViewById(R.id.pTitle);
		TextView pMsg = (TextView) builder.findViewById(R.id.pMsg);
		ptitle.setText(title);
		pMsg.setText(msg);
		Button btn = (Button) builder.findViewById(R.id.pBtn);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				builder.dismiss();
			}
		});
		// builder.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		builder.show();
	}

	/**
	 * 网络连接异常
	 * 
	 * @param context
	 */
	public void alertNetError() {
		final Dialog dialog = new Dialog(context, R.style.dialog);
		dialog.setContentView(R.layout.network_set);
////		final Button update = (Button) dialog.findViewById(R.id.update);
//		update.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
////				context.startActivity(new Intent(
////						android.provider.Settings.ACTION_WIRELESS_SETTINGS));// 网络设置
//				Intent intent = new  Intent("android.settings.WIRELESS_SETTINGS");
//				intent.addCategory("android.intent.category.DEFAULT");
//				context.startActivity(intent);
//				context.finish();
//			}
//		});
//		final Button close = (Button) dialog.findViewById(R.id.close);
//		close.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				dialog.dismiss();
//				// context.finish();//退出
//			}
//		});
		dialog.show();
	}
}
