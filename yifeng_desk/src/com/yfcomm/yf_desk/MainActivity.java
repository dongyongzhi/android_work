package com.yfcomm.yf_desk;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.yfcomm.db.MySQLiteHelper;
import com.yfcomm.homekey.HomeWatcher;
import com.yfcomm.homekey.OnHomePressedListener;
import com.yfcomm.public_define.AppInfo;
import com.yfcomm.public_define.PackagesTools;
import com.yfcomm.public_define.ReCreateWin;
import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.public_define;
import com.yfcomm.public_define.WinInfo.Postion;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity {

	public List<WinInfo> win_i = null;
	public String TAG = "MainActivity";
	public RelativeLayout layout, bottombar, leftOutView;
	private ViewPager vp;
	private FragAdapter adapter;

	private int bottom_d = 1090;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private List<AppInfo> App_i = null;
	private MySQLiteHelper mysqlite = null;

	private HomeWatcher mHomeWatcher;
	private List<ImageButton> imageButton;
	private int curpage = 0;
	private String[] leftAppInfosName = { public_define.otaPacketname, public_define.settingname };
	private String[] ProhibitPackagename = { public_define.safeName, public_define.chrome };
	private String[] AllowSystemAppName = { public_define.defendName };
	private MyGridView[] myGridViews;
	private final static int HOMEPAGE = 1;
	private final static int ItemNum = 6;
	private int Frame_size;
	private PackagesTools packtools;
	private static MyHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		handler = new MyHandler(this);
		mysqlite = new MySQLiteHelper(this, public_define.yfdb, null, public_define.ver);
		WatchHomeKEY();

		packtools = new PackagesTools(this);
		vp = (ViewPager) findViewById(R.id.viewpager);
		bottombar = (RelativeLayout) findViewById(R.id.bottombar);
		layout = new RelativeLayout(this);

		Read_xmlwin_config();

		ReadMydbData();

		leftOutView = GetLeftSideView();
		fragments.add(new MyFragment(leftOutView)); // LeftView

		if (win_i != null) {
			set_XML_view(this);
		}
		fragments.add(new MyFragment(layout));// HomeView

		LoadRightView();

		create_bottombar(bottombar, 0);
		adapter = new FragAdapter(getSupportFragmentManager(), fragments);
		setViewPagerListener();
	}

	public RelativeLayout GetLeftSideView() {

		List<AppInfo> app_i = new ArrayList<AppInfo>();
		RelativeLayout r_l = new RelativeLayout(this);

		for (int i = 0; i < leftAppInfosName.length; i++) {
			AppInfo appInfo = packtools.GetPackageInfo(leftAppInfosName[i]);
			if (appInfo != null) {
				app_i.add(appInfo);
			}
		}
		r_l.setBackgroundColor(AppXmltran.backcolor);

		public_define.width = 720;
		public_define.hight = bottom_d - 80 - 30;

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(680, bottom_d);
		param.topMargin = 40;
		param.leftMargin = 20;

		MyGridView girdview = new MyGridView(this, RecreateW, Frame_size, vp);
		girdview.setId(-1);
		girdview.setVerticalSpacing(10);
		girdview.setHorizontalSpacing(10);
		girdview.setNumColumns(2);
		girdview.setAdapter(new MyGridAdapter(this, app_i,2));
		girdview.setOnItemClickListener(mAppClickListener);
		girdview.setBackgroundColor(AppXmltran.backcolor);

		r_l.addView(girdview, param);
		return r_l;

	}

	public void setViewPagerListener() {
		vp.setAdapter(adapter);
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				curpage = arg0;
				ResertButtonImg(arg0);
			}

		});
		vp.setCurrentItem(HOMEPAGE);
	}

	public void ResertButtonImg(int isSel) {

		for (int i = 0; i < Frame_size; i++) {
			if (i == isSel) {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.fouces));
			} else {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.unfouce));
			}
		}
	}

	public void WatchHomeKEY() {
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {

			@Override
			public void onHomePressed() {
				vp.setCurrentItem(HOMEPAGE);
			}

			@Override
			public void onHomeLongPressed() {
				Log.e(TAG, "onHomeLongPressed");
			}
		});

		mHomeWatcher.startWatch();
	}

	public void LoadRightView() {
		
		Frame_size += 2; // leftView+HomeView+RightView
		myGridViews=new MyGridView[Frame_size];
		for (int i = 0; i < Frame_size - 2; i++) {
			myGridViews[i+2]=Create_newfragPage(i, i + 2);
		}
	}

	public void save_appinfo() {
		mysqlite.DelAllData();
		for (AppInfo nativeapp : App_i) {
			mysqlite.InsertAppData(nativeapp);
		}
	}

	public MyGridView Create_newfragPage(int num, int pageid) {

		RelativeLayout r_l = new RelativeLayout(this);
		r_l.setBackgroundColor(AppXmltran.backcolor);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(680, bottom_d);		
		param.topMargin = 40;
		param.leftMargin = 20;

		MyGridView girdview = new MyGridView(this, RecreateW, Frame_size, vp);
		girdview.setId(pageid);
		girdview.setVerticalSpacing(10);
		girdview.setHorizontalSpacing(10);
		girdview.setNumColumns(2);
		girdview.setAdapter(new MyGridAdapter(this,App_i.subList(num*ItemNum , (num+1)*ItemNum),ItemNum));
		girdview.setOnItemClickListener(mAppClickListener);
		girdview.setBackgroundColor(AppXmltran.backcolor);

		r_l.addView(girdview, param);
		fragments.add(new MyFragment(r_l));
		return girdview;
	}

	public void Read_xmlwin_config() {

		try {
			InputStream is = getAssets().open("home_hft.xml");
			win_i = AppXmltran.Read(is);
			is.close();

		} catch (IOException e) {
			Log.e(TAG, "文件加载失败");
		}
	}

	private OnItemClickListener mAppClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parentview, View childView, int postion, long row) {

			MyGridView m_beg = (MyGridView) parentview;
			MyGridAdapter beg = (MyGridAdapter) m_beg.getAdapter();
			AppInfo app_i = beg.getPostionAppInfo(postion);
			if (app_i.IsNull == 0) {

				if (app_i.Packetname.equals(public_define.customerservice)) {
					String url = "http://chat32.live800.com/live800/chatClient/chatbox.jsp?companyID=649370&configID=84621&jid=3529787052";
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);

				} else {

					Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(app_i.Packetname);
					startActivity(LaunchIntent);
				}
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class ButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (packtools.checkPackage(win_i.get(v.getId()).packetname)) {

				if (v.getId() == 2) {// 刷一刷
					packtools.LanchActvie(win_i.get(v.getId()).packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (v.getId() == 5) {// 收银台
					packtools.LanchActvie(win_i.get(v.getId()).packetname, "com.hftcom.ui.LoadingActivity");
				} else if (v.getId() == 0) {// 扫一扫
					packtools.LanchActvieParam(win_i.get(v.getId()).packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (v.getId() == 1) {// 付款码

				} else {
					packtools.LanchAPk(win_i.get(v.getId()).packetname);
				}
			}
		}
	}

	private void ReadMydbData() {
		App_i = mysqlite.GetlistAppData(); // 获取数据库数据
		if (App_i.size() == 0) {
			App_i = GetNative_AppData();
		}
		if (App_i.size() != 0) {
			if (CoverPagerApps())// 重新分配页面
				save_appinfo();// 保存页面数据
		}
	}

	public boolean WhetherNewAddPacket() {// 检查是否有新增PACKET

		List<AppInfo> curapp = GetNative_AppData();

		for (AppInfo app : curapp) {

			if (!CheckWetherIsExist(app)) {
				App_i.add(app);
				return true;
			}
		}
		return false;
	}

	public boolean CheckWetherIsExist(AppInfo curapp) {
		for (AppInfo app : App_i) {
			if (curapp.Packetname.equals(app.Packetname)) {
				return true;
			}
		}
		return false;
	}

	public boolean DelnotExistPackage() {
		boolean isExistedUnInstall = false;
		for (AppInfo app : App_i) {
			if (app.IsNull == 1 || app.Packetname.equals("null"))// 空白APP需保留
				continue;
			if (!packtools.checkPackage(app.Packetname)) {
				App_i.remove(app);
				isExistedUnInstall = true;
			}
		}
		return isExistedUnInstall;
	}

	public boolean CoverPagerApps() {
		boolean isNeadSave = false, bturn;
		bturn = DelnotExistPackage(); // 删除不存在的APP
		if (bturn)
			isNeadSave = bturn;
		bturn = WhetherNewAddPacket();// 加载未加载的APP
		if (bturn)
			isNeadSave = bturn;
		if (App_i.size() != 0 && App_i.size() % ItemNum != 0) {
			int loop = ItemNum - App_i.size() % ItemNum;
			for (int i = 0; i < loop; i++) {
				AppInfo app = new AppInfo();
				app.IsNull = 1;
				app.Packetname = "null";
				app.title = "null";
				App_i.add(app);
			}
			isNeadSave = true;
		}
		Frame_size = App_i.size() / ItemNum;
		return isNeadSave;
	}

	private List<AppInfo> GetNative_AppData() {

		List<AppInfo> appInfo = new ArrayList<AppInfo>();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appLists = getPackageManager().queryIntentActivities(intent, 0);

		for (int i = 0; i < appLists.size(); i++) {

			ResolveInfo appList = appLists.get(i);
			if (CheckAppWhterLoading(appList.activityInfo.applicationInfo.packageName,
					appList.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)) {
				AppInfo appinfo = new AppInfo();
				appinfo.icon = appList.activityInfo.applicationInfo.loadIcon(getPackageManager());
				appinfo.title = appList.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				appinfo.Packetname = appList.activityInfo.applicationInfo.packageName;
				appInfo.add(appinfo);
			}
		}

		return appInfo;
	}

	public boolean CheckAppWhterLoading(String packetname, int systemflag) {

		/********** System Application whether allowed **********/
		if (systemflag != 0) {
			for (int i = 0; i < AllowSystemAppName.length; i++) {
				if (packetname.equals(AllowSystemAppName[i]))
					return true;
			}
			return false;
		} else {/********* User Application ************/

			/********** check whether prohibit loading package **********/
			for (int i = 0; i < ProhibitPackagename.length; i++) {
				if (packetname.equals(ProhibitPackagename[i]))
					return false;
			}

			/*********** check whether is in one page0 (left page) ******/
			for (int i = 0; i < leftAppInfosName.length; i++) {
				if (packetname.equals(leftAppInfosName[i]))
					return false;
			}

			/*********** check whether is in one page1 (home page) *******/
			for (int i = 0; i < win_i.size(); i++) {
				if (packetname.equals(win_i.get(i).packetname))
					return false;
			}
		}

		return true;
	}

	public void set_XML_view(Context context) {

		layout.setBackgroundColor(AppXmltran.backcolor);

		for (int i = 0; i < win_i.size(); i++) {
			set_Relativelayout_View(layout, win_i.get(i), i);
		}
	}

	public void set_Relativelayout_View(Object obj, WinInfo wi, int i) {

		RelativeLayout m_rL = (RelativeLayout) obj;

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(wi.right, wi.bottom);
		param.topMargin = wi.top;
		param.leftMargin = wi.left;

		RelativeLayout f_rl = new RelativeLayout(this);
		f_rl.setId(i);
		f_rl.setClickable(true);

		wi.id = f_rl.getId();
		f_rl.setOnClickListener(new ButtonListener());
		f_rl.setBackgroundColor(wi.color);

		m_rL.addView(f_rl, param);
		Set_Relativelayout_View(f_rl, wi.icon, wi.text);
	}

	public void Set_Relativelayout_View(Object obj, List<Postion> icon, List<Postion> text) {

		RelativeLayout.LayoutParams param;

		RelativeLayout m_rl = (RelativeLayout) obj;

		param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		if (icon != null) {
			for (int i = 0; i < icon.size(); i++)
				set_Child_Icon(m_rl, param, icon.get(i));
		}
		if (text != null) {
			for (int i = 0; i < text.size(); i++)
				Set_Child_Text(m_rl, param, text.get(i));
		}
	}

	public void UpdateUIbyInstalledAPK(String packageName) {
		AppInfo appInfo = null;
		PackageInfo info;

		try {
			info = getPackageManager().getPackageInfo(packageName, 0);
			appInfo = new AppInfo();
			appInfo.Packetname = info.packageName;
			appInfo.title = info.applicationInfo.loadLabel(getPackageManager()).toString();
			appInfo.icon = info.applicationInfo.loadIcon(getPackageManager());

			List<AppInfo> infos = GetNative_AppData();
			for (AppInfo appcur : infos) {
				if (appcur.Packetname.equals(appInfo.Packetname)) {
					UpdateDBData(appInfo);
				}
			}

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void UnInstallAPk(String packectname) {

		for (int i = 0; i < App_i.size(); i++) {
			if (App_i.get(i).Packetname.equals(packectname) && App_i.get(i).IsNull == 0) {

				App_i.get(i).IsNull = 1;
				mysqlite.UpdateAppData(App_i.get(i), public_define.packetname + "=?",
						new String[] { App_i.get(i).Packetname });

				// IsRemoveView((MyGridAdapter)
				// public_define.myView.get(App_i.get(i).curpageid -
				// 1).getAdapter());
				break;
			}
		}
	}

	public boolean IsRemoveView(MyGridAdapter Gadapter) {
		boolean IsNeadRemove = true;
		List<AppInfo> apps = Gadapter.GetAppInfoList();
		if (apps != null) {
			for (AppInfo app : apps) {
				if (app.IsNull == 0) {
					IsNeadRemove = false;
					break;
				}
			}
			if (IsNeadRemove) {
				mysqlite.DeleteListAppInfo(apps);
				ReCreateView();
			} else {
				Gadapter.notifyDataSetChanged();
			}
		}
		return IsNeadRemove;
	}

	public void ReCreateView() {
		vp.removeAllViews();
		if (fragments != null) {
			fragments.clear();
		}
		ReadMydbData();

		leftOutView = GetLeftSideView();
		fragments.add(new MyFragment(leftOutView));

		fragments.add(new MyFragment(layout));
		LoadRightView();

		bottombar.removeAllViews();
		create_bottombar(bottombar, curpage % fragments.size());
		adapter.notifyDataSetChanged();
	}

	public void UpdateDBData(AppInfo appInfo) {

		boolean IsExistNull = false;

		for (int i = 0; i < App_i.size(); i++) {
			if (App_i.get(i).IsNull == 1) {
				appInfo.id = App_i.get(i).id;
				App_i.remove(i);
				App_i.add(i, appInfo);
				IsExistNull = true;
				break;
			}
		}
		if (IsExistNull) {
			mysqlite.UpdateAppData(appInfo, "id=?", new String[] {Integer.toString(appInfo.id) });
		} else {
			mysqlite.InsertAppData(appInfo);
			ReCreateView();
		}
	}

	private static class MyHandler extends Handler {

		private MainActivity main;

		public MyHandler(MainActivity main) {
			this.main = main;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case public_define.MSG_PACKAGE_INSTALLED:
				if (!main.IsInwin(msg.obj.toString()))
					main.UpdateUIbyInstalledAPK(msg.obj.toString());
				break;
			case public_define.MSG_PACKAGE_UNINSTALLED:
				if (!main.IsInwin(msg.obj.toString()))
					main.UnInstallAPk(msg.obj.toString());
				break;

			}
		}
	}

	public static class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

				if (!intent.getData().getSchemeSpecificPart().equals(public_define.safeName)) {
					handler.obtainMessage(public_define.MSG_PACKAGE_INSTALLED, intent.getData().getSchemeSpecificPart())
							.sendToTarget();
				}

			} else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {

				if (!intent.getData().getSchemeSpecificPart().equals(public_define.safeName))
					handler.obtainMessage(public_define.MSG_PACKAGE_UNINSTALLED,
							intent.getData().getSchemeSpecificPart()).sendToTarget();
			}
		}

	}

	public boolean IsInwin(String packetname) {
		boolean isIN = false;
		for (int i = 0; i < win_i.size(); i++) {
			if (win_i.get(i).packetname.equals(packetname)) {
				isIN = true;
				break;
			}
		}
		return isIN;
	}

	@Override
	public void onDestroy() {
		if (mHomeWatcher != null)
			mHomeWatcher.stopWatch();
		super.onDestroy();
	}

	@SuppressLint("RtlHardcoded")
	public void Set_Child_Text(Object m_r, Object m_parm, Postion text) {
		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) m_parm;
		TextView tv = new TextView(this);
		tv.setPadding(text.left, text.top, text.right, text.bottom);
		tv.setText(text.buff);
		if (text.diplaymode == 0)
			tv.setGravity(Gravity.CENTER);
		else if (text.diplaymode == 1)
			tv.setGravity(Gravity.LEFT);
		else if (text.diplaymode == 4)
			tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		tv.setClickable(false);
		if (text.size != 0)
			tv.setTextSize(text.size);
		tv.setTextColor(text.color);
		// tv.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
		m_rl.addView(tv, param);
	}

	public void set_Child_Icon(Object m_r, Object m_parm, Postion icon) {

		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) m_parm;
		ImageButton btn;
		btn = new ImageButton(this);
		btn.setPadding(icon.left, icon.top, icon.right, icon.bottom);
		btn.setBackgroundColor(0x00000000);

		try {
			Field field = R.drawable.class.getField(icon.buff);
			btn.setImageDrawable(getResources().getDrawable(field.getInt(field.getName())));
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "loading failed: " + e.getMessage());
		} catch (NotFoundException e) {
			Log.e(TAG, "loading failed: " + e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e(TAG, "loading failed: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "loading failed: " + e.getMessage());
		}

		btn.setClickable(false);
		m_rl.addView(btn, param);
	}

	public void create_bottombar(Object m_r, int IsSelect) {
		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams margin = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				30);
		margin.topMargin = bottom_d;
		margin.leftMargin = 360 - 20 * Frame_size;

		if (imageButton == null)
			imageButton = new ArrayList<ImageButton>();
		else
			imageButton.clear();

		for (int i = 0; i < Frame_size; i++) {
			imageButton.add(new ImageButton(this));
			imageButton.get(i).setPadding(40 * i, 0, 40 * Frame_size - 40 * (i + 1), 0);
			imageButton.get(i).setBackgroundColor(0x00000000);
			if (i == IsSelect) {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.fouces));
			} else {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.unfouce));
			}

			m_rl.addView(imageButton.get(i), margin);
		}
	}

	private ReCreateWin RecreateW = new ReCreateWin() {

		@Override
		public void OnOtherpageSwapAppInfos(MyGridAdapter src, int srcpostion, int GirdID,int despostion) {

			AppInfo srcInfo = src.getPostionAppInfo(srcpostion);
			
			MyGridAdapter des=(MyGridAdapter) myGridViews[GirdID].getAdapter();
			
			AppInfo desInfo = des.getPostionAppInfo(despostion);

			mysqlite.swappostion(srcInfo, desInfo);
			modifyAppInfo(srcInfo,desInfo);	
			src.changeAppInfoData(desInfo, srcpostion);
			des.changeAppInfoData(srcInfo, despostion);
		    IsRemoveView(src);// 判断是否需要清除SrcPage
		}

		@Override
		public void OnOnepageSwapAppInfos(MyGridAdapter mDragAdapter, int srcpostion, int despostion) {
			
			if (public_define.pageId != 0) {
				
				AppInfo src=mDragAdapter.getPostionAppInfo(srcpostion);
				AppInfo des=mDragAdapter.getPostionAppInfo(despostion);
				mysqlite.swappostion(src,des);
				modifyAppInfo(src,des);	
			}
	    	mDragAdapter.exchange(srcpostion, despostion);
		}
	};

	public void modifyAppInfo(AppInfo src, AppInfo des){
	
		int dragItem=src.id-1;
		int dropItem=des.id-1;
		
		src.id=dropItem+1;
		des.id=dragItem+1;
		
		App_i.remove(dragItem);
		App_i.add(dragItem, des);
		App_i.remove(dropItem);
		App_i.add(dropItem, src);
	}
	
	public void Print_DBAppInfo() {
		List<AppInfo> dbAppInfos;
		dbAppInfos = mysqlite.GetlistAppData();
		Log.e(TAG, "*****************Begin******************************");
		for (AppInfo app : dbAppInfos) {
			Log.e(TAG, "db packet:" + app.Packetname + "isnull:" + app.IsNull + " app.id=" + app.id);
		}
		Log.e(TAG, "***************************************************");
		for (AppInfo app : App_i) {
			Log.e(TAG, "App_i packet:" + app.Packetname + "isnull:" + app.IsNull + " app.id=" + app.id);
		}
		Log.e(TAG, "******************End********************************");
	}

}
