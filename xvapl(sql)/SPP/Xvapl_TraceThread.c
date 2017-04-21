/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2009-3-2   12:01
文件名称: E:\XVAPL业务逻辑平台\源代码\SPP\Xvapl_TraceThread.c
文件路径: E:\XVAPL业务逻辑平台\源代码\SPP
file base:Xvapl_TraceThread
file ext: c
author:	  刘定文

  purpose:	跟踪日志记录
*********************************************************************/
#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <stdio.h>
#include "public_define.h"
#include "Xvapl_TraceThread.h"
void Trace_ThreadProc(PVOID pVoid)
{
	MSG   msg ;
	
	RegisterThread(MID_TRACE);
	
	while(bRun)
	{
		GetMessage(&msg,NULL,0,0);
		if(msg.message!=evThreadMessage)
		{
			continue;
		}
		else//处理消息
		{
			Trace_Deal_Message(msg.wParam,msg.lParam);
		}
	}
}
/********************************************************************
函数名称：Trace_Deal_Message
函数功能: 读消息，跟踪日志写入文件
参数定义: wParam：消息编号，lParam：会话号
返回定义: 无
创建时间: 2009-3-2 13:58
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void Trace_Deal_Message(WPARAM wParam,LPARAM lParam)
{
	BOOL bFind = FALSE;
	MESSAGE msg;
	
	
	bFind = COM_Read_Message((WORD)wParam,&msg);
	if(!bFind)
		return;
	
	Trace_WriteFileLog(msg);
}
/********************************************************************
函数名称：WriteFileLog
函数功能:写跟踪日志
参数定义: pMessage：消息
返回定义: 成功返回TRUE,拾芊祷谾ALSE
创建时间: 2009-3-2 14:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL Trace_WriteFileLog(MESSAGE pMessage)
{
	FILE	    *fp=NULL;
	char	 	fileName[64];
	char content[448];
	int len = sizeof(fileName);
	memset(fileName,0,len);
	memset(content,0,sizeof(content));
	memcpy(fileName,pMessage.data,len);
	memcpy(content,pMessage.data+len,pMessage.head.len-len);
	fp = fopen (fileName,"a");
	if ( fp == NULL )
		return FALSE;
	fprintf (fp, "%s\n",content);
	fclose (fp);
	
	return TRUE;
}