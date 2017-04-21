/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2008-12-11   16:57
文件名称: E:\XVAPL业务逻辑平台\源代码\SPP\xvapl_monitor.c
文件路径: E:\XVAPL业务逻辑平台\源代码\SPP
file base:xvapl_monitor
file ext: c
author:	  刘定文

purpose:	处理来自SvrMan接口的消息处理
*********************************************************************/
#include "Commdll.h"
#include "yftrace.h"
#include "xvapl_monitor.h"
/********************************************************************
函数名称：xvapl_monitor_run
函数功能: 启动入口函数
参数定义: 无
返回定义: wu
创建时间: 2008-12-11 17:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void xvapl_monitor_run(PVOID pVoid)
{
	MSG   msg ;
	RegisterThread(MID_MONITOR);
	while(bRun)
	{
		GetMessage(&msg,NULL,0,0);
		if(msg.message!=evThreadMessage)
		{
			continue;
		}
		else//处理消息
		{
			Monitor_Deal_Message(msg.wParam,msg.lParam);
		}
	}
}
/********************************************************************
函数名称：Deal_Message
函数功能: 消息处理
参数定义: wParam:消息内容偏移量，lparam:会话号(不用)
返回定义: 无
返回定义: 无
创建时间: 2008-12-11 17:07
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Monitor_Deal_Message(WPARAM wParam,LPARAM lParam)
{
	BOOL bFind = FALSE;
	MESSAGE msg;
	
	
	bFind = COM_Read_Message((WORD)wParam,&msg);
	if(!bFind)
		return;

	switch(msg.head.event)
	{
	case evSerMan_ActivityTest:/*连接测试*/
		{
			
			xvapl_svrman_ActivityTestAck(msg);
		}
		break;
	case evSerMan_SetStatus://设置业务状态
		{
			xvapl_svrman_SetStatusAck(msg);
		}
		break;
	case evSerMan_SetConstValue://设置常量值
	case evSerMan_GetConstValue://读取常量值
	case evSerMan_GetConstAttr://读取常量属性
	case evSerMan_GetAllConstAttr://获取常量整体属性属性
		{
			xvapl_svrman_UpdateService(msg,msg.head.event);
		}
		break;
	default:
		break;
	}
}
/********************************************************************
函数名称：xvapl_svrman_ActivityTestAck
函数功能: 连接测试确认
参数定义: msg:消息内容
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-11 17:27
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL xvapl_svrman_ActivityTestAck(MESSAGE  msg)
{
	TID pTid;
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	msg.head.event = evSerMan_ActivityTestAck;
	memcpy(&msg.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_MONITOR,&pTid);
	memcpy(&msg.head.sender,&pTid,sizeof(TID));
	memset(msg.data,0,sizeof(msg.data));
	msg.head.len=0;
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&msg,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX+1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称：xvapl_svrman_SetStatusAck
函数功能: 设置状态确认
参数定义: msg:消息内容
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-11 17:27
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL xvapl_svrman_SetStatusAck(MESSAGE msg)
{
	BYTE status = 0;
	stSERMAN_STATUS *pStatus = (stSERMAN_STATUS *)msg.data;
	if(pStatus == NULL)
		return FALSE;
	status = pStatus->operStatus;
	switch(status)
	{
	case StartTrace://  启动跟踪
		g_Trace = EnableTrace(TRUE);
		SendSvrManAck(msg,g_Trace);
		break;
	case CancelTrace:   //  取消跟踪
		g_Trace = EnableTrace(FALSE);
		SendSvrManAck(msg,g_Trace);
		break;
	case StopServiceRun:  //  停止业务
	case ActiveService: //  激活业务
	case WriteConfig://写配置文件
	case LoadService://  加载业务
	case UnloadService://  卸载业务
	    xvapl_svrman_UpdateService(msg,evSerMan_SetStatus);
		break;
	default:
		break;
	}
	return TRUE;
}
BOOL SendSvrManAck(MESSAGE msg,BOOL bValue)
{
	TID pTid;
	if(!bValue)
	{
		msg.head.event = evSerMan_ErrorReport;
		msg.head.len = 0;
		memset(msg.data,0,sizeof(msg.data));
	}
	else
	{
		msg.head.event = evSerMan_SetStatusAck;
	}
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	memcpy(&msg.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_MONITOR,&pTid);
	memcpy(&msg.head.sender,&pTid,sizeof(TID));
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&msg,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX-1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称:xvapl_svrman_UpdateService
函数功能:返回常量值
参数定义: msg:消息内容
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-15 10:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL xvapl_svrman_UpdateService(MESSAGE msg,WORD event)
{
	TID pTid;
	stSERMAN_VALUE *pValue=(stSERMAN_VALUE *)msg.data;
	if(pValue == NULL)
		return FALSE;
	if(!GetTid(MID_SLP,&pTid))
		return FALSE;
	msg.head.event = event;
	memcpy(&msg.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_MONITOR,&pTid);
	memcpy(&msg.head.sender,&pTid,sizeof(TID));
	
	if(!SEND_MSG(evThreadMessage,MID_SLP,(void *)&msg,(WORD)(sizeof(MESSAGE)),(WORD)(0),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}