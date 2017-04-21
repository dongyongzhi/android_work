package com.yifengcom.yfpos.api;


import java.nio.ByteBuffer;
import java.util.Date;

import android.content.Context;
import android.os.Handler;

import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.DeviceDetect;
import com.yifengcom.yfpos.DeviceInfo;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.YFMPos;
import com.yifengcom.yfpos.adapter.bt.BluetoothDeviceDetect;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.codec.DevicePackage.DevicePackageSequence;
import com.yifengcom.yfpos.exception.MPOSException;
import com.yifengcom.yfpos.listener.CalculateMacListener;
import com.yifengcom.yfpos.listener.DeviceSearchListener;
import com.yifengcom.yfpos.listener.DeviceStateChangeListener;
import com.yifengcom.yfpos.listener.DownloadListener;
import com.yifengcom.yfpos.listener.SetListeners.DisplayListener;
import com.yifengcom.yfpos.listener.SetListeners.PBOCOnlineDataProcessListener;
import com.yifengcom.yfpos.listener.SetListeners.ResetListener;
import com.yifengcom.yfpos.listener.SetListeners.SetDateTimeListener;
import com.yifengcom.yfpos.listener.SetListeners.SetDeviceDataListener;
import com.yifengcom.yfpos.listener.SetListeners.UpdateAidListener;
import com.yifengcom.yfpos.listener.SetListeners.UpdateMainKeyListener;
import com.yifengcom.yfpos.listener.SetListeners.UpdatePublicKeyListener;
import com.yifengcom.yfpos.listener.SetListeners.UpdateWorkKeyListener;
import com.yifengcom.yfpos.listener.support.AckListener;
import com.yifengcom.yfpos.model.Action;
import com.yifengcom.yfpos.model.syn.OpenReadCard;
import com.yifengcom.yfpos.model.syn.TrxType;
import com.yifengcom.yfpos.service.DeviceModel;
import com.yifengcom.yfpos.service.WorkKey;
import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfpos.wzc.ReadSLECardTask;
import com.yifengcom.yfpos.wzc.UpdatePwdTask;
import com.yifengcom.yfpos.wzc.WriteSLECardTask;

/**
 * 怡丰刷卡器
 * @author qc
 *
 */
public class SwiperController implements DeviceStateChangeListener,ResetListener,DownloadListener,SetDeviceDataListener {
	
	private final static YFLog logger = YFLog.getLog(SwiperController.class);
	
	private final Context context;
	private final SwiperListener listener;
	private final YFMPos mpos;
	private DeviceDetect deviceDetect;
	
	public SwiperController(Context context,SwiperListener listener) {
		this.context = context;
		this.listener = listener;
		this.mpos = YFMPos.getInstance(this.context);
	}
	
	public YFMPos getMpos() {
		return mpos;
	}

	/**
	 * 重启设备
	 */
	public void restart() {
		try {
			this.mpos.reset(this);
		} catch(Exception ex) {
			logger.e(ex.getMessage(), ex);
		}
	}
	
	public void cancel() {
		try {
			logger.d("取消指令");
			this.mpos.cancel();
		} catch(MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	/**
	 * 设置时间
	 * @param time  时间
	 */
	public void setDateTime(Date time){
		this.mpos.setDateTime(time, new SetDateTimeListener(){

			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}

			@Override
			public void onSetDateTimeSuccess() {
				listener.onResultSuccess(0x31);
			}
		});
	}
	
	/**
	 * 获取设备信息
	 */
	public void getDeviceInfo() {
		this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_DEVICE_VERSION),
				new AckListener<SwiperListener>(this.listener){
					@Override
					public void doCallback(SwiperListener listener,DevicePackage pack) {
						DeviceModel model = new DeviceModel();
						byte[] data=pack.getBody();
						String customerNo = new String(data,0,15);
						model.setCustomerNo(customerNo);
						String termNo = new String(data,15,8);
						model.setTermNo(termNo);
						String batchNo = ByteUtils.byteToHex(data,23,3);
						model.setBatchNo(batchNo);
						int existsMainKey = data[26] == 0x01 ? 1 : 0;
						model.setExistsMainKey(existsMainKey);
						String terVersion = new String(data,27,6);
						model.setTerVersion(terVersion);
						String softVersion = new String(data,33,13);
						model.setSoftVersion(softVersion);
						String sn = new String(data,46,16);
						model.setSn(sn);
						listener.onGetDeviceInfoSuccess(model);
					}
		});
	 
	}
	
	/**
	 * 获取PSAM、ST720 ATR内容
	 */
	public void getPsamAndSt720Info() {
		this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_ST720),
				new AckListener<SwiperListener>(this.listener){
					@Override
					public void doCallback(SwiperListener listener,DevicePackage pack) {
						String psam = "error";
						String st720 = "error";
						byte[] data=pack.getBody();
						byte[] data1;
						if(data[0] == 0x01){
							int plen = data[1];
							psam = ByteUtils.byteToHex(data, 2, plen);
							data1 = new byte[data.length - (2+ plen)];
							System.arraycopy(data, 2+plen, data1, 0, data1.length);
							if(data1[0] == 0x01){
								int slen = data1[1];
								st720 = ByteUtils.byteToHex(data1, 2, slen);
							}
						}else{
							if(data[2] == 0x01){
								int slen = data[3];
								st720 = ByteUtils.byteToHex(data, 4, slen);
							}
						}
						listener.onGetPsamAndSt720Info(psam, st720);
					}
		});
	 
	}
	
	/**
	 * 获取PSAM卡号
	 */
	public void getPsamInfo() {
		this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_INFO),
				new AckListener<SwiperListener>(this.listener){
					@Override
					public void doCallback(SwiperListener listener,DevicePackage pack) {
						String psamNo = "error";
						byte[] data= pack.getBody();
						if(data.length == 8){
							psamNo = new String(data);
						}
						listener.onReadPsamNo(psamNo);
					}
		});
	 
	}
	
	/**
	 * 打开ADB
	 */
	public void openADB(){
		exeKeyCard(PackageBuilder.CMD_OPEN_ADB);
	}
	
	/**
	 * 关闭ADB
	 */
	public void closeADB(){
		exeKeyCard(PackageBuilder.CMD_CLOSE_ADB);
	}
	
	/**
	 * 清除安全触发
	 */
	public void clearSecurity(){
		exeKeyCard(PackageBuilder.CMD_CLEAR_SECURITY_TRIGGER);
	}
	
	/**
	 * 清除密钥证书
	 */
	public void clearKey(){
		exeKeyCard(PackageBuilder.CMD_CLEAR_KEY_CERTIFICATE);
	}
	
	public void exeKeyCard(int cmd) {
		try{
			byte[] timeout = new byte[1];
			timeout[0] = (byte)30;
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(cmd,timeout),30*1000);
			if(dev != null){
				listener.onReadSuccess(dev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	 
	}
	
	/**
	 * 设置设备信息
	 * @param customerNo 商户号
	 * @param termNo  终端号
	 * @param serialNo  流水号
	 * @param batchNo   批次号
	 */
	public void setDeviceData(String customerNo,String termNo,String serialNo ,String batchNo) {
		new SetDeviceDataTask(this.mpos.getDeviceComm(),this.listener).execute(customerNo,termNo,serialNo,batchNo);
	}
	
	/**
	 * 写入商户号
	 * @param customerNo
	 */
	public void writeCustomerNo(String customerNo) {
		this.mpos.setDeviceData(0x9F02, customerNo.getBytes(), this);
	}

	/**
	 * 写入终端号
	 * @param termNo
	 */
	public void writeTermNo(String termNo) {
		this.mpos.setDeviceData(0x9F03, termNo.getBytes(), this);
	}
	/**
	 * 写入流水号
	 * @param serialNo
	 */
	public void writeSerialNo(String serialNo) {
		this.mpos.setDeviceData(0x9F04, serialNo.getBytes(), this);
	}
	/**
	 * 写入批次号
	 * @param batchNo
	 */
	public void writeBatchNo(String batchNo) {
		this.mpos.setDeviceData(0x9F05, batchNo.getBytes(), this);
	}
	
	/**
	 * 写入设备信息
	 * @param customerNo 商户号
	 * @param termNo  终端号
	 * @param serialNo  流水号
	 * @param batchNo   批次号
	 */
	public void synWriteDeviceData(String customerNo,String termNo,String serialNo ,String batchNo) {
		SetDeviceDataTask task = new SetDeviceDataTask(this.mpos.getDeviceComm(),this.listener);
		task.writeDeviceParams(customerNo, termNo, serialNo, batchNo);
	}
	
	/**
	 * 写入主密钥
	 * @param key  密文
	 */
	public void writeMainKey(byte[] key) {
		this.mpos.updateMainKey(0, key, new UpdateMainKeyListener(){
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode,errorMessage);
			}
			@Override
			public void onUpdateMainKeySuccess() {
				listener.onResultSuccess(0x34);
			}
		});
	}
	
	/**
	 * 写入主密钥
	 * @param key 密文
	 */
	public void synWriteMainKey(byte[] key) {
		byte[] body = new byte[27];
		body[1] = 0;
		body[2] = key==null || key.length==0 ? 0x00 : (byte)24;
		if(key!=null) {
			System.arraycopy(key, 0, body, 3, key.length< 24 ? key.length : 24);
		}
		this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE_MAIN_KEY, body));
	}
	
	/**
	 * 计算mac
	 * @param data 报文内容
	 */
	public void calculateMac(byte[] data) {
		this.mpos.calculateMac(data, new CalculateMacListener() {
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode,errorMessage);
			}

			@Override
			public void onCalculateMacSuccess(byte[] mac) {
				listener.onCalculateMacSuccess(mac);
			}
		});
	}
	
	/**
	 * 计算mac
	 * @param data  报文内容
	 * @return  计算结果
	 */
	public byte[] synCalculateMac(byte[] data) {
		try {
			DevicePackage ack =  this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_CALCULATE_MAC, data));
			return ack.getBody();
		}catch(Exception ex) {
			logger.e(ex.getMessage());
			return null;
		}
	}
	
	/**
	 * 写入工作密钥
	 * @param len   密钥长度
	 * @param bWorkKey  密文
	 */
	public void writeWorkKey(WorkKey key) {
		this.mpos.updateWorkKey(key, new UpdateWorkKeyListener(){
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}
			@Override
			public void onUpdateWorkKeySuccess() {
				listener.onResultSuccess(0x35);
			}
		});
	}
	
	/**
	 * 写入工作密钥 同步
	 * @param key
	 */
	public void synWriteWorkKey(WorkKey key) {
		this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE_WORK_KEY, key.encode()));
	}

	/**
	 * 启动刷卡
	 * @param timeout  超时时间 单位秒
	 * @param nAmount   金额 单位分
	 * @param brushCard 刷卡模式 0：不支持降级，1：支持降级
	 * @param handler 监听密码
	 */
	public void startSwiper(int timeout,long nAmount,int brushCard,byte type,Handler handler) {

		OpenReadCard readCard = new OpenReadCard();
		//超时
		readCard.setTimeout((byte)timeout);
		//交易类型
		readCard.setTrxType(TrxType.convert(type));
		//金额
		readCard.setTrxMoney(nAmount / 100.0);
		readCard.setActiveCode(new byte[]{(byte)brushCard,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
		//执行刷卡
		new SwiperAndReadPinTask(this.mpos.getDeviceComm(),readCard,this.listener,handler).execute();
	}
	
	
	/**
	 * 通知打印
	 * * @param body  数据包
	 */
	public void startPrint(byte[] body) {
		new PrintDataTask(this.mpos.getDeviceComm(), this.listener).execute(body);
	}
	
	/**
	 * 读取电子现金余额
	 */
	public void readMoney() {
		new ReadMoneyTask(this.mpos.getDeviceComm(), this.listener).execute();
	}
	
	/**
	 * 读取射频卡
	 */
	public void readRFCard() {
		new ReadRFCardTask(this.mpos.getDeviceComm(), this.listener).execute();
	}
	
	/**
	 * 打开射频卡
	 */
	public void openRFID() {
		new OpenRFIDTask(this.mpos.getDeviceComm(), this.listener).execute();
	}
	
	/**
	 * 发送射频命令
	 * @param data 报文内容
	 */
	public void sendRFIDCmd(byte[] data) {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_RFID_CMD_SET,data));
			if(dev != null){
				listener.onReadSuccess(dev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	 
	}
	
	/**
	 * 关闭射频
	 */
	public void closeRFID() {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_RFID_CLOSE));
			if(dev != null){
				listener.onResultSuccess(0x43);
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	
	
	/**
	 * 打开IC卡寻卡
	 */
	public void openIC() {
		new OpenICTask(this.mpos.getDeviceComm(), this.listener).execute();
	}
	
	/**
	 * IC卡数据交互
	 * @param data 报文内容
	 */
	public void sendICCmd(byte[] data) {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_IC_DATA_EXCHANGE,data));
			if(dev != null){
				listener.onReadSuccess(dev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	/**
	 * IC卡下电
	 */
	public void closeIC() {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_IC_POWER_DOWN));
			if(dev != null){
				listener.onResultSuccess(0x42);
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	
	/**
	 * PSAM卡复位
	 * @param data 报文内容
	 */
	public void resetPSAM(byte[] data) {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_RESET,data));
			if(dev != null){
				listener.onReadSuccess(dev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	/**
	 * 读PSAM卡
	 * @param data 报文内容
	 */
	public void readPSAM(byte[] data) {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_READ,data));
			if(dev != null){
				listener.onReadSuccess(dev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	/**
	 * 写PSAM卡
	 * @param data 报文内容
	 */
	public void writePSAM(byte[] data) {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_READ,data));
			if(dev != null){
				listener.onReadSuccess(dev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	
	public byte[] psamResetEx(int siteNo, int timeout){
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_RESET, new byte[]{(byte)siteNo}), timeout*1000);
			if(dev != null){
				return dev.getBody();
			}else
				return null;
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
			return null;
		}
		
	}
	
	
	public byte[] psamApduEx(int siteNo, byte[] send, int timeout){
		try{
			
			ByteBuffer buff = ByteBuffer.allocate(1 + send.length);
			buff.put((byte)siteNo)
				.put(send);
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_PSAM_READ, buff.array()), timeout*1000);
			if(dev != null){
				return dev.getBody();
			}else
				return null;
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
			return null;
		}
	}
	
	public byte[] icReset(byte[] atr){
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_IC_FIND));
			if(dev != null){
				return dev.getBody();
			}else
				return null;
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
			return null;
		}
		
	}
	
	
	public byte[] icApdu(byte[] send){
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_IC_DATA_EXCHANGE, send));
			if(dev != null){
				return dev.getBody();
			}else
				return null;
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
			return null;
		}
	}
	
	public byte[] icDown(){
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_IC_POWER_DOWN));
			if(dev != null){
				return dev.getBody();
			}else
				return null;
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
			return null;
		}
	}
	
	public byte[] rfidOpenEx(int timeout){
		if ((timeout>255) || (timeout<0)) timeout = 60;
		DeviceComm deviceComm = mpos.getDeviceComm();
		DevicePackage recv;
		try {
			
			
			byte[] tm = new byte[1];
			tm[0] = (byte)60;
			
			logger.d("射频寻卡开始");
			deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_RFID_OPEN, tm));
			recv = deviceComm.recv(new DevicePackageSequence(PackageBuilder.CMD_RFID_FIND_RESULT, PackageBuilder.getNextPackageSequence()), (timeout+5) * 1000);
			logger.d("射频寻卡结束");
			if (recv == null) {
				return null;
			}
		} catch(Exception ex) {
			this.listener.onError(99, ex.getMessage());
			return null;
		}
		return recv.getBody();
	}
	
	public byte[] rfidApdu(byte[] send, int timeout){
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_RFID_CMD_SET, send), timeout*1000);
			//this.mpos.getDeviceComm().recv(PackageBuilder.getCurrentPackageSequence());
			if(dev != null){
				return dev.getBody();
			}else
				return null;
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			if (listener != null)
					this.listener.onError(ex.getErrorCode(), ex.getMessage());
			return null;
		}
	}
	
	public int rfidCloseEx() {
		try{
			DevicePackage dev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_RFID_CLOSE));
			if(dev != null){
				return 1;
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
		}
		return -1;
	}
	
	/**
	 * 发送身份证数据
	 * @param data
	 */
	public byte[] sendIDCardData(byte[] data){
		try{
			this.mpos.getDeviceComm().write(PackageBuilder.syn(PackageBuilder.CMD_IDCARD_DOWN,data));
			DevicePackage rev = this.mpos.getDeviceComm().recv(new DevicePackageSequence(PackageBuilder.CMD_IDCARD_UP, PackageBuilder.getNextPackageSequence()),10 * 1000);
			if(rev != null){
				return rev.getBody();
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
		return null;
	}
	
	/**
	 * 自毁测试
	 */
	public void testDestroy() {
		try{
			byte[] body = new byte[1];
			body[0] = 1;
			DevicePackage rev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_DESTROY_TEST, body));
			if(rev != null){
				listener.onResultSuccess(rev.getBody()[0]);
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	/**
	 * 请求录入SN号
	 */
	public void requestSN() {
		try{
			byte[] timeout = new byte[1];
			timeout[0] = (byte)30;
			DevicePackage rev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_REQUEST_SN,timeout), 30*1000);
			if(rev != null){
				listener.onReadSuccess(rev.getBody());
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}
	
	/**
	 * SN号下发
	 */
	public void writeSN(byte[] body) {
		try{
			DevicePackage rev = this.mpos.getDeviceComm().execute(PackageBuilder.syn(PackageBuilder.CMD_WRITE_SN, body));
			if(rev != null){
				listener.onResultSuccess(rev.getBody()[0]);
			}
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
			this.listener.onError(ex.getErrorCode(), ex.getMessage());
		}
	}

        /**
	 * EMVTest
	 */
	public void EMVTest(byte[] body) {
		try{
			this.mpos.getDeviceComm().write(PackageBuilder.syn(PackageBuilder.CMD_EMV_TEST, body));
			
		}catch (MPOSException ex) {
			logger.e(ex.getMessage(), ex);
		}
	}
	/**
	 * 上传电子签名
	 * @param filePath  文件地址
	 */
	public void uploadSignBitmap(String filePath) {
		this.mpos.uploadSignBitmap(filePath, this);
	}
	
	/**
	 * 更新AID
	 * @param action 设置操作
	 * @param tlv  AID数据
	 */
	public void updateAid(Action action,byte[] tlv) {
		this.mpos.updateAid(action, tlv, new UpdateAidListener() {

			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}
			@Override
			public void onUpdateAidSuccess() {
				listener.onResultSuccess(0x36);
			}
		});
	}
	
	/**
	 * 更新公钥
	 * @param action 设置操作
	 * @param tlv  公钥信息
	 */
	public void updatePublicKey(Action action,byte[] tlv) {
		this.mpos.updatePublicKey(action, tlv, new UpdatePublicKeyListener() {
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}
			@Override
			public void onUpdatePublicKeySuccess() {
				listener.onResultSuccess(0x37);
			}
		});
	}
	
	/**
	 *  PBOC 联机交易结果 (二次授权)
	 * @param onlineData  tlv格式
	 */
	public void onlineDataProcess(byte[] onlineData) {
		this.mpos.onlineDataProcess(onlineData, new PBOCOnlineDataProcessListener(){
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}

			@Override
			public void onWriteProcessSuccess(byte[] icdata) {
				listener.onResultSuccess(0x38);
			}
		});
	}
	
	/**
	 * 显示文字
	 * @param row  显示行数偏移
	 * @param col   显示列数偏移
	 * @param text  显示内容长度
	 * @param showTime  显示时长，单位秒
	 */
	public void displayText(int row,int col,String text,int showTime) {
		row = row==0 ? 1 : row;
		col = col==0 ? 1 : col;
		showTime = showTime==0 ? 1 : showTime;
		
		this.mpos.displayText(row, col, text, showTime, new DisplayListener(){
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}
			@Override
			public void onDisplaySuccess() {
				listener.onResultSuccess(0x41);
			}
		});
	}
	
	/**
	 * 更新固件
	 * @param filePath  文件地址
	 */
	public void updateFirmware(String filePath) {
		this.mpos.updateFirmware(filePath, this);
	}
	
	
	/**
	 * 查找设备
	 */
	public void startSearchDevice() {
		deviceDetect = new BluetoothDeviceDetect(this.context);
		deviceDetect.startSearchDevice(60,new DeviceSearchListener(){

			@Override
			public void discoveryFinished() {
				listener.onResultSuccess(0x40);
			}

			@Override
			public void foundOneDevice(DeviceInfo deviceInfo) {
				listener.foundOneDevice(deviceInfo);
			}
		});
	}
	
	/**
	 * 停止查找设备
	 */
	public void stopSearchDevice() {
		deviceDetect.stopSearchDevice();
	}
	
	/**
	 * 查询设备是否存在
	 * @return
	 */
	public boolean isDevicePresent() {
		return this.mpos.connected();
	}
	
	/**
	 * 连接蓝牙刷卡器
	 * @param bondtime  超时 秒
	 * @param blueToothAddress  蓝牙地址
	 */
	public void connectBluetoothDevice(int bondtime, String blueToothAddress)  {
		DeviceInfo di = new DeviceInfo();
		di.setDeviceChannel(DeviceInfo.DEVICECHANNEL_BLUETOOTH);
		di.setAddress(blueToothAddress);
		this.mpos.connect(di,bondtime * 1000, this);
	}
	
	public void connectBluetoothDevice(String blueToothAddress)  {
		this.connectBluetoothDevice(20,blueToothAddress);
	}
	
	/**
	 * 断开
	 */
	public void disconnect() {
		this.mpos.close();
//		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
//		if(bt!=null) {
//			bt.disable();
//		}
	}
 
    //=============回调相关方式============//
	//打开设备成功
	@Override
	public void onConnected() {
		 this.listener.onResultSuccess(0x30);
	}

	/**
	 * 回调错误处理
	 * @param errCode  代码
	 * @param errDesc  描述
	 */
	@Override
	public void onError(int errCode, String errDesc) {
		//错误分发
		if(errCode == ErrorCode.TIMEOUT.getCode()) {
			this.listener.onTimeout();
		} else {
			this.listener.onError(errCode, errDesc);
		}
	}

	/**
	 * 重启成功
	 */
	@Override
	public void onResetSuccess() {
		
	}
 
	@Override
	public void onDownloadProgress(long current, long total) {
		this.listener.onDownloadProgress(current, total);
	}

	@Override
	public void onDownloadComplete() {
		this.listener.onResultSuccess(0x39);
	}

	@Override
	public void onDisconnect() {
		this.listener.onDisconnect();
	}

	@Override
	public void onWriteData(byte[] data) {
		
	}

	@Override
	public void onRecvData(byte[] data, int count) {
		
	}

	@Override
	public void onSetDeviceDataSuccess() {
		listener.onResultSuccess(0x32);
	}
	//////////////////////////网之畅读卡///////////////////////////////////
	 
	/**
	 * @param offset 偏移地址
	 * @param len    长度
	 */
	public void readSLECardData(int offset,int len) {
		new ReadSLECardTask(this.mpos.getDeviceComm(),this.listener).execute(offset,len);
	}
	
	/**
	 * @param pwd 密码
	 * @param offset 偏移地址
	 * @param data  数据
	 */
	public void writeSLECardData(String pwd ,int offset, byte[] data) {
		new WriteSLECardTask(this.mpos.getDeviceComm(),this.listener).execute(pwd,offset,data);
	}
	
	/**
	 * @param pwd 密码
	 * @param newPwd 新密码
	 */
	public void updateSLEpwd(String pwd ,String newPwd) {
		new UpdatePwdTask(this.mpos.getDeviceComm(),this.listener).execute(pwd,newPwd);
	}
}
