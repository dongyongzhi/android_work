package com.yfcomm.businesshall;

import java.util.Timer;
import java.util.TimerTask;

import com.yfcomm.businesshall.SwapCard.SwapCardlistener;
import com.yfcomm.mpos.codec.CardModel;
import com.yfcomm.mpos.codec.Print;
import com.yfcomm.mpos.codec.PrintPackage;
import com.yfcomm.mpos.codec.TrxType;
import com.yfcomm.public_define.public_define;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OpencardActivety extends Activity implements SwapCardlistener {

	private TextView tv_phonenum, tv_tcexpenses, plantsel_tc, plantsel_tcinfo, plantsel_tczs, plantsclshowamount;
	private String phonenum, phone_price;
	private LinearLayout llseltc, llreadcard;
	private String planname, plannote, planprice;
	private TextView peoplename, peoplecertno;
	private Button gm_order;
	private boolean isseltc = false, isreadcard = false;
	// private final static String TAG = "OpencardActivety";
	private ProgressDialog progressDialog = null;
	private int acount = 0;
	private MyHandler myhandler;
	private SwapCard swapCard;
	private ImageView im;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plantcsel);

		im= (ImageView) findViewById(R.id.pageupload);
		im.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OpencardActivety.this.finish();
			}
		});
		tv_phonenum = (TextView) findViewById(R.id.plantcsel_phonenum);
		tv_tcexpenses = (TextView) findViewById(R.id.plantcsel_expenses);
		plantsel_tc = (TextView) findViewById(R.id.plantsel_tc);
		plantsel_tcinfo = (TextView) findViewById(R.id.plantsel_tcinfo);
		plantsel_tczs = (TextView) findViewById(R.id.plantsel_tczs);
		plantsclshowamount = (TextView) findViewById(R.id.plantsclshowamount);

		peoplename = (TextView) findViewById(R.id.peoplename);
		peoplecertno = (TextView) findViewById(R.id.peoplecertno);
		myhandler = new MyHandler(this);
		Bundle arguments = getIntent().getBundleExtra(public_define.SelphonenumInfo);

		if (arguments != null) {
			phonenum = arguments.getString(public_define.phonenum, null);
			phone_price = arguments.getString(public_define.price, null);
		}
		tv_phonenum.setText(phonenum);
		tv_tcexpenses.setText("预存话费: " + phone_price + ".0 元");
		init();
		getKeyboardcoordinates();
	}

	@SuppressWarnings("deprecation")
	public void getKeyboardcoordinates() {

		WindowManager wm = this.getWindowManager();
		int height = wm.getDefaultDisplay().getHeight();
		int width = wm.getDefaultDisplay().getWidth();
		/************ 键盘左上角 坐标 (x,y) ***************/
		public_define.Keyboardcoordinates[0] = 0;
		public_define.Keyboardcoordinates[1] = (int) (height - getResources().getDimension(R.dimen.Keybord_allsize));
		/************ 键盘右下角 坐标 (x,y) ***************/
		public_define.Keyboardcoordinates[2] = width;
		public_define.Keyboardcoordinates[3] = height;

		Log.e("TAG", "x=" + public_define.Keyboardcoordinates[0] + "y=" + public_define.Keyboardcoordinates[1] + "x1="
				+ public_define.Keyboardcoordinates[2] + "y2=" + public_define.Keyboardcoordinates[3]);
	}

	public void init() {

		llseltc = (LinearLayout) findViewById(R.id.plantcsel_tc);
		llseltc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setAction("android.intent.action.selectplanActivety");
				startActivityForResult(intent, 0);
			}
		});

		llreadcard = (LinearLayout) findViewById(R.id.plantcsel_readcard);
		llreadcard.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				IdentityAuthentication.isReturn=true;
				Intent intent = new Intent();
				intent.setAction("android.intent.action.IdentityAuthentication");
				startActivityForResult(intent, 0);
			}

		});

		gm_order = (Button) findViewById(R.id.gm_order);
		gm_order.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (isseltc && isreadcard) {
					progressDialog = new ProgressDialog(OpencardActivety.this);
					progressDialog.setMessage("请插入空白IC卡...");
					progressDialog.setCancelable(false);
					progressDialog.setOnCancelListener(new OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							// 取消刷卡
							// mSerialPort.getOutputStream(PackageBuilder.syn(PackageBuilder.CMD_CANCEL).getPackData());
						}
					});
					progressDialog.show();
					swapCard = new SwapCard(OpencardActivety.this);
					swapCard.startswap(120, acount * 100, 0, TrxType.PURCHASE, OpencardActivety.this, true);

				} else {

					Toast.makeText(OpencardActivety.this, "请选择套餐或者读取身份证", Toast.LENGTH_SHORT).show();
				}

			}

		});
	}

	private static class MyHandler extends Handler {
		private OpencardActivety activity;

		MyHandler(OpencardActivety activity) {
			this.activity = activity;
		}

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 1:
				if (activity != null) {
					Toast.makeText(activity, "交易成功", Toast.LENGTH_SHORT).show();
					activity.finish();
				}
				break;
			case 2:
				if (activity != null && activity.progressDialog != null) {
					activity.progressDialog.setMessage("检测到IC卡");
				}
				break;
			case 3:
				if (activity != null && activity.progressDialog != null) {
					activity.progressDialog.setMessage("请输入密码");
				}
				break;

			case 4:
				if (activity != null && activity.progressDialog != null) {
					activity.startPayment();
				}
				break;

			}
		}
	}

	public void startPayment() {
		progressDialog.dismiss();
		Intent intent = new Intent();
		intent.setAction("android.intent.action.com.yfcomm.businesshall.paymentActivety");
		Bundle arguments = new Bundle();
		arguments.putString(public_define.acount, String.valueOf(acount));
		arguments.putString(public_define.phonenum, phonenum);
		arguments.putString(public_define.planname, planname);
		arguments.putString(public_define.plannote, plannote);
		
		arguments.putInt(public_define.Tradetype, 4);
		intent.putExtra(public_define.SelphonenumInfo, arguments);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}

		Bundle args = data.getBundleExtra(public_define.SelphonenumInfo);

		int type = args.getInt(public_define.SevName);
		switch (type) {
		case public_define.selplan:
			isseltc = true;
			planname = args.getString(public_define.planname);
			plannote = args.getString(public_define.plannote);
			planprice = args.getString(public_define.price);
			plantsel_tc.setText(planname);
			plantsel_tcinfo.setText(plannote);
			plantsel_tczs.setText(selectplanActivety.OverPlan);
			acount = Integer.valueOf(planprice) + Integer.valueOf(phone_price);
			plantsclshowamount.setText(String.valueOf(acount) + ".0");
			break;
		case public_define.readcard:
			if (IdentityAuthentication.cardZ != null) {
				peoplename.setText(IdentityAuthentication.cardZ.name);
				peoplecertno.setText(IdentityAuthentication.cardZ.cardNo);
				isreadcard = true;
			}
			break;
		}
	}

	@Override
	public void onDetectIc() {
		progressDialog.setMessage("检测到空白卡，正在写卡请稍后");
	}

	@Override
	public void onInputPin() {
		// progressDialog.setMessage("写入空白卡成功，正在启动支付流程请稍后...");

	}

	@Override
	public void onSwiperSuccess(CardModel cardModel) {
		if (progressDialog != null)
			progressDialog.setMessage("写卡成功...正在启动支付.");
		myhandler.sendEmptyMessageDelayed(4, 2000);
	}

	@Override
	public void onError(int errorCode, String errorMessage) {
		if (progressDialog != null)
			progressDialog.dismiss();
		new AlertDialog.Builder(OpencardActivety.this).setTitle("提示")
				.setMessage("操作失败，返回码：" + errorCode + " 信息:" + errorMessage).setPositiveButton("确定", null).show();
		closeKeybord();
	}

	@Override
	public void onTimeout() {
		if (progressDialog != null)
			progressDialog.setMessage("写卡成功...正在启动支付.");

		new AlertDialog.Builder(this).setTitle("错误").setMessage("刷卡超时").setPositiveButton("确定", null).show();
	}

	@Override
	public void onTradeCancel() {
		if (progressDialog != null)
			progressDialog.dismiss();

		new AlertDialog.Builder(OpencardActivety.this).setTitle("错误").setMessage("取消刷卡").setPositiveButton("确定", null)
				.show();
		closeKeybord();
	}

	@Override
	public void onShowKeyPad(byte[] keyvalue) {
		if (progressDialog != null)
			progressDialog.dismiss();
		startKeybord();
	}

	@Override
	public void OnChangeKeyBord(byte[] body) {

		myhandler.obtainMessage(4, body[0]).sendToTarget();

	}

	@Override
	public void OnCloseKeyBord() {
		closeKeybord();
	}

	private void closeKeybord() {

	}

	private void startKeybord() {
		progressDialog.setMessage("写入空白卡成功");
	}

	@Override
	public void OnPrintOK() {
		myhandler.obtainMessage(1).sendToTarget();
	}

}