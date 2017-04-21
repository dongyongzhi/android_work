package com.yfcomm.businesshall;

import java.text.SimpleDateFormat;

import com.yfcomm.businesshall.SwapCard.SwapCardlistener;
import com.yfcomm.mpos.codec.CardModel;
import com.yfcomm.mpos.codec.Print;
import com.yfcomm.mpos.codec.PrintPackage;
import com.yfcomm.mpos.codec.TrxType;
import com.yfcomm.public_define.public_define;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class paymentActivety extends Activity implements SwapCardlistener {

	private String acount, phonenum, planname, plannote;
	private TextView[] textviews = new TextView[5];
	private int[] ID = { R.id.paytype1, R.id.paytype2, R.id.paytype3, R.id.paytype4, R.id.paytype5 };
	private ProgressDialog progressDialog;
	private SwapCard swapCard;
	private MyHandler myhandler;
	private DigitPasswordKeyPad dpk;
	private View passwdview;
	private WindowManager windowmanager;
	private boolean WindowIsclosed = true;
	private final static String TAG = "paymentActivety";
	private int tradetype;
	private String Cardissuingbank;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		myhandler = new MyHandler(this);
		Bundle arguments = getIntent().getBundleExtra(public_define.SelphonenumInfo);
		init();
		if (arguments != null) {
			acount = arguments.getString(public_define.acount, null);
			phonenum = arguments.getString(public_define.phonenum, null);

			planname = arguments.getString(public_define.planname, null);
			plannote = arguments.getString(public_define.plannote, null);
			Cardissuingbank = arguments.getString(public_define.Cardissuingbank, null);
			tradetype = arguments.getInt(public_define.Tradetype);
			textviews[3].setText(acount + ".0");
		}

	}

	public void SelpaytypePrompt() {
		CustomDialog dialog;
		CustomDialog.Builder customBuilder = new CustomDialog.Builder(this);
		customBuilder.setTitle("操作提示").setMessage("●" + "暂未开通\r\n").setPositiveButton("确定",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

					}
				});
		dialog = customBuilder.create(R.drawable.prompt);
		dialog.setCancelable(false);
		dialog.show();
	}

	public void init_keybord() {
		dpk = new DigitPasswordKeyPad(this);
		passwdview = dpk.setup();
	}

	public void init() {

		getKeyboardcoordinates();
		init_keybord();
		for (int i = 0; i < textviews.length; i++) {
			textviews[i] = (TextView) findViewById(ID[i]);
			textviews[i].setId(i);
			textviews[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int id = v.getId();
					switch (id) {
					case 0:
					case 1:
					case 2:
						SelpaytypePrompt();
						break;
					case 3:
						break;
					case 4:
						sendpay();
						break;
					default:
						break;
					}

				}
			});
		}

	}

	public void sendpay() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在刷卡...");
		progressDialog.setCancelable(false);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

			}
		});
		progressDialog.show();
		swapCard = new SwapCard(this);
		int money = Integer.valueOf(acount);
		swapCard.startswap(120, money * 100, 0, TrxType.PURCHASE, this, false);
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

	private static class MyHandler extends Handler {
		private paymentActivety activity;

		MyHandler(paymentActivety activity) {
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
				if (activity != null) {
					activity.dpk.setPassText((int) Integer.valueOf(msg.obj.toString()));
				}
				break;

			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public byte[] getPrintBody(String pan) {
		byte[] sendbuf = new byte[1024];
		try {
			Print printinfor = new Print();
			printinfor.PRINT_clear();

			// printinfor.PRINT_Add_picture((byte) 100, (byte) 1);

			printinfor.PRINT_Add_setp((short) (100));

			String print0 = "商户名称：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print0.getBytes("gb2312"),
					(short) (print0.getBytes("gb2312").length));

			String print1 = "江苏怡丰测试专用商户:";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_48X24, print1.getBytes("gb2312"),
					(short) (print1.getBytes("gb2312").length));

			String print2 = "商户编号:801389670704798";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print2.getBytes("gb2312"),
					(short) (print2.getBytes("gb2312").length));

			String print3 = "终端编号:20140923";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print3.getBytes("gb2312"),
					(short) (print3.getBytes("gb2312").length));

			String print4 = "操作员:01";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print4.getBytes("gb2312"),
					(short) (print4.getBytes("gb2312").length));

			String print5 = "--------------------------------";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print5.getBytes("gb2312"),
					(short) (print5.getBytes("gb2312").length));

			String print6, print7;

			if (tradetype == 1) {
				print6 = "交易类型: " + "话费充值";
				print7 = "充值手机号：" + phonenum;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print6.getBytes("gb2312"),
						(short) (print6.getBytes("gb2312").length));

				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print7.getBytes("gb2312"),
						(short) (print7.getBytes("gb2312").length));
			} else if (tradetype == 2) {
				print6 = "交易类型: " + "手机缴费";
				print7 = "缴费手机号：" + phonenum;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print6.getBytes("gb2312"),
						(short) (print6.getBytes("gb2312").length));

				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print7.getBytes("gb2312"),
						(short) (print7.getBytes("gb2312").length));
			} else if (tradetype == 3) {
				print6 = "交易类型: " + "固话充值";
				print7 = "固话号码：" + phonenum;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print6.getBytes("gb2312"),
						(short) (print6.getBytes("gb2312").length));

				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print7.getBytes("gb2312"),
						(short) (print7.getBytes("gb2312").length));

			} else if (tradetype == 4) {

				print6 = "姓名: " + IdentityAuthentication.cardZ.name;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print6.getBytes("gb2312"),
						(short) (print6.getBytes("gb2312").length));

				print7 = "身份证号：" + IdentityAuthentication.cardZ.cardNo;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print7.getBytes("gb2312"),
						(short) (print7.getBytes("gb2312").length));

				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print5.getBytes("gb2312"),
						(short) (print5.getBytes("gb2312").length));

				String print9 = "已选手机号：" + phonenum;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print9.getBytes("gb2312"),
						(short) (print9.getBytes("gb2312").length));

				String print10 = "已选套餐: " + planname;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print10.getBytes("gb2312"),
						(short) (print10.getBytes("gb2312").length));

				String print11 = "套餐详情: " + plannote;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print11.getBytes("gb2312"),
						(short) (print11.getBytes("gb2312").length));

				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print5.getBytes("gb2312"),
						(short) (print5.getBytes("gb2312").length));

			} else if (tradetype == 5 || tradetype == 6) {
				
				if (tradetype == 5)
					print6 = "交易类型: " + "开卡";
				else
					print6 = "交易类型: " + "充值";
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print6.getBytes("gb2312"),
						(short) (print6.getBytes("gb2312").length));

				print6 = "姓名: " + IdentityAuthentication.cardZ.name;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print6.getBytes("gb2312"),
						(short) (print6.getBytes("gb2312").length));

				print7 = "身份证号：" + IdentityAuthentication.cardZ.cardNo;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print7.getBytes("gb2312"),
						(short) (print7.getBytes("gb2312").length));

				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print5.getBytes("gb2312"),
						(short) (print5.getBytes("gb2312").length));

				String print9 = "发卡单位：" + Cardissuingbank;
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print9.getBytes("gb2312"),
						(short) (print9.getBytes("gb2312").length));
			}

			String paninfo = "支付卡号：" + pan;
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, paninfo.getBytes("gb2312"),
					(short) (paninfo.getBytes("gb2312").length));

			String print16 = "金 额: ";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print16.getBytes("gb2312"),
					(short) (print16.getBytes("gb2312").length));

			String print17 = "RMB " + String.valueOf(acount) + ".0元";
			printinfor.PRINT_Add_character((byte) 10, Print.PNT_48X24, print17.getBytes("gb2312"),
					(short) (print17.getBytes("gb2312").length));

			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print5.getBytes("gb2312"),
					(short) (print5.getBytes("gb2312").length));

			String print12 = "批 次 号：000020";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print12.getBytes("gb2312"),
					(short) (print12.getBytes("gb2312").length));

			String print15 = "参 考 号：268381955837";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print15.getBytes("gb2312"),
					(short) (print15.getBytes("gb2312").length));

			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			String print14 = "日期/时间：" + date;
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24, print14.getBytes("gb2312"),
					(short) (print14.getBytes("gb2312").length));

			String print18 = "备注：";
			printinfor.PRINT_Add_character((byte) 0, Print.PNT_16X16, print18.getBytes("gb2312"),
					(short) (print18.getBytes("gb2312").length));

			printinfor.PRINT_Add_setp((short) (50));

			short sendLen = printinfor.PRINT_packages(sendbuf);
			byte[] sendbuf1 = new byte[sendLen];

			System.arraycopy(sendbuf, 0, sendbuf1, 0, sendLen);
			PrintPackage package1 = new PrintPackage(sendbuf1);
			return package1.getPackData();// PackageBuilder.syn(PackageBuilder.CMD_PRINT,
											// package1.getPackData()).getPackData();

		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public void onDetectIc() {
		progressDialog.setMessage("检测到IC卡");
	}

	@Override
	public void onInputPin() {
		progressDialog.setMessage("请输入密码");

	}

	@Override
	public void onSwiperSuccess(CardModel cardModel) {
		progressDialog.dismiss();
		closeKeybord();
		// 数据加密成功返回
		StringBuilder sb = new StringBuilder();
		sb.append("pan:").append(cardModel.getPan()).append("\n");
		sb.append("expireDate:").append(cardModel.getExpireDate()).append("\n");

		sb.append("batchNo:").append(cardModel.getBatchNo()).append("\n");
		sb.append("serialNo:").append(cardModel.getSerialNo()).append("\n");
		sb.append("track2:").append(cardModel.getTrack2()).append("\n");
		sb.append("track3:").append(cardModel.getTrack3()).append("\n");
		sb.append("encryTrack2:").append(cardModel.getEncryTrack2()).append("\n");
		sb.append("encryTrack3:").append(cardModel.getEncryTrack3()).append("\n");

		sb.append("icData:").append(cardModel.getIcData()).append("\n");

		sb.append("mac:").append(cardModel.getMac()).append("\n");
		sb.append("pinBlock:").append(cardModel.getPinBlock()).append("\n");

		sb.append("icseq:").append(cardModel.getIcSeq()).append("\n");
		sb.append("random:").append(cardModel.getRandom()).append("\n");

		byte[] sendbuf = getPrintBody(cardModel.getPan());
		if (swapCard != null)
			swapCard.sendPrint(sendbuf, sendbuf.length);

		Log.e(TAG, "pay sucess：\n" + sb.toString());
	}

	@Override
	public void onError(int errorCode, String errorMessage) {
		if (progressDialog != null)
			progressDialog.dismiss();
		new AlertDialog.Builder(this).setTitle("提示").setMessage("操作失败，返回码：" + errorCode + " 信息:" + errorMessage)
				.setPositiveButton("确定", null).show();
		closeKeybord();
	}

	@Override
	public void onTimeout() {
		if (progressDialog != null)
			progressDialog.dismiss();
		new AlertDialog.Builder(this).setTitle("错误").setMessage("刷卡超时").setPositiveButton("确定", null).show();
		closeKeybord();
	}

	@Override
	public void onTradeCancel() {
		if (progressDialog != null)
			progressDialog.dismiss();

		new AlertDialog.Builder(this).setTitle("错误").setMessage("取消刷卡").setPositiveButton("确定", null).show();
		closeKeybord();
	}

	@Override
	public void onShowKeyPad(byte[] keyvalue) {
		if (progressDialog != null)
			progressDialog.dismiss();
		dpk.setKeyValue(keyvalue);
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
		if (windowmanager != null && passwdview != null && WindowIsclosed) {
			windowmanager.removeView(passwdview);
			WindowIsclosed = false;
		}
	}

	private void startKeybord() {

		if (windowmanager == null) {
			windowmanager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
			LayoutParams layoutparams = new LayoutParams(-1, -1, WindowManager.LayoutParams.FIRST_SUB_WINDOW,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888);
			layoutparams.gravity = Gravity.BOTTOM;

			passwdview.findViewById(R.id.transpwdpdpanel).getBackground().setAlpha(140);
			windowmanager.addView(passwdview, layoutparams);
		} else {

			LayoutParams layoutparams = new LayoutParams(-1, -1, WindowManager.LayoutParams.FIRST_SUB_WINDOW,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.RGBA_8888);
			layoutparams.gravity = Gravity.BOTTOM;
			windowmanager.addView(passwdview, layoutparams);
		}
		WindowIsclosed = true;
		Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void OnPrintOK() {
		myhandler.obtainMessage(1).sendToTarget();
	}
}