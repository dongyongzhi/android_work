package com.yifengcom.yfpos;

import java.io.Serializable;

public class DeviceInfo implements Serializable {

	private static final long serialVersionUID = -2485419453096020730L;

	/**音频设备**/
	public final static int DEVICECHANNEL_AUDIOJACK = 0;
	/**蓝牙设备**/
	public final static int DEVICECHANNEL_BLUETOOTH = 1;

	private int deviceChannel = DEVICECHANNEL_BLUETOOTH;

	private String name;

	private String address;

	public DeviceInfo() {

	}

	/**
	 * 默认蓝牙设备
	 * @param name
	 * @param address
	 */
	public DeviceInfo(String name, String address) {
		this(name, address, DeviceInfo.DEVICECHANNEL_BLUETOOTH);
	}

	public DeviceInfo(String name, String address, int deviceChannel) {
		this.name = name;
		this.address = address;
		this.deviceChannel = deviceChannel;
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

	public int getDeviceChannel() {
		return deviceChannel;
	}

	public void setDeviceChannel(int deviceChannel) {
		this.deviceChannel = deviceChannel;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (obj instanceof DeviceInfo) {
			DeviceInfo target = (DeviceInfo) obj;
			if (target == this) {
				return true;
			} else {
				return target.address.equals(this.address)
						&& deviceChannel == target.deviceChannel;
			}

		} else {
			return false;
		}
	}

}
