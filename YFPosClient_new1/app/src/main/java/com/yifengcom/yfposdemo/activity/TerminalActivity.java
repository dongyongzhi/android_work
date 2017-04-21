package com.yifengcom.yfposdemo.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jarjar.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import com.ctsi.idcertification.CloudReaderClient;
import com.ctsi.idcertification.constant.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yifengcom.yfposdemo.Des33;
import com.yifengcom.yfposdemo.IDCard;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

/**
 * @Description: 终端协议
 */
public class TerminalActivity extends BaseActivity implements OnClickListener {

	public final static int FILE_SELECT_CODE = 0;
	private ProgressDialog progressDialog;
	public static final String TAG = "TerminalActivity";

	private final static String ISREADIDENTITY = "isReadInden";

	private ExecutorService pool = Executors.newCachedThreadPool();
	private NfcAdapter nfcAdapter;
	private CloudReaderClient reader;
	private boolean isComplete = false;
	String appSecret_3des = "D34AE719CE3246E40729411452759F86D34AE719CE3246E4"; // appId对应的加密密钥
	String appSecret = "30b5c231a8ea42c09c87f75d22ebc9ea"; // appId对应的加密密钥
	String appId = "1035";
	String timestamp = "";
	String nonce = "jfoiiuylkjljpohi";
	String businessExt = "{\"busiSerial\":\"12345\",\"staffCode\":\"110011\","
			+ "\"channelCode\":\"2001\",\"areaCode\":\"020\",\"teminalType\":\"PC\","
			+ "\"srcSystem\":\"CRM\",\"osType\":\"\",\"browserModel\":\"\","
			+ "\"clientIP\":\"\",\"deviceModel\":\"\",\"deviceSerial\":\"\"}";
	StringBuffer sbData = new StringBuffer();
	String signature = "";
	private String ErrcardReadInfo;
	
	
	NfcAdapter.ReaderCallback nfcCallBack;
	private Handler mhandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terminal_main);
		setTitleName("终端协议");
		initView();
		/*
		new Thread() {
			public void run() {
				doListen();
			};
		}.start();*/
		mhandler = handler;
	}

	private void initView() {
		this.findViewById(R.id.btnDeviceInfo).setOnClickListener(this); // 获取设备版本
		this.findViewById(R.id.btnCache).setOnClickListener(this); // 缓存数据
		this.findViewById(R.id.btnClearCache).setOnClickListener(this); // 清除缓存
		this.findViewById(R.id.btnSetData).setOnClickListener(this); // 设置数据
		this.findViewById(R.id.btnReadData).setOnClickListener(this); // 读取数据
		this.findViewById(R.id.btnReset).setOnClickListener(this); // 复位操作
		this.findViewById(R.id.btnGetTime).setOnClickListener(this); // 获取时间
		this.findViewById(R.id.btnSetTime).setOnClickListener(this); // 设置时间
		this.findViewById(R.id.btnPrint).setOnClickListener(this); // 打印数据
		this.findViewById(R.id.btnUpdate).setOnClickListener(this); // 升级固件
		this.findViewById(R.id.btnAutoUpdate).setOnClickListener(this); // 自动升级固件
		this.findViewById(R.id.btnCancel).setOnClickListener(this); // 取消

		this.findViewById(R.id.btnWriteMainKey).setOnClickListener(this); // 更新主密钥
		this.findViewById(R.id.btnWriteKey).setOnClickListener(this); // 更新工作密钥
		this.findViewById(R.id.btnCalMac).setOnClickListener(this); // 验证MAC

		this.findViewById(R.id.btnRFCard).setOnClickListener(this); // 射频卡测试
		this.findViewById(R.id.btnIDCard).setOnClickListener(this); // 身份证读卡
		this.findViewById(R.id.btnPSAM).setOnClickListener(this); // PSAM卡测试

		this.findViewById(R.id.btnSwiping).setOnClickListener(this); // 刷卡
		this.findViewById(R.id.btnSwipingAndPrint).setOnClickListener(this); // 刷卡+打印
		this.findViewById(R.id.btnReadMoney).setOnClickListener(this); // 读取电子现金余额

		this.findViewById(R.id.btnHft).setOnClickListener(this); // 测试调用惠付通

		this.findViewById(R.id.btnSetRFID).setOnClickListener(this); // 射频卡操作
		this.findViewById(R.id.btnSetPSAM).setOnClickListener(this); // PSAM卡操作
		this.findViewById(R.id.btnSetIC).setOnClickListener(this); // IC卡操作

		this.findViewById(R.id.btnTestDestroy).setOnClickListener(this); // 测试
	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage) throws RemoteException {
			progressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(TerminalActivity.this).setTitle("提示")
							.setMessage("更新失败，返回码：" + code + " 信息:" + messsage).setPositiveButton("确定", null).show();
				}
			});

		}


		@Override
		public void onDownloadProgress(long current, long total) throws RemoteException {
			progressDialog.setProgress((int) Math.rint((current * 1.0 / total) * 100));
		}

		@Override
		public void onResultSuccess(final int nType) throws RemoteException {
			progressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					if (nType == 0x30) {
						// 连接刷卡器成功
						Toast.makeText(TerminalActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
						// swiper.getDeviceInfo();
					} else if (nType == 0x39) {
						// 固件更新成功
						Toast.makeText(TerminalActivity.this, "固件更新成功", Toast.LENGTH_SHORT).show();
					}
				}
			});

		}

		@Override
		public void onTradeCancel() throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(TerminalActivity.this).setTitle("错误").setMessage("取消升级")
							.setPositiveButton("确定", null).show();
				}
			});

		}

		@Override
		public void onTimeout() throws RemoteException {
			progressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(TerminalActivity.this).setTitle("错误").setMessage("更新超时")
							.setPositiveButton("确定", null).show();
				}
			});

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDeviceInfo:
			startActivity(DeviceInfoActivity.class);
			break;
		case R.id.btnCache:
			break;
		case R.id.btnClearCache:
			break;
		case R.id.btnSetData:
			startActivity(SetDataActivity.class);
			break;
		case R.id.btnReadData:
			break;
		case R.id.btnReset:
			// this.swiper.restart();
			break;
		case R.id.btnGetTime:
			startActivity(GetTimeActivity.class);
			break;
		case R.id.btnSetTime:
			startActivity(SetTimeActivity.class);
			break;
		case R.id.btnPrint:
			startActivity(PrintActivity.class);
			break;
		case R.id.btnUpdate:
			// 选择升级文件
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);

			try {
				startActivityForResult(Intent.createChooser(intent, "选择要更新的程序"), FILE_SELECT_CODE);
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btnAutoUpdate:
			intent = new Intent();
			intent.putExtra("K81OTAFile", "/storage/emulated/0/JKT.bin");
			intent.setAction("android.intent.action.yfcomm.K81.OTAUpdate");
			sendBroadcast(intent);
			break;
		case R.id.btnCancel:
			break;
		case R.id.btnWriteMainKey:
			startActivity(WriteMainKeyActivity.class);
			break;
		case R.id.btnWriteKey:
			startActivity(WriteKeyActivity.class);
			break;
		case R.id.btnCalMac:
			startActivity(CalMacActivity.class);
			break;
		case R.id.btnSwiping:
			startActivity(PurchaseActivity.class);
			break;
		case R.id.btnSwipingAndPrint:
			startActivity(PurchasePrintActivity.class);
			break;
		case R.id.btnReadMoney:
			startActivity(ReadMoneyActivity.class);
			break;
		case R.id.btnRFCard:
			startActivity(RFCardActivity.class);
			break;
		case R.id.btnIDCard:
			// YFApp.getApp().unBind();
			YFApp.getApp().SetIdentityActivity(this);
			startActivity(IdentityAuthentication.class);
			break;
		case R.id.btnHft:
			startActivity(TestActivity.class);
			break;
		case R.id.btnPSAM:
			startActivity(PSAMActivity.class);
			break;
		case R.id.btnSetRFID:
			startActivity(SetRFIDActivity.class);
			break;
		case R.id.btnSetPSAM:
			startActivity(SetPSAMActivity.class);
			break;
		case R.id.btnSetIC:
			startActivity(SetICActivity.class);
			break;
		case R.id.btnTestDestroy:
			// startActivity(TestDestroyActivity.class);//请勿测试存在风险
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case FILE_SELECT_CODE:
			Uri uri = data.getData();
			String path = uri.getPath();
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setTitle("正在更新固件");
			progressDialog.setCancelable(true);
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					try {
						YFApp.getApp().iService.cancel();
					} catch (RemoteException e) {
						e.printStackTrace();
						showToast("接口访问错误");
						closeDialog();
					}
				}
			});
			progressDialog.setMax(100);
			progressDialog.show();

			try {
				YFApp.getApp().iService.updateFirmware(path, mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				showToast("接口访问错误");
				closeDialog();
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		YFApp.getApp().unBind();
		System.exit(0);
	}

	@SuppressLint("NewApi")
	public void ReadNfcIdentityAuthentication(final Context mContext, final Handler handler) {
		this.mhandler = handler;
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		reader = new CloudReaderClient(this);
		try {

			nfcCallBack = new NfcAdapter.ReaderCallback() {
				@Override
				public void onTagDiscovered(final Tag tag) {
					if (!isComplete) {
						if (tag != null) {
							nfcAdapter.disableForegroundDispatch(TerminalActivity.this);
						}

						pool.execute(new Runnable() {
							@Override
							public void run() {
								signature = getSignature();
								Map resultMap = reader.CloudReadCert(appId, timestamp, nonce, businessExt, signature,
										Constant.READER_TYPE_NFC, tag);

								if (resultMap != null) {
									if (!isComplete && mhandler != null)
										mhandler.obtainMessage(0, resultMap).sendToTarget();
								}
							}
						});
					}
				}
			};
		} catch (NoClassDefFoundError e) {

		}
		if (nfcCallBack != null) {
			isComplete = false;
			reader.connect(Constant.READER_TYPE_NFC, nfcCallBack);
		}
	}

	private String getSignature() {
		// TODO Auto-generated method stub
		timestamp = System.currentTimeMillis() + "";
		Log.i("timestamp", timestamp);
		sbData = new StringBuffer();
		sbData.append(appId).append(appSecret).append(businessExt).append(nonce).append(timestamp);
		return DigestUtils.shaHex(sbData.toString());
	}

	public void readComplete() {
		isComplete = true;
		mhandler = null;
	}

	public static class sysBroadcastReceiver extends BroadcastReceiver {

		private static final String START_ACTION = "com.yifengcom.readcard";

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if (START_ACTION.equalsIgnoreCase(action)) {

				Intent newIntent = new Intent(context, TerminalActivity.class);
				// newIntent.putExtra(ISREADIDENTITY, true);
				newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(newIntent);
			}
		}
	}

	private void doListen() {

		final int SERVER_PORT = 10086;
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			Log.e(TAG, "socket listen started");
			while (!isDestroyed()) {
				Socket socket = serverSocket.accept();
				Log.e(TAG, "accept socket =" + socket);
				new Thread(new ThreadRWSocket(socket)).start();
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}
	}

	private String content_decryp = null;
	private Object lock = new Object();
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	private  void  sendErrMsgtoServer(String paramString, BufferedOutputStream out, InputStream in){
		try {
			out.write(paramString.getBytes("gb2312"), 0,paramString.getBytes("gb2312").length);
			out.flush();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void sendMsgtoServer(String paramString, BufferedOutputStream out, InputStream in) {

		JSONObject jsonObj = null;
		String content = null;

		try {

			jsonObj = XML.toJSONObject(paramString);
			content = jsonObj.getString("certificate");

			Gson builder = new Gson();
			IDCard idcard = builder.fromJson(content, IDCard.class);

			Bitmap pic = StringToPic(idcard.identityPic);
			baos.reset();
			pic.compress(Bitmap.CompressFormat.JPEG, 100, baos);

			byte[] bytes = baos.toByteArray();

			StringBuilder sb = new StringBuilder();
			sb.append("{").append("\"partyName\"" + ":").append("\"" + idcard.partyName + "\",")
					.append("\"gender\"" + ":").append("\"" + (idcard.gender.equals("0") ? "女" : "男") + "\",")
					.append("\"nation\"" + ":").append("\"" + idcard.nation + "\",").append("\"bornDay\"" + ":")
					.append("\"" + idcard.bornDay + "\",").append("\"certAddress\"" + ":")
					.append("\"" + idcard.certAddress + "\",").append("\"certNumber\"" + ":")
					.append("\"" + idcard.certNumber + "\",").append("\"certOrg\"" + ":")
					.append("\"" + idcard.certOrg + "\",").append("\"effDate\"" + ":")
					.append("\"" + idcard.effDate + "\",").append("\"expDate\"" + ":")
					.append("\"" + idcard.expDate + "\",").append("\"identityPic\"" + ":")
					.append("\"" + String.valueOf(bytes.length) + "\"").append("}");

			out.write(sb.toString().getBytes("gb2312"), 0, sb.toString().getBytes("gb2312").length);
			out.flush();

			byte[] tempbuffer = new byte[50];
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			String msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

			if (msg.equals("Rsp_OK")) {
				out.write(bytes, 0, bytes.length);
				out.flush();
			}

		} catch (JsonSyntaxException e) {
			Log.e(TAG, e.getMessage().toString());
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage().toString());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage().toString());
		}

		// Bitmap pic = StringToPic(idcard.identityPic);

	}

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E',
			'F' };

	public static String bytesString(byte[] raw, int offset, int count) {
		if (raw == null) {
			return " waring data is null!";
		}
		final StringBuilder hex = new StringBuilder();
		int len = raw.length;

		offset = offset > (len - 1) ? len - 1 : offset;

		int end = offset + count;
		end = end > len ? len : end;
		for (int i = offset; i < end; i++) {
			byte b = raw[i];
			hex.append(HEX[(b & 0xF0) >> 4]).append(HEX[b & 0x0F]);
		}
		return hex.toString();
	}

	// Bitmap对象保存味图片文件
	public void saveBitmapFile(Bitmap bitmap) {
		File file = new File("/mnt/sdcard/Pictures/dyz01.jpg");// 将要保存图片的路径
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Bitmap StringToPic(String picString) {
		{
			try {
				byte[] arrayOfByte = Base64.decode(picString, 0);
				Bitmap localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
				return localBitmap;
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			switch (msg.what) {
			case 0:
				@SuppressWarnings("rawtypes")
				Map resultMap = (Map) msg.obj;
				int resultCode = Integer.valueOf(resultMap.get(Constant.RESULT_MAP_KEY_FLAG).toString());
				if (resultCode == Constant.RESULT_OK) {
					String content = (String) resultMap.get(Constant.RESULT_MAP_KEY_CONTENT);
					try {
						content_decryp = Des33.decode1(content, appSecret_3des);
						synchronized (lock) {
							lock.notifyAll();
						}
					} catch (Exception e) {
						Log.e(TAG, e.getMessage().toString());
					}
				} else {
					ErrcardReadInfo=(String) resultMap.get(Constant.RESULT_MAP_KEY_ERRORMESSAGE);
					Log.e(TAG,
							resultMap.get(Constant.STEP_MAP_KEY_FLAG) + "**********" + "resultFlag:"
									+ String.valueOf(resultCode) + "\n" + "errorMsg: "
									+ ErrcardReadInfo);
					synchronized (lock) {
						lock.notifyAll();
					}
				}
				break;

			case 1:
				PC_ReadCard();
				break;
			}

		}

	};

	private class ThreadRWSocket implements Runnable {

		private Socket client;
		private BufferedOutputStream out;
		private BufferedInputStream in;

		public ThreadRWSocket(Socket client) {
			this.client = client;
			try {
				out = new BufferedOutputStream(client.getOutputStream());
				in = new BufferedInputStream(client.getInputStream());
			} catch (IOException e) {
			}
		}

		@Override
		public void run() {
			while (client.isConnected()) {
				try {
					String currCMD = "";
					currCMD = readCMDFromSocket(in);
					if (currCMD.equals("yf_test_readcard")) {
						content_decryp = null;
						handler.obtainMessage(1).sendToTarget();
						synchronized (lock) {
							lock.wait(30000);
							readComplete();
							if (content_decryp != null) {
								sendMsgtoServer(content_decryp, out, in);
							}else{
								sendErrMsgtoServer(ErrcardReadInfo,out,in);
							}
						}
					} 
				} catch (Exception e) {
					Log.e(TAG, e.getMessage().toString());

				}
			}
			try {  
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (client != null) {
					client.close();
				}
			} catch (IOException e) {

			}

		}

		/* 读取命令 */
		private String readCMDFromSocket(InputStream in) {
			int MAX_BUFFER_BYTES = 2048;
			String msg = "";
			byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
			try {
				int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
				msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
				tempbuffer = null;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage().toString());
			}
			return msg;
		}
	}

	private void PC_ReadCard() {
		ReadNfcIdentityAuthentication(this, handler);
	}

}
