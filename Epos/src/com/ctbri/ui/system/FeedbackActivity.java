package com.ctbri.ui.system;
/*package com.yifeng.skzs.ui.system;

import com.yifeng.skzs.R;
import com.yifeng.skzs.ui.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

*//**
 * ClassName:FeedbackActivity
 * Description：系统设置-意见反馈
 * @author Administrator
 * Date：2012-10-16
 *//*
public class FeedbackActivity extends BaseActivity implements OnClickListener
{
	private EditText contentEdt,contactEdt;
	private Button backBtn,sendBtn,cancelBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_feedback);
		
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.backBtn:
			FeedbackActivity.this.finish();
			break;

		default:
			break;
		}
		
	}
	
}
*/