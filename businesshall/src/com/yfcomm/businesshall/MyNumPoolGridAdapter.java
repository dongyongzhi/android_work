package com.yfcomm.businesshall;

import java.util.List;

import com.yfcomm.public_define.OperterData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class MyNumPoolGridAdapter extends BaseAdapter {

	private Context mContext;
	//private final static String TAG = "MyGridAdapter";
	private List<OperterData.numberpool> numpool;
	private TextView tx1, tx2;

	public MyNumPoolGridAdapter(Context mContext, List<OperterData.numberpool> numpool) {
		super();
		this.numpool = numpool;
		this.mContext = mContext;
	}

	public boolean setItemsel(int postion) {
		if(postion>=(numpool.size()-1)) return false;

		for (int i = 0; i < numpool.size(); i++) {
			if (i == postion) {
				numpool.get(i).is_sel = true;
			} else {
				numpool.get(i).is_sel = false;
			}
		}
		notifyDataSetChanged();
		return true;
	}
	
	public  void setNUmpoolList(List<OperterData.numberpool> numpool){
		this.numpool=numpool;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return numpool == null ? 0 : numpool.size();
	}

	@Override
	public OperterData.numberpool getItem(int position) {

		if (numpool != null && numpool.size() != 0) {
			return numpool.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.gird_item, null);

		tx1 = (TextView) view.findViewById(R.id.tv_item1);
		tx2 = (TextView) view.findViewById(R.id.tv_item2);
		OperterData.numberpool Onephoneum = getItem(position);
		tx1.setText(Onephoneum.phonenum);
		tx2.setText("Ô¤´æ»°·Ñ" + Onephoneum.prcie + ".0Ôª");

		if (Onephoneum.is_sel) {
			view.setBackgroundColor(view.getResources().getColor(R.color.title_color));
			tx2.setTextColor(view.getResources().getColor(R.color.white));
		}

		return view;
	}
}
