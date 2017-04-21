package com.ctbri.ui.query;

import java.util.Map;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.ctbri.R;
import com.ctbri.ui.BaseActivity;

/**
 * 查询订单详细显示界面.
 * 
 * <p>
 * 如果是当天消费则提供撤消功能，否则提供退货功能.
 * </p>
 * 
 * @author xingliangzheng
 * 
 * @date 2013-2-25
 */
public class OrderDetailActivity extends BaseActivity implements
		OnClickListener {

	private Button top_back,back_btn;
	private Map<String, String> order;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_order_detail);
		setTitle(R.string.title_query_order_detail);

		top_back = (Button) findViewById(R.id.top_back);
		
		top_back.setOnClickListener(this);

		// 获取订单信息
		order = (Map<String, String>) this.getIntent().getSerializableExtra(
				"OrderDetail");

		if (order != null && order.size() > 0) { // 显示信息
			((TextView) findViewById(R.id.mTextOrderCode)).setText(order
					.get("OrdId")); // 订单号
			((TextView) findViewById(R.id.mTextTransTime)).setText(order
					.get("SysTime")); // 交易时间
			
			((TextView) findViewById(R.id.mTextTransMoney)).setText(String
					.format("%,.2f 元",
							Double.parseDouble(order.get("OrdAmt")))); // 交易金额
			((TextView) findViewById(R.id.mTextOrderState))
					.setText(order.get("OrdStat")); // 订单状态
			((TextView) findViewById(R.id.mTextOrderDataTime)).setText(
					 order.get("AcctDate")
			); // 交易日期
			((TextView) findViewById(R.id.mTextPosNo)).setText(order
					.get("MtId")); // 终端编号
			((TextView) findViewById(R.id.mTextBatchNo)).setText(order
					.get("PayCard")); // 交易银行卡号
		}
	}

	private String orderStatus(String ordStat) {
		String status = "";
		if (ordStat != null) {
			if (ordStat.equals("S")) {
				status = "成功";
			} else if (ordStat.equals("F")) {
				status = "失败";
			} else {
				status = "初始化失败";
			}
		}
		return status;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.top_back:
				finish();
				break;
			case R.id.back_btn:
				finish();
				break;
		}
	}

}
