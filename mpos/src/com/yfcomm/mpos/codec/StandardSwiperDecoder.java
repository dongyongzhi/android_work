package com.yfcomm.mpos.codec;

import com.yfcomm.mpos.utils.ByteUtils;

/**
 * 标准 解码协议
 * 
 * @author qc
 * 
 */
public class StandardSwiperDecoder extends SwiperDecoder {

	private String serialNo;
	private String batchNo;

	private int track2Len;
	private int track3Len;
	private byte[] track2;
	private byte[] track3;

	@Override
	public void decodeMagnetic(byte[] data) {
		// 磁条卡数据解析
		ksn = ByteUtils.byteToHex(data, 0, 8);
		// 交易时间
		trxTime = ByteUtils.byteToHex(data, 8, 7);
		// 随机数
		this.random = new byte[8];
		System.arraycopy(data, 15, random, 0, 8);
		// 取出密文信息
		int len = data[23] & 0xFF;
		byte[] encData = new byte[len];
		System.arraycopy(data, 24, encData, 0, encData.length);

		// 解码加密内容
		decode(encData);

		// 获取MAC
		this.mac = new byte[8];
		System.arraycopy(data, 24 + encData.length, this.mac, 0,
				this.mac.length);

		// 批次号
		batchNo = ByteUtils.byteToHex(data, 32 + len, 3);
		// 流水号
		serialNo = ByteUtils.byteToHex(data, 35 + len, 3);

		// 非加密磁道数据
		// 二磁道长度
		track2Len = data[38 + len] & 0xFF;
		track2 = new byte[Double.valueOf( Math.ceil(track2Len/2.0)).intValue()];
		System.arraycopy(data, 39 + len, track2, 0, track2.length);
		
		// 三磁道长度
		track3Len = data[39 + len + track2.length] & 0xFF;
		track3 = new byte[Double.valueOf( Math.ceil(track3Len/2.0)).intValue()];
		System.arraycopy(data, 40 + len + track2.length, track3, 0, track3.length);
	}

	public void decodeIc(byte[] data) {
		// 交易时间
		trxTime = ByteUtils.byteToHex(data, 0, 7);
		// 随机数
		this.random = new byte[8];
		System.arraycopy(data, 7, random, 0, 8);
		// 取出密文信息
		int encDataLen = data[15] & 0xFF;
		byte[] encData = new byte[encDataLen];
		System.arraycopy(data, 16, encData, 0, encDataLen);
		// 解析
		this.decode(encData);

		// 获取MAC
		this.mac = new byte[8];
		System.arraycopy(data, 16 + encDataLen, this.mac, 0, this.mac.length);
		// 批次号
		batchNo = ByteUtils.byteToHex(data, 24 + encDataLen, 3);
		// 流水号
		serialNo = ByteUtils.byteToHex(data, 27 + encDataLen, 3);

		// 非加密磁道数据
		// 二磁道长度
		track2Len = data[30 + encDataLen] & 0xFF;
		track2 = new byte[Double.valueOf( Math.ceil(track2Len/2.0)).intValue()];
		System.arraycopy(data, 31 + encDataLen, track2, 0, track2.length);
		
		// 三磁道长度
		track3Len = data[31 + encDataLen + track2.length] & 0xFF;
		track3 = new byte[Double.valueOf( Math.ceil(track3Len/2.0)).intValue()];
		System.arraycopy(data, 32 + encDataLen + track2.length, track3, 0,track3.length);

		int offset = 32 + encDataLen + track2.length + track3.length;
		// icdata
		int iccdataLen = data.length- offset;
		this.icData = new byte[iccdataLen];
		System.arraycopy(data, offset, this.icData, 0, iccdataLen);
	}

	public String getSerialNo() {
		return serialNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public int getTrack2Len() {
		return track2Len;
	}

	public int getTrack3Len() {
		return track3Len;
	}

	public byte[] getTrack2() {
		return track2;
	}

	public byte[] getTrack3() {
		return track3;
	}

}
