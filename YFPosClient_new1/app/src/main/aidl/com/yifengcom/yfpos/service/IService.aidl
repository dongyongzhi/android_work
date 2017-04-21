package com.yifengcom.yfpos.service;
import com.yifengcom.yfpos.service.ICallBack;
import com.yifengcom.yfpos.service.WorkKey;

interface IService {
	void registerICallback(ICallBack cb);
	
    void unregisterICallback(ICallBack cb);

	// 获取设备版本
	void onGetDeviceInfo(ICallBack icallback);

	// 设置数据
	void setDeviceData(String customerNo, String termNo, String serialNo,String batchNo, ICallBack icallback);

	// 打印
	void onPrint(inout byte[] body, ICallBack icallback);
	
	// 验证MAC
	void calculateMac(inout byte[] data, ICallBack icallback);

	// 更新主密钥
	void writeMainKey(inout byte[] key, ICallBack icallback);
	
	// 更新工作密钥
	void writeWorkKey(in WorkKey key, ICallBack icallback);
	
	// 射频卡
	void readRFCard(ICallBack icallback);
	
	// 读取电子现金余额
	void readMoney(ICallBack icallback);
	
	// 刷卡
	void startSwiper(int timeout,long nAmount,int brushCard,byte type,ICallBack icallback);

	// 取消指令
	void cancel();
	
	// 设置时间
	void setDateTime(long time, ICallBack icallback);
	
	// 获取时间
	void getDateTime(ICallBack icallback);
	
	// 获取PSAM卡号
	void getPsamInfo(ICallBack icallback);
	
	// 获取PSAM、ST720 ATR内容
	void getPsamAndSt720Info(ICallBack icallback);
	
	// 发送身份证数据
	void sendIDCardData(inout byte[] body, ICallBack icallback);
	
	// 打开ADB
	void openADB(ICallBack icallback);
	
	// 关闭ADB
	void closeADB(ICallBack icallback);
	
	// 清除安全触发
	void clearSecurity(ICallBack icallback);
	
	// 清除密钥证书
	void clearKey(ICallBack icallback);
	
	// 更新固件
	void updateFirmware(String path,ICallBack icallback);
	
	// 打开射频
	void openRFID(ICallBack icallback);
	
	// 发送射频命令
	void sendRFIDCmd(inout byte[] data,ICallBack icallback);
	
	// 关闭射频
	void closeRFID(ICallBack icallback);
	
	// 打开IC卡寻卡
	void openICFind(ICallBack icallback);
	
	// IC卡数据交互
	void sendICCmd(inout byte[] data,ICallBack icallback);
	
	// IC卡下电
	void powerDownIC(ICallBack icallback);
	
	// PSAM卡复位
	void resetPSAM(inout byte[] data,ICallBack icallback);
	
	// 读PSAM卡
	void readPSAM(inout byte[] data,ICallBack icallback);
	
	// 写PSAM卡
	void writePSAM(inout byte[] data,ICallBack icallback);
	
	// 自毁测试
	void testDestroy(ICallBack icallback);
	
	// 请求录入SN号
	void requestSN(ICallBack icallback);
	
	// SN号下发
	void writeSN(inout byte[] data,ICallBack icallback);

	void EMVTest(inout byte[] data, ICallBack icallback);
	
	// 读取SLE4442数据
	void readSLECardData(int offset,int len,ICallBack icallback);
	
	// 写入SLE4442数据
	void writeSLECardData(String pwd,int offset,inout byte[] data,ICallBack icallback);
	
	// 修改密码
	void updateSLEpwd(String pwd,String newPwd,ICallBack icallback);
	
	// 上传电子签名
	void uploadSignBitmap(String path,ICallBack icallback);
	
	
	//PSAM卡复位
	byte[] psamResetEx(int SiteNo,  int timeout);
	
	//PSAM卡APDU指令透传
	byte[] psamApduEx(int SiteNo, in byte[] send, int timeout);
	
	// 打开射频, 单位：秒,最大255秒
	byte[] rfidOpenEx(int timeout);
	
	// 发送射频命令
	byte[] rfidApduEx(inout byte[] data, int timeout);
	
	// 关闭射频
	int rfidCloseEx();
	
}
