#include "pMsg.h"
extern MESSAGE g_Slave_Message[];

BOOL Read_Message(WORD senssionID,MESSAGE *pMsg)
{
	if(senssionID<MESSAGE_MAX)
	{
		memcpy( pMsg,&g_Slave_Message[senssionID],sizeof(MESSAGE));
		return TRUE;
	}
	else
	{
		return FALSE;
	}
}
/********************************************************************
函数名称：Update_Message
函数功能: 消息内容更新，包括删除和更新判断
参数定义: pMsg:消息内容,wNumber:消息偏移量，
		  bDelete:TRUE为清空消息，FALSE为更新消息
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-7-21 1:44
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Update_Message(WORD senssionID,MESSAGE *pMsg,BOOL bDelete)
{
	BOOL bTrue = TRUE;
	if(MESSAGE_MAX<=senssionID)
	{
		return FALSE;
	}
	if(bDelete)//清空消息
	{
		//call clean message
		memset(&g_Slave_Message[senssionID],0,sizeof(MESSAGE));
	}
	else//更新消息
	{
		if((pMsg->head.sender.node == 0)&&(pMsg->head.receiver.node == 0))//来自TIMER的消息
		{
			g_Slave_Message[senssionID].head.event=pMsg->head.event;
		}
		else
		{
			memcpy(&g_Slave_Message[senssionID],pMsg,sizeof(MESSAGE));	
		}
		
	}
	return  bTrue;
}