package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.TextView;

/**
 * @Description: 设置时间
 */
public class SetTimeActivity extends BaseActivity{
	private TextView tv_info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gettime);
		setTitleName("设置时间");
		
		tv_info = (TextView) findViewById(R.id.tv_info);
	}
	
	
	protected void onResume() {
		super.onResume();
		pd = ProgressDialog.show(this, "", "正在设置...");	
		try {
			YFApp.getApp().iService.setDateTime(System.currentTimeMillis(), mCallBack);
		} catch (RemoteException e) {
			e.printStackTrace();
			showToast("接口访问错误");
			pd.dismiss();
		}
	};
	
	CallBackListener mCallBack = new CallBackListener() {
		@Override
		public void onError(final int errorCode,final String errorMessage) {
			pd.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					tv_info.setText("Error  code:"+errorCode +"  message:"+errorMessage);
				}});
		}
	 
		public void onResultSuccess(int arg0) {
			pd.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					tv_info.setText("success");
				}});
		};
	};
}
