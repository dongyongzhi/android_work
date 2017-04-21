/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-15   22:31
文件名称: F:\江苏怡丰\源代码\public\define.h
文件路径: F:\江苏怡丰\源代码\public
file base:define
file ext: h
author:	  刘定文

purpose:	宏定义
*********************************************************************/

#ifndef _DEFINE_H_
#define _DEFINE_H_
#define  MAIN_WINDOW_TITLE   "SPPServer"



#define  NODE_MAX            256/*节点个数*/
#define  MODULE_MAX          256/*模块个数*/

#define  SYSMODULE           40/*系统模块数*/


//////////////////////////////////////////////////////////////////////////

#define evThreadMessage  ((UINT)49000) 
#define evIPCMessage     ((UINT)49001)
#define evSocketMessage  ((UINT)49002)

#define evACCMessage     ((UINT)49003)
#define evDBTest         ((UINT)49004)

#define evSerManMessage  ((UINT)49005)//XVAPL与SerMaN专用


#define STACK_SIZE 20 //堆栈大小
/*   预定义模块编号     */
#define     MID_COM       0
#define     MID_ACC       1
#define     MID_SLP       2
#define     MID_TIMER     3
#define     MID_CDR       4
#define     MID_BILL      5
#define     MID_MONITOR   6
#define     MID_SVRMAN    7
#define     MID_LOG       8
#define     MID_DBD       9
#define     MID_DBQ       10
#define     MID_TRACE     20
#define     MID_TCP       21
#define     MID_ODI       40
#define     MID_TEST      60

/*  共享内存标识  */
#define SPP_MESSAGE    "MESSAGE"
#define SPP_NODE       "NODE"
#define SPP_MODULE     "MODULE"
#define SPP_REBUFFER   "REBUFFER"
#define SPP_SENSSIONBUFFER "SENSSIONBUFFER"

#define SPP_SID   "SIDBUFFER"
#define SPP_OFFSET "OFFSETBUFFER"

#define SPP_PUBLIC     "PUBLIC"
/*  数据库  */

#define  SQL_COL_INT         0
#define  SQL_COL_CHAR        1
#define  SQL_COL_FLOAT       2
#define  SQL_COL_DOUBLE      3
#define  SQL_COL_DATE        4
#define  SQL_COL_TIME        5
#define  SQL_COL_DATETIME    6
/*  网络 */

#define IPPORT				 51200 /*网络端口号*/
#define SERVICEMANPORT       51300 /*网络端口号*/


#define INI_SPP_COMM   "COMM"
#define INI_SPP_NODE   "Node"
#define INI_SPP_SQL   "DATABASE"
#define INI_SPP_IPP    "IPP"
#define INI_SPP_IPPX   "Ipp"
#define INI_SPP_VERSION "Version"

#define INI_XSSM_SERKEY "SERVICEKEY"
#define INI_XSSM_KEYNUMBER "KeyNumber"
#define INI_XSSM_HDL   "HDL"
#define INI_XSSM_TIME  "TIMESET"
#define INI_XSSM_KEYVERSION "VERSION"

#define INI_XSSM_SERVICETONEKEY "SERVETONEKEY"
#define INI_XSSM_TONE            "TONE"

#endif