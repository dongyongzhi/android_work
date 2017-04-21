package com.yifengcom.yfpos.bank;

import android.os.Handler;


public interface SocketInterface {
	public void sendData(EposClientPacket out, Handler handler);
}
