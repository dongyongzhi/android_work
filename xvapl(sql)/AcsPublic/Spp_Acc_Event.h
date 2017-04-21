/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-12-13   15:33
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\AcsPublic\Spp_Acc_Event.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\AcsPublic
file base:Spp_Acc_Event
file ext: h
author:	  刘定文

purpose:	ACC结构定义
*********************************************************************/
#ifndef _XMS_DEMO_EVENT_H_
#define _XMS_DEMO_EVENT_H_
#include <windows.h>
#include "DJAcsDataDef.h"
#include "DJAcsAPIDef.h"
#include "DJAcsTUPDef.h"
#include "DJAcsISUPDef.h"
//#include "DJTest.h"
#define MAX_DSP_MODULE_NUMBER_OF_XMS 256


enum TONE_ELEMENT{
	TONE_ZERO,//0
	TONE_ONE,//1
	TONE_TWO,//2
	TONE_THREE,//3
	TONE_FOUR,//4
	TONE_FIVE,//5
	TONE_SIX,//6
	TONE_SEVEN,//7
	TONE_EIGHT,//8
	TONE_NINE,//9
	TONE_TEN,//10
	TONE_HUNDRED,//百
	TONE_THOUSAND,//千
	TONE_TENTHOUSAND,//万
	TONE_HUNFREDMILLION,//亿
	TONE_K,//k
	TONE_M,//M
	TONE_G,//G
	TONE_DOT,//点
	TONE_NEGATIVE,//负
	TONE_YUAN,//元
	TONE_JIAO,//角
	TONE_FEN,//分
	TONE_YEAR,//年
	TONE_MONTH,//月
	TONE_DAY,//日
	TONE_HOUR,//时
	TONE_MINUTE,//分
	TONE_SECOND,//秒
	TONE_YAO,//幺
};

enum TRUNK_STATE{
	TRK_WAITOPEN,//等待打开
	TRK_FREE,	//空闲
	TRK_USED,//占用
	TRK_CALLIN, //会话进入
	TRK_CALLOUT,//呼出
// 	TRK_WELCOME,
//	TRK_ACCOUNT,
//	TRK_PASSWORD,
//	TRK_SELECT,		
//	TRK_PLAYRESULT,
//	TRK_RECORDFILE,
//	TRK_PLAYRECORD,	
	TRK_HANGON,//挂机

	// new add for XMS
	TRK_WAIT_ANSWERCALL,//
	TRK_WAIT_LINKOK,
	TRK_EXDATA,//数据交换
	TRK_BUSY_SIGNAL,//忙音
	TRK_WAIT_REMOVE,
};
enum USER_STATE {
	USER_WAITOPEN,
	USER_FREE,
	USER_OFFHOOK,
	USER_CALLOUT,
	USER_LINK,
	USER_WAITHANGUP,

	USER_WAIT_REMOVE,
};
typedef struct
{
	// ----------------
//	DeviceID_t	deviceID;
//	int			iSeqID;
//	int			iModSeqID;
//	int			iLineState;
//
//	DeviceID_t	VocDevID;
//	DJ_U8		u8PlayTag;
//	enum TRUNK_STATE	State;
//
//	int		DtmfCount;
//	char	DtmfBuf[32];
//
//	char CallerCode[20];
//	char CalleeCode[20];
	DeviceID_t	deviceID;
	int			iSeqID;
	int			iModSeqID;
	int			iLineState;

	DeviceID_t	VocDevID;
	DJ_U8		u8PlayTag;
	DeviceID_t	LinkDevID;

	// -----------------
	enum TRUNK_STATE	State;

	// -----------------
	enum USER_STATE	UserState;
	int			iUserSeqID;

	int		DtmfCount;
	char	DtmfBuf[32];

	char CallerCode[20];
	char CalleeCode[20];

} TRUNK_STRUCT;//中继通道

enum VOICE_STATE {
	VOC_WAITOPEN,
	VOC_FREE,
	VOC_USED,
	VOC_WAIT_REMOVE,
} ;

typedef struct
{
	// ----------------
	DeviceID_t	deviceID;
	int			iSeqID;

	DeviceID_t	UsedDevID;

	// ----------------
	enum VOICE_STATE	State;


} VOICE_STRUCT;//语音通道

typedef struct
{
	// ----------------
	DeviceID_t	deviceID;
	int			iSeqID;
	BOOL		bOpenFlag;		// OpenDevice成功的标志

	//
	DJ_U8		u8E1Type;
	DJ_S32		s32AlarmVal;
} PCM_STRUCT;//PCM通道

enum	REMOVE_STATE
{
	DSP_REMOVE_STATE_NONE	=	0,		// 没有删除DSP硬件
	DSP_REMOVE_STATE_START	=	1,		// 准备删除DSP硬件，等待所有设备资源释放
	DSP_REMOVE_STATE_READY	=	2,		// 所有资源已经释放，可以删除DSP硬件了
};
// --------------------------------------------------------------------------------
// 定义结构：单个DSP可以提供的设备资源
typedef	struct
{
	BYTE	lFlag;				/*本DSP是否存在, 0：不存在，1：存在*/

	DeviceID_t	deviceID;		/*本DSP模块的deviceID*/
	int			iSeqID;			/*本DSP模块的顺序号*/
	BOOL		bOpenFlag;		/*OpenDevice成功的标志*/
	BOOL		bErrFlag;		/*发生过CloseDevice事件的标志*/
	enum REMOVE_STATE	RemoveState;	/*停止DSP硬件的标志*/

	WORD	lVocNum;			/*该DSP上的ACS_DEVMAIN_VOICE数量*/
	WORD	lVocOpened;			/*该DSP上OpenDevice成功的VOICE数量*/
	WORD	lVocFreeNum;		/*该DSP上可用的VOICE数量*/
	VOICE_STRUCT	*pVoice;	/*根据需要，分配相应的结构*/

	WORD	lPcmNum;			/*该DSP上的ACS_DEVMAIN_DIGITAL_PORT数量*/
	WORD	lPcmOpened;			/*该DSP上OpenDevice成功的Pcm数量*/
	PCM_STRUCT		*pPcm;		/*根据需要，分配相应的结构*/

	WORD	lTrunkNum;			/*该DSP上的ACS_DEVMAIN_INTERFACE_CH数量*/
	WORD	lTrunkOpened;		/*该DSP上OpenDevice成功的Trunk数量*/
	TRUNK_STRUCT	*pTrunk;			/*根据需要，分配相应的结构*/

} TYPE_XMS_DSP_DEVICE_RES_DEMO;

// 定义结构：从通道号，可以方便地查找到模块和通道
//           不要更改此结构，也不要将状态或者变量放入此结构中
typedef struct
{
    ModuleID_t      m_s8ModuleID;    /*device module type*/
    ChannelID_t     m_s16ChannelID;  /*device chan id*/
} TYPE_CHANNEL_MAP_TABLE;
//记录存在的DSP结构,便于检索
typedef struct{
	BYTE g_iTotalModule;//DSP个数
	BYTE pMapTable[MAX_DSP_MODULE_NUMBER_OF_XMS];//每个DSP的信息
} TYPE_XMS_DSP_EXIST;
typedef struct  
{
	WORD total;//总数
	TYPE_CHANNEL_MAP_TABLE pTable[8*MAX_DSP_MODULE_NUMBER_OF_XMS];//每个通道的属性
}TYPE_CHANNEL_EXIST;



#define		M_OneVoice(DevID)		AllDeviceRes[(DevID).m_s8ModuleID].pVoice[(DevID).m_s16ChannelID]
#define		M_OnePcm(DevID)			AllDeviceRes[(DevID).m_s8ModuleID].pPcm[(DevID).m_s16ChannelID]
#define		M_OneTrunk(DevID)		AllDeviceRes[(DevID).m_s8ModuleID].pTrunk[(DevID).m_s16ChannelID]
#endif