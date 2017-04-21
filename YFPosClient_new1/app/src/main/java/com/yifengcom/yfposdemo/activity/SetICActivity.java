package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @Description: IC卡操作
 * @date 2016-7-28
 */
public class SetICActivity extends BaseActivity implements OnClickListener {
	private TextView txtResult, txtResult1, txtResult2;
	private EditText packData;
	private int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_ic);
		setTitleName("IC卡操作");

		txtResult = (TextView) this.findViewById(R.id.txtResult);
		txtResult1 = (TextView) this.findViewById(R.id.txtResult1);
		txtResult2 = (TextView) this.findViewById(R.id.txtResult2);

		packData = (EditText) this.findViewById(R.id.packData);

		this.findViewById(R.id.btnOpen).setOnClickListener(this); // 打开IC寻卡
		this.findViewById(R.id.btnClose).setOnClickListener(this); // IC卡下电
		this.findViewById(R.id.btnCmdSet).setOnClickListener(this); // IC卡数据交互
		this.findViewById(R.id.btnClear).setOnClickListener(this);  

		pd = new ProgressDialog(this);
	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage)
				throws RemoteException {
			closeDialog();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(SetICActivity.this)
							.setTitle("提示")
							.setMessage("操作失败，返回码：" + code + " 信息:" + messsage)
							.setPositiveButton("确定", null).show();
				}
			});

		}

		@Override
		public void onTimeout() throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(SetICActivity.this)
							.setTitle("错误").setMessage("读取超时")
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onTradeCancel() throws RemoteException {
			closeDialog();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(SetICActivity.this)
							.setTitle("错误").setMessage("取消读取")
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onReadSuccess(final byte[] body) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					String text = "";
					if (flag == 1) {
						if (body[0] == 0x01) {
							text = "寻卡失败";
						} else if (body[0] == 0x00) {
							text = "寻卡成功:"
									+ ByteUtils.byteToHex(body, 2,
											body.length - 2);
						} else {
							text = "操作超时";
						}
						txtResult.setText(text);
					} else {
						text = "IC卡数据交互返回：" + ByteUtils.byteToHex(body, 0,body.length);
						txtResult1.setText(text);
					}
				}
			});
		};
		
		@Override
		public void onResultSuccess(int ntype) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					txtResult2.setText("下电成功");
				}
			});
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOpen:
			flag = 1;
			pd.setMessage("打开IC寻卡...");
			pd.setCancelable(true);
			pd.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					try {
						YFApp.getApp().iService.cancel();
					} catch (RemoteException e) {
						e.printStackTrace();
						showToast("接口访问错误");
					}
					closeDialog();
				}
			});
			pd.show();
			try {
				YFApp.getApp().iService.openICFind(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
			}
			break;
		case R.id.btnCmdSet:
			if (this.packData.getText().length() == 0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("请输入报文").setPositiveButton("确定", null)
						.show();
				return;
			}
			flag = 2;
			byte[] data = ByteUtils.hexToByte(this.packData.getText()
					.toString().trim());
			pd.setMessage("IC交互数据发送...");
			pd.setCancelable(false);
			pd.show();
			try {
				YFApp.getApp().iService.sendICCmd(data, mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
			}
			break;
		case R.id.btnClose:
			pd.setMessage("IC下电...");
			pd.setCancelable(false);
			pd.show();
			try {
				YFApp.getApp().iService.powerDownIC(mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
			}
			break;
		case R.id.btnClear:
			txtResult.setText("");
			txtResult1.setText("");
			txtResult2.setText("");
			break;
		}
	}

}
