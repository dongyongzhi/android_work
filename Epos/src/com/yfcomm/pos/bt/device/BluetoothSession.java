package com.yfcomm.pos.bt.device;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.utils.ByteUtils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Handler;

public class BluetoothSession {

	private final static YFLog logger = YFLog.getLog(BluetoothSession.class);
	
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
	
	
	//解码编码
	private final DeviceDecoder decoder;
	
	private ConnectedThread connectedThread = null;
	//message mHandler
	private Handler mHandler;
	//是否连接
	private boolean isConnected = false;
	private BluetoothDevice btDevice = null;

	public BluetoothSession(Handler handler,DeviceDecoder decoder) {
		this.mHandler = handler;
		this.decoder = decoder;
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
	 */
	public synchronized void open(BluetoothSocket btSocket) {
		logger.d(" Bluetooth  open");
		//启动连接
		new SocketThread(btSocket);
	}
	
	/**
	 * 是否连接
	 * @return
	 */
	public  boolean connected(){
		return isConnected;
	}
	
	/**
	 * 打开
	 * @param btDevice  目标设备
	 */
	public synchronized void open(BluetoothDevice btDevice) {
		logger.d("============>connect  BluetoothDevice "+btDevice);
		if(btDevice!=null) {
			//己打开连接
			if(this.btDevice!=null && this.isConnected && this.btDevice.getAddress().equals(btDevice.getAddress())) {
				//发送连接成功信息
				mHandler.obtainMessage(MESSAGE_CONNECT_SUCCESS).sendToTarget();
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
				open(btSocket);
				
			} catch(Exception e) {
				logger.e("open failed ", e);
				mHandler.obtainMessage(MESSAGE_CONNECT_FAILED).sendToTarget();
			}
		} else {
			mHandler.obtainMessage(MESSAGE_CONNECT_FAILED).sendToTarget();
		}
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
				setConnected(true);
				//发送连接成功信息
				mHandler.obtainMessage(MESSAGE_CONNECT_SUCCESS).sendToTarget();
				//创建连接线程
				connectedThread = new ConnectedThread(btSocket);
				
			} catch (IOException e) {
				
				setConnected(false);
				logger.e("connect failed", e);
				mHandler.obtainMessage(MESSAGE_CONNECT_FAILED).sendToTarget();
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
		
		private final BluetoothSocket btSocket;
		private InputStream in;
	    private OutputStream out;
	        
		public ConnectedThread(BluetoothSocket btSocket) {
			this.btSocket = btSocket;
			try {
				in = btSocket.getInputStream();
				out = btSocket.getOutputStream();
				
				Thread thread = new Thread(this,btSocket.getRemoteDevice().toString());
				thread.start();
				
				logger.d("Set up bluetooth socket i/o stream");
			} catch(IOException e) {
				logger.e(" sockets not created",e);
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
					mHandler.obtainMessage(MESSAGE_WRITE_SUCCESS).sendToTarget();
					if(YFLog.DEBUG) {
						logger.d("send message bytes :" +count);
						logger.d(ByteUtils.printBytes(data, offset, count));
					}
					result = true;
				} else {
					mHandler.obtainMessage(MESSAGE_WRITE_FAILED).sendToTarget();
					result = false;
				}
				
			} catch(IOException e) {
				mHandler.obtainMessage(MESSAGE_WRITE_FAILED).sendToTarget();
				logger.e(" write message failed",e);
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
			setConnected(false);
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
				mHandler.obtainMessage(MESSAGE_CONNECT_CLOSE).sendToTarget();
				
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
				 while(connected()) {
					 readBytesCount = in.read(buf, 0, BUFFER_SIZE);
					 logger.d("read bytes:" +readBytesCount );
					 decoder.append(buf, 0, readBytesCount);
					 if(decoder.complete()) {
						 mHandler.obtainMessage(MESSAGE_READ,readBytesCount,readBytesCount,decoder).sendToTarget();
					 }
				 }
			} catch(Exception e) {
				logger.w("connection lost");
			} finally {
				//关闭bt
				this.close();
			}
		}
	}
}
