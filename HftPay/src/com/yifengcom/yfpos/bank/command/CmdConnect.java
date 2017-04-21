package com.yifengcom.yfpos.bank.command;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;

public class CmdConnect extends EposCommand{
		
		public CmdConnect(){
			command = EposProtocol.CMD2_CONNECT;
		}

		@Override
		public byte[] getFieldData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int decodeFromBuffer(IoBuffer in) {
			// TODO Auto-generated method stub
			return 0;
		}
}
