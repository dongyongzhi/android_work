package com.ctbri.ui.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.biz.OrderService;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.QueryPosOrderRequest;
import com.ctbri.domain.QueryPosOrderResponse;
import com.ctbri.domain.QueryPosOrderResponse.Order;
import com.ctbri.utils.ElecLog;

/**
 * @comment:查询当前pos机 当天交易的订单
 * @author:Zhu
 * @Date:2012-12-6
 */
public class QueryDayActivity extends QueryOrderActivity implements OnScrollListener{

	private OrderService  orderService;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd"); //日期格式化
	private POSInfo posInfo;
	
	private TextView mTextMessage;
	private ListView listView;
	
	private int currentPage=0;
	private int currentCount = 0;
	private long count =0;
	private int currentItem= 0;
	private  boolean isRuning =false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.balance_inquires);

		this.initTopBtn();
		this.setTopFocus(this.bt_day, R.drawable.query_selected);
		mTextMessage = (TextView)findViewById(R.id.resultMessage);
		
		//数据列表
		listView = (ListView) findViewById(R.id.listview);
		this.listView.setOnScrollListener(this);
		this.listView.setOnItemClickListener(this);
		 
	}
	
	/**
	 * 服务绑定成功
	 */
	@Override 
	protected void serviceBindComplete(){
		posInfo = this.getPOSInfo(); //获取终端详细
		if(posInfo!=null){
			findViewById(R.id.publicloading).setVisibility(View.VISIBLE);
			orderService = ServiceFactory.getInstance().getOrderService();
			this.startAsynService(); //开始查询
		}
	}
	
	/**
	 * 查询订单
	 */
	@Override 
	public Object onExecAsynService(){
		isRuning = true;
		
		//查询订单信息
		QueryPosOrderRequest req = new QueryPosOrderRequest();
		Calendar calendar = Calendar.getInstance(); 
		 
		req.setPoscati(posInfo.getPosNumber());//终端号
		
		currentPage++;
		req.setCurrentpage(currentPage);
		
		req.setStartTime(df.format(calendar.getTime())); //开始时间
		//calendar.add(Calendar.DATE, 1); 平台己修正
		req.setEndTime(df.format(calendar.getTime())); //结束时间
		
		return orderService.queryByPos(req);
	}
	
	/**
	 * 成功返回
	 */
	@Override 
	public void onServiceSuccess(Object obj){
		findViewById(R.id.publicloading).setVisibility(View.INVISIBLE);
		
		isRuning = false;
		if(commonUtil.loadingLayout!=null)
			listView.removeFooterView(commonUtil.loadingLayout);
		//检查返回信息
		if(obj==null){
			mTextMessage.setText("查询失败！");
			return;
		}
		ElecResponse baseResp = (ElecResponse)obj;
		if(baseResp.getErrCode()!=0){
			findViewById(R.id.promptMessage).setVisibility(View.VISIBLE);
			
			mTextMessage.setText(baseResp.getErrMsg());
			return;
		}
		//查询成功
		QueryPosOrderResponse resp = (QueryPosOrderResponse)obj;
		if(resp.getOrders()==null || resp.getOrders().size()==0){  //无记录
			findViewById(R.id.promptMessage).setVisibility(View.VISIBLE);
			return;
		}
		count = resp.getCount();
		 
		if(currentPage==1){
			currentCount +=resp.getOrders().size();
			
			orderAdapter = new OrderAdapter(resp);
			if(currentCount<count)
				listView.addFooterView(commonUtil.addFootBar());
			
			listView.setAdapter(orderAdapter);
			listView.setVisibility(View.VISIBLE);
			
		}else{  //存在直接加入
			 
			for(Order order : resp.getOrders()){
				orderAdapter.getOrdes().add(order);
			}
			orderAdapter.notifyDataSetChanged();
		} 
		 
	}
	
	/**
	 * 执行失败
	 */
	@Override 
	public void onServiceError(Exception e){
		isRuning = false;
		findViewById(R.id.promptMessage).setVisibility(View.VISIBLE);
		mTextMessage.setText(e.getMessage());
	}

	@Override 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		currentItem = firstVisibleItem + visibleItemCount-1;
		
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		ElecLog.d(getClass(), " list onScrollStateChanged");
		if (currentCount == currentItem && scrollState == OnScrollListener.SCROLL_STATE_IDLE && !isRuning) {
			if(currentCount<count){
				this.startAsynService(); //开始查询
			}else
				this.listView.removeFooterView(commonUtil.loadingLayout);
		}
	}
}
