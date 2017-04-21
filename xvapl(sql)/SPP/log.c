#include <stdio.h>
#include <string.h>
#include <windows.h>
#include <sys/stat.h>


#include "comm.h"
#include "Commdll.h"
#include "public_define.h"
#include "event.h"
#include "define.h"
#include "log.h"

#define     LOGNAME           "c:\\yfcomm\\ini\\Log.ini"

void logProc(PVOID lp)
{   

	MSG           umsg;
    ATT_FILE      all_file; 

	memset(&all_file,0,sizeof(ATT_FILE));          //将所有日志的属性先置为0


	all_file.nomal_file.index=1;                   //程序初始化将正常日志序号初始化为1
	all_file.Trace_file.index=1;                   //程序初始化将跟踪日志序号初始化为1

    if(!RegisterThread(MID_LOG))
		return ;

	CreateDirectory("C:\\yfcomm\\log\\",NULL);
	if(!SetCurrentDirectory("C:\\yfcomm\\log\\"))
		return ;
    if(!Find_MaxIndex(&all_file))
		return ;
    
	while( bRun)
	{   
		
		memset(&umsg,0,sizeof(MSG));
		GetMessage(&umsg,NULL,0,0);
		if(umsg.message!=evThreadMessage)
		{
			continue;
		}
		else
		{ 
   			WriteLogMsg((WORD)umsg.wParam,&all_file);
		}	
		
	}
    UnRegisterThread(MID_LOG);

	return ;
	
}
/*************************************************************************************

  功能：将读到的消息写入日志

  参数：传送的是共享内存中，消息的偏离量

**************************************************************************************/

int WriteLogMsg(WORD wparam,ATT_FILE *all_file)
{  
    SYSTEMTIME    clock;
    TID *         lptid;
	TID *         tptid; 
	MESSAGE       lmsg;
	MESSAGE       wmsg;
	SQLSrcMsg     strMsg;
	Log_Message   ulog;
	SQLDestMsg    sql_ack;
    char          q_clock[40];
     
	memset(&ulog,0,sizeof(Log_Message));
	GetLocalTime(&clock);
    memset(&sql_ack,0,sizeof(SQLDestMsg));
	if(!COM_Read_Message(wparam, &lmsg))
		return FALSE;
    if(lmsg.head.event==evDBQueryAck)
    {
		memcpy(&sql_ack,lmsg.data,sizeof(SQLDestMsg));
		if(sql_ack.iSqlExecRet!=1&&sql_ack.iSqlExecRet!=2&&sql_ack.iSqlExecRet!=3&&sql_ack.iSqlExecRet!=4)
		{
		   memcpy(ulog.content,sql_ack.sResults,sizeof(Log_Message)-1);
		   Judge_FileName("Normal",&all_file->nomal_file,ulog);
		}
		 return TRUE;
	}
    else if(lmsg.head.event==evLogAsk)
    {
       memcpy(&ulog,lmsg.data,sizeof(Log_Message)-1);
    
    }
    else
	{
		return FALSE;
	}

    if(lmsg.head.len==0)
		return FALSE;

	switch(ulog.type)
	{		
	case NORMAL:	
	
         Judge_FileName("Normal",&all_file->nomal_file,ulog);
	     break;

	case WARN:

       if(lmsg.head.sender.mId==MID_DBD)
	   {
           Write_DBAPWarnLog(ulog);
		   return TRUE;
	   }   
		lptid = GetTID(MID_LOG);
		tptid=GetTID(MID_DBD);
		memset(&wmsg,0,sizeof(MESSAGE));
		memset(q_clock,0,40);
	    sprintf(q_clock,"%04d%02d%02d %02d:%02d:%02d:%03d",clock.wYear,clock.wMonth,clock.wDay,clock.wHour,clock.wMinute,clock.wSecond,clock.wMilliseconds); 
		
		if(lptid != NULL)
		{	
			wmsg.head.sender=*lptid;
			wmsg.head.Source=*lptid;
		}
			
		if(tptid != NULL)
		{
			wmsg.head.receiver=*tptid;
		}
		
		wmsg.head.event = evDBDAsk;
		wmsg.head.len=sizeof(SQLSrcMsg);
		wmsg.head.reserve=0;
		wmsg.head.ptrAck=0;
		wmsg.head.ackLen=0;
		wmsg.head.sync=FALSE;
		strMsg.iRow = 0;

        Change_char_To(ulog.content);
		sprintf(strMsg.sSql,"INSERT INTO Warnlog (line,fileName,warn,msg,logTime) VALUES(%d,'%s',%d,'%s','%s')"	
			,ulog.lines,ulog.funname,ulog.warnstep,ulog.content,q_clock);
		memcpy(wmsg.data,&strMsg,sizeof(SQLSrcMsg));
		SEND_MSG(evThreadMessage,MID_DBD, &wmsg,sizeof(wmsg),1,FALSE,0,0);
  
		break;

	case TRACE:

        Judge_FileName("Trace",&all_file->Trace_file,ulog);
		break;

	default:

		break;
	}

	return TRUE;  
}

int  Judge_FileName(char* name,FileAtt*fileatt,Log_Message ulog)
{
     SYSTEMTIME    clock;
  	 char          buf[10];
     int            i;
     FILE*         fp=NULL;
	 int           filesize;
     char          temp[40];
	 char          logname[20];

     memset(buf,0,10);
 	 memset(logname,0,20);
  	 GetLocalTime(&clock);
     itoa(clock.wYear,buf,10); 
	 i=strlen(buf);                     //将年份取到 buf中去
  
	 sprintf(logname,"%s%c%c%02d%02d.log",name,buf[i-2],buf[i-1],clock.wMonth,clock.wDay);//将文件名写入到Logname中去  
        
	 sprintf(temp,"%s%c%c%02d%02d",name,buf[i-2],buf[i-1],clock.wMonth,clock.wDay);       //保存当前的时间           
		
		if(strcmp(fileatt->filename,logname)!=0)         //确定文件名不相同         
        {	
		   if(strlen(fileatt->filename)!=0)              //程序不是刚启动
		   {
              if(!strstr(fileatt->filename,temp))        //不是当天的文件
			  {
                  fileatt->index=1; //将序号置为1
				  memset(fileatt->filename,0,30);
				  strcpy(fileatt->filename,logname);
			  }
			}
		   else   //程序刚启动
		   {
	          strcpy(fileatt->filename,logname);
		   }
		 }
	
		fp=fopen(fileatt->filename,"a");
		if(fp==NULL)
		{
			return FALSE;
		}
         fseek(fp,0,SEEK_END); 
		 filesize=ftell(fp);
		 if(filesize>=MAXLINE)
		 {
            sprintf(logname,"%s%c%c%02d%02d%02d.log",name,buf[i-2],buf[i-1],clock.wMonth,clock.wDay,fileatt->index); 
		    fileatt->index+=1; 
			memset(fileatt->filename,0,30);
			strcpy(fileatt->filename,logname);
		    fclose(fp);
			fp=fopen(fileatt->filename,"a");
			if(fp==NULL)
				return FALSE;
		 }
		fprintf(fp,"[%02d:%02d:%02d.%03d] L[%04d] [%s] [%s]\n",clock.wHour,clock.wMinute,clock.wSecond,
			clock.wMilliseconds,ulog.lines,ulog.funname,ulog.content);
		fclose(fp);
  
	   return TRUE;
}

/*******************************************
  
功能:找寻当前文件正常日志和更新日志的最大序号
  
参数:传送文件结构 

********************************************/

int   Find_MaxIndex(ATT_FILE*all_file)

{  
   
	WIN32_FIND_DATA  FindFileData;
	HANDLE           hFind;
	char             filename[40];
    SYSTEMTIME       clock;
    char             temp[40];
	char             buf[10];
    int              index=0;


	GetLocalTime(&clock);
    memset(temp,0,40);
    memset(filename,0,40);
    memset(buf,0,10);

	sprintf(buf,"%04d",clock.wYear);
	
    sprintf(temp,"%c%c%02d%02d",buf[2],buf[3],clock.wMonth,clock.wDay);

  	hFind = FindFirstFile("*.* ",&FindFileData);
	
	if(hFind!=INVALID_HANDLE_VALUE)
	{
		do
		{
			if(!(FindFileData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) )
			{
				if(strstr(FindFileData.cFileName,temp)!=0)
				{
					    
					    strcpy(filename,FindFileData.cFileName);
                     
                         if(strstr(filename,"Normal")!=0)
						 {
							 if(strlen(filename)>16)
							 {
						         memset(buf,0,10);		 
								 memcpy(buf,filename+12,2);
							     index=atoi(buf);
								 if(all_file->nomal_file.index<index)
								 {
                                    all_file->nomal_file.index=index;
								 }
							 }
							
						 }
                         if(strstr(filename,"Trace")!=0)
						 {
							 if(strlen(filename)>15)
							 {
								 memset(buf,0,10);		 
								 memcpy(buf,filename+11,2);
							     index=atoi(buf);
                                if(all_file->Trace_file.index<index)
								{
                                   all_file->Trace_file.index=index;
								}
							 }	
						}	 
				}     
			}
                    
		}while(FindNextFile(hFind,&FindFileData));
					
	   FindClose(hFind);	
	}
    
	return TRUE;

}



/**************************************************
 
  功能:接收来自DBAP的消息,将消息内容写入到文本中
   
  参数: DPAP发送的消息内容

***************************************************/

int  Write_DBAPWarnLog(Log_Message ulog)
{
   FILE        *fp=NULL;
   char        filename[50];
   int         index=0; 
   SYSTEMTIME  clock;
   int         filesize;

   GetLocalTime(&clock);
   memset(filename,0,50);
   sprintf(filename,"DbErr%04d%02d%02d%02d.log",clock.wYear,clock.wMonth,clock.wDay,index);
   
   fp=fopen(filename,"a");
   if(fp==NULL)
	   return FALSE;   
   fseek(fp,0,SEEK_END);
   filesize=ftell(fp);
   if(filesize>MAXLINE)
   {  
	  fclose(fp);
      index++;
	  if(index>=100)
		  index=0;
      sprintf(filename,"DbErr%04d%02d%02d%02d.log",clock.wYear,clock.wMonth,clock.wDay,index);
      fp=fopen(filename,"a");
	  if(fp==NULL)
		  return FALSE;
   }
   
  	fprintf(fp,"[%02d:%02d:%02d.%03d] L[%04d] [%s] [%s]\n",clock.wHour,clock.wMinute,clock.wSecond,
			clock.wMilliseconds,ulog.lines,ulog.funname,ulog.content);
	fclose(fp);

    return TRUE;
}

int  Change_char_To(char* buf)
{
  char*p=NULL;

  while(1)
  {
    p=strpbrk(buf,"'");
    if(p==NULL)
	  return 0;
    *p='"';
  }
   return 1;
}
