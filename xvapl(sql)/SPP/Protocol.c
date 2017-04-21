/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-6   11:33
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP\Protocol.c
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP
file base:Protocol
file ext: c
author:	  刘定文

purpose:	协议分析
*********************************************************************/
#include "../public/Protocol.h"
#include "xvapl_slp.h"
/********************************************************************
函数名称：GetSum
函数功能:和校验
参数定义: data:数据内容，len:数据长度
返回定义: 处理成功返回TRUE，失败返回FALSE
创建时间: 2007-11-6 18:00
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE GetSum(BYTE *data,int len)
{
	BYTE pdata=0;
	int i;
	for(i=0;i<len;i++)
	{
		pdata=*(data+i)+pdata;
	}
	pdata=~pdata;
	return (BYTE)(pdata+1);
}
/********************************************************************
函数名称：JudgSum
函数功能: 数据校验判断
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-1 10:37
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL JudgSum(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind = FALSE;
	BYTE value;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	value=GetSum(pMessage.messageInfor.data,pMessage.messageInfor.head.len-1);
	if(value == pMessage.messageInfor.data[pMessage.messageInfor.head.len-1])
		return TRUE;
	else
		return FALSE;
}
/********************************************************************
函数名称：UpdateFskData
函数功能: 更新FSK数据
参数定义: data:数据内容，datalen:数据长度
返回定义: 成功返回FSK数据长度,错误返回-1
创建时间: 2008-12-1 12:03
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

int UpdateFskData(BYTE *data,int datalen,BYTE step)
{
	BYTE data1[1024];
	if(data == NULL)
		return -1;
	memset(data1,0,sizeof(data1));
	data1[0]=0x84;
	data1[1]=datalen+1;
	data1[2]=step;
	memcpy(data1+3,data,datalen);
	data1[3+datalen]=GetSum(data1,datalen+3);
	return (datalen+4);
}

