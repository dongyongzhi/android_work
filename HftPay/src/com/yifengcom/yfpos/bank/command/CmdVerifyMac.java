package com.yifengcom.yfpos.bank.command;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdVerifyMac extends EposCommand{
		private String mac = "0000000000000000";
		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}

		public CmdVerifyMac(){
			command = EposProtocol.CMD2_VERIFY_MAC;
		}

		@Override
		public byte[] getFieldData() {
			byte tmp[] = StringUtil.hexStringToByte(mac);
			return tmp;
		}

		@Override
		public int decodeFromBuffer(IoBuffer in) {
			byte tmp[] = new byte[8];
			in.get(tmp);
			setMac(StringUtil.bytesToHexString(tmp));
			return 0;
		}
}
