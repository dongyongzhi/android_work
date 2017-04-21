package com.yifengcom.yfpos.serialport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import serialapi.SerialPort;

import com.yifengcom.yfpos.DeviceInfo;
import com.yifengcom.yfpos.DeviceSession;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.codec.DeviceDecoder;
import com.yifengcom.yfpos.listener.ConnectionStateListener;
import com.yifengcom.yfpos.listener.DeviceStateChangeListener;
import com.yifengcom.yfpos.utils.ByteUtils;

public class SerialportSession implements DeviceSession {

	private final static YFLog logger = YFLog.getLog(SerialportSession.class);
	// 解码编码
	private final DeviceDecoder decoder;
	private ConnectedThread connectedThread = null;

	private SerialPort mSerialPort = null;
	private final String mSerialDevice = "/dev/ttyUSB1";
	private final int mBaudrate = 115200;
	
	private volatile boolean isConnected = false;
	
	
	public SerialportSession(DeviceDecoder decoder) {
		this.decoder = decoder;
		connectedThread = new ConnectedThread();
	}

	@Override
	public void setOnDeviceStateChangeListener(
			DeviceStateChangeListener listener) {

	}

	@Override
	public void connect(DeviceInfo deviceInfo,
			ConnectionStateListener openDeviceListener) {

	}

	@Override
	public void connect(DeviceInfo deviceInfo, long timeout,
			ConnectionStateListener openDeviceListener) {

	}

	@Override
	public boolean connected() {
		return isConnected;
	}

	@Override
	public void close() {
		if(connectedThread!=null) {
			connectedThread.close();
			connectedThread = null;
		}
	}

	@Override
	public boolean write(byte[] data) {
		return this.write(data, 0, data.length);
	}

	@Override
	public boolean write(byte[] data, int offset, int count) {
		if (connectedThread != null) {
			return connectedThread.write(data, offset, count);
		} else {
			return false;
		}
	}

	public class ConnectedThread implements Runnable {
		private final static int BUFFER_SIZE = 1024;
		private InputStream in;
		private OutputStream out;
		public Thread thread = null;

		public ConnectedThread() {
			try {
				mSerialPort = getSerialPort();
				out = mSerialPort.getOutputStream();
				in = mSerialPort.getInputStream();

				setConnected(true);

				/* Create a receiving thread */
				thread = new Thread(this);
				thread.start();
				
			} catch (SecurityException e) {
				logger.e("没有有读写串口的权限", e);
			} catch (IOException e) {
				logger.e("串行端口不能打开", e);
			} catch (InvalidParameterException e) {
				logger.e("请先设置正确的串口", e);
			} catch(Exception e){
                                logger.e("serial error", e);
                        }
		}

		/**
		 * 写入数据
		 * 
		 * @param data
		 *            数据
		 */
		public synchronized boolean write(byte[] data, int offset, int count) {
			boolean result = false;
			try {
				if (out != null) {
					out.write(data, offset, count);
					if (YFLog.DEBUG) {
						logger.d(">>" + ByteUtils.printBytes(data, offset, count));
					}
					result = true;
				} else {
					result = false;
				}
			} catch (IOException e) {
				logger.e("write message failed", e);
				result = false;
			}
			return result;
		}

		public synchronized boolean write(byte[] data) {
			return this.write(data, 0, data.length);
		}

		public synchronized void close() {
			try {
				setConnected(false);
				if(in!=null) {
					in.close();
				}
				if(out!=null) {
					out.close();
				}
				if (mSerialPort != null) {
					mSerialPort.close();
					mSerialPort = null;
				}
				if(thread != null && thread.isAlive()){
					thread.interrupt();
					thread = null;
				}
			}catch (IOException e) {
				logger.e("connect close failed", e);
			}
		}

		@Override
		public void run() {
			while (isConnected) {
				int size;
				try {
					byte[] buffer = new byte[BUFFER_SIZE];
					if (in == null)
						return;
					size = in.read(buffer);
					if (size > 0) {
						decoder.append(buffer, 0, size);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	
	
	private synchronized void setConnected(boolean isConnected) {
		logger.d("setConnected  "+isConnected);
		this.isConnected = isConnected;
	}

	public SerialPort getSerialPort() throws SecurityException, IOException,
			InvalidParameterException {
		if (mSerialPort == null) {
			try{
				mSerialPort = new SerialPort(new File(mSerialDevice), mBaudrate, 0);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return mSerialPort;
	}
}
