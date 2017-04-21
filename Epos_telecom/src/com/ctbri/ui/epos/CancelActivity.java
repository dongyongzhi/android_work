package com.ctbri.ui.epos;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

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
import com.ctbri.utils.MessageBox;

/**
 * 交易取消
 * @author qin
 *
 */
public class CancelActivity extends BaseActivity implements OnClickListener {

	private EditText mEditSerNo;
	
	private TransService trans;
 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cancel_order);
		
		findViewById(R.id.btnDoTrade).setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);
		 
		mEditSerNo       = (EditText)findViewById(R.id.mEditSerNo);
		mEditSerNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
		
		setTitle(R.string.title_cancel_order);
	 
	}
	
	/**
	 * pos 绑定完成
	 */
	protected void serviceBindComplete(){
		trans = ServiceFactory.getInstance().getTransService(this.getPOS(),this);
		//创建订单
		
	}
	
	/**
	 * 交易取消
	 */
	@Override
	public TransResponse onExecAsynService(){
		 return trans.revoke(mEditSerNo.getText().toString(), null,null);	
	}
	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
		
		TransResponse resp = (TransResponse)obj;
		if(resp.getErrCode()!=0)
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
			if(mEditSerNo.getText().length()<6){
				MessageBox.showWarn(this, R.string.warn_SerNo_required);
				return;
			}
			//执行取消
			//this.startAsynService(true);
			POSTransRequest  request  = new POSTransRequest();
			request.setMessageType(MessageType.CANCEL_PURCHASE);//消费撤销
			request.setOriginalSerialNumber(mEditSerNo.getText().toString()); //原流水号
			
			request.setCustomerNumber(this.getPOSInfo().getCustomerNumber());//商户编号
			request.setPosNumber(this.getPOSInfo().getPosNumber());//终端编号
			
			TransActionFactory.getInstance().startAction(CancelActivity.this, request);
			break;
		case R.id.top_back:
			onBack();
			break;
		}
	}
}
