package com.ctbri.ui.query;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.domain.QueryPosOrderResponse;
import com.ctbri.domain.QueryPosOrderResponse.Order;

/**
 * 显示订单列表
 * @author qin
 * 
 * 2012-12-10
 */
public class OrderAdapter extends BaseAdapter {
	
	private  final QueryPosOrderResponse response;
	
	public OrderAdapter(){
		this.response = null;
	}
	public OrderAdapter(QueryPosOrderResponse response){
		this.response = response;
	}
	
	
	public List<Order> getOrdes(){
		return  response.getOrders();
	}
	
	@Override
	public int getCount() {
		return response.getOrders()==null ? 0 : response.getOrders().size();
	}

	@Override
	public Object getItem(int index) {
		if(response.getOrders()==null)
			return null;
		if(index>getCount()-1)
			return null;
		return response.getOrders().get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int index, View v, ViewGroup vg) {
		v = LayoutInflater.from(vg.getContext()).inflate(R.layout.list_item, null);
		//显示订单信息
		QueryPosOrderResponse.Order order = (QueryPosOrderResponse.Order)getItem(index);
		if(order!=null){
			((TextView)v.findViewById(R.id.mTextOrderCode)).setText(String.format("订单号：%s", order.getOrderCode()));
			//金额
			((TextView)v.findViewById(R.id.mTextMoney)).setText(String.format("%,.2f 元",order.getMoney()/100.00));
			//创建订单时间
			((TextView)v.findViewById(R.id.mTextOrderTime)).setText(order.getTrxtime());
			//订单状态
			((TextView)v.findViewById(R.id.mTextOrderState)).setText(order.getStatus().getMessage());
		}
		return v;
	}
}
