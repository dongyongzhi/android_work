package com.ctbri.ui.epos;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.TextView;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.domain.QueryPosOrderResponse.Order;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.pos.support.POSPurchaseRefundRequest;
import com.ctbri.ui.BaseActivity;
import com.ctbri.ui.collection.TradeSuccessActivity;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.MoneyEditText;

/**
 * 退货
 * @author qin
 * 
 * 2012-11-23
 */
public class BackMoneyActivity  extends BaseActivity implements OnClickListener,OnTouchListener {
	
	private EditText mEditTransDate,mEditReferenceNo;
	private MoneyEditText mEditMoney;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.back_money);
		
		findViewById(R.id.btnDoTrade).setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);
		
		mEditTransDate       = (EditText)findViewById(R.id.mEditTransDate);
		mEditTransDate.setOnTouchListener(this);
		mEditTransDate.setFocusable(false);
		
		//隐藏日期输入的输入法
		//InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.hideSoftInputFromWindow(mEditTransDate.getWindowToken(), 0);
		
		mEditReferenceNo = (EditText)findViewById(R.id.mEditReferenceNo);
		mEditReferenceNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
		mEditMoney       = (MoneyEditText)findViewById(R.id.mEditMoney);
		
		//是否订单详细传入
		Order order = (Order)this.getIntent().getParcelableExtra(EXTRA_ORDER_DETAIL);
		if(order!=null){ //显示退货信息
			mEditReferenceNo.setText(order.getExternalid());
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
			mEditTransDate.setText(df.format(order.getTime()));
			mEditTransDate.setOnTouchListener(null); //禁用
			
			mEditReferenceNo.clearFocus();
			mEditReferenceNo.setFocusable(false);
			mEditReferenceNo.setFocusableInTouchMode(false);
			
			
			mEditMoney.setFocusable(true);
			mEditMoney.requestFocus();
			mEditMoney.setFocusableInTouchMode(true);
		}
	 
		setTitle(R.string.title_backmoney);
	}
	

	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
		TransResponse resp = (TransResponse)obj;
		
		if(!ResponseCode.SUCCESS.getClass().equals(resp.getErrCode()))
			this.showWarn(resp.getErrMsg());
		else{
			 //转至成功界面
			Intent mIntent = new Intent(this,TradeSuccessActivity.class);
			//参数
			Bundle mBundle = new Bundle();
			mBundle.putParcelable(ElecActivity.EXTRA_TRANS_RESPONSE, resp);
			mIntent.putExtras(mBundle);
			this.startActivity(mIntent);
			this.finish();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnDoTrade:
			//数据验证

			if(mEditReferenceNo.getText().length()<12){
				MessageBox.showWarn(this, R.string.warn_ReferenceNumber_required);
				return;
			}
			if(mEditTransDate.getText().length()==0){
				MessageBox.showWarn(this, R.string.warn_transDate_required);
				return;
			}
			if(mEditMoney.getMoney()==0L){
				MessageBox.showWarn(this, R.string.warn_BackMoney_required);
				return;
			}
			//退货交易
			//this.startAsynService(true);
			POSPurchaseRefundRequest request = new POSPurchaseRefundRequest();
			//request.setMoney(mEditMoney.getMoney()); //金额
			request.setReferenceNumber(mEditReferenceNo.getText().toString());//原参考号
			
			//获取原交易日期(格式MMDD)
			String[] arrDate = mEditTransDate.getText().toString().split("-");
			
			//request.setCustomerNumber(this.getPOSInfo().getCustomerNumber());//商户编号
			//request.setPosNumber(this.getPOSInfo().getPosNumber());//终端编号
			
			request.setOriginalTransDate(arrDate[1]+arrDate[2]);//原交易日期
			TransActionFactory.getInstance().startAction(this, request);//退货交易
			break;
			
		case R.id.top_back:
			onBack();
			break;
		}
	}
	
	/**
	 * 清空输入内容
	 */
	public void clearEdit(){
		mEditReferenceNo.setText("");
		mEditMoney.setText("");
		mEditTransDate.setText("");
	}

	/**
	 * 选择日期
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction()==MotionEvent.ACTION_DOWN) {
			if(v.getId() == R.id.mEditTransDate)
				commonUtil.getTime((TextView)v);
		}
		return false;
	}
}
