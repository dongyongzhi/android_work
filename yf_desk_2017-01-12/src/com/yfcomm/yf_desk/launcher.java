package com.yfcomm.yf_desk;

import java.util.ArrayList;
import java.util.List;

import com.yfcomm.public_define.PackagesTools;
import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.public_define;
import com.yfcomm.public_define.yf_SharedPreferences;
import com.yfcomm.yf_desk.PagedView.PageSwitchListener;

import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.YFComm;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public final class launcher extends FragmentActivity {

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
	private final List<WinInfo> apps = new ArrayList<WinInfo>();
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
	private int currentPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FrameLayout worksapce;
		
 		worksapce = (FrameLayout) findViewById(R.id.workspace);
		
		//bottombar = (RelativeLayout) findViewById(R.id.paged_view_indicator);
		mhandler = new Handler();
		dragController = new DragController(this, mhandler);
		mInflater = getLayoutInflater();
		packtools = new PackagesTools(this);

		initView();

	
		vp =new MyPageView(this,mhandler);		
		LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		for (RelativeLayout child : frament) {
			child.setVisibility(View.VISIBLE);
			vp.addView(child,params);
		}
		worksapce.addView(vp);
		setViewPagerAdpater();
		BroadCastRcv = new PackageBroadcastReceiver(this, mhandler);
		BroadCastRcv.StartListen();
	}

	private void initView() {
		setupViews();
	
	}

	private void setupViews() {

		int pageid = 0, offset, page_num, rightnum;

		// left pager
		offset = createLeftsideView(pageid);

		pageid = offset == 0 ? 0 : 1;

		if (AppXmltran.Read(getResources().getXml(R.xml.home_hft), apps)) {
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
		Frame_size = page_num + pageid;

		//create_bottombar(bottombar, HOMEPAGE);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		vp.setCurrentPage(HOMEPAGE);
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
		for (int i = start; i < end; i++) {
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

		vp.setPageSwitchListener(new PageSwitchListener() {

			@Override
			public void onPageSwitch(View newPage, int newPageIndex) {
				currentPage = newPageIndex;
			}

		});
		vp.setCurrentPage(HOMEPAGE);
	}

	protected int FindCellItem(int totalpage, WinInfo winInfo) {

		int pager = INVAILD_PAGER;
		int item = 0;

		for (int i = HOMEPAGE + 1; i <= totalpage; i++) {
			MyRelFragment fragment = (MyRelFragment) adapter.getMyViews().get(i);
			if (MyRelFragment.INVALID_POSITION != (item = fragment.getIdleItem())) {
				pager = i;
				fragment.AddchildtoIdle(winInfo, item);
				apps.add(winInfo);
				break;
			}
		}
		return pager;
	}

	protected void UpdateDBData(WinInfo winInfo) {

		if (INVAILD_PAGER == FindCellItem(vp.getChildCount(), winInfo)) {
			// Need to allocate pager;

		}
	}

	private void printWinInfo() {

		for (WinInfo win : apps) {
			Log.e(TAG, "ID=" + win.id + " title=" + win.title + " package=" + win.Packetname + " pagid=" + win.pageId);
		}
	}

	protected void UnInstallAPk(String packectname) {
		final int apps_index = isExistPackageName(packectname);
		if (apps_index != INVAILD_PACKAGE) {
			MyRelFragment fragment = (MyRelFragment) adapter.getMyViews().get(apps.get(apps_index).pageId);
			fragment.removeChild(apps.get(apps_index).id);
			apps.remove(apps_index);
			if (fragment.getChildCount() == 0) {
				RemoveFremnt(apps.get(apps_index).pageId);
			}
		}
	}

	protected void RemoveFremnt(int pageId) {

		adapter.getMyViews().remove(pageId);
		adapter.notifyDataSetChanged();
		Log.e(TAG, "ViewPagerNumber=" + vp.getChildCount());

	}

	protected void bindAppWorkSpace(String packageName) {

		if (INVAILD_PACKAGE == isExistPackageName(packageName) && CheckAppWhterLoading(packageName)) {
			UpdateDBData(packtools.GetPackageInfo(packageName));
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

	protected void OnDrop(View dragV, int dragid, WinInfo dragwin, int dropx, int dropy) {

		final int CurrentPageId = this.currentPage;
		final MyRelFragment dragRel = (MyRelFragment) dragV;
		final MyRelFragment dropRel = (MyRelFragment) adapter.getMyViews().get(CurrentPageId);

		if ((Integer) dropRel.getTag() == HOMEPAGE) {
			dragRel.getchildView(dragid).setVisibility(View.VISIBLE);
		} else {

			int dropId = dropRel.DropToCellPoint(dropx, dropy, dragwin);

			if (dropId != MyRelFragment.INVALID_POSITION && dropId != MyRelFragment.NO_WININFO) {

				WinInfo dropwin = dropRel.getDropWinInfo();
				dragRel.switchInfo(dropwin, dragwin);

				dragRel.onDrop(dragRel.getchildView(dragid), dragwin);
				dropRel.onDrop(dropRel.getchildView(dropId), dropwin);
				// vp.setCurrentItem(CurrentPageId);
			} else {
				if (dropId == MyRelFragment.INVALID_POSITION) {
					dragRel.getchildView(dragid).setVisibility(View.VISIBLE);
				} else {
					dragRel.removeChild(dragid);
					dragwin.pageId = (Integer) dropRel.getTag();
					dropRel.AddchildtoIdle(dragwin, dropRel.getDropID());
					// vp.setCurrentItem(CurrentPageId);
					if (dragRel.getChildCount() == 0) {
						RemoveFremnt((Integer) dragRel.getTag());
					}
				}
			}
		}
	}

	public void onClick(View v, int dropx, int dropy) {

		final int pager = this.currentPage;
		MyRelFragment vFragment = (MyRelFragment) adapter.getMyViews().get(pager);
		WinInfo winInfo = vFragment.getWinInfo(dropx, dropy);
		if (winInfo != null) {
			if (pager == HOMEPAGE && (winInfo.id == 0 || winInfo.id == 2 || winInfo.id == 5)) {
				if (winInfo.id == 0) {
					packtools.LanchActvieParam(winInfo.Packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (winInfo.id == 2) {
					packtools.LanchActvie(winInfo.Packetname, "com.hftcom.ui.mpos.PurchaseActivity");
				} else if (winInfo.id == 5) {
					packtools.LanchActvie(winInfo.Packetname, "com.hftcom.ui.LoadingActivity");
				}
				return;
			}
			packtools.LanchAPk(winInfo.Packetname);
		}
	}

	public boolean onLongClick(int dropx, int dropy) {

		MyRelFragment vFragment = (MyRelFragment) adapter.getMyViews().get(this.currentPage);
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
		return this.currentPage;
	}

	protected boolean SwitchPage(boolean isgotoleft) {

		final int curPage = this.currentPage;
		final int nextPage;

		if (isgotoleft) {
			nextPage = curPage == 0 ? 0 : (curPage - 1);
		} else {
			nextPage = curPage == vp.getChildCount() ? vp.getChildCount() : (curPage + 1);
		}
		// vp.setCurrentItem(nextPage);
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
