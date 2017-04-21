package com.yifengcom.yfpos;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.StandardSwiperDecoder;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.listener.CalculateMacListener;
import com.yifengcom.yfpos.listener.DownloadListener;
import com.yifengcom.yfpos.listener.ConnectionStateListener;
import com.yifengcom.yfpos.listener.ReaderListeners.GetDateTimeListener;
import com.yifengcom.yfpos.listener.ReaderListeners.GetDeviceDataListener;
import com.yifengcom.yfpos.listener.ReaderListeners.GetDeviceInfoListener;
import com.yifengcom.yfpos.listener.ReaderListeners.ReadPinListener;
import com.yifengcom.yfpos.listener.ReaderListeners.SwiperCardListener;
import com.yifengcom.yfpos.listener.ReaderListeners.WaitingCardListener;
import com.yifengcom.yfpos.listener.SetListeners.CheckSystemListener;
import com.yifengcom.yfpos.listener.SetListeners.DisplayListener;
import com.yifengcom.yfpos.listener.SetListeners.EncryptListener;
import com.yifengcom.yfpos.listener.SetListeners.OpenReadCardListener;
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
import com.yifengcom.yfpos.model.ack.DeviceVersion;
import com.yifengcom.yfpos.model.syn.Encrypt;
import com.yifengcom.yfpos.model.syn.OpenReadCard;
import com.yifengcom.yfpos.model.syn.ReadPin;
import com.yifengcom.yfpos.service.WorkKey;
import com.yifengcom.yfpos.task.OnlineDataProcessTask;
import com.yifengcom.yfpos.task.ReadPinTask;
import com.yifengcom.yfpos.task.SwiperTask;
import com.yifengcom.yfpos.task.UploadSignBitmapTask;
import com.yifengcom.yfpos.task.SwiperTask.SwiperTaskListener;
import com.yifengcom.yfpos.task.UploadFileTask;
import com.yifengcom.yfpos.utils.ByteUtils;
import com.yifengcom.yfpos.utils.StringUtils;

/**
 * mpos 操作接口
 * @author qc
 *
 */
public class YFMPos {
	
	private static final String CHARTSET  = "gb2312";
	private static YFMPos mpos = null;
	private final Context context;
	private final DeviceComm deviceComm;
	
	public DeviceComm getDeviceComm() {
		return deviceComm;
	}

	private YFMPos(Context context) {
		this.context =  context instanceof Application ? context : context.getApplicationContext();
		//连接蓝牙设备
		this.deviceComm = new DefaultDeviceComm(this.context);
	}
	
	public static YFMPos getInstance(Context context) {
		if(mpos==null) {
			mpos = new YFMPos(context);
		}
		return mpos;
	}
	
	/**
	 * 连接设备
	 * @param deviceInfo  设备信息
	 * @param listener 连接监听
	 */
	public void connect(DeviceInfo deviceInfo,long timeout,ConnectionStateListener listener) {
		this.deviceComm.connect(deviceInfo,timeout, listener);
	}
	
	public void connect(DeviceInfo deviceInfo,ConnectionStateListener listener) {
		connect(deviceInfo,6000,listener);
	}
	
	/**
	 * 关闭设备
	 */
	public void close() {
		this.deviceComm.close();
	}
	
	public boolean connected() {
		return this.deviceComm.connected();
	}
	
	/**
	 * 获取设备版本信息
	 * @return 版本信息
	 */
	public void getDeviceVersion(GetDeviceInfoListener listener) {
		DevicePackage syn = PackageBuilder.syn(PackageBuilder.CMD_DEVICE_VERSION);
		this.deviceComm.execute(syn, new AckListener<GetDeviceInfoListener>(listener){
			@Override
			public void doCallback(GetDeviceInfoListener listener,DevicePackage pack) {
				DeviceVersion dv = new DeviceVersion();
				dv.decode(pack);
				listener.onGetDeviceInfoSuccess(dv);
			}
		});
	}
	
	
	/**
	 * 获取设备上的数据
	 * @param offset  
	 * @param length
	 * @param listener
	 */
	public void getDeviceData(int offset,int length,GetDeviceDataListener listener) {
		byte[] body = new byte[4];
		body[0] = (byte)(offset>>8);
		body[1] = (byte)offset;
		
		body[2] = (byte)length;
		body[3] = (byte)(length>>8);
		
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_READ_CACHE, body), 
				new AckListener<GetDeviceDataListener>(listener){
					@Override
					public void doCallback(GetDeviceDataListener listener,DevicePackage pack) {
						byte[] buf = pack.getBody();
						if(buf == null)  {
							listener.onGetDeviceDataSuccess(null); 
						} else {
							int len = ByteUtils.byteToIntLE(buf[0], buf[1]);
							byte[] data = new byte[len];
							System.arraycopy(buf, 2, data, 0, len);
							listener.onGetDeviceDataSuccess(data);
						}
					}
			
		});
	}
	
	/**
	 * 设置数据
	 * @param offset 偏移地址
	 * @param length 数据长度
	 * @param data   数据内容
	 * @param listener 
	 */
	public void setDeviceData(int offset,byte[] data,SetDeviceDataListener listener) {
		byte[] body = new byte[4 + data.length];
		body[0] = (byte)(offset >> 8);
		body[1] = (byte)(offset);
		body[2] = (byte)data.length;
		body[3] = (byte)(data.length>>8);
		System.arraycopy(data, 0, body, 4, data.length);
		
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SET_CACHE, body), 
				new AckListener<SetDeviceDataListener>(listener){
					@Override
					public void doCallback(SetDeviceDataListener listener,DevicePackage pack) {
						listener.onSetDeviceDataSuccess();
					}
		});
	}
	
	/**
	 * 复位操作
	 */
	public void reset(ResetListener listener) {
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_RESET),
				new AckListener<ResetListener>(listener){
					@Override
					public void doCallback(ResetListener listener,DevicePackage pack) {
						listener.onResetSuccess();
					}
			
		});
	}
	
	/**
	 * 取消指令
	 */
	public void cancel() {
		this.deviceComm.cancel();
	}
	
	/**
	 * 获取设置时间
	 * @param listener 回调
	 */
	public void getDateTime(GetDateTimeListener listener) {
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_GETTIME),new AckListener<GetDateTimeListener>(listener){
			@Override
			public void doCallback(GetDateTimeListener listener,DevicePackage pack) {
				listener.onGetDateTimeSuccess(new String(pack.getBody()));
			}
		});
	}
	
	/**
	 * 设置当前时间
	 * @param time
	 */
	public void setDateTime(Date time,SetDateTimeListener listener) {
		DateFormat df =  new SimpleDateFormat("yyyyMMddHHmmss",Locale.getDefault());
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SETTIME,df.format(time).getBytes()), 
				new AckListener<SetDateTimeListener>(listener){

					@Override
					public void doCallback(SetDateTimeListener listener,DevicePackage pack) {
						listener.onSetDateTimeSuccess();
					}
		});
	}
	
	/**
	 * 更新固件
	 * @param filePath  文件路径
	 * @param listener  更 新监听
	 */
	public void updateFirmware(final String filePath,final DownloadListener listener) {
		 new UploadFileTask(filePath,this.deviceComm,listener).execute();
	}
	
	/**
	 * 上传电子签名
	 * @param filePath  bitmap
	 * @param listener  上传监听
	 */
	public void uploadSignBitmap(final String filePath,final DownloadListener listener) {
		 new UploadSignBitmapTask(filePath,this.deviceComm,listener).execute();
	}
	
	/**
	 * 更新主密钥
	 * @param keyIndex  密钥索引
	 * @param key       密文
	 * @param listener  监听
	 */
	public void updateMainKey(int keyIndex,byte[] key, UpdateMainKeyListener listener) {
		byte[] body = new byte[27];
		body[1] = (byte)keyIndex;
		body[2] = key==null || key.length==0 ? 0x00 : (byte)24;
		if(key!=null) {
			System.arraycopy(key, 0, body, 3, key.length< 24 ? key.length : 24);
		}
		
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE_MAIN_KEY, body),
				new AckListener<UpdateMainKeyListener>(listener){
					@Override
					public void doCallback(UpdateMainKeyListener listener,DevicePackage pack) {
						listener.onUpdateMainKeySuccess();
					}
		});
	}
	
	/**
	 * 更新工作密钥
	 */
	public void updateWorkKey(WorkKey key,UpdateWorkKeyListener listener){
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE_WORK_KEY, key.encode()),
				new AckListener<UpdateWorkKeyListener>(listener){

					@Override
					public void doCallback(UpdateWorkKeyListener listener,DevicePackage pack) {
						listener.onUpdateWorkKeySuccess();
					}
			
		});
	}
	
	/**
	 * 开启读卡器
	 * @param openReadCard  开启参数
	 * @param listener      监听
	 */
	public void openReadCard(OpenReadCard openReadCard,OpenReadCardListener listener) {
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_OPEN_SWIPER,openReadCard.encode()), 
				new AckListener<OpenReadCardListener>(listener){
					@Override
					public void doCallback(OpenReadCardListener listener,DevicePackage pack) {
						listener.onOpenReadCardSuccess();
					}
		});
	}
	
	/**
	 * 刷卡并返回结果
	 * @param openReadCard  参数
	 * @param listener   监听
	 */
	public void swiperCard(OpenReadCard openReadCard,final WaitingCardListener listener) {
		new SwiperTask(this.deviceComm,openReadCard,new SwiperTaskListener(){

			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}

			@Override
			public void onTimeout() {
				listener.onTimeout();
			}

			@Override
			public void onSwiperSuccess(StandardSwiperDecoder swiperDecoder, boolean isIc) {
				listener.onWaitingCardSuccess(swiperDecoder.getKsn(), swiperDecoder.getTrxTime(), 
						swiperDecoder.getRandom(), swiperDecoder.getEncryTrack2(), swiperDecoder.getEncryTrack3(), 
						swiperDecoder.getMac(), swiperDecoder.getIcData());
			}
			
		}).execute();
	}
	
	
	/**
	 * 读取pin
	 * @param readPin  参数
	 * @param listener 监听
	 */
	public void readPin(ReadPin readPin,ReadPinListener listener) {
		new ReadPinTask(this.deviceComm,readPin,listener).execute();
	}
	
	/**
	 * 刷卡+读pin
	 * @param openReadCard
	 */
	public void swiperCard(OpenReadCard openReadCard,final SwiperCardListener listener) {
		
		this.swiperCard(openReadCard, new WaitingCardListener() {
			@Override
			public void onError(int errorCode, String errorMessage) {
				listener.onError(errorCode, errorMessage);
			}
			@Override
			public void onTimeout() {
				listener.onTimeout();
			}
			@Override
			public void onWaitingCardSuccess(final String ksn, final String trxTime,final byte[] random, final byte[] track2Cipher, final byte[] track3Cipher,final byte[] mac,final  byte[] icTrxData) {
				//成功读取PIN
				ReadPin readPin = new ReadPin();
				readPin.setRandom(random);
				
				YFMPos.this.readPin(readPin, new ReadPinListener() {
					@Override
					public void onError(int errorCode, String errorMessage) {
						listener.onError(errorCode, errorMessage);
					}
					@Override
					public void onTimeout() {
						listener.onTimeout();
					}
					@Override
					public void onReadPinSuccess(int pinNumber, byte[] pinBlock) {
						listener.onSwiperSuccess(ksn, trxTime, random, track2Cipher, track3Cipher, mac, icTrxData, pinBlock);
					}
				});
			}
		});
	}
	
	/**
	 * 计算mac
	 * @param mac 
	 */
	public void calculateMac(byte[] data,CalculateMacListener listener) {
		 this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_CALCULATE_MAC, data), 
				 new AckListener<CalculateMacListener>(listener){
					@Override
					public void doCallback(CalculateMacListener listener,DevicePackage pack) {
						listener.onCalculateMacSuccess(pack.getBody());
					}
		 });
	}
	
	/**
	 * 验证MAC
	 */
	public void validateMac() {
		
	}
	
	
	/**
	 * PBOC 联机交易结果 (二次授权)
	 * @param onlineData  银联交易返回的数据
	 * @param listener   监听
	 */
	public void onlineDataProcess(byte[] onlineData,PBOCOnlineDataProcessListener listener) {
		new OnlineDataProcessTask(this.deviceComm,onlineData,listener).execute();
	}
	
	/**
	 * 数据加密
	 * @param encrypt  要加密的内容
	 * @param listener 监听
	 */
	public void encrypt(Encrypt encrypt,EncryptListener listener) {
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_ENCRYPT,encrypt.encode()), 
				new AckListener<EncryptListener>(listener){
					@Override
					public void doCallback(EncryptListener listener,DevicePackage pack) {
						byte[] random = new byte[8];
						System.arraycopy(pack.getBody(), 0, random, 0, random.length);
						
						//密文
						byte[] cipher = new byte[pack.getBody().length-9];
						System.arraycopy(pack.getBody(), 9, cipher, 0, cipher.length);
						
						//回调
						listener.onEncryptSuccess(random, cipher);
					}
		});
	}
	
	/**
	 * 更新AID
	 * @param action
	 * @param tlv
	 * @param listener
	 */
	public void updateAid(Action action,byte[] tlv,UpdateAidListener listener) {
		byte[] body;
		if(tlv==null) {
			body = new byte[]{action.getValue()};
		} else {
			body = new byte[tlv.length+1];
			body[0] = action.getValue();
			System.arraycopy(tlv, 0, body, 1, tlv.length);
		}
		
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_UPDATE_AID,body), 
				new AckListener<UpdateAidListener>(listener){
					@Override
					public void doCallback(UpdateAidListener listener,
							DevicePackage pack) {
						listener.onUpdateAidSuccess();
					}
		});
	}
	
	/**
	 * 更新公钥
	 * @param action
	 * @param tlv
	 */
	public void updatePublicKey(Action action,byte[] tlv,UpdatePublicKeyListener listener){
		byte[] body;
		if(tlv==null) {
			body = new byte[]{action.getValue()};
		} else {
			body = new byte[tlv.length+1];
			body[0] = action.getValue();
			System.arraycopy(tlv, 0, body, 1, tlv.length);
		}
		
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_PBOC_UPDATE_PUBLIC_KEY,body), 
				new AckListener<UpdatePublicKeyListener>(listener){
					@Override
					public void doCallback(UpdatePublicKeyListener listener,
							DevicePackage pack) {
						listener.onUpdatePublicKeySuccess();
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
	public void displayText(int row,int col,String text,int showTime,DisplayListener listener) {
		if(!StringUtils.isEmpty(text)) {
			byte[] data;
			try {
				data = text.getBytes(CHARTSET);
				byte[] body = new byte[data.length + 4];
				body[0] = (byte)row;
				body[1] = (byte)col;
				body[2] = (byte)data.length;
				System.arraycopy(data, 0, body, 3, data.length);
				body[body.length-1] = (byte)showTime;
				
				this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_DISPLAY,body), 
						new AckListener<DisplayListener>(listener){
							@Override
							public void doCallback(DisplayListener listener,DevicePackage pack) {
								listener.onDisplaySuccess();
							}
				});
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			listener.onDisplaySuccess();
		}
	}
	
	
	/**
	 * 获取POS机状态
	 */
	public void checkSystem(CheckSystemListener listener){
		this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SYSTEM_TEST),
				new AckListener<CheckSystemListener>(listener){
					@Override
					public void doCallback(CheckSystemListener listener,
							DevicePackage pack) {
						String sn = "",softVersion = "";
						if(pack != null){
							byte[] data=pack.getBody();
							softVersion = new String(data,0,13);
							sn  = new String(data,13,16);
						}
						listener.onCheckSuccess(sn, softVersion);
					}
		});
	}
	
}
