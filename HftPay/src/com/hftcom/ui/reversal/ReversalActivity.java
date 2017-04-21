package com.hftcom.ui.reversal;

import java.io.IOException;
import org.apache.mina.core.buffer.IoBuffer;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
import com.yifengcom.yfpos.bank.command.CmdReadReverse;
import com.yifengcom.yfpos.bank.command.EposCommand.CMD_TYPE;
import com.yifengcom.yfpos.bank.mina.EposMessageType;
import com.yifengcom.yfpos.callback.CallBackListener;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
 * @Description: 冲正
 * @date 2016-6-28
 */
public class ReversalActivity extends MyActivity implements OnClickListener {
	int count = 0;
	private Transaction transaction;
	private DBUtil dbUtil;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reversal_activity);

		dbUtil = DBUtil.getInstance(getApplicationContext());
		button = (Button) findViewById(R.id.top_back);
		button.setOnClickListener(this);

		setTitle("冲正");

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在冲正...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				closeDialog();
			}
		});
		progressDialog.show();

		reverseEncode();
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
	};

	
	@Override
	protected void setDialogMessage() {
		progressDialog.setMessage("接收到返回数据");
	}
	
	@Override
	public void onSessionClosed(Message msg) {
		if (!isReturn) {
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
	}

	private Handler errorHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case EposMessageType.REMOTE_ERROR:
				closeDialog();
				MessageBox.showError(ReversalActivity.this, msg.obj + "",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								ReversalActivity.this.finish();
							}
						});
				break;
			case EposMessageType.MACSUCCESS:
				byte[] mac = (byte[]) msg.obj;
				sendDate(ByteUtils.byteToHex(mac));
				break;
			case EposMessageType.RESULT_DATA:

				break;
			default:
				break;
			}
		};
	};

	private EposClientPacket out;
	/**
	 * 冲正操作
	 */
	public void reverseEncode() {
		
		if(this.getIntent().getSerializableExtra("transaction") != null){
			transaction = (Transaction) this.getIntent().getSerializableExtra("transaction");
		}else{
			transaction = checkLastOrder();
		}
		
		if (transaction == null) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "未查询到数据")
			.sendToTarget();
			return;
		}

		int busiNo = YFApp.getApp().getNextSequence(Config.sName);
		if (busiNo == -1) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "流水号获取失败")
					.sendToTarget();
			return;
		}

		out = new EposClientPacket();
		String psamID = YFApp.getApp().getPsamNo(Config.pName);
		if (psamID.trim().equals("")) {
			errorHandler
					.obtainMessage(EposMessageType.REMOTE_ERROR, "psam卡号为空")
					.sendToTarget();
			return;
		}
		out.setPsamID(psamID + "0000");
		out.setBusiNo(busiNo);
		out.setBusiCode("004");

		CmdReadReverse reverse = new CmdReadReverse();
		reverse.setHintIndex(0x11).setCmdType(CMD_TYPE.MUTI);

		String values = "";
		String body = "";
		if (transaction.getCard_type().equals("0")) {
			values = transaction.getSerial_no() + transaction.getMac();
		} else {
			body = "01" + transaction.getSerial_no() + transaction.getMac()
					+ "96";
//			String icData = convertICData(transaction.getIc_data());
			String icData = transaction.getIc_data();
			String icDataLen = String.format("%2x", icData.length() / 2);
			body = body + icDataLen + icData;
			String bodyLen = String.format("%2x", body.length() / 2);
			values = bodyLen + body;
		}

		reverse.setValue(values);
		out.fields.add(reverse);

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
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "计算mac服务异常")
					.sendToTarget();
		} catch (Exception e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "计算mac异常").sendToTarget();
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
		CmdCalcMac cmdMac = (CmdCalcMac) out.fields.get(1);
		cmdMac.setMac(mac);
		YFApp.getApp().sendData(out, handler,errorHandler);
	}

	@Override
	public void onReverseOK(EposClientPacket packet) {
		try {
			String msg = "", result = "";
			CmdDisplay display = null;
			for (int i = 0; i < packet.fields.size(); i++) {
				if (packet.fields.get(i) instanceof CmdDisplay) {
					display = (CmdDisplay) packet.fields.get(i);
					break;
				}
			}
			if (display != null) {
				msg = "返回码：" + display.getReturnCode() + " 信息："
						+ display.getDisplayInfo();
				result = display.getDisplayInfo();

				transaction.setResult_code(display.getReturnCode());
				transaction.setResult_msg(display.getDisplayInfo());
				int count = Integer.parseInt(transaction.getCorrect_num());
				ContentValues values = new ContentValues();
				values.put(DBHelper.RESULT_CODE, display.getReturnCode());
				values.put(DBHelper.RESULT_MSG, display.getDisplayInfo());
				values.put(DBHelper.CORRECT_NUM, count - 1 );
				updateSql(values);
				
				closeDialog();
				MessageBox.showError(ReversalActivity.this, msg,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								ReversalActivity.this.finish();
							}
						});
			} 
		} catch (Exception e) {
			e.printStackTrace();
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "冲正错误")
					.sendToTarget();
		}
	}

	/**
	 * 更新记录
	 * 
	 * @return
	 */
	public boolean updateSql(ContentValues values) {
		try {
			return dbUtil.updateCache(
					values,
					"_id=?",
					new String[] { transaction.get_id()});
		} catch (Exception e) {

		}
		return false;
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