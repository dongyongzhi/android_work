package com.ctbri.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctbri.net.MessageType;


public class TransResponse  extends ElecResponse implements Parcelable {
 
	private static final long serialVersionUID = 6007223710775338277L;
	private static final DateFormat df = new SimpleDateFormat("yyyy");
	
	private int messageType;//交易类型
	private String card; //交易卡号
	private long money; //交易金额
	private String transNumber; //交易流水号 参考号
	private String transDate; //交易日期    MMDD
	private String transTime; //交易时间   hhmmss
	private String transDatetime; //交易日期和时间
	private String orderNumber;// 订单号
	
	private String pushCardNo;//发卡行号
	private String batchNo;//批次号
	private String voucherNo;//凭证号
	private String authNo;//授权码  (预授权时才有)
	
	private String cardExpDate;//卡有效期
	private String acquirer;//收单行号
	private String subErrMsg;//操作员提示错误信息
	
	private String remark;//备注
	private boolean print;//是否打印
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(messageType);
		dest.writeLong(money);
		dest.writeString(transNumber);
		dest.writeString(transDate);
		dest.writeString(transTime);
		dest.writeString(card);
		dest.writeString(orderNumber);
		
		dest.writeString(pushCardNo);
		dest.writeString(batchNo);
		dest.writeString(voucherNo);
		dest.writeString(authNo);
		
		dest.writeString(cardExpDate);
		dest.writeString(acquirer);
		dest.writeString(subErrMsg);
		dest.writeString(transDatetime);
		dest.writeString(remark);
		dest.writeByte(print? (byte)1 : (byte)0);
	}
	
	public static final Parcelable.Creator<TransResponse> CREATOR = new Parcelable.Creator<TransResponse>() {
		
		public TransResponse createFromParcel(Parcel source) {
			TransResponse  item = new TransResponse();
			item.messageType = source.readInt();
			item.money = source.readLong();
			item.transNumber = source.readString();
			item.transDate = source.readString();
			item.transTime = source.readString();
			item.card = source.readString();
			item.orderNumber = source.readString();
			
			item.pushCardNo = source.readString();
			item.batchNo = source.readString();
			item.voucherNo = source.readString();
			item.authNo = source.readString();
			
			item.cardExpDate = source.readString();
			item.acquirer = source.readString();
			item.subErrMsg = source.readString();
			item.transDatetime = source.readString();
			item.remark = source.readString();
			item.print = source.readByte()==0x01 ? true : false;
			
			return item;
		}
		public TransResponse[] newArray(int n) {
			return new TransResponse[n];
		}
	};

	public long getMoney() {
		return money;
	}
	
	public String getMoneyString(){
		return String.format("%,.2f 元",money/100.00);
	}

	public void setMoney(long money) {
		this.money = money;
	}

	public String getTransNumber() {
		return transNumber;
	}

	public void setTransNumber(String transNumber) {
		this.transNumber = transNumber;
	}

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public String getCard() {
		return card;
	}
	
	/**
	 * 返回格式化的卡号
	 * @return
	 */
	public String getFormatCard(){
		if(this.card==null)
			return "";
		
		//只取前后4位数字
		int len = this.card.length();
		if(len<8)
			return this.card;
	 
		char[] x = new char[len];
		for(int i=0;i<len;i++){
			if(i<4 || i>=len-4)
				x[i] = this.card.charAt(i);
			else
				x[i] = '*';
		}
		return new String(x);
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getMessageType() {
		return messageType;
	}
	
	public String getMessageTypeName(){
		if(messageType == MessageType.PRE_AUTHORIZE)
			return "预授权";
		else if(messageType == MessageType.ADD_PRE_AUTHORIZE)
			return "追加预授权";
		else if(messageType==MessageType.PURCHASE)
			return "消费";
		else if(messageType==MessageType.QUERYBALANCE)
			return "查询";
		else if(messageType==MessageType.RETURNS)
			return "退货";
		else if(messageType==MessageType.CANCEL_PURCHASE)
			return "消费撤销";
		else if(messageType==MessageType.CANCEL_PRE_AUTHORIZE)
			return "预授权撤销";
		else
			return "";
	}
	
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}
	
	public String getDateTime(){
		if(transDatetime!=null && !"".equals(transDatetime))
			return transDatetime;
		
		String dateTime = "";
		Date now = new Date();
		if(this.transDate!=null && !this.transDate.equals("") && this.transDate.length()==4){
			dateTime = String.format("%s-%s-%s ", df.format(now)
						,transDate.substring(0, 2)
						,transDate.substring(2, 4));
		}
		
		if(this.transTime!=null && !"".equals(this.transTime) && this.transTime.length()==6){
			dateTime = String.format("%s%s:%s:%s", dateTime
						,this.transTime.substring(0, 2)
						,this.transTime.substring(2, 4)
						,this.transTime.substring(4, 6));
		}
		this.transDatetime = dateTime;
		
		return transDatetime;
	}

	public String getPushCardNo() {
		return pushCardNo;
	}

	public void setPushCardNo(String pushCardNo) {
		this.pushCardNo = pushCardNo;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getAuthNo() {
		return authNo;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}

	public String getCardExpDate() {
		if(this.cardExpDate==null)
			return "";
		else if(this.cardExpDate.equals("0000"))
			return "";
		
		return cardExpDate;
	}

	public void setCardExpDate(String cardExpDate) {
		this.cardExpDate = cardExpDate;
	}

	public void setAcquirer(String acquirer) {
		this.acquirer = acquirer;
	}

	public String getAcquirer() {
		return acquirer;
	}

	public void setSubErrMsg(String subErrMsg) {
		this.subErrMsg = subErrMsg;
	}

	public String getSubErrMsg() {
		return subErrMsg;
	}

	public void setTransDatetime(String transDatetime) {
		this.transDatetime = transDatetime;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRemark() {
		return remark;
	}

	public void setPrint(boolean print) {
		this.print = print;
	}

	public boolean isPrint() {
		return print;
	}
}
