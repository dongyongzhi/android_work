package com.ctbri.ui.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.ui.BaseActivity;

/**
 * @comment:帮助
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class HelpActivity extends BaseActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		TextView mTextVersion = (TextView)findViewById(R.id.mail);
		mTextVersion.setText("版本："+getVersion().getVersionName()+" ( Android )");
		
		Button top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				HelpActivity.this.finish();
			}
		});
	}

}
