package com.hftcom.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hftcom.R;

public class Keyboard extends LinearLayout implements  View.OnClickListener,
											   View.OnLongClickListener{

	private EditText mEditText;
	
	public Keyboard(Context context) {
		super(context);
	}

	public Keyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	
	protected void onFinishInflate(){
		super.onFinishInflate();
		//查找button 绑定事件
		int layout = getChildCount();
		int clen = 0;
		ViewGroup view;
		for(int i=0;i<layout;i++){
			view = (ViewGroup)this.getChildAt(i);
			clen = view.getChildCount();
			for(int j=0;j<clen;j++){
				((ViewGroup)this.getChildAt(i)).getChildAt(j).setOnClickListener(this);
			}
		}
	}

	@Override
	public boolean onLongClick(View view) {
		if(view.getId()==R.id.btn_keyboard_del){
			//长按清空
			if(mEditText!=null)
				mEditText.setText("");
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		if(mEditText==null)
			return;
		Vibrator vibrator = (Vibrator)view.getContext().getSystemService(Activity.VIBRATOR_SERVICE);  
		vibrator.vibrate(new long[]{0,50,0}, -1);  
		
		int id = view.getId();
		int cursor = mEditText.getSelectionStart();
		switch (id) {
		case R.id.btn_keyboard_del:
			if(cursor>=1)
				mEditText.getText().delete(cursor-1,cursor);
			break;
		default:   //number
			if (view instanceof Button) {
				 String text = ((Button) view).getText().toString();
				 mEditText.getText().insert(cursor, text);
			}
			break;
		}
	}
	
	public void setFocusEditText(EditText mEditText){
		this.mEditText = mEditText;
	}
}
