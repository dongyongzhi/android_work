package com.ctbri.ui.more;

import android.os.Bundle;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.ui.BaseActivity;

/**
 * @comment:关于
 * @author:Zhu
 * @Date:2012-12-1
 */
public class AboutActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_about);
		
		TextView mTextVersion = (TextView)findViewById(R.id.mTextVersion);
		mTextVersion.setText("版本："+getVersion().getVersionName()+" ( Android )");
	}
}
