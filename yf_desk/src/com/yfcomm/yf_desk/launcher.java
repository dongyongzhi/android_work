package com.yfcomm.yf_desk;

import java.util.ArrayList;
import java.util.List;

import com.yfcomm.public_define.MySQLiteHelper;
import com.yfcomm.public_define.PackagesTools;
import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.public_define;
import com.yfcomm.public_define.yf_SharedPreferences;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.YFComm;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public final class launcher extends Activity {

	private final static String TAG = "launcher";
	private PackageBroadcastReceiver BroadCastRcv;
	private String[] ProhibitPackagename;
	private PackagesTools packtools;
	private MyPageView vp;
	private ViewPagerAdapter adapter;
	private LayoutInflater mInflater;
	private int HOMEPAGE = 0;
	private final static int FRAME_ITEM = 6;
	private List<RelativeLayout> frament = new ArrayList<RelativeLayout>();
	private List<WinInfo> apps;
	private String[] leftAppInfosName = { public_define.otaPacketname, public_define.settingname };
	private static final String ROOT_NAME = "yf_desk";
	private static final String PACK_ITEM = "prohibit_package";
	private RelativeLayout bottombar;
	private int Frame_size;
	private List<ImageButton> imageButton;
	private int bottom_d = 1090;
	private DragController dragController;
	private Handler mhandler;
	private final static int INVAILD_PAGER = -1;
	private final static int INVAILD_PACKAGE = -1;
	private boolean isPause = true;
	private MySQLiteHelper mysqlite = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mysqlite = new MySQLiteHelper(this, public_define.yfdb, null, public_define.ver);
		mhandler = new Handler();
		dragController = new DragController(this, mhandler);
		mInflater = getLayoutInflater();
		packtools = new PackagesTools(this);
		initView();
		adapter = new ViewPagerAdapter(frament);
		setViewPagerAdpater();
		BroadCastRcv = new PackageBroadcastReceiver(this, mhandler);
		BroadCastRcv.StartListen();
	}

	private void initView() {

		RelativeLayout layout = new RelativeLayout(this);

		vp = new MyPageView(this, mhandler);
		layout.addView(vp);

		bottombar = new RelativeLayout(this);
		layout.addView(bottombar);
		loadWinData();
		setContentView(layout);
	}

	private void loadWinData() {

		apps = mysqlite.GetlistAppData();

		if (apps == null || apps.isEmpty()) {

			if (apps == null) {
				apps = new ArrayList<WinInfo>();
			}
			setupViews();
			mysqlite.DelAllData();
			mysqlite.BatchImportList(apps);

		} else {
			AllocationPager(apps);
		}
		Frame_size = frament.size();
		create_bottombar(bottombar, HOMEPAGE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;

		if (YFComm.propertyGroupVersionFromXml(ROOT_NAME)!=null && !YFComm.propertyGroupVersionFromXml(ROOT_NAME)
				.equals(yf_SharedPreferences.Read_conf(this, public_define.YF_PROHIBIT_VER))) {
			mysqlite.DelAllData();// 先删除数据存储
			frament.clear(); // 清除缓存
			apps.clear();// 清除WinInfo缓存
			setupViews();
			mysqlite.BatchImportList(apps);// 加入缓存
			final ViewPagerAdapter adapter_v = new ViewPagerAdapter(frament);
			vp.setAdapter(adapter_v);
			adapter = adapter_v;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	private void AllocationPager(List<WinInfo> wInfos) {

		List<List<WinInfo>> wins = new ArrayList<List<WinInfo>>();
		List<WinInfo> win;
		int type;

		for (WinInfo wi : wInfos) {
			win = IsAddNewPager(wins, wi.pageId);
			if (win == null) {
				win = new ArrayList<WinInfo>();
				win.add(wi);
				wins.add(win);
			} else {
				win.add(wi);
			}
		}

		for (int i = 0; i < wins.size(); i++) {
			type = wins.get(i).size() > FRAME_ITEM ? MyRelFragment.custom : MyRelFragment.standard;
			if (type == MyRelFragment.custom)
				HOMEPAGE = i;
			MyRelFragment rel = new MyRelFragment(this, i, wins.get(i), type);

			frament.add(rel);
		}
		// sortFramnt(frament);
	}

	private List<WinInfo> IsAddNewPager(List<List<WinInfo>> wins, int pageID) {

		for (List<WinInfo> win : wins) {
			if (win.get(0).pageId == pageID) {
				return win;
			}
		}
		return null;
	}

	private void setupViews() {

		int pageid = 0, offset, page_num, rightnum;

		// left pager
		offset = createLeftsideView(pageid);

		pageid = offset == 0 ? 0 : 1;

		if (AppXmltran.Read(getResources().getXml(R.xml.home_yf), apps, this)) {
			// home pager
			MyRelFragment rel = new MyRelFragment(this, pageid, Sublist(apps, offset, apps.size()),
					MyRelFragment.custom);
			frament.add(rel);
			offset = apps.size();
			HOMEPAGE = pageid;
			pageid++;
		}

		ReadLocalAppInfo();

		rightnum = apps.size() - offset;

		page_num = rightnum % FRAME_ITEM == 0 ? rightnum / FRAME_ITEM : rightnum / FRAME_ITEM + 1;
		for (int i = 0; i < page_num; i++) {
			// right pager
			MyRelFragment relFragment = new MyRelFragment(this, pageid + i,
					Sublist(apps, i * FRAME_ITEM + offset,
							(((i + 1) * FRAME_ITEM) > rightnum ? rightnum : (i + 1) * FRAME_ITEM) + offset),
					MyRelFragment.standard);
			frament.add(relFragment);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (vp != null)
			vp.setCurrentItem(HOMEPAGE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private List<WinInfo> Sublist(List<WinInfo> parent, int start, int end) {

		List<WinInfo> childlist = new ArrayList<WinInfo>();
		for (int i = start, item = 0; i < end; i++, item++) {
			parent.get(i).item = item;
			childlist.add(parent.get(i));
		}
		return childlist;
	}

	private int createLeftsideView(int index) {

		for (int i = 0; i < leftAppInfosName.length; i++) {
			WinInfo appInfo = packtools.GetPackageInfo(leftAppInfosName[i]);
			if (appInfo != null) {
				apps.add(appInfo);
			}
		}
		if (apps.size() != 0) {
			MyRelFragment rel = new MyRelFragment(this, index, Sublist(apps, 0, apps.size()), MyRelFragment.standard);
			frament.add(rel);
		}
		return apps.size();
	}

	protected LayoutInflater GetLauncherInflater() {
		return mInflater;
	}

	private void ReadLocalAppInfo() {

		loadprohibitApp();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> appLists = getPackageManager().queryIntentActivities(intent, 0);

		for (int i = 0; i < appLists.size(); i++) {

			ResolveInfo appList = appLists.get(i);
			if (CheckAppWhterLoading(appList.activityInfo.applicationInfo.packageName)) {

				WinInfo appinfo = new WinInfo();

				//appinfo.isSystem = appList.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM;
				appinfo.icon = appList.activityInfo.applicationInfo.loadIcon(getPackageManager());
				appinfo.title = appList.activityInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				appinfo.Packetname = appList.activityInfo.applicationInfo.packageName;
				apps.add(appinfo);
			}
		}
	}

	private void loadprohibitApp() {

		List<String> prohbit = null;
		ReadProhbitFromYFComm();
		if (ProhibitPackagename == null) {
			prohbit = AppProhibitXml.Read(getResources().getXml(R.xml.prohibit));
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

	private void setViewPagerAdpater() {

		vp.setAdapter(adapter);
		vp.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				ResertButtonImg(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		vp.setCurrentItem(HOMEPAGE);
	}

	protected int FindCellItem(int totalpage, final WinInfo winInfo) {

		int pager = INVAILD_PAGER;
		int item = 0;

		for (int i = HOMEPAGE + 1; i <= totalpage; i++) {
			MyRelFragment fragment = (MyRelFragment) adapter.getMyViews().get(i);
			if (MyRelFragment.INVALID_POSITION != (item = fragment.getIdleItem())) {
				pager = i;
				fragment.AddchildtoIdle(winInfo, item);
				apps.add(winInfo);
				mhandler.post(new Runnable() {
					@Override
					public void run() {
						mysqlite.UpdateWinData(winInfo);
					}
				});

				break;
			}
		}
		return pager;
	}

	protected void bindWinInfo(final WinInfo winInfo) {

		final int pageCount = vp.getChildCount();
		final List<WinInfo> winInfos = new ArrayList<WinInfo>();

		if (INVAILD_PAGER == FindCellItem(pageCount, winInfo)) {

			winInfo.pageId = pageCount + 1;
			winInfos.add(winInfo);
			apps.add(winInfo);

			MyRelFragment mFragment = new MyRelFragment(this, pageCount + 1, winInfos, MyRelFragment.standard);
			frament.add(mFragment);
			adapter.notifyDataSetChanged();

			mhandler.post(new Runnable() {

				@Override
				public void run() {
					mysqlite.InsertWinData(winInfo, apps.size());
				}
			});

			Frame_size = Frame_size + 1;
			create_bottombar(bottombar, pageCount + 1);
			if (!isPause) {
				vp.setCurrentItem(pageCount + 1);
			}
		}
	}

	private void printWinInfo() {

		for (WinInfo win : apps) {
			Log.e(TAG,
					"ID=" + win.item + " title=" + win.title + " package=" + win.Packetname + " pagid=" + win.pageId);
		}
	}

	protected void UnInstallAPk(String packectname) {

		final int apps_index = isExistPackageName(packectname);
		if (apps_index != INVAILD_PACKAGE) {
			
			final int dropPage = apps.get(apps_index).pageId;
			if (packtools.GetPackageIsSystem(packectname)<= 0) {
				MyRelFragment fragment = (MyRelFragment) adapter.getMyViews().get(dropPage);
				final WinInfo winInfo = apps.get(apps_index);
				fragment.removeChild(winInfo.item);
				apps.remove(apps_index);
				if (fragment.getChildCount() == 0) {
					RemoveFremnt(dropPage, dropPage == 0 ? 0 : dropPage - 1);
				}
				mhandler.post(new Runnable() {

					@Override
					public void run() {
						mysqlite.deleteWinInfo(winInfo);
					}
				});
			}

		}

	}

	private List<RelativeLayout> SublistRelativeLayout(List<RelativeLayout> parent, int start, int end) {

		List<RelativeLayout> childlist = new ArrayList<RelativeLayout>();
		for (int i = start; i < end; i++) {
			childlist.add(parent.get(i));
		}
		return childlist;
	}

	protected void RemoveFremnt(int pageId, int dropPage) {
		int TagPage;
		final List<RelativeLayout> newframent = SublistRelativeLayout(frament, 0, frament.size());
		newframent.remove(pageId);
		final ViewPagerAdapter adapter_v = new ViewPagerAdapter(newframent);
		vp.setAdapter(adapter_v);
		HOMEPAGE = HOMEPAGE > pageId ? HOMEPAGE - 1 : HOMEPAGE;
		for (WinInfo win : apps) {
			win.pageId = win.pageId > pageId ? win.pageId - 1 : win.pageId;
		}
		for (int i = 0; i < vp.getChildCount(); i++) {
			MyRelFragment mFragment = (MyRelFragment) vp.getChildAt(i);
			TagPage = (Integer) mFragment.getTag();
			if (TagPage > pageId) {
				mFragment.setTag(TagPage - 1);
			}
		}
		frament = newframent;
		adapter = adapter_v;
		dropPage = dropPage > pageId ? dropPage - 1 : dropPage;
		if (!isPause) {
			vp.setCurrentItem(dropPage);
		}
		Frame_size = Frame_size - 1;
		create_bottombar(bottombar, dropPage);
	}

	protected void InstallAppInfo(String packageName) {

		if (INVAILD_PACKAGE == isExistPackageName(packageName) && CheckAppWhterLoading(packageName)) {
			bindWinInfo(packtools.GetPackageInfo(packageName));
		}
	}

	protected int isExistPackageName(String packagename) {

		for (int i = 0; i < apps.size(); i++) {

			if (apps.get(i).Packetname.equals(packagename)) {
				return i;
			}
		}
		return INVAILD_PACKAGE;
	}

	protected boolean CheckAppWhterLoading(String packetname) {

		if (ProhibitPackagename != null) {
			for (int i = 0; i < ProhibitPackagename.length; i++) {
				if (ProhibitPackagename[i].equals(packetname))
					return false;
			}
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BroadCastRcv.RemoveListen();
	}

	protected void OnDrop(View dragV, int dragid, final WinInfo dragwin, int dropx, int dropy) {

		final int CurrentPageId = vp.getCurrentItem();
		final MyRelFragment dragRel = (MyRelFragment) dragV;
		final MyRelFragment dropRel = (MyRelFragment) adapter.getMyViews().get(CurrentPageId);

		if ((Integer) dropRel.getTag() == HOMEPAGE) {
			dragRel.getchildView(dragid).setVisibility(View.VISIBLE);
		} else {

			int dropId = dropRel.DropToCellPoint(dropx, dropy, dragwin);

			if (dropId != MyRelFragment.INVALID_POSITION && dropId != MyRelFragment.NO_WININFO) {

				final WinInfo dropwin = dropRel.getDropWinInfo();
				dragRel.switchInfo(dropwin, dragwin);

				dragRel.onDrop(dragRel.getchildView(dragid), dragwin);
				dropRel.onDrop(dropRel.getchildView(dropId), dropwin);

				mhandler.post(new Runnable() {

					@Override
					public void run() {
						mysqlite.UpdateWinData(dragwin);
						mysqlite.UpdateWinData(dropwin);

					}
				});

			} else {
				if (dropId == MyRelFragment.INVALID_POSITION) {
					dragRel.getchildView(dragid).setVisibility(View.VISIBLE);
				} else {

					dragRel.removeChild(dragid);
					dragwin.pageId = (Integer) dropRel.getTag();
					dropRel.AddchildtoIdle(dragwin, dropRel.getDropID());

					if (dragRel.getChildCount() == 0) {
						RemoveFremnt((Integer) dragRel.getTag(), (Integer) dropRel.getTag());
					}

					mhandler.post(new Runnable() {
						@Override
						public void run() {
							mysqlite.UpdateWinData(dragwin); // 修改落点坐标
						}
					});

				}
			}
		}
	}

	public void onClick(View v, int dropx, int dropy) {

		final int pager = vp.getCurrentItem();
		MyRelFragment vFragment = (MyRelFragment) adapter.getMyViews().get(pager);
		WinInfo winInfo = vFragment.getWinInfo(dropx, dropy);
		if (winInfo != null) {
			if (pager == HOMEPAGE && (winInfo.item == 0 || winInfo.item == 2 || winInfo.item == 5)) {
				if (winInfo.item == 0) {
					packtools.LanchActvieParam(winInfo.Packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (winInfo.item == 2) {
					packtools.LanchActvie(winInfo.Packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (winInfo.item == 5) {
					packtools.LanchActvie(winInfo.Packetname, "com.hftcom.ui.LoadingActivity");
				}
				return;
			}
			packtools.LanchAPk(winInfo.Packetname);
		}
	}

	public boolean onLongClick(int dropx, int dropy) {

		MyRelFragment vFragment = (MyRelFragment) adapter.getMyViews().get(vp.getCurrentItem());
		if ((Integer) vFragment.getTag() != HOMEPAGE) {
			int index = vFragment.pointToPosition(dropx, dropy, false);
			if (index == MyRelFragment.INVALID_POSITION)
				return false;
			final View child = vFragment.getchildView(index);
			child.setDrawingCacheEnabled(true);
			final Bitmap dragBitmap = Bitmap.createBitmap(child.getDrawingCache());
			final int x = (int) child.getLeft();
			final int y = (int) child.getTop();
			child.destroyDrawingCache();
			child.setVisibility(View.GONE);
			final int srcId = (Integer) child.getTag();
			dragController.startDrag(vFragment, srcId, vFragment.getWinInfo(srcId), dragBitmap, x, y);
		} else {
			printWinInfo();
		}
		return true;
	}

	protected int getCurrentPage() {
		return vp.getCurrentItem();
	}

	protected boolean SwitchPage(boolean isgotoleft) {

		final int curPage = vp.getCurrentItem();
		final int nextPage;

		if (isgotoleft) {
			nextPage = curPage == 0 ? 0 : (curPage - 1);
			vp.setCurrentItem(nextPage);
		} else {
			nextPage = curPage == vp.getChildCount() ? vp.getChildCount() : (curPage + 1);
		}
		vp.setCurrentItem(nextPage);
		return true;
	}

	protected DragController GetDragController() {

		return dragController;
	}

	public void create_bottombar(Object m_r, int IsSelect) {

		RelativeLayout m_rl = (RelativeLayout) m_r;
		RelativeLayout.LayoutParams margin = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				30);
		margin.topMargin = bottom_d;
		margin.leftMargin = 360 - 20 * Frame_size;

		if (m_rl.getChildCount() != 0) {
			m_rl.removeAllViews();
		}
		if (imageButton == null)
			imageButton = new ArrayList<ImageButton>();
		else {
			imageButton.clear();
		}

		for (int i = 0; i < Frame_size; i++) {
			imageButton.add(new ImageButton(this));
			imageButton.get(i).setPadding(40 * i, 0, 40 * Frame_size - 40 * (i + 1), 0);
			imageButton.get(i).setBackgroundColor(0x00000000);
			if (i == IsSelect) {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.fouces, getTheme()));
			} else {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.unfouce, getTheme()));
			}

			m_rl.addView(imageButton.get(i), margin);
		}
	}
	protected  PackagesTools getPacktools(){
		
		return packtools;
	}

	private void ResertButtonImg(int isSel) {

		for (int i = 0; i < Frame_size; i++) {
			if (i == isSel) {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.fouces, getTheme()));
			} else {
				imageButton.get(i).setImageDrawable(getResources().getDrawable(R.drawable.unfouce, getTheme()));
			}
		}
	}

}
