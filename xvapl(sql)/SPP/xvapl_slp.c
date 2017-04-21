/********************************************************************
公司名称：江苏怡丰通讯有限公司重庆研发中心
创建时间: 2008-6-11   10:47
文件名称: E:\XVAPL业务逻辑平台\xvapl_slp.c
文件路径: E:\XVAPL业务逻辑平台
file base:xvapl_slp
file ext: c
author:	  刘定文

  purpose:	XVAPL通用业务逻辑处理程序
*********************************************************************/
#include <stdio.h>
#include <time.h>
#include <process.h>  
#include <stdlib.h>
#include <string.h>
#include "public_define.h"

#include "log.h"
#include "serviceMan_global.h"
#include "_xvapl__odi.h"
#include "spp_des.h"
#include "xvapl_slp.h"
WORD hDBDAsk[]={evPXSSM_ReleaseCall,evAckDBD,evDBQueryNak,evDBQueryAck,evTimer0};
WORD hODI_Message[]={evPXSSM_ReleaseCall,evODI_Message_Ack,evTimer0};
WORD hPXSSM_PlayAnnouncement[]={evPXSSM_ReleaseCall,evPXSSM_ResourceReport,evPXSSM_ErrorReport,evTimer0};
WORD hPXSSM_PromptCollectFSK[]={evPXSSM_ReleaseCall,evPXSSM_CollectedFSKInformation,evPXSSM_ResourceReport,evPXSSM_ErrorReport,evTimer0};
WORD hPXSSM_PromptCollectInformation[]={evPXSSM_ReleaseCall,evPXSSM_CollectedInformation,evPXSSM_ResourceReport,evPXSSM_ErrorReport,evTimer0};
WORD hPXSSM_PromptCollectInformationAndSetFormat[]={evPXSSM_ReleaseCall,evPXSSM_CollectedInformation,evPXSSM_ErrorReport,evTimer0};
WORD hPXSSM_SendFSK[]={evPXSSM_ReleaseCall,evPXSSM_ResourceReport,evPXSSM_ErrorReport,evTimer0};

WORD hPXSSM_SendFSKCollectFSK[]={evPXSSM_ReleaseCall,evPXSSM_CollectedFSKInformation,evPXSSM_ResourceReport,evPXSSM_ErrorReport,evTimer0};
WORD hPXSSM_SendFSKCollectInformation[]={evPXSSM_ReleaseCall,evPXSSM_CollectedInformation,evPXSSM_ResourceReport,evPXSSM_ErrorReport,evTimer0};
WORD hTcp_SendDataAndReceiveData[]={evPXSSM_ReleaseCall,evTcp_ReceiveData,evPXSSM_ResourceReport,evTimer0};
WORD hTcp_ReceiveData[]={evPXSSM_ReleaseCall,evTcp_ReceiveData,evTimer0};

WORD hTimer0[]={evPXSSM_ReleaseCall,evTimer0};


void DataTest();
void TestTimer();
/********************************************************************
函数名称：SLPThreadProc
函数功能:业务逻辑处理主循环，注册线程、接收消息并处理
参数定义: 无
返回定义: 无
创建时间: 2007-11-5 14:28
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void XSCM_ThreadProc(PVOID pVoid)
{
	MSG   msg ;
	//	EnableTrace_Slp(TRUE);
	//	EnableTrace(TRUE);
	//	YF_LOG_SPP "SLPThreadProc,业务线程启动");
	while(bRun)
	{
		if(!InitXscmSlp())
			return ;
		RegisterThread(MID_SLP);
		//Sleep(20000);
		//TestTimer();//提名TIMER极限测试
		while(bRun&&(!bInital))
		{
			//		DataTest();
			memset(&msg,0,sizeof(MSG));
			GetMessage(&msg,NULL,0,0);
			if(msg.message!=evThreadMessage)
			{
				continue;
			}
			else//处理消息
			{
				Deal_Message(msg.wParam,msg.lParam);
			}
		}
		ReleaseAllData();
		if(!bInital)
			break;
	}
	_endthread();
	return;
	
}
/********************************************************************
函数名称：InitXscmSlp
函数功能: 初始化、读配置文件等
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-9-5 10:09
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL InitXscmSlp()
{
	initService();
	if(!ReadServiceKey())
		return FALSE;
	if(!InitMerroy())
		return FALSE;
	return TRUE;
}
void ReleaseAllData()
{
	UnRegisterThread(MID_SLP);
	ReleaseServiceKey();
	ReleaseMerroy();
}
BOOL ReleaseServiceKey()
{
	int i,j;
	for(i=0;i<ServiceKey_Total;i++)
	{
		if((pXscmSlp+i)->pConstAtte!=NULL)
		{
			free((pXscmSlp+i)->pConstAtte);
			(pXscmSlp+i)->pConstAtte=NULL;
		}
		if((pXscmSlp+i)->pConstVaribles!=NULL)
		{
			free((pXscmSlp+i)->pConstVaribles);
			(pXscmSlp+i)->pConstVaribles=NULL;
		}
		if((pXscmSlp+i)->pConstVaribles!=NULL)
		{
			free((pXscmSlp+i)->pConstVaribles);
			(pXscmSlp+i)->pVariableAttr=NULL;
		}
		if((pXscmSlp+i)->pSib!=NULL)
		{
			for(j=0;j<(pXscmSlp+i)->sBasic.nTotalSib;j++)
			{
				if(((pXscmSlp+i)->pSib+j)->pParam!=NULL)
				{
					free(((pXscmSlp+i)->pSib+j)->pParam);
					((pXscmSlp+i)->pSib+j)->pParam=NULL;
				}
				if(((pXscmSlp+i)->pSib+j)->pvalue!=NULL)
				{
					free(((pXscmSlp+i)->pSib+j)->pvalue);
					((pXscmSlp+i)->pSib+j)->pvalue=NULL;
				}
				
			}
			free((pXscmSlp+i)->pSib);
			(pXscmSlp+i)->pSib=NULL;
		}
	}
	return TRUE;
}
BOOL ReleaseMerroy()
{
	if(g_MESSAGE_XSCM_SLAVE!=NULL)
	{
		free(g_MESSAGE_XSCM_SLAVE);
		g_MESSAGE_XSCM_SLAVE=NULL;
	}
	return TRUE;
}
/********************************************************************
函数名称：UpdateAckMessage
函数功能: 响应事件过滤
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2011-06-24 13:51
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateAckMessage(WORD wNowvent,WORD wLastevent,WORD senssionID,int mId)
{
	BOOL bFind=FALSE;
	int i,arraySize;
	switch(wLastevent)
	{
	case evDBDAsk:
		{
			arraySize=sizeof(hDBDAsk)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hDBDAsk[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
	case evODI_Message:
		{
			arraySize=sizeof(hODI_Message)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hODI_Message[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_PlayAnnouncement:
		{
			arraySize=sizeof(hPXSSM_PlayAnnouncement)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_PlayAnnouncement[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_PromptCollectFSK:
		{
			arraySize=sizeof(hPXSSM_PromptCollectFSK)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_PromptCollectFSK[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_PromptCollectInformation:
		{
			arraySize=sizeof(hPXSSM_PromptCollectInformation)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_PromptCollectInformation[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_PromptCollectInformationAndSetFormat:
		{
			arraySize=sizeof(hPXSSM_PromptCollectInformationAndSetFormat)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_PromptCollectInformationAndSetFormat[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_SendFSK:
		{
			arraySize=sizeof(hPXSSM_SendFSK)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_SendFSK[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_SendFSKCollectFSK:
		{
			arraySize=sizeof(hPXSSM_SendFSKCollectFSK)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_SendFSKCollectFSK[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
		
	case evPXSSM_SendFSKCollectInformation:
		{
			arraySize=sizeof(hPXSSM_SendFSKCollectInformation)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hPXSSM_SendFSKCollectInformation[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
	case evTcp_ReceiveData:
		{
			arraySize=sizeof(hTcp_ReceiveData)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hTcp_ReceiveData[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
	case evTcp_SendDataAndReceiveData:
		{
			arraySize=sizeof(hTcp_SendDataAndReceiveData)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hTcp_SendDataAndReceiveData[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
	case evTimer0:
		{
			arraySize=sizeof(hTimer0)/sizeof(WORD);
			for(i=0;i<arraySize;i++)
			{
				if(hTimer0[i]==wNowvent)
				{
					bFind=TRUE;
					break;
				}
			}
			break;
		}
	default:
		bFind = TRUE;
		break;
	}
	if(!bFind)
	{
		if(wNowvent>0)
			YF_LOG_SPP "SLP:接收到异常信号，抛弃,sid=%d，源事件号=%d,异常事件号wNowvent=%d,from mId=%d",senssionID,wLastevent,wNowvent,mId);
		
	}
	return bFind;
}
/********************************************************************
函数名称：Deal_Message
函数功能: 消息处理
参数定义: wParam:消息内容偏移量，lparam:会话号
返回定义: 无
创建时间: 2008-8-18 16:49
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Deal_Message(WPARAM wParam,LPARAM lParam)
{
	BOOL bFind = FALSE;
	MESSAGE_XSCM_SLAVE pXscmMsg;
	if(lParam==(MESSAGE_MAX+2))
	{
		lParam = GetSenssionID();
	}
	if(lParam>=MESSAGE_MAX)
		return;
	if(!ReadXSCMMessage((WORD)lParam,&pXscmMsg))
		return;
	bFind = COM_Read_Message((WORD)wParam,&pXscmMsg.messageInfor);
	if(!bFind)
		return;
	if(!UpdateAckMessage(pXscmMsg.messageInfor.head.event,pXscmMsg.slavehead.wlastevent,(WORD)lParam,pXscmMsg.messageInfor.head.sender.mId))//屏蔽不相应的事件
	{
		return;
	}
	pXscmMsg.slavehead.wlastevent=0;
	if((pXscmMsg.messageInfor.head.event!=evTest_BeginControl)||(pXscmMsg.messageInfor.head.event!=evTest_GetTestSenssion)||(pXscmMsg.messageInfor.head.event!=evTest_EndControl))
	{
		if(!UpdateXSCMMessage((WORD)lParam,&pXscmMsg,FALSE))
			return ;
	}

	g_Trace_Slp=1;
	//g_MESSAGE_XSCM_SLAVE[lParam].AcceseID=pXscmMsg.AcceseID;

	switch(pXscmMsg.messageInfor.head.event)
	{
	case evInitial://初始化
		bInital = TRUE;
		break;
	case evTraceStatus://跟踪控制
		ControlTrace((WORD)lParam);
		break;
	case evPXSSM_InitiateDP:/*开启会话,pstn*/
		{
			if(!UpdateDataPstnInit((WORD)lParam))
				break;
			XVAPL_Push((WORD)lParam,0);
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
	case evTcp_InitTCP:/*开启会话,tcp*/
		{
			if(!UpdateDataTCPInit((WORD)lParam))
				break;
			XVAPL_Push((WORD)lParam,0);
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
	case evPXSSM_CollectedInformation://收集的用户信息
		{
			if(!UpdateCollectInformation((WORD)lParam))
			{
				EndSenssion((WORD)lParam,4);
			}
		}
		break;
	case evPXSSM_CollectedFSKInformation://收集的FSK信息
		{
			YF_TRACE_SLP lParam,"","SLP:收集的FSK信息");
			
			if(!UpdateCollectFskInformation((WORD)lParam))
			{
				EndSenssion((WORD)lParam,4);
				return ;
			}
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
	case evPXSSM_ReleaseCall://释放会话
		{
			if((WORD)lParam>=MESSAGE_MAX)
				break;

			g_MESSAGE_XSCM_SLAVE[lParam].accID=1;
		//		break;
			ReleaseCall((WORD)lParam);
		}
		break;
	case evTcp_ReceiveData:
		YF_TRACE_SLP lParam,"","SLP:收集的TCP数据");
		
		if(!UpdateTcpReceiveData((WORD)lParam))
		{
			EndSenssion((WORD)lParam,4);
			return ;
		}
		ControlRunSib((WORD)lParam,TRUE);
		break;
	case evAckDBD:/*数据库连接异常*/
		{
			YF_LOG_SPP "SLP:数据库连接异常,sid=%d，sender=%d,recevi=%d",lParam,pXscmMsg.messageInfor.head.sender.module,pXscmMsg.messageInfor.head.receiver.module);
			DBQueryResultNak((WORD)lParam);
		}
		break;
	case evDBQueryNak:/*查询数据库异常返回*/
		{
			YF_LOG_SPP "SLP:查询结果返回异常,sid=%d",lParam);
			YF_TRACE_SLP lParam,"","SLP:查询结果返回异常");
			
			DBQueryResultNak((WORD)lParam);
		}
		break;
	case evDBQueryAck:/*查询数据库返回*/
		{
			YF_TRACE_SLP lParam,"","SLP:收集的DB信息");
			
			if(UpdateDBResultData((WORD)lParam))
			{
				//RunSib((WORD)lParam,TRUE);
				ControlRunSib((WORD)lParam,TRUE);
			}
			else
				DBQueryResultNak((WORD)lParam);
		}
		break;
	case evPXSSM_Connect://外呼事件响应
		if(UpdateCallOutData((WORD)lParam))
		{
			ControlRunSib((WORD)lParam,TRUE);
		}
		else
		{
			DBQueryResultNak((WORD)lParam);
		}
		break;

	case evFSKQueryAck:/*第三方查询结果返回*/
		{
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
		
	case evPXSSM_ErrorReport:/*错误信息*/
		{
			YF_TRACE_SLP lParam,"","SLP:错误信息");
			if(DoWithErrorReport((WORD)lParam))
				ControlRunSib((WORD)lParam,TRUE);
			else
				ReleaseCall((WORD)lParam);
		}
		break;
	case evPXSSM_ResourceReport:/*事件报告*/
		YF_TRACE_SLP lParam,"","SLP:事件报告");
		if(DoWithReport((WORD)lParam))
			ControlRunSib((WORD)lParam,TRUE);
		break;

	case evODI_Access:/*来自ODI模块的消息，启动一个业务*/
		
		if(!UpdateDataOdiAccess((WORD)lParam))
			break;
		XVAPL_Push((WORD)lParam,0);
		//RunSib((WORD)lParam,TRUE);
		ControlRunSib((WORD)lParam,TRUE);
		break;
	case evODI_Message_Ack:/*来自ODI的消息*/

		YF_TRACE_SLP lParam,"","SLP:收集的ODI数据");	
		UpdataOdiData((WORD)lParam);
		//RunSib((WORD)lParam,TRUE);
		ControlRunSib((WORD)lParam,TRUE);
		break;

	case evTimer0:/*定时器1SQL*/
		{
			YF_TRACE_SLP lParam,"","SLP:timer0,超时挂机,timer=%d",evTimer0);
			OnTimerAck((WORD)lParam);
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
	case evTimer1:/*定时器1SQL*/
		{
			YF_TRACE_SLP lParam,"","SLP:timer1,超时挂机,timer=%d",evTimer1);
			OnTimerAck((WORD)lParam);
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
	case evTimer2:/*定时器2（第三方）*/
		{
			OnTimerAck((WORD)lParam);
			//RunSib((WORD)lParam,TRUE);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
		
	case evSerMan_SetConstValue://设置常量值
		{
			SvrMan_SetConstValue((WORD)lParam);
		}
		break;
	case evSerMan_GetConstValue://读取常量值
		{
			SvrMan_GetConstValue((WORD)lParam);
		}
		break;
	case evSerMan_SetStatus://设置业务状态
		{
			SvrMan_UpdateService((WORD)lParam);
		}
		break;
	case evSerMan_GetConstAttr://读取常量属性
		{
			SvrMan_GetConstAttr((WORD)lParam);
		}
		break;
	case evSerMan_GetAllConstAttr://获取常量整体属性属性
		{
			SvrMan_GetAllConstAttr((WORD)lParam);
		}
		break;
	case evTest_BeginControl://启动调试
		{
			CheckCase_Begin(pXscmMsg.messageInfor);
		}
		break;
	case evTest_EndControl://调试结束
		{
			CheckCase_End(pXscmMsg.messageInfor);
		}
		break;
	case evTest_GetTestSenssion://获取当前测试的会话号
		{
			CheckCase_getSenssionID(pXscmMsg.messageInfor);
		}
		break;
	case evTest_ReadValue://读常量值、变量值
		{
			CheckCase_ReadValue((WORD)lParam);
		}
		break;
	case evTest_SetValue://设置常量、变量值
		{
			CheckCase_SetValue((WORD)lParam);
		}
		break;
	case evTest_CurrentSibNo://获取当前执行的SIB编号请求
		{
			CheckCase_CurrentSibNo((WORD)lParam);
		}
		break;
	case evTest_SibControl://控制SIB操作
		{
			CheckCase_SibControl((WORD)lParam);
			ControlRunSib((WORD)lParam,TRUE);
		}
		break;
	default:
		break;
	}
	return;
	
}

BOOL WriteConfigValue(SIB_BASIC sBasic,char *value)
{
	FILE *fp;
	char buffer[2048];
	fp=fopen("C:\\yfcomm\\log\\xmlconfig.ini","a");
	
	if(value==NULL)
	{
		sprintf(buffer,"[identiNo]=%03d [sibType]=%02d [nextstep]=%05d [Errhandle]=%05d [paramTotal]=%02d [len]=%02d \n",sBasic.identiNo,sBasic.sibType,sBasic.nextstep
			,sBasic.Errhandle,sBasic.paramTotal,sBasic.len);
		fwrite(buffer,strlen(buffer),1,fp);
	}
	else
	{
		sprintf(buffer,"[identiNo]=%03d [sibType]=%02d [nextstep]=%05d [Errhandle]=%05d [paramTotal]=%02d [len]=%02d [string]=%s \n",sBasic.identiNo,sBasic.sibType,sBasic.nextstep
			,sBasic.Errhandle,sBasic.paramTotal,sBasic.len,value);
		fwrite(buffer,strlen(buffer),1,fp);
	}
	fclose(fp);
	return TRUE;
}
/********************************************************************
函数名称：ControlTrace
函数功能: 跟踪控制
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-4-1 9:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL ControlTrace(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	if(pMessage.messageInfor.data[0]==TRUE)
		EnableTrace_Slp(TRUE);
	else
		EnableTrace_Slp(FALSE);
	return TRUE;
}
BOOL  WriteTotalMessage(XSCM_SLP_BASIC sBasic)
{
	FILE *fp;
	char buffer[512];
	fp=fopen("C:\\yfcomm\\log\\xvaplTotal.ini","a");
	
	sprintf(buffer,"[ServiceLogo]=%s [languageSelect]=%d [serviceKey]=%05d [serviceDescrible]=%s [nVariableTotal]=%02d [nVariableLen]=%04d [nConstVariableTotal]=%02d [nConstVariableLen]=%04d [nTotalSib]=%02d [nSibLen]=%04d \n",
		sBasic.ServiceLogo,sBasic.languageSelect,sBasic.serviceKey
		,sBasic.serviceDescrible,sBasic.nVariableTotal,sBasic.nVariableLen,sBasic.nConstVariableTotal,sBasic.nConstVariableLen
		,sBasic.nTotalSib,sBasic.nSibLen);
	fwrite(buffer,strlen(buffer),1,fp);
	fclose(fp);
	return TRUE;
}
BOOL WriteParamMessage(SIB_PARAM sParam,int identiNo )
{
	FILE *fp;
	char buffer[512];
	fp=fopen("C:\\yfcomm\\log\\xvaplparam.ini","a");
	
	
	sprintf(buffer,"[identiNo]=%03d [paramNo]=%02d [paramType]=%02d [paramValue]=%04d \n",identiNo,sParam.paramNo,sParam.paramType,sParam.paramValue);
	fwrite(buffer,strlen(buffer),1,fp);
	fclose(fp);
	return TRUE;
}
BOOL WriteVaribleMessage(XSCM_VARIABLE sConstVarible,BOOL bVarible)
{
	FILE *fp;
	char buffer[2048];
	fp=fopen("C:\\yfcomm\\log\\xvaplvarible.ini","a");
	
	if(bVarible)
		sprintf(buffer,"[name]=%s [type]=%d [len]=%02d [offset]=%04d [变量] \n",sConstVarible.name,
		sConstVarible.type,sConstVarible.length,sConstVarible.offset);
	
	else
	{
		sprintf(buffer,"[name]=%s [type]=%d [len]=%02d [offset]=%04d [常量] \n",sConstVarible.name,
			sConstVarible.type,sConstVarible.length,sConstVarible.offset);
	}
	fwrite(buffer,strlen(buffer),1,fp);
	fclose(fp);
	return TRUE;
}
/********************************************************************
函数名称：ReadServiceConfig
函数功能: 读取业务配置信息，初始化
参数定义: bNo:业务下标,keyNumber:业务键号
返回定义: 无
创建时间: 2008-6-11 10:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL ReadServiceConfig(int bNo,WORD keyNumber)
{
	FILE *fp;
	int i,j;
	BOOL bTrue = FALSE;
	char fileName[128];
	XSCM_VARIABLE s;
	memset(fileName,0,sizeof(fileName));
	if(bNo>=ServiceKey_Total)
		return FALSE;
	sprintf(fileName,"C:\\yfcomm\\ini\\ser%03d.dat",keyNumber);
	fp = fopen(fileName,"rb");
	if(fp == NULL)
		return FALSE;
	fread(&(pXscmSlp+bNo)->sBasic,sizeof(XSCM_SLP_BASIC),1,fp);
	//	WriteTotalMessage((pXscmSlp+bNo)->sBasic);
	if((pXscmSlp+bNo)->sBasic.nConstVariableLen>0)
	{
		(pXscmSlp+bNo)->pConstVaribles = (BYTE *)malloc((pXscmSlp+bNo)->sBasic.nConstVariableLen);//申请常量空间
		if((pXscmSlp+bNo)->pConstVaribles == NULL)
			return FALSE;
		memset((pXscmSlp+bNo)->pConstVaribles,0,(pXscmSlp+bNo)->sBasic.nConstVariableLen);
	}
	
	
	(pXscmSlp+bNo)->pVariableAttr = (XSCM_VARIABLE *)malloc(sizeof(XSCM_VARIABLE)*(pXscmSlp+bNo)->sBasic.nVariableTotal); //变量属性
	
	/*读变量基本信息*/
	if((pXscmSlp+bNo)->pVariableAttr != NULL)
	{
		fread((pXscmSlp+bNo)->pVariableAttr,sizeof(XSCM_VARIABLE)*(pXscmSlp+bNo)->sBasic.nVariableTotal,1,fp);
	}
	for(i=0;i<(pXscmSlp+bNo)->sBasic.nVariableTotal;i++)
	{
		WriteVaribleMessage((pXscmSlp+bNo)->pVariableAttr[i],TRUE);
	}
	(pXscmSlp+bNo)->pConstAtte = (XSCM_CONSTVARIABLE *)malloc(sizeof(XSCM_CONSTVARIABLE)*(pXscmSlp+bNo)->sBasic.nConstVariableTotal);// 常量属性
	
	/*读常量基本信息*/
	if((pXscmSlp+bNo)->pConstAtte != NULL)
	{
		fread((pXscmSlp+bNo)->pConstAtte,sizeof(XSCM_CONSTVARIABLE)*(pXscmSlp+bNo)->sBasic.nConstVariableTotal,1,fp);
	}
	/*读常量值*/
	fread((pXscmSlp+bNo)->pConstVaribles,(pXscmSlp+bNo)->sBasic.nConstVariableLen,1,fp);
	for(i=0;i<(pXscmSlp+bNo)->sBasic.nConstVariableTotal;i++)
	{
		memcpy(&s,&((pXscmSlp+bNo)->pConstAtte[i]),sizeof(XSCM_CONSTVARIABLE));
		WriteVaribleMessage(s,FALSE);
	}
	/*SIB总体个数*/
	fread(&(sSibStatistics.wTotal),2,1,fp);
	/*申请记录SIB整体属性的内存空间*/
	sSibStatistics.pSibStatistics = (SIB_STATISTICS *)malloc(sizeof(SIB_STATISTICS)*sSibStatistics.wTotal);
	if(sSibStatistics.pSibStatistics != NULL)
	{
		memset(sSibStatistics.pSibStatistics,0,sizeof(SIB_STATISTICS)*sSibStatistics.wTotal);
		fread(sSibStatistics.pSibStatistics,sizeof(SIB_STATISTICS)*sSibStatistics.wTotal,1,fp);
	}
	/*申请记录各个SIB的空间*/
	(pXscmSlp+bNo)->pSib = (XSCM_SIB *)malloc(sizeof(XSCM_SIB)*sSibStatistics.wTotal);
	if((pXscmSlp+bNo)->pSib != NULL)
	{	
		memset((pXscmSlp+bNo)->pSib,0,sizeof(XSCM_SIB)*sSibStatistics.wTotal);
		for(i=0;i<sSibStatistics.wTotal;i++)//读各个SIB的基本信息
		{
			fread(&((pXscmSlp+bNo)->pSib[i].sibBasic),sizeof(SIB_BASIC),1,fp);//SIB基本属性
			if((pXscmSlp+bNo)->pSib[i].sibBasic.paramTotal > 0)//如果参数>0
			{
				(pXscmSlp+bNo)->pSib[i].pParam = (SIB_PARAM*)malloc(sizeof(SIB_PARAM)*(pXscmSlp+bNo)->pSib[i].sibBasic.paramTotal);
				if((pXscmSlp+bNo)->pSib[i].pParam != NULL)//申请内存成功
				{
					memset((pXscmSlp+bNo)->pSib[i].pParam,0,sizeof(SIB_PARAM)*(pXscmSlp+bNo)->pSib[i].sibBasic.paramTotal);
					fread((pXscmSlp+bNo)->pSib[i].pParam,sizeof(SIB_PARAM)*(pXscmSlp+bNo)->pSib[i].sibBasic.paramTotal,1,fp);
					for(j=0;j<(pXscmSlp+bNo)->pSib[i].sibBasic.paramTotal;j++)
					{
						WriteParamMessage((pXscmSlp+bNo)->pSib[i].pParam[j],(pXscmSlp+bNo)->pSib[i].sibBasic.identiNo);
					}
					if((pXscmSlp+bNo)->pSib[i].sibBasic.len > 0)//串值总长度>0
					{
						(pXscmSlp+bNo)->pSib[i].pvalue = (BYTE *)malloc(sizeof(BYTE) * (pXscmSlp+bNo)->pSib[i].sibBasic.len+1);
						if((pXscmSlp+bNo)->pSib[i].pvalue != NULL)
						{
							memset((pXscmSlp+bNo)->pSib[i].pvalue,0,(pXscmSlp+bNo)->pSib[i].sibBasic.len);
							fread((pXscmSlp+bNo)->pSib[i].pvalue,sizeof(BYTE) * (pXscmSlp+bNo)->pSib[i].sibBasic.len,1,fp);
						}
					}
				}
				
			}
			WriteConfigValue((pXscmSlp+bNo)->pSib[i].sibBasic,(pXscmSlp+bNo)->pSib[i].pvalue);
			
		}
		bTrue = TRUE;
	}
	else//申请内存失败
	{
		bTrue = FALSE;
	}
	
	fclose(fp);
	
	return  bTrue;
}
/********************************************************************
函数名称：ReadServiceKey
函数功能: 读取业务信息，包括多少个业务等等
参数定义: 无
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-6-11 16:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL ReadServiceKey()
{
	char FILENAME1[256] = "pXSSM.ini";
	SECTION *sec=NULL;
	INI ini;
	BYTE i;
	char ToneNmae[128];
	char tmpSection[LENGTH_SECTION];
	sprintf(ToneNmae,"C:\\yfcomm\\ini\\%s",FILENAME1);
	loadINI(&ini,ToneNmae);
	/*得到[COMM]*/
	sec=getSection(&ini,INI_XSSM_SERKEY);
	if(sec==NULL)
	{
		//		YF_LOG "read pxssm.ini failed");
		return FALSE;
	}
	
	ServiceKey_Total=atoi(GetKeyList(sec,0));
	pServiceKey = (SERVICE_KEY *)malloc(sizeof(SERVICE_KEY) * ServiceKey_Total);
	
	
	pXscmSlp = (XSCM_SLP *)malloc(sizeof(XSCM_SLP) * ServiceKey_Total);
	if(pXscmSlp == NULL)
	{
		return FALSE;
	}
	for (i=0;i<ServiceKey_Total;i++) 
	{
 		memset(&pXscmSlp[i],0,sizeof(XSCM_SLP));
	}
	
	/*得到[KeyNumber]*/
	for(i=0;i<ServiceKey_Total;i++)
	{
		memset(&pServiceKey[i],0,sizeof(SERVICE_KEY));
		sprintf(tmpSection,"%s%d",INI_XSSM_KEYNUMBER,i);
		sec=getSection(&ini,tmpSection);
		if(sec==NULL)
		{
			//			YF_LOG "config file %s errno",ToneNmae);
			return FALSE;
		}
		else
		{
			pServiceKey[i].SkeyType=atoi(GetKeyList(sec,0));
			
			pServiceKey[i].serviceKey=atoi(GetKeyList(sec,1));
			if(pServiceKey[i].SkeyType==0)//PSTN
			{
				memset(pServiceKey[i].sServiceKey.sPstn.initialNum,0,sizeof(pServiceKey[i].sServiceKey.sPstn.initialNum));
				pServiceKey[i].sServiceKey.sPstn.initialtype =(BYTE)atoi(GetKeyList(sec,2));
				strcpy(pServiceKey[i].sServiceKey.sPstn.initialNum,GetKeyList(sec,3));
				pServiceKey[i].sServiceKey.sPstn.mask = (BYTE)atoi(GetKeyList(sec,4));
				pServiceKey[i].nodeTotal = (BYTE)atoi(GetKeyList(sec,5));
				strcpy(pServiceKey[i].description,GetKeyList(sec,(BYTE)(pServiceKey[i].nodeTotal+6)));
			}
			else
			{
				memset(pServiceKey[i].sServiceKey.sTcp.Ip,0,sizeof(pServiceKey[i].sServiceKey.sTcp.Ip));
				pServiceKey[i].sServiceKey.sTcp.port =(WORD)atoi(GetKeyList(sec,3));
				strcpy(pServiceKey[i].sServiceKey.sTcp.Ip,GetKeyList(sec,2));
				pServiceKey[i].nodeTotal = (BYTE)atoi(GetKeyList(sec,4));
				strcpy(pServiceKey[i].description,GetKeyList(sec,(BYTE)(pServiceKey[i].nodeTotal+5)));
			}
			
			pServiceKey[i].bLoad = TRUE;
			pServiceKey[i].bStart = TRUE;
			
		}
		ReadServiceConfig(i,(WORD)pServiceKey[i].serviceKey);
	}
	/*end*/
	freeINI(&ini);
	return TRUE;
}


/********************************************************************
函数名称：InitMerroy
函数功能: 分配业务内存, 按种类分
参数定义: 无
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-6-11 17:28
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL InitMerroy()
{	
	int i;
	g_MESSAGE_XSCM_SLAVE = (MESSAGE_XSCM_SLAVE *)malloc(sizeof(MESSAGE_XSCM_SLAVE) * MESSAGE_MAX);
	if(g_MESSAGE_XSCM_SLAVE == NULL)
	{
		return FALSE;
	}
	for(i=0;i<MESSAGE_MAX;i++)
	{
		memset(&g_MESSAGE_XSCM_SLAVE[i],0,sizeof(MESSAGE_XSCM_SLAVE));
		g_MESSAGE_XSCM_SLAVE[i].slavehead.sibStack.top=-1;
	}
	return TRUE;
}
/********************************************************************
函数名称：initService
函数功能: 初始化业务线程的各个变量
参数定义: 无
返回定义: 无
创建时间: 2008-6-11 16:40
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void initService()
{
	pServiceKey = NULL;
	pXscmSlp = NULL;
	ServiceKey_Total = 0;
	g_MESSAGE_XSCM_SLAVE = NULL;
	bInital = FALSE;
}
/********************************************************************
函数名称：GetBusyTimeId
函数功能: 查找忙碌的定时器
参数定义: senssionID:会话号
返回定义: 成功返回定时器的ID号，失败返回-1
创建时间: 2008-9-22 10:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int GetBusyTimeId(WORD senssionID)
{
	int timerId = -1;
	int i;
	MESSAGE_XSCM_SLAVE pMessage;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return timerId;
	for(i=0;i<TIMER_MAX;i++)
	{
		if(pMessage.slavehead.timerevent[i].bUsed)
		{
			timerId = pMessage.slavehead.timerevent[i].timerId;
			pMessage.slavehead.timerevent[i].bUsed = FALSE;
			pMessage.slavehead.timerevent[i].timerId = 0;
			UpdateXSCMMessage(senssionID,&pMessage,FALSE);
			break;
		}
	}
	return timerId;
}
/********************************************************************
函数名称：SvrMan_SetConstValue
函数功能: 设置常量值
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-12 14:56
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL SvrMan_SetConstValue(WORD senssionID)
{
	int serviceID;
	MESSAGE_XSCM_SLAVE pMessage;
	stSERMAN_VALUE *psValue;
	TID  pTid;
	int offset;
	WORD datalen = 0;
	int len;
	XSCM_CONSTVARIABLE sConstVar;
	BYTE value[1024];
	memset(&sConstVar,0,sizeof(XSCM_CONSTVARIABLE));
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	psValue=(stSERMAN_VALUE *)pMessage.messageInfor.data;
	if(psValue == NULL)
		return FALSE;
	serviceID=GetServiceKeyIdByKey(psValue->serviceID);
	if(!GetParamAttr((BYTE)serviceID,psValue->no,&sConstVar,TRUE))
		return FALSE;
	len  = GetDataTypeLen((BYTE)sConstVar.type)*sConstVar.length;
	if((serviceID==-1)||(len<psValue->datalen))
	{
		pMessage.messageInfor.head.event = evSerMan_ErrorReport;
		pMessage.messageInfor.head.len = 0;
		memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	}
	
	else
	{
		
		if(sConstVar.type==Var_BYTESTR)
		{
			offset=sConstVar.offset;
			memset(value,0,sizeof(value));
			memcpy(value,psValue->pValue,psValue->datalen);
			
			datalen = psValue->datalen ;
		}
		else
		{
			offset=sConstVar.offset;
			memcpy(value,psValue->pValue,psValue->datalen);
			datalen = psValue->datalen ; 
			
		}
		if(!SetConstValue((BYTE)serviceID,value,offset,datalen))
		{
			pMessage.messageInfor.head.event = evSerMan_ErrorReport;
			pMessage.messageInfor.head.len = 0;
			memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
		}
		else
		{
			pMessage.messageInfor.head.event = evSerMan_SetConstValueAck;
		}
	}
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_SLP,&pTid);
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&pMessage.messageInfor,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX+1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称：SvrMan_GetConstValue
函数功能: 获取常量值
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-12 14:56
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL SvrMan_GetConstValue(WORD senssionID)
{
	int serviceID;
	MESSAGE_XSCM_SLAVE pMessage;
	stSERMAN_VALUE *psValue;
	XSCM_CONSTVARIABLE sConstVar;
	BYTE data[1024];
	//	WORD nLen;
	TID  pTid;
	memset(&sConstVar,0,sizeof(XSCM_CONSTVARIABLE));
	
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	psValue=(stSERMAN_VALUE *)pMessage.messageInfor.data;
	if(psValue == NULL)
		return FALSE;
	serviceID=GetServiceKeyIdByKey(psValue->serviceID);
	if(serviceID==-1)
	{
		pMessage.messageInfor.head.event = evSerMan_ErrorReport;
		pMessage.messageInfor.head.len = 0;
		memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	}
	else
	{
		memset(data,0,sizeof(data));
		if(!GetParamAttr((BYTE)serviceID,psValue->no,&sConstVar,TRUE))
			return FALSE;
		if(!GetConstValue((BYTE)serviceID,data,(DWORD)sConstVar.offset,(WORD)(GetDataTypeLen((BYTE)sConstVar.type)*sConstVar.length)))
		{
			pMessage.messageInfor.head.event = evSerMan_ErrorReport;
			pMessage.messageInfor.head.len = 0;
			memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
		}
		else
		{
			pMessage.messageInfor.head.len = GetDataTypeLen((BYTE)sConstVar.type)*sConstVar.length;
			
			if(psValue->datalen>=sizeof(data))
			{
				psValue->datalen=(BYTE)(sizeof(data));
				pMessage.messageInfor.head.len = sizeof(data);
			}
			else
			{
				pMessage.messageInfor.head.len = psValue->datalen;
				
			}
			
			memcpy(pMessage.messageInfor.data,data,pMessage.messageInfor.head.len);
			pMessage.messageInfor.head.event = evSerMan_GetConstValueAck;
		}
	}
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_SLP,&pTid);
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&pMessage.messageInfor,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX+1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称：SvrMan_UpdateService
函数功能: 设置业务状态
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-15 10:18
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL SvrMan_UpdateService(WORD senssionID)
{
	BYTE status = 0;
	MESSAGE_XSCM_SLAVE pMessage;
	stSERMAN_STATUS *pStatus;
	BOOL b = FALSE;
	TID pTid;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	pStatus = (stSERMAN_STATUS *)pMessage.messageInfor.data;
	if(pStatus == NULL)
		return FALSE;
	status = pStatus->operStatus;
	switch(status)
	{
	case WriteConfig://写配置文件
		b = WriteServiceconfig(pStatus->serviceID);
		break;
	case LoadService://  加载业务
		b = UpdateServiceStatus(pStatus->serviceID,TRUE);
		break;
	case UnloadService://  卸载业务
		b = UpdateServiceStatus(pStatus->serviceID,FALSE);
		break;
	case StopServiceRun:  //  停止业务
		b=UpdateServiceActive(pStatus->serviceID,FALSE);
		break;
	case  ActiveService: //  激活业务
		b=UpdateServiceActive(pStatus->serviceID,TRUE);
		break;
	default:
		break;
	}
	if(!b)
	{
		pMessage.messageInfor.head.event = evSerMan_ErrorReport;
		pMessage.messageInfor.head.len = 0;
		memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	}
	else
	{
		pMessage.messageInfor.head.event = evSerMan_SetStatusAck;
	}
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_SLP,&pTid);
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&pMessage.messageInfor,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX-1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称：SvrMan_GetConstAttr
函数功能: 获取常量的基本属性
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-3-23 9:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL SvrMan_GetConstAttr(WORD senssionID)
{
	BYTE status = 0;
	MESSAGE_XSCM_SLAVE pMessage;
	stSERMAN_ASKCONSTATTR *pAskConstAttr;
	int serviceID;
	WORD  starteId;//常量起始编号
	BYTE    btotal;//读取常量属性个数
	TID pTid;
	stSERMAN_ASKCONSTATTRAck sConstAttrAck;
	sConstAttrAck.pConstAttr=NULL;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	pAskConstAttr = (stSERMAN_ASKCONSTATTR *)pMessage.messageInfor.data;
	if(pAskConstAttr == NULL)
		return FALSE;
	serviceID=GetServiceKeyIdByKey(pAskConstAttr->serviceID);
	if(serviceID==-1)
	{
		pMessage.messageInfor.head.event = evSerMan_ErrorReport;
		pMessage.messageInfor.head.len = 0;
		memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	}
	else
	{
		starteId = pAskConstAttr->starteId;
		btotal = pAskConstAttr->btotal;
		if(starteId>=(pXscmSlp+serviceID)->sBasic.nConstVariableTotal)
		{
			
			btotal=0;
			
		}
		if((starteId+btotal)>(pXscmSlp+serviceID)->sBasic.nConstVariableTotal)//
		{
			btotal=(pXscmSlp+serviceID)->sBasic.nConstVariableTotal-starteId;
		}
		if((btotal==0)||((pXscmSlp+serviceID)->pConstAtte==NULL))
		{
			pMessage.messageInfor.head.event = evSerMan_ErrorReport;
			pMessage.messageInfor.head.len = 0;
			memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
		}
		else
		{
			sConstAttrAck.serviceID=pAskConstAttr->serviceID;
			sConstAttrAck.starteId = starteId;
			sConstAttrAck.btotal = btotal;
			//对常量配置信息
			sConstAttrAck.pConstAttr=(XSCM_CONSTVARIABLE*)malloc(btotal*sizeof(XSCM_CONSTVARIABLE));
			if(sConstAttrAck.pConstAttr==NULL)
			{
				pMessage.messageInfor.head.event = evSerMan_ErrorReport;
				pMessage.messageInfor.head.len = 0;
				memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
			}
			else
			{
				memset(sConstAttrAck.pConstAttr,0,btotal*sizeof(XSCM_CONSTVARIABLE));
				memcpy(sConstAttrAck.pConstAttr,(pXscmSlp+serviceID)->pConstAtte+starteId*sizeof(XSCM_CONSTVARIABLE),btotal*sizeof(XSCM_CONSTVARIABLE));
				pMessage.messageInfor.head.len = sizeof(stSERMAN_ASKCONSTATTR)+btotal*sizeof(XSCM_CONSTVARIABLE)-sizeof(sConstAttrAck.pConstAttr);
				memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
				memcpy(pMessage.messageInfor.data,&sConstAttrAck,sizeof(stSERMAN_ASKCONSTATTR)-sizeof(sConstAttrAck.pConstAttr));
				memcpy(pMessage.messageInfor.data+sizeof(stSERMAN_ASKCONSTATTR)-sizeof(sConstAttrAck.pConstAttr)
					,sConstAttrAck.pConstAttr,btotal*sizeof(XSCM_CONSTVARIABLE));
				pMessage.messageInfor.head.event = evSerMan_GetConstAttrAck;
				free(sConstAttrAck.pConstAttr);
				sConstAttrAck.pConstAttr = NULL;
			}
		}
		
	}
	pAskConstAttr = NULL;
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_SLP,&pTid);
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&pMessage.messageInfor,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX-1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称：SvrMan_GetAllConstAttr
函数功能: 获取常量的整体属性
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-3-23 9:55
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL SvrMan_GetAllConstAttr(WORD senssionID)
{
	BYTE status = 0;
	MESSAGE_XSCM_SLAVE pMessage;
	stSERMAN_ASKALLCONSTATTR *pAskAllConstAttr;
	int serviceID;
	TID pTid;
	stSERMAN_ASKALLCONSTATTRAck sConstAllAttrAck;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	pAskAllConstAttr = (stSERMAN_ASKALLCONSTATTR *)pMessage.messageInfor.data;
	if(pAskAllConstAttr == NULL)
		return FALSE;
	serviceID=GetServiceKeyIdByKey(pAskAllConstAttr->serviceID);
	if(serviceID==-1)
	{
		pMessage.messageInfor.head.event = evSerMan_ErrorReport;
		pMessage.messageInfor.head.len = 0;
		memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	}
	else
	{
		sConstAllAttrAck.serviceID=pAskAllConstAttr->serviceID;
		sConstAllAttrAck.ntotal = (pXscmSlp+serviceID)->sBasic.nConstVariableTotal;
		pMessage.messageInfor.head.len = sizeof(stSERMAN_ASKALLCONSTATTRAck);
		memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
		memcpy(pMessage.messageInfor.data,&sConstAllAttrAck,sizeof(stSERMAN_ASKALLCONSTATTRAck));
		pMessage.messageInfor.head.event = evSerMan_GetAllConstAttrAck;
	}
	pAskAllConstAttr = NULL;
	if(!GetTid(MID_SVRMAN,&pTid))
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	GetTid(MID_SLP,&pTid);
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	if(!SEND_MSG(evIPCMessage,MID_SVRMAN,(void *)&pMessage.messageInfor,(WORD)(sizeof(MESSAGE)),(WORD)(MESSAGE_MAX-1),FALSE,NULL,0))
		return FALSE;
	return TRUE;
}
/********************************************************************
函数名称：UpdateServiceStatus
函数功能: 设置业务加载/卸载状态
参数定义: serviceID:业务ID，bLoad:加载/卸载
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-15 11:12
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateServiceStatus(DWORD serviceID,BOOL bLoad)
{
	int no;
	no=GetServiceKeyIdByKey(serviceID);
	if((no>=0)&&(no<ServiceKey_Total))
	{
		pServiceKey[no].bLoad = bLoad;
		return TRUE;
	}
	else
		return FALSE;
}
BOOL UpdateServiceActive(DWORD serviceID,BOOL bStart)
{
	int no;
	no=GetServiceKeyIdByKey(serviceID);
	if((no>=0)&&(no<ServiceKey_Total))
	{
		pServiceKey[no].bStart = bStart;
		return TRUE;
	}
	else
		return FALSE;
}
/********************************************************************
函数名称：WriteServiceconfig
函数功能: 写业务文件
参数定义: serviceID:业务ID
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-15 11:15
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL WriteServiceconfig(DWORD serviceID)
{
	int no;
	no=GetServiceKeyIdByKey(serviceID);
	if((no>=0)&&(no<ServiceKey_Total))
	{
		
		return TRUE;
	}
	else
		return FALSE;
}
/********************************************************************
函数名称：UpdataOdiData
函数功能: 更新ODI数据 
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-3-30 11:20
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdataOdiData(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	XSCM_CONSTVARIABLE sVarible;
	ODIMSG_ACK odiAck;
	BOOL bFind = FALSE;
	int timeId;
	BYTE data[1024];
	BYTE serviceID;
	WORD llen;
	int i;
	int offset;
	int len;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	memset(&sVarible,0,sizeof(XSCM_CONSTVARIABLE));
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	
	memcpy(&odiAck,pMessage.messageInfor.data,pMessage.messageInfor.head.len);
	offset=0;
	for(i=0;i<pMessage.sVaribleSave.bVaribleTotal;i++)
	{
		if(GetParamAttr(serviceID,(WORD)pMessage.sVaribleSave.dVaribleNo[i],&sVarible,FALSE))
		{
			len  = GetDataTypeLen((BYTE)sVarible.type)*sVarible.length;
			memset(data,0,sizeof(data));	
			llen = pMessage.messageInfor.head.len-1;
			if(offset>=llen)
			{
				break;
			}
			if((offset+len)>=llen)
				len=llen-offset;
			
			memcpy(data,odiAck.cmsg.buf+offset,len);
			SetVaribleValue(senssionID,data,sVarible.offset,(WORD)(len));
			offset=offset+len;
			
		}	
	}
	
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	return TRUE;
}
/***************************
函数名称：UpdateCallOutData
函数功能: 更新外呼数据
参数定义:
返回定义: 无
创建时间: 2009/12/21  13:39:47
函数作者: LDW
注意事项: 无
****************************/
BOOL UpdateCallOutData(WORD senssionID)
{
	stPXSSM_CallOutInfor pCallOutInfor;
	MESSAGE_XSCM_SLAVE pMessage;
	XSCM_CONSTVARIABLE sVarible;
	BOOL bFind = FALSE;
	int timeId;
	BYTE serviceID;
	BYTE data[1024];
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(pMessage.messageInfor.head.len<sizeof(stPXSSM_CallOutInfor))
		memcpy(&pCallOutInfor,pMessage.messageInfor.data,pMessage.messageInfor.head.len);
	else
		memcpy(&pCallOutInfor,pMessage.messageInfor.data,sizeof(stPXSSM_CallOutInfor));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	
	if(sizeof(stPXSSM_CallOutInfor)<sizeof(pMessage.messageInfor.data))
	{
		memcpy(pMessage.messageInfor.data,&pCallOutInfor,sizeof(stPXSSM_CallOutInfor));
		pMessage.messageInfor.head.len = sizeof(stPXSSM_CallOutInfor);
	}
	else
	{
		memcpy(pMessage.messageInfor.data,&pCallOutInfor,sizeof(pMessage.messageInfor.data));
		pMessage.messageInfor.head.len = sizeof(pMessage.messageInfor.data);
	}
	
	if(pMessage.sVaribleSave.bVaribleTotal==1)
	{
		if(GetParamAttr(serviceID,(WORD)pMessage.sVaribleSave.dVaribleNo[0],&sVarible,FALSE))
		{
			memset(data,0,sizeof(data));
			
			memcpy(data,&pCallOutInfor.pType,sizeof(pCallOutInfor.pType));
			SetVaribleValue(senssionID,data,sVarible.offset,(WORD)(sizeof(pCallOutInfor.pType)));
		}
	}
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	return TRUE;
	
}

/********************************************************************
函数名称：UpdateDataTCPInit
函数功能: PSTN呼入初始化
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-7 10:22
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateDataTCPInit(WORD senssionID)
{
	stTCP_InitTcp *dp=NULL;
	MESSAGE_XSCM_SLAVE  pMessage;
	BOOL bFind = FALSE;
	BYTE serviceID;
	int len=sizeof(stTCP_InitTcp);
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	dp=(stTCP_InitTcp *)pMessage.messageInfor.data; 
	serviceID = GetServiceKeyIdByKeyAndType(dp->serviceKey,1);
	if(serviceID<0)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(serviceID>=ServiceKey_Total)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!pServiceKey[serviceID].bLoad)//业务在卸载状态不能运行下面的操作
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!pServiceKey[serviceID].bStart)//业务在卸载状态不能运行下面的操作
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.slavehead.serviceId = serviceID;
	pMessage.slavehead.serviceKey = (int)dp->serviceKey;
	pMessage.slavehead.sibNo = 0;
	
	pMessage.pVarileValue = NULL;
	pMessage.variblelen = pXscmSlp[serviceID].sBasic.nVariableLen;
	memcpy(&pMessage.slavehead.serInit.sInit.init_tcp,dp,sizeof(stTCP_InitTcp));
	pMessage.slavehead.serInit.servicetype=1;//tcp
	UpdateAndInitXSCMMessage(senssionID,&pMessage);// 
	
	return TRUE;
}
/********************************************************************
函数名称：UpdateDataPstnInit
函数功能: PSTN呼入初始化
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-7 10:22
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateDataPstnInit(WORD senssionID)
{
	stPXSSM_InitiateDP *dp=NULL;
	MESSAGE_XSCM_SLAVE  pMessage;
	BOOL bFind = FALSE;
	BYTE serviceID;
	int serviceKey;
	
	BOOL NoProCode = FALSE;
	int len=sizeof(stPXSSM_InitiateDP);
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	dp=(stPXSSM_InitiateDP *)pMessage.messageInfor.data; 
	NoProCode = GetServiceKeyIdAndKey(dp->calledNumber,dp->callingNumber,&serviceID,&serviceKey);
	if(!NoProCode )
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(serviceID>=ServiceKey_Total)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!pServiceKey[serviceID].bLoad)//业务在卸载状态不能运行下面的操作
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!pServiceKey[serviceID].bStart)//业务在卸载状态不能运行下面的操作
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.slavehead.serviceId = serviceID;
	pMessage.slavehead.serviceKey = (BYTE)serviceKey;
	pMessage.slavehead.sibNo = 0;
	
	pMessage.pVarileValue = NULL;
	pMessage.variblelen = pXscmSlp[serviceID].sBasic.nVariableLen;
	memcpy(&pMessage.slavehead.serInit.sInit.init_pstn,dp,sizeof(stPXSSM_InitiateDP));
	pMessage.slavehead.serInit.servicetype=0;
	UpdateAndInitXSCMMessage(senssionID,&pMessage);// 
	
	return TRUE;
}
/********************************************************************
函数名称：UpdateDataOdiAccess
函数功能: 更新ODI呼入数据
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-3-30 11:37
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateDataOdiAccess(WORD senssionID)
{
	stODI_InitODI dp;
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind = FALSE;
	int sid;
	TID pTid;
	BYTE serviceID;
	UINT bReceiveModule;
	BOOL bSuc = FALSE;
	stODI_InitODI_Ack sAck;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memset(&dp,0,sizeof(dp));
	memcpy(&dp,pMessage.messageInfor.data,sizeof(stODI_InitODI));
	sAck.id=dp.id;
	sid=GetSenssionID();//分配会话号
	if((sid<0)||(dp.servicetype<10))//分配会话号失败
	{
		sAck.state=0;
	}
	else//分配会话号成功
	{
		sAck.state=1;
	}
	sAck.sid=sid;
	pMessage.messageInfor.head.len=sizeof(stODI_InitODI_Ack);
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	if(!GetTid(bReceiveModule,&pTid))
	{
		return FALSE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		return FALSE;
	}
	pMessage.messageInfor.head.event=evODI_Access_Ack;
	pMessage.slavehead.oldevent = evODI_Access_Ack;
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&sAck,sizeof(stODI_InitODI_Ack));
	UpdateXSCMMessage((WORD)sid,&pMessage,FALSE);
	
	bSuc =SEND_MSG(evIPCMessage,bReceiveModule,(void *)&pMessage.messageInfor,sizeof(MESSAGE),(WORD)sid,FALSE,NULL,0);
	if((sid<0)||(dp.servicetype<10))
		return FALSE;
	
	
	serviceID = GetServiceKeyIdByKeyAndType(dp.serviceKey,(int)dp.serviceKey);
	if(serviceID<0)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(serviceID>=ServiceKey_Total)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!pServiceKey[serviceID].bLoad)//业务在卸载状态不能运行下面的操作
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!pServiceKey[serviceID].bStart)//业务在卸载状态不能运行下面的操作
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.slavehead.serviceId = serviceID;
	pMessage.slavehead.serviceKey = (int)dp.serviceKey;
	pMessage.slavehead.sibNo = 0;
	
	pMessage.pVarileValue = NULL;
	pMessage.variblelen = pXscmSlp[serviceID].sBasic.nVariableLen;
	memcpy(&pMessage.slavehead.serInit.sInit.init_tcp,&dp,sizeof(stODI_InitODI));
	pMessage.slavehead.serInit.servicetype=dp.servicetype;//odi
	UpdateAndInitXSCMMessage((WORD)sid,&pMessage);// 
	return TRUE;
}
/********************************************************************
函数名称：UpdateCollectInformation
函数功能: 更新DTMF信息，判断业务功能
参数定义: senssionID:会话号
返回定义: 成功返回TRUE,失败返回FALSE;
创建时间: 2008-9-19 14:28
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL UpdateCollectInformation(WORD senssionID)
{
	stPXSSM_CollectedInformation pCollectedInformation;
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind = FALSE;
	BOOL bFinished = FALSE;
	int len;
	int timeId;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	memset(&pCollectedInformation,0,sizeof(stPXSSM_CollectedInformation));
	memcpy(&pCollectedInformation,pMessage.messageInfor.data,sizeof(stPXSSM_CollectedInformation));
	len=strlen(pCollectedInformation.collectedDigits);
	if(pMessage.sVaribleSave.bVaribleTotal==1)
	{
		SetVaribleValue(senssionID,pCollectedInformation.collectedDigits,pMessage.sVaribleSave.dVaribleNo[0],(WORD)len);
	}
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	return TRUE;
}
/********************************************************************
函数名称：UpdateTcpReceiveData
函数功能: 解析TCP接收到的数据
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-7 17:15
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateTcpReceiveData(WORD senssionID)
{
	stTCP_RecvData pRecvData;
	MESSAGE_XSCM_SLAVE pMessage;
	XSCM_CONSTVARIABLE sVarible;
	BOOL bFind = FALSE;
	int timeId;
	BYTE serviceID;
	BYTE data[1024];
	int len;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(pMessage.messageInfor.head.len<sizeof(stTCP_RecvData))
		memcpy(&pRecvData,pMessage.messageInfor.data,pMessage.messageInfor.head.len);
	else
		memcpy(&pRecvData,pMessage.messageInfor.data,sizeof(stTCP_RecvData));
	
	
	if(pMessage.sVaribleSave.bVaribleTotal>=2)
	{
		if(GetParamAttr(serviceID,(WORD)pMessage.sVaribleSave.dVaribleNo[0],&sVarible,FALSE))
		{
			len  = GetDataTypeLen((BYTE)sVarible.type)*sVarible.length;
			if(sizeof(pRecvData.datalen)>len)
				len=sizeof(pRecvData.datalen);
			SetVaribleValue(senssionID,(BYTE *)&pRecvData.datalen,sVarible.offset,(WORD)(len));
			
		}
		if(GetParamAttr(serviceID,(WORD)pMessage.sVaribleSave.dVaribleNo[1],&sVarible,FALSE))
		{
			len  = GetDataTypeLen((BYTE)sVarible.type)*sVarible.length;
			memset(data,0,sizeof(data));
			if(pRecvData.datalen>len)
				pRecvData.datalen = len;
			
			memcpy(data,pRecvData.data,pRecvData.datalen);
			SetVaribleValue(senssionID,data,sVarible.offset,(WORD)(pRecvData.datalen));
			
		}
	}
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	return TRUE;
}
/********************************************************************
函数名称：UpdateCollectFskInformation
函数功能:转换FSK内容
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-9-19 14:27
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL UpdateCollectFskInformation(WORD senssionID)
{
	stPXSSM_CollectedFSKInformation pCollectedFskInformation;
	MESSAGE_XSCM_SLAVE pMessage;
	XSCM_CONSTVARIABLE sVarible;
	BOOL bFind = FALSE;
	int timeId;
	BYTE serviceID;
	BYTE data[1024];
	int len;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(pMessage.messageInfor.head.len<sizeof(stPXSSM_CollectedFSKInformation))
		memcpy(&pCollectedFskInformation,pMessage.messageInfor.data,pMessage.messageInfor.head.len);
	else
		memcpy(&pCollectedFskInformation,pMessage.messageInfor.data,sizeof(stPXSSM_CollectedFSKInformation));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	if(pCollectedFskInformation.fsk.length>0)
	{
		if(pCollectedFskInformation.fsk.length<sizeof(pMessage.messageInfor.data))
		{
			memcpy(pMessage.messageInfor.data,pCollectedFskInformation.fsk.message,pCollectedFskInformation.fsk.length);
			pMessage.messageInfor.head.len = pCollectedFskInformation.fsk.length;
		}
		else
		{
			memcpy(pMessage.messageInfor.data,pCollectedFskInformation.fsk.message,sizeof(pMessage.messageInfor.data));
			pMessage.messageInfor.head.len = sizeof(pMessage.messageInfor.data);
		}
	}
	
	if(pMessage.sVaribleSave.bVaribleTotal==1)
	{
		if(GetParamAttr(serviceID,(WORD)pMessage.sVaribleSave.dVaribleNo[0],&sVarible,FALSE))
		{
			len  = GetDataTypeLen((BYTE)sVarible.type)*sVarible.length;
			memset(data,0,sizeof(data));
			if(pCollectedFskInformation.fsk.length>len)
				pCollectedFskInformation.fsk.length = len;
			
			memcpy(data,pCollectedFskInformation.fsk.message,pCollectedFskInformation.fsk.length);
			SetVaribleValue(senssionID,data,sVarible.offset,(WORD)(pCollectedFskInformation.fsk.length));
			
		}
	}
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	return TRUE;
}
/********************************************************************
函数名称：ReleaseCall
函数功能: 释放会话
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-9-19 14:26
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL ReleaseCall(WORD senssionID)
{
	int timeId ;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	if(CheckCaseIsNow(senssionID))
		return FALSE;
	RelaseCheckCase(senssionID);
	UpdateXSCMMessage(senssionID,NULL,TRUE);
	
	ReleaseSenssionID(senssionID);
	return TRUE;
}
/********************************************************************
函数名称：EndSenssion
函数功能: 发送挂机FSK通知终端挂机
参数定义: wNumber：会话号
返回定义: 无
创建时间: 2008-9-19 15:05
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void EndSenssion(WORD senssionID,BYTE reason)
{
	BOOL bFind= FALSE;
	MESSAGE_XSCM_SLAVE pMessage;
	int timeId ;
	if(senssionID >= MESSAGE_MAX)
		return ;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return ;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	
	ControlReleaseCall((WORD)senssionID);
	
}
BOOL DoWithErrorReport(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	stPXSSM_ErrorReport errorReport;
	BOOL bFind = FALSE;
	BYTE reason;
	//	WORD oldtimer;
	int timeId;
	BOOL bTrue =FALSE;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memset(&errorReport,0,sizeof(stPXSSM_ErrorReport));
	memcpy(&errorReport,pMessage.messageInfor.data,sizeof(stPXSSM_ErrorReport));
	reason = errorReport.error;
	
	switch(reason)
	{
	case TimeOut:  // 超时
		{
			g_MESSAGE_XSCM_SLAVE[senssionID].accID=3;
			bTrue = TRUE;
			timeId = GetBusyTimeId(senssionID);//先取消定时器
			if(timeId >= 0)
				KILL_TIMER((WORD)timeId);
			OnTimerAck(senssionID);
			
		}
		break;
	case MissingSession:  //  找不到对应的会话
		{
			
			g_MESSAGE_XSCM_SLAVE[senssionID].accID=2;
				
			bTrue = FALSE;
			
		}
		break;
		
		
	default:
		break;
	}
	
	return bTrue;
}
/********************************************************************
函数名称：DoWithReport
函数功能: 事件报告处理
参数定义: senssionID：会话号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-9-19 15:19
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL DoWithReport(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	stPXSSM_ResourceReport resourceReport;
	BOOL bFind = FALSE;
	BYTE reason;
	//	WORD oldtimer;
	int timeId;
	BOOL bTrue =FALSE;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memset(&resourceReport,0,sizeof(stPXSSM_ResourceReport));
	memcpy(&resourceReport,pMessage.messageInfor.data,sizeof(stPXSSM_ResourceReport));
	//	timeId = GetBusyTimeId(senssionID);//先取消定时器
	//	if(timeId >= 0)
	//		KILL_TIMER((WORD)timeId);
	reason = resourceReport.reason;
	switch(reason)
	{
	case R_PlayAnnouncement:  // 放音完毕
		{
			bTrue = FALSE;
		}
		break;
	case R_SendFsk:  //  发送数据完毕
		{
			if(pMessage.slavehead.oldevent == evPXSSM_SendFSK)
			{
				timeId = GetBusyTimeId(senssionID);//先取消定时器
				if(timeId >= 0)
					KILL_TIMER((WORD)timeId);
				bTrue = TRUE;
			}
			else
				bTrue = FALSE;
		}
		break;
	case R_TcpSend://TCP发送数据完成
		{
			timeId = GetBusyTimeId(senssionID);//先取消定时器
			if(timeId >= 0)
				KILL_TIMER((WORD)timeId);
			bTrue = TRUE;
		}
		break;
	case R_InitiateRecord://  录音或者控制音完成
		bTrue = FALSE;
		break;
	case R_TTSPlay://TTS转换完毕
		bTrue = FALSE;
		break;
	default:
		break;
	}
	pMessage.slavehead.oldevent = 0;
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	return bTrue;
}

BOOL ControlReleaseCall(WORD senssionID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_ReleaseCall pReleaseCall;
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind = FALSE;
	TID pTid ;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&pMessage))
		return FALSE;
	
	pMessage.messageInfor.head.event=evPXSSM_ReleaseCall;
	pReleaseCall.sid=senssionID;
	pReleaseCall.reason = pMessage.slavehead.oldevent;
	pMessage.messageInfor.head.len=sizeof(stPXSSM_ReleaseCall);
	if(pMessage.slavehead.serInit.servicetype==0)//PSTN
	{
		if(!GetTid(MID_ACC,&pTid))
			return FALSE;
		bReceiveModule = MID_ACC;
	}
	else if(pMessage.slavehead.serInit.servicetype==1)//TCP
	{
		if(!GetTid(g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,&pTid))
			return FALSE;
		bReceiveModule = g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID;
	}
	else
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,&pTid))
		return FALSE;
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	if(sizeof(stPXSSM_ReleaseCall)<=sizeof(pMessage.messageInfor.data))
		memcpy(pMessage.messageInfor.data,&pReleaseCall,sizeof(stPXSSM_ReleaseCall));
	else
		memcpy(pMessage.messageInfor.data,&pReleaseCall,sizeof(pMessage.messageInfor.data));
	UpdateXSCMMessage(senssionID,&pMessage,TRUE);
	bSuc = SEND_MSG(evIPCMessage,bReceiveModule,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if(CheckCaseIsNow(senssionID))
		return FALSE;
	RelaseCheckCase(senssionID);
	if(!bSuc)
	{
		ReleaseSenssionID(senssionID);
	}
	return TRUE;
}
/********************************************************************
函数名称：SetWaiteTimer
函数功能:  设置定时器
参数定义: 无
返回定义: 无
创建时间: 2008-9-19 15:08
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void SetWaiteTimer(WORD senssionID,WORD timerevent,int intertime)
{
	int timerId;
	if(senssionID >= MESSAGE_MAX)
		return ;
	timerId = SET_TIMER(intertime,timerevent,MID_SLP,senssionID,FALSE);
	
	
}
void OnTimerAck(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE sMessage;
	BOOL bFind = FALSE;
	WORD sibNo;
	if(senssionID >= MESSAGE_MAX)
		return ;
	
	bFind=ReadXSCMMessage((WORD)senssionID,&sMessage);
	if(!bFind)
		return;
	//	if(sMessage.messageInfor.head.Source.node== 0)
	//		return;
	XVAPL_Pop(senssionID,&sibNo);
	UpdateNextSib((WORD)senssionID,(WORD)sMessage.slavehead.lastsibNo,FALSE);
	//	ControlEndSenssion((WORD)senssionID);
	
	
	
}

void ControlEndSenssion(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE sMessage;
	if(senssionID<MESSAGE_MAX)
	{
		ReadXSCMMessage(senssionID,&sMessage);
		
		EndSenssion(senssionID,(BYTE)sMessage.slavehead.oldevent);
		sMessage.slavehead.oldevent =0;
		UpdateXSCMMessage(senssionID,&sMessage,FALSE);
	}
	
}
void DBQueryResultNak(WORD senssionID)
{	
	MESSAGE_XSCM_SLAVE sMessage;
	BOOL bFind = FALSE;
	if(senssionID >= MESSAGE_MAX)
		return ;
	bFind=ReadXSCMMessage((WORD)senssionID,&sMessage);
	if(!bFind)
	{
		return ;
	}
	
	EndSenssion(senssionID,UnknowReason);
	
}
BOOL CheckCaseIsNow(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE sMessage;
	BOOL  bCheckStopRun= FALSE;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&sMessage))
		return FALSE;
	if(sMessage.sTestinit.bCheckCase)
		return TRUE;
	return FALSE;
	
}
/***************************************************************
函数名称：RelaseCheckCase
函数功能: 释放跟踪功能
参数定义:
返回定义: 无
创建时间: 2010/05/06  12:10:55
函数作者: LDW
注意事项: 无
****************************************************************/
void RelaseCheckCase(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE sMessage;
	int serviceId=-1;
	BOOL  bCheckStopRun= FALSE;
	if(senssionID >= MESSAGE_MAX)
		return ;
	if(!ReadXSCMMessage(senssionID,&sMessage))
		return ;
	serviceId=sMessage.slavehead.serviceId;
	if(pServiceKey[serviceId].sServiceTest.senssionID==senssionID)
	{
		memset(&pServiceKey[serviceId].sServiceTest,0,sizeof(pServiceKey[serviceId].sServiceTest));
	}
	else
	{
		bCheckStopRun=TRUE;
		serviceId=2;
	}
}
/***************************************************************
函数名称：CheckCaseTest
函数功能: 检查是否处于调试状态
参数定义:senssionID:会话号，将要执行的SIB编号
返回定义: 无
创建时间: 2010/06/02  10:54:19
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCaseTest(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE sMessage;
	int serviceId=-1;
	BOOL  bCheckStopRun= FALSE;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&sMessage))
		return FALSE;
	serviceId=sMessage.slavehead.serviceId;
	if(serviceId<ServiceKey_Total)
	{
		if(!pServiceKey[serviceId].sServiceTest.bTest)//看业务是否以设置为需要调试
			return FALSE;
		else
		{
			
			
			if(pServiceKey[serviceId].sServiceTest.bTestNow)//已经处于调试状态
			{
				if(pServiceKey[serviceId].sServiceTest.senssionID==senssionID)//和当前的会话号相等
				{
					if(sMessage.sTestinit.bCheckCase)
					{
						if(sibID==sMessage.sTestinit.stopsib)//和当前要执行的SIB编号相等
							bCheckStopRun= TRUE;
					}
					else
						return FALSE;
				}
			}
			else//未处于调试状态，需要判断
			{
				if(sMessage.slavehead.serInit.servicetype==pServiceKey[serviceId].SkeyType)//判断和当前运行的条件是否满足
				{
					if(pServiceKey[serviceId].SkeyType==0)//pstn
					{
						if(strcmp(pServiceKey[serviceId].sServiceTest.reference,sMessage.slavehead.serInit.sInit.init_pstn.callingNumber)==0)
						{
							sMessage.sTestinit.bCheckCase=TRUE;
							//	bCheckStopRun= TRUE;
						}
						else
							return FALSE;
					}
					else if(pServiceKey[serviceId].SkeyType==1)
					{
						if(strcmp(pServiceKey[serviceId].sServiceTest.reference,sMessage.slavehead.serInit.sInit.init_tcp.IP)==0)
						{
							sMessage.sTestinit.bCheckCase=TRUE;
							//	bCheckStopRun= TRUE;
						}
						else
							return FALSE;
					}
					else
						return FALSE;
					sMessage.sTestinit.stopsib=pServiceKey[serviceId].sServiceTest.stopsibno;
					
					if(sibID==pServiceKey[serviceId].sServiceTest.stopsibno)//和当前要执行的SIB编号相等
						bCheckStopRun= TRUE;
					else
						bCheckStopRun= FALSE;
					pServiceKey[serviceId].sServiceTest.bTestNow=TRUE;
					pServiceKey[serviceId].sServiceTest.senssionID=senssionID;
				}	
			}
		}
	}
	if(bCheckStopRun)//和当前要执行的SIB编号相等
		sMessage.sTestinit.bStopRun=TRUE;
	else 
		sMessage.sTestinit.bStopRun=FALSE;
	
	UpdateXSCMMessage((WORD)senssionID,&sMessage,FALSE);
	return bCheckStopRun;
	
}
/********************************************************************
created:	2009/06/22
created:	22:6:2009   22:40
filename: 	F:\江苏怡丰\XVAPL业务逻辑平台\源代码\SPP\xvapl_slp.c
file path:	F:\江苏怡丰\XVAPL业务逻辑平台\源代码\SPP
file base:	xvapl_slp
file ext:	c
author:		ldw

  purpose:	控制SIB执行流程
*********************************************************************/
void ControlRunSib(WORD senssionID,BOOL bNextStep)
{
	BOOL bRecallSib;
	bRecallSib = RunSib(senssionID,bNextStep);
	while(bRecallSib)
	{
		bRecallSib = RunSib(senssionID,TRUE);
	}
}
/********************************************************************
函数名称：RunSib
函数功能: 业务SIB管理
参数定义: senssionID:会话号，bNextStep：是否调用XVAPL_ReCallRunSib进行连续执行
返回定义: 无
创建时间: 2008-6-11 16:41
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL RunSib(WORD senssionID,BOOL bNextStep)
{
	int bSibType;
	MESSAGE_XSCM_SLAVE sMessage;
	SIB_BASIC sibBasic;
	BOOL IsReCall = FALSE;
	WORD sibNo;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	if(!ReadXSCMMessage(senssionID,&sMessage))
		return FALSE; 
	if(CheckCaseTest(senssionID,sMessage.slavehead.sibNo))
		return FALSE;
	//	if(sMessage.sTestinit.bCheckCase)
	//	{
	//		if(sMessage.sTestinit.bStopRun)
	//		{
	//			if(sMessage.sTestinit.stopsib==sMessage.slavehead.sibNo)
	//				return FALSE;
	//		}
	//	}
	if(!XVAPL_Pop(senssionID,&sibNo))
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
    memset(&sibBasic,0,sizeof(SIB_BASIC));
	GetSibBasicAttr(sMessage.slavehead.serviceId,sibNo,&sibBasic);//取得SIB的基本属性
	//{
	//	EndSenssion(senssionID,FatalFail);
	//	return FALSE;
	//}
	YF_TRACE_SLP senssionID,"","xvapl:sibNo=%d,sib_type=%d",sibNo,sibBasic.sibType);
	if(sibNo==0xFFFF)
	{
       sibBasic.sibType=tsibReleaseCall;
	}
	bSibType = sibBasic.sibType;
	switch(bSibType)
	{
	case tsibFun :
		IsReCall = Run_SIB_FUN(senssionID,sibNo);
		break;
	case tsibIf:
		IsReCall = Run_SIB_if(senssionID,sibNo);
		break;
	case tsibSwitch:
		IsReCall = Run_SIB_Switch(senssionID,sibNo);
		break;
	case tsibCommpare:
		IsReCall = Run_SIB_Compare(senssionID,sibNo);
		break;
	case tsibBCSM://设置BCSM
		IsReCall = Run_SIB_BCSM(senssionID,sibNo);
		break;
	case tsibReleaseCall://释放呼叫
		Run_SIB_ReleaseCall(senssionID,sibNo);
		//////////////////////////////////////////////////////////////////////////
		IsReCall = FALSE;
		break;
	case tsibPlayAnnouncement://放音
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_PlayAnnouncement(senssionID,sibNo);
		break;
	case tsibPromptCollectInformation://放音并设置接收DTMF
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_PromptCollectInformation(senssionID,sibNo);
		break;
	case tsibPromptCollectInformationAndSetFormat://设置接收DTMF格式
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_PromptCollectInformationAndSetFormat(senssionID,sibNo);
		break;
	case tsibPromptCollectFSK://放音并设置接收FSK
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_PromptCollectFSK(senssionID,sibNo);
		break;
	case tsibSendFSK://发送FSK
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_SendFSK(senssionID,sibNo);
		break;
	case tsibSendFSKCollectInformation://发送FSK并接收DTMF(设置DTMF格式)
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_SendFSKCollectInformation(senssionID,sibNo);
		break;
	case tsibSendFSKCollectFSK://发送FSK并接收FSK
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_SendFSKCollectFSK(senssionID,sibNo);
		break;
	case tsibInitiateRecord://设置录音
		IsReCall = Run_SIB_InitiateRecord(senssionID,sibNo);
		break;
	case tsibDBQuery://数据库查询 
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_DBQuery(senssionID,sibNo);
		break;
	case tsibMessage://消息
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_Message(senssionID,sibNo);
		break;
	case tsibTimer://超时
		//////////////////////////////////////////////////////////////////////////
		IsReCall = Run_SIB_Timer(senssionID,sibNo);
		break;
	case tsibLog://日志
		IsReCall = Run_SIB_Log(senssionID,sibNo);
		break;
	case tsibCdr://CDR记录
		IsReCall = Run_SIB_CDR(senssionID,sibNo);
		break;
	case tsibInitDAP://初始化
		IsReCall = Run_SIB_InitDAP(senssionID,sibNo);
		break;
	case tsibCallSib://回掉SIB
		IsReCall = Run_SIB_CallSib(senssionID,sibNo);
		break;
	case tsibReturnSib:
		IsReCall = TRUE;
		break;
	case tsibAttempCallOutSib://外呼SIB
		IsReCall = Run_SIB_CallOutSib(senssionID,sibNo);
		break;
	case tsibInitOdiSib://会话开始(ODI)
		IsReCall = Run_SIB_InitOdiSib(senssionID,sibNo);
		break;
	case tsibTcpLinkSib://TCP连接SIB
		IsReCall = Run_SIB_TcpLinkSib(senssionID,sibNo);
		break;
	case tsibInitTcpSib://会话开始(TCP)
		IsReCall = Run_SIB_InitTcpSib(senssionID,sibNo);
		break;
	case tsibTcpRecvDataSib://TCP接收数据
		IsReCall = Run_SIB_TcpRecvDataSib(senssionID,sibNo);
		break;
	case tsibTcpSendDataRecvDataSib://TCP发送数据接收数据
		IsReCall = Run_SIB_TcpSendDataRecvDataSib(senssionID,sibNo);
		break;
	case tsibDataToFixStructSib://数据固定结构解析
		IsReCall = Run_SIB_DataToFixStructSib(senssionID,sibNo);
		break;
	default :
		IsReCall = FALSE;
		break;
	}
	//	return IsReCall;
	if(bNextStep)
		return IsReCall;
	else
		return FALSE;
	//		XVAPL_ReCallRunSib(IsReCall,senssionID);
}
/********************************************************************
函数名称：XVAPL_ReCallRunSib
函数功能: 调用RunSib函数
参数定义: IsReCall:是否调用
senssionID：会话号	
返回定义: wu
创建时间: 2009-2-2 14:27
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void  XVAPL_ReCallRunSib(BOOL IsReCall,WORD senssionID)
{
	if(IsReCall)//调用RunSib函数
	{
		RunSib(senssionID,TRUE);	
	}
}
/********************************************************************
函数名称：Run_SIB_FUN
函数功能: 运算
参数定义: senssionID：会话号,sibID:sib编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL Run_SIB_FUN(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibFun)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(Fun_Operation_Control(senssionID,serviceID,sibID,&sibBasic))
		return TRUE;
	else 
		return FALSE;
}
/********************************************************************
函数名称：Fun_Operation_INTEGER
函数功能: 整数操作
参数定义: senssionID：会话号,serviceID：业务编号，
sibID:sib编号，bTimes:4*bTimes为操作运算的目标变量的参数编号
operType：操作类型
返回定义: 无
创建时间: 2008-8-27 10:51
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void Fun_Operation_INTEGER(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE operType)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return ;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return ;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return ;
	}
	switch(sVariable.type)
	{
	case 0://
		Fun_Operation_INTEGER_BYTE(senssionID,serviceID,sibID,sVariable.offset,bTimes,operType);
		break;
	case 1://
		Fun_Operation_INTEGER_BYTE(senssionID,serviceID,sibID,sVariable.offset,bTimes,operType);
		break;
	case 2:
		Fun_Operation_INTEGER_WORD(senssionID,serviceID,sibID,sVariable.offset,bTimes,operType);
		break;
	case 3:
		Fun_Operation_INTEGER_DWORD(senssionID,serviceID,sibID,sVariable.offset,bTimes,operType);
		break;
	default:
		break;
	}
}
/********************************************************************
函数名称：Fun_Operation_INTEGER_BYTE
函数功能: 整数操作(BYTE)
参数定义: senssionID：会话号,serviceID：业务编号，
sibID:sib编号，bTimes:4*bTimes为操作运算的目标变量的参数编号
operType：操作类型
返回定义: 无
创建时间: 2008-8-27 18:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Fun_Operation_INTEGER_BYTE(WORD senssionID,BYTE serviceID,WORD sibID,DWORD opsition,BYTE bTimes,BYTE operType)
{
	BYTE Result=0;
	BYTE Ops1 = 0;
	BYTE Ops2 = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&Ops1,sizeof(Ops1),(BYTE)(4*bTimes+1),0))
		return;
	GetFunctionVaribleValue(senssionID,serviceID,sibID,&Ops2,sizeof(Ops2),(BYTE)(4*bTimes+3),0);
	switch(operType)
	{
	case OPERATION_EQUL://=
		{
			Result = Ops1;
		}
		break;
	case OPERATION_ADD://+
		{
			Result = Ops1 + Ops2;
		}
		break;
	case OPERATION_SUB://-
		{
			Result = Ops1 - Ops2;
		}
		break;
	case OPERATION_MUL: //* 
		{
			Result = Ops1 * Ops2;
		}
		break;
	case OPERATION_DIV:  // / 
		{
			Result = Ops1 / Ops2;
		}
		break;
	case OPERATION_RESIDUAL: // % 
		{
			Result = Ops1 % Ops2;
		}
		break;
	case OPERATION_AND:   //&
		{
			Result = Ops1 & Ops2;
		}
		break;
	case OPERATION_OR :   //|
		{
			Result = Ops1 | Ops2;
		}
		break;
	case  OPERATION_XOR:   //^
		{
			Result = Ops1 ^ Ops2;
		}
		break;
	case OPERATION_NOT:   //!
		{
			Result = ~Ops1;
		}
		break; 
	default :
		return;
	}
	
	SetVaribleValue(senssionID,&Result,opsition,sizeof(BYTE));
}
/********************************************************************
函数名称：Fun_Operation_INTEGER_WORD
函数功能: 整数操作(WORD)
参数定义: senssionID：会话号,serviceID：业务编号，
sibID:sib编号，bTimes:4*bTimes为操作运算的目标变量的参数编号
operType：操作类型
返回定义: 无
创建时间: 2008-8-27 18:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Fun_Operation_INTEGER_WORD(WORD senssionID,BYTE serviceID,WORD sibID,DWORD opsition,BYTE bTimes,BYTE operType)
{
	WORD Result=0;
	WORD Ops1 = 0;
	WORD Ops2 = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Ops1,sizeof(Ops1),(BYTE)(4*bTimes+1),0))
		return;
	GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Ops2,sizeof(Ops2),(BYTE)(4*bTimes+3),0);
	switch(operType)
	{
	case OPERATION_EQUL://=
		{
			Result = Ops1;
		}
		break;
	case OPERATION_ADD://+
		{
			Result = Ops1 + Ops2;
		}
		break;
	case OPERATION_SUB://-
		{
			Result = Ops1 - Ops2;
		}
		break;
	case OPERATION_MUL: //* 
		{
			Result = Ops1 * Ops2;
		}
		break;
	case OPERATION_DIV:  // / 
		{
			Result = Ops1 / Ops2;
		}
		break;
	case OPERATION_RESIDUAL: // % 
		{
			Result = Ops1 % Ops2;
		}
		break;
	case OPERATION_AND:   //&
		{
			Result = Ops1 & Ops2;
		}
		break;
	case OPERATION_OR :   //|
		{
			Result = Ops1 | Ops2;
		}
		break;
	case  OPERATION_XOR:   //^
		{
			Result = Ops1 ^ Ops2;
		}
		break;
	case OPERATION_NOT:   //!
		{
			Result = ~Ops1;
		}
		break; 
	default :
		return;
	}
	SetVaribleValue(senssionID,(BYTE *)&Result,opsition,sizeof(WORD));
}
/********************************************************************
函数名称：Fun_Operation_INTEGER_DWORD
函数功能: 整数操作(DWORD)
参数定义: senssionID：会话号,serviceID：业务编号，
sibID:sib编号，bTimes:4*bTimes为操作运算的目标变量的参数编号
operType：操作类型
返回定义: 无
创建时间: 2008-8-27 18:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Fun_Operation_INTEGER_DWORD(WORD senssionID,BYTE serviceID,WORD sibID,DWORD opsition,BYTE bTimes,BYTE operType)
{
	DWORD Result=0;
	DWORD Ops1 = 0;
	DWORD Ops2 = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Ops1,sizeof(Ops1),(BYTE)(4*bTimes+1),0))
		return;
	GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Ops2,sizeof(Ops2),(BYTE)(4*bTimes+3),0);
	switch(operType)
	{
	case OPERATION_EQUL://=
		{
			Result = Ops1;
		}
		break;
	case OPERATION_ADD://+
		{
			Result = Ops1 + Ops2;
		}
		break;
	case OPERATION_SUB://-
		{
			Result = Ops1 - Ops2;
		}
		break;
	case OPERATION_MUL: //* 
		{
			Result = Ops1 * Ops2;
		}
		break;
	case OPERATION_DIV:  // / 
		{
			Result = Ops1 / Ops2;
		}
		break;
	case OPERATION_RESIDUAL: // % 
		{
			Result = Ops1 % Ops2;
		}
		break;
	case OPERATION_AND:   //&
		{
			Result = Ops1 & Ops2;
		}
		break;
	case OPERATION_OR :   //|
		{
			Result = Ops1 | Ops2;
		}
		break;
	case  OPERATION_XOR:   //^
		{
			Result = Ops1 ^ Ops2;
		}
		break;
	case OPERATION_NOT:   //!
		{
			Result = ~Ops1;
		}
		break; 
	default :
		return;
	}
	SetVaribleValue(senssionID,(BYTE *)&Result,opsition,sizeof(DWORD));
}
/********************************************************************
函数名称：
函数功能:
参数定义: 无
返回定义: 无
创建时间: 2008-8-27 18:50
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void ltrim(char *s)
{// 去掉前部的
    int l=0,p=0,k=0;
	if(s==NULL)
		return;
    l = strlen(s);
    if( l == 0 ) return;
    p = 0;
    while( s[p] == ' ' || s[p] == '\t' )  p++;
    if( p == 0 ) return;
    while( s[k] != '\0') s[k++] = s[p++];
    return;
}
/********************************************************************
函数名称：
函数功能:
参数定义: 无
返回定义: 无
创建时间: 2008-8-27 18:50
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void rtrim(char *s)
{// 去掉尾部的
    int l=0,p=0;
	if(s==NULL)
		return;
    l = strlen(s);
    if( l == 0 ) return;
    p = l -1;
    while( s[p] == ' ' || s[p] == '\t' ) {
        s[p--] = '\0';
        if( p < 0 ) break;
    }
    return;
}
/********************************************************************
函数名称：strLoWer
函数功能:将字符串转换成小写
参数定义: pFrom：需要转换的字符串，pTo：转换成小写之后的字符串
返回定义: wu
创建时间: 2009-9-2 13:04
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void strLoWer(char *pFrom,char *pTo)   
{   
	char   ch;   
	char   *p,*q;   
    
	p   =   pFrom;   
	q   =   pTo;   
	while(ch=*(p++))   
	{   
		if(ch>=0x41&&ch<=0x5a)   
			ch=ch+0x20;   
		*(q++) =ch;   
	}   
}   

/********************************************************************
函数名称：strUpper
函数功能:将字符串转换成大写
参数定义: pFrom：需要转换的字符串，pTo：转换成大写之后的字符串
返回定义: wu
创建时间: 2009-9-2 13:04
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void strUpper(char *pFrom,char *pTo)   
{   
	char   ch;   
	char   *p,*q;   
    
	p   =   pFrom;   
	q   =   pTo;   
	while(ch=*(p++))   
	{   
		if(ch>=0x61&&ch<=0x7a)   
			ch  = ch - 0x20;   
		*(q++) =ch;   
	}   
}   

/********************************************************************
函数名称：
函数功能:
参数定义: 无
返回定义: 无
创建时间: 2008-8-27 18:50
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void  strallcut(char *str)
{
	int        i,j=0;
	char sp[1024];
	if(str == NULL)
		return;
	for (i = 0; *(str + i) != '\0'; i++) {
		if (*(str + i) == ' ' )
			continue;
		sp[j++]=*(str + i);
	}
	sp[j] = 0;
	strcpy(str, sp);
}
/********************************************************************
函数名称：
函数功能:
参数定义: 无
返回定义: 无
创建时间: 2008-8-27 18:50
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void strplace(char *s,char ops1,char ops2)
{
	int i;
	int len = strlen(s);
	for(i=0;i<len;i++)
	{
		if(*(s+i) == ops1)
			*(s+i) = ops2;
	}
	return;
}
/********************************************************************
函数名称：
函数功能:
参数定义: 无
返回定义: 无
创建时间: 2008-8-27 18:50
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL IsAllNum(char *s)
{
	int i;
	int len;
	if(s == NULL)
		return FALSE;
	len = strlen(s);
	if(len==0)
		return FALSE;
	for (i=0 ; i<len ; i++) 
	{
		if((*(s+i)=='-')||(*(s+i)<'0')||(*(s+i)>'9'))
			return FALSE;
	}
	return TRUE;
}
/********************************************************************
函数名称：Fun_Operation_STRING
函数功能: 运算SIB的字符串操作
参数定义: senssionID：会话号,serviceID：业务编号，
sibID:sib编号，bTimes:4*bTimes为操作运算的目标变量的参数编号
operType：操作类型
返回定义: 无
创建时间: 2008-8-27 11:35
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Fun_Operation_STRING(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE operType)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	//	XSCM_CONSTVARIABLE sVariable1;
	char des[2048];
	char ops1[1024];
	char ops2[1024];
	BYTE des1[2048];
	WORD len = 0;
	int value=0,value1 =0;
	SYSTEMTIME stLocal;
	
	time_t ltime;
	time_t timep; 
	struct tm *p; 
	//	DWORD opp;
	struct tm when;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return ;
	//	if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
	//		return;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return ;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return ;
	}
	memset(des,0,sizeof(des));
	memset(des1,0,sizeof(des1));
	memset(ops1,0,sizeof(ops1));
	memset(ops2,0,sizeof(ops2));
	if((operType!=OPERATION_NOW)&&(operType!=OPERATION_SID)&&(operType!=OPERATION_CRUURENTSIB))
	{
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,ops1,sizeof(ops1),(BYTE)(4*bTimes+1),0))
			return;
	}
	
	switch(operType)//操作类型
	{
	case OPERATION_STRCAT:   //strcat 串相加
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,ops2,sizeof(ops2),(BYTE)(4*bTimes+3),0);
			strcpy(des,ops1);
			strcat(des,ops2);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRLEFT:   //left 求左边的子串
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0);
			memcpy(des,ops1,len);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRRIGHT:   //right 求右边的子串
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0);
			if(strlen(ops1)>=len)
				memcpy(des,ops1+strlen(ops1)-len,len);
			else
				memcpy(des,ops1,strlen(ops1));
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRLTRIM:   //去掉左空格
		{
			ltrim(ops1);
			strcpy(des,ops1);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRRTRIM:   //去掉右空格
		{
			rtrim(ops1);
			strcpy(des,ops1);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRTRIM:   //去掉两边的空格
		{
			strallcut(ops1);
			strcpy(des,ops1);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRLENGT:   //求串长度
		len = strlen(ops1);
		SetVaribleValue(senssionID,(BYTE *)&len,sVariable.offset,(WORD)(GetDataTypeLen((BYTE)sVariable.type)*sVariable.length));
		break;
	case OPERATION_STRPLACE:   //替换串
		{
			if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,des,sizeof(des),(BYTE)(4*bTimes),0))
				return;
			if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)ops2,sizeof(ops2),(BYTE)(4*bTimes+3),0))
				return;
			strplace(des,ops1[0],ops2[0]);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_STRATOI:   //将串按照指定的进制转换成整数
		{
			if(IsAllNum(ops1))
			{
				value1 = atoi(ops1);
				GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0);
				if(len == 16)//16进制
				{
					value=ConverHexData(value1);
					//						value = (value1/10)*16+value1%10;
				}
				else//10进制
				{
					value = value1;
				}
			}
			else
			{
				value = 0;
			}
			SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));
			
		}
		break;
	case OPERATION_STRITOA:   //将数值按照指定的进制转换成字符串
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&value,sizeof(value),(BYTE)(4*bTimes+1),0);
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0);
			_itoa(value,des,len);
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		}
		break;
	case OPERATION_NOW :   //取得当前时间，值为串
		{
			memset(&stLocal,0,sizeof(SYSTEMTIME));
			GetLocalTime(&stLocal);
			//				time(&timep); 
			//				p=gmtime(&timep); 
			//			   sprintf(des,"%04d-%02d-%02d %02d:%02d:%02d",(1900+p->tm_year), (1+p->tm_mon),p->tm_mday,p->tm_hour, p->tm_min, p->tm_sec);
			sprintf(des,"%04d-%02d-%02d %02d:%02d:%02d",stLocal.wYear, stLocal.wMonth,stLocal.wDay,stLocal.wHour, stLocal.wMinute, stLocal.wSecond);
		}
		SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length-1));
		
		break;
	case OPERATION_SECONDS :  //将时间串转换为秒数 
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&des,sizeof(des),(BYTE)(4*bTimes+1),0);
		if(!ConvertTime(des,&when))
			break;
		ltime=mktime(&when);
		SetVaribleValue(senssionID,(BYTE *)&ltime,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));
		break;
	case OPERATION_TIME :   //秒值转换为时间
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&timep,sizeof(timep),(BYTE)(4*bTimes+1),0);
			p=gmtime(&timep); 
			sprintf(des,"%04d-%02d-%02d %02d:%02d:%02d",(1900+p->tm_year), (1+p->tm_mon),p->tm_mday,p->tm_hour, p->tm_min, p->tm_sec);
			
		}
		SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));	
		break;
	case OPERATION_ENCRYPT:   //加密串
		break;
	case OPERATION_UNENCRYPT :   //解密串
		break;
	case OPERATION_SID   :   //取会话号
		SetVaribleValue(senssionID,(BYTE *)&senssionID,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));
		break;
		
	case OPERATION_CRUURENTSIB://取得当前的SIB编号
		SetVaribleValue(senssionID,(BYTE *)&sibID,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));
		break;
	case OPERATION_MEMST:
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&value,sizeof(value),(BYTE)(4*bTimes+1),0);
			GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0);
			if(len>=sVariable.length)
				len = sVariable.length;
			if(sVariable.type==Var_CHARSTR)
			{
				memset(des,value,sizeof(des));
				SetVaribleValue(senssionID,des,sVariable.offset,len);
			}
			else
			{
				memset(des1,value,sizeof(des1));
				SetVaribleValue(senssionID,des1,sVariable.offset,len);
			}
		}
		break;
	default:
		break;
	}
}
/********************************************************************
函数名称：ConverHexData
函数功能: 将整数转换成16进制数值
参数定义: data：需要转换的值
返回定义: 返回转换后的结果
创建时间: 2009-3-17 11:34
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int ConverHexData(int data)
{
	int value;
	BYTE data1;
	if(data>=100)
	{
		data1=data%100;
		value=(data1/10)*16+data1%10;
		if(data>=10000)
		{
			data1=(data%10000)/100;
			value=((data1/10)*16+data1%10)*256+value;
			if(data>1000000)
			{
				data1=(data%1000000)/10000;
				value=((data1/10)*16+data1%10)*256*256+value;
				//如果再大不处理
			}
			else
			{
				data1=(data%1000000)/10000;
				value=((data1/10)*16+data1%10)*256*256+value;
			}
		}
		else
		{
			data1 = data/100;
			value = ((data1/10)*16+data1%10)*256+value;
		}
	}
	else
		value=(data/10)*16+data%10;
	return value;
}
/********************************************************************
函数名称：ConvertTime
函数功能: 字符串向时间格式转换
参数定义: pValue：字符串，pWhen：返回时间结构函数
返回定义: 成功返回TRUE,拾芊祷谾ALSE
创建时间: 2009-3-17 10:21
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL ConvertTime(char *pValue,struct tm *pWhen)
{
	char value[4];
	if((pValue==NULL)||(pWhen==NULL))
		return FALSE;
	memset(pWhen,0,sizeof(struct tm));
	memset(value,0,sizeof(value));
	memcpy(value,pValue,4);
	pWhen->tm_year=atoi(value)-1900;
	memset(value,0,sizeof(value));
	memcpy(value,pValue+5,2);
	pWhen->tm_mon=atoi(value)-1;
	memset(value,0,sizeof(value));
	memcpy(value,pValue+8,2);
	pWhen->tm_mday=atoi(value);
	memset(value,0,sizeof(value));
	memcpy(value,pValue+11,2);
	pWhen->tm_hour=atoi(value);
	memset(value,0,sizeof(value));
	memcpy(value,pValue+14,2);
	pWhen->tm_min=atoi(value);
	memset(value,0,sizeof(value));
	memcpy(value,pValue+17,2);
	pWhen->tm_sec=atoi(value);
	return TRUE;
}
BYTE * SetMemBuffer(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,WORD *datalen)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len = 0;
	BYTE *pDes = NULL;
	int offset=0;
	if(senssionID>=MESSAGE_MAX)
		return NULL;
	if(serviceID>=ServiceKey_Total)
		return NULL;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return NULL;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return NULL;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes+1),FALSE))//
		return NULL;
	
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return NULL;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return NULL;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(WORD),(BYTE)(4*bTimes+3),0))
		return NULL;
	if((sVariable.length*(GetDataTypeLen((BYTE)sVariable.type))) < len)
		return NULL;
	if(sibParam.paramType == 3)//变量
	{
		
		if(sVariable.type == Var_BYTESTR)
		{
			pDes= g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue+sVariable.offset+len;
			datalen[0]=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type))-len;
		}
		else
		{
			pDes= g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue + sVariable.offset+len;
			datalen[0]=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type))-len;
		}
	}
	else if(sibParam.paramType == 4)//常量
	{
		if(sVariable.type == Var_BYTESTR)
		{
			pDes= (pXscmSlp+serviceID)->pConstVaribles+sVariable.offset+len;
			datalen[0]=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type))-len;
		}
		else
		{
			pDes= (pXscmSlp+serviceID)->pConstVaribles+sVariable.offset+len;
			datalen[0]=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type))-len;
		}
	}
	
	return pDes;	
}
BOOL BufferMemcpy(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pMem,BYTE bTimes,WORD datalen)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len = 0;
	WORD srcoffset;
	char des[2048];
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(pMem==NULL)
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&srcoffset,sizeof(srcoffset),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(pMem == NULL)
		return FALSE;
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	if(len>=datalen)
	{
		len=datalen;
	}
	memcpy(des,pMem/*+srcoffset*/,len);
	
	SetVaribleValue(senssionID,des,sVariable.offset,len);
	//	if(sVariable.type==Var_BYTESTR)
	//	{
	//		memcpy(des,&len,sizeof(WORD));
	//		len = len+2;
	//		SetVaribleValue(senssionID,des,sVariable.offset,len);
	//	}
	//	else
	//	{
	//		SetVaribleValue(senssionID,des+2,sVariable.offset,len);
	//	}
	//	if(len>=datalen)
	//	{
	//		if(sVariable.type==Var_BYTESTR)
	//		SetVaribleValue(senssionID,des,sVariable.offset,datalen);	
	
	//	}
	//	else
	//	{
	//		SetVaribleValue(senssionID,des,sVariable.offset,len);
	//		
	//	}
	
	
	return TRUE;
	
}
/********************************************************************
函数名称：SetDesBuffer
函数功能: 设置目标变量地址
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-1-8 16:46
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE * SetDesBuffer(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,WORD *des_len,BOOL *bDesByte/*,BYTE operType*/)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len = 0;
	BYTE *pDes = NULL;
	if(senssionID>=MESSAGE_MAX)
		return NULL;
	if(serviceID>=ServiceKey_Total)
		return NULL;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return NULL;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return NULL;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return NULL;
	//	if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
	//		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return NULL;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return NULL;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(WORD),(BYTE)(4*bTimes+1),0))
		return NULL;
	if(sVariable.length*(GetDataTypeLen((BYTE)sVariable.type)) < len)
		return NULL;
	des_len[0]=len;
	if(sibParam.paramType == 3)//变量
		pDes= g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue + sVariable.offset;
	else if(sibParam.paramType == 4)//常量
		pDes= (pXscmSlp+serviceID)->pConstVaribles+sVariable.offset;
	if(sVariable.type==Var_BYTESTR)
	{
		*bDesByte = TRUE;
	}
	else
	{
		*bDesByte = FALSE;
	}
	return pDes;	
}
/********************************************************************
函数名称：SetSrcBuffer
函数功能: 设置源变量地址
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-1-8 16:46
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE *SetSrcBuffer(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,WORD *src_len,BOOL *bSrcByte/*BYTE operType*/)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len = 0;
	BYTE *pSrc;
	pSrc = NULL;
	if(senssionID>=MESSAGE_MAX)
		return NULL;
	if(serviceID>=ServiceKey_Total)
		return NULL;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return NULL;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return NULL;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return NULL;
	//	if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
	//		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return NULL;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return NULL;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(sVariable.length*(GetDataTypeLen((BYTE)sVariable.type)) < len)
		return FALSE;
	src_len[0]=len;
	if(sibParam.paramType == 3)//变量
		pSrc= g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue + sVariable.offset;
	else if(sibParam.paramType == 4)//常量
		pSrc= (pXscmSlp+serviceID)->pConstVaribles+sVariable.offset;
	if(sVariable.type==Var_BYTESTR)
	{
		*bSrcByte = TRUE;
	}
	else
	{
		*bSrcByte = FALSE;
	}
	return pSrc;	
}

/********************************************************************
函数名称:SPP_RunAuthr
函数功能:鉴权算法
参数定义:
返回定义:成功返回TRUE,失败返回FALSE
创建时间：2012-3-8   14:04	
函数作者：ldw
注意事项：无

*********************************************************************/

BOOL SPP_RunAuthr(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pSrc,BYTE *key_value,int key_len,BYTE key_des,BYTE bTimes,BOOL bSrc,WORD src_len,BOOL bSrcByte)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len ;
	BYTE des[256];
	BYTE data[8],data1[8],xorvalue[8];
	BYTE output[9];
	BYTE byValue;
	WORD srccode=0;
	int mod=0;
	int i,ldatalen;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	
	len  = 0;
    memset(des,0,sizeof(des));
	

	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&mod,sizeof(mod),(BYTE)(4*bTimes+3),0))
		return FALSE;
	memset(data,0,sizeof(data));
	memset(data1,0,sizeof(data1));
	if(src_len>8)
	{
		len=8;
	}
	else
	{
		len=src_len;
	}
	for(i=0;i<len;i++)
	{
		data1[i]=pSrc[i];
	}
	memset(output,0,sizeof(output));

	if(mod==0)//鉴权
	{
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
			return FALSE;
		if(sibParam.paramType==Param_VARIABLE)
		{
			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				return FALSE;
		}
		else
		{
			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
				return FALSE;
		}
	
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&srccode,sizeof(srccode),(BYTE)(4*bTimes+1),0))
			return FALSE;
		byValue=srccode%256;
		for(i=0;i<4;i++)
		{
			data[i+4]=byValue^data1[i+4];
		}
		byValue=srccode/256;
		for(i=0;i<4;i++)
		{
			data[i]=byValue^data1[i];
		}
		if(key_des==0)//des加密算法
		{
			DESByByte(data,key_value,output);
		}
		else//des解密算法
		{
			_DESByByte(data,key_value,output);
		}
		ldatalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
		if(8>=(size_t)ldatalen)
			SetVaribleValue(senssionID,output,sVariable.offset,(WORD)ldatalen);
		else
			SetVaribleValue(senssionID,output,sVariable.offset,8);
	}
	else//反鉴权
	{
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes+1),FALSE))//
			return FALSE;
		if(sibParam.paramType==Param_VARIABLE)
		{
			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				return FALSE;
		}
		else
		{
			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
				return FALSE;
		}
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,data,sizeof(data),(BYTE)(4*bTimes+0),0))
			return FALSE;
		if(key_des==1)//des加密算法
		{
			DESByByte(data,key_value,output);
		}
		else//des解密算法
		{
			_DESByByte(data,key_value,output);
		}
		memset(xorvalue,0,sizeof(xorvalue));
		for(i=0;i<8;i++)
		{
			xorvalue[i]=output[i]^data1[i];
		}
		srccode=xorvalue[0]*256+xorvalue[4];
		SetVaribleValue(senssionID,(BYTE *)&srccode,sVariable.offset,sizeof(srccode));
	}
	
	return TRUE;
	
}

/********************************************************************
函数名称:SPP_RunQuerypos
函数功能:查找与查找值相等第一个匹配的偏移地址
参数定义:
返回定义:成功返回TRUE,失败返回FALSE
创建时间：2012-3-8   14:04	
函数作者：ldw
注意事项：无

*********************************************************************/

BOOL SPP_RunQuerypos(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pSrc,BYTE bTimes,BOOL bSrc,WORD src_len,BOOL bSrcByte)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len ;
	BYTE byValue;
	int roffset=0;
	BYTE des[2048];
	int offset=0;
	int i;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	
	len  = 0;
    memset(des,0,sizeof(des));
	
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&offset,sizeof(offset),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&byValue,sizeof(byValue),(BYTE)(4*bTimes+3),0))
		return FALSE;
	if(pSrc==NULL)
		return FALSE;
	if(offset>=src_len)
	{
		roffset=0;
	}
	else
	{
		for(i=offset;i<src_len;i++)
		{
			if(pSrc[i]==byValue)
			{
				roffset=i+1;
				break;
			}
		}
	}
	SetVaribleValue(senssionID,(BYTE *)&roffset,sVariable.offset,sizeof(roffset));
	return TRUE;
	
}
/********************************************************************
函数名称:SPP_RunEscapes
函数功能:转义操作
参数定义:
返回定义:成功返回TRUE,失败返回FALSE
创建时间：2012-3-8   14:04	
函数作者：ldw
注意事项：无

*********************************************************************/

BOOL SPP_RunEscapes(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pSrc,BYTE bTimes,BOOL bSrc,WORD src_len,BOOL bSrcByte)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable,sVariablelen;
	WORD len ;
	BYTE des[2048];
	int mod=0;
	int i;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	
	len  = 0;
    memset(des,0,sizeof(des));
	
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&mod,sizeof(mod),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes+3),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariablelen,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariablelen,TRUE))
			return FALSE;
	}
	if(mod==0)//转义
	{
		for(i=0;i<src_len;i++)
		{
			if(pSrc[i]==0x5E)
			{
				des[len]=0x5E;
				des[len+1]=0x5D;
				len=len+2;
			}
			else if(pSrc[i]==0x7E)
			{
				des[len]=0x5E;
				des[len+1]=0x7D;
				len=len+2;
			}
			else if(pSrc[i]==0x7F)
			{
				des[len]=0x5E;
				des[len+1]=0x5F;
				len=len+2;
			}
			else
			{
				des[len]=pSrc[i];
				len++;
			}
		}
	}
	else
	{
		for(i=0;i<src_len;i++)
		{
			if(pSrc[i]==0x5E)
			{
				
				if((i+1)<src_len)
				{
					if(pSrc[i+1]==0x5D)
					{
						des[len]=0x5E;
						i++;
					}
					else if(pSrc[i+1]==0x7D)
					{
						des[len]=0x7E;
						i++;
					}
					else if(pSrc[i+1]==0x5F)
					{
						des[len]=0x7F;
						i++;
					}
					else
						des[len]=pSrc[i];
				}
				else
					des[len]=pSrc[i];
				
			}
			else
				des[len]=pSrc[i];
			len++;
			
		}
	}
	SetVaribleValue(senssionID,des,sVariable.offset,len);
	SetVaribleValue(senssionID,(BYTE *)&len,sVariablelen.offset,sizeof(len));
	return TRUE;
	
}

/********************************************************************
函数名称：RunBufferMemcpy
函数功能: 内存拷贝
参数定义: 无
返回定义: 无
创建时间: 2009-1-8 17:21
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL RunBufferMemcpy(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pSrc,BYTE *pDes,BYTE bTimes,BOOL bSrc,BOOL bDes,WORD src_len,WORD des_len,BOOL bSrcByte,BOOL bDesByte)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD len ;
    WORD desoffset;
	WORD srcoffset;
	char des[2048];
	char ops1[1024];
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	len  = 0;
	desoffset = 0;
	srcoffset = 0;
    memset(des,0,sizeof(des));
	memset(ops1,0,sizeof(ops1));
	
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(bDes)//取出的源目标是地址偏移量
	{
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&desoffset,sizeof(desoffset),(BYTE)(4*bTimes),0))
			return FALSE;
		if(desoffset>=des_len)
			return FALSE;
		if(pDes==NULL)
			return FALSE;
	}
	else
	{
		if(sibParam.paramType==Param_VARIABLE)
		{
			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				return FALSE;
		}
		else
		{
			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
				return FALSE;
		}
		//		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
		//			return FALSE;
	}
	
	if(bSrc)//取出的目标目标是地址偏移量
	{
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&srcoffset,sizeof(srcoffset),(BYTE)(4*bTimes+1),0))
			return FALSE;
		if(srcoffset>=src_len)
			return FALSE;
		if(pSrc == NULL)
			return FALSE;
	}
	else
	{
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&ops1,sizeof(ops1),(BYTE)(4*bTimes+1),0))
			return FALSE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	if((bDes)&&(bSrc))
	{
		if(((desoffset+len)>des_len)||((srcoffset+len)>src_len))
			return FALSE;
		if((pDes==NULL)||(pSrc==NULL))
			return FALSE;
		//		if((bDesByte)&&(bSrcByte))
		//		{
		//			memcpy(pDes+desoffset+2,pSrc+srcoffset+2,len);
		//			len=len+desoffset;
		//			memcpy(pDes,&len,2);
		//		}
		//		else if((bDesByte)&&(!bSrcByte))
		//		{
		//			memcpy(pDes+desoffset+2,pSrc+srcoffset,len);
		//			len=len+desoffset;
		//			memcpy(pDes,&len,2);
		//		}
		//		else if((!bDesByte)&&(bSrcByte))
		//		{
		//			memcpy(pDes+desoffset,pSrc+srcoffset+2,len);
		//		}
		//		else
		//		{
		memcpy(pDes+desoffset,pSrc+srcoffset,len);
		//		}
	}
	else if((bDes)&&(!bSrc))
	{
		if((desoffset+len)>des_len)
			
			return FALSE;
		if(pDes==NULL)
			return FALSE;
		memcpy(pDes+desoffset,ops1,len);
	}
	else if((!bDes)&&(bSrc))
	{
		if((srcoffset+len)>src_len)
			return FALSE;
		if(pSrc==NULL)
			return FALSE;
		memcpy(des,pSrc+srcoffset,len);
		if(len>=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)))
			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));	
		else
		{
			SetVaribleValue(senssionID,des,sVariable.offset,len);
		}
	}
	else
	{
		if(len>=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)))
			SetVaribleValue(senssionID,ops1,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));	
		else
		{
			SetVaribleValue(senssionID,ops1,sVariable.offset,len);
		}
	}
	
	return TRUE;
	
}

/********************************************************************
函数名称：SPP_REPLACEMEM
函数功能:内存值替换
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-11 16:07
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
//BOOL SPP_REPLACEMEM(WORD senssionID,BYTE serviceID,WORD sibID,BYTE *pSrc,BYTE *pDes,BYTE bTimes,BOOL bSrc,BOOL bDes,WORD src_len,WORD des_len,BOOL bSrcByte,BOOL bDesByte)
//{
//	SIB_PARAM sibParam;
//	XSCM_CONSTVARIABLE sVariable;
//	WORD len ;
//    WORD desoffset;
//	WORD srcoffset;
//	char des[2048];
//	char ops1[1024];
//	if(senssionID>=MESSAGE_MAX)
//		return FALSE;
//	if(serviceID>=ServiceKey_Total)
//		return FALSE;
//	len  = 0;
//	desoffset = 0;
//	srcoffset = 0;
//    memset(des,0,sizeof(des));
//	memset(ops1,0,sizeof(ops1));
//
//	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
//		return FALSE;
//	if(bDes)//取出的源目标是地址偏移量
//	{
//		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&desoffset,sizeof(desoffset),(BYTE)(4*bTimes),0))
//			return FALSE;
//		if(desoffset>=des_len)
//			return FALSE;
//		if(pDes==NULL)
//			return FALSE;
//	}
//	else
//	{
//		if(sibParam.paramType==Param_VARIABLE)
//		{
//			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
//				return FALSE;
//		}
//		else
//		{
//			if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
//				return FALSE;
//		}
//	}
//	
//	if(bSrc)//取出的目标目标是地址偏移量
//	{
//		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&srcoffset,sizeof(srcoffset),(BYTE)(4*bTimes+1),0))
//			return FALSE;
//		if(srcoffset>=src_len)
//			return FALSE;
//		if(pSrc == NULL)
//			return FALSE;
//	}
//	else
//	{
//		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&ops1,sizeof(ops1),(BYTE)(4*bTimes+1),0))
//			return FALSE;
//	}
//	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
//		return FALSE;
//	if((bDes)&&(bSrc))
//	{
//		if(((desoffset+len)>des_len)||((srcoffset+len)>src_len))
//			return FALSE;
//		if((pDes==NULL)||(pSrc==NULL))
//			return FALSE;
//		memcpy(pDes+desoffset,pSrc+srcoffset,len);
//	}
//	else if((bDes)&&(!bSrc))
//	{
//		if((desoffset+len)>des_len)
//			
//			return FALSE;
//		if(pDes==NULL)
//			return FALSE;
//		memcpy(pDes+desoffset,ops1,len);
//	}
//	else if((!bDes)&&(bSrc))
//	{
//		if((srcoffset+len)>src_len)
//			return FALSE;
//		if(pSrc==NULL)
//			return FALSE;
//		memcpy(des,pSrc+srcoffset,len);
//		if(len>=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)))
//			SetVaribleValue(senssionID,des,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));	
//		else
//		{
//			SetVaribleValue(senssionID,des,sVariable.offset,len);
//		}
//	}
//	else
//	{
//		if(len>=(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)))
//			SetVaribleValue(senssionID,ops1,sVariable.offset,(WORD)(sVariable.length*GetDataTypeLen((BYTE)sVariable.type)));	
//		else
//		{
//			SetVaribleValue(senssionID,ops1,sVariable.offset,len);
//		}
//	}
//
//	return TRUE;
//	
//}
/********************************************************************
函数名称：Crt_Rand
函数功能: 获取RANGE_MIN与RANGE_MAX之间的一个随机数
参数定义: RANGE_MIN：最小值，RANGE_MAX：最大值
返回定义: 返回获取的随机数值
创建时间: 2009-7-13 14:40
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int Crt_Rand(int RANGE_MIN,int RANGE_MAX)
{
	int rand100;
	if((RANGE_MIN>RANGE_MAX)||(RANGE_MIN==RANGE_MAX))
	{
		return RANGE_MIN;
	}
    
	srand( (unsigned)time( NULL ) ); 
	if(RANGE_MAX==0)
	{
		rand100 =0-(rand()% (0-RANGE_MIN)); 
	}
	else if(RANGE_MAX<0)
	{
		rand100 = 0-((rand()% (0-RANGE_MIN) -RANGE_MAX));
	}
	else
		rand100 = ((rand()% RANGE_MAX) + RANGE_MIN); 
	if(rand100>RANGE_MAX)
		rand100 = RANGE_MAX;
	if(rand100<RANGE_MIN)
		rand100 = RANGE_MIN;
	
	return rand100;
	
}
/********************************************************************
函数名称：GetRandData
函数功能: 获取随机数
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-1-8 16:46
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetRandData(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	int v_min,v_max,des_value;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&v_min,sizeof(v_min),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&v_max,sizeof(v_max),(BYTE)(4*bTimes+3),0))
		return FALSE;
	
	des_value = Crt_Rand(v_min,v_max);
	SetVaribleValue(senssionID,(BYTE *)&des_value,sVariable.offset,(WORD)(GetDataTypeLen((BYTE)sVariable.type)*sVariable.length));
	return TRUE;	
}
void AscToHexII(BYTE * p,BYTE * s,int length) 
{
    int i;
    
    for(i=0; i<length/2; i++)
    {
        if(s[2*i] > '9')
        {
            p[i] = (s[2*i] - 0x37)*16;
        }
        else
        {
            p[i] = (s[2*i] - 0x30)*16;
        }
        if(s[2*i+1] > '9')
        {
            p[i] += (s[2*i+1] - 0x37);
        }
        else
        {
            p[i] += (s[2*i+1] - 0x30);
        }
    }    
}
BOOL ReadAccID(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
		if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	
	
	SetVaribleValue(senssionID,&g_MESSAGE_XSCM_SLAVE[senssionID].accID,sVariable.offset,sVariable.length);
	return TRUE;	
}
BOOL SetAsciiToHexII(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE Asc_value[1024];
	BYTE Hex_value[1024];
	int len;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(Asc_value,0,sizeof(Asc_value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)Asc_value,sizeof(Asc_value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	memset(Hex_value,0,sizeof(Hex_value));
	AscToHexII(Hex_value,Asc_value,len);
	
	SetVaribleValue(senssionID,Hex_value,sVariable.offset,sVariable.length);
	return TRUE;	
}
/********************************************************************
函数名称：SetBcdToStr
函数功能: 将BCD码转换成字符串
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-7-13 10:36
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL SetBcdToStr(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE bcd_value[512];
	char strvalue[1024];
	int i;
	int len;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(bcd_value,0,sizeof(bcd_value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)bcd_value,sizeof(bcd_value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	memset(strvalue,0,sizeof(strvalue));
	for(i=0;i<len;i++)
	{
		if(!IsBcd(bcd_value[i]))
			return FALSE;
		sprintf(strvalue+i*2,"%02x",bcd_value[i]);
	}
	
	SetVaribleValue(senssionID,strvalue,sVariable.offset,sVariable.length);
	return TRUE;	
}
/********************************************************************
函数名称：SetHexToStr
函数功能: 将BCD码转换成字符串
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-7-13 10:36
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL SetStrToWord(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	char bcd_value[512];
	char strvalue[1024];
	int offset;
	int len;
	BYTE data[2];
	BYTE _value;
	WORD vv;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(bcd_value,0,sizeof(bcd_value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)bcd_value,sizeof(bcd_value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&offset,sizeof(offset),(BYTE)(4*bTimes+3),0))
		return FALSE;
	memset(strvalue,0,sizeof(strvalue));
	len=strlen(bcd_value);
	if(len<4)
	{
		sprintf(strvalue,"%04s",bcd_value);
	}
	else
	{
		memcpy(strvalue,bcd_value,4);
	}
	vv=atoi(strvalue);
	_value=vv/100;
	data[0]=(_value/10)*16+_value%10;
	_value=vv%100;
	data[1]=(_value/10)*16+_value%10;
	//	if(sVariable.)
	SetVaribleValue(senssionID,data,sVariable.offset+offset,sizeof(data));
	return TRUE;	
}
/*******************************
*
*标准CRC16校验
*MODBUS标准的校验多项式,初值0x0000
*返回：(WORD)wReturn
********************************/

WORD  GetCRCCode(BYTE *pBuffer,int nSize)
{
	
	WORD wReturn = 0x0000;
	WORD wNew = 0xa001;
	BOOL b=FALSE; 
	int i,j;
	for (i = 0 ;i < nSize ;i++)
	{
		wReturn ^= pBuffer[i];
		for(j = 0 ; j < 8 ; j++)
		{
			if(wReturn & 0x0001)
				b = TRUE;
			else
				b = FALSE;
			
			wReturn >>= 1;
			if(b)
				wReturn ^= wNew;
		}
	}
	return wReturn;
	
}
/********************************************************************
函数名称:SPP_GetCRC
函数功能:CRC算法
参数定义:
返回定义:成功返回TRUE,拾芊祷谾ALSE
创建时间：2012-3-8   13:23	
函数作者：ldw
注意事项：无

*********************************************************************/

BOOL SPP_GetCRC(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE bcd_value[512];
	int len;
	WORD vv;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(bcd_value,0,sizeof(bcd_value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,bcd_value,sizeof(bcd_value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	vv=GetCRCCode(bcd_value,len);
	SetVaribleValue(senssionID,(BYTE *)&vv,sVariable.offset,sizeof(vv));
	return TRUE;	
}
/********************************************************************
函数名称：SetHexToStr
函数功能: 将BCD码转换成字符串
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-7-13 10:36
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL SetHexToStr(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE bcd_value[512];
	char strvalue[1024];
	int i;
	int len;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(bcd_value,0,sizeof(bcd_value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)bcd_value,sizeof(bcd_value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	memset(strvalue,0,sizeof(strvalue));
	for(i=0;i<len;i++)
	{
		sprintf(strvalue+i*2,"%02x",bcd_value[i]);
	}
	
	SetVaribleValue(senssionID,strvalue,sVariable.offset,sVariable.length);
	return TRUE;	
}

/********************************************************************
函数名称：GetHighByte
函数功能: 获取高字节的值
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-7-13 10:36
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL GetHighByte(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD value;
	BYTE highvalue;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	value = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&value,sizeof(value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	highvalue =(BYTE) (value/256);
	
	SetVaribleValue(senssionID,(BYTE *)&highvalue,sVariable.offset,sVariable.length);
	return TRUE;	
}
/********************************************************************
函数名称：GetLowByte
函数功能: 获取低字节的值
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-7-13 10:36
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL GetLowByte(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD value;
	BYTE lowvalue;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	value = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&value,sizeof(value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	lowvalue = (value%0x100);
	
	SetVaribleValue(senssionID,(BYTE *)&lowvalue,sVariable.offset,sVariable.length);
	return TRUE;	
}
/********************************************************************
函数名称：GetXorValue
函数功能: 值异或
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-8-3 16:39
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetXorValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE data[1024];
	int len;
	int i;
	BYTE bXorValue = 0;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(data,0,sizeof(data));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)data,sizeof(data),(BYTE)(4*bTimes+1),0))
		return FALSE;
	len = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	for (i=0;i<len;i++) {
		bXorValue = bXorValue ^  data[i];
	}
	SetVaribleValue(senssionID,(BYTE *)&bXorValue,sVariable.offset,sVariable.length);
	return TRUE;	
}
/********************************************************************
函数名称：GetSumValue
函数功能: 值相加取低位
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-8-3 16:48
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetSumValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE data[1024];
	int len;
	int i;
	BYTE bSumValue = 0;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(data,0,sizeof(data));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)data,sizeof(data),(BYTE)(4*bTimes+1),0))
		return FALSE;
	len = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	for (i=0;i<len;i++) {
		bSumValue = (bSumValue + data[i])%256;
	}
	bSumValue=(BYTE)(~bSumValue+1);
	SetVaribleValue(senssionID,(BYTE *)&bSumValue,sVariable.offset,sVariable.length);
	return TRUE;
}
BYTE ConVertByte(char pValue)
{
	if(pValue<=0x39)
		return (BYTE)(pValue - 0x30);
	else
		return (BYTE)(pValue - 0x37);
}
/********************************************************************
函数名称：ConvertTwoCharToByte
函数功能: 字符串向字节串转换函数
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-8-17 15:51
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL ConvertTwoCharToByte(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	char data[1024];
	WORD len;
	int i;
	BYTE value[1024];
	BYTE hi,lo;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else 
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(data,0,sizeof(data));
	memset(value,0,sizeof(value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)data,sizeof(data),(BYTE)(4*bTimes+1),0))
		return FALSE;
	len = 0;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	if(len>=strlen(data))
		len = strlen(data);
	for (i=0;i<len/2;i++) {
		hi = ConVertByte(data[i*2]);
		lo=ConVertByte(data[i*2+1]);
		value[i]=(hi%16)*16+(lo%16);
	}
	//	len =(WORD) len/2;
	//	memcpy(value,&len,2);
	SetVaribleValue(senssionID,value,sVariable.offset,sVariable.length);
	return TRUE;
}
/********************************************************************
函数名称：SPP_MakeWord
函数功能: 将两个字节合并成一个字节
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-10-12 9:46
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL SPP_MakeWord(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	WORD value = 0;
	BYTE high = 0;
	BYTE low = 0;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&high,sizeof(high),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&low,sizeof(low),(BYTE)(4*bTimes+3),0))
		return FALSE;
	value = high*256+low;
	if(sizeof(value)>=(GetDataTypeLen((BYTE)sVariable.type)*sVariable.length))
		SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,(WORD)(GetDataTypeLen((BYTE)sVariable.type)*sVariable.length));
	else
		SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,sizeof(value));
	return TRUE;
}
void converasciitohexdes(BYTE *src,BYTE *des,int len)
{
	int i;
	BYTE high,low;
	if((src==NULL)||(des==NULL))
		return;
	for(i=0;i<len;i++)
	{
		high=src[i]/16;
		low=src[i]%16;
		if(high<10)
			des[i*2+0]=high+0x30;
		else
			des[i*2+0]=high+97;
		if(low<10)
			des[i*2+1]=low+0x30;
		else
			des[i*2+1]=low+97;
	}
}
void WriteKeyValue(BYTE *key_value,int key_len)
{
	FILE *fp;
	char buffer[512];
	int i;
	if(key_value==NULL)
		return;
	fp=fopen("C:\\yfcomm\\log\\test.ini","a");
	for(i=0;i<key_len;i++)
	{		
		if(i==key_len-1)
			sprintf(buffer+i*3,"%02x\n ",key_value[i]);
		else
			sprintf(buffer+i*3,"%02x\n ",key_value[i]);
	}
	
	fwrite(buffer,strlen(buffer),1,fp);
	memset(buffer,0,sizeof(buffer));
	sprintf(buffer,"len=%d\n",key_len);
	fwrite(buffer,strlen(buffer),1,fp);
	
	fclose(fp);
}
/********************************************************************
函数名称：SPP_MacCalculate
函数功能:计算MAC值
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-10-12 10:17
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL SPP_MacCalculate(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE *key_value,int key_len,BYTE key_des)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	BYTE value[1024];
	int len = 0;
	BYTE output[9];
	BYTE setvalue[10];
	BYTE xorvalue[9];
	BYTE hexdes[16];
	//	BYTE input[8];
	int ldatalen;
	BYTE nTime;
	int i;
	int offset;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	memset(value,0,sizeof(value));
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,value,sizeof(value),(BYTE)(4*bTimes+1),0))
		return FALSE;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		return FALSE;
	memset(xorvalue,0,sizeof(xorvalue));
	memset(hexdes,0,sizeof(hexdes));
	//	WriteKeyValue(value,len);
    if(len>sizeof(value))
		return FALSE;
	offset=sizeof(value)-len;
	
	memset(value+len,0,offset);
	if((BYTE)(len%8)!=0)
	{
		nTime=(BYTE)(len/8)+1;
		len = nTime*8;
	}
	else
		nTime =(BYTE)(len/8);
	
	for(i=0;i<nTime;i++)
	{
		xorvalue[0]=xorvalue[0]^value[i*8+0];
		xorvalue[1]=xorvalue[1]^value[i*8+1];
		xorvalue[2]=xorvalue[2]^value[i*8+2];
		xorvalue[3]=xorvalue[3]^value[i*8+3];
		xorvalue[4]=xorvalue[4]^value[i*8+4];
		xorvalue[5]=xorvalue[5]^value[i*8+5];
		xorvalue[6]=xorvalue[6]^value[i*8+6];
		xorvalue[7]=xorvalue[7]^value[i*8+7];
	}
	//	converasciitohexdes(xorvalue,hexdes,8);
	//	memcpy(input,hexdes,8);
	//	WriteKeyValue(key_value,key_len);
	//	WriteKeyValue(xorvalue,key_len);
	if(key_des==0)//des算法
	{
		DES_Encryption(key_value,xorvalue,output);
	}
	else//3DES算法
	{
		DES_Decryption(key_value,xorvalue,output);
	}
	//	input[0]=output[0]^hexdes[8+0];
	//	input[1]=output[1]^hexdes[8+1];
	//	input[2]=output[2]^hexdes[8+2];
	//	input[3]=output[3]^hexdes[8+3];
	//	input[4]=output[4]^hexdes[8+4];
	//	input[5]=output[5]^hexdes[8+5];
	//	input[6]=output[6]^hexdes[8+6];
	//	input[7]=output[7]^hexdes[8+7];
	//	if(key_des==0)//des算法
	//	{
	//		DES_Encryption(key_value,input,output);
	//	}
	//	else//3DES算法
	//	{
	//		DES_Decryption(key_value,input,output);
	//	}
	//	converasciitohexdes(output,hexdes,8);
	
	memset(setvalue,0,sizeof(setvalue));
	memcpy(setvalue,output,8);
	//	len = 8;
	//	memcpy(setvalue,&len,sizeof(WORD));
	ldatalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	if(strlen(setvalue)>=(size_t)ldatalen)
		SetVaribleValue(senssionID,setvalue,sVariable.offset,(WORD)ldatalen);
	else
		SetVaribleValue(senssionID,setvalue,sVariable.offset,sizeof(setvalue));
	return TRUE;
}
/********************************************************************
函数名称:SPP_EnOrDescriptiom
函数功能:DES算法加解密
参数定义:
返回定义:成功返回TRUE,失败返回FALSE
创建时间：2012-2-21   14:13	
函数作者：ldw
注意事项：无

  *********************************************************************/
  
  BOOL SPP_EnOrDescriptiom(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE *key_value,int key_len,BYTE key_des)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  BYTE value[1024];
	  int len = 0;
	  BYTE output[9];
	  BYTE setvalue[10];
	  BYTE xorvalue[9];
	  BYTE hexdes[16];
	  //	BYTE input[8];
	  int ldatalen;
	  BYTE nTime;
	  int i;
	  int offset;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  memset(value,0,sizeof(value));
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,value,sizeof(value),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		  return FALSE;
	  memset(xorvalue,0,sizeof(xorvalue));
	  memset(hexdes,0,sizeof(hexdes));
	  //	WriteKeyValue(value,len);
	  if(len>sizeof(value))
		  return FALSE;
	  offset=sizeof(value)-len;
	  
	  memset(value+len,0,offset);
	  if((BYTE)(len%8)!=0)
	  {
		  nTime=(BYTE)(len/8)+1;
		  len = nTime*8;
	  }
	  else
		  nTime =(BYTE)(len/8);
	  
	  for(i=0;i<nTime;i++)
	  {
		  xorvalue[0]=xorvalue[0]^value[i*8+0];
		  xorvalue[1]=xorvalue[1]^value[i*8+1];
		  xorvalue[2]=xorvalue[2]^value[i*8+2];
		  xorvalue[3]=xorvalue[3]^value[i*8+3];
		  xorvalue[4]=xorvalue[4]^value[i*8+4];
		  xorvalue[5]=xorvalue[5]^value[i*8+5];
		  xorvalue[6]=xorvalue[6]^value[i*8+6];
		  xorvalue[7]=xorvalue[7]^value[i*8+7];
	  }
	  
	  if(key_des==0)//des加密算法
	  {
		  DESByByte(xorvalue,key_value,output);
	  }
	  else//des解密算法
	  {
		  _DESByByte(xorvalue,key_value,output);
	  }
	  
	  
	  memset(setvalue,0,sizeof(setvalue));
	  memcpy(setvalue,output,8);
	  
	  ldatalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  if(strlen(setvalue)>=(size_t)ldatalen)
		  SetVaribleValue(senssionID,setvalue,sVariable.offset,(WORD)ldatalen);
	  else
		  SetVaribleValue(senssionID,setvalue,sVariable.offset,sizeof(setvalue));
	  return TRUE;
  }
  /********************************************************************
  函数名称：SetMacKeyValue
  函数功能: 设置加密类型
  参数定义: 无
  返回定义: 成功返回TRUE,失败返回FALSE
  创建时间: 2009-10-12 10:09
  函数作者: 刘定文
  注意事项: 无	
  *********************************************************************/
  
  BOOL SetMacKeyValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes,BYTE *value,int *len,BYTE *type)
  {
	  if((value == NULL)||(len  == NULL)||(type == NULL))
		  return FALSE;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,value,1024,(BYTE)(4*bTimes+0),0))
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)len,sizeof(int),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,type,sizeof(BYTE),(BYTE)(4*bTimes+3),0))
		  return FALSE;
	  
	  return TRUE;
  }
  
  /********************************************************************
  函数名称：ConvertToLower
  函数功能: 字符串转换成小写函数
  参数定义: 无
  返回定义: 成功返回TRUE,失败返回FALSE
  创建时间: 2009-9-2 14:17
  函数作者: 刘定文
  注意事项: 无	
  *********************************************************************/
  
  BOOL ConvertToLower(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  char data[2048];
	  char value[2048];
	  int datalen;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  memset(data,0,sizeof(data));
	  memset(value,0,sizeof(value));
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)data,sizeof(data),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(strlen(data)==0)
		  return FALSE;
	  strLoWer(data,value);
	  if(strlen(value)==0)
		  return FALSE;
	  datalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  if(strlen(value)>=(size_t)datalen)
		  SetVaribleValue(senssionID,value,sVariable.offset,(WORD)datalen);
	  else
		  SetVaribleValue(senssionID,value,sVariable.offset,(WORD)strlen(value));
	  return TRUE;
  }
  
  
  
  BOOL SPP_StrToBCD(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  char data[2048];
	  BYTE bValue[1024];
	  char strBcd[2];
	  int i,j;
	  char value[2048];
	  int len;
	  int datalen;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  memset(bValue,0,sizeof(bValue));
	  memset(data,0,sizeof(data));
	  memset(value,0,sizeof(value));
	  len=0;
	  datalen=0;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)data,sizeof(data),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		  return FALSE;
	  if(!IsAllNum(data))
		  return FALSE;
	  if((len%2)!=0)
		  len++;
	  if((int)strlen(data)>=len)
	  {
		  memcpy(value,data,len);
	  }
	  else
	  {
		  j=len-strlen(data);
		  memset(value,0x30,j);
		  //		for(i=0;i<j;i++)
		  //			value[j]='0';
		  memcpy(value,data,len-j);
	  }
	  len  = len/2;
	  for(i=0;i<len;i++)
	  {
		  memcpy(strBcd,value+i*2,2);
		  bValue[i]=atoi(strBcd);
	  }
	  
	  //	memcpy(bValue,&len,sizeof(WORD));
	  //  len = len + 2;
	  datalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  if(len>=datalen)
		  SetVaribleValue(senssionID,bValue,sVariable.offset,(WORD)datalen);
	  else
		  SetVaribleValue(senssionID,bValue,sVariable.offset,(WORD)len);
	  return TRUE;
  }
  
  BOOL IsBcd(BYTE value)
  {
	  //	if((0x0a<=value<=0x0f)||(0x1a<=value<=0x1f)||(0x2a<=value<=0x2f)||(0x3a<=value<=0x3f)||(0x4a<=value<=0x4f)||(0x5a<=value<=0x5f)||(value>=0x64))
	  //		return FALSE;
	  char a[5];
	  memset(a,0,sizeof(a));
	  itoa(value,a,10);
	  if(!IsAllNum(a))
		  return FALSE;
	  else
		  return TRUE;
	  
  }
  BOOL SPP_BCDToDecWORD(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  BYTE bValue[1024];
	  int offset;
	  WORD value;
	  WORD datalen;
	  BYTE value1,value2;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  offset = 0;
	  memset(bValue,0,sizeof(bValue));
	  memset(&value,0,sizeof(value));
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,bValue,sizeof(bValue),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&offset,sizeof(offset),(BYTE)(4*bTimes+3),0))
		  return FALSE;
	  if((!IsBcd(bValue[offset]))||(!IsBcd(bValue[offset+1])))
		  return FALSE;
	  value1 = BcdToHex(bValue[offset]);
	  value2 = BcdToHex(bValue[offset+1]);
	  value = value1*100+value2;
	  
	  
	  datalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  if(sizeof(WORD)>=datalen)
		  SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,(WORD)datalen);
	  else
		  SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,(WORD)sizeof(WORD));
	  return TRUE;
  }
  BOOL SPP_GetServiceType(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  BYTE value;
	  WORD datalen;
	  MESSAGE_XSCM_SLAVE pMessage;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  //	ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	  if(!ReadXSCMMessage(senssionID,&pMessage))
	  {
		  EndSenssion(senssionID,FatalFail);
		  return FALSE;
	  }
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  datalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  value = (BYTE)pMessage.slavehead.serInit.servicetype;
	  if(sizeof(BYTE)>=datalen)
		  SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,(WORD)datalen);
	  else
		  SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,(WORD)sizeof(BYTE));
	  return TRUE;	
  }
  BOOL SPP_BCDToHex(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  BYTE bValue[1024];
	  int i;
	  BYTE value[1024];
	  
	  WORD len;
	  WORD datalen;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  memset(bValue,0,sizeof(bValue));
	  memset(value,0,sizeof(value));
	  len=0;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,bValue,sizeof(bValue),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+3),0))
		  return FALSE;
	  datalen=sizeof(bValue);
	  //	memcpy(&datalen,bValue,sizeof(WORD));
	  if(len>datalen)
		  len = datalen;
	  for(i=0;i<len;i++)
	  {
		  if(!IsBcd(bValue[i]))
			  return FALSE;
		  value[i] = HextoBcd(bValue[i]);
		  
	  }
	  //	memcpy(value,&len,sizeof(WORD));
	  //  len = len+2;
	  datalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  if(len>=datalen)
		  SetVaribleValue(senssionID,value,sVariable.offset,(WORD)datalen);
	  else
		  SetVaribleValue(senssionID,value,sVariable.offset,(WORD)len);
	  return TRUE;
  }
  BOOL ConvertToUpper(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  char data[2048];
	  char value[2048];
	  int datalen;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  memset(data,0,sizeof(data));
	  memset(value,0,sizeof(value));
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)data,sizeof(data),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(strlen(data)==0)
		  return FALSE;
	  strUpper(data,value);
	  if(strlen(value)==0)
		  return FALSE;
	  datalen = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	  if(strlen(value)>=(size_t)datalen)
		  SetVaribleValue(senssionID,value,sVariable.offset,(WORD)datalen);
	  else
		  SetVaribleValue(senssionID,value,sVariable.offset,(WORD)strlen(value));
	  return TRUE;
  }
  /********************************************************************
  函数名称：GetArrayValue
  函数功能: 获取数组的第一个值
  参数定义: 无
  返回定义: 成功返回TRUE,失败返回FALSE
  创建时间: 2009-1-8 16:46
  函数作者: 刘定文
  注意事项: 无	
  *********************************************************************/
  BOOL GetArrayValue(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
  {
	  SIB_PARAM sibParam;
	  XSCM_CONSTVARIABLE sVariable;
	  WORD len = 0;
	  if(senssionID>=MESSAGE_MAX)
		  return FALSE;
	  if(serviceID>=ServiceKey_Total)
		  return FALSE;
	  if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue==NULL)
		  return FALSE;
	  if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		  return FALSE;
	  if(sibParam.paramType==Param_VARIABLE)
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			  return FALSE;
	  }
	  else
	  {
		  if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			  return FALSE;
	  }
	  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&len,sizeof(len),(BYTE)(4*bTimes+1),0))
		  return FALSE;
	  if(sVariable.length*(GetDataTypeLen((BYTE)sVariable.type)) < len)
		  return FALSE;
	  
	  return TRUE;	
  }
  /********************************************************************
  函数名称：Fun_Operation_Control
  函数功能: 运算操作
  参数定义: senssionID：会话号,sibID:sib编号,pSibBasic:SIB基本属性
  返回定义: 无
  创建时间: 2008-8-26 16:30
  函数作者: 刘定文
  注意事项: 无	
  *********************************************************************/
  
  BOOL  Fun_Operation_Control(WORD senssionID,BYTE serviceID,WORD sibID,SIB_BASIC *pSibBasic)
  {
	  SIB_PARAM sibParam;
	  BYTE bFunTimes;//运算表达式个数
	  int i;
	  BYTE operType=0;//表达式操作符
	  BOOL bSetDes=FALSE;//是否设置了目标地址
	  BOOL bSetSrc=FALSE;//是否设置了源地址
	  BYTE *pDes=NULL;
	  BYTE *pSrc=NULL;
	  BYTE *pMem=NULL;
	  WORD src_len=0;
	  WORD des_len=0;
	  WORD datalen=0;
	  BOOL bDesByte = FALSE;
	  BOOL bSrcByte = FALSE;
	  BYTE key_value[1024];
	  int key_len=0;
	  BYTE key_des=0;
	  if(pSibBasic == NULL)
		  return FALSE;
	  if(pSibBasic->paramTotal == 0)
		  return FALSE;
	  if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(pSibBasic->paramTotal-1),TRUE))
		  return FALSE;
	  bFunTimes = (sibParam.paramNo/4) + 1;
	  for (i = 0 ; i < bFunTimes ; i++)
	  {
		  if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&operType,sizeof(operType),(BYTE)(4*i+2),0))
			  break;
		  switch(operType)
		  {
		  case OPERATION_EQUL://=
		  case OPERATION_ADD://+
		  case OPERATION_SUB://-
		  case OPERATION_MUL: //* 
		  case OPERATION_DIV:  // / 
		  case OPERATION_RESIDUAL: // % 
		  case OPERATION_AND:   //&
		  case OPERATION_OR :   //|
		  case OPERATION_XOR:   //^
		  case OPERATION_NOT:   //!
			  Fun_Operation_INTEGER(senssionID,serviceID,sibID,(BYTE)i,operType);
			  break;
		  case OPERATION_STRCAT:   //strcat 串相加
		  case OPERATION_STRLEFT:   //left 求左边的子串
		  case OPERATION_STRRIGHT:   //right 求右边的子串
		  case OPERATION_STRLTRIM:   //去掉左空格
		  case OPERATION_STRRTRIM:   //去掉右空格
		  case OPERATION_STRTRIM:   //去掉两边的空格
		  case OPERATION_STRLENGT:   //求串长度
		  case OPERATION_STRPLACE:   //替换串
		  case OPERATION_STRATOI:   //将串按照指定的进制转换成整数
		  case OPERATION_STRITOA:   //将数值按照指定的进制转换成字符串
		  case OPERATION_NOW :   //取得当前时间，值为串
		  case OPERATION_SECONDS :  //将时间串转换为秒数 
		  case OPERATION_TIME :   //秒值转换为时间
			  
		  case OPERATION_SID   :   //取会话号
			  
		  case OPERATION_MEMST: //清空缓冲区内容
		  case OPERATION_CRUURENTSIB :   //取得当前SIB的ID号
			  Fun_Operation_STRING(senssionID,serviceID,sibID,(BYTE)i,operType);
			  break;
		  case OPERATION_ADDRESS  :  //取地址，并相加
			  pMem=SetMemBuffer(senssionID,serviceID,sibID,/*pDes,*/(BYTE)i,&datalen); 
			  break;
		  case OPERATION_MEMCPY  :   //串拷贝		
			  BufferMemcpy(senssionID,serviceID,sibID,pMem,(BYTE)i,datalen);
			  break;
		  case OPERATION_SETDESBUFFER://设置目标指针地址
			  pDes = SetDesBuffer(senssionID,serviceID,sibID,/*pDes,*/(BYTE)i,&des_len,&bDesByte/*,operType*/);
			  if(pDes!=NULL)
				  bSetDes =TRUE;
			  else
				  bSetDes = FALSE;
			  break;
		  case OPERATION_SETSOURCEBUFFER://设置源指针地址
			  pSrc = SetSrcBuffer(senssionID,serviceID,sibID,/*pSrc*/(BYTE)i,&src_len,&bSrcByte/*,operType*/);
			  if(pSrc!=NULL)
				  bSetSrc =TRUE;
			  else
				  bSetSrc = FALSE;
			  break;
		  case OPERATION_BUFFERMEMCPY://内存拷贝
			  RunBufferMemcpy(senssionID,serviceID,sibID,pSrc,pDes,(BYTE)i,bSetSrc,bSetDes,src_len,des_len,bSrcByte,bDesByte);
			  break;
			  //		case OPERATION_ARRAY://取数组的第一个值
			  //			GetArrayValue(senssionID,serviceID,sibID,(BYTE)i);
			  //			break;
		  case OPERATION_TONEID :   //转换语音ID号
		  case OPERATION_ERRORNO :   //当前出错编号
			  
			  break;
		  case OPERATION_RAND://随机数
			  GetRandData(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_BCDToStr://BCD向字符串转换
			  SetBcdToStr(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_HIBYTE:
			  GetHighByte(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_LOBYTE:
			  GetLowByte(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_XORVALUE://值异或
			  GetXorValue(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_SUMVALUE://值相加
			  GetSumValue(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_CHARTOBYTE://字符串向字节串转换函数
			  ConvertTwoCharToByte(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_CHARTOLOWER://字符串转换成小写函数
			  ConvertToLower(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_CHARTOUPPER://字符串转换成大写函数
			  ConvertToUpper(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_MAKEWORD://将两个BYTE组合成WORD
			  SPP_MakeWord(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_SETSERCERTKEY://设置加密类型
			  memset(key_value,0,sizeof(key_value));
			  SetMacKeyValue(senssionID,serviceID,sibID,(BYTE)i,key_value,&key_len,&key_des);
			  break;
		  case OPERATION_ENCRYPT:   //加密串
			  SPP_EnOrDescriptiom(senssionID,serviceID,sibID,(BYTE)i,key_value,key_len,0);
			  break;
		  case OPERATION_UNENCRYPT :   //解密串
			  SPP_EnOrDescriptiom(senssionID,serviceID,sibID,(BYTE)i,key_value,key_len,1);
			  break;
		  case OPERATION_MACCALCULATE://计算MAC值
			  SPP_MacCalculate(senssionID,serviceID,sibID,(BYTE)i,key_value,key_len,key_des);
			  break;
		  case OPERATION_STRTOBCD://字符串向BCD码转换
			  SPP_StrToBCD(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_BCDTOHEX://BCD码向HEX装换
			  SPP_BCDToHex(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_BCDTODECVALUE:
			  SPP_BCDToDecWORD(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_HEXTOSTR://16进制向字符转换
			  SetHexToStr(senssionID,serviceID,sibID,(BYTE)i);
			  break;
			  //		case OPERATION_REPLACEMEM://值替换
			  //			RunBufferMemcpy(senssionID,serviceID,sibID,pSrc,pDes,(BYTE)i,bSetSrc,bSetDes,src_len,des_len,bSrcByte,bDesByte);
			  //			break;
		  case OPERATION_GETSERVICETYPE://获取业务类型
			  SPP_GetServiceType(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_STRTOWORD://串向WORD转换
			  SetStrToWord(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_CRC://CRC校验
			  SPP_GetCRC(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_ESCAPES://转义
			  SPP_RunEscapes(senssionID,serviceID,sibID,pSrc,(BYTE)i,bSetSrc,src_len,bSrcByte);
			  
			  break;
		  case OPERATION_AUTHR://鉴权算法
			  SPP_RunAuthr(senssionID,serviceID,sibID,pSrc,key_value,key_len,0,(BYTE)i,bSetSrc,src_len,bSrcByte);
			  
			  break;
		  case OPERATION_QUERYPOS://搜索地址
			  SPP_RunQuerypos(senssionID,serviceID,sibID,pSrc,(BYTE)i,bSetSrc,src_len,bSrcByte);
			  
			  break;
		  case OPERATION_ASCHEXII://ASCTOHEXII
			  SetAsciiToHexII(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  case OPERATION_READACCID:
			  ReadAccID(senssionID,serviceID,sibID,(BYTE)i);
			  break;
		  default:
			  break;
		}
	}
	pDes = NULL;
	pSrc = NULL;
	pMem = NULL;
	UpdateNextSib(senssionID,sibID,TRUE);
	//RunSib(senssionID);
	return TRUE;
	
}
void IF_Operation_Control(WORD senssionID,BYTE serviceID,WORD sibID,SIB_BASIC *pSibBasic)
{
	SIB_PARAM sibParam;
	BYTE bFunTimes;//运算表达式个数
	int i;
	BYTE operType;//表达式操作符
	BOOL bValue = FALSE;
	BOOL opValue = FALSE;
	if(pSibBasic == NULL)
		return;
	if(pSibBasic->paramTotal == 0)
		return;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(pSibBasic->paramTotal-1),TRUE))
		return ;
	bFunTimes = (sibParam.paramNo/4) + 1;
	for( i=0 ; i<bFunTimes; i++)
	{
		
		opValue = IF_Operation_Control_Value(senssionID,serviceID,sibID,(BYTE)i);
		if(i==0)
		{
			bValue = opValue;
		}
		
		
		if(i>0)
		{
			switch(operType)
			{
			case 0://and
				bValue = bValue && opValue;
				break;
			case 1://or
				bValue = bValue || opValue;
				break;
				//			case 2://not
				//				bValue = !opValue;
				//				break;
			default:
				break;
			}
			
		}
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&operType,sizeof(operType),(BYTE)(4*i+3),0))
			break;
	}
	//设置下一步操作
	if(bValue)
	{
		UpdateNextSib(senssionID,sibID,TRUE);		
	}else
	{
		UpdateNextSib(senssionID,sibID,FALSE);
	}
	//	RunSib(senssionID);
}
/********************************************************************
函数名称：IF_Operation_Control_Value
函数功能:
参数定义: 无
返回定义: 无
创建时间: 2008-8-28 16:18
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL IF_Operation_Control_Value(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	BOOL bValue = FALSE;
	DWORD op1=0;
	BYTE op=0;
	DWORD op2=0;
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	if(sVariable.type==Var_CHARSTR)
	{
		bValue=IF_Operation_Control_Value_CHAR(senssionID,serviceID,sibID,bTimes);
	}
	else
	{
		bValue=IF_Operation_Control_Value_INT(senssionID,serviceID,sibID,bTimes);
	}
	
	return bValue;
	
}
BOOL IF_Operation_Control_Value_INT(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	BOOL bValue = FALSE;
	int op1=0;
	BYTE op=0;
	int op2=0;
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	if(sVariable.type==Var_CHARSTR)
	{
		return FALSE;
	}
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&op1,sizeof(op1),(BYTE)(4*bTimes),0))
		return FALSE;
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&op,sizeof(op),(BYTE)(4*bTimes+1),0))
	{
		if(op1 == 0)
			return FALSE;
		else
			return TRUE;
	}
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&op2,sizeof(op2),(BYTE)(4*bTimes+2),0))
		return FALSE;
	switch(op)
	{
	case 0://=
		if(op1==op2)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 1://>
		if(op1>op2)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 2://<
		if(op1<op2)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 3://>=
		if(op1>=op2)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 4://<=
		if(op1<=op2)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 5://!=
		if(op1!=op2)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	default:
		break;
	}
	return bValue;
	
}

BOOL IF_Operation_Control_Value_CHAR(WORD senssionID,BYTE serviceID,WORD sibID,BYTE bTimes)
{
	BOOL bValue = FALSE;
	char  op1[256];
	BYTE op=0;
	char  op2[256];
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	int value;
	memset(op1,0,sizeof(op1));
	memset(op2,0,sizeof(op2));
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(4*bTimes),FALSE))//
		return FALSE;
	if(sibParam.paramType==Param_VARIABLE)
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
			return FALSE;
	}
	else
	{
		if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,TRUE))
			return FALSE;
	}
	if(sVariable.type!=Var_CHARSTR)
	{
		return FALSE;
	}
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&op1,sizeof(op1),(BYTE)(4*bTimes),0))
		return FALSE;
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,&op,sizeof(op),(BYTE)(4*bTimes+1),0))
	{
		if(op1 == "")
			return FALSE;
		else
			return TRUE;
	}
	
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&op2,sizeof(op2),(BYTE)(4*bTimes+2),0))
		return FALSE;
	value=strcmp(op1,op2);
	switch(op)
	{
	case 0://=
		if(value==0)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 1://>
		if(value>0)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 2://<
		if(value<0)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 3://>=
		if(value>=0)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 4://<=
		if(value<=0)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	case 5://!=
		if(value!=0)
			bValue = TRUE;
		else
			bValue = FALSE;
		break;
	default:
		break;
	}
	return bValue;
	
}
/********************************************************************
函数名称：Run_SIB_if
函数功能:
参数定义: senssionID：会话号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_if(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibIf)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	IF_Operation_Control(senssionID,serviceID,sibID,&sibBasic);
	//	UpdateNextSib(senssionID,sibID,TRUE);
	//	RunSib(senssionID);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_Switch
函数功能: switch判断
参数定义: senssionID：会话号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_Switch(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	SIB_PARAM sibParam;
	BOOL bFind = FALSE;
	BYTE serviceID;
	int i;
	int value[2];
	int caseValue[10];
	WORD  caseNext[10];
	BOOL bTrue = FALSE;
	int bFunTimes;
	memset(caseValue,0,sizeof(caseValue));
	memset(caseNext,0,sizeof(caseNext));
	memset(value,0,sizeof(value));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibSwitch)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(sibBasic.paramTotal-1),TRUE))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	bFunTimes = ((sibParam.paramNo-1)/2) + 1;
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)value,sizeof(value),0,0))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	for(i=0;i<bFunTimes;i++)
	{
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&caseValue[i],sizeof(caseValue[i]),(BYTE)(2*i+1),0))
		{
			UpdateNextSib(senssionID,sibID,FALSE);
			return TRUE;
		}
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&caseNext[i],sizeof(caseNext[i]),(BYTE)(2*i+2),0))
		{
			UpdateNextSib(senssionID,sibID,FALSE);
			return TRUE;
		}
		if(value[0] == caseValue[i])
		{
			pMessage.slavehead.sibNo = caseNext[i];
			UpdateXSCMMessage(senssionID,&pMessage,FALSE);
			XVAPL_Push(senssionID,caseNext[i]);
			bTrue = TRUE;
			break;	
		}
	}
	if(!bTrue)
	{
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_Compare
函数功能: 比较SIB，根据两个给定值的大小决定流程方向
参数定义: senssionID：会话号,sibID:操作的SIB编号
返回定义: 无
创建时间: 2008-8-28 17:20
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL Run_SIB_Compare(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	DWORD Op1,Op2;
	int Link1,Link2;
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibCommpare)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Op1,sizeof(Op1),0,0))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Op2,sizeof(Op2),1,0))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Link1,sizeof(Link1),2,0))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&Link2,sizeof(Link2),3,0))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	if(Op1==Op2)
	{
		pMessage.slavehead.sibNo = sibBasic.nextstep;
	}
	else if(Op1 > Op2)
	{
		pMessage.slavehead.sibNo = Link1;
	}
	else
	{
		pMessage.slavehead.sibNo = Link2;
	}
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	XVAPL_Push(senssionID,pMessage.slavehead.sibNo);
	return TRUE;
}

/********************************************************************
函数名称：Run_SIB_BCSM
函数功能: 设置BCSM事件
参数定义: senssionID：会话号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_BCSM(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_RequestReportBCSMEvent pBcsmEvent;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int i;
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	if(sibBasic.sibType != tsibBCSM)
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memset(&pBcsmEvent,0,sizeof(stPXSSM_RequestReportBCSMEvent));
	pMessage.messageInfor.head.event=evPXSSM_RequestReportBCSMEvent;
	pBcsmEvent.sid = senssionID;
	pBcsmEvent.eventNumber = 0;
	memset(pBcsmEvent.events,0,sizeof(pBcsmEvent.events));
	if(sibBasic.paramTotal > 0)
	{
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pBcsmEvent.eventNumber,sizeof(pBcsmEvent.eventNumber),0,0);
		for(i=0;i<pBcsmEvent.eventNumber;i++)
		{
			if(i<sizeof(pBcsmEvent.events))
				GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pBcsmEvent.events[i],sizeof(pBcsmEvent.events[i]),(BYTE)(i+1),0);
		}
		
	}
	
	pMessage.messageInfor.head.len=sizeof(stPXSSM_RequestReportBCSMEvent);
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pBcsmEvent,sizeof(stPXSSM_RequestReportBCSMEvent));
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	UpdateNextSib(senssionID,sibID,TRUE);
	//	RunSib(senssionID);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_ReleaseCall
函数功能: 释放呼叫 
参数定义: senssionID：会话号,sibID:sib编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_ReleaseCall(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_ReleaseCall pReleaseCall;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	memset(&pReleaseCall,0,sizeof(stPXSSM_ReleaseCall));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType != tsibReleaseCall)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	
	pMessage.messageInfor.head.event=evPXSSM_ReleaseCall;
	pReleaseCall.sid=senssionID;
	pReleaseCall.reason = 0;
	if(sibBasic.paramTotal > 0)
	{
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pReleaseCall.reason,sizeof(pReleaseCall.reason),0,0);
		
	}
	
	pMessage.messageInfor.head.len=sizeof(stPXSSM_ReleaseCall);
	if(pMessage.slavehead.serInit.servicetype==0)//PSTN
	{
		if(!GetTid(MID_ACC,&pTid))
			return FALSE;
		bReceiveModule = MID_ACC;
	}
	else if(pMessage.slavehead.serInit.servicetype==1)//TCP
	{
		if(!GetTid(g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,&pTid))
			return FALSE;
		bReceiveModule = g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID;
	}
	else
		return FALSE;
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pReleaseCall,sizeof(stPXSSM_ReleaseCall));
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);

	bSuc =SEND_MSG(evIPCMessage,bReceiveModule,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	UpdateXSCMMessage(senssionID,NULL,TRUE);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_PlayAnnouncement
函数功能: 放音
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_PlayAnnouncement(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_PlayAnnouncement pPlayTone;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid;
	BYTE serviceID;
	int   timerId = -1;
	memset(&pPlayTone,0,sizeof(stPXSSM_PlayAnnouncement));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibPlayAnnouncement)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_PlayAnnouncement;	
	pPlayTone.sid=senssionID;
	pPlayTone.tone.variable=0;
	
	pPlayTone.canInterupt=FALSE;
	pPlayTone.duration=0;
	pPlayTone.interval=0;
	pPlayTone.repeatTime=0;
	pPlayTone.tone.vchoiceVariable.toneID=0;
	if(sibBasic.paramTotal > 0)
	{
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPlayTone.tone.vchoiceVariable.toneID,sizeof(pPlayTone.tone.vchoiceVariable.toneID),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPlayTone.repeatTime,sizeof(pPlayTone.repeatTime),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPlayTone.duration,sizeof(pPlayTone.duration),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPlayTone.interval,sizeof(pPlayTone.interval),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPlayTone.canInterupt,sizeof(pPlayTone.canInterupt),4,0);
		
	}
	pMessage.messageInfor.head.len=sizeof(stPXSSM_PlayAnnouncement);
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pPlayTone,sizeof(stPXSSM_PlayAnnouncement));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if((pPlayTone.duration>TIMER_DTMF)||(pPlayTone.duration<=0))
		pPlayTone.duration = TIMER_DTMF;
	timerId = SET_TIMER(pPlayTone.duration,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	pMessage.slavehead.wlastevent=evPXSSM_PlayAnnouncement;
	pMessage.slavehead.timerevent[0].timerId = timerId;
	pMessage.slavehead.timerevent[0].bUsed = TRUE;
	
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	UpdateNextSib(senssionID,sibID,TRUE);
	//	RunSib(senssionID);
	//	return TRUE;
	return FALSE;;
}
/********************************************************************
函数名称：Run_SIB_PromptCollectInformation
函数功能: 放音并设置接收DTMF格式的SIB
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_PromptCollectInformation(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_PromptCollectInformation pPromptCollectInformation;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid;
	BYTE serviceID;
	int  opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	memset(&pPromptCollectInformation,0,sizeof(stPXSSM_PromptCollectInformation));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibPromptCollectInformation)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;	
	}
	pMessage.messageInfor.head.event=evPXSSM_PromptCollectInformation;	
	pPromptCollectInformation.playTone.tone.variable=0;
	pPromptCollectInformation.minDigits=0;
	pPromptCollectInformation.maxDigits=64;
	pPromptCollectInformation.endDigit='#';
	pPromptCollectInformation.cancelDigits=0;
	pPromptCollectInformation.startDigit=0;
	pPromptCollectInformation.firstDigitTimeOut=10;
	pPromptCollectInformation.interDigitTimeOut=10;
	pPromptCollectInformation.playTone.canInterupt=FALSE;
	pPromptCollectInformation.playTone.duration=0;
	pPromptCollectInformation.playTone.interval=0;
	pPromptCollectInformation.playTone.repeatTime=0;
	pPromptCollectInformation.playTone.sid = senssionID;
	
	pPromptCollectInformation.playTone.tone.vchoiceVariable.toneID=0;
	if(sibBasic.paramTotal > 0)
	{
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.minDigits,sizeof(pPromptCollectInformation.minDigits),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.maxDigits,sizeof(pPromptCollectInformation.maxDigits),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.endDigit,sizeof(pPromptCollectInformation.endDigit),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.cancelDigits,sizeof(pPromptCollectInformation.cancelDigits),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.startDigit,sizeof(pPromptCollectInformation.startDigit),4,0);
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.firstDigitTimeOut,sizeof(pPromptCollectInformation.firstDigitTimeOut),5,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.interDigitTimeOut,sizeof(pPromptCollectInformation.interDigitTimeOut),6,0);
		
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.playTone.tone.vchoiceVariable.toneID,sizeof(pPromptCollectInformation.playTone.tone.vchoiceVariable.toneID),7,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.playTone.repeatTime,sizeof(pPromptCollectInformation.playTone.repeatTime),8,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.playTone.duration,sizeof(pPromptCollectInformation.playTone.duration),9,0);
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.playTone.interval,sizeof(pPromptCollectInformation.playTone.interval),10,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectInformation.playTone.canInterupt,sizeof(pPromptCollectInformation.playTone.canInterupt),11,0);
		/************************************************************************/
		/*           //返回DTMF结果存储的变量,存放位置???                       */
		/************************************************************************/
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),12);
		
		//////////////////////////////////////////////////////////////////////////
		delayTimer=pPromptCollectInformation.playTone.duration;
		
		
	}
	pMessage.messageInfor.head.len=sizeof(stPXSSM_PromptCollectInformation);
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	pMessage.sVaribleSave.bVaribleTotal = 1;
	pMessage.sVaribleSave.dVaribleNo[0] = opsition;
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pPromptCollectInformation,sizeof(stPXSSM_PromptCollectInformation));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if((delayTimer>TIMER_DTMF)||(delayTimer<=0))
		delayTimer = TIMER_DTMF;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_PromptCollectInformation;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	
	
	
	//	return TRUE;
	return FALSE;
	//	RunSib(senssionID);
}
/********************************************************************
函数名称：Run_SIB_PromptCollectInformationAndSetFormat
函数功能: 设置接收DTMF的指令
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_PromptCollectInformationAndSetFormat(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_PromptCollectInformationAndSetFormat pPromptCollectInformation;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int  opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	memset(&pPromptCollectInformation,0,sizeof(stPXSSM_PromptCollectInformationAndSetFormat));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibPromptCollectInformationAndSetFormat)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_PromptCollectInformationAndSetFormat;	
	
	pPromptCollectInformation.sid = senssionID;
	pPromptCollectInformation.minDigits=0;
	pPromptCollectInformation.maxDigits=64;
	pPromptCollectInformation.endDigit='#';
	pPromptCollectInformation.cancelDigits=0;
	pPromptCollectInformation.startDigit=0;
	pPromptCollectInformation.firstDigitTimeOut=10;
	pPromptCollectInformation.interDigitTimeOut=10;
	
	if(sibBasic.paramTotal > 0)
	{
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.minDigits,sizeof(pPromptCollectInformation.minDigits),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.maxDigits,sizeof(pPromptCollectInformation.maxDigits),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.endDigit,sizeof(pPromptCollectInformation.endDigit),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.cancelDigits,sizeof(pPromptCollectInformation.cancelDigits),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.startDigit,sizeof(pPromptCollectInformation.startDigit),4,0);
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.firstDigitTimeOut,sizeof(pPromptCollectInformation.firstDigitTimeOut),5,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pPromptCollectInformation.interDigitTimeOut,sizeof(pPromptCollectInformation.interDigitTimeOut),6,0);
		/************************************************************************/
		/*           //返回DTMF结果存储的变量,存放位置???                       */
		/************************************************************************/
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),7);
		
		
	}
	delayTimer = pPromptCollectInformation.firstDigitTimeOut+pPromptCollectInformation.interDigitTimeOut*(pPromptCollectInformation.maxDigits-1);
	pMessage.messageInfor.head.len=sizeof(stPXSSM_PromptCollectInformationAndSetFormat);
	memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
	pMessage.sVaribleSave.bVaribleTotal = 1;
	pMessage.sVaribleSave.dVaribleNo[0] = opsition;
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pPromptCollectInformation,sizeof(stPXSSM_PromptCollectInformationAndSetFormat));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if((delayTimer>TIMER_DTMF)||(delayTimer<=0))
		delayTimer = TIMER_DTMF;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_PromptCollectInformationAndSetFormat;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	//	return TRUE;
	return FALSE;
	//	RunSib(senssionID);
}
/********************************************************************
函数名称：Run_SIB_PromptCollectFSK
函数功能: 放音并设置接收FSK
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_PromptCollectFSK(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_PromptCollectFSK pPromptCollectFSK; 
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	memset(&pPromptCollectFSK,0,sizeof(stPXSSM_PromptCollectFSK));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibPromptCollectFSK)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_PromptCollectFSK;	
	pPromptCollectFSK.playTone.sid=senssionID;
	
	
	pPromptCollectFSK.playTone.tone.variable = 0;
	pPromptCollectFSK.playTone.canInterupt=FALSE;
	pPromptCollectFSK.playTone.duration=0;
	pPromptCollectFSK.playTone.interval=0;
	pPromptCollectFSK.playTone.repeatTime=0;
	pPromptCollectFSK.Fskenable = TRUE;
	
	
	if(sibBasic.paramTotal > 0)
	{
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectFSK.playTone.tone.vchoiceVariable.toneID,sizeof(pPromptCollectFSK.playTone.tone.vchoiceVariable.toneID),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectFSK.playTone.repeatTime,sizeof(pPromptCollectFSK.playTone.repeatTime),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectFSK.playTone.duration,sizeof(pPromptCollectFSK.playTone.duration),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectFSK.playTone.interval,sizeof(pPromptCollectFSK.playTone.interval),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectFSK.playTone.canInterupt,sizeof(pPromptCollectFSK.playTone.canInterupt),4,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pPromptCollectFSK.Fskenable,sizeof(pPromptCollectFSK.Fskenable),5,0);
		/************************************************************************/
		/*        返回FSK结果存储变量                                           */
		/************************************************************************/
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),6);
		//////////////////////////////////////////////////////////////////////////
		
	}
	delayTimer=pPromptCollectFSK.playTone.duration;
	pMessage.messageInfor.head.len=sizeof(stPXSSM_PromptCollectFSK);
	memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
	pMessage.sVaribleSave.bVaribleTotal = 1;
	pMessage.sVaribleSave.dVaribleNo[0] = opsition;
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pPromptCollectFSK,sizeof(stPXSSM_PromptCollectFSK));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if((delayTimer>TIMER_FSK)||(delayTimer<=0))
		delayTimer = TIMER_FSK;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_PromptCollectFSK;
		
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	//	return TRUE;
	return FALSE;
	//	RunSib(senssionID);
}
/********************************************************************
函数名称：Run_SIB_SendFSK
函数功能: 发送FSK
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_SendFSK(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_SendFSK pSendFSK; 
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int delayTimer = -1; //定时器
	int timerId;
	WORD datalen=0;
	BYTE data[1024];
	//WORD llen;
	memset(&pSendFSK,0,sizeof(stPXSSM_SendFSK));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibSendFSK)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_SendFSK;
	
	pSendFSK.sid=senssionID;
	pSendFSK.duration=0;
	
	pSendFSK.fsk.length = 0;
	memset(pSendFSK.fsk.message,0,sizeof(pSendFSK.fsk.message));
	
	
	if(sibBasic.paramTotal > 0)
	{
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendFSK.fsk.length,sizeof(pSendFSK.fsk.length),0,0))
			datalen=sizeof(pSendFSK.fsk.message);
		else
		{
			if(pSendFSK.fsk.length>sizeof(pSendFSK.fsk.message))
			{
				datalen=sizeof(pSendFSK.fsk.message);
				pSendFSK.fsk.length=datalen;
			}
			else
			{
				datalen=pSendFSK.fsk.length;
			}
			
		}
		memset(data,0,sizeof(data));
		GetFunctionVaribleValue(senssionID,serviceID,sibID,data,sizeof(data),1,0);
		if(sizeof(data)>datalen)
		{
			memcpy(pSendFSK.fsk.message,data,datalen);
		}
		else
		{
			memcpy(pSendFSK.fsk.message,data,sizeof(data));
			pSendFSK.fsk.length = sizeof(data);
		}
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&delayTimer,sizeof(delayTimer),2,0);
	}
	if(UpdateFskData(pSendFSK.fsk.message,pSendFSK.fsk.length,pMessage.slavehead.step)<=0)
		return FALSE;
	pMessage.messageInfor.head.len=sizeof(stPXSSM_SendFSK);
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	pMessage.slavehead.oldevent = evPXSSM_SendFSK;
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pSendFSK,sizeof(stPXSSM_SendFSK));
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if((delayTimer>TIMER_FSK)||(delayTimer<=0))
		delayTimer = TIMER_FSK;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_SendFSK;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	//	RunSib(senssionID);
	//return TRUE;
	return FALSE;
}
/********************************************************************
函数名称：Run_SIB_SendFSKCollectInformation
函数功能: 发送FSK并设置接收DTMF
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_SendFSKCollectInformation(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_SendFSKCollectInformation pSendFSK; 
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int opsition = -1;
	int delayTimer = -1; //定时器
	int   timerId = -1;
	WORD datalen=0;
	BYTE data[1024];
	WORD llen;
	memset(&pSendFSK,0,sizeof(stPXSSM_SendFSKCollectInformation));
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibSendFSKCollectInformation)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_SendFSKCollectInformation;
	
	pSendFSK.playFSK.sid=senssionID;
	pSendFSK.minDigits=1;
	pSendFSK.maxDigits=20;
	pSendFSK.endDigit='#';
	pSendFSK.cancelDigits='*';
	pSendFSK.startDigit=0x87;
	pSendFSK.firstDigitTimeOut=30;
	pSendFSK.interDigitTimeOut=20;
	pSendFSK.playFSK.duration=0;
	
	pSendFSK.playFSK.fsk.length = 0;
	memset(pSendFSK.playFSK.fsk.message,0,sizeof(pSendFSK.playFSK.fsk.message));
	
	
	if(sibBasic.paramTotal > 0)
	{
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pSendFSK.minDigits,sizeof(pSendFSK.minDigits),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pSendFSK.maxDigits,sizeof(pSendFSK.maxDigits),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pSendFSK.endDigit,sizeof(pSendFSK.endDigit),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pSendFSK.cancelDigits,sizeof(pSendFSK.cancelDigits),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pSendFSK.startDigit,sizeof(pSendFSK.startDigit),4,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&pSendFSK.firstDigitTimeOut,sizeof(pSendFSK.firstDigitTimeOut),5,0);
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendFSK.interDigitTimeOut,sizeof(pSendFSK.interDigitTimeOut),6,0);
		
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendFSK.playFSK.fsk.length,sizeof(pSendFSK.playFSK.fsk.length),7,0))
			datalen=sizeof(pSendFSK.playFSK.fsk.message);
		else
		{
			if(pSendFSK.playFSK.fsk.length>sizeof(pSendFSK.playFSK.fsk.message))
			{
				datalen=sizeof(pSendFSK.playFSK.fsk.message);
				pSendFSK.playFSK.fsk.length=datalen;
			}
			else
			{
				datalen=pSendFSK.playFSK.fsk.length;
			}
			
		}
		memset(data,0,sizeof(data));
		GetFunctionVaribleValue(senssionID,serviceID,sibID,data,sizeof(data),8,0);
		memcpy(&llen,data,sizeof(WORD));
		if(sizeof(data)>datalen)
		{
			memcpy(pSendFSK.playFSK.fsk.message,data,datalen);
		}
		else
		{
			memcpy(pSendFSK.playFSK.fsk.message,data,sizeof(data));
			pSendFSK.playFSK.fsk.length = sizeof(data);
			
		}
		//		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)pSendFSK.playFSK.fsk.message,datalen,8,0);
		/************************************************************************/
		/*        返回DTMF结果存储变量                                           */
		/************************************************************************/
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),9);
		//////////////////////////////////////////////////////////////////////////
		
	}
	delayTimer=pSendFSK.firstDigitTimeOut+pSendFSK.interDigitTimeOut*(pSendFSK.maxDigits-1);
	pMessage.messageInfor.head.len=sizeof(stPXSSM_SendFSKCollectInformation);
	memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
	pMessage.sVaribleSave.bVaribleTotal = 1;
	pMessage.sVaribleSave.dVaribleNo[0] = opsition;
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pSendFSK,sizeof(stPXSSM_SendFSKCollectInformation));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	if((delayTimer>TIMER_DTMF)||(delayTimer<0))
		delayTimer = TIMER_DTMF;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_SendFSKCollectInformation;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	//	return TRUE;
	return FALSE;
	//	RunSib(senssionID);
}
/********************************************************************
函数名称：Run_SIB_SendFSKCollectFSK
函数功能: 发送FSK并接收FSK
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_SendFSKCollectFSK(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_SendFSKCollectFSK pSendFskCollect;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	WORD datalen=0;
	BYTE data[1024];
	//	WORD llen;
	memset(&pSendFskCollect,0,sizeof(stPXSSM_SendFSKCollectFSK));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibSendFSKCollectFSK)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_SendFSKCollectFSK;
	
	pSendFskCollect.timeOut=50;
	pSendFskCollect.playFSK.duration=0;
	pSendFskCollect.playFSK.sid=senssionID;
	pSendFskCollect.playFSK.fsk.length=0;
	memset(pSendFskCollect.playFSK.fsk.message,0,sizeof(pSendFskCollect.playFSK.fsk.message));
	
	
	if(sibBasic.paramTotal > 0)
	{
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendFskCollect.timeOut,sizeof(pSendFskCollect.timeOut),0,0);
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendFskCollect.playFSK.fsk.length,sizeof(pSendFskCollect.playFSK.fsk.length),1,0))
			datalen=sizeof(pSendFskCollect.playFSK.fsk.message);
		else
		{
			if(pSendFskCollect.playFSK.fsk.length>sizeof(pSendFskCollect.playFSK.fsk.message))
			{
				datalen=sizeof(pSendFskCollect.playFSK.fsk.message);
				pSendFskCollect.playFSK.fsk.length=datalen;
			}
			else
			{
				datalen=pSendFskCollect.playFSK.fsk.length;
			}
			
		}
		memset(data,0,sizeof(data));
		//GetFunctionVaribleValue(senssionID,serviceID,sibID,pSendFskCollect.playFSK.fsk.message,datalen,2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,data,sizeof(data),2,0);
		//		memcpy(&llen,data,sizeof(WORD));
		if(sizeof(data)>datalen)
		{
			memcpy(pSendFskCollect.playFSK.fsk.message,data,datalen);
		}
		else
		{
			memcpy(pSendFskCollect.playFSK.fsk.message,data,sizeof(data));
			pSendFskCollect.playFSK.fsk.length = sizeof(data); 
		}
		/************************************************************************/
		/*        返回FSK结果存储变量                                           */
		/************************************************************************/
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),3);
		
	}
	pMessage.messageInfor.head.len=sizeof(stPXSSM_SendFSKCollectFSK);
	memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
	pMessage.sVaribleSave.bVaribleTotal = 1;
	pMessage.sVaribleSave.dVaribleNo[0] = opsition;
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pSendFskCollect,sizeof(stPXSSM_SendFSKCollectFSK));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	delayTimer = pSendFskCollect.timeOut;
	if((delayTimer>TIMER_DTMF)||(delayTimer<=0))
		delayTimer = TIMER_DTMF;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_SendFSKCollectFSK;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	//	return TRUE;
	return FALSE;
	//	RunSib(senssionID);
	
}
/***************************
函数名称：Run_SIB_CallOutSib
函数功能: 外呼
参数定义:senssionID:会话号，sibID：业务编号
返回定义: 无
创建时间: 2009/12/18  16:59:23
函数作者: LDW
注意事项: 无
****************************/
BOOL Run_SIB_CallOutSib(WORD senssionID,WORD sibID)
{
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_Connect pCallOutCollect;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	WORD datalen=0;
	
	memset(&pCallOutCollect,0,sizeof(stPXSSM_Connect));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibAttempCallOutSib)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_Connect;
	
	memset(&pCallOutCollect,0,sizeof(pCallOutCollect));
	
	pCallOutCollect.sid=senssionID;
	if(sibBasic.paramTotal > 0)
	{
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,pCallOutCollect.routeNumber,sizeof(pCallOutCollect.routeNumber),0,0);
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,pCallOutCollect.callingNumber,sizeof(pCallOutCollect.callingNumber),1,0);
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pCallOutCollect.needNullCDR,sizeof(pCallOutCollect.needNullCDR),2,0);
		
		
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pCallOutCollect.timeOut,sizeof(pCallOutCollect.timeOut),3,0))
			pCallOutCollect.timeOut=60*10;
		
		/************************************************************************/
		/*        返回FSK结果存储变量                                           */
		/************************************************************************/
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),4);
		
	}
	pMessage.messageInfor.head.len=sizeof(stPXSSM_Connect);
	memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
	pMessage.sVaribleSave.bVaribleTotal = 1;
	pMessage.sVaribleSave.dVaribleNo[0] = opsition;
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pCallOutCollect,sizeof(stPXSSM_Connect));
	
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	bSuc =SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	delayTimer = pCallOutCollect.timeOut+50;
	
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evPXSSM_Connect;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	//	return TRUE;
	return FALSE;
	//	RunSib(senssionID);
	
}

/********************************************************************
函数名称：Run_SIB_InitiateRecord
函数功能: 录音
参数定义: senssionID：会话号，sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_InitiateRecord(WORD senssionID,WORD sibID)
{
	
	DWORD bReceiveModule;
	BOOL bSuc=FALSE;
	stPXSSM_InitiateRecord pInitiateRecord;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid;
	BYTE serviceID;
	memset(&pInitiateRecord,0,sizeof(stPXSSM_InitiateRecord));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibInitiateRecord)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evPXSSM_InitiateRecord;
	
	pInitiateRecord.sid = senssionID;
	pInitiateRecord.fileIndex = 0;
	pInitiateRecord.duration = 0;
	pInitiateRecord.canInterupt = TRUE;
	pInitiateRecord.replaceExistedFile = FALSE;
	
	if(sibBasic.paramTotal > 0)
	{
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pInitiateRecord.fileIndex,sizeof(pInitiateRecord.fileIndex),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pInitiateRecord.duration,sizeof(pInitiateRecord.duration),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pInitiateRecord.canInterupt,sizeof(pInitiateRecord.canInterupt),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pInitiateRecord.replaceExistedFile,sizeof(pInitiateRecord.replaceExistedFile),3,0);
		
	}
	pMessage.messageInfor.head.len=sizeof(stPXSSM_InitiateRecord);
	if(!GetTid(MID_ACC,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pInitiateRecord,sizeof(stPXSSM_InitiateRecord));
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	bReceiveModule = pMessage.messageInfor.head.Source.mId;
	
	bSuc = SEND_MSG(evIPCMessage,MID_ACC,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	UpdateNextSib(senssionID,sibID,TRUE);
	//	RunSib(senssionID);
	return TRUE;
	
}
/********************************************************************
函数名称：GetInVaribleTotal
函数功能: 得到输入返回变量定义个数
参数定义: pSqlExe：SQL语句
返回定义: 返回输入变量个数
创建时间: 2008-11-6 11:08
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE GetInVaribleTotal(char *pSqlExe)
{
	char *pchar = NULL;
	BYTE total = 0;
	if(pSqlExe == NULL)
		total = 0;
	else
	{
		pchar = strtok(pSqlExe,":");
		if(pchar != NULL)
		{
			while(strtok(NULL,":"))
			{
				total ++;
			}
		}
		
	}
	return total;
}
/********************************************************************
函数名称：GetOutVaribleTotal
函数功能: 得到输出返回变量定义个数
参数定义: pSqlExe：SQL语句
返回定义: 返回输出变量个数
创建时间: 2008-11-6 11:04
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BYTE GetOutVaribleTotal(char *pSqlExe)
{
	char *pchar = NULL;
	BYTE total = 0;
	if(pSqlExe == NULL)
		total = 0;
	else
	{
		pchar = strtok(pSqlExe,"@");
		if(pchar != NULL)
		{
			while(strtok(NULL,"@"))
			{
				total ++;
			}
		}
		
	}
	return total;
}

/********************************************************************
函数名称：UpdateSqlExeStr
函数功能: 更新填充SQL语句，将变量值取出来填充到SQL语句当中
参数定义: senssionID:会话编号
sibID:sib编号
serviceID：业务编号
*pSqlExe：填充前的SQL语句
*sqlstr：填充后需要返回的SQL语句
len：返回的填充语句空间长度
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-11-6 15:23
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateSqlExeStr(WORD senssionID,BYTE serviceID,WORD sibID,char *pSqlExe,char *sqlstr,WORD len)
{
	
	char *pchar = NULL;
	BYTE total = 0;
	SIB_PARAM sSibParam;
	char value[1024];
	char RemoveValue[256];
	char Formatvalue[1024];
	BYTE data=0;
	WORD wData=0;
	DWORD dData=0;
	BOOL  bTrue=FALSE;
	char buffer[4];
	int i=0;
	int j = 0;
	int findtotal = 0;
	int findvalue=0;
	int nlen;
	int outfun = 0;
	XSCM_CONSTVARIABLE pVarible ;
	
	memset(sqlstr,0,len);
	if((pSqlExe == NULL)||(sqlstr == NULL))
	{	total = 0;
	return FALSE;
	}
	nlen  = strlen(pSqlExe);
	for(j=0;j<nlen;j++)
	{
		if(pSqlExe[j]=='^')
			findtotal ++;
		else if(pSqlExe[j]=='$')
		{
			outfun++;
		}
		
	}
	if((findtotal==0)||(findtotal%2==1))
	{
		strcpy(sqlstr,pSqlExe);
		findvalue++;
	}
	else
	{
		
		pchar = strtok(pSqlExe,"^");
		if(pchar != NULL)
		{
			strcat(sqlstr,pchar);
			while(pchar=strtok(NULL,"^"))
			{
				memset(&sSibParam,0,sizeof(SIB_PARAM));
				memset(buffer,0,sizeof(buffer));
				memset(value,0,sizeof(value));
				total=atoi(pchar);
				if(!GetSibParamAttr(serviceID,sibID,&sSibParam,total,FALSE))
					continue;
				switch(sSibParam.paramType)
				{
				case Param_VARIABLE:
					if(!GetParamAttr(serviceID,(WORD)sSibParam.paramValue,&pVarible,FALSE))
						break;
					if(pVarible.type==Var_BYTE)//BYTE
					{
						GetVaribleValue(senssionID,&data,pVarible.offset,1);
						_itoa(data,buffer,10);
						if((strlen(buffer)+strlen(sqlstr))<=len)
							strncat(sqlstr,buffer,strlen(buffer));	
					}
					else if(pVarible.type==Var_BOOL)//BOOL
					{
						GetVaribleValue(senssionID,(BYTE *)&bTrue,pVarible.offset,1);
						_itoa(bTrue,buffer,10);
						if((strlen(buffer)+strlen(sqlstr))<=len)
							strncat(sqlstr,buffer,strlen(buffer));
					}
					else if(pVarible.type==Var_WORD)//WORD
					{
						GetVaribleValue(senssionID,(BYTE *)&wData,pVarible.offset,sizeof(WORD));
						_itoa(wData,buffer,10);
						if((strlen(buffer)+strlen(sqlstr))<=len)
							strncat(sqlstr,buffer,strlen(buffer));
					}
					else if(pVarible.type==Var_DWORD)//DWORD
					{
						GetVaribleValue(senssionID,(BYTE *)&dData,pVarible.offset,sizeof(DWORD));
						_itoa(dData,buffer,10);
						if((strlen(buffer)+strlen(sqlstr))<=len)
							strncat(sqlstr,buffer,strlen(buffer));
					}
					else if(pVarible.type==Var_CHARSTR)//char string
					{
						if(sizeof(value)>=pVarible.length)
							GetVaribleValue(senssionID,value,pVarible.offset,pVarible.length);
						else
						{
							GetVaribleValue(senssionID,value,pVarible.offset,sizeof(value));
						}
						memset(Formatvalue,0,sizeof(Formatvalue));
						sprintf(Formatvalue,"'%s'",value);
						if((strlen(Formatvalue)+strlen(sqlstr))<=len)
							strncat(sqlstr,Formatvalue,strlen(Formatvalue));
					}
					pchar=strtok(NULL,"^");
					if(pchar!=NULL)
					{
						if((strlen(pchar)+strlen(sqlstr))<=len)
							strcat(sqlstr,pchar);
					}
					break;
				default:
					break;
				}
				
				findvalue ++;
			}
			findvalue ++;
		}
		
	}
	//del all @output@
	
	if((outfun==0)||(outfun%2==1))
		return(findvalue>0?TRUE:FALSE);
	memset(RemoveValue,0,sizeof(RemoveValue));
	strcpy(RemoveValue,sqlstr);
	memset(sqlstr,0,len);
	pchar = strtok(RemoveValue,"$");
	if(pchar != NULL)
	{
		strcat(sqlstr,pchar);
		while(pchar=strtok(NULL,"$"))
		{
			total=atoi(pchar);
			if(!GetSibParamAttr(serviceID,sibID,&sSibParam,total,FALSE))
				continue;				
			if(Param_VARIABLE==sSibParam.paramType)
			{
				if(!GetParamAttr(serviceID,(WORD)sSibParam.paramValue,&pVarible,FALSE))
					break;
				switch(pVarible.type)
				{
				case Var_BYTE:
				case Var_BOOL:
				case Var_WORD:
				case Var_DWORD:
					{
						memset(Formatvalue,0,sizeof(Formatvalue));
						sprintf(Formatvalue,"$INT");
						if((strlen(Formatvalue)+strlen(sqlstr))<=len)
							strncat(sqlstr,Formatvalue,strlen(Formatvalue));
						pchar=strtok(NULL,"$");
						if(pchar!=NULL)
						{
							if((strlen(pchar)+strlen(sqlstr))<=len)
								strcat(sqlstr,pchar);
						}
					}
					break;
				case Var_CHARSTR:
					{
						memset(Formatvalue,0,sizeof(Formatvalue));
						sprintf(Formatvalue,"$STR");
						if((strlen(Formatvalue)+strlen(sqlstr))<=len)
							strncat(sqlstr,Formatvalue,strlen(Formatvalue));
						pchar=strtok(NULL,"$");
						if(pchar!=NULL)
						{
							if((strlen(pchar)+strlen(sqlstr))<=len)
								strcat(sqlstr,pchar);
						}
					}
					break;
				default :
					break;
					
				}
				
			}
		}
	}
	return(findvalue>0?TRUE:FALSE);
}
/********************************************************************
函数名称：Run_SIB_DBQuery
函数功能:
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_DBQuery(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	char sqlExe[1024];
	WORD returnRecordNo = 0;
	VARIBLE_SAVE varibleSave; 
	int i;
	SIB_PARAM sSibParam;
	SQLSrcMsg strMsg;
	int delayTimer = -1; //定时器
	int   timerId = -1;
	TID pTid  ;
	memset(&strMsg,0,sizeof(SQLSrcMsg));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibDBQuery)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(sibBasic.paramTotal > 0)
	{
		memset(sqlExe,0,sizeof(sqlExe));
		memset(strMsg.sSql,0,sizeof(strMsg.sSql));
		memset(&varibleSave,0,sizeof(VARIBLE_SAVE));
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&returnRecordNo,sizeof(returnRecordNo),20,0);//返回第几个结果集，=0无结果集返回
		GetFunctionVaribleValue(senssionID,serviceID,sibID,sqlExe,sizeof(sqlExe),21,0);//得到SQL语句
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&delayTimer,sizeof(delayTimer),22,0);//得到延时时间
		for(i=0;i<10;i++)//得到返回结果存储变量的编号
		{
			if(GetSibParamAttr(serviceID,sibID,&sSibParam,(BYTE)(10+i),FALSE))
			{
				if(varibleSave.bVaribleTotal<sizeof(varibleSave.dVaribleNo))
				{
					
					varibleSave.dVaribleNo[varibleSave.bVaribleTotal]=sSibParam.paramValue;
					varibleSave.bVaribleTotal++;
				}
			}
		}
		memcpy(&pMessage.sVaribleSave,&varibleSave,sizeof(VARIBLE_SAVE));
		if(UpdateSqlExeStr(senssionID,serviceID,sibID,sqlExe,strMsg.sSql,(WORD)sizeof(strMsg.sSql)))
		{
			pMessage.messageInfor.head.event=evDBDAsk;
			pMessage.messageInfor.head.len=sizeof(strMsg.iRow)+strlen(strMsg.sSql);
			if(!GetTid(MID_DBD,&pTid))
			{
				UpdateNextSib(senssionID,sibID,FALSE);
				return TRUE;
			}
			memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
			if(!GetTid(MID_SLP,&pTid))
			{
				UpdateNextSib(senssionID,sibID,FALSE);
				return TRUE;
			}
			memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
			memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
			strMsg.iRow = returnRecordNo;
			
			memcpy(pMessage.messageInfor.data,(BYTE *)&strMsg,pMessage.messageInfor.head.len);
			UpdateXSCMMessage(senssionID,&pMessage,FALSE);
			SEND_MSG(evThreadMessage,MID_DBD,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
			//send dbd exe sql
			//			if(returnRecordNo>0)
			//			{
			if((delayTimer>TIMER_SQL)||(delayTimer<0))
				delayTimer = TIMER_SQL;
			timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
			if(timerId !=-1)
			{
				pMessage.slavehead.wlastevent=evDBDAsk;
				pMessage.slavehead.timerevent[0].timerId = timerId;
				pMessage.slavehead.timerevent[0].bUsed = TRUE;
			}
			UpdateXSCMMessage(senssionID,&pMessage,FALSE);
			/*			}*/
		}
		
	}
	UpdateNextSib(senssionID,sibID,TRUE);
	//	return TRUE;
	return FALSE;
}

/********************************************************************
函数名称：Run_SIB_CallSib
函数功能: 回掉SIB，执行完毕返回
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2009-1-5 14:46
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_CallSib(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	//	BYTE beginno;
	//	int i;
	WORD CallNo;
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibCallSib)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	XVAPL_Push(senssionID,sibBasic.nextstep);//将下一步压入堆栈
	if(sibBasic.paramTotal>=1)
	{
		if(GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&CallNo,sizeof(CallNo),(BYTE)0,0))
		{
			XVAPL_Push(senssionID,CallNo);//将下一步压入堆栈	
		}
	}
	
	
	return TRUE;
}
/********************************************************************

  函数名称: Run_SIB_Message
  函数功能: 发送消息
  参数定义: senssionID：会话号,sibID：SIB编号
  返回定义: 无
  创建时间: 2008-8-15 17:32
  函数作者: 刘定文
  注意事项: 无	
  
*********************************************************************/
BOOL Run_SIB_Message(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	SIB_PARAM sibParam;
	BOOL bFind = FALSE;
	BYTE serviceID;
	BYTE data[MAX_LENGTH];
	char data1[1024];
	BOOL dataType = FALSE;
	MESSAGE sMsg;
	int  len;
	DWORD dRecvMod = 0;    //接收消息的模块
	WORD  wMessageID = 0;  //消息编号
	int delayTimer = -1;   //定时器
	int   eventNumber = 0; //事件号
	WORD  RecallId = 0;
	WORD  event;
	int   timerId = -1;
	BOOL bSync = FALSE;
	int i,nCount;
	BYTE basicparam = 0;
	BYTE messageparam = 0;
	BYTE reparam = 0;
	BYTE paramNo;
	TID pTid;
	int offset = 0;
	WORD len1 = 0;
	XSCM_CONSTVARIABLE sConstVarible;
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
		return FALSE;
	memset(&sMsg,0,sizeof(sMsg));
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibMessage)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(sibBasic.paramTotal > 0)
	{
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(sibBasic.paramTotal-1),TRUE))
			return FALSE;
		paramNo = sibParam.paramNo;
		for(i=0;i<paramNo;i++)//统计各个部分的参数个数
		{
			if(GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)i,FALSE))	
			{
				if(i<10)
					basicparam = i + 1;
				else if(i<30)
					messageparam = i-10 + 1;
				else
					reparam = i - 30 + 1;
				
			}
		}
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&dRecvMod,sizeof(dRecvMod),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&wMessageID,sizeof(wMessageID),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&delayTimer,sizeof(delayTimer),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&eventNumber,sizeof(eventNumber),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&bSync,sizeof(bSync),4,0);
		memset(data,0,sizeof(data));
		for(i=0;i<messageparam;i++)
		{
			memset(&sibParam,0,sizeof(SIB_PARAM));
			GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(10+i),FALSE);
			memset(data1,0,sizeof(data1));
			//			GetFunctionVaribleValue(senssionID,serviceID,sibID,data1,sizeof(data1),(BYTE)(10+i),0);
			switch(sibParam.paramType)
			{
			case VAR_INT://整数值
				{
					memcpy(data+offset,data1,sizeof(sibParam.paramValue));
					offset = offset + sizeof(sibParam.paramValue);
				}
				break;
			case VAR_CHARSTR://字符串值
				//memcpy(data+offset,data1,strlen(data1));
				//offset = offset + strlen(data1);
				memset(data1,0,sizeof(data1));
				len1 = GetVarByteStrLen(serviceID,sibID,10+i,10+messageparam-i);
				if(len1>(sizeof(data)-offset))
					len1 = sizeof(data)-offset;
				//	else
				//		len1 = sizeof(data1);
				if(!GetSibStringValue(serviceID,sibID,data1,len1,sibParam.paramValue,VAR_CHARSTR))
					break;
				
				memcpy(data+offset,data1,len1);
				offset= offset + len1;
				break;
			case VAR_BYTESTR://字节串值
				memset(data1,0,sizeof(data1));
				len1 = GetVarByteStrLen(serviceID,sibID,10+i,10+messageparam-i);
				if(len1>(sizeof(data)-offset))
					len1 = sizeof(data)-offset;
				//	else
				//		len1 = sizeof(data1);
				if(!GetSibStringValue(serviceID,sibID,data1,len1,sibParam.paramValue,VAR_BYTESTR))
					break;
				
				//	memcpy(&llen,data1,sizeof(WORD));
				
				memcpy(data+offset,data1,len1);
				offset= offset + len1;
				break;
			case VAR_VARIABLE://变量
				if(GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,FALSE))
				{
					len1 = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
					if(len1>(sizeof(data)-offset))
						len1 = sizeof(data)-offset;
					//	else
					//		len1 = sizeof(data1);
					if(GetVaribleValue(senssionID,data1,sConstVarible.offset,len1))
					{
						//						if(sConstVarible.type == DATA_BYTESTR)
						//						{
						//							
						//						//	memcpy(&llen,data1,sizeof(WORD));
						//							memcpy(data+offset,data1,(llen+offset)>sizeof(data)?(sizeof(data)-offset):len1);
						//							offset = offset + len1;
						//						}
						//						else if(sConstVarible.type == DATA_CHARSTR)
						//						{
						//							memcpy(data+offset,data1,len1);
						//							offset = offset + len1;
						//						}
						//						else
						//						{
						memcpy(data+offset,data1,len1);
						offset = offset + len1;
						//	}
					}
				}
				break;
			case VAR_CONSTVAR://常量
				
				if(GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,TRUE))
				{
					len1 = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
					if(len1>(sizeof(data)-offset))
						len1 = sizeof(data)-offset;
					//else
					//	len1 = sizeof(data1);
					if(GetConstValue(serviceID,(BYTE *)data1,sConstVarible.offset,len1))
					{
						if(sConstVarible.type == DATA_BYTESTR)
						{
							//memcpy(&llen,data1,sizeof(WORD));
							memcpy(data+offset,data1,(len1+offset)>sizeof(data)?(sizeof(data)-offset):len1);
							offset = offset + ((len1+offset)>sizeof(data)?(sizeof(data)-offset):len1);
						}
						else if(sConstVarible.type == DATA_CHARSTR)
						{
							memcpy(data+offset,data1,(strlen(data)+offset)>sizeof(data)?(sizeof(data)-offset):strlen(data));
							offset = offset + ((strlen(data)+offset)>sizeof(data)?(sizeof(data)-offset):strlen(data));
						}
						else
						{
							memcpy(data+offset,data1,len1);
							offset = offset + len1;
						}
					}
				}
				else
				{
					UpdateNextSib(senssionID,sibID,FALSE);
					return TRUE;
				}
				break;
			default:
				{
					UpdateNextSib(senssionID,sibID,FALSE);
					return TRUE;
				}
				break;
			}	
		}
		nCount=0;
		pMessage.sVaribleSave.bVaribleTotal=0;
		for(i=0;i<10;i++)
		{
			if(GetFunctionVaribleConfig(senssionID,(BYTE)serviceID,(WORD)sibID,(BYTE *)&RecallId,sizeof(RecallId),(BYTE)(30+i)))
			{
				pMessage.sVaribleSave.bVaribleTotal=nCount+1;
				pMessage.sVaribleSave.dVaribleNo[nCount] = RecallId;
				nCount++;
			}
		}
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&RecallId,sizeof(RecallId),30);
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&RecallId,sizeof(RecallId),30);
		//		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&RecallId,sizeof(RecallId),30,0);
	}
	len  = offset;
	
//	pMessage.sVaribleSave.bVaribleTotal = 1;
//	pMessage.sVaribleSave.dVaribleNo[0] = RecallId;
	
	if(GetTid(MID_SLP,&pTid));
	{
		sMsg.head.sender=pTid;
		sMsg.head.Source=pTid;
	}
	if(GetTid(dRecvMod,&pTid))
	{
		sMsg.head.receiver=pTid;
	}
	memcpy(sMsg.data,data,len);
	sMsg.head.len = len;
	sMsg.head.event = evODI_Message;
	sMsg.head.sync = FALSE;
	sMsg.head.ackLen = 0;
	
	SEND_MSG(evThreadMessage,dRecvMod,(void *)&sMsg,(WORD)sizeof(sMsg),senssionID,FALSE,NULL,0);
	
	/*设置超时*/
	event = (WORD) evTimer0;
	if(eventNumber >= TIMER_MAX)
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	if(delayTimer<0)
		delayTimer = TIMER_ODI;
	timerId = SET_TIMER(delayTimer,event,MID_SLP,senssionID,bSync);
	if(timerId == -1)
	{
		pMessage.slavehead.wlastevent=evODI_Message;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
    //	return TRUE;
	return FALSE;
}
/********************************************************************
函数名称：Run_SIB_Timer
函数功能: 设置超时
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_Timer(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	int delayTimer = 0;
	int   eventNumber = 0;
	WORD  event;
	int   timerId;
	BOOL bSync = FALSE;
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibTimer)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.paramTotal > 0)
	{
		
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&delayTimer,sizeof(delayTimer),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&eventNumber,sizeof(eventNumber),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&bSync,sizeof(bSync),2,0);
		
	}
	event = (WORD)evTimer0;
	timerId = SET_TIMER(delayTimer,event,MID_SLP,senssionID,bSync);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evTimer0;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
		
	}
	//	return TRUE;
	return FALSE;
	
}
/********************************************************************
函数名称：GetVarByteStrLen
函数功能: 得到SIB中某个字节串值的长度
参数定义: 无
返回定义: 字节串定义长度
创建时间: 2010-1-6 16:33
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int GetVarByteStrLen(BYTE serviceID,WORD sibID,int begintimes,int endtimes)
{
	SIB_PARAM sibParam;
	int offset = 0;
	int offset1 = 0;
	int i;
	SIB_BASIC sibBasic;
	int len;
	BOOL bFind = FALSE;
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
		return 0;
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(begintimes),FALSE))
	{
		return 0;
	}
	else
	{
		if(sibParam.paramType==VAR_BYTESTR)
		{
			offset = sibParam.paramValue;
		}
	}
	for (i=begintimes+1;i<endtimes;i++)
	{
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(i),FALSE))
		{
			offset=0;
			break;
		}
		else
		{
			if(sibParam.paramType==VAR_BYTESTR)
			{
				offset1 = sibParam.paramValue;
				bFind = TRUE;
				break;
			}
		}
		
	}
	if(bFind)
		len=offset1-offset;
	else
		len=sibBasic.len-offset;
	return len;
}
/********************************************************************
函数名称：Run_SIB_Log
函数功能: 日志SIB
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_Log(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	SIB_PARAM sibParam;
	BOOL bFind = FALSE;
	BYTE serviceID;
	int i;
	char Title[256];
	char content[1024];
	BYTE byte_content[1024];
    Log_Message logMessage;
	TID pTid;
	SIB_PARAM sibParam1;
	XSCM_CONSTVARIABLE sConstVarible;
	BYTE value0;
	WORD value1;
	DWORD value2;
	WORD llen;
	int len,j,len1;
	YF_TRACE_SLP senssionID,"","call log sib begin");
	memset(&logMessage,0,sizeof(Log_Message));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
	{
		return FALSE;
	}
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}	
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibLog)//定义的不是log SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(!GetTid(MID_LOG,&pTid))
	{
		YF_TRACE_SLP senssionID,"","MID_LOG unregister");
		{
			UpdateNextSib(senssionID,sibID,FALSE);
			return TRUE;
		}
	}
	if(sibBasic.paramTotal > 0)
	{
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(sibBasic.paramTotal-1),TRUE))
		{
			YF_TRACE_SLP senssionID,"","GetSibParamAttr");
			{
				UpdateNextSib(senssionID,sibID,FALSE);
				return TRUE;
			}
		}
		{
			for (i=0;i<sibParam.paramNo-9;i++)
			{
				memset(Title,0,sizeof(Title));
				memset(content,0,sizeof(content));
				memset(byte_content,0,sizeof(byte_content));
				GetFunctionVaribleValue(senssionID,serviceID,sibID,Title,sizeof(Title),(BYTE)i,0);
				//				memset(content,0,sizeof(content));
				//				GetFunctionVaribleValue(senssionID,serviceID,sibID,content,sizeof(content),(BYTE)(10+i),0);
				
				//////////////////////////////////////////////////////////////////////////
				if(GetSibParamAttr(serviceID,sibID,&sibParam1,(BYTE)(10+i),FALSE))//取得SIB的第sibId个参数的属性
				{
					switch(sibParam1.paramType)
					{
					case VAR_INT://整数值
						{
							sprintf(content,"%d",sibParam1.paramValue);
						}
						break;
					case VAR_CHARSTR://字符串值
						llen = GetVarByteStrLen(serviceID,sibID,10+i,sibParam.paramNo-9+10-i);
						if(sizeof(content)<llen)
							llen=sizeof(content);
						GetSibStringValue(serviceID,sibID,content,llen,sibParam1.paramValue,VAR_CHARSTR);
						break;
					case VAR_BYTESTR://字节串值
						llen = GetVarByteStrLen(serviceID,sibID,10+i,sibParam.paramNo-9+10-i);
						if(sizeof(byte_content)<llen)
							llen=sizeof(byte_content);
						GetSibStringValue(serviceID,sibID,byte_content,(WORD)llen,sibParam1.paramValue,VAR_BYTESTR);
						
						//	memcpy(&llen,byte_content,sizeof(WORD));
						for(j=0;j<llen;j++)
						{
							len1=strlen(content);
							if(j==(llen-1))
							{
								if(len1+2>=sizeof(content))
									break;
								sprintf(content+len1,"%d",byte_content[j]);
							}
							else
							{
								if(len1+3>=sizeof(content))
									break;
								sprintf(content+len1,"%d,",byte_content[j]);
							}
						}
						//						sprintf(content,"%d",byte_content);
						break;
					case VAR_VARIABLE://变量
						if(GetParamAttr(serviceID,(WORD)sibParam1.paramValue,&sConstVarible,FALSE))
						{
							len = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
							switch(sConstVarible.type)
							{
							case Var_BYTE:
							case Var_BOOL:
								if(GetVaribleValue(senssionID,(BYTE *)&value0,sConstVarible.offset,(WORD)len))
								{
									sprintf(content,"%d",value0);
								}
								break;
							case Var_WORD:
								if(GetVaribleValue(senssionID,(BYTE *)&value1,sConstVarible.offset,(WORD)len))
								{
									sprintf(content,"%d",value1);
								}
								break;
							case Var_DWORD:
								if(GetVaribleValue(senssionID,(BYTE *)&value2,sConstVarible.offset,(WORD)len))
								{
									sprintf(content,"%d",value2);
								}
								break;
							case Var_CHARSTR:
								GetVaribleValue(senssionID,content,sConstVarible.offset,(WORD)len);
								break;
							case Var_BYTESTR:
								if(sizeof(byte_content)<len)
									len=sizeof(byte_content);
								GetVaribleValue(senssionID,byte_content,sConstVarible.offset,(WORD)len);
								//	memcpy(&llen,byte_content,sizeof(WORD));
								for(j=0;j<len;j++)
								{
									len1=strlen(content);
									if(j==(len-1))
									{
										if(len1+2>=sizeof(content))
											break;
										sprintf(content+len1,"%d",byte_content[j]);
									}
									else
									{
										if(len1+3>=sizeof(content))
											break;
										sprintf(content+len1,"%d,",byte_content[j]);
									}
								}
								break;
							default:
								break;
							}
						}
						break;
					case VAR_CONSTVAR://常量
						if(GetParamAttr(serviceID,(WORD)sibParam1.paramValue,&sConstVarible,TRUE))
						{
							len = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
							switch(sConstVarible.type)
							{
							case Var_BYTE:
							case Var_BOOL:
								if(GetConstValue(serviceID,(BYTE *)&value0,sConstVarible.offset,(WORD)len))
								{
									sprintf(content,"%d",value0);
								}
								break;
							case Var_WORD:
								if(GetConstValue(serviceID,(BYTE *)&value1,sConstVarible.offset,(WORD)len))
								{
									sprintf(content,"%d",value1);
								}
								break;
							case Var_DWORD:
								if(GetConstValue(serviceID,(BYTE *)&value2,sConstVarible.offset,(WORD)len))
								{
									sprintf(content,"%d",value2);
								}
								break;
							case Var_CHARSTR:
								GetConstValue(serviceID,content,sConstVarible.offset,(WORD)len);
								break;
							case Var_BYTESTR:
								if(sizeof(byte_content)<len)
									len=sizeof(byte_content);
								GetConstValue(serviceID,byte_content,sConstVarible.offset,(WORD)len);
								//memcpy(&llen,byte_content,sizeof(WORD));
								for(j=0;j<len;j++)
								{
									len1=strlen(content);
									if(j==(len-1))
									{
										if(len1+2>=sizeof(content))
											break;
										sprintf(content+len1,"%d",byte_content[j]);
									}
									else
									{
										if(len1+3>=sizeof(content))
											break;
										sprintf(content+len1,"%d,",byte_content[j]);
									}
								}
								break;
							default:
								break;
							}
						}
						break;
					default:
						break;
					}
				}
				//////////////////////////////////////////////////////////////////////////
				
				
				
				//				YF_LOG_SPP "%s,%s",Title,content);
				logMessage.type = 0;
				logMessage.lines = __LINE__;
				strcpy(logMessage.funname,Title);
				logMessage.warnstep = 0;
				if(strlen(content)>=sizeof(logMessage.content))
					memcpy(logMessage.content,content,sizeof(logMessage.content));
				else
					strcpy(logMessage.content,content);
				//strcpy(logMessage.content,"This is a first test and it fuck  heuoe ghueddj nice day");
				pMessage.messageInfor.head.len = sizeof(Log_Message);
				pMessage.messageInfor.head.event = evLogAsk;
				memcpy(pMessage.messageInfor.data,&logMessage,sizeof(Log_Message));
				SEND_MSG(evThreadMessage,MID_LOG,(void *)&pMessage.messageInfor,(WORD)sizeof(MESSAGE),senssionID,FALSE,NULL,0);
			}
		}
	}
	UpdateNextSib(senssionID,sibID,TRUE);
	
	
	
	
	
	YF_TRACE_SLP senssionID,"","call log sib end");
	//	RunSib(senssionID);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_CDR
函数功能: 话单SIB
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-8-15 17:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_CDR(WORD senssionID,WORD sibID)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	SIB_PARAM sibParam;
	BOOL bFind = FALSE;
	BYTE serviceID;
	char CallingNum[32];
	char CalledNum[32];
	char beginTime[30];
	char endTime[30];
	BYTE bType;//通话类别
	char UserNum[20];
	char ref0[40];
	char ref1[40];
	char ref2[40];
	char ref3[40];
	struct tm when1,when2;
	time_t ltime,ltime1,ltime2;
	
	
	BYTE data[CHAR_NUMBER];
	int offset=0;
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return TRUE;
	}
	if(sibBasic.sibType !=tsibCdr)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return TRUE;
	}
	if(sibBasic.paramTotal > 0)
	{
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)(sibBasic.paramTotal-1),TRUE))
		{
			UpdateNextSib(senssionID,sibID,FALSE);
			return TRUE;
		}
		memset(CallingNum,0,sizeof(CallingNum));
		memset(CalledNum,0,sizeof(CalledNum));
		memset(beginTime,0,sizeof(beginTime));
		memset(endTime,0,sizeof(endTime));
		memset(UserNum,0,sizeof(UserNum));
		memset(ref0,0,sizeof(ref0));
		memset(ref1,0,sizeof(ref1));
		memset(ref2,0,sizeof(ref2));
		memset(ref2,0,sizeof(ref2));
		bType = 0;
		GetFunctionVaribleValue(senssionID,serviceID,sibID,CallingNum,sizeof(CallingNum),0,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,CalledNum,sizeof(CalledNum),1,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,beginTime,sizeof(beginTime),2,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,endTime,sizeof(endTime),3,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,&bType,sizeof(bType),4,0);
		GetFunctionVaribleValue(senssionID,serviceID,sibID,UserNum,sizeof(UserNum),5,0);
		if(sibParam.paramNo>=6)
		{
			GetFunctionVaribleValue(senssionID,serviceID,sibID,ref0,sizeof(ref0),6,0);
			if(sibParam.paramNo>=7)
			{
				GetFunctionVaribleValue(senssionID,serviceID,sibID,ref1,sizeof(ref1),7,0);
				if(sibParam.paramNo>=8)
				{
					GetFunctionVaribleValue(senssionID,serviceID,sibID,ref2,sizeof(ref2),8,0);	
				}
				if(sibParam.paramNo>=9)
				{
					GetFunctionVaribleValue(senssionID,serviceID,sibID,ref3,sizeof(ref3),9,0);
				}
			}
		}
	}
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memset(data,0,sizeof(data));
	memcpy(data,CallingNum,sizeof(CallingNum));
	offset=sizeof(CallingNum);
	memcpy(data+offset,CalledNum,sizeof(CalledNum));
	offset=offset+sizeof(CalledNum);
	memset(&when1,0,sizeof(when1));
	memset(&when2,0,sizeof(when2));
	memcpy(data+offset,beginTime,sizeof(beginTime));
	offset=offset+sizeof(beginTime);
	memcpy(data+offset,endTime,sizeof(endTime));
	offset=offset+sizeof(endTime);
	if(!ConvertTime(beginTime,&when1))
	{
		ltime1 = 0;
	}
	else
		ltime1=mktime(&when1);
	if(!ConvertTime(endTime,&when2))
	{
		ltime2 = 0;
	}
	else
		ltime2=mktime(&when2);
	ltime = ltime2-ltime1;
	if(ltime>=0)
		memcpy(data+offset,&ltime,sizeof(DWORD));
	//////////////////////////////////////////////////////////////////////////
	offset = offset + 4;//时长
	//	memcpy(data+offset,&bType,sizeof(bType));
	//	offset=offset+sizeof(bType);
	memcpy(data+offset,UserNum,sizeof(UserNum));
	offset=offset+sizeof(UserNum);
	memcpy(data+offset,&pServiceKey[serviceID].serviceKey,sizeof(WORD));
	offset=offset+sizeof(WORD);
	offset = offset + 2;//费用
	offset = offset + 2;//流量
	memcpy(data+offset,ref0,sizeof(ref0));
	offset=offset+sizeof(ref0);
	memcpy(data+offset,ref1,sizeof(ref1));
	offset=offset+sizeof(ref1);
	memcpy(data+offset,ref2,sizeof(ref2));
	offset=offset+sizeof(ref2);
	memcpy(data+offset,ref3,sizeof(ref3));
	offset=offset+sizeof(ref3);
	memcpy(pMessage.messageInfor.data,data,offset);
	pMessage.messageInfor.head.len=offset;
	pMessage.messageInfor.head.event = evASK_CDR;
	SEND_MSG(evThreadMessage,MID_CDR,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	//	YF_LOG_SPP senssionID,CallingNum,CalledNum,beginTime,endTime,bType,UserNum,"%s,%s,%s,%s",ref0,ref1,ref2,ref3);
	UpdateNextSib(senssionID,sibID,TRUE);
	//	RunSib(senssionID);
	return TRUE;
}


/********************************************************************
函数名称：Run_SIB_InitDAP
函数功能: 会话开始SIB
参数定义: senssionID：会话号,sibID：SIB编号
返回定义: 无
创建时间: 2008-11-20 10:35
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL Run_SIB_InitDAP(WORD senssionID,WORD sibID)
{
	
	stPXSSM_InitiateDP dp;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(sibBasic.sibType !=tsibInitDAP)//定义的不是InitDAP SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}	
	
	memcpy(&dp,&pMessage.slavehead.serInit.sInit.init_pstn,sizeof(stPXSSM_InitiateDP));
	if(sibBasic.paramTotal > 0)
	{
		//		GetFunctionVaribleValue(senssionID,serviceID,sibID,(void *)&serviceKey1,sizeof(serviceKey1),0,0);
		////		if(serviceKey1 != serviceKey)
		//			return;
		if(GetSibParamAttr(serviceID,sibID,&sibParam,1,FALSE))
		{
			if(sibParam.paramType==Param_VARIABLE)
			{
				if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				{
					UpdateNextSib(senssionID,sibID,FALSE);
					return TRUE;
				}
				SetVaribleValue(senssionID,dp.callingNumber,sVariable.offset,(WORD)strlen(dp.callingNumber));	
			}
		}
		if(GetSibParamAttr(serviceID,sibID,&sibParam,2,FALSE))
		{
			if(sibParam.paramType==Param_VARIABLE)
			{
				if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				{
					UpdateNextSib(senssionID,sibID,FALSE);
					return TRUE;
				}
				SetVaribleValue(senssionID,dp.calledNumber,sVariable.offset,(WORD)strlen(dp.calledNumber));	
			}
		}
		
		
	}
	UpdateNextSib(senssionID,sibID,TRUE);
	//	RunSib(senssionID);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_InitOdiSib
函数功能: ODI接入初始化
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE；
创建时间: 2010-1-8 11:35
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_InitOdiSib(WORD senssionID,WORD sibID)
{
	stODI_InitODI dp;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	int i;
	int offset;
	WORD len;
	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(sibBasic.sibType !=tsibInitOdiSib)//定义的不是InitODI SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}	
	
	memcpy(&dp,&pMessage.slavehead.serInit.sInit.init_odi,sizeof(stODI_InitODI));
	offset=0;
	for(i=0;i<sibBasic.paramTotal;i++)
	{
		if(GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)i,FALSE))
		{
			if(sibParam.paramType==Param_VARIABLE)
			{
				if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				{
					UpdateNextSib(senssionID,sibID,FALSE);
					return TRUE;
				}
				len = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
				if((offset+len)>=sizeof(dp.content))
					len=sizeof(dp.content)-offset-1;
				SetVaribleValue(senssionID,dp.content+offset,sVariable.offset,len);	
				offset=offset+len;
				if(offset>=sizeof(dp.content))
					break;
			}
		}
	}
	
	UpdateNextSib(senssionID,sibID,TRUE);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_TcpLinkSib
函数功能: tcpLINK
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-8 11:41
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_TcpLinkSib(WORD senssionID,WORD sibID)
{
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_InitTcpSib
函数功能: TCP启动业务
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-8 11:42
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_InitTcpSib(WORD senssionID,WORD sibID)
{
	stTCP_InitTcp dp;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sVariable;
	int i;
	int offset;
	WORD len;
	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	memset(&sibBasic,0,sizeof(SIB_BASIC));
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(sibBasic.sibType !=tsibInitTcpSib)//定义的不是InitODI SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}	
	
	memcpy(&dp,&pMessage.slavehead.serInit.sInit.init_tcp,sizeof(stTCP_InitTcp));
	offset=0;
	for(i=0;i<sibBasic.paramTotal;i++)
	{
		if(GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)i,FALSE))
		{
			if(sibParam.paramType==Param_VARIABLE)
			{
				if(!GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sVariable,FALSE))
				{
					UpdateNextSib(senssionID,sibID,FALSE);
					return TRUE;
				}
				len = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
				if(i==0)//业务键
				{
					if(len>sizeof(dp.serviceKey))
						len=sizeof(dp.serviceKey);
					SetVaribleValue(senssionID,(BYTE *)&dp.serviceKey,sVariable.offset,len);	
				}
				else if(i==1)//IP地址
				{
					if(len>sizeof(dp.IP))
						len=sizeof(dp.IP);
					SetVaribleValue(senssionID,dp.IP,sVariable.offset,len);	
				}
				else if(i==2)//MAC地址
				{
					if(len>sizeof(dp.MAC))
						len=sizeof(dp.MAC);
					SetVaribleValue(senssionID,dp.MAC,sVariable.offset,len);	
				}
				else
					break;
			}
		}
	}
	
	g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID=pMessage.messageInfor.head.Source.mId;
	UpdateNextSib(senssionID,sibID,TRUE);
	return TRUE;
}
/********************************************************************
函数名称：Run_SIB_TcpRecvDataSib
函数功能: TCP发送接收数据SIB
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-8 11:56
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_TcpRecvDataSib(WORD senssionID,WORD sibID)
{
	BOOL bSuc=FALSE;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	stTCP_RecvData_Ask sAsk;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	memset(&sAsk,0,sizeof(stTCP_RecvData_Ask));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibTcpRecvDataSib)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evTcp_ReceiveData;
	
	if(sibBasic.paramTotal > 0)
	{
		
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&sAsk.timeOut,sizeof(sAsk.timeOut),0,0))
			sAsk.timeOut=50;
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&sAsk.datalen,sizeof(sAsk.datalen),1,0))
			sAsk.datalen=0;	
		/************************************************************************/
		/*        返回FSK结果存储变量                                           */
		/************************************************************************/
		memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
		
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),2);
		pMessage.sVaribleSave.bVaribleTotal = 1;
		pMessage.sVaribleSave.dVaribleNo[0] = opsition;
		opsition=-1;
		GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),3);
		pMessage.sVaribleSave.bVaribleTotal = 2;
		pMessage.sVaribleSave.dVaribleNo[1] = opsition;
		
	}
	pMessage.messageInfor.head.len=sizeof(stTCP_RecvData_Ask);
	
	
	if(!GetTid(	g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&sAsk,sizeof(stTCP_RecvData_Ask));
	
	
	bSuc =SEND_MSG(evIPCMessage,g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	delayTimer = sAsk.timeOut*10;
	if((delayTimer>TIMER_TCP)||(delayTimer<=0))
		delayTimer = TIMER_TCP;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evTcp_ReceiveData;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	return FALSE;
}
/********************************************************************
函数名称：Run_SIB_DataToFixStructSib
函数功能: 数据固定结构解析
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2010-1-14 11:01
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Run_SIB_DataToFixStructSib(WORD senssionID,WORD sibID)
{
	BOOL bSuc=FALSE;
	MESSAGE_XSCM_SLAVE  pMessage;
	XSCM_CONSTVARIABLE sConstVarible;
	SIB_BASIC sibBasic;
	SIB_PARAM sibParam;
	BOOL bFind = FALSE;
	BYTE serviceID;
	WORD datalen=0;
	int des_len;
	int len,i;
	BYTE data[2048];
	BYTE data1[2048];
	int offset=0;
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibDataToFixStructSib)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.paramTotal <= 0)
		return TRUE;
	memset(&sibParam,0,sizeof(sibParam));
	memset(data,0,sizeof(data));
	if(!GetSibParamAttr(serviceID,sibID,&sibParam,0,FALSE))//取得SIB的第sibId个参数的属性
		return TRUE;
	
	if(sibParam.paramType==VAR_CONSTVAR)
	{
		GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,TRUE);
	}
	else
	{
		GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,FALSE);
	}
	des_len=GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
	if(des_len<sizeof(data))
	{
		len=des_len;
	}
	else
	{
		len=sizeof(data);
	}
	if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,data,(WORD)len,0,0))
		return FALSE;
	for(i=1;i<sibBasic.paramTotal;i++)
	{
		memset(&sibParam,0,sizeof(sibParam));
		memset(data1,0,sizeof(data1));
		if(!GetSibParamAttr(serviceID,sibID,&sibParam,(BYTE)i,FALSE))//取得SIB的第sibId个参数的属性
			return TRUE;
		
		if(sibParam.paramType==VAR_CONSTVAR)
		{
			GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,TRUE);
		}
		else
		{
			GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,FALSE);
		}
		len=GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
		if(des_len<=(len+offset))
		{
			UpdateXSCMMessage(senssionID,&pMessage,FALSE);
			UpdateNextSib(senssionID,sibID,FALSE);
			return TRUE;
		}
		memcpy(data1,data+offset,len);
		offset=offset+len;
		SetVaribleValue(senssionID,data1,sConstVarible.offset,(WORD)len);
	}
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	UpdateNextSib(senssionID,sibID,TRUE);
	return TRUE;
	
}
BOOL Run_SIB_TcpSendDataRecvDataSib(WORD senssionID,WORD sibID)
{
	BOOL bSuc=FALSE;
	stTCP_SendDataRecvData pSendAndRecvData;
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	TID pTid ;
	BYTE serviceID;
	int opsition = -1;
	DWORD delayTimer = 0; //定时器
	int   timerId = -1;
	WORD datalen=0;
	BYTE data[1024];
	memset(&pSendAndRecvData,0,sizeof(stTCP_SendDataRecvData));
	memset(&sibBasic,0,sizeof(SIB_BASIC));	
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	if(serviceID >= ServiceKey_Total)// 业务编号大于业务个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibID >= GetServiceSibCount(serviceID))//读取的SIB大于实际定义的SIB个数
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	if(sibBasic.sibType !=tsibTcpSendDataRecvDataSib)//定义的不是FUN SIB
	{
		EndSenssion(senssionID,FatalFail);
		return FALSE;
	}
	pMessage.messageInfor.head.event=evTcp_SendDataAndReceiveData;
	
	pSendAndRecvData.sid=senssionID;
	memset(pSendAndRecvData.data,0,sizeof(pSendAndRecvData.data));
	
	
	if(sibBasic.paramTotal > 0)
	{
		pSendAndRecvData.timeOut=0;
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendAndRecvData.timeOut,sizeof(pSendAndRecvData.timeOut),0,0);
		if(!GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendAndRecvData.senddatalen,sizeof(pSendAndRecvData.senddatalen),1,0))
			datalen=sizeof(pSendAndRecvData.data);
		else
		{
			if(pSendAndRecvData.senddatalen>sizeof(pSendAndRecvData.data))
			{
				datalen=sizeof(pSendAndRecvData.data);
				pSendAndRecvData.senddatalen=datalen;
			}
			else
			{
				datalen=pSendAndRecvData.senddatalen;
			}
			
		}
		memset(data,0,sizeof(data));
		GetFunctionVaribleValue(senssionID,serviceID,sibID,data,sizeof(data),2,0);
		if(sizeof(data)>datalen)
		{
			memcpy(pSendAndRecvData.data,data,datalen);
		}
		else
		{
			memcpy(pSendAndRecvData.data,data,sizeof(data));
			pSendAndRecvData.senddatalen = sizeof(data); 
		}
		GetFunctionVaribleValue(senssionID,serviceID,sibID,(BYTE *)&pSendAndRecvData.recvdatalen,sizeof(pSendAndRecvData.recvdatalen),3,0);
		/***********************************************************************
	          返回TCP结果存储变量                                          
		/************************************************************************/
		memset(pMessage.sVaribleSave.dVaribleNo,0,sizeof(pMessage.sVaribleSave.dVaribleNo));
		if(GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),4))
		{			
			pMessage.sVaribleSave.bVaribleTotal = 1;
			pMessage.sVaribleSave.dVaribleNo[0] = opsition;
		}
		if(GetFunctionVaribleConfig(senssionID,serviceID,sibID,(BYTE *)&opsition,sizeof(opsition),5))
		{
			pMessage.sVaribleSave.bVaribleTotal = 2;
			pMessage.sVaribleSave.dVaribleNo[1] = opsition;
		}	
	}
	pMessage.messageInfor.head.len=sizeof(stTCP_SendDataRecvData);
	
	if(!GetTid(	g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&pSendAndRecvData,sizeof(stTCP_SendDataRecvData));
	
	bSuc =SEND_MSG(evIPCMessage,	g_MESSAGE_XSCM_SLAVE[senssionID].AcceseMID,(void *)&pMessage.messageInfor,sizeof(MESSAGE),senssionID,FALSE,NULL,0);
	delayTimer = pSendAndRecvData.timeOut*10;
	if((delayTimer>TIMER_TCP)||(delayTimer<=0))
		delayTimer = TIMER_TCP;
	timerId = SET_TIMER(delayTimer,evTimer0,MID_SLP,senssionID,FALSE);
	if(timerId == -1)
	{
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,FALSE);
		return TRUE;
	}
	else
	{
		pMessage.slavehead.wlastevent=evTcp_SendDataAndReceiveData;
		pMessage.slavehead.timerevent[0].timerId = timerId;
		pMessage.slavehead.timerevent[0].bUsed = TRUE;
		UpdateXSCMMessage(senssionID,&pMessage,FALSE);
		UpdateNextSib(senssionID,sibID,TRUE);
	}
	return FALSE;
}
/********************************************************************
函数名称：GetServiceKeyIdAndKey
函数功能: 查找业务键对应下标
参数定义: CalledNum：拨打号码，CallingNum:本机号码
返回定义: 成功返回业务键对应的模块号,失败返回-1
创建时间: 2008-8-19  18:41
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetServiceKeyIdAndKey(char CalledNum[32],char CallingNum[32],BYTE *serviceId,int *serviceKey)
{
	int i;
	
	if((pServiceKey==NULL)||(serviceId == NULL)||(serviceKey == NULL))
		return FALSE;
	for(i=0;i<ServiceKey_Total;i++)
	{
		if(pServiceKey[i].SkeyType!=0)
			continue;
		switch(pServiceKey[i].sServiceKey.sPstn.initialtype)
		{
		case 1://主叫号码识别业务
			{
				if(strcmp(CallingNum,pServiceKey[i].sServiceKey.sPstn.initialNum)==0)
				{
					serviceKey[0] = pServiceKey[i].serviceKey;
					serviceId[0] = i;
					return TRUE;
				}
			}
			break;
		case 2://被叫号码识别业务
			{
				if(strcmp(CalledNum,pServiceKey[i].sServiceKey.sPstn.initialNum)==0)
				{
					serviceKey[0] = pServiceKey[i].serviceKey;
					serviceId[0] = i;
					return TRUE;
				}
			}
			break;
		default:
			break;
		}
	}
	return FALSE;
}
int GetServiceKeyIdByKeyAndType(int serviceKey,int type)
{
	int i;
	for(i=0;i<ServiceKey_Total;i++) {
		if((pServiceKey[i].serviceKey == serviceKey)&&(pServiceKey[i].SkeyType==type))
		{
			return i;
			break;
		}
		
	}
	return -1;
}
/********************************************************************
函数名称：GetServiceKeyIdByKey
函数功能: 根据业务健获取业务ID（下标）
参数定义: serviceKey:业务健
返回定义: 成功返回业务ID，失败返回-1
创建时间: 2008-12-12 15:13
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int GetServiceKeyIdByKey(int serviceKey)
{
	int i;
	for(i=0;i<ServiceKey_Total;i++) {
		if(pServiceKey[i].serviceKey == serviceKey)
		{
			return i;
			break;
		}
		
	}
	return -1;
}
/********************************************************************
函数名称：XVAPL_Push
函数功能: SIB编号堆栈入栈，将下一步执行的SIB编号入栈
参数定义: senssionID：会话号，sibNo：SIB编号
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-2-13 14:59
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL XVAPL_Push(WORD senssionID,WORD sibNo)
{
	int top;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	top=(int)g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.top;
	if(top!=0xffffffff)
	{
		if(top>=(STACK_SIZE-1))//堆栈满
			return FALSE;
	}
	if(top<0)//堆栈为空
		top = 0;
	else
		top = top +1;
	g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.sibNo[top] = sibNo;
	g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.top = top;
	return TRUE;
}
/********************************************************************
函数名称：XVAPL_Pop
函数功能: SIB编号堆栈出栈取SIB执行编号
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2009-2-13 14:56
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL XVAPL_Pop(WORD senssionID,WORD *sibNo)
{
	int top;
	if((senssionID>=MESSAGE_MAX)||(sibNo==NULL))
		return FALSE;
	top=g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.top;
	if((top<0)||(top>=STACK_SIZE))//堆栈无内容或者已经越解
		return FALSE;
	
	*(sibNo+0)=g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.sibNo[top];
	g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.top = g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.top -1;
	return TRUE;
}
/********************************************************************
函数名称：UpdateNextSib
函数功能: 更新备份里的SIB位置
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-9-4 17:25
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateNextSib(WORD senssionID,WORD sibID,BOOL next)
{
	MESSAGE_XSCM_SLAVE  pMessage;
	SIB_BASIC sibBasic;
	BOOL bFind = FALSE;
	BYTE serviceID;
	if(senssionID >= MESSAGE_MAX) //会话ID>最大ID
		return FALSE;
	bFind=ReadXSCMMessage(senssionID,&pMessage);	//读备份消息
	if(!bFind)
		return FALSE;
	//	if(pMessage.messageInfor.head.Source.node== 0)
	//		return FALSE;
	serviceID = pMessage.slavehead.serviceId; //得到业务编号
	
	if(!GetSibBasicAttr(serviceID,sibID,&sibBasic))//取得SIB的基本属性
		return FALSE;
	pMessage.slavehead.lastsibNo=sibID;
	if(next)
		pMessage.slavehead.sibNo = sibBasic.nextstep;
	else
		pMessage.slavehead.sibNo = sibBasic.Errhandle;
	
	UpdateXSCMMessage(senssionID,&pMessage,FALSE);
	XVAPL_Push(senssionID,pMessage.slavehead.sibNo);//把下步执行写入堆栈当中
	return TRUE;
}
/********************************************************************
函数名称：GetServiceSibCount
函数功能: 得到本业务SIB的个数
参数定义: serviceID:业务编号
返回定义: 成功返回SIB个数，失败返回0
创建时间: 2008-8-21 18:03
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
WORD GetServiceSibCount(BYTE serviceID)
{
	if(pXscmSlp == NULL)
		return 0;
	if(serviceID >= ServiceKey_Total)
		return 0;
	return (pXscmSlp+serviceID)->sBasic.nTotalSib;
	
}
/********************************************************************
函数名称：GetSibBasicAttr
函数功能: 得到SIB的基本属性
参数定义: 
serviceID：业务键编号，从0开始
sibID:SIB编号
pSibBasic:存放SIB的基本属性的结构
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-22 14:33
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetSibBasicAttr(BYTE serviceID,WORD sibId,SIB_BASIC *pSibBasic)
{
	if(pXscmSlp == NULL)
		return FALSE;
	if(pSibBasic == NULL)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if((pXscmSlp+serviceID)->sBasic.nTotalSib <=sibId)
		return FALSE;
	if((pXscmSlp+serviceID)->pSib == NULL)
		return FALSE;
	memcpy(pSibBasic,&((pXscmSlp+serviceID)->pSib+sibId)->sibBasic,sizeof(SIB_BASIC));
	return TRUE;
}
/********************************************************************
函数名称：GetSibParamAttr
函数功能: 得到SIB的某一个参数的基本属性
参数定义: 
serviceID：业务键编号，从0开始
sibID:SIB编号
pSibParam:存放SIB的某一参数的结构
offsetId:参数编号
bBySibId:
TRUE表示按下标得到参数的基本属性，offsetId表示下标;
FALSE表示按参数编号得到参数的基本属性，offsetId表示参数编号
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-21 18:23
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetSibParamAttr(BYTE serviceID,WORD sibId,SIB_PARAM *pSibParam,BYTE offsetId,BOOL bBySibId)
{
	int i;
	if(pXscmSlp == NULL)
		return FALSE;
	if(pSibParam == NULL)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if((pXscmSlp+serviceID)->sBasic.nTotalSib <=sibId)
		return FALSE;
	if((pXscmSlp+serviceID)->pSib == NULL)
		return FALSE;
	if(((pXscmSlp+serviceID)->pSib+sibId)->pParam == NULL)
		return FALSE;
	if(!bBySibId)
	{
		
		
		for(i=0;i<((pXscmSlp+serviceID)->pSib+sibId)->sibBasic.paramTotal;i++)
		{
			if((((pXscmSlp+serviceID)->pSib+sibId)->pParam+i)->paramNo==offsetId)//找到匹配的参数ID号 
			{
				memcpy(pSibParam,((pXscmSlp+serviceID)->pSib+sibId)->pParam+i,sizeof(SIB_PARAM));
				return TRUE;
			}
		}
	}
	else
	{
		if(((pXscmSlp+serviceID)->pSib+sibId)->sibBasic.paramTotal <= offsetId)
			return FALSE;
		memcpy(pSibParam,((pXscmSlp+serviceID)->pSib+sibId)->pParam+offsetId,sizeof(SIB_PARAM));
		return TRUE;
		
	}
	return FALSE;
	
}

/********************************************************************
函数名称：GetSibStringValue
函数功能: 得到SIB的串值
参数定义: 
serviceID：业务键编号，从0开始
sibId:SIB编号
value:存放串值
len:读取串值长度
postion：读取串值的开始位置
bStringType:串值类型，bSTRString表示字符串，bBYTEString表示字节串
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-21 18:54
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetSibStringValue(BYTE serviceID,WORD sibId,void *value,WORD len, DWORD position,BYTE bStringType)
{
	char data[2048];
	if(pXscmSlp == NULL)
		return FALSE;
	if(value == NULL)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if((pXscmSlp+serviceID)->sBasic.nTotalSib <=sibId)
		return FALSE;
	if((pXscmSlp+serviceID)->pSib == NULL)
		return FALSE;
	if(((pXscmSlp+serviceID)->pSib+sibId)->sibBasic.len <= position)
		return FALSE;
	if(((pXscmSlp+serviceID)->pSib+sibId)->pvalue == NULL)
		return FALSE;
	if((position + len)>=((pXscmSlp+serviceID)->pSib+sibId)->sibBasic.len)
		len=(WORD)(((pXscmSlp+serviceID)->pSib+sibId)->sibBasic.len-position);
	switch(bStringType)
	{
	case STRSTRING://字符串
		{
			memset(data,0,sizeof(data));
			strcpy(data,((pXscmSlp+serviceID)->pSib+sibId)->pvalue+position);
			if(strlen(data)<=len)
			{
				memcpy(value,data,strlen(data));
			}
			else
			{
				memcpy(value,data,len);
			}
		}
		break;
	case BYTESTRING://字节串
		//	if(((pXscmSlp+serviceID)->pSib+sibId)->pvalue[position]>=len)
		//	{
		memcpy(value,((pXscmSlp+serviceID)->pSib+sibId)->pvalue+(position),len);
		//	}
		//	else
		//	{
		//		memcpy(value,((pXscmSlp+serviceID)->pSib+sibId)->pvalue+(position),((pXscmSlp+serviceID)->pSib+sibId)->pvalue[position]+1);
		//}
		break;
	default :
		return FALSE;
		break;
	}
	return TRUE;
}
/********************************************************************
函数名称：GetParamAttr
函数功能: 或者变量或者常量的配置属性
参数定义: 
serviceID：业务键编号，从0开始
offsetId:常、变量编号
pVariable:存放常、变量属性
bConst:TRUE常量，FALSE为变量
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-20 17:41
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetParamAttr(BYTE serviceID,WORD offsetId,XSCM_CONSTVARIABLE *pVariable,BOOL bConst)
{
	if(pVariable == NULL)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if(bConst)//找常量属性
	{
		if(offsetId >=(pXscmSlp+serviceID)->sBasic.nConstVariableTotal)
			return FALSE;
		if((pXscmSlp+serviceID)->pConstAtte == NULL)
			return FALSE;
		memcpy(pVariable,(pXscmSlp+serviceID)->pConstAtte+offsetId,sizeof(XSCM_CONSTVARIABLE));
	}
	else//变量属性
	{
		if(offsetId >=(pXscmSlp+serviceID)->sBasic.nVariableTotal)
			return FALSE;
		if((pXscmSlp+serviceID)->pVariableAttr == NULL)
			return FALSE;
		memcpy(pVariable,(pXscmSlp+serviceID)->pVariableAttr+offsetId,sizeof(XSCM_VARIABLE));
	}
	return TRUE;
}
/********************************************************************
函数名称：GetConstValue
函数功能:  获取常量的值
参数定义: 
serviceID：业务键编号，从0开始
value:值存放的位置
position:开始读取的位置
offset:读取的个数
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-20 10:38
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL GetConstValue(BYTE serviceID,BYTE *value,DWORD position,WORD offset)
{
	if(value == NULL)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if((position>=(pXscmSlp+serviceID)->sBasic.nConstVariableLen)||((position+offset)>(pXscmSlp+serviceID)->sBasic.nConstVariableLen))
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	memcpy(value,(pXscmSlp+serviceID)->pConstVaribles+position,offset);
	return TRUE;
}
/********************************************************************
函数名称：SetConstValue
函数功能: 设置常量的值
参数定义:
serviceID：业务键编号，从0开始
value:设置的值
position:开始读取的位置
offset:读取的个数
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-20 16:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL SetConstValue(BYTE serviceID,BYTE *value,DWORD position,WORD offset)
{
	if(value == NULL)
		return FALSE;
	if(serviceID>=ServiceKey_Total)
		return FALSE;
	if((position>=(pXscmSlp+serviceID)->sBasic.nConstVariableLen)||((position+offset)>(pXscmSlp+serviceID)->sBasic.nConstVariableLen))
		return FALSE;
	if((pXscmSlp+serviceID)->pConstVaribles == NULL)
		return FALSE;
	if(value == NULL)
		return FALSE;
	memcpy((pXscmSlp+serviceID)->pConstVaribles+position,value,offset);
	return TRUE;
}
/********************************************************************
函数名称：GetVaribleValue
函数功能: 取得变量的值
参数定义: 
senssionID：会话号
value:值存放的位置
position:开始读取的位置
offset:读取的个数
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-20 14:25
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL GetVaribleValue(WORD senssionID,BYTE *value,DWORD position,WORD offset)
{
	if(value == NULL)
		return FALSE;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if((position >= g_MESSAGE_XSCM_SLAVE[senssionID].variblelen)
		||((position+offset) > g_MESSAGE_XSCM_SLAVE[senssionID].variblelen))
		return FALSE;
	
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue == NULL)
		return FALSE;
	memcpy(value,g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue+position,offset);
	return TRUE;
}
/********************************************************************
函数名称：SetVaribleValue
函数功能: 设置变量的值
参数定义: senssionID：会话号
value:值存放的位置
position:开始读取的位置
offset:读取的个数
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2008-8-20 15:36
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL SetVaribleValue(WORD senssionID,BYTE *value,DWORD position,WORD offset)
{
	if(value == NULL)
		return FALSE;
	if(senssionID>=MESSAGE_MAX)
		return FALSE;
	if((position >= g_MESSAGE_XSCM_SLAVE[senssionID].variblelen)
		||((position+offset) > g_MESSAGE_XSCM_SLAVE[senssionID].variblelen))
		return FALSE;
	
	if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue == NULL)
		return FALSE;
	memcpy(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue+position,value,offset);
	return TRUE;
}
/********************************************************************
函数名称：GetFunctionVaribleValue
函数功能: 得到函数变量的地址
参数定义: 
serviceID:业务编号
sibId:SIB编号
value:存取值
len : 存取值的长度
bNo:第几个参数
返回定义: 成功返回TRUE,失败 返回FALSE
创建时间: 2009-1-19 11:13
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL GetFunctionVaribleConfig(WORD senssionID,BYTE serviceID,WORD sibId,BYTE *value,WORD len,BYTE bNo)
{
	SIB_PARAM sibParam;
	if(value == NULL)
		return FALSE;
	if(GetSibParamAttr(serviceID,sibId,&sibParam,bNo,FALSE))//取得SIB的第sibId个参数的属性
	{
		
		if(sizeof(sibParam.paramValue)<=len)
		{
			memcpy(value,&sibParam.paramValue,sizeof(sibParam.paramValue));
		}
		else
		{
			memcpy(value,&sibParam.paramValue,len);
		}
		
	}
	else
	{
		return FALSE;
	}
	return TRUE;
}
/********************************************************************
函数名称：GetFunctionVaribleValue
函数功能: 得到函数变量的值
参数定义: 
serviceID:业务编号
sibId:SIB编号
value:存取值
len : 存取值的长度
bNo:第几个参数
返回定义: 无
创建时间: 2008-8-22 16:28
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL GetFunctionVaribleValue(WORD senssionID,BYTE serviceID,WORD sibId,BYTE *value,WORD len,BYTE bNo,DWORD offset)
{
	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sConstVarible;
	int len1;
	BYTE data[1024];
	int i;
	//	WORD llen;
	if(value == NULL)
		return FALSE;
	if(GetSibParamAttr(serviceID,sibId,&sibParam,bNo,FALSE))//取得SIB的第sibId个参数的属性
	{
		switch(sibParam.paramType)
		{
		case VAR_INT://整数值
			{
				//				value[0]=(void *)sibParam.paramValue;
				if(sizeof(sibParam.paramValue)<=len)
				{
					memcpy(value,&sibParam.paramValue,sizeof(sibParam.paramValue));
				}
				else
				{
					memcpy(value,&sibParam.paramValue,len);
				}
			}
			break;
		case VAR_CHARSTR://字符串值
			len1 = GetVarByteStrLen(serviceID,sibId,bNo,bNo+10);
			if(len>len1)
				len = len1;
			if(!GetSibStringValue(serviceID,sibId,value,len,sibParam.paramValue,VAR_CHARSTR))
				return FALSE;
			break;
		case VAR_BYTESTR://字节串值
			memset(data,0,sizeof(data));
			len1 = GetVarByteStrLen(serviceID,sibId,bNo,bNo+10);
			if(len>len1)
				len = len1;
			if(!GetSibStringValue(serviceID,sibId,data,len,sibParam.paramValue,VAR_BYTESTR))
				return FALSE;
			
			for(i=0;i<len;i++)
				value[i]=data[i];
			break;
		case VAR_VARIABLE://变量
			if(GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,FALSE))
			{
				len1 = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
				if(len1<len)
					len = len1;
				if(!GetVaribleValue(senssionID,value,sConstVarible.offset+offset,len))
					return FALSE;
			}
			else
			{
				return FALSE;
			}
			break;
		case VAR_CONSTVAR://常量
			if(GetParamAttr(serviceID,(WORD)sibParam.paramValue,&sConstVarible,TRUE))
			{
				len1 = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
				if(len1<len)
					len = len1;
				if(!GetConstValue(serviceID,value,sConstVarible.offset+offset,len))
					return FALSE;
			}
			else
				return FALSE;
			break;
		default:
			return FALSE;
			break;
		}
	}
	else
	{
		return FALSE;
	}
	return TRUE;
}




/********************************************************************
函数名称：ReadXSCMMessage
函数功能: 读取备份消息
参数定义: SenssionID:会话号，pMsg:存取消息的空间
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-5-6 16:07
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL ReadXSCMMessage(WORD SenssionID,MESSAGE_XSCM_SLAVE *pMsg)
{
	if((g_MESSAGE_XSCM_SLAVE == NULL)||(SenssionID>=MESSAGE_MAX))
	{
		return FALSE;
	}
	if(pMsg == NULL)
		return FALSE;
	memcpy(&pMsg->messageInfor,&g_MESSAGE_XSCM_SLAVE[SenssionID].messageInfor,sizeof(MESSAGE));
	memcpy(&pMsg->sVaribleSave,&g_MESSAGE_XSCM_SLAVE[SenssionID].sVaribleSave,sizeof(VARIBLE_SAVE));
	memcpy(&pMsg->slavehead,&g_MESSAGE_XSCM_SLAVE[SenssionID].slavehead,sizeof(MSG_HEAD_SLAVE));
	memcpy(&pMsg->sTestinit,&g_MESSAGE_XSCM_SLAVE[SenssionID].sTestinit,sizeof(TEST_INIT));
	pMsg->variblelen = g_MESSAGE_XSCM_SLAVE[SenssionID].variblelen ;
	pMsg->pVarileValue = g_MESSAGE_XSCM_SLAVE[SenssionID].pVarileValue;
	
	return TRUE;
}

/********************************************************************
函数名称：UpdateAndInitXSCMMessage
函数功能: 更新并初始化备份消息内容
参数定义: wNumber:消息下标，pMesg:消息内容
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-5-6 16:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL UpdateAndInitXSCMMessage(WORD senssionID,MESSAGE_XSCM_SLAVE *pMesg)
{
	if((g_MESSAGE_XSCM_SLAVE == NULL)||(senssionID>=MESSAGE_MAX))
	{
		return FALSE;
	}
	else
	{
		
		if(pMesg != NULL)
		{
			memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].messageInfor,&pMesg->messageInfor,sizeof(MESSAGE));
			memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].sVaribleSave,&pMesg->sVaribleSave,sizeof(VARIBLE_SAVE));
			memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].slavehead,&pMesg->slavehead,sizeof(MSG_HEAD_SLAVE));
			g_MESSAGE_XSCM_SLAVE[senssionID].accID=0;
			if((pMesg->pVarileValue == NULL)&&(pMesg->variblelen !=0))
			{
				if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue == NULL)
				{
					g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue = (BYTE *)malloc(sizeof(BYTE)*pMesg->variblelen);
					if(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue)
					{
						{
							memset(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue,0,pMesg->variblelen);
						}
						//						memcpy(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue,pMesg->pVarileValue
						//							,g_MESSAGE_XSCM_SLAVE[senssionID].variblelen);
						g_MESSAGE_XSCM_SLAVE[senssionID].variblelen = pMesg->variblelen;
					}
				}
				else
				{
					if(pMesg->pVarileValue==NULL)
					{
						memset(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue,0,pMesg->variblelen);
						g_MESSAGE_XSCM_SLAVE[senssionID].variblelen = pMesg->variblelen;
					}
					else
					{
						if(g_MESSAGE_XSCM_SLAVE[senssionID].variblelen >= pMesg->variblelen)
						{
							memcpy(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue,pMesg->pVarileValue
								,pMesg->variblelen);
							g_MESSAGE_XSCM_SLAVE[senssionID].variblelen = pMesg->variblelen;
						}
						else
						{
							memcpy(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue,pMesg->pVarileValue
								,g_MESSAGE_XSCM_SLAVE[senssionID].variblelen);
						}
					}
				}
			}
			else
				return FALSE;
			
		}
		
	}
	return TRUE;
}
/********************************************************************
函数名称：UpdateXSCMMessage
函数功能: 更新备份消息内容
参数定义: wNumber:消息下标，pMesg:消息内容,bDelete:是否删除
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-5-6 16:02
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL UpdateXSCMMessage(WORD senssionID,MESSAGE_XSCM_SLAVE *pMesg,BOOL bDelete)
{
	if((g_MESSAGE_XSCM_SLAVE == NULL)||(senssionID>=MESSAGE_MAX))
	{
		return FALSE;
	}
	else
	{
		if(!bDelete)//update buffer
		{
			if(pMesg != NULL)
			{
				memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].messageInfor,&pMesg->messageInfor,sizeof(MESSAGE));
				memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].sVaribleSave,&pMesg->sVaribleSave,sizeof(VARIBLE_SAVE));
				memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].slavehead,&pMesg->slavehead,sizeof(MSG_HEAD_SLAVE));
				memcpy(&g_MESSAGE_XSCM_SLAVE[senssionID].sTestinit,&pMesg->sTestinit,sizeof(TEST_INIT));
				g_MESSAGE_XSCM_SLAVE[senssionID].variblelen = pMesg->variblelen;
				memcpy(g_MESSAGE_XSCM_SLAVE[senssionID].pVarileValue,pMesg->pVarileValue,pMesg->variblelen);
			}
		}
		else//clean buffer
		{
			free((g_MESSAGE_XSCM_SLAVE+senssionID)->pVarileValue);
			memset(g_MESSAGE_XSCM_SLAVE+senssionID,0,sizeof(MESSAGE_XSCM_SLAVE));
			memset(g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.sibNo,0,sizeof(STACK_SIZE));
			memset(&g_MESSAGE_XSCM_SLAVE[senssionID].sTestinit,0,sizeof(TEST_INIT));
			(g_MESSAGE_XSCM_SLAVE+senssionID)->pVarileValue = NULL;
			g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.sibStack.top=-1;
			YF_TRACE_SLP senssionID," ","clear out,clear all mem");
			
		}
	}
	return TRUE;
}
/********************************************************************
函数名称：GetDataTypeLen
函数功能: 根据数据类型得到长度
参数定义: bType:数据类型，
0：整型，1：BOOL，2：WORD，3；DWORD，4：字符串，5：字节串
返回定义: 返回长度
创建时间: 2008-8-22 15:55
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE GetDataTypeLen(BYTE bType)
{
	BYTE len = 0;
	switch(bType)
	{
	case DATA_BYTE:
	case DATA_BOOL:
	case DATA_CHARSTR:
	case DATA_BYTESTR:
		len = 1;
		break;
	case DATA_WORD:
		len =2 ;
		break;
	case DATA_DWORD:
		len = 4;
		break;
		
	default:
		len = 0;
		break;
	}
	return len;
}
BOOL getvalue(WORD senssionID,BYTE serviceID,MESSAGE_XSCM_SLAVE sMessage,char *pData,int begin,int end,int offset)
{
	char pValue[1024];
	WORD datalen;
	int len;
	int value;
	XSCM_CONSTVARIABLE sVariable;
	int nResultNo = sMessage.sVaribleSave.bVaribleTotal;
	if(offset>=nResultNo)
		return FALSE;
	memset(pValue,0,sizeof(pValue));
	if(begin<end)
		memcpy(pValue,pData+begin,end-begin);
	
	if(!GetParamAttr(serviceID,(WORD)sMessage.sVaribleSave.dVaribleNo[offset],&sVariable,FALSE))
		return FALSE;
	len  = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	if((int)strlen(pValue)>len)
		datalen = len;
	else
		datalen = strlen(pValue);
	if(sVariable.type == Var_CHARSTR)
	{
		SetVaribleValue(senssionID,pValue,sVariable.offset,datalen);
	}
	else
	{
		value = atoi(pValue);
		SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,datalen);
	}
	return TRUE;
}
int GetCount(WORD senssionID,BYTE serviceID,MESSAGE_XSCM_SLAVE sMessage,char* str,char* s)
{
    char* s1;
    char* s2;
    int count = 0;
	int len=strlen(str);
	int begin=0;
	int end=0;
    while(*str!='\0')
    {
        s1 = str;
        s2 = s;
        while(*s2 == *s1&&(*s2!='\0')&&(*s1!='0'))
        {
            s2++;
            s1++;
        }
        if(*s2 == '\0')
		{
			
			getvalue(senssionID,serviceID,sMessage,str-end,begin,end,count);
			count++;
			begin=end+1;
		}
        str++;
		end++;
    }
	getvalue(senssionID,serviceID,sMessage,str-end,begin,end,count);
    return count;
}



/********************************************************************
函数名称：UpdateDBResultData
函数功能: 更新返回的DATABASE数据
参数定义: senssionID:消息号
返回定义: 成功返回TRUE,是失败返回FALSE
创建时间: 2008-11-10 11:06
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UpdateDBResultData(WORD senssionID)
{
	char pchar[1024];
	MESSAGE_XSCM_SLAVE sMessage;
	char *pValue = NULL;
	BYTE nResultNo;
	BYTE serviceID ;
	// 	XSCM_CONSTVARIABLE sVariable;
	SQLDestMsg pDestMsg;
	int i = 0;
	int timeId;
	// 	WORD datalen;
	//	int value;
	// 	int len;
	WORD sibno;
	memset(pchar,0,sizeof(pchar));
	memset(&sMessage,0,sizeof(MESSAGE_XSCM_SLAVE));
	if(!ReadXSCMMessage(senssionID,&sMessage))
		return FALSE;
	timeId = GetBusyTimeId(senssionID);//先取消定时器
	if(timeId >= 0)
		KILL_TIMER((WORD)timeId);
	
	serviceID = sMessage.slavehead.serviceId;
	memcpy(&pDestMsg,sMessage.messageInfor.data,sizeof(SQLDestMsg));
	YF_TRACE_SLP senssionID,"","sql return status=%d",pDestMsg.iSqlExecRet);
	if ((pDestMsg.iSqlExecRet==DBACK_FAILED)||(pDestMsg.iSqlExecRet==DBACK_FAILED_NOCONN)||(pDestMsg.iSqlExecRet==DBACK_FAILED_EXECSQL)||(pDestMsg.iSqlExecRet==DBACK_FAILED_LENLACK)||(pDestMsg.iSqlExecRet==DBACK_FAILED_MEMLACK))
	{
		//	return FALSE;
		XVAPL_Pop(senssionID,&sibno);
		UpdateNextSib(senssionID,(WORD)sMessage.slavehead.lastsibNo,FALSE);
		return TRUE;
	}
	nResultNo = sMessage.sVaribleSave.bVaribleTotal;
	if(nResultNo == 0)
		return TRUE;
	strcpy(pchar,pDestMsg.sResults);
	GetCount(senssionID,serviceID,sMessage,pchar,"\t");
	// 	pValue=strtok(pchar,"\t");
	//	if(pValue != NULL)
	//	{
	//		if(!GetParamAttr(serviceID,(WORD)sMessage.sVaribleSave.dVaribleNo[i],&sVariable,FALSE))
	//			return FALSE;
	//		len  = GetDataTypeLen((BYTE)sVariable.type)*sVariable.length;
	//		if((int)strlen(pValue)>len)
	//			datalen = len;
	//		else
	//			datalen = strlen(pValue);
	//		if(sVariable.type == Var_CHARSTR)
	//		{
	//			SetVaribleValue(senssionID,pValue,sVariable.offset,datalen);
	//		}
	//		else
	//		{
	//			value = atoi(pValue);
	//			SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,datalen);
	//		}
	//		i++;
	//	}
	//	while((pValue = strtok(NULL,"\t"))!=NULL)
	//	{
	//		if(i>=nResultNo)
	//			break;
	//		if(!GetParamAttr(serviceID,(WORD)sMessage.sVaribleSave.dVaribleNo[i],&sVariable,FALSE))
	//			continue;
	//		if(strlen(pValue)>sVariable.length)
	//			datalen = sVariable.length;
	//		else
	//			datalen = strlen(pValue);
	//		if(sVariable.type == Var_CHARSTR)
	//			SetVaribleValue(senssionID,pValue,sVariable.offset,datalen);
	//		else
	//		{
	//			value = atoi(pValue);
	//			SetVaribleValue(senssionID,(BYTE *)&value,sVariable.offset,datalen);
	//		}
	////		SetVaribleValue(senssionID,pValue,sVariable.offset,sVariable.length);
	//		i++;
	// 	}
	return TRUE;
}
/********************************************************************
函数名称：HextoBcd
函数功能: 16进制转BCD码	
参数定义: value:需要转换值
返回定义: 返回BCD码值
创建时间: 2008-4-6 14:19
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE HextoBcd(int value)
{
	BYTE pValue;
	pValue = (value/10)*16+(value%10);
	return pValue;
}
/********************************************************************
函数名称：BcdToHex
函数功能: BCD码转16进制
参数定义: value：需要转换的值
返回定义: 返回16进制值
创建时间: 2008-4-6 14:19
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE BcdToHex(BYTE value)
{
	BYTE pValue;
	pValue = (value/16)*10+(value%16);
	return pValue;
}
BYTE GetNum(BYTE value)
{
	if(value<=9)
		value = value + 0x30;
	else
	{
		value =value + 0x37;
	}
	return value;
}
void BYTEToAscii(BYTE *distance,BYTE value)
{
	distance[0] = GetNum((BYTE)(value/16));
	distance[1] = GetNum((BYTE)(value%16));
}

void WORDToAscii(BYTE *distance,WORD value)
{
	distance[0] = GetNum((BYTE)((value>>8)/16));
	distance[1] = GetNum((BYTE)((value>>8)%16));
	distance[2] = GetNum((BYTE)(((BYTE)value)>>4));
	distance[3] = GetNum((BYTE)(((BYTE)value)%16));
}
void DWORDToAscii(BYTE *distance,DWORD value)
{
	WORD value1;
	WORD value2;
	value2=(WORD)(value%(256*256));
	value1 = (WORD)(value>>16);
	distance[0] = GetNum((BYTE)((value1>>8)/16));
	distance[1] = GetNum((BYTE)((value1>>8)%16));
	distance[2] = GetNum((BYTE)(((BYTE)value1)>>4));
	distance[3] = GetNum((BYTE)(((BYTE)value1)%16));
	distance[4] = GetNum((BYTE)((value2>>8)/16));
	distance[5] = GetNum((BYTE)((value2>>8)%16));
	distance[6] = GetNum((BYTE)(((BYTE)value2)>>4));
	distance[7] = GetNum((BYTE)(((BYTE)value2)%16));
}

/***************************************************************
函数名称：CheckCase_Begin
函数功能: 调试开始
参数定义:senssionID：会话号
返回定义: 无
创建时间: 2010/04/20  16:00:23
函数作者: LDW
注意事项: 无
****************************************************************/

BOOL CheckCase_Begin(MESSAGE pMessage)
{
	TEST_BEGINCONTROL tTestBegin;
	char ip[32];
	char callnum[32];
	TEST_BEGINCONTROLACK tAck;
	BOOL bFind = FALSE;
	int i;
	TID pTid;
	int bSeted;
	memset(&tTestBegin,0,sizeof(tTestBegin));
	memset(&tAck,0,sizeof(tAck));
	memcpy(&tTestBegin,pMessage.data,sizeof(TEST_BEGINCONTROL));
	memset(ip,0,sizeof(ip));
	memset(callnum,0,sizeof(callnum));
	if((tTestBegin.bType!=0)&&(tTestBegin.bType!=1))
	{
		return FALSE;
	}
	bSeted=-1;
	for(i=0;i<ServiceKey_Total;i++)
	{
		if((pServiceKey[i].SkeyType==tTestBegin.bType)&&(pServiceKey[i].serviceKey==tTestBegin.bServiceNumber))
		{
			if(pServiceKey[i].sServiceTest.bTest)
			{
				bSeted=1;
				break;
			}
			else
			{
				pServiceKey[i].sServiceTest.bTest=TRUE;
				pServiceKey[i].sServiceTest.bTestNow=FALSE;
				pServiceKey[i].sServiceTest.bServiceNumber= tTestBegin.bServiceNumber;
				pServiceKey[i].sServiceTest.stopsibno=tTestBegin.stopsibno;
				memcpy(pServiceKey[i].sServiceTest.reference,tTestBegin.reference,sizeof(tTestBegin.reference));
				bSeted=0;
				break;
				
			}
		}
	}
	if(bSeted==0)//设置成功
	{
		tAck.bFind=TRUE;
	}
	else
	{
		tAck.bFind=FALSE;
	}
	pMessage.head.len=sizeof(TEST_BEGINCONTROLACK);
	if(!GetTid(MID_TEST,&pTid))
	{
		return FALSE;
	}
	memcpy(&pMessage.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		return FALSE;
	}
	
	pMessage.head.event=evTest_BeginControlAck;
	memcpy(&pMessage.head.sender,&pTid,sizeof(TID));
	memset(pMessage.data,0,sizeof(pMessage.data));
	memcpy(pMessage.data,&tAck,sizeof(TEST_BEGINCONTROLACK));
	
	SEND_MSG(evIPCMessage,MID_TEST,(void *)&pMessage,sizeof(MESSAGE),(WORD)0,FALSE,NULL,0);
	
	
	return TRUE;
}

/***************************************************************
函数名称：CheckCase_End
函数功能: 调试结束
参数定义:senssionID：会话号
返回定义: 无
创建时间: 2010/04/20  16:00:23
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCase_End(MESSAGE sMessage)
{
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind;
	TEST_ENDCONTROL sEnd;
	WORD senssionID;
	int bSeted=-1;
	int i;
	int nNumber=-1;
	
	memcpy(&sEnd,sMessage.data,sizeof(TEST_ENDCONTROL));
	
	if((sEnd.bType!=0)&&(sEnd.bType!=1))
	{
		return FALSE;
	}
	bSeted=-1;
	for(i=0;i<ServiceKey_Total;i++)
	{
		if((pServiceKey[i].SkeyType==sEnd.bType)&&(pServiceKey[i].serviceKey==sEnd.bServiceNumber))
		{
			if(pServiceKey[i].sServiceTest.bTest)
			{
				bSeted=1;
				if(pServiceKey[i].sServiceTest.bTestNow)
				{
					bSeted=2;
					senssionID=pServiceKey[i].sServiceTest.senssionID;
				}
				nNumber=i;
				break;
			}
		}
	}
	if(bSeted==1)
	{
		memset(&pServiceKey[nNumber].sServiceTest,0,sizeof(pServiceKey[nNumber].sServiceTest));
	}
	else if(bSeted==2)//获取成功
	{
		
		if(senssionID >= MESSAGE_MAX)
			return FALSE;
		bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
		if(!bFind)
			return FALSE;
		pMessage.sTestinit.bCheckCase=FALSE;
		pMessage.sTestinit.bStopRun=FALSE;
		UpdateXSCMMessage((WORD)senssionID,&pMessage,FALSE);
		memset(&pServiceKey[nNumber].sServiceTest,0,sizeof(pServiceKey[nNumber].sServiceTest));
		ControlRunSib(senssionID,FALSE);
	}
	
	
	return TRUE;
}
int GetVaribleId(WORD senssionID,BYTE bType,char *pdescrible)
{
	int i;
	int ntotal;
	int serviceId;
	serviceId=g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.serviceId;
	if(-1==serviceId)
		return -1;
	if(bType==0)//changliang 
	{
		ntotal=pXscmSlp[serviceId].sBasic.nConstVariableTotal;
		for (i=0;i<ntotal;i++) 
		{
			if(strcmp(pXscmSlp[serviceId].pConstAtte[i].name,pdescrible)==0)
			{
				return i;
			}
		}
	}
	else if(bType==1)
	{
		ntotal=pXscmSlp[serviceId].sBasic.nVariableTotal;
		for (i=0;i<ntotal;i++) 
		{
			if(strcmp(pXscmSlp[serviceId].pVariableAttr[i].name,pdescrible)==0)
			{
				return i;
			}
		}
	}
	else
		return FALSE;
	return -1;
}
/***************************************************************
函数名称：CheckCase_getSenssionID
函数功能: 获取调试会话号
参数定义:
返回定义: 无
创建时间: 2010/05/03  13:32:49
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCase_getSenssionID(MESSAGE pMessage)
{
	TEST_GETTESTSENSSION tTestBegin;
	char ip[32];
	char callnum[32];
	//	MESSAGE_XSCM_SLAVE pMessage;
	TEST_GETTESTSENSSIONACK tAck;
	BOOL bFind = FALSE;
	int i;
	TID pTid;
	int bSeted;
	//	if(senssionID >= MESSAGE_MAX)
	//		return FALSE;
	//	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	//	if(!bFind)
	//		return FALSE;
	memset(&tTestBegin,0,sizeof(tTestBegin));
	memset(&tAck,0,sizeof(tAck));
	memcpy(&tTestBegin,pMessage.data,sizeof(TEST_GETTESTSENSSION));
	memset(ip,0,sizeof(ip));
	memset(callnum,0,sizeof(callnum));
	if((tTestBegin.bType!=0)&&(tTestBegin.bType!=1))
	{
		return FALSE;
	}
	bSeted=-1;
	for(i=0;i<ServiceKey_Total;i++)
	{
		if((pServiceKey[i].SkeyType==tTestBegin.bType)&&(pServiceKey[i].serviceKey==tTestBegin.bServiceNumber))
		{
			if((pServiceKey[i].sServiceTest.bTest)&&(pServiceKey[i].sServiceTest.bTestNow))
			{
				bSeted=1;
				break;
			}
		}
	}
	if(bSeted==1)//获取成功
	{
		tAck.bFind=TRUE;
		tAck.senssionID=pServiceKey[i].sServiceTest.senssionID;
	}
	else
	{
		tAck.bFind=FALSE;
		tAck.senssionID=-1;
	}
	
	pMessage.head.len=sizeof(TEST_GETTESTSENSSIONACK);
	if(!GetTid(MID_TEST,&pTid))
	{
		return FALSE;
	}
	memcpy(&pMessage.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		return FALSE;
	}
	
	pMessage.head.event=evTest_GetTestSenssionAck;
	memcpy(&pMessage.head.sender,&pTid,sizeof(TID));
	memset(pMessage.data,0,sizeof(pMessage.data));
	memcpy(pMessage.data,&tAck,sizeof(TEST_GETTESTSENSSIONACK));
	//	UpdateXSCMMessage((WORD)sid,&pMessage,FALSE);
	
	SEND_MSG(evIPCMessage,MID_TEST,(void *)&pMessage,sizeof(MESSAGE),(WORD)0,FALSE,NULL,0);
	return TRUE;
}
/***************************************************************
函数名称：CheckCase_ReadValue
函数功能: 调试开始
参数定义:senssionID：会话号
返回定义: 无
创建时间: 2010/04/20  16:00:23
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCase_ReadValue(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind;
	TEST_READVALUE sReadValue;
	TEST_READVALUEACK sReadValueAck;
	//	SIB_PARAM sibParam;
	XSCM_CONSTVARIABLE sConstVarible;
	int len;
	int varibleno;
	BYTE value[1024];
	TID pTid;
	memset(value,0,sizeof(value));
	varibleno=-1;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memcpy(&sReadValue,pMessage.messageInfor.data,sizeof(TEST_READVALUE));
	if(sReadValue.senssionID==senssionID)
	{
		varibleno=GetVaribleId(senssionID,(BYTE)sReadValue.VaribleType,sReadValue.variblename);
		memset(&sReadValueAck,0,sizeof(sReadValueAck));
		if(varibleno==-1)//没找到
		{
			sReadValueAck.bStatus=FALSE;
			sReadValueAck.datalen=0;
			
		}
		else
		{
			sReadValueAck.bStatus=TRUE;
			if(sReadValue.VaribleType==0)//常量
			{
				if(GetParamAttr(g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.serviceId,(WORD)varibleno,&sConstVarible,TRUE))
				{
					len = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
					GetConstValue(g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.serviceId,value,sConstVarible.offset,(WORD)len);
					
				}
			}
			else
			{
				if(GetParamAttr(g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.serviceId,(WORD)varibleno,&sConstVarible,FALSE))
				{
					len = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
					GetVaribleValue(senssionID,value,sConstVarible.offset,(WORD)len);
					
				}
			}
			
			if(len<sizeof(sReadValueAck.data))
			{
				sReadValueAck.datalen=len;
				memcpy(sReadValueAck.data,value,len);
			}
			else
			{
				sReadValueAck.datalen=sizeof(sReadValueAck.data);
				memcpy(sReadValueAck.data,value,sizeof(sReadValueAck.data));
				
			}
		}
	}
	else
		return FALSE;
	memcpy(sReadValueAck.variblename,sReadValue.variblename,sizeof(sReadValue.variblename));
	pMessage.messageInfor.head.len=sizeof(TEST_READVALUEACK);
	if(!GetTid(MID_TEST,&pTid))
	{
		return FALSE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		return FALSE;
	}
	
	pMessage.messageInfor.head.event=evTest_ReadValueAck;
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&sReadValueAck,sizeof(TEST_READVALUEACK));
	SEND_MSG(evIPCMessage,MID_TEST,(void *)&pMessage.messageInfor,sizeof(MESSAGE),(WORD)senssionID,FALSE,NULL,0);
	
	
	return TRUE;
}

/***************************************************************
函数名称：CheckCase_SetValue
函数功能: 调试开始
参数定义:senssionID：会话号
返回定义: 无
创建时间: 2010/04/20  16:00:23
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCase_SetValue(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind;
	TEST_SETVALUE sSetValue;
	TEST_SETVALUEACK sSetValueAck;
	XSCM_CONSTVARIABLE sConstVarible;
	int len;
	int varibleno;
	BYTE value[1024];
	TID pTid;
	memset(value,0,sizeof(value));
	varibleno=-1;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memcpy(&sSetValue,pMessage.messageInfor.data,sizeof(TEST_SETVALUE));
	if(sSetValue.senssionID==senssionID)
	{
		varibleno=GetVaribleId(senssionID,(BYTE)sSetValue.VaribleType,sSetValue.variblename);
		memset(&sSetValueAck,0,sizeof(sSetValueAck));
		if(varibleno==-1)//没找到
		{
			sSetValueAck.bStatus=FALSE;
			
		}
		else
		{
			sSetValueAck.bStatus=FALSE;
			if(sSetValue.VaribleType==0)//常量
			{
				sSetValueAck.bStatus=FALSE;
				
			}
			else
			{
				if(GetParamAttr(g_MESSAGE_XSCM_SLAVE[senssionID].slavehead.serviceId,(WORD)varibleno,&sConstVarible,FALSE))
				{
					len = GetDataTypeLen((BYTE)sConstVarible.type)*sConstVarible.length;
					if(sSetValue.datalen>=len)
						SetVaribleValue(senssionID,(BYTE *)sSetValue.data,sConstVarible.offset,(WORD)len);
					else
					{
						SetVaribleValue(senssionID,(BYTE *)sSetValue.data,sConstVarible.offset,(WORD)sSetValue.datalen);
					}
					sSetValueAck.bStatus=TRUE;	
				}
			}
		}
	}
	else
		return FALSE;
	pMessage.messageInfor.head.len=sizeof(TEST_SETVALUEACK);
	if(!GetTid(MID_TEST,&pTid))
	{
		return FALSE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		return FALSE;
	}
	
	pMessage.messageInfor.head.event=evTest_SetValueAck;
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&sSetValueAck,sizeof(TEST_SETVALUEACK));
	SEND_MSG(evIPCMessage,MID_TEST,(void *)&pMessage.messageInfor,sizeof(MESSAGE),(WORD)senssionID,FALSE,NULL,0);
	
	
	return TRUE;
}

/***************************************************************
函数名称：CheckCase_CurrentSibNo
函数功能: 调试开始
参数定义:senssionID：会话号
返回定义: 无
创建时间: 2010/04/20  16:00:23
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCase_CurrentSibNo(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind;
	TEST_CURRENTSIBNO sCurrentSibNo;
	TEST_CURRENTSIBNOACK sCurrentSibNoAck;
	TID pTid;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memcpy(&sCurrentSibNo,pMessage.messageInfor.data,sizeof(TEST_CURRENTSIBNO));
	memset(&sCurrentSibNoAck,0,sizeof(TEST_CURRENTSIBNOACK));
	if(sCurrentSibNo.senssionID==senssionID)
	{
		if(pMessage.slavehead.sibStack.top>=0)
		{
			pMessage.sTestinit.bCheckCase=TRUE;
			pMessage.sTestinit.bStopRun=TRUE;
			sCurrentSibNoAck.bStatus=TRUE;
			sCurrentSibNoAck.sibno=pMessage.slavehead.sibNo;
			pMessage.sTestinit.stopsib=sCurrentSibNoAck.sibno;
		}
		else
		{
			sCurrentSibNoAck.bStatus=FALSE;
		}
		
	}
	else
		return FALSE;
	pMessage.messageInfor.head.len=sizeof(TEST_CURRENTSIBNOACK);
	if(!GetTid(MID_TEST,&pTid))
	{
		return FALSE;
	}
	memcpy(&pMessage.messageInfor.head.receiver,&pTid,sizeof(TID));
	if(!GetTid(MID_SLP,&pTid))
	{
		return FALSE;
	}
	
	pMessage.messageInfor.head.event=evTest_CurrentSibNoAck;
	memcpy(&pMessage.messageInfor.head.sender,&pTid,sizeof(TID));
	memset(pMessage.messageInfor.data,0,sizeof(pMessage.messageInfor.data));
	memcpy(pMessage.messageInfor.data,&sCurrentSibNoAck,sizeof(TEST_CURRENTSIBNOACK));
	SEND_MSG(evIPCMessage,MID_TEST,(void *)&pMessage.messageInfor,sizeof(MESSAGE),(WORD)senssionID,FALSE,NULL,0);
	UpdateXSCMMessage((WORD)senssionID,&pMessage,FALSE);
	
	return TRUE;
}

/***************************************************************
函数名称：CheckCase_SibControl
函数功能: 调试开始
参数定义:senssionID：会话号
返回定义: 无
创建时间: 2010/04/20  16:00:23
函数作者: LDW
注意事项: 无
****************************************************************/
BOOL CheckCase_SibControl(WORD senssionID)
{
	MESSAGE_XSCM_SLAVE pMessage;
	BOOL bFind;
	TEST_SIBCONTROL sSibControl;
	if(senssionID >= MESSAGE_MAX)
		return FALSE;
	bFind=ReadXSCMMessage((WORD)senssionID,&pMessage);
	if(!bFind)
		return FALSE;
	memcpy(&sSibControl,pMessage.messageInfor.data,sizeof(TEST_SIBCONTROL));
	if(sSibControl.senssionID==senssionID)
	{
		if(pMessage.slavehead.sibStack.top>=0)
		{
			pMessage.sTestinit.bCheckCase=!sSibControl.bRun;
			pMessage.sTestinit.bStopRun=!sSibControl.bRun;
			pMessage.sTestinit.stopsib=sSibControl.sibno;
			if(sSibControl.bRun)
			{
				RelaseCheckCase(senssionID);
			}
			
		}
		else
		{
			return FALSE;
		}
		
	}
	else
		return FALSE;
	
	UpdateXSCMMessage((WORD)senssionID,&pMessage,FALSE);
	return TRUE;
}

void DataTest()
{
	MESSAGE pMessage;
	TID pTid ;
	int wNumber = -1;
	stPXSSM_InitiateDP pInitiateDP;
	
	if(!GetTid(MID_SLP,&pTid))
		return;
	
	memset(&pMessage,0,sizeof(MESSAGE));
	
	pMessage.head.sender.node = pTid.node;
	
	pMessage.head.Source.node = pTid.node;
#ifdef _WIN32
	{
		pMessage.head.sender.hWnd= pTid.hWnd;
		pMessage.head.Source.hWnd= pTid.hWnd;
		
	}
#else
	{
		pMessage.head.sender.procece=pTid.procece;
		pMessage.head.Source.procece=pTid.procece;
	}
#endif
	pMessage.head.sender.module=pTid.module;
	pMessage.head.Source.module=pTid.module;
	pMessage.head.Source.mId=pTid.mId;
	
	
	pMessage.head.ptrAck=0;
	pMessage.head.ackLen=0;
	pMessage.head.reserve=0;
	pMessage.head.sync=FALSE;
	pMessage.head.event = evPXSSM_InitiateDP;
	//	wNumber=GetSenssionID();/*申请会话号*/
	pInitiateDP.sid = 1;
	pInitiateDP.serviceKey = 11;
	strcpy(pInitiateDP.calledNumber,"744991");
	strcpy(pInitiateDP.callingNumber,"86354537");
	strcpy(pInitiateDP.dialedNumber,"744991");
	memcpy(pMessage.data,&pInitiateDP,sizeof(stPXSSM_InitiateDP));
	pMessage.head.len=sizeof(stPXSSM_InitiateDP);
	SEND_MSG(evThreadMessage,MID_SLP,&pMessage,sizeof(MESSAGE),/*(WORD)wNumber*/1,FALSE,NULL,0);;
} 


//void TestTimer()
//{
//	int i;
//	int timerId;
//	for(i=0;i<5000;i++)
//	{
//	
//		timerId = SET_TIMER(i*2,evTimer0,MID_SLP,1,FALSE);
//		if(timerId<0)
//			YF_TRACE_SLP 1,"TestTimer","settimer error,timerId=%d,i=%d",timerId,i);
//		if(i%500==0)
//		{
//			YF_TRACE_SLP 1,"TestTimer","settimer ok,timerId=%d,i=%d",timerId,i);
//		}
//	}
//	for(i=0;i<5000;i++)
//	{
//	
//		timerId = SET_TIMER(i*2+90000,evTimer0,MID_SLP,1,FALSE);
//		if(timerId<0)
//			YF_TRACE_SLP 1,"TestTimer","settimer error,timerId=%d,i=%d",timerId,i+5000);
//		if(i%500==0)
//		{
//			YF_TRACE_SLP 1,"TestTimer","settimer ok,timerId=%d,i=%d",timerId,i+5000);
//		}
//	}
//	for(i=0;i<5000;i++)
//	{
//	
//		timerId = SET_TIMER(i*2,evTimer0,MID_SLP,1,FALSE);
//		if(timerId<0)
//			YF_TRACE_SLP 1,"TestTimer","settimer error,timerId=%d,i=%d",timerId,i+10000);
//		if(i%500==0)
//		{
//			YF_TRACE_SLP 1,"TestTimer","settimer ok,timerId=%d,i=%d",timerId,i+10000);
//		}
//	}
//	for(i=0;i<5000;i++)
//	{
//	
//		timerId = SET_TIMER(i+90000,evTimer0,MID_SLP,1,FALSE);
//		if(timerId<0)
//			YF_TRACE_SLP 1,"TestTimer","settimer error,timerId=%d,i=%d",timerId,i+15000);
//		if(i%500==0)
//		{
//			YF_TRACE_SLP 1,"TestTimer","settimer ok,timerId=%d,i=%d",timerId,i+15000);
//		}
//	}
// }