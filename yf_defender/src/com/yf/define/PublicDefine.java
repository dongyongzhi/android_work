package com.yf.define;

import android.os.Build;
import android.os.Environment;

public class PublicDefine {
    
	public static String  url=null; //连接地址
	public static String  sn=null;  //序列号
	public static String  ver="2016-11-10";//版本日期
	public static boolean isDownloading = false;
	public static final int sevice_test = -1; // 服务测试
	public final static int yf_success = 0; // 操作成功
	public final static int yf_UnistallFailed = 101; // 卸载失败
	public final static int yf_MD5_Err = 102; // MD5错误
	public final static int yf_getSignFailed = 103; // 签名获取失败
	public final static int yf_getYzSignFailed = 104; // 签名验证失败
	public final static int yf_loadxmlfailed = 105; // 装载xml失败
	public final static int yf_filesignfailed = 106; //文件签名验证失败
	public final static int yf_downlaodfilfailed = 107; //文件下载失败
	public final static int yf_unpermission=108;    //非法应用被删除   
	public final static int TIME_OUT = 10000; // 启动线程
	public final static int RCV_INSTALLED = 109;//安装事件
	public final static int RCV_UNINSTALLED =110;//卸载事件
	public final static int RCV_CONNECTED = 111;//网络连接成功事件
	public final static int RCV_DISCONNECTED= 112;//网络断开事件
	public final static int REMOTE_SEVERSUCC= 113;//远程代码执行成功
	public final static int WRITE_SNERR= 114;//写入SN错误
	public final static int MSG_YFPOSSEVERNOTEXIST= 115;//YFPOSSEVER不存在
	public final static int MSG_WRITESNNUM= 116;//延迟写入 SN号
	public final static String APP_PACKETNAME = "com.yf.defender";
	public final static String DESK_PACKETNAME ="com.yfcomm.yf_desk";
	public final static int THREAD_BEGIN = 1;// 线程下载开始
	public final static int YF_Complete = 2; // 初始XML下载完成
	public final static int MSG_DOWNLOAD_PROGRESS = 3; // 线程进度显示
	public final static int THREAD_FINISHED = 4; // 线程下载结束
	public final static int MSG_DOWNLOAD_ERROR = 5; // 下载错误
	public final static int MSG_CONNECT_HTTP_ERROR = 6; // 打开文件出错
	public final static int MSG_PACKAGE_ERROR = 7; // 数据包错误
	public final static int MSG_TERM_TIMEOUT = 8; // 和终端通信超时
	public final static int MSG_NOT_FOUNDFILE = 9; // 未找到初始文件
	public final static int START_THREAD = 10; // 启动线程
	public final static int MSG_SIGNED_FAILED = 11; // 签名验证失败
	public final static int MSG_MD5_FAILED = 12; // MD5验证失败
	public final static int DOWNLOADWAITING = 13;//等待下载
	public final static int STOPDOWNLOAD=14;
	public final static int SCANNEADDOWNLOAD=15;//扫描需要下载的 
	public final static int SHOWINSTALLINFO=16;//显示安装信息 
	public final static int MSG_GETURLADDRESS=17;//url 地址下载成功 
	public final static int MSG_GETURLADDREERR=18;//url 地址下载失败
	public final static int MSG_GETONDEVICE=19;//响应地址
	public final static int YF_CompleteRepeat = 20; // 下载完成XML重复
	
	
	
	
	public final static String save_xml = "yfstoreIng.xml"; // 首次解析的XML
	public final static String save_bakxml = "yfstore.xml"; // 备份解析的XML
    public final static String path=Environment.getExternalStorageDirectory()+"/";
  //public final static String YF_DNS = "http://218.4.71.218:10000/yfstore/"; // 连接的服务器地址
	public final static String YF_DNS = "http://"+ Build.HOST+"/"; // 连接的服务器地址
    public static String SubDir;
//	public final static String YF_DNS = "http://"+"121.40.170.88:87/"; // 连接的服务器地址
	public final static String XML_NAME = "yfstore.xml"; // 连接服务器数据的数据文件名

	public final static String RSApublic_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1x6qmZyzmSVbTip7qu/yl7rNJ"
			+ "\r"
			+ "f/ci5zMOGjuAEfUPBNu4c496da0UmKs7Hgz/en0S42IHnM0Tnd3ZZ0n+FF58QKGM"
			+ "\r"
			+ "UWbBOWpjrSrh+kaTKDDJg3Hzga1uOWTXg7GoCOMBkJWftBrrCta1Q7BT7jZgW9OT"
			+ "\r" + "tWQDjiCcQWWfTS6SQQIDAQAB";
}