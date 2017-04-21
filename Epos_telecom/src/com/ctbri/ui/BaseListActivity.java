package com.ctbri.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ctbri.R;
import com.ctbri.ui.MyListView.OnRefreshListener;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.ListViewUtil;

/**
 * 列表基础类
 * 
 * @author user_ygl
 * 
 *         初始化所有父类的抽象方法 可用的父类变量 SUPERPAGENUM 页数 从0开始 SURPERDATA listview dataset
 *         formatDate 需要将 STRINGLIST 转化为 SURPERDATA
 * 
 */
public abstract class BaseListActivity extends BaseActivity
{
	protected MyListView listview;
	int lastItem = 0;
	ListViewUtil footbar;
	protected int SUPERPAGENUM = 0;
	protected List<Map<String, Object>> SURPERDATA = new ArrayList<Map<String, Object>>();
	protected List<Map<String, String>> STRINGLIST;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	protected void intiListview()
	{
		listview = (MyListView) findViewById(R.id.listview);
		footbar = new ListViewUtil(this, listview);
		listview.setonRefreshListener(new OnRefreshListener()
		{
			public void onRefresh()
			{
				new AsyncTask<Void, Void, Void>()
				{
					@Override
					protected Void doInBackground(Void... params)
					{
						SUPERPAGENUM = 0;
						STRINGLIST = setDataMethod();
						return null;
					}

					@Override
					protected void onPostExecute(Void result)
					{
						addData();
						listview.onRefreshComplete();
					}

					@Override
					protected void onPreExecute()
					{
						footbar.removeFootBar();
						footbar.addFootBar();

					}
				}.execute();
			}

			@Override
			public void addDate()
			{
				doSetListView();

			}

		});
		doSetListView();
	}

	protected void doSetListView()
	{
		if (SUPERPAGENUM == 0)
		{
			SURPERDATA.clear();
			footbar.addFootBar();
			setAdapter();
		}
		new Thread(SUPERRUNBLE).start();
	}

	/**
	 * 为listview 设置adapter
	 */
	protected abstract void setAdapter();

	void addData()
	{
		if (STRINGLIST == null)
		{
			footbar.showListAddDataState(ConstantUtil.IS_EMPTY + "");
			return;
		}
		if (SUPERPAGENUM == 0)
			SURPERDATA.clear();

		SUPERPAGENUM++;
		String state = STRINGLIST.get(0).get("state");

		if (STRINGLIST.get(0).get("state").equals(String.valueOf(ConstantUtil.LOGIN_SUCCESS)))
		{
			if (STRINGLIST.size() < 10)
				footbar.removeFootBar();
			formatDate();

		} else
		{
			footbar.showListAddDataState(state);
		}
		myNotifyDataSetChanged();
	}

	/**
	 * 格式化返回数据 将
	 */
	protected abstract void formatDate();

	/**
	 * listview 刷新
	 */
	protected abstract void myNotifyDataSetChanged();

	protected abstract List<Map<String, String>> setDataMethod();

	Runnable SUPERRUNBLE = new Runnable()
	{
		public void run()
		{
			try
			{
				Thread.sleep(100);
				STRINGLIST = setDataMethod();
				SUPERHANDLER.sendMessage(SUPERHANDLER.obtainMessage());
			} catch (InterruptedException e)
			{
			}
		}
	};
	Handler SUPERHANDLER = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			addData();
		}
	};
}
