package com.yifengcom.yfpos.api;

import com.yifengcom.yfpos.DefaultDeviceComm;
import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.codec.DevicePackage.DevicePackageSequence;
import com.yifengcom.yfpos.exception.MPOSException;

import android.os.AsyncTask;


/**
 * 打开IC卡
 * @author zzc
 *
 */
public class OpenICTask extends AsyncTask<Void, Object, Integer> {

	private final static YFLog logger = YFLog.getLog(OpenICTask.class);
	private final SwiperListener listener;
	private final DeviceComm deviceComm;
	private byte[] result;

	public OpenICTask(DeviceComm deviceComm, SwiperListener listener) {
		this.listener = listener;
		this.deviceComm = deviceComm;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		try {
			DevicePackage recv;
			
			byte[] timeout = new byte[1];
			timeout[0] = (byte)60;
			
			logger.d("打开IC卡寻卡");
			this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_IC_FIND, timeout));
			logger.d("等待IC卡寻卡结果上报");
			recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_IC_FIND_RESULT, PackageBuilder.getNextPackageSequence()), 60 * 1000);
			if (recv == null) {
				return ErrorCode.UNKNOWN.getCode();
			}
			result = recv.getBody();
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
