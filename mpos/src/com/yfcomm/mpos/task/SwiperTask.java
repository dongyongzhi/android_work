package com.yfcomm.mpos.task;

import java.io.ByteArrayOutputStream;

import android.os.AsyncTask;

import com.yfcomm.mpos.DefaultDeviceComm;
import com.yfcomm.mpos.DeviceComm;
import com.yfcomm.mpos.ErrorCode;
import com.yfcomm.mpos.YFLog;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.DevicePackage.DevicePackageSequence;
import com.yfcomm.mpos.codec.StandardSwiperDecoder;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.mpos.exception.MPOSException;
import com.yfcomm.mpos.listener.ErrorListener;
import com.yfcomm.mpos.listener.TimeoutListener;
import com.yfcomm.mpos.model.ReadCardModel;
import com.yfcomm.mpos.model.syn.OpenReadCard;
import com.yfcomm.mpos.tlv.TLVCollection;
import com.yfcomm.mpos.tlv.support.BERTLV;
import com.yfcomm.mpos.utils.ByteUtils;
import com.yfcomm.mpos.utils.StringUtils;

/**
 * 刷卡任务
 * @author qc
 *
 */
public class SwiperTask extends AsyncTask<Void,Object,Integer>{

	protected final  YFLog logger = YFLog.getLog(this.getClass());
			
	/*======= 处理进度定义 ========*/
	public final static int EVENT_WAITINGFORCARDSWIPE = 0; //等待刷卡回调
	public final static int EVENT_DETECTICC = 1; //检测到IC卡
	public final static int EVENT_DECODINGSTART = 2; //开始解码回调
	
	public final static int EVENT_READ_CARD_DATA = 3; //读取数据
	//private final static int READ_PINBLOCK = 4; //读取pinBlock
	public final static int EVENT_COMPLETE = 5;  //完成
	
	private final DeviceComm deviceComm;
	private final OpenReadCard openReadCard;
	private final SwiperTaskListener listener;
	private StandardSwiperDecoder swiperDecoder = new StandardSwiperDecoder();
	
	private boolean isIc = false;
	
	public SwiperTask(DeviceComm deviceComm,OpenReadCard openReadCard, SwiperTaskListener listener) {
		this.deviceComm = deviceComm;
		this.openReadCard = openReadCard;
		this.listener = listener;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		try {
		   int code = doTask();
		   if(code == ErrorCode.SUCC.getCode()) {
			   //完成
			   logger.d("执行结束");
			   this.publishProgress(EVENT_COMPLETE);
		   }
		   return code;
		   
		} catch(MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			return ex.getErrorCode();
		} catch(Exception ex) {
			logger.e(ex.getMessage(), ex);
			return ErrorCode.UNKNOWN.getCode();
		}
	}
	
	protected Integer doTask() throws Exception {
		
		DevicePackage ack;
		DevicePackage recv;
		int code = ErrorCode.SUCC.getCode();
		
		//请求刷卡
		logger.d("请求刷卡");
		this.publishProgress(EVENT_WAITINGFORCARDSWIPE);
		ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_OPEN_SWIPER,openReadCard.encode()));
		
		//刷卡上报结果
		logger.d("等待刷卡上报结果");
		recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_UPLOAD_CARD,  PackageBuilder.getNextPackageSequence()), (openReadCard.getTimeout() +1) * 1000);
		this.deviceComm.write(PackageBuilder.ackSucc(recv.getIndex(), recv.getCmd()));
		
		this.publishProgress(EVENT_DECODINGSTART);
		
		byte[] recvData = recv.getBody();
		if(recvData==null) {
			return ErrorCode.UNKNOWN.getCode();
		}
		if(recvData[0] == 0x00 || recvData[0] == 0x06) {
			//刷卡成功，加密处理成功 / 手输成功
			//读取刷卡信息
			code = this.readMagnetic(ack);
			if(code !=ErrorCode.SUCC.getCode()) {
				return code;
			}
			
		} else if(recvData[0] == 0x01 || recvData[0] == 0x02) {
			//刷卡失败
			return ErrorCode.SWIPER_FAIL.getCode();
		} else if(recvData[0] == 0x03) {
			// 刷卡超时
			return ErrorCode.SWIPER_TIMEOUT.getCode();
		} else if(recvData[0] == 0x04) {
			//取消刷卡
			return ErrorCode.CANCEL.getCode();
		}else if(recvData[0] == 0x05) {
			
			//=================IC卡已插入  进入pboc 流程==================//
			code = this.readIc(ack);
			if(code !=ErrorCode.SUCC.getCode()) {
				return code;
			}
		}
		return code;
	}
	
	
	/**
	 * 处理完成
	 */
	protected void onPostExecute(Integer code) {
		
		if(this.listener != null) {
			if(code != ErrorCode.SUCC.getCode()) { 
				if(code==ErrorCode.SWIPER_TIMEOUT.getCode()) {
					listener.onTimeout();
				} else {
					listener.onError(code, ((DefaultDeviceComm)deviceComm).getErrorMessage(code));
				}
			} else {
				//回调刷卡信息
				this.listener.onSwiperSuccess(swiperDecoder, isIc);
			}
		}
	}
	
	/**
	 * 读取磁条卡信息
	 * @param ack MPOS应答包
	 * @return 错误码
	 * @throws InterruptedException 
	 */
	private int readMagnetic(DevicePackage ack) throws InterruptedException {
		setIc(false);
		
		Thread.sleep(200);
		ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_READ_CARD));
		//返回加密数据
		this.publishProgress(EVENT_READ_CARD_DATA,ReadCardModel.MAGNETIC_CARD,ack.getBody());
		//获取终端上传随机数
		if(ack.getBody()==null || ack.getBody().length<23) {
			return ErrorCode.SWIPER_FAIL.getCode();
		}else {
			 this.swiperDecoder.decodeMagnetic(ack.getBody());
		}
		return ErrorCode.SUCC.getCode();
	}
	
	/**
	 * 获取IC卡内容
	 * @param ack  MPOS应答包
	 * @return 错误码
	 * @throws InterruptedException 
	 */
	private int readIc(DevicePackage ack) throws InterruptedException {
		setIc(true);
		
		this.publishProgress(EVENT_DETECTICC);
		logger.d("开始执行PBOC标准流程");
		Thread.sleep(200);
		// 开始执行PBOC标准流程
		//TLV 参数
		ByteArrayOutputStream out = new ByteArrayOutputStream(100);
		TLVCollection tlvs = new TLVCollection();
		//授权金额
		long money = ((Double)(openReadCard.getTrxMoney() * 100)).longValue();
		tlvs.add(new BERTLV(0x9F02,ByteUtils.hexToByte(StringUtils.leftAddZero(money,12))));
		//返现金额
		tlvs.add(new BERTLV(0x9F03,new byte[6]));
		//交易类型
		tlvs.add(new BERTLV(0x9C,openReadCard.getTrxType().getValue()));
		//自定义的交易类型
		tlvs.add(new BERTLV(0xDF7C,(byte)0x01));
		//读应用数据
		tlvs.add(new BERTLV(0xDF71,(byte)0x01));
		//强制联机
		tlvs.add(new BERTLV(0xDF72,(byte)0x01));
		tlvs.add(new BERTLV(0xDF73,(byte)0x00));
		tlvs.encode(out);
		ack =  this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_START_PROCESS,out.toByteArray()));
		
		//接收上报执行PBOC标准流程结果
		logger.d("等待接收上报执行PBOC标准流程结果");
		DevicePackage recv = this.deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_PBOC_UPLOAD_RESULT, PackageBuilder.getNextPackageSequence()),60*1000);
		this.deviceComm.write(PackageBuilder.ackSucc(recv.getIndex(), recv.getCmd()));
		
		//解析结果
		if(recv.getBody()==null || recv.getBody()[0] ==0x07) {
			return ErrorCode.SWIPER_FAIL.getCode();
		}
		
		//获取数据
		logger.d("获取数据PBOC标准流程结果");
		Thread.sleep(200);
		ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_READ));
		byte[] ic = ack.getBody();
		
		logger.d("结束PBOC流程指令");
		ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_END));
		
		//获取终端上传随机数
		if(ic==null || ic.length<15) {
			return ErrorCode.SWIPER_FAIL.getCode();
		} else {
			this.swiperDecoder.decodeIc(ic);
		}
		return ErrorCode.SUCC.getCode();
	}

	public boolean isIc() {
		return isIc;
	}

	public void setIc(boolean isIc) {
		this.isIc = isIc;
	}

	public StandardSwiperDecoder getSwiperDecoder() {
		return swiperDecoder;
	}

	public void setSwiperDecoder(StandardSwiperDecoder swiperDecoder) {
		this.swiperDecoder = swiperDecoder;
	}
	
	
	public DeviceComm getDeviceComm() {
		return deviceComm;
	}

	public  interface SwiperTaskListener extends ErrorListener,TimeoutListener {
		
		void onSwiperSuccess(StandardSwiperDecoder decoder,boolean isIc);
	}
}
