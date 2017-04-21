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

/**
 * 写入主密钥
 * 
 */
public class WriteMainKeyActivity extends BaseActivity implements
		OnClickListener {
	private EditText mainKey;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writemainkey);
		setTitleName("更新主密钥");

		mainKey = (EditText) this.findViewById(R.id.mainKey);

		this.findViewById(R.id.btnWriteKey).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			this.finish();
			break;

		// 写入
		case R.id.btnWriteKey:
			if (this.mainKey.getText().length() == 0
					|| this.mainKey.getText().length() % 2 != 0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("输入的主密钥有误").setPositiveButton("确定", null)
						.show();
				return;
			}

			pd = ProgressDialog.show(this, null, "正在执行...", false);
			pd.show();
			
			try {
				YFApp.getApp().iService.writeMainKey(ByteUtils.hexToByte(mainKey.getText().toString()),mCallBack);
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
		public void onError(final int code, final String messsage)
				throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(WriteMainKeyActivity.this)
							.setTitle("提示")
							.setMessage("操作失败，返回码：" + code + " 信息:" + messsage)
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onResultSuccess(int nType)
				throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					showToast("写入成功");
				}
			});
		}
	};

}
