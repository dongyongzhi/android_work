package com.yifengcom.yfpos.bank.command;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;


public class CmdBusiAffirm extends EposCommand{
		
		public CmdBusiAffirm(){
			command = EposProtocol.CMD2_BUSI_AFFIRM;
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
