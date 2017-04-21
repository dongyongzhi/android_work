package com.ctbri.domain;

/**
 * 生成电子签名返回
 * 
 * @author qin
 * 
 *         2012-12-8
 */
public class ElectronicSignResponse extends ElecResponse {

	private static final long serialVersionUID = 2529504909843255961L;
	
	private String validateurl;
	private String pageUrl; //手机访问的订单详细页面url

	public void setValidateurl(String validateurl) {
		this.validateurl = validateurl;
	}
	public String getValidateurl() {
		return validateurl;
	}
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	public String getPageUrl() {
		return pageUrl;
	}
}
