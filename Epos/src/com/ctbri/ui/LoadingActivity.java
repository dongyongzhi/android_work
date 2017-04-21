package com.ctbri.ui;

import java.util.Date;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.R;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.pos.support.ParamsWriterRequest;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecPOSSelect;
import com.yfcomm.pos.ui.DeviceServiceActivity;

/**
 * comment:闪屏界面
 * 
 * @author:ZhangYan Date:2012-10-24
 */
public class LoadingActivity extends BaseActivity implements OnClickListener {
	
	// Intent request codes
    private static final int REQUEST_ENABLE_BT = 1;

	private ImageView imageView;
	private AnimationDrawable animDrawable;
	private BluetoothAdapter deviceAdapter;

	private TextView txtStatus;
	private View mDeviceSelect, mSettingPOSName;

	private String connAddress, connName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		txtStatus = (TextView) this.findViewById(R.id.txtStatus);
		mDeviceSelect = this.findViewById(R.id.mDeviceSelect);
		mSettingPOSName = this.findViewById(R.id.mSettingPOSName);
		imageView = (ImageView) findViewById(R.id.frameview);
		deviceAdapter  = BluetoothAdapter.getDefaultAdapter();
	}

	/**
	 * 设备绑定成功
	 */
	protected void onDeviceReady() {
		//打开蓝牙设备
		if(deviceAdapter!=null) {
			if(!deviceAdapter.isEnabled()) {
				Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			 	startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			} else {
				setup();
			}
		}else {
			Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 重新初始化蓝牙设备
	 */
	protected void reServiceBind() {
		//SharedPreferences settings = this.getSharedPreferences(STORE_POS_KEY,Context.MODE_PRIVATE);
		//settings.edit().putString (STORE_POSTYPE_KEY, "").commit();
		selectDevice();
	}

	/**
	 * 选择终端设备
	 */
	private void selectDevice() {
		ElecPOSSelect posSelectDialog = new ElecPOSSelect(this,
			new ElecPOSSelect.OnSelectedListener() {
			
				//选择返回
				@Override
				public void onSelected(String mac, String name) {
					LoadingActivity.this.connAddress = mac;
					LoadingActivity.this.connName = name;
					connectDevice(mac,name);
				}
			}
		);

		// 取消继续连接
		posSelectDialog.setOnCancelListener(new ElecPOSSelect.OnCancelListener() {
			@Override
			public void onCancel() {
				finish();
			}
		});
		//显示窗体
		posSelectDialog.show();
	}
	
	/**
	 * 连接设备
	 * @param address  mac 地址
	 * @param name      设备名称 /蓝牙名称
	 */
	private void connectDevice(String address,String name) {
		BluetoothDevice remoteDevice = this.deviceAdapter.getRemoteDevice(address);
		this.getDevice().connect(remoteDevice);
		
		//等待连接返回动画
		imageView.post(new Runnable() {
			@Override
			public void run() {
				// 取得帧动画
				animDrawable = (AnimationDrawable) imageView.getBackground();
				animDrawable.start();
			}
		});
		
		mDeviceSelect.setOnClickListener(LoadingActivity.this);
		mDeviceSelect.setBackgroundResource(R.drawable.btn_key_clear_on);
		mSettingPOSName.setVisibility(View.VISIBLE);
		txtStatus.setTextColor(Color.parseColor("#4F9FC9"));
		
		txtStatus.setText(String.format("正在连接：%s", name));
	}
	
	/**
	 * 连接成功返回
	 */
	@Override
	protected void onConnected(){
		super.onConnected();
		
		mDeviceSelect.setOnClickListener(null);
		mDeviceSelect.setBackgroundDrawable(null);
		mSettingPOSName.setVisibility(View.GONE);
		txtStatus.setTextColor(Color.WHITE);
		txtStatus.setText("蓝牙设备连接成功...");

		goPageHome();
	}
	
	
	/**
	 * 连接失败
	 */
	@Override
	protected void onConnectFailed() {
		super.onConnectFailed();
		
		if (animDrawable != null && animDrawable.isRunning()) {
			animDrawable.stop();
		}
		//获取帮助信息
		String message = this.getString(R.string.wran_term_not_found_help);
		SpannableString mSpannableString = new SpannableString(message);
		 //设置字体大小
		mSpannableString.setSpan(new AbsoluteSizeSpan(15),0,message.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		//goPageHome();
		 
		MessageBox.showError(LoadingActivity.this,mSpannableString,
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog,int arg1) {
				 LoadingActivity.this.reServiceBind();
				 dialog.dismiss();	 
				}
			}
		);		 
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch (requestCode) {
		 
		 //蓝牙打开返回
		 case  REQUEST_ENABLE_BT:
			 if (resultCode == Activity.RESULT_OK) {
				 setup();
			 } else {
				 Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
	             finish();
			 }
		 }
	}
	
	//加载
	private void setup() {
		//获取最近一次连接的设备
		SharedPreferences settings = this.getSharedPreferences(STORE_POS_KEY,Context.MODE_PRIVATE);
		// 获取上次连接的名称
		connAddress = settings.getString(STORE_POS_ADDRESS_KEY, "");
		connName = settings.getString(STORE_POS_NAME_KEY, "");
		
		if ("".equals(connAddress)) {
			selectDevice();
			return;
		}
		
	}

	/**
	 * 跳转登录
	 */
	private void goPageHome() {
		Intent main = new Intent(LoadingActivity.this, MainMenuActivitys.class);
		//main.putExtra(EXTRA_POSINFO, (Parcelable) posInfo); // pos机参数信息
		startActivity(main);
		this.finish();
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 返回
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (animDrawable != null && animDrawable.isRunning()) {
				animDrawable.stop();
			}
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mDeviceSelect: // 选择设备
			if (animDrawable != null && animDrawable.isRunning()) {
				animDrawable.stop();
			}
			selectDevice();
			break;
		}
	}

}