/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2008-04-12   14:26
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP\trace.c
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP
file base:SPP_SLP
file ext: c
author:	  刘定文

purpose:	调试跟踪处理
*********************************************************************/
#include "yftrace.h"
/********************************************************************
函数名称：EnableTrace
函数功能: 启动跟踪
参数定义: 无
返回定义: 无
创建时间: 2008-4-12 21:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL EnableTrace(BYTE fileType,BOOL bTrace)
{
	if(!bTrace)//使能重要日志
	{
	//	g_Trace = TRUE;
	//	Openfile(fileType);//先打开文件
		FileTypelog = fileType;
		return g_Trace;
	}
	if(g_Trace)//已经处于跟踪状态
		return g_Trace;
	if(ReadTraceIni())//读调试配置
	{
		if(g_Trace_Infor.bTrace)
		{
			Openfile(fileType);//先打开文件
			g_Trace = TRUE;
//			SetTraceCondition(&g_Trace_Infor);
		}
		else if(g_Trace_Infor.bGetSysInfor)//得到系统信息
		{
			GetSysInfor();
		}
	}
	return g_Trace;
	
}
/********************************************************************
函数名称：DisaleTrace
函数功能: 停止跟踪
参数定义: 无
返回定义: 无
创建时间: 2008-4-12 21:33
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL DisaleTrace(BOOL bTrace)
{
	if(!bTrace)//关闭总要日志文件
	{
		Closefile(FileTypelog);//先打开文件
		return g_Trace; 
	}
	g_Trace = FALSE;
	Closefile(FileTypeTrace);//关闭文件
	memset(&g_Trace_Infor,0,sizeof(TRACE_CONDITION));
	return g_Trace;
}

/********************************************************************
函数名称：TRACE
函数功能: 重要信息或者跟踪信息显示
参数定义: sid:会话号，filetype:文件类型，msg:错误信息内容
返回定义: 无
创建时间: 2008-4-12 21:33
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DLLIMPORT void TRACE(WORD sid,BOOL bImportInfor,const char *function_name,const char *msg)
{
	FILE		*fp=NULL;
	if(bImportInfor)//写重要信息
	{
		WriteLog(FileTypelog,function_name,msg);
	}
	else//写调试跟踪日志
	{
		WriteTrace(sid,FileTypeTrace,function_name,msg);
	}
}

/********************************************************************
函数名称：SetTraceCondition
函数功能: 设置跟踪条件
参数定义: pInfor:跟踪条件
返回定义: 无
创建时间: 2008-4-12 21:47
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

//DLLIMPORT void SetTraceCondition(TRACE_CONDITION *pInfor)
//{
//	if(pInfor == NULL)
//	{
//		g_Trace = FALSE;
//	}
//	else
//	{
//		memcpy(&g_Trace_Infor,pInfor,sizeof(TRACE_CONDITION));
//		g_Trace = TRUE;
//	}
//	
//}
/********************************************************************
函数名称：ReadTraceIni
函数功能: 读取跟踪配置文件
参数定义: 无
返回定义: 无
创建时间: 2008-4-12 22:08
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL ReadTraceIni()
{
	SECTION *sec;
	INI ini;
	char TraceName[32];
	char tmpSection[LENGTH_SECTION];
	sprintf(TraceName,"C:\\yfcomm\\ini\\%s",TRANCE_INI);
	loadINI(&ini,TraceName);
	/*得到[TRACE]*/
	sprintf(tmpSection,"TRACE");
	sec=getSection(&ini,tmpSection);
	if(sec==NULL)
	{
		return FALSE;
	}
	{
		g_Trace_Infor.Tel_Type=atoi(GetKeyList(sec,0));//主叫号码判断还是被叫号码判断，1为主叫，2为被叫
		g_Trace_Infor.Tel_Match=atoi(GetKeyList(sec,1));//1表示左匹配，2表示右匹配，3表示全匹配，4表示全部电话号码
		strcpy(g_Trace_Infor.Telephone,GetKeyList(sec,2));//匹配号码
		g_Trace_Infor.bGetSysInfor=(BOOL)atoi(GetKeyList(sec,3));	
		g_Trace_Infor.bTrace=(BOOL)atoi(GetKeyList(sec,4));	
	}

	/*end*/
	freeINI(&ini);
	return TRUE;	
}
/********************************************************************
函数名称：GetSysInfor
函数功能: 得到系统信息
参数定义: 无
返回定义: 无
创建时间: 2008-4-12 22:08
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void GetSysInfor()
{
	int i;
	i=0;
}
/********************************************************************
函数名称：WriteLog
函数功能: 记录重要信息
参数定义: filetype：类型，msg：记录信息
返回定义: 无
创建时间: 2008-4-12 22:14
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void WriteLog(BYTE filetype,const char *function_name,const char *msg)
{
	FILE	       *fp=NULL;
	SYSTEMTIME     Clock;
	char		   Title[255];
	switch(filetype)
	{
	case SPP_LOG:
		{
			sprintf(Title,"C:\\yfcomm\\log\\%s",SPP_LOG_FILE);
			fp = fopen (Title,"a");
		}
		break;
	case PXSSM_LOG:
		{
			sprintf(Title,"C:\\yfcomm\\log\\%s",PXSSM_LOG_FILE);
			fp = fopen (Title,"a");
		}
		break;
	default:
		return;
		break;
	}
	GetLocalTime(&Clock);
	if ( fp == NULL ) return;
	fprintf (fp, "%4d-%02d-%02d %02d:%02d:%02d:%03d [%s] %s\n",Clock.wYear,Clock.wMonth, Clock.wDay,
		Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,function_name,msg);
	fclose(fp);
}
/********************************************************************
函数名称：WriteTrace
函数功能: 记录跟踪信息
参数定义: sid:会话号，filetype:文件类型，msg:错误信息内容
返回定义: 无
创建时间: 2008-4-12 22:17
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void WriteTrace(WORD sid,BYTE filetype,const char *function_name,const char *msg)
{
	FILE			*fp=NULL;
	SYSTEMTIME      Clock;
	BYTE			errno_type ;
	char			telephone[32];
	memset(telephone,0,sizeof(telephone));
	switch(filetype)
	{
	case SPP_TRANCE:
		{
			fp = fp_sppTrace;			
		}
		break;
	case PXSSM_TRANCE:
		{
			fp = fp_pXSSMTrace;
		}
		break;
	default:
		return ;
		break;
	}
	errno_type = GetErrorType(sid);
	GetLocalTime(&Clock);
	if ( fp == NULL ) return;
	switch(errno_type)
	{
	case 0://条件不满足
		break;
	case 1://条件满足
		{
			fprintf (fp, "%4d-%02d-%02d %02d:%02d:%02d:%03d [%s] [sid]=%d : %s\n",Clock.wYear,Clock.wMonth, Clock.wDay,
		Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,function_name,sid,msg);
		}
		break;
	case 2://条件不满足但是sid越界,sid==MESSAGE_MAX
		{
			fprintf (fp, "%4d-%02d-%02d %02d:%02d:%02d:%03d [%s] [sid]=%d is uneffect: %s\n",Clock.wYear,Clock.wMonth, Clock.wDay,
		Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,function_name,sid,msg);
		}
		break;
	case 3://条件不满足且sid>MESSAGE_MAX
		{
			fprintf (fp, "%4d-%02d-%02d %02d:%02d:%02d:%03d [%s] [sid]=%d is overflow: %s\n",Clock.wYear,Clock.wMonth, Clock.wDay,
		Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,function_name,sid,msg);
		}
	default :
		break;
	}
}
/********************************************************************
函数名称：GetErrorType
函数功能: 条件判断
参数定义: sid:会话号
返回定义: 无
创建时间: 2008-4-13 9:43
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BYTE GetErrorType(WORD sid)
{
	char  telephone[32];
	MESSAGE pMessage;
	BYTE bWrite;
	if(sid==MESSAGE_MAX)
		return 2;
	if(sid >MESSAGE_MAX)
		return 3;
	if(!Read_Message(sid,&pMessage))//读取不到消息内容
		return 3;

	if(g_Trace_Infor.Tel_Type==1)//主叫方式判断
	{
		strcpy(telephone,pMessage.head.CallingNum);
	}
	else
	{
		strcpy(telephone,pMessage.head.CalledNum);
	}
	switch(g_Trace_Infor.Tel_Match)
	{
	case MATCH_LEFT: // 左匹配
		{
			if(strcspn(telephone,g_Trace_Infor.Telephone)==0)
				bWrite = 1;
			else
				bWrite = 0;
		}
		break;
	case MATCH_RIGHT://右匹配
		{
			if((strcspn(telephone,g_Trace_Infor.Telephone)+strlen(g_Trace_Infor.Telephone)+1)
				==strlen(telephone))
				bWrite = 1;
			else
				bWrite = 0;
		}
		break;
	case MATCH_ALL://全部匹配
		{
			if(strcmp(telephone,g_Trace_Infor.Telephone)==0)
			{
				bWrite = 1;
			}
			else
			{
				bWrite = 0;
			}
		}
		break;
	case MATCH_NONE://无须匹配，全部跟踪
		{
			bWrite = 1;
		}
		break;
	default:
		{
			bWrite = 0;
		}
		break;
	}
	return bWrite;
	
}
/********************************************************************
函数名称：Openfile
函数功能: 打开文件
参数定义: filetype:文件种类
返回定义: 无
创建时间: 2008-4-13 11:13
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Openfile(BYTE filetype)
{
	char        Title[255];
	switch(filetype)
	{
	case SPP_LOG:
		{
			FileTypelog = filetype;
			sprintf(Title,"C:\\yfcomm\\log\\%s",SPP_LOG_FILE);
			fp_spplog = fopen (Title,"a");
		}
		break;
	case SPP_TRANCE:
		{
			FileTypeTrace = filetype;
			sprintf(Title,"C:\\yfcomm\\log\\%s",SPP_TRANCE_FILE);
			fp_sppTrace = fopen (Title,"a");
		}
		break;
	case PXSSM_LOG:
		{
			FileTypelog = filetype;
			sprintf(Title,"C:\\yfcomm\\log\\%s",PXSSM_LOG_FILE);
			fp_pXSSMlog = fopen (Title,"a");
		}
		break;
	case PXSSM_TRANCE:
		{
			FileTypeTrace = filetype;
			sprintf(Title,"C:\\yfcomm\\log\\%s",PXSSM_TRANCE_FILE);
			fp_pXSSMTrace = fopen (Title,"a");
		}
		break;
	default:
		return ;
		break;
	}
}
/********************************************************************
函数名称：Closefile
函数功能: 关闭文件
参数定义: filetype:文件种类
返回定义: 无
创建时间: 2008-4-13 11:14
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Closefile(BYTE filetype)
{
	switch(filetype)
	{
	case SPP_LOG:
		{
			fclose(fp_spplog); 
		}
		break;
	case SPP_TRANCE:
		{
			fclose(fp_sppTrace);
		}
		break;
	case PXSSM_LOG:
		{
			fclose(fp_pXSSMlog);
		}
		break;
	case PXSSM_TRANCE:
		{
			fclose(fp_pXSSMTrace);
		}
		break;
	default:
		return ;
		break;
	}
}