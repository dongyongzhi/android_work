package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfposdemo.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends Activity{
	protected ProgressDialog pd = null;
	private TextView title_name;
	private TextView tv_info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	/**
	 * 设置通用标题
	 * @param name
	 */
	protected void setTitleName(String name){
		title_name = (TextView)findViewById(R.id.title_name);
		title_name.setText(name);
	}
	
	protected void startActivity(Class<?> c) {
		Intent intent = new Intent(this, c);
		startActivity(intent);
	}
	
	protected void closeDialog() {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}
	}
	
	public void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	 
	/**
	 * 设置通用返回内容
	 * @param content
	 */
	protected void setResult(String content){
		tv_info = (TextView)findViewById(R.id.tv_info);
		tv_info.setText(content);
	}
}
