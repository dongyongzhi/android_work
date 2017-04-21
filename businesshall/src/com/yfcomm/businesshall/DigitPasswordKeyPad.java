package com.yfcomm.businesshall;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class DigitPasswordKeyPad extends View {
	private Context ctx = null;
	private View v;
	private byte[] keyvalue = new byte[10];
	private TextView[] tv = new TextView[10];
	private int[] KeyId = { R.id.keybord1, R.id.keybord2, R.id.keybord3, R.id.keybord4, R.id.keybord5, R.id.keybord6,
			R.id.keybord7, R.id.keybord8, R.id.keybord9, R.id.keybord0 };

	private TextView text_pass;

	public DigitPasswordKeyPad(Context ctx) {
		super(ctx);
		this.ctx = ctx;
	}

	public void setKeyValue(byte[] key) {
		keyvalue = key;
		for (int i = 0; i < 10; i++) {
			tv[i].setText(String.valueOf(keyvalue[i]));
		}
		text_pass.setText("");
	}

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
	}

	public View setup() {
		LayoutInflater lif = LayoutInflater.from(ctx);
		v = lif.inflate(R.layout.keyborad, null);
		for (int i = 0; i < 10; i++) {
			tv[i] = (TextView) v.findViewById(KeyId[i]);
		}
		text_pass = (TextView) v.findViewById(R.id.text_pass);
		return v;
	}

	public void setPassText(int isadd) {
		String text;

		if (text_pass != null) {
			text = text_pass.getText().toString();
			if (isadd == 0x01) {// 增加一个密码
				if (text !=null) {
					text_pass.setText(text + " *");
				}
			} else {// 删除一个密码
				if (text!=null &&text.length() > 0) {

					text_pass.setText(text.substring(0, text.length() - 2));

				}
			}

		}
	}

}