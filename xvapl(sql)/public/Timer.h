#ifndef _TIMER_H_
#define _TIMER_H_
#if TIMER_H
# define DLLIMPORT __declspec (dllimport)
#else /* Not COMM_DLL */
# define DLLIMPORT __declspec (dllexport)
#endif /* Not COMM_DLL */

//#define LOG

#include <windows.h>
#include <Mmsystem.h>
#include "TimerDll.h"
#include "../public/Commdll.h"
#include "../public/comm.h"

#define MAX_TIMERS 8192  //8192:相对计时器数目
#define MAX_ABS_TIMERS 10   //10:绝对计时器数目

#pragma  pack(1)
typedef struct 
{
	long    ldTime;    /*定时器的触发时间*/
	WORD	event;		/* 定时器到时，触发的事件 */
	UINT    mId;       /* 模块号*/
	WORD	ref;		/* 参考值，由申请者自己定义，返回时原值返回 */	
	WORD    tmID;       //定时器的ID.
}YF_TIMER;

typedef struct tagTMCtrl
{
	YF_TIMER    timer;
	struct tagTMCtrl  *prev,*next;
}YF_TmCtrl;

typedef struct 
{
	WORD index;
	YF_TmCtrl* vec[10];
}timer_vec;
#pragma  pack()

/*********************************相对定时器用到的数据***************************************/
YF_TmCtrl* szHeadNode[51];
YF_TmCtrl  szHead[51] = {0};

static short int yf_revise = 0;//用来纠正时间偏差
static long yf_beginYfTime = 0;//用来纠正时间偏差
static long yf_curYfTime = 0;//用来纠正时间偏差
static long yf_beginRealTime = 0;//用来纠正时间偏差
static long yf_curRealTime = 0;//用来纠正时间偏差

static long yf_YfTime = 0;//计时,以0.1秒为单位
static YF_TmCtrl* pHead = NULL;//指向计时器向量的开始处
static YF_TmCtrl* pCurrent = NULL;//指向计时器向量的开始处
static WORD yf_curTimerCursor = 0;//指向计时器向量的当前处
static YF_TmCtrl* yf_szTimer[MAX_TIMERS];//每个计时器的地址.
static YF_TIMER* yf_szAbsTimer[MAX_ABS_TIMERS];//每个绝对计时器的地址.

static  timer_vec tv1;//0.1秒级
static  timer_vec tv2;//1秒级 
static  timer_vec tv3;//10秒级 
static  timer_vec tv4;//100秒级 
static  timer_vec tv5;//1000秒级（最长时间约为：10000/60/60 ＝ 2.7小时）
static  timer_vec *  yf_tvecs[] = {&tv1,&tv2,&tv3,&tv4,&tv5};


/********************************绝对定时器用到的数据****************************************/
YF_TmCtrl* pabsHead = NULL,*pabsCur = NULL,*pabsTail = NULL;

CRITICAL_SECTION sec;

CRITICAL_SECTION absSec;

//纠正时间偏差:yf_curTime
static /*inline*/ void revise_time();

static /*inline*/ void init_timer(/**/);

static int get_tmid(/*YF_TmCtrl * pTm*/);//在yf_timerList中找一个未用的ID，将数组下标返回

static void del_tmid(WORD tmid);// 

static /*inline*/ int add_timer(YF_TIMER timer);

static int transformnAddTimer(YF_TIMER,WORD); 

//static /*inline*/ void kill_timer(WORD tmID);

static /*inline*/ int internal_add_timer(YF_TIMER,WORD);

static /*inline*/ void internal_mod_timer(YF_TIMER timer,WORD tmID);

static /*inline*/ void mod_timer(YF_TIMER timer,WORD tmID);

static /*inline*/ void cascade_timers(/*YF_TmCtrl* tv*/);//计时器的迁移操作

static void TriggleTimer(YF_TIMER timer);

unsigned __stdcall timerProc(void *lpP);//相对定时器的循环线程

static /*inline*/ int add_abs_timer(YF_TIMER timer);
static int get_abs_tmid();
static void del_abs_tmid(WORD tmid);
static void TriggleAbsTimer(YF_TIMER timer);
unsigned __stdcall absTimerProc(void *lpP);//绝对定时器的循环线程

void CALLBACK TimeProc(UINT uID,UINT uMsg, DWORD dwUser,DWORD dw1,DWORD dw2);

void WriteLog(const char *fmt,...);

#endif