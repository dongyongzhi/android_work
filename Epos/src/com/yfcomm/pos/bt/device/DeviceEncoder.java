package com.yfcomm.pos.bt.device;

import com.yfcomm.pos.utils.ByteUtils;

/**
 * 编码
 * @author qc
 *
 */
public class DeviceEncoder extends DevicePackage {
	
	/**
	 * 编码
	 * @param cmd  命令
	 * @param in   数据
	 * @return
	 */
	public byte[] encode(byte cmd,byte[] in) {
		int len = in.length;
		
		//返回数据空间
		byte[] data = new byte[len + 6];
		data[0] = STX;
		
		//长度
		byte[] dataLen = ByteUtils.unsignedShort(len);
		data[1] = dataLen[0];
		data[2] = dataLen[1];
		
		//CMD
		data[3] = cmd;
		
		//数据内容
		System.arraycopy(in, 0, data, 4, len);
		//获取 LRC
		byte lrc = this.genLRC(data, 1, len+3);
		data[data.length-2] = ETX;
		
		data[data.length-1] = lrc;
		return data;
	}
}
