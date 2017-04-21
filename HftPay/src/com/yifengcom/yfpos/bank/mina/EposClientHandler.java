package com.yifengcom.yfpos.bank.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class EposClientHandler extends IoHandlerAdapter{
	private Handler handler=null;
	public EposClientHandler(Handler handler){
		//if (handler == null)
		//	throw new Exception();
		this.handler = handler;
	}
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		Log.i(this.getClass().toString(), "messageReceived");;
		super.messageReceived(session, message);
		
		
		Message msg= Message.obtain();
		msg.obj = message;
		msg.what = EposMessageType.MESSAGE_RECEIVED;
		handler.sendMessage(msg);
		session.close(true);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Log.i(this.getClass().toString(), "sessionClosed");
		Message msg= Message.obtain();
		msg.what = EposMessageType.SESSION_CLOSED;
		handler.sendMessage(msg);
		super.sessionClosed(session);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		Log.i(this.getClass().toString(), "sessionOpened");
		Message msg= Message.obtain();
		msg.what = EposMessageType.SESSION_OPENED;
		handler.sendMessage(msg);
		super.sessionOpened(session);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
	    session.close(true);
		Message msg= Message.obtain();
		msg.what = EposMessageType.EXCEPTION_CAUGHT;
		handler.sendMessage(msg);
		Log.e("ClientSessionHandler", cause.getMessage());
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		Log.i(this.getClass().toString(), "messageSent");
		super.messageSent(session, message);
		Message msg= Message.obtain();
		msg.what = EposMessageType.MESSAGE_SENT;
		handler.sendMessage(msg);
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		Log.i(this.getClass().toString(), "sessionCreated");
		super.sessionCreated(session);
		Message msg= Message.obtain();
		msg.what = EposMessageType.SESSION_CREATED;
		handler.sendMessage(msg);
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		Log.i(this.getClass().toString(), "sessionIdle");
		super.sessionIdle(session, status);
		Message msg= Message.obtain();
		msg.what = EposMessageType.SESSION_IDLE;
		handler.sendMessage(msg);
	}
}
