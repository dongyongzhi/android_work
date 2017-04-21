/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2008-04-12   15:31
文件名称: F:\江苏怡丰\源代码\public\WatchDefine.h
文件路径: F:\江苏怡丰\源代码\public
file base:WatchDefine.h
file ext: h
author:	  刘定文

purpose:	监视宏定义
*********************************************************************/

#ifndef _WACH_DEFINE_H_
#define _WACH_DEFINE_H_

//#define WM_LINKTEST		  50010 //方向pXSSM->SPP ，SPP->pXSSM(ACK)
//#define WM_MONITOR		  50011 // 方向Monitor->SPP
//#define WM_MONITOR_ACK	  50011 // SPP->Monitor(ACK),确认来自Monitor的握手
//#define WM_SPP			  50012 // 方向SPP->Monitor
//#define WM_SPP_ACK		  50012 // Monitor->SPP(ACK),确认来自SPP的握手
//#define WM_MONITOR_PXSSM  50013 //方向Monitor->PXSSM,
//#define WM_MONITOR_PXSSM_ACK  50013//PXSSM->Monitor(ACK)

#define MSG_WATCHDOG_CHECK   0x5110                  //监视握手消息
#define MSG_WATCHDOG_ACK     0x5112                  //监视握手应答
#define MSG_SPP              0xd113
#define MSG_SPP_ACK          0xd114
#define MSG_TRACE_ON         0xd115
#define MSG_TRACE_OFF        0xd116

#define ALT_F7            6666
#define ALT_F8            6667
#define ALT_F9            6668
#define ALT_F10           6669


#define SPP_LOG       1
#define SPP_TRANCE    2
#define PXSSM_LOG     3
#define PXSSM_TRANCE  4

#define SPP_LOG_FILE       "Spp.log"
#define SPP_TRANCE_FILE    "SPPtrace.log"
#define PXSSM_LOG_FILE     "pXSSM.log"
#define PXSSM_TRANCE_FILE  "pXSSMtrace.log"

#define MATCH_LEFT     1
#define MATCH_RIGHT    2
#define MATCH_ALL      3
#define MATCH_NONE     4
#endif