package com.hftcom.ui.pay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.mina.core.buffer.IoBuffer;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.hftcom.R;
import com.hftcom.YFApp;
import com.hftcom.YFLog;
import com.hftcom.db.DBHelper;
import com.hftcom.db.DBUtil;
import com.hftcom.domain.Transaction;
import com.hftcom.ui.MyActivity;
import com.hftcom.utils.Config;
import com.hftcom.utils.MessageBox;
import com.yifengcom.yfpos.bank.EposClientPacket;
import com.yifengcom.yfpos.bank.command.CmdCalcMac;
import com.yifengcom.yfpos.bank.command.CmdDisplay;
import com.yifengcom.yfpos.bank.command.CmdPrint;
import com.yifengcom.yfpos.bank.command.CmdReadBankNo;
import com.yifengcom.yfpos.bank.command.CmdReadBusiAmount;
import com.yifengcom.yfpos.bank.command.EposCommand.CMD_TYPE;
import com.yifengcom.yfpos.bank.mina.EposMessageType;
import com.yifengcom.yfpos.callback.CallBackListener;
import com.yifengcom.yfpos.model.PrintType;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
* @Description: 消费刷卡 
* @date 2016-7-12 
 */
public class WxPayActivity extends MyActivity implements
		OnClickListener {
	int count = 0;
	private Transaction transaction;
	private DBUtil dbUtil;
	private int code;
	Button button;
	private long money;
	private String wxPayNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wx_pay);
		setTitle(R.string.title_wx_pay);
		
		dbUtil = DBUtil.getInstance(getApplicationContext());
		
		money = this.getIntent().getLongExtra("money", 0);
		
		wxPayNum = this.getIntent().getStringExtra("wxcode");

		button = (Button) findViewById(R.id.top_back);
		button.setOnClickListener(this);

		code = this.getIntent().getIntExtra("code", -1);
		if (code != -1) {
			button.setVisibility(View.GONE);
		}

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在付款...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {

			}
		});
		progressDialog.show();
		onLoad();
	}
	
	private void onLoad(){
		transaction = new Transaction();
		int busiNo = YFApp.getApp().getNextSequence(Config.sName);
		if (busiNo == -1) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "流水号获取失败").sendToTarget();
			return;
		}
		transaction.setSerial_no(String.format("%06d", busiNo));

		out = new EposClientPacket();
		String psamID = YFApp.getApp().getPsamNo(Config.pName);
		if (psamID.trim().equals("")) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "PSAM卡号为空").sendToTarget();
			return;
		}
		out.setBusiNo(busiNo);
		out.setPsamID(psamID + "0000");
		
		purchaseEncode();
	}

	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage)
				throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,
					"操作失败，返回码：" + code + " 信息:" + messsage).sendToTarget();
		}

		@Override
		public void onTimeout() throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "超时")
					.sendToTarget();
		}

		@Override
		public void onCalculateMacSuccess(byte[] mac) throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.MACSUCCESS, mac)
					.sendToTarget();
		}

		@Override
		public void onResultSuccess(int ntype) throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.PRINTSUCCESS, ntype)
					.sendToTarget();
		}

	};

	private EposClientPacket out;
	
	/**
	 * 微信付款
	 * 
	 * @param cardModel
	 */
	public void purchaseEncode() {
		transaction.setIc_data("");
		transaction.setCard_type("2");
		out.setBusiCode("311");

		CmdReadBusiAmount cmdAmount = new CmdReadBusiAmount();
		cmdAmount.setHintIndex(0x10).setCmdType(CMD_TYPE.MUTI);
		transaction.setMoney(money +"");
		cmdAmount.setValue(money); // 金额
		out.fields.add(cmdAmount);
		
		CmdReadBankNo bankNo = new CmdReadBankNo();
		bankNo.setHintIndex(0x25).setCmdType(CMD_TYPE.MUTI);


//		String hexWxPayNum = ByteUtils.byteToHex(wxPayNum.getBytes());
//		String len = String.format("%02x", hexWxPayNum.length() / 2);
//		String trackData = len + hexWxPayNum;

		bankNo.setValue(wxPayNum);
		out.fields.add(bankNo);

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
			YFApp.getApp().iService.calculateMac(macData, mCallBack);
		} catch (RemoteException e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"计算Mac服务异常").sendToTarget();
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
		if(mac.equals("0000000000000000")){
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "Mac计算错误，请检查是否插入PSAM卡").sendToTarget();
			return;
		}
		progressDialog.setMessage("发送数据");
		CmdCalcMac cmdMac = (CmdCalcMac) out.fields.get(2);
		cmdMac.setMac(mac);
		transaction.setMac(mac);
		YFApp.getApp().sendData(out, handler,errorHandler);
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
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"已超时").sendToTarget();
		}
	}
	
	@Override
	public void onExceptionCaught(Message msg) {
		errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"连接异常，断开连接").sendToTarget();
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
				MessageBox.showError(WxPayActivity.this, m,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								WxPayActivity.this.finish();
							}
						});
				break;
			case EposMessageType.MACSUCCESS:
				byte[] mac = (byte[]) msg.obj;
				sendDate(ByteUtils.byteToHex(mac));
				break;
			case EposMessageType.PRINTSUCCESS:
				int type = (Integer) msg.obj;
				String text = PrintType.convert(type).getMesssage();
				if(type == 0){
					printCount --;
					if(printCount == 1){
						onPrint(arrayList2,true);
					}else{
						showPrintDialog(text,type);
					}
				}else{
					showPrintDialog(text,type);
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
	 * @param text 显示内容
	 */
	private void showPrintDialog(String text,int type){
		closeDialog();
		if(type == 1){
			MessageBox.showConfirmBox(WxPayActivity.this, text, "交易完成", "打印数据", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					WxPayActivity.this.finish();
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if(printCount == 2){
						onPrint(arrayList1, false);
					}else if(printCount == 1){
						onPrint(arrayList2, true);
					}
				}
			});
		}else{
			MessageBox.showError(WxPayActivity.this, text,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
							WxPayActivity.this.finish();
						}
					});
		}
	}
	
	/**
	 * 打印数据
	 * @param body 打印数据
	 * @param isWait 
	 */
	private void onPrint(ArrayList<String> strs,boolean isWait){
		if(strs == null || strs.size() == 0){
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"打印数据为空").sendToTarget();
			return;
		}
		try {
			byte[] data = getPrintBody(strs,false);
			if(isWait){
				Thread.sleep(2000);
			}
			progressDialog.setMessage(printResult);
			if(progressDialog != null && !progressDialog.isShowing()){
				progressDialog.show();
			}
			YFApp.getApp().iService.onPrint(data, mCallBack);
		} catch (RemoteException e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"打印服务异常").sendToTarget();
		} catch (Exception e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"打印异常").sendToTarget();
		}
	}
	
	private int printCount = 2; //打印次数
	private ArrayList<String> arrayList1;
	private ArrayList<String> arrayList2;
	private String printResult = ""; //交易返回信息
	
	private void convert(ArrayList<String> list){
		if(list != null){
			StringBuilder sb = new StringBuilder();
			arrayList1 = new ArrayList<String>();
			arrayList2 = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i) + ",");
				byte[] date = ByteUtils.hexToByte(list.get(i));
				String str;
				try {
					str = new String(date, "gb2312");
					if(str.contains("FFFF")){
						str = str.replace("FFFF", "微信支付");
					}
				} catch (UnsupportedEncodingException e) {
					str = "";
				}
				arrayList1.add(str);
				if(str.contains("持卡人签字")){
					continue;
				}
				if(str.contains("商户存根")){
					arrayList2.add(str.replace("商户", "持卡人"));
					continue;
				}
				if(str.trim().equals("")){
					continue;
				}
				arrayList2.add(str);
			}
			// 更新打印信息
			transaction.setPrint_data(sb.toString());
			ContentValues values = new ContentValues();
			values.put(DBHelper.PRINT_DATA, sb.toString());
			updateSql(values);
		}
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
				}
			}
			if (display != null) {
				transaction.setResult_code(display.getReturnCode());
				transaction.setResult_msg(display.getDisplayInfo());

				msg = "返回码：" + display.getReturnCode() + " 信息："
						+ display.getDisplayInfo();
				result = display.getDisplayInfo();

				// 更新返回码信息
				ContentValues values = new ContentValues();
				values.put(DBHelper.RESULT_CODE, display.getReturnCode());
				values.put(DBHelper.RESULT_MSG, display.getDisplayInfo());
				updateSql(values);
			}
			if (pnt != null) {
				printResult = result + ",正在打印...";
				convert(pnt.getPrintList());
				onPrint(arrayList1,false);
			} else {
				closeDialog();
				MessageBox.showError(WxPayActivity.this, msg,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								WxPayActivity.this.finish();
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
			return dbUtil.updateCache(values,"serial_no=? and mac=?",new String[] { transaction.getSerial_no(),transaction.getMac()});
		} catch (Exception e) {
			
		}
		return false;
	}

	/**
	 * 更新发送状态
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
}
