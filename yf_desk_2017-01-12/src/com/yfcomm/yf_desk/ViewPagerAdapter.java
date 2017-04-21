package com.yfcomm.yf_desk;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ViewPagerAdapter extends PagerAdapter {

	private List<RelativeLayout> myViews;

	public ViewPagerAdapter(List<RelativeLayout> fragments) {
		this.myViews = fragments;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(myViews.get(position));// 删除页卡
	}
	
	public List<RelativeLayout> getMyViews() {
		return myViews;
	}
	@Override
	public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
		container.addView(myViews.get(position), 0);// 添加页卡
		return myViews.get(position);
	}

	@Override
	public int getCount() {
		return myViews.size();// 返回页卡的数量
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;// 官方提示这样写
	}

}
