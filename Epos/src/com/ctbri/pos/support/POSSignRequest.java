package com.ctbri.pos.support;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctbri.net.MessageType;
import com.ctbri.pos.POSTransRequest;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.tlv.support.TLVString;

/**
 * 签到请求
 * @author qc
 *
 */
public class POSSignRequest extends POSTransRequest {

	private final  String messageType = MessageType.SIGN;
	private String operNo;
	private String operPwd;
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(operNo);
		dest.writeString(operPwd);
	}
	
	public static final Parcelable.Creator<POSSignRequest> CREATOR = new Parcelable.Creator<POSSignRequest>() {

		@Override
		public POSSignRequest createFromParcel(Parcel dest) {
			POSSignRequest req = new POSSignRequest();
			req.operNo = dest.readString();
			req.operPwd = dest.readString();
			return req;
		}

		@Override
		public POSSignRequest[] newArray(int arg0) {
			return new POSSignRequest[arg0];
		}
	};

	@Override
	public void encode(TLVCollection tlvs) {
    	//操作人号
    	tlvs.add(new TLVString(0x9F08,this.operNo));
    	//操作人密码
    	tlvs.add(new TLVString(0x9F2E,this.operPwd));
	}
	
	public static Parcelable.Creator<POSSignRequest> getCreator() {
		return CREATOR;
	}

	public String getOperNo() {
		return operNo;
	}

	public void setOperNo(String operNo) {
		this.operNo = operNo;
	}

	public String getOperPwd() {
		return operPwd;
	}

	public void setOperPwd(String operPwd) {
		this.operPwd = operPwd;
	}

	public String getMessageType() {
		return messageType;
	}
}
