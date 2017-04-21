package com.ctbri.ui.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.ui.BaseActivity;
import com.yifeng.skzs.adapter.CommonAdapter;

/**
 * ClassName:SystemSettings Description：系统设置
 * 
 * @author Administrator Date：2012-10-16
 */
public class SystemSettings extends BaseActivity {
	private TextView titleTxt;
	private Button backBtn;
	private ListView listView;
	private List<Map<String, Object>> list;
	private CommonAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_settings);

		titleTxt = (TextView) findViewById(R.id.titleTxt);
		titleTxt.setText(this.getIntent().getStringExtra("title"));

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SystemSettings.this.finish();
			}
		});

		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				/*
				 * switch (arg2) { case 0: Intent intent0 = new
				 * Intent(SystemSettings.this,AccountActivity.class);
				 * startActivity(intent0); break; case 1: Intent intent1 = new
				 * Intent(SystemSettings.this,HelpActivity.class);
				 * startActivity(intent1); break; case 2: Intent intent2 = new
				 * Intent(SystemSettings.this,FeedbackActivity.class);
				 * startActivity(intent2); break; case 3: Intent intent3 = new
				 * Intent(SystemSettings.this,AboutActivity.class);
				 * startActivity(intent3); break; default: break; }
				 */

			}
		});

		doLoadData();

		adapter = new CommonAdapter(this, list, R.layout.system_settings_item,
				new String[] { "image", "content" }, new int[] { R.id.imageIvw,
						R.id.contentTxt }, getResources());

		// adapter.setViewBinder();
		listView.setAdapter(adapter);

	}

	private void doLoadData() {
		list = new ArrayList<Map<String, Object>>();

		// Resources res = getResources();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("image", R.drawable.setting_account_setting_icon);
		map.put("content", "帐号管理");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("image", R.drawable.setting_user_helper_icon);
		map.put("content", "新手帮助");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("image", R.drawable.setting_opinion_feedback_icon);
		map.put("content", "意见反馈");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("image", R.drawable.setting_about_us_icon);
		map.put("content", "关于我们");
		list.add(map);

	}
}
