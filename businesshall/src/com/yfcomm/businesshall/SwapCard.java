package com.yfcomm.businesshall;

import java.io.IOException;
import java.io.Serializable;

import com.yfcomm.mpos.codec.CardModel;
import com.yfcomm.mpos.codec.DeviceContext;
import com.yfcomm.mpos.codec.DeviceDecoder;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.TrxType;
import com.yfcomm.mpos.codec.DevicePackage.PackageType;
import com.yfcomm.mpos.codec.MPOSException;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.mpos.listener.ErrorListener;
import com.yfcomm.mpos.listener.TimeoutListener;
import com.yfcomm.mpos.model.syn.ErrorCode;
import com.yfcomm.mpos.model.syn.OpenReadCard;
import com.yfcomm.mpos.task.SwiperAndReadPinTask;
import com.yfcomm.pos.SerialPort;
import com.yfcomm.public_define.public_define;

import android.content.Context;
import android.util.Log;

public class SwapCard implements SerialPort.SerialPortListener {

	private MyDeviceComm myComm;
	private OpenReadCard readCard;
	private static int timeout;
	private final static DeviceDecoder decoder = new DeviceDecoder();
	private final static String TAG = "SwapCard";
	private SwapCardlistener listener;
	private final static int MAX_LEN = 800;
	private final byte[] packBuf = new byte[MAX_LEN];
	private int offset = 0;
	private int neadlen;
	
	public static interface SwapCardlistener extends ErrorListener, TimeoutListener {

		public void onDetectIc();// 检测到IC卡

		public void onInputPin();// 输入pin设备

		public void onTradeCancel();// 交易取消

		public void onShowKeyPad(byte[] keyvalue);// 传进键值

		public void onSwiperSuccess(CardModel model);// 刷卡完成

		public void OnChangeKeyBord(byte[] body);// 密码键盘值在改变

		public void OnCloseKeyBord(); // 关闭密码键盘

		public void OnPrintOK(); // 打印完成
	}

	public SwapCard(Context context) {

		try {
			myComm = new MyDeviceComm(context, public_define.getSerialPort(this,context));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startswap(int timeout, long nAmount, int brushCard, TrxType type, SwapCardlistener listener,boolean isWirteNullcard) {

		this.listener = listener;
		readCard = new OpenReadCard();
		// 超时
		readCard.setTimeout((byte) timeout);
		SwapCard.timeout = timeout * 1000;
		// 交易类型
		readCard.setTrxType(type);
		// 金额
		readCard.setTrxMoney(nAmount / 100.0);
		readCard.setActiveCode(new byte[] { (byte) brushCard, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
		new SwiperAndReadPinTask(myComm, readCard, myComm, listener,isWirteNullcard).execute();
	}

	public void sendPrint(byte[] sendbuf, int sendlen) {
     
	  this.myComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PRINT, sendbuf));

	}

	public static class MyDeviceComm extends DeviceContext {

		private final SerialPort mSerialPort;

		public MyDeviceComm(Context context, SerialPort mSerialPort) {
			super(context);
			this.mSerialPort = mSerialPort;
		}

		public DevicePackage recv(Serializable sequence, int timeout) {

			DevicePackage ack = decoder.waitDecodeComplete(sequence, timeout * 1L);
			if (ack.getPackType() == PackageType.ACK_ERROR.getType()) {
				// 应答错误信息
				ErrorCode errorCode = ErrorCode.convert(ack.getBody()[0]);
				throw new MPOSException(errorCode, this);

			} else {
				return ack;
			}
		}

		public synchronized DevicePackage execute(DevicePackage cmd, int timeout) {
			// 写入数据
			this.write(cmd);
			// 应答数据
			return recv(cmd.getPackSequence(), timeout);
		}

		public synchronized DevicePackage execute(DevicePackage cmd) {
			// 写入数据
			this.write(cmd);
			// 应答数据
			return recv(cmd.getPackSequence(), timeout);
		}

		public void write(DevicePackage pack) {

			mSerialPort.getOutputStream(pack.getPackData());
			Log.e(TAG, "sendData" + ByteUtils.printBytes(pack.getPackData()));
		}

	}

	@Override
	public void OnRcvData(byte[] group_pack, int len) {
		Log.e(TAG, "RcvData:" + ByteUtils.printBytes(packBuf, 0, len));
		
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

		if (aDevicePackage.getCmd() == PackageBuilder.CMD_UPLOAD_KEYBORD) {
			listener.OnChangeKeyBord(aDevicePackage.getBody());
		}else if (aDevicePackage.getCmd() == PackageBuilder.CMD_PRINT_RESULT) {
			this.listener.OnPrintOK();
		}else {
			decoder.append(packBuf, 0, len);
		}
	}
}
