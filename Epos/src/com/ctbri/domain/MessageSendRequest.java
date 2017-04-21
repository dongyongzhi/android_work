package com.ctbri.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ctbri.Constants;


public class MessageSendRequest extends ElecRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2229989086536489735L;

	private String cutomerOrderNumber; // 商户订单号.
	private String type; // 发送方式 0：短信，1：邮件
	private String toUser;// 接收方
	private String message;// 发送消息 可为空，此时有MPOSP下发固定内容

	public String getCutomerOrderNumber() {
		return cutomerOrderNumber;
	}

	public void setCutomerOrderNumber(String cutomerOrderNumber) {
		this.cutomerOrderNumber = cutomerOrderNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getPayData() {
		//message 中文utf-8 转换成gb2312
		 
	    try {
			message = URLEncoder.encode(message,Constants.PACK_CHARSET);// new String(message.getBytes(Constants.PACK_CHARSET),Constants.PACK_CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		 
		//返回 json字符串
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"message\":").append("\""+message+"\",");
		sb.append("\"type\":").append("\""+type+"\",");
		sb.append("\"customerOrderNumber\":").append("\""+cutomerOrderNumber+"\",");
		sb.append("\"toUser\":").append("\""+toUser+"\"");
		sb.append("}");
		return sb.toString();
	}

}
