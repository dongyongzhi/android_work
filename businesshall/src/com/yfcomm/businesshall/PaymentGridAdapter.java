package com.yfcomm.businesshall;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PaymentGridAdapter  extends BaseAdapter{

	private Context mContext;
	public  List<String> PayMentNames;
	private TextView tx1;
	private int Selpostion;

	public PaymentGridAdapter(Context mContext, List<String> names) {
		super();
		this.PayMentNames = names;
		this.mContext = mContext;
	}

	public boolean setItemsel(int postion) {
		this.Selpostion=postion;
		notifyDataSetChanged();
		return true;
	}
	
	@Override
	public int getCount() {
		return PayMentNames == null ? 0 : PayMentNames.size();
	}

	@Override
	public String getItem(int position) {

		if (PayMentNames != null && PayMentNames.size() != 0) {
			return PayMentNames.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.grid_itempay,
				null);

		tx1 = (TextView) view.findViewById(R.id.tv_item1);
	    String name = getItem(position);
		tx1.setText(name);
	
		if (Selpostion==position) {
			view.setBackgroundColor(view.getResources().getColor(R.color.title_color));
		}
	    return view;
	}	

	
}
