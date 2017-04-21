#ifndef _COMM_H_
#define _COMM_H_
#include "comm.h"
//#include "init_global.h"

/***********************************comm*******************************/
int node_numer;//节点个数
int iHaveNodeNumber;//已经注册的节点个数


/************************************************************************
函数功能： 添加一个消息				  							
函数参数： MESSAGE *pMsg	//消息内容							
返回值  ： 成功返回消息的偏移量,否则返回-1					
注意事项： 无													
************************************************************************/
int Add_Message(void *pMsg,int len);
BOOL Clean_Message(WORD wNumber);
BOOL UpdateMessage(WORD wNumber,const MESSAGE *pMsg);

BOOL Update_Message_COM(MESSAGE *pMsg,WORD wNumber,BOOL bDelete);

BOOL SendThreadMessage(WORD event,UINT receiverModule,WORD SenssionID,WORD wNumber);
BOOL SendForkMessage(WORD event,UINT receiverModule,WORD SenssionID,WORD wNumber);
BOOL SendSocketMessage(WORD event,UINT receiverModule,WORD SenssionID,WORD sid);
//BOOL ReadRebuffer(LPDWORD wNumber);
BOOL ReadRebuffer();
BOOL WriteRebuffer(WORD wNumber);
int ReadSenssionBuffer();
BOOL WriteSenssionBuffer(WORD senssionID);

BOOL SetSenssionId(WORD offset);
void SetOffsetId(WORD offset);
int GetSenssionNumber();
int GetOffsetId();
/********************************************************************
函数名称：SetWindowsHandle
函数功能: 设置动态库依附的窗口句柄
参数定义: hWnd：窗口句柄号
返回定义: 成功返回TRUE,失败返回FALSE
注意事项: 无	
*********************************************************************/

BOOL SetWindowsHandle(HWND hWnd);
/********************************************************************
函数名称：GetWindowsHandle
函数功能: 得到本动态库依附的进程的窗口句柄
参数定义: 无
返回定义: 返回本动态库依附的进程的窗口句柄
注意事项: 无	
*********************************************************************/
HWND GetWindowsHandle();
BOOL Share_Merry();
void Detach_Merry();

HANDLE hPublicMapping;
HANDLE hMessageMapping;
HANDLE hNodeMapping;
HANDLE hModuleMapping;
HANDLE hReBufferMapping;
HANDLE hSenssionMapping;
HANDLE hSidMapping;
HANDLE hOffsetMapping;
/***********************************timer*******************************/
#pragma pack(1)
typedef struct tagTMCtrl
{
	TMID    tmID;
	struct tagTMCtrl  *prev,*next;
} TMCtrl;
#pragma pack()

/*  分配内存，获得一个 TMCtrl，插入到双向链表尾部   */
BOOL NewTimer(TMID  tmid);

/*  从链表中查找一个定时器控制块   */
TMCtrl * FindTimer(TMID tmid);
/*  从链表中找到该定时器，删除，然后释放内存  */
BOOL DeleteTimer(TMID  tmid);
/*  定时器到，触发定时事件，然后删除该定时器控制块  */
void TriggerTimer(TMCtrl * pTmctrl);
unsigned __stdcall TimerThread(void * lpP);

/***********************************timer*******************************/

#endif