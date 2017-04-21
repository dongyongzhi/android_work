package com.yifengcom.yfpos.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import org.json.JSONException;
import org.json.JSONObject;

import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.YFPosApp;
import com.yifengcom.yfpos.api.SimpleSwiperListener;
import com.yifengcom.yfpos.api.SwiperController;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.listener.CallBackListener;
import com.yifengcom.yfpos.listener.ReaderListeners.GetDateTimeListener;
import com.yifengcom.yfpos.model.ack.Display;
import com.yifengcom.yfpos.utils.CustomDialog;
import com.yifengcom.yfpos.utils.WindowUtils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

public class YFPosService extends Service {
	public final static String BROADTCAST_ACTION_STRING_DESTROY = "com.yifengcom.yfpos.destroy";
	public final static String BROADTCAST_ACTION_STRING_k81UPDATE = "android.intent.action.yfcomm.K81.OTAUpdate";

	private YFLog log = YFLog.getLog(YFPosService.class);
	private final static int CODE_SHOWPINPAD = 10001;
	private SwiperController swiper;
	private ICallBack mCallBack = null;

	CountDownLatch countDownLatch;

	// 屏幕送显
	private Dialog dialog;
	private Point point;
	private Timer timer;
	private TimerTask task;
	private WindowManager wm;

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			try {
				if (msg.what == CODE_SHOWPINPAD) {
					WindowUtils.showPopupWindow(getApplicationContext(), (byte[]) msg.obj);
				} else if (msg.what == PackageBuilder.CMD_CURRENT_KEY) {
					// 当前按键
					int cmd = (Integer) msg.obj;
					WindowUtils.setPwd(cmd);
				} else if (msg.what == PackageBuilder.CMD_DESTROY) {
					// 自毁
					closeDialog();

					byte[] body = (byte[]) msg.obj;
					int result = body[0] & 0xFF;
					String sn = new String(body, 1, 16);

					showSysDialog(0, 0, Gravity.CENTER, "本机已自我保护，类型：00" + result);

					JSONObject obj = new JSONObject();
					try {
						obj.put("destroy", "00" + result);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					// 发送全局自毁广播
					Intent intent = new Intent();
					intent.setAction(BROADTCAST_ACTION_STRING_DESTROY);
					sendBroadcast(intent);

					log.d("自毁上报");
					upload(sn, obj.toString());

				} else if (msg.what == PackageBuilder.CMD_DISPLAY) {
					// 屏幕送显
					closeDialog();

					Display display = (Display) msg.obj;
					int height = (int) (point.y * 0.6);
					showSysDialog(0, height, display.getType(), display.getContent());

					if (display.getTime() != 0xFF && display.getTime() >= 0) {
						timer = new Timer();
						task = new TimerTask() {
							@Override
							public void run() {
								closeDialog();
							}
						};
						timer.schedule(task, display.getTime() * 1000);
					}
				} else if (msg.what == PackageBuilder.CMD_EMV_TEST_RETURN) {
					byte[] body = (byte[]) msg.obj;
					try {
						mCallBack.onEMVTestOK(body);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				Log.e("YFPosService", e.getMessage());
			}
		}

		;

	};

	/**
	 * 弹出系统提示框
	 *
	 * @param width
	 * @param height
	 * @param type
	 * @param msg
	 */
	private void showSysDialog(int width, int height, int type, String msg) {
		width = (width == 0 ? (int) (point.x * 0.85) : width);
		height = (height == 0 ? (int) (point.y * 0.45) : height);
		dialog = CustomDialog.createDialog(getApplicationContext(), width, height, type, msg);
		dialog.show();
	}

	/**
	 * 开机、自毁上报
	 *
	 * @param sn  SN号
	 * @param msg 软件版本号（目前就一个）
	 */
	private void upload(final String sn, final String msg) {
		new Thread() {
			@Override
			public void run() {
				HttpURLConnection conn = null;
				InputStream input = null;
				String strUrl = "http://" + Build.HOST + "/yf.php?op=10&id=" + sn + "&msg=" + msg;
				try {
					URL url = new URL(strUrl);
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setConnectTimeout(10000);
					conn.setReadTimeout(10000);
					conn.connect();
					input = conn.getInputStream();
					if (input != null) {
						byte[] data = new byte[1024];
						input.read(data, 0, 1024);
						conn.disconnect();
						input.close();
						log.d("上报结果：" + new String(data));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}.start();
	}

	private void closeDialog() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			dialog = null;
		}
	}

	private synchronized boolean setCallBack(ICallBack cb) {
		// if(mCallBack == null){
		// if(cb == null)
		// return false;
		// mCallBack = cb;
		// }else{
		// try {
		// cb.onError(ErrorCode.BUSY.getCode(),
		// ErrorCode.BUSY.getDefaultMessage());
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// return false;
		// }
		// return true;
		mCallBack = cb;
		return true;
	}

	private class MyBinder extends IService.Stub {

		@Override
		public void registerICallback(ICallBack cb) throws RemoteException {

		}

		@Override
		public void unregisterICallback(ICallBack cb) throws RemoteException {

		}

		/**
		 * 获取设备版本
		 */
		@Override
		public void onGetDeviceInfo(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.getDeviceInfo();
		}

		/**
		 * 设置数据
		 */
		@Override
		public void setDeviceData(String customerNo, String termNo, String serialNo, String batchNo,
								  ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.setDeviceData(customerNo, termNo, serialNo, batchNo);
		}

		/**
		 * 打印
		 */
		@Override
		public void onPrint(byte[] body, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.startPrint(body);
		}

		/**
		 * 验证mac
		 */
		@Override
		public void calculateMac(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.calculateMac(data);
		}

		/**
		 * 更新主密钥
		 */
		@Override
		public void writeMainKey(byte[] key, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.writeMainKey(key);
		}

		/**
		 * 取消操作
		 */
		@Override
		public void cancel() throws RemoteException {
			swiper.cancel();
		}

		/**
		 * 更新工作密钥
		 */
		@Override
		public void writeWorkKey(WorkKey key, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.writeWorkKey(key);
		}

		/**
		 * 射频卡A/B类卡读取
		 */
		@Override
		public void readRFCard(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.readRFCard();
		}

		/**
		 * 读取电子现金余额
		 */
		@Override
		public void readMoney(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.readMoney();
		}

		/**
		 * 设置时间
		 */
		@Override
		public void setDateTime(long time, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.setDateTime(new Date(time));
		}

		/**
		 * 获取时间
		 */
		@Override
		public void getDateTime(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.getMpos().getDateTime(new GetDateTimeListener() {
				@Override
				public void onError(int errorCode, String errorMessage) {
					try {
						mCallBack.onError(errorCode, errorMessage);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					mCallBack = null;
				}

				@Override
				public void onGetDateTimeSuccess(String dateTime) {
					try {
						mCallBack.onGetDateTimeSuccess(dateTime);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					mCallBack = null;
				}
			});

		}

		/**
		 * 开始刷卡
		 */
		@Override
		public void startSwiper(int timeout, long nAmount, int brushCard, byte type, ICallBack icallback)
				throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.startSwiper(timeout, nAmount, brushCard, type, null);
		}

		/**
		 * 获取PSAM卡号
		 */
		@Override
		public void getPsamInfo(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.getPsamInfo();

		}

		/**
		 * 获取PSAM、ST720 ATR内容
		 */
		@Override
		public void getPsamAndSt720Info(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.getPsamAndSt720Info();
		}

		/**
		 * 发送身份证数据
		 */
		@Override
		public void sendIDCardData(byte[] body, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			try {
				byte[] b = swiper.sendIDCardData(body);
				if (b != null) {
					mCallBack.onIDCardResultData(b);
				}
			} catch (Exception e) {
				log.e("idcard result data exception...");
				e.printStackTrace();
			} finally {
				mCallBack = null;
			}
		}

		/**
		 * 打开ADB
		 */
		@Override
		public void openADB(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.openADB();
		}

		/**
		 * 关闭ADB
		 */
		@Override
		public void closeADB(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.closeADB();
		}

		/**
		 * 清除安全触发
		 */
		@Override
		public void clearSecurity(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.clearSecurity();
		}

		/**
		 * 清除密钥证书
		 */
		@Override
		public void clearKey(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.clearKey();
		}

		/**
		 * 更新固件
		 */
		@Override
		public void updateFirmware(String path, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.updateFirmware(path);
		}

		/**
		 * 打开射频
		 */
		@Override
		public void openRFID(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.openRFID();
		}

		/**
		 * 发送射频命令
		 */
		@Override
		public void sendRFIDCmd(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.sendRFIDCmd(data);
		}

		/**
		 * 关闭射频
		 */
		@Override
		public void closeRFID(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.closeRFID();
		}

		/**
		 * 打开IC卡寻卡
		 */
		@Override
		public void openICFind(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.openIC();
		}

		/**
		 * IC卡数据交互
		 */
		@Override
		public void sendICCmd(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.sendICCmd(data);
		}

		/**
		 * IC卡下电
		 */
		@Override
		public void powerDownIC(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.closeIC();
		}

		@Override
		public void resetPSAM(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.resetPSAM(data);

		}

		@Override
		public void readPSAM(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.readPSAM(data);

		}

		@Override
		public void writePSAM(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.writePSAM(data);

		}

		/**
		 * 自毁测试
		 */
		@Override
		public void testDestroy(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.testDestroy();
		}

		/**
		 * 请求录入SN号
		 */
		@Override
		public void requestSN(ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.requestSN();
		}

		/**
		 * SN号下发
		 */
		@Override
		public void writeSN(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.writeSN(data);
		}

		/**
		 * EMVTest
		 */
		@Override
		public void EMVTest(byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.EMVTest(data);
		}

		@Override
		public void readSLECardData(int offset, int len, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.readSLECardData(offset, len);
		}

		@Override
		public void writeSLECardData(String pwd, int offset, byte[] data, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.writeSLECardData(pwd, offset, data);
		}

		@Override
		public void updateSLEpwd(String pwd, String newPwd, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.updateSLEpwd(pwd, newPwd);
		}

		/**
		 * 上传电子签名
		 */
		@Override
		public void uploadSignBitmap(String path, ICallBack icallback) throws RemoteException {
			if (!setCallBack(icallback))
				return;
			swiper.uploadSignBitmap(path);
		}

		@Override
		public byte[] psamResetEx(int siteNo, int timeout) throws RemoteException {
			return swiper.psamResetEx(siteNo, timeout);
		}

		@Override
		public byte[] psamApduEx(int SiteNo, byte[] send, int timeout) throws RemoteException {
			return swiper.psamApduEx(SiteNo, send, timeout);
		}

		@Override
		public byte[] rfidOpenEx(int timeout) throws RemoteException {
			return swiper.rfidOpenEx(timeout);
		}

		@Override
		public byte[] rfidApduEx(byte[] data, int timeout) throws RemoteException {
			return swiper.rfidApdu(data, timeout);
		}

		@Override
		public int rfidCloseEx() throws RemoteException {
			return swiper.rfidCloseEx();
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		log.d("service onCreate");
		YFPosApp.handler = this.handler;
		point = new Point();
		wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getSize(point);
		// 注册监听k81协处理器更新消息
		registerReceiver(updateReceiver, new IntentFilter(BROADTCAST_ACTION_STRING_k81UPDATE));

		swiper = new SwiperController(this, changeListener);

		// TODO: 暂时屏蔽开机检测
		// if(setCallBack(new CallBackListener())){
		// swiper.getMpos().checkSystem(new CheckSystemListener() {
		// @Override
		// public void onError(int errorCode, String errorMessage) {
		// mCallBack = null;
		// }
		//
		// @Override
		// public void onCheckSuccess(String sn, String softVersion) {
		// mCallBack = null;
		// if(sn.equals(""))
		// return;
		// JSONObject obj = new JSONObject();
		// try {
		// obj.put("softVersion", softVersion);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// log.d("开机检测上报");
		// upload(sn, obj.toString());
		// }
		// });
		// }
	}

	@Override
	public void onStart(Intent intent, int startId) {
		log.d("service onStart id=" + startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);

	}

	@Override
	public void onDestroy() {
		log.d("service onDestroy");
		swiper.getMpos().getDeviceComm().close();
		unregisterReceiver(updateReceiver);
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public IBinder onBind(Intent intent) {
		log.d("service onBind");
		return new MyBinder();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		log.d("service onUnbind");
		return super.onUnbind(intent);
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		log.d("service bindService");
		return super.bindService(service, conn, flags);
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		super.unbindService(conn);
		log.d("service unbindService");
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		log.d("service onRebind");
	}

	/**
	 * 接口回调
	 */
	private SimpleSwiperListener changeListener = new SimpleSwiperListener() {

		@Override
		public void onError(int errorCode, String errorMessage) {
			try {
				mCallBack.onError(errorCode, errorMessage);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		/**
		 * 获取设备版本-new
		 */
		@Override
		public void onGetDeviceInfoSuccess(DeviceModel deviceModel) {
			try {
				mCallBack.onGetDeviceInfoSuccess(deviceModel);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		/**
		 * 获取设备版本
		 */
		@Override
		public void onGetDeviceInfo(String customerNo, String termNo, String batchNo, boolean existsMainKey, String sn,
									String version) {
			try {
				mCallBack.onGetDeviceInfo(customerNo, termNo, batchNo, existsMainKey, sn, version);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		@Override
		public void onResultSuccess(int ntype) {
			try {
				mCallBack.onResultSuccess(ntype);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		@Override
		public void onTimeout() {
			try {
				//swiper.cancel();
				mCallBack.onTimeout();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		@Override
		public void onReadSuccess(byte[] result) {
			try {
				mCallBack.onReadSuccess(result);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		@Override
		public void onCalculateMacSuccess(byte[] arg0) {
			try {
				mCallBack.onCalculateMacSuccess(arg0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		/**
		 * 取消刷卡
		 */
		@Override
		public void onTradeCancel() {
			try {
				mCallBack.onTradeCancel();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		/**
		 * 检测到ic卡
		 */
		@Override
		public void onDetectIc() {
			try {
				mCallBack.onDetectIc();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 请输入密码
		 */
		@Override
		public void onInputPin() {
			try {
				mCallBack.onInputPin();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 刷卡成功
		 */
		@Override
		public void onSwiperSuccess(CardModel arg0) {
			try {
				mCallBack.onSwiperSuccess(arg0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		@Override
		public void onShowPinPad(byte[] pad) {
			handler.obtainMessage(CODE_SHOWPINPAD, pad).sendToTarget();
		}

		@Override
		public void onClosePinPad() {
			WindowUtils.hidePopupWindow();
		}

		;

		/**
		 * 获取PSAM卡号
		 */
		@Override
		public void onReadPsamNo(String num) {
			try {
				mCallBack.onReadPsamNo(num);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		/**
		 * 获取PSAM、ST720 ATR内容
		 */
		@Override
		public void onGetPsamAndSt720Info(String psam, String st720) {
			try {
				mCallBack.onGetPsamAndSt720Info(psam, st720);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mCallBack = null;
		}

		/**
		 * 更新进度
		 */
		@Override
		public void onDownloadProgress(long arg0, long arg1) {
			try {
				mCallBack.onDownloadProgress(arg0, arg1);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	private ProgressDialog progressDialog;

	BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			log.d("收到协处理器升级消息");
			final String filePath = intent.getStringExtra("K81OTAFile");
			if (filePath == null) {
				return;
			}
			if (setCallBack(new CallBackListener() {
				public void onError(int errorCode, String errorMessage) throws RemoteException {
					progressDialog.dismiss();
					showSysDialog(0, 0, Gravity.CENTER, "固件升级\n" + "操作失败，返回码：" + errorCode + " 信息:" + errorMessage);
					mCallBack = null;
				}

				public void onDownloadProgress(long current, long total) throws RemoteException {
					progressDialog.setProgress((int) Math.rint((current * 1.0 / total) * 100));
				}

				public void onResultSuccess(int ntype) throws RemoteException {
					progressDialog.dismiss();
					if (ntype == 0x39) {
						try {
							showSysDialog(0, 0, Gravity.CENTER, "固件升级成功");
							File file = new File(filePath);
							if (file.exists()) {
								file.delete();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mCallBack = null;
				}
			})) {
				progressDialog = new ProgressDialog(getApplicationContext());
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setTitle("正在更新固件");
				progressDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				progressDialog.setCancelable(false);
				progressDialog.setMax(100);
				progressDialog.show();
				log.d(filePath);
				swiper.updateFirmware(filePath);
			}
		}
	};

}
