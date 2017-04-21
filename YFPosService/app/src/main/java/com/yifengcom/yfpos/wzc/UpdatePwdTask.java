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
import com.yifengcom.yfpos.utils.ByteUtils;

import android.os.AsyncTask;


/**
 * 修改密码
 */
public class UpdatePwdTask extends AsyncTask<String, Object, Integer> {

	private final static YFLog logger = YFLog.getLog(UpdatePwdTask.class);
	private final SwiperListener listener;
	private final DeviceComm deviceComm;
	private byte[] result;

	public UpdatePwdTask(DeviceComm deviceComm, SwiperListener listener) {
		this.listener = listener;
		this.deviceComm = deviceComm;
	}

	@Override
	protected Integer doInBackground(String... params) {
		try {
			String pwd = (String) params[0];
			String newPwd = (String) params[1];
			
			DevicePackage recv;
			byte[] timeout = new byte[1];
			timeout[0] = (byte)60;
			this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SLE_FIND, timeout));
			recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_SLE_FIND_RESULT, PackageBuilder.getNextPackageSequence()), 60 * 1000);
			byte[] recvData = recv.getBody();
			if(recvData==null) {
				return ErrorCode.UNKNOWN.getCode();
			}
			if(recvData[0] == 0x00){
				//寻卡成功
				byte[] body = new byte[6];
				System.arraycopy(ByteUtils.hexToByte(pwd), 0, body, 0, 3);
				System.arraycopy(ByteUtils.hexToByte(newPwd), 0, body, 3, 3);
				recv = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SLE_UPDATE_PWD, body));
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
			this.listener.onResultSuccess(result[0]);
		}
	}


}
