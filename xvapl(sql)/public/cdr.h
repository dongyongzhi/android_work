#ifdef  DYZ_YF_CDR_BILL_H 
#define DYZ_YF_CDR_BILL_H

#else 

#include <stdio.h>
#include <windows.h>
#include <string.h>
#include <time.h>
#include "comm.h"
#include "Commdll.h"
#include "public_define.h"
#include "event.h"
#include "define.h"
#include "yftrace.h"




#pragma  pack(1)

typedef struct 
 {
  char    callnum[32];      //主叫号码
  char    callednum[32];    //被叫号码   
  char    s_time[30];       //开始时间 
  char    f_time[30];       //结束时间 
  DWORD   l_time;           //通话时长                                       
  char    usernum[20];      //用户号码
  WORD    bkey;             //业务键
  WORD    cost;             //费用    
  WORD    FlowRate;         //流量  
  char    fir_msg[40];      //附加消息1
  char    se_msg[40];       //附加信息2
  char    tid_msg[40];      //附加信息3
  char    fth_msg[40];      //附加信息4
}CDR;

typedef struct 
{
 
   int   cdrmode;  //记录配置文件选择的方式  0、表示通过文件大小设置,1、  表示通过时间设置
   int   size;     //当cdrmode为0时，表示文件大小 单位为 k;当g_mode为1时，表示时间的大小单位为秒 
   int   index;    //记录文件序号   

}MODE_CDR;

typedef struct 
{
  FILE    *fp;           //标识当前文件打开的句柄
  char    filename[40];  //标识当前文件名
  int     index;         //标识当前文件名引用的序号
  int     bkey;          //当前文件的业务键
  time_t  stime;         //程序刚启动时记录秒数   
  char    temp[40];      //标识程序运行时的日期
}CDR_FILE;

typedef struct 
{
	BYTE    btrace;         //0:表示关闭，1表示打开
    BYTE    type;           // 1为主叫，   2为被叫，3是业务键，
	BYTE    Match;          //1表示左匹配，2表示右匹配，3表示全匹配，4表示全部电话号码
	WORD    bkey;           //业务键，为0表示跟踪所有的业务类型
    char    Telephone[32];  //电话号码
	
}CDR_TRACE;                  //跟踪条件


#pragma  pack()



#define    FPATH            "C:\\yfcomm\\CDR\\"    
#define    P_PATH           "C:\\yfcomm\\ini\\CDR.ini"
#define    F_FORMAT         "%s | %s | %d | %s | %s | %s | %d | %d | %d | %s | %s | %s | %s \n" 

#define    CTRACE_NAME     "callingnum:%s callednum:%s bkey:%d usernum:%s s_time:%s  f_time:%s l_time:%d  cost:%d FlowRate:%d fir_msg:%s se_msg:%s tid_msg:%s fth_msg:%s \n" 

unsigned  _stdcall Cdr_proc(LPVOID lp);

int   Deal_Cdr_Msg( CDR*cdr,MESSAGE*cmsg,MODE_CDR* cmode,CDR_FILE*cdrfile);
int   judge_filename(WORD wapram,WORD lparam,CDR_FILE* cdrfile,MODE_CDR* cmode);
int   choose_mode(MODE_CDR* cmode);
void  Get_Cdr_TraceCondition(CDR_TRACE cdrtrace,CDR cdr,WORD lparam);
void  Judge_Cdr_Match(BYTE  type,char* filename, char* callnum,CDR cdr,WORD lparam);

#endif DYZ_YF_CDR_BILL_H