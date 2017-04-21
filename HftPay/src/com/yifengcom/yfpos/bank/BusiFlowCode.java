package com.yifengcom.yfpos.bank;

import java.nio.ByteBuffer;

public class BusiFlowCode {
	private byte command;
	public byte hintIndex;	
	public byte cmdType = 0;
	

	public BusiFlowCode(byte cmd, byte hint, byte typ){
		command = cmd;
		hintIndex = hint;
		cmdType = typ;
	}
	
	public byte[] getCodeHex(){
		ByteBuffer bb = ByteBuffer.allocate(10);
		
		byte tmp = (byte) (command |(cmdType << 6));
		bb.put(tmp);
		if (0 == cmdType){
			bb.put(hintIndex);
		}else if (3 == cmdType){
			bb.put((byte) 0x00);
		}
		
		return bb.array();
		
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	public byte getCommand() {
		return command;
	}
}
