package com.yifengcom.yfpos.model.ack;

import com.yifengcom.yfpos.codec.DevicePackage;

/**
 * 设备版本信息
 * 
 * @author qc
 * 
 */
public class DeviceVersion extends AckPackage {

	private static final long serialVersionUID = -7249768000786065246L;

	private String authentication; // 安全认证编号
	private String icp; // 银联入网许可号
	private String model;// 设备型号
	private String hardwareVersion; //硬件版本号
	private String firmwareVersion; //固件版本号
	private String versionSource; // 终端应用版本来源码
	private String version;
	private String sn;  //序列号
	private byte[] moduleState = new byte[8]; //硬件模块状态

	@Override
	public void decode(DevicePackage ack) {
		byte[] body = ack.getBody();
		this.authentication = new String(body,0,6);
		this.icp = new String(body,6,5);
		this.model = new String(body,11,8);
		this.hardwareVersion = new String(body,19,10);
		this.firmwareVersion = new String(body,29,10);
		this.versionSource = new String(body,39,2);
		this.version = new String(body,41,6);
		this.sn = new String(body,47,24);
		System.arraycopy(body, 71, this.moduleState, 0, 8);
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

	public String getIcp() {
		return icp;
	}

	public void setIcp(String icp) {
		this.icp = icp;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public String getVersionSource() {
		return versionSource;
	}

	public void setVersionSource(String versionSource) {
		this.versionSource = versionSource;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public byte[] getModuleState() {
		return moduleState;
	}

	public void setModuleState(byte[] moduleState) {
		this.moduleState = moduleState;
	}
	
	public String toString() {
		return "{model:"+this.model+",hardwareVersion:"+this.hardwareVersion+",firmwareVersion:"+this.firmwareVersion+","
				+ "version:"+this.version+",sn:"+this.sn+"}";
	}
}
