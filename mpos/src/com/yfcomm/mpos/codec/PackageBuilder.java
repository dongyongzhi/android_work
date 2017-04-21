package com.yfcomm.mpos.codec;

import com.yfcomm.mpos.codec.DevicePackage;
import com.yfcomm.mpos.codec.DevicePackage.PackageType;

public class PackageBuilder {
	
	/**获取设备版本**/
	public static final int CMD_DEVICE_VERSION = 0x0101;
	/**缓存数据**/
	public static final int CMD_CACHE = 0x0102;
	/**清除缓存**/
	public static final int CMD_CLEAR_CACHE = 0x0103;
	/**设置数据**/
	public static final int CMD_SET_CACHE = 0x0104;
	/**读取数据**/
	public static final int CMD_READ_CACHE = 0x0105;
	/**复位操作**/
	public static final int CMD_RESET = 0x0107;
	/**读取当前时间**/
	public static final int CMD_GETTIME = 0x0108;
	/**设置当前时间**/
	public static final int CMD_SETTIME = 0x0109;
	/**打印数据**/
	public static final int CMD_PRINT = 0x010A;
	/**上报打印数据结果**/ 	
	public static final int CMD_PRINT_RESULT = 0x010B;
	/**升级固件/应用**/
	public static final int CMD_UPDATE = 0x010C;
	public static final int CMD_CANCEL = 0x010D;

	/**更新主密钥**/
	public static final int CMD_UPDATE_MAIN_KEY = 0x0201;
	/**更新工作密钥**/
	public static final int CMD_UPDATE_WORK_KEY = 0x0202;
	/**验证MAC**/
	public static final int CMD_VALID_MAC= 0x0204;
	/**计算mac**/
	public static final int CMD_CALCULATE_MAC = 0x0205;
	/**数据加密指令**/
	public static final int CMD_ENCRYPT = 0x0207;
	
	/*===========磁条卡类============*/
	/**上报读卡结果***/
	public static final int CMD_UPLOAD_CARD= 0x0302;
	/**开启读卡器（DUKPT）***/
	public static final int CMD_OPEN_SWIPER = 0x0304;
	/**读取磁道数据（DUKPT）**/
	public static final int CMD_READ_CARD = 0x0305;
	
	/*===========密码键盘类===========*/
	/**上报输密结果**/
	public static final int CMD_UPLOAD_PASSWORD = 0x0402;
	/**请求输密**/
	public static final int CMD_REQUEST_PASSWORD = 0x0404;
	/**读取密码密文**/
	public static final int CMD_READ_PASSWORD = 0x0405;
	
	
	/*============PBOC类============*/
	/***更新公钥**/
	public static final int CMD_PBOC_UPDATE_PUBLIC_KEY = 0x0501;
	/**更新AID**/
	public static final int CMD_PBOC_UPDATE_AID = 0x0502; 
	/**执行PBOC标准流程**/
	public static final int CMD_PBOC_START_PROCESS = 0x0503;
	/**上报执行PBOC标准流程结果**/
	public static final int CMD_PBOC_UPLOAD_RESULT = 0x0504;
	/**读取执行PBOC标准流程结果**/
	public static final int CMD_PBOC_READ = 0x0505;
	/**执行PBOC二次授权**/
	public static final int CMD_PBOC_TWO_AUTH = 0x0506;
	/**上报PBOC二次授权结果**/
	public static final int CMD_PBOC_UPLOAD_TWO_RESULT = 0x0507;
	/**读取PBOC二次授权结果**/
	public static final int CMD_PBOC_READ_TWO=0x0508;
	/**流程结束**/
	public static final int CMD_PBOC_END = 0x0509;
	
	/*============显示类============*/
	public static final int CMD_DISPLAY = 0x0701;
	
	//当前包序列
	private static byte currentPackageSequence = (byte)0x00;

	/**
	 * 获取一下个包序列
	 * @return
	 */
	public synchronized static byte getNextPackageSequence() {
		int nextSequence = (currentPackageSequence & 0xFF) + 1;
		nextSequence = nextSequence > 0xFF ? 1 : (byte)nextSequence;
		currentPackageSequence = (byte)nextSequence;
		return (byte)nextSequence;
	}
	
	public static byte getNextPackageSequence(byte current) {
		return (current &0xFF) +1 >=0xFF ? (byte)0x01 : (byte)(current+1);
	}

	public static byte getCurrentPackageSequence() {
		return currentPackageSequence;
	}
 
	/**
	 * 获取请求包
	 * @param cmd   包命令
	 * @param body  参数内容
	 * @return
	 */
	public static DevicePackage syn(int cmd, byte[] body) {
		DevicePackage pack = new DevicePackage(PackageType.SYN.getType(),
				getNextPackageSequence(),cmd,body);
		return pack;
	}
	
	
	public static DevicePackage syn(int cmd) {
		return syn(cmd,null);
	}
	
	/**
	 * 获取应答成功包
	 * @param sequence  包序列
	 * @param cmd 包命令
	 * @param body 参数内容
	 * @return
	 */
	public static DevicePackage ackSucc(byte sequence,int cmd,byte[] body) {
		return new DevicePackage(PackageType.ACK_SUCC.getType(),sequence,cmd,body);
	}
	
	public static DevicePackage ackSucc(byte sequence,int cmd) {
		return new DevicePackage(PackageType.ACK_SUCC.getType(),sequence,cmd,null);
	}
	
	/**
	 * 获取应答错误包
	 * @param sequence  包序列
	 * @param cmd 包命令
	 * @Param errorCode 错误代码
	 * @return
	 */
	public static DevicePackage ackError(byte sequence,int cmd,int errorCode) {
		return new DevicePackage(PackageType.ACK_ERROR.getType(),sequence,cmd
				,new byte[]{(byte)errorCode});
	}
}
