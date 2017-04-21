package com.yfcomm.yf_desk;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

public class FragAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;

	public FragAdapter(FragmentManager fm) {
		super(fm);
	}

	public FragAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public int getItemPosition(Object object) {
		// refresh all fragments when data set changed
		return PagerAdapter.POSITION_NONE;
	}
}