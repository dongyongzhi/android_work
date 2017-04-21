package com.ctbri.ui.collection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.ctbri.R;

import com.ctbri.domain.TransResponse;
import com.ctbri.ui.BaseActivity;
import com.ctbri.ui.MainMenuActivitys;
/**
 * 
 * @comment:交易失败
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class TradeFailActivity extends BaseActivity implements OnClickListener {
 
	private Button swip_success_btn,top_back;
	private ProgressBar loadingPOSInfo;
	private TransResponse resp;
	private TextView mTextTransSuccess,lable_customerName;
	private Button mButtonSuccess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_fail);
		setTitle("交易失败");
		
		swip_success_btn = (Button)findViewById(R.id.swip_success_btn);
		swip_success_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		mTextTransSuccess = (TextView)findViewById(R.id.mTextTransSuccess);
		mButtonSuccess = (Button)findViewById(R.id.swip_success_btn);
		lable_customerName= (TextView)findViewById(R.id.lable_customerName);
		loadingPOSInfo = (ProgressBar)findViewById(R.id.loadingPOSInfo);
		lable_customerName.setText("订单号："+getIntent().getStringExtra("lable_customerName"));
		mTextTransSuccess.setText(getIntent().getStringExtra("message"));
		
	}


	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.swip_success_btn:
			startActivity(new Intent(this,
					MainMenuActivitys.class));
			this.finish();
			break;
			
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}
}
