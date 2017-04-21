package com.ctbri.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.ui.BaseActivity;
/**
 * 
 * @comment:交易成功
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class TradeSuccessActivity extends BaseActivity implements OnClickListener {
 
	private Button swip_success_btn,top_back;
	private ProgressBar loadingPOSInfo;
	private TransResponse resp;
	private TextView mTextTransSuccess;
	private Button mButtonSuccess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_success);
		setTitle(R.string.title_trans_success);
		
		swip_success_btn = (Button)findViewById(R.id.swip_success_btn);
		swip_success_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		mTextTransSuccess = (TextView)findViewById(R.id.mTextTransSuccess);
		mButtonSuccess = (Button)findViewById(R.id.swip_success_btn);
		
		loadingPOSInfo = (ProgressBar)findViewById(R.id.loadingPOSInfo);
		//获取交易数据
		this.resp = TransActionFactory.getInstance().getTransResult(this);
		
		if(resp!=null){
			TextView mTextMoney = (TextView)this.findViewById(R.id.mTextMoney);
			
			if(resp.getMessageType() == MessageType.CANCEL_PURCHASE){
				mButtonSuccess.setText("撤销完成");
				mTextTransSuccess.setText("撤销成功");
				setTitle("撤销成功");
				mTextMoney.setText(String.format("撤销金额：%s", resp.getMoneyString()));
				
			}else if(resp.getMessageType() == MessageType.RETURNS){
				mButtonSuccess.setText("退款完成");
				mTextTransSuccess.setText("退款申请已受理");
				setTitle("退款成功");
				mTextMoney.setText(String.format("退款金额：%s", resp.getMoneyString()));
			}else{
				mButtonSuccess.setText("交易完成");
				mTextTransSuccess.setText("交易成功");
				setTitle("交易成功");
				mTextMoney.setText(String.format("消费金额：%s", resp.getMoneyString()));
			}
		}
	}

	
	protected void serviceBindComplete(){
		loadingPOSInfo.setVisibility(View.VISIBLE);
		//pos参数为空重新获取
		if(getPOSInfo()==null)
			this.startAsynService();
		else
			setPOSInfoView(getPOSInfo());
	}
	
	/**
	 * 获取 pos 参数 
	 */
	public POSInfo onExecAsynService(){
		return this.getPOS().getPosInfo();
	}
	
	public void onServiceSuccess(Object obj){
		hideLoadingPosInfo();
		
		POSInfo pos = (POSInfo)obj;
		if(pos.getErrCode()!=0){
			showWarn(pos.getErrMsg());
		}else{
			setPOSInfo(pos);
			setPOSInfoView(pos);
		}
	}
	/**
	 * 返回错误
	 */
	public void onServiceError(Exception e){
		super.onServiceError(e);
		hideLoadingPosInfo();
	}
	
	public void hideLoadingPosInfo(){
		loadingPOSInfo.setVisibility(View.INVISIBLE);
	}
	
	public void setPOSInfoView(POSInfo info){
		TextView tv = (TextView)this.findViewById(R.id.lable_customerName);
		//商户名称
		String customerName = info.getCustomerName();
		customerName = customerName== null ?  "" : customerName;
		tv.setText(String.format("%s%s", this.getResources().getString(R.string.lable_customerName),customerName));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.swip_success_btn:
			if(resp!=null){  //跳转至签名界面
				Intent mIntent =null;
				//测试整个流程
				resp.setPrint(false);
				
				 //打印成功则不需要签名
				 if(resp.isPrint()) 
					mIntent = new Intent(TradeSuccessActivity.this,OrderDetailActivity.class);
				 else
					mIntent = new Intent(TradeSuccessActivity.this,SignCardNameActivity.class);
				
				//参数
				mIntent.putExtra(ElecActivity.EXTRA_TRANS_RESPONSE, (Parcelable)resp); //交易
				//外部apk调用数据
				mIntent.putExtra(ElecActivity.EXTRA_APK_PAY_REQUEST, getIntent().getParcelableExtra(ElecActivity.EXTRA_APK_PAY_REQUEST));
				startActivity(mIntent);
			}
			this.finish();
			break;
			
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}
}
