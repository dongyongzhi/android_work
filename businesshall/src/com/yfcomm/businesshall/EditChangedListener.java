package com.yfcomm.businesshall;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

class EditChangedListener implements TextWatcher {
	private CharSequence temp;// 监听前的文本
	private int editStart;// 光标开始位置
	private int editEnd;// 光标结束位置
	private final int charMaxNum;
	private EditText mEditTextMsg;
	Context context;

	EditChangedListener(EditText mEditTextMsg, Context context, int maxnum) {

		this.mEditTextMsg = mEditTextMsg;
		this.context = context;
		this.charMaxNum = maxnum;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		temp = s;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {

		/** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
		editStart = mEditTextMsg.getSelectionStart();
		editEnd = mEditTextMsg.getSelectionEnd();
		if (temp.length() > charMaxNum) {
			Toast.makeText(context.getApplicationContext(), "你的输入已经超过了限制！", Toast.LENGTH_LONG).show();
			s.delete(editStart - 1, editEnd);
			int tempSelection = editStart;
			mEditTextMsg.setText(s);
			mEditTextMsg.setSelection(tempSelection);
		}

	}
};