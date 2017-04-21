/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-2   10:51
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public\init_global.h
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\public
file base:init_global
file ext: h
author:	  刘定文

purpose:	整体结构定义
*********************************************************************/
#ifndef _INIT_GLOBAL_H_
#define _INIT_GLOBAL_H_
#include "comm.h"
#include "spp_comm.h"
BOOL       bRun;				/*运行状态*/
HWND       g_hWnd;				/*窗口句柄*/
DWORD      g_dwNode;			/*节点（IP地址）*/

SPP_BASIC    s_basic;           /*总体配置信息*/
SPP_DATABASE s_database;        /*数据库信息*/
PUBLIC     *g_Public;           /*记录窗口和节点信息*/
MESSAGE    *g_Message;			/*消息*/
NID        *g_Node;				/*节点信息*/
MID        *g_Module;			/*模块信息*/
SPP_IPP    *g_Ipp;              /*记录第三方模块信息*/
SID_POSITION *g_Sid_Position;   /*会话号ID*/
SID_POSITION  *g_Offset_Position; /*消息偏移量*/
// BOOL g_Trace;//是否跟踪
BOOL bWritConfig;//写业务配置文件
BOOL bLoadService;//加载业务
TRACE_CONDITION g_Trace_Infor;//跟踪条件
#endif