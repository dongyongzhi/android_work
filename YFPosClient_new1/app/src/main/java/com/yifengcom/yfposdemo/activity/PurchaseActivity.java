package com.yifengcom.yfposdemo.activity;

import java.util.ArrayList;
import com.yifengcom.yfpos.model.TrxType;
import com.yifengcom.yfpos.service.CardModel;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 消费
 */
public class PurchaseActivity extends BaseActivity implements OnClickListener {

	private EditText mEditMoney;
	private Button btnPurchase;
	private Button btnCancel;
	private ProgressDialog progressDialog;
	private long amount = 0L;
	protected InputMethodManager imm;  

	ArrayList<Integer> list = new ArrayList<Integer>();
	
	PowerManager manager;
	WakeLock lock;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);
		setTitleName("刷卡");
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
		this.manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.lock = this.manager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

		mEditMoney = (EditText) this.findViewById(R.id.mEditMoney);
		btnPurchase = (Button) this.findViewById(R.id.btnPurchase);
		btnCancel = (Button) this.findViewById(R.id.btnCancel);

		this.btnPurchase.setOnClickListener(this);
		this.btnCancel.setOnClickListener(this);

		progressDialog = new ProgressDialog(this);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.lock.acquire();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.lock.release();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			this.finish();
			break;

		// 消费
		case R.id.btnPurchase:
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			if (this.mEditMoney.getText().length() == 0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("请输入消费金额").setPositiveButton("确定", null)
						.show();
				this.mEditMoney.setFocusable(true);
				return;
			}

			progressDialog.setMessage("正在刷卡...");
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
			PurchaseActivity.this.setText(R.id.trxResult, "");
			amount = ((Double) (Double.valueOf(this.mEditMoney.getText()
					.toString()) * 100)).longValue();

			try {
				YFApp.getApp().iService.startSwiper(120, amount, 0,
						TrxType.PURCHASE.getValue(), mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				progressDialog.dismiss();
			}
			break;
		}
	}

	public void setText(int resId, String text) {
		((TextView) this.findViewById(resId)).setText(text);
	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage)
				throws RemoteException {
			progressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PurchaseActivity.this)
							.setTitle("提示")
							.setMessage("操作失败，返回码：" + code + " 信息:" + messsage)
							.setPositiveButton("确定", null).show();
				}
			});

		}

		public void onDetectIc() throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog.setMessage("检测到IC卡");

				}
			});

		}

		public void onInputPin() throws RemoteException {
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog.setMessage("请输入密码");
				}});
		}
		
		public void onSwiperSuccess(final CardModel cardModel) throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					// 数据加密成功返回
					StringBuilder sb = new StringBuilder();
					sb.append("pan:").append(cardModel.getPan()).append("\n");
					sb.append("expireDate:").append(cardModel.getExpireDate())
							.append("\n");

					sb.append("batchNo:").append(cardModel.getBatchNo())
							.append("\n");
					sb.append("serialNo:").append(cardModel.getSerialNo())
							.append("\n");
					sb.append("track2:").append(cardModel.getTrack2())
							.append("\n");
					sb.append("track3:").append(cardModel.getTrack3())
							.append("\n");
					sb.append("encryTrack2:")
							.append(cardModel.getEncryTrack2()).append("\n");
					sb.append("encryTrack3:")
							.append(cardModel.getEncryTrack3()).append("\n");

					sb.append("icData:").append(cardModel.getIcData())
							.append("\n");

					sb.append("mac:").append(cardModel.getMac()).append("\n");
					sb.append("pinBlock:").append(cardModel.getPinBlock())
							.append("\n");

					sb.append("icseq:").append(cardModel.getIcSeq())
							.append("\n");
					sb.append("random:").append(cardModel.getRandom())
							.append("\n");

					PurchaseActivity.this
							.setText(R.id.trxResult, sb.toString());

				}
			});
		};


		@Override
		public void onTimeout() throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PurchaseActivity.this)
							.setTitle("错误").setMessage("刷卡超时")
							.setPositiveButton("确定", null).show();
				}
			});
		}

		@Override
		public void onTradeCancel() throws RemoteException {
			progressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(PurchaseActivity.this)
							.setTitle("错误").setMessage("取消刷卡")
							.setPositiveButton("确定", null).show();
				}
			});
		}

	};
}
