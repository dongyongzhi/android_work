package com.yfcomm.public_define;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class PackagesTools {
	private Context context;
	private PackageManager pm;
	private static final String TAG = "PackagesTools";

	public PackagesTools(Context context) {
		this.context = context;
		pm = context.getPackageManager();
	}

	public boolean checkPackage(String packageName) {
		if (packageName == null || packageName.isEmpty())
			return false;
		try {
			pm.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
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
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Log.e(TAG, "LanchActvie err:" + e.getMessage());
		}

	}

	public void LanchActvieParam(String packetname, String classname) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.putExtra("wx", true);
		ComponentName cn = new ComponentName(packetname, classname);
		intent.setComponent(cn);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "LanchActvieParam err:" + e.getMessage());
		}
	}

	public Intent getLaunchIntentForPackage(String packageName) {

		return pm.getLaunchIntentForPackage(packageName);
	}

	public WinInfo GetPackageInfo(String packageName) {
		WinInfo app = new WinInfo();
		try {
			PackageInfo info = pm.getPackageInfo(packageName, 0);

			app.icon = info.applicationInfo.loadIcon(pm);
			app.title = info.applicationInfo.loadLabel(pm).toString();
			app.Packetname = info.applicationInfo.packageName;

			return app;

		} catch (NameNotFoundException e) {
			Log.e(TAG, "GetPackageInfo err:" + e.getMessage());
		}
		return null;
	}

	public void LanchAPk(String packetname) {
		Intent LaunchIntent = pm.getLaunchIntentForPackage(packetname);
		try {
			context.startActivity(LaunchIntent);
		} catch (Exception e) {
			Log.e(TAG, "LanchAPk err:" + e.getMessage());
		}
	}

}
