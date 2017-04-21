package com.yifengcom.yfpos.adapter.bt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.yifengcom.yfpos.DeviceSession;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.codec.DeviceDecoder;
import com.yifengcom.yfpos.listener.DeviceStateChangeListener;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
 * 蓝牙会话
 * @author qc
 *
 */
public abstract class BluetoothDeviceSession implements DeviceSession {

	private final static YFLog logger = YFLog.getLog(BluetoothDeviceSession.class);
	
	//UseSecure
	private final static String SECURE_MANUFACTURER = "Xiaomi";
	private final static String SECURE_OS_MODEL = "2013022";
	private final static String SECURE_OS_VERSION = "4.2.1";
	
	//bt comm uuid
	private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	//信息标识
	/** 连接设备失败**/
	public final static int MESSAGE_CONNECT_FAILED = 0;
	/** 连接设备成功**/
	public final static int MESSAGE_CONNECT_SUCCESS = 1;
	/** 关闭连接 **/
	public final static int MESSAGE_CONNECT_CLOSE = 2;
	/** 读取信息**/
	public final static int MESSAGE_READ = 3;
	/** 写入信息**/
	public final static int MESSAGE_WRITE_SUCCESS = 4;
	/** 写入信息失败**/
	public final static int MESSAGE_WRITE_FAILED = 5;
	
	/** 是否连接超时 **/
	private volatile boolean  connectTimeOut = false;
	
	
	//解码编码
	private final DeviceDecoder decoder;
	
	private ConnectedThread connectedThread = null;
	 
	//是否连接
	private volatile boolean isConnected = false;
	private BluetoothDevice btDevice = null;
	private DeviceStateChangeListener listener;
	
	private final SessionHandler mHandler = new SessionHandler(this);

	public BluetoothDeviceSession(DeviceDecoder decoder) {
		this.decoder = decoder;
	}
	
	public void setOnDeviceStateChangeListener(DeviceStateChangeListener listener) {
		this.listener = listener;
	}
	
	public DeviceStateChangeListener getOnDeviceStateChangeListener() {
		return this.listener;
	}
	
	/**
	 * insecure 
	 * @return
	 */
	public static boolean shouldUseSecure() {
		return Build.MANUFACTURER.equals(SECURE_MANUFACTURER) && Build.MODEL.equals(SECURE_OS_MODEL) && Build.VERSION.RELEASE.equals(SECURE_OS_VERSION);
	}
	
	/**
	 * 打开
	 * @param btDevice  目标设备
	 */
	public synchronized void open(BluetoothDevice btDevice,long timeout) {
		logger.d("===>connect  BluetoothDevice "+btDevice);
		
		if(btDevice!=null) {
			//己打开连接
			if(this.btDevice!=null && this.isConnected && this.btDevice.getAddress().equals(btDevice.getAddress())) {
				//发送连接成功信息
				if(listener!=null) {
					listener.onConnected();
				}
				return;
			} else if(this.isConnected) {
				this.close();
			}
			BluetoothSocket btSocket = null;
			try {
				if(shouldUseSecure()) {
					btSocket = btDevice.createRfcommSocketToServiceRecord(SPP_UUID);
				}else {
					btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
				}
				this.btDevice = btDevice;
				open(btSocket,timeout);
				
			} catch(Exception e) {
				logger.e("open failed ", e);
				if(listener!=null) {
					listener.onError(ErrorCode.OPEN_DEVICE_FAIL.getCode(), ErrorCode.OPEN_DEVICE_FAIL.getDefaultMessage());
				}
			}
		} else {
			if(listener!=null) {
				listener.onError(ErrorCode.OPEN_DEVICE_FAIL.getCode(), ErrorCode.OPEN_DEVICE_FAIL.getDefaultMessage());
			}
		}
	}
	
	/**
	 * 打开
	 */
	private void open(BluetoothSocket btSocket,long timeout) {
		logger.d("Bluetooth open");
		connectTimeOut = false;
		//启动连接
		new SocketThread(btSocket);
		
		//开始计时器
		Timer timer = new Timer(true);
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				//设置连接超时
				if(!connected()) {
					connectTimeOut = true;
					mHandler.obtainMessage(MESSAGE_CONNECT_FAILED,ErrorCode.OPEN_DEVICE_FAIL).sendToTarget();
					logger.e("connection timeout");
				}
			}
		}, timeout);
	}
	
	/**
	 * 是否连接
	 * @return
	 */
	public boolean connected(){
		return isConnected;
	}
	
	/**
	 * 关闭
	 */
	public void close() {
		if(connectedThread!=null) {
			logger.d(" close bt");
			connectedThread.close();
			connectedThread = null;
		}
	}
	
	/**
	 * 写入数据
	 */
	public boolean write(byte[] data) {
		 return this.write(data,0,data.length);
	}
	
	/**
	 * 写入数据
	 * @param data    要写入的数据
	 * @param offset  超启位置
	 * @param count   要写入的个数
	 * @return
	 */
	public boolean write(byte[] data,int offset,int count) {
		if(connectedThread!=null) {
			return connectedThread.write(data,offset,count);
		} else {
			return false;
		}
	}
	
	private synchronized void setConnected(boolean isConnected) {
		logger.d("setConnected  "+isConnected);
		this.isConnected = isConnected;
	}
	
	/**
	 * socket thread 蓝牙
	 * @author qc
	 *
	 */
	private class SocketThread implements Runnable {
		
		private final BluetoothSocket btSocket;
		
		public SocketThread(BluetoothSocket btSocket) {
			this.btSocket = btSocket;
			Thread thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run() {
			try {
				//连接
				this.btSocket.connect();
				if(connectTimeOut) {
					btSocket.close();
					return;
				}
				
				logger.d("Bluetooth open success");
				decoder.reset();
				//创建连接线程
				setConnected(true);
				connectedThread = new ConnectedThread(btSocket);
				//发送连接成功信息
				if(!connectedThread.isComplete) {
					synchronized(connectedThread.lockConnectedThread) {
						try {
							connectedThread.lockConnectedThread.wait(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				Thread.sleep(500);
				logger.d("create ConnectedThread success ");
				Message message = Message.obtain(mHandler, MESSAGE_CONNECT_SUCCESS);
				mHandler.sendMessageDelayed(message, 500);
				
			} catch (Exception e) {
				
				setConnected(false);
				logger.e("connect failed", e);
				mHandler.obtainMessage(MESSAGE_CONNECT_FAILED,ErrorCode.OPEN_DEVICE_FAIL).sendToTarget();
				/*
				if(listener!=null) {
					listener.onError(ErrorCode.OPEN_DEVICE_FAIL.getCode(), ErrorCode.OPEN_DEVICE_FAIL.getDefaultMessage());
				}*/
				// Close the socket
				try {
					this.btSocket.close();
				} catch (IOException e1) {
					logger.e("close failed ", e);
				}
			}
		}
	}
	
	public class ConnectedThread implements Runnable {

		private final static int BUFFER_SIZE = 1024;
		
		private final Object lockConnectedThread = new Object();
		
		private final BluetoothSocket btSocket;
		private InputStream in;
	    private OutputStream out;
	    
	    private boolean isComplete = false;
	        
		public ConnectedThread(BluetoothSocket btSocket) {
			this.btSocket = btSocket;
			this.isComplete = false;
			
			try {
				in = btSocket.getInputStream();
				out = btSocket.getOutputStream();
				
				Thread thread = new Thread(this);
				thread.start();
				
				logger.d("Set up bluetooth socket i/o stream");
			} catch(IOException e) {
				logger.e("sockets not created",e);
			}
		}
		
		/**
		 * 写入数据
		 * @param data  数据
		 */
		public synchronized boolean write(byte[] data,int offset,int count) {
			boolean result = false;
			try {
				if(out!=null) {
					out.write(data,offset,count);
					/*
					if(listener!=null) {
						listener.onWriteData(data);
					}*/
					mHandler.obtainMessage(MESSAGE_WRITE_SUCCESS,data).sendToTarget();
					 
					if(YFLog.DEBUG) {
						logger.d("send message bytes :" +count);
						logger.d(ByteUtils.printBytes(data, offset, count));
					}
					result = true;
				} else {
					result = false;
				}
				
			} catch(IOException e) {
				logger.e("write message failed",e);
				result = false;
			}
			return result;
		}
		
		public synchronized boolean write(byte[] data) {
			return this.write(data, 0, data.length);
		}
		
		/**
		 * 关闭
		 */
		public synchronized void close() {
			try {
				if(in!=null) {
					in.close();
				}
				if(out!=null) {
					out.close();
				}
				if(btSocket!=null) {
					btSocket.close();
				}
			} catch (IOException e) {
				logger.e("connect close failed", e);
			}
		}
		
		@Override
		public void run() {
			
			byte[] buf = new byte[BUFFER_SIZE];
			int readBytesCount;
			
			//读取socket 内容
			try {
				 logger.d("run recv message....  ");
				 isComplete = true;
				 synchronized(lockConnectedThread) {
					 lockConnectedThread.notifyAll();
				 }
				 
				 while(connected()) {
					 readBytesCount = in.read(buf, 0, BUFFER_SIZE);
					 decoder.append(buf, 0, readBytesCount);
					 mHandler.obtainMessage(MESSAGE_READ,0,readBytesCount,buf).sendToTarget();
				 }
			} catch(Exception e) {
				logger.w("connection lost");
			} finally {
				//关闭bt
				//this.close();
				decoder.reset();
				setConnected(false);
				mHandler.obtainMessage(MESSAGE_CONNECT_CLOSE).sendToTarget();
			}
		}
	}
	
	public static class SessionHandler extends Handler {
		
		private final BluetoothDeviceSession session;
		private  DeviceStateChangeListener listener;
		
		public SessionHandler(BluetoothDeviceSession session) {
			this.session = session;
		}
		
		public void handleMessage(Message msg) {
			this.listener = this.session.getOnDeviceStateChangeListener();
			switch(msg.what) {
				case MESSAGE_CONNECT_SUCCESS:
					if(listener!=null) {
						listener.onConnected();
					}
					break;
				case MESSAGE_CONNECT_CLOSE:
					if(listener!=null) {
						listener.onDisconnect();
					}
					break;
				case MESSAGE_CONNECT_FAILED:
					ErrorCode code = (ErrorCode)msg.obj;
					if(listener!=null) {
						listener.onError(code.getCode(), code.getDefaultMessage());
					}
					break;
					
				case MESSAGE_WRITE_SUCCESS:
					if(listener!=null) {
						listener.onWriteData((byte[])msg.obj);
					}
					break;
				case MESSAGE_READ:
					if(listener!=null) {
						listener.onRecvData((byte[])msg.obj, msg.arg2);
					}
					break;
			}
		}
	};
}
