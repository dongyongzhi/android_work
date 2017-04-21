package com.yfcomm.businesshall;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Business extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";
	private int num;
	private Context context;
	private View rootView;

	public static Business newInstance(int sectionNumber, Context context) {
		Business fragment = new Business(sectionNumber, context);
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);

		return fragment;
	}

	public Business(int sectionNumber, Context context) {
		this.num = sectionNumber;
		this.context = context;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (null != rootView) {
			ViewGroup parent = (ViewGroup) rootView.getParent();
			if (null != parent) {
				parent.removeView(rootView);
			}
		} else {
		
			if (num == 1)
				rootView = inflater.inflate(R.layout.planchange, container, false);
			
			
			
			
			
			else if (num == 2) {
				rootView = inflater.inflate(R.layout.orderflow, container, false);
			}else{
				rootView = inflater.inflate(R.layout.reservation, container, false);
			}
		}
		return rootView;
	}

}
