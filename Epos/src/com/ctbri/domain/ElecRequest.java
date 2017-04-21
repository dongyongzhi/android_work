package com.ctbri.domain;

import java.util.Map;

import com.ctbri.ElecObject;

public abstract class ElecRequest extends ElecObject {

	/**  */
	private static final long serialVersionUID = 8663647943818971686L;
	
	public abstract  String getPayData();
	 
	
	public Map<String,String> getParams(){
		return null;
	}
}
