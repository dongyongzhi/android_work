package com.yfcomm.yf_desk;

import com.yfcomm.public_define.public_define;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

public class PackageBroadcastReceiver extends BroadcastReceiver {

	private launcher launcher;
	private Handler handler;
	private IntentFilter mIntentFilter;

	PackageBroadcastReceiver(launcher launcher, Handler handler) {
		this.launcher = launcher;
		this.handler = handler;
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
			if (!intent.getData().getSchemeSpecificPart().equals(public_define.safeName)) {
				final String packagename = intent.getData().getSchemeSpecificPart();
				handler.post(new Runnable() {
					@Override
					public void run() {
						launcher.InstallAppInfo(packagename);
					}
				});
			}

		} else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {

			final String packagename = intent.getData().getSchemeSpecificPart();
			if (!intent.getData().getSchemeSpecificPart().equals(public_define.safeName)) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						launcher.UnInstallAPk(packagename);
					}
				});
			}
		}

	}

	protected void StartListen() {
		if (mIntentFilter == null) {
			mIntentFilter = new IntentFilter();
			mIntentFilter.addDataScheme("package");
			mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
			mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
			mIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
			launcher.registerReceiver(this, mIntentFilter);
		}
	}

	protected void RemoveListen() {
		launcher.unregisterReceiver(this);
	}
}
