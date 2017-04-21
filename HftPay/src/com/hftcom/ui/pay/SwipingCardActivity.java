package com.hftcom.ui.pay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.mina.core.buffer.IoBuffer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hftcom.ActionCode;
import com.hftcom.R;
import com.hftcom.YFApp;
import com.hftcom.db.DBHelper;
import com.hftcom.db.DBUtil;
import com.hftcom.domain.Transaction;
import com.hftcom.ui.MyActivity;
import com.hftcom.ui.handwriting.DialogListener;
import com.hftcom.ui.handwriting.WritePadDialog;
import com.hftcom.utils.Config;
import com.hftcom.utils.MessageBox;
import com.yifengcom.yfpos.bank.EposClientPacket;
import com.yifengcom.yfpos.bank.command.CmdCalcMac;
import com.yifengcom.yfpos.bank.command.CmdDisplay;
import com.yifengcom.yfpos.bank.command.CmdPrint;
import com.yifengcom.yfpos.bank.command.CmdPrint.signData;
import com.yifengcom.yfpos.bank.command.CmdReadBusiAmount;
import com.yifengcom.yfpos.bank.command.CmdReadPin;
import com.yifengcom.yfpos.bank.command.CmdReadTrack;
import com.yifengcom.yfpos.bank.command.EposCommand.CMD_TYPE;
import com.yifengcom.yfpos.bank.mina.EposMessageType;
import com.yifengcom.yfpos.callback.CallBackListener;
import com.yifengcom.yfpos.model.PrintType;
import com.yifengcom.yfpos.model.TrxType;
import com.yifengcom.yfpos.service.CardModel;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
 * @Description: 消费刷卡
 * @date 2016-6-29
 */
public class SwipingCardActivity extends MyActivity implements OnClickListener {

	int count = 0;
	private Transaction transaction;
	private DBUtil dbUtil;
	private int code;
	Button button;
	private long money;
	private String pkgName = "", actName = "";
	private boolean isSwiper = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swiping_card);

		dbUtil = DBUtil.getInstance(getApplicationContext());
		pkgName = this.getIntent().getStringExtra("pkgName");
		actName = this.getIntent().getStringExtra("actName");

		money = this.getIntent().getLongExtra("money", 0);
		button = (Button) findViewById(R.id.top_back);
		button.setOnClickListener(this);

		code = this.getIntent().getIntExtra("code", -1);
		if (code != -1) {
			button.setVisibility(View.GONE);
		}

		setTitle(R.string.title_swiping_card);

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("请刷卡...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (isSwiper) {
					try {
						YFApp.getApp().iService.cancel();
					} catch (RemoteException e) {
						e.printStackTrace();
						errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "取消刷卡服务异常").sendToTarget();
					} catch (Exception e) {
						e.printStackTrace();
						errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "取消刷卡异常").sendToTarget();
					}
				}
			}
		});
		progressDialog.show();
		onLoad();
	}

	private void onLoad() {

		transaction = new Transaction();
		int busiNo = YFApp.getApp().getNextSequence(Config.sName);
		if (busiNo == -1) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "流水号获取失败").sendToTarget();
			return;
		}
		serialNo = String.format("%06d", busiNo);
		transaction.setSerial_no(serialNo);

		out = new EposClientPacket();
		psamID = YFApp.getApp().getPsamNo(Config.pName);
		if (psamID.trim().equals("")) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "PSAM卡号为空").sendToTarget();
			return;
		}
		out.setBusiNo(busiNo);
		out.setPsamID(psamID + "0000");

		try {

			YFApp.getApp().iService.startSwiper(120, 100, 0, TrxType.PURCHASE.getValue(), mCallBack);
		} catch (RemoteException e) {
			e.printStackTrace();
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "刷卡服务异常").sendToTarget();
		} catch (Exception e) {
			e.printStackTrace();
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "刷卡异常").sendToTarget();
		}

	}

	private void setIsSwiper() {
		isSwiper = false;
		progressDialog.setCancelable(false);
	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage) throws RemoteException {
			setIsSwiper();
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "操作失败，返回码：" + code + " 信息:" + messsage)
					.sendToTarget();
		}

		public void onDetectIc() throws RemoteException {
			setIsSwiper();
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog.setMessage("检测到IC卡");
				}
			});
		}

		public void onInputPin() throws RemoteException {

		}

		public void onSwiperSuccess(CardModel cardModel) throws RemoteException {
			setIsSwiper();
			errorHandler.obtainMessage(EposMessageType.SWIPERSUCCESS, cardModel).sendToTarget();
		};

		@Override
		public void onTimeout() throws RemoteException {
			setIsSwiper();
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "超时").sendToTarget();
		}

		@Override
		public void onTradeCancel() throws RemoteException {
			setIsSwiper();
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "取消刷卡").sendToTarget();
		}

		@Override
		public void onCalculateMacSuccess(byte[] mac) throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.MACSUCCESS, mac).sendToTarget();
		}

		@Override
		public void onResultSuccess(int ntype) throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.PRINTSUCCESS, ntype).sendToTarget();
		}

	};

	private EposClientPacket out;

	/**
	 * 消费付款
	 * 
	 * @param cardModel
	 */
	public void purchaseEncode(CardModel cardModel) {

		transaction.setIc_data(cardModel.getIcData());
		out.setBusiCode("101");

		CmdReadBusiAmount cmdAmount = new CmdReadBusiAmount();
		cmdAmount.setHintIndex(0x10).setCmdType(CMD_TYPE.MUTI);
		transaction.setMoney(money + "");
		cmdAmount.setValue(money); // 金额
		out.fields.add(cmdAmount);

		CmdReadTrack cmdTrack = new CmdReadTrack();
		cmdTrack.setHintIndex(8).setCmdType(CMD_TYPE.MUTI);

		String type = "";
		String sData = "";

		if (cardModel.isIc()) {
			type = "01";
			sData = cardModel.getEncryTrack2();
		} else {
			type = "00";
			sData = cardModel.getEncryTrack2() + cardModel.getEncryTrack3();
		}
		String sLen = String.format("%2x", sData.length() / 2);
		String trackData = type + sLen + sData;

		cmdTrack.setTrackData(trackData);
		out.fields.add(cmdTrack);

		CmdReadPin cmdPin = new CmdReadPin();
		cmdPin.setHintIndex(7).setCmdType(CMD_TYPE.MUTI);
		String password = "";
		String len = "";
		if (cardModel.isIc()) {
			transaction.setCard_type("1");
			password = "35" + cardModel.getPinBlock() + "2C642C6E00000000"
					+ String.format("%2X", cardModel.getIcData().length() / 2) + cardModel.getIcData();
			len = String.format("%04X", password.length() / 2);
			password = len + password;
		} else {
			transaction.setCard_type("0");
			password = "0012" + "35" + cardModel.getPinBlock() + "2C642C6E0000000000";
		}

		cmdPin.setPassword(password);
		out.fields.add(cmdPin);

		CmdCalcMac cmdMac = new CmdCalcMac();
		cmdMac.setHintIndex(0).setCmdType(CMD_TYPE.SINGLE);
		out.fields.add(cmdMac);

		IoBuffer buf = null;
		try {
			buf = out.encodeToStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] macData = new byte[buf.remaining() - 8];
		buf.get(macData);

		try {
			Thread.sleep(200);
			YFApp.getApp().iService.calculateMac(macData, mCallBack);
		} catch (RemoteException e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "计算Mac服务异常").sendToTarget();
		} catch (Exception e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "计算Mac异常").sendToTarget();
		}
	}

	/**
	 * 取到MAC,发送数据
	 * 
	 * @param mac
	 */
	public void sendDate(String mac) {
		if (mac.equals("0000000000000000")) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "Mac计算错误，请检查是否插入PSAM卡").sendToTarget();
			return;
		}
		progressDialog.setMessage("发送数据");
		CmdCalcMac cmdMac = (CmdCalcMac) out.fields.get(3);
		cmdMac.setMac(mac);
		transaction.setMac(mac);
		YFApp.getApp().sendData(out, handler, errorHandler);
	}

	@Override
	public void setDialogMessage() {
		progressDialog.setMessage("接收到返回数据");
		updateSendResult("已接收");
	}

	@Override
	public void onSessionClosed(Message msg) {
		if (!isReturn) {
			updateSendResult("已超时");
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "已超时").sendToTarget();
		}
	}

	@Override
	public void onExceptionCaught(Message msg) {
		errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "连接异常，断开连接").sendToTarget();
	}

	@Override
	public void onMessageSent(Message msg) {
		progressDialog.setMessage("发送成功，等待接收");
		transaction.setSend_result("已发送");
		transaction.setSend_time(getCurrentTime());
		dbUtil.insertData(transaction);
	}

	private Handler errorHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case EposMessageType.REMOTE_ERROR:
				closeDialog();
				String m = msg.obj + "";
				MessageBox.showError(SwipingCardActivity.this, m, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onResult("Error");
						SwipingCardActivity.this.finish();
					}
				});
				break;
			case EposMessageType.SWIPERSUCCESS:
				CardModel cardModel = (CardModel) msg.obj;
				cardNo = cardModel.getPan();
				purchaseEncode(cardModel);
				break;
			case EposMessageType.MACSUCCESS:
				byte[] mac = (byte[]) msg.obj;
				sendDate(ByteUtils.byteToHex(mac));
				break;
			case EposMessageType.PRINTSUCCESS:
				int type = (Integer) msg.obj;
				String text = PrintType.convert(type).getMesssage();
				if (type == 0) {
					printCount--;
					if (printCount == 1) {
						onPrint(arrayList2, true);
					} else {
						showPrintDialog(text, type);
					}
				} else {
					showPrintDialog(text, type);
				}
				break;
			case EposMessageType.RESULT_DATA:
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 弹出打印提示框
	 * 
	 * @param text
	 *            显示内容
	 */
	private void showPrintDialog(String text, int type) {
		closeDialog();
		if (type == 1) {
			MessageBox.showConfirmBox(SwipingCardActivity.this, text, "交易完成", "打印数据",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							onResult("OK");
							SwipingCardActivity.this.finish();
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (printCount == 2) {
								onPrint(arrayList1, false);
							} else if (printCount == 1) {
								onPrint(arrayList2, true);
							}
						}
					});
		} else {
			MessageBox.showError(SwipingCardActivity.this, text, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					onResult("OK");
					SwipingCardActivity.this.finish();
				}
			});
		}
	}

	/**
	 * 打印数据
	 * 
	 * @param body
	 *            打印数据
	 * @param isWait
	 */
	private void onPrint(ArrayList<String> strs, boolean isWait) {

		if (strs == null || strs.size() == 0) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "打印数据为空").sendToTarget();
			return;
		}
		try {
			byte[] data = getPrintBody(strs, isWait == true ? false : true);
			if (isWait) {
				Thread.sleep(2000);
			}
			progressDialog.setMessage(printResult);
			if (progressDialog != null && !progressDialog.isShowing()) {
				progressDialog.show();
			}
			YFApp.getApp().iService.onPrint(data, mCallBack);
		} catch (RemoteException e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "打印服务异常").sendToTarget();
		} catch (Exception e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "打印异常").sendToTarget();
		}
	}

	private int printCount = 2; // 打印次数
	private ArrayList<String> arrayList1;
	private ArrayList<String> arrayList2;
	private String printResult = ""; // 交易返回信息

	private void convert(ArrayList<String> list) {
		if (list != null) {
			StringBuilder sb = new StringBuilder();
			printBody = new StringBuilder();
			arrayList1 = new ArrayList<String>();
			arrayList2 = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i) + ",");
				byte[] date = ByteUtils.hexToByte(list.get(i));
				String str;
				try {
					str = new String(date, "gb2312");
					printBody.append(str + ",");
					if (str.contains("FFFF")) {
						str = str.replace("FFFF", "银行卡消费");
					}
					if (str.contains("中国银联交易凭条")) {
						str = "     " + str;
					}
					if (str.contains("商户存根")) {
						str = "      " + str;
					}
				} catch (UnsupportedEncodingException e) {
					str = "";
				}
				arrayList1.add(str);

				if (str.contains("持卡人签字")) {
					continue;
				}
				if (str.trim().equals("")) {
					continue;
				}
				if (str.contains("商户存根")) {
					arrayList2.add("     " + str.replace(" ", "").replace("商户", "持卡人"));
					continue;
				}
				arrayList2.add(str);
			}

			transaction.setPrint_data(sb.toString());
			ContentValues values = new ContentValues();
			values.put(DBHelper.PRINT_DATA, sb.toString());
			updateSql(values);
		}

	}

	private signData signdata = null;

	public signData getSignData() {

		return signdata;
	}

	@Override
	public void onPurchaseOk(EposClientPacket packet) {
		try {
			String msg = "", result = "";
			CmdPrint pnt = null;
			CmdDisplay display = null;
			for (int i = 0; i < packet.fields.size(); i++) {
				if (packet.fields.get(i) instanceof CmdDisplay) {
					display = (CmdDisplay) packet.fields.get(i);
					continue;
				}
				if (packet.fields.get(i) instanceof CmdPrint) {
					pnt = (CmdPrint) packet.fields.get(i);
					signdata = new signData();
					pnt.getSectionPrintValue(signdata);
				}
			}
			if (display != null) {
				
				paymentCode = display.getReturnCode();
				paymentMsg = display.getDisplayInfo();
				transaction.setResult_code(paymentCode);
				transaction.setResult_msg(paymentMsg);

				if (signdata != null) {
					signdata.Status = paymentCode;
					signdata.resultMsg = paymentMsg;
				}

				msg = "返回码：" + paymentCode + " 信息：" + paymentMsg;
				result = paymentMsg;

				// 更新返回码信息
				ContentValues values = new ContentValues();
				values.put(DBHelper.RESULT_CODE, paymentCode);
				values.put(DBHelper.RESULT_MSG, paymentMsg);
				updateSql(values);
			}
			if (pnt != null) {
				printResult = result + ",正在打印...";
				convert(pnt.getPrintList());
				showSign(); // 开始手写签名
			} else {
				closeDialog();
				MessageBox.showError(SwipingCardActivity.this, msg, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						onResult("Error");
						SwipingCardActivity.this.finish();
					}
				});
			}
		} catch (Exception e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "交易错误").sendToTarget();
		}
	}

	/**
	 * 更新记录
	 * 
	 * @return
	 */
	private boolean updateSql(ContentValues values) {
		try {
			return dbUtil.updateCache(values, "serial_no=? and mac=?",
					new String[] { transaction.getSerial_no(), transaction.getMac() });
		} catch (Exception e) {

		}
		return false;
	}

	/**
	 * 更新发送状态
	 * 
	 * @return
	 */
	private boolean updateSendResult(String txt) {
		transaction.setSend_result(txt);
		ContentValues values = new ContentValues();
		values.put(DBHelper.SEND_RESULT, txt);
		boolean status = updateSql(values);
		return status;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			this.finish();
			break;
		}
	}

	private String cardNo = ""; // 卡号
	private String serialNo = ""; // 流水号
	private String psamID = ""; // 终端号
	private String paymentCode = ""; // 支付状态码
	private String paymentMsg = ""; // 支付状态信息
	private StringBuilder printBody; // 所有打印内容

	private void onResult(String result) {
		Intent intent = null;
		try {
			if (code == ActionCode.OTHER.getCode()) {

				intent = new Intent();
				ComponentName cn = new ComponentName(pkgName, actName);
				intent.putExtra("resultCode", result); // OK,Error
				intent.putExtra("cardNo", cardNo); // 卡号
				intent.putExtra("serialNo", serialNo); // 流水号
				intent.putExtra("psamID", psamID); // 终端号
				intent.putExtra("paymentCode", paymentCode); // 支付状态码
				intent.putExtra("paymentMsg", paymentMsg); // 支付状态信息
				intent.putExtra("printBody", printBody == null ? "" : printBody.toString()); // 所有打印内容
				intent.setComponent(cn);
				startActivity(intent);

			} else if (code == ActionCode.HTML5.getCode()) {
				intent = new Intent();
				intent.setAction("com.hftcom.html5.PayResult");
				intent.putExtra("result", result);
				this.sendBroadcast(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bitmap mSignBitmap;
	private WritePadDialog writeTabletDialog;
	private ProgressDialog signProgressDialog;

	CallBackListener signCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage) throws RemoteException {
			signProgressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					MessageBox.showError(SwipingCardActivity.this, "上传失败，返回码：" + code + " 信息:" + messsage,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
				}
			});

		}

		@Override
		public void onDownloadProgress(long current, long total) throws RemoteException {
			signProgressDialog.setProgress((int) Math.rint((current * 1.0 / total) * 100));
		}

		@Override
		public void onResultSuccess(final int nType) throws RemoteException {
			signProgressDialog.dismiss();

			runOnUiThread(new Runnable() {
				public void run() {
					// Toast.makeText(SwipingCardActivity.this, "上传成功",
					// Toast.LENGTH_SHORT).show();
					writeTabletDialog.dismiss();

					// convert(new CmdPrint().getPrintList());
					onPrint(arrayList1, false);
				}
			});

		}

		@Override
		public void onTradeCancel() throws RemoteException {
			signProgressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					MessageBox.showError(SwipingCardActivity.this, "取消上传", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				}

			});

		}

		@Override
		public void onTimeout() throws RemoteException {
			signProgressDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					new AlertDialog.Builder(SwipingCardActivity.this).setTitle("错误").setMessage("上传超时")
							.setPositiveButton("确定", null).show();
				}
			});
		}
	};

	private void showSign() {

		writeTabletDialog = new WritePadDialog(SwipingCardActivity.this, new DialogListener() {
			@Override
			public void refreshActivity(Object object) {

				mSignBitmap = (Bitmap) object;
				mSignBitmap = zoomImage(mSignBitmap, 360, 140);
				
				String path = createFile();
				

				signProgressDialog = new ProgressDialog(SwipingCardActivity.this);
				signProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				signProgressDialog.setTitle("正在上传电子签名");
				signProgressDialog.setCancelable(false);
				signProgressDialog.setCanceledOnTouchOutside(false);
				signProgressDialog.setOnCancelListener(new OnCancelListener() {
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
				signProgressDialog.setMax(100);
				signProgressDialog.show();

				try {
					YFApp.getApp().iService.uploadSignBitmap(path, signCallBack);
				} catch (RemoteException e) {
					e.printStackTrace();
					showToast("接口访问错误");
					closeDialog();
				}

			}
		});
		writeTabletDialog.setCancelable(false);
		writeTabletDialog.show();
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = writeTabletDialog.getWindow().getAttributes();

		lp.width = (int) (display.getWidth());
		lp.height = display.getHeight();

		writeTabletDialog.getWindow().setGravity(Gravity.BOTTOM);
		writeTabletDialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
		writeTabletDialog.getWindow().setAttributes(lp);
	}

	public Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		matrix.postScale(scaleWidth, scaleHeight, width, height);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

	/**
	 * 创建手写签名文件
	 * 
	 * @return
	 */
	private String createFile() {
		ByteArrayOutputStream baos = null;
		String _path = null;
		try {
			String sign_dir = Environment.getExternalStorageDirectory() + File.separator;	
			//String sign_dir = getExternalCacheDir().getPath() + "/";
			_path = sign_dir + "print.png";
			baos = new ByteArrayOutputStream();
			mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) {
				File signFile = new File(_path);
				FileOutputStream outputStream = new FileOutputStream(signFile);
				outputStream.write(photoBytes);
				outputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _path;
	}
}
