#include "yftrace.h"
#include "GLM_Log.h"
#include "DBI.h"



DBI_CONN dbi_Conn[DBQUERY_THREAD_NUM];
extern DBCONNSTATE DBConnState[];
BYTE g_DBIP[20];

/**************************************************************************
函 数 名: dbi_InitialDB
函数功能: 初始化数据库,初始化DBI用到的全局数据结构
参数定义: 无
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL dbi_InitialDB()
{
	static BOOL bHaveInit = FALSE; //初始化过程是否执行过
	UINT i = 0;
	if(!bHaveInit)
	{
		if(NULL == dbinit())
		{
			//dbi_WriteLogFile("dbi_InitialDB failed!");
			GLM_WARN "dbi_InitialDB failed!");
			return FALSE;
		}
		bHaveInit = TRUE;
	}
	
	for(i = 0;i < DBQUERY_THREAD_NUM; i++)
	{
		dbi_Conn[i].mId = i + MID_DBQ;
		dbi_Conn[i].pDBConn = NULL;  //数据库连接初始化为空
	}
	return TRUE;
}

/**************************************************************************
函数名：dbi_InitialConn
函数功能: 初始化数据库连接与此线程的相关数据.
参数定义: mId:模块号
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 与数据库相关的api只调用一次.
**************************************************************************/
BOOL dbi_InitialConn(UINT mId)
{
	static BOOL bInited = FALSE;
	if(!bInited)
	{
	//	dbprocerrhandle (dbi_Conn[mId-MID_DBQ].pDBConn, err_handler);
	//	dbprocmsghandle (dbi_Conn[mId-MID_DBQ].pDBConn, msg_handler);
		dberrhandle(err_handler);
		dbmsghandle(msg_handler);
		bInited = TRUE;
	}	
	return TRUE;
}

/**************************************************************************
函 数 名: dbi_CreateConn
函数功能: 给mId模块创建数据库连接,如果此连接已建立,则不重建.
参数定义: mId
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL dbi_CreateConn(UINT mId)
{
	PLOGINREC   login;	
//	GLM_WARN "dbi_CreateConn:mId1=%d",mId);
	if(mId<MID_DBQ)
	{
		GLM_WARN "dbi_CreateConn:mId=%d<%d",mId,MID_DBQ);
		return FALSE;
	}
	login = dblogin();
	DBSETLUSER(login, g_sDBUser);
	DBSETLPWD(login, g_sDBPwd);
	//		DBSETLSECURE (login);//以windows验证身份登录
	DBSETLAPP(login, "DBI");
	DBSETLTIME (login,DBQ_OPEN_TIMEOUT);	//设定登陆超时上限
	dbsettime(DBQ_SQL_TIMEOUT);				//设定查询超时		
	dbi_Conn[mId-MID_DBQ].pDBConn = dbopen(login,g_DBIP);
	
	if(dbisavail(dbi_Conn[mId-MID_DBQ].pDBConn))
	{
		DBConnState[mId-MID_DBQ].iConnState = 1;
		YF_LOG_SPP "sql server db logon success");
//			GLM_WARN "dbi_CreateConn:mId2=%d",mId);
		return TRUE;
	}
	else
	{
		DBConnState[mId-MID_DBQ].iConnState = 0;
		GLM_WARN "sql server db logon failed");
//			GLM_WARN "dbi_CreateConn:mId2=%d",mId);
		return FALSE;
	}
}

/**************************************************************************
函 数 名: dbi_ReleaseConn
函数功能: 释放模块号所保持的数据库连接.
参数定义: mId
返回定义: 成功返回TRUE,有异常返回FALSE
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL dbi_ReleaseConn(UINT mId)
{
//		GLM_WARN "dbi_ReleaseConn:mId1=%d",mId);
	if(mId<MID_DBQ)
	{
		GLM_WARN "dbi_ReleaseConn:mId=%d<%d",mId,MID_DBQ);
		return FALSE;
	}
	dbclose(dbi_Conn[mId-MID_DBQ].pDBConn);
//	GLM_WARN "dbi_ReleaseConn:mId2=%d",mId);
	return TRUE;
}

/**************************************************************************
函 数 名: dbi_ConnAvailCheck
函数功能: 判断模块所在的数据库连接是否可用
参数定义: mId
返回定义: 连接可用返回TRUE,连接不可用返回FALSE
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL dbi_ConnAvailCheck(UINT mId)
{
	BOOL bOK;
//	GLM_WARN "dbi_ConnAvailCheck:mId1=%d",mId);
	if(mId<MID_DBQ)
	{
		GLM_WARN "dbi_ConnAvailCheck:mId=%d<%d",mId,MID_DBQ);
		return FALSE;
	}
	if(1 == DBConnState[mId-MID_DBQ].iConnState)
		bOK=TRUE;
	else
		bOK=FALSE;
//	GLM_WARN "dbi_ConnAvailCheck:mId2=%d",mId);
	return bOK;
}
int test_handler(PDBPROCESS dbproc, DBINT msgno, INT msgstate,
				 INT severity, LPCSTR msgtext, LPCSTR server,
				 LPCSTR procedure, DBUSMALLINT line)
{
//	char buf[1000];
//	memset(buf,0,1000);
//	if(msgno != 5701)
	{
//		sprintf (buf,"SQL Server Message %ld: %s\n", msgno, msgtext);
//		errid=msgno;
		GLM_WARN "SQL Server Message testhandle %ld: %s", msgno, msgtext);
//		dbi_WriteLogFile(buf);
	}
	return (0);
}

/**************************************************************************
函 数 名: ExecSql
函数功能: 执行查询,处理查询后得到的数据.
参数定义: mId,msg,wNum
返回定义: 成功返回TRUE,失败返回FALSE.
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
BOOL dbi_ExecSql(UINT mId, MESSAGE* msg,WORD wNum)
{
	RETCODE retcode;
	UINT i = 0,totallen = 0,CurRow = 0,DstRow = 0;
	INT numcols = 0,j = 0;
	char **p = NULL,sBuf[1000];
	BOOL bFindDstRow = FALSE;
	TID pTid;
	MESSAGE  pMsg;
//	GLM_WARN "dbi_ExecSql:mId1=%d",mId);
	memcpy(&pMsg,msg,sizeof(MESSAGE));
	if(mId<MID_DBQ)
	{
		GLM_WARN "dbi_ExecSql:mId=%d<%d",mId,MID_DBQ);
		return FALSE;
	}
	if(!GetTid(mId,&pTid))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return FALSE;
	}
	dbcancel(dbi_Conn[mId-MID_DBQ].pDBConn);
	dbcmd(dbi_Conn[mId-MID_DBQ].pDBConn,((SQLSrcMsg*)(pMsg.data))->sSql); 
	dbsetuserdata(dbi_Conn[mId-MID_DBQ].pDBConn,test_handler);

	if(SUCCEED == dbsqlexec(dbi_Conn[mId-MID_DBQ].pDBConn))//sql 成功执行
	{
		if((errid==1205)||(errid==3621))
		{
			GLM_WARN "SQL Server Message 死锁: mid=%d,%s",mId,((SQLSrcMsg*)(pMsg.data))->sSql);
		//	memset(msg->data,0,msg->head.len);
			dbi_MsgAck_ExecFailed(mId,&pMsg,wNum);

			PostThreadMessage(pTid.module,evDBQConnection,mId,0);
//			GLM_WARN "dbi_ExecSql:mId6=%d",mId);
			errid=0;
			return FALSE;
			
		}
		for(i = 0;(retcode = dbresults(dbi_Conn[mId-MID_DBQ].pDBConn)) != NO_MORE_RESULTS;i++)
		{  //返回的表如果有多个,只处理第一个表,剩下的抛弃处理
			if((retcode == SUCCEED) && (i == 0)) //retcode为SUCCEED且i为0才能处理,即处理第一个表
			{
				numcols = 0; CurRow = 0; DstRow = 0;
				if(SUCCEED != dbrows(dbi_Conn[mId-MID_DBQ].pDBConn))//无表返回
				{
					dbi_MsgAck_NoneTableRet(mId,&pMsg,wNum);
//					GLM_WARN "dbi_ExecSql:mId5=%d",mId);
					return TRUE;
				}
				numcols = dbnumcols(dbi_Conn[mId-MID_DBQ].pDBConn);
				p = (char**)malloc(sizeof(char*) * numcols);//以动态数组形式为每个列申请一个指针
				if(p == NULL)
				{
					dbi_MsgAck_MemLack(mId,&pMsg,wNum);
//					GLM_WARN "dbi_ExecSql:mId4=%d",mId);
					return TRUE; 
				}
				else
				{
					for(j = 0;(j < numcols); j++)//为每列申请内存,将列数据与之绑定
					{
						p[j] = NULL;
						p[j] = (char*)malloc(300);
						if(p[j] == NULL)
						{
							//dbi_WriteLogFile("malloc memory failed");
							GLM_WARN "malloc memory failed");
							continue;
						}
						memset(p[j],0,300);
						dbbind(dbi_Conn[mId-MID_DBQ].pDBConn,j+1,NTBSTRINGBIND,0,p[j]);
					}
				}
				for(CurRow = 0,DstRow = 0;dbnextrow(dbi_Conn[mId-MID_DBQ].pDBConn) != NO_MORE_ROWS;CurRow++)
				{////处理表中的指定行
					if( (CurRow+1) == ((SQLSrcMsg*)(pMsg.data))->iRow )//找到指定行并且内存申请无错
					{
						memset(sBuf,0,1000);//将结果值全部赋给缓冲区
						for(j = 0; j < numcols; j++)
						{
							strncat(sBuf,p[j],300);
							strcat(sBuf,"\t");
						}
						DstRow = CurRow+1;	
					}
				}
				if(CurRow == 0)//没有表返回
					dbi_MsgAck_NoneTableRet(mId,&pMsg,wNum);
				else//有表返回
				{
					if(DstRow == 0)				
						dbi_MsgAck_UnFindDstRow(mId,&pMsg,wNum,CurRow);
					else
						dbi_MsgAck_FindDstRow(mId,&pMsg,wNum,sBuf,DstRow,CurRow);
				}
				
				dbi_ReleaseMemory(p,numcols);
			}
			else//抛弃处理
				return FALSE;
		}//end of  for
		if((retcode != NO_MORE_RESULTS)  && (retcode != SUCCEED))//报告失败
		{
			//memset(msg->data,0,msg->head.len);
			dbi_MsgAck_ExecFailed(mId,&pMsg,wNum);
//			GLM_WARN "dbi_ExecSql:mId2=%d",mId);
			return TRUE;
		}
	}
	else//sql 执行失败:可能是SQL语法错误，也可能是数据库连接失效,测试连接有效性
	{
	//	memset(msg->data,0,msg->head.len);
		dbi_MsgAck_ExecFailed(mId,&pMsg,wNum);
		PostThreadMessage(pTid.module,evDBQConnection,mId,0);
	}
//	GLM_WARN "dbi_ExecSql:mId3=%d",mId);
	return TRUE;
}

void dbi_ReleaseMemory(char** p,int numcols)
{
	INT j;
	for(j = 0; j < numcols; j++)//release the pointers
	{
		free(p[j]);
		p[j] = NULL;
	}
	free(p);
	p = NULL;
	return;
}
/**************************************************************************
函 数 名: 
函数功能: 
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_DBExit()
{
	dbexit();//Closes and frees all DBPROCESS structures created
	dbwinexit();//release DB-Library
	return;
}

/**************************************************************************
函 数 名: 
函数功能: 
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_WriteLogFile(char* str)
{
	char sFilePath[200];
	FILE* file = NULL;
	SYSTEMTIME t;
	
	memset(sFilePath,0,200);
	if(str == NULL || strlen(str) >= 150)
		return;
	
	GetCurrentDirectory(200,sFilePath);
	strcat(sFilePath,"\\DBILog.log");
	GetLocalTime(&t);
	if( (file = fopen(sFilePath,"a")) != NULL)
	{
		fprintf(file,"%d/%d/%d,%d:%d:%d:%s\n",t.wYear,t.wMonth,t.wDay,t.wHour,t.wMinute,t.wSecond,str);
		fclose(file);
	}
	return;
}

/**************************************************************************
函 数 名: 
函数功能: 
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
int err_handler(PDBPROCESS dbproc, INT severity,
				INT dberr, INT oserr, LPCSTR dberrstr, LPCSTR oserrstr)
{	
//	char buf[1000];
//	memset(buf,0,1000);
	if (oserr != DBNOERR)
	{
//		sprintf (buf,"Operating System Error %i: %s", oserr, oserrstr);
//		dbi_WriteLogFile(buf);
		GLM_WARN "Operating System Error %i: %s",oserr, oserrstr);
	}
	return (INT_CANCEL);
}

/**************************************************************************
函 数 名: 
函数功能: 
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
int msg_handler(PDBPROCESS dbproc, DBINT msgno, INT msgstate,
				 INT severity, LPCSTR msgtext, LPCSTR server,
				 LPCSTR procedure, DBUSMALLINT line)
{
//	char buf[1000];
//	memset(buf,0,1000);
	if(msgno != 5701)
	{
//		sprintf (buf,"SQL Server Message %ld: %s\n", msgno, msgtext);
		errid=msgno;
		GLM_WARN "SQL Server Message %ld: %s", msgno, msgtext);
//		dbi_WriteLogFile(buf);
	}
	return (0);
}

/**************************************************************************
// 函  数：TransformIp(char* addr,DWORD *pdwIP,bool flag)
// 参  数：char* addr,DWORD *pdwIP,bool flag
// 返回值：如果成功返回true，不成功，返回false
// 描  述：当dwToStr为真时，把pdwIP转换为字符串，当为假时字符串转化为DWORD型
// 注  意：Declared in Winsock2.h,Use Ws2_32.lib
//#include <Winsock2.h>
//#pragma comment(lib,"Ws2_32.lib")
**************************************************************************/
BOOL TransformIP(char *addr,DWORD *pdwIP,BOOL dwToStr)
{
	char *strIP=NULL;
	struct in_addr inaddr;
	
	if(dwToStr)
	{
		if(pdwIP == NULL)
			return FALSE;
		inaddr.s_addr = *pdwIP;
		strIP=inet_ntoa(inaddr);
		strcpy(addr,strIP);
		strIP=NULL;
		return TRUE;	
	}
	else
	{
		if( addr == NULL)
			return FALSE;
		*pdwIP=inet_addr(addr);
		return TRUE;
	}
}

/**************************************************************************
函 数 名: 
函数功能: 数据库无法连接;
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_NoConn(UINT mId, MESSAGE* pyfMsg,WORD wNum)
{
	TID src;
//		GLM_WARN "dbi_MsgAck_NoConn:mId1=%d",mId);
	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}
	pyfMsg->head.event = evDBQueryAck;
	pyfMsg->head.receiver = pyfMsg->head.sender;
	pyfMsg->head.sender = src;

	((SQLDestMsg*)(pyfMsg->data))->iSqlExecRet = 8;
	((SQLDestMsg*)(pyfMsg->data))->iRow =0;
	((SQLDestMsg*)(pyfMsg->data))->iTotalNumsOfRows = 0;
	memset(((SQLDestMsg*)(pyfMsg->data))->sResults,0,MAX_LENGTH-3*sizeof(UINT));
	pyfMsg->head.len = sizeof(SQLDestMsg);
//	if(g_bDbapTrace)
		YF_TRACE_DBAP -1,"dbi_MsgAck_NoConn","sqlDest:execret:8,retrow:0,totalrow:0,result:null");
	SEND_MSG(evThreadMessage,pyfMsg->head.receiver.mId,pyfMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);	
//	GLM_WARN "dbi_MsgAck_NoConn:mId2=%d",mId);

	return ;
}

/**************************************************************************
函 数 名: 
函数功能: SQL执行失败(语法错误)
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_ExecFailed(UINT mId, MESSAGE* pyfMsg,WORD wNum)
{
	//TID* src = GetTID(mId);
	char buf[CHAR_NUMBER];
	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));

//		GLM_WARN "dbi_MsgAck_ExecFailed:mId1=%d",mId);

	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}
	
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender = src;
	
	memset(buf,0,CHAR_NUMBER);
	strcpy(buf,((SQLSrcMsg*)(pMsg.data))->sSql);
	((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 5;
	((SQLDestMsg*)(pMsg.data))->iRow =0;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = 0;
	memset(((SQLDestMsg*)(pMsg.data))->sResults,0,sizeof(((SQLDestMsg*)(pMsg.data))->sResults ) );
	strcpy(((SQLDestMsg*)(pMsg.data))->sResults,buf);

	pMsg.head.len = sizeof(SQLDestMsg);	

	YF_TRACE_DBAP -1,"dbi_MsgAck_ExecFailed","sqlDest:execret:5,retrow:0,totalrow:0,result:%s",buf);
		
	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//		GLM_WARN "dbi_MsgAck_ExecFailed:mId2=%d",mId);
	return ;
}

/**************************************************************************
函 数 名: 
函数功能: 申请内存失败时的处理
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_MemLack(UINT mId, MESSAGE* pyfMsg,WORD wNum)
{
//	TID* src = GetTID(mId);
	char buf[CHAR_NUMBER];
	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));

//	GLM_WARN "dbi_MsgAck_MemLack:mId1=%d",mId);
	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}
	
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender = src;
	
	memset(buf,0,CHAR_NUMBER);
	strcpy(buf,((SQLSrcMsg*)(pMsg.data))->sSql);
	memset(((SQLDestMsg*)(pMsg.data))->sResults,0,sizeof(((SQLDestMsg*)(pMsg.data))->sResults ) );
	strcpy(((SQLDestMsg*)(pMsg.data))->sResults,buf);
	((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 7;
	((SQLDestMsg*)(pMsg.data))->iRow =0;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = 0;

	pMsg.head.len = sizeof(SQLDestMsg);
//	if(g_bDbapTrace)
		YF_TRACE_DBAP -1,"dbi_MsgAck_MemLack","sqlDest:execret:7,retrow:0,totalrow:0,result:%s",buf);
	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//	GLM_WARN "dbi_MsgAck_MemLack:mId2=%d",mId);
	return;
}

/**************************************************************************
函 数 名: 
函数功能: 数据库返回的长度大于存储区长度
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_LenLack(UINT mId, MESSAGE* pyfMsg,WORD wNum)
{
//	TID* src = GetTID(mId);
	char buf[CHAR_NUMBER];

	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));
//	GLM_WARN "dbi_MsgAck_LenLack:mId1=%d",mId);
	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender =  src;

	memset(buf,0,CHAR_NUMBER);
	strcpy(buf,((SQLSrcMsg*)(pMsg.data))->sSql);
	if((((SQLSrcMsg*)(pMsg.data))->iRow == 0))//不需要返回
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 1;
	}
	else
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 6;
	}
	memset(((SQLDestMsg*)(pMsg.data))->sResults,0,sizeof(((SQLDestMsg*)(pMsg.data))->sResults ) );
	strcpy(((SQLDestMsg*)(pMsg.data))->sResults,buf);
	((SQLDestMsg*)(pMsg.data))->iRow =0;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = 0;
	pMsg.head.len = sizeof(SQLDestMsg);	
//	if(g_bDbapTrace)
		YF_TRACE_DBAP -1,"dbi_MsgAck_LenLack","sqlDest:execret:6,retrow:0,totalrow:0,result:%s",buf);
	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//	GLM_WARN "dbi_MsgAck_LenLack:mId2=%d",mId);
	return;
}

/**************************************************************************
函 数 名: 
函数功能: 数据库没有结果记录可返回
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_NoneTableRet(UINT mId, MESSAGE* pyfMsg,WORD wNum)//没有结果记录可返回
{
//	TID* src = GetTID(mId);
	char buf[CHAR_NUMBER];

	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));
//	GLM_WARN "dbi_MsgAck_NoneTableRet:mId1=%d",mId);

	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender = src;
	
	memset(buf,0,CHAR_NUMBER);
	strcpy(buf,((SQLSrcMsg*)(pMsg.data))->sSql);
	if((((SQLSrcMsg*)(pMsg.data))->iRow == 0))//不需要返回
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 1;
	}
	else
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 4;
	}
	memset(((SQLDestMsg*)(pMsg.data))->sResults,0,sizeof(((SQLDestMsg*)(pMsg.data))->sResults ) );
	strcpy(((SQLDestMsg*)(pMsg.data))->sResults,buf);
	((SQLDestMsg*)(pMsg.data))->iRow =0;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = 0;

	pMsg.head.len = sizeof(SQLDestMsg);
//	if(g_bDbapTrace)
	YF_TRACE_DBAP -1,"dbi_MsgAck_NoneTableRet","sqlDest:execret:%d,retrow:0,totalrow:0,result:%s",((SQLDestMsg*)(pMsg.data))->iSqlExecRet,buf);
	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//	GLM_WARN "dbi_MsgAck_NoneTableRet:mId2=%d",mId);
	return;
}

/**************************************************************************
函 数 名: 
函数功能: 未找到指定要返回的行
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_UnFindDstRow(UINT mId, MESSAGE* pyfMsg,WORD wNum,UINT TotalRow)//未找到指定要返回的行
{
//	TID* src = GetTID(mId);
	char buf[CHAR_NUMBER];
	
	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));
//	GLM_WARN "dbi_MsgAck_UnFindDstRow:mId1=%d",mId);
	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL");
		return;
	}
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender = src;

	memset(buf,0,CHAR_NUMBER);
	strcpy(buf,((SQLSrcMsg*)(pMsg.data))->sSql);
	if((((SQLSrcMsg*)(pMsg.data))->iRow == 0))//不需要返回
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 1;
	}
	else
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 3;
	}
	memset(((SQLDestMsg*)(pMsg.data))->sResults,0,sizeof(((SQLDestMsg*)(pMsg.data))->sResults ) );
	strcpy(((SQLDestMsg*)(pMsg.data))->sResults,buf);
	((SQLDestMsg*)(pMsg.data))->iRow =0;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = 0;
	pMsg.head.len = sizeof(SQLDestMsg);	
//	if(g_bDbapTrace)
		YF_TRACE_DBAP -1,"dbi_MsgAck_UnFindDstRow","sqlDest:execret:%d,retrow:0,totalrow:0,result:%s",((SQLDestMsg*)(pMsg.data))->iSqlExecRet,buf);

	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//	GLM_WARN "dbi_MsgAck_UnFindDstRow:mId2=%d",mId);
	return;
}

/**************************************************************************
函 数 名: 
函数功能: 找到指定行,正常返回,长度超限另作处理.
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_FindDstRow(UINT mId, MESSAGE* pyfMsg,WORD wNum,char *sBuf,UINT DstRow,UINT TotalRow)
{
	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));
//	GLM_WARN "dbi_MsgAck_FindDstRow:mId1=%d",mId);
	
	if(strlen(sBuf) > (MAX_LENGTH - 3*sizeof(UINT)))//判断结果集的长度是否超限
	{
		dbi_MsgAck_LenLack(mId, &pMsg,wNum);
		return;
	}
//	TID src;
	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL,mId2=%d",mId);
		return;
	}
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender =  src;
	
	if((((SQLSrcMsg*)(pMsg.data))->iRow == 0))//不需要返回
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 1;
	}
	else
	{
		((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 2;
	}
	
	((SQLDestMsg*)(pMsg.data))->iRow = DstRow;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = TotalRow;
	strncpy(((SQLDestMsg*)(pMsg.data))->sResults,sBuf,(MAX_LENGTH - 3*sizeof(UINT)));
		
	pMsg.head.len = sizeof(SQLDestMsg) ;	
//	if(g_bDbapTrace)
		YF_TRACE_DBAP -1, "dbi_MsgAck_FindDstRow","sqlDest:execret:%d,retrow:%d,totalrow:%d,result:%s",((SQLDestMsg*)(pMsg.data))->iSqlExecRet,DstRow,TotalRow,sBuf);
	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//	GLM_WARN "dbi_MsgAck_FindDstRow:mId2=%d",mId);
	return;
}

/**************************************************************************
函 数 名: 
函数功能: SQL执行成功(不要求返回结果集)
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_MsgAck_NeednotRet(UINT mId, MESSAGE* pyfMsg,WORD wNum)//不用返回结果集
{
//	TID* src = GetTID(mId);
	char buf[CHAR_NUMBER];

	MESSAGE pMsg;
		TID src;
	memcpy(&pMsg,pyfMsg,sizeof(MESSAGE));
//	GLM_WARN "dbi_MsgAck_NeednotRet:mId1=%d",mId);

	if(!GetTid(mId,&src))
	{
		GLM_WARN "dbi_MsgAck_NoConn:GetTID is NULL,mId2=%d",mId);
		return;
	}
	pMsg.head.event = evDBQueryAck;
	pMsg.head.receiver = pMsg.head.sender;
	pMsg.head.sender =  src;
	
	memset(buf,0,CHAR_NUMBER);
	strcpy(buf,((SQLSrcMsg*)(pMsg.data))->sSql);
	memset(((SQLDestMsg*)(pMsg.data))->sResults,0,sizeof(((SQLDestMsg*)(pMsg.data))->sResults ) );
	strcpy(((SQLDestMsg*)(pMsg.data))->sResults,buf);
	((SQLDestMsg*)(pMsg.data))->iSqlExecRet = 1;
	((SQLDestMsg*)(pMsg.data))->iRow =0;
	((SQLDestMsg*)(pMsg.data))->iTotalNumsOfRows = 0;

	pMsg.head.len = sizeof(SQLDestMsg);
	YF_TRACE_DBAP -1,"dbi_MsgAck_NeednotRet","sqlDest:execret:%d,retrow:0,totalrow:0,result:%s",((SQLDestMsg*)(pMsg.data))->iSqlExecRet,buf);
	SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,&pMsg,sizeof(MESSAGE),wNum,FALSE,NULL,0);
//	GLM_WARN "dbi_MsgAck_NeednotRet:mId2=%d",mId);
	return;
}
BOOL dbi_TestExeSql(UINT mId)
{
	char sSql[100];
	BOOL bOK;
	RETCODE retCode = FAIL;
//	GLM_WARN "dbi_TestExeSql:mId1=%d",mId);

	if(mId<MID_DBQ)
	{
		GLM_WARN "dbi_TestExeSql:mId=%d<%d",mId,MID_DBQ);
		return FALSE;
	}
	memset(sSql,0,100);
	strcpy(sSql,DBCONN_TEST_SQL);
	
	
	dbcancel(dbi_Conn[mId-MID_DBQ].pDBConn);
	dbcmd(dbi_Conn[mId-MID_DBQ].pDBConn,sSql); 
	retCode = dbsqlexec(dbi_Conn[mId-MID_DBQ].pDBConn);
//	YF_TRACE -1,"dbi_TestExeSql","return:%d",retCode);
	if(retCode == SUCCEED)
		bOK=TRUE;
	else 
		bOK=FALSE;
//	GLM_WARN "dbi_TestExeSql:mId2=%d",mId);
	return bOK;
//	retCode = FAIL;
}

/**************************************************************************
函 数 名: dbi_TestConn
函数功能: 测试连接状态，据此判断是否重建数据库连接
参数定义: 
返回定义: 
创建时间: 2008-6-18 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_TestConn(UINT mId)
{
//	GLM_WARN "dbi_TestConn:mId1=%d",mId);
	if(mId<MID_DBQ)
	{
		GLM_WARN "dbi_TestConn:mId=%d<%d",mId,MID_DBQ);
		return ;
	}
	DBConnState[mId - MID_DBQ].bIsBusy = TRUE;
	if(!dbi_TestExeSql(mId))
	{
		YF_LOG_SPP "DBMonitorProc:DB connection reopen:%d",mId);
		dbi_ReleaseConn(mId);
		dbi_CreateConn(mId);
	}
	DBConnState[mId - MID_DBQ].bIsBusy = FALSE;
//	GLM_WARN "dbi_TestConn:mId2=%d",mId);

	return;
}

/**************************************************************************
函 数 名: dbi_GetDBIni
函数功能: 获得数据库用户名密码
参数定义: 
返回定义: 
创建时间: 2009-3-10 
函数作者: 郭立明
注意事项: 
**************************************************************************/
void dbi_GetDBIni(char * DstUser,char * DstPwd)
{
	PDBPROCESS  dbproc = NULL;    
    PLOGINREC   login = NULL;    	 
	char      name[100];
    char      pwd[100];
	RETCODE ret = -3;
	UINT i = 0,j=0;
//	char sIP[20];
//	memset(sIP,0,20);
	memset(g_DBIP,0,sizeof(g_DBIP));
	GetPrivateProfileString("DATABASE","DBIP","127.0.0.1",g_DBIP,sizeof(g_DBIP),DBINIFILEPATH);
//	TransformIP(sIP,&s_database.ComputerIp,TRUE);
	
	if(DstUser == NULL || DstPwd == NULL)
	{
		GLM_WARN "dbi_GetDBIni failed:DstUser or DstPwd is null");
//		exit(1);
	}
	dbi_InitialDB();
	dbi_InitialConn(0);
    login = dblogin ();
    DBSETLUSER (login, DBUSER);
    DBSETLPWD (login, DBPWD);
    DBSETLAPP (login, "DBI");
    dbproc = dbopen (login,g_DBIP);
	dbcmd (dbproc,DBINISQL_SQLSERVER);
	memset(name,0,100);
	memset(pwd,0,100);
    ret = dbsqlexec (dbproc) ;
	if(ret != SUCCEED)
	{
		GLM_WARN "dbi_GetDBIni failed:dbsqlexec failed");
//		exit(1);
	}
	if (dbresults (dbproc) == SUCCEED)
	{
		dbbind (dbproc, 1, NTBSTRINGBIND, 0, name);
		dbbind (dbproc, 2, NTBSTRINGBIND, 0, pwd);
		
		while(dbnextrow (dbproc) != NO_MORE_ROWS)
		{
		//	sprintf("user:%s,pwd:%s", name,pwd);
			break;
		}
	}
	dbcancel(dbproc);
	for(i = 0,j = 0; i < strlen(name);i=i+2,j++)
	{
		if(name[i] != '\0')
		{
			DstUser[j] = name[i];
		}
		else
			break;
	}
	for(i = 0,j = 0; i< strlen(pwd);i=i+2,j++)
	{
		if(pwd[i] != '\0')
		{
			DstPwd[j] = pwd[i];
		}
		else
			break;
	}
	dbclose(dbproc);
//	return TRUE;
}