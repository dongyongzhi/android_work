package com.yfcomm.mpos.model.syn;

import com.yfcomm.businesshall.ByteUtils;
import com.yfcomm.public_define.public_define;

public class ReadPin extends SynPackage {

	private static final long serialVersionUID = -463542992327196209L;

	private byte[] activeCode; // 设置业务激活码，根据业务定义，无为空
	private int timeout = 0; // 操作超时时间 单位秒
	private int minNumber = 4; // PIN允许最小个数
	private int maxNumber = 4; // PIN允许最大个数
	private byte[] random; // 随机数

	@Override
	public byte[] encode() {

		byte[] body = new byte[9];
		body[0] = 60;
		for (int i = 0; i < 4; i++)
			System.arraycopy(ByteUtils.unsignedShort(public_define.Keyboardcoordinates[i]), 0, body, 1 + 2 * i, 2);

		return body;
	}

	public byte[] getActiveCode() {
		return activeCode;
	}

	/**
	 * 设置业务激活码，根据业务定义，无为空
	 * 
	 * @param activeCode
	 */
	public void setActiveCode(byte[] activeCode) {
		this.activeCode = activeCode;
	}

	public int getTimeout() {
		return timeout;
	}

	/**
	 * 操作超时时间 单位秒
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getMinNumber() {
		return minNumber;
	}

	/**
	 * PIN允许最小个数
	 * 
	 * @param minNumber
	 */
	public void setMinNumber(int minNumber) {
		this.minNumber = minNumber;
	}

	public int getMaxNumber() {
		return maxNumber;
	}

	/**
	 * PIN允许最大个数
	 * 
	 * @param maxNumber
	 */
	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}

	public byte[] getRandom() {
		return random;
	}

	/**
	 * 随机数
	 * 
	 * @param random
	 */
	public void setRandom(byte[] random) {
		this.random = random;
	}

}
