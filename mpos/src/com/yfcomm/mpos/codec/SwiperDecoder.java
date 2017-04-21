package com.yfcomm.mpos.codec;

import com.yfcomm.mpos.utils.ByteUtils;

/**
 * 刷卡解码
 * @author qc
 *
 */
public class SwiperDecoder {
	
	protected String ksn; //ksn
	protected String trxTime;  //交易序列号
	protected byte[] random;  //随机数
	protected byte[] mac;   //mac
	
	protected String expiryDate; //过期日期
	protected String pan;//卡号
	protected String icSeq;// ic卡序列号
	
	protected int encryTrack2Len;
	protected int encryTrack3Len;
	protected byte[] encryTrack2;
	protected byte[] encryTrack3;
	
	protected byte[] icData;

	/**
	 * 解码磁条卡
	 * @param data
	 */
	public void decodeMagnetic(byte[] data) {
		//磁条卡数据解析
		ksn =  ByteUtils.byteToHex(data,0,8);
		//交易时间
		trxTime = ByteUtils.byteToHex(data,8,7) ;
		//随机数
		this.random = new byte[8];
		System.arraycopy(data, 15, random, 0, 8);
		//取出密文信息
		byte[] encData = new byte[data.length-32];
		System.arraycopy(data, 24, encData, 0, encData.length);
		
		//解码加密内容
		decode(encData);
		
		//获取MAC
		this.mac = new byte[8];
		System.arraycopy(data, 24+encData.length, this.mac, 0, this.mac.length);
	}
	
	/**
	 * 解码ic卡
	 * @param data
	 */
	public void decodeIc(byte[] data) {
		//交易时间
		trxTime = ByteUtils.byteToHex(data,0,7) ;
		//随机数
		this.random = new byte[8];
		System.arraycopy(data, 7, random, 0, 8);
		//取出密文信息
		int encDataLen = data[15] & 0xFF;
		byte[] encData = new byte[encDataLen];
		System.arraycopy(data, 16, encData, 0, encDataLen);
		//解析
		this.decode(encData);
		
		//获取MAC
		this.mac = new byte[8];
		System.arraycopy(data, 16+encDataLen, this.mac, 0, this.mac.length);
		
		//icdata
		int iccdataLen = data.length - (24 + encDataLen);
		this.icData = new byte[iccdataLen];
		System.arraycopy(data, 24 + encDataLen, this.icData, 0, iccdataLen);
	}
	
	 
	protected void decode(byte[] encrypt) {

		//过期日期
		this.expiryDate = ByteUtils.byteToHex(encrypt[0],encrypt[1]);
		int panLen = encrypt[2] & 0xFF;
		int panByteSize = Double.valueOf( Math.ceil(panLen/2.0)).intValue();
		
		//卡号
		byte[] panData = new byte[panByteSize];
		System.arraycopy(encrypt, 3, panData, 0, panByteSize);
		this.pan = ByteUtils.byteToHex(panData);
		this.pan = panLen % 2 ==0 ? this.pan  :  this.pan.substring(0, panLen);
	 
		//二磁道 
		this.encryTrack2Len = encrypt[3+panByteSize] & 0xFF;
		this.encryTrack2 = new byte[Double.valueOf( Math.ceil(encryTrack2Len/2.0)).intValue()];
		System.arraycopy(encrypt, 4+panByteSize, encryTrack2, 0, encryTrack2.length);
		
		//三磁道
		this.encryTrack3Len = encrypt[4+panByteSize+encryTrack2.length] & 0xFF;
		this.encryTrack3 = new byte[Double.valueOf( Math.ceil(encryTrack3Len/2.0)).intValue()];
		System.arraycopy(encrypt, 5+panByteSize+encryTrack2.length, encryTrack3, 0, encryTrack3.length);
		
		//解析 ic卡序列号
		int offset = 5 + panByteSize + encryTrack2.length + encryTrack3.length;
		if(encrypt.length - offset >= 3 ) {
			this.icSeq = new String(encrypt,offset,3);
		}
	}

	public String getKsn() {
		return ksn;
	}

	public String getTrxTime() {
		return trxTime;
	}

	public byte[] getRandom() {
		return random;
	}

	public byte[] getMac() {
		return mac;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public String getPan() {
		return pan;
	}

	public String getIcSeq() {
		return icSeq;
	}

	public int getEncryTrack2Len() {
		return encryTrack2Len;
	}

	public int getEncryTrack3Len() {
		return encryTrack3Len;
	}

	public byte[] getEncryTrack2() {
		return encryTrack2;
	}

	public byte[] getEncryTrack3() {
		return encryTrack3;
	}

	public byte[] getIcData() {
		return icData;
	}
	
}
