/********************************************************************
公司名称：江苏怡丰通讯有限公司重庆研发中心
创建时间: 2008-11-11   
file base:
file ext: 
author:	  刘定文
使用方法:	
*********************************************************************/
#ifndef _LOG_H_
#define _LOG_H_
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "log.h"

#define LOG

#define  g_TimerLogFile     "c:\\yfcomm\\log\\l_Timer"
#define  g_setTimerLogFile     "c:\\yfcomm\\log\\l_setTimer"
#define  g_TimerAlarmFile     "c:\\yfcomm\\log\\a_Timer"


BOOL     WriteFileLog_1(const char * logname,  const char *cfile, const int line,const char *fmt,...);

#define  LOG_SETTIMER   WriteFileLog_1(g_setTimerLogFile,__FILE__,__LINE__,
#define  LOG_RUNTIMER   WriteFileLog_1(g_TimerLogFile,__FILE__,__LINE__,
#define  ALARM_TIMER   WriteFileLog_1(g_TimerAlarmFile,__FILE__,__LINE__,

#endif