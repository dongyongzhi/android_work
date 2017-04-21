package com.ctbri.ui.query;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;
/**
 * 
 * @author user_ygl
 *
 */
public abstract class MyBaseAdapter extends SimpleAdapter{
	private List<? extends Map<String, ?>> data;
	/**
	 * 
	 * @param context  
	 * @param data
	 * @param resource  xm�ļ�
	 * @param from   ��Ӧ��id
	 * @param to    ��Ӧ��data���
	 */
	public MyBaseAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.data=data;
	}
}
