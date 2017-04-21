#ifndef _TEST_DLL_H_
#define _TEST_DLL_H_
#if BUILDING_DLL
# define DLLIMPORT __declspec (dllimport)
#else /* Not BUILDING_DLL */
# define DLLIMPORT __declspec (dllexport)
#endif

#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <process.h> 
#include <time.h>
#include <../AcsPublic/DJAcsDataDef.h>
#define CALLEDNUM "744991"
#define DEVICENUM 516
#define TESTINIFILE "C:\\yfcomm\\test\\device.ini"

#pragma  pack(1)

typedef struct 
{
	DJ_S8 DspID;
	WORD wE1Num;//DIGITAL_PORT
	WORD wInterfaceNum;//INTERFACE_CH
	WORD wVoiceNum;
	WORD wFaxNum;
	WORD wVoIPNum;
	WORD wConferenceNum;
	WORD wTotalDevNum;
}DspInfo;

typedef struct 
{
	DeviceID_t Device_ID;	//m_s8ModuleID:				设备所在的DSP的ID
							//m_s16DeviceGroup:			代表设备类型--0:E1,1:接口,2:语音,3:传真,4:VOIP,5:会议组.
							//m_s16ChannelID:			本类型设备所在数组中的位置
							//m_s8MachineID:			设备所在的DSP的ID
							//m_CallID.m_s32FlowChannel:在所有设备中的数组下标.
							//m_CallID.m_s32FlowType:	绑定设备的数组下标,如无则为-1
							//m_Rfu2[1]:				绑定设备类型,无:-1, 有源绑定设备:-2, 目的设备:-3, 其他:-4.					
	INT DeviceState;				//设备状态.
	BOOL bHaveSrcBinding;			//是否有源绑定设备
	DeviceID_t SrcBindingDevID;		//源绑定设备
	BOOL bHaveDstBinding;			//是否有目的绑定设备
	DeviceID_t DstBindingDevID;		//目的绑定设备
	char TerminalNum[32];			//此设备被使用后主叫号码
	char CalledNum[32];				//被叫号码
}DeviceID;

enum DJ_MSG_TYPE
{
	MSG_TYPE_CALLIN		= 0,
	MSG_TYPE_ALERT		= 1,
	MSG_TYPE_ANSWER		= 2,
	MSG_TYPE_PLAY		= 3,
	MSG_TYPE_SENDDATA	= 4,
	MSG_TYPE_CLEARCALL	= 5,
	MSG_TYPE_CONTROLPLAY = 6,
	MSG_TYPE_PAUSE      = 7,
};

typedef struct
{
	BYTE MsgType;		//信息头
	DJ_S8 DeviceType;	//设备类型
	WORD wDataLen;//消息长度
	DJ_S8 nDspID;		//DSP的ID
	WORD wDevPos;		//设备所在数组的位置
	char TerminalNum[32];
}MsgHead;

typedef struct 
{
	MsgHead msgHead;
	char message[256];
}NetMsg;

typedef struct
{
	Acs_Evt_t Event;
	Acs_GeneralProc_Data Data;
}DJ_OPEN_STREAM;//打开东进,打开设备,

typedef struct 
{
	Acs_Evt_t Event;
	Acs_Dev_List_Head_t DevListHead;
	DeviceID_t szDev[256];
}DJ_DEV_LIST;

typedef struct 
{
	Acs_Evt_t Event;
	Acs_UniFailure_Data Data;
}DJ_OpenDev_Failed;

typedef struct 
{
	Acs_Evt_t Event;
	Acs_MediaProc_Data Data;
}DJ_Voice_Play;

typedef struct
{
	Acs_Evt_t Event;
	Acs_CallControl_Data Data;
}DJ_Link_Dev;

typedef struct 
{
	Acs_Evt_t Event;
	Acs_IO_Data Data;
	BYTE Content[256];
}DJ_SEND_DATA;
typedef struct
{
	Acs_Evt_t Event;
	Acs_ParamProc_Data Data;
}DJ_Param;

typedef struct
{
	BYTE TYPE; //信息类型
	BYTE len;//信息长度
	BYTE Index;//信息序列号
	BYTE FSK;//业务格式标志码
	//--------------------------------------------------------
	BYTE C5_T;//消息预定义类别
	BYTE C5_A;//告警类别
	BYTE C5_L;//消息长度
//	BYTE C5_L2;//消息长度2
	BYTE C5_L3;//消息长度3
	BYTE C5_C4;//终端的特定消息 
				/*	0X00 为有问题批次商品，返回终端特定的消息？？？？？？？？待查
					0X01为有问题批次商品，请求终端输入批次
					0X02为有问题批次商品，请求终端输入生产日期
					0X03为无问题批次商品，参数C4中增加有效期说明*/			
	BYTE C5_Content[64];//业务格式标志码
}DJ_ACCC5;//记录查询到的消息，无附加信息

#pragma  pack()

typedef	 void (*pEvtdefine)(DJ_U32 );


void Init();


DLLIMPORT RetCode_t  XMS_acsOpenStream(ACSHandle_t * acsHandle,
									   ServerID_t *serverID,
									   DJ_U8 u8AppID,
									   DJ_U32 u32SendQSize,
									   DJ_U32 u32RecvQSize,
									   DJ_S32 s32DebugOn,
									   PrivateData_t * privateData);

DLLIMPORT RetCode_t  XMS_acsSetESR(ACSHandle_t acsHandle,
								   EsrFunc esr,
								   DJ_U32 esrParam,
								   BOOL notifyAll);	


DLLIMPORT RetCode_t  XMS_ctsSendIOData(ACSHandle_t acsHandle,
									   DeviceID_t * deviceID,
									   DJ_U16 u16IoType,
									   DJ_U16 u16IoDataLen,
									   DJ_Void * ioData);
		

DLLIMPORT RetCode_t  XMS_ctsLinkDevice(ACSHandle_t acsHandle,
									   DeviceID_t * srcDeviceID,
									   DeviceID_t * destDeviceID,
									   PrivateData_t * privateData);


DLLIMPORT RetCode_t  XMS_ctsUnlinkDevice(ACSHandle_t acsHandle,
									 DeviceID_t * srcDeviceID,
									 DeviceID_t * destDeviceID,
									 PrivateData_t * privateData);


DLLIMPORT RetCode_t  XMS_acsGetDeviceList(ACSHandle_t acsHandle,
									  PrivateData_t * privateData);


DLLIMPORT RetCode_t XMS_ctsAlertCall(ACSHandle_t acsHandle,
									 DeviceID_t * deviceID,
									 PrivateData_t * privateData);


DLLIMPORT RetCode_t XMS_ctsAnswerCallIn(ACSHandle_t acsHandle,
										DeviceID_t * deviceID,
										PrivateData_t * privateData);


DLLIMPORT RetCode_t XMS_ctsClearCall(ACSHandle_t acsHandle, 
									 DeviceID_t * deviceID,
									 DJ_S32 s32ClearCause,
									 PrivateData_t * privateData);


DLLIMPORT RetCode_t XMS_ctsPlay(ACSHandle_t acsHandle,
							 DeviceID_t * mediaDeviceID,
							 PlayProperty_t * playProperty,
							 PrivateData_t * privateData);


DLLIMPORT RetCode_t XMS_ctsSetParam(ACSHandle_t acsHandle,
									DeviceID_t * deviceID,
									DJ_U16 u16ParamCmdType,
									DJ_U16 u16ParamDataSize,
									DJ_Void * paramData);
	
DLLIMPORT	RetCode_t  XMS_acsCloseStream(ACSHandle_t acsHandle,PrivateData_t * privateData);

DLLIMPORT	RetCode_t  XMS_ctsOpenDevice(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

DLLIMPORT	RetCode_t  XMS_ctsCloseDevice(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

DLLIMPORT	RetCode_t  XMS_ctsResetDevice(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);
	
DLLIMPORT	RetCode_t  XMS_ctsGetDevState(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);
			
DLLIMPORT	RetCode_t  XMS_ctsMakeCallOut(ACSHandle_t acsHandle,DeviceID_t * deviceID,CallNum_t * callingID,
										CallNum_t * calledID,PrivateData_t * privateData);	

DLLIMPORT	RetCode_t  XMS_ctsGetParam(ACSHandle_t acsHandle,DeviceID_t * deviceID,
									   DJ_U16 u16ParamCmdType,DJ_U16 u16ParamDataSize,DJ_Void * paramData);
	
DLLIMPORT	RetCode_t  XMS_ctsInitPlayIndex(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,PrivateData_t * privateData);

DLLIMPORT	RetCode_t  XMS_ctsBuildPlayIndex(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
											 PlayProperty_t * playProperty,PrivateData_t * privateData);

DLLIMPORT	RetCode_t  XMS_ctsControlPlay(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								ControlPlay_t * controlPlay,PrivateData_t * privateData);
	
DLLIMPORT	RetCode_t  XMS_ctsRecord(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								RecordProperty_t * recordProperty,PrivateData_t * privateData);	
DLLIMPORT	RetCode_t  XMS_ctsControlRecord(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								ControlRecord_t * controlRecord,PrivateData_t * privateData);
void OpenE1Device(DeviceID_t * deviceID);
void OpenInterfaceDevice(DeviceID_t * deviceID);
void OpenVoiceDevice(DeviceID_t * deviceID);
void OpenDevice(DeviceID_t * deviceID,int nStart,int nEnd);
void PlayThread(void * arg);
//
void FindDevice(DeviceID_t * deviceID,int * nDspNum,int * nDevNum);

WORD GetMsgBufPos();

void DealMsg_SendData(NetMsg* pMsg,int len);

void DealMsg_RecvData(NetMsg* pMsg,int len);

void  DealMsg_Play(NetMsg* pMsg,int len);

void DealMsg_CallIn(NetMsg* pMsg,int len);

void DealMsg_Answer(NetMsg* pMsg,int len);

void DealMsg_Alert(NetMsg* pMsg,int len);

void DealMsg_Connect(NetMsg* pMsg,int len);

void DealMsg_ClearCall(NetMsg* pMsg,int len);

BOOL GetFreeTrunk(int *pDspPos,int *pDevPos);

void openStream(void *arg);

BOOL judgeClearCall(NetMsg* pMsg);

BOOL GetIP();

#endif