package com.yifengcom.yfposdemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

/**
 * 射频卡测试
 */
public class RFCardActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog progressDialog;
	private TextView tv1, tv2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rfcard);
		setTitleName("射频卡测试");

		tv1 = (TextView) findViewById(R.id.tv1);
		tv2 = (TextView) findViewById(R.id.tv2);

		this.findViewById(R.id.btnRead).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);

	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage)
				throws RemoteException {
			progressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(RFCardActivity.this).setTitle("提示")
							.setMessage("操作失败，返回码：" + code + " 信息:" + messsage)
							.setPositiveButton("确定", null).show();
				}
			});

		}

		@Override
		public void onTimeout() throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(RFCardActivity.this).setTitle("错误")
							.setMessage("读取超时").setPositiveButton("确定", null)
							.show();
				}
			});
		}

		@Override
		public void onTradeCancel() throws RemoteException {
			progressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(RFCardActivity.this).setTitle("错误")
							.setMessage("取消读取").setPositiveButton("确定", null)
							.show();
				}
			});
		}

		@Override
		public void onReadSuccess(final byte[] body) throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					String text = "";
					if (body[0] == 0x0A) {
						tv1.setText("");
						int len = body[1];
						text = "读取A类卡成功\nUID:"
								+ ByteUtils.byteToHex(body, 2, len);
						tv1.setText(text);
					} else if (body[0] == 0x0B) {
						tv1.setText("");
						int len = body[1];
						text = "读取B类卡成功\nUID:"
								+ ByteUtils.byteToHex(body, 2, len);
						tv1.setText(text);
					} else {
						tv1.setText("操作失败");
					}
				}
			});
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			this.finish();
			break;

		// 读取
		case R.id.btnRead:
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在读取...");
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					try {
						YFApp.getApp().iService.cancel();
					} catch (RemoteException e) {
						e.printStackTrace();
						showToast("接口访问错误");
					}
					progressDialog.dismiss();
				}
			});
			progressDialog.show();
			
			try {
				YFApp.getApp().iService.readRFCard(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				progressDialog.dismiss();
			}
			break;
		}
	}
}
