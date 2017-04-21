package com.ctbri.ui.epos;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.pos.support.POSPurchaseRequest;
import com.ctbri.ui.BaseActivity;
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
	//private ElecProgressDialog mProgressDialog; //处理状态显示
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);
		 // Disable IME for this application
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
       
		this.setTitle(R.string.title_enter_money);
		
	}
	
	@Override
	public void onDeviceReady() {
		super.onDeviceReady();
		
		mKeyboard  = (Keyboard)this.findViewById(R.id.keyboard);
	    mEditMoney = (MoneyEditText)this.findViewById(R.id.enter_pay);
	    mKeyboard.setFocusEditText(mEditMoney);
	        
		findViewById(R.id.start_swip).setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start_swip:
			if(mEditMoney.getMoney()<100){
				this.showWarn("请输入金额(不小于1元)!");
				mEditMoney.setFocusable(true);
				return;
				
			}
			POSPurchaseRequest request  = new POSPurchaseRequest();
			//消费金额
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
