package com.yfcomm.mpos.api;


import java.util.Date;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import com.yfcomm.mpos.DeviceDetect;
import com.yfcomm.mpos.DeviceInfo;
import com.yfcomm.mpos.ErrorCode;
import com.yfcomm.mpos.YFLog;
import com.yfcomm.mpos.YFMPos;
import com.yfcomm.mpos.adapter.bt.BluetoothDeviceDetect;
import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.PackageBuilder;
import com.yfcomm.mpos.exception.MPOSException;
import com.yfcomm.mpos.listener.CalculateMacListener;
import com.yfcomm.mpos.listener.DeviceSearchListener;
import com.yfcomm.mpos.listener.DeviceStateChangeListener;
import com.yfcomm.mpos.listener.DownloadListener;
import com.yfcomm.mpos.listener.SetListeners.DisplayListener;
import com.yfcomm.mpos.listener.SetListeners.PBOCOnlineDataProcessListener;
import com.yfcomm.mpos.listener.SetListeners.ResetListener;
import com.yfcomm.mpos.listener.SetListeners.SetDateTimeListener;
import com.yfcomm.mpos.listener.SetListeners.SetDeviceDataListener;
import com.yfcomm.mpos.listener.SetListeners.UpdateAidListener;
import com.yfcomm.mpos.listener.SetListeners.UpdateMainKeyListener;
import com.yfcomm.mpos.listener.SetListeners.UpdatePublicKeyListener;
import com.yfcomm.mpos.listener.SetListeners.UpdateWorkKeyListener;
import com.yfcomm.mpos.listener.support.AckListener;
import com.yfcomm.mpos.model.Action;
import com.yfcomm.mpos.model.TrxType;
import com.yfcomm.mpos.model.syn.OpenReadCard;
import com.yfcomm.mpos.model.syn.WorkKey;
import com.yfcomm.mpos.utils.ByteUtils;

/*
 * 怡丰刷卡器
 * @author qc
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
						byte[] data=pack.getBody();
						String customerNo = new String(data,0,15);
						String termNo = new String(data,15,8);
						//批次号
						String batchNo = ByteUtils.byteToHex(data,23,3);
						
						boolean existsMainKey = data[26] == 0x01 ? true : false;
						String version = new String(data,27,6);
						String sn = new String(data,33,24);
						listener.onGetDeviceInfo(customerNo, termNo,batchNo, existsMainKey, sn, version);
					}
		});
	 
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
	 */
	public void startSwiper(int timeout,long nAmount,int brushCard,TrxType type) {

		OpenReadCard readCard = new OpenReadCard();
		//超时
		readCard.setTimeout((byte)timeout);
		//交易类型
		readCard.setTrxType(type);
		//金额
		readCard.setTrxMoney(nAmount / 100.0);
		readCard.setActiveCode(new byte[]{(byte)brushCard,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
		//执行刷卡
		new SwiperAndReadPinTask(this.mpos.getDeviceComm(),readCard,this.listener).execute();
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
		BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
		if(bt!=null) {
			bt.disable();
		}
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
	
}
