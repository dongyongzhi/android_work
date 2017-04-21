#ifndef _YF_DBD_H
#define _YF_DBD_H

#include <windows.h>
#include <process.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "Commdll.h"
#include "define.h"
#include "event.h"
#include "init_viriable.h"
#include "public_define.h"
#include "DBI.h"

#define DBQUERY_THREAD_NUM  10
extern char g_sDBUser[50],g_sDBPwd[50];
extern BYTE g_DBtns[150];
extern BOOL g_bDbapTrace;
#pragma pack(1)



typedef struct
{
	UINT iConnectionNum;     //数据库连接的总个数
	BYTE sConnInfo[DBQUERY_THREAD_NUM]; //各连接的信息,每个BYTE存储一个连接的信息;
							//0:连接断开,1:连接正常;2:连接无效;... 
}DBCONNINFO;      //用于数据库连接有效性检查的消息中

typedef struct
{
	UINT mId;   //模块号
	BYTE iConnState;//数据库状态: 0:不可用; 1:可用;....
	BOOL bIsBusy;//此模块所有的数据库连接是否忙碌
}DBCONNSTATE;

#pragma pack()

BOOL DBInitial(MESSAGE*,WORD);
BOOL DBConnect(MESSAGE*,WORD);
BOOL DBAvailCheck(MESSAGE*,WORD);
BOOL DBDistrQuery(MESSAGE*,WORD);
void DBInfoMag();
void DBWriteLogFile(char* str);
void DBStateReport(MESSAGE*);
void DBTraceStatus(MESSAGE*);
//void DBAPThreadProc(LPVOID);
unsigned  __stdcall /*void*/ DBAPThreadProc(LPVOID);
unsigned  __stdcall /*void*/ DBIProc(LPVOID);
unsigned  __stdcall /*void*/ DBMonitorProc(LPVOID);

void InitThread();//初始化各个线程

#endif