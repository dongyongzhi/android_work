package com.ctbri.ui.notice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.QueryNoticeRequest;
import com.ctbri.domain.QueryNoticeResponse;
import com.ctbri.domain.QueryNoticeResponse.Notice;
import com.ctbri.domain.ResponseCode;
import com.ctbri.ui.BaseActivity;
import com.yifeng.skzs.adapter.HomeAdapter;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.ListViewUtil;

/**
 * @comment:通知公告
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class NoticeListActivity extends BaseActivity  implements OnScrollListener{
	private ListView listview;
	private ListViewUtil util;
	private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//换转后的结果集
	private HomeAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notice_list);
		initView();
		setTitle(R.string.title_notice);
	}
	
	/**
	 * 界面初始化操作
	 */
	private void initView() {
		Button top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				NoticeListActivity.this.finish();
			}
		});
		listview = (ListView)findViewById(R.id.list_view);
		util = new ListViewUtil(this, listview);
		util.addFootBar();
		adapter = new HomeAdapter(this, list, R.layout.notice_list_item, 
				new String[] {"NOTICE_CONTENT"}, 
				new int[] {R.id.notice_content}, getResources());
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		//点击item 事件
		listview.setOnItemClickListener(new OnItemClickListener(){
			
			@Override
			public void onItemClick(AdapterView<?> parent,View view,int position, long id) {
				TextView tv = (TextView)view.findViewById(R.id.notice_content);
			    tv.setMaxLines(Integer.MAX_VALUE);
			}
		});
	}
	
	protected void serviceBindComplete(){
		//查询公告
		(new AsyncTask<Void,Void,QueryNoticeResponse>(){
			@Override
			protected QueryNoticeResponse doInBackground(Void... arg0) {
				POSInfo posInfo = getPOSInfo();
				if(posInfo==null)
					 return QueryNoticeResponse.getErrorResponse(QueryNoticeResponse.class,ResponseCode.TERM_NOT_FOUND);//终端未找到
				
				QueryNoticeRequest request = new QueryNoticeRequest();
				request.setCustomerNumber(posInfo.getCustomerNumber());
				request.setNoticeType("0"); //0 一般公告  1 
				request.setPosCati(posInfo.getPosNumber());
				
				try{
					return ServiceFactory.getInstance().getManagerService().queryNotice(request);
				}catch(Exception e){
					return ElecResponse.getErrorResponse(QueryNoticeResponse.class, 1, e.getMessage());
				}
			}
			/**成功返回*/
			protected void onPostExecute(QueryNoticeResponse response){
				list.clear();
				//返回成功
				if(response.getErrCode()==0){
					for(Notice notice: response.getNotices()){
						Map<String,Object> tmap = new HashMap<String,Object>();
						if(notice.getNoticeInfo().toLowerCase().indexOf("src=")<0){
							tmap.put("NOTICE_CONTENT", notice.getNoticeInfo());
							list.add(tmap);
						}
					}
					if(list.size()==0)
						util.showListAddDataState(String.valueOf(ConstantUtil.IS_EMPTY));
				}else{
					Toast.makeText(NoticeListActivity.this, response.getErrMsg(), Toast.LENGTH_SHORT).show();
				}
				util.removeFootBar();
				adapter.count = list.size();
				adapter.notifyDataSetChanged();
			}
			
		}).execute();
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//lastItem = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}
}
