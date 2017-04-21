package com.yfcomm.pos;

import java.io.IOException;

import android.util.Log;

public class SerialPort {

	private static final String TAG = "SerialPort";
	private boolean running = false;
	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	private int mFd;
	private int baudrate;
	private SerialPortListener listener = null;
	private boolean isDelay=false;
   
	public static interface SerialPortListener{
		 public void  OnRcvData(byte[] rcvData,int rcvlen);
	}
	
	
	public SerialPort(String device, int baudrate, SerialPort.SerialPortListener listener) throws IOException {

		mFd = open(device, baudrate);
		if (mFd == 0) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		Log.e(TAG, "FD=" + mFd);
		this.baudrate = baudrate;
		if (0 == set(mFd, baudrate, 0, 8, 1, 'N')) {
			Log.e(TAG, "set mfd failed");
			throw new IOException();
		}
		this.listener = listener;
		running = true;
		new SerialrcvThread().start();
         
	}

	public int getInputStream(byte[] rcvbuf) {
		int state = sel(mFd);
		if (state > 0) {
			return rcv(mFd, rcvbuf, rcvbuf.length, baudrate);
		} else if (state < 0) {
			return -1;
		}
		return 0;
	}

	private class SerialrcvThread extends Thread {
		byte[] rcvbuf = new byte[1024];

		public void run() {
			while (running) {
				int len = getInputStream(rcvbuf);
				if (len < 0)
					break;
				else if (len == 0)
					continue;
				else {
					if (listener != null) {
						listener.OnRcvData(rcvbuf, len);
					}
				}

			}

		}
	}

	public void setlistener(SerialPortListener listener) {
		this.listener = listener;
	}

	public int getInputStream(byte[] rcvbuf, int rcvlen) {

		int state = sel(mFd);
		if (state > 0) {
			return rcv(mFd, rcvbuf, rcvlen, baudrate);
		} else if (state < 0) {
			return -1;
		}
		return 0;

	}

	public int getOutputStream(byte[] sendbuf) {

		return send(mFd, sendbuf, sendbuf.length);
	}

	public int getOutputStream(byte[] sendbuf, int sendlen) {

		return send(mFd, sendbuf, sendlen);
	}

	public void close() {
		close(mFd);
	}

	// JNI
	private native int open(String path, int baudrate);

	private native void close(int mFd);

	private native int sel(int mFd);

	private native int send(int mFd, byte[] sendbuf, int sendlen);

	private native int rcv(int mFd, byte[] rcvbuf, int rcvmaxlen, int speed);

	private native int set(int fd, int speed, int flow_ctrl, int databits, int stopbits, int parity);

	static {
		System.loadLibrary("serial_port");
	}
}
