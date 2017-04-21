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
 * 打印流程
 * @author zzc
 *
 */
public class PrintDataTask extends AsyncTask<byte[], Object, Integer> {

	private final static YFLog logger = YFLog.getLog(PrintDataTask.class);
	private final SwiperListener listener;
	private final DeviceComm deviceComm;
	private int result;

	public PrintDataTask(DeviceComm deviceComm, SwiperListener listener) {
		this.listener = listener;
		this.deviceComm = deviceComm;
	}

	@Override
	protected Integer doInBackground(byte[]... body) {
		try {
			byte[] data = body[0];
			DevicePackage recv;
			
			logger.d("请求打印");
			this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PRINT, data));
			logger.d("等待打印结果");
			recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_PRINT_RESULT, PackageBuilder.getNextPackageSequence()), 30 * 1000);
			if (recv != null) {
				result = recv.getBody()[0] & 0xFF;
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
			this.listener.onResultSuccess(result);
		}
	}


}
