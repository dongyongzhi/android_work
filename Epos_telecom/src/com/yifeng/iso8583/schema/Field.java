package com.yifeng.iso8583.schema;

import java.util.LinkedHashSet;
import java.util.Set;

import com.yifeng.iso8583.IsoType;

public class Field implements java.io.Serializable {
	/**  */
	private static final long serialVersionUID = -5691282731526552265L;
	
	private int index = 0;
	private IsoType type;
	private String name;
	private int length = 0;
	private int maxlen = 0;
	private Set<Field> fields = new LinkedHashSet<Field>(0);  //子项
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public IsoType getType() {
		return type;
	}
	public void setType(String type) {
		this.type = IsoType.valueOf(type);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getMaxlen() {
		return maxlen;
	}
	public void setMaxlen(int maxlen) {
		this.maxlen = maxlen;
	}
	public void setFields(Set<Field> fields) {
		this.fields = fields;
	}
	public Set<Field> getFields() {
		return fields;
	}
}
