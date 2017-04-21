package com.yifengcom.yfposdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

/**
 * @Description: 自毁测试
 */
public class TestDestroyActivity extends BaseActivity implements OnClickListener{
	private TextView tv_info;
	private Button btnInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		setTitleName("自毁测试");
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		btnInfo = (Button)this.findViewById(R.id.btnInfo); 
		btnInfo.setOnClickListener(this);
		btnInfo.setText("自毁测试");

		pd = new ProgressDialog(this);
		pd.setMessage("自毁测试请求...");
		pd.setCancelable(true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInfo:
			pd.show();
			try {
				YFApp.getApp().iService.testDestroy(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				tv_info.setText("接口访问错误");
				closeDialog();
			}
			break;
		default:
			break;
		}
	}
	

	CallBackListener mCallBack = new CallBackListener(){

		@Override
		public void onError(final int errorCode, final String errorMessage)
				throws RemoteException {
			closeDialog();

			runOnUiThread(new Runnable() {
				public void run() {
					tv_info.setText("Error  code:" + errorCode + "  message:"
							+ errorMessage);
				}
			});

		}

		@Override
		public void onResultSuccess(final int arg0) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					if(arg0 == 0x01){
						tv_info.setText("成功");
					}
				}
			});
			
		};
 
	};

}
