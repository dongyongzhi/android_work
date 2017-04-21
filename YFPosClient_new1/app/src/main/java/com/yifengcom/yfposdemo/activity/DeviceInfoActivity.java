package com.yifengcom.yfposdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yifengcom.yfpos.service.DeviceModel;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

/**
 * @Description: 获取设备信息
 */
public class DeviceInfoActivity extends BaseActivity implements OnClickListener{
	private TextView tv_info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_version);
		setTitleName("获取设备信息");
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		this.findViewById(R.id.btnInfo).setOnClickListener(this); // 获取设备版本

		pd = new ProgressDialog(this);
		pd.setMessage("正在读取...");
		pd.setCancelable(true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnInfo:
			try {
				if (!YFApp.getApp().isBind) {
					showToast("请先绑定");
					return;
				}
				pd.show();
				YFApp.getApp().iService.onGetDeviceInfo(mCallBack);
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
		public void onGetDeviceInfoSuccess(DeviceModel deviceModel) throws RemoteException {
			closeDialog();
			final StringBuilder sb = new StringBuilder();
			sb.append("商户号:").append(deviceModel.getCustomerNo()).append("\n");
			sb.append("终端号:").append(deviceModel.getTermNo()).append("\n");
			sb.append("批次号:").append(deviceModel.getBatchNo()).append("\n");
			sb.append("是否己下载主密钥:").append(deviceModel.getExistsMainKey()).append("\n");
			sb.append("终端应用版本号:").append(deviceModel.getTerVersion()).append("\n");
			sb.append("软件版本号:").append(deviceModel.getSoftVersion()).append("\n");
			sb.append("SN号:").append(deviceModel.getSn()).append("\n");
			
			runOnUiThread(new Runnable() {
				public void run() {
					tv_info.setText(sb.toString());
				}
			});
			
		};
 
 
	};

}
