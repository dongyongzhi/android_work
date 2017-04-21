package com.winksoft.yzsmk.home;

import java.util.ArrayList;
import java.util.List;

import com.winksoft.yzsmk.public_define.AppInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MyGridAdapter extends BaseAdapter {

	private Context mContext;
	private final static String TAG = "MyGridAdapter";
	/** TAG */
	public List<AppInfo> appinfo;

	private boolean isListChanged = false;

	boolean isVisible = true;

	private TextView item_text;
	private ImageView iv;
	public int remove_position = -1;

	public MyGridAdapter(Context mContext, List<AppInfo> Ainfo) {
		super();
		this.appinfo = Ainfo;
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

//	private int[] colors = new int[] { 0xff33CCFF, 0xff8C88FF, 0xff50D2C2, 0xffFDAF27, 0xff2CACFE };
	private int[] colors = new int[] { R.drawable.main_menu1, R.drawable.main_menu2, R.drawable.main_menu3,
			R.drawable.main_menu4, R.drawable.main_menu5 };
	
	@SuppressLint({ "ResourceAsColor", "ViewHolder", "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = LayoutInflater.from(mContext).inflate(R.layout.gird_item,
				null);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,250));

		item_text = (TextView) view.findViewById(R.id.tv_item);
		iv = (ImageView) view.findViewById(R.id.iv_item);

		AppInfo appi = getItem(position);

		if (appi.ismove == true || appi.IsNull == 1) {

			view.setBackgroundColor(0x00000000);
			appi.ismove = false;
			return view;

		} else {
			item_text.setText(appi.title);
//			if (appi.Packetname.equals(public_define.settingname)) {
//				iv.setImageDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.sz));
//				appi.color = 0xff4ec72e;
//				appi.firstpic = true;
//			} else if (appi.Packetname.equals("com.litian.huiming")) {
//				appi.color = 0xfffc5163;
//				appi.firstpic = true;
//				iv.setImageDrawable(appi.icon);
//			} else if (appi.Packetname.equals("com.huimin.ordersystem")) {
//				appi.color = 0xff3c98fd;
//				appi.firstpic = true;
//				iv.setImageDrawable(appi.icon);
//			} else if (appi.Packetname.equals(public_define.defendName)) {
//				appi.color = 0xFFFFA500;
//				appi.firstpic = true;
//				iv.setImageDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.store));
//			} else if (appi.Packetname.equals(public_define.customerservice)) {
//				appi.color = 0xFFFF668B;
//				appi.firstpic = true;
//				iv.setImageDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.kefu));
//			} else if (appi.Packetname.equals("com.sankuai.meituan.merchant")) {
//				appi.color = 0xFFFD3A58;
//				appi.firstpic = true;
//				iv.setImageDrawable(this.mContext.getResources().getDrawable(
//						R.drawable.kdb));
//				item_text.setText("开店宝");
//			} else {
//				iv.setImageDrawable(appi.icon);
//			}
			iv.setImageDrawable(appi.icon);
		}
		if (appi.firstpic == false) {

			int colorPos = position % colors.length;
			System.out.println(position + "%" + colors.length + "=" + colorPos);
			appi.color =  colors[colorPos];
			
			appi.firstpic = true;
		}
//		view.setBackgroundColor(appi.color);
		view.setBackground(mContext.getResources().getDrawable(appi.color));
		return view;
	}

	public void exchange(int dragPostion, int dropPostion) {

		AppInfo dragItem = getItem(dragPostion);
		AppInfo dropItem = getItem(dropPostion);

		int id = dragItem.id;
		int pageid = dragItem.curpageid;
		int pageitem = dragItem.curindex;

		dragItem.id = dropItem.id;
		dragItem.curpageid = dropItem.curpageid;
		dragItem.curindex = dropItem.curindex;

		dropItem.curpageid = pageid;
		dropItem.id = id;
		dropItem.curindex = pageitem;

		appinfo.remove(dragPostion);
		appinfo.add(dragPostion, dropItem);
		appinfo.remove(dropPostion);
		appinfo.add(dropPostion, dragItem);
	}

	public void setRemove(int position) {
		remove_position = position;
		notifyDataSetChanged();
	}

	public void remove() {
		appinfo.remove(remove_position);
		remove_position = -1;
		isListChanged = true;
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

	public void setListDate(ArrayList<AppInfo> list) {
		appinfo = list;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public boolean isListChanged() {
		return isListChanged;
	}

	public void setVisible(boolean visible) {
		isVisible = visible;
	}

	public void setShowDropItem(boolean show) {
		// isItemShow = show;
	}

}
