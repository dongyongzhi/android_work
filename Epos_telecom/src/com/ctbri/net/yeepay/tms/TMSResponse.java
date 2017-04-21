package com.ctbri.net.yeepay.tms;

import com.ctbri.domain.ElecResponse;

public class TMSResponse extends ElecResponse {
	
	/**  */
	private static final long serialVersionUID = -916953192959545384L;
	
	private String softTaskId;
	private String softVersion;
	private String softLenght;
	private String paramTaskId;
	private String paramVersion;
	private String paramLength;

 
	public String getSoftTaskId() {
		return softTaskId;
	}

	public void setSoftTaskId(String softTaskId) {
		this.softTaskId = softTaskId;
	}

	public String getSoftVersion() {
		return softVersion;
	}

	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}

	public String getSoftLenght() {
		return softLenght;
	}

	public void setSoftLenght(String softLenght) {
		this.softLenght = softLenght;
	}

	public String getParamTaskId() {
		return paramTaskId;
	}

	public void setParamTaskId(String paramTaskId) {
		this.paramTaskId = paramTaskId;
	}

	public String getParamVersion() {
		return paramVersion;
	}

	public void setParamVersion(String paramVersion) {
		this.paramVersion = paramVersion;
	}

	public String getParamLength() {
		return paramLength;
	}

	public void setParamLength(String paramLength) {
		this.paramLength = paramLength;
	}
}
