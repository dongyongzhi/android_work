 package com.hftcom;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import com.hftcom.db.DBUtil;
import com.hftcom.utils.Config;
import com.hftcom.utils.FileUtils;
import com.hftcom.utils.SharedUtils;
import com.yifengcom.yfpos.bank.EposClientPacket;
import com.yifengcom.yfpos.bank.EposDecoder;
import com.yifengcom.yfpos.bank.EposEncoder;
import com.yifengcom.yfpos.bank.mina.EposClientHandler;
import com.yifengcom.yfpos.bank.mina.EposCodecFactory;
import com.yifengcom.yfpos.bank.mina.EposMessageType;
import com.yifengcom.yfpos.service.IService;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class YFApp extends Application {
	public IService iService;
	public Myconn myconn;
	public boolean isBind = false;
	private static YFApp instance;
	private IoSession session = null;
	private NioSocketConnector connector = null;
	private static final int PORT = 9901;
	private static final String CONNIP = "218.83.152.229";
	private static final YFLog logger = YFLog.getLog(YFApp.class);
	private FileUtils fileUtils;
	
	BroadcastReceiver destroyReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			SharedUtils.setBoolean(getApplicationContext(), SharedUtils.SERVER_DISTROY, true);
			String path = Environment.getExternalStorageDirectory() + "/" + Config.appName;
			File file = new File(path);
			DeleteFile(file);
			stopService(new Intent(getApplicationContext(),PayWebSocketService.class));
			unregisterReceiver(destroyReceiver);
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
	};
	
	public void DeleteFile(File file) {  
        if (file.exists()) {  
            if (file.isFile()) {  
                file.delete();  
                return;  
            }  
            if (file.isDirectory()) {  
                File[] childFile = file.listFiles();  
                if (childFile == null || childFile.length == 0) {  
                    file.delete();  
                    return;  
                }  
                for (File f : childFile) {  
                    DeleteFile(f);  
                }  
                file.delete();  
            }  
        }  
    }  

	@Override
	public void onCreate() {
		super.onCreate();
		if(SharedUtils.getBoolean(getApplicationContext(), SharedUtils.SERVER_DISTROY)){
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
		}
		instance = this;
		registerReceiver(destroyReceiver, new IntentFilter("com.yifengcom.yfpos.destroy"));
		DBUtil.getInstance(this);
		fileUtils = new FileUtils();
		bind();
		checkSeqFile();
		startService(new Intent(getApplicationContext(),
				PayWebSocketService.class));
		startService(new Intent(getApplicationContext(), PsamService.class));
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		unBind();
	}

	private void checkSeqFile() {
		if (fileUtils.checkSDCard()) {
			String fileUrl = fileUtils.getSDPATH() + Config.appName + "/"
					+ Config.sName;
			File file = new File(fileUrl);
			if (!file.exists()) {
				writeSDCard(Config.seq_text, Config.sName);
			}
		} else {
			logger.e("SDCard不可用");
		}
	}

	public static YFApp getApp() {
		return instance;
	}

	private class Myconn implements ServiceConnection {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iService = IService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			iService = null;
		}

	}

	public void bind() {
		Intent intent = new Intent();
		intent.setAction("com.yifeng.aidl");
		intent.setPackage("com.yifengcom.yfpos");
		myconn = new Myconn();
		isBind = bindService(intent, myconn, BIND_AUTO_CREATE);
		if (isBind) {
			logger.d("绑定成功");
		} else {
			logger.e("绑定失败");
		}
	}

	public void unBind() {
		if (myconn != null) {
			try {
				unbindService(myconn);
				isBind = false;
				myconn = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class SocketSendRunnable implements Runnable {
		private EposClientPacket data = null;
		private Handler handler = null;
		private Handler errorHandler = null;

		public void setData(EposClientPacket data) {
			this.data = data;
		}

		public void setHandler(Handler handler,Handler errorHandler) {
			this.handler = handler;
			this.errorHandler = errorHandler;
		}

		@Override
		public void run() {
			
			if (session == null) {
				startConnection();
			} else {
				if (!session.isConnected()) {
					startConnection();
				}
			}

			if (session != null && session.isConnected()) {
				session.write(data);
			} else {
				this.errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,
						"服务器连接失败").sendToTarget();
			}
		}

		private void startConnection() {
			try {
				connector = new NioSocketConnector();
				DefaultIoFilterChainBuilder chain = connector.getFilterChain();
				chain.addLast("logger", new LoggingFilter());
				EposCodecFactory factory = new EposCodecFactory(
						new EposDecoder(), new EposEncoder());
				chain.addLast("myChain", new ProtocolCodecFilter(factory));
				connector.setHandler(new EposClientHandler(handler));
				connector.setConnectTimeoutMillis(10000);
				InetSocketAddress inetSocketAddress = new InetSocketAddress(
						CONNIP, PORT);
				ConnectFuture cf = connector.connect(inetSocketAddress);
				cf.awaitUninterruptibly();
				session = cf.getSession();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public void sendData(EposClientPacket out, Handler handle,Handler errorHandler) {
		SocketSendRunnable runable = new SocketSendRunnable();
		runable.setData(out);
		runable.setHandler(handle,errorHandler);
		new Thread(runable).start();
	}

	public synchronized int getNextSequence(String fileName) {
		Map<String, String> map = loadSequence(fileName);
		int currentSequence = 0;
		int nextSequence = 0;
		if (map.get("currentSequence") != null) {
			
			currentSequence = Integer.parseInt(map.get("currentSequence"));
			nextSequence = currentSequence + 1;
			nextSequence = nextSequence > 999999 ? 0 : nextSequence;
			// writeSDCard("currentSequence="+nextSequence,fileName);
		} else {
			return -1;
		}
		return nextSequence;
	}

	public String getPsamNo(String fileName) {
		Map<String, String> map = loadSequence(fileName);
		String psam = "";
		if (map.get("psamNo") != null) {
			psam = map.get("psamNo");
		}
		return psam;
	}

	public Map<String, String> loadSequence(String fileName) {
		Map<String, String> map = new HashMap<String, String>();
		if (fileUtils.checkSDCard()) {
			String fileUrl = fileUtils.getSDPATH() + Config.appName + "/"
					+ fileName;
			try {
				File file = new File(fileUrl);
				if (file.exists()) {
					FileInputStream fileR = new FileInputStream(file);
					BufferedReader reads = new BufferedReader(
							new InputStreamReader(fileR));
					String st = null;
					String[] sInfo = new String[2];
					while ((st = reads.readLine()) != null) {
						if (!st.equalsIgnoreCase("")) {
							sInfo = st.split("=");
							if (sInfo.length == 2) {
								if (sInfo[1] == null || sInfo[1].length() <= 0) {
									Log.e("Sequence", sInfo[0] + "is null");
									break;
								}
								map.put(sInfo[0], sInfo[1]);
							}
						}
					}
					fileR.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			logger.e("SDCard不可用");
		}

		return map;
	}

	public synchronized void writeSDCard(String content, String fileName) {
		if (fileUtils.checkSDCard()) {
			InputStream in;
			try {
				in = new ByteArrayInputStream(content.getBytes("UTF-8"));
				fileUtils.write2SDFromInput(Config.appName, fileName, in);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			logger.e("SDCard不可用");
		}
	}
}
