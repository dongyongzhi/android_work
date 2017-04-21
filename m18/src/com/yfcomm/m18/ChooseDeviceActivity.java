package com.yfcomm.m18;

import com.yfcomm.R;
import com.yfcomm.mpos.DeviceInfo;
import com.yfcomm.mpos.api.SwiperController;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;


public class ChooseDeviceActivity extends BaseActivity implements OnItemClickListener,OnClickListener {

	private BluetoothAdapter bluetoothAdapter;
	private ListView lvBluetooth;
    private LinearLayout progressBar;
    private Button stopsearchbtn;
    
    private SwiperController swiper;
	 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.choose_device);
        
        stopsearchbtn = (Button)findViewById(R.id.stopsearchbtn);
        stopsearchbtn.setOnClickListener(this);
        
        progressBar = (LinearLayout) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        
        bluetoothAdapter = new BluetoothAdapter(this);
        
        lvBluetooth = (ListView) this.findViewById(R.id.listview);
	    lvBluetooth.setAdapter(bluetoothAdapter);
	    lvBluetooth.setOnItemClickListener(this);
	    
	    //初始化刷卡器类
	    swiper = new SwiperController(this,changeListener);
	     //开始搜索
	    swiper.startSearchDevice();
	    progressBar.setVisibility(View.VISIBLE);
    }
 
	  
	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.stopsearchbtn){
			swiper.stopSearchDevice();
    	}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		DeviceInfo deviceInfo =(DeviceInfo)bluetoothAdapter.getItem(arg2);
		Intent mIntent = new Intent();
		mIntent.putExtra("device", deviceInfo);
		this.setResult(RESULT_CANCELED, mIntent);
		this.finish();
		
		//选择一个设备连接，并停止搜索
		swiper.stopSearchDevice();
	}
	 
	
	private SimpleSwiperListener changeListener = new SimpleSwiperListener() {
		//重写找到一个设备
		@Override
		public void foundOneDevice(DeviceInfo deviceInfo) {
			if (deviceInfo.getName() != null) {
				bluetoothAdapter.addItem(deviceInfo);
			}
		}
		
		@Override
		public void onResultSuccess(int nType) {
			//查找完成
			if(nType == 0x40) {
				progressBar.setVisibility(View.GONE);
			}
		}
	};

}
