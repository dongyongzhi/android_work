#ifndef _SPP_SLP_H_
#define _SPP_SLP_H_



#include "comm.h"
#include "event.h"
#include "init_viriable.h"
#include "Commdll.h"
#pragma pack(1)
typedef struct
{
	char Tone_name[32];
	BYTE playtime;
}TONE_TIME_LEN;
#pragma pack()


#define SQLQUERY 1
#define SQLTERMINAL 2
#define SQLRECORD 3
#define SQLATTENCE 4
#define SQLSYNC 5

#define SQLSYNC_V11 6 //v1.1版本
#define SQL_NOTICEACK 7 //公告信息确认

TONE_TIME_LEN *pTimeLen;
void SLPThreadProc (PVOID pVoid) ;
void Deal_Message(WPARAM wParam,int iNumber);
void InitalDialog(int iNumber);
void DBQueryResultAck(int iNumber);
void DBQueryResultNak(int iNumber);
void DBQueryTerminalAck(WORD wNumber);
void DBQueryRecordAck(WORD wNumber);
void DBQueryAttenceAck(WORD wNumber);
void DBQuerySyncAck(WORD wNumber);

/************************************************************************/
                                                               
void DBQueryRecordAck_V11(WORD wNumber);
void DBQueryAttenceAck_V11(WORD wNumber);
void DBQueryResultAck_V11(int iNumber);
void DBQuerySyncAck_V11(WORD wNumber);
void InitReadyRecall(WORD wNumber);
/************************************************************************/

void FSKQueryAck(int iNumber);
void OnTimerAck(int iNumber);
void ControlPlayAnnounce(int iNumber,BYTE bEndSenssion);
void ControlSendFSKNoContent(int iNumber);
void ControlSendMessage(WORD wNumber);
void ControlEndSenssion(WORD wNumber);
void ControlOpenAndPlay(WORD wNumber);
void SendSqlQuery(int iNumber,BYTE bType);
void SendFskQuery(int iNumber);
void EndSenssion(int iNumber,BYTE reason);
void ResendMessage(int iNumber);
BOOL UpdateSlaveMessage(WORD wNumber,MESSAGE *pMesg,BOOL bDelete);
BOOL InitiateDPAck(int wNumber);
BOOL UpdateCollectInformation(WORD wNumber);
BOOL UpdateCollectInformation_Terminal(WORD wNumber);
BOOL UpdateCollectInformation_Query(WORD wNumber);
BOOL UpdateCollectInformation_Record(WORD wNumber);
BOOL UpdateCollectInformation_Attence(WORD wNumber);
BOOL UpdateCollectInformation_Sync(WORD wNumber);
BOOL UpdateCollectFskInformation(WORD wNumber);
BOOL UpdateRecallUp(WORD wNumber);
BOOL UpdateFskRecord(WORD wNumber);
BOOL ReleaseCall(WORD wNumber);
BOOL ControlReleaseCall(WORD wNumber);
BOOL PlayAnnounce(WORD wNumber,BYTE type,BYTE item);
BOOL PlayAnnounce_Endsenssion(WORD wNumber,BYTE type,BYTE item);
BOOL PlayAnnounceAndSetNocontentFSK(WORD wNumber,BYTE type,BYTE item);
BOOL SendBeginAck(WORD wNumber);
void DisconnectForwardConnection(WORD wNumber);
BOOL WaitCollectedFsk(WORD wNumber);
BOOL WaitCollectedDtmf(WORD wNumber);
BOOL WaitCollectedDtmfAndSetFormat(WORD wNumber);
BOOL UpdateSQLData(WORD wNumber);

BOOL UpdateSQLData_SYNC(WORD wNumber);

BOOL UpdateNoticeAck(WORD wNumber);
void DoWithReport(WORD wNumber);
void SetWaiteTimer(WORD wNumber,WORD timerevent,int intertime);
BOOL OpenToneDevice(WORD wNumber);
BYTE HextoBcd(int value);
BYTE BcdToHex(BYTE value);
void BYTEToAscii(BYTE *distance,BYTE value);
void WORDToAscii(BYTE *distance,WORD value);
void DWORDToAscii(BYTE *distance,DWORD value);
BOOL InitToneList();
int GetToneIndex(WORD wNumber);
void CallOutputDebugString(char *pchar);

int TransCharToByte(char *pInfor,BYTE *pData);
//BOOL Update_Message(MESSAGE *pMesg,WORD wNumber,BOOL bDelete);
//BOOL Read_Message(WORD senssionID,MESSAGE *pMsg);
#endif