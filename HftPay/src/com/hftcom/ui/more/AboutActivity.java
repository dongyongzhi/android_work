package com.hftcom.ui.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hftcom.R;
import com.hftcom.ui.BaseActivity;

public class AboutActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_about);
		this.setTitle(R.string.title_about);
		TextView mTextVersion = (TextView)findViewById(R.id.mail);
		mTextVersion.setText("版本："+getVersion().getVersionName()+" ( Android )");
		
		Button top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AboutActivity.this.finish();
			}
		});
	}
}
