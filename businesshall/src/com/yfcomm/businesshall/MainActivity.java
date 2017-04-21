package com.yfcomm.businesshall;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import com.yfcomm.db.MySQLiteHelper;
import com.yfcomm.homekey.HomeWatcher;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.public_define.CustomInfo;
import com.yfcomm.public_define.OperterData;
import com.yfcomm.public_define.public_define;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	public static Handler myHandler;
	public final static int MSG_CLOSEKEYBOARD = 1;
	public final static String TAG = "MainActivity";
	public final static String OPERTERS_SETTINGS = "SEL_OPERTINGS";
	private final int SING_CHOICE_DIALOG = 1;
	private final static int ITEM_DIALOG = 2;
	private FragmentManager fm;
	private static PagerAdapter[] mPagerAdapter = new PagerAdapter[4];
	private LinearLayout[] BotoomLayout = new LinearLayout[4];
	private int[] BotoomID = { R.id.Recharge_server, R.id.opencard_server, R.id.businesschange_server,
			R.id.publicutilityfee_server };
	public static OperterData[] operdata = new OperterData[3]; // 运营商套餐和号码池信息
	public static int operter_sel;
	private MySQLiteHelper db = null;
	public static List<CustomInfo> customifo = null;// 用户信息
	private static MyNumPoolGridAdapter numpooladpter = null;
	//private long mExitTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sever);

		myHandler = new MyHandler(this);
		fm = getSupportFragmentManager();
		mSectionsPagerAdapter = new SectionsPagerAdapter(fm, this);
		mViewPager = (ViewPager) findViewById(R.id.pager_server);
		for (int i = 0; i < mPagerAdapter.length; i++)
			mPagerAdapter[i] = new PagerAdapter(fm, this, i);
		initlisten();
		readconifg_init();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			forceStopPackage("com.yifengcom.yfpos", this);
			Log.e(TAG, "com.yifengcom.yfpos succ");
		} catch (Exception e) {
			Log.e(TAG, "error:" + e.getMessage());
		}
	
	}

	private void forceStopPackage(String pkgName, Context context) throws Exception {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}

	/****** 测试数据 **********/
	public void test_InsertUsers() {

		db.DelAllData();
		CustomInfo user = new CustomInfo();

		user.planname = "联通4G全国套餐106元";
		user.phonenum = "18581051066";

		user.price = "106";
		user.acount = 0;
		user.attribute_operters = 0;
		user.name = "苏普";
		user.useraddress = "重庆市金果园26-5";
		user.certno = "321084198812162312";

		db.InsertAppData(user);
		user.planname = "乐享4G套餐99元";
		user.phonenum = "17782393838";

		user.price = "106";
		user.acount = 0;
		user.attribute_operters = 1;
		user.name = "柳辰飞";
		user.useraddress = "重庆市金果园26-5";
		user.certno = "321084198812162313";
		db.InsertAppData(user);

		user.planname = "移动4G飞享套餐18元";
		user.phonenum = "13548541507";
		user.price = "106";
		user.acount = 0;
		user.attribute_operters = 2;
		user.name = "冷文卿";
		user.useraddress = "重庆市金果园26-5";
		user.certno = "321084198812162314";

		db.InsertAppData(user);

	}

	public void readconifg_init() {
		String[] xmlname = { "unicom.xml", "telcomm.xml", "mobile.xml" };

		db = new MySQLiteHelper(this, public_define.yfdb, null, public_define.ver);

		SharedPreferences settings = getSharedPreferences(OPERTERS_SETTINGS, 0);

		operter_sel = settings.getInt(OPERTERS_SETTINGS, 0);// 默认选择联通的用户

		test_InsertUsers();// 增加一个测试用户

		customifo = db.GetUserData(operter_sel); // 读取存入的用户信息

		for (int i = 0; i < 3; i++) {
			try {
				InputStream is = getAssets().open(xmlname[i]);
				operdata[i] = AppXmltran.Read(is); // 联通套餐
				is.close();

			} catch (IOException e) {

				Log.e(TAG, operdata[i] + " 文件加载失败");
			}
		}
	}

	public void savechoiceOperter(int whitch) {

		SharedPreferences settings = getSharedPreferences(OPERTERS_SETTINGS, 0);
		Editor editor = settings.edit();
		editor.putInt(OPERTERS_SETTINGS, whitch);
		editor.commit();
		operter_sel = whitch;
		customifo = db.GetUserData(operter_sel);
		if (numpooladpter != null) {
			numpooladpter.setNUmpoolList(operdata[operter_sel].numberpool);
		}

	}

	public void initlisten() {

		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				setselect(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		for (int i = 0; i < BotoomLayout.length; i++) {
			BotoomLayout[i] = (LinearLayout) findViewById(BotoomID[i]);
			BotoomLayout[i].setId(i);
			BotoomLayout[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(v.getId());
				}
			});
		}
		mViewPager.setCurrentItem(0);
		BotoomLayout[0].setBackgroundColor(getResources().getColor(R.color.title_color));
	}

	public void setselect(int index) {

		for (int i = 0; i < BotoomLayout.length; i++) {
			if (index == i) {
				BotoomLayout[i].setBackgroundColor(getResources().getColor(R.color.title_color));
			} else {
				BotoomLayout[i].setBackgroundColor(getResources().getColor(R.color.btn_text_color));
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			/*
			 * Intent intent = new Intent();
			 * intent.setAction("android.intent.action.seloperators");
			 * startActivity(intent);
			 */
			showDialog(SING_CHOICE_DIALOG);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
   /*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {

				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	protected Dialog onCreateDialog(int id) {

		Dialog dialog = null;
		Builder builder;

		switch (id) {

		case SING_CHOICE_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.prompt);
			builder.setTitle("运营商选择");
			final ChoiceOnClickListener choiceListener = new ChoiceOnClickListener();
			builder.setSingleChoiceItems(R.array.hobby, 0, choiceListener);
			DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int which) {

					int choiceWhich = choiceListener.getWhich();
					String hobbyStr = getResources().getStringArray(R.array.hobby)[choiceWhich];
					savechoiceOperter(choiceWhich);
					Log.e("1111", "choiceWhich:" + operter_sel + " " + hobbyStr);
				}
			};
			builder.setPositiveButton("确定", btnListener);
			dialog = builder.create();
			dialog.show();
			break;

		case ITEM_DIALOG:

			builder = new AlertDialog.Builder(this);
			builder.setIcon(R.drawable.prompt);
			builder.setTitle("运营商选择");
			builder.setMessage("134444");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}

			});
			dialog = builder.create();
			/*
			 * Window window = dialog.getWindow();
			 * window.setGravity(Gravity.BOTTOM); dialog.show();
			 */
			break;

		}
		return dialog;
	}

	private class ChoiceOnClickListener implements DialogInterface.OnClickListener {

		private int which = 0;

		@Override
		public void onClick(DialogInterface dialogInterface, int which) {
			this.which = which;
		}

		public int getWhich() {
			return which;
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		Context context;

		public SectionsPagerAdapter(FragmentManager fm, Context context) {
			super(fm);
			this.context = context;
		}

		@Override
		public Fragment getItem(int position) {
			return PlaceholderFragment.newInstance(position + 1, context);
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	public void closekeyboard() {
		View view = getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	private static class MyHandler extends Handler {

		private MainActivity active;

		public MyHandler(MainActivity main) {
			this.active = main;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case MSG_CLOSEKEYBOARD:
				active.closekeyboard();
				break;
			case ITEM_DIALOG:

				active.showDialog(ITEM_DIALOG);
				break;

			default:
				break;
			}
		}
	}

	public static class PlaceholderFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";
		private int i;
		private ViewPager[] childViewPager = new ViewPager[4];
		private int[] ViewPagerId = { R.id.viewpage, 0, R.id.bviewpage, 0 };
		private TextView[][] textViews = new TextView[4][3];// 保存 VIEWPAGE 标签ID
		private int[] TEXTID = { R.id.TextView0, R.id.TextView1, R.id.TextView2 };
		private int[] LoadID = { R.layout.recharge, R.layout.choicenum, R.layout.businesschange, R.layout.publicpay };
		private View rootView;
		private Context context;
		private final static int SEL_PHONERECHARGE = 0;
		private final static int SEL_CHOICENUM = 1;
		private final static int SEL_BUSINESSCHARGE = 2;
		private final static int SEL_PUBLICPAY = 3;
		private GridView mYGridView;
		private OperterData.numberpool pool;

		public static PlaceholderFragment newInstance(int sectionNumber, Context context) {

			PlaceholderFragment fragment = new PlaceholderFragment(sectionNumber, context);
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);

			return fragment;
		}

		public PlaceholderFragment(int sectionNumber, Context context) {
			this.i = sectionNumber - 1;
			this.context = context;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (null != rootView) {
				ViewGroup parent = (ViewGroup) rootView.getParent();
				if (null != parent) {
					parent.removeView(rootView);
				}
			} else {

				rootView = inflater.inflate(LoadID[i], container, false);
				switch (i) {
				case SEL_CHOICENUM:
					mYGridView = (GridView) rootView.findViewById(R.id.gridview);
					numpooladpter = new MyNumPoolGridAdapter(context, operdata[operter_sel].numberpool);
					mYGridView.setAdapter(numpooladpter);
					mYGridView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							MyNumPoolGridAdapter adpter = (MyNumPoolGridAdapter) mYGridView.getAdapter();
							if (adpter.setItemsel(position)) {
								pool = adpter.getItem(position);
								CustomDialog dialog;
								CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
								customBuilder
										.setMessage("已选号码: " + pool.phonenum + "\r\n" + "预存话费: " + pool.prcie + ".0 元")
										.setPositiveButton("下一步", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
												Intent intent = new Intent();
												intent.setAction("android.intent.action.bussinesshallopencard");
												Bundle arguments = new Bundle();
												arguments.putString(public_define.phonenum, pool.phonenum);
												arguments.putString(public_define.price, pool.prcie);
												intent.putExtra(public_define.SelphonenumInfo, arguments);
												startActivity(intent);
											}
										});
								dialog = customBuilder.create(0);
								Window window = dialog.getWindow();
								window.setGravity(Gravity.BOTTOM);
								dialog.show();
							}
						}
					});

					break;
				case SEL_PHONERECHARGE:
				case SEL_BUSINESSCHARGE:
					setmViewPager(rootView);
					break;
				case SEL_PUBLICPAY:
					SetPublicListen(rootView);
					break;

				default:
					break;
				}
			}
			return rootView;
		}

		public void SetPublicListen(View v) {

			final int[] publicpayid = { R.id.publicpay_electric, R.id.publicpay_water, R.id.publicpay_gas };
			for (int i = 0; i < publicpayid.length; i++)
				((ImageView) v.findViewById(publicpayid[i])).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CustomDialog dialog;
						CustomDialog.Builder customBuilder = new CustomDialog.Builder(context);
						customBuilder.setTitle("操作提示").setMessage("●" + "暂时未开通\r\n" + "●" + "需要根据当地的实际接口调试")
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();

									}
								});
						dialog = customBuilder.create(R.drawable.prompt);
						dialog.setCancelable(false);
						dialog.show();
					}
				});
		}

		public void setmViewPager(View v) {

			childViewPager[i] = (ViewPager) v.findViewById(ViewPagerId[i]);
			childViewPager[i].setAdapter(mPagerAdapter[i]);
			textViews[i] = find_v_byid(v, TEXTID);

			childViewPager[i].setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
				@Override
				public void onPageSelected(int arg0) {
					setRechargeselect(arg0, textViews[i]);
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});

		}

		public TextView[] find_v_byid(View v, int[] controlID) {

			TextView[] textViews1 = new TextView[controlID.length];
			for (int i = 0; i < controlID.length; i++) {
				textViews1[i] = (TextView) v.findViewById(controlID[i]);
			}
			return textViews1;
		}

		public void setRechargeselect(int index, TextView[] stext) {

			for (int i = 0; i < stext.length; i++) {
				if (index == i) {
					stext[i].setBackground(getResources().getDrawable(R.drawable.settext_bg));
				} else {
					stext[i].setBackground(getResources().getDrawable(R.drawable.setbar_bg));
				}
			}
		}

	}

}
