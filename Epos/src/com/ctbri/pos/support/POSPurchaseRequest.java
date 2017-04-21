package com.ctbri.pos.support;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctbri.net.MessageType;
import com.ctbri.pos.POSTransRequest;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.tlv.support.TLVString;
import com.yfcomm.pos.utils.StringUtils;

/**
 * 消费请求
 * 
 * @author qc
 * 
 */
public class POSPurchaseRequest extends POSTransRequest {

	private final String messageType = MessageType.PURCHASE;
	// 交易金 额
	private long money;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(money);
	}

	@Override
	public void encode(TLVCollection tlvs) {
    	//交易金额
    	tlvs.add(new TLVString(0x9F10,StringUtils.leftAddZero(money,12)));
	}
	
	public static final Parcelable.Creator<POSPurchaseRequest> CREATOR = new Parcelable.Creator<POSPurchaseRequest>() {

		@Override
		public POSPurchaseRequest createFromParcel(Parcel dest) {
			POSPurchaseRequest req = new POSPurchaseRequest();
			req.money = dest.readLong();
			return req;
		}

		@Override
		public POSPurchaseRequest[] newArray(int arg0) {
			return new POSPurchaseRequest[arg0];
		}
	};
	
	public static Parcelable.Creator<POSPurchaseRequest> getCreator() {
		return CREATOR;
	}
	
	@Override
	public String getMessageType() {
		return messageType;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
		this.money = money;
	}

}
