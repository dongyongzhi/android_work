#ifndef _SPP_COMM_H_
#define _SPP_COMM_H_

#include "comm.h"
#include "pXSSM_global.h"

#pragma   pack(1)

typedef struct 
{
	int timerId;  //返回的timerId
	BOOL bUsed;    //TIMER是否在用
}TIMER_COUNT;


typedef struct 
{
	BYTE bVaribleTotal;//变量个数
	DWORD dVaribleNo[20];//变量编号
}VARIBLE_SAVE;//记录结果返回变量的编号及变量个数


typedef struct  
{ 
	WORD sibNo[STACK_SIZE]; //存入堆栈的数据
	int top;//堆栈位置,初始化为-1
}SIB_STACK; //定义堆栈数组用于记录SIB编号堆栈




typedef struct 
{
	WORD servicetype;//业务类型，0表示PSTN，1表示TCP,odi必须大于10
	union INIT
	{
		BYTE init_value[1024];
		stPXSSM_InitiateDP init_pstn;
		stTCP_InitTcp init_tcp;
		stODI_InitODI init_odi;
	}sInit;

}SP_INIT;//接入基本属性

typedef struct 
{
	BYTE           serviceId;//业务编号
	int		       serviceKey;//业务类型
	SP_INIT        serInit;//接入基本属性
	BYTE           status;   /*状态*/
	BYTE           step;     /*步骤*/
	WORD           lastsibNo; /*上次执行的SIB*/
	WORD           sibNo;//将要执行的SIB
	BOOL           bReissues;/*是否已经重复*/    
//	char    CallingNum[32];//主叫号码
//	char    CalledNum[32];//被叫号码
	SIB_STACK      sibStack;//堆栈SIB编号
	TIMER_COUNT    timerevent[TIMER_MAX];//设置定时器的事件
	WORD           oldevent;//上次事件
	WORD           wlastevent;//上次发送事件
	DWORD          toneID;//语音编号
	char           ref[32];//

}MSG_HEAD_SLAVE;

typedef struct 
{
	BOOL bCheckCase;//是否已经开启调试
	BOOL bStopRun;//是否暂停运行
	int  stopsib;//停止运行的SIB

}TEST_INIT;//调试属性

typedef struct 
{
	MESSAGE           messageInfor;   //备份信息
	TEST_INIT         sTestinit;      //调试信息
	MSG_HEAD_SLAVE    slavehead;      //消息备份记录头
	BYTE              accID;          //异常事件号
	WORD              AcceseMID;       //启动消息来源
	VARIBLE_SAVE      sVaribleSave;   //记录结果返回变量的编号及变量个数
	WORD              variblelen;     //变量长度
	BYTE              *pVarileValue;  //变量值
	
}MESSAGE_XSCM_SLAVE;//消息备份



/*内部定义,只给SPP用*/
typedef struct 
{
	HWND       g_hWnd;				/*窗口句柄*/
	DWORD      g_dwNode;			/*节点（IP地址）*/ 
}PUBLIC;/*记录窗口和节点信息*/

typedef struct
{
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




typedef struct 
{
	BYTE bType;//业务类型
	BYTE bServiceNumber;//业务编号
    int stopsibno;//停止运行的SIB编号
	char reference [32];//参数值，业务类型为0表示参数值是电话号码，为1则是IP地址
}TEST_BEGINCONTROL;
typedef struct 
{
	BOOL  bFind;//设置是否成功，如果成功，后面的会话号有意义，反之无
//	DWORD senssionID;//被设置调试的会话号 
}TEST_BEGINCONTROLACK;


typedef struct 
{
	BYTE bType;//业务类型
    BYTE bServiceNumber;//业务编号
    char reference [32];//参数值，业务类型为0表示参数值是电话号码，为1则是IP地址
}TEST_GETTESTSENSSION;//获取当前调试的会话号

typedef struct 
{
	BOOL  bFind;//设置是否成功，如果成功，后面的会话号有意义，反之无
   WORD senssionID;//会话号
}TEST_GETTESTSENSSIONACK;//回送当前调试的会话号

typedef struct 
{
	BYTE bType;//业务类型
    BYTE bServiceNumber;//业务编号
    char reference [32];//参数值，业务类型为0表示参数值是电话号码，为1则是IP地址 
}TEST_ENDCONTROL;

typedef struct 
{
	DWORD senssionID;//被设置调试的会话号 
    BOOL  VaribleType;//类型，0表示常量，1表示变量
    char    variblename[32];// 常量或变量名称
}TEST_READVALUE;
typedef struct 
{
	BOOL bStatus;//读取是否成功
	char    variblename[32];// 常量或变量名称
    int datalen;// 数据字节长度
    BYTE data[CHAR_NUMBER-40];//数据内容

}TEST_READVALUEACK;
typedef struct 
{
	DWORD senssionID;//被设置调试的会话号 
    int  VaribleType;//类型，0表示常量，1表示变量
    char    variblename[32];// 常量或变量名称
    int datalen;//数据长度
    BYTE data[CHAR_NUMBER-44];//数据内容
}TEST_SETVALUE;

typedef struct 
{
	BOOL bStatus;//设置是否成功
}TEST_SETVALUEACK;
typedef struct 
{
	DWORD senssionID;//被设置调试的会话号 
}TEST_CURRENTSIBNO;

typedef struct 
{
	BOOL bStatus;//获取是否成功
    int sibno;//当前要执行SIB编号
}TEST_CURRENTSIBNOACK;
typedef struct 
{
    DWORD senssionID;//被设置调试的会话号
	BOOL bRun;//TRUE表示执行不用在执行某个SIB时停止操作，FALSE表示需要在sibno执行前停止操作
    int sibno;//当前要执行SIB编号
}TEST_SIBCONTROL;

#pragma  pack()
#endif