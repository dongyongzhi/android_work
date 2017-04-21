package com.yfcomm.yf_desk;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yfcomm.public_define.AppInfo;
import com.yfcomm.public_define.public_define;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
		// this.appinfo = Ainfo;
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
				appi.color = 0xff4ec72e;
				appi.firstpic = true;
			} else if (appi.Packetname.equals("com.litian.huiming")) {
				appi.color = 0xfffc5163;
				appi.firstpic = true;
				iv.setImageDrawable(appi.icon);
			} else if (appi.Packetname.equals("com.huimin.ordersystem")) {
				appi.color = 0xff3c98fd;
				appi.firstpic = true;
				iv.setImageDrawable(appi.icon);
			} else if (appi.Packetname.equals(public_define.defendName)) {
				appi.color = 0xFFFFA500;
				appi.firstpic = true;
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.store));
			} else if (appi.Packetname.equals(public_define.customerservice)) {
				appi.color = 0xFFFF668B;
				appi.firstpic = true;
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.kefu));
			} else if (appi.Packetname.equals("com.sankuai.meituan.merchant")) {
				appi.color = 0xFFFD3A58;
				appi.firstpic = true;
				iv.setImageDrawable(this.mContext.getResources().getDrawable(R.drawable.kdb));
				item_text.setText("开店宝");
			} else {
				iv.setImageDrawable(appi.icon);
			}
		}
		if (appi.firstpic == false) {

			int x = (int) (Math.random() * 6) + 1;
			if (x == 1)
				appi.color = 0xFFFFA500;
			else if (x == 2)
				appi.color = 0xFFFF668B;
			else if (x == 3)
				appi.color = 0xFFFFB922;
			else if (x == 4)
				appi.color = 0xFFFD3A58;
			else if (x == 5)
				appi.color = 0xFF0EC5D9;
			else if (x == 6)
				appi.color = 0xFF91D92A;
			appi.firstpic = true;
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

		if (postion >= appinfo.size()) {
			AppInfo app = new AppInfo();
			app.IsNull = 1;
			appinfo.add(app);
		}
		return appinfo.get(postion);
	}

	public void changeAppInfoData(AppInfo dragItem, int Postion) {
		appinfo.remove(Postion);
		appinfo.add(Postion, dragItem);
		notifyDataSetChanged();
	}

	public List<AppInfo> GetAppInfoList() {

		return appinfo == null ? null : appinfo;
	}

}
