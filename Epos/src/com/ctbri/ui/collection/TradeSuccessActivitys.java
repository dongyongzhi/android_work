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
 * @comment:交易成功
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class TradeSuccessActivitys extends BaseActivity implements OnClickListener {
 
	private Button swip_success_btn,top_back;
	private ProgressBar loadingPOSInfo;
	private TransResponse resp;
	private TextView mTextTransSuccess,lable_customerName;
	private Button mButtonSuccess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_success);
		setTitle("撤销交易成功");
		
		swip_success_btn = (Button)findViewById(R.id.swip_success_btn);
		swip_success_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		mTextTransSuccess = (TextView)findViewById(R.id.mTextTransSuccess);
		mTextTransSuccess.setText("撤销交易成功");
		lable_customerName= (TextView)findViewById(R.id.lable_customerName);
		mButtonSuccess = (Button)findViewById(R.id.swip_success_btn);
		mButtonSuccess.setText("撤销交易成功");
		lable_customerName.setText("订单号："+getIntent().getStringExtra("lable_customerName"));
		loadingPOSInfo = (ProgressBar)findViewById(R.id.loadingPOSInfo);

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
