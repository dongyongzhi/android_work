#ifndef _YF_DBQ_H
#define _YF_DBQ_H

#include "DBD.h"
/*unsigned __stdcall*/void  DBQThreadProc(LPVOID lpP);
unsigned __stdcall DBTestThreadProc(LPVOID lp);

extern int err_handler(PDBPROCESS, INT, INT, INT, LPCSTR, LPCSTR);
extern int msg_handler(PDBPROCESS, DBINT, INT, INT, LPCSTR, LPCSTR,LPCSTR, DBUSMALLINT);
extern BOOL TransformIP(char *addr,DWORD *dwIP,BOOL flag);

#endif