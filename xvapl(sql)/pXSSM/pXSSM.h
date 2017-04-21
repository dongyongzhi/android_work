#ifndef _PXSSM_H_
#define _PXSSM_H_
#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include "comm.h"
#include "../AccDll/AccDll.h"
#include "WatchDefine.h"

#pragma pack(1)
union BCSM_ATTR{
	WORD attr;
	struct
	{
		WORD bcsm:1;/*是否启用BCSM*/
		WORD bPlayFile:1;/*是否正在放音*/
		WORD bInterrupt:1;/*是否可以打断*/
		WORD bPlayFileEnd:1;/*是否放音完毕*/
		WORD bfirstDigitTimeOut:1;/*是否首位超时*/
		WORD binterDigitTimeOut:1;/*是否位间超时*/
		WORD bneedNullCDR:1;/*是否上报0话单*/
		WORD bCallOut:1;/*是否呼出*/
		WORD bSendDataNow:1;/*之前是否发出数据*/
		WORD res : 7;
	};
	
}; /*BCSM状态等其他属性*/
typedef struct 
{
	BYTE    minDigits;   /* 最小收集的数字数 */
    BYTE    maxDigits;   /* 最多搜集的数字  */
    BYTE    endDigit;    /* 结束字符 */
    BYTE    cancelDigits;       /* 取消字符 */
    BYTE    startDigit;         /* 开始字符 */
    WORD    firstDigitTimeOut;  /* 首位超时  */
    BYTE    interDigitTimeOut;  /* 位间超时  */
	BYTE   bConnnectNumber; /* 采集到的数据个数 */
	BYTE   collectedDigits[64];/*收集到的字符串*/
}COLLECTINFORMATION;//收集信息基本属性
typedef struct
{
	DWORD    sid;     /* 会话号 */
	char    CalledNumber[32];    /* 被叫号码 */
	char    CallingNumber[32]; /* 主叫号码 */
	DWORD   status;//状态
	union BCSM_ATTR  attrd;   /*BCSM状态等其他属性*/
	COLLECTINFORMATION pCollectInfor;//收集信息基本属性
	WORD          eventNumber;   //注册的事件总数，小于等于8
    enum emPXSSM_BCSM  events[8];     //注册的事件
	BYTE bnodenum;//属于那个节点
	int  serviceKey;     /* 业务键 */
	WORD timerId;//定时器返回值
	
}XSSM_SID;//会话记录基本属性
typedef struct
{
	int    serviceKey;     /* 业务键 */
	BYTE   toneIndex;    /* 业务语音编号 */
	BYTE   language; /* 语音描述 */
	BYTE   argIndes;/*参数编号，当为0时表示reference值为参数的总个数，必须连续*/
	BYTE   argType; /*参数类别： 0：音元    1：文件语音 2：数值 3：数字串  4：金额 5：日期    6：时间*/
	char reference[32];/*参数值*/	
} SERVICE_TONE;//业务语音音元定义
typedef struct 
{
	BYTE         elementNum;//音元个数
	SERVICE_TONE *pToneContent;//具体音元
}SERVICE_KEY_ELEMENT;//语音内容记录
typedef struct 
{
	BYTE ToneNum;//语音条目数
	SERVICE_KEY_ELEMENT *pElement;//语音内容记录
}SERVICE_KEY_TONE;//语音业务
typedef struct 
{
	BYTE   nodenum; /*节点号*/
	WORD   serviceTotal;    /* 业务处理条数 */
}NODE_INFOR;/*节点业务处理信息*/
typedef struct
{
	int   SkeyType;//业务接入类型
	int   serviceKey;     /* 业务键 */
	BYTE  initialtype;     /*1：主叫号码识别业务 2：被叫号码识别业务*/
	char  initialNum[16]; /*触发业务的号码*/
	BYTE  mask;  /* 0：号码左匹配 1：号码右匹配 2：号码完全匹配*/
	BYTE  nodeTotal; /*处理该业务的XVAPL平台节点个数，支持1-4个节点*/	
	NODE_INFOR *pNodeInfor;/*节点业务处理信息*/
	int  bUseNode;//上次业务使用的节点号
	SERVICE_KEY_TONE pTone;//语音业务
	char description[32];//业务简要描述
//	int  modulenumber;/*模块编号*/
} SERVICE_KEY;/*业务信息*/

#pragma  pack()
XSSM_SID  g_Sid[MESSAGE_MAX];

BOOL bPlatForm;//是否为平台
BOOL bHdlConnectStatus;/*和硬件设备连接状态 */
int  ServiceKey_Total;//业务总个数
BOOL IsPSTN;//是否有PSTN
SERVICE_KEY *pServiceKey;//业务总类
ACS_ATTR Hdl_Attr;//记录HDL基本信息
XSSM_TIME sXssmTime;//记录超时时间信息
HWND pwnd;

BOOL bFindSpp;   /*spp连接标志*/
BOOL ControlTrace(void *infor);
BOOL XSSM_ActiveTestResponse();
BOOL XSSM_ErrorReport(DWORD sid,BYTE ErrorType);
BOOL XSSM_StartDP(stPXSSM_InitiateDP pInitiateDP);
BOOL XSSM_RealeaseCall(stPXSSM_ReleaseCall pReleaseCall);
BOOL XSSM_CollectedInformation(stPXSSM_CollectedInformation pCollectedInformation);
BOOL XSSM_CollectedFSKInformation(stPXSSM_CollectedFSKInformation *pCollectedFSKInformation);
//BOOL XSSM_ResourceReport(DWORD sid);
BOOL XSSM_ResourceReport(/*DWORD sid*/stPXSSM_ResourceReport *pResourceReport);
BOOL XSSM_DoCallIn(void * pInfor);
BOOL XSSM_DoCallOut(void * pInfor);
BOOL XSSM_DoClearCall(void *pInfor);
BOOL XSSM_DoAll(void * pInfor);
BOOL XSSM_DoRecvIoData(void *pInfor);


BOOL XSSM_RecvConnectToResouce(void *infor);
BOOL XSSM_RecvDisConnectForwardConnection(void *infor);
BOOL XSSM_RecvPlayAnnouncement(void *infor);
BOOL XSSM_RecvPromptCollectInformation(void *infor);
BOOL XSSM_PromptCollectInformationAndSetFormat(void *infor);
BOOL XSSM_RecvSendFSK(void *infor);
BOOL XSSM_RecvSendFSKCollectInformation(void *infor);
BOOL XSSM_RecvPlayAnnouncementAndSetFSK(void *infor);
BOOL XSSM_RecvSendFSKCollectFSK(void *infor);
BOOL XSSM_RecvInitiateRecord(void *infor);
BOOL XSSM_RecvStopRecord(void *infor);
BOOL XSSM_RecvReleaseCall(void *infor);
BOOL XSSM_RecvConect(WORD sid,void *infor);
BOOL XSSM_RecvRequestReportBCSMEvent(void *infor);
BOOL XSSM_RecvTTSConvert(void *infor);
BOOL XSSM_RecvTTSPlay(void *infor);

void DealWithMessage(WPARAM wParam,LPARAM lParam);
void DoTimer(DWORD evt,DWORD sid);
void DoKillTimer(DWORD evt,DWORD sid);
void SetWaiteTimer(DWORD sid);
BOOL Init_XSSM();
BOOL Start_All();
BOOL ReadConfig();
BOOL ReadKeyTone(int keyNum);
BOOL SetRecallFuction();
void EvtHandler(DWORD esrParam);
void ReInitKeygoe();


BOOL XSSM_SENDMESSAGE(DWORD sid,void *pInfor,int len,WORD devent,BOOL bNew);
PVOICE *SwtichToneFile(DWORD sid,stPXSSM_Tone *pTone);
int FindKeyModule(char CalledNum[32],char CallingNum[32]);
int FindKeyNode(char CalledNum[32],char CallingNum[32]);
void FreeMerroy();
void SetSidEvent(WORD wNumber,DWORD ievent);

void SetRegisterHotKey(HWND hwnd);
void FreeHotKey(HWND hwnd);
void Monitor(int event, LPARAM lParam);
void InitTimer();
void GetSppStatus();
void StartAllWork();
//BOOL Update_Message(WORD senssionID,MESSAGE *pMsg,BOOL bDelete);
//BOOL Read_Message(WORD senssionID,MESSAGE *pMsg);
#endif