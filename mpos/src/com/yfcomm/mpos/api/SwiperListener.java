package com.yfcomm.mpos.api;


import com.yfcomm.mpos.DeviceInfo;
import com.yfcomm.mpos.listener.ErrorListener;
import com.yfcomm.mpos.model.CardModel;

public interface SwiperListener extends ErrorListener {//,TimeoutListener{
	 
	/**
	 * 连接关闭
	 */
	 void onDisconnect();
	 /**
	  * 获取设备信息成功
	  * @param sn
	  * @param version
	  */
	 void onGetDeviceInfo(String customerNo,String termNo,String batchNo,boolean existsMainKey,String sn,String version);
	 
	 /**
	  * 刷卡超时回调
	  */
	 void onTimeout();
	 
	 /**
	  * 交易取消回调
	  */
	 void onTradeCancel();
	 
	 /**
	  * 出错
	  */
	 void onError(int code, String messsage);
	 
	 /**
	  * 操作成功返回
	  * @param nType
	  */
	 void onResultSuccess(int nType);
	 
	 /**
	  * 检测到IC卡
	  */
	 void onDetectIc();
	 
	 /**
	  * 输入密码事件
	  */
	 void onInputPin();
	 
	 /**
	  * 刷卡成功返回
	  * @param pan
	  * @param expireDate
	  */
	 void onSwiperSuccess(CardModel model);
	 
	 /**
	  * 计算mac 成功回调
	  * @param mac
	  */
     void onCalculateMacSuccess(byte[] mac);
	 
	 /**
	  * 固件更新进度回调 
	  * @param current  己上传大小 
	  * @param total总大小
	  */
	 void onDownloadProgress(long current,long total);
	 
	 /**
	  * 找到一个设备
	  * @param deviceInfo
	  */
	 void foundOneDevice(DeviceInfo deviceInfo);
	 
	 
 
}
