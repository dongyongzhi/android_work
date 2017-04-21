#ifndef _YF_DBD_H
#define _YF_DBD_H

#define DBNTWIN32
#include <windows.h>
#include <process.h>
#include <sqlfront.h>
#include <sqldb.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include "Commdll.h"
#include "event.h"
#include "init_viriable.h"
#include "Alarmdll.h"

/****************  数据库访问事件  **********************/
//#define evInitial               WM_USER
//#define evDataBase              (WORD)(evInitial  + 200)
//#define evDBDInital				(WORD)(evDataBase + 1)/*:数据库初始化DBD*/
//#define evAckDBConnectionTest   (WORD)(evDataBase + 2)/*: 数据库连接状态测试结果DBD*/
//#define evDBQueryAsk			(WORD)(evDataBase + 3)/*:数据库查询请求DBD*/
//
//#define evDBConnectInital		(WORD)(evDataBase + 4)/*:数据库连接初始化DBQ*/
//#define evDBConnectionTest		(WORD)(evDataBase + 5)/*：数据库连接状态测试DBQ*/
//#define evDBExeAsk				(WORD)(evDataBase + 6)/*:数据库查询执行DBQ*/
//#define evDBDisconnection		(WORD)(evDataBase + 7)/*:数据库连接断开DBQ*/

#define DBQ_CON_NUM			10          //数据库连接线程数
#define TEST_TIME_INTERVAL  1500		//DBQ线程检测连接状态的时间间隔(ms)
#define DBQ_OPEN_TIMEOUT    5           //新建一个数据库连接的超时等待时间(s)
#define DBQ_SQL_TIMEOUT		2			//SQL语句的查询等待超时时间(s)
#define DBTEST_PAUSE_TIME	5000		//DBTEST 线程第一次启动时要等待的时间(等所有DBQ启动完)

#pragma pack(1)
typedef struct 
{
	UINT		mId;               // for DBQ,DBT
	PDBPROCESS	pDBConName;			//数据库连接名  for DBQ,DBT
	BOOL		bBusy;              //DBQ查询是否忙碌 for DBQ,DBT
	BOOL		bConState;			//数据库状态
}DBQTHREAD;
#pragma pack()

//void GetIni(void);
/*unsigned  __stdcall*/void DBDThreadProc(LPVOID lpParameter);
BOOL SetMsg(MESSAGE * ,TID *sender,TID * recv,UINT msg);
extern void InitDBQ(DBQTHREAD *);
#endif