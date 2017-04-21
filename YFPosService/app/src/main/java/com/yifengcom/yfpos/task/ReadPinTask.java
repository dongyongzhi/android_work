package com.yifengcom.yfpos.task;

import android.os.AsyncTask;

import com.yifengcom.yfpos.DefaultDeviceComm;
import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.DevicePackage.DevicePackageSequence;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.exception.MPOSException;
import com.yifengcom.yfpos.listener.ReaderListeners.ReadPinListener;
import com.yifengcom.yfpos.model.syn.ReadPin;

/**
 * 读取pin密文
 * @author qc
 *
 */
public class ReadPinTask  extends AsyncTask<Void,byte[],Integer>{

	private final static YFLog logger = YFLog.getLog(ReadPinTask.class);
	
	private final DeviceComm deviceComm;
	private final ReadPin readPin;
	private final ReadPinListener listener;
	private byte[] pinBlock=new byte[8];
	private int pinNumber = 0;
	
	public ReadPinTask(DeviceComm deviceComm,ReadPin readPin,ReadPinListener listener) {
		this.deviceComm = deviceComm;
		this.readPin = readPin;
		this.listener = listener;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		//请求输入
		DevicePackage ack;
		DevicePackage recv;
		try {
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_REQUEST_PASSWORD,this.readPin.encode()));
			
			//接收上报数据
			recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_UPLOAD_PASSWORD, PackageBuilder.getNextPackageSequence()),(readPin.getTimeout() +2) * 1000);
			this.deviceComm.write(PackageBuilder.ackSucc(recv.getIndex(), recv.getCmd()));
			
			if(recv.getBody()==null) {
				return 0x01;
			}
			if(recv.getBody()[0]!= 0x00) {
				return recv.getBody()[0] & 0xFF;
			}
			
			//读取数据
			Thread.sleep(200);
			
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
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_READ_PASSWORD));
			if(ack.getBody()==null || ack.getBody().length<9) {
				return 0x01;
			}
			
			
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_READ_PASSWORD));
			if(ack.getBody()==null || ack.getBody().length<9) {
				return 0x01;
			}
			pinNumber  = ack.getBody()[0] &  0xFF;
			System.arraycopy(ack.getBody(), 1, pinBlock, 0, pinBlock.length);
			
			return ErrorCode.SUCC.getCode();
		
		} catch(MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			return ex.getErrorCode();
		} catch(Exception ex) {
			logger.e(ex.getMessage(), ex);
			return ErrorCode.UNKNOWN.getCode();
		}
	}
	
	/**
	 * 处理完成
	 */
	protected void onPostExecute(Integer code) {
		
		if(code != ErrorCode.SUCC.getCode()) { 
			if(code==ErrorCode.SWIPER_TIMEOUT.getCode()) {
				listener.onTimeout();
			} else {
				listener.onError(code, ((DefaultDeviceComm)deviceComm).getErrorMessage(code));
			}
		}else {
			this.listener.onReadPinSuccess(pinNumber, pinBlock);
		}
	}

}
