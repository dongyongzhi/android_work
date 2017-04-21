package com.ctbri.pos.support;

import android.os.Parcel;
import android.os.Parcelable;

import com.ctbri.net.MessageType;
import com.ctbri.pos.POSTransRequest;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.tlv.support.TLVString;
import com.yfcomm.pos.utils.StringUtils;

/**
 * 退货请求
 * @author qc
 *
 */
public class POSPurchaseRefundRequest extends POSTransRequest  {

	private final String messageType = MessageType.RETURNS;
	private String managerPwd;
	private String originalSerialNumber;  //原流水号 /凭证号
	private String originalBatchNumber;   //原批次号    (原交易日期)
	private String referenceNumber;       //原参考号
	private String originalTransDate;//原交易日期  (MMDD) 格式 
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(managerPwd);
		dest.writeString(originalSerialNumber);
		dest.writeString(originalBatchNumber);
		dest.writeString(referenceNumber);
		dest.writeString(originalTransDate);
	}
	
	public static final Parcelable.Creator<POSPurchaseRefundRequest> CREATOR = new Parcelable.Creator<POSPurchaseRefundRequest>() {

		@Override
		public POSPurchaseRefundRequest createFromParcel(Parcel source) {
			POSPurchaseRefundRequest req = new POSPurchaseRefundRequest();
			req.managerPwd = source.readString();
			req.originalSerialNumber = source.readString();
			req.originalBatchNumber = source.readString();
			req.referenceNumber = source.readString();
			req.originalTransDate = source.readString();
			return req;
		}

		@Override
		public POSPurchaseRefundRequest[] newArray(int arg0) {
			return new POSPurchaseRefundRequest[arg0];
		}
	};
	
	
	public static Parcelable.Creator<POSPurchaseRefundRequest> getCreator() {
		return CREATOR;
	}

	@Override
	public void encode(TLVCollection tlvs) {
		tlvs.add(new TLVString(0x9F2F,this.managerPwd));
		//参考号
		tlvs.add(new TLVString(0x9F0F,StringUtils.rightAddSpace(this.referenceNumber, 12)));
		//原批次号
		tlvs.add(new TLVString(0x9F0B,this.originalBatchNumber));
		//原流水号
		tlvs.add(new TLVString(0x9F29,this.originalSerialNumber));
		//原交易日期
		tlvs.add(new TLVString(0x9F0D,this.originalTransDate));
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

	public String getOriginalBatchNumber() {
		return originalBatchNumber;
	}

	public void setOriginalBatchNumber(String originalBatchNumber) {
		this.originalBatchNumber = originalBatchNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getOriginalTransDate() {
		return originalTransDate;
	}

	public void setOriginalTransDate(String originalTransDate) {
		this.originalTransDate = originalTransDate;
	}

	@Override
	public String getMessageType() {
		return messageType;
	}

}
