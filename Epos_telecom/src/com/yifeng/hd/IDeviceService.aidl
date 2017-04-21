package com.yifeng.hd;
import com.yifeng.hd.FontStyle;
import com.yifeng.hd.TransRequest;

interface IDeviceService{

    /**
    * 初始化设备
    */
    int init();
    
    /**
    * 用mac初始化设备
    */
    int initForMAC(String mac);
    
    int initForName(String name);
    /**
     * 读取终端版本号
     * @param version  版本内容
     */
	int getVersion(out byte[] version);
	
	/**
     * 获取pos地址
     */
	String getPOSAddress();
	
	/**
	 * 清屏
	 */
	int clearScreen();
	
	/**
	 * 清除指定行
	 *@param line      第几行
	 */ 
	int clearScreenLine(int line);
	
	/**
	 * 在终端屏幕上显示字符串
	 * @param line   行号（0 – 3） （每行占16像素高）
	 * @param col    列号（0 – 127）（单位：像素）
	 * @param str    需显示的字符串 
	 * 
	 */
	int screenDisplay(int line,int col,String str);
	 
	
    /**
	 *  文字打印
	 * <li>编码为GB2312</li>
	 *
	 * @param offset   打印内容在打印纸上的偏移位置
	 * @param size     字体大小     2种大小（0：16*16,1:24*24）
	 * @param info     要打印的内容，每个元素为一行
	 * @return   返回应答码
	 */
	int printASCII(int offset,int size,in List<FontStyle> info);
	
	/**
     * 图片打印
     * @param str   位图数据
     */
	int printBitmap(in byte[] bitmap);
	
	/**
     * 打印条形码 
     * <li>目前支持打印商品码EAN-13、code 128码（A集中的特殊ASCLL不支持） 、code 39码</li>
     * @param offset   打印条形码在打印纸上的偏移位置  (0 -- 0x01 0x80 )
     * @param type     打印类型 0: 128码 ,1: 39码 , 2: 商品码  
     * @param check    打印内容包括检验
     * @param barcode  打印内容   GB2312 编码
     */
	int printBarcode(int offset,int type,boolean check,in byte[] barcode);
	
	/**
	 * 走纸
	 * @param step  步进数
	 */
	int movePaper(int step);
	
    /**
	 * IC卡读取
	 * @param  ic  ic卡内容
	 * @return 
	 */
	int readIC(out byte[] ic);
	
	/**
	 * 读取 RFID 数据
	 * @param row   显示键值起始行(0-4)
	 * @param col   显示键值起始列(1-0x80)
	 * @param type  0 读取卡号  1  读取二、三磁道所有数据  
	 * @param data   返回码 和内容
	 */
	int readRFID(int row,int col,int type,out byte[] data);
	
	/**
	 * 刷卡和输密码
	 * @param type  0 读取卡号  1  读取二、三磁道所有数据  
	 * @param data   返回码 和内容
	 */
	int readBankCard(int type,out byte[] data);
	/**
	 * 设置超时时长   
	 * @param time  单位秒
	 * @return
	 */
	int setWaitTimeout(int time);
	
	/**
	 * 获取金额  
	 * @param row    显示键值起始行 (1-128)
     * @param col    显示键值起始列 (0-4)
     * @param min    最小长度
     * @param max    最大长度 (<=12)
	 * @param data    输入金额 单位为分
	 */
	int getMoney(int row,int col,int min,int max,out byte[] data);
	
	 /**
	 * 显示字符串
	 * @param line    显示起始行（0--4）
     * @param col     显示起始列（1--0x80）
	 * @param data    显示内容 GB2312
	 */
	int displayASCII(int line,int col,in byte[] data);
	
	 /**
	 * 显示点阵
	 * @param bline    显示起始行（0--4）
     * @param bcol     显示起始列（1--0x80）
     * @param eline    显示结束行（1--0x80）
     * @param ecol     显示结束列（1--0x80）
	 * @param data     显示数据
	 */
	int displayDotMatrix(int bline,int bcol,int eline,int ecol,in byte[] data);
	
	/**
	 * 下载logo至设备
	 * @param len     数据长度
	 * @param data    logo内容
	 */
	int downLoadLogo(int len,in byte[] data);
	
	/**
	 * 交易请求接口
	 * @param messageType  交易类型
	 * @param inData       输入数据
	 * @param outData      输出数据
	 */
	int transRequest(in TransRequest req,out byte[] outData);
	
	/**
	 * 交易应答报文解析接口
	 * @param inData     输入数据
	 * @param outData    输出数据
	 */
	int transResponse(in byte[] inData,out byte[] outData);
	
	/**
	 * 交易参数配置
	 * @param inData     读取数据
	 */
	int transParams(in byte[] inData,out byte[] outData);
	
	/**
	 * 打印最后一笔交易
	 */
	int printLastTrans();
	
	void close();
	/**
	 * 结束 命令
	 */
	void endCmd();
}