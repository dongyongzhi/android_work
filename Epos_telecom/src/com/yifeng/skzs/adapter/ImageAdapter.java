package com.yifeng.skzs.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ctbri.R;
import com.ctbri.widget.AsynLoadImageView;

public class ImageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> images;

	public ImageAdapter(Context context, List<String> images) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.images = images;
	}

	@Override
	public int getCount() {
		return images == null ? 0 : this.images.size(); // 返回很大的值使得getView中的position不断增大来实现循环。
	}

	@Override
	public Object getItem(int position) {
		return this.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int id = position;
       
		
		 
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.advertisement_item, null);
			AsynLoadImageView img = (AsynLoadImageView) convertView
					.findViewById(R.id.imgView);
			if(this.images==null || this.images.size()==0)
				img.setImageResource(R.drawable.pic);
			else
				img.setImageURI(this.images.get(position));
			img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.out.println("第 ===" + id + " ===张图!");
				}
			});

		}
		return convertView;
	}
}
