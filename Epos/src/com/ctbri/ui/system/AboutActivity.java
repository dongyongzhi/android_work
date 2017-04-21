package com.ctbri.ui.system;
/*package com.yifeng.skzs.ui.system;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.yifeng.qzt.R;
import com.yifeng.qzt.ui.BaseActivity;

*//**
 * ClassName:AboutActivity
 * Description：关于我们
 * @author Administrator
 * Date：2012-10-16
 *//*
public class AboutActivity extends BaseActivity
{
	private TextView introduceTxt;
	private Button backBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_about);
		
		introduceTxt = (TextView) findViewById(R.id.introduceTxt);
		
		String text = "\n1.版本号：1.0";
		text += "\n\t此版本适用于Android 2.1及2.1以上操作系统，对于在其他操作系统平台的手机上使用本软件，出现问题，本公司不承担责任。";
		text += "\n\n2.版权所有：江苏电信扬州分公司";
		text += "\n\n3.技术支持：江苏怡丰设备有限公司";
		text += "\n\n4.地址：扬州广陵产业园元辰路1号";
		text += "\n\n5.联系电话：0514-87362768";
		introduceTxt.setText(text);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				AboutActivity.this.finish();
			}
		});
	}
	
}
*/