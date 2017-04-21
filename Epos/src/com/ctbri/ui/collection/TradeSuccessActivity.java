package com.ctbri.ui.collection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
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
	private TransResponse resp;
	private TextView mTextTransSuccess,lable_customerName;
	private Button mButtonSuccess;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_success);
		setTitle(R.string.title_trans_success);
		
		//获取交易数据
		this.resp = TransActionFactory.getInstance().getTransResult(this);
		
		swip_success_btn = (Button)findViewById(R.id.swip_success_btn);
		swip_success_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		mTextTransSuccess = (TextView)findViewById(R.id.mTextTransSuccess);
		lable_customerName= (TextView)findViewById(R.id.lable_customerName);
		mButtonSuccess = (Button)findViewById(R.id.swip_success_btn);
		lable_customerName.setText(resp.getBusiness());

		if(resp!=null){
			TextView mTextMoney = (TextView)this.findViewById(R.id.mTextMoney);
			
			if(resp.getMessageType().equals(MessageType.CANCEL_PURCHASE)){
				mButtonSuccess.setText("撤销完成");
				mTextTransSuccess.setText("撤销成功");
				setTitle("撤销成功");
				mTextMoney.setText(String.format("撤销金额：%s", resp.getMoneyString()));
				
			}else if(resp.getMessageType().equals(MessageType.RETURNS)){
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


	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.swip_success_btn:
			Intent mIntent = new Intent(this, OrderDetailActivity.class);
			mIntent.putExtra(EXTRA_TRANS_RESPONSE,(Parcelable) resp);
			this.startActivity(mIntent);
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
