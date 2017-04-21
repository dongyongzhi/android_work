package com.hftcom.adapter;

import java.util.List;
import java.util.Map;
import com.hftcom.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * ClassName:CommonAdapter Description：公用适配器
 * 
 * @author WuJiaHong Date：2012-10-15
 */
public class CommonAdapter extends SimpleAdapter {
	public int count = 10;
	private Activity context;
	private List<Map<String, Object>> data;
	private int[] colors = new int[] { 0x30000000, 0x30A0A0A0 };
	public boolean isHtml = false;
	public boolean travel = false;
	private ListView listView;
	public Handler chandler;

	public CommonAdapter(Activity context, List<Map<String, Object>> data,
			int resource, String[] from, int[] to, ListView listView) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.listView = listView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		return view;
	}

	public void setViewBinder() {
		super.setViewBinder(new MyViewBinder());
	}

	public class MyViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (data != null && (view instanceof ImageView)
					&& (data instanceof String)) {
				return true;
			} else if (data != null & (view instanceof ImageView)
					& (data instanceof Bitmap)) {
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);
				return true;
			}
			return false;
		}
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
		notifyDataSetChanged();
	}
}
