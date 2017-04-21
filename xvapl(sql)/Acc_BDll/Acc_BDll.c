/********************************************************************
	created:	2010/10/27
	created:	27:10:2010   15:17
	filename: 	E:\苏州卡易付系统\卡易付最新\xvapl源代码\Acc_BDll\Acc_BDll.c
	file path:	E:\苏州卡易付系统\卡易付最新\xvapl源代码\Acc_BDll
	file base:	Acc_BDll
	file ext:	c
	author:		ldw
	
	purpose:	
*********************************************************************/
#include <windows.h>
#include <process.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h> 
#include <fcntl.h>
#include <shellapi.h>
#include <sys\stat.h> 
#include <Tlhelp32.h>
#include "Tc08a32.h"
#include "NewSig.h"
#include "commdll.h"
#include "D.h"

#include "Acc_BDll.h"
TONE_INDEX_FILE *pToneIndex;
int nToneTotal;
BOOL InitToneIndex();
BOOL InitAllDllInfor();

BOOL APIENTRY DllMain (HINSTANCE hInst     /* Library instance handle. */ ,
                       DWORD reason        /* Reason this function is being called. */ ,
                       LPVOID reserved     /* Not used. */ )
{
		
    switch (reason)
    {
      case DLL_PROCESS_ATTACH:
        break;

      case DLL_PROCESS_DETACH:
        break;

      case DLL_THREAD_ATTACH:
        break;

      case DLL_THREAD_DETACH:
        break;
    }

    /* Returns TRUE on success, FALSE on failure */
    return TRUE;
}

/********************************************************************
函数名称：Hdl_Init
函数功能: Hdl初始化接口
参数定义: 无
返回定义: 无
创建时间: 2007-12-23 16:58
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT void Hdl_Init(ACS_ATTR attr)
{
	TotalChannel=0;
	DriverOpenFlag=-50;
	InitAllDllInfor();
	nToneTotal=0;
	InitToneIndex();
	_beginthreadex(NULL,0,BAcc_ThreadProc,NULL,0,NULL);
	
}

DLLIMPORT void Hdl_SetTrace(BOOL value)
{
	//g_Trace = value;
}
/********************************************************************
函数名称：Hdl_SetRecall
函数功能: 设置回掉函数
参数定义: esr：回掉函数指针
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2007-12-23 16:59
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_SetRecall(void *esr)
{
//	pEvtHandle= esr ;
	return  TRUE;
}
/********************************************************************
函数名称：Hdl_SendFSK
函数功能: 发送FSK
参数定义: sid:会话号，data:数据内容，len:数据长度
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-1-31 14:50
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_SendFSK(DWORD sid,/*BYTE *data,WORD len*/void *infor)
{


	WORD ChannelId;
	int r;
	stFSK *fsk=(stFSK *)infor;

	if(fsk == NULL)
		return FALSE;
	
	if(sid>MESSAGE_MAX)
	{
		
//		YF_LOG_SMM ,"ACC->Keygoe:sid is overflow,send fsk failed");
		
		return FALSE;
	}
	if(!g_Sid_Xssm_Dll_Infor[sid].bBusy)
	{
//		YF_LOG_SMM "Send FSK resource busy");
		return FALSE;
	}
	ChannelId = (WORD)(g_Sid_Xssm_Dll_Infor[sid].DeviceNumber/(256*256));
	if(ChannelId >= TotalChannel)
		return FALSE;
	if((pChannelInfor[ChannelId].State==CH_FREE))
		return FALSE;
//	pChannelInfor[ChannelId].senddatalen=fsk->length;
//	memset(pChannelInfor[ChannelId].senddata,0,sizeof(pChannelInfor[ChannelId].senddata));
//	memcpy(pChannelInfor[ChannelId].senddata,fsk->message,fsk->length);
//	pChannelInfor[ChannelId].State=CH_SENDFSK;
//	return TRUE;
	r=DJFsk_SendFSK(ChannelId,fsk->message,fsk->length,FSK_CH_TYPE_160);
	if(r!=1)
		return FALSE;
	else
	{
		pChannelInfor[ChannelId].State=CH_CHECKFSK;
		return TRUE;
	}
}
/********************************************************************
函数名称：Hdl_Exit
函数功能: Hdl退出，系统自动关闭与ACS系统的连接，释放分配的内存
参数定义: 无
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2007-12-23 17:00
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

DLLIMPORT BOOL Hdl_Exit()
{
	DriverOpenFlag=-255;
	InitAllDllInfor();
	free(pToneIndex);
	pToneIndex=NULL;
//	return(ExitSystem());
	return TRUE;
}
/********************************************************************
函数名称：Hdl_PlayFile
函数功能: 放音
参数定义: 无
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2007-12-23 17:01
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
//DLLIMPORT BOOL Hdl_PlayFile(BYTE bType,BYTE bNo,BOOL bPlay)
DLLIMPORT void Hdl_PlayFile(int nDsp,int nTrunkNo,LPARAM lParam)
{
	char pChar[32];
	char FileName[200];
	int toneno;
	PVOICE *pVoice =(PVOICE *)lParam;
	

	int i;
	if(nDsp>=TotalChannel)
		return;
	if(pChannelInfor[nDsp].State==CH_FREE)
		return ;
	if(pVoice == NULL)
		return;
	StopPlayFile((WORD)nDsp);
	RsetIndexPlayFile((WORD)nDsp);
	for(i=0;i<pVoice->pVoiceHead.nNo;i++)
	{
		
		memset(pChar,0,sizeof(pChar));
		memset(FileName,0,sizeof(FileName));
		strcpy(pChar,pVoice->pVoice_Content[i].content);
		toneno=atoi(pChar);
		sprintf(FileName,"C:\\yfcomm\\Voc\\%s",pToneIndex[toneno].filename);
		AddIndexPlayFile((WORD)nDsp,FileName);
	}
	StartIndexPlayFile((WORD)nDsp);
	pChannelInfor[nDsp].State=CH_CHECKPLAY;
	pChannelInfor[nDsp].datalen=0;
	memset(pChannelInfor[nDsp].data,0,sizeof(pChannelInfor[nDsp].data));
}
/********************************************************************
函数名称：Hdl_FilePlay
函数功能: 文件放音
参数定义: sid:会话号，pVoiceContent：放音内容
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 14:33
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

DLLIMPORT BOOL Hdl_FilePlay(DWORD sid, void * VoiceAddr/*PVOICE *pVoiceContent*/)
{
	PVOICE *pVoice =(PVOICE *)VoiceAddr;
	int i;
	int nDsp,nTrunkNo;
	char pChar[32];
	char FileName[200];
	int toneno;
	if(sid>=MESSAGE_MAX)
		return FALSE;
	nDsp=g_Sid_Xssm_Dll_Infor[sid].DeviceNumber/(256*256);
	nTrunkNo = g_Sid_Xssm_Dll_Infor[sid].DeviceNumber%(256*256);

	if(nDsp>=TotalChannel)
		return FALSE;
	if(pChannelInfor[nDsp].State==CH_FREE)
		return FALSE;
	if(pVoice == NULL)
		return FALSE;
	StopPlayFile((WORD)nDsp);
	RsetIndexPlayFile((WORD)nDsp);
	for(i=0;i<pVoice->pVoiceHead.nNo;i++)
	{
		
		memset(pChar,0,sizeof(pChar));
		memset(FileName,0,sizeof(FileName));
		strcpy(pChar,pVoice->pVoice_Content[i].content);
		toneno=atoi(pChar);
		sprintf(FileName,"C:\\yfcomm\\Voc\\%s",pToneIndex[toneno].filename);
		AddIndexPlayFile((WORD)nDsp,FileName);
	}
	StartIndexPlayFile((WORD)nDsp);
	pChannelInfor[nDsp].State=CH_CHECKPLAY;
	pChannelInfor[nDsp].datalen=0;
	memset(pChannelInfor[nDsp].data,0,sizeof(pChannelInfor[nDsp].data));
	return TRUE;
}
/********************************************************************
函数名称：Hdl_setsid
函数功能: 设置会话号
参数定义: 
		sid：会话号
		calledNum：被叫号码
		callingnum:主叫号码
返回定义: 成功：TRUE，失败：FALSE
创建时间: 2010-10-29 11:54
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_setsid(DWORD sid,DWORD DeviceNumber,char calledNum[32],char callingNum[32])
{
	int number;
	if(sid>=MESSAGE_MAX)
		return FALSE;
	number = sid;
	g_Sid_Xssm_Dll_Infor[number].sid = (WORD)sid;
	g_Sid_Xssm_Dll_Infor[number].DeviceNumber = DeviceNumber;
	strcpy(g_Sid_Xssm_Dll_Infor[number].CalleeCode,callingNum);
	strcpy(g_Sid_Xssm_Dll_Infor[number].CallerCode,calledNum);
	g_Sid_Xssm_Dll_Infor[number].bBusy=TRUE;
	return FALSE;
}

/********************************************************************
函数名称：ClearSid
函数功能: 清空会话表内容
参数定义:sid：会话号
返回定义: 无
创建时间: 2010-10-29 12:05
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void ClearSid(DWORD sid)
{
	if(sid>=MESSAGE_MAX)
		return;
	g_Sid_Xssm_Dll_Infor[sid].sid=0;
	memset(g_Sid_Xssm_Dll_Infor[sid].CallerCode,0,sizeof(g_Sid_Xssm_Dll_Infor[sid].CallerCode));
	memset(g_Sid_Xssm_Dll_Infor[sid].CalleeCode,0,sizeof(g_Sid_Xssm_Dll_Infor[sid].CalleeCode));
	g_Sid_Xssm_Dll_Infor[sid].DeviceNumber=0;
	g_Sid_Xssm_Dll_Infor[sid].bBusy=FALSE;
}
/********************************************************************
函数名称：InitAllDllInfor
函数功能: 初始化所有记录会话的信息
参数定义: 无
返回定义: 无
创建时间: 2010-10-29 11:14
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL InitAllDllInfor()
{
	int i;
	for (i=0;i<MESSAGE_MAX;i++)
	{
		ClearSid((WORD)i);
	}
	return TRUE;
}
/********************************************************************
函数名称：Hdl_clearsid
函数功能: 注销会话号
参数定义: sid:会话号
返回定义: 成功：TRUE，失败：FALSE
创建时间: 2010-10-29 13:38
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_clearsid(DWORD sid)
{
	if(sid>=MESSAGE_MAX)
	{
		return FALSE;
	}
	else
	{
		ClearSid(sid);
	}
	return TRUE;
}
/********************************************************************
函数名称：Hdl_InitateRecord
函数功能: 启动录音
参数定义: pInitateRecord:录音文件具体格式
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 14:57
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_InitateRecord(stPXSSM_InitiateRecord pInitateRecord)
{

	return TRUE;
}
/********************************************************************
函数名称：Hdl_IniateCallAttempt
函数功能: 启动试呼
参数定义: pCallAttempt:呼叫内容
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 15:15
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_IniateCallAttempt(stPXSSM_InitiateCallAttempt pCallAttempt)
{

	Hdl_Connect(pCallAttempt);

	return TRUE;
}
/********************************************************************
函数名称：Hdl_SendMessage
函数功能: 向ACS系统发送消息
参数定义: 无
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2007-12-23 16:58
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_SendMessage(DWORD sid,DWORD esrParam )
{

	return TRUE;
}
/********************************************************************
函数名称：Hdl_StartDPAck
函数功能: 呼叫确认
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-2-1 15:53
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_StartDPAck(DWORD DeviceNumber,BOOL bFree)
{
	return TRUE;
}
/********************************************************************
函数名称：Hdl_StopFilePlay
函数功能: 停止放音接口
参数定义: sid: 会话号
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 14:39
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_StopFilePlay(DWORD sid)
{
	int nDsp;
	if(sid>=MESSAGE_MAX)
		return FALSE;
	nDsp=g_Sid_Xssm_Dll_Infor[sid].DeviceNumber/(256*256);
	if(nDsp>=TotalChannel)
		return FALSE;
	if(pChannelInfor[nDsp].State==CH_FREE)
		return FALSE;
	StopPlayFile((WORD)nDsp);
	return TRUE;
}
/********************************************************************
函数名称：Hdl_StopRecord
函数功能: 结束正在的录音
参数定义: sid:会话号
返回定义: 成功返回TRUE,拾芊祷谾ALSE
创建时间: 2010-10-29 15:17
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_StopRecord(DWORD sid)
{

	return TRUE;
}
/********************************************************************
函数名称：Hdl_TTSConvert
函数功能:将文本转化为语音文件
参数定义: pTTSConvert：转换内容及要求
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-10-29 15:26
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

DLLIMPORT BOOL Hdl_TTSConvert(stPXSSM_TTSConvert pTTSConvert)
{
	return TRUE;
}
/********************************************************************
函数名称：Hdl_TTSPlay
函数功能: TTS放音
参数定义: sid:会话号，pTTSFile：放音内容
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-10-29 15:28
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_TTSPlay(DWORD sid,stPXSSM_TTSPlayAnnouncement pTTSFile)
{

	return TRUE;
}
/********************************************************************
函数名称：Hdl_StopTTSPlay
函数功能: 停止TTS放音
参数定义: sid:会话号
返回定义: 成功返回TRUE,拾芊祷谾ALSE
创建时间: 2010-10-29 15:31
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_StopTTSPlay(DWORD sid)
{
	return TRUE;
}
/********************************************************************
函数名称：Hdl_Connect
函数功能: 呼叫连接到一个目的地址，完成呼叫功能
参数定义: pConnect：呼叫内容
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 15:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_Connect(stPXSSM_Connect pConnect)
{


	DWORD sid =pConnect.sid;
	int i;
	int ChNo=-1;
	DWORD Devicenumber;
	for(i=0;i<TotalChannel;i++)
	{
		if((pChannelInfor[i].nType==CHTYPE_TRUNK)&&(pChannelInfor[i].State=CH_FREE))
		{
			ChNo=i;
			break;
		}
	}
	if(ChNo==-1)
		return FALSE;
	Devicenumber =(DWORD)(ChNo*256*256);
	strcpy(pChannelInfor[ChNo].CallerID,pConnect.callingNumber);
	strcpy(pChannelInfor[ChNo].TelNum,pConnect.routeNumber);
	pChannelInfor[ChNo].datalen=0;
	memset(pChannelInfor[ChNo].data,0,sizeof(pChannelInfor[ChNo].data));
	OffHook((WORD)ChNo);
	Sig_StartDial((WORD)ChNo,pChannelInfor[ChNo].TelNum,"", 0);
	pChannelInfor[ChNo].State=CH_DIAL;
	Hdl_setsid(sid,Devicenumber,pConnect.routeNumber,pConnect.callingNumber);
	return TRUE;
}
/********************************************************************
函数名称：Hdl_ConnectToResource
函数功能: 将呼叫绑定到语音资源
参数定义: sid: 会话号
返回定义: 无
创建时间: 2010-10-29 14:42
函数作者: 刘定文
注意事项: 成功返回TRUE，失败返回FALSE	
*********************************************************************/
DLLIMPORT BOOL Hdl_ConnectToResource(DWORD sid)
{

	return TRUE;
}
/********************************************************************
函数名称：Hdl_DisconnectForwardResource
函数功能: 切断前向语音资源连接
参数定义: sid: 会话号
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 14:44
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_DisconnectForwardResource(DWORD sid)
{
	return TRUE;
}
void HWriteBLog_char1(char *pChar)
{
	SYSTEMTIME     Clock;
	FILE	    *fp=NULL;
	fp = fopen ("C:\\yfcomm\\log\\HACC_BLock.log","a");

	if ( fp == NULL )
		return ;

	GetLocalTime(&Clock);

	
		fprintf (fp, "%04d-%02d-%02d %02d:%02d:%02d:%03d have call in message,pChar is %s\n",
			Clock.wYear,Clock.wMonth, Clock.wDay,
		    Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,pChar);

	fclose (fp);
}
/********************************************************************
函数名称：Hdl_ReleaseCall
函数功能: 释放呼叫
参数定义: sid: 会话号,DeviceNumber:设备号,bUseSid:sid是否有效，TRUE有效的
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2010-10-29 14:56
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Hdl_ReleaseCall(DWORD sid,DWORD DeviceNumber,BOOL bUseSid)
{
	int nDsp,nTrunkNo;
	char pData[255];
	if(sid>=MESSAGE_MAX)
		return FALSE;
	if(!g_Sid_Xssm_Dll_Infor[sid].bBusy)
	{
		return FALSE;
	}
	if(!bUseSid)
	{
		nDsp = DeviceNumber/(256*256);
		nTrunkNo = DeviceNumber%(256*256);	
	}
	else
	{
		nDsp=g_Sid_Xssm_Dll_Infor[sid].DeviceNumber/(256*256);
		nTrunkNo = g_Sid_Xssm_Dll_Infor[sid].DeviceNumber%(256*256);
		ClearSid(sid);
	}
	memset(pData,0,sizeof(pData));
	sprintf(pData,"release call channel is %d,ntype=%d,ntotal=%d",nDsp,pChannelInfor[nDsp].nType,TotalChannel);
	
	if(nDsp>=TotalChannel)
		return FALSE;
	if(pChannelInfor==NULL)
		return FALSE;
	HWriteBLog_char1(pData);
	if(pChannelInfor[nDsp].nType==CHTYPE_TRUNK)
	{
		
		HangUp((WORD)nDsp);
		Sig_ResetCheck((WORD)nDsp);
		StartSigCheck((WORD)nDsp);
		
	}
	pChannelInfor[nDsp].Dtmf[0]=0;
	pChannelInfor[nDsp].CallerID[0]=0;
	pChannelInfor[nDsp].State = CH_FREE;
	

	return TRUE;
}
BOOL InitToneIndex()
{
	SECTION *sec;
	INI ini;
	BYTE i;
	char ToneName[32];
	char tmpSection[LENGTH_SECTION];
	sprintf(ToneName,"C:\\yfcomm\\Tone\\ToneIndex.ini");
	loadINI(&ini,ToneName);
	/*得到[TONEINDEX]*/
	sprintf(ToneName,"TONEINDEX");
	sec=getSection(&ini,ToneName);
	if(sec==NULL)
	{
		return FALSE;
	}
	{
		nToneTotal=(WORD)atoi(GetKeyList(sec,0));
	}
	pToneIndex = (TONE_INDEX_FILE *)malloc(sizeof(TONE_INDEX_FILE) * nToneTotal);
	/*得到[KeyNumber]*/
	for(i=0;i<nToneTotal;i++)
	{
		sprintf(tmpSection,"INDEX%d",i);
		sec=getSection(&ini,tmpSection);
		if(sec==NULL)
		{
			return FALSE;
		}
		{
			pToneIndex[i].index=atoi(GetKeyList(sec,0));		
			strcpy(pToneIndex[i].filename,GetKeyList(sec,1));
			
		}
	}
	/*end*/
	freeINI(&ini);
	return TRUE;
}