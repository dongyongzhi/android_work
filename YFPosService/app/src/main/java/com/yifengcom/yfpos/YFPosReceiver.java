package com.yifengcom.yfpos;

import com.yifengcom.yfpos.service.YFPosService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class YFPosReceiver extends BroadcastReceiver {

	private final String ACTION = "android.intent.action.BOOT_COMPLETED";
	private final String TAG = YFPosReceiver.class.getName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			context.startService(new Intent(context, YFPosService.class));
			Log.d(TAG, "yfpos service has started!");
		}
	}
}
