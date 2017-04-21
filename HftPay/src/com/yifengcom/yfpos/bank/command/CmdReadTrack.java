package com.yifengcom.yfpos.bank.command;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdReadTrack  extends EposCommand{
	String trackData="";
	String trackMac="";
	public CmdReadTrack(){
		command = EposProtocol.CMD2_READ_TRACK;
	}
	@Override
	public byte[] getFieldData() {
		
		byte[] id = StringUtil.hexStringToByte(trackData+trackMac);
		return id;
	}

	@Override
	public int decodeFromBuffer(IoBuffer in) {
		int len = in.get()&0xFF;
		if (len > 4){
			byte[] id = new byte[len-4];
			in.get(id);	 
			byte[] mac = new byte[4];
			in.get(mac);
			trackData = StringUtil.bytesToHexString(id);
			trackMac = StringUtil.bytesToHexString(mac);
		}else {
			trackData = "";
		}
		return 0;
	}
	
	public String getTrackData() {
		return trackData;
	}
	
	public void setTrackData(String trackData) {
		this.trackData = trackData;
	}
	public String getTrackMac() {
		return trackMac;
	}
	public void setTrackMac(String trackMac) {
		this.trackMac = trackMac;
	}

}
