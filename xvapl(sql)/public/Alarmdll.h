/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-27   14:51
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPPAlarm\Alarmdll.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPPAlarm
file base:Alarmdll
file ext: h
author:	  刘定文

purpose:	Alarm 告警处理
*********************************************************************/
#ifndef _ALARMDLL_H_
#define _ALARMDLL_H_
#include <windows.h>
#if _ALARM_DLL
# define DLLIMPORT __declspec (dllimport)
#else /* Not ALARM_DLL */
# define DLLIMPORT __declspec (dllexport)
#endif /* Not ALARM_DLL */


DLLIMPORT BOOL EventLog(const WORD wMid,const char *function_name,const char *reason);

#endif