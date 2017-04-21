package com.yfcomm.mpos.model.ack;

import java.io.Serializable;

import com.yfcomm.mpos.codec.DevicePackage;

public abstract class AckPackage implements Serializable {
 
	private static final long serialVersionUID = -7161333791894864352L;
	
	
	public abstract void decode(DevicePackage ack);
	
}
