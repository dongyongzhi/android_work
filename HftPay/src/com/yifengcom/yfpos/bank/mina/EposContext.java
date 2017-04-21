package com.yifengcom.yfpos.bank.mina;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

import com.yifengcom.yfpos.bank.EposCommContext;

//EPOS上下文类
public class EposContext {
	private static final AttributeKey CONTEXT = new AttributeKey(EposContext.class, "context");
	
	private EposCommContext commContext = new EposCommContext();
	private EposBusiContext busiContext = new EposBusiContext();
	
	public EposCommContext getCommContext() {
		return commContext;
	}
	
	public void setCommContext(EposCommContext commContext) {
		this.commContext = commContext;
	}
	
	public EposBusiContext getBusiContext() {
		return busiContext;
	}
	
	public void setBusiContext(EposBusiContext busiContext) {
		this.busiContext = busiContext;
	}
	
	/*
	 * @author: mcb
	 * @date: 2011/05/04
	 * @comment:获取上下文
	 */
	public static EposContext getContext(IoSession session){
		if (session == null) return null;
		EposContext ctx = (EposContext)session.getAttribute(CONTEXT);
		if (ctx == null){
			ctx = new EposContext();
			session.setAttribute(CONTEXT, ctx);
		}
		
		return ctx;
	}
}
