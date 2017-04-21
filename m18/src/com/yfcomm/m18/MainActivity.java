package com.yfcomm.m18;



import com.yfcomm.R;
import com.yfcomm.mpos.DeviceInfo;
import com.yfcomm.mpos.api.SwiperController;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends BaseActivity   implements OnClickListener{
	
	public final static int FILE_SELECT_CODE = 0;
	public final static int DEVICE_SELECT_CODE = 1;
	
	private Button btnConnect;
	private Button btnSearch;

	private Button btnSwiping;
	private Button btnWriteKey;
	private Button btnUpdate;
	
	private EditText mEditDeviceName;
	private ProgressDialog progressDialog;
	
	private DeviceInfo selectDeviceInfo;
	
	
	private SwiperController swiper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mEditDeviceName = (EditText)this.findViewById(R.id.mEditDeviceName);
		btnConnect = (Button) this.findViewById(R.id.btnConnect);
		btnSearch = (Button) this.findViewById(R.id.btnSearch);
	
		btnSwiping = (Button) this.findViewById(R.id.btnSwiping);
		btnWriteKey = (Button) this.findViewById(R.id.btnWriteKey);
		btnUpdate = (Button) this.findViewById(R.id.btnUpdate);
		
		btnConnect.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		
		this.btnSwiping.setOnClickListener(this);
		this.btnWriteKey.setOnClickListener(this);
		this.btnUpdate.setOnClickListener(this);
		
 
		this.findViewById(R.id.btnClose).setOnClickListener(this);
		this.findViewById(R.id.btnReset).setOnClickListener(this);
		this.findViewById(R.id.btnDeviceInfo).setOnClickListener(this);
		this.findViewById(R.id.btnWriteParams).setOnClickListener(this);
		this.findViewById(R.id.btnWriteMainKey).setOnClickListener(this);
		this.findViewById(R.id.btnCalMac).setOnClickListener(this);
		this.findViewById(R.id.btnUpdateAid).setOnClickListener(this);
		this.findViewById(R.id.btnUpdatePublicKey).setOnClickListener(this);
		this.findViewById(R.id.btnDisplay).setOnClickListener(this);
		
		 //初始化刷卡器类
	    swiper = new SwiperController(this,swiperListener);
	    //注册连接断开通知
	    this.registerReceiver(disconnectReceiver, new IntentFilter("yifeng.mpos.connect.close"));
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		
		case R.id.btnSearch:
			this.startActivityForResult(new Intent(this, ChooseDeviceActivity.class),DEVICE_SELECT_CODE);
			break;
		
		//连接
		case R.id.btnConnect:
			if(mEditDeviceName.getText().length()>0) {
				if(this.selectDeviceInfo!=null) {
					progressDialog = new ProgressDialog(this);
					progressDialog.setCancelable(false);
					progressDialog.setMessage("正在连接...");	
					progressDialog.show();
					swiper.connectBluetoothDevice(10, selectDeviceInfo.getAddress());
				}
			}
			break;
			
		// 关闭连接
		case R.id.btnClose:
			this.swiper.disconnect();
			break;
			
		//复位
		case R.id.btnReset:
			this.swiper.restart();
			break;
		
		//获取设备信息
		case R.id.btnDeviceInfo:
			this.startActivity(new Intent(this,DeviceInfoActivity.class));
		break;
		
		//显示
		case R.id.btnDisplay:
			this.startActivity(new Intent(this,DisplayActivity.class));
			break;
		
		//写入设备参数
		case R.id.btnWriteParams:
			this.startActivity(new Intent(this,WriteParamsActivity.class));
			break;
		
		//写入主密钥
		case R.id.btnWriteMainKey:
			this.startActivity(new Intent(this,WriteMainKeyActivity.class));
			break;
			
		//写入工作密钥
		case R.id.btnWriteKey:
			this.startActivity(new Intent(this, WriteKeyActivity.class));
			break;
			
		//刷卡
		case R.id.btnSwiping:
			this.startActivity(new Intent(this,PurchaseActivity.class));
			break;
		
		//计算MAC
		case R.id.btnCalMac:
			this.startActivity(new Intent(this,CalMacActivity.class));
			break;
			
		//更新AID
		case R.id.btnUpdateAid:
			this.startActivity(new Intent(this,UpdateAidActivity.class));
			break;
		
		//更新公钥
		case R.id.btnUpdatePublicKey:
			this.startActivity(new Intent(this,UpdateMainKeyActivity.class));
			break;
		
		//更新固件
		case R.id.btnUpdate:
			//选择升级文件
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		    intent.setType("*/*"); 
		    intent.addCategory(Intent.CATEGORY_OPENABLE);
		 
		    try {
		        startActivityForResult( Intent.createChooser(intent, "选择要更新的程序"), FILE_SELECT_CODE);
		    } catch (android.content.ActivityNotFoundException ex) {
		        Toast.makeText(this, "Please install a File Manager.",  Toast.LENGTH_SHORT).show();
		    }
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
		
		if(resultCode!=RESULT_OK) {
			return;
		}
		
	    switch (requestCode) {
	    case FILE_SELECT_CODE:      
            Uri uri = data.getData();
            String path = uri.getPath();
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("正在更新固件");
            progressDialog.setCancelable(false);
            progressDialog.setMax(100);
            progressDialog.show();
			this.swiper.updateFirmware(path);
	        break;
	      
	    case DEVICE_SELECT_CODE:
	    	DeviceInfo deviceInfo = (DeviceInfo)data.getSerializableExtra("device");
	    	if(deviceInfo!=null) {
	    		mEditDeviceName.setText(deviceInfo.getName());
	    		
	    		this.selectDeviceInfo = deviceInfo;
	    	}
	    	break;
	    }
	    
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	protected void onDestroy() {
		super.onDestroy();
		this.swiper.disconnect();
		this.unregisterReceiver(disconnectReceiver);
	}
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			
			new AlertDialog.Builder(MainActivity.this).setTitle("提示")
			.setMessage("更新失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		

		@Override
		public void onDownloadProgress(long current, long total) {
			progressDialog.setProgress((int)Math.rint( (current *1.0 /total) * 100 ));
		}
		
		@Override
		public void onResultSuccess(int nType) {
			progressDialog.dismiss();
			 
			if(nType== 0x30) {
				//连接刷卡器成功
				Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
				//swiper.getDeviceInfo();
			} else if(nType == 0x39) {
				//固件更新成功
			}
		}
	 
	};
	
	/**
	 * 处理关闭通知
	 */
	private BroadcastReceiver disconnectReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent arg1) {
			new AlertDialog.Builder(context).setTitle("提示")
			.setMessage("连接断开")
			.setPositiveButton("确定", null) 
			.show();
		}
		
	};
}
