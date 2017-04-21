package com.ctbri.ui.epos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.ui.BaseActivity;
import com.ctbri.ui.collection.TradeSuccessActivity;
import com.ctbri.widget.Keyboard;
import com.ctbri.widget.MoneyEditText;

/**
 * 消费
 * @comment: 直接消费不用创建订单
 * 
 * 
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class PurchaseActivity extends BaseActivity implements OnClickListener{
	
	
	private Keyboard mKeyboard;
	private MoneyEditText mEditMoney;
	private TransService trans;
	
	//private ElecProgressDialog mProgressDialog; //处理状态显示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);
		 // Disable IME for this application
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        
        mKeyboard  = (Keyboard)this.findViewById(R.id.keyboard);
        mEditMoney = (MoneyEditText)this.findViewById(R.id.enter_pay);
        mKeyboard.setFocusEditText(mEditMoney);
        
		findViewById(R.id.start_swip).setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);
		
		this.setTitle(R.string.title_enter_money);
	}
	
	/**
	 * pos 绑定完成
	 */
	protected void serviceBindComplete(){
		trans = ServiceFactory.getInstance().getTransService(this.getPOS(),this);
	}
	
	/**
	 * 交易付款 <br/>
	 */
	public Object onExecAsynService(){
		return trans.purchase(mEditMoney.getMoney(),null,null);//收款
	}
	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
	 
		if(obj==null){
			this.showError("交易失败！");
			return;
		}
		TransResponse resp = (TransResponse)obj;
		if(resp.getErrCode()!=0)
			this.showError(resp.getErrMsg());
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
		switch (v.getId()) {
		case R.id.start_swip:
			if(mEditMoney.getMoney()==0){
				this.showWarn("请输入金额");
				mEditMoney.setFocusable(true);
				return;
			}
			
			/*
			mProgressDialog = new ElecProgressDialog(this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.setMessage("请刷卡输密码...");
			mProgressDialog.show();
			this.startAsynService(mProgressDialog);
			*/
			POSTransRequest  request  = new POSTransRequest();
			request.setMessageType(MessageType.PURCHASE);//消费
			request.setMoney(mEditMoney.getMoney());
			TransActionFactory.getInstance().startAction(PurchaseActivity.this, request);
			
			break;
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}
}
