package com.yfcomm.pos.bt.device;

import java.util.Set;

import com.yfcomm.pos.DeviceInfo;
import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.listener.DeviceSearchListener;
import com.yfcomm.pos.utils.BlueUtils;
import com.yfcomm.pos.utils.StringUtils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

/**
 * pos 接口
 * @author qc
 *
 */
public class DeviceComm extends BroadcastReceiver {

	private final static YFLog logger = YFLog.getLog(DeviceComm.class);
	
	//连接状态广播 		
	public final static String CONNECT_STATE = "device.connect.state";
	public final static String CONNECT_CHANGE_MESSAGE= "com.yfcomm.pos.device.connect_state";
	
	//private final static String DEFAULT_PIN = "0000";
	
	//调用接口返回类型t
	public final static int SUCCESS = 0;         //成功
	public final static int CONNECT_ERROR = -1;  //未连接
	public final static int TIMEOUT = -2;        //超时
	public final static int DATA_ERROR = -3;   //通讯故障，接收数据不完整或校验错误
	
	
	private final static int ACTION_NONE = 0;
	//连接
	private final static int ACTION_CONNECT = 1;
	//查找 
	private final static int ACTION_SEARCH = 2;
	//正在连接
	private final static int ACTION_CONNECTING = 3;
	
	
	private int action = ACTION_NONE;
	
	
	private final static Object lock = new Object();
	private final BluetoothSession session;
	
	//解码编码
	private final DeviceDecoder decoder = DeviceDecoder.newDecoder();
	private final DeviceEncoder encoder = new DeviceEncoder();
	
	private final BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
	//远程设备
	private BluetoothDevice remoteDevice = null;
	//设备名称
	private String deviceName = null;
	private boolean registerReceiver = false;
	private DeviceSearchListener deviceSearchListener;
	
	//超时时间
	private int timeout = 60 * 1000; 
	 
	private final Context context;
	
	/**
	 * 初始化一个context用于发送广播信息
	 * @param context
	 */
	public DeviceComm(Context context) {
		session = new BluetoothSession(new DeviceHandler(this),this.decoder);
		this.context = context;
		registerReceiver();
	}
	
	/***
	 * 设置超时时间，单位毫秒
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * 查找 设备
	 */
	public void startSearchDevice(DeviceSearchListener listener) {
		this.deviceSearchListener = listener;
		setAction(ACTION_SEARCH);
		
		//开始搜索
		if(!bt.isEnabled()) {
			bt.enable();
		} else if(!bt.isDiscovering()){
			bt.startDiscovery();
		}
	}
	
	/**
	 * 停止查找设备
	 */
	public void stopSearchDevice() {
		bt.cancelDiscovery();
	}
	
	/**
	 * 按设备名称 /sn 连接
	 * @param deviceName
	 */
	public void connect(String deviceName) {
		this.deviceName  = deviceName;
		setAction(ACTION_CONNECT);
		
		//打开设备
		if(bt!=null && !bt.isEnabled()) {
			bt.enable();
			
		} else if(bt.isEnabled()) {
			
			//连接设备
			this.remoteDevice = findRemoteDeviceByName(deviceName);
			if(this.remoteDevice!=null) {
				doConnect(this.remoteDevice);
			} else {
				//查找 设备
				if(!bt.isDiscovering()) {
					bt.startDiscovery();
				}
			}
		}
	}
	
	/**
	 * 连接设备
	 * @param deviceInfo
	 */
	public void connect(DeviceInfo deviceInfo) {
		this.remoteDevice= bt.getRemoteDevice(deviceInfo.getAddress());
		
		connect(this.remoteDevice);
	}
	
	/**
	 * 连接设备
	 * @param remoteDevice  设备
	 * @param handler       
	 */
	public void connect(BluetoothDevice remoteDevice) {
		this.remoteDevice = remoteDevice;
		setAction(ACTION_CONNECT);
		
		//打开设备
		if(bt!=null && !bt.isEnabled()) {
			bt.enable();
			
		} else if(bt.isEnabled()) {
			//连接设备
			setAction(ACTION_CONNECTING);
			doConnect(this.remoteDevice);
		}
	}
	
	 
	private void registerReceiver() {
		registerReceiver = true;
		// 注册查找 蓝牙设备的intent 并返回结果
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(this, filter);
        
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        context.registerReceiver(this, filter);
        
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(this, filter);
        
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(this, filter);
        
        //Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(this, filter);
        
        filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        context.registerReceiver(this, filter);
	}
	
	/**
	 * 连接设备
	 */
	private synchronized void doConnect(BluetoothDevice remoteDevice){
		session.open(remoteDevice);
	}
	 
	/**
	 * 按名称查找设备
	 * @param deviceName 设备名称
	 * @return
	 */
	private BluetoothDevice findRemoteDeviceByName(String deviceName) {
		// 查找已经配对的蓝牙设备，即以前已经配对过的设备
		Set<BluetoothDevice> pairedDevices = bt.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if(device.getBondState() != BluetoothDevice.BOND_BONDED)
					continue;
				if(device.getName().equalsIgnoreCase(deviceName)) {
					return device;
				}
			}
		}
		return null;
	}
	
	/**
	 * 写入信息
	 * @param in   输入内容
	 * @param out  输出内容
	 * @return 状态
	 */
	protected synchronized int write(byte[] in,byte[] out) {
		if(session.connected()) {
			
			//重置解码器,等待接收新的数据并解码
			decoder.reset();
			//写入数据
			if(session.write(in)) {
				try {
					synchronized(lock) {
					
						//等待接收数据
						while(!decoder.complete()) {
							//wait 并设置超时时长
							lock.wait(timeout); //timeout
						}
						
						//解析完成
						if(decoder.complete()) {
							if(decoder.getResultCode() == DeviceDecoder.SUCCESS) {
								//获取数据
								decoder.getData(out);
								return SUCCESS;
							} else {
								return DATA_ERROR;
							}
						} else {
							//超时
							return TIMEOUT;
						}
						 
					}
				} catch (InterruptedException e) {
					//中断
					logger.e(e.getMessage(),e);
					return SUCCESS;
				}
			}
			return DATA_ERROR;
		} else {
			return CONNECT_ERROR;
		}
	}
	
	
	/**
	 * 组包
	 * @param in  输入数据 
	 * @param out 输出数据
	 * @return
	 */
	public int packData(byte[] in,byte[] out) {
		byte[] data = encoder.encode(DevicePackage.CMD_PACK, in);
		return this.write(data, out);
	}
	
	/**
	 * 解包
	 * @param in   输入数据
	 * @param out  输出数据
	 * @return
	 */
	public int unpackData(byte[] in,byte[] out) {
		byte[] data = encoder.encode(DevicePackage.CMD_UNPACK, in);
		return this.write(data, out);
	}
		
	
	/**
	 * 认证
	 * @param password  固定长度为6位
	 * @param out 输出数据
	 * @return
	 */
	public int auth(String password,byte[] out) {
		byte[] authData = StringUtils.rightAddSpace(password, 6).getBytes();
		return this.write(encoder.encode(DevicePackage.CMD_AUTH, authData),out);
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		if(this.context!=null && registerReceiver) {
			this.context.unregisterReceiver(this);
		}
		this.session.close();
	}
 
	public DeviceDecoder getDecoder() {
		return decoder;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		logger.d(action);
		if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {  
			
			//蓝牙状态变更，并连接己配对的设备
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
			
        	switch (state){
			case BluetoothAdapter.STATE_OFF:
				bt.cancelDiscovery();
				break;
				
			case BluetoothAdapter.STATE_ON:
				
				if(getAction() == ACTION_CONNECT ) {
					//按名称获取己配对的设备
					if(!StringUtils.isEmpty(this.deviceName) && this.remoteDevice == null) {
						this.remoteDevice = this.findRemoteDeviceByName(this.deviceName);
					}
					if(this.remoteDevice == null) {
						//查找设备
						if(!bt.isDiscovering()) {
							bt.startDiscovery();
						}
					} else {
						//连接设备
						bt.cancelDiscovery();
						doConnect(this.remoteDevice);
					}
					
				} else if(getAction() == ACTION_SEARCH) {
					
					if(!bt.isDiscovering()) {
						bt.startDiscovery();
					}
				}
				break;
        	}
        	
		} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			//发现设备
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			logger.d("found:"+device.getName()+" "+device.getAddress());
			
			if(getAction() == ACTION_SEARCH && this.deviceSearchListener!=null) {
				this.deviceSearchListener.foundOneDevice(new DeviceInfo(device.getName(),device.getAddress()));
				
			} else if(getAction() == ACTION_CONNECT  &&  device.getName()!=null && 
					device.getName().equalsIgnoreCase(this.deviceName)) {
				
				bt.cancelDiscovery();
				this.remoteDevice = device;
			}
			
		} else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
			
			//请求配对
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			logger.d("bond state:"+device.getName()+" "+device.getAddress());
			
			if(device.getAddress().equals(this.remoteDevice.getAddress())) {
				switch (device.getBondState()) {  
					case BluetoothDevice.BOND_BONDING:
						logger.d("正在配对......");
						break;
						
					case BluetoothDevice.BOND_BONDED:  
						logger.d("完成配对");
						doConnect(this.remoteDevice); 
						break;
						
					case BluetoothDevice.BOND_NONE:
						logger.d("BOND_NONE");
						//配对失败
						doConnect(null); 
						break;
					default:
						break;
				}
			}
			
		} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
			
			switch (getAction()) {
			
			case ACTION_SEARCH : 
				if(this.deviceSearchListener!=null) {
					this.deviceSearchListener.discoveryFinished();
				}
				break;
				
			case ACTION_CONNECT:
				//开始配对及连接连接设备
				if(this.remoteDevice!=null && this.remoteDevice.getBondState() == BluetoothDevice.BOND_NONE) {
					//BlueUtils.setPin(remoteDevice, DEFAULT_PIN);
					BlueUtils.createBond(remoteDevice);
					//BlueUtils.cancelPairingUserInput(remoteDevice);
				} else {
					doConnect(this.remoteDevice); 
				}
				break;
			}
		}
	}
	
	private synchronized void setAction(int action) {
		this.action = action;
	}
	
	public int getAction() {
		return this.action;
	}
	
	/**
	 * device session handler
	 * @author qc
	 *
	 */
	private static class DeviceHandler extends Handler {
		private final DeviceComm deviceComm;
		
		public DeviceHandler(DeviceComm deviceComm) {
			this.deviceComm = deviceComm;
		}
		
		public void handleMessage(Message msg) {
			
			switch(msg.what) {
			
				//连接状态返回
				case BluetoothSession.MESSAGE_CONNECT_SUCCESS:
				case BluetoothSession.MESSAGE_CONNECT_CLOSE:
				case BluetoothSession.MESSAGE_CONNECT_FAILED:
					
					//发送广播信息
					if(deviceComm.getContext()!=null) {
						Intent intent = new Intent(DeviceComm.CONNECT_CHANGE_MESSAGE);
						intent.putExtra(DeviceComm.CONNECT_STATE, msg.what);
						deviceComm.getContext().sendBroadcast(intent);
					}
					break;
				
				//读取数据
				case BluetoothSession.MESSAGE_READ:
					synchronized(lock) {
						//deviceComm.getDecoder().append((byte[])msg.obj,0,msg.arg1);
						//解析是否完整数据包
						//if(deviceComm.getDecoder().complete()) {
						//解码完成通知数据处理
						lock.notify();
						//}
					}
					break;
			}
		}
	}

}
