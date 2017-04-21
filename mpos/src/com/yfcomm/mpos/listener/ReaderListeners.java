package com.yfcomm.mpos.listener;

import com.yfcomm.mpos.model.ack.DeviceVersion;


/**
 * 读取监听
 * @author qc
 *
 */
public interface ReaderListeners {

	/**
	 * 获取设备信息回调用
	 * @author qc
	 *
	 */
	public interface GetDeviceInfoListener extends ErrorListener{

		void onGetDeviceInfoSuccess(DeviceVersion version);
	}
	
	/**
	 * 获取设备时间
	 * @author qc
	 *
	 */
	public interface GetDateTimeListener  extends ErrorListener{
		
		void onGetDateTimeSuccess(String dateTime);
	}
	
	
	/**
	 * 获取设备数据回调
	 * @author qc
	 *
	 */
	public interface GetDeviceDataListener extends ErrorListener{
		
		void onGetDeviceDataSuccess(byte[] data);
	}
	
	/**
	 * 刷卡监听
	 * @author qc
	 *
	 */
	public interface WaitingCardListener extends ErrorListener,TimeoutListener{
		
		/**
		 * 成功回调
		 * @param ksn   KSN号
		 * @param trxTime  交易时间 YYYYMMDDHHMMSS
		 * @param random   随机数8位
		 * @param track2Cipher   二磁道信息
		 * @param track3Cipher 三磁道信息
		 * @param mac    MAC8位
		 * @param icTrxData  交易数据
		 */
		void onWaitingCardSuccess(String ksn,String trxTime,byte[] random,byte[] track2Cipher,byte[] track3Cipher,byte[] mac,byte[] icTrxData);
	}
	
	/**
	 * 刷卡监听 
	 * @author qc
	 *
	 */
	public interface SwiperCardListener extends ErrorListener,TimeoutListener{
		
		/**
		 * 成功回调
		 * @param ksn   KSN号
		 * @param trxTime  交易时间 YYYYMMDDHHMMSS
		 * @param random   随机数8位
		 * @param track2Cipher   二磁道信息
		 * @param track3Cipher 三磁道信息
		 * @param mac    MAC8位
		 * @param icTrxData  交易数据
		 * @param pinBlock pinBlock
		 */
		void onSwiperSuccess(String ksn,String trxTime,byte[] random,byte[] track2Cipher,byte[] track3Cipher,byte[] mac,byte[] icTrxData,byte[] pinBlock);
	}
	
	/**
	 * 读取pin 监听
	 * @author qc
	 *
	 */
	public interface ReadPinListener extends ErrorListener,TimeoutListener{
		
		/**
		 * 成功架设
		 * @param pinNumber  输入密码个数
		 * @param pinBlock   PINBLOCK密文
		 */
		void onReadPinSuccess(int pinNumber,byte[] pinBlock);
	}
}
