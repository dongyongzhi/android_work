package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;

public class PSAMActivity extends BaseActivity implements OnClickListener {
	
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_psam);
		setTitleName("PSAM卡测试");
		
		this.findViewById(R.id.btnPsamSt720).setOnClickListener(this);
		this.findViewById(R.id.btnPsam).setOnClickListener(this);
		
		progressDialog = new ProgressDialog(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// PSAM卡信息
		case R.id.btnPsam:
			setResult("");
			progressDialog.setMessage("正在读取PSAM卡信息...");
			progressDialog.show();
			try {
				YFApp.getApp().iService.getPsamInfo(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				progressDialog.dismiss();
			}
			break;

		// PSAM-ST720
		case R.id.btnPsamSt720:
			setResult("");
			progressDialog.setMessage("正在读取PSAM-ST720...");
			progressDialog.show();
			try {
				YFApp.getApp().iService.getPsamAndSt720Info(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
				progressDialog.dismiss();
			}
			break;
		}
	}
	
	CallBackListener mCallBack = new CallBackListener() {
		@Override
		public void onError(final int errorCode, final String errorMessage) {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PSAMActivity.this)
							.setTitle("提示")
							.setMessage(
									"操作失败，返回码：" + errorCode + " 信息:"
											+ errorMessage)
							.setPositiveButton("确定", null).show();
				}
			});
		}
	 
		@Override
		public void onGetPsamAndSt720Info(final String psam, final String st720) {
			progressDialog.dismiss();
			
			runOnUiThread(new Runnable() {
				public void run() {
					StringBuilder sb = new StringBuilder();
					sb.append("PSAM ATR:").append(psam).append("\n");
					sb.append("ST720 ATR:").append(st720).append("\n");
					setResult(sb.toString());
				}
			});
		};
		
		@Override
		public void onReadPsamNo(final String num) {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					setResult("PSAM 卡号:"+ num);
				}
			});
			
		};
	 
	};
 
}
