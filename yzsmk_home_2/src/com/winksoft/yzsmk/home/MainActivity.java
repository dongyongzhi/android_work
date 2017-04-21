package com.winksoft.yzsmk.home;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.winksoft.yzsmk.db.MySQLiteHelper;
import com.winksoft.yzsmk.home.R.color;
import com.winksoft.yzsmk.homekey.HomeWatcher;
import com.winksoft.yzsmk.homekey.OnHomePressedListener;
import com.winksoft.yzsmk.public_define.AppInfo;
import com.winksoft.yzsmk.public_define.ReCreateWin;
import com.winksoft.yzsmk.public_define.WinInfo;
import com.winksoft.yzsmk.public_define.public_define;
import com.winksoft.yzsmk.public_define.WinInfo.Postion;
import com.winksoft.yzsmk.utils.UiUtils;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("RtlHardcoded")
public class MainActivity extends FragmentActivity {

	public List<WinInfo> win_i = null;
	public String TAG = "YF_DESK";
	public RelativeLayout layout, bottombar;
	private ViewPager vp;
	private FragAdapter adapter;
	private int Frame_size;
	private int bottom_d = 1090;
	private List<Fragment> fragments;
	private List<AppInfo> App_i = null;
	private MyInstalledReceiver InstalledReceiver = null;
	private MySQLiteHelper mysqlite = null;
	private int ItemNum = 6;
	private HomeWatcher mHomeWatcher;
	private List<ImageView> imageViews = new ArrayList<ImageView>();
	private int curpage = 0;
	private Banner banner;
	private Object[] images= new Object[]{R.drawable.banner1,R.drawable.banner2,R.drawable.banner3};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initBanner();
		
		RegeistRecv_DBinit();

		vp = (ViewPager) findViewById(R.id.viewpager);
		bottombar = (RelativeLayout) findViewById(R.id.bottombar);

		fragments = new ArrayList<Fragment>();

		layout = new RelativeLayout(this);
		Read_xmlwin_config();
		ReadMydbData();

		if (win_i != null) {
			set_XML_view(this);
		}
		fragments.add(new MyFragment(layout, 0));
		LoadMyGridView();
		create_bottombar(bottombar, 0);
		adapter = new FragAdapter(getSupportFragmentManager(), fragments);
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
		vp.setCurrentItem(0);

	}
	
	private void initBanner(){
		banner = (Banner) findViewById(R.id.banner1);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setDelayTime(4000);
        banner.setImages(images);
	}

	public void WatchHomeKEY() {
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {

			@Override
			public void onHomePressed() {
				vp.setCurrentItem(0);
			}

			@Override
			public void onHomeLongPressed() {
				Log.e(TAG, "onHomeLongPressed");
			}
		});

		mHomeWatcher.startWatch();

	}

	public void LoadMyGridView() {

		if (public_define.myView != null) {
			public_define.myView.clear();
		} else {
			public_define.myView = new ArrayList<MyGridView>();
		}

		for (int i = 0; i < (Frame_size - 1); i++) {
			Create_newfragPage(i * ItemNum, i + 1);
		}
		public_define.total_page = Frame_size;
		public_define.vp = vp;
	}

	public void save_appinfo() {
		mysqlite.DelAllData();
		for (AppInfo nativeapp : App_i) {
			mysqlite.InsertAppData(nativeapp);
		}
	}

	public void RegeistRecv_DBinit() {

		public_define.my_sqlite = mysqlite = new MySQLiteHelper(this, public_define.yfdb, null, public_define.ver);

		InstalledReceiver = new MyInstalledReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.PACKAGE_ADDED");
		filter.addAction("android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");
		this.registerReceiver(InstalledReceiver, filter);
		WatchHomeKEY();
	}

	public void Create_newfragPage(int offset, int index) {

		List<AppInfo> app_i = new ArrayList<AppInfo>();
		RelativeLayout r_l = new RelativeLayout(this);
		for (int i = 0; i < ItemNum; i++) {
			if (App_i.size() == offset + i)
				break;
			app_i.add(App_i.get(offset + i));
		}

		r_l.setBackgroundColor(AppXmltran.backcolor);

		public_define.width = 720;
		public_define.hight = bottom_d - 80 - 30;
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		param.topMargin = 396;
		param.leftMargin = 20;
		param.rightMargin = 20;
		param.bottomMargin = 10;

		MyGridView girdview = new MyGridView(this, RecreateW);
		girdview.setId(index);
		girdview.setVerticalSpacing(20);
		girdview.setHorizontalSpacing(20);
		girdview.setNumColumns(2);
		girdview.setAdapter(new MyGridAdapter(this, app_i));
		girdview.setOnItemClickListener(mAppClickListener);
		girdview.setBackgroundColor(AppXmltran.backcolor);
		girdview.setSelector(new StateListDrawable());

		r_l.addView(girdview, param);
		public_define.myView.add(girdview);
		fragments.add(new MyFragment(r_l, index));
	}

	public void Read_xmlwin_config() {

		try {
			InputStream is = getAssets().open("home_yzsmk.xml");
			win_i = AppXmltran.Read(is);
			is.close();

		} catch (IOException e) {
			Log.e(TAG, "文件加载失败");
		}
	}

	private OnItemClickListener mAppClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			MyGridView m_beg = public_define.myView.get(av.getId() - 1);
			MyGridAdapter beg = (MyGridAdapter) m_beg.getAdapter();
			AppInfo app_i = beg.getPostionAppInfo(arg2);
			if (app_i.IsNull == 0) {

				if (app_i.Packetname.equals(public_define.customerservice)) {
					String url = "http://chat32.live800.com/live800/chatClient/chatbox.jsp?companyID=649370&configID=84621&jid=3529787052";
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);

				}else {
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

	class ButtonListener implements OnClickListener {

		private MainActivity activity;

		public ButtonListener(MainActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			Log.e(TAG, "id=" + v.getId());

			if (checkPackage(win_i.get(v.getId()).packetname)) {
				activity.LanchAPk(win_i.get(v.getId()).packetname);
			}
		}
	}

	public boolean checkPackage(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		if(packageName.equals("none")){
			Toast.makeText(MainActivity.this, "尚未开通", Toast.LENGTH_SHORT).show();
			return false;
		}
		try {
			getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public void LanchActvie(String packetname, String classname) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		ComponentName cn = new ComponentName(packetname, classname);
		intent.setComponent(cn);
		startActivity(intent);
	}

	public void LanchActvieParam(String packetname, String classname) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.putExtra("wx", true);
		ComponentName cn = new ComponentName(packetname, classname);
		intent.setComponent(cn);
		startActivity(intent);
	}

	private void ReadMydbData() {

		App_i = mysqlite.GetlistAppData(); // 获取数据库数据
		if (App_i.size() == 0) {
			App_i = GetNative_AppData();
			checkAppAndFilltheseats();
			save_appinfo();
		} else {
			if (!checkAppAndFilltheseats()) {
				
				save_appinfo();
			}
		}
		Frame_size = 1 + App_i.size() / ItemNum;

	}

	public void IsNewAddPacket() {// 检查是否有新增PACKET
    
		List<AppInfo> curapp = GetNative_AppData();

		for (AppInfo app : curapp) {
			if (!CheckWetherIsExist(app)) {
				addApp(app);
			}
		}
	}

	public int addApp(AppInfo appInfo) {
		int postion = -1;
		for (int i = 0; i < App_i.size(); i++) {
			if (App_i.get(i).IsNull == 1) {
				postion = i;
				break;
			}
		}
		if (postion != -1) {
			App_i.get(postion).Packetname = appInfo.Packetname;
			App_i.get(postion).title = appInfo.title;
			App_i.get(postion).icon = appInfo.icon;
			App_i.get(postion).IsNull = appInfo.IsNull;
		} else {
			App_i.add(appInfo);
		}
		return postion;
	}

	public boolean CheckWetherIsExist(AppInfo curapp) {
		for (AppInfo app : App_i) {
			if (curapp.Packetname.equals(app.Packetname) && app.IsNull!=1) {
				return true;
			}
		}
		return false;
	}

	public boolean checkAppAndFilltheseats() {

		for (int i = 0; i < App_i.size(); i++) {// 检查是否被卸载
			if (!checkPackage(App_i.get(i).Packetname))
				App_i.get(i).IsNull = 1;
			
			  if (App_i.get(i).id == 0) App_i.get(i).id = i + 1;
			  App_i.get(i).curpageid = i / ItemNum + 1; 
                          App_i.get(i).curindex = i % 5;
			 
		}
                IsNewAddPacket();
		int offset = App_i.size();
		if (App_i.size() != 0 && App_i.size() % ItemNum != 0) {// 分页
			int loop = ItemNum - App_i.size() % ItemNum;
			for (int i = 0; i < loop; i++) {
				AppInfo app = new AppInfo();
				app.IsNull = 1;
				app.id = offset + i + 1;
				app.curpageid = App_i.size() / ItemNum + 1;
				app.curindex = App_i.size() % ItemNum + i + 1;
				app.Packetname = App_i.get(0).Packetname;
				app.title = App_i.get(0).title;
				app.icon = App_i.get(0).icon;
				App_i.add(app);
			}

			return false;
		}
		return true;
	}

	private List<AppInfo> GetNative_AppData() {

		boolean IsSetingExists = false;
		List<AppInfo> appInfo = new ArrayList<AppInfo>();
		AppInfo appsetinfo = new AppInfo();

		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> appLists = getPackageManager().queryIntentActivities(intent, 0);

		for (int i = 0; i < appLists.size(); i++) {

			ResolveInfo appList = appLists.get(i);
			if ((appList.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					|| appList.activityInfo.applicationInfo.packageName.equals(public_define.settingname)
					|| appList.activityInfo.applicationInfo.packageName.equals(public_define.defendName)
					|| appList.activityInfo.applicationInfo.packageName.equals(public_define.otaPacketname)) {

				if (appList.activityInfo.applicationInfo.packageName.equals(public_define.settingname)) {

					appsetinfo.icon = appList.activityInfo.applicationInfo.loadIcon(getPackageManager());
					appsetinfo.title = appList.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
					appsetinfo.Packetname = appList.activityInfo.applicationInfo.packageName;

					IsSetingExists = true;

				} else if (!appList.activityInfo.applicationInfo.packageName.equals(public_define.safeName)
						&& !appList.activityInfo.applicationInfo.packageName.equals(public_define.chrome)) {
					if (checkAppisLoad(appList.activityInfo.applicationInfo.packageName)) {
						AppInfo appinfo = new AppInfo();
						appinfo.icon = appList.activityInfo.applicationInfo.loadIcon(getPackageManager());
						appinfo.title = appList.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
						appinfo.Packetname = appList.activityInfo.applicationInfo.packageName;
						appInfo.add(appinfo);
					}
				}
			}
		}
		if (IsSetingExists)
			appInfo.add(appsetinfo);
		return appInfo;
	}

	public boolean checkAppisLoad(String packetname) {

		for (int i = 0; i < win_i.size(); i++) {
			if (packetname.equals(win_i.get(i).packetname))
				return false;
		}
		return true;
	}

	public void LanchAPk(String packetname) {
		Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(packetname);
		startActivity(LaunchIntent);
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
		f_rl.setOnClickListener(new ButtonListener(this));
		f_rl.setBackground(getResources().getDrawable(getResource(wi.drawable)));

		m_rL.addView(f_rl, param);
		Set_Relativelayout_View(f_rl, wi.icon, wi.text);
	}

	/**
	 * 设置子View图片和文字
	 * @param obj
	 * @param icon
	 * @param text
	 */
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

				IsRemoveView((MyGridAdapter) public_define.myView.get(App_i.get(i).curpageid - 1).getAdapter());
				break;
			}
		}
	}

	public void IsRemoveView(MyGridAdapter Gadapter) {
		boolean IsNeadRemove = true;

		for (int i = 0; i < Gadapter.appinfo.size(); i++) {
			if (Gadapter.appinfo.get(i).IsNull == 0) {
				IsNeadRemove = false;
			}
		}
		if (IsNeadRemove) {
			for (int i = 0; i < Gadapter.appinfo.size(); i++) {
				App_i.remove(Gadapter.appinfo.get(i));
			}
			save_appinfo();
			ReCreateView();
		} else {
			Gadapter.notifyDataSetChanged();
		}
	}

	public void Print_DBAppInfo() {
		List<AppInfo> dbAppInfos;
		dbAppInfos = mysqlite.GetlistAppData();
		for (AppInfo app : dbAppInfos) {
			Log.e(TAG, "db packet:" + app.Packetname + "isnull:" + app.IsNull);
		}
		Log.e(TAG, "***************************************************");
		for (AppInfo app : App_i) {
			Log.e(TAG, "app_i packet:" + app.Packetname + "isnull:" + app.IsNull);
		}
	}

	public void ReCreateView() {
		vp.removeAllViews();
		if (fragments != null) {
			fragments.clear();
		}
		ReadMydbData();
		fragments.add(new MyFragment(layout, 0));
		LoadMyGridView();
		bottombar.removeAllViews();
		create_bottombar(bottombar, curpage % fragments.size());
		adapter.notifyDataSetChanged();
	}

	public void UpdateDBData(AppInfo appInfo) {

		boolean IsExistNull = false;
		int i;
		for (i = 0; i < App_i.size(); i++) {
			if (App_i.get(i).IsNull == 1) {
				IsExistNull = true;
				break;
			}
		}
		if (IsExistNull) {
			App_i.get(i).Packetname = appInfo.Packetname;
			App_i.get(i).title = appInfo.title;
			App_i.get(i).icon = appInfo.icon;
			App_i.get(i).IsNull = appInfo.IsNull;
			mysqlite.UpdateAppData(App_i.get(i), "id=?", new String[] { Integer.toString(i + 1) });

			MyGridAdapter Gadapter = (MyGridAdapter) public_define.myView.get(App_i.get(i).curpageid - 1).getAdapter();
			Gadapter.notifyDataSetChanged();

		} else {

			appInfo.curpageid = App_i.size() / ItemNum + 1;
			appInfo.curindex = 0;
			mysqlite.InsertAppData(appInfo);
			App_i.add(appInfo);
			ReCreateView();

		}
	}

	public class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {

				if (!intent.getData().getSchemeSpecificPart().equals(public_define.safeName)) {
					if (!IsInwin(intent.getData().getSchemeSpecificPart()))
						UpdateUIbyInstalledAPK(intent.getData().getSchemeSpecificPart());
				}

			} else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {

				if (!intent.getData().getSchemeSpecificPart().equals(public_define.safeName))
					if (!IsInwin(intent.getData().getSchemeSpecificPart()))
						UnInstallAPk(intent.getData().getSchemeSpecificPart());
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
		if (InstalledReceiver != null)
			unregisterReceiver(InstalledReceiver);
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

	/**
	 * 设置子View图片
	 * @param m_r
	 * @param m_parm
	 * @param icon
	 */
	public void set_Child_Icon(Object m_r, Object m_parm, Postion icon) {

		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) m_parm;
		ImageButton btn;
		btn = new ImageButton(this);

		btn.setPadding(icon.left, icon.top, icon.right, icon.bottom);
		btn.setBackgroundColor(0x00000000);
		
		int resId = getResource(icon.buff);
		if(resId != -1){
			btn.setImageDrawable(this.getResources().getDrawable(getResource(icon.buff)));
		}

		btn.setClickable(false);
		m_rl.addView(btn, param);
	}

	/**
	 * 创建底部小圆点
	 * @param m_r
	 * @param IsSelect
	 */
	public void create_bottombar(Object m_r, int IsSelect) {
		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams margin = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
//		margin.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(margin);
		layout.setGravity(Gravity.CENTER);
		
		imageViews.clear();

		for (int i = 0; i < Frame_size; i++) {
			ImageView imageView = new ImageView(this);
			if (i == IsSelect) {
				imageView.setBackgroundResource(R.drawable.fouces);
			} else {
				imageView.setBackgroundResource(R.drawable.unfouce);
			}
			imageViews.add(imageView);
			layout.addView(imageView);
		}
		m_rl.addView(layout);
	}
	
	/**
	 * 设置当前页小圆点
	 * @param isSel
	 */
	public void ResertButtonImg(int isSel) {

		for (int i = 0; i < Frame_size; i++) {
			if (i == isSel) {
				imageViews.get(i).setBackgroundResource(R.drawable.fouces);
			} else {
				imageViews.get(i).setBackgroundResource(R.drawable.unfouce);
			}
		}
	}

	private ReCreateWin RecreateW = new ReCreateWin() {

		@Override
		public void recreateWin(List<AppInfo> appInfo) {

			for (int i = 0; i < ItemNum; i++) {
				App_i.remove(appInfo.get(i));
			}
			save_appinfo();
			ReCreateView();

		}

		@Override
		public void UpdateAPP_I() {
			if (App_i != null)
				App_i.clear();
			App_i = mysqlite.GetlistAppData();
		

		}

		@Override
		public void swapAppInfo(AppInfo src, AppInfo des) {

			App_i.remove(src.id - 1);
			App_i.add(src.id - 1, src);
			App_i.remove(des.id - 1);
			App_i.add(des.id - 1, des);

		}
	};
	
	
	public int getResource(String imageName){  
		 int resId = -1;
		 try{
			 resId = getResources().getIdentifier(imageName, "drawable" , getPackageName());
		 }catch (Exception e) {
			e.printStackTrace();
		 }
		 return resId;  
	 }
	
	
//	@Override  
//	public void onWindowFocusChanged(boolean hasFocus) {  
//	    super.onWindowFocusChanged(hasFocus);  
//	  
//	    Rect rect = new Rect();  
//	    getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);  
//	    // 状态栏高度  
//	    int statusBarHeight = rect.top;  
//	    View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);  
//	    int contentTop = v.getTop();  
//	  
//	    // 标题栏高度  
//	    int titleBarHeight = contentTop - statusBarHeight;  
//	    Log.e("", "标题栏的高度：" + Integer.toString(titleBarHeight) + "\n"  
//	            + "状态栏高度：" + statusBarHeight + "\n" + "视图的宽度：" + v.getWidth()  
//	            + "\n" + "视图的高度（不包含状态栏和标题栏）：" + v.getHeight());
//	}  
}
