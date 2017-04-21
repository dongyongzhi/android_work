package com.ctbri.ui.notice;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ctbri.R;
import com.ctbri.ui.BaseWebView;
/**
 * 
 * @comment:通知公告详情
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class NoticeDetailActivity extends BaseWebView{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_detail);
		
		Button top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NoticeDetailActivity.this.finish();
			}
		});
		
		String fixUrl = "file:///android_asset/html/notice.html";
		this.setUrl(fixUrl);
		this.doInitView();
	}

}
