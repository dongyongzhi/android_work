package com.yifeng.commonutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class MyArrayList  extends ArrayList<Map<String, Object> >{

	/**
	 * 
	 */
	private static final long serialVersionUID = 11111111111111L;
	
	@Override
	public    boolean add(Map<String, Object> object) {
		if(super.size()>200){
			super.remove(super.size()-1);
			super.add(object);
		}else
		super.add(object);
		return true;
	}

	@Override
	public    boolean addAll(Collection<? extends Map<String, Object>> collection) {
		if(super.size()>200){
			
			
			super.removeRange(super.size()-collection.size()-1, super.size()-1);
			
		super.addAll(collection);
		}else
			super.addAll(collection);
		return true;
	}

	
	
}
