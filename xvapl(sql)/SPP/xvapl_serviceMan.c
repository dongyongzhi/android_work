/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2008-12-9   16:30
文件名称: E:\XVAPL业务逻辑平台\源代码\SPP\xvapl_serviceMan.c
文件路径: E:\XVAPL业务逻辑平台\源代码\SPP
file base:xvapl_serviceMan
file ext: c
author:	  刘定文

purpose:	XVAPL与ServiceMan接口之间的通讯
*********************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <winsock.h>
#include <process.h>
#include "xvapl_serviceMan.h"
#pragma   comment(lib,"wsock32")
/********************************************************************
函数名称：OnServiceManInit
函数功能: 入口函数
参数定义: 无
返回定义: 无
创建时间: 2008-12-9 16:29
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void  OnServiceManInit(LPVOID lPvoid)
{
	xvapl_serviceman_Server_Run();
}
/********************************************************************
函数名称：TcpServer_Run
函数功能: 服务端通讯入口
参数定义: 无
返回定义: 无
创建时间: 2007-11-9 15:19
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL xvapl_serviceman_Server_Run()
{
	WORD wVersionRequested;
	WSADATA wsaData;
	int err;
	SOCKET  sock_fd,accept_fd;
	unsigned long ul=1;
	static struct sockaddr_in remote_addr,local_addr ; 
	int nRet,nLis,nbind;
	struct sockaddr_in ServerSocketAddr;
    int addrlen;
	SERVICE_INFOR pTcp;
	
	wVersionRequested = MAKEWORD( 2, 2 );
	
	err = WSAStartup( wVersionRequested, &wsaData );
	if ( err != 0 ) {
		return FALSE;
	}
	
	if ( LOBYTE( wsaData.wVersion ) != 2 ||
        HIBYTE( wsaData.wVersion ) != 2 ) {
		WSACleanup( );
		return FALSE; 
	}
	while(bRun)
	{
		sock_fd=socket(AF_INET, SOCK_STREAM, /*IPPROTO_TCP*/0);
		if (sock_fd == INVALID_SOCKET)
		{
			closesocket(sock_fd);
			Sleep(1000);
		}
		else
		{
			break;
		}
	}
	local_addr.sin_family = AF_INET;
	local_addr.sin_port = htons(SERVICEMANPORT);
	local_addr.sin_addr.s_addr = /*htonl*/(g_Node[0].node);
	nbind=bind(sock_fd,(struct sockaddr *)&local_addr,sizeof(local_addr));
	if(nbind == SOCKET_ERROR)
	{
		closesocket(sock_fd);
		return FALSE;
	}
	nLis=listen(sock_fd,10);
	if(nLis == SOCKET_ERROR)
	{
		return FALSE; 
	}
	
	nRet = ioctlsocket(sock_fd,FIONBIO,(unsigned long *) &ul);/*set 非阻塞模式，默认是阻塞模式*/
	if(nRet == SOCKET_ERROR)
	{
		return FALSE; 
	}
    addrlen=sizeof(ServerSocketAddr);
	while(bRun)
	{
		accept_fd=accept(sock_fd,(struct sockaddr *)&ServerSocketAddr,&addrlen);
		if (accept_fd == INVALID_SOCKET)
		{
			closesocket(accept_fd);
			Sleep(5000);
		}
		else
		{
			//开辟发送数据线程，接收数据线程
			pTcp.fd=accept_fd;
			pTcp.ip=ServerSocketAddr.sin_addr.S_un.S_addr;
			_beginthread(xvapl_serviceman_RunThread,0,&pTcp);
		}
	}
	
	closesocket (sock_fd) ;
    WSACleanup () ;
    return TRUE ;
}
/********************************************************************
函数名称：xvapl_serviceman_RunThread
函数功能: 接口通讯解析线程
参数定义: arg:传递的参数，网络句柄
返回定义: 无
创建时间: 2008-12-9 16:35
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void xvapl_serviceman_RunThread(void *arg)
{
	SERVICE_INFOR *pServer = (SERVICE_INFOR *)arg;
	
	BYTE buf[255];
	int len=0;
	int fd;
	DWORD ip;
	if(pServer == NULL)
	{
		_endthread();
		return;
	
	}
	fd = pServer->fd;
	ip=pServer->ip;
	while(bRun)
	{
		memset(buf,0,sizeof(buf));
        len=recv(fd,buf,255,0);
		if(len<0)
		{
			//判断错误
			{
				if (WSAEWOULDBLOCK != (WSAGetLastError ()))
				{
					closesocket (fd) ;
					break;
				}
				
			}
		}
		else if(len>0)
		{
			if(!TakeData(fd,buf,len))
				break;
		}
		Sleep(100*1000);
		
	}
	_endthread();
}

BOOL TakeData(int fd,BYTE *buf,int len)
{
	
	return TRUE;
}