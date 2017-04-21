#ifndef _XVAPL_DEFINE_H_
#define _XVAPL_DEFINE_H_

#include <windows.h>
#include "../public/comm.h"
#include "../public/Commdll.h"
#pragma pack(1)


#define OPERATION_EQUL 0
#define OPERATION_ADD  1
#define OPERATION_SUB  2
#define OPERATION_MUL  3
#define OPERATION_DIV  4
#define OPERATION_RESIDUAL  5
#define OPERATION_AND  6
#define OPERATION_OR   7
#define OPERATION_XOR  8
#define OPERATION_NOT  9
#define OPERATION_STRCAT 10
#define OPERATION_STRLEFT 11
#define OPERATION_STRRIGHT 12
#define OPERATION_STRLTRIM 13
#define OPERATION_STRRTRIM 14
#define OPERATION_STRTRIM  15
#define OPERATION_STRLENGT 16
#define OPERATION_STRPLACE 17
#define OPERATION_STRATOI 18
#define OPERATION_STRITOA 19
#define OPERATION_NOW     20
#define OPERATION_SECONDS 21
#define OPERATION_TIME    22
#define OPERATION_ENCRYPT 23
#define OPERATION_UNENCRYPT 24
#define OPERATION_SID       25
#define OPERATION_ADDRESS   26
#define OPERATION_MEMCPY    27
#define OPERATION_TONEID    28
#define OPERATION_ERRORNO   29
#define OPERATION_CRUURENTSIB 30
#define OPERATION_MEMST       31
#define OPERATION_SETDESBUFFER 32
#define OPERATION_SETSOURCEBUFFER 33
#define OPERATION_BUFFERMEMCPY 34
#define OPERATION_RAND 35
#define OPERATION_BCDToStr 36
#define OPERATION_HIBYTE 37
#define OPERATION_LOBYTE 38
#define OPERATION_XORVALUE 39
#define OPERATION_SUMVALUE 40
#define OPERATION_CHARTOBYTE 41
#define OPERATION_CHARTOLOWER 42
#define OPERATION_CHARTOUPPER 43
#define OPERATION_MAKEWORD  44
#define OPERATION_SETSERCERTKEY 45
#define OPERATION_MACCALCULATE 46
#define OPERATION_STRTOBCD 47
#define OPERATION_BCDTOHEX 48
#define OPERATION_BCDTODECVALUE 49
#define OPERATION_GETSERVICETYPE 51
#define OPERATION_HEXTOSTR 50
//#define OPERATION_REPLACEMEM 50

#define OPERATION_STRTOWORD 52
#define OPERATION_CRC 53
#define OPERATION_ESCAPES 54
#define OPERATION_AUTHR 55
#define OPERATION_QUERYPOS 56
#define OPERATION_ASCHEXII 57
#define OPERATION_READACCID 58

#define TIMER_SQL     100
#define TIMER_DTMF    200
#define TIMER_FSK     50
#define TIMER_VOICE   200
#define TIMER_ODI     300
#define TIMER_TCP     6000
//#define TIMER_TCP     100


typedef enum
{
    Var_BYTE,           //字节型
	Var_BOOL,          //BOOL 型
	Var_WORD,           //字型
	Var_DWORD,          //双字型
	Var_CHARSTR,       //字符串
	Var_BYTESTR,           //字节串        
}  emSIB_VarType;//变量类型



typedef enum
{
	tsibFun = 0,//运算
	tsibIf,//判断
	tsibSwitch,	//分支
	tsibCommpare,//比较
	tsibDBQuery,//数据库查询	
	tsibLog,//日志
	tsibCdr,//CDR记录
	tsibTimer,//延时
	tsibMessage,//消息
	tsibBCSM,//设置BCSM	
	tsibPlayAnnouncement,//放音
	tsibPromptCollectInformation,//放音并设置接收DTMF
	tsibPromptCollectFSK,//放音并设置接收FSK
	tsibPromptCollectInformationAndSetFormat,//设置接收DTMF格式	
	tsibSendFSK,//发送FSK
	tsibSendFSKCollectInformation,//发送FSK并接收DTMF(设置DTMF格式)
	tsibSendFSKCollectFSK,//发送FSK并接收FSK
	tsibReleaseCall,//释放呼叫
	tsibInitiateRecord,//设置录音
	tsibInitDAP,//会话开始
	tsibCallSib,//回掉SIB
	tsibReturnSib,//返回SIB
	tsibAttempCallOutSib,//外呼SIB
	tsibInitOdiSib,//会话开始(ODI)
	tsibTcpLinkSib,//TCP连接SIB
	tsibInitTcpSib,//会话开始(TCP)
	tsibTcpRecvDataSib,//TCP接收数据
	tsibTcpSendDataRecvDataSib,//TCP发送数据接收数据
	tsibDataToFixStructSib,//数据固定结构解析
}SIB_TYPE;//SIB类型列表
typedef struct 
{
	char name[16];//变量名称
	WORD type;//变量类型
	WORD length;//长度
	DWORD offset;//相对于数据区开始的偏移量
}XSCM_VARIABLE;//变量定义

typedef struct 
{
	char name[16];//常量名称
	WORD type;//变量类型,0:单字节；1：BOOL；2、双字节；3、四字节的整数；4、字符串；5：字节串
	WORD length;//长度
	DWORD offset;//相对于变量数据区开始的偏移量
}XSCM_CONSTVARIABLE;//常量定义

BYTE *pConstVariableValue;//常量值

typedef struct 
{
	DWORD len;//长度
	DWORD offset;//偏移位置(在配置文件中)
}SIB_STATISTICS;//SIB统计属性


typedef struct 
{
	WORD wTotal;//SIB总数
	SIB_STATISTICS *pSibStatistics;//每个SIB统计属性
}SIB_ALL_STATISTICS;//SIB整体统计属性

typedef struct 
{
	WORD identiNo;//标识号，用于跟踪
//	enum SIB_TYPE sibType;//SIB类型
	BYTE sibType;//SIB类型
	WORD nextstep;//下一步(下标)
	WORD Errhandle;//错误连接
	BYTE paramTotal;//参数个数
	DWORD len;//本SIB定义的串值的总长度L
}SIB_BASIC;//SIB基本属性

typedef enum
{
	Param_INT=0,//整数
	Param_CHARSTRING,//字符串
	Param_BYTESTRING,//字节串
	Param_VARIABLE,//变量
	Param_CONSTVARIALE//常量		
}PARAM_TYPE;//参数类别

typedef struct 
{
	BYTE paramNo;//参数编号
//	enum PARAM_TYPE paramType;//参数类别
	BYTE paramType;//参数类别
	DWORD paramValue;/*参数值,当参数类别＝0：表示一个整数值	＝1，2：串的偏移地址
						＝3：变量编号，在变量集定义中的编号，以0开始依次编号
						＝4：常量编号，在常量集定义中的编号，以0开始依次编号*/
}SIB_PARAM;//SIB配置属性

typedef struct 
{
	
	SIB_BASIC	sibBasic;//基本属性
	SIB_PARAM	*pParam;//配置属性
	BYTE		*pvalue;//串值
}XSCM_SIB;//SIB定义
/********************************************************************
业务基本属性
  参数定义: 无
返回定义: 无
创建时间: 2008-6-10 17:06
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

typedef struct 
{
	char ServiceLogo[16];//文件标识
	BYTE languageSelect;//0:为WINDOWS，1为UNIX
	WORD serviceKey;//业务键
	char serviceDescrible[109];//业务描述
	WORD nVariableTotal;//变量个数
	WORD nVariableLen;//变量长度
	WORD nConstVariableTotal;//常量个数
	WORD nConstVariableLen;//常量长度
	WORD nTotalSib;//SIB个数
	DWORD nSibLen;//SIB长度
	BYTE bAttr[66];//基本属性
	char author[32];//作者/设计者
	BYTE date[8];//日期
	BYTE version[8];
}XSCM_SLP_BASIC;//业务基本属性

typedef struct 
{
	BYTE bServiceNumber;//业务编号
	int  stopsibno;//停止运行的SIB编号
	char reference[32];
	BOOL bTest;//是否设置了调试
	BOOL bTestNow;//是否正在调试
	WORD senssionID;//会话号
	
}TEST_SERVICRSET;//调试属性

typedef struct 
{
	XSCM_SLP_BASIC	 sBasic;//基本信息
//	TEST_SERVICRSET  sServiceTest;//调试信息
	XSCM_CONSTVARIABLE *pConstAtte;//常量属性
	XSCM_VARIABLE    *pVariableAttr;//变量属性
	BYTE		     *pConstVaribles;//常量集
	XSCM_SIB		 *pSib;//SIB集
}XSCM_SLP;//业务流程记录




#pragma  pack()


//void XVAPL_CDR(WORD senssionID,char ServiceDesc[32],char CallingNum[32],char ServiceCode[32],const char *fmt,.....);
//void XVAPL_TRACE(const char * logname, const char *cfile, const int line,int senssionID,const char *function_name,const char *fmt,...);
//void XVAPL_WARN(const char * logname,  const char *cfile, const int line,int level,const char *fmt,...);
//void XVAPL_LOG(const char * logname,  const char *cfile, const int line,const char *fmt,...);
/********************************************************************
函数名称：XVAPL_ServiceMAN
函数功能: ServiceMan与增值业务接口
参数定义: wAttr:属性，按位定义
15	14	13	12	11	10	9	8	7	6	5	4	3	2	1	0
									写配置文件	取消跟踪	启动跟踪	卸载业务	加载业务	激活业务	停业务止

返回定义: 无
*********************************************************************/
void XVAPL_ServiceMAN(WORD  wAttr);
//作用：通知SPP与业务方面的一些操作，包括停止业务、激活业务、加载业务、卸载业务、启动跟踪、取消跟踪、写配置文件等等,在设置的时候有些互斥的位可以都为"0"，但是不能都为"1"。



/********************************************************************
函数名称：XVAPL_SetConstValue
函数功能: 设置常量值
参数定义: ServiceKey :业务键，offsetAddr：偏移位置，len：数据长度，pValue:设置值
返回定义: 无
*********************************************************************/

void XVAPL_SetConstValue(int ServiceKey,WORD offsetAddr,BYTE len ,BYTE *pValue);

//作用：对具体业务的业务流程中的一些常量值进行修改。




/********************************************************************
函数名称：XVAPL_LINKTEST
函数功能: SPP运行状态测试
参数定义: 无
返回定义: 状态正常返回TRUE，不正常返回FALSE
*********************************************************************/
BOOL  XVAPL_LINKTEST();
//作用：测试SPP运行状态，在状态正常的情况下才能进行上面的信息的设置，且ServiceMAN显示的SPP状态信息也通过此函数得到。


#endif