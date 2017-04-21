package com.ctbri.ui.notice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
//import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.R;
import com.ctbri.ui.BaseActivity;
import com.yifeng.skzs.adapter.CommonAdapter;
import com.yifeng.skzs.data.UserDAL;
import com.yifeng.skzs.util.CommonUtil;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.DialogUtil;

/**
 * @comment:新闻中心
 * @Date:2013-12-6
 */
//@SuppressLint("HandlerLeak")
public class NoticeListActivitys extends BaseActivity implements OnScrollListener,OnTouchListener {
	@SuppressWarnings("rawtypes")
	UserDAL userDAL;

	//public DialogUtil dialogUtil;
	private ListView listView;
	public CommonUtil commonUtil;

	Button querys,topback,btDay,btMonth,btHistory;
	LinearLayout queryTrans;
	protected List<Map<String, Object>> SURPERDATA = new ArrayList<Map<String, Object>>();
	protected List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private int pageNum = 0;
	private CommonAdapter adapter;
	private boolean isLoading = true;// 标志正在加载数据
	
	Button backBtn;
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		commonUtil = new CommonUtil(this);
		setContentView(R.layout.news_list);
		userDAL = new UserDAL(this);
		listView = (ListView) findViewById(R.id.listview);
		backBtn= (Button) findViewById(R.id.top_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(NoticeListActivitys.this,
						NoticeDetailActivitys.class);
				if (SURPERDATA != null && SURPERDATA.size() > 0) {
					intent.putExtra("newid",
							(Serializable) SURPERDATA.get(arg2).get("newid"));
					startActivity(intent);
				}
			}
		});
		this.setTitle(R.string.title_news);
		loadData();
		
	}


	@Override
	protected void onResume() {
		super.onResume();
		pageNum = 0;
		 loadData();
	}

	private void loadData() {
		if (pageNum == 0) {
			list.clear();
			listView.removeFooterView(commonUtil.loadingLayout);
			listView.addFooterView(commonUtil.addFootBar());
			adapter = new CommonAdapter(
					this,
					list,
					R.layout.news_page_item,
					new String[] { "title", "content","pic","time"},
					new int[] {R.id.news_title, R.id.news_content,R.id.pic,R.id.news_date},listView);
			listView.setAdapter(adapter);
			adapter.setViewBinder();
		}
		if (isLoading) {
			isLoading = false;
			new Thread(mRunnable).start();
		}
	}



	private Runnable mRunnable = new Runnable() {
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				Thread.sleep(300);
				Map<String, String> ps = new HashMap<String, String>();
				ps.put("type", "1");
				ps.put("page", "0");
				try {
					SURPERDATA = userDAL.News(ps);
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};

	//@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				addData();
			}
			isLoading = true;
		}
	};

	private void addData() {
		pageNum++;
		if (SURPERDATA.size() > 0) {
			String state = (String) SURPERDATA.get(0).get("state");
			if (state.equals("1")) {
				if (SURPERDATA.size() < 10)
					listView.removeFooterView(commonUtil.loadingLayout);
				for (Map<String, Object> map : SURPERDATA) {
					map.put("title", map.get("title"));
					map.put("content", map.get("content"));
					map.put("pic",ConstantUtil.news_ip+map.get("pic"));//Ip+map.get("pic")
					map.put("newid", map.get("newid"));
					map.put("time", map.get("createtime"));
					list.add(map);
				}
			} else {
				listView.removeFooterView(commonUtil.loadingLayout);
				Toast.makeText(NoticeListActivitys.this,
						SURPERDATA.get(0).get("msg") + "", Toast.LENGTH_SHORT)
						.show();
			}
		} else 
		{
			new DialogUtil(this).showToast("未找到合适的数据！");
			listView.removeFooterView(commonUtil.loadingLayout);
		}
		adapter.notifyDataSetChanged();
	}

	/**日期选择*/
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN) {
			if(v.getId() == R.id.mEditStartDate || v.getId()==R.id.mEditEndDate)
				commonUtil.getTime((TextView)v);
		}
		return false;
	}


	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}




}
