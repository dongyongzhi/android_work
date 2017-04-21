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

#define  MESSAGE_MAX         2000    /*  消息最大条数                  */
#define  TIMER_MAX           10      /*  timer种类                     */
#define  CHAR_NUMBER         1024    /*  一条消息个字节个数            */
#define  RETSQL_INT_LEN	 	 1024	 /*  SQL执行后返回的整形数据的长度 */
#define  RETSQL_STR_LEN      1024	 /*  SQL执行后返回的字符串的长度   */

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
	UINT   mId;         /*模块号*/
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

typedef struct 
{
	WORD offset;//本次该读的位置
	BYTE bSign[MESSAGE_MAX];//可用标志，0为可用，1为不可用
} SID_POSITION;//记录会话及消息偏移位置


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




#endif _YFSPP_COMM_H_