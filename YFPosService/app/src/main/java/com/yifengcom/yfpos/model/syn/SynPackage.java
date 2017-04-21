package com.yifengcom.yfpos.model.syn;

import java.io.Serializable;

public abstract class SynPackage implements Serializable {
 
	private static final long serialVersionUID = 4043712008094561422L;
	
	protected static final String CHARSET = "gb2312";
	
	protected static final byte SPACE = (byte)0x20;

	public abstract byte[] encode();
}
