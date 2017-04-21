/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-12-23   17:29
文件名称: F:\江苏怡丰\源代码\AccDll\SppAccDll.h
文件路径: F:\江苏怡丰\源代码\AccDll
file base:SppAccDll
file ext: h
author:	  刘定文

purpose:	ACCDLL内部函数、变量定义	
*********************************************************************/
#ifndef _SPP_ACC_DLL_H_
#define _SPP_ACC_DLL_H_
#include "../public/comm.h"
#include "AccDll.h"
//#include "../public/pXSSM.h"
#pragma  pack(1)
//enum PLAY_FILE_TYPE{
//	PLAY_FILE_NONE=1,//无类型
//	PLAY_FILE_DIGIT,//数字
//	PLAY_FILE_CHAR,//串
//	PLAY_FILE_CURRENCY,//货币
//	PLAY_FILE_DATE,//日期
//	PLAY_FILE_TIME,//时间
//	PLAY_FILE_FILE,//文件名（带路径） 
//};//语音内容格式
//typedef struct 
//{
//	int nKey;//编号,会话号
//	BYTE nNo;//内容条目数
//}PVOICE_HEAD;//播放内容头
//typedef struct{
//	enum PLAY_FILE_FILE pType;//播放内容类型
//	char content[32];//播放内容
//}PVOICE_CONTENT;//播放内容
//
//typedef struct
//{
//	PVOICE_HEAD pVoiceHead;//播放内容头
//	PVOICE_CONTENT *pVoice_Content;//播放内容指针
//}PVOICE;//播放文件

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

typedef	 void (*pEvtdefine)(DJ_U32 esrParam);


ServerID_t		cfg_ServerID;
char			cfg_VocPath[128];
int				cfg_iDispChnl;
int				cfg_iVoiceRule;

int				cfg_iPartWork;
int				cfg_iPartWorkModuleID;

int				cfg_s32DebugOn;

// var about work
DWORD		g_acsHandle ;
BYTE		g_u8UnitID ;
pEvtdefine    pEvtHandle;
TONE_INDEX pToneIndex;//语音文件索引
TYPE_XMS_DSP_DEVICE_RES_DEMO	AllDeviceRes[MAX_DSP_MODULE_NUMBER_OF_XMS];//DSP属性记录
SID_XSSM_DLL_INFOR g_Sid_Xssm_Dll_Infor[MESSAGE_MAX];//会话记录基本属性
TYPE_XMS_DSP_EXIST pDspMap;//记录存在的DSP信息（索引表）
TYPE_CHANNEL_EXIST pTrunkMap;//中继
TYPE_CHANNEL_EXIST pUserMap;//模拟中继
TYPE_CHANNEL_EXIST pVoiceMap;//语音中继
TYPE_CHANNEL_EXIST pPcmMap;//E网
void VoiceFileHandle(DeviceID_t *pDevId,PVOICE *pVoice,int offset);
void HandleDevState(Acs_Evt_t *pAcsEvt );
BOOL InitAcsSys(ACS_ATTR pAcs_attr);
BOOL ExitSystem();
void EvtHandler(DJ_U32 esrParam);
BOOL InitAllDevice();
void ClearSid(DWORD sid);
BOOL InitAllDllInfor();
void AddDeviceRes(Acs_Dev_List_Head_t *pAcsDevList);
void AddDeviceRes_Trunk(DJ_S8 s8DspModID, Acs_Dev_List_Head_t *pAcsDevList);
void AddDeviceRes_Voice(DJ_S8 s8DspModID, Acs_Dev_List_Head_t *pAcsDevList);
void AddDeviceRes_Board(DJ_S8 s8DspModID, Acs_Dev_List_Head_t *pAcsDevList);
void AddDeviceRes_Pcm(DJ_S8 s8DspModID, Acs_Dev_List_Head_t *pAcsDevList);
void CloseBoardDevice(DeviceID_t *pBoardDevID );
void ClosePcmDevice(PCM_STRUCT *pOnePcm );
void CloseVoiceDevice(VOICE_STRUCT *pOneVoice );
void CloseTrunkDevice(TRUNK_STRUCT *pOneTrunk );
void CloseDeviceOK(DeviceID_t *pDevice );
void OpenAllDevice_Dsp(DJ_S8 s8DspModID );
void OpenTrunkDevice(TRUNK_STRUCT *pOneTrunk );
void OpenVoiceDevice(VOICE_STRUCT *pOneVoice );
void OpenPcmDevice(PCM_STRUCT *pOnePcm );
void OpenBoardDevice(DJ_S8 s8DspModID );
void ResetUser ( TRUNK_STRUCT *pOneUser );
void OpenDeviceOK(DeviceID_t *pDevice);
int	SearchOneFreeVoice(TRUNK_STRUCT *pOneTrunk, DeviceID_t *pFreeVocDeviceID );
void ResetTrunk(TRUNK_STRUCT *pOneTrunk);
void InitTrunkChannel(TRUNK_STRUCT *pOneTrunk );
void Change_State(TRUNK_STRUCT *pOneTrunk, enum TRUNK_STATE NewState );
void Change_UserState (TRUNK_STRUCT *pOneTrunk, enum USER_STATE NewState);
void Change_Voc_State(VOICE_STRUCT *pOneVoice, enum VOICE_STATE NewState );
int	 FreeOneFreeVoice(DeviceID_t *pFreeVocDeviceID );
void BuildVoiceIndex(DeviceID_t	*pVocDevID );
void InitDtmfBuf(TRUNK_STRUCT *pOneTrunk);
int StopPlayFile(DeviceID_t *pVocDevID);
void DualLinkDevice(DeviceID_t *pDev1, DeviceID_t *pDev2 );
void DualUnLinkDevice(DeviceID_t *pDev1, DeviceID_t *pDev2 );
void TrunkHandle(DJ_U32 esrParam/*TRUNK_STRUCT *pOneTrunk, Acs_Evt_t *pAcsEvt*/ );
void UserHandle(DJ_U32 esrParam/*TRUNK_STRUCT *pOneTrunk, Acs_Evt_t *pAcsEvt*/ );
int	 PlayFile(DeviceID_t	*pVocDevID, DJ_S8 *s8FileName, DJ_U8 u8PlayTag, BOOL bIsQueue );
int	 PlayIndex(DeviceID_t	*pVocDevID, DJ_U16 u16Index, DJ_U8 u8PlayTag, BOOL bIsQueue );
BOOL CheckPlayEnd(TRUNK_STRUCT *pOneTrunk, Acs_Evt_t *pAcsEvt );
void SetGtD(DeviceID_t* pDevId);
void FreeOneDeviceRes(int ID );
void FreeAllDeviceRes(void);
int RecordFile(WORD nKey,stPXSSM_InitiateRecord pInitateRecord);
int	 SearchOneFreeTrunk (DeviceID_t *pFreeTrkDeviceID);
BOOL IsTrunk(int s16DevSub);
BOOL IsDigitTrunk(int s16DevSub);
DWORD SearchSid(WORD wDsp,WORD wTrunk);

int TestPrintf(int i,int j,const char *format,...);
void RefreshDspMap(void);
BOOL ReadToneIndexFile();
#endif