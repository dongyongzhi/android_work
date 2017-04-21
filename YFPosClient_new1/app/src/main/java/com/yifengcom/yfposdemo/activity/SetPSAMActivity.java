package com.yifengcom.yfposdemo.activity;

import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @Description: PSAM卡操作
 * @date 2016-7-28
 */
public class SetPSAMActivity extends BaseActivity implements OnClickListener {
	private TextView txtResult, txtResult1, txtResult2;
	private EditText packData1, packData2;
	private int flag = 0; // 1、复位；2、读卡；3：写卡
	private int sub = 0; // 0、卡1；1、卡2

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_psam);
		setTitleName("PSAM卡操作");

		txtResult = (TextView) this.findViewById(R.id.txtResult);
		txtResult1 = (TextView) this.findViewById(R.id.txtResult1);
		txtResult2 = (TextView) this.findViewById(R.id.txtResult2);

		packData1 = (EditText) this.findViewById(R.id.packData1);
		packData2 = (EditText) this.findViewById(R.id.packData2);

		this.findViewById(R.id.btnPsamReset1).setOnClickListener(this); // 复位1
		this.findViewById(R.id.btnPsamReset2).setOnClickListener(this); // 复位2
		this.findViewById(R.id.btnPsamRead1).setOnClickListener(this); // 读卡1
		this.findViewById(R.id.btnPsamRead2).setOnClickListener(this); // 读卡2
		this.findViewById(R.id.btnPsamWrite1).setOnClickListener(this); // 写卡1
		this.findViewById(R.id.btnPsamWrite2).setOnClickListener(this); // 写卡2
		
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
					new AlertDialog.Builder(SetPSAMActivity.this)
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
					new AlertDialog.Builder(SetPSAMActivity.this)
							.setTitle("错误").setMessage("读取超时")
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onReadSuccess(final byte[] body) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					String text = ByteUtils.byteToHex(body, 0, body.length);
					if (flag == 1) {
						txtResult.setText((sub == 0 ? "卡1" : "卡2") + "复位返回："
								+ text);
					} else if (flag == 2) {
						txtResult1.setText((sub == 0 ? "卡1" : "卡2") + "读卡返回："
								+ text);
					} else if (flag == 3) {
						txtResult2.setText((sub == 0 ? "卡1" : "卡2") + "写卡返回："
								+ text);
					}
				}
			});
		};
	};
	
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			closeDialog();
			showToast("接口访问错误");
		};
	};

	public void resetPSAM(int type) {
		flag = 1;
		final byte[] data = new byte[1];
		data[0] = (byte) type;
		pd.setMessage("PSAM卡复位...");
		pd.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					YFApp.getApp().iService.resetPSAM(data, mCallBack);
				} catch (RemoteException e) {
					e.printStackTrace();
					handler.obtainMessage().sendToTarget();
				}
			}
		}).start();
	}

	public void readPSAM(String type) {
		if (this.packData1.getText().length() == 0) {
			new AlertDialog.Builder(this).setTitle("提示").setMessage("请输入读卡报文")
					.setPositiveButton("确定", null).show();
			return;
		}
		flag = 2;
		String txt = type + this.packData1.getText().toString().trim();
		final byte[] data = ByteUtils.hexToByte(txt);
		pd.setMessage("读PSAM卡...");
		pd.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					YFApp.getApp().iService.readPSAM(data, mCallBack);
				} catch (RemoteException e) {
					e.printStackTrace();
					handler.obtainMessage().sendToTarget();
				}
			}
		}).start();
	}

	public void writePSAM(String type) {
		if (this.packData2.getText().length() == 0) {
			new AlertDialog.Builder(this).setTitle("提示").setMessage("请输入写卡报文")
					.setPositiveButton("确定", null).show();
			return;
		}
		flag = 3;
		String txt = type + this.packData2.getText().toString().trim();
		final byte[] data = ByteUtils.hexToByte(txt);
		pd.setMessage("写PSAM卡...");
		pd.show();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					YFApp.getApp().iService.writePSAM(data, mCallBack);
				} catch (RemoteException e) {
					e.printStackTrace();
					handler.obtainMessage().sendToTarget();
				}
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnPsamReset1:
			sub = 0;
			resetPSAM(0);
			break;
		case R.id.btnPsamReset2:
			sub = 1;
			resetPSAM(1);
			break;
		case R.id.btnPsamRead1:
			sub = 0;
			readPSAM("01");
			break;
		case R.id.btnPsamRead2:
			sub = 1;
			readPSAM("02");
			break;
		case R.id.btnPsamWrite1:
			sub = 0;
			writePSAM("01");
			break;
		case R.id.btnPsamWrite2:
			sub = 1;
			writePSAM("02");
			break;
		case R.id.btnClear:
			txtResult.setText("");
			txtResult1.setText("");
			txtResult2.setText("");
			break;
		}
	}

}
