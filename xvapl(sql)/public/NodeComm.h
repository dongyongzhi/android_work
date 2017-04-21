/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-9   11:42
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP\NodeComm.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP
file base:NodeComm
file ext: h
author:	  刘定文

purpose:	节点通讯
*********************************************************************/

#ifndef _NODE_COMM_H_
#define _NODE_COMM_H_
#include <windows.h>
#include <process.h> 
#include "event.h"
#include "init_viriable.h"
#include "define.h"
#include "Commdll.h"
#include "yftrace.h"
#pragma   pack(1)
typedef struct
{
	int fd;/*socket句柄*/
	DWORD ip;/*节点IP*/
	BOOL bThread;/*是否启动线程*/
}TCP_INFOR;/*TCP网络信息*/
#pragma   pack()



void  OnInitNetWork(LPVOID lPvoid);
BOOL TcpServer_Run();
BOOL TcpCustom_Run();
void Tcp_SendThread(void *arg);
void Tcp_SendThread_Custom(void *arg);
void Tcp_RecvThread(void *arg);
int RegisterNodeThread(DWORD wNode,DWORD wModule);
BOOL UnRegisterNodeThread(const int Node_number);
static void Clean_Module(const DWORD ip);
BOOL GetNodeState(const int Node_number);
int GetNodeByIp(const DWORD ip);
int SendData(DWORD ip,int fd,const WORD wNumber,BOOL bTestLink,BOOL bServer);
int SendMessageData(DWORD ip,int fd,const WORD wNumber,WORD offset);
void Deal_Server_Message(WPARAM wParam,LPARAM lParam);
void Tcp_ServControlThread(void *arg);
DWORD FindNodeModule(UINT ModID);
BOOL RecvData_Analysis(BYTE *pBuf,int len);
BYTE  GetXORCode(BYTE *pBuffer,int nSize);
#endif