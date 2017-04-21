package com.yifengcom.yfpos.service;

import com.yifengcom.yfpos.service.CardModel;
import com.yifengcom.yfpos.service.DeviceModel;

interface ICallBack{  
    void onError(int errorCode, String errorMessage);
    void onGetDeviceInfo(String customerNo,String termNo,String batchNo,boolean existsMainKey,String sn, String version);  
	void onTimeout();
	void onResultSuccess(int ntype);
	void onCalculateMacSuccess(inout byte[] mac);
	void onReadSuccess(inout byte[] body);
	void onGetDateTimeSuccess(String dateTime);
	void onSwiperSuccess(inout CardModel cardModel);
	void onDetectIc();
	void onInputPin();
	void onTradeCancel();
	void onShowPinPad(inout byte[] pad);
	void onClosePinPad();
	void onGetPsamAndSt720Info(String psam, String st720);
	void onReadPsamNo(String num);
	void onIDCardResultData(inout byte[] body);
	void onDownloadProgress(long current, long total);
	void onGetDeviceInfoSuccess(in DeviceModel deviceModel);
	void onEMVTestOK(inout byte[] body);
}  
