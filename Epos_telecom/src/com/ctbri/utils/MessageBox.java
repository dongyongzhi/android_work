package com.ctbri.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;

import com.ctbri.R;
import com.ctbri.widget.ElecDialog;
import com.ctbri.widget.ElecDialog.Style;

/**
 * 提示框
 * @author qin
 * 
 * 2012-11-29
 */
public class MessageBox {
	
	/**
	 * 提醒对话框
	 * @param ctx   context
	 * @param msg   信息内容
	 */
	public static void showWarn(Context ctx,String msg){
		try{
			new ElecDialog.Builder(ctx).setTitle(R.string.dialog_title_warn)
			.setMessage(msg)
			.setConfirmButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d, int arg1) {
					d.dismiss();
				}
			}).show();
			
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	/**
	 * 提醒对话框
	 * @param ctx
	 * @param title
	 * @param msg
	 * @param ok
	 */
	public static void showWarn(Context ctx,String title,String msg,DialogInterface.OnClickListener ok){
		try{
			new ElecDialog.Builder(ctx)
			.setMessage(msg)
			.setTitle(title)
			.setConfirmButtonListener(ok).show();
			
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	/**
	 * 提醒对话框
	 * @param ctx     context
	 * @param msgid   信息内容
	 */
	public static void showWarn(Context ctx,int msgid){
		try{
			new ElecDialog.Builder(ctx).setTitle(R.string.dialog_title_warn)
			.setMessage(msgid)
			.setConfirmButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d, int arg1) {
					d.dismiss();
				}
			}).show();
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	
	/**
	 * 显示错误信息
	 * @param ctx
	 * @param msgid
	 */
	public static void showError(Context ctx,String msg){
		try{
			showError(ctx,msg,new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d, int arg1) {
					d.dismiss();
				}
			});
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	/**
	 * 显示错误信息
	 * @param ctx
	 * @param msg
	 * @param ok
	 */
	public static void showError(Context ctx,String msg,DialogInterface.OnClickListener ok){
		try{
			new ElecDialog.Builder(ctx).setTitle(R.string.dialog_title_warn)
			.setMessage(msg)
			.setConfirmButtonListener(ok).show();
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	/**
	 * 显示错误信息
	 * @param ctx
	 * @param msg
	 * @param ok
	 */
	public static void showError(Context ctx,SpannableString msg,DialogInterface.OnClickListener ok){
		try{
			new ElecDialog.Builder(ctx).setTitle(R.string.dialog_title_warn)
			.setMessage(msg)
			.setConfirmButtonListener(ok).show();
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	
	public static void showWarn(Context ctx,String msg,DialogInterface.OnClickListener cancel){
		try{
			new ElecDialog.Builder(ctx).setTitle(R.string.dialog_title_warn)
			.setMessage(msg)
			.setConfirmButtonListener(cancel)
			.show();
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
	
	/**
	 * 确认对话框
	 * @param ctx
	 * @param cs   要显示的内容
	 * @param ok   确定按钮事件
	 */
	public static void showConfirmBox(Context ctx, CharSequence cs,DialogInterface.OnClickListener ok){
		try{
			new ElecDialog.Builder(ctx,Style.CONFIRM).setTitle(R.string.dialog_title_warn)
			.setMessage(cs)
			.setConfirmButtonListener(ok)
			.setCancelButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d, int arg1) {
					d.dismiss();
				}
			}).show();
		}catch(Exception e){
			ElecLog.w(MessageBox.class, e.getMessage());
		}
	}
}
