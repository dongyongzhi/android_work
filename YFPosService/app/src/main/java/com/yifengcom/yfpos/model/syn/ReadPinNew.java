package com.yifengcom.yfpos.model.syn;

import com.yifengcom.yfpos.utils.ByteUtils;

public class ReadPinNew extends SynPackage {

	private static final long serialVersionUID = -380803931905323638L;
	
	private int timeout = 0;     //操作超时时间  单位秒
	private int leftX = 0;       //键盘左上角坐标x
	private int leftY = 714;		//键盘左上角坐标y
	private int rightX = 720;     //键盘右下角坐标x
	private int rightY = 1280;     //键盘右下角坐标y

	@Override
	public byte[] encode() {
		byte[] body = new byte[9];
		body[0] = (byte)this.timeout;
		System.arraycopy(ByteUtils.unsignedShort(this.leftX), 0,
				body, 1, 2);
		System.arraycopy(ByteUtils.unsignedShort(this.leftY), 0,
				body, 3, 2);
		System.arraycopy(ByteUtils.unsignedShort(this.rightX), 0,
				body, 5, 2);
		System.arraycopy(ByteUtils.unsignedShort(this.rightY), 0,
				body, 7, 2);
		return body;
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

	 

}
