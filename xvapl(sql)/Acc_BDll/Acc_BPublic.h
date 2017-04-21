#ifndef _ACC_BPUBLIC_H
#define _ACC_BPUBLIC_H
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h> 
#include <fcntl.h>
#include "comm.h"
#pragma  pack(1)
enum CHANNEL_STATE {
	CH_FREE,//空闲 
	CH_RECEIVEID,//接收主叫号码，摘机
	CH_CHECKPLAY,//检查 放音是否完毕
	CH_CHECKFSK,//检查FSK是否发送完毕
	CH_RECVFSK,//接收FSK
	CH_SENDFSK,//发送FSK
	CH_RECVDTMF,//接收DTMF
	CH_DOOTHER,//统一处理
	CH_WAITSECONDRING,
	CH_WELCOME,
	CH_ACCOUNT,
	CH_ACCOUNT1,
	CH_PASSWORD,
	CH_PASSWORD1,
	CH_SELECT,
	CH_SELECT1,
	CH_PLAYRESULT,
	CH_RECORDFILE,
	CH_PLAYRECORD,
	CH_OFFHOOK,
	CH_WAITUSERONHOOK,
	CH_DIAL,
	CH_CHECKSIG,
	CH_PLAY,
	CH_ONHOOK,
	CH_CONNECT,

	CH_BUSY,
	CH_NOBODY,
	CH_NOSIGNAL,
	CH_NODIALTONE,
	CH_NORESULT
};
typedef struct 
{
	int nType;//类型
	int State;//状态
	char CallerID[32];//被叫号码
	char TelNum[32];//主叫号码
	char Dtmf[32];//dtmf数据
	int nTimeElapse;//等待时间
	int senddatalen;//发送FSK长度
	BYTE senddata[1024];//发送数据长度
	int datalen;//数据长度
	BYTE data[1024];// 数据内容 
	BOOL FBeginRecv;
	int FPrevLen;
	int FHeadPos;
	DWORD dStart;
}CHANNElINFOR;//板卡通道信息
enum VOICE_TYPE{
		VOICE_NONE,//无类型
		VOICE_FILE,//文件
		VOICE_INDEX,//序号
		VOICE_Variable,//变量
};
/************************************************************************/
/* 根据类型不同，声音文件格式也不同，当为VOICE_FILE时,文件内容为文件名(不带路径),
当为VOICE_INDEX时,文件内容为整型数据,当为VOICE_Variable时, 文件内容为变量个数,
每个变量的内容,格式如下:
数值%d,金额%m,串%s,日期%date,时间%t                                            */
/************************************************************************/
typedef struct 
{
	enum VOICE_TYPE pVoiceType;//声音类型
	char content[256];//内容
	int nLen;//内容长度
	BOOL bInterrupt;//是否可以打断
}PA;//语音文件

typedef struct 
{
	PA   pPa;//语音文件
	BYTE bFirstOverTime;//首位超时
	BYTE bPositionOverTime;//位间超时
	BOOL bEndChar;//是否需要结束符
	char pEndChar;//结束符
	BYTE bMinLen;//接收最小长度
	BYTE bMaxLen;//接收最大长度
}RECEIVE_PA;//接收语音文件
enum DIRECTION
{
	DIR_NONE,
	DIR_FSK,//fsk->fsk
	DIR_DTMF,//fsk->dtmf
};
typedef struct 
{
	BYTE data[256];//data content
	BYTE len; //data length
	BYTE overtime; //overtime set
	enum DIRECTION fsk_dir;//fsk direction
}FSK;

typedef struct
{
	WORD sid;/*会话号*/
	WORD serviceKey;/*业务键*/
	DWORD DeviceNumber;/*设备编号组合，根据设备不同，需要填写的值也不同，Keygoe系统高字为DSP号，低字为中继设备编号*/
	char CallerCode[20];  /*主叫号码*/
	char CalleeCode[20]; /*被叫号码*/
	BOOL bBusy;/*状态，正在使用还是空闲，FALSE为空闲，TRUE为占用*/
}SID_XSSM_DLL_INFOR;//会话基本信息记录

typedef struct
{
	WORD sid;/*会话号*/
	WORD serviceKey;/*业务键*/
	SOCKET fd;/*网络连接句柄*/
	char ip[16];  /*客户端IP*/
	char markadd[20]; /*MARK地址*/
	BOOL bState;/*状态，正在使用还是空闲，FALSE为空闲，TRUE为占用*/
}SID_TCP_INFOR;//TCP会话基本信息记录

typedef struct 
{
	WORD serviceKey;/*业务键*/
	char ip[16];  /*客户端IP*/
	WORD port; /*port*/
	SOCKET fd;/*网络连接句柄*/
	BOOL bState;/*状态，正在使用还是空闲，FALSE为空闲，TRUE为占用*/
}TCP_SERVICE_INFOR;//业务基本属性;
typedef struct 
{

	WORD index;//索引号
	char filename[32];//文件名

}TONE_INDEX_FILE;//语音文件属性
typedef struct 
{
	WORD g_total;//索引条目数
	TONE_INDEX_FILE *pIndexFIle;//语音文件属性
}TONE_INDEX;//语音文件索引表

#pragma  pack()
//typedef unsigned short      WORD;
typedef unsigned long     DJ_U32;
typedef	 void (*pEvtdefine)(DJ_U32 esrParam);
SID_XSSM_DLL_INFOR g_Sid_Xssm_Dll_Infor[MESSAGE_MAX];//会话记录基本属性
WORD TotalChannel;
pEvtdefine    pEvtHandle;
long DriverOpenFlag;
CHANNElINFOR *pChannelInfor;
char VoicePath[100];
char sTmp[128];

#endif
