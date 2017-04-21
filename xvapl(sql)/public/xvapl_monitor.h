/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2008-12-11   16:50
文件名称: E:\XVAPL业务逻辑平台\源代码\PUBLIC\xvapl_monitor.h
文件路径: E:\XVAPL业务逻辑平台\源代码\PUBLIC
file base:xvapl_monitor
file ext: h
author:	  刘定文

purpose:	处理来自SvrMan接口的消息处理
*********************************************************************/
#ifndef _XVAPL_MONITOR_H_
#define _XVAPL_MONITOR_H_
#include "serviceMan_global.h"
void xvapl_monitor_run(PVOID pVoid);

void Monitor_Deal_Message(WPARAM wParam,LPARAM lParam);

BOOL xvapl_svrman_ActivityTestAck(MESSAGE  msg);

BOOL xvapl_svrman_SetStatusAck(MESSAGE msg);

BOOL xvapl_svrman_UpdateService(MESSAGE msg,WORD event);
BOOL SendSvrManAck(MESSAGE msg,BOOL bValue);
#endif