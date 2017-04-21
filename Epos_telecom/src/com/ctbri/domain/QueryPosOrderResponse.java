package com.ctbri.domain;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryPosOrderResponse extends ElecResponse {

	/**  */
	private static final long serialVersionUID = -1254845021181897198L;

	private List<Order> orders  = new ArrayList<Order>();
	private long count;//总的记录数据

	public static class Order implements Parcelable {
		private String orderCode; // 订单号
		private String trxtime; // 交易时间
		private long money; // 交易金额
		private Long time; //交易时间
		
		private OrderState status;// 状态 1-已支付，0-未支付，2－已冲正、3-已撤销、4－已退款、5－其他失败
		
		private String posBatch;//批次号.
		private String posRequestId;//凭证号.
		private String externalid; //流水号.
		
		private String validateUrl;//电子签名地址URL
		private String cardNo;//卡号后四位
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int arg1) {
			dest.writeString(orderCode);
			dest.writeString(trxtime);
			dest.writeLong(money);
			dest.writeLong(time);
			dest.writeInt(status.value);
			dest.writeString(posBatch);
			dest.writeString(posRequestId);
			dest.writeString(externalid);
			dest.writeString(validateUrl);
			dest.writeString(cardNo);
		}
		
		public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>(){
			@Override
			public Order createFromParcel(Parcel source) {
				Order order = new Order();
				order.orderCode = 	source.readString();
			   	order.trxtime  = source.readString();
			   	order.money = source.readLong();
			   	order.time = source.readLong();
			   	order.status = OrderState.convert(source.readInt());
			   	order.posBatch = source.readString();
			   	order.posRequestId = source.readString();
			   	order.externalid = source.readString();
			   	order.validateUrl = source.readString();
			   	order.cardNo = source.readString();
				return order;
			}

			@Override
			public Order[] newArray(int size) {
				return new Order[size];
			}
		};
		

		public String getOrderCode() {
			return orderCode;
		}

		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}

		public String getTrxtime() {
			return trxtime;
		}

		public void setTrxtime(String trxtime) {
			this.trxtime = trxtime;
		}

		public long getMoney() {
			return money;
		}

		public void setMoney(long money) {
			this.money = money;
		}

		public OrderState getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = OrderState.convert(status);
		}
		
		public void setStatus(OrderState status){
			this.status = status;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public Long getTime() {
			return time;
		}

		public String getPosBatch() {
			return posBatch;
		}

		public void setPosBatch(String posBatch) {
			this.posBatch = posBatch;
		}

		public String getPosRequestId() {
			return posRequestId;
		}

		public void setPosRequestId(String posRequestId) {
			this.posRequestId = posRequestId;
		}

		public String getExternalid() {
			return externalid;
		}

		public void setExternalid(String externalid) {
			this.externalid = externalid;
		}

		public String getValidateUrl() {
			return validateUrl;
		}

		public void setValidateUrl(String validateUrl) {
			this.validateUrl = validateUrl;
		}

		public String getCardNo() {
			return cardNo;
		}

		public void setCardNo(String cardNo) {
			this.cardNo = cardNo;
		}
	}

	/**
	 * 订单状态
	 * 
	 * @author qin
	 * 
	 *         2012-12-8
	 */
	public enum OrderState {
		INIT(0, "未支付"),SUCCESS(1, "已支付"), BACKOUT(2, "已冲正"), REPEAL(3, "已撤销"),RETURN(4,"已退款"), OTHER(5, "失败");

		private final int value;
		private final String message;

		OrderState(int value, String message) {
			this.value = value;
			this.message = message;
		}

		public int getValue() {
			return value;
		}

		public String getMessage() {
			return message;
		}
		
		public static OrderState convert(String state){
			if(state==null || "".equals(state))
				return OrderState.OTHER;
			
			int status = Integer.parseInt(state);
			for(OrderState e : OrderState.values()){
				if(e.value == status)
					return e;
			}
			return OrderState.OTHER;
		}
		
		public static OrderState  convert(int state){
			for(OrderState e : OrderState.values()){
				if(e.value == state)
					return e;
			}
			return OrderState.OTHER;
		}
	}
	
	public void setCount(long count) {
		this.count = count;
	}

	public long getCount() {
		return count;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public List<Order> getOrders() {
		return orders;
	}
}
