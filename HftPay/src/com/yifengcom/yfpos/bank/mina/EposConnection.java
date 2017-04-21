package com.yifengcom.yfpos.bank.mina;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import net.mfs.util.StringUtil;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.yifengcom.yfpos.bank.EposDecoder;
import com.yifengcom.yfpos.bank.EposEncoder;
import com.yifengcom.yfpos.bank.EposClientPacket;

import android.content.Context;
import android.widget.Toast;

public class EposConnection {
	private static EposConnection connection=null;

	private IoSession session;
	private NioSocketConnector connector;

	public static EposConnection getconnection(Context context) {
		if (connection == null) {
			connection = new EposConnection();
		}
		return connection;
	}

	public IoSession getSession() {
		return session;
	}

	public EposConnection() {
		try{
			startConn();
		}
		catch(Exception e){
			
		}
	}

// private static int PORT = 8989;
//	 private static String CONNIP="192.168.4.26";
//	 private static int PORT = 8989;
	private static int PORT = 4402;
	
	
//	private static String CONNIP = "222.189.207.150";
	private static String CONNIP = "192.168.1.108";

	
	
	
	public void startConn() {

		connector = new NioSocketConnector();

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();


		chain.addLast("logger", new LoggingFilter());


		EposCodecFactory factory = new EposCodecFactory(new EposDecoder(), 	new EposEncoder());
		chain.addLast("myChain", new ProtocolCodecFilter(factory));


		connector.setHandler(new EposClientHandler(null));

		connector.setConnectTimeoutMillis(10000);

		InetSocketAddress inetSocketAddress = new InetSocketAddress(CONNIP,	PORT);
//		InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.4.204",	PORT);
		ConnectFuture cf = connector.connect(inetSocketAddress);

		cf.awaitUninterruptibly();
		session = cf.getSession();
	}

	public void sendData(EposClientPacket out){
		if (session == null){
			startConn();
		}
		
		session.write(out);
	}

}
