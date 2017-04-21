/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2007-11-9   13:24
文件名称: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP\NodeComm.c
文件路径: E:\商品信息查询分析系统业务逻辑处理项目\源代码\SPP
file base:NodeComm
file ext: c
author:	  刘定文

  purpose:	节点通讯主程序
*********************************************************************/
#include <stdio.h>
#include <stdlib.h>
#include <winsock.h>
#include <process.h>
#include "../public/NodeComm.h"
#pragma   comment(lib,"wsock32")
#define evSocketTestAck 50000
/********************************************************************
函数名称：OnInitNetWork
函数功能: 入口函数
参数定义: 无
返回定义: 无
创建时间: 2007-11-9 15:17
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void  OnInitNetWork(LPVOID lPvoid)
{
	if(g_Node!=NULL)
	{
		if((g_Node+0)->isServer)/*server*/
		{
			TcpServer_Run();
		}
		else
		{
			TcpCustom_Run();/*custom*/
		}
	}

	//	return  TRUE;
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
BOOL TcpServer_Run()
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
	TCP_INFOR pTcp;
	
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
	//////////////////////////////////////////////////////////////////////////
	_beginthread(Tcp_ServControlThread,0,0);

	//////////////////////////////////////////////////////////////////////////
	
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
	local_addr.sin_port = htons(8888);
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
		closesocket(sock_fd);
		return FALSE; 
		//Failed to put the socket into nonblocking mode
	}
	
	nRet = ioctlsocket(sock_fd,FIONBIO,(unsigned long *) &ul);/*set 非阻塞模式，默认是阻塞模式*/
	if(nRet == SOCKET_ERROR)
	{
		closesocket(sock_fd);
		return FALSE; 
		//Failed to put the socket into nonblocking mode
	}
    addrlen=sizeof(ServerSocketAddr);
	while(bRun)
	{
		accept_fd=accept(sock_fd,(struct sockaddr *)&ServerSocketAddr,&addrlen);
		if (accept_fd == INVALID_SOCKET)
		{
			closesocket(accept_fd);
			Sleep(1000);
		}
		else
		{
		//开辟发送数据线程，接收数据线程
			ioctlsocket(accept_fd,FIONBIO,(unsigned long *) &ul);/*set 非阻塞模式，默认是阻塞模式*/
			pTcp.fd=accept_fd;
			pTcp.ip=ServerSocketAddr.sin_addr.S_un.S_addr;
			pTcp.bThread=TRUE;
			
			
			_beginthread(Tcp_SendThread,0,&pTcp);
			Sleep(150);
//			_beginthread(Tcp_RecvThread,0,&pTcp);
		}
	}
	
	closesocket (sock_fd) ;
    WSACleanup () ;
    return  FALSE;
}
/********************************************************************
函数名称：Tcp_ServControlThread
函数功能: 服务器消息转发
参数定义: 无
返回定义: 成功返回TRUE,拾芊祷谾ALSE
创建时间: 2009-6-12 15:11
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

void Tcp_ServControlThread(void *arg)
{
	MSG   msg ;
	RegisterThread(MODULE_MAX);
	while(bRun)
	{
	
		GetMessage(&msg,NULL,0,0);
		if(msg.message!=evSocketMessage)
		{
			continue;
		}
		else//处理消息
		{
			Deal_Server_Message(msg.wParam,msg.lParam);
		}
	}
	UnRegisterThread(MODULE_MAX);
	_endthread();
}

void Deal_Server_Message(WPARAM wParam,LPARAM lParam)
{
	MESSAGE pMsg;
	DWORD iThreadId;
	memset(&pMsg,0,sizeof(pMsg));
	if(COM_Read_Message((WORD)wParam,&pMsg))
	{
		iThreadId = FindNodeModule(pMsg.head.receiver.mId);
		SEND_SOCKET_MSG(evSocketMessage,iThreadId,(void *)&pMsg,sizeof(MESSAGE),(WORD)lParam,FALSE,NULL,0);
	}
}

/********************************************************************
函数名称：GetServerIp
函数功能: 查找服务器IP
参数定义: 无
返回定义: 返回DWROD值
创建时间: 2007-12-4 15:45
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DWORD GetServerIp()
{
	int i;
	for(i=0;i<s_basic.bNodeNum;i++)
	{
		if(g_Node[i].isServer)
		{
			return g_Node[i].node;
		}
	}
	return 0;
}
/********************************************************************
函数名称：TcpCustom_Run
函数功能: 客户端通讯入口
参数定义: 无
返回定义: 无
创建时间: 2007-11-9 15:20
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL TcpCustom_Run()
{
	WORD wVersionRequested;
	WSADATA wsaData;
	int err;
	SOCKET  sock_fd;
	unsigned long ul=1;
	int nRet,bconn;
	static struct sockaddr_in remote_addr;
	static struct sockaddr_in local_addr;
	TCP_INFOR pTcp;
	fd_set rdset;
	struct timeval tv;
	int m_error = -1;
	BOOL m_ret;
	int len = sizeof(int);
	DWORD ip = GetServerIp();
	wVersionRequested = MAKEWORD( 2, 2 );
	
	err = WSAStartup( wVersionRequested, &wsaData );
	if ( err != 0 ) {
		/* Tell the user that we could not find a usable */
		/* WinSock DLL.                                  */
		return FALSE;
	}
	
	
	if ( LOBYTE( wsaData.wVersion ) != 2 ||
        HIBYTE( wsaData.wVersion ) != 2 ) {
		/* Tell the user that we could not find a usable */
		/* WinSock DLL.                                  */
		WSACleanup( );
		return FALSE; 
	}
	
	while(bRun)
	{
		sock_fd=socket(AF_INET, SOCK_STREAM, /*IPPROTO_TCP*/0);
		if (sock_fd == INVALID_SOCKET)
		{
			closesocket(sock_fd);
			Sleep(5000);
			continue;
		}
		
		nRet = ioctlsocket(sock_fd,FIONBIO,(unsigned long *) &ul);/*非阻塞模式，默认是阻塞模式*/
		if(nRet == SOCKET_ERROR)
		{
			//return FALSE; 
			closesocket(sock_fd);
			continue;
			//Failed to put the socket into nonblocking mode
		}
		
		
		memset(&remote_addr,0,sizeof(remote_addr));
		remote_addr.sin_family = AF_INET ;
		remote_addr.sin_port = htons (8888) ; 
		remote_addr.sin_addr.S_un.S_addr = ip ;
		bconn = connect(sock_fd,(SOCKADDR *)&remote_addr,sizeof(remote_addr));
		if( bconn==SOCKET_ERROR)
		{
			FD_ZERO(&rdset);
			FD_SET(sock_fd,&rdset);
			tv.tv_sec=0;
			tv.tv_usec=500*1000;
			if (select(sock_fd + 1, NULL, &rdset, NULL, &tv) > 0)
			{
				getsockopt(sock_fd, SOL_SOCKET, SO_ERROR, (char *)&m_error, &len);
				if (m_error == 0)
					m_ret = TRUE;
				else
					m_ret = FALSE;
			}
			else
			{
				m_ret = FALSE;
				closesocket (sock_fd) ;
			}
			//			
		}
		if(m_ret)
		{
			//开辟发送数据线程，接收数据线程
			pTcp.fd=sock_fd;
			pTcp.ip=remote_addr.sin_addr.S_un.S_addr;
			pTcp.bThread=FALSE;
// 			_beginthread(Tcp_RecvThread,0,&pTcp);
			Tcp_SendThread_Custom(&pTcp);
			closesocket(sock_fd);
			
		}
		closesocket(sock_fd);
		Sleep(1*1000);
	}
	closesocket (sock_fd) ;
    WSACleanup () ;
    return TRUE ;
	
}
BOOL WriteNodeCommData(BYTE *data,int len,BOOL bTx,DWORD ip)
{
	char content[2048];
	char Title[256];
	int i;
	FILE *fp;
	SYSTEMTIME     Clock;
	int datalen;
	memset(content,0,sizeof(content));
	memset(Title,0,sizeof(Title));
	GetLocalTime(&Clock);
	sprintf(content,"%04d-%02d-%02d %02d:%02d:%02d:%03d\n",Clock.wYear,Clock.wMonth, Clock.wDay,Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds);
	sprintf(Title,"C:\\yfcomm\\log\\NodeCommData%08x.log",ip);
	fp=fopen(Title,"a");
	if(fp!=NULL)
		fwrite(content,strlen(content),1,fp);

	if(bTx)
	{
		sprintf(content,"TX:");
	}
	else
	{
		sprintf(content,"RX:");
	}
	for(i=0;i<len;i++)
	{
		sprintf(content+i*3+3,"%02x ",*(data+i));
	}
	datalen = strlen(content);
	sprintf(content+datalen,"\n");
	if(fp!=NULL)
	{
		fwrite(content,strlen(content),1,fp);
		fclose(fp);

	}
	return TRUE;
	
}
/********************************************************************
函数名称：Tcp_SendThread
函数功能: 网络发送线程
参数定义: arg:网络socket句柄
返回定义: 无
创建时间: 2007-11-9 15:22
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Tcp_SendThread(void *arg)
{
	TCP_INFOR *pTcp=(TCP_INFOR *)arg;
	int fd = pTcp->fd;
	DWORD ip=pTcp->ip;
	BOOL bThread=pTcp->bThread;
	MSG   msg ;
	int len;
	int Node_number;
	int loop_time=0;
	BOOL bTestLink = FALSE;
	BYTE buffer[255];
	BOOL bBreak;
	int i;
	BYTE buf[512];
	char beginbuf[256];
	int nodatacome = 0;
	memset(buffer,0,sizeof(buffer));
	Node_number = RegisterNodeThread(ip,GetCurrentThreadId());
	if(!bThread) 
		RegisterThread(MODULE_MAX);
	sprintf(beginbuf,"begin send and recv data");
	WriteNodeCommData(beginbuf,strlen(beginbuf),FALSE,ip);
	//	Node_number = RegisterNodeThread(g_Node[0].node,GetCurrentThreadId());
	while(bRun)
	{
		memset(&msg,0,sizeof(MSG));
		len = 0;
		bBreak = FALSE;
		if(!GetNodeState(Node_number))//serach node status
		{
			closesocket(fd);
			break;
		}
		if(PeekMessage(&msg,NULL,0,0,PM_REMOVE))
		{
			if(msg.message==evSocketMessage)//网络通讯发送
			{
				len = SendMessageData(ip,fd,(WORD)msg.lParam,(WORD)msg.wParam);
			}
			else if(msg.message == evSocketTestAck)//连接确认
			{
				len = SendData(ip,fd,(WORD)msg.lParam,/*(BOOL)msg.wParam*/FALSE,bThread);
			}
		}
		else //控制测试报文发送
		{
			if((loop_time%20)==0)
			{
				bTestLink = TRUE;
				len = SendData(ip,fd,(WORD)msg.lParam,bTestLink,bThread);
			}
		}
		if(len < 0)
		{
			if (WSAEWOULDBLOCK != (WSAGetLastError ()))//判断错误
			{
				bBreak = TRUE;
				break;
			}
		}
		else
			bBreak = FALSE;
		if(bBreak)
			break;
		

//////////////////////////////////////////////////////////////////////////
		bBreak = FALSE;
		i=0;
		len = 0;
		while(i<4)
		{

			memset(buf,0,sizeof(buf));
			len=recv(fd,buf,512,0);
			if(len<0)
			{
				if (WSAEWOULDBLOCK != (WSAGetLastError ()))//判断错误
				{
					bBreak = TRUE;
					break;
				}
			}
			else if(len>0)
			{
				WriteNodeCommData(buf,len,FALSE,ip);
				RecvData_Analysis(buf,len);
				bBreak = FALSE;
				break;
			}
			Sleep(20);
			i++;
			
		}
//		if(len<=0)
//			nodatacome++;
//		else
//			nodatacome=0;
//		if(nodatacome >= 10)
//			break;
//////////////////////////////////////////////////////////////////////////
		if(bBreak)
			break;
		if(loop_time >= 10000)
		{
			loop_time = 0;
		}
		loop_time ++;
		Sleep(20);
	}
	closesocket (fd) ;
	UnRegisterNodeThread(Node_number);//清除节点注册
	Clean_Module(ip);//set all module state is 0 with this node;
	if(bThread)
	{
		_endthread();
	}
	if(!bThread) 
		UnRegisterThread(MODULE_MAX);
}
void Tcp_SendThread_Custom(void *arg)
{
	TCP_INFOR *pTcp=(TCP_INFOR *)arg;
	int fd = pTcp->fd;
	DWORD ip=pTcp->ip;
	BOOL bThread=pTcp->bThread;
	MSG   msg ;
	int len;
	int Node_number;
	int loop_time=0;
	BOOL bTestLink = FALSE;
	BYTE buffer[255];
	BOOL bBreak;
	int i;
	BYTE buf[512];
	int nodatacome = 0;
	int erron;
	memset(buffer,0,sizeof(buffer));
	Node_number = RegisterNodeThread(ip,GetCurrentThreadId());
	RegisterThread(MODULE_MAX);
	while(bRun)
	{
		memset(&msg,0,sizeof(MSG));
		
		if(!GetNodeState(Node_number))//serach node status
		{
			closesocket(fd);
			break;
		}
		//////////////////////////////////////////////////////////////////////////
		bBreak = FALSE;
		i=0;
		len = 0;
		while(i<4)
		{
			Sleep(20);
			memset(buf,0,sizeof(buf));
			len=recv(fd,buf,512,0);
			if(len<0)
			{
				erron= WSAGetLastError ();
				if (WSAEWOULDBLOCK != erron)//判断错误
				{
					bBreak = TRUE;
					break;
				}
			}
			else if(len>0)
			{
				WriteNodeCommData(buf,len,FALSE,ip);
				RecvData_Analysis(buf,len);
				bBreak = FALSE;
				break;
			}
			
			i++;
			
		}
//		if(len<=0)
//			nodatacome++;
//		else
//			nodatacome=0;
//		if(nodatacome>=10)
//			break;
//////////////////////////////////////////////////////////////////////////
		if(bBreak)
			break;
		Sleep(20);
		len = 0;
		bBreak = FALSE;
		if(PeekMessage(&msg,NULL,0,0,PM_REMOVE))
		{
			if(msg.message==evSocketMessage)//网络通讯发送
			{
				len = SendMessageData(ip,fd,(WORD)msg.lParam,(WORD)msg.wParam);
			}
			else if(msg.message == evSocketTestAck)//连接确认
			{
				len = SendData(ip,fd,(WORD)msg.lParam,/*(BOOL)msg.wParam*/FALSE,bThread);
			}
		}
		else //控制测试报文发送
		{
			if((loop_time%20)==0)
			{
				bTestLink = TRUE;
				len = SendData(ip,fd,(WORD)msg.lParam,bTestLink,bThread);
			}
		}
		if(len < 0)
		{
			if (WSAEWOULDBLOCK != (WSAGetLastError ()))//判断错误
			{
				bBreak = TRUE;
				break;
			}
		}
		else
			bBreak = FALSE;
		if(bBreak)
			break;
		
		if(loop_time >= 10000)
		{
			loop_time = 0;
		}
		loop_time ++;
	}
	closesocket (fd) ;
	UnRegisterNodeThread(Node_number);//清除节点注册
	Clean_Module(ip);//set all module state is 0 with this node;
	UnRegisterThread(MODULE_MAX);
}
int SendMessageData(DWORD ip,int fd,const WORD wNumber,WORD offset)
{
	BYTE databuf[2048];
	BYTE data[2048];
	int len = 0;
	int i;
	int sendlen;
	MESSAGE pMesg ;
	BOOL bFind = FALSE;
	memset(databuf,0,sizeof(databuf));
	memset(data,0,sizeof(data));
	memset(&pMesg,0,sizeof(MESSAGE));
	bFind = COM_Read_Message(offset,&pMesg);
	
	if(bFind )
	{
		if(pMesg.head.event == evTimer1)//超时或者无结果
		{
			databuf[10] = 0x15;
			len = 0;
		}
		else 
		{
			if((pMesg.head.event !=evODI_Message_Ack)||(pMesg.head.event !=evDBQueryAck))
			{
				databuf[10] = 0x03; //query
			}
			else
			{
				databuf[10] = 0x06; //result back
			}
			len = sizeof(MSG_HEAD)+pMesg.head.len ;
			memcpy(data,&pMesg,len);
			for(i=0;i<len;i++)
			{
				databuf[i+12]=data[i];
			}
		}
	}
	else
	{
		return -1;
	}
	*((DWORD*)(databuf + 0)) = ip;
	*((DWORD*)(databuf + 4)) = g_Node[0].node;
	*((WORD*)(databuf + 8)) = wNumber;
	databuf[11] = len;
	databuf[12+len]=GetXORCode(databuf,12+len);
	databuf[13+len] = 0x16;
	sendlen=send(fd,databuf,len+14,0);
	WriteNodeCommData(databuf,sendlen,TRUE,ip);
	return sendlen;
}

/********************************************************************
函数名称：SendData
函数功能: 组织和发送网络数据
参数定义: ip:节点号,fd：网络句柄，wNumber:消息下标，
bTestLink:是否是测试报文
返回定义: len：返回发送数据的个数
创建时间: 2007-11-12 15:43
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

int SendData(DWORD ip,int fd,const WORD wNumber,BOOL bTestLink,BOOL bServer)
{
	BYTE databuf[1024];
	BYTE len = 0;
	int i;
	int sendlen;
	BOOL bFind = FALSE;
// 	int error;
	memset(databuf,0,sizeof(databuf));
	
	if(bTestLink)
	{
		databuf[10] = 0x05;
		if(!bServer)//custom->server
		{
			for(i=0;i<MODULE_MAX;i++)
			{
				if((!(g_Module[i].isSysModule))&&(g_Module[i].state)>0)//非系统模块，且状态激活
				{
					if(g_Module[i].tId.node!=ip)
					{
						databuf[len+12]=g_Module[i].mId;
						len ++;
					}
				}
			}
		}
		else//server->custom
		{
			for(i=0;i<MODULE_MAX;i++)
			{
				if(g_Module[i].state>0)//模块状态激活
				{
					if(g_Module[i].tId.node!=ip)
					{
						databuf[len+12]=g_Module[i].mId;
						len ++;
					}
				}
			}
		}
	}
	else
		databuf[10] = 0x10;
	
	*((DWORD*)(databuf + 0)) = ip;
	*((DWORD*)(databuf + 4)) = g_Node[0].node;
	*((WORD*)(databuf + 8)) = wNumber;
	databuf[11] = len;
	databuf[12+len]=GetXORCode(databuf,12+len);
	databuf[13+len] = 0x16;
// 	error = WSAGetLastError();
	sendlen=send(fd,databuf,len+14,0);
	WriteNodeCommData(databuf,sendlen,TRUE,ip);
	return sendlen;
}
/********************************************************************
函数名称：Tcp_RecvThread
函数功能:网络接收线程
参数定义: arg:网络socket句柄
返回定义: 无
创建时间: 2007-11-9 15:22
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void Tcp_RecvThread(void *arg)
{
	
	TCP_INFOR *pTcp =(TCP_INFOR *)arg;
	int fd = pTcp->fd;
	DWORD ip=pTcp->ip;
	int Node_number = GetNodeByIp(ip);
	BYTE buf[2048];
	int len=0;
	while(bRun)
	{
		memset(buf,0,sizeof(buf));
        len=recv(fd,buf,2048,0);
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
			WriteNodeCommData(buf,len,FALSE,ip);
			RecvData_Analysis(buf,len);
		}
		Sleep(200);
		
	}
	closesocket (fd) ;
	_endthread();
	
}
/********************************************************************
函数名称：RecvData_Analysis
函数功能: 数据解析
参数定义: 无
返回定义: 无
创建时间: 2007-11-12 17:29
函数作者: 刘定文
注意事项: 无	
*********************************************************************/

BOOL RecvData_Analysis(BYTE *pBuf,int len)
{
	
	DWORD ip = 0;
	DWORD ip2 = 0;
	BYTE code;
	BYTE data_len;
	int Node_number;
	int i;
	MESSAGE pMesg ,*ppMsg;
	int wNumber;
	WORD wReceiveModule,event;
	WORD modid;
	BYTE bXorValue=GetXORCode(pBuf,len-2);
	ip=*pBuf+*(pBuf+1)*256+*(pBuf+2)*256*256+*(pBuf+3)*256*256*256;
	ip2=*(pBuf+4)+*(pBuf+5)*256+*(pBuf+6)*256*256+*(pBuf+7)*256*256*256;
	code= *(pBuf +10);
	data_len = *(pBuf +11);
	Node_number = GetNodeByIp(ip2);
	if((Node_number<0)&&(Node_number>=NODE_MAX))
	{
		return FALSE;
	}
	switch(code)
	{
	case 0x05://test link
		{
			for(i=0;i<data_len;i++)
			{
				
				modid = *(pBuf+i+12);
				RegisterNodeMod(modid,ip2);
			}
			PostThreadMessage(g_Node[Node_number].module,evSocketTestAck,0,0);//确认消息
		}
		break;
	case 0x10://test link ack
		g_Node[Node_number].isActive = TRUE;
		break;
	case 0x03://ask search 
		{
			ppMsg = (MESSAGE *)(pBuf+12);
			memcpy(&pMesg.head,pBuf+12,sizeof(pMesg.head));
			memcpy(&pMesg.data,pBuf+12+sizeof(pMesg.head),pMesg.head.len);
			wReceiveModule = (WORD)pMesg.head.receiver.mId;
			memcpy(&wNumber,pBuf+8,2);
			if(wNumber>=0)
			{
				SEND_MSG(evThreadMessage,(UINT)wReceiveModule,(void *)&pMesg,sizeof(MESSAGE),(WORD)wNumber,FALSE,NULL,0);
			}
			else
			{
				PostThreadMessage(g_Node[Node_number].module,evSocketTestAck,0,1);//否认消息
			}
		}
		break;
	case 0x06://search result
		{
			memcpy(&pMesg,pBuf+12,len-12);
			wReceiveModule = (WORD)pMesg.head.receiver.mId;
			if(g_Module[wReceiveModule].isSysModule)
			{
				event = evThreadMessage;
			}
			else
			{
				event = evIPCMessage;
			}
			memcpy(&wNumber,pBuf+8,2);
			if(wNumber>=0)
			{
				SEND_MSG(event,(UINT)wReceiveModule,(void *)&pMesg,sizeof(MESSAGE),(WORD)wNumber,FALSE,NULL,0);
			}
			
		}
		break;
	default:
		break;
	}
	return TRUE;
	
}
/********************************************************************
函数名称：Clean_Module
函数功能: 节点之间通讯中断，相应模块的状态置为0
参数定义: IP：节点号
返回定义: 无
创建时间: 2007-11-12 11:31
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
static void Clean_Module(const DWORD ip)
{
	int i;
	for(i=0;i<MODULE_MAX;i++)
	{
		if(g_Module[i].tId.node == ip)
		{
			g_Module[i].state = 0; 
		}
	}
}
/********************************************************************
函数名称：GetNodeByIp
函数功能: 根据节点IP得到节点下标
参数定义: ip:
返回定义: 成功返回下标，失败返回-1；
创建时间: 2007-11-12 17:01
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int GetNodeByIp(const DWORD ip)
{
	int i;
	for(i=0;i<NODE_MAX;i++)
	{
		if(g_Node[i].node == ip)
		{
			return  i;
			break;
		}
	}
	return  -1;
}
/********************************************************************
函数名称：GetNodeState
函数功能: 得到节点状态
参数定义: Node_number:节点下标
返回定义: 返回节点状态
创建时间: 2007-11-12 11:32
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
static BOOL GetNodeState(const int Node_number)
{
	if((Node_number<NODE_MAX)&&(Node_number>=0))
	{
		return g_Node[Node_number].isActive;
	}
	else
		return FALSE;
}
/********************************************************************
函数名称：FindNodeModule
函数功能: 查找模块的线程句柄
参数定义: 无
返回定义: 成功返回线程ID号,失败返回-1
创建时间: 2009-6-12 16:09
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DWORD FindNodeModule(UINT ModID)
{
	int i;
	BOOL bFine=FALSE;
	DWORD wModule = -1;
	if(g_Node==NULL)
		return FALSE;
	if(ModID>MODULE_MAX)
		return -1;
	for(i=0;i<NODE_MAX;i++)
	{
		if((g_Node[i].node==g_Module[ModID].tId.node)&&(g_Node[i].isActive== TRUE))
		{
			wModule=g_Node[i].module;
			break;
		}
	}
	return wModule;
}
/********************************************************************
函数名称：RegisterNodeThread
函数功能: 节点通讯模块注册
参数定义: wNode:节点号，wModule:通讯模块号
返回定义: 成功返回节点下标，失败返回-1
创建时间: 2007-11-9 17:11
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
int RegisterNodeThread(DWORD wNode,DWORD wModule)
{
	int i;
	BOOL bFine=FALSE;
	int Node_number = -1;
	if(g_Node==NULL)
		return FALSE;
	for(i=0;i<NODE_MAX;i++)
	{
		if(g_Node[i].node==wNode)
		{
			g_Node[i].module=wModule;
			g_Node[i].isActive=TRUE;
			bFine=TRUE;
			Node_number=i;
		}
		if(bFine)
			break;
	}
	return Node_number;
}
/********************************************************************
函数名称：UnRegisterNodeThread
函数功能: 释放节点通讯模块注册
参数定义: Node_number:节点下标
返回定义: 成功返回TRUE，失败返回FALSE
创建时间: 2007-11-9 17:24
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
BOOL UnRegisterNodeThread(const int Node_number)
{
	BOOL bFine=FALSE;
	if(g_Node==NULL)
		return FALSE;
	if((Node_number <NODE_MAX)&&(Node_number>=0))
	{
		g_Node[Node_number].module=0;
		g_Node[Node_number].isActive=FALSE;
		bFine = TRUE;
	}
	
	return bFine;
	
}
/*******************************************************
*	函数名:		GetXORCode()
*	功能:		异或校验
*	输入参数:	缓存,缓存大小
*   输出参数:	失败返回－1 成功返回wReturn
*	返回值:		程序正常结束返回wReturn(BYTE型)，
*******************************************************/

BYTE  GetXORCode(BYTE *pBuffer,int nSize)
{
	int i;
	BYTE wReturn = pBuffer[0];
	for (i = 1 ;i < nSize ;i++)
	{
		wReturn ^= pBuffer[i];
	}
	return wReturn;
	
}
