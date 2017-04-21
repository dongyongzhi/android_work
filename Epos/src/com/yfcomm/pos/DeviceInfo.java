package com.yfcomm.pos;

import java.io.Serializable;

public class DeviceInfo implements Serializable {
 
	private static final long serialVersionUID = -2485419453096020730L;

	private String name;
	
	private String address;
	
	public DeviceInfo() {
		
	}
	
	public DeviceInfo(String name,String address) {
		this.name = name;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result
				+ ((address == null) ? 0 : address.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof DeviceInfo) {
			DeviceInfo target = (DeviceInfo)obj;
			if(target == this) {
				return true;
			} else  {
				return target.address.equals(this.address);
			}
			
		} else {
			return false;
		}
	}
	
}
