package com.yifengcom.yfpos.api;

import java.io.ByteArrayOutputStream;

import com.yifengcom.yfpos.DefaultDeviceComm;
import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.exception.MPOSException;
import com.yifengcom.yfpos.tlv.TLVCollection;
import com.yifengcom.yfpos.tlv.support.TLVByteArray;
import com.yifengcom.yfpos.utils.StringUtils;

import android.os.AsyncTask;

public class SetDeviceDataTask  extends AsyncTask<String,Object,Integer>{

	private final static YFLog logger = YFLog.getLog(SetDeviceDataTask.class);
	private final SwiperListener listener;
	private final DeviceComm deviceComm;
	
	public SetDeviceDataTask(DeviceComm deviceComm,SwiperListener listener) {
		this.listener = listener;
		this.deviceComm = deviceComm;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		String customerNo = (String)params[0];
		String termNo = (String)params[1];
		String serialNo = (String)params[2]; 
		String batchNo = (String)params[3];
		
		try {
			writeDeviceParams(customerNo,termNo,serialNo,batchNo);
			
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
		if(code == ErrorCode.SUCC.getCode()) { 
			listener.onResultSuccess(0x32);
		} else {
			listener.onError(code, ((DefaultDeviceComm)deviceComm).getErrorMessage(code));
		}
	}
	
	/**
	 * 写入参数
	 * @param customerNo
	 * @param termNo
	 * @param serialNo
	 * @param batchNo
	 */
	public void writeDeviceParams(String customerNo,String termNo ,String serialNo,String batchNo) {
		byte[] body;
		
		try {
			if(!StringUtils.isEmpty(customerNo)) {
				customerNo = StringUtils.rightAddSpace(customerNo, 15);
				logger.d("写入商户号:%s",customerNo);
			
				body = new byte[19];
				body[0] = (byte)0x9F;
				body[1] = (byte)0x02;
				body[2] = (byte)customerNo.length();
				body[3] = (byte)(customerNo.length()>>8);
				System.arraycopy(customerNo.getBytes(), 0, body, 4, customerNo.length());
				deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SET_CACHE,body));
			}
			
			if(!StringUtils.isEmpty(termNo)){
				termNo =  StringUtils.rightAddSpace(termNo, 8);
				logger.d("写入终端号:%s",termNo);
				body = new byte[12];
				body[0] = (byte)0x9F;
				body[1] = (byte)0x03;
				body[2] = (byte)termNo.length();
				body[3] = (byte)(termNo.length()>>8);
				System.arraycopy(termNo.getBytes(), 0, body, 4, termNo.length());
				deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SET_CACHE,body));
			}
			//流水号
			if(!StringUtils.isEmpty(serialNo)) {
				if(serialNo.length()<8) {
					serialNo = StringUtils.leftAddZero(Integer.valueOf(serialNo), 6);
				}
				logger.d("写入流水号:%s",serialNo);
				body = new byte[10];
				body[0] = (byte)0x9F;
				body[1] = (byte)0x04;
				body[2] = (byte)serialNo.length();
				body[3] = (byte)(serialNo.length()>>8);
				System.arraycopy(serialNo.getBytes(), 0, body, 4, serialNo.length());
				deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SET_CACHE,body));
			}
			
			//批次号
			if(!StringUtils.isEmpty(batchNo)) {
				if(batchNo.length()<8) {
					batchNo = StringUtils.leftAddZero(Integer.valueOf(batchNo), 6);
				}
				logger.d("写入批次号:%s",batchNo);
				body = new byte[10];
				body[0] = (byte)0x9F;
				body[1] = (byte)0x05;
				body[2] = (byte)batchNo.length();
				body[3] = (byte)(batchNo.length()>>8);
				System.arraycopy(batchNo.getBytes(), 0, body, 4, batchNo.length());
				deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SET_CACHE,body));
			}
			
		} catch(MPOSException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new MPOSException(ErrorCode.UNKNOWN.getCode(),ErrorCode.UNKNOWN.getDefaultMessage());
		}
	}

	/**
	 * tlv格式写入
	 * @param customerNo
	 * @param termNo
	 * @param serialNo
	 * @param batchNo
	 */
	public void writeDeviceParamsTLV(String customerNo,String termNo ,String serialNo,String batchNo) {
		
		try {
			
			TLVCollection tlvs = new TLVCollection();
			
			if(!StringUtils.isEmpty(customerNo)) {
				logger.d("写入商户号:%s",customerNo);
				customerNo = StringUtils.rightAddSpace(customerNo, 15);
				tlvs.add(new TLVByteArray(0x9F02,customerNo.getBytes()));
			}
			
			if(!StringUtils.isEmpty(termNo)){
				termNo =  StringUtils.rightAddSpace(termNo, 8);
				logger.d("写入终端号:%s",termNo);
				tlvs.add(new TLVByteArray(0x9F03,termNo.getBytes()));
			}
			//流水号
			serialNo = StringUtils.isEmpty(serialNo) ? "000000" : serialNo;
			if(serialNo.length()<8) {
				serialNo = StringUtils.leftAddZero(Integer.valueOf(serialNo), 6);
			}
			logger.d("写入流水号:%s",serialNo);
			tlvs.add(new TLVByteArray(0x9F04,serialNo.getBytes()));
			
			//批次号
			batchNo = StringUtils.isEmpty(batchNo) ? "000000" : batchNo;
			if(batchNo.length()<8) {
				batchNo = StringUtils.leftAddZero(Integer.valueOf(batchNo), 6);
			}
			logger.d("写入批次号:%s",batchNo);
			tlvs.add(new TLVByteArray(0x9F05,batchNo.getBytes()));
			 
			ByteArrayOutputStream os = new ByteArrayOutputStream(500);
			tlvs.encode(os);
			deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SET_CACHE,os.toByteArray()));
			
		} catch(MPOSException ex) {
			throw ex;
		} catch(Exception ex) {
			throw new MPOSException(ErrorCode.UNKNOWN.getCode(),ErrorCode.UNKNOWN.getDefaultMessage());
		}
	}
}
