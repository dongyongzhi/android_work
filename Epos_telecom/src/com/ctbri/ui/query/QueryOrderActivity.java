package com.ctbri.ui.query;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.ctbri.domain.QueryPosOrderResponse.Order;
import com.ctbri.domain.QueryPosOrderResponse.OrderState;
import com.ctbri.net.MessageType;
import com.ctbri.ui.BaseActivity;

public abstract class QueryOrderActivity extends BaseActivity implements OnItemClickListener{

	private int currentPosition=-1;// 当前选择中的项
	protected OrderAdapter  orderAdapter;
	
	public final static String EXTRA_QUERY_ORDER_DETAIL = "extra_query_order_detail";//订单查询详细
	
	/**单击选择行事件*/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(position<orderAdapter.getOrdes().size()){
			currentPosition = position;
			
			Order order = orderAdapter.getOrdes().get(position);
			Intent intent = new Intent(this,OrderDetailActivity.class);
			intent.putExtra(EXTRA_QUERY_ORDER_DETAIL, order);
			
			ctbriStartActivityForResult(intent,1);
		}
	}
	
	/**
	 * 接收交易返回
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data){  
		switch (resultCode){  
		case RESULT_OK:
			if(currentPosition!=-1){
				Order source = orderAdapter.getOrdes().get(currentPosition);
				
				//获取返回交易类型
				int messageType = data.getIntExtra(EXTRA_TRANS_RESULT, -1);
				switch(messageType){
				case MessageType.CANCEL_PURCHASE:
					source.setStatus(OrderState.REPEAL);
					orderAdapter.notifyDataSetChanged(); //数据变更
					break;
				case MessageType.RETURNS:
					source.setStatus(OrderState.RETURN);
					orderAdapter.notifyDataSetChanged(); //数据变更
					break;
				}
			}
			break;
		}
	}
}
