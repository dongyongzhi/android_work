package com.ctbri.ui.query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
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

/**
 * @comment:查询历史订单
 * @author:Zhu
 * @Date:2012-12-6
 */
public class QueryHistoryActivity extends QueryOrderActivity implements OnClickListener,OnTouchListener,OnScrollListener {

	private View mLoading,mQueryArea,mPromptMessage;
	private TextView mEditStartDate,mEditEndDate,mTextMessage,mTextOrderCode,mExternalId;
	private Date startDate,endDate;
	private DateFormat df = new SimpleDateFormat("yyyyMMdd"); //日期格式化
	private DateFormat parse = new SimpleDateFormat("yyyy-MM-dd"); //日期格式化
	
	private POSInfo posInfo;
	private OrderService  orderService;
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
		
		findViewById(R.id.btn_query).setOnClickListener(this);
		mPromptMessage = findViewById(R.id.promptMessage);
		mLoading  = findViewById(R.id.publicloading);
		mQueryArea = findViewById(R.id.queryTrans);
		mLoading.setVisibility(View.INVISIBLE);
		mQueryArea.setVisibility(View.VISIBLE); //显示查询区域
		
		mTextOrderCode = (TextView)findViewById(R.id.mOrderCode);
		mExternalId = (TextView)findViewById(R.id.mExternalId);
		
		mTextMessage = (TextView)findViewById(R.id.resultMessage);
		mEditStartDate = (TextView)findViewById(R.id.mEditStartDate);
		mEditStartDate.setFocusable(false);
		
		mEditEndDate   = (TextView)findViewById(R.id.mEditEndDate);
		mEditStartDate.setOnTouchListener(this);
		mEditEndDate.setOnTouchListener(this);
		mEditEndDate.setFocusable(false);

		listView = (ListView) findViewById(R.id.listview);
		
		this.initTopBtn();
		this.setTopFocus(this.bt_history, R.drawable.query_selected);
		listView.setOnScrollListener(this);
		listView.setOnItemClickListener(this);
	}
	
	/**
	 * 服务绑定成功
	 */
	protected void serviceBindComplete(){
		posInfo = this.getPOSInfo(); //获取终端详细
		if(posInfo!=null){
			orderService = ServiceFactory.getInstance().getOrderService();
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
		
		if(startDate!=null)
			req.setStartTime(df.format(startDate)); //开始时间
		if(endDate!=null)
			req.setEndTime(df.format(endDate)); //结束时间
		
		req.setOrderCode(mTextOrderCode.getText().toString());//订单号
		req.setExternalId(mExternalId.getText().toString());//参考号
		
		currentPage++;
		req.setCurrentpage(currentPage);
		return orderService.queryByPos(req);
	}
	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		isRuning = false;
		mLoading.setVisibility(View.INVISIBLE);
		//检查返回信息
		if(obj==null){
			mPromptMessage.setVisibility(View.VISIBLE);
			mQueryArea.setVisibility(View.VISIBLE);
			mTextMessage.setText("查询失败！");
			return;
		}
		ElecResponse baseResp = (ElecResponse)obj;
		if(baseResp.getErrCode()!=0){
			mPromptMessage.setVisibility(View.VISIBLE);
			mQueryArea.setVisibility(View.VISIBLE);
			mTextMessage.setText(baseResp.getErrMsg());
			return;
		}
		//查询成功
		QueryPosOrderResponse resp = (QueryPosOrderResponse)obj;
		if(resp.getOrders()==null || resp.getOrders().size()==0){  //无记录
			findViewById(R.id.promptMessage).setVisibility(View.VISIBLE);
			return;
		}
		mPromptMessage.setVisibility(View.INVISIBLE);
		mQueryArea.setVisibility(View.INVISIBLE); //隐藏查询区域
		listView.setVisibility(View.VISIBLE);
		
		//显示订单记录
		currentCount +=resp.getOrders().size();
		
		if(currentPage==1){
			count = resp.getCount();
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
		mLoading.setVisibility(View.INVISIBLE);
		mPromptMessage.setVisibility(View.VISIBLE);
		mQueryArea.setVisibility(View.VISIBLE);
		mTextMessage.setText(e.getMessage());
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_query: //查询
			
			if(mTextOrderCode.getText().length()==0 
					&& mExternalId.getText().length() == 0 
					&& mEditStartDate.getText().length()==0 
					&& mEditEndDate.getText().length()==0){
				this.showWarn("请输入查询条件！");
				return;
			}
			
			//check 数据
			if(mEditStartDate.getText().length()==0 && mEditEndDate.getText().length()!=0){
				this.showWarn("请选择开始日期！");
				return;
			}
			if(mEditStartDate.getText().length()!=0 && mEditEndDate.getText().length()==0){
				this.showWarn("请选择结束日期！");
				return;
			}
			
			if(mEditStartDate.getText().length()!=0 && mEditEndDate.getText().length()!=0){
				try {
					startDate = parse.parse(mEditStartDate.getText().toString());
					endDate   = parse.parse(mEditEndDate.getText().toString());
					if(startDate.getTime()>endDate.getTime()){
						this.showWarn("开始日期不能大于结束日期！");
						return;
					}
				} catch (ParseException e) {
					this.showWarn("输入的日期格式有误！");
					return;
				}
			}
			
			mPromptMessage.setVisibility(View.INVISIBLE);
			mLoading.setVisibility(View.VISIBLE);
			mQueryArea.setVisibility(View.INVISIBLE);
			currentPage = 0;  //当前页重置
			
			this.startAsynService(); //开始业务
			break;
			
		default:
			break;
		}
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
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		currentItem = firstVisibleItem + visibleItemCount-1;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (currentCount == currentItem && scrollState == OnScrollListener.SCROLL_STATE_IDLE && !isRuning) {
			if(currentCount<count){
				this.startAsynService(); //开始查询
			}else
				listView.removeFooterView(commonUtil.loadingLayout);
		}
	}
}
