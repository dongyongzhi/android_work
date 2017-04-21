package com.yifengcom.yfpos;

import java.io.Serializable;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import com.yifengcom.yfpos.adapter.DeviceSessionFactory;
import com.yifengcom.yfpos.codec.DeviceDecoder;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.DevicePackage.PackageType;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.exception.MPOSException;
import com.yifengcom.yfpos.listener.ConnectionStateListener;
import com.yifengcom.yfpos.listener.ExecuteListener;
import com.yifengcom.yfpos.serialport.SerialportSessionFactory;

public  class DefaultDeviceComm extends DeviceContext implements DeviceComm {

	private final static YFLog logger = YFLog.getLog(DefaultDeviceComm.class);
	
	//超时时间  请求与应答的最长间隔不超过500ms 否则为交互超时，需进行重发
	private int timeout = 2000; 
	private DeviceSession session; 
	private final DeviceDecoder decoder = new DeviceDecoder();
	private final Context context;
	private DeviceInfo deviceInfo;
	
	public DefaultDeviceComm(Context context) {
		super(context);
		this.context = context;
		this.session = SerialportSessionFactory.getSession(context, decoder);
	}

	@Override
	public void connect(DeviceInfo deviceInfo,long timeout, ConnectionStateListener listener) {
		this.deviceInfo = deviceInfo;
		this.session = DeviceSessionFactory.getSession(context, deviceInfo, decoder);
		this.session.connect(deviceInfo,timeout, listener);
	}
	
	@Override
	public DevicePackage recv(Serializable sequence) {
		return this.recv(sequence, this.timeout);
	}
	
	@Override
	public DevicePackage execute(DevicePackage cmd) {
		return this.execute(cmd,this.timeout);
	}

	@Override
	public synchronized DevicePackage execute(DevicePackage cmd, int timeout) {
		//写入数据
		this.write(cmd);
		//应答数据
		return recv(cmd.getPackSequence(),timeout);
	}

	@Override
	public void execute(final DevicePackage cmd,final ExecuteListener listener) {
		//写入数据
		try {
			//异步执行命令并回调
			(new AsyncTask<DefaultDeviceComm,Void,Object>(){
				@Override
				protected Object doInBackground(DefaultDeviceComm... params) {
					DefaultDeviceComm comm = params[0];
					try{
						//接收数据包
						write(cmd);
						return comm.recv(cmd.getPackSequence(),comm.getTimeout());
						
					} catch(MPOSException ex) {
						return ex;
					} catch(Exception ex) {
						return new MPOSException(ex);
					}
				}
				
				protected void onPostExecute(Object obj) {
					if(obj instanceof MPOSException) {
						MPOSException ex = (MPOSException)obj;
						listener.onError(ex.getErrorCode(), ex.getMessage());
					} else if(obj instanceof DevicePackage) {
						listener.onRecv((DevicePackage)obj);
					}
				}
				
			}).execute(this);
			
		} catch(MPOSException ex) {
			listener.onError(ex.getErrorCode(), ex.getMessage());
		} catch(Exception ex) {
			logger.e(ex.getMessage(),ex);
			listener.onError(ErrorCode.UNKNOWN.getCode(), ex.getMessage());
		}
	}
	
	@Override
	public void write(DevicePackage pack) {
		if(session==null || !session.write(pack.getPackData())) {
			throw new MPOSException(ErrorCode.DEVICE_CLOSE,this);
		}
		
	}

	@Override
	public DevicePackage recv(Serializable sequence, int timeout) {
		DevicePackage ack = decoder.waitDecodeComplete(sequence, timeout * 1L);
		
		if(ack.getPackType() ==  PackageType.ACK_ERROR.getType()) {
			//应答错误信息
			ErrorCode errorCode = ErrorCode.convert(ack.getBody()[0]);
			throw new MPOSException(errorCode,this);
			
		} else {
			return ack;
		}
	}

	@Override
	public boolean connected() {
		return session==null ? false : session.connected();
	}

	@Override
	public void close() {
		if(session!=null) {
			session.close();
		}
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	@Override
	public DeviceInfo getDeviceInfo() {
		return this.deviceInfo;
	}
 

	@Override
	public void cancel() {
		this.write(PackageBuilder.syn(PackageBuilder.CMD_CANCEL));
		this.decoder.cancel();
	}

	public void setHander(Handler handler) {
		decoder.handler = handler;
	}
	
	public void setAppHander(Handler handler) {
		decoder.appHandler = handler;
	}
}
