#ifndef _COMM_DLL_H_
#define _COMM_DLL_H_
#include "../public/comm.h"
#include "../public/init_viriable.h"
#if COMM_DLL
# define DLLIMPORT __declspec (dllimport)
#else /* Not COMM_DLL */
# define DLLIMPORT __declspec (dllexport)
#endif /* Not COMM_DLL */

/********************************************************************
函数功能: 发送消息
参数定义: event:事件号，receiverModule:接收消息模块号
          wNumber：消息偏移量,bSync:是否同步
返回定义: 成功返回TURE，失败返回FALSE
注意事项: 无	
*********************************************************************/
//DLLIMPORT BOOL COM_SEND_MSG(WORD SenssionID,MESSAGE *pMsg,WORD event,UINT receiverModule,BOOL bSync);
DLLIMPORT BOOL SEND_MSG(WORD event,UINT receiverModule,void *data,WORD len,WORD ref,BOOL sync, BYTE far *ack,WORD ackLen);
DLLIMPORT BOOL SEND_SOCKET_MSG(WORD event,DWORD iThreadId,void *data,WORD len,WORD ref,BOOL sync, BYTE far *ack,WORD ackLen);

/********************************************************************
函数名称：Update_SEND_MSG
函数功能: 更新并发送消息
参数定义: wNumber：消息偏移量
		  event:事件号
		  pMsg：消息内容
		  receiverModule:接收消息模块号
          bSync:是否同步
		  bDelete:更新消息方式，TRUE为清楚消息内容，FALSE为更新消息内容
返回定义: 成功返回TRUE,失败返回FALSE
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL Update_SEND_MSG(WORD wNumber,WORD event,MESSAGE *pMsg,UINT receiverModule,BOOL bSync,BOOL bDelete);

/********************************************************************
函数功能: 注册一个线程
参数定义: 线程模块号
返回定义: 成功返回TURE，失败返回FALSE
注意事项: 注册名必须唯一，其他线程可以通过该名称获得TID	
*********************************************************************/
DLLIMPORT BOOL RegisterThread(UINT mId);


/********************************************************************
函数功能: 释放已经注册过一个线程
参数定义: 线程模块号
返回定义: 释放成功返回TRUE，失败返回FALSE
注意事项: 无
*********************************************************************/
DLLIMPORT BOOL UnRegisterThread(UINT mId);


/********************************************************************
函数功能: 注册一个进程模块
参数定义: mId：进程模块号，hwnd：进程的窗口句柄
返回定义: 成功返回TURE，失败返回FALSE
注意事项: 注册模块号必须唯一，其他线程可以通过该模块号称获得TID	
*********************************************************************/
DLLIMPORT BOOL RegisterIPC(UINT mId,HWND hwnd);


/********************************************************************
函数功能: 释放注册过的一个进程模块
参数定义: mId：进程模块号
返回定义: 成功返回TURE，失败返回FALSE
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL UnRegisterIPC(UINT mId);//release IPC
/********************************************************************
函数功能: 注册网络模块
参数定义: mId：进程模块号，nodeip：节点IP
返回定义: 成功返回TURE，失败返回FALSE
注意事项: 注册模块号必须唯一，其他线程可以通过该模块号称获得TID	
*********************************************************************/
DLLIMPORT BOOL RegisterNodeMod(UINT mId,DWORD nodeip);

/********************************************************************
函数功能: 释放注册网络模块
参数定义: mId：进程模块号
返回定义: 成功返回TURE，失败返回FALSE
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL UnRegisterNodeMod(UINT mId);
/************************************************************************
函数功能：读取消息具体内容   										
函数参数：WORD wNumber      //消息偏移量							
返回值  ：MESSAGE *         //有返回消息内容，无消息则返回空指针	
注意事项：无													
************************************************************************/
//DLLIMPORT MESSAGE *Read_Message(WORD wNumber);
DLLIMPORT BOOL COM_Read_Message(WORD wNumber,MESSAGE *pMsg);



/***********************************************************************
函数功能：分配一个会话号   					
函数参数：无         								
返回值  ：成功返回会话号>=0，失败返回-1							
注意事项：无													
***********************************************************************/
DLLIMPORT int GetSenssionID();
/***********************************************************************
函数功能：释放会话号   					
函数参数：SenssionID:会话号        								
返回值  ：成功返回TRUE，失败返回FALSE							
注意事项：无													
***********************************************************************/
DLLIMPORT BOOL ReleaseSenssionID(WORD SenssionID);



/********************************************************************
函数功能: 释放共享内存
参数定义: 无
返回定义: 无
注意事项: 无	
*********************************************************************/
DLLIMPORT void FreeDll();
DLLIMPORT void Comm_SetTraceValue(BOOL bValue);


/********************************************************************
函数功能: 事件异常记录
参数定义: wMid：事件所在模块，*function_name：函数名称，*reason：原因
返回定义: 成功返回TRUE，失败返回FALSE	
注意事项: 无	
*********************************************************************/
DLLIMPORT BOOL EventLog(const WORD wMid,const char *function_name,const char *reason);


/************************************************************************
函数功能：根据模块号得到注册ID 									
函数参数：mId:模块号         										
返回值  ：Tid *:  //TID指针									
注意事项：无													
************************************************************************/
DLLIMPORT TID  *GetTID(UINT mId); 
DLLIMPORT BOOL GetTid(UINT mId,TID *pTid);
DLLIMPORT BOOL GetMid(UINT mId,MID *pMid);
/* 导入INI文件 */
DLLIMPORT void loadINI(INI *ini,char *filename);
/*释放INI文件*/
DLLIMPORT void freeINI(INI *ini);
/*得到需要的值*/
DLLIMPORT char * GetKeyList(SECTION *keySEC,BYTE index);
/*根据名称获取指定节*/
DLLIMPORT SECTION *getSection(INI *ini,char *name);

#endif