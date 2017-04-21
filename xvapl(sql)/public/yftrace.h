/********************************************************************
公司名称：江苏怡丰通讯有限公司重庆研发中心
创建时间: 2008-4-12   21:49
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public\yftrace.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public
file base:yftrace
file ext: h
author:	  刘定文

purpose:跟踪事件具体处理	
*********************************************************************/
#ifndef _TRACE_H_
#define _TRACE_H_
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "yftrace.h"

BOOL g_Trace;//是否跟踪
BOOL g_Trace_Spp;//是否跟踪
BOOL g_Trace_Slp;//是否跟踪
extern BOOL g_bDbapTrace ;//是否跟踪DBAP模块.

#define  g_sppLogFile       "c:\\yfcomm\\log\\l_spp"
#define  g_sppTraceFile     "c:\\yfcomm\\log\\t_xvapl"
#define  g_smmLogFile      "c:\\yfcomm\\log\\l_smm"
#define  g_smmTraceFile     "c:\\yfcomm\\log\\t_smm"
#define  g_accTraceFile     "c:\\yfcomm\\log\\t_acc"
#define  g_commLogFile      "c:\\yfcomm\\log\\l_comm"
#define  g_commTraceFile     "c:\\yfcomm\\log\\t_comm"
#define  g_timerLogFile      "c:\\yfcomm\\log\\l_timer"
#define  g_timerTraceFile     "c:\\yfcomm\\log\\t_timer"
     
#define  TRACE_INI        "trace.ini"
BOOL     WriteFileLog_Trace(const char * logname, const char *cfile, const int line,const char *fmt,...);
BOOL     WriteFileLog_WARN(const char * logname, const char *cfile, const int line,const char *fmt,...);

BOOL     WriteFileLog(const char * logname,  const char *cfile, const int line,const char *fmt,...);
void     WriteErrorInf(const char * logname, const char *cfile, const int line);
void     WriteTraceInf(const char * logname, const char *cfile, const int line,int sid,const char *function_name,const char *fmt,...);
BOOL     EnableTrace(BOOL bTrace);
BOOL     EnableTrace_Spp(BOOL bTrace);
BOOL     EnableTrace_Slp(BOOL bTrace);
#define  YF_WARN     WriteFileLog_WARN(g_sppLogFile,__FILE__,__LINE__,
#define  YF_LOG_SPP     WriteFileLog(g_sppLogFile,__FILE__,__LINE__,
#define  YF_LOG_SMM     WriteFileLog(g_smmLogFile,__FILE__,__LINE__,
#define  YF_LOG_COMM     WriteFileLog(g_commLogFile,__FILE__,__LINE__,
#define  YF_LOG_TIMER     WriteFileLog(g_timerLogFile,__FILE__,__LINE__,

#define  YF_TRACE   if (g_Trace) WriteTraceInf(g_sppTraceFile,__FILE__,__LINE__,
#define  YF_TRACE_SPP   if (g_Trace_Spp) WriteTraceInf(g_sppTraceFile,__FILE__,__LINE__,
#define  YF_TRACE_SLP   if (g_Trace_Slp) WriteTraceInf(g_sppTraceFile,__FILE__,__LINE__,
#define  YF_TRACE_DBAP   if (g_bDbapTrace) WriteTraceInf(g_sppTraceFile,__FILE__,__LINE__,
//#define  YF_TRACE_SMM   if (g_Trace) WriteTraceInf(g_smmTraceFile,__FILE__,__LINE__,
//#define  YF_TRACE_ACC   if (g_Trace) WriteTraceInf(g_accTraceFile,__FILE__,__LINE__,
//#define  YF_TRACE_COMM   if (g_Trace) WriteTraceInf(g_commTraceFile,__FILE__,__LINE__,
//#define  YF_TRACE_TIMER   if (g_Trace) WriteTraceInf(g_timerTraceFile,__FILE__,__LINE__,


#define  YF_ERROR_SPP   WriteErrorInf(g_sppLogFile ,__FILE__,__LINE__)
#define  YF_ERROR_SMM   WriteErrorInf(g_smmLogFile ,__FILE__,__LINE__)

#endif