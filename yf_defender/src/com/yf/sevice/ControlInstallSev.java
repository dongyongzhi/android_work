package com.yf.sevice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yf.Interfaces.ControlSevInterface;
import com.yf.Interfaces.SevXmlInterface;
import com.yf.define.AppInfo;
import com.yf.define.PublicDefine;
import com.yf.define.YFShopAppInfo;
import com.yf.download.DownLoadFileThread;
import com.yf.download.DownLoadUrladd;

import com.yf.tools.AppXmltran;
import com.yf.tools.MD5;
import com.yifengcom.yfpos.service.DeviceModel;
import com.yifengcom.yfpos.service.IService;
import com.yifengcom.yfpos.service.listener.CallBackListener;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import android.util.YFComm;

/**
 * @name: ControlInstallSev
 * @Description: 应用安装和卸载、后台数据下载、文件保存读取
 * @version:ROCET.Tang VERSION
 */

@SuppressLint("HandlerLeak")
public class ControlInstallSev extends Service implements ControlSevInterface {

	private static MyHandler mhandler = null; // Message handler
	private SevXmlInterface sevxml; // Activity interface address

	private final static String TAG = "ControlInstallSev";
	private final static String s_xml = "yfstorebak.xml"; // 本地备份的XML文件
	private final static String init_xml = "yfstore_init.xml"; // 初始本地文件

	private List<YFShopAppInfo> store_apps = null; // 安装在本地的所有引用
	private AppXmltran xml = new AppXmltran();

	public IService iService;
	private Myconn myconn;
	public boolean isBind = false;
	public boolean IsSetSn = false;

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	public void init() {
		mhandler = new MyHandler(this);
		// mhandler.sendEmptyMessageDelayed(PublicDefine.MSG_WRITESNNUM, 5000);
	}

	public void bind() {
		final Intent intent = new Intent();
		intent.setAction("com.yifeng.aidl");
		intent.setPackage("com.yifengcom.yfpos");
		myconn = new Myconn();
		isBind = bindService(intent, myconn, Context.BIND_AUTO_CREATE);
		if (isBind) {
			Log.i(TAG, "Bind RemoteSevice Succ");
		} else {
			Log.i(TAG, "Bind RemoteSevice failed");
			if (sevxml != null)
				sevxml.downloadfailed(PublicDefine.MSG_YFPOSSEVERNOTEXIST,
						"YFPOSSever Bind　failed or Sever not existed");
		}
	}

	/** 解绑 */
	public void unBind() {
		if (myconn != null) {
			try {
				unbindService(myconn);
				isBind = false;
				myconn = null;
				IsSetSn = false;
			} catch (Exception e) {
				Log.i(TAG, "unBind=" + e.getMessage());
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new LocalBinder();// 通信的Binder
	}

	public class LocalBinder extends Binder {
		public ControlInstallSev getService() {
			return ControlInstallSev.this;// 返回当前服务的实例
		}
	}

	public List<AppInfo> LoadLoacalApp() {

		List<AppInfo> appInfos;

		appInfos = new ArrayList<AppInfo>();

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {

			PackageInfo packageInfo = packages.get(i);

			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {

				AppInfo appinfo = new AppInfo();

				appinfo.icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
				appinfo.Packetname = packageInfo.applicationInfo.packageName;
				appinfo.title = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
				appinfo.version = packageInfo.versionName;

				if (!packageInfo.applicationInfo.packageName.equals(PublicDefine.APP_PACKETNAME)
						&& !packageInfo.applicationInfo.packageName.equals(PublicDefine.DESK_PACKETNAME)) {
					appInfos.add(appinfo);
				}
			}
		}
		return appInfos;
	}

	@Override
	public int uninstallPacket(String packetname) {// 删除 packet

		// Log.i(TAG, "签名验证失败非法APP,卸载的PACKET:" + packetname);

		PackageManager pm = getPackageManager();
		IPackageDeleteObserver observer = (IPackageDeleteObserver) new MyPackageDeleteObserver();
		pm.deletePackage(packetname, observer, 0);

		return PublicDefine.yf_success;

	}

	public class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {

		@Override
		public void packageDeleted(String packageName, int returnCode) {

			if (returnCode == 1) {
				Log.i(TAG, packageName + "卸载成功");
			} else {
				Log.i(TAG, packageName + "卸载失败,返回码是:" + returnCode);
			}
		}
	}

	@Override
	public boolean CheckPacketSignature(String packetname, String certmd5) {// 验证签名

		String appMd5 = getSignature(packetname);
		if (appMd5 == null || certmd5 == null) {
			return false;
		}
		if (!appMd5.equals(certmd5)) {
			return false;
		}
		return true;
	}

	public String getSignature(String packetname) {// 得到包的签名

		PackageManager pm = getPackageManager();
		List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
		Iterator<PackageInfo> it = apps.iterator();
		while (it.hasNext()) {
			PackageInfo info = it.next();
			if (info.packageName.equals(packetname)) {
				return MD5.getMD5(info.signatures[0].toByteArray());
			}
		}
		return null;
	}

	@Override
	public int Downloadxml(SevXmlInterface sevxml, int Isextend) {
		this.sevxml = sevxml;

		// test
		// mhandler.obtainMessage(PublicDefine.REMOTE_SEVERSUCC,
		// "1000000000000010").sendToTarget();

		if (!IsSetSn) {
			bind();
		} else {
			mhandler.obtainMessage(PublicDefine.REMOTE_SEVERSUCC, YFComm.snCode()).sendToTarget();
		}

		return PublicDefine.yf_success;
	}

	public void test() {

		if (!isBind) {
			bind();
		}else{
			try{
				
				iService.onGetDeviceInfo(mCallBack);
				Log.e(TAG, "onGetDeviceInfo");
				iService.getPsamInfo(mCallBack);
				
			}catch (RemoteException e) {
				Log.e(TAG, "onGetDeviceInfo error="+e.getMessage());
			}
		}

	}

	private class Myconn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iService = IService.Stub.asInterface(service);
			if (iService != null) {
				
				try {
					Log.e(TAG, "GetVersionfromRemote");
					iService.onGetDeviceInfo(mCallBack);
					iService.getPsamInfo(mCallBack);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iService = null;
			Log.e(TAG, "iService is null");
		}
	}

	private CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int errorCode, final String errorMessage) throws RemoteException {
			if (sevxml != null && store_apps == null) {
				sevxml.downloadfailed(errorCode, errorMessage); // 回调失败下载;
			}
			Log.e(TAG, "Error:onGetDeviceInfo ErrMsg=" + errorMessage);
			//unBind();

		}

		@Override
		public void onGetDeviceInfo(String customerNo, String termNo, String batchNo, boolean existsMainKey, String sn,
				String version) throws RemoteException {

			Log.i(TAG, "Remote Result: sn=" + sn + " ver=" + version);
			mhandler.obtainMessage(PublicDefine.REMOTE_SEVERSUCC, sn).sendToTarget();
			//unBind();
		}

		@Override
		public void onGetDeviceInfoSuccess(DeviceModel deviceModel) throws RemoteException {

			String sn = deviceModel.getSn();
			String ver = deviceModel.getTerVersion();
			Log.e(TAG, "Remote Result111: sn=" + sn + " ver=" + ver);
			try {
				if (YFComm.setSnCode(sn, ControlInstallSev.this))
					mhandler.obtainMessage(PublicDefine.REMOTE_SEVERSUCC, sn).sendToTarget();
				else
					mhandler.obtainMessage(PublicDefine.WRITE_SNERR).sendToTarget();
				YFComm.setCoCpuVersion(ver, ControlInstallSev.this);

			} catch (RuntimeException e) {
				if (YFComm.snCode().equals(sn)) {
					mhandler.obtainMessage(PublicDefine.REMOTE_SEVERSUCC, sn).sendToTarget();
				} else {
					mhandler.obtainMessage(PublicDefine.WRITE_SNERR).sendToTarget();
				}

			}
			//unBind();
		}
		
		@Override
		public void onReadPsamNo(final String num) {
			Log.e(TAG, "Remote onReadPsamNo=" + num);
		};
		
		
		

	};

	@Override
	public void CheckAllUserApplication() {

		List<AppInfo> app = LoadLoacalApp();// 装载本地应用,验证安装的应用是否合法

		if (!loadxmltoGlobalParms(PublicDefine.save_xml)) {// 加载当前目录下载文件。
			if (!loadxmltoGlobalParms(s_xml)) {// 加载目录备份数据
				InputStream is;
				try {
					is = getAssets().open(init_xml);
					store_apps = xml.Read(is);
					is.close();
				} catch (IOException e) {

					Log.i(TAG, "xmlFile read error:" + e.getMessage());
				}

			}
		}

		if (app != null) {
			for (AppInfo appi : app) {
				if (!NativeAppIsLegitimate(appi.Packetname)) {
					uninstallPacket(appi.Packetname);
					mhandler.obtainMessage(PublicDefine.yf_unpermission, appi.title).sendToTarget();
				}
			}
		}
	}

	public boolean NativeAppIsLegitimate(String packetname) {

		boolean IsLegitimate = true;
		/*
		 * PackageInfo info;
		 * 
		 * try { info = getPackageManager().getPackageInfo(packetname, 0); if
		 * ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
		 * return true; } catch (NameNotFoundException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * if (store_apps == null) return IsLegitimate; for (YFShopAppInfo
		 * appshop : store_apps) { if (appshop.packname.equals(pac ketname)) {//
		 * check packet name and IsLegitimate = CheckPacketSignature(packetname,
		 * appshop.certmd5); break; } }
		 */
		return IsLegitimate;
	}

	@Override
	public boolean loadxmltoGlobalParms(String filename) {
		boolean ret = false;
		File file = new File(filename);
		if (!file.exists()) {
			Log.i(TAG, "filename: " + filename + " is not exits");
			return false;
		}
		try {
			FileInputStream fis = openFileInput(filename);
			store_apps = xml.Read(fis);
			fis.close();
			ret = true;

		} catch (FileNotFoundException e1) {

		} catch (IOException e) {

		}
		return ret;
	}

	public String getPacketCert(String packname) {

		for (YFShopAppInfo appi : store_apps) {
			if (appi.packname.equals(packname)) {
				return appi.certmd5;
			}
		}
		return null;
	}

	public String gettitlename(String packageName) {

		try {
			PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
			return info.applicationInfo.loadLabel(getPackageManager()).toString();

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class MyHandler extends Handler {

		private ControlInstallSev sev;
		private boolean isupdate;

		public MyHandler(ControlInstallSev sev) {
			this.sev = sev;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case PublicDefine.yf_unpermission:

				Toast.makeText(sev, "非法应用 " + (String) msg.obj + " 被卸载", Toast.LENGTH_SHORT).show();
				break;

			case PublicDefine.RCV_CONNECTED: // 连接事件

				break;

			case PublicDefine.RCV_INSTALLED: // 安装事件 --验证签名如果不合法 就卸载。

				if (!sev.IsInwhitelist(msg.obj.toString())) {
					if (!YFComm.resetDebugState()
							&& !sev.CheckPacketSignature((String) msg.obj, sev.getPacketCert((String) msg.obj))) {

						Toast.makeText(sev, "非法应用 " + sev.gettitlename((String) msg.obj) + " 被卸载", Toast.LENGTH_SHORT)
								.show();

						sev.uninstallPacket((String) msg.obj);
					}
				}
				break;

			case PublicDefine.RCV_UNINSTALLED:// 卸载事件
				if (sev.sevxml != null) {
					sev.sevxml.deletepacket((String) msg.obj);
				}
				break;
			case PublicDefine.MSG_SIGNED_FAILED:
			case PublicDefine.MSG_NOT_FOUNDFILE:
			case PublicDefine.MSG_PACKAGE_ERROR:
			case PublicDefine.MSG_CONNECT_HTTP_ERROR:
			case PublicDefine.MSG_DOWNLOAD_ERROR:
			case PublicDefine.MSG_MD5_FAILED:
			case PublicDefine.MSG_GETURLADDREERR:
				if (sev.sevxml != null) {
					sev.sevxml.downloadfailed(msg.what, (String) msg.obj); // 回调失败下载;
				}
				break;
			case PublicDefine.WRITE_SNERR:
				if (sev.sevxml != null) {
					sev.sevxml.downloadfailed(PublicDefine.WRITE_SNERR, "序列号被更改 应用商店停止使用,请恢复出厂设置..."); // 回调失败下载;
				} else {
					Toast.makeText(sev, "序列号被更改,请恢复出厂设置...", Toast.LENGTH_SHORT).show();
				}
				break;

			case PublicDefine.MSG_WRITESNNUM:

				sev.bind();
				break;

			case PublicDefine.YF_Complete:

				if (msg.arg1 == 0) {
					isupdate = false;
				} else {
					isupdate = true;
				}
				if (sev.sevxml != null) {
					try {
						sev.store_apps = sev.xml.Read(sev.openFileInput(PublicDefine.save_xml));
						if (sev.store_apps != null) {
							sev.YFshopTransform();// 应用商店对比信息,判断是否需要更新.
							sev.sevxml.downloadsuc(sev.store_apps, isupdate); // 回调成功下载;
						} else {
							sev.sevxml.downloadfailed(PublicDefine.MSG_NOT_FOUNDFILE, "应用商店下载文件错误...");
						}
					} catch (FileNotFoundException e) {
						Log.e(TAG, e.getMessage().toString());
					}
				} else {// 定时提示,在通知栏上提示更新。
						// 先比较文件签名，验证是否需要更新。

				}
				Log.i(TAG, "下载 XML");
				break;

			case PublicDefine.MSG_GETURLADDRESS:

				PublicDefine.SubDir = "http://" + msg.obj.toString() + "/yfstore/";
				Log.i(TAG, "Url=" + PublicDefine.SubDir);
				PublicDefine.url = PublicDefine.SubDir;
				new DownLoadFileThread(PublicDefine.SubDir + PublicDefine.XML_NAME, 1, ControlInstallSev.mhandler,
						this.sev).start();
				break;

			case PublicDefine.REMOTE_SEVERSUCC:
				sev.IsSetSn = true;
				PublicDefine.sn = msg.obj.toString();
				Log.i(TAG, "sn=" + msg.obj.toString());
				new DownLoadUrladd((String) msg.obj, mhandler, this.sev).start();
				break;

			default:
				break;
			}
		}
	}

	private boolean IsInwhitelist(String packname) {

		List<String> cps = YFComm.arrayFromXml(null, "Settings", "credible_package");
		if (cps != null) {
			for (String apkname : cps) {
				if (apkname.equals(packname))
					return true;
			}
		}
		return false;
	}

	private void YFshopTransform() {

		for (int i = 0; i < store_apps.size(); i++) {

			if (checkPackage(store_apps.get(i).packname)) {// 是本地应用

				store_apps.get(i).isnaticeApp = true;

				int ver = getversion(store_apps.get(i).packname);

				if (ver < store_apps.get(i).vercode)
					store_apps.get(i).isupdate = true;
			}

		}
	}

	public int getversion(String packageName) {

		try {
			PackageInfo info = getPackageManager().getPackageInfo(packageName, 0);
			return info.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean checkPackage(String packageName) {
		if (packageName == null || "".equals(packageName))
			return false;
		try {
			getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	@Override
	public int UpdateXmlFile(String file) {
		return PublicDefine.yf_success;
	}

	public static class YFMyInstalledReceiver extends BroadcastReceiver {

		private String packageName;

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) { // 安装事件
				packageName = intent.getData().getSchemeSpecificPart();

				Log.i(TAG, "安装了包名:" + packageName);
				if (!packageName.equals(PublicDefine.APP_PACKETNAME)) {
					if (mhandler != null)
						mhandler.obtainMessage(PublicDefine.RCV_INSTALLED, (Object) packageName).sendToTarget();
				}

			} else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {// 卸载事件

				packageName = intent.getData().getSchemeSpecificPart();
				Log.i(TAG, "卸载了包名:" + packageName);
				if (mhandler != null)
					mhandler.obtainMessage(PublicDefine.RCV_UNINSTALLED, (Object) packageName).sendToTarget();

			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) { // 网络事件，网络打开和连接

				@SuppressWarnings("deprecation")
				NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if (info != null) {

					if (NetworkInfo.State.CONNECTED == info.getState()) {
						Log.i(TAG, "网络连接");
					} else if (info.getType() == 1) {
						if (NetworkInfo.State.DISCONNECTING == info.getState()) {
							Log.i(TAG, "网络断开");
						}
					}
				}
			}
		}
	}
}
