#ifndef _PXSSM_GLOBAL_H_
#define _PXSSM_GLOBAL_H_

/*  pXSSM  事件定义     */
/************************************************************************/
/* XVAPL<->pXSSM.EXE之间事件定义                                        */
/************************************************************************/
#define evPXSSM_BASE							4000   //该数值由XVAPL统一指定。
#define	evPXSSM_ActivityTest	      			evPXSSM_BASE +	1//连接状态测试
#define	evPXSSM_ActivityTestResponse			evPXSSM_BASE +	2//连接状态测试确认
#define	evPXSSM_ErrorReport					    evPXSSM_BASE +	3//错误报告
#define	evPXSSM_InitiateDP						evPXSSM_BASE +	10/*开启会话*/
#define	evPXSSM_ConnectToResource			    evPXSSM_BASE +	11/*绑定语音资源*/
#define	evPXSSM_DisconnectForwardConnection		evPXSSM_BASE +	12/*取消语音资源绑定*/
#define	evPXSSM_ReleaseCall					    evPXSSM_BASE +	13//释放会话
#define	evPXSSM_PlayAnnouncement				evPXSSM_BASE +	20//放音
#define	evPXSSM_PromptCollectInformation		evPXSSM_BASE +	21//放音并设置接收DTMF格式
#define evPXSSM_PromptCollectInformationAndSetFormat evPXSSM_BASE +	22//设置接收DTMF的指令
#define	evPXSSM_CollectedInformation			evPXSSM_BASE +	23//收集的DTMF用户信息
#define	evPXSSM_ResourceReport					evPXSSM_BASE +	24//资源报告
#define evPXSSM_PromptCollectFSK        		evPXSSM_BASE +	25//放音并设置接收FSK
#define	evPXSSM_SendFSK						    evPXSSM_BASE +	30//发送FSK
#define	evPXSSM_SendFSKCollectInformation		evPXSSM_BASE +	31//发送FSK并设置接收DTMF
#define	evPXSSM_SendFSKCollectFSK			    evPXSSM_BASE +	32//发送FSK并设置接收FSK
#define	evPXSSM_CollectedFSKInformation			evPXSSM_BASE +	33//收集的FSK信息
#define	evPXSSM_InitiateRecord					evPXSSM_BASE +	40//要求录音
#define	evPXSSM_StopRecord					    evPXSSM_BASE +	41//停止录音
#define	evPXSSM_RequestReportBCSMEvent			evPXSSM_BASE +	50//设置BCSM事件
#define	evPXSSM_EventReportBCSM				    evPXSSM_BASE +	51//BCSM事件确认
#define	evPXSSM_Connect						    evPXSSM_BASE +	52//外呼处理
#define	evPXSSM_InitiateCallAttempt				evPXSSM_BASE +	53//尝试外呼
#define	evPXSSM_Continue						evPXSSM_BASE +	54
#define	evPXSSM_TTSConvert					    evPXSSM_BASE +	60//TTS文件格式转换
#define	evPXSSM_TTSPlay						    evPXSSM_BASE +	61//TTS文件播放
/************************************************************************/
/* pXSSM.EXE与pXSSM.DLL之间事件定义                                     */
/************************************************************************/
#define evXSSM_Base				5000
#define evtXSSM_LinkStatus		evXSSM_Base+1//与硬件设备连接正常
#define evtXSSM_FailStatus		evXSSM_Base+2//与硬件设备连接异常
#define evtXSSM_CallOut			evXSSM_Base+3//呼出
#define evtXSSM_CallIn			evXSSM_Base+4//呼入
#define evtXSSM_AlertCall		evXSSM_Base+5
#define evtXSSM_AnswerCall		evXSSM_Base+6
#define evtXSSM_LinkDevice		evXSSM_Base+7//单向设备连接
#define evtXSSM_UnLinkDevice	evXSSM_Base+8//单向解除设备连接
#define evtXSSM_ClearCall		evXSSM_Base+9//挂机
#define evtXSSM_JoinConf		evXSSM_Base+10//加入会议
#define evtXSSM_LeaveFromConf	evXSSM_Base+11//离开会议
#define evtXSSM_ClearConf		evXSSM_Base+12//清除会议完成
#define evtXSSM_Play			evXSSM_Base+13//放音完成
#define evtXSSM_ControlPlay		evXSSM_Base+14//控制放音操作完成
#define evtXSSM_Record			evXSSM_Base+15//控制录音操作完成
#define evtXSSM_ControlRecord	evXSSM_Base+16//控制录音操作完成
#define evtXSSM_SendFax			evXSSM_Base+17//发送传真
#define evtXSSM_RecvFax			evXSSM_Base+18//接收传真
#define evtXSSM_SendIoData		evXSSM_Base+19//发送数据
#define evtXSSM_RecvIoData		evXSSM_Base+20//接收数据


//////////////////////////////////////////////////////////////////////////
/************************************************************************/
/* tACC与xvapl之间事件定义                                              */
/************************************************************************/
#define evTcp_Base								6000
#define evTcp_InitTCP							evTcp_Base+1///*TCP开启会话*/
#define evTcp_ReceiveData						evTcp_Base+2//接收到的数据
#define evTcp_SendDataAndReceiveData			evTcp_Base+3//发送数据并接收数据




//////////////////////////////////////////////////////////////////////////
/************************************************************************/
/* checkcase与xvapl之间事件定义                                              */
/************************************************************************/
#define evTest_Base          6500
#define evTest_BeginControl  evTest_Base+1//启动调试
#define evTest_BeginControlAck  evTest_Base+2 //启动调试确认

#define evTest_GetTestSenssion  evTest_Base +3//获取当前测试的会话号
#define evTest_GetTestSenssionAck  evTest_Base +4//返回当前测试的会话号


#define evTest_EndControl  evTest_Base+5//调试结束
#define evTest_ReadValue  evTest_Base+6 //读常量值、变量值
#define evTest_ReadValueAck  evTest_Base+7 //读常量值、变量值
#define evTest_SetValue   evTest_Base+8//设置常量、变量值
#define evTest_SetValueAck   evTest_Base+9//设置常量、变量值确认
#define evTest_CurrentSibNo   evTest_Base+10//获取当前执行的SIB编号请求
#define evTest_CurrentSibNoAck  evTest_Base+11 //获取当前执行的SIB编号确认
#define evTest_SibControl  evTest_Base+12 //控制SIB操作


#pragma  pack(1)
typedef  char    PSTNNumber[32];       

typedef char       DateTime[10];
/*    错误报告     */
typedef enum
{
    Cancelled=1,              // 被取消
    CancelFailed,           //取消失败 
    ImproperCallerResponse, //错误的用户响应
    TimeOut,                // 超时
    MissingSession,         // 找不到对应的会话
	MissingParameter,        //丢失参数 
	ParameterOutOfRange,   //参数超出范围
    SystemFailure,          //系统错误,
    TaskRefused,           //任务被拒绝，例如状态不正确,
    UnavailableResource,    //资源不可用,
    UnexpectedParameter,    // 参数不正确,
	UnknownResource,       // 不可知的资源
    OtherError               // 其他错误
}  emPXSSM_ErrorType;

typedef struct  
{	
		DWORD               sid;           //  会话号
		enum emPXSSM_ErrorType   error;         //  错误代码
		BYTE                level;         //  错误级别
		DWORD               rev;            //  保留参数
} stPXSSM_ErrorReport;

typedef struct
{
		DWORD  sid		           ;  //  会话号
		WORD   serviceKey        ;  //业务键
		CHAR   IP[16];//IP地址
        CHAR   MAC[20];        //Mac地址
} stTCP_InitTcp;//TCP接入
typedef struct
{   
	WORD      timeOut;    //等待时间
    WORD      datalen;      //请求接收数据长度
}  stTCP_RecvData_Ask;//TCP请求接收数据

typedef struct
{   
    DWORD    sid;   //会话号
    WORD     datalen;      //数据长度
 	BYTE      data[CHAR_NUMBER-6];   //数据内容
}  stTCP_RecvData;//TCP接收数据
typedef struct
{   
    DWORD    sid;   //会话号
	WORD     timeOut;    //等待时间
    WORD     senddatalen;      //send数据长度
	WORD     recvdatalen;   //接收数据长度
 	BYTE     data[CHAR_NUMBER-10];   //数据内容
	
}  stTCP_SendDataRecvData;//TCP接收数据
typedef struct
{
		WORD   id		           ;  //  编号
		WORD   servicetype        ;  //业务类型
		WORD   serviceKey        ;  //业务键
        BYTE   content[1000];        //内容
} stODI_InitODI;//ODI接入
typedef struct
{
		WORD   id		           ;  //  编号
		BYTE   state        ;  //状态
		WORD   sid        ;  //会话号
} stODI_InitODI_Ack;//ODI接入确认


typedef struct
{
		DWORD  sid		           ;  //  会话号
		WORD   serviceKey        ;  //业务键
		PSTNNumber   dialedNumber  ;  //所拨数字,
		PSTNNumber   calledNumber  ;  //原始被叫用户号码，用于呼叫转移等业务记录初始的号码，否则与dialedNumber相同
		PSTNNumber   callingNumber ;  //主叫用户号码
}   stPXSSM_InitiateDP;//PSTN接入

typedef struct
{
		DWORD   sid        ; //会话号
		DWORD   resouceID  ; //资源号，暂时保留
}   stPXSSM_ConnectToResource;

typedef enum 
{	
  BCSM_RouteFail  = 0  ,  //  路由选择故障
  BCSM_ORelease   = 1  ,  //  O-拆线
  BCSM_OAbandon   = 2  ,  //  O-放弃
  BCSM_Busy       = 3  ,  //  被叫忙
  BCSM_NoResponse = 4  ,  //  无应答
  BCSM_Answer     = 5  ,  //  应答
  BCSM_TRelease   = 6  ,  //  T-拆线
  BCSM_TAbandon   = 7     //  T-放弃
} emPXSSM_BCSM;

typedef enum
{
  RouteFail  = 0  ,  //  路由错误
  ORelease   = 1  ,  //  O-拆线
  OAbandon   = 2  ,  //  O-放弃
  Busy       = 3  ,  //  被叫忙
  NoResponse = 4  ,  //  无应答
  Answer     = 5  ,  //  应答
  TRelease   = 6  ,  //  T-拆线
//	enum emPXSSM_BCSM pbasic;
 TAbandon   = 7  ,  //  T-放弃
  FatalFail  = 10 ,  //  致命错误
  DeviceLoss = 11 ,  //  设备连接丢失、设备不存在
  LogicalEnd = 12 ,  //  用户要求，正常的逻辑结束
  UnknowReason =20   //  未知
}emPXSSM_ReasonType;


typedef struct
{
	DWORD                     sid	    ;  //会话号
	enum emPXSSM_ReasonType        reason  ;  //释放原因
} stPXSSM_ReleaseCall;
union choiceValue
{
     DWORD       value;      //数值
     char        number[16]; //数字串
     int         price;      //金额，单位为分的整数，可以为负
     char        date[11];   //日期，格式为YYYY-MM-DD
     char        time[9];    //事件，格式为HH:MM:SS
};
typedef struct
{   
	union choiceValue value;
    BYTE    choiceType;          //2：数值 3：数字串  4：金额5：日期  6：时间
}stPXSSM_ToneParameter;   // 变音参数

typedef struct
{  
    DWORD                     toneID; //语音条目号
    
    BYTE                     number;  //参数总数
	stPXSSM_ToneParameter    parameter[4];
}stPXSSM_VariableTone;   // 带参数的可变音
union choiceVariable
{
    DWORD                 toneID;  //语音条目号
    stPXSSM_VariableTone  variableTone;  
} ;
typedef struct
{   
    union choiceVariable    vchoiceVariable;//语音内容
    BYTE                     variable;   // 0 固定音，1：可变音
}stPXSSM_Tone;    // 语音


typedef struct
{   
    DWORD            sid;   //会话号
    stPXSSM_Tone     tone;  //语音
    BYTE             repeatTime; //重复次数
    WORD             duration;  //最大持续时间，0表示无限制
    WORD             interval; // 重复之间的间隔时间，仅对repeatTime>0有效
    BOOL             canInterupt  ; //用户按任意键是否可以打断
} stPXSSM_PlayAnnouncement;

typedef struct
{ 
    BYTE    minDigits;   /* 最小收集的数字数 */
    BYTE    maxDigits;   /* 最多搜集的数字  */
    BYTE    endDigit;    /* 结束字符 */
    BYTE    cancelDigits;       /* 取消字符 */
    BYTE    startDigit;         /* 开始字符 */
    BYTE    firstDigitTimeOut;  /* 首位超时  */
    BYTE    interDigitTimeOut;  /* 位间超时  */
	stPXSSM_PlayAnnouncement  playTone;  // 播放的语音
}   stPXSSM_PromptCollectInformation;
typedef struct
{ 
    DWORD            sid;   //会话号
    BYTE    minDigits;   /* 最小收集的数字数 */
    BYTE    maxDigits;   /* 最多搜集的数字  */
    BYTE    endDigit;    /* 结束字符 */
    BYTE    cancelDigits;       /* 取消字符 */
    BYTE    startDigit;         /* 开始字符 */
    BYTE    firstDigitTimeOut;  /* 首位超时  */
    BYTE    interDigitTimeOut;  /* 位间超时  */
}   stPXSSM_PromptCollectInformationAndSetFormat;//设置接收DTMF格式

typedef struct
{ 
    
    BOOL Fskenable;//是否接收FSK
	stPXSSM_PlayAnnouncement  playTone;  // 播放的语音
}stPXSSM_PromptCollectFSK;//放音完毕接收FSK

typedef struct
{ 
    DWORD            sid;        //会话号
    char    collectedDigits[64]; //收集到的数字串 */
}   stPXSSM_CollectedInformation;

typedef struct
{
    WORD     length;        //消息长度
	BYTE     message[512];  //二进制的FSK序列
}   stFSK;

typedef struct
{   
    DWORD    sid;   //会话号
    WORD     duration;      //最大持续时间，0表示无限制
 	stFSK    fsk;   //fsk消息
}  stPXSSM_SendFSK;

typedef struct
{ 
    
    BYTE    minDigits;   /* 最小收集的数字数 */
    BYTE    maxDigits;   /* 最多搜集的数字  */
    BYTE    endDigit;    /* 结束字符 */
    BYTE    cancelDigits;       /* 取消字符 */
    BYTE    startDigit;         /* 开始字符 */
    BYTE    firstDigitTimeOut;  /* 首位超时  */
    BYTE    interDigitTimeOut;  /* 位间超时  */
	stPXSSM_SendFSK  playFSK;  // 播放的FSK
}   stPXSSM_SendFSKCollectInformation;


typedef struct
{ 
    BYTE    timeOut;  /* 超时  */
	stPXSSM_SendFSK  playFSK;  // 播放的FSK
    
}   stPXSSM_SendFSKCollectFSK;

typedef struct
{   
    DWORD    sid;   //会话号
    stFSK    fsk;   //收到的fsk消息
}  stPXSSM_CollectedFSKInformation;


/************************************************************************/
/*                 资源报告                                             */
/************************************************************************/
typedef enum
{
  R_PlayAnnouncement  = 1  ,  // 放音完毕
  R_SendFsk   = 2  ,  //  发送数据
  R_InitiateRecord   = 3  ,  //  录音或者控制音完成
  R_TTSPlay       = 4,    //  TTS转换完毕
  R_TcpSend       =5  //TCP发送数据
}emPXSSM_ResourceReportReason;
typedef struct
{   
    DWORD    sid;   //会话号
    enum emPXSSM_ResourceReportReason     reason;   //资源报告类型
}  stPXSSM_ResourceReport;//资源报告


typedef struct
{   
    DWORD   sid;           //会话号
    DWORD   fileIndex;     //录音文件编号
    WORD    duration;      //最长录音时间，0表示无限制
    BOOL    canInterupt  ; //用户按任意键是否可以打断
    BOOL    replaceExistedFile ; //是否覆盖存在的文件
}  stPXSSM_InitiateRecord;


/*   BCSM事件定义                     */

typedef struct
{   
    DWORD         sid;           //会话号
    WORD          eventNumber;   //注册的事件总数，小于等于8
    enum emPXSSM_BCSM  events[8];     //注册的事件
}  stPXSSM_RequestReportBCSMEvent;

typedef struct
{   
    DWORD           sid;      //会话号
    enum emPXSSM_BCSM    event;    //BCSM事件 
}  stPXSSM_EventReportBCSM;


typedef struct
{
	DWORD           sid;      //会话号
	PSTNNumber   routeNumber   ;  //路由数字
	PSTNNumber   callingNumber ;  //主叫用户号码
	BOOL         needNullCDR; /*是否上报0话单*/
	WORD         timeOut;//超时等待时间
}stPXSSM_Connect,stPXSSM_InitiateCallAttempt;

//typedef  struct
//{
//	stPXSSM_Connect  
//} stPXSSM_InitiateCallAttempt;

typedef struct
{
	DWORD		 sid;  //会话号
	PSTNNumber   callingNumber;  //主叫用户号码
	PSTNNumber   calledNumber;  //被叫号码
	DateTime     connectTime;      //开始接续时间
	DateTime     answerTime;       //应答时间
	DWORD        timeOut;         //呼叫时长
}stPXSSM_CDRReport;

typedef struct
{
	DWORD   sid		 ;  //会话号
	char    text[256];  //文本
    DWORD   fileIndex;     //录音文件编号
    BOOL    replaceExistedFile; //是否覆盖存在的文件
}   stPXSSM_TTSConvert;

typedef struct
{   
    DWORD            sid;        //会话号
    char             text[256];  //语音
    BYTE             repeatTime; //重复次数
    WORD             duration;   //最大持续时间，0表示无限制
    WORD             interval;   // 重复之间的间隔时间，仅对repeatTime>0有效
    BOOL             canInterupt  ; //用户按任意键是否可以打断
}  stPXSSM_TTSPlayAnnouncement;

typedef struct 
{
	DWORD sid;//会话号
	DWORD DeviceNumber;//编号属性
	char CallingNum[32];
	char CalledNum[32];
	int event;//事件
	int datalen;//数据长度
	BYTE ioData[512];//数据内容
//	void *ioData; //数据内容
}ACS_Hdl_EVENT;
union WATTR{
		WORD attr;
		struct
		{
			WORD debug : 1;
			WORD channel : 15;
		};
		
};
typedef struct 
{
	char ServerIP[32];
	WORD port;
	char passwd[32];
	char username[32];
	int UintId;
	union WATTR attrd;
}ACS_ATTR;//接口基本属性
typedef struct 
{
	int sysTimeout;
	int hdlTimeout;
}XSSM_TIME;//定时默认设置

/************************************************************************/
/* 语音转换格式定义                                                     */
/************************************************************************/
enum PLAY_FILE_TYPE{
	PLAY_FILE_TONE_ELMENT,//无类型
	PLAY_FILE_FILE,//文件名（带路径）
	PLAY_FILE_DIGIT,//数字
	PLAY_FILE_CHAR,//串
	PLAY_FILE_CURRENCY,//货币
	PLAY_FILE_DATE,//日期
	PLAY_FILE_TIME,//时间
	 
};//语音内容格式
typedef struct 
{
	int nKey;//编号,会话号
	BYTE nNo;//内容条目数
	
}PVOICE_HEAD;//播放内容头
typedef struct{
	enum PLAY_FILE_FILE pType;//播放内容类型
	BYTE language;//语言
	char content[32];//播放内容
}PVOICE_CONTENT;//播放内容

typedef struct
{
	PVOICE_HEAD pVoiceHead;//播放内容头
	PVOICE_CONTENT *pVoice_Content;//播放内容指针
}PVOICE;//播放文件



enum CALL_OUT_TYPE{
CALLOUT_SUCCESS=1,//外呼成功
CALLOUT_FAILED,//外呼失败
FIND_VOICE_FAILED,//找不到空闲的语音中继
FIND_E_FAILED,//找不到空闲的数字中继
};//外呼返回结果
typedef struct{
	WORD sid;
	enum CALL_OUT_TYPE pType;
}stPXSSM_CallOutInfor;//外呼消息确认


#pragma  pack()

#endif