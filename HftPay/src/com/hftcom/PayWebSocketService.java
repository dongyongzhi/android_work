package com.hftcom;

import java.io.IOException;
import org.json.JSONObject;
import elonen.NanoWSD;
import elonen.NanoHTTPD.IHTTPSession;
import elonen.NanoWSD.WebSocket;
import elonen.NanoWSD.WebSocketFrame;
import elonen.NanoWSD.WebSocketFrame.CloseCode;
import elonen.NanoWSD.WebSocketFrame.OpCode;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;

public class PayWebSocketService extends Service {
	private static int PORT = 8080;
	private PayWebSocketServer ws = null;

	BroadcastReceiver payResultReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			JSONObject obj = new JSONObject();
			try {
				obj.put("result", intent.getStringExtra("result"));
				obj.put("busidate", System.currentTimeMillis());

				if ((ws != null) && ws.isAlive() && (ws.getCurSocket() != null)) {
					WebSocketFrame frame = new WebSocketFrame(OpCode.Text,
							true, obj.toString());
					ws.getCurSocket().sendFrame(frame);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	@Override
	public void onCreate() {
		super.onCreate();
		registerReceiver(payResultReceiver, new IntentFilter(
				"com.hftcom.html5.PayResult"));
		startServer();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(payResultReceiver);
		stopServer();
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public void startServer() {
		ws = new PayWebSocketServer(PORT);
		try {
			ws.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void stopServer() {
		if (ws != null)
			ws.stop();
		System.out.println("Server stopped.\n");
	}

	public class WebSocketBinder extends Binder {
		public PayWebSocketService getService() {
			return PayWebSocketService.this;
		}
	}

	private class PayWebSocketServer extends NanoWSD {
		private PayWebSocket curSocket = null;

		public PayWebSocketServer(int port) {
			super(port);
		}

		@Override
		protected WebSocket openWebSocket(IHTTPSession handshake) {
			curSocket = new PayWebSocket(this, handshake);
			return curSocket;
		}

		public PayWebSocket getCurSocket() {
			return curSocket;
		}

		public void setCurSocket(PayWebSocket curSocket) {
			this.curSocket = curSocket;
		}

	}

	private class PayWebSocket extends WebSocket {

		private final NanoWSD server;

		public PayWebSocket(NanoWSD server, IHTTPSession handshakeRequest) {
			super(handshakeRequest);
			this.server = server;
		}

		@Override
		protected void onOpen() {
		}

		@Override
		public void onClose(CloseCode code, String reason,
				boolean initiatedByRemote) {
			System.out
					.println("C ["
							+ (initiatedByRemote ? "Remote" : "Self")
							+ "] "
							+ (code != null ? code : "UnknownCloseCode[" + code
									+ "]")
							+ (reason != null && !reason.isEmpty() ? ": "
									+ reason : ""));
		}

		@Override
		protected void onMessage(WebSocketFrame message) {
			JSONObject obj;
			try {
				obj = new JSONObject(message.getTextPayload());
				String amount = obj.getString("payMoney");

				Intent intent = new Intent("com.hftcom.html5.pay");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("tradeType","付款"); //交易类型
				intent.putExtra("tradeSerialNum","000001");//流水号
				intent.putExtra("companyName","惠民通测试商户"); //商户名称
				intent.putExtra("companyType","超市");//商户类型
				intent.putExtra("orderCode","201606220003");//支付订单号
				intent.putExtra("orderNum","201606220005");//商户订单号
				intent.putExtra("orderExplain","HTML5");//订单说明
				intent.putExtra("phoneIMSINum","18061169555");//手持设备号
				intent.putExtra("operatorNum","10001");//操作工号
				intent.putExtra("payMoney",amount);//金额
				intent.putExtra("extensionField","");//扩展域
				intent.putExtra("checkDigit","");//校验域
				startActivity(intent);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		@Override
		protected void onPong(WebSocketFrame pong) {
			System.out.println("P " + pong);
		}

		@Override
		protected void onException(IOException exception) {
			// DebugWebSocketServer.LOG.log(Level.SEVERE, "exception occured",
			// exception);
		}

		@Override
		protected void debugFrameReceived(WebSocketFrame frame) {
			System.out.println("R " + frame);
		}

		@Override
		protected void debugFrameSent(WebSocketFrame frame) {
			System.out.println("S " + frame);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
