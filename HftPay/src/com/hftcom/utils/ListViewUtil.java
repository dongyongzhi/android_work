package com.hftcom.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hftcom.R;

/**
 * 
 * comment:普通ListView类
 * @author:ZhangYan 
 * Date:2012-7-19
 */
public class ListViewUtil
{

	public ListViewUtil(Context context, ListView listview)
	{
		this.context = context;
		this.listview = listview;
	}

	Context context;
	ProgressBar progressBar;
	LinearLayout loadingLayout;
	ListView listview;

	/**
	 * 在底部增加内容
	 */
	public void addFootBar()
	{
		LayoutInflater inflater = LayoutInflater.from(this.context);
		View searchLayout = inflater.inflate(R.layout.publicloadingprogress, null);
		loadingLayout = new LinearLayout(this.context);
		loadingLayout.addView(searchLayout);
		loadingLayout.setGravity(Gravity.CENTER);
		listview.addFooterView(loadingLayout);
	}

	/**
	 * 底部移除
	 */
	public void removeFootBar()
	{
		listview.removeFooterView(loadingLayout);
	}

	/**
	 * 显示当前请求回来的状态
	 * 
	 * @param state
	 */
	public void showListAddDataState(String state)
	{
		if (state.equals(String.valueOf(ConstantUtil.SERVER_ERROR)))
		{
			Toast.makeText(context, "服务器未响应", Toast.LENGTH_SHORT).show();
			removeFootBar();
		}

		else if (state.equals(String.valueOf(ConstantUtil.INNER_ERROR)))
		{
			Toast.makeText(context, "数据解析失败", Toast.LENGTH_SHORT).show();
			removeFootBar();

		} else if (state.equals(String.valueOf(ConstantUtil.IS_EMPTY)))
		{
			removeFootBar();
			Toast.makeText(context, "无新数据", Toast.LENGTH_SHORT).show();
		} else if (state.equals(String.valueOf(ConstantUtil.DATA_NULL)))
		{
			removeFootBar();
			Toast.makeText(context, "服务端放回null", Toast.LENGTH_SHORT).show();
		} else if (state.equals(String.valueOf(ConstantUtil.KEY_ERROR)))
		{
			removeFootBar();
			Toast.makeText(context, "重复登录", Toast.LENGTH_SHORT).show();
		}
	}

}
