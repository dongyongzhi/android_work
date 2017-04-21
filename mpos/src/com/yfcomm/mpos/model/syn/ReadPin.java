package com.yfcomm.mpos.model.syn;

public class ReadPin extends SynPackage {

	private static final long serialVersionUID = -463542992327196209L;

	private byte[] activeCode;   //设置业务激活码，根据业务定义，无为空  
	private int timeout = 0;     //操作超时时间  单位秒
	private int minNumber = 4;   //PIN允许最小个数
	private int maxNumber = 4;  // PIN允许最大个数
	private byte[] random;  //随机数

	@Override
	public byte[] encode() {
		
		byte[] body = new byte[19];
		if(activeCode==null) {
			for(int i=0;i<8;i++) {
				body[i] = (byte)0xFF;
			}
		} else {
			System.arraycopy(this.activeCode, 0, body, 0, this.activeCode.length > 8 ? 8 : this.activeCode.length);
		}
		body[8] = (byte)this.timeout;
		body[9] = (byte)this.minNumber;
		body[10] = (byte)this.maxNumber;
		
		if(random!=null) {
			System.arraycopy(this.random, 0, body, 11, this.random.length>8 ?  8 : this.random.length);
		}
		
		return body;
	}

	public byte[] getActiveCode() {
		return activeCode;
	}

	/**
	 * 设置业务激活码，根据业务定义，无为空
	 * @param activeCode
	 */
	public void setActiveCode(byte[] activeCode) {
		this.activeCode = activeCode;
	}

	public int getTimeout() {
		return timeout;
	}

	/**
	 * 操作超时时间  单位秒
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
	 * @param random
	 */
	public void setRandom(byte[] random) {
		this.random = random;
	}

}
