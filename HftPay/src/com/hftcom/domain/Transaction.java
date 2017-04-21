package com.hftcom.domain;

import java.io.Serializable;

public class Transaction implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String _id; // id
	private String serial_no; // 流水号
	private String send_time; // 交易发送时间
	private String send_result; // 交易发送结果（已发送、已接收、已超时、故障）
	private String card_type; // 卡类型
	private String correct_num; // 冲正次数
	private String trading_time; // 交易日期
	private String result_code; // 交易结果-编号
	private String result_msg; // 交易结果-信息
	private String mac; // 发送报文的mac
	private String print_data; // 打印数据
	private String ic_data; // ic卡数据
	private String money; // 金额分

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getIc_data() {
		return ic_data;
	}

	public void setIc_data(String ic_data) {
		this.ic_data = ic_data;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getSerial_no() {
		return serial_no;
	}

	public void setSerial_no(String serial_no) {
		this.serial_no = serial_no;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	public String getSend_result() {
		return send_result;
	}

	public void setSend_result(String send_result) {
		this.send_result = send_result;
	}

	public String getCard_type() {
		return card_type;
	}

	public void setCard_type(String card_type) {
		this.card_type = card_type;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getCorrect_num() {
		return correct_num;
	}

	public void setCorrect_num(String correct_num) {
		this.correct_num = correct_num;
	}

	public String getTrading_time() {
		return trading_time;
	}

	public void setTrading_time(String trading_time) {
		this.trading_time = trading_time;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getResult_msg() {
		return result_msg;
	}

	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPrint_data() {
		return print_data;
	}

	public void setPrint_data(String print_data) {
		this.print_data = print_data;
	}

}
