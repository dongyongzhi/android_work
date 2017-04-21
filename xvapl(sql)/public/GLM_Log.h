#ifndef _GLM_LOG_H_
#define _GLM_LOG_H_
#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "yftrace.h"


//#define  g_sppLogFile       "c:\\yfcomm\\log\\l_spp"
///#define  g_djLogFile       "c:\\l_DB"
//#define  g_djTraceFile     "c:\\t_DB"
extern BOOL g_bDbapTrace ;
//BOOL GLM_EnableTrace(BOOL bTrace);

BOOL     GLM_WriteFileLog(const char * logname,  const char *cfile, const int line,const char *fmt,...);

#define  GLM_WARN   GLM_WriteFileLog(g_sppLogFile,__FILE__,__LINE__,


#endif 