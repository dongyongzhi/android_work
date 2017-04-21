#include <windows.h>
#include <process.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h> 
#include <fcntl.h>
#include <shellapi.h>
#include <sys\stat.h> 
#include <Tlhelp32.h>
#include "TimerDll.h"
#include "comm.h"
#include "commdll.h"
#include "init_viriable.h"
#include "event.h"
#include "NodeComm.h"
#include "xvapl_monitor.h"
#include "xvapl_slp.h"
#include "log.h"
#include "cdr.h"
#include "DBAP.h"


//#include "../public/DBD.h"
#include "ReadConfig.h"
#include "WatchDefine.h"
#include "Xvapl_TraceThread.h"
#include "yftrace.h"
#include "pMsg.h"

#define WM_OPENWND WM_USER+2220
#define FILENAME "SPP.ini"
#define MONITOR_NAME "YFWATCHDOG"
#define FILE_NAME "yfdog.exe"
HANDLE hPublicMapping;
HANDLE hMessageMapping;
HANDLE hNodeMapping;
HANDLE hModuleMapping;
HANDLE hReBufferMapping;
HANDLE hSenssionMapping;

HANDLE hSidMapping;
HANDLE hOffsetMapping;
HWND    hMonitorWnd=NULL;//被监视端的窗口句柄MONITOR
int		gProgramTime=0;//连续握手没握上次数
#define TIMER_LEN  5000             // 握手消息检测发送时间间隔
#define MAX_SEND_CHECK_COUNT 3      // 被监视的程序不应答握手消息达到此值，重起所有被监视程序
MESSAGE g_Slave_Message[MESSAGE_MAX];


LRESULT APIENTRY WndProc (HWND, UINT, WPARAM, LPARAM) ;
BOOL InitialAll();
BOOL Init_Merry();
BOOL DetachMerry();
BOOL ReadSppIni();
BOOL StartAllThread();
BOOL NetCommInit();
void SetRegisterHotKey(HWND hwnd);
void FreeHotKey(HWND hwnd);
int GetKeyValue(SECTION *sec,int bKey);
void Monitor(int event, LPARAM lParam);
void StartTraceThread();
BOOL StartMonitor();
void ReStartProgram();
void Spp_Deal_Message(WPARAM wParam,LPARAM lParam);
void Spp_Send_NodeCom(WPARAM wParam,LPARAM lParam);
void WriteLog();

#define SYSTRAY_ICON_BASE 2222

int WINAPI WinMain (HINSTANCE hInstance, HINSTANCE hPrevInstance,
					
					PSTR szCmdLine, int iCmdShow)
					
{
	
	static TCHAR szAppName[] = TEXT (MAIN_WINDOW_TITLE) ;
	HWND                    hwnd ;
	MSG                     msg ;	
	WNDCLASS                wndclass ;
	BOOL bDll=FALSE;
	HWND hwnd1 = FindWindow(szAppName,szAppName);//如果存在，不再重启
// 	g_Trace=EnableTrace(TRUE);//打开重要日志记录文件

	if(hwnd1!=NULL)
	{
		YF_LOG_SPP "SPP已经启动，试图启动程序失败");
		return 0;
	}
	wndclass.style                               = CS_HREDRAW | CS_VREDRAW ;
	
	wndclass.lpfnWndProc                         = WndProc ;
	
	wndclass.cbClsExtra                          = 0 ;
	
	wndclass.cbWndExtra                          = 0 ;
	
	wndclass.hInstance                           = hInstance ;
	
	wndclass.hIcon                              = LoadIcon (NULL, IDI_APPLICATION) ;
	
	wndclass.hCursor                             = LoadCursor (NULL, IDC_ARROW) ;
	
	wndclass.hbrBackground              = (HBRUSH) GetStockObject (WHITE_BRUSH) ;
	
	wndclass.lpszMenuName                = NULL ;
	
	wndclass.lpszClassName               = szAppName ;
	


	if (!RegisterClass (&wndclass))
        
	{
		return 0; 
	} 
	InitialAll();
	if(!Init_Merry())
	{
		return 0;

	}
	ReadSppIni();	
	hwnd = CreateWindow ( szAppName, TEXT (MAIN_WINDOW_TITLE),
        
		WS_OVERLAPPEDWINDOW,
        
		CW_USEDEFAULT, CW_USEDEFAULT,
        
		CW_USEDEFAULT, CW_USEDEFAULT,
        
		NULL, NULL, hInstance, NULL) ;
	
	ShowWindow (hwnd, iCmdShow) ;
	UpdateWindow (hwnd) ;
	g_hWnd = hwnd;
	g_Public->g_hWnd =hwnd;
	if(!RegisterIPC(MID_COM,hwnd))
	{
		YF_LOG_SPP "E00000002:模块注册失败");
	}
//	if(g_Node[0].isSPP)//SPP server is 1,then start all thread
	{
		INIT_TIMER();
		StartAllThread();
	}
//	INIT_TIMER();
// 	StartAllThread();
	NetCommInit();//start ne,t thread
	YF_LOG_SPP "SPP started");
	WriteLog();
	while (GetMessage (&msg, NULL, 0, 0))   
	{    
		TranslateMessage (&msg);      
		DispatchMessage (&msg);   
	}
	return msg.wParam ;	
}
BOOL systray_add(HWND hwnd, UINT uID, HICON hIcon, LPSTR lpszTip)
{
    NOTIFYICONDATA tnid;
    tnid.cbSize = sizeof(NOTIFYICONDATA);
    tnid.hWnd = hwnd;
    tnid.uID = uID+SYSTRAY_ICON_BASE;
    tnid.uFlags = NIF_ICON | NIF_TIP | NIF_MESSAGE;
    tnid.uCallbackMessage = WM_OPENWND;     //定义发送到窗体的消息
    tnid.hIcon = hIcon;
//  tnid.dwState=NIS_SHAREDICON;
//  tnid.uVersion=NOTIFYICON_VERSION;
    lstrcpyn(tnid.szTip,lpszTip,sizeof(tnid.szTip)-1);
    return (Shell_NotifyIcon(NIM_ADD, &tnid));
}
BOOL systray_del(HWND hwnd, UINT uID) {
    NOTIFYICONDATA tnid;
    tnid.cbSize = sizeof(NOTIFYICONDATA);
    tnid.hWnd = hwnd;
    tnid.uID = uID+SYSTRAY_ICON_BASE;
    return(Shell_NotifyIcon(NIM_DELETE, &tnid));
}
/********************************************************************
函数名称：WndProc
函数功能: 消息处理
参数定义: 无
返回定义: 无
创建时间: 2007-11-21 11:11
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

LRESULT APIENTRY WndProc ( HWND hwnd, UINT message, WPARAM wParam, LPARAM lParam)

{
	MESSAGE pMsg;

	switch (message)
	{
		
	case evIPCMessage:

		if(COM_Read_Message((WORD)wParam,&pMsg))
		{
			SEND_MSG(evThreadMessage,pMsg.head.receiver.mId,(void *)&pMsg,sizeof(MESSAGE),(WORD)lParam,FALSE,NULL,0);
		}
		break;

	case evSocketMessage:

		Spp_Send_NodeCom(wParam,lParam);
		break;

	case evThreadMessage:
	
		Spp_Deal_Message(wParam,lParam);

		break;

    case WM_CREATE:
		break;
	case WM_TIMER :
		{
			Monitor(WM_TIMER,lParam);
		}
		break;
	case   WM_SIZE:
		if (wParam==SIZE_MINIMIZED)
		{
			systray_add(hwnd,1,LoadIcon(NULL,IDI_APPLICATION),"SPP通讯模块V1.0.0");
			ShowWindow(hwnd,SW_HIDE);
		}	
		break;
    case WM_OPENWND:
		{
			if(lParam == WM_LBUTTONDOWN)// 点击系统托盘栏的图标时，在5.0前版本是发送WM_LBUTTONDOWN消息，不同版本的比较在MSDN中查找Shell_NotifyIcon
			{
				systray_del(hwnd,1);
				ShowWindow(hwnd,SW_SHOW);     //一定要这样showwindow两次才能激活窗体，真是奇怪，可能是在SW_HIDE状态一定要SW_SHOW一下才行吧
				ShowWindow(hwnd,SW_SHOWNORMAL);	
			}
		}
		break;
	case MSG_TRACE_ON:
		{
			
				g_Trace = EnableTrace(TRUE);
//				SetTraceValue(g_Trace);
				YF_TRACE_SPP -1,"","SPP启动跟踪");			
		}
		break;
	case MSG_TRACE_OFF:
		{
			YF_TRACE_SPP -1,"","SPP停止跟踪");
			g_Trace = EnableTrace(FALSE);
//			SetTraceValue(g_Trace);
		}
		break;

	case MSG_WATCHDOG_CHECK://接收来自监视程序的握手（心跳）
		{
				WriteLog();
	
			Monitor(MSG_WATCHDOG_CHECK,lParam);
		}
		break;
	case MSG_SPP://接受来自PXSSM的握手
		{
			Monitor(MSG_SPP,lParam);
		}
		break;
	case  WM_DESTROY:
		{
			DetachMerry();
			FreeHotKey(hwnd);
			YF_LOG_SPP "SPP主程序退出");
			EnableTrace(FALSE);//关闭重要日志记录文件句柄
			PostQuitMessage (0) ;
			return 0 ;
		}
		break;
	default:
		break;
        
	}
	return DefWindowProc (hwnd, message, wParam, lParam) ;
	
}
void Spp_Deal_Message(WPARAM wParam,LPARAM lParam)
{
	BOOL bFind = FALSE;
	MESSAGE pMsg;
	
	if(lParam>=MESSAGE_MAX)
		return;
	bFind = COM_Read_Message((WORD)wParam,&pMsg);
	if(!bFind)
		return;
		
	switch(pMsg.head.event)
	{
	case evInitial://初始化
		bInital = TRUE;
		break;
	case evTraceStatus://跟踪控制
		{
			EnableTrace_Spp(pMsg.data[0]);
			Comm_SetTraceValue(g_Trace_Spp);
		}
		break;
	
	default:
		break;
	}
	return;
}
void Spp_Send_NodeCom(WPARAM wParam,LPARAM lParam)
{
	TID tId;
//	if(lParam>=MESSAGE_MAX)
//		return;
	if(GetTid(MODULE_MAX,&tId))
	{
		 PostThreadMessage(tId.module,evSocketMessage,wParam,lParam);
	}
	return;
}
/********************************************************************
函数名称：GetKeyValue
函数功能: 读取INI文件某一项
参数定义: sec：节点，bKey：项
返回定义: 无
创建时间: 2008-4-13 11:58
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

int GetKeyValue(SECTION *sec,int bKey)
{
	char *getvalue=NULL;
	getvalue=GetKeyList(sec,(BYTE)bKey);
	if(getvalue == NULL)
	{
		return 0;
	}
	else
	{
		return atoi(getvalue);
	}
}
void WriteLog()
{
	SYSTEMTIME     Clock;
	FILE	    *fp=NULL;
	fp = fopen ("C:\\yfcomm\\log\\test.log","w");

	if ( fp == NULL )
		return ;

	GetLocalTime(&Clock);
	fprintf (fp, "%04d-%02d-%02d %02d:%02d:%02d:%03d have message\n",
			Clock.wYear,Clock.wMonth, Clock.wDay,
		    Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds);
	fclose (fp);
}
/********************************************************************
函数名称：InitialAll
函数功能: 初始化各个变量
参数定义: 
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2007-11-5 22:12
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL InitialAll()
{
	int i;
	bRun = TRUE;
	bWritConfig = FALSE;
	bLoadService = FALSE;
	g_Public = NULL;
	g_hWnd = 0;
	g_Message = NULL;
	g_Module = NULL;
	g_Node = NULL;
	g_dwNode = 0;
	for(i=0;i<MESSAGE_MAX;i++)
	{
		memset(&g_Slave_Message[i],0,sizeof(MESSAGE));
	}
	return TRUE;
}
/********************************************************************
函数名称：ReadSppIni
函数功能: 读配置文件
参数定义: 无
返回定义: succed:true,else false
创建时间: 2007-12-7 18:09
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL ReadSppIni()
{
	
	SECTION *sec;
	INI ini;
	BYTE i,j;
	char pChar[32];
//	SPP_CDR cdr;
	BYTE bModule;
	sprintf(pChar,"C:\\yfcomm\\ini\\%s",FILENAME);
	loadINI(&ini,pChar);
	/*得到[COMM]*/
	sec=getSection(&ini,INI_SPP_COMM);
	if(sec==NULL)
	{
		YF_LOG_SPP "E00000000:Read %s fail",FILENAME);
		return FALSE;
	}
	{
		s_basic.bNodeNum=GetKeyValue(sec,0);
		s_basic.wAttri.att.isCdr=(WORD)GetKeyValue(sec,1);
		s_basic.wAttri.att.isCdrToSql=(WORD)GetKeyValue(sec,2);
	}
	/*得到[Node]*/
	for(i=0;i<s_basic.bNodeNum;i++)
	{
		sprintf(tmpSection,"%s%d",INI_SPP_NODE,i);
		sec=getSection(&ini,tmpSection);
		if(sec==NULL)
		{
			YF_WARN "E00000000:Read %s fail",FILENAME);

			return FALSE;
		}
		{	
			g_Node[i].node=inet_addr(GetKeyList(sec,0));
			if(i==0)
			{
				g_dwNode = g_Node[i].node;
				g_Public->g_dwNode = g_Node[i].node;
			}
			g_Node[i].isSPP =(BOOL)GetKeyValue(sec,1);
			g_Node[i].isServer = (BOOL)GetKeyValue(sec,2);
			g_Node[i].bModuleNum = (BYTE)GetKeyValue(sec,3);
			for(j=0;j<g_Node[i].bModuleNum;j++)
			{
				bModule = (BYTE)GetKeyValue(sec,4+j);
				if(SYSMODULE<=bModule<MODULE_MAX)//确定是否系统模块也要写入其中
				{
					g_Module[bModule].mId = bModule;
					g_Module[bModule].isSysModule = FALSE;
				}
			}
			
		}
	}
	/*得到[DATABASE]*/
	sec=getSection(&ini,INI_SPP_SQL);
	if(sec==NULL)
	{
		YF_WARN "W00000000:Read %s fail",FILENAME);

		return FALSE;
	}
	s_database.ComputerIp=inet_addr(GetKeyList(sec,0));
	strcpy(s_database.pUserName,GetKeyList(sec,1));
	strcpy(s_database.pPassWd,GetKeyList(sec,2));
	/*得到[IPP]*/
	sec=getSection(&ini,INI_SPP_IPP);
	if(sec==NULL)
	{
		YF_WARN "E00000000:Read %s fail",FILENAME);

		return FALSE;
	}
	s_basic.bIppNum=(BYTE)GetKeyValue(sec,0);
	g_Ipp = (SPP_IPP *)malloc(sizeof(SPP_IPP) * s_basic.bIppNum);
	/*得到[Ipp]*/
	for(i=0;i<s_basic.bIppNum;i++)
	{
		sprintf(tmpSection,"%s%d",INI_SPP_IPPX,i);
		sec=getSection(&ini,tmpSection);
		if(sec==NULL)
		{
		YF_WARN "E00000000:Read %s fail",FILENAME);

			return FALSE;
		}
		g_Ipp[i].id = GetKeyValue(sec,0);
		strcpy(g_Ipp[i].pName,GetKeyList(sec,1));						
		g_Ipp[i].isServer = (BOOL)GetKeyValue(sec,2);
		g_Ipp[i].dIp=inet_addr(GetKeyList(sec,3));
		g_Ipp[i].wPort = (WORD)GetKeyValue(sec,4);
	}
	/*得到[Version]*/
	sec=getSection(&ini,INI_SPP_VERSION);
	if(sec==NULL)
	{

		YF_WARN "E00000000:Read %s fail",FILENAME);
		return FALSE;
	}
	strcpy(s_basic.version,GetKeyList(sec,0));
	/*end*/
	freeINI(&ini);
	return TRUE;
	
}
/********************************************************************
函数名称：StartAllThread
函数功能: 启动各个功能线程
参数定义: 无
返回定义: 成功：TRUE，失败：FALSE
创建时间: 2007-11-21 11:14
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL StartAllThread()
{
	//start slp thread
//	StartTraceThread();
	StartMonitor();//启动监视线程

	if(g_Node[0].isSPP)//SPP server is 1,then start all thread
	{
		_beginthreadex(NULL,0,DBAPThreadProc,NULL,0,NULL);
		_beginthread(XSCM_ThreadProc,0,NULL);
		_beginthread(logProc,0,NULL);
		_beginthreadex(NULL,0,Cdr_proc,NULL,0,NULL);
	}
	
	return TRUE;
}
/********************************************************************
函数名称：NetCommInit
函数功能: 启动网络通讯线程
参数定义: 无
返回定义: 无
创建时间: 2007-12-7 11:16
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL NetCommInit()
{
	if(s_basic.bNodeNum>1)//如果节点>1
	{
		_beginthread(OnInitNetWork,0,NULL);
		return TRUE;
	}
	return FALSE;

}
/********************************************************************
函数名称：StartTraceThread
函数功能: 启动跟踪日志线程
参数定义: 无
返回定义: wu
创建时间: 2009-3-2 14:15
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void StartTraceThread()
{
	_beginthread(Trace_ThreadProc,0,NULL);
}
/********************************************************************
函数名称：StartMonitor
函数功能: 启动监视程序
参数定义: 无
返回定义: 成功返回TRUE,失败返回FALSE
创建时间: 2008-12-29 9:58
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL StartMonitor()
{
	_beginthread(xvapl_monitor_run,0,NULL);
	return TRUE;
}
/********************************************************************
函数名称：Init_Merry
函数功能: 初始化共享内存
参数定义: 无
返回定义: 成功：TRUE，失败：FALSE
创建时间: 2007-11-15 15:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL Init_Merry()
{
	DWORD dw=MESSAGE_MAX*sizeof(MESSAGE);
	int i;
	//////////////////////////////////////////////////////////////////////////
	/*记录窗口和节点信息*/
	//////////////////////////////////////////////////////////////////////////
	hPublicMapping = CreateFileMapping(INVALID_HANDLE_VALUE,NULL,PAGE_READWRITE,0,1*sizeof(PUBLIC),SPP_PUBLIC);
	if(hPublicMapping==NULL)
	{
		YF_LOG_SPP "E00000001:Init_Merry失败");
		return FALSE;
	}
	g_Public=(PUBLIC *)MapViewOfFile(hPublicMapping,FILE_MAP_ALL_ACCESS,0,0,0);
	g_Public->g_hWnd = NULL;
	g_Public->g_dwNode =0;
	//////////////////////////////////////////////////////////////////////////
	//消息内存申请
	//////////////////////////////////////////////////////////////////////////
	
	hMessageMapping=CreateFileMapping(INVALID_HANDLE_VALUE,NULL,PAGE_READWRITE,0,MESSAGE_MAX*sizeof(MESSAGE),SPP_MESSAGE);
	if(hMessageMapping==NULL)
	{
		YF_LOG_SPP "E00000001:Init_Merry失败");

		return FALSE;
	}
	g_Message=(MESSAGE *)MapViewOfFile(hMessageMapping,FILE_MAP_ALL_ACCESS,0,0,0);
	for(i=0;i<MESSAGE_MAX;i++)
	{
		memset(g_Message+i,0,sizeof(MESSAGE));
	}

	
	//////////////////////////////////////////////////////////////////////////
	//节点内存申请
	//////////////////////////////////////////////////////////////////////////
	hNodeMapping=CreateFileMapping(INVALID_HANDLE_VALUE,NULL,PAGE_READWRITE,0,NODE_MAX*sizeof(NID),SPP_NODE);
	if(hNodeMapping==NULL)
	{
		YF_LOG_SPP "E00000001:Init_Merry失败");

		return FALSE;
	}
	g_Node=(NID *)MapViewOfFile(hNodeMapping,FILE_MAP_ALL_ACCESS,0,0,0);
	for(i=0;i<NODE_MAX;i++)
	{
		memset(g_Node+i,0,sizeof(NID));
	}
	//////////////////////////////////////////////////////////////////////////
	//模块内存申请
	//////////////////////////////////////////////////////////////////////////
	hModuleMapping=CreateFileMapping(INVALID_HANDLE_VALUE,NULL,PAGE_READWRITE,0,(MODULE_MAX+1)*sizeof(MID),SPP_MODULE);
	if(hModuleMapping==NULL)
	{
		YF_LOG_SPP "E00000001:Init_Merry失败");

		return FALSE;
	}
	g_Module=(MID *)MapViewOfFile(hModuleMapping,FILE_MAP_ALL_ACCESS,0,0,0);
	for(i=0;i<MODULE_MAX+1;i++)
	{
		g_Module[i].mId=i;
		memset(g_Module+i,0,sizeof(MID));
	}
	
	//////////////////////////////////////////////////////////////////////////
	//记录消息空白内存申请
	//////////////////////////////////////////////////////////////////////////


	hOffsetMapping = CreateFileMapping(INVALID_HANDLE_VALUE,NULL,PAGE_READWRITE,0,1*sizeof(SID_POSITION),SPP_OFFSET);
	if(hOffsetMapping==NULL)
	{
		YF_LOG_SPP "E00000001:Init_Merry失败");
		return FALSE;
	}
	g_Offset_Position=(SID_POSITION *)MapViewOfFile(hOffsetMapping,FILE_MAP_ALL_ACCESS,0,0,0);
	g_Offset_Position->offset = 0;
	for(i=0;i<MESSAGE_MAX;i++)
	{
		g_Offset_Position->bSign[i] = 0;
	}

	hSidMapping = CreateFileMapping(INVALID_HANDLE_VALUE,NULL,PAGE_READWRITE,0,1*sizeof(SID_POSITION),SPP_SID);
	if(hSidMapping==NULL)
	{
		return FALSE;
	}
	g_Sid_Position=(SID_POSITION *)MapViewOfFile(hSidMapping,FILE_MAP_ALL_ACCESS,0,0,0);
	g_Sid_Position->offset = 0;
	for(i=0;i<MESSAGE_MAX;i++)
	{
		g_Sid_Position->bSign[i] = 0;
	}
	return TRUE;
}

/********************************************************************
函数名称：DetachMerry
函数功能: 释放共享内存和其他内存
参数定义: 无
返回定义: 无
创建时间: 2007-11-15 15:33
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL DetachMerry()
{
	
	UnmapViewOfFile(g_Message);
	UnmapViewOfFile(g_Node);
	UnmapViewOfFile(g_Module);
	UnmapViewOfFile(g_Sid_Position);
	UnmapViewOfFile(g_Offset_Position);
	UnmapViewOfFile(g_Public);

	g_Public = NULL;
	g_Message = NULL;
	g_Node = NULL;
	g_Module = NULL;
	g_Sid_Position = NULL;
	g_Offset_Position = NULL;
	
	CloseHandle(hPublicMapping);
	CloseHandle(hMessageMapping);
	CloseHandle(hNodeMapping);
	CloseHandle(hModuleMapping);
	
	CloseHandle(hSidMapping);
	CloseHandle(hOffsetMapping);
	
	return TRUE;
}
/********************************************************************
函数名称：SetRegisterHotKey
函数功能: 注册热键
参数定义: 无
返回定义: 无
创建时间: 2008-4-13 15:53
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void SetRegisterHotKey(HWND hwnd)
{
	DWORD dwPID;
	HWND hWND2;
	char bb[256];
	UnregisterHotKey(hwnd,ALT_F7);
	UnregisterHotKey(hwnd,ALT_F8);
	GetWindowThreadProcessId(hwnd,&dwPID);
	hWND2 = OpenProcess(PROCESS_ALL_ACCESS,FALSE,dwPID);
	if(RegisterHotKey(hwnd,ALT_F7,MOD_ALT,VK_F7))
	{
		YF_LOG_SPP "SetRegisterHotKey,Set ALT+F7 success");
	}
	else
	{
		sprintf(bb,"SetRegisterHotKey,Set ALT+F7 fail,%d",GetLastError());
		YF_LOG_SPP bb);
	}
}
/********************************************************************
函数名称：FreeHotKey
函数功能: 热键注销
参数定义: 无
返回定义: 无
创建时间: 2008-4-13 15:53
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void FreeHotKey(HWND hwnd)
{
	UnregisterHotKey(hwnd,ALT_F7);
}
/********************************************************************
函数名称：monitor
函数功能: 监视程序
参数定义: event：事件号,lParam:窗口句柄
返回定义: 无
创建时间: 2008-4-13 15:57
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Monitor(int event, LPARAM lParam)
{
	HWND   hMonitorWnd = NULL;
	if(event == MSG_WATCHDOG_CHECK)//MONITOR监视握手
	{
		hMonitorWnd = (HWND) lParam;
		if (hMonitorWnd) 
		{
			PostMessage(hMonitorWnd, MSG_WATCHDOG_ACK, 0, (LPARAM)g_hWnd);
		}
	}

	if(event == MSG_SPP)
	{
		hMonitorWnd = (HWND) lParam;
		if (hMonitorWnd) 
		{
			PostMessage(hMonitorWnd, MSG_SPP_ACK, 0, (LPARAM)g_hWnd);
		}
	}
}


