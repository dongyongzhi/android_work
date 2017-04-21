/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-12-23   16:19
文件名称: F:\江苏怡丰\源代码\HdlDll\HdlDll.h
文件路径: F:\江苏怡丰\源代码\HdlDll
file base:HdlDll
file ext: h
author:	  刘定文

purpose:	HdlDLL函数定义
*********************************************************************/

#ifndef _pXSSM_DLL_H_
#define _pXSSM_DLL_H_
#include "pXSSM_global.h"

#if BUILDING_DLL
# define DLLIMPORT __declspec (dllimport)
#else /* Not BUILDING_DLL */
# define DLLIMPORT __declspec (dllexport)
#endif /* Not BUILDING_DLL */

DLLIMPORT void Hdl_Init(ACS_ATTR attr);

DLLIMPORT BOOL Hdl_SetRecall(void *esr);

DLLIMPORT BOOL Hdl_StartDPAck(DWORD DeviceNumber,BOOL bFree);

DLLIMPORT BOOL Hdl_setsid(DWORD sid,DWORD DeviceNumber,char calledNum[32],char callingNum[32]);

DLLIMPORT BOOL Hdl_clearsid(DWORD sid);

DLLIMPORT BOOL Hdl_SendMessage(DWORD sid,DWORD esrParam );

DLLIMPORT BOOL Hdl_SendFSK(DWORD sid,/*BYTE *data,WORD len*/void *infor);

DLLIMPORT BOOL Hdl_FilePlay (DWORD sid, void * VoiceAddr/*PVOICE *pVoiceContent*/);

DLLIMPORT BOOL Hdl_StopFilePlay(DWORD sid);

DLLIMPORT BOOL Hdl_ConnectToResource(DWORD sid);

DLLIMPORT BOOL Hdl_DisconnectForwardResource(DWORD sid);

DLLIMPORT BOOL Hdl_ReleaseCall(DWORD sid,DWORD DeviceNumber,BOOL bUseSid);

DLLIMPORT BOOL Hdl_InitateRecord(stPXSSM_InitiateRecord pInitateRecord);

DLLIMPORT BOOL Hdl_Connect(stPXSSM_Connect pConnect);

DLLIMPORT BOOL Hdl_IniateCallAttempt(stPXSSM_InitiateCallAttempt pCallAttempt);

DLLIMPORT BOOL Hdl_StopRecord(DWORD sid);

DLLIMPORT BOOL Hdl_TTSConvert(stPXSSM_TTSConvert pTTSConvert);

DLLIMPORT BOOL Hdl_TTSPlay(DWORD sid,stPXSSM_TTSPlayAnnouncement pTTSFile);

DLLIMPORT BOOL Hdl_StopTTSPlay(DWORD sid);

DLLIMPORT BOOL Hdl_Exit();

DLLIMPORT void Hdl_SetTrace(BOOL value);


#endif