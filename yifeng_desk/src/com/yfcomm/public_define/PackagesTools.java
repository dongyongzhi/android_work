package com.yfcomm.public_define;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackagesTools {
    private Context context;
    private PackageManager pm;
	
	public PackagesTools(Context context){
		this.context=context;
		pm=context.getPackageManager();
	}
	
	public boolean checkPackage(String packageName) {
		if (packageName == null || "".equals(packageName))
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
		context.startActivity(intent);
	}

	public void LanchActvieParam(String packetname, String classname) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.putExtra("wx", true);
		ComponentName cn = new ComponentName(packetname, classname);
		intent.setComponent(cn);
		context.startActivity(intent);
	}
	
	public AppInfo GetPackageInfo(String packageName) {
		AppInfo app = new AppInfo();
		try {
			PackageInfo info = pm.getPackageInfo(packageName, 0);

			app.icon = info.applicationInfo.loadIcon(pm);
			app.title = info.applicationInfo.loadLabel(pm).toString();
			app.Packetname = info.applicationInfo.packageName;

			return app;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void LanchAPk(String packetname) {
		Intent LaunchIntent = pm.getLaunchIntentForPackage(packetname);
		context.startActivity(LaunchIntent);
	}
	
}
