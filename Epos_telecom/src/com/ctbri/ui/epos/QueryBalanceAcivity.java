package com.ctbri.ui.epos;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.ui.BaseActivity;

/**
 * 查询余额
 * @author qin
 * 
 * 2012-12-1
 */
public class QueryBalanceAcivity extends BaseActivity implements OnClickListener{

	private TextView mTextCard,mTextMoney,mTextTime;
	private TransService trans;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_balance);
		setTitle(R.string.title_query_balance);
		
		mTextCard  = (TextView)findViewById(R.id.mTextCard);
		mTextMoney = (TextView)findViewById(R.id.mTextMoney);
		mTextTime  = (TextView)findViewById(R.id.mTextTime);
		this.findViewById(R.id.top_back).setOnClickListener(this);
		this.findViewById(R.id.btnRepeatQuery).setOnClickListener(this);
		this.findViewById(R.id.btnBack).setOnClickListener(this);
		
		TransResponse response = TransActionFactory.getInstance().getTransResult(this);
		if(response!=null)
			setValue(response);
	}
	
	/**
	 * pos 绑定完成
	 */
	protected void serviceBindComplete(){
		trans =ServiceFactory.getInstance().getTransService(this.getPOS(),this);
		//this.startAsynService(true); //启动业务
	}
	
	/**
	 * 查询余额
	 */
	public TransResponse onExecAsynService(){
		 return trans.queryBalance();
	}
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
		TransResponse resp = (TransResponse)obj;
		if(resp.getErrCode()!=0)
			this.showWarn(resp.getErrMsg());
		else
			setValue(resp);
	}
	
	/**
	 * 设置显示内容
	 * @param resp
	 */
	public void setValue(TransResponse resp){
		mTextCard.setText(resp.getCard());
		mTextMoney.setText(resp.getMoneyString());
		mTextTime.setText(resp.getDateTime());
	}
	/**
	 * 清除显示
	 */
	public void clear(){
		mTextCard.setText("");
		mTextMoney.setText("");
		mTextTime.setText("");
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.btnRepeatQuery:
			clear();
			
			POSTransRequest  request  = new POSTransRequest();
			request.setMessageType(MessageType.QUERYBALANCE);//查询
			TransActionFactory.getInstance().startAction(this, request);
			
			break;
			
		case R.id.top_back:
		case R.id.btnBack:
			this.finish();
			break;
		}
	}
}
