package com.yfcomm.mpos.codec;

import java.io.Serializable;

import com.yfcomm.businesshall.ByteUtils;


public class DevicePackage {

	/** pack STX **/
	public final static int STX = 0x55AA;
	/** pack ETX **/
	public final static int ETX = 0xCC33;
	/** 请求 **/
	public final static byte REQUEST = 0x51;
	/** 成功应答 **/
	public final static byte RESPONSE_SUCCESS = 0x41;
	/** 错误应答 **/
	public final static byte RESPONSE_ERROR = 0x45;

	private final byte packType; // 包类型

	private final byte index; // 包序号 取值为1~255，每次交互后增加1，超过255后重新从1开始计数

	private final int length; // 包长度 表示指令和参数的长度总和，低字节优先传输

	private final int cmd; // 指令

	public final byte[] body; // 内容

	public final byte lrc; // 校验和

	private final byte[] packData;// 包数据
	private final int bodyLenght ;
	

	public DevicePackage(byte packType, byte index, int cmd, byte[] body) {
		this.packType = packType;
		this.index = index;
		this.bodyLenght = body ==null ? 0 : body.length;
		this.length = bodyLenght + 2;
		this.cmd = cmd;
		this.body = body;

		// 组织数据包
		this.packData = new byte[11 + bodyLenght];
		this.packData[0] = (byte) (STX >> 8);
		this.packData[1] = (byte) STX;
		this.packData[2] = packType;
		this.packData[3] = index;
		// 包长度
		System.arraycopy(ByteUtils.unsignedShortLE(this.length), 0,
				this.packData, 4, 2);
		// 指令
		this.packData[6] = (byte) (cmd >> 8);
		this.packData[7] = (byte) cmd;
		
		if(this.body!=null) {
			// 内容
			System.arraycopy(this.body, 0, this.packData, 8, bodyLenght);
		}

		// lrc
		this.lrc = ByteUtils.genLRC(this.packData, 2, 6 + this.bodyLenght);
		this.packData[6 + this.length] = this.lrc;

		// 终止符
		this.packData[9 + this.bodyLenght] = (byte) (ETX >> 8);
		this.packData[10 + this.bodyLenght] = (byte) ETX;
	}

	public DevicePackage(byte[] packData) {
		this.packData = packData;

		this.packType = packData[2];
		this.index = packData[3];
		this.length = ByteUtils.byteToIntLE(packData[4], packData[5]);
		this.cmd = ByteUtils.byteToInt(packData[6], packData[7]);
		this.bodyLenght = length - 2;
		this.body = new byte[bodyLenght];
		System.arraycopy(packData, 8, body, 0, body.length);

		this.lrc = packData[6 + length];
	}

	/**
	 * 获取校验和
	 * 
	 * @return
	 */
	public byte getLRC() {
		return this.lrc;
	}

	/**
	 * 获取包数据
	 * 
	 * @return
	 */
	public byte[] getPackData() {
		return this.packData;
	}

	public int getLength() {
		return length;
	}

	public byte getPackType() {
		return packType;
	}

	public byte getIndex() {
		return index;
	}

	public int getCmd() {
		return cmd;
	}

	public byte[] getBody() {
		return body;
	}

	/**
	 * 是否应答成功
	 * 
	 * @return
	 */
	public boolean isResponseSuccess() {
		return this.packType == RESPONSE_SUCCESS;
	}

	public Serializable getPackSequence() {
		return new DevicePackageSequence(this.cmd,this.index);
	}
	/**
	 * 包唯一索引
	 * @author qc
	 *
	 */
	public static class DevicePackageSequence implements Serializable {
		private static final long serialVersionUID = -5245325414792668136L;

		private final Integer cmd;
		
		private final Byte index;
		
		public DevicePackageSequence(Integer cmd,Byte index) {
			this.cmd = cmd;
			this.index = index;
		}

		public Integer getCmd() {
			return cmd;
		}

		public Byte getIndex() {
			return index;
		}
		
		public int hashCode() {
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + cmd.hashCode();
			result = PRIME * result + index.hashCode();
			return result;
		}

		public boolean equals(Object obj) {
			if (obj instanceof DevicePackageSequence) {
				DevicePackageSequence target = (DevicePackageSequence) obj;
				if (target == this) {
					return true;
				} else {
					return target.getCmd().equals(this.cmd) && index.equals(target.getIndex());
				}
			} else {
				return false;
			}
		}
		
		public String toString() {
			byte[] bcmd = ByteUtils.unsignedShort(cmd);
			return "cmd:"+ByteUtils.printBytes(bcmd) +" index:"+(index & 0xFF);
		}
	}
	
	/**
	 * 包类型
	 * @author qc
	 *
	 */
	public enum PackageType {
		/**
		 * 请求包
		 */
		SYN((byte)0x51),
		
		/**
		 * 应答正解包
		 */
		ACK_SUCC((byte)0x41),
		
		/**
		 * 应答错误包
		 */
		ACK_ERROR((byte)0x45);
		
		private final byte type;
		
		PackageType(byte type) {
			this.type = type;
		}

		public byte getType() {
			return type;
		}
	}
}
