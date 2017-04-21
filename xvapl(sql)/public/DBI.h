#ifndef _YF_DBI_H
#define _YF_DBI_H

#define DBNTWIN32

#include <windows.h>
#include <stdio.h>
#include <sqlfront.h>
#include <sqldb.h>
#include "event.h"
#include "init_viriable.h"
#include "Commdll.h"
#include "DBAP.h"

#define DBUSER "yfxvapluser"
#define DBPWD "yfxvaplpwd"
#define DBQ_OPEN_TIMEOUT    5           //新建一个数据库连接的超时等待时间(s)
#define DBQ_SQL_TIMEOUT		5			//SQL语句的查询等待超时时间(s)
#define DBINIFILEPATH "C:\\yfcomm\\ini\\SPP.ini"
#define DBINISQL_SQLSERVER "SELECT [user],[password] FROM [master].[dbo].[yfcommini]"
#define DBCONN_TEST_SQL "select 1"
//#define DBCONN_TEST_SQL "select * from a"

#pragma pack(1)

typedef struct
{
	UINT mId;   //模块号
	PDBPROCESS	pDBConn;//数据库连接名 
}DBI_CONN;

#pragma pack()

int errid;
BOOL dbi_InitialDB();//初始化数据库;

BOOL dbi_InitialConn(UINT mId);//初始化数据库连接

BOOL dbi_CreateConn(UINT mId);//建立连接(单个连接)

BOOL dbi_ReleaseConn(UINT mid);//释放连接(单个连接)

BOOL dbi_ConnAvailCheck(UINT mId);//检测连接是否可用

BOOL dbi_ExecSql(UINT mId, MESSAGE* msg,WORD);//sql执行体(连接/模块号,SQL语句) 

void dbi_DBExit();

void dbi_WriteLogFile(char* str);

BOOL TransformIP(char *addr,DWORD *dwIP,BOOL flag);
int err_handler(PDBPROCESS, INT, INT, INT, LPCSTR, LPCSTR);
int msg_handler(PDBPROCESS, DBINT, INT, INT, LPCSTR, LPCSTR,LPCSTR, DBUSMALLINT);
BOOL dbi_TestExeSql(UINT mId);

void dbi_MsgAck_NoConn(UINT mId, MESSAGE* pyfMsg,WORD wNum);//数据库无连接
void dbi_MsgAck_ExecFailed(UINT mId, MESSAGE* pyfMsg,WORD wNum);//数据库执行失败
void dbi_MsgAck_MemLack(UINT mId, MESSAGE* pyfMsg,WORD wNum);//内存申请时失败
void dbi_MsgAck_LenLack(UINT mId, MESSAGE* pyfMsg,WORD wNum);//结果长度超过存储区长度
void dbi_MsgAck_NoneTableRet(UINT mId, MESSAGE* pyfMsg,WORD wNum);//无结果记录集可返回
void dbi_MsgAck_NeednotRet(UINT mId, MESSAGE* pyfMsg,WORD wNum);
void dbi_MsgAck_UnFindDstRow(UINT mId, MESSAGE* pyfMsg,WORD wNum,UINT TotalRow);//未找到指定要返回的行
void dbi_MsgAck_FindDstRow(UINT mId, MESSAGE* pyfMsg,WORD wNum,char *sBuf,UINT DstRow,UINT TotalRow);//找到指定行,正常返回.
void dbi_TestConn(UINT);

void dbi_GetDBIni(char * DstUser,char * DstPwd);
void 	dbi_ReleaseMemory(char**,INT);
#endif