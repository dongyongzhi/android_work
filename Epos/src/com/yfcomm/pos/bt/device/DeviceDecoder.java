package com.yfcomm.pos.bt.device;

import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.utils.ByteUtils;

public class DeviceDecoder extends DevicePackage {
	private final static YFLog logger = YFLog.getLog(DeviceDecoder.class);
	
	//缓冲区大小
	private final static int DEFAULT_SIZE = 2048;
	//解析成功
	public final static int SUCCESS = 0;
	//解析出错
	public final static int ERROR = -1;
	
	private boolean isCompelete = false;
	private int resultCode = SUCCESS;
	private int dataLen = 0;
	
	//返回包命令
	private byte cmd = 0x00;
	
	//指针位置
	private int pointer = 0;
	private final byte[] buffer;
	
	private DeviceDecoder(){
		buffer = new byte[DEFAULT_SIZE];
		reset();
	}
	
	private DeviceDecoder(int size) {
		buffer = new byte[size];
		reset();
	}

	/**
	 * 获取一个新的解码器
	 * @return
	 */
	public static DeviceDecoder newDecoder(){
		return new DeviceDecoder();
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
		if(this.pointer == 0 && data.length>0 && data[0]!=STX)  {
			this.isCompelete = true;
			this.resultCode = ERROR;
		} else {
			System.arraycopy(data, 0, buffer, this.pointer, len);
			this.pointer += len;
		}
		
		//解析数据长度
		if(dataLen == 0 && this.pointer >=3) {
			dataLen = ((this.buffer[1] & 0xFF) << 8) | (this.buffer[2] & 0xFF);
		}
		
		//是否完成
		if(dataLen>0 && dataLen <= this.pointer-6) {
			//接收数据包完成
			this.isCompelete = true;
			//包结束标识不正确
			if(this.buffer[this.pointer-2] != ETX) {
				this.resultCode = ERROR;
			}else {
				//验证LRC
				byte lrc = this.genLRC(this.buffer, 1, dataLen+3);
				if(lrc != this.buffer[this.pointer-1]) {
					this.resultCode = ERROR;
				} else {
					this.resultCode = SUCCESS;
					//获取命令
					this.cmd = this.buffer[3];
				}
			}
		}
	}
	
	/**
	 * 解码完成
	 * @return
	 */
	public boolean complete(){
		return isCompelete;
	}
	
	/**
	 * 重置buffer
	 */
	public void reset() {
		this.pointer = 0;
		this.isCompelete = false;
		this.dataLen = 0;
		this.resultCode = 0;
		this.cmd = 0;
	}
	
	/**
	 * 获取缓冲区空间
	 * @return
	 */
	public byte[] getBufferSpace(){
		return this.buffer;
	}
	
	/**
	 * 获包数据
	 * @return
	 */
	public byte[] getPackageData(){
		byte[] data = new byte[pointer];
		System.arraycopy(this.buffer, 0, data, 0, pointer);
		return data;
	}
	
	/**
	 * 获取
	 * @param out
	 */
	public void getData(byte[] out) {
		if(this.isCompelete && this.resultCode == SUCCESS) {
			System.arraycopy(this.buffer, 4, out, 0, this.dataLen);
		}
	}
	
	/**
	 * 获取解析返回码
	 * @return
	 */
	public int getResultCode() {
		return resultCode;
	}
	
	/**
	 * 获取当前解码出来的命令
	 * @return
	 */
	public byte getCmd() {
		return this.cmd;
	}
}
