package com.ctbri.pos.support;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctbri.net.MessageType;
import com.ctbri.pos.POSTransRequest;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.tlv.support.TLVString;

/**
 * 消费撤销
 * @author qc
 *
 */
public class POSRevokeRequest extends POSTransRequest {

	private final String messageType = MessageType.CANCEL_PURCHASE;
	//主管密码 6位
	private String managerPwd;
	//原流水号 /凭证号
	private String originalSerialNumber;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(managerPwd);
		dest.writeString(originalSerialNumber);
	}

	@Override
	public void encode(TLVCollection tlvs) {
		tlvs.add(new TLVString(0x9F2F,this.managerPwd));
		//凭证号 9F0C   即 pos流水9F29
		tlvs.add(new TLVString(0x9F29,this.originalSerialNumber));
	}
	
	public static final Parcelable.Creator<POSRevokeRequest> CREATOR = new Parcelable.Creator<POSRevokeRequest>() {

		@Override
		public POSRevokeRequest createFromParcel(Parcel source) {
			POSRevokeRequest req = new POSRevokeRequest();
			req.managerPwd = source.readString();
			req.originalSerialNumber = source.readString();
			return req;
		}

		@Override
		public POSRevokeRequest[] newArray(int arg0) {
			return new POSRevokeRequest[arg0];
		}
	};
	
	public static Parcelable.Creator<POSRevokeRequest> getCreator() {
		return CREATOR;
	}

	public String getManagerPwd() {
		return managerPwd;
	}

	public void setManagerPwd(String managerPwd) {
		this.managerPwd = managerPwd;
	}

	public String getOriginalSerialNumber() {
		return originalSerialNumber;
	}

	public void setOriginalSerialNumber(String originalSerialNumber) {
		this.originalSerialNumber = originalSerialNumber;
	}

	@Override
	public String getMessageType() {
		return messageType;
	}
}
