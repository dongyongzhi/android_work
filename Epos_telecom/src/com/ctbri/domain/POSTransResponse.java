package com.ctbri.domain;

import com.ctbri.ElecObject;

/**
 * 消费应答
 * @author qin
 * 
 * 2012-11-19
 */
public class POSTransResponse extends ElecObject {
 
	private static final long serialVersionUID = 1855370158836628700L;
	
	private ResponseCode state = ResponseCode.EXEC_CMD_FAIL;  //返回状态 
	private int len;           //返回数据长度
	private byte[] data;      //返回数据
	private int messageType;
	
	private TransResponse transResponse;
	 
	public void setState(int state) {
		this.state = ResponseCode.convert(state);
	}
	public ResponseCode getState() {
		return state;
	}
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	public void setTransResponse(TransResponse transResponse) {
		this.transResponse = transResponse;
	}
	public TransResponse getTransResponse() {
		return transResponse;
	}
	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
	public int getMessageType() {
		return messageType;
	}
}
