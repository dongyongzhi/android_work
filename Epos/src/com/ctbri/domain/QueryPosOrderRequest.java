package com.ctbri.domain;

import org.json.JSONException;
import org.json.JSONObject;

import com.ctbri.utils.ElecLog;

public class QueryPosOrderRequest extends ElecRequest {

	/**  */
	private static final long serialVersionUID = 1397787224417325750L;

	private String operator; // 操作员
	private String poscati; // 终端号
	private String orderCode; // 订单号
	private String externalId; //参考号


	private String startTime; // yyyymmdd
	private String endTime; // yyyymmdd
	private int currentpage = 1;//当前页  
	private int pagenum = 20;//每页条数

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPoscati() {
		return poscati;
	}

	public void setPoscati(String poscati) {
		this.poscati = poscati;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}
	
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	@Override
	public String getPayData() {
		JSONObject json = new JSONObject();
		try {
			json.put("operator", operator);
			json.put("poscati", poscati);
			json.put("ordercode", orderCode);
			json.put("externalid", externalId);
			json.put("starttime", startTime);
			json.put("endtime", endTime);
			json.put("currentpage", String.valueOf(currentpage));
			json.put("pagenum",String.valueOf(pagenum));
			
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(),e);
		}
		return json.toString();
	}
}
