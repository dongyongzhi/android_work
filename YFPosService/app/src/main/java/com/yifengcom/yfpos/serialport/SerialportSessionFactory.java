package com.yifengcom.yfpos.serialport;

import android.content.Context;
import com.yifengcom.yfpos.DeviceSession;
import com.yifengcom.yfpos.codec.DeviceDecoder;

public class SerialportSessionFactory {
	private static DeviceSession session;
	
	public static DeviceSession getSession(Context context,DeviceDecoder decoder) {
		if(session == null) {
			session = newSession(context,decoder);
		}
		return session;
	}
	
	private static DeviceSession newSession(Context context,DeviceDecoder decoder) {
		return new SerialportSession(decoder);
	}
}
