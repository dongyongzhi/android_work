#ifndef _PROTOCOL_H_
#define _PROTOCOL_H_
#include "comm.h"
#include "init_viriable.h"
#include "Commdll.h"
BOOL JudgSum(WORD senssionID);
BYTE GetSum(BYTE *data,int len);
int UpdateFskData(BYTE *data,int datalen,BYTE step);
#endif