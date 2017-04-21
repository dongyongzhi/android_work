package com.hftcom.ui;

import com.hftcom.ActionCode;
import com.hftcom.R;
import com.hftcom.YFLog;
import com.hftcom.ui.pay.SwipingCardActivity;
import com.hftcom.utils.Config;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HftPayActivity extends BaseActivity implements OnClickListener {
 
	private String orderCode; 
	private long money;
	private int code = 1;
	private String pkgName="",actName="";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		findViewById(R.id.btnDoTrade).setOnClickListener(this);
		findViewById(R.id.btnResult).setOnClickListener(this);
		pkgName = this.getIntent().getStringExtra("pkgName");
		actName = this.getIntent().getStringExtra("actName");
		
		
		String action =  this.getIntent().getAction();
		if(action.equals(Config.ACTION_OTHER_PAY)){
			code = ActionCode.OTHER.getCode();
			if(pkgName == null || pkgName.equals("") || actName == null || actName.equals("")){
				this.finish();
			}
		}else if(action.equals(Config.ACTION_HTML5_PAY)){
			code = ActionCode.HTML5.getCode();
		}else{
			this.finish();
		}
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		String tradeType = getString("tradeType"); // 交易类型
		String tradeSerialNum = getString("tradeSerialNum");// 流水号
		String companyName = getString("companyName"); // 商户名称
		String companyType = getString("companyType");// 商户类型
		orderCode = getString("orderCode");// 支付订单号
		String orderNum = getString("orderNum");// 商户订单号
		String orderExplain = getString("orderExplain");// 订单说明
		String phoneIMSINum = getString("phoneIMSINum");// 手持设备号
		String operatorNum = getString("operatorNum");// 操作工号
		String payMoney = getString("payMoney");// 金额
		String extensionField = getString("extensionField");// 扩展域
		String orderSignName = getString("checkDigit");// 校验域
		
		if(payMoney!=null && !"".equals(payMoney)){
			if(payMoney.indexOf(".")<0){
				money = Long.parseLong(payMoney);
			}else{
				money = ((Float)(Float.parseFloat(payMoney) * 100)).longValue();
			}
		}

		((TextView) findViewById(R.id.order_comment)).setText(orderExplain); // 订单说明
		((TextView) findViewById(R.id.merchant_name)).setText(companyName); // 商户名称
		((TextView) findViewById(R.id.should_money)).setText(String.format("%,.2f 元",money/100.00)); // 应付金额
		((TextView) findViewById(R.id.order_pay)).setText(tradeType); // 交易类型
		((TextView) findViewById(R.id.oper_num)).setText(operatorNum); // 操作工号
		((TextView) findViewById(R.id.mTradeSerialNum)).setText(tradeSerialNum); // 流水号
		((TextView) findViewById(R.id.orderCode)).setText(orderCode); // 订单号
		((TextView) findViewById(R.id.mCompanyType)).setText(companyType); // 商户类型
 
	}
	
	private String getString(String name){
		String value = this.getIntent().getStringExtra(name);
		if(value == null){
			return "";
		}else{
			return value;
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btnResult:
			break;
		case R.id.btnDoTrade:
			intent = new  Intent(HftPayActivity.this,SwipingCardActivity.class);
			intent.putExtra("code", code);
			intent.putExtra("money", money);
			intent.putExtra("pkgName", pkgName);
			intent.putExtra("actName", actName);
			startActivity(intent);
			this.finish();
			break;

		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			dialogUtil.doExitProcess();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}