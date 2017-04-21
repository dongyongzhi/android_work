package com.hftcom.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;

import com.hftcom.R;
import com.hftcom.widget.AsynLoadImageView;

public class ImageAdapter1 extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Integer> images;

	public ImageAdapter1(Context context, List<Integer> images) {
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
			img.setAdjustViewBounds(true);
			img.setScaleType(ScaleType.CENTER_CROP);
			
			if(this.images==null || this.images.size()==0)
				img.setImageResource(R.drawable.pic);
			else
				img.setImageResource(this.images.get(position));
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
