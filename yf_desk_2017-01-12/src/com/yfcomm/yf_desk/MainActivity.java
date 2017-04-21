package com.yfcomm.yf_desk;

import java.util.ArrayList;
import java.util.List;
import com.yfcomm.db.MySQLiteHelper;
import com.yfcomm.public_define.AppInfo;
import com.yfcomm.public_define.PackagesTools;
import com.yfcomm.public_define.ReCreateWin;
import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.public_define;
import com.yfcomm.public_define.yf_SharedPreferences;
import com.yfcomm.public_define.WinInfo.Postion;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
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
import android.util.YFComm;

public  class MainActivity extends FragmentActivity {

	public List<WinInfo> win_i = null;
	public String TAG = "MainActivity";
	public RelativeLayout layout, bottombar, leftOutView;
	private ViewPager vp;
	private FragAdapter adapter;

	private int bottom_d = 1090;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private List<AppInfo> App_i = null;
	private MySQLiteHelper mysqlite = null;
	private List<ImageButton> imageButton;
	private int curpage = 0;
	private String[] leftAppInfosName = { public_define.otaPacketname, public_define.settingname };
	private String[] ProhibitPackagename;
	private MyGridView[] myGridViews;
	private final static int HOMEPAGE = 1;
	private final static int ItemNum = 6;
	private int Frame_size;
	private PackagesTools packtools;
	private static final String ROOT_NAME = "yf_desk";
	private static final String PACK_ITEM = "prohibit_package";
    private  PackageBroadcastReceiver BroadCastRcv;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mysqlite = new MySQLiteHelper(this, public_define.yfdb, null, public_define.ver);
		packtools = new PackagesTools(this);
		
	//	vp = (ViewPager) findViewById(R.id.viewpager);
		bottombar = (RelativeLayout) findViewById(R.id.paged_view_indicator);
		
		layout = new RelativeLayout(this);
		Read_xmlwin_config();
		ReadMydbData();
		leftOutView = GetLeftSideView();
		fragments.add(new MyFragment(leftOutView));// LeftView
		if (win_i != null) {
			set_XML_view(this);
		}
		fragments.add(new MyFragment(layout));// HomeView
		LoadRightView();
		create_bottombar(bottombar, 0);
		adapter = new FragAdapter(getSupportFragmentManager(), fragments);
		setViewPagerListener();
		
		
		
	}
	
	@Override
	protected  void onResume() {
		super.onResume();

		if (ReadProhbitFromYFComm()) {
			ReCreateView();
		}
	}
	
    private boolean ReadProhbitFromYFComm() {

		String yfcomm_ver = YFComm.propertyGroupVersionFromXml(ROOT_NAME);
		String desk_ver = yf_SharedPreferences.Read_conf(this, public_define.YF_PROHIBIT_VER);
		if (yfcomm_ver != null) {
			yf_SharedPreferences.write_conf(this, public_define.YF_PROHIBIT_VER, yfcomm_ver);
			if (!yfcomm_ver.equals(desk_ver) || ProhibitPackagename == null) {
				List<String> proh_app = YFComm.arrayFromXml(null, ROOT_NAME, PACK_ITEM);
				if (proh_app != null) {
					ProhibitPackagename = new String[proh_app.size() + leftAppInfosName.length];
					System.arraycopy(leftAppInfosName, 0, ProhibitPackagename, 0, leftAppInfosName.length);
					proh_app.toArray(ProhibitPackagename);
					System.arraycopy(leftAppInfosName, 0, ProhibitPackagename, proh_app.size(),
							leftAppInfosName.length);
				}
				return true;
			}
		}
		return false;
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
		girdview.setAdapter(new MyGridAdapter(this, app_i, app_i.size()));
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

	@SuppressWarnings("deprecation")
	public void ResertButtonImg(int isSel) {

		for (int i = 0; i < Frame_size; i++) {
			if (i == isSel) {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.fouces));
			} else {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.unfouce));
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(vp!=null)
	    	vp.setCurrentItem(HOMEPAGE);
	}
	public void LoadRightView() {

		Frame_size += 2; // leftView+HomeView+RightView
		myGridViews = new MyGridView[Frame_size];
		for (int i = 0; i < Frame_size - 2; i++) {
			myGridViews[i + 2] = Create_newfragPage(i, i + 2);
		}
	}

	public void save_appinfo() {
		mysqlite.DelAllData();
		for (AppInfo nativeapp : App_i) {
			mysqlite.InsertAppData(nativeapp);
		}
		App_i = mysqlite.GetlistAppData();
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
		girdview.setAdapter(new MyGridAdapter(this, App_i.subList(num * ItemNum, (num + 1) * ItemNum), ItemNum));
		girdview.setOnItemClickListener(mAppClickListener);
		girdview.setBackgroundColor(AppXmltran.backcolor);

		r_l.addView(girdview, param);
		fragments.add(new MyFragment(r_l));
		return girdview;
	}

	public void Read_xmlwin_config() {
		XmlResourceParser xml = getResources().getXml(R.xml.home_hft);
	//	win_i = AppXmltran.Read(xml);
		loadprohibitApp();
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

			if (packtools.checkPackage(win_i.get(v.getId()).Packetname)) {

				if (v.getId() == 2) {
					packtools.LanchActvie(win_i.get(v.getId()).Packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (v.getId() == 5) {
					packtools.LanchActvie(win_i.get(v.getId()).Packetname, "com.hftcom.ui.LoadingActivity");
				} else if (v.getId() == 0) {
					packtools.LanchActvieParam(win_i.get(v.getId()).Packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (v.getId() == 1) {

				} else {
					packtools.LanchAPk(win_i.get(v.getId()).Packetname);
				}
			}
		}
	}

	private void ReadMydbData() {

		App_i = mysqlite.GetlistAppData();
		if (App_i.size() == 0) {
			App_i = GetNative_AppData();
			save_appinfo();
		}
		if (App_i.size() != 0) {
			if (DelnotExistPackage() || WhetherNewAddPacket()) {
				mysqlite.DelAllData();
				App_i = GetNative_AppData();
			}
			if (CoverPagerApps()) {
				save_appinfo();
			}
		}
	}

	public boolean WhetherNewAddPacket() {

		List<AppInfo> curapp = GetNative_AppData();
		for (AppInfo app : curapp) {
			if (!CheckWetherIsExist(app)) {
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
			if (app.IsNull == 1 || app.Packetname.equals("null"))
				continue;
			if (!packtools.checkPackage(app.Packetname)) {
				isExistedUnInstall = true;
			}
		}
		return isExistedUnInstall;
	}

	public boolean CoverPagerApps() {
		boolean isNeadSave = false;
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
			if (CheckAppWhterLoading(appList.activityInfo.applicationInfo.packageName)) {
				AppInfo appinfo = new AppInfo();
				appinfo.icon = appList.activityInfo.applicationInfo.loadIcon(getPackageManager());
				appinfo.title = appList.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				appinfo.Packetname = appList.activityInfo.applicationInfo.packageName;
				appInfo.add(appinfo);
			}
		}

		return appInfo;
	}

	public void loadprohibitApp() {

		List<String> prohbit = null;

		ReadProhbitFromYFComm();
		if (ProhibitPackagename == null) {

			XmlResourceParser xml = getResources().getXml(R.xml.prohibit);
			prohbit = AppProhibitXml.Read(xml);
			if (prohbit == null || prohbit.isEmpty()) {
				ProhibitPackagename = new String[leftAppInfosName.length];
				System.arraycopy(leftAppInfosName, 0, ProhibitPackagename, 0, leftAppInfosName.length);
			} else {
				ProhibitPackagename = new String[leftAppInfosName.length + prohbit.size()];
				prohbit.toArray(ProhibitPackagename);
				System.arraycopy(leftAppInfosName, 0, ProhibitPackagename, prohbit.size(), leftAppInfosName.length);
			}
		}
	}

	public boolean CheckAppWhterLoading(String packetname) {

		if (ProhibitPackagename != null) {
			for (int i = 0; i < ProhibitPackagename.length; i++) {
				if (ProhibitPackagename[i].equals(packetname))
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
		Set_Relativelayout_View(f_rl, wi.child_view, wi.child_text);
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

	public  void UpdateUIbyInstalledAPK(String packageName) {

		if (null != packtools.getLaunchIntentForPackage(packageName)) {

			AppInfo appInfo = packtools.GetPackageInfo(packageName);
			UpdateDBData(appInfo);
		}
	}

	public  void UnInstallAPk(String packectname) {

		for (int i = 0; i < App_i.size(); i++) {
			if (App_i.get(i).Packetname.equals(packectname) && App_i.get(i).IsNull == 0) {
				App_i.get(i).IsNull = 1;
				if (!IsRemoveView(((MyGridAdapter) myGridViews[i / ItemNum + 2].getAdapter()))) {
					mysqlite.UpdateAppData(App_i.get(i), public_define.packetname + "=?",
							new String[] { App_i.get(i).Packetname });
				}
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
				// mysqlite.DeleteListAppInfo(apps);
				ReCreateView();
			} else {
				Gadapter.notifyDataSetChanged();
			}
		}
		return IsNeadRemove;
	}

	public void ReCreateView() {

		if (mysqlite != null)
			mysqlite.DelAllData();
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
		int i = 0;
	    for(AppInfo app:App_i){
	    	if(app.Packetname.equals(appInfo)){
	    		return;
	    	}
	    }
		for (i = 0; i < App_i.size(); i++) {
			if (App_i.get(i).IsNull == 1) {
				IsExistNull = true;
				break;
			}
		}
		if (IsExistNull) {

		//	appInfo.id = App_i.get(i).id;
			App_i.get(i).icon = appInfo.icon;
			App_i.get(i).IsNull = appInfo.IsNull;
			App_i.get(i).Packetname = appInfo.Packetname;
			App_i.get(i).title = appInfo.title;

			((MyGridAdapter) myGridViews[i / ItemNum + 2].getAdapter()).notifyDataSetChanged();
		//	mysqlite.UpdateAppData(appInfo, "id=?", new String[] { Integer.toString(appInfo.id) });
		} else {
			mysqlite.InsertAppData(appInfo);
			ReCreateView();
		}
	}
	public boolean IsInwin(String packetname) {
		boolean isIN = false;
		for (int i = 0; i < win_i.size(); i++) {
			if (win_i.get(i).Packetname.equals(packetname)) {
				isIN = true;
				break;
			}
		}
		return isIN;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		BroadCastRcv.RemoveListen();
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

	@SuppressWarnings("deprecation")
	public void set_Child_Icon(Object m_r, Object m_parm, Postion icon) {

		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) m_parm;
		ImageButton btn;
		btn = new ImageButton(this);
		btn.setPadding(icon.left, icon.top, icon.right, icon.bottom);
		btn.setBackgroundColor(0x00000000);

		if (icon.buff.equals("shaoyishao.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.shaoyishao, null));
		else if (icon.buff.equals("fukuan.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.fukuan, null));
		else if (icon.buff.equals("shuaiyishua.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.shuayishua, null));
		else if (icon.buff.equals("huiyuanka.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.huiyuanka, null));
		else if (icon.buff.equals("licai.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.licai, null));
		else if (icon.buff.equals("syt.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.syt, null));
		else if (icon.buff.equals("tuangou.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.tuangou, null));
		else if (icon.buff.equals("kajuan.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.kajuan, null));
		else if (icon.buff.equals("bq.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.bq, null));
		else if (icon.buff.equals("ppsh.png"))
			btn.setImageDrawable(getResources().getDrawable(R.drawable.ppsh, null));
		else if (icon.buff.equals("huiming.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.huiming, null));
		} else if (icon.buff.equals("kdb.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.kdb, null));
		} else if (icon.buff.equals("hsy.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.hsy, null));
		} else if (icon.buff.equals("fc.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.fc, null));
		} else if (icon.buff.equals("hjy.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.hjy, null));
		} else if (icon.buff.equals("hfthuiyuanka.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.hfthuiyuanka, null));
		} else if (icon.buff.equals("busnisshall.png")) {
			btn.setImageDrawable(getResources().getDrawable(R.drawable.busnisshall, null));
		}
		btn.setClickable(false);
		m_rl.addView(btn, param);
	}

	@SuppressWarnings("deprecation")
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
		public void OnOtherpageSwapAppInfos(MyGridAdapter src, int srcpostion, int GirdID, int despostion) {

			AppInfo srcInfo = src.getPostionAppInfo(srcpostion);
			MyGridAdapter des = (MyGridAdapter) myGridViews[GirdID].getAdapter();
			AppInfo desInfo = des.getPostionAppInfo(despostion);
			mysqlite.swappostion(srcInfo, desInfo);
			int dragItem = (public_define.pageId - 2) * ItemNum + srcpostion;
			int dropItem = (GirdID - 2) * ItemNum + despostion;
			modifyAppInfo(srcInfo, desInfo, dragItem, dropItem);
			src.changeAppInfoData(desInfo, srcpostion);
			des.changeAppInfoData(srcInfo, despostion);
			IsRemoveView(src);
		}

		@Override
		public void OnOnepageSwapAppInfos(MyGridAdapter mDragAdapter, int srcpostion, int despostion) {

			if (public_define.pageId != 0) {

				AppInfo src = mDragAdapter.getPostionAppInfo(srcpostion);
				AppInfo des = mDragAdapter.getPostionAppInfo(despostion);
				mysqlite.swappostion(src, des);
				int dragItem = (public_define.pageId - 2) * ItemNum + srcpostion;
				int dropItem = (public_define.pageId - 2) * ItemNum + despostion;
				modifyAppInfo(src, des, dragItem, dropItem);
			}
			mDragAdapter.exchange(srcpostion, despostion);
		}
	};

	public void modifyAppInfo(AppInfo src, AppInfo des, int dragItem, int dropItem) {


		if (dragItem >= App_i.size() || dragItem >= App_i.size())
			return;
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
		//	Log.e(TAG, "db packet:" + app.Packetname + "isnull:" + app.IsNull + " app.id=" + app.id);
		}
		Log.e(TAG, "***************************************************");
		for (AppInfo app : App_i) {
		//	Log.e(TAG, "App_i packet:" + app.Packetname + "isnull:" + app.IsNull + " app.id=" + app.id);
		}
		Log.e(TAG, "******************End********************************");
	}

}
