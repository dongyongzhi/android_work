#ifndef _TIMER_DLL_H_
#define _TIMER_DLL_H_
#if TIMER_DLL
# define DLLIMPORT __declspec (dllimport)
#else /* Not COMM_DLL */
# define DLLIMPORT __declspec (dllexport)
#endif /* Not COMM_DLL */

#include <windows.h>
/***************************************************************************************
*名称:INIT_TIMER
*参数:no
*返回值:TURE OR FALSE
*功能:initialize timer
*注意: 无
*******************************************************************************************/
DLLIMPORT BOOL INIT_TIMER();

/****************************************************************************************
*名称:SET_TIMER
*参数 : tm-time,event-event,mId-module number,ref-ref,abs-absolute time or not
*返回值:INT表示申请到的计时器ID.范围:0---8192.如果返回值小于0,表示申请不成功.		
*功能:增加一个计时器
*使用说明:
	1.相对计时器(精确度:100毫秒): 
		1)--tm:  1---1000(即0.1秒---1000秒)
		2)--如果 tm<1,则立即触发此计时器,tm>1000*1000(1000秒),返回失败值. 
		3)--大于100秒的计时器最好使用绝对计时器实现.
		4)--相对计时器运行时间最长为: 0x7fffffff/10/60/60/24/365 = 6.8年.(INT型数据最大值:0X7FFFFFFF)
	2.绝对计时器(精确度:10秒):
	  tm--是表示以秒为单位的未来某个时间.当前时间由CURRENTTIME()获得.也可以由time()获得
	  或设置未来要触发的时间值.
****************************************************************************************/
DLLIMPORT INT SET_TIMER(LONG tm,WORD event, UINT mId,WORD ref ,BOOL abs);

/*
*名称:KILL_TIMER
*参数:timerID
*返回值:TRUE OR FALSE
*功能:kill a timer
*注意:no
*/
DLLIMPORT BOOL KILL_TIMER(WORD timerID);

/************************************************************************/
/* 
*名称:CLEAR_TIMER
*参数:none
*返回值:void
*功能:清除所有定时器
*注意:no                                                                */
/************************************************************************/
DLLIMPORT void  CLEAR_TIMER();

/************************************************************************/
/* 
*名称:CURRENTTIME
*参数:none
*返回值:DWORD:返回以秒为单位的一个整数
*功能:返回以秒为单位的一个整数
*注意:no                                                                */
/************************************************************************/
DLLIMPORT LONG CURRENTTIME();

#endif