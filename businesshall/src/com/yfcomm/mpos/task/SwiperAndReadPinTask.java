package com.yfcomm.mpos.task;

import com.yfcomm.businesshall.ByteUtils;
import com.yfcomm.businesshall.SwapCard.MyDeviceComm;
import com.yfcomm.businesshall.SwapCard.SwapCardlistener;
import com.yfcomm.mpos.codec.CardModel;
import com.yfcomm.mpos.codec.DeviceContext;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.DevicePackage.DevicePackageSequence;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.mpos.model.syn.ErrorCode;
import com.yfcomm.mpos.model.syn.OpenReadCard;
import com.yfcomm.mpos.model.syn.ReadPin;
import com.yfcomm.mpos.task.SwiperTask;

import android.content.Context;
import android.util.Log;

/**
 * 刷卡业务流程
 * 
 * @author qc
 *
 */
public class SwiperAndReadPinTask extends SwiperTask {

	public final static int EVENT_INPUTPINEVENT = 3; // 输入密码
	public final static int EVENT_INPUTKEYBORD = 4; // 密码键盘
	public final static int EVENT_CLOSEKEYBORD = 7; // 关闭密码键盘
	private byte[] pinBlock = new byte[8];
	private final SwapCardlistener listener;
	private DeviceContext context;
	private byte[] keyvalue = new byte[10];
	private boolean isWirteNullcard=false;

	public SwiperAndReadPinTask(MyDeviceComm deviceComm, OpenReadCard openReadCard, DeviceContext context,
			SwapCardlistener listener,boolean isWirteNullcard) {
		super(deviceComm, openReadCard, context, null);
		this.context = context;
		this.listener = listener;
		this.isWirteNullcard=isWirteNullcard;
	}

	/**
	 * 重写加入读取密码流程
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Integer doTask() throws Exception {
		int code = super.doTask();
		if (code == ErrorCode.SUCC.getCode()) {
			code = this.readPin();
		}
		return code;
	}

	/**
	 * 处理进度
	 */
	@Override
	protected void onProgressUpdate(Object... args) {
		Integer state = (Integer) args[0];
		if (state == EVENT_INPUTPINEVENT) {
			this.listener.onInputPin();

		} else if (state == EVENT_COMPLETE) {

		} else if (state == EVENT_DETECTICC) { // 检测到ic卡
			this.listener.onDetectIc();
		} else if (state == EVENT_INPUTKEYBORD) {
			this.listener.onShowKeyPad(keyvalue);
		}else if(state==EVENT_CLOSEKEYBORD){//检测到关闭密码键盘
			Log.e("TASK", "closeKEybord");
			this.listener.OnCloseKeyBord();
		}
	}

	/**
	 * 处理完成
	 */
	@Override
	protected void onPostExecute(Integer code) {
		if (code != ErrorCode.SUCC.getCode()) {
			// 分发错误事件
			if (code == ErrorCode.SWIPER_TIMEOUT.getCode() || code == ErrorCode.INPUT_PASSWORD_TIMEOUT.getCode()) {
				listener.onTimeout();
			} else if (code == ErrorCode.CANCEL.getCode() || code == ErrorCode.CANCEL_INPUT_PASSWORD.getCode()) {
				listener.onTradeCancel();
			} else {
				listener.onError(code, context.getErrorMessage(code));
			}
		} else {
			// 执行完成
			CardModel model = new CardModel();

			model.setIc(this.isIc());
			model.setExpireDate(this.getSwiperDecoder().getExpiryDate());
			model.setMac(ByteUtils.byteToHex(this.getSwiperDecoder().getMac()));
			model.setPan(this.getSwiperDecoder().getPan());

			model.setEncryTrack2Len(getSwiperDecoder().getEncryTrack2Len());
			model.setEncryTrack3Len(getSwiperDecoder().getEncryTrack3Len());
			model.setEncryTrack2(ByteUtils.byteToHex(this.getSwiperDecoder().getEncryTrack2()));
			model.setEncryTrack3(ByteUtils.byteToHex(this.getSwiperDecoder().getEncryTrack3()));

			model.setTrack2Len(this.getSwiperDecoder().getTrack2Len());
			model.setTrack3Len(this.getSwiperDecoder().getTrack3Len());
			model.setTrack2(ByteUtils.byteToHex(this.getSwiperDecoder().getTrack2()));
			model.setTrack3(ByteUtils.byteToHex(this.getSwiperDecoder().getTrack3()));

			if (this.isIc()) {
				model.setIcData(ByteUtils.byteToHex(this.getSwiperDecoder().getIcData()));
			}
			model.setIcSeq(this.getSwiperDecoder().getIcSeq());
			model.setRandom(ByteUtils.byteToHex(this.getSwiperDecoder().getRandom()));
			model.setPinBlock(ByteUtils.byteToHex(this.pinBlock));

			model.setBatchNo(getSwiperDecoder().getBatchNo());
			model.setSerialNo(getSwiperDecoder().getSerialNo());

			this.listener.onSwiperSuccess(model);
		}
	}

	/**
	 * 读取pin
	 * 
	 * @throws InterruptedException
	 */
	private int readPin() throws InterruptedException {

		this.publishProgress(EVENT_INPUTPINEVENT);
		if(isWirteNullcard){
			Thread.sleep(2000);
			return ErrorCode.SUCC.getCode();
		}
		

	   // Thread.sleep(200);
		DevicePackage recv;
		ReadPin readPin = new ReadPin();
		readPin.setTimeout(120);
		readPin.setRandom(this.getSwiperDecoder().getRandom());
		
		DevicePackage ack = this.getDeviceComm()
				.execute(PackageBuilder.syn(PackageBuilder.CMD_REQUEST_PASSWORD, readPin.encode()));

		if (ack.getCmd() == PackageBuilder.CMD_REQUEST_PASSWORD){
			//Thread.sleep(400);
			keyvalue=ack.getBody();	
			this.publishProgress(EVENT_INPUTKEYBORD);
		}
	
		
		
		// 接收上报数据
		recv = this.getDeviceComm().recv(
				new DevicePackageSequence(PackageBuilder.CMD_UPLOAD_PASSWORD, PackageBuilder.getNextPackageSequence()),
				(readPin.getTimeout() + 1) * 1000);
	//	this.getDeviceComm().write(PackageBuilder.ackSucc(recv.getIndex(), recv.getCmd()));
		this.publishProgress(EVENT_CLOSEKEYBORD);
	//	Thread.sleep(200);
		if (recv.getBody() == null) {
			return ErrorCode.CANCEL_INPUT_PASSWORD.getCode();
		}
		// 密码输入返回状态
		int pinInputState = recv.getBody()[0] & 0xFF;
		if (pinInputState == 0x01) {
			// 空密码直接返回
			pinBlock = new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
					(byte) 0xFF, (byte) 0xFF };
			return ErrorCode.SUCC.getCode();
		} else if (pinInputState == 0x02) {
			return ErrorCode.CANCEL_INPUT_PASSWORD.getCode();
		} else if (pinInputState == 0x03) {
			return ErrorCode.INPUT_PASSWORD_TIMEOUT.getCode();
		}

		ack = this.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_READ_PASSWORD));
		if (ack.getBody() == null || ack.getBody().length < 9) {
			return 0x01;
		}
		// pinBlock
		System.arraycopy(ack.getBody(), 1, pinBlock, 0, pinBlock.length);

		return ErrorCode.SUCC.getCode();
	}
}
