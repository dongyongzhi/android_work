package com.yfcomm.mpos.codec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yfcomm.mpos.YFLog;
import com.yfcomm.mpos.exception.SystemCancelException;
import com.yfcomm.mpos.exception.TimeoutException;
import com.yfcomm.mpos.utils.ByteUtils;

/**
 * 协议解码
 * @author qc
 *
 */
public class DeviceDecoder {
	
	private final static YFLog logger = YFLog.getLog(DeviceDecoder.class);
	
	//缓冲区大小
	private final static int DEFAULT_SIZE = 2048;
	
	protected int dataLen = 0;
	
	private List<Thread> waitThreads= new ArrayList<Thread>(10);
	 
	//指针位置
	protected int pointer = 0;
	protected final byte[] buffer;
	
	//缓存最多255个包
	private Map<Serializable,DevicePackage> cachePackages  = new ConcurrentHashMap<Serializable,DevicePackage>(255);
	private final List<Serializable> lockPackageSequence = new ArrayList<Serializable>(255);
	

	public DeviceDecoder(){
		buffer = new byte[DEFAULT_SIZE];
		reset();
	}
	
	public DeviceDecoder(int size) {
		buffer = new byte[size];
		reset();
	}
	
	/**
	 * 等待解析完成
	 * @param cmd       命令
	 * @param index     索引
	 * @param waitTime  等待时长
	 */
    public DevicePackage waitDecodeComplete(Serializable sequence,long waitTime) {
    	logger.d("wait recv %s",sequence);
    	try {
	    	if(!this.exists(sequence)) {
	    		lockPackageSequence.add(sequence);
	    		//等超时通知
	    		synchronized(sequence) {
	    			waitThreads.add(Thread.currentThread());
	    			sequence.wait(waitTime);
	    		}
	    	} 
	    	logger.d("wait complete");
	    	DevicePackage pack = this.cachePackages.get(sequence);
	    	//是否超时
	    	if(pack == null) {
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
 		}
    }
    
    /**
     * 取消解码
     */
    public void cancel() {
    	for(Thread waitThread : waitThreads) {
    		waitThread.interrupt();
    	}
    }
	
	/**
	 * append byte 数组
	 * @param data  
	 * @param start
	 * @param len
	 */
	public void append(byte[] data,int start,int len) {
		if(buffer.length - this.pointer < len) {
			//长度不够
			throw new RuntimeException("buffer len overflow");
		}
		
		logger.d("decoder bytes :" +len);
		logger.d(ByteUtils.printBytes(data,start,len));
		
		//验证是否包头
		if(this.pointer == 0 && data.length>=2 && ByteUtils.byteToInt(data[0], data[1])!=DevicePackage.STX)  {
			reset();
			logger.e("package stx error: " + ByteUtils.printBytes(data, 0, 2));
		} else {
			System.arraycopy(data, 0, buffer, this.pointer, len);
			this.pointer += len;
		}
		
		//解析数据长度 低字节优先传输
		if(dataLen == 0 && this.pointer >=6) {
			dataLen = ByteUtils.byteToIntLE(this.buffer[4], this.buffer[5]);
		}
		
		//是否完成
		if(dataLen>0 && dataLen <= this.pointer-9) {
			//包结束标识不正确
			if(ByteUtils.byteToInt(this.buffer[7+this.dataLen],this.buffer[8 + this.dataLen]) != DevicePackage.ETX) {
				logger.e("package etx error: " + ByteUtils.printBytes(this.buffer, this.dataLen+7, 2));
			}else {
				//验证LRC
				byte lrc = ByteUtils.genLRC(this.buffer, 2, dataLen+4);
				if(lrc != this.buffer[6+this.dataLen]) {
					logger.e("package lrc error recv: " + ByteUtils.printByte(this.buffer[6+this.dataLen]) + " result:"+ByteUtils.printByte(lrc));
				} else {
					complete();
				}
			}
			resetBuffer();
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
	 * @return
	 */
	public byte[] getBufferSpace(){
		return this.buffer;
	}
	
	public boolean exists(Serializable sequence) {
		synchronized(cachePackages) {
			return this.cachePackages.containsKey(sequence);
		}
	}
	
	public void remove(Serializable sequence) {
		this.cachePackages.remove(sequence);
	}
	
	private void add(Serializable sequence,DevicePackage pack) {
		synchronized (cachePackages) {
			this.cachePackages.put(sequence, pack);
		}
	}
	
	/**
	 * 获包数据
	 * @return
	 */
	private DevicePackage getPackageData(){
		byte[] packData = new byte[this.dataLen + 9];
		System.arraycopy(this.buffer, 0, packData, 0, packData.length);
		return new DevicePackage(packData);
	}
	
	/**
	 * 解码完成
	 */
	private void complete() {
		//放入数据包
		DevicePackage pack = this.getPackageData();
		Serializable completeSequence = pack.getPackSequence();
	    this.add(completeSequence, pack);
		
		//通知己解析完成
		for(Serializable sequence : this.lockPackageSequence) {
			if(sequence.equals(completeSequence)) {
				synchronized(sequence) {
					logger.d("notify %s",sequence);
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
