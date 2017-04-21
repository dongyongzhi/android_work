package com.ctbri.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.R;

/**
 * 对话框
 * 
 * @author qin
 * 
 *         2012-11-27
 */
public class ElecDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Style style = Style.DIALOG;
	private DialogInterface.OnClickListener confirmButtonListener = null;
	private DialogInterface.OnClickListener cancelButtonListener = null;
	
	public ElecDialog(Context context) {
		this(context,Style.DIALOG);
	}

	public ElecDialog(Context context, Style style) {
		super(context,R.style.dialog);
		this.style = style;
		createDialog();
	}

	protected void createDialog() {

		switch (style) {
		case DIALOG:
			setContentView(R.layout.dialog);
			((Button) findViewById(R.id.pBtn)).setOnClickListener(this);
			break;
		case CONFIRM:
			setContentView(R.layout.confirm_dialog);
			((Button) findViewById(R.id.confirm_btn)).setOnClickListener(this);
			((Button) findViewById(R.id.cancel_btn)).setOnClickListener(this);
			break;
		case SIGN:
			setContentView(R.layout.dialog_sign);
			((Button) findViewById(R.id.confirm_btn)).setOnClickListener(this);
			((Button) findViewById(R.id.cancel_btn)).setOnClickListener(this);
			break;
		case DOWNLOAD:
			setContentView(R.layout.down_dialog);
			break;
		}
	}

	public void setTitle(CharSequence text) {
		((TextView) findViewById(R.id.pTitle)).setText(text);
	}
	
	public void setTitle(int resid){
		((TextView) findViewById(R.id.pTitle)).setText(resid);
	}
	

	public void setMessage(CharSequence message) {
		((TextView) findViewById(R.id.pMsg)).setText(message);
	}

	public void setMessage(int resid){
		((TextView) findViewById(R.id.pMsg)).setText(resid);
	}
	
	public void setMessage(SpannableString message){
		((TextView) findViewById(R.id.pMsg)).setText(message);
	}
	
	public void setConfirmButtonListener(DialogInterface.OnClickListener listener) {
		this.confirmButtonListener = listener;
	}

	public void setCancelButtonListener(DialogInterface.OnClickListener listener) {
		this.cancelButtonListener = listener;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.pBtn:
		case R.id.confirm_btn:
			if (this.confirmButtonListener != null)
				confirmButtonListener.onClick(this,DialogInterface.BUTTON_POSITIVE);
			break;

		case R.id.cancel_btn:
			if (this.cancelButtonListener != null)
				this.cancelButtonListener.onClick(this,DialogInterface.BUTTON_NEGATIVE);
			break;
		}
	}

	/**
	 * 创建对话框
	 * @author qin
	 * 
	 * 2012-11-27
	 */
	public static final class Builder {
		public final ElecDialog dialog;

		public Builder(Context context) {
			dialog = new ElecDialog(context);
		}

		public Builder(Context context, Style style) {
			dialog = new ElecDialog(context, style);
		}

		public Builder setTitle(int resid){
			dialog.setTitle(resid);
			return this;
		}
		public Builder setTitle(CharSequence text) {
			dialog.setTitle(text);
			return this;
		}
		
		public Builder setMessage(int resid) {
			dialog.setMessage(resid);
			return this;
		}

		public Builder setMessage(CharSequence message) {
			dialog.setMessage(message);
			return this;
		}
		
		public Builder setMessage(SpannableString message){
			dialog.setMessage(message);
			return this;
		}
		
		public Builder setConfirmButtonListener(DialogInterface.OnClickListener listener) {
			dialog.setConfirmButtonListener(listener);
			return this;
		}
		
		public Builder setCancelButtonListener(DialogInterface.OnClickListener listener) {
			dialog.setCancelButtonListener(listener);
			return this;
		}
		
		public ElecDialog show(){
			dialog.show();
			return dialog;
		}
	}

	public enum Style {
		CONFIRM, DIALOG, DOWNLOAD,SIGN
	}
}
