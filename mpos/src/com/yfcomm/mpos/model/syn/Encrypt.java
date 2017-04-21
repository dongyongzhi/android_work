package com.yfcomm.mpos.model.syn;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.yfcomm.mpos.utils.StringUtils;

public class Encrypt extends SynPackage {
 
	private static final long serialVersionUID = 9063222575248605299L;
	
	private int keyIndex = 0x00;
	private int timeout=10; //单位秒
	
	private String title;   //标题
	private String context; //内容
	private byte[] contextData;

	@Override
	public byte[] encode() {
		ByteArrayOutputStream in = new ByteArrayOutputStream(255);
		in.write(keyIndex);
		in.write(timeout);
		
		try {
			if(StringUtils.isEmpty(title)) {
				in.write(0);
			} else  {
				byte[] titleBytes = this.title.getBytes(CHARSET);
				in.write(titleBytes.length);
				in.write(titleBytes);
			}
			
			if(this.contextData!=null) {
				in.write(this.contextData.length);
				in.write(this.contextData);
			} else if(!StringUtils.isEmpty(context)) {
				byte[] contextBytes = this.context.getBytes(CHARSET);
				in.write(contextBytes.length);
				in.write(contextBytes);
			} else {
				in.write(0);
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return in.toByteArray();
	}

	public int getKeyIndex() {
		return keyIndex;
	}

	public void setKeyIndex(int keyIndex) {
		this.keyIndex = keyIndex;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public byte[] getContextData() {
		return contextData;
	}

	public void setContextData(byte[] contextData) {
		this.contextData = contextData;
	}
	
}
