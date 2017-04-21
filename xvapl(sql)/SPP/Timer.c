#include <stdio.h>
#include <process.h>
#include <time.h>
#include "yftrace.h"
#include "Timer.h"
#include "TimerDll.h" 


BOOL APIENTRY DllMain(HINSTANCE hInst,
                       DWORD reason,
                       LPVOID reserved)
{
	
    switch (reason)
    {
	case DLL_PROCESS_ATTACH:
        break;
		
	case DLL_PROCESS_DETACH:
        break;
		
	case DLL_THREAD_ATTACH:
        break;
		
	case DLL_THREAD_DETACH:
        break;
    }
    return TRUE;
}
/************************************************************************/
/* 名字:init_timer
参数:无
返回值:无
功能:初始化
注意:                                                                   */
/************************************************************************/
static /*inline*/ void init_timer(/**/)
{
	static bHaveInit = FALSE;
	DWORD dwHandle1,dwHandle2;	
	int i = 0 , j = 0; 
	if(!bHaveInit)
		bHaveInit = TRUE;
	else
		return;

	//初始化各个向量,数据
	pCurrent = pHead = NULL;
	yf_curTimerCursor = 0;
	yf_revise = 0;
	for(i = 0;i < MAX_TIMERS;i++)
		yf_szTimer[i] = NULL;
	for(i = 0;i < 5;i++)//初始化计时器向量
	{
		yf_tvecs[i]->index = 0;
		for(j = 0;j < 10; j++)
		{
			szHeadNode[i*10+j] = &szHead[i*10+j];
			szHeadNode[i*10+j]->timer.event = 0;
			szHeadNode[i*10+j]->timer.ldTime = 0;
			szHeadNode[i*10+j]->timer.mId = 0;
			szHeadNode[i*10+j]->timer.ref =0;
			szHeadNode[i*10+j]->timer.tmID = 65535;
			szHeadNode[i*10+j]->prev = &szHead[i*10+j];
			szHeadNode[i*10+j]->next = &szHead[i*10+j];//带头结点的双向循环链表.
			yf_tvecs[i]->vec[j] = &szHead[i*10+j];//每个向量是一个有头节点的双向链表
		}
	}
	szHeadNode[50] = &szHead[50];
	szHeadNode[50]->timer.event = 0;
	szHeadNode[50]->timer.ldTime = 0;
	szHeadNode[50]->timer.mId = 0;
	szHeadNode[50]->timer.ref =0;
	szHeadNode[50]->timer.tmID = 65535;
	szHeadNode[50]->prev =  &szHead[50];
	szHeadNode[50]->next = &szHead[50];
	pabsCur = pabsTail = pabsHead = szHeadNode[50];

	for(i = 0; i < MAX_ABS_TIMERS;i++)
	{
		yf_szAbsTimer[i] = NULL;
	}
	 InitializeCriticalSection(&sec);//初始化信号量
	 InitializeCriticalSection(&absSec);//初始化信号量
	//启动计时器线程
//	 yf_beginYfTime = yf_YfTime;
	 yf_beginRealTime = CURRENTTIME();
	 dwHandle1=_beginthreadex(NULL,0,timerProc,NULL,0,NULL);
	 dwHandle2=_beginthreadex(NULL,0,absTimerProc,NULL,0,NULL);
	if(dwHandle1 == 0 || dwHandle2 == 0)
	{ 
#ifdef LOG
		ALARM_TIMER "timer proc error");
#endif		
//		exit(1);
	}
	else
	{
#ifdef LOG
		LOG_RUNTIMER "timer thread begin");
#endif	
		CloseHandle((HANDLE)dwHandle1);
		CloseHandle((HANDLE)dwHandle2);
	}
	return;
}

/************************************************************************/
/* 名字:revise_time
*参数:	无
*返回值:无
*功能:	纠正时间偏差:yf_curTime
*注意:	                                                                  */
/************************************************************************/
static /*inline*/ void revise_time()
{	
	yf_curRealTime = CURRENTTIME();
	//将真实时间与计时器的时间差值比较,得到时间偏差值.
	yf_revise = (short int)(yf_curRealTime - yf_beginRealTime - 20)*5/2;//(yf_curRealTime - yf_beginRealTime)*1000/1000

#ifdef LOG
	LOG_RUNTIMER "revisetime:%d",yf_revise);
	LOG_RUNTIMER "currennt timers number:%d",yf_curTimerCursor);
#endif
	yf_beginRealTime = yf_curRealTime;
}

/************************************************************************/
/* 
名称:get_tmid
参数:
返回值:int:一个计时器的ID(0--8192),如未找到,返回-1
功能:yf_szTimer中找一个未用的ID，将数组下标返回
注意:                                                                     */
/************************************************************************/
static int get_tmid(/*YF_TmCtrl * pTm*/)
{
	WORD i ;
	int ret =  -1;
	EnterCriticalSection(&sec);
	for(i = 0; i < MAX_TIMERS;i++)
	{
		if(yf_szTimer[yf_curTimerCursor] == NULL)
		{
			ret = yf_curTimerCursor;
			yf_curTimerCursor = ((1 + yf_curTimerCursor) < MAX_TIMERS) ? (1 + yf_curTimerCursor) : 0 ;
			LeaveCriticalSection(&sec); 
			return ret;
		}
		else
			yf_curTimerCursor = ((1 + yf_curTimerCursor) < MAX_TIMERS) ? (1 + yf_curTimerCursor) : 0 ;
	}
#ifdef LOG
	ALARM_TIMER "timers are full");
#endif
	LeaveCriticalSection(&sec);
	return ret;
}

/************************************************************************/
/* 
名称:del_tmid
参数:WORD:计时器ID
返回值:无
功能:根据计时器ID将此计时器删除,将计时器的游标退回到上一处
注意:                                                                   */
/************************************************************************/
static void del_tmid(WORD tmid)
{
	if(tmid >= MAX_TIMERS || tmid < 0)
		return;

	EnterCriticalSection(&sec);

	if(yf_szTimer[tmid] != NULL)
	{
		yf_szTimer[tmid]->prev->next = yf_szTimer[tmid]->next;//前一个节点的NEXT指向下一个节点.
		yf_szTimer[tmid]->next->prev = yf_szTimer[tmid]->prev;//下一节点的头部指向前一节点.

		yf_szTimer[tmid]->next = NULL;
		yf_szTimer[tmid]->prev = NULL;
		free(yf_szTimer[tmid]);
		yf_szTimer[tmid] = NULL;		
	}	
	LeaveCriticalSection(&sec);
	return;
}

/************************************************************************/
/* 增加相对计时器                                                           
*/
/************************************************************************/
static  int add_timer(YF_TIMER timer)
{
	 int timer_id = -1;
	 int ret = -1;
	//判断定时器的时间是否在正常范围
	timer.ldTime = timer.ldTime/100;//将其转化为100毫秒级。
	if(timer.ldTime < 0 || timer.ldTime > 10000)  //0.1-999秒,不在正常范围内,返回失败信息
	{
		ret = -1;
	}
	else if (timer.ldTime == 0)//立即触发.
	{	
		TriggleTimer(timer);		
		 ret = - 2;
	}
	else//增加计时器.
	{
		EnterCriticalSection(&sec);
		timer_id = get_tmid();
		if( timer_id <0 || timer_id >= MAX_TIMERS )
		{
			ret =  -1;
			LeaveCriticalSection(&sec);
			return ret;
		}
		timer.ldTime = transformTimer(timer.ldTime);			
		ret =  internal_add_timer(timer,(WORD)timer_id);
		LeaveCriticalSection(&sec);
	}
	return ret;
}
 
static long transformTimer(long ldTime)
{
	if(ldTime > 0 && ldTime < 10)
	{
		if(ldTime < 5)//当触发时间在500毫秒内,防止误差过大,将触发时间加100毫秒.
			ldTime = ldTime + tv1.index + 1;
		else
			ldTime = ldTime + tv1.index;
		if(ldTime >= 10 && ldTime < 100)
			ldTime += tv2.index*10;
		if(ldTime >= 100 && ldTime < 1000)
			ldTime += tv3.index*100;
		if(ldTime >= 1000 && ldTime < 10000)
			ldTime += tv4.index*1000;
		if(ldTime == 10000)
			ldTime += tv5.index *10000;
	}
	else if (ldTime >= 10 && ldTime < 100)
	{
		ldTime += tv1.index + tv2.index*10;
		if(ldTime >= 100 && ldTime < 1000)
			ldTime += tv3.index*100;
		if(ldTime >= 1000 && ldTime < 10000)
			ldTime += tv4.index*1000;
		if(ldTime == 10000)
			ldTime += tv5.index *10000;

	}
	else if(ldTime >= 100 && ldTime < 1000)
	{
		ldTime += tv1.index + tv2.index*10 + tv3.index*100;
		if(ldTime >= 1000 && ldTime < 10000)
			ldTime += tv4.index*1000;
		if(ldTime == 10000)
			ldTime += tv5.index *10000;
	}
	else if(ldTime >= 1000 && ldTime < 10000)
	{
		ldTime += tv1.index + tv2.index*10 + tv3.index*100 + tv4.index*1000 ;
		if(ldTime == 10000)
			ldTime += tv5.index *10000;
	}
	else if(ldTime == 10000)
	{
		ldTime += tv1.index + tv2.index*10 + tv3.index*100 + tv4.index*1000 + tv5.index*10000;
	}
	else
	{
#ifdef LOG
			ALARM_TIMER "transformTimer,ldtime:%d,ref:%d",ldTime);
#endif
	}
	return ldTime;
}
/************************************************************************/
/* 新增一个相对计时器                                                    */
/************************************************************************/
static /*inline*/ int internal_add_timer(YF_TIMER timer,WORD timer_id)	//将定时器加入到指定向量中
{
	YF_TmCtrl * pTm = NULL;//要增加的TIMER
	YF_TmCtrl * head = NULL;//指向TV1或TV2或...中10个数组中的其中一个,指向头结点.
	if(timer_id < 0 || timer_id > MAX_TIMERS)
	{	
		YF_LOG_TIMER "internal_add_timer error,id is %d ",timer_id);
		return -1;
	}

//	GLM_LOG "internal_add_timer,id:%d,ld:%ld",timer_id,timer.ldTime);
	EnterCriticalSection(&sec);
	pTm = (YF_TmCtrl *)malloc(sizeof(YF_TmCtrl));
	if(pTm == NULL)
	{		
		LeaveCriticalSection(&sec);
		return -1;
	}
	
	yf_szTimer[timer_id] = pTm;

	if(timer.ldTime == 0)
	{
		head = tv1.vec[tv1.index];
	}
	else if(timer.ldTime > 0 && timer.ldTime < 10)
	{
		head = tv1.vec[timer.ldTime];
	}
	else if(timer.ldTime >=10 && timer.ldTime < 100)
	{
		head = tv2.vec[timer.ldTime/10];
	}

	else if(timer.ldTime >= 100 && timer.ldTime < 1000)
	{
		head = tv3.vec[timer.ldTime/100];
	}
	
	else if( timer.ldTime >= 1000 && timer.ldTime < 10000)
	{
		head = tv4.vec[timer.ldTime/1000];
	}
	
	else if( timer.ldTime >= 10000 && timer.ldTime <= 109999)
	{
		head = tv5.vec[timer.ldTime/10000];
	}
	else/*(timer.ldTime == 10000*1000)*/
	{
		del_tmid(timer_id);
		LeaveCriticalSection(&sec);
		return -1;   //不可能发生的情况,仅为保险起见.
	}

	//将TIMER填充到目标向量中.
	
	memcpy(&(pTm->timer),&timer,sizeof(YF_TIMER));
	pTm->next = head->next;
	head->next = pTm;
	pTm->prev = head;
	pTm->next->prev = pTm;
	pTm->timer.tmID = timer_id;//将其ID保存起来.触发计时器时使用.
	LeaveCriticalSection(&sec);

	return timer_id;
}
//
//static /*inline*/ void kill_timer(WORD tmID)
//{
//	del_tmid(tmID);
//}

/************************************************************************/
/* 
名称:internal_mod_timer
参数:timer:欲修改的新相对计时器.tmID:旧的计时器的ID.
返回值:WORD:修改后的返回值,新的TIMER的ID.
功能:修改包括了触发时间的相对计时器.
注意: 暂不使用此功能                                                     */
/************************************************************************/
static /*inline*/ void internal_mod_timer(YF_TIMER timer,WORD tmID)
{

}

/************************************************************************/
/* 修改计时器(相对计时器和绝对计时器) 
注意：暂时不使用此功能                                  */
/************************************************************************/
static /*inline*/ void mod_timer(YF_TIMER timer,WORD tmID)
{

}

/************************************************************************/
/* 相对计时器的迁移.                                                     */
/************************************************************************/
static /*inline*/ void cascade_timers(/*YF_TmCtrl* tv*/)//计时器的迁移操作
{
	YF_TmCtrl* head = NULL,*curr = NULL,*next = NULL;
	YF_TIMER timer;
	WORD timerid = -1;
	EnterCriticalSection(&sec);

//	GLM_LOG "cascade_timers");

	tv2.index = ++tv2.index%10;
	head = tv2.vec[tv2.index];//先使INDEX加1,再将里面的计时器加到上一层.
	curr = next = head->next;
	while(next != head)
	{
		next = next->next;
		memcpy(&timer,&curr->timer,sizeof(YF_TIMER));
		timer.ldTime = curr->timer.ldTime%10;
		timerid = curr->timer.tmID;
		del_tmid(curr->timer.tmID);
//		GLM_LOG "tv2:id:%d,event:%d,ld:%ld,mid:%d,ref:%d",timer.tmID,timer.event,timer.ldTime,timer.mId,timer.ref);
		internal_add_timer(timer,timerid);
		curr = next;
	}	
	if(tv2.index == 0)
	{
		tv3.index = ++tv3.index%10;
		head = tv3.vec[tv3.index];//先使INDEX加1,再将里面的计时器加到上一层.
		curr = next = head->next;
		while(next != head)
		{
			next = next->next;
			memcpy(&timer,&curr->timer,sizeof(YF_TIMER));			
			timer.ldTime = curr->timer.ldTime%100;
			del_tmid(curr->timer.tmID);
			YF_TRACE_TIMER -1,"cascade_timers","tv3:id:%d,event:%d,ld:%ld,mid:%d,ref:%d",timer.tmID,timer.event,timer.ldTime,timer.mId,timer.ref);
			internal_add_timer(timer,timer.tmID);
			curr = next;
		}
		if(tv3.index == 0)
		{
			tv4.index = ++tv4.index%10;
			head = tv4.vec[tv4.index];
			curr = next = head->next;
			while(next != head)
			{
				next = next->next;
				memcpy(&timer,&curr->timer,sizeof(YF_TIMER));			
				timer.ldTime = curr->timer.ldTime%1000;
				del_tmid(curr->timer.tmID);
				YF_TRACE_TIMER -1,"cascade_timers","tv4:id:%d,event:%d,ld:%ld,mid:%d,ref:%d",timer.tmID,timer.event,timer.ldTime,timer.mId,timer.ref);
				internal_add_timer(timer,timer.tmID);
				curr = next;
			}
			if(tv4.index == 0)
			{
				tv5.index = ++tv5.index%10;
				head = tv5.vec[tv5.index];
				curr = next = head->next;
				while(next != head)
				{
					next = next->next;
					memcpy(&timer,&curr->timer,sizeof(YF_TIMER));			
					timer.ldTime = curr->timer.ldTime%10000;
					del_tmid(curr->timer.tmID);
					YF_TRACE_TIMER -1,"cascade_timers","tv5:id:%d,event:%d,ld:%ld,mid:%d,ref:%d",timer.tmID,timer.event,timer.ldTime,timer.mId,timer.ref);
					internal_add_timer(timer,timer.tmID);
					curr = next;
				}
			}		
		}
	}
	LeaveCriticalSection(&sec);
}

//相对定时器的循环线程
unsigned __stdcall timerProc(void *lpP)
{
	YF_TmCtrl *pCur = NULL;
//	tv1.index = 1;
	static WORD timeDelay = 0;
//	yf_bIsTimerProcBegin = TRUE;
	YF_LOG_TIMER "timerproc begin");
	while(TRUE)
	{
		Sleep(100 - yf_revise);
		EnterCriticalSection(&sec);
		/*****************************执行顺序:**************************
		++index;//先使INDEX加1
		cascade_timers();再根据判断,将上一层计时器迁移进来
		triggleTimer();触发
		*****************************************************************/
		//计时器的迁移	
		tv1.index = ++tv1.index%10;
		if(tv1.index == 0)
		{
			cascade_timers();
		}	
	
		pCur = tv1.vec[tv1.index]->next;
		while(pCur != tv1.vec[tv1.index])
		{
			TriggleTimer(pCur->timer);
			pCur = tv1.vec[tv1.index]->next;
		}
	
		//时间加1
		yf_YfTime++;

		//时间纠正
		if(yf_YfTime % 200 == 0)
			revise_time();
	
		LeaveCriticalSection(&sec);
	}
	DeleteCriticalSection(&sec);
	return 0;
}

DLLIMPORT BOOL INIT_TIMER()
{
	EnableTrace(TRUE);
	init_timer();
	return TRUE;
}

/*************************************************************************************************
*名称:SET_TIMER
*参数 : tm-time,event-event,mId-module number,ref-ref,abs-absolute time or not
		tm单位:100毫秒
*返回值:int-计时器ID.范围:0---8191.
			 -1:申请计时器失败
			 -2:计时器线程还未启动，申请失败
*功能:add a timer
*注意:
*********************************************************************************************/
DLLIMPORT int SET_TIMER(LONG tm,WORD event, UINT mId,WORD ref ,BOOL abs)
{
	//如果是绝对定时器,判断是否在未来时间段内,进入下步.
	//如果是相对定时器,如果相对时间为0,立即触发,如果为否,进入下步.
	int ret =  -1;
	YF_TIMER timer;


	timer.ldTime = tm;
	timer.event = event;
	timer.mId = mId;
	timer.ref = ref;
	if(abs)
	{
		timer.ldTime = tm*100;
		ret = add_abs_timer(timer);
	}
	else
		ret = add_timer(timer);
	if(ret >= 0 && ret < MAX_TIMERS+MAX_ABS_TIMERS)
		YF_TRACE_TIMER -1,"SET_TIMER","time:%ld,abs:%d,ID:%d",tm,(int)abs,ret);
	return ret;
}


DLLIMPORT BOOL KILL_TIMER(WORD timerID)
{
	YF_TRACE_TIMER -1,"KILL_TIMER","%d",timerID);
	if(timerID >= 0 && timerID < MAX_TIMERS)
	{
		del_tmid(timerID);
		return TRUE;
	}
	else if(timerID >= MAX_TIMERS && timerID < MAX_TIMERS+MAX_ABS_TIMERS)
	{
		del_abs_tmid(timerID);
		return TRUE;
	}
	else
		return FALSE;
}


DLLIMPORT void  CLEAR_TIMER()
{
	WORD i ;
	for(i = 0;i < MAX_TIMERS + MAX_ABS_TIMERS ;i++)
	{
		KILL_TIMER(i);
	}
}

DLLIMPORT LONG CURRENTTIME()
{
	time_t tm;
	time(&tm);
	return tm;
}

static void TriggleTimer(YF_TIMER timer)
{
	MESSAGE yfMsg;
	TID* dst = NULL;
	BYTE buf[20];
	memset(buf,0,20);

	dst = GetTID(timer.mId);
	yfMsg.head.event = timer.event;
#ifdef _WIN32
//	yfMsg->head.sender.hWnd = src->hWnd;
	yfMsg.head.receiver.hWnd = dst->hWnd;
#else
//	yfMsg->head.sender.procece = src->procece;
	yfMsg.head.receiver.procece = dst->procece;
#endif	
//	yfMsg->head.sender.node = src->node;
	yfMsg.head.receiver.node = dst->node;
//	yfMsg->head.sender.module = src->module;
	yfMsg.head.receiver.module = dst->module;
	itoa(timer.ref,buf,10);
	strncpy(yfMsg.data,buf,20);		
	yfMsg.head.len = strlen(buf);	
	
	SEND_MSG(evThreadMessage,timer.mId,&yfMsg,(WORD)(sizeof(yfMsg.data)+yfMsg.head.len),timer.ref,FALSE,NULL,0);

	YF_TRACE_TIMER -1,"TriggleTimer","id:%d,event:%d,ld:%d,ref:%d,mid:%d",timer.tmID,timer.event,timer.ldTime,timer.ref,timer.mId);
#ifdef LOG
	LOG_RUNTIMER "\tTriggleTimer:ldtime:%ld,tv1.index:%d,ref:%d",timer.ldTime,tv1.index,timer.ref);
#endif
	del_tmid(timer.tmID);
}

/*
static inline BOOL timer_pending(YF_TIMER timer)//判断一个计时器是否已经存在
{
	return TRUE;
}
*/

static void del_abs_tmid(WORD tmid)
{
	if(tmid < MAX_TIMERS || tmid >= (MAX_TIMERS + MAX_ABS_TIMERS))
		return;

	EnterCriticalSection(&absSec);
	if(yf_szAbsTimer[tmid - MAX_TIMERS] != NULL)
	{
		free(yf_szAbsTimer[tmid - MAX_TIMERS]);
		yf_szAbsTimer[tmid - MAX_TIMERS] = NULL;
	}	
	LeaveCriticalSection(&absSec);
	return;
}

static  int add_abs_timer(YF_TIMER timer)
{	
	int timer_id = -1;
	YF_TIMER *pTm = NULL;
	time_t curtime;
	time(&curtime);
	if(timer.ldTime < curtime)//判断计时器边界
	{
		return -1;
	}
	else if(timer.ldTime <= curtime+5)
	{
		TriggleAbsTimer(timer);
	}
	else 
	{
		EnterCriticalSection(&absSec);
		timer_id = get_abs_tmid();//找到一个ID
		if( timer_id < MAX_TIMERS || timer_id >= MAX_TIMERS + MAX_ABS_TIMERS )
		{
			LeaveCriticalSection(&absSec);
			return -1;
		}
		
		yf_szAbsTimer[timer_id - MAX_TIMERS] = (YF_TIMER *)malloc(sizeof(YF_TIMER));//增加计时器
		if(yf_szAbsTimer[timer_id - MAX_TIMERS] == NULL)
		{	
			LeaveCriticalSection(&absSec);
			return -1;
		}		
		memcpy(yf_szAbsTimer[timer_id - MAX_TIMERS],&timer,sizeof(YF_TIMER));
		yf_szAbsTimer[timer_id - MAX_TIMERS]->tmID = timer_id;
		LeaveCriticalSection(&absSec);
	}

	return timer_id;
}


static int get_abs_tmid()
{
	static WORD absTimerCursor = 0;
	int ret = -1;
	WORD i;
	
	EnterCriticalSection(&absSec);
	for(i = 0;i < MAX_ABS_TIMERS;i++)
	{
		if(yf_szAbsTimer[absTimerCursor] == NULL)
		{
			ret = absTimerCursor + MAX_TIMERS;
		}
		absTimerCursor = ((absTimerCursor + 1) < MAX_ABS_TIMERS) ? (absTimerCursor + 1) : 0;
	}	
	LeaveCriticalSection(&absSec);
	return ret;
}

unsigned __stdcall absTimerProc(void *lpP)//绝对定时器的循环线程
{
	time_t curTime;
	WORD i = 0;
//	yf_bIsTimerProcBegin = TRUE;
	while(TRUE)
	{	
		Sleep(10000);
		EnterCriticalSection(&absSec);
		time(&curTime);
		for(i = 0; i < MAX_ABS_TIMERS;i++)
		{		
			if(yf_szAbsTimer[i] != NULL && yf_szAbsTimer[i]->ldTime <= curTime)
			{
				TriggleAbsTimer(*(yf_szAbsTimer[i]));	
			}
		}	
		LeaveCriticalSection(&absSec);
	}
	DeleteCriticalSection(&absSec);
	return -1;
}

static void TriggleAbsTimer(YF_TIMER timer)
{
	MESSAGE yfMsg;
	TID* dst = NULL;
	BYTE buf[20];
	memset(buf,0,20);

	dst = GetTID(timer.mId);
	yfMsg.head.event = timer.event;
#ifdef _WIN32
//	yfMsg->head.sender.hWnd = src->hWnd;
	yfMsg.head.receiver.hWnd = dst->hWnd;
#else
//	yfMsg->head.sender.procece = src->procece;
	yfMsg.head.receiver.procece = dst->procece;
#endif	
//	yfMsg->head.sender.node = src->node;
	yfMsg.head.receiver.node = dst->node;
//	yfMsg->head.sender.module = src->module;
	yfMsg.head.receiver.module = dst->module;
	itoa(timer.ref,buf,10);
	strncpy(yfMsg.data,buf,20);		
	yfMsg.head.len = strlen(buf);	

	SEND_MSG(evThreadMessage,timer.mId,&yfMsg,(WORD)(sizeof(yfMsg.data)+yfMsg.head.len),timer.ref,FALSE,NULL,0);

	YF_TRACE_TIMER -1,"TriggleAbsTimer","id:%d",timer.tmID);

#ifdef LOG
	LOG_RUNTIMER "\tTriggleTimer:ref:%d",timer.ref);
#endif
	del_abs_tmid(timer.tmID);
}
