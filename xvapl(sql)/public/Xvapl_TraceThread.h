#ifndef _XVAPL_TRACETHREAD_H_
#define _XVAPL_TRACETHREAD_H_
#include "comm.h"
#include "event.h"
#include "init_viriable.h"
#include "Commdll.h"
void Trace_ThreadProc(PVOID pVoid);
void Trace_Deal_Message(WPARAM wParam,LPARAM lParam);
BOOL Trace_WriteFileLog(MESSAGE pMessage);
#endif