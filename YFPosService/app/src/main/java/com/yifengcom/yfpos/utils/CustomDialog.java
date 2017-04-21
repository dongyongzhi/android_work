package com.yifengcom.yfpos.utils;

import com.yifengcom.yfpos.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomDialog {
	
	public static Dialog createDialog(Context context, int width,
			int height,int type, String msg) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progressdialog, null);
		RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.dialog_view);
		TextView tipTextView = (TextView) v.findViewById(R.id.msg); 
		tipTextView.setGravity(type);
		tipTextView.setText(msg); 
		Dialog dialog = new Dialog(context, R.style.loading_dialog); 
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.setContentView(layout, new LinearLayout.LayoutParams(width, height)); 
		return dialog;
	}
}
