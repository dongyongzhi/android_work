package com.ctbri.pos.support;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctbri.net.MessageType;
import com.ctbri.pos.POSTransRequest;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.tlv.support.TLVString;

public class POSQueryBalanceRequest extends POSTransRequest {

	private final  String messageType = MessageType.QUERYBALANCE;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		 
	}
	
	public static final Parcelable.Creator<POSQueryBalanceRequest> CREATOR = new Parcelable.Creator<POSQueryBalanceRequest>() {

		@Override
		public POSQueryBalanceRequest createFromParcel(Parcel arg0) {
			return new POSQueryBalanceRequest();
		}

		@Override
		public POSQueryBalanceRequest[] newArray(int arg0) {
			return new POSQueryBalanceRequest[arg0];
		}
	};

	public static Parcelable.Creator<POSQueryBalanceRequest> getCreator() {
		return CREATOR;
	}

	@Override
	public void encode(TLVCollection tlvs) {
	}

	@Override
	public String getMessageType() {
		return messageType;
	}

}
