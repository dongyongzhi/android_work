package com.yifengcom.yfposdemo.activity;

import java.text.NumberFormat;
import java.util.Locale;

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
 * 读取电子现金余额
 */
public class ReadMoneyActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_money);
		setTitleName("读取电子现金余额");

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
					new AlertDialog.Builder(ReadMoneyActivity.this)
							.setTitle("提示")
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
					new AlertDialog.Builder(ReadMoneyActivity.this)
							.setTitle("错误").setMessage("读取超时")
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onTradeCancel() throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(ReadMoneyActivity.this)
							.setTitle("错误").setMessage("取消读取")
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onReadSuccess(final byte[] money) throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					if (money[0] == 0x00) {
						byte[] data = new byte[6];
						System.arraycopy(money, 1, data, 0, data.length);
						String m = ByteUtils.bcdToString(data, 0, data.length);
						double num = Double.parseDouble(m);
						NumberFormat format = NumberFormat
								.getCurrencyInstance(Locale.CHINA);
						setText(format.format(num / 100) + "元");
					} else if (money[0] == 0x01) {
						setText("操作失败");
					} else if (money[0] == 0x02) {
						setText("操作超时");
					}
				}
			});
		};
	};

	private void setText(String txt) {
		((TextView) ReadMoneyActivity.this.findViewById(R.id.trxResult))
				.setText(txt);
	}

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
			progressDialog.setMessage("正在读取电子现金余额...");
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
				YFApp.getApp().iService.readMoney(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				progressDialog.dismiss();
			}
			break;
		}
	}
}
