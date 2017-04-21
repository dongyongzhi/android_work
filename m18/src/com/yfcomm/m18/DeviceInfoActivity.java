package com.yfcomm.m18;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.yfcomm.R;
import com.yfcomm.mpos.api.SwiperController;

public class DeviceInfoActivity  extends BaseActivity{

	ProgressDialog pd=null;	
	TextView result;
	
	private SwiperController swiper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.deviceinfo);
		
		result = (TextView)this.findViewById(R.id.result);
		
		pd = ProgressDialog.show(this, "", "正在读取...");	
		
	    //初始化刷卡器类
	    swiper = new SwiperController(this,changeListener);
	     //获取设备信息
	    swiper.getDeviceInfo();
	}
	
	private SimpleSwiperListener changeListener = new SimpleSwiperListener() {

		@Override
		public void onError(int errorCode, String errorMessage) {
			pd.dismiss();
			DeviceInfoActivity.this.result.setText("Error  code:"+errorCode +"  message:"+errorMessage);
		}
	 
		@Override
		public void onGetDeviceInfo(String customerNo,String termNo,String batchNo,boolean existsMainKey,String sn, String version) {
			pd.dismiss();
			StringBuilder sb = new StringBuilder();
			sb.append("商户号:").append(customerNo).append("\n");
			sb.append("终端号:").append(termNo).append("\n");
			sb.append("批次号:").append(batchNo).append("\n");
			sb.append("是否己下载主密钥:").append(existsMainKey ? 1 : 0).append("\n");
			sb.append("序列号:").append(sn).append("\n");
			sb.append("version:").append(version).append("\n");
			DeviceInfoActivity.this.result.setText(sb.toString());
		}
		
	};
}
