#ifndef _TRACE_H_
#define _TRACE_H_


#define SPP_LOG       1
#define SPP_TRANCE    2
#define PXSSM_LOG     3
#define PXSSM_TRANCE  4
#define SPP_LOG_FILE       "Spp.log"
#define SPP_TRANCE_FILE    "SPPtrace.log"
#define PXSSM_LOG_FILE     "pXSSM.log"
#define PXSSM_TRANCE_FILE  "pXSSMtrace.log"


#include <windows.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "init_viriable.h"


void OpenFile(char *filename);
void CloseFile(FILE *fp);
void EnableTrace();
void DisaleTrace();
void TRANCE(WORD sid,BYTE filetype,const char *msg,)

#endif