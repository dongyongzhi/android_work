package com.yf.download;

import com.yf.define.PublicDefine;
//import com.yf.download.YFComm.OTAResult;
import android.util.YFComm.OTAResult;
import android.content.Context;
import android.os.Handler;
import android.util.YFComm;

public class DownLoadUrladd extends Thread {
	
	private final String param;
	private OTAResult retMsg;
	private Handler handler;
	private String errmsg;

	public DownLoadUrladd(String sn, Handler mHandler, Context context) {
		
		this.param = "op=4&sn=" + sn;
		this.handler = mHandler;
	}

	public void run() {
		
		retMsg=YFComm.resultFromOTA(param);
		if(retMsg.result == null || retMsg.result.isEmpty()){
			  if(retMsg.msg ==null || retMsg.msg.isEmpty()){
				  errmsg="YFComm无返回,请检查网络和SN号";
			  }else{
				  errmsg="YFComm返回:"+ retMsg.msg;
			  }
			  handler.obtainMessage(PublicDefine.MSG_GETURLADDREERR, errmsg).sendToTarget();
			
		}else{
			handler.obtainMessage(PublicDefine.MSG_GETURLADDRESS,retMsg.result).sendToTarget();
		}
	}
}
