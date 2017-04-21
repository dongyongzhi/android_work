/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-12-17   9:47
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public\public_define.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public
file base:public_define
file ext: h
author:	  刘定文

purpose:	其他结构定义
*********************************************************************/
#ifndef _PUBLIC_DEFINE_H_
#define _PUBLIC_DEFINE_H_
#include <windows.h>
#include "comm.h"
#pragma   pack(1)   //   按照1字节方式进行对齐   

#define MAX_LENGTH CHAR_NUMBER
typedef struct
{
	BYTE bAccNum;/*接入服务器个数*/
	BYTE bIppNum;/*第三方系统个数*/
	char version[4];/*版本号*/
	char pName[64];/*名称*/
	WORD wAttri;/*基本属性*/
	
} BASIC_INFOR; /*基本信息*/


typedef struct
{
	char pComputerName[64];/*计算机名*/
	char pDataBase[64];/*数据库名*/
	char pPassWd[16];/*密码*/
} DATABASE_INFOR;/*数据库信息*/

typedef struct
{
	int id;/*编号*/
	char pName[64];/*名称*/
	WORD wAttri;/*属性*/

}ACC_INFOR;/*接入服务器信息*/

typedef struct
{
	int id;/*编号*/
	char pName[64];/*名称*/
	DWORD dIp;/*IP地址;*/
	WORD  wPort;/*端口号*/
	WORD  wAttri;/*属性*/
}IPP_INFOR;/*第三方接口信息*/


typedef struct 
{
	UINT iRow;	//需要返回的记录:0:不需要返回数据记录;1:返回第一条;2:返回第二条;....	                     	
	char sSql[MAX_LENGTH - sizeof(UINT)];  //sql语句

}SQLSrcMsg;

typedef enum
{
	DBACK_FAILED =				0,
	DBACK_SUCC_NOTRETRECORD =	1,
	DBACK_SUCC_FINDRECORD =		2,
	DBACK_SUCC_NOTFINDRECORD =	3,
	DBACK_SUCC_NORECORDRET =	4,
	DBACK_FAILED_EXECSQL =		5,
	DBACK_FAILED_LENLACK =		6,
	DBACK_FAILED_MEMLACK =		7,
	DBACK_FAILED_NOCONN =		8
}DBAPACK;
typedef struct 
{
	UINT iSqlExecRet;  //SQL语句执行结果:
						//0:失败; 
						//1:执行成功,未要求返回记录集;=
						//2:执行成功,找到指定记录;
						//3:执行成功,未找到指定的数据记录
		                //4:执行成功,没有记录集可返回; =
						//5:SQL执行失败(语法错误)或网络断开; 
						//6:返回结果长度超过存储区长度
						//7:系统处理查询结果过程中申请内存不成功;
						//8:数据库无法连接;
	UINT iTotalNumsOfRows; //此执行结果产生的记录总数
	UINT iRow;       //如果是多条记录,返回的是条记录集中的第几条记录.
	char sResults[MAX_LENGTH-3*sizeof(UINT)];
}SQLDestMsg;

//typedef struct
//{
//	char userNum[32];//用户号码
//	char accessNum[32];//接入号码
//	char reversed[64]; //保留，不同业务可能不同
//	WORD bkey;//处理该消息的业务键
//}strODIACCESS;//ODI启动业务结构


#pragma   pack() 

#endif