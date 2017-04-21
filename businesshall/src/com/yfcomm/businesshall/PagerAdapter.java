package com.yfcomm.businesshall;

import java.util.Locale;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PagerAdapter extends FragmentPagerAdapter {

	Context context;
	int index;

	public PagerAdapter(FragmentManager fm, Context context, int index) {
		super(fm);
		this.context = context;
		this.index = index;

	}

	@Override
	public Fragment getItem(int position) {

		if (index == 0)
			return Recharge.newInstance(position + 1, context);
		else {
			return Business.newInstance(position + 1, context);
		}
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return context.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return context.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return context.getString(R.string.title_section3).toUpperCase(l);
		}
		return null;
	}
}