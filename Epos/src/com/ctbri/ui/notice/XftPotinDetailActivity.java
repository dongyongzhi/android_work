package com.ctbri.ui.notice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ctbri.R;
import com.ctbri.ui.BaseWebView;
import com.yifeng.skzs.util.ConstantUtil;
/**
 * 
 * @comment:新闻中心详情
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class XftPotinDetailActivity extends BaseWebView{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xftpoint_detail);
		
		Button top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				XftPotinDetailActivity.this.finish();
			}
		});
		
		this.setTitle(R.string.title_xftpoint_detail);
		Intent in = getIntent();
		String fixUrl = ConstantUtil.news_ip+"/android/news/viewNews?news_id="+in.getStringExtra("newid");
		this.setUrl(fixUrl);
		this.doInitView();
	
	}

}
