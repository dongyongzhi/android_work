/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-12-17   9:48
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public\comm.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public
file base:comm
file ext: h
author:	  刘定文

purpose:	通讯结构定义
*********************************************************************/
#ifndef _YFSPP_COMM_H_
#define _YFSPP_COMM_H_


#include <windows.h>
#include "define.h"
/* 请在主程序代码中定义 _YFSPP_COMM_C_  和 g_dwNode ,g_hWnd或g_dwProcessId*/
#define _YFSPP_COMM_C_
// #define _WIN32
#ifndef _YFSPP_COMM_C_
extern  DWORD g_dwNode;
#ifdef  _WIN32
extern  HWND  g_hWnd;
#else
extern  DWORD g_dwProcessId;
#endif
#endif

#define  MESSAGE_MAX         4000 /*消息最大条数*/
#define  CHAR_NUMBER         470 /* 一条消息个字节个数*/
#define  RETSQL_INT_LEN	 	10		//SQL执行后返回的整形数据的长度
#define  RETSQL_STR_LEN     1024	//SQL执行后返回的字符串的长度
#pragma   pack(1)

typedef struct
{
	DWORD  node;     /* 节点号 */
	BOOL   isSPP;    /* 是否SPP服务器 */
	BOOL   isServer; /* 是否通信联接服务端 */
	BYTE   bModuleNum;/*总模块数*/
	DWORD  module;   /*节点通讯模块线程号*/
	BOOL   isActive; /* 联接状态是否完好 */
} NID;

typedef struct
{
	DWORD  node;       /* 节点号   */
#ifdef _WIN32	
	HWND   hWnd;       /* 本进程的主窗口句柄 */
#else
	DWORD  procece;    /* 进程号，非window系统   */
#endif
	DWORD  module;     /*  进程中线程，为线程号，否则为模块号   */
} TID;

typedef struct  
{
	UINT     mId;       /* 模块号 */
	TID      tId;       /* 模块线程标识 */  
	BOOL     isSysModule; /* 是否系统模块 */
	UINT     state;      
}MID;

typedef struct
{
	TID     Source;   /* 消息产生源 */
	TID		sender;   /* 发送者 */
	TID		receiver;	/* 接收者 */
	WORD	event;    /* 事件 */
	WORD	len;      /* 消息长度 */
	BOOL	sync;     /* 同步标志，如果为TRUE，必须等待消息返回 */
	DWORD	ptrAck;   /* 返回数据缓冲区指针，对于同步消息需要返回数据时设置，异步方式设为NULL */
	WORD	ackLen;   /* 返回数据缓冲区长度 */
	DWORD	reserve;  /* 保留 */
} MSG_HEAD;
typedef struct
{
	MSG_HEAD  head;   /* 消息头 */
	BYTE     data[CHAR_NUMBER];   /* 由len指定长度的数据 */
} MESSAGE;              /* 消息缓冲区由发起消息的线程申请和释放 */
                    /* 接受消息的线程收到消息后必须立即分配一块内存，将消息缓冲区数据拷贝过来进行操作   */



typedef struct 
{
	WORD	event;		/* 定时器到时，触发的事件 */
	//TID		recvTid;	/* 定时器申请者 */
	UINT     mId;       /* 模块号 */
	WORD    wNum;       /*消息偏移量*/
	BOOL	isAbs;		// 绝对定时器标志 
						//TRUE:为一个绝对时间,当时间到达时触发 
						//FALSE:一个相对时间
	DWORD	dwTime;	/* 定时器，秒为单位。如果绝对定时器，则表示时间 */
	DWORD	ref;		/* 参考值，由申请者自己定义，返回时原值返回 */
} TMID;


/*内部定义*/
typedef struct 
{
	HWND       g_hWnd;				/*窗口句柄*/
	DWORD      g_dwNode;			/*节点（IP地址）*/ 
}PUBLIC;/*记录窗口和节点信息*/

typedef struct
{
//	BYTE bAccNum;/*接入服务器个数*/
	BYTE bNodeNum;/*节点个数*/
	BYTE bIppNum;/*第三方系统个数*/
	char version[4];/*版本号*/
	char pName[64];/*名称*/
	union ATTR{
		WORD uwAttri;
		struct 
		{
			WORD isCdr : 1;//
			WORD isCdrToSql : 1;
			WORD res :14;
		}att;
	} wAttri;/*基本属性*/
	
} SPP_BASIC; /*基本信息*/


typedef struct
{
	DWORD ComputerIp;/*计算机名*/
	char pUserName[64];/*用户名*/
	char pPassWd[16];/*密码*/
} SPP_DATABASE;/*数据库信息*/

typedef struct
{
	int id;/*编号*/
	char pName[64];/*名称*/
	WORD wAttri;/*属性*/

}SPP_ACC;/*接入服务器信息*/

typedef struct
{
	int id;/*编号*/
	char pName[64];/*名称*/
	BOOL isServer; /*服务器端*/
	DWORD dIp;/*IP地址;*/
	WORD  wPort;/*端口号*/
	
}SPP_IPP;/*第三方接口信息*/
typedef struct 
{
	DWORD ip;
	char content[256];
}SPP_CDR;


/************************************************************************/
/* 语音转换格式定义                                                     */
/************************************************************************/
enum PLAY_FILE_TYPE{
	PLAY_FILE_TONE_ELMENT,//无类型
	PLAY_FILE_FILE,//文件名（带路径）
	PLAY_FILE_DIGIT,//数字
	PLAY_FILE_CHAR,//串
	PLAY_FILE_CURRENCY,//货币
	PLAY_FILE_DATE,//日期
	PLAY_FILE_TIME,//时间
	 
};//语音内容格式
typedef struct 
{
	int nKey;//编号,会话号
	BYTE nNo;//内容条目数
	
}PVOICE_HEAD;//播放内容头
typedef struct{
	enum PLAY_FILE_FILE pType;//播放内容类型
	BYTE language;//语言
	char content[32];//播放内容
}PVOICE_CONTENT;//播放内容

typedef struct
{
	PVOICE_HEAD pVoiceHead;//播放内容头
	PVOICE_CONTENT *pVoice_Content;//播放内容指针
}PVOICE;//播放文件


/*INI文件结构*/
#define LENGTH_KEY     32   /*关键字长度*/
#define LENGTH_SECTION 64   /*节长度*/
#define LENGTH_VALUE   128  /*配置数据长度*/
#define LENGTH_COMMENT 128  /*注释长度*/
#define LENGTH_BUFFER  256  /*字符缓冲大小*/
/*关键词存储结构*/
typedef struct Key
{
    char key[LENGTH_KEY],value[LENGTH_VALUE],comment[LENGTH_COMMENT];/*关键词\数据\注释*/
    struct Key *next;/*下一结点*/
}KEY,K;
/*节存储结构*/
typedef struct Section
{
   char section[LENGTH_SECTION],comment[LENGTH_COMMENT];/*节名及注释*/
   struct Key headKey;/*对应节关键词首结点,首结点不含数据*/
   struct Section *next;/*下一结点*/
}SECTION,SEC,S;
/*配置文件结构*/
typedef struct Ini
{
   struct Section headSection;/*节首结点,首结点不含数据*/
}INI,I;



typedef struct 
{
	BYTE Tel_Type;//主叫号码判断还是被叫号码判断，1为主叫，2为被叫
	BYTE Tel_Match;//1表示左匹配，2表示右匹配，3表示全匹配，4表示全部电话号码
	char Telephone[32];//电话号码
	BOOL bGetSysInfor;//获得系统信息
	BOOL bTrace;//是否跟踪
}TRACE_CONDITION;//跟踪条件

#pragma   pack()




#endif


















