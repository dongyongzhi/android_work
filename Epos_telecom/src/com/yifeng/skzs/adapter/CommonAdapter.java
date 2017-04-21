package com.yifeng.skzs.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

/**
 * ClassName:CommonAdapter 
 * Description：公用适配器
 * @author WuJiaHong 
 * Date：2012-10-15
 */
public class CommonAdapter extends SimpleAdapter
{
	public int count = 8;
	Context context;
	List<Map<String, Object>> data;
	Resources res;
	private int colors[] = new int[]{ 0x30FFFFFF, 0x30BDBABD };
	public boolean isHtml = false;

	public CommonAdapter(Context context, List<Map<String, Object>> data, int resource, String[] from, int[] to, Resources res)
	{
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
		this.res = res;
	}

	public boolean need = false;// 是否需要按钮功能

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		View view = super.getView(position, convertView, parent);
		if (need)
		{
			int colorPos = position % colors.length;
			view.setBackgroundColor(colors[colorPos]);
		}
		
		return view;
	}

	public void setViewBinder()
	{
		super.setViewBinder(new MyViewBinder());
	}

	public class MyViewBinder implements ViewBinder
	{
		@Override
		public boolean setViewValue(View view, Object data, String textRepresentation)
		{
			if (data != null & (view instanceof ImageView) & (data instanceof Bitmap))
			{
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);
				return true;
			}
			return false;
		}
	}
}
