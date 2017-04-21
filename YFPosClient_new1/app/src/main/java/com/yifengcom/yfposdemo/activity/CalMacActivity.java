package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class CalMacActivity extends BaseActivity implements OnClickListener {

	private EditText packData;
	private TextView txtResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calmac);
		setTitleName("验证MAC");

		packData = (EditText) this.findViewById(R.id.packData);
		txtResult = (TextView) this.findViewById(R.id.txtResult);

		this.findViewById(R.id.btnCalMac).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			this.finish();
			break;

		// 计算
		case R.id.btnCalMac:
			if (this.packData.getText().length() == 0
					|| this.packData.getText().length() % 2 != 0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("输入的报文有误").setPositiveButton("确定", null)
						.show();
				return;
			}
			pd = ProgressDialog.show(this, null, "正在执行...", false);

			try {
				YFApp.getApp().iService.calculateMac(
						ByteUtils.hexToByte(packData.getText().toString()),
						mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
			}

			break;
		}
	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {

					new AlertDialog.Builder(CalMacActivity.this).setTitle("提示")
							.setMessage("操作失败，返回码：" + code + " 信息:" + messsage)
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onCalculateMacSuccess(final byte[] mac) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					txtResult.setText("mac:" + ByteUtils.byteToHex(mac));
				}
			});
		}
	};
}
