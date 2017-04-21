package com.ctbri.ui.query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

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
 * @comment:查询当前pos机 当月交易的订单
 * @author:Zhu
 * @Date:2012-12-6
 */
public class QueryMonthActivity extends QueryOrderActivity implements OnScrollListener{
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
		this.setTopFocus(this.bt_month, R.drawable.query_selected);
		mTextMessage = (TextView)findViewById(R.id.resultMessage);
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(this);
	}

	
	/**
	 * 服务绑定成功
	 */
	protected void serviceBindComplete(){
		posInfo = this.getPOSInfo(); //获取终端详细
		if(posInfo!=null){
			orderService =ServiceFactory.getInstance().getOrderService();
			findViewById(R.id.publicloading).setVisibility(View.VISIBLE); //显示进度
			this.startAsynService(); //开始查询
		}
	}
	
	/**
	 * 查询订单
	 */
	public Object onExecAsynService(){
		isRuning = true;
		//查询订单信息
		QueryPosOrderRequest req = new QueryPosOrderRequest();
		req.setPoscati(posInfo.getPosNumber());//终端号
		
		Calendar calendar = Calendar.getInstance(); 
		//当前月的第一天
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		req.setStartTime(df.format(calendar.getTime())); 
		
		//当月最后一天
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		req.setEndTime(df.format(calendar.getTime())); //结束时间
		
		currentPage ++;//当前页
		req.setCurrentpage(currentPage);
		
		return orderService.queryByPos(req);
	}
	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		isRuning = false;
		findViewById(R.id.publicloading).setVisibility(View.INVISIBLE);
		//检查返回信息
		if(obj==null){
			findViewById(R.id.promptMessage).setVisibility(View.VISIBLE);
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
		//显示订单记录
		currentCount +=resp.getOrders().size();
		
		if(currentPage==1){
			
			count = resp.getCount();
			listView.setVisibility(View.VISIBLE);
			orderAdapter = new OrderAdapter(resp);
			if(currentCount<count)
				listView.addFooterView(commonUtil.addFootBar());
			
			listView.setAdapter(orderAdapter);
			
		}else{
			
			for(Order order : resp.getOrders()){
				orderAdapter.getOrdes().add(order);
			}
			orderAdapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 执行失败
	 */
	public void onServiceError(Exception e){
		isRuning = false;
		findViewById(R.id.publicloading).setVisibility(View.INVISIBLE);
		findViewById(R.id.promptMessage).setVisibility(View.VISIBLE);
		mTextMessage.setText(e.getMessage());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 显示详情，目前没有查询订单详情接口，所以无用
	 */
	public void showDetail() {
		final Dialog builder = new Dialog(QueryMonthActivity.this, R.style.dialog);
		builder.setContentView(R.layout.show_detail_dialog);
		final Button btn_dialog = (Button) builder.findViewById(R.id.btn_dialog);
		btn_dialog.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				builder.dismiss();
			}
		});
		builder.setCanceledOnTouchOutside(false);// 设置点击Dialog外部任意区域关闭Dialog
		builder.show();
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
				//listView.addFooterView(commonUtil.addFootBar());
				this.startAsynService(); //开始查询
			}else 
				listView.removeFooterView(commonUtil.loadingLayout);
		}
	}
}
