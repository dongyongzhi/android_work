package com.yfcomm.yf_desk;

import java.util.ArrayList;
import java.util.List;

import com.yfcomm.public_define.AppInfo;
import com.yfcomm.public_define.public_define;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridAdapter extends BaseAdapter {

	private Context mContext;
	private List<AppInfo> appinfo;
	boolean isVisible = true;
	private TextView item_text;
	private ImageView iv;
	public int remove_position = -1;

	public MyGridAdapter(Context mContext, List<AppInfo> Ainfo, int size) {
		super();
		appinfo = new ArrayList<AppInfo>();
		for (int i = 0; i < size; i++) {
			appinfo.add(Ainfo.get(i));
		}
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return appinfo == null ? 0 : appinfo.size();
	}

	@Override
	public AppInfo getItem(int position) {

		if (appinfo != null && appinfo.size() != 0) {
			return appinfo.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "ResourceAsColor", "ViewHolder", "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.gird_item, null);

		item_text = (TextView) view.findViewById(R.id.tv_item);
		iv = (ImageView) view.findViewById(R.id.iv_item);

		AppInfo appi = getItem(position);

		if (appi.ismove == true || appi.IsNull == 1) {

			view.setBackgroundColor(0x00000000);
			appi.ismove = false;
			return view;

		} else {
			item_text.setText(appi.title);
			if (appi.Packetname.equals(public_define.settingname)) {
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.sz));
			} else if (appi.Packetname.equals("com.litian.huiming")) {
				iv.setImageDrawable(appi.icon);
			} else if (appi.Packetname.equals("com.huimin.ordersystem")) {
				iv.setImageDrawable(appi.icon);
			} else if (appi.Packetname.equals(public_define.defendName)) {
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.store));
			} else if (appi.Packetname.equals(public_define.customerservice)) {
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.kefu));
			} else if (appi.Packetname.equals("com.sankuai.meituan.merchant")) {
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.kdb));
				item_text.setText("开店宝");
			} else {
				iv.setImageDrawable(appi.icon);
			}
		}
	    int color[]={0xFFFF668B,0xFFFFA500,0xFFFD3A58,0xFF0EC5D9,0xFF91D92A,0xFFFFB922};
	    if(!appi.firstpic){
	    	appi.color=color[position];
	    	appi.firstpic=true;
	    }
		view.setBackgroundColor(appi.color);
		return view;
	}

	public void exchange(int dragPostion, int dropPostion) {

		AppInfo dragItem = getItem(dragPostion);
		AppInfo dropItem = getItem(dropPostion);

		appinfo.remove(dragPostion);
		appinfo.add(dragPostion, dropItem);

		appinfo.remove(dropPostion);
		appinfo.add(dropPostion, dragItem);
		
		notifyDataSetChanged();
	}

	public AppInfo getPostionAppInfo(int postion) {

		return appinfo.get(postion);
	}

	public void changeAppInfoData(AppInfo dragItem, int Postion) {
		appinfo.remove(Postion);
		appinfo.add(Postion, dragItem);
		notifyDataSetChanged();
	}

	public List<AppInfo> GetAppInfoList() {

		return appinfo;
	}

}
