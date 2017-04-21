package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;

import org.apache.mina.core.buffer.IoBuffer;

public abstract class EposCommand {
	public enum CMD_TYPE {
		SINGLE(2, "单字节指令"),
		MUTI(0, "双字节指令"),
		TRIPLE(3, "三字节指令");
		private final int value;
		private final String message;

		CMD_TYPE(int value, String message) {
			this.value = value;
			this.message = message;
		}

		public int getValue() {
			return value;
		}

		public String getMessage() {
			return message;
		}
		
		public static CMD_TYPE convert(String state){
			if(state==null || "".equals(state))
				return CMD_TYPE.MUTI;
			
			int status = Integer.parseInt(state);
			for(CMD_TYPE e : CMD_TYPE.values()){
				if(e.value == status)
					return e;
			}
			return CMD_TYPE.MUTI;
		}
	
	};
	protected byte command;		
	private byte hintIndex;  //操作提示索引
	public CMD_TYPE cmdType = CMD_TYPE.MUTI;//2双字节       0单字节
	
	public EposCommand setHintIndex(int index){
		hintIndex=(byte)index;
		return this;
	}
	public EposCommand setCmdType(CMD_TYPE type){
		cmdType=  type;
		return this;
	}
	
	public abstract byte[]  getFieldData();
	public abstract int decodeFromBuffer(IoBuffer in);
	public byte[] getCodeHex(){
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		byte tmp = (byte) (command |((byte)cmdType.value << 6));
		os.write(tmp);
		
		if (0 == (byte)cmdType.value){
			os.write(hintIndex);
		}else if (3 == (byte)cmdType.value){
			os.write((byte) 0x00);
		}
		
		return os.toByteArray();
	}

	public byte getCommand() {
		return command;
	}
}
