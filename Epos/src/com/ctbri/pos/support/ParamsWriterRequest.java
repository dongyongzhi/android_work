package com.ctbri.pos.support;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Parcel;
import com.ctbri.net.MessageType;
import com.ctbri.pos.POSTransRequest;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.tlv.support.TLVString;

public class ParamsWriterRequest extends POSTransRequest {

	@Override
	public int describeContents() {
		return 0;
	}
	
	private Date time;

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {

	}

	@Override
	public void encode(TLVCollection tlvs) {
		time = time == null ? new Date() : time;
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		tlvs.add(new TLVString(0x9F0D,df.format(time)));
	}

	@Override
	public String getMessageType() {
		return MessageType.UPDATEPARAMETER;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
