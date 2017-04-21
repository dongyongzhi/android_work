package com.yfcomm.pos.bt.device;

public class DevicePackage {

	/**
	 * pack STX
	 */
	public final static byte STX = 0x02;
	
	/**
	 * pack ETX 
	 */
	public final static byte ETX = 0x03;
	

	/**打包命令**/
	public final static byte CMD_PACK = 0x01;
	/**解包命令**/
	public final static byte CMD_UNPACK = 0x02;
	/**认证命令**/
	public final static byte CMD_AUTH = 0x03;
	/**设置超时时长**/
	public final static byte CMD_TIMEOUT = 0x04;
	
	/**
	 * 获取 LRC
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public byte genLRC(byte[] data,int offset,int len) {
		byte lrc = 0;
		for(int i=offset,end=offset+len; i<end; i++) {
			lrc ^=data[i];
		}
		return lrc;
	}
}
 