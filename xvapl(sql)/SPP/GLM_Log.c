#include <sys/types.h>
#include <sys/stat.h>
#include <stdlib.h>
#include <stdio.h>

#include "Commdll.h"
#include "WatchDefine.h"
#include "event.h"
#include "log.h"
#include "GLM_Log.h"


/******************************************************************************
*  Function Name:  WriteFilelog()
*  Description	:  write a log information to the log file. 
*  INPUT	:      error messages--errmsg,filename--file, and line count--line.
*  OUTPUT	:      if error return false,else return true. 
*  Note		:
*  Modify	:  
*******************************************************************************/

BOOL GLM_WriteFileLog(const char * logname, const char *cfile, const int line,const char *fmt,...)
{
	Log_Message log_msg;
	va_list	args;	
	SYSTEMTIME     Clock;
	char	 	fileName[64];
	char	    buf[1024];
	char drive[_MAX_DRIVE];
	char dir[_MAX_DIR];
	char fname[_MAX_FNAME];
	char ext[_MAX_EXT];
	char content[448];
	MESSAGE pMessage;
	TID pTid ;
	int len;
 	memset(buf,0,sizeof(buf));
	memset(content,0,sizeof(content));
	va_start(args,fmt);
	len=_vsnprintf(buf,sizeof(buf)-1,fmt,args);
	va_end(args);

	if (!logname) 
		return FALSE;
	memset(&log_msg,0,sizeof(Log_Message));
	log_msg.type = 1;
	log_msg.warnstep = 0;
	log_msg.lines = line;
	
	GetLocalTime(&Clock);

	sprintf(fileName,"%s%04d%02d%02d.log",logname,Clock.wYear,Clock.wMonth, Clock.wDay);
	 _splitpath( cfile, drive, dir, fname, ext ); 
	sprintf(log_msg.funname,"%8s%4s",fname, ext);
	strcpy(log_msg.content,buf);
	memset(&pMessage,0,sizeof(MESSAGE));
	if(GetTid(MID_DBD,&pTid))
	{
		pMessage.head.sender = pTid;
		pMessage.head.Source = pTid;
	}
	
	
	if(!GetTid(MID_LOG,&pTid))
		return FALSE;
	
	pMessage.head.receiver = pTid;
	
	pMessage.head.ptrAck=0;
	pMessage.head.ackLen=0;
	pMessage.head.reserve=0;
	pMessage.head.sync=FALSE;
	pMessage.head.event = evLogAsk;
	memcpy(pMessage.data,&log_msg,sizeof(Log_Message));
	pMessage.head.len = sizeof(Log_Message);
	SEND_MSG(evThreadMessage,MID_LOG,&pMessage,sizeof(MESSAGE),1,FALSE,NULL,0);
	return TRUE;
}
