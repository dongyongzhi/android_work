package com.yfcomm.businesshall;

import java.lang.reflect.Method;

import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.public_define.public_define;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class EntranceSel extends Activity {

	private LinearLayout consumption_treasure, bussinesshall, readsfcard;
	private long mExitTime;
	public final static String TAG = "EntranceSel";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectsevtype);
		
		consumption_treasure = (LinearLayout) findViewById(R.id.consumption_treasure);
		consumption_treasure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LanchActive(consumptionTreasure.class);
			}
		});

		bussinesshall = (LinearLayout) findViewById(R.id.businesshall);
		bussinesshall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			   LanchActive(MainActivity.class);
			}
		});

		readsfcard = (LinearLayout) findViewById(R.id.readsfcard);
		readsfcard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IdentityAuthentication.isReturn = false;
				LanchActive(IdentityAuthentication.class);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
	}


	public void LanchActive(Class<?> cls) {

		Intent intent = new Intent(this, cls);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {

				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				finish();
			}
		//	moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void Release() {

		byte[] data = PackageBuilder.syn(PackageBuilder.CMD_CANCEL, null).getPackData();

		if (data != null && public_define.serialport != null) {
			public_define.serialport.getOutputStream(data);
			Log.i(TAG, "发送串口终端数据:" + ByteUtils.printBytes(data, 0, data.length));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Release();
		public_define.close();
		Log.e(TAG, "释放资源....");
	}
}
