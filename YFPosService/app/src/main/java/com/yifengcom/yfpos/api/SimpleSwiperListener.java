package com.yifengcom.yfpos.api;

import com.yifengcom.yfpos.DeviceInfo;
import com.yifengcom.yfpos.service.CardModel;
import com.yifengcom.yfpos.service.DeviceModel;

public class SimpleSwiperListener implements SwiperListener {

	@Override
	public void foundOneDevice(DeviceInfo arg0) {
		
	}

	@Override
	public void onCalculateMacSuccess(byte[] arg0) {
		
	}

	@Override
	public void onDetectIc() {
		
	}

	@Override
	public void onDisconnect() {
		
	}

	@Override
	public void onDownloadProgress(long arg0, long arg1) {
		
	}

	@Override
	public void onError(int arg0, String arg1) {
		
	}
 

	@Override
	public void onInputPin() {
		
	}

	@Override
	public void onResultSuccess(int arg0) {
		
	}

	@Override
	public void onSwiperSuccess(CardModel arg0) {
		
	}

	@Override
	public void onTimeout() {
		
	}

	@Override
	public void onTradeCancel() {
		
	}

	@Override
	public void onGetDeviceInfo(String arg0, String arg1, String arg2,
			boolean arg3, String arg4, String arg5) {
		
	}

	@Override
	public void onShowPinPad(byte[] pad) {
		
	}

	@Override
	public void onClosePinPad() {
		
	}

	@Override
	public void onReadSuccess(byte[] money) {
		
	}

	@Override
	public void onGetPsamAndSt720Info(String psam, String st720) {
		
	}

	@Override
	public void onReadPsamNo(String num) {
		
	}

	@Override
	public void onGetDeviceInfoSuccess(DeviceModel deviceModel) {
		
	}
}
