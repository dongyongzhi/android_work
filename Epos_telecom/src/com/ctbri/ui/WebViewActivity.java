package com.ctbri.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.R;

public class WebViewActivity extends BaseWebView {
	private TextView titleTxt;
	private Button backBtn;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		Intent qz = this.getIntent();

		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText(qz.getStringExtra("title"));

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				WebViewActivity.this.finish();
			}
		});

		/*
		 * homeBtn = (Button) findViewById(R.id.homeBtn);
		 * homeBtn.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new
		 * Intent(WebViewActivity.this,MainMenuActivity.class);
		 * startActivity(intent); } });
		 */

		url = qz.getStringExtra("url");
		this.setUrl(url);
		this.doInitView();
	}

}