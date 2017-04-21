package com.yf.defender;

import com.yf.define.PublicDefine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class yfver extends Activity {
	private int[] textid = { R.id.company, R.id.urladd, R.id.snnum, R.id.version };
	private Button B_VOK;
	private TextView[] ver_show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.version);

		B_VOK = (Button) findViewById(R.id.Ver_OK);
		B_VOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				yfver.this.finish();
			}
		});
		initsetText();
	}

	private void initsetText() {

		ver_show = new TextView[textid.length];
		for (int i = 0; i < textid.length; i++) {
			ver_show[i] = (TextView) findViewById(textid[i]);
			settext(ver_show[i], textid[i]);
		}

	}

	private void settext(TextView v, int id) {
		switch (id) {

		case R.id.company:
			v.setText("江苏怡丰通信设备有限公司");
			break;
		case R.id.urladd:
			if(PublicDefine.url != null)
				v.setText(PublicDefine.url);
			break;
		case R.id.snnum:
			if(PublicDefine.sn != null)
				v.setText(PublicDefine.sn);
			break;
		case R.id.version:
			if(PublicDefine.ver != null)
			 v.setText(PublicDefine.ver);
			break;
		default:
			break;
		}

	}

}