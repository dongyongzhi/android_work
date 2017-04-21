#ifdef     YFCOMM_DYZ_2009_5_19

#define    YFCOMM_DYZ_2009_5_19


#else


#define MAXLINE   10*1024*1024  
#define NORMAL     0
#define WARN       1 
#define TRACE      2             


#pragma   pack(1)
typedef struct 
{		
	BYTE  type;               // 0:normal 1：warn  2：trace 。           
    WORD  lines;              // 标识行        
	char  funname[100];       // 函数名 
    BYTE  warnstep;           // 告警等级  
	char  content[CHAR_NUMBER-105];       // 日志内容

}Log_Message;


typedef  struct               //三种文件的基本属性
{ 

   int   index;               //标识当前文件的序列号
   char  filename[30];        //标识当前文件名 

}FileAtt;


typedef struct  
{
  FileAtt  nomal_file;        //标识正常日志的属性

  FileAtt  Trace_file;         //标识跟踪日志的属性

}ATT_FILE;     

#pragma   pack()

int   Find_MaxIndex(ATT_FILE* all_file);
int   Write_DBAPWarnLog(Log_Message ulog);
int   WriteLogMsg(WORD wparam,ATT_FILE *all_file);
void  logProc(PVOID lp);
int   Change_char_To(char* buf);
int   Judge_FileName(char* name,FileAtt*fileatt,Log_Message  ulog);

#endif  YFCOMM_DYZ_2009_5_19 