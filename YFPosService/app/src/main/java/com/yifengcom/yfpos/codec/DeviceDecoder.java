package com.yifengcom.yfpos.codec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import android.os.Handler;
import android.os.Message;
import com.yifengcom.yfpos.YFLog;
import com.yifengcom.yfpos.YFMPos;
import com.yifengcom.yfpos.YFPosApp;
import com.yifengcom.yfpos.exception.SystemCancelException;
import com.yifengcom.yfpos.exception.TimeoutException;
import com.yifengcom.yfpos.model.ack.Display;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
 * 协议解码
 * 
 * @author qc
 * 
 */
public class DeviceDecoder {

	private final static YFLog logger = YFLog.getLog(DeviceDecoder.class);

	// 缓冲区大小
	private final static int DEFAULT_SIZE = 2048;

	protected int dataLen = 0;

	private List<Thread> waitThreads = new ArrayList<Thread>(10);

	// 指针位置
	protected int pointer = 0;
	protected final byte[] buffer;

	// 缓存最多255个包
	private Map<Serializable, DevicePackage> cachePackages = new ConcurrentHashMap<Serializable, DevicePackage>(255);
	private final List<Serializable> lockPackageSequence = new ArrayList<Serializable>(255);

	public Handler handler;
	
	public Handler appHandler;

	public DeviceDecoder() {
		buffer = new byte[DEFAULT_SIZE];
		reset();
	}

	public DeviceDecoder(int size) {
		buffer = new byte[size];
		reset();
	}

	/**
	 * 等待解析完成
	 * 
	 * @param cmd
	 *            命令
	 * @param index
	 *            索引
	 * @param waitTime
	 *            等待时长
	 */
	public DevicePackage waitDecodeComplete(Serializable sequence, long waitTime) {
		logger.d("wait recv %s", sequence);
		try {
			if (!this.exists(sequence)) {
				lockPackageSequence.add(sequence);
				// 等超时通知
				synchronized (sequence) {
					waitThreads.add(Thread.currentThread());
					sequence.wait(waitTime);
				}
			}
			logger.d("wait complete");
			DevicePackage pack = this.cachePackages.get(sequence);
			// 是否超时
			if (pack == null) {
				throw new TimeoutException();
			} else {
				return pack;
			}

		} catch (InterruptedException e) {
			logger.e(e.getMessage());
			throw new SystemCancelException();

		} finally {
			lockPackageSequence.remove(sequence);
			waitThreads.remove(Thread.currentThread());
			cachePackages.remove(sequence);
		}
	}

	/**
	 * 取消解码
	 */
	public void cancel() {
		for (Thread waitThread : waitThreads) {
			waitThread.interrupt();
		}
		lockPackageSequence.clear();
		waitThreads.clear();
		cachePackages.clear();
	}

	/**
	 * append byte 数组
	 * 
	 * @param data
	 * @param start
	 * @param len
	 */
	public void append(byte[] data, int start, int len) {
		if (buffer.length - this.pointer < len) {
			// 长度不够
			throw new RuntimeException("buffer len overflow");
		}

		System.arraycopy(data, 0, buffer, this.pointer, len);
		this.pointer += len;
		
		logger.d("<<"+ByteUtils.printBytes(buffer, 0, this.pointer));

		// 验证是否包头
		if (pointer >= 2 
				&& ByteUtils.byteToInt(buffer[0], buffer[1]) != DevicePackage.STX) {
			logger.e("package stx error: " + ByteUtils.printBytes(buffer, 0, 2));
			reset();
			return;
		};
		
		// 解析数据长度 低字节优先传输
		if (this.pointer < 9)
			return;
		
		dataLen = ByteUtils.byteToIntLE(this.buffer[4], this.buffer[5]);
		
		//报文数据长度不足
		if (dataLen+9>pointer) 
			return;
		
		if (ByteUtils.byteToInt(this.buffer[7 + this.dataLen],
					this.buffer[8 + this.dataLen]) != DevicePackage.ETX) {
				logger.e("package etx error: "	+ ByteUtils.printBytes(this.buffer, this.dataLen + 7, 2));
			reset();
			return;
		} 
		
		// 验证LRC
		byte lrc = ByteUtils.genLRC(this.buffer, 2, dataLen + 4);
		if (lrc != this.buffer[6 + this.dataLen]) {
			logger.e("package lrc error recv: "
						+ ByteUtils
							.printByte(this.buffer[6 + this.dataLen])
						+ " result:" + ByteUtils.printByte(lrc));
			return;
		}
		complete();
			
		int LeftLen = this.pointer- (this.dataLen+9);
		if (LeftLen >0){
			byte[] tmpbuf = new byte[LeftLen];
			System.arraycopy(buffer, this.dataLen+9, tmpbuf, 0, LeftLen);
			this.pointer = 0;
			this.dataLen = 0;
			append(tmpbuf, 0, tmpbuf.length);
		}else{
			this.pointer = 0;
			this.dataLen = 0;
		}
	}

	/**
	 * 重置buffer
	 */
	public void reset() {
		resetBuffer();
		this.cachePackages.clear();
	}

	/**
	 * 获取缓冲区空间
	 * 
	 * @return
	 */
	public byte[] getBufferSpace() {
		return this.buffer;
	}

	public boolean exists(Serializable sequence) {
		synchronized (cachePackages) {
			return this.cachePackages.containsKey(sequence);
		}
	}

	public void remove(Serializable sequence) {
		this.cachePackages.remove(sequence);
	}

	private void add(Serializable sequence, DevicePackage pack) {
		synchronized (cachePackages) {
			this.cachePackages.put(sequence, pack);
		}
	}

	/**
	 * 获包数据
	 * 
	 * @return
	 */
	private DevicePackage getPackageData() {
		byte[] packData = new byte[this.dataLen + 9];
		System.arraycopy(this.buffer, 0, packData, 0, packData.length);
		return new DevicePackage(packData);
	}

	/**
	 * 解码完成
	 */
	private void complete() {
		// 放入数据包
		DevicePackage pack = this.getPackageData();

		if (pack.getCmd() == PackageBuilder.CMD_CURRENT_KEY) {
			if (YFPosApp.handler != null) {
				Message msg = new Message();
				msg.what = PackageBuilder.CMD_CURRENT_KEY;
				msg.obj = pack.getBody()[0] & 0xFF;
				YFPosApp.handler.sendMessage(msg);
			}
			return;
		}else if(pack.getCmd() == PackageBuilder.CMD_DISPLAY){
			if (YFPosApp.handler != null) {
				Display display = new Display();
				display.decode(pack);
				Message msg = new Message();
				msg.what = PackageBuilder.CMD_DISPLAY;
				msg.obj = display;
				YFPosApp.handler.sendMessage(msg);
			}
			return;
		}else if(pack.getCmd() == PackageBuilder.CMD_DESTROY){
			if (YFPosApp.handler != null) {
				Message msg = new Message();
				msg.what = PackageBuilder.CMD_DESTROY;
				msg.obj = pack.getBody();
				YFPosApp.handler.sendMessage(msg);
			}
			return;
		}else if (pack.getCmd() == PackageBuilder.CMD_EMV_TEST_RETURN){
			if (YFPosApp.handler != null) {
				Message msg = new Message();
				msg.what = PackageBuilder.CMD_EMV_TEST_RETURN;
				msg.obj = pack.getBody();
				YFPosApp.handler.sendMessage(msg);
			}
			return;		
		}

		Serializable completeSequence = pack.getPackSequence();
		logger.d("complete "+ completeSequence);
		this.add(completeSequence, pack);

		// 通知己解析完成
		for (Serializable sequence : this.lockPackageSequence) {
			if (sequence.equals(completeSequence)) {
				synchronized (sequence) {
					logger.d("notify %s", sequence);
					sequence.notifyAll();
				}
			}
		}
	}

	private void resetBuffer() {
		this.pointer = 0;
		this.dataLen = 0;
	}
}
