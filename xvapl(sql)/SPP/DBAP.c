#include "yftrace.h"
#include "GLM_Log.h"
#include "DBAP.h"


char g_sDBUser[50],g_sDBPwd[50];
BYTE g_DBtns[150];
DBCONNSTATE DBConnState[DBQUERY_THREAD_NUM] = {0};
BOOL g_bDbapTrace = TRUE;
/**************************************************************************
函数名：DBInitial
函数功能: 初始化所有数据库分布处理模块
参数定义: 无
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-10 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL DBInitial(MESSAGE* pyfMsg,WORD wNum)
{
	int i = 0;
	
	for(i = 0; i < DBQUERY_THREAD_NUM; i++)//启动注册DBM,DBQ线程
	{
		DBConnState[i].mId = i + MID_DBQ;
		DBConnState[i].iConnState = 1;  //数据库连接初始化为不可用状态
		DBConnState[i].bIsBusy = FALSE;
	}
	
//	Update_Message(pyfMsg,wNum,TRUE);
	
	return TRUE;
}

/**************************************************************************
函数名：InitThread
函数功能: 初始化查询线程DBQ、DBM监控线程，注册DBD线程
参数定义: 无
返回定义: 
创建时间: 2008-6-13 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void InitThread()
{
	DWORD dwHandle = 0;
	int i = 0;
	BOOL ret = FALSE;
	
	for(i = 0; i < DBQUERY_THREAD_NUM; i++)
	{
		if(_beginthreadex(NULL,0,DBIProc,&DBConnState[i].mId,0,NULL) == 0)
		{
			GLM_WARN "start DBIProc failed");
		}
	}
	if(_beginthreadex(NULL,0,DBMonitorProc,NULL,0,NULL) == 0)
	{
		GLM_WARN "start DBMonitorProc failed");
	}
	
	ret = RegisterThread(MID_DBD);
	
	return;
}
/**************************************************************************
函数名：DBConnect
函数功能:对每个DBQ建立连接.如以前已经建立连接并可用,不重建.
参数定义: no
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-10 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL DBConnect(MESSAGE* pyfMsg,WORD wNum)
{
	int i = 0;
	TID * tid = NULL;
	
	for(i = 0; i < DBQUERY_THREAD_NUM; i++)
	{
		tid = GetTID(MID_DBQ + i);
		if(tid == NULL) 
			continue;
		else
			PostThreadMessage(tid->module,evDBQConnection,MID_DBQ+i,0);
	}
	
//	Update_Message(pyfMsg,wNum,TRUE);

	return TRUE;
}


/**************************************************************************
函数名：DBAvailCheck
函数功能: 检查指定模块的连接有效性
参数定义: mId,数据库访问模块号
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-10 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL DBAvailCheck(MESSAGE* pyfMsg,WORD wNum)
{
	TID src;


	UINT i ;
	if(!GetTid(MID_DBD,&src))
	{
		GLM_WARN "GetTID(MID_DBD) or GetTID(MID_SLP)  failed");
		return FALSE;
	}
	pyfMsg->head.event = evDBDConnAvailCheckAck;
	pyfMsg->head.receiver = pyfMsg->head.sender;
	pyfMsg->head.sender = src;
	
	((DBCONNINFO*)(pyfMsg->data))->iConnectionNum = DBQUERY_THREAD_NUM;
	for(i = 0; i < DBQUERY_THREAD_NUM; i++)
	{
		(((DBCONNINFO*)(pyfMsg->data))->sConnInfo)[i] = DBConnState[i].iConnState;
	}
	pyfMsg->head.len = sizeof(DBCONNINFO);

	SEND_MSG(evThreadMessage,pyfMsg->head.receiver.mId,(BYTE*)pyfMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);

	return TRUE;
}

/**************************************************************************
函数名：DBDistrQuery
函数功能: 数据库分布式查询处理
参数定义: 查询信息
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-12 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL DBDistrQuery(MESSAGE* pyfMsg,WORD wNum)
{
	static UINT iCursor = 0; 
	UINT j;
	BOOL bFind = FALSE;
	TID dst ;
	
	YF_TRACE_DBAP -1,"DBDistrQuery","sqlSrc:req row:%d,sql:%s",((SQLSrcMsg*)(pyfMsg->data))->iRow,((SQLSrcMsg*)(pyfMsg->data))->sSql);
	for(j = 0; j < DBQUERY_THREAD_NUM; j++)//遍历连接,遇到正常且不忙的连接则进行查询,如找到,则bFind = TRUE;
	{
		if((DBConnState[iCursor].iConnState == 1) && (!DBConnState[iCursor].bIsBusy))
		{
			if(!GetTid(DBConnState[iCursor].mId,&dst))
			{
				GLM_WARN "GetTID(MID_DBD,MID_DBQ) execute failed,conn:%d",DBConnState[iCursor].mId);
				return FALSE;
			}
			pyfMsg->head.event = evDBQuery;
			pyfMsg->head.receiver = dst ;

			SEND_MSG(evThreadMessage,DBConnState[iCursor].mId,(BYTE*)pyfMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
			iCursor=++iCursor%DBQUERY_THREAD_NUM;
			bFind = TRUE;
			break;
		}
		else
			iCursor=++iCursor%DBQUERY_THREAD_NUM;		
	}	
	if(!bFind)//未找到正常且不忙的连接,则找一个正常连接,不管连接是否忙碌
	{
		for(j = 0; j < DBQUERY_THREAD_NUM; j++)
		{
			if(DBConnState[iCursor].iConnState == 1)
			{
				if(!GetTid(DBConnState[iCursor].mId,&dst))
				{
					GLM_WARN "GetTID(MID_DBD,MID_DBQ) execute failed,conn:%d",DBConnState[iCursor].mId);
					return FALSE;
				}
				pyfMsg->head.event = evDBQuery;
				pyfMsg->head.receiver = dst ;
				
				SEND_MSG(evThreadMessage,DBConnState[iCursor].mId,(BYTE*)pyfMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
				iCursor=++iCursor%DBQUERY_THREAD_NUM;
				bFind = TRUE;
				break;
			}
			else
				iCursor=++iCursor%DBQUERY_THREAD_NUM;		
		}
	}

	if(!bFind)//没有找到一个正常连接.告诉SLP查询失败
	{
		dbi_MsgAck_NoConn(MID_DBD,pyfMsg,wNum);
	}
	return TRUE;
}

/**************************************************************************
函数名：DBInfoMag
函数功能: 
参数定义: 
返回定义: 
创建时间: 2008-6-10 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void  DBInfoMag(BOOL bDirection)
{
	memset(g_sDBUser,0,sizeof(g_sDBUser));
	memset(g_sDBPwd,0,sizeof(g_sDBPwd));
	dbi_GetDBIni(g_sDBUser,g_sDBPwd);
	return;
}

unsigned  __stdcall /*void*/ DBAPThreadProc(LPVOID lpP)
{
	MESSAGE yfMsg;
	MSG msg;
	int i;
	
	for(i = 0; i < DBQUERY_THREAD_NUM; i++)
	{
		DBConnState[i].mId = i + MID_DBQ;
		DBConnState[i].iConnState = 0;  //数据库连接初始化为不可用状态
		DBConnState[i].bIsBusy = FALSE;
	}
	
	DBInfoMag(TRUE);
	
	if(dbi_InitialDB())
		InitThread();
//	g_bDbapTrace = TRUE;
	while(bRun)
	{
		memset(&msg,0,sizeof(MSG));
		memset(&yfMsg,0,sizeof(MESSAGE));
		GetMessage(&msg,NULL,0,0);
		if(msg.message != evThreadMessage)		continue;
		if(!COM_Read_Message((WORD)msg.wParam,&yfMsg))		continue;
		switch ((yfMsg.head).event)
		{
		case  evInitial:  //线程初始化和复位。
			DBInitial(&yfMsg,(WORD)msg.lParam);
			break;
		case  evState:   //线程状态监测，连续三次心跳无反映，监控进程将终止，并重启该进程
			//SEND_MSG(evState,msgPtr->sender,NULL, 0);
			break;

		case evStateReport:
			DBStateReport(&yfMsg);
			break;

		case evTraceStatus:
			DBTraceStatus(&yfMsg);
			break;
			
		case evDBDInital:
			DBInitial(&yfMsg,(WORD)msg.lParam);
			break;
			
		case evDBDConnection:
			DBConnect(&yfMsg,(WORD)msg.lParam);
			break;
			
		case evDBDConnAvailCheck:
			DBAvailCheck(&yfMsg,(WORD)msg.lParam);
			break;
			
		case evDBDAsk://分布式查询
			DBDistrQuery(&yfMsg,(WORD)msg.lParam);
			break;
			
		default:
			break;
		}
	}  //end of while(1)
	dbi_DBExit();
	return 0;
}
unsigned  __stdcall /*void*/ DBIProc(LPVOID lpP)
{
	
	int mId;
	MESSAGE yfMsg;
	MSG msg;
	mId=*((UINT*)lpP);
	RegisterThread(mId);
	dbi_InitialConn(mId);	
	dbi_CreateConn(mId);
	
	while(bRun)
	{
//		GLM_WARN "DBIProc:mId1=%d",mId);
		memset(&msg,0,sizeof(MSG));
		GetMessage(&msg,NULL,0,0);
		memset(&yfMsg,0,sizeof(MESSAGE));
		if(msg.message != evThreadMessage)		
		{
			if(msg.message == evDBTest)
			{
				dbi_TestConn(msg.wParam);
		//		continue;
			}
			else if(msg.message == evDBQConnection )
			{
				dbi_ReleaseConn(msg.wParam);
				dbi_CreateConn(msg.wParam);
			}
		//	else
//			GLM_WARN "DBIProc:mId2=%d",mId);

			continue;
		}
		if(!COM_Read_Message((WORD)msg.wParam,&yfMsg))	
		{
//			GLM_WARN "DBIProc:mId2=%d",mId);
			continue;
		}
		switch ((yfMsg.head).event)
		{
		case evDBQInital: /*:数据库初始化DBQ*/
			dbi_InitialConn(mId);
			break;
			
		case evDBQConnection: /*建立数据库连接DBQ*/
			dbi_CreateConn(mId);
			break;
			
		case evDBQuery:/*:对DBQ进行数据库查询请求 结果返回:evDBQueryAck*/

		  //if(1 == DBConnState[*((UINT*)lpP))-MID_DBQ].iConnState)
			if(dbi_ConnAvailCheck(mId))
			{
//				GLM_WARN "DBIProc:mId3=%d",mId);
				if(mId>=MID_DBQ)
					DBConnState[mId - MID_DBQ].bIsBusy = TRUE;
//				GLM_WARN "DBIProc:mId4=%d",mId);
				dbi_ExecSql(mId,&yfMsg,(WORD)msg.lParam);
//				GLM_WARN "DBIProc:mId5=%d",mId);
				if(mId>=MID_DBQ)
					DBConnState[mId - MID_DBQ].bIsBusy = FALSE;
//				GLM_WARN "DBIProc:mId6=%d",mId);
			}
			else
				dbi_MsgAck_NoConn(mId,&yfMsg,(WORD)msg.lParam);
			break;
			
		default:
			break;
		}
//		GLM_WARN "DBIProc:mId2=%d",mId);

	}  //end of while(1)
	return 0;
}

unsigned  __stdcall /*void*/ DBMonitorProc(LPVOID lpP)
{
	int i=0;
	TID  tid ;
    Sleep(10000);
	while(bRun)
	{
		if(!GetTid(MID_DBQ+i,&tid))
			continue;
		else
			PostThreadMessage(tid.module,evDBTest,MID_DBQ+i,0);
		
		Sleep(10000);
		i = ++i % DBQUERY_THREAD_NUM;
	}
	return 0;
}

void DBStateReport(MESSAGE* pyfMsg)
{
//	TID* src = GetTID(MID_DBD);
	TID src;
	if(!GetTid(MID_DBD,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}

	pyfMsg->head.event = evStateReportAck;
	pyfMsg->head.receiver = pyfMsg->head.sender;
	pyfMsg->head.sender =  src;
	
	pyfMsg->head.len = sizeof(DWORD);
	SEND_MSG(evThreadMessage,pyfMsg->head.receiver.mId,(BYTE*)pyfMsg,sizeof(MESSAGE),0,FALSE,NULL,0);
}

void DBTraceStatus(MESSAGE* pyfMsg)
{
	if( pyfMsg->data[0] != 0)
	{
		g_bDbapTrace = TRUE;
	}
	else
	{
		g_bDbapTrace = FALSE;
	}
}