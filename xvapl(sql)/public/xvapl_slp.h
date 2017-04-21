/********************************************************************
公司名称：江苏怡丰通讯有限公司重庆研发中心
创建时间: 2008-6-11   10:46
文件名称: E:\XVAPL业务逻辑平台\xvapl_slp.h
文件路径: E:\XVAPL业务逻辑平台
file base:xvapl_slp
file ext: h
author:	  刘定文

purpose:	通用业务逻辑处理程序
*********************************************************************/
#ifndef _XVAPL_SLP_H_
#define _XVAPL_SLP_H_
#include "comm.h"
#include "event.h"
#include "init_viriable.h"
#include "Commdll.h"

#include "xvapl_define.h"
#include "pXSSM_global.h"
#include "TimerDll.h"
#include "spp_comm.h"
#include "Protocol.h"
#include "yftrace.h"
#pragma pack(1)
#define STRSTRING 1
#define BYTESTRING 2
//数据类型
#define DATA_BYTE    0
#define DATA_BOOL    1
#define DATA_WORD    2
#define DATA_DWORD   3
#define DATA_CHARSTR 4
#define DATA_BYTESTR 5

//参数类型
#define VAR_INT      0
#define VAR_CHARSTR  1
#define VAR_BYTESTR  2
#define VAR_VARIABLE 3
#define VAR_CONSTVAR 4


typedef struct 
{
	char Ip[16];//IP地址
	WORD port;//端口号
}SERVICE_KEY_TCP;//TCP业务信息

typedef struct
{
	BYTE  initialtype;     /*1：主叫号码识别业务 2：被叫号码识别业务*/
	char  initialNum[16]; /*触发业务的号码*/
	BYTE  mask;  /* 0：号码左匹配 1：号码右匹配 2：号码完全匹配*/

}SERVICE_KEY_PSTN;//PSTN业务信息
typedef struct
{
	int SkeyType;//业务类型
	int serviceKey;     /* 业务键 */
	union SSERVICEKEY
	{
		SERVICE_KEY_PSTN sPstn;
		SERVICE_KEY_TCP sTcp;
	}sServiceKey;
	TEST_SERVICRSET  sServiceTest;//调试信息
	BYTE  nodeTotal; /*处理该业务的XVAPL平台节点个数，支持1-4个节点*/	
	BOOL  bLoad;//是否加载
	BOOL  bStart;//是否启动
	char description[32];//业务简要描述

} SERVICE_KEY;/*业务信息*/
//typedef struct 
//{
//	int sysTimeout;
//	int hdlTimeout;
//}XSSM_TIME;//定时默认设置

#pragma pack()

void XSCM_ThreadProc(PVOID pVoid);
void Deal_Message(WPARAM wParam,LPARAM lParam);

BOOL InitXscmSlp();
BOOL ControlTrace(WORD senssionID);

void initService();
BOOL ReadServiceConfig(int bNo,WORD keyNumber);
BOOL ReadServiceKey();
BOOL InitMerroy();
BOOL RunSib(WORD senssionID,BOOL bNextStep);
void ControlRunSib(WORD senssionID,BOOL bNextStep);
void  XVAPL_ReCallRunSib(BOOL IsReCall,WORD senssionID);
/*sib function*/ 
BOOL Run_SIB_FUN(WORD senssionID,WORD sibID);
BOOL Run_SIB_if(WORD senssionID,WORD sibID);
BOOL Run_SIB_Switch(WORD senssionID,WORD sibID);
BOOL Run_SIB_Compare(WORD senssionID,WORD sibID);
BOOL Run_SIB_BCSM(WORD senssionID,WORD sibID);
BOOL Run_SIB_ReleaseCall(WORD senssionID,WORD sibID);
BOOL Run_SIB_PlayAnnouncement(WORD senssionID,WORD sibID);
BOOL Run_SIB_PromptCollectInformation(WORD senssionID,WORD sibID);
BOOL Run_SIB_PromptCollectInformationAndSetFormat(WORD senssionID,WORD sibID);
BOOL Run_SIB_PromptCollectFSK(WORD senssionID,WORD sibID);
BOOL Run_SIB_SendFSK(WORD senssionID,WORD sibID);
BOOL Run_SIB_SendFSKCollectInformation(WORD senssionID,WORD sibID);
BOOL Run_SIB_SendFSKCollectFSK(WORD senssionID,WORD sibID);
BOOL Run_SIB_InitiateRecord(WORD senssionID,WORD sibID);
BOOL Run_SIB_DBQuery(WORD senssionID,WORD sibID);
BOOL Run_SIB_Message(WORD senssionID,WORD sibID);
BOOL Run_SIB_Timer(WORD senssionID,WORD sibID);
BOOL Run_SIB_Log(WORD senssionID,WORD sibID);
BOOL Run_SIB_CDR(WORD senssionID,WORD sibID);
BOOL Run_SIB_InitDAP(WORD senssionID,WORD sibID);
BOOL Run_SIB_CallSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_CallOutSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_InitOdiSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_TcpLinkSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_InitTcpSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_TcpRecvDataSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_TcpSendDataRecvDataSib(WORD senssionID,WORD sibID);
BOOL Run_SIB_DataToFixStructSib(WORD senssionID,WORD sibID);
BOOL CheckCaseTest(WORD senssionID,WORD sibID);
void RelaseCheckCase(WORD senssionID);
/*end sib function*/

BOOL GetServiceKeyIdAndKey(char CalledNum[32],char CallingNum[32],BYTE *serviceId,int *serviceKey);
int GetServiceKeyIdByKey(int serviceKey);

BOOL GetParamAttr(BYTE serviceID,WORD offsetId,XSCM_CONSTVARIABLE *pVariable,BOOL bConst);

BOOL GetConstValue(BYTE serviceID,BYTE *value,DWORD position,WORD offset);
BOOL SetConstValue(BYTE serviceID,BYTE *value,DWORD position,WORD offset);
BOOL GetVaribleValue(WORD senssionID,BYTE *value,DWORD position,WORD offset);
BOOL SetVaribleValue(WORD senssionID,BYTE *value,DWORD position,WORD offset);

WORD GetServiceSibCount(BYTE serviceID);
BOOL GetSibParamAttr(BYTE serviceID,WORD sibId,SIB_PARAM *pSibParam,BYTE offsetId,BOOL bBySibId);
BOOL GetSibBasicAttr(BYTE serviceID,WORD sibId,SIB_BASIC *pSibBasic);
BOOL GetSibStringValue(BYTE serviceID,WORD sibId,void *value,WORD len, DWORD position,BYTE bStringType);
BOOL GetFunctionVaribleValue(WORD senssionID,BYTE serviceID,WORD sibId,BYTE *value,WORD len,BYTE bNo,DWORD offset);

BOOL GetFunctionVaribleConfig(WORD senssionID,BYTE serviceID,WORD sibId,BYTE *value,WORD len,BYTE bNo);
BOOL UpdateNextSib(WORD senssionID,WORD sibID,BOOL next);
BOOL UpdateSqlExeStr(WORD senssionID,BYTE serviceID,WORD sibID,char *pSqlExe,char *sqlstr,WORD len);

BOOL UpdateDBResultData(WORD senssionID);

void IF_Operation_Control(WORD senssionID,BYTE serviceID,WORD sibID,SIB_BASIC *pSibBasic);
BOOL IF_Operation_Control_Value(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL IF_Operation_Control_Value_CHAR(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL IF_Operation_Control_Value_INT(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL  Fun_Operation_Control(WORD senssionID,BYTE serviceID,WORD sibID,SIB_BASIC *pSibBasic);
void  Fun_Operation_STRING(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE operType);
void  Fun_Operation_INTEGER(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE operType);
void  Fun_Operation_INTEGER_BYTE(WORD senssionID,BYTE serviceID,WORD sibID,DWORD opsition,BYTE bTimes,BYTE operType);
void  Fun_Operation_INTEGER_WORD(WORD senssionID,BYTE serviceID,WORD sibID,DWORD opsition,BYTE bTimes,BYTE operType);
void  Fun_Operation_INTEGER_DWORD(WORD senssionID,BYTE serviceID,WORD sibID,DWORD opsition,BYTE bTimes,BYTE operType);
BYTE * SetMemBuffer(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,WORD *datalen);

BOOL BufferMemcpy(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pMem,BYTE bTimes,WORD datalen);

BYTE * SetDesBuffer(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,WORD *des_len,BOOL *bDesByte/*,BYTE operType*/);
BYTE *SetSrcBuffer(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,WORD *src_len,BOOL *bSrcByte/*,BYTE operType*/);
BOOL RunBufferMemcpy(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pSrc,BYTE *pDes,BYTE bTimes,BOOL bSrc,BOOL bDes,WORD src_len,WORD des_len,BOOL bSrcByte,BOOL bDesByte);

BOOL GetArrayValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);

BOOL ReadXSCMMessage(WORD SenssionID,MESSAGE_XSCM_SLAVE *pMsg);
BOOL UpdateXSCMMessage(WORD senssionID,MESSAGE_XSCM_SLAVE *pMesg,BOOL bDelete);
BOOL UpdateAndInitXSCMMessage(WORD senssionID,MESSAGE_XSCM_SLAVE *pMesg);
int GetBusyTimeId(WORD senssionID);
int GetVarByteStrLen(BYTE serviceID,WORD sibID,int begintimes,int endtimes);

BOOL UpdateCollectFskInformation(WORD senssionID);
BOOL UpdateCollectInformation(WORD enssionID);
BOOL UpdateTcpReceiveData(WORD senssionID);
BOOL UpdataOdiData(WORD senssionID);
BOOL UpdateDataOdiAccess(WORD senssionID);
BOOL UpdateDataPstnInit(WORD senssionID);
BOOL UpdateDataTCPInit(WORD senssionID);
BOOL ReleaseCall(WORD senssionID);
BOOL ControlReleaseCall(WORD senssionID);
void ControlEndSenssion(WORD senssionID);

void EndSenssion(WORD senssionID,BYTE reason);
void SetWaiteTimer(WORD senssionID,WORD timerevent,int intertime);
void OnTimerAck(WORD senssionID);

BOOL DoWithReport(WORD senssionID);
BOOL DoWithErrorReport(WORD senssionID);
void DBQueryResultNak(WORD senssionID);
BOOL UpdateCallOutData(WORD senssionID);

BOOL SvrMan_SetConstValue(WORD senssionID);
BOOL SvrMan_GetConstValue(WORD senssionID);
BOOL SvrMan_GetConstAttr(WORD senssionID);
BOOL SvrMan_GetAllConstAttr(WORD senssionID);
BOOL SvrMan_UpdateService(WORD senssionID);
BOOL UpdateServiceStatus(DWORD serviceID,BOOL bLoad);
BOOL WriteServiceconfig(DWORD serviceID);
BOOL UpdateServiceActive(DWORD serviceID,BOOL bStart);

BYTE GetDataTypeLen(BYTE bType);
BYTE HextoBcd(int value);
BYTE BcdToHex(BYTE value);
void BYTEToAscii(BYTE *distance,BYTE value);
void WORDToAscii(BYTE *distance,WORD value);
void DWORDToAscii(BYTE *distance,DWORD value);

BOOL XVAPL_Push(WORD senssionID,WORD sibNo);
BOOL XVAPL_Pop(WORD senssionID,WORD *sibNo);

BOOL ConvertTime(char *pValue,struct tm *pWhen);
int ConverHexData(int data);
void ReleaseAllData();
BOOL ReleaseServiceKey();
BOOL ReleaseMerroy();
BOOL SetBcdToStr(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL GetLowByte(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL GetHighByte(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL GetXorValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL GetSumValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL ConvertTwoCharToByte(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL SPP_GetServiceType(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL ConvertToUpper(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL ConvertToLower(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
void strLoWer(char *pFrom,char *pTo);
void strUpper(char *pFrom,char *pTo);
BOOL SPP_MakeWord(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL SetMacKeyValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE *value,int *len,BYTE *type);
BOOL SPP_MacCalculate(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE *key_value,int key_len,BYTE key_des);
BOOL SPP_BCDToHex(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL SPP_StrToBCD(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL SPP_BCDToDecWORD(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL SetHexToStr(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL SetStrToWord(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes);
BOOL IsBcd(BYTE value);
int GetVaribleId(WORD senssionID,BYTE bType,char *pdescrible);
int GetServiceKeyIdByKeyAndType(int serviceKey,int type);

BOOL CheckCase_Begin(MESSAGE pMessage);
BOOL CheckCase_End(MESSAGE pMessage);;
BOOL CheckCase_getSenssionID(MESSAGE pMessage);
BOOL CheckCase_ReadValue(WORD senssionID);
BOOL CheckCase_SetValue(WORD senssionID);
BOOL CheckCase_CurrentSibNo(WORD senssionID);
BOOL CheckCase_SibControl(WORD senssionID);
BOOL CheckCaseIsNow(WORD senssionID);

BOOL SPP_EnOrDescriptiom(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE *key_value,int key_len,BYTE key_des);


BOOL UpdateAckMessage(WORD wNowvent,WORD wLastevent,WORD senssionID,int mId);
SIB_ALL_STATISTICS sSibStatistics;//SIB整体信息统计

SERVICE_KEY *pServiceKey;//业务信息
XSCM_SLP *pXscmSlp;//记录业务流程信息
int ServiceKey_Total;//业务键个数

XSSM_TIME sXssmTime;
MESSAGE_XSCM_SLAVE *g_MESSAGE_XSCM_SLAVE;
BOOL bInital;//初始化

#endif