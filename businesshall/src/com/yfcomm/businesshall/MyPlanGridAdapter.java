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
public class MyPlanGridAdapter extends BaseAdapter {

	private Context mContext;
	//private final static String TAG = "MyPlanGridAdapter";
	public List<OperterData.Operplannames> plannames;
	private TextView tx1,tx2;

	public MyPlanGridAdapter(Context mContext, List<OperterData.Operplannames> Operplannames) {
		super();
		this.plannames = Operplannames;
		this.mContext = mContext;
	}

	
	public boolean setItemsel(int postion) {
		if(postion>=(plannames.size()-1)) return false;

		for (int i = 0; i < plannames.size(); i++) {
			if (i == postion) {
				plannames.get(i).is_sel = true;
			} else {
				plannames.get(i).is_sel = false;
			}
		}
		notifyDataSetChanged();
		return true;
	}
	
	@Override
	public int getCount() {
		return plannames == null ? 0 : plannames.size();
	}

	@Override
	public OperterData.Operplannames getItem(int position) {

		if (plannames != null && plannames.size() != 0) {
			return plannames.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.gird_item,
				null);

		tx1 = (TextView) view.findViewById(R.id.tv_item1);
		tx2 = (TextView) view.findViewById(R.id.tv_item2);
		OperterData.Operplannames oneplan = getItem(position);
		tx1.setText(oneplan.planname);
	//	tx1.setTextSize(10);
		tx2.setText(oneplan.note);
		tx2.setTextSize(15);

		if (oneplan.is_sel) {
			view.setBackgroundColor(view.getResources().getColor(R.color.title_color));
			tx2.setTextColor(view.getResources().getColor(R.color.white));
		}
	    return view;
	}	
}
