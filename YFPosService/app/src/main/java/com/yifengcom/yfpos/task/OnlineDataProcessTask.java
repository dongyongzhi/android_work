package com.yifengcom.yfpos.task;

import android.os.AsyncTask;

import com.yifengcom.yfpos.DefaultDeviceComm;
import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.exception.MPOSException;
import com.yifengcom.yfpos.listener.SetListeners.PBOCOnlineDataProcessListener;

/**
 * PBOC 二次授权处理
 * @author qc
 *
 */
public class OnlineDataProcessTask  extends AsyncTask<Void,byte[],Integer>{
	private final static YFLog logger = YFLog.getLog(OnlineDataProcessTask.class);
	
	private final DeviceComm deviceComm;
	private final PBOCOnlineDataProcessListener listener;
	private final byte[] onlineData;
	
	public OnlineDataProcessTask(DeviceComm deviceComm,byte[] onlineData,PBOCOnlineDataProcessListener listener) {
		this.deviceComm = deviceComm;
		this.listener = listener;
		this.onlineData = onlineData;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		DevicePackage ack;
		DevicePackage recv;
		
		try {
			
			logger.d("执行PBOC二次流程");
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_TWO_AUTH,this.onlineData));
			
			//接收上报数据
			logger.d("等待执行PBOC二次流程数据上报");
			recv = this.deviceComm.recv(PackageBuilder.CMD_PBOC_UPLOAD_TWO_RESULT, PackageBuilder.getNextPackageSequence());
			this.deviceComm.write(PackageBuilder.ackSucc(ack.getIndex(), ack.getCmd()));
			
			//执行异常
			if(recv.getBody()==null || recv.getBody()[0] == 0x07) {
				return ErrorCode.TERM_EXEC_EXCEPTION.getCode();
			}
			
			//获取数据
			Thread.sleep(200);
			logger.d("读取PBOC二次流程结果");
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_READ_TWO));
			byte[] execResult = ack.getBody();
			if(ack.getBody()==null) {
				return ErrorCode.TERM_EXEC_EXCEPTION.getCode();
			}
			
			//流程结束指令
			logger.d("结束PBOC流程指令");
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_END));
			if(ack.getBody()==null || ack.getBody()[0] == 0x01) {
				//执行失败
				return ErrorCode.CMD_EXEC_FAIL.getCode();
			}
			
			this.publishProgress(execResult);
			
		} catch(MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			return ex.getErrorCode();
		} catch(Exception ex) {
			logger.e(ex.getMessage(), ex);
			return ErrorCode.UNKNOWN.getCode();
		}
		return ErrorCode.SUCC.getCode();
	}
	
	/**
	 * 处理结果
	 */
	protected void onProgressUpdate(byte[]... args) {
		this.listener.onWriteProcessSuccess(args[0]);
	}
	
	/**
	 * 处理完成
	 */
	protected void onPostExecute(Integer code) {
		if(code != ErrorCode.SUCC.getCode()) { 
			listener.onError(code, ((DefaultDeviceComm)deviceComm).getErrorMessage(code));
		}
	}

}
