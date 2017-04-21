package com.yf.defender;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.yf.Interfaces.FreedomCallback;
import com.yf.Interfaces.SevXmlInterface;
import com.yf.define.PublicDefine;
import com.yf.define.YFShopAppInfo;
import com.yf.download.DownloadApk;
import com.yf.download.ImageDownload;
import com.yf.sevice.ControlInstallSev;
import com.yf.sevice.ControlInstallSev.LocalBinder;
import com.yf.sevice.SeviceForYFComm;
import com.yf.sevice.SeviceForYFComm.MyServiceImpl;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity {

	private long mExitTime;
	public static Object message;
	private ListView mListView;
	private final static String TAG = "MainActivity";
	private MyHandler handler = null;
	private MainListViewAdapter adapter = null;
	private ControlInstallSev cts;
	private boolean isfirststart = true;
	private List<YFShopAppInfo> appInfos = new ArrayList<YFShopAppInfo>();
	private String errmsg11;
	private String[] permission_i = { "磁条刷卡权限", "非接触刷卡权限", "密码键盘权限", "二维码扫描权限", "条码扫描权限", "图像扫描权限", "云闪付权限", "打印机权限",
			"签字版权限" };
	private static final String WIFI_SETTINGS = "com.android.settings.wifi.WifiSettings";
	private static final String ACTION_APPLICATION_SETTINGS = "com.android.settings.applications.ManageApplications";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.listsow);
		mListView.setOnItemClickListener(mAppClickListener);
		handler = new MyHandler(this);
		startService(new Intent(MainActivity.this, ControlInstallSev.class));// 启动服务
		handler.obtainMessage(PublicDefine.START_THREAD).sendToTarget();
		Log.i(TAG, "yf_defender.MainActivity.onCreate");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (cts != null) {
			cts.Downloadxml(sxml, 1);
		}
	}

	public class ImageCallBack implements FreedomCallback {

		public synchronized void imageLoaded(final Bitmap imageDrawable, final Object tag) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ListItemView listItemView;
					listItemView = (ListItemView) tag;
					if (listItemView != null) {
						listItemView.imageView.setImageBitmap(imageDrawable);
					}
				}
			});
		}
	}

	private OnItemClickListener mAppClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			String str = "";
			try {
				byte[] permisson = appInfos.get(arg2).permisson.getBytes("GB2312");

				for (int i = 0; i < permisson.length; i++) {
					if (permisson[i] == 49 && permission_i.length >= i) {
						if (str.length() > 0)
							str += "\r\n";
						str = str + "● " + permission_i[i];
					}
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			CustomDialog dialog;
			CustomDialog.Builder customBuilder = new CustomDialog.Builder(MainActivity.this);
			customBuilder.setTitle("权限提示").setMessage(str).setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog = customBuilder.create(R.drawable.prompt);
			dialog.setCancelable(false);
			dialog.show();
		}
	};

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		Intent intent;
		
		switch (item.getItemId()) {

		case R.id.action_settings:

			runSettingsActivity(ACTION_APPLICATION_SETTINGS, "应用商店卸载");
			break;

		case R.id.action_version:

			intent = new Intent(this, yfver.class);// 版本信息
			startActivity(intent);
			break;
			
		case R.id.action_test:

            cts.test();
			break;

		default:

			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	public class MainListViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ListItemView listItemView;
			if (convertView == null || appInfos.get(position).tag == null) {

				convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.items, null);

				listItemView = new ListItemView();
				listItemView.imageView = (ImageView) convertView.findViewById(R.id.image);
				listItemView.textView = (TextView) convertView.findViewById(R.id.title);
				listItemView.btnInstall = (RoundProgressBar) convertView.findViewById(R.id.btnAnzhuang);
				convertView.setTag(listItemView);
				listItemView.btnInstall.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						int i = (Integer) v.getTag();
						if (!appInfos.get(i).isInsatll) {
							if (appInfos.get(i).isnaticeApp && (appInfos.get(i).isupdate == false)) {
								Intent LaunchIntent = getPackageManager()
										.getLaunchIntentForPackage(appInfos.get(i).packname);
								startActivity(LaunchIntent);
							} else {
								if (!appInfos.get(i).IsWaitingDownload) {
									appInfos.get(i).IsWaitingDownload = true;
									StartDownloadApk(i);
								} else {
									appInfos.get(i).IsWaitingDownload = false;

									handler.obtainMessage(PublicDefine.STOPDOWNLOAD, i, 0).sendToTarget();
								}
							}
						} else {
							Log.i(TAG, "正在安装...");
						}
					}
				});

			} else {
				listItemView = (ListItemView) convertView.getTag();
			}
			appInfos.get(position).tag = listItemView;
			listItemView.btnInstall.setTag(position);
			listItemView.textView.setText(appInfos.get(position).title + "\nversion:" + appInfos.get(position).version
					+ "\nsize:" + appInfos.get(position).fileszie + "M");

			if (appInfos.get(position).isnaticeApp) {

				if (appInfos.get(position).isInsatll) {
					listItemView.btnInstall.setProgress(0, true, "安装中");
				} else if (appInfos.get(position).isupdate) {
					listItemView.btnInstall.setProgress(0, true, "更新 ");
				} else {
					listItemView.btnInstall.setProgress(0, true, "开启");
				}
			} else {
				if (appInfos.get(position).isInsatll) {
					listItemView.btnInstall.setProgress(0, true, "安装中");
				} else if (appInfos.get(position).IsWaitingDownload) {

				} else {
					listItemView.btnInstall.setProgress(0, true, "安装");
				}
			}
			if (appInfos.get(position).shopicon != null) {
				listItemView.imageView.setImageBitmap(appInfos.get(position).shopicon);
			} else {
				listItemView.imageView.setImageResource(R.drawable.moren);
			}
			return convertView;
		}
	}

	public void StartDownloadApk(int i) {

		if (!PublicDefine.isDownloading) {
			try {
				PublicDefine.isDownloading = true;

				if (PublicDefine.SubDir != null) {
					((ListItemView) appInfos.get(i).tag).btnInstall.stop();
					new DownloadApk(this, appInfos.get(i).filemd5,
							new URL(PublicDefine.SubDir + appInfos.get(i).apkdir), PublicDefine.path, i,
							appInfos.get(i), handler).start();
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else {
			handler.obtainMessage(PublicDefine.DOWNLOADWAITING, i, 0).sendToTarget();
		}
	}

	public void ScanDownloadAPK() {

		for (int i = 0; i < appInfos.size(); i++) {
			if (appInfos.get(i).IsWaitingDownload) {
				StartDownloadApk(i);
			}
		}
	}

	private static class MyHandler extends Handler {

		private ListItemView listItemView;
		private MainActivity MainActivity;

		public MyHandler(MainActivity main) {
			this.MainActivity = main;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case PublicDefine.START_THREAD:
				Intent intent = new Intent(MainActivity, ControlInstallSev.class);
				MainActivity.bindService(intent, MainActivity.sc, BIND_AUTO_CREATE);
				Log.i(TAG, "bindService ControlInstallSev");
				break;

			case PublicDefine.MSG_NOT_FOUNDFILE:

				MainActivity.RepeatDownload("未找到下载文件..");
				break;

			case PublicDefine.MSG_CONNECT_HTTP_ERROR:

				MainActivity.RepeatDownload("连接错误...");
				break;

			case PublicDefine.MSG_DOWNLOAD_ERROR:
				MainActivity.RepeatDownload("下载失败...");
				break;
			case PublicDefine.DOWNLOADWAITING:

				listItemView = (ListItemView) MainActivity.appInfos.get(msg.arg1).tag;
				if (listItemView != null) {
					listItemView.btnInstall.WaitingDownload();
				}
				break;
			case PublicDefine.SHOWINSTALLINFO:

				if (msg.arg2 == 1) {
					MainActivity.InstallApkSuccUpdateAdapter(msg.arg1, true);
				} else {
					MainActivity.InstallApkSuccUpdateAdapter(msg.arg1, false);
				}
				Toast.makeText(MainActivity, MainActivity.appInfos.get(msg.arg1).title + msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				break;

			case PublicDefine.STOPDOWNLOAD:

				listItemView = (ListItemView) MainActivity.appInfos.get(msg.arg1).tag;
				if (listItemView != null) {
					listItemView.btnInstall.stop();
				}
				break;
			case PublicDefine.SCANNEADDOWNLOAD:
				MainActivity.ScanDownloadAPK();
				break;

			case PublicDefine.THREAD_FINISHED:

				listItemView = (ListItemView) MainActivity.appInfos.get(msg.arg1).tag;
				MainActivity.appInfos.get(msg.arg1).isInsatll = true;
				if (listItemView != null) {
					listItemView.btnInstall.setProgress(0, true, "安装中");
				}
				InstallAppThread installAppThread = new InstallAppThread();
				installAppThread.SetDirAndPackname(msg.obj.toString(), msg.arg1,
						MainActivity.appInfos.get(msg.arg1).packname, MainActivity, this);
				new Thread(installAppThread).start();

				break;

			case PublicDefine.MSG_DOWNLOAD_PROGRESS:

				int rate = (int) Math.rint(((msg.arg2 * 1.0) / (Long) msg.obj) * 100);

				listItemView = (ListItemView) MainActivity.appInfos.get(msg.arg1).tag;
				if (listItemView != null && MainActivity.appInfos.get(msg.arg1).IsWaitingDownload) {

					listItemView.btnInstall.setProgress(rate, false, "");
				}
				break;

			case PublicDefine.MSG_MD5_FAILED:

				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity);
				dialog.setTitle("操作提示:");
				dialog.setMessage("APK MD5验证失败...");
				dialog.setNegativeButton("确定", null);
				dialog.show();
				listItemView = (ListItemView) MainActivity.appInfos.get(msg.arg1).tag;
				if (listItemView != null) {
					listItemView.btnInstall.setProgress(0, true, "安装");
				}
				break;

			default:
				break;
			}
		}
	}

	public void RepeatDownloadSetNetWork(String str) {

		CustomDialog dialog;
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		customBuilder.setTitle("错误提示").setMessage(str).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				MainActivity.this.finish();
			}
		}).setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// runbatteryInfo();
				runSettingsActivity(WIFI_SETTINGS, "应用商店设置网络");
			}
		});
		dialog = customBuilder.create(R.drawable.error);
		dialog.setCancelable(false);
		dialog.show();
	}

	private void runbatteryInfo() {
		Intent intent = new Intent(); // (className);
		intent.setPackage("com.android.settings");
		intent.setAction("android.settings.BATTERY_SAVER_SETTINGS");
		// intent.putExtra("", value);
		// intent.setClassName("com.android.settings","Settings$BatterySaverSettingsActivity");
		startActivity(intent);
	}

	@SuppressWarnings("unused")
	public void runSettingsActivity(String fragment, String title) {

		final String EXTRA_SHOW_FRAGMENT_AS_SUBSETTING = ":settings:show_fragment_as_subsetting";
		final String EXTRA_TITLE_COLOR = "settings:title_color_yfcomm";
		final String EXTRA_SHOW_FRAGMENT = ":settings:show_fragment";
		final String EXTRA_SHOW_FRAGMENT_TITLE = ":settings:show_fragment_title";
		Intent intent = new Intent(); // (className);
		intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
		intent.putExtra(EXTRA_SHOW_FRAGMENT, fragment);
		intent.putExtra(EXTRA_SHOW_FRAGMENT_TITLE, title);
		intent.putExtra(EXTRA_TITLE_COLOR, getResources().getColor(R.color.darkblue));
		startActivity(intent);

	}

	public void RepeatDownload(String str) {

		CustomDialog dialog;
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		customBuilder.setTitle("错误提示").setMessage(str).setPositiveButton("请重试", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (cts != null)
					cts.Downloadxml(sxml, 1);
			}
		});
		dialog = customBuilder.create(R.drawable.error);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void Resetter(String str) {

		CustomDialog dialog;
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		customBuilder.setTitle("错误提示").setMessage(str).setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent intent = new Intent("android.settings.BACKUP_AND_RESET_SETTINGS");
				startActivity(intent);
				finish();
			}
		});
		dialog = customBuilder.create(R.drawable.error);
		dialog.setCancelable(false);
		dialog.show();
	}

	public String InstallApkSuccUpdateAdapter(int i, boolean retcode) {

		if (appInfos != null) {
			appInfos.get(i).isnaticeApp = retcode;
			appInfos.get(i).isInsatll = false;
			DeleteDownApk(appInfos.get(i));
			ListItemView listItemView = (ListItemView) appInfos.get(i).tag;

			if (listItemView != null && retcode) {
				appInfos.get(i).isupdate = false;
				listItemView.btnInstall.setProgress(0, true, "开启");
			} else {
				if (appInfos.get(i).isupdate) {
					listItemView.btnInstall.setProgress(0, true, "更新");
				} else {
					listItemView.btnInstall.setProgress(0, true, "安装");
				}
			}
			return appInfos.get(i).title;
		}
		return null;
	}

	public void DeleteDownApk(YFShopAppInfo app) {

		File file = new File(PublicDefine.path + app.apkdir.substring(app.apkdir.lastIndexOf("/") + 1));
		if (file.exists()) {
			file.delete();
		}

	}

	public void loadview() {

		if (isfirststart) {
			adapter = new MainListViewAdapter();
			mListView.setAdapter(adapter);
			isfirststart = false;
			Log.i(TAG, "firststart setAdapter!!");
			new ImageDownload(this, appInfos).loadDrawable(new ImageCallBack());
		} else {
			if (adapter != null)
				adapter.notifyDataSetChanged();
			new ImageDownload(this, appInfos).loadDrawable(new ImageCallBack());
		}
	}

	private SevXmlInterface sxml = new SevXmlInterface() {

		@Override
		public void downloadsuc(List<YFShopAppInfo> apps, boolean isupdate) {

			if (apps != null) {
				Log.i(TAG, "Download xml sucess,Begin load view.");
				if (appInfos.isEmpty() || isupdate) {
					appInfos = apps;
					loadview();
				}
			} else {
				Log.i(TAG, "Download xml failed no apps");
			}

		}

		@Override
		public void downloadfailed(int err, String errmsg) {

			errmsg11 = errmsg;

			switch (err) {

			case PublicDefine.MSG_NOT_FOUNDFILE:
			case PublicDefine.MSG_PACKAGE_ERROR:
			case PublicDefine.MSG_CONNECT_HTTP_ERROR:
			case PublicDefine.MSG_DOWNLOAD_ERROR:
			case PublicDefine.MSG_GETURLADDREERR:

				if (handler != null) {
					handler.post(new Runnable() {
						public void run() {
							RepeatDownloadSetNetWork(errmsg11);
						}
					});
				}
				break;

			case PublicDefine.WRITE_SNERR:

				if (handler != null) {
					handler.post(new Runnable() {
						public void run() {
							Resetter(errmsg11);
						}
					});
				}
				break;

			default:
				if (handler != null) {
					handler.post(new Runnable() {
						public void run() {
							RepeatDownload(errmsg11);
						}
					});
				}
				break;
			}
		}

		@Override
		public void updateStoreUi() {

			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}

		@Override
		public void deletepacket(String packname) {

			for (int i = 0; i < appInfos.size(); i++) {
				if (appInfos.get(i).packname.equals(packname)) {
					appInfos.get(i).isnaticeApp = false;
				}
			}
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
		}
	};

	public static class MyInstalledReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) { // 开机事件
				Intent newIntent = new Intent(context, ControlInstallSev.class);
				context.startService(newIntent);// 开机启动服务
			}
		}
	}

	private ServiceConnection sc = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			LocalBinder binder = (LocalBinder) service;// 获得自定义的LocalBinder对象
			cts = binder.getService();// 获得CurrentTimeService对象
			cts.Downloadxml(sxml, 1); // Services download Request
		}
	};

	public static class ListItemView {
		ImageView imageView;
		TextView textView;
		RoundProgressBar btnInstall;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (sc != null)
			unbindService(sc);// 解绑定
	}

}
