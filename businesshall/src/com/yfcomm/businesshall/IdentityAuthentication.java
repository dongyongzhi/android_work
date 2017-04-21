package com.yfcomm.businesshall;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.codehaus.jackson.map.ObjectMapper;

import com.yfcomm.fd.ForwardData;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.pos.SerialPort;
import com.yfcomm.public_define.public_define;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.senter.helper.ConsantHelper;
import cn.com.senter.model.IdentityCardZ;
import cn.com.senter.sdkdefault.mediator.AllCardRead;

public class IdentityAuthentication extends Activity implements SerialPort.SerialPortListener {

	private final static String TAG = "IdentityAuthentication";
	public static IdentityCardZ cardZ = new IdentityCardZ();
	public static boolean isReturn = true;
	private final static int MSG_SERIAL_OPEN_FAIL = 1;
	private final static int MSG_CONNECT_SERIAL = 2;
	private final static int MSG_RCV_TDATA = 3;
	private final static int MSG_RCV_TDATA1 = 4;
	private final static int Bloothclose = 4002;
	private TextView tv_info, nameTextView, sexTextView, folkTextView, birthTextView, addrTextView;
	private TextView codeTextView, policyTextView, validDateTextView, mplaceHolder;
	private ImageView photoView;
	private Button button_COM;
	private String server_address = "senter-online.cn";
	private int server_port = 60002;
	public static Handler uiHandler;
	private AllCardRead cardreader;
	private ForwardData fd = ForwardData.getInstance();
	private boolean readsuc = true; // 读卡状态 1:线程状态开启，发送延迟G机信息 0:线程关闭 ,不发送
	private boolean isRelease = false; // 读线程是否释放
	private SerialPort mSerialPort;
	private boolean isOutLog = false; // 是否打印日志
	private final static int RAED_TIMES=2;
	private int ReadTimes = RAED_TIMES;

	private final static int MAX_LEN = 800;
	private final byte[] packBuf = new byte[MAX_LEN];
	private int offset = 0;
	private int neadlen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.sfcard);
		uiHandler = new MyHandler(this);
		cardreader = new AllCardRead(uiHandler, this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initViews();
		cardreader.setServerAddress(this.server_address);
		cardreader.setServerPort(this.server_port);
		fd.IsPrintLog = false;
	}

	private void initViews() {

		tv_info = (TextView) findViewById(R.id.tv_info);
		nameTextView = (TextView) findViewById(R.id.tv_name);
		sexTextView = (TextView) findViewById(R.id.tv_sex);
		folkTextView = (TextView) findViewById(R.id.tv_ehtnic);
		birthTextView = (TextView) findViewById(R.id.tv_birthday);
		addrTextView = (TextView) findViewById(R.id.tv_address);
		codeTextView = (TextView) findViewById(R.id.tv_number);
		policyTextView = (TextView) findViewById(R.id.tv_signed);
		validDateTextView = (TextView) findViewById(R.id.tv_validate);
		photoView = (ImageView) findViewById(R.id.iv_photo);

		mplaceHolder = (TextView) findViewById(R.id.placeHolder);

		// 屏幕大小
		WindowManager wm = this.getWindowManager();
		@SuppressWarnings("deprecation")
		int height = wm.getDefaultDisplay().getHeight();
		android.view.ViewGroup.LayoutParams p = mplaceHolder.getLayoutParams();
		p.height = height / 6;
		mplaceHolder.setLayoutParams(p);

		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
		android.view.ViewGroup.LayoutParams w = addrTextView.getLayoutParams();
		w.width = (width / 2) - 10;
		addrTextView.setLayoutParams(w);

		tv_info.setTextColor(Color.rgb(240, 65, 85));

		button_COM = (Button) findViewById(R.id.buttonOTG);
		button_COM.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ReadTimes = RAED_TIMES;
				startReadsfCard();
			}
		});
	}

	public void startReadsfCard() {
		button_COM.setEnabled(false);
		resetUI();
		Connect_Comm(); // 串口连接
		cardZ = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*
		 * if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
		 * 
		 * return true; }
		 */
		return super.onKeyDown(keyCode, event);

	}

	public void Connect_Comm() {

		try {
			readsuc = true;
			mSerialPort = public_define.getSerialPort(this, this);
			uiHandler.obtainMessage(MSG_CONNECT_SERIAL).sendToTarget();
			isRelease = false;
			new ReadFromSdkThread(); // 启动接收SDK线程.
			new CardReadTask() // 读身份证
					.executeOnExecutor(Executors.newCachedThreadPool());

		} catch (Exception e) {
			// 打开失败直接返回
			uiHandler.obtainMessage(MSG_SERIAL_OPEN_FAIL).sendToTarget();
			return;
		}

	}

	@Override
	public void OnRcvData(byte[] group_pack, int len) {

		if ((offset + len) > MAX_LEN) {
			offset = 0;
			return;
		}
		System.arraycopy(group_pack, 0, packBuf, offset, len);
		offset += len;
		if (offset < 3)
			return;
		neadlen = (packBuf[5] & 0xFF) * 0x100 + (packBuf[4] & 0xFF) + 9;
		if (neadlen > offset)
			return;
		else if (neadlen < offset) {
			offset = 0;
			return;
		}
		len = offset;
		offset = 0;
		DevicePackage aDevicePackage = new DevicePackage(packBuf);

		byte[] data = aDevicePackage.getBody();

		if (isOutLog && data != null && data.length != 0)
			Log.i(TAG, "接收到串口终端数据:" + ByteUtils.printBytes(data, 0, data.length));
		if (readsuc && data != null && data.length != 0)
			fd.sendtosdkdata(data, data.length);
	}

	private class ReadFromSdkThread extends Thread {

		public ReadFromSdkThread() {
			Thread thread = new Thread(this);
			thread.start();
		}

		@Override
		public void run() {

			byte[] buffer = new byte[1024];
			int rcvlen = 1024, sendlen;
			while (readsuc) {
				if ((sendlen = fd.RcvfromSdkdata(buffer, rcvlen, 1)) > 0) {
					byte[] data = new byte[sendlen];
					System.arraycopy(buffer, 0, data, 0, sendlen);
					if (isOutLog)
						Log.i(TAG, "发送串口终端数据:" + ByteUtils.printBytes(data, 0, data.length));
					mSerialPort.getOutputStream(PackageBuilder.syn(PackageBuilder.CMD_SFXF, data).getPackData());
				} else {// 超时
					isRelease = true;
				}
			}
		}
	}

	private class CardReadTask extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPostExecute(String strCardInfo) {
			super.onPostExecute(strCardInfo);
			readsuc = false;
			if (TextUtils.isEmpty(strCardInfo)) {
				readCardFailed(strCardInfo);
				return;
			}

			if (strCardInfo.length() <= 2) {
				readCardFailed(strCardInfo);
				return;
			}

			ObjectMapper objectMapper = new ObjectMapper();
			IdentityCardZ mIdentityCardZ = new IdentityCardZ();

			try {

				mIdentityCardZ = (IdentityCardZ) objectMapper.readValue(strCardInfo, IdentityCardZ.class);

			} catch (Exception e) {

				readCardFailed(strCardInfo);
				return;
			}

			readCardSuccess(mIdentityCardZ);

			try {

				Bitmap bm = BitmapFactory.decodeByteArray(mIdentityCardZ.avatar, 0, mIdentityCardZ.avatar.length);
				DisplayMetrics dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);

				photoView.setMinimumHeight(dm.heightPixels);
				photoView.setMinimumWidth(dm.widthPixels);
				photoView.setImageBitmap(bm);
				Log.e(ConsantHelper.STAGE_LOG, "图片成功");

			} catch (Exception e) {

				readCardFailed(strCardInfo);
			}

		}

		@Override
		protected String doInBackground(Void... params) {

			String strCardInfo = cardreader.readCard_Sync();
			return strCardInfo;
		}
	};

	public void resetUI() {

		this.nameTextView.setText("");
		this.sexTextView.setText("");
		this.folkTextView.setText("");
		this.birthTextView.setText("");
		this.codeTextView.setText("");
		this.policyTextView.setText("");
		this.addrTextView.setText("");
		this.validDateTextView.setText("");
		this.tv_info.setText("");
		this.photoView.setImageResource(android.R.color.transparent);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Release();
	}

	static class MyHandler extends Handler {
		private IdentityAuthentication activity;

		MyHandler(IdentityAuthentication activity) {
			this.activity = activity;
		}

		@SuppressLint("UseValueOf")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case ConsantHelper.SERVER_CANNOT_CONNECT:
				if (activity != null) {
					activity.tv_info.setText("服务器连接失败! 请检查网络。");
				}
				break;
			case ConsantHelper.READ_CARD_FAILED:
				if (activity != null) {
					activity.tv_info.setText("无法读取信息请重试!");
				}
				break;
			case ConsantHelper.READ_CARD_WARNING:

				String str = (String) msg.obj;
				if (activity != null) {
					if (str.indexOf("card") > -1) {
						activity.tv_info.setText("读卡失败: 卡片丢失,或读取错误!");
					} else {
						String[] datas = str.split(":");
						activity.tv_info.setText("网络超时 错误码: " + Integer.toHexString(new Integer(datas[1])));
					}
				}
				break;

			case ConsantHelper.READ_CARD_PROGRESS:

				int progress_value = (Integer) msg.obj;
				if (activity != null) {
					activity.tv_info.setText("正在读卡......,进度：" + progress_value + "%");
				}
				break;
			case ConsantHelper.READ_CARD_START:
				if (activity != null) {
					activity.resetUI();
					activity.tv_info.setText("开始读卡......");
				}
				break;
			case Bloothclose:

				if (activity != null) {
					activity.Release();
					activity.tv_info.setText("读取成功,可重新读卡");
					if (isReturn)
						activity.sendResylt(cardZ);
				}
				break;

			case Bloothclose + 1:
				if (activity != null) {
					activity.checkIsReapertRead((Boolean) msg.obj);
				}
				break;
			case MSG_CONNECT_SERIAL:
				if (activity != null)
					activity.tv_info.setText("串口打开成功");
				break;
			case MSG_SERIAL_OPEN_FAIL:
				if (activity != null)
					activity.tv_info.setText("串口打开失败");
				break;
			case MSG_RCV_TDATA:
				if (activity != null)
					activity.tv_info.setText("收到终端数据");
				break;

			case MSG_RCV_TDATA1:
				byte[] data = (byte[]) msg.obj;
				if (activity != null)
					activity.tv_info.setText(ByteUtils.printBytes(data, 0, data.length));
				break;

			}
		}
	}

	private void checkIsReapertRead(final boolean isOK) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isRelease) {
					timer.cancel();
					if (isOK)
						uiHandler.sendEmptyMessage(Bloothclose);
					else {
						if (ReadTimes > 0) {
							uiHandler.post(new Runnable() {

								@Override
								public void run() {
									Release();
									startReadsfCard();
									ReadTimes--;
								}
							});
						} else {
							uiHandler.post(new Runnable() {
								@Override
								public void run() {
									Release();
								} 
							});
						}
					}
				}
			}
		}, 500, 500);
	}

	private void Release() {
		button_COM.setEnabled(true);
		byte[] data = PackageBuilder.syn(PackageBuilder.CMD_CANCEL, null).getPackData();
		if (data != null && mSerialPort != null) {
			mSerialPort.getOutputStream(data);
			Log.i(TAG, "发送串口终端数据:" + ByteUtils.printBytes(data, 0, data.length));
		}
	}

	private void readCardFailed(String strcardinfo) {
		int bret = Integer.parseInt(strcardinfo);
		switch (bret) {
		case -1:
			tv_info.setText("服务器连接失败,,请重试!");
			break;
		case 1:
			tv_info.setText("读卡失败,请重试!");
			break;
		case 2:
			tv_info.setText("读卡失败,请重试!");
			break;
		case 3:
			tv_info.setText("网络超时,请重试!");
			break;
		case 4:
			tv_info.setText("读卡失败,请重试");
			break;
		case -2:
			tv_info.setText("读卡失败!");
			break;
		case 5:
			tv_info.setText("照片解码失败,请重试!");
			break;
		}
		if (isOutLog)
			Log.e(TAG, "readCardFailed=" + bret);
		uiHandler.obtainMessage(Bloothclose + 1, false).sendToTarget();
	}

	private void readCardSuccess(IdentityCardZ identityCard) {

		if (identityCard != null) {

			nameTextView.setText(identityCard.name);
			sexTextView.setText(identityCard.sex);
			folkTextView.setText(identityCard.ethnicity);
			birthTextView.setText(identityCard.birth);
			codeTextView.setText(identityCard.cardNo);
			policyTextView.setText(identityCard.authority);
			addrTextView.setText(identityCard.address);
			validDateTextView.setText(identityCard.period);
		}
		if (isOutLog)
			Log.e(ConsantHelper.STAGE_LOG, "读卡成功!");
		if (isReturn)
			tv_info.setText("正在退出,请稍后...");
		cardZ = identityCard;
		uiHandler.obtainMessage(Bloothclose + 1, true).sendToTarget();
	}

	public void sendResylt(IdentityCardZ identityCard) {

		Intent mIntent = new Intent();
		Bundle arguments = new Bundle();
		arguments.putInt(public_define.SevName, public_define.readcard);
		mIntent.putExtra(public_define.SelphonenumInfo, arguments);
		setResult(RESULT_OK, mIntent);
		if (isReturn)
			this.finish();
	}

}
