package com.yifengcom.yfpos.model.ack;

import java.io.Serializable;

import com.yifengcom.yfpos.codec.DevicePackage;

public abstract class AckPackage implements Serializable {
 
	private static final long serialVersionUID = -7161333791894864352L;
	
	
	public abstract void decode(DevicePackage ack);
	
}
