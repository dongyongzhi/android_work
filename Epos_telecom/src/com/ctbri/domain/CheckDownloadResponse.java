package com.ctbri.domain;

/**
 * 下载应答信息
 * 
 * @author qin
 * 
 *         2012-11-19
 */
public class CheckDownloadResponse extends ElecResponse {

	private static final long serialVersionUID = -4450266767968730306L;

	private String url; // 下载文件地址

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

}
