package com.ctbri.domain;

import com.ctbri.ElecObject;
import com.yfcomm.pos.tlv.TLVCollection;

/**
 * 消费应答
 * 
 * @author qin
 * 
 *         2012-11-19
 */
public class POSTransResponse extends ElecObject {

	private static final long serialVersionUID = 1855370158836628700L;

	private ResponseCode code = ResponseCode.DATA_ERROR; // 返回状态
	private int len; // 返回数据长度
	private int offset = 0; //数据偏移地址
	private byte[] data; // 返回数据
	//private String messageType;
 
	public POSTransResponse() {
		
	}

	public POSTransResponse(ResponseCode responseCode) {
		this.code = responseCode;
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
	
	public TransResponse decode(){
		TransResponse transResponse = null;
		if(data!=null ) {
			TLVCollection tc = new TLVCollection();
			tc.decode(data, offset, len);
			transResponse = new TransResponse();
			transResponse.setPrint(false);
			//交易结果
			transResponse.setErrCode(tc.getValueString(0x9F2D));
			if(transResponse.getErrCode()!=null && transResponse.getErrCode().equals("00"))  {
				String money = tc.getValueString(0x9F10);
				if(money!=null && !money.equals("")) {
					transResponse.setMoney(Long.valueOf(money));
				}
				transResponse.setCard(tc.getValueString(0x9F07));
				transResponse.setTransTime(tc.getValueString(0x9F0D));
				transResponse.setTransNumber(tc.getValueString(0x9F0F));
				transResponse.setBatchNo(tc.getValueString(0x9F0B));
				transResponse.setVoucherNo(tc.getValueString(0x9F0C));
				
				transResponse.setBusiness(tc.getValueString(0x9F01));
				transResponse.setBusinessNo(tc.getValueString(0x9F02));
				transResponse.setTermNo(tc.getValueString(0x9F03));
			}
		}
		return transResponse;
	}

	/*
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getMessageType() {
		return messageType;
	}
	*/

	public ResponseCode getCode() {
		return code;
	}

	public void setCode(ResponseCode code) {
		this.code = code;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
