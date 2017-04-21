package com.hftcom.ui;

import android.os.Handler;
import android.os.Message;
import com.yifengcom.yfpos.bank.EposClientPacket;
import com.yifengcom.yfpos.bank.mina.EposMessageType;

public class MyActivity extends BaseActivity {
	protected boolean isReturn = false;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case EposMessageType.MESSAGE_RECEIVED:
				isReturn = true;
				setDialogMessage();
				EposClientPacket packet = (EposClientPacket) msg.obj;
				if (packet.getBusiCode().equals("101")) {
					onPurchaseOk(packet);
				} else if (packet.getBusiCode().equals("004")) {
					onReverseOK(packet);
				} else if (packet.getBusiCode().equals("311")) {
					onPurchaseOk(packet);
				}
				break;
			case EposMessageType.MESSAGE_TIMER:
				onMessageTimer(msg);
				break;
			case EposMessageType.SESSION_CLOSED:
				onSessionClosed(msg);
				break;
			case EposMessageType.SESSION_OPENED:
				onSessionOpened(msg);
				break;
			case EposMessageType.EXCEPTION_CAUGHT:
				onExceptionCaught(msg);
				break;
			case EposMessageType.MESSAGE_SENT:
				onMessageSent(msg);
				break;
			case EposMessageType.SESSION_CREATED:
				onSessionCreated(msg);
				break;
			case EposMessageType.SESSION_IDLE:
				onSessionIDLE(msg);
				break;
			default:
				break;
			}
		}

	};

	protected void setDialogMessage() {

	}

	/**
	 * 消费返回
	 * 
	 * @param packet
	 */
	protected void onPurchaseOk(EposClientPacket packet) {

	}

	/**
	 * 冲正返回
	 * 
	 * @param packet
	 */
	protected void onReverseOK(EposClientPacket packet) {

	}

	public void onMessageTimer(Message msg) {

	}

	/**
	 * 连接断开
	 * 
	 * @param msg
	 */
	public void onSessionClosed(Message msg) {

	}

	/**
	 * 连接成功
	 * 
	 * @param msg
	 */
	public void onSessionOpened(Message msg) {
	}

	/**
	 * 连接异常，断开连接
	 * 
	 * @param msg
	 */
	public void onExceptionCaught(Message msg) {

	}

	/**
	 * 发送成功，等待接收
	 * 
	 * @param msg
	 */
	public void onMessageSent(Message msg) {

	}

	/**
	 * 连接建立
	 * 
	 * @param msg
	 */
	public void onSessionCreated(Message msg) {

	}

	/**
	 * 连接空闲
	 * 
	 * @param msg
	 */
	public void onSessionIDLE(Message msg) {

	}
}
