package com.yfcomm.mpos.api;

import com.yfcomm.mpos.DefaultDeviceComm;
import com.yfcomm.mpos.DeviceComm;
import com.yfcomm.mpos.ErrorCode;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.DevicePackage.DevicePackageSequence;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.mpos.model.CardModel;
import com.yfcomm.mpos.model.syn.OpenReadCard;
import com.yfcomm.mpos.model.syn.ReadPin;
import com.yfcomm.mpos.task.SwiperTask;
import com.yfcomm.mpos.utils.ByteUtils;

/**
 * 刷卡业务流程
 * @author qc
 *
 */
public class SwiperAndReadPinTask extends SwiperTask {

	
	public final static int EVENT_INPUTPINEVENT = 3; //输入密码
	private byte[] pinBlock=new byte[8];
	private final SwiperListener listener;
	
	public SwiperAndReadPinTask(DeviceComm deviceComm,OpenReadCard openReadCard, SwiperListener listener) {
		super(deviceComm, openReadCard, null);
		this.listener = listener;
	}
	
	
	/**
	 * 重写加入读取密码流程
	 * @return
	 * @throws Exception
	 */
	@Override
	protected Integer doTask() throws Exception {
		int code = super.doTask();
		if(code == ErrorCode.SUCC.getCode()) {
			code = this.readPin();
		}
 		return code;
	}
	/**
	 * 处理进度
	 */
	@Override
	protected void onProgressUpdate(Object... args) {
		Integer state = (Integer)args[0];
		if(state == EVENT_INPUTPINEVENT) {
			this.listener.onInputPin();
			
		} else if (state == EVENT_COMPLETE) {
			
			
		} else if(state == EVENT_DETECTICC) {  //检测到ic卡
			this.listener.onDetectIc();
		}
	}
	
	/**
	 * 处理完成
	 */
	@Override
	protected void onPostExecute(Integer code) {
		if(code != ErrorCode.SUCC.getCode()) { 
			//分发错误事件
			if(code==ErrorCode.SWIPER_TIMEOUT.getCode() || code == ErrorCode.INPUT_PASSWORD_TIMEOUT.getCode()) {
				listener.onTimeout();
			} else if(code == ErrorCode.CANCEL.getCode() || code == ErrorCode.CANCEL_INPUT_PASSWORD.getCode()) {
				listener.onTradeCancel();
			} else {
				listener.onError(code, ((DefaultDeviceComm)this.getDeviceComm()).getErrorMessage(code));
			}
		} else {
			//执行完成
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
			
			if(this.isIc()) {
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
	 * @throws InterruptedException 
	 */
	private int readPin() throws InterruptedException{
		
		logger.d("请求PIN输入");
		this.publishProgress(EVENT_INPUTPINEVENT);
		
		//Thread.sleep(200);
		DevicePackage recv;
		ReadPin readPin = new ReadPin();
		readPin.setTimeout(120);
		readPin.setRandom(this.getSwiperDecoder().getRandom());
		DevicePackage ack = this.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_REQUEST_PASSWORD,readPin.encode()));
		
		//接收上报数据
		logger.d("等待PIN输入上报数据");
		recv = this.getDeviceComm().recv(new DevicePackageSequence(PackageBuilder.CMD_UPLOAD_PASSWORD, PackageBuilder.getNextPackageSequence()),(readPin.getTimeout() + 1) * 1000);
		this.getDeviceComm().write(PackageBuilder.ackSucc(recv.getIndex(), recv.getCmd()));
		if(recv.getBody()==null) {
			return ErrorCode.CANCEL_INPUT_PASSWORD.getCode();
		}
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//密码输入返回状态
		int pinInputState = recv.getBody()[0] & 0xFF;
		if(pinInputState == 0x01) {
			//空密码直接返回
			pinBlock = new byte[]{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
			return ErrorCode.SUCC.getCode();
		} else if(pinInputState == 0x02) {
			return ErrorCode.CANCEL_INPUT_PASSWORD.getCode();
		} else if(pinInputState == 0x03) {
			return ErrorCode.INPUT_PASSWORD_TIMEOUT.getCode();
		}
		
		logger.d("读取PIN密文");
		ack = this.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_READ_PASSWORD));
		if(ack.getBody()==null || ack.getBody().length<9) {
			return 0x01;
		}
		//pinBlock
		System.arraycopy(ack.getBody(), 1, pinBlock, 0, pinBlock.length);
		
		return ErrorCode.SUCC.getCode();
	}
}
