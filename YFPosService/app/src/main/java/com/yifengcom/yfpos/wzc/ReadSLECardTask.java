package com.yifengcom.yfpos.wzc;

import com.yifengcom.yfpos.DefaultDeviceComm;
import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.api.SwiperListener;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.codec.DevicePackage.DevicePackageSequence;
import com.yifengcom.yfpos.exception.MPOSException;

import android.os.AsyncTask;


/**
 * 读卡
 */
public class ReadSLECardTask extends AsyncTask<Integer, Object, Integer> {

	private final static YFLog logger = YFLog.getLog(ReadSLECardTask.class);
	private final SwiperListener listener;
	private final DeviceComm deviceComm;
	private byte[] result;

	public ReadSLECardTask(DeviceComm deviceComm, SwiperListener listener) {
		this.listener = listener;
		this.deviceComm = deviceComm;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		try {
			int offset = params[0];
			int len = params[1];
			DevicePackage recv;
			byte[] timeout = new byte[1];
			timeout[0] = (byte)60;
			logger.d("打开SLE4442寻卡");
			this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SLE_FIND, timeout));
			logger.d("等待SLE4442寻卡结果上报");
			recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_SLE_FIND_RESULT, PackageBuilder.getNextPackageSequence()), 60 * 1000);
			byte[] recvData = recv.getBody();
			if(recvData==null) {
				return ErrorCode.UNKNOWN.getCode();
			}
			if(recvData[0] == 0x00){
				//寻卡成功
				byte[] data = new byte[2];
				data[0] = (byte) offset;
				data[1] = (byte) len;
				recv = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SLE_READ, data));
				result = recv.getBody();
				this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SLE_POWER_DOWN));
			}else if(recvData[0] == 0x01){
				return ErrorCode.SWIPER_FAIL.getCode();
			}else{
				return ErrorCode.TIMEOUT.getCode();
			}
		} catch(MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			return ex.getErrorCode();
		} catch(Exception ex) {
			logger.e(ex.getMessage(), ex);
			return ErrorCode.UNKNOWN.getCode();
		}

		return ErrorCode.SUCC.getCode();
	}

	protected void onPostExecute(Integer code) {
		if(code != ErrorCode.SUCC.getCode()) { 
			//分发错误事件
			if(code==ErrorCode.SWIPER_TIMEOUT.getCode() || code == ErrorCode.INPUT_PASSWORD_TIMEOUT.getCode()) {
				listener.onTimeout();
			} else if(code == ErrorCode.CANCEL.getCode() || code == ErrorCode.CANCEL_INPUT_PASSWORD.getCode()) {
				listener.onTradeCancel();
			} else {
				listener.onError(code, ((DefaultDeviceComm)deviceComm).getErrorMessage(code));
			}
		} else {
			this.listener.onReadSuccess(result);
		}
	}


}
