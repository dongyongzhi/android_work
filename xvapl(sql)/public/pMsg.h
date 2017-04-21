#ifndef _P_MSG_H_
#define _P_MSG_H_
#include "comm.h"

BOOL Read_Message(WORD senssionID,MESSAGE *pMsg);
BOOL Update_Message(WORD senssionID,MESSAGE *pMsg,BOOL bDelete);


#endif