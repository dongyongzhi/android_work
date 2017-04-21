#ifndef _XVAPL_SERVICEMAN_H_
#define _XVAPL_SERVICEMAN_H_
#include "public_define.h"
#include "Commdll.h"
#include "define.h"

#pragma  pack(1)
typedef struct 
{
	int fd;/*socket¾ä±ú*/
	DWORD ip;/*½ÚµãIP*/
}SERVICE_INFOR;
#pragma pack()
void OnServiceManInit(LPVOID lPvoid);
void xvapl_serviceman_RunThread(void *arg);
BOOL xvapl_serviceman_Server_Run();
BOOL TakeData(int fd,BYTE *buf,int len);
#endif
