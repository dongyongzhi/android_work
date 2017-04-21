
#include "cdr.h" 



unsigned  _stdcall Cdr_proc(LPVOID lp)
{
	MSG          umsg;
    MODE_CDR     cmode;
    CDR_FILE     cdrfile;
   	SYSTEMTIME   clock; 
	char         filename[40];

    memset(filename,0,40);
	GetLocalTime(&clock);
    memset(&cdrfile,0,sizeof(CDR_FILE));
    sprintf(cdrfile.temp,"%04d%02d%02d",clock.wYear,clock.wMonth,clock.wDay);    //取程序启动时的日期
    time(&cdrfile.stime);                                                        //获取程序运行时的秒数  
	cdrfile.bkey=-1;                                                             //用于业务键比较            
	
	if(!RegisterThread(MID_CDR))
		return FALSE;
    CreateDirectory(FPATH,NULL);
	
	choose_mode(&cmode);                                                //读配置文件选择方式
    cdrfile.index=cmode.index;	
   

	while(bRun)
	{   
		memset(&umsg,0,sizeof(MSG));
		GetMessage(&umsg,NULL,0,0);                                 //等待消息
		if(umsg.message!=evThreadMessage)                           //判断消息号，是否正确
		{
			continue;
		}
		else
		{   
			judge_filename((WORD)umsg.wParam,(WORD)umsg.lParam,&cdrfile,&cmode);
		}
	}
	fclose(cdrfile.fp);

	UnRegisterThread(MID_CDR);

	return 0;
}

/******************************************************************************
参数：接收消息的偏离量

功能：判断文件名是否为当天或者判断文件名的业务键是否一致，来决定重新打开文件名
*******************************************************************************/

int judge_filename( WORD wapram,WORD lparam ,CDR_FILE*cdrfile,MODE_CDR*cmode)
{
	MESSAGE     cmsg;
	CDR         cdr;
	char        temp[40];
	SYSTEMTIME  clock; 
	char        buf[10];
	CDR_TRACE   cdrtrace;
    BOOL        label=0;   //标识是否打开 跟踪标识

	memset(temp,0,40);
	memset(&cdr,0,sizeof(CDR));
	GetLocalTime(&clock);
	if(!COM_Read_Message(wapram,&cmsg))
		return FALSE; 
	if(cmsg.head.len==0)
		   return FALSE;
   switch(cmsg.head.event)
   {
   
   case evASK_CDR :
	  
	memcpy(&cdr,cmsg.data,sizeof(CDR));  
	sprintf(temp,"%04d%02d%02d",clock.wYear,clock.wMonth,clock.wDay);    //取现在的时间

	if(cdrfile->bkey!=cdr.bkey||strcmp(cdrfile->temp,temp)!=0)
	{  
		cdrfile->bkey=cdr.bkey;                                          //将当前业务键保存                  
		if(strcmp(cdrfile->temp,temp)!=0)                                //不是当天
		{   
			memset(cdrfile->temp,0,40);
			strcpy(cdrfile->temp,temp);                                   //保存当前的日期
			cdrfile->index=0;                                             //不同天序号重新编排         
		}
		memset(cdrfile->filename,0,40);
		memset(buf,0,10);
		sprintf(buf,"%04d",clock.wYear);       
		sprintf(cdrfile->filename,FPATH"CDR%c%c%02d%02d%03d%02d.txt",buf[2],buf[3],clock.wMonth,clock.wDay,cdr.bkey,cdrfile->index);	
		if(cdrfile->fp!=NULL)
		{
			fclose(cdrfile->fp);
		}
		cdrfile->fp=fopen(cdrfile->filename,"a");
		if(cdrfile->fp==NULL)
		{
			return FALSE;
		}
	}
	    break;
   case  evTraceStatus :
     
	     memset(&cdrtrace,0,sizeof(CDR_TRACE));
         memcpy(&cdrtrace,cmsg.data,sizeof(CDR_TRACE)); 
		 if(cdrtrace.btrace)
		 {
			 label=1;
			 g_Trace=1;
		 }
		 else
		 {
			 g_Trace=0;
			 label=0;
		 }
		 break;
   default :

	   return FALSE;
   }
   if(label)
   {
	   Get_Cdr_TraceCondition(cdrtrace,cdr,lparam);     
   }
	Deal_Cdr_Msg(&cdr,&cmsg,cmode,cdrfile);     //调用写话单

	return TRUE;
}

/*************************************************************************************

typedef struct 
{
	BYTE    btrace;        //0:表示关闭，1表示打开
    BYTE    type;          // 1为主叫，   2为被叫，3是业务键，
	BYTE    Match;         //1表示左匹配，2表示右匹配，3表示全匹配，4表示全部电话号码
	WORD    bkey;          //业务键，为0表示跟踪所有的业务类型
    char    Telephone[32]; //电话号码
	
}CDR _TRACE;               //跟踪条件

*************************************************************************************/

void  Get_Cdr_TraceCondition(CDR_TRACE cdrtrace,CDR cdr,WORD lparam)
{
    if(cdrtrace.type==1)       //按主叫号码  
    {
         Judge_Cdr_Match(cdrtrace.Match,cdrtrace.Telephone,cdr.callnum,cdr,lparam);  
    }
    else if(cdrtrace.type==2)  //按被叫号码
    {
        Judge_Cdr_Match(cdrtrace.Match,cdrtrace.Telephone,cdr.callednum,cdr,lparam);
    }
    else if(cdrtrace.type==3)  //业务键
    {    
       if(cdrtrace.bkey==cdr.bkey)
         YF_TRACE  lparam,"CDR.C",CTRACE_NAME, cdr.callnum,cdr.callednum,cdr.bkey,cdr.usernum,cdr.s_time,cdr.f_time,cdr.l_time,cdr.cost,cdr.FlowRate,cdr.fir_msg,cdr.se_msg,cdr.tid_msg,cdr.fth_msg);   
	} 
	else
	{
		return ;
	}
   
	return  ;
}

/**************************************************************
  
  参数： type      表示匹配方式
         filename  为话单需要匹配的电话号码
		 callnum   为存储的主叫 或者 被叫号码

  功能： 将接收到的话单结构，打印出来
****************************************************************/

void Judge_Cdr_Match(BYTE  type,char* filename,char* callnum,CDR cdr,WORD lparam)
{ 
  
  char* p=NULL;
  char  buf[50];
  int   len;
  
  memset(buf,0,50);
  if(type==1)      //左匹配
  {
	 p=strstr(callnum,filename);
		if(p==NULL) return ;
     len=strlen(callnum)-strlen(filename);
	 if(len<0)
		 return ;
	 strncpy(buf,callnum,len);
	 if(strcmp(buf,filename)!=0)
		 return ;
      YF_TRACE  lparam,"CDR.C",CTRACE_NAME, cdr.callnum,cdr.callednum,cdr.bkey,cdr.usernum,cdr.s_time,cdr.f_time,cdr.l_time,cdr.cost,cdr.FlowRate,cdr.fir_msg,cdr.se_msg,cdr.tid_msg,cdr.fth_msg);   
  }
  else if(type==2) //右匹配
  {
    strrev(filename);
	strrev(callnum);
    p=strstr(callnum,filename);
		if(p==NULL) return ;
     len=strlen(callnum)-strlen(filename);
	 if(len<0)
		 return ;
	 strncpy(buf,callnum,len);
	 if(strcmp(buf,filename)!=0)
		 return ;
      YF_TRACE  lparam,"CDR.C",CTRACE_NAME, cdr.callnum,cdr.callednum,cdr.bkey,cdr.usernum,cdr.s_time,cdr.f_time,cdr.l_time,cdr.cost,cdr.FlowRate,cdr.fir_msg,cdr.se_msg,cdr.tid_msg,cdr.fth_msg);   

  }
  else if(type==3) //全匹配
  {   
      if(strcmp(callnum,filename)==0) 
	    YF_TRACE  lparam,"CDR.C",CTRACE_NAME, cdr.callnum,cdr.callednum,cdr.bkey,cdr.usernum,cdr.s_time,cdr.f_time,cdr.l_time,cdr.cost,cdr.FlowRate,cdr.fir_msg,cdr.se_msg,cdr.tid_msg,cdr.fth_msg);   
  }
  else if(type==4)//全部电话号码
  {
       YF_TRACE  lparam,"CDR.C",CTRACE_NAME, cdr.callnum,cdr.callednum,cdr.bkey,cdr.usernum,cdr.s_time,cdr.f_time,cdr.l_time,cdr.cost,cdr.FlowRate,cdr.fir_msg,cdr.se_msg,cdr.tid_msg,cdr.fth_msg);    
  }
  else
  {
	  return ;
  }
   p=NULL;
   return ;
}

/**************************************************************
参数：接收消息的偏离量

  功能：将接收到的话单结构，打印出来
****************************************************************/
int Deal_Cdr_Msg( CDR*cdr,MESSAGE*cmsg,MODE_CDR*cmode,CDR_FILE*cdrfile)
{
	SYSTEMTIME   clock;
    char         buf[10];
    time_t       ltime;                                                
	time_t       rtime;
    int          size;
	
	time(&ltime);
	GetLocalTime(&clock);
	
    switch( cmsg->head.event)
	{
	case evASK_CDR:
		if(cmode->cdrmode==1)
		{
			fseek(cdrfile->fp,0,SEEK_END);
			size=ftell(cdrfile->fp); 
			if(size >= (cmode->size))
			{
				cdrfile->index++;
				if(cdrfile->index==100)                                            //实现g_num取值范围为0~99;            
				{
					cdrfile->index=99;
				} 
				fclose(cdrfile->fp);	
				cdrfile->fp=NULL;	
				memset(buf,0,10);
				sprintf(buf,"%04d",clock.wYear);
				memset(cdrfile->filename,0,40);
				sprintf(cdrfile->filename,FPATH "CDR%c%c%02d%02d%03d%02d.txt",buf[2],buf[3],clock.wMonth,clock.wDay,cdr->bkey,cdrfile->index);
				cdrfile->fp=fopen(cdrfile->filename,"a");
				if(cdrfile->fp==NULL) return -1;
				
			}
		}
		else
		{
			rtime=ltime-cdrfile->stime;
			if(rtime>cmode->size)                                           //将所得到的时间转化为秒   
			{
				cdrfile->stime=ltime;
				cdrfile->index++;
				if(cdrfile->index==100)
				{
					cdrfile->index=99;
				} 
				if(cdrfile->fp!=NULL)
				{
					fclose(cdrfile->fp);
				}
				cdrfile->fp=NULL;
				memset(cdrfile->filename,0,40);
				memset(buf,0,10);
				sprintf(buf,"%04d",clock.wYear);
				sprintf(cdrfile->filename,FPATH"CDR%c%c%02d%02d%03d%02d.txt",buf[2],buf[3],clock.wMonth,clock.wDay,cdr->bkey,cdrfile->index);
				cdrfile->fp=fopen(cdrfile->filename,"a");
				if(cdrfile->fp==NULL) return -1;
			}
		}
         if(cdrfile->fp==NULL)
		 {
			 return -1;
		 }
		fprintf(cdrfile->fp,F_FORMAT,cdr->callnum,cdr->callednum,cdr->bkey,cdr->usernum,cdr->s_time,cdr->f_time,cdr->l_time,
			cdr->cost,cdr->FlowRate,cdr->fir_msg,cdr->se_msg,cdr->tid_msg,cdr->fth_msg);
		fflush(cdrfile->fp);	            
		break;
	default:
		break;
	}
	return 0;	
}

/***********************************************************
参数：MODE_CDR结构

功能：读配置文件记下选择的方式,通过全局变量记下配置文件的方式
*************************************************************/
int choose_mode(MODE_CDR* cmode)
{    
	int              mode;
	WIN32_FIND_DATA  FindFileData;
	HANDLE           hFind;
	char             temp[40];
    char             filename[40];	
    char             buf[20];  
    int              index;
    int              num; 
	char             _buf[10];
    SYSTEMTIME       clock; 
	int              i;

	GetLocalTime(&clock);
  
	num=0;
	memset(_buf,0,10);
    memset(buf,0,20);
    memset(filename,0,40);
    memset(temp,0,40);
     
    sprintf(_buf,"%4d",clock.wYear);
	i=strlen(_buf);
	sprintf(temp,"%c%c%02d%02d",_buf[2],_buf[3],clock.wMonth,clock.wDay); 
	mode=GetPrivateProfileInt("index","ByFileSize",0,P_PATH); 
	if(mode==1)
	{
	    cmode->cdrmode=mode;
		cmode->size=GetPrivateProfileInt("index","FileSize",0,P_PATH); 
		if(cmode->size==0)
		{	
			YF_LOG_SPP  "设置文件大小的配置文件设置有误");
			return -1;                                                    //错误设置改变文件大小的为0                                    
		}
        cmode->size=cmode->size*1024;                                      //将BYTE转化为KB
	}
    else
    {
		cmode->cdrmode=2;
		cmode->size=GetPrivateProfileInt("index","Hour",0,P_PATH); 	 
		if(	cmode->size==0)
		{	
			YF_LOG_SPP "按时间的配置文件设置有误");
			return -1;                                   
		}        
		cmode->size=cmode->size*3600;
	}

	hFind = FindFirstFile("C:\\yfcomm\\CDR\\*.* ",&FindFileData);
	
	if(hFind!=INVALID_HANDLE_VALUE)
	{
		do
		{
			if(!(FindFileData.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) )
			{
				if(strstr(FindFileData.cFileName,temp)!=0)
				{
					strcpy(filename,FindFileData.cFileName);
                    
			        sprintf(buf,"%c%c",filename[12],filename[13]);
					index=atoi(buf);
					if(index==99)
					{
                      cmode->index=99;
					  return  TRUE;
					}
                    if(index>num)
					{   
                       num=index;
					}
				}     
			}
                    
		}while(FindNextFile(hFind,&FindFileData));
					
	   FindClose(hFind);	
	}	
    num++;
    cmode->index=num;
                       
    return FALSE;
}
