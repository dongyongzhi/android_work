package com.ctbri.ui.query;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

//import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.R;
import com.ctbri.ui.BaseActivity;
import com.yifeng.skzs.adapter.CommonAdapter;
import com.yifeng.skzs.data.UserDAL;
import com.yifeng.skzs.entity.User;
import com.yifeng.skzs.util.DialogUtil;

/**
 * @comment:消費明細
 * @author:xingliangzheng
 * @Date:2012-12-6
 */
//@SuppressLint("HandlerLeak")
public class QueryMonthActivitys extends BaseActivity implements OnScrollListener,OnTouchListener {
	@SuppressWarnings("rawtypes")
	UserDAL userDAL;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd"); //日期格式化
	public DialogUtil dialogUtil;
	private ListView listView;
	private String BeginDate, EndDate, TransStat;
	private EditText BeginDates, EndDates, TransStats;
	Button querys,topback,btDay,btMonth,btHistory,backBtn;
	LinearLayout queryTrans;
	protected List<Map<String, Object>> SURPERDATA = new ArrayList<Map<String, Object>>();
	protected List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private int pageNum = 1, lastItem = 0;;
	private CommonAdapter adapter;
	private boolean isLoading = true;// 标志正在加载数据

	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balance_inquires);
		querys = (Button) findViewById(R.id.btn_query);
		btDay = (Button) findViewById(R.id.bt_day);
		btMonth = (Button) findViewById(R.id.bt_month);
		btHistory = (Button) findViewById(R.id.bt_history);
//		BeginDates = (EditText) findViewById(R.id.mEditStartDate);
//		EndDates = (EditText) findViewById(R.id.mEditEndDate);
		TransStats = (EditText) findViewById(R.id.mTranst);
		backBtn= (Button) findViewById(R.id.top_back);
		
		queryTrans = (LinearLayout) findViewById(R.id.queryTrans);
		querys.setOnClickListener(onClick);
		btDay.setOnClickListener(onClick);
		btHistory.setOnClickListener(onClick);
		btMonth.setOnClickListener(onClick);
		backBtn.setOnClickListener(onClick);
		
//		BeginDates.setFocusable(false);
//		BeginDates.setOnTouchListener(this);
		
//		EndDates.setFocusable(false);
//		EndDates.setOnTouchListener(this);
		
		
		TransStat = TransStats.getText().toString();
		dialogUtil=new DialogUtil(this);
		userDAL = new UserDAL(this);
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(QueryMonthActivitys.this,
						OrderDetailActivity.class);
				if (list != null && list.size() > 0) {
					intent.putExtra("OrderDetail",
							(Serializable) list.get(arg2));
					startActivity(intent);
				}
			}
		});
		listView.setOnScrollListener(this);
		this.initTopBtn();
		this.setTopFocus(this.bt_month, R.drawable.query_selected);
		this.setTitle(R.string.title_query_order_list);
		 
		queryTrans.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		Calendar calendar = Calendar.getInstance(); 
		//当前月的第一天
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		BeginDate = df.format(calendar.getTime()); 
		
		//当月最后一天
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		EndDate = df.format(calendar.getTime()); //结束时间
//		BeginDate = BeginDates.getText().toString();
//		EndDate = EndDates.getText().toString();
		loadData();
	}

	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.top_back:
				finish();
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		pageNum = 1;
		Calendar calendar = Calendar.getInstance(); 
		//当前月的第一天
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		BeginDate = df.format(calendar.getTime()); 
		
		//当月最后一天
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		EndDate = df.format(calendar.getTime()); //结束时间
//		BeginDate = BeginDates.getText().toString();
//		EndDate = EndDates.getText().toString();
		 loadData();
	}

	private void loadData() {
		if (pageNum == 1) {
			list.clear();
			listView.removeFooterView(commonUtil.loadingLayout);
			listView.addFooterView(commonUtil.addFootBar());
			adapter = new CommonAdapter(
					this,
					list,
					R.layout.list_view_item,
					new String[] { "OrdId", "OrdAmt", "longTime", "OrdStat" },
					new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.text4 },listView);
			listView.setAdapter(adapter);
		}
		if (isLoading) {
			isLoading = false;
			new Thread(mRunnable).start();
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

		lastItem = arg1 + arg2 - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

		if(lastItem== adapter.count
				&& arg1 == OnScrollListener.SCROLL_STATE_IDLE){
			loadData();
		}
	}

	private Runnable mRunnable = new Runnable() {
		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
//				Thread.sleep(1000);
				TransStat="S";//状态为必须参数，所有此次传入成功的状态
				Map<String, String> ps = new HashMap<String, String>();
				ps.put("CustId", User.getCustId());
				ps.put("EndDate", EndDate);
				ps.put("BeginDate", BeginDate);
				ps.put("TransStat", TransStat);
				ps.put("PageSize", "10");
				ps.put("PageNum", pageNum+"");
				try {
					
					SURPERDATA = userDAL.QueryPayOrder(ps);
					
					Message msg = new Message();
					msg.what = 0;
					mHandler.sendMessage(msg);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
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
					map.put("OrdId", map.get("OrdId"));
					map.put("OrdAmt", map.get("OrdAmt"));
				
					String s = (String) map.get("AcctDate");
					try {
						map.put("AcctDate", s.substring(0,4)+"-"+s.substring(4,6)+"-"+s.substring(6,8));
						s = (String) map.get("SysTime");			
						map.put("SysTime", s.substring(0,2)+":"+s.substring(2,4)+":"+s.substring(4,6));
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					map.put("longTime", map.get("AcctDate")+" "+map.get("SysTime"));
					if(map.get("OrdStat").equals("S")){
						map.put("OrdStat", "成功");
					}else if(map.get("OrdStat").equals("F")){
						map.put("OrdStat", "失败");
					}else if(map.get("OrdStat").equals("I")){
						map.put("OrdStat", "初始化");
					}else if(map.get("OrdStat").equals("R")){
						map.put("OrdStat", "已撤销");
					}else{
						map.put("OrdStat", "失败");
					}
					
					list.add(map);
				}
			} else {
				listView.removeFooterView(commonUtil.loadingLayout);
				Toast.makeText(QueryMonthActivitys.this,
						SURPERDATA.get(0).get("msg") + "", Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			dialogUtil.showToast("未找到合适的数据！");
			listView.removeFooterView(commonUtil.loadingLayout);
		}
		adapter.count = list.size();
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


}
