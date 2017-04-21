package com.yifengcom.yfposdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yifengcom.yfposdemo.R;

public class TestActivity extends BaseActivity{
	 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
	}
	
	public void open(View v){
		((TextView)findViewById(R.id.tv_info)).setText("");
		Intent intent = new Intent("com.hftcom.elecpayment.pay");   
		intent.putExtra("tradeType","付款"); //交易类型
		intent.putExtra("tradeSerialNum","000001");//流水号
		intent.putExtra("companyName","惠民通测试商户"); //商户名称
		intent.putExtra("companyType","超市");//商户类型
		intent.putExtra("orderCode","201606220003");//支付订单号
		intent.putExtra("orderNum","201606220005");//商户订单号
		intent.putExtra("orderExplain","惠民通");//订单说明
		intent.putExtra("phoneIMSINum","18061169555");//手持设备号
		intent.putExtra("operatorNum","10001");//操作工号
		intent.putExtra("payMoney","5");//金额
		intent.putExtra("extensionField","");//扩展域
		intent.putExtra("checkDigit","");//校验域
		
		intent.putExtra("pkgName", "com.yifengcom.yfposdemo"); 
		intent.putExtra("actName", "com.yifengcom.yfposdemo.activity.TestActivity"); 
		startActivity(intent);    
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		((TextView)findViewById(R.id.tv_info)).setText("惠付通收银台返回："+intent.getStringExtra("resultCode"));
	}
 
}