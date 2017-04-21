package com.ctbri.pos;

import com.yfcomm.pos.tlv.TLVCollection;

import android.os.Parcelable;

public abstract class POSTransRequest implements Parcelable  {
	
	/**
	 * 获取编码数据
	 * @return
	 */
	public abstract void encode(TLVCollection tlvs);
	
	
	public abstract String getMessageType();
}
