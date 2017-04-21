/********************************************************************
	created:	2010/10/27
	created:	27:10:2010   15:13
	filename: 	E:\苏州卡易付系统\卡易付最新\xvapl源代码\Acc_BDll\Acc_BDll.h
	file path:	E:\苏州卡易付系统\卡易付最新\xvapl源代码\Acc_BDll
	file base:	Acc_BDll
	file ext:	h
	author:		
	
	purpose:	
*********************************************************************/


#ifndef _ACC_B_DLL_H_
#define _ACC_B_DLL_H_
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