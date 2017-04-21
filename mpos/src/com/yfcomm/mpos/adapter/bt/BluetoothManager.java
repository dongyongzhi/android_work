package com.yfcomm.mpos.adapter.bt;

import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.yfcomm.mpos.DeviceInfo;
import com.yfcomm.mpos.ErrorCode;
import com.yfcomm.mpos.YFLog;
import com.yfcomm.mpos.codec.DeviceDecoder;
import com.yfcomm.mpos.listener.DeviceSearchListener;
import com.yfcomm.mpos.listener.DeviceStateChangeListener;
import com.yfcomm.mpos.listener.ConnectionStateListener;
import com.yfcomm.mpos.utils.BlueUtils;
import com.yfcomm.mpos.utils.StringUtils;

/**
 * pos 接口
 * @author qc
 *
 */
public class BluetoothManager extends BluetoothDeviceSession {

	private final static YFLog logger = YFLog.getLog(BluetoothManager.class);
	
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
	
	
	private final BluetoothAdapter bt = BluetoothAdapter.getDefaultAdapter();
	private final BluezBroadcastReceiver bluezBroadcastReceiver;
	//远程设备
	private BluetoothDevice remoteDevice = null;
	//设备名称
	private String deviceName = null;
	private boolean registerReceiver = false;
	private boolean registerReciverPair = false;
	
	private DeviceSearchListener deviceSearchListener;
	 
	private final Context context;
		
	private ConnectionStateListener openDeviceListener;
	
	private final static String MPOS_DISCONNECT_MESSAGE = "yifeng.mpos.connect.close";
	private long connectTimeOut = 5000L;
	
	
	/**
	 * 初始化一个context用于发送广播信息
	 * @param context
	 */
	public BluetoothManager(Context context,DeviceDecoder decoder) {
		super(decoder);
		this.context = context;
		this.bluezBroadcastReceiver = new BluezBroadcastReceiver(this);
		super.setOnDeviceStateChangeListener(internalDeviceStateChangeListener);
	}
	
	/**
	 * 查找 设备
	 */
	public void startSearchDevice(DeviceSearchListener listener) {
		this.deviceSearchListener = listener;
		setAction(ACTION_SEARCH);
		registerReceiver();
		
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
	private void connect(String deviceName) {
		this.deviceName  = deviceName;
		
		setAction(ACTION_CONNECT);
		registerReceiver();
		
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
	private void connect(DeviceInfo deviceInfo) {
		if(deviceInfo.getDeviceChannel() ==DeviceInfo.DEVICECHANNEL_BLUETOOTH ) {
			this.remoteDevice= bt.getRemoteDevice(deviceInfo.getAddress());
			connect(this.remoteDevice);
		}
	}
	
	/**
	 * 连接设备
	 * @param remoteDevice  设备
	 * @param handler       
	 */
	private void connect(BluetoothDevice remoteDevice) {
		this.remoteDevice = remoteDevice;
		
		logger.d("start open bluetooth device" +remoteDevice);
		//回调
		
		setAction(ACTION_CONNECT);
		
		//打开设备
		if(bt!=null && !bt.isEnabled()) {
			registerReceiver();
			bt.enable();
			
		} else if(bt.isEnabled()) {
			//连接设备
			setAction(ACTION_CONNECTING);
			doConnect(this.remoteDevice);
		}
	}

	@Override
	public void connect(DeviceInfo deviceInfo, long timeout, ConnectionStateListener listener) {
		this.openDeviceListener = listener;
		this.connectTimeOut = timeout;
		
		if(!StringUtils.isEmpty(deviceInfo.getAddress())) {
			this.connect(deviceInfo);
		} else if(!StringUtils.isEmpty(deviceInfo.getName())) {
			this.connect(deviceInfo.getName());
		} else {
			this.doPairConnect(null);
		}
	}
	
	@Override
	public	void connect(DeviceInfo deviceInfo, ConnectionStateListener listener) {
		connect(deviceInfo,connectTimeOut,listener);
	}
	
	 
	private void registerReceiver() {
		registerReceiver = true;
		// 注册查找 蓝牙设备的intent 并返回结果
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(bluezBroadcastReceiver, filter);
        
        filter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        context.registerReceiver(bluezBroadcastReceiver, filter);
        
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluezBroadcastReceiver, filter);
        
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(bluezBroadcastReceiver, filter);
        
        //Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(bluezBroadcastReceiver, filter);
        
        //filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //context.registerReceiver(bluezBroadcastReceiver, filter);
	}
	
	private void registerReceiverPair() {
		this.registerReciverPair = true;
		IntentFilter filter = new IntentFilter();
	    filter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
	    filter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
	    context.registerReceiver(bluetoothPairingRequest, filter);
	}
	
	private void unregisterReceiver() {
		//unregisterReceiver
		if(context!=null && registerReceiver) {
			context.unregisterReceiver(bluezBroadcastReceiver);
		}
		if(context!=null && this.registerReciverPair) {
			context.unregisterReceiver(bluetoothPairingRequest);
		}
		registerReceiver = false;
		registerReciverPair = false;
	}
	
	/**
	 * 连接设备
	 */
	private void doConnect(BluetoothDevice remoteDevice){
		
		if(remoteDevice==null) {
			//回调
			this.openDeviceListener.onError(ErrorCode.OPEN_DEVICE_FAIL.getCode(), ErrorCode.OPEN_DEVICE_FAIL.getDefaultMessage());
			return;
		}
		
		if(remoteDevice!=null && remoteDevice.getBondState() == BluetoothDevice.BOND_NONE) {
			//开始配对
			logger.d("开始配对设备" +remoteDevice );
			registerReceiverPair();
			BlueUtils.createBond(remoteDevice);
		} else {
			doPairConnect(remoteDevice); 
		}
	}
	
	/**
	 * 对配对的进度连接
	 * @param remoteDevice
	 */
	private synchronized void doPairConnect(BluetoothDevice remoteDevice) {
		open(remoteDevice,connectTimeOut);
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
	 * 关闭
	 */
	@Override
	public void close() {
		super.close();
		registerReceiver = false;
	}

	public Context getContext() {
		return context;
	}
	
	private synchronized void setAction(int action) {
		this.action = action;
	}
	
	public int getAction() {
		return this.action;
	}
	
	
	private static class BluezBroadcastReceiver extends BroadcastReceiver{
		
		private final BluetoothManager deviceComm;
		
		public BluezBroadcastReceiver(BluetoothManager deviceComm) {
			this.deviceComm = deviceComm;
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
					//deviceComm.bt.cancelDiscovery();
					logger.d("BluetoothAdapter.STATE_OFF");
					break;
					
				case BluetoothAdapter.STATE_ON:
					logger.d("BluetoothAdapter.STATE_ON");
					if(deviceComm.getAction() == ACTION_CONNECT ) {
						//按名称获取己配对的设备
						if(!StringUtils.isEmpty(deviceComm.deviceName) && deviceComm.remoteDevice == null) {
							deviceComm.remoteDevice = deviceComm.findRemoteDeviceByName(deviceComm.deviceName);
						}
						if(deviceComm.remoteDevice == null) {
							//查找设备
							if(!deviceComm.bt.isDiscovering()) {
								deviceComm.bt.startDiscovery();
							}
						} else {
							//连接设备
							deviceComm.bt.cancelDiscovery();
							deviceComm.doConnect(deviceComm.remoteDevice);
						}
						
					} else if(deviceComm.getAction() == ACTION_SEARCH) {
						
						if(!deviceComm.bt.isDiscovering()) {
							deviceComm.bt.startDiscovery();
						}
					}
					break;
	        	}
	        	
			} else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				//发现设备
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				logger.d("found:%s %s",device.getName(),device.getAddress());
				
				if(deviceComm.getAction() == ACTION_SEARCH && deviceComm.deviceSearchListener!=null) {
					deviceComm.deviceSearchListener.foundOneDevice(new DeviceInfo(device.getName(),device.getAddress()));
					
				} else if(deviceComm.getAction() == ACTION_CONNECT  &&  device.getName()!=null && 
						device.getName().equalsIgnoreCase(deviceComm.deviceName)) {
					
					deviceComm.bt.cancelDiscovery();
					deviceComm.remoteDevice = device;
				}
				
			} else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				
				switch (deviceComm.getAction()) {
				
				case ACTION_SEARCH : 
					if(deviceComm.deviceSearchListener!=null) {
						deviceComm.deviceSearchListener.discoveryFinished();
					}
					deviceComm.unregisterReceiver();
					break;
					
				case ACTION_CONNECT:
					deviceComm.doConnect(deviceComm.remoteDevice); 
					break;
				}
			}
		}
	}
	
	/**
	 * 配对广播
	 */
	private BroadcastReceiver bluetoothPairingRequest = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			 logger.d( "receive PairingRequest Broadcast:" + intent.getAction());
			 BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
			 if (device == null) {
				 logger.w("device is null");
			     return;
			 }
			 logger.d( "BluetoothDevice=%s" , device);
			 
			 
			 //配对请求
			 if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
				 int type = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_VARIANT",Integer.MIN_VALUE);
				 logger.d("Pair type=" + type);
				
				 if(type == 0) {
					 if(!BlueUtils.setPin(device, "000000")) {
						 logger.e("setPin fail");
						 internalDeviceStateChangeListener.onError(ErrorCode.OPEN_DEVICE_FAIL.getCode(),ErrorCode.OPEN_DEVICE_FAIL.getDefaultMessage());
					 }
				 } if (type==2 || type== 3) {
					 //A bond attempt failed because we canceled the bonding process
					 if(!BlueUtils.setPairingConfirmation(device, true)) {
						 logger.e("setPairingConfirmation fail");
						 internalDeviceStateChangeListener.onError(ErrorCode.OPEN_DEVICE_FAIL.getCode(),ErrorCode.OPEN_DEVICE_FAIL.getDefaultMessage());
					 }
				 }
			 }else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(intent.getAction())) {
				 //doConnect(null); 
				 
				 switch (device.getBondState()) {  
					case BluetoothDevice.BOND_BONDING:
						logger.d("正在配对......");
						break;
						
					case BluetoothDevice.BOND_BONDED:  
						logger.d("完成配对");
						doPairConnect(device); 
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
		}
	};
	
	private DeviceStateChangeListener  internalDeviceStateChangeListener = new DeviceStateChangeListener() {

		@Override
		public void onConnected() {
			unregisterReceiver();
			//openDeviceListener.onOpenSuccess();
			if(openDeviceListener!=null) {
				openDeviceListener.onConnected();
			}
		}

		@Override
		public void onError(int errCode, String errDesc) {
			unregisterReceiver();
			//openDeviceListener.onError(errCode, errDesc);
			if(openDeviceListener!=null) {
				openDeviceListener.onError(errCode, errDesc);
			}
		}

		@Override
		public void onDisconnect() {
			//deviceStateListener.onClose();
			if(openDeviceListener!=null) {
				openDeviceListener.onDisconnect();
			}
			//发送广播
			Intent intent = new Intent(MPOS_DISCONNECT_MESSAGE);
			context.sendBroadcast(intent);
		}

		@Override
		public void onWriteData(byte[] data) {
			
		}

		@Override
		public void onRecvData(byte[] data, int count) {
			
		}
 
	};
}
