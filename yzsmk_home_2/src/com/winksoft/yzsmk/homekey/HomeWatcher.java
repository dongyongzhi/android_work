package com.winksoft.yzsmk.homekey;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * watch the HOME key pressed, we only listen the HOME pressed <b>NOT Intercept
 * </b>this pressed.<br>
 * Tips:invoke {@link #startWatch()} in activity's onResume method and
 * {@link #stopWatch()} in activity's onStop Method.
 * 
 * @author
 */
public class HomeWatcher {

	static final String TAG = "HomeWatcher";

	private Context mContext;

	private IntentFilter mFilter;

	private OnHomePressedListener mListener;

	private InnerRecevier mRecevier;

	public HomeWatcher(Context context) {
		mContext = context;
		mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	}

	/**
	 * set the home pressed listener, if set will callback the home pressed
	 * listener's method when home pressed.
	 * 
	 * @param listener
	 */
	public void setOnHomePressedListener(OnHomePressedListener listener) {
		mListener = listener;
		mRecevier = new InnerRecevier();
	}

	/**
	 * start watch
	 */
	public void startWatch() {
		if (mRecevier != null) {
			mContext.registerReceiver(mRecevier, mFilter);
		}
	}

	/**
	 * stop watch
	 */
	public void stopWatch() {
		if (mRecevier != null) {
			mContext.unregisterReceiver(mRecevier);
		}
	}

	class InnerRecevier extends BroadcastReceiver {

		private final String SYSTEM_DIALOG_REASON_KEY = "reason";
		private final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
		private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
		private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
				if (reason != null) {
					Log.i(TAG, "receive action:" + action + ",reason:" + reason);
					if (mListener != null) {
						if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
						
							mListener.onHomePressed();
						} else if (reason
								.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {

							mListener.onHomeLongPressed();
						}
					}
				}
			}
		}
	}
}