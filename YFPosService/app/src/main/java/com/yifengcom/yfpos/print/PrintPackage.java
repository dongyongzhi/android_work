package com.yifengcom.yfpos.print;

import com.yifengcom.yfpos.utils.ByteUtils;

public class PrintPackage {

	/** pack STX **/
	public final static byte STX = 0x02;
	/** pack ETX **/
	public final static byte ETX = 0x03;

	private final byte[] body; // 内容
	private final byte lrc; // 校验和
	private final byte[] packData;// 包数据
	private final int bodyLenght;

	public PrintPackage(byte[] body) {
		this.bodyLenght = body == null ? 0 : body.length;
		this.body = body;

		// 组织数据包
		this.packData = new byte[5 + this.bodyLenght];
		this.packData[0] = STX;
		this.packData[1] = (byte) (this.bodyLenght >> 8);
		this.packData[2] = (byte) this.bodyLenght;
		
		// cmd
		// data[3] = (byte)pack.getCmd();
		
		//内容
		if (this.body != null) {
			System.arraycopy(body, 0, this.packData, 3, this.bodyLenght);
		}
		
		// 终止符
		this.packData[this.packData.length - 2] = ETX;
		
		// lrc
		this.lrc = ByteUtils.genLRC(this.packData, 1, this.bodyLenght+2);
		this.packData[this.packData.length - 1] = this.lrc;
	}

	public byte[] getBody() {
		return body;
	}

	public byte getLrc() {
		return lrc;
	}

	public byte[] getPackData() {
		return packData;
	}

	public int getBodyLenght() {
		return bodyLenght;
	}

}
