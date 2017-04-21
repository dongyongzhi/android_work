#include <stdio.h>
#include <time.h>
#include <process.h>  
#include <stdlib.h>
#include <string.h>
#include "Acc_BPublic.h"
#include "D.h"
#include "tc08a32.h"

#include "NewSig.h"
#include "commdll.h"

void  DoWork();
BOOL  InitSystem();
void  ExitSystem();

char  ConvertDtmf(int ch);
void  GetVoicePath();
BOOL InitToneIndex();
void ACC_BSendMessage(ACS_Hdl_EVENT pHdlEvent);
void DoAllotherWord(WORD channelno,int iType);
BOOL InitChannelCalledNum();
void WriteBLog_Testchar();
__stdcall BAcc_ThreadProc(PVOID pVoid)
{

	if(!InitSystem())
		return 0;
    while(DriverOpenFlag==0)
	{
		Sleep(100);
		DoWork();
		WriteBLog_Testchar();
	}
	WriteBLog_Testchar();
	if(DriverOpenFlag==-255)
	{
		ExitSystem();
	}
	_endthread();
	return 0;
}


void WriteBLog_Testchar()
{
	SYSTEMTIME     Clock;
	FILE	    *fp=NULL;
	fp = fopen ("C:\\yfcomm\\log\\ACCB_test.log","w");

	if ( fp == NULL )
		return ;

	GetLocalTime(&Clock);

	
		fprintf (fp, "%04d-%02d-%02d %02d:%02d:%02d:%03d have call in message,channel no is=%d\n",
			Clock.wYear,Clock.wMonth, Clock.wDay,
		    Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,DriverOpenFlag);

	fclose (fp);
}
void WriteBLog_char()
{
	SYSTEMTIME     Clock;
	FILE	    *fp=NULL;
	int i;
	fp = fopen ("C:\\yfcomm\\log\\ACC_BLock.log","a");

	if ( fp == NULL )
		return ;

	GetLocalTime(&Clock);

	for(i=0;i<TotalChannel;i++)
	{
		fprintf (fp, "%04d-%02d-%02d %02d:%02d:%02d:%03d have call in message,channel no is=%d,type is %d\n",
			Clock.wYear,Clock.wMonth, Clock.wDay,
		    Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,i,pChannelInfor[i].nType);
	}
	fclose (fp);
}
void WriteBLog_char1(char *pChar)
{
	SYSTEMTIME     Clock;
	FILE	    *fp=NULL;
	fp = fopen ("C:\\yfcomm\\log\\ACC_BLock.log","a");

	if ( fp == NULL )
		return ;

	GetLocalTime(&Clock);

	
		fprintf (fp, "%04d-%02d-%02d %02d:%02d:%02d:%03d have call in message,pChar is %s\n",
			Clock.wYear,Clock.wMonth, Clock.wDay,
		    Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,pChar);

	fclose (fp);
}
void BCD_TO_ASCIIString(WORD sid,unsigned char *cSource, unsigned int iSourceLen)
{
	FILE *fp;
	char mmmmm[3200];
	char cResult[3200];
	unsigned int i=0;
    int j=0;
	char filename[60];
	SYSTEMTIME     Clock;
	memset(mmmmm, 0, 1600);
	memset(cResult,0,sizeof(cResult));
    for(i=0;i<iSourceLen;i++) 
    {
        cResult[j+2]=32;
        cResult[j+1] = cSource[i]%16 + '0';
        if(cResult[j+1] > 9+'0')
        {
            cResult[j+1] = cResult[j+1]+7;
        }
        cResult[j] = cSource[i]/16+'0';
        if(cResult[j] > 9+'0')
        {
            cResult[j] = cResult[j]+7;
        }
        j+=3;
    }
    cResult[j] = '\0';
	cResult[j++]='\n';

	sprintf(filename,"C:\\YFCOMM\\LOG\\AccB_data.log");
	fp = fopen(filename,"a");
	if(fp == NULL)
	{
//		fclose(fp);
		return ;
	}
	GetLocalTime(&Clock);
		fprintf (fp, "%04d-%02d-%02d %02d:%02d:%02d:%03d  %s\n",
			Clock.wYear,Clock.wMonth, Clock.wDay,
		    Clock.wHour, Clock.wMinute, Clock.wSecond, Clock.wMilliseconds,cResult);
//	fwrite(cResult,strlen(cResult),1,fp);
//	fwrite('\n',1,1,fp);
	
	fclose(fp);
	fp=NULL;

} 
//#pragma argsused
BOOL InitSystem()
{
	WORD i;
//	GetVoicePath();
	DriverOpenFlag = LoadDRV( );
	if ( DriverOpenFlag ) {

		return FALSE;
	}

	TotalChannel = CheckValidCh();
	if ( EnableCard(TotalChannel,1024*8) != (long)0) {
		FreeDRV();
		return FALSE;
	}
	if(DJFsk_InitForFsk(FSK_CH_TYPE_160)!=1)//初始化FSK,定义是模拟卡接收FSK,初始化失败
	{
		DisableCard();
		FreeDRV();
		return FALSE;
	}
	pChannelInfor=(CHANNElINFOR *)malloc(sizeof(CHANNElINFOR)*TotalChannel);
	SetBusyPara(350);
	for(i=0;i<TotalChannel;i++)
	{

		memset(pChannelInfor[i].data,0,sizeof(pChannelInfor[i].data));
		pChannelInfor[i].datalen=0;
        pChannelInfor[i].nType=CheckChTypeNew(i);
		strcpy(pChannelInfor[i].CallerID,"");
		strcpy(pChannelInfor[i].Dtmf,"");
		pChannelInfor[i].State = CH_FREE;
		
	}
	WriteBLog_char();
	InitChannelCalledNum();
	Sig_Init(0);
	return TRUE;
}
void ExitSystem()
{
	DJFsk_Release();//释放FSK
	DisableCard();
	FreeDRV();
	free(pChannelInfor);
	pChannelInfor=NULL;

	
}

void ResetChannel(WORD channelID)
{
	if(pChannelInfor[channelID].nType==CHTYPE_TRUNK)
	{
		HangUp(channelID);
		Sig_ResetCheck(channelID);
		StartSigCheck(channelID);
		//StopSigCheck(channelID);
	}
	pChannelInfor[channelID].Dtmf[0]=0;
	pChannelInfor[channelID].CallerID[0]=0;
	pChannelInfor[channelID].State = CH_FREE;
	pChannelInfor[channelID].datalen=0;
	memset(pChannelInfor[channelID].data,0,sizeof(pChannelInfor[channelID].data));
}


char ConvertDtmf(int ch)
{
	char c;

	switch(ch)
    {
		case 10:
			c = '0';
			break;
		case 11:
			c = '*';
			break;
		case 12:
			c = '#';
			break;
        case 13:
        case 14:
        case 15:
            c=ch-13+'a';
            break;
        case 0:
            c='d';
            break;
		default:
			c = ch + '0';//change DTMF from number to ASCII
	}
	return c;
}
/********************************************************************
函数名称：查找会话号SID
函数功能:
参数定义: 无
返回定义: 返回会话号
创建时间: 2008-1-25 11:14
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
DWORD SearchSid(WORD wDsp,WORD wTrunk)
{
	int i;
	for (i=0;i<MESSAGE_MAX;i++)
	{
		if((g_Sid_Xssm_Dll_Infor[i].DeviceNumber==(DWORD)(wDsp*256*256+wTrunk))&&(g_Sid_Xssm_Dll_Infor[i].bBusy))
		{
			return i;
		}
	}
	return MESSAGE_MAX+1;
}
void DoWork()
{
 	int a;
	WORD i;
	int tt,r;
	BOOL bOffHook;
	ACS_Hdl_EVENT pHdlEvent;
	GetTickCount();
    for( i=0;i<TotalChannel;i++)
    {
		PUSH_PLAY();
		FeedSigFunc();
        switch(pChannelInfor[i].State)
        {
        case CH_FREE:
            if(RingDetect(i))
            {
                if(pChannelInfor[i].nType==CHTYPE_USER)
					pChannelInfor[i].State=CH_OFFHOOK;
				else 
				{
					pChannelInfor[i].State=CH_RECEIVEID;
					ResetCallerIDBuffer(i);
					pChannelInfor[i].nTimeElapse=0;
				}
            }
            break;
		case CH_DIAL:
			if(CheckSendEnd(i) == 1)
			{
				StartSigCheck(i);
				pChannelInfor[i].State=CH_CHECKSIG;
			}
			break;
		case CH_CHECKSIG:
			
			tt = Sig_CheckDial(i);
			if(tt == S_BUSY)
			{
				pChannelInfor[i].State = CH_BUSY;
			}
			else if(tt == S_CONNECT)
			{
				pChannelInfor[i].State = CH_CONNECT;
			}
			else if(tt == S_NOSIGNAL)
			{
				pChannelInfor[i].State= CH_NOSIGNAL;
			}
			else if(tt == S_NOBODY)
			{
				pChannelInfor[i].State= CH_NOBODY;
			}
			else if(tt == S_NODIALTONE)
			{
				pChannelInfor[i].State= CH_NODIALTONE;
			}
			break;
		case CH_BUSY:
		case CH_NOSIGNAL:
		case CH_NOBODY:
		case CH_NODIALTONE:
			ResetChannel(i);

			break;
		case CH_CONNECT:
			pHdlEvent.event=evtXSSM_CallOut;
			pHdlEvent.datalen = 0;
			pHdlEvent.DeviceNumber = i*256*256;
			pHdlEvent.sid=SearchSid(i,0);
			ACC_BSendMessage(pHdlEvent);
			pChannelInfor[i].State = CH_DOOTHER;
			StartSigCheck(i);//开始新的信号音检测
			DJFsk_ResetFskBuffer(i,FSK_CH_TYPE_160);
			InitDtmfBuf(i);
			break;

        case CH_RECEIVEID:
			{
				bOffHook=FALSE;
				if(pChannelInfor[i].nTimeElapse>2000 && RingDetect(i)) 
					bOffHook=TRUE;
				if(pChannelInfor[i].nTimeElapse>7000) 
					bOffHook=TRUE;
				a=GetCallerIDStr(i,pChannelInfor[i].CallerID);
				if((a==3)||(a==4))//接收主叫号码完毕
				{
					bOffHook=TRUE;
				}
				
				if(bOffHook)
				{
					OffHook(i);//摘机
					StartSigCheck(i);//开始新的信号音检测
					DJFsk_ResetFskBuffer(i,FSK_CH_TYPE_160);
					InitDtmfBuf(i);
					if(strlen(pChannelInfor[i].CallerID)==0)
					{
						strcpy(pHdlEvent.CallingNum,"88888888");
					}
					else
						strcpy(pHdlEvent.CallingNum,pChannelInfor[i].CallerID+8);
	//				pHdlEvent.CalledNum="744991";
					strcpy(pHdlEvent.CalledNum,pChannelInfor[i].TelNum);
					pHdlEvent.event=evtXSSM_CallIn;
					pHdlEvent.datalen = 0;
					pHdlEvent.DeviceNumber = i*256*256;
					pHdlEvent.sid=SearchSid(i,0);
					pChannelInfor[i].State=CH_DOOTHER;
					ACC_BSendMessage(pHdlEvent);
				}
				pChannelInfor[i].nTimeElapse+=50;
			}
            break;
		case CH_SENDFSK:
			{
				if(pChannelInfor[i].datalen>0)
				{
					DoAllotherWord(i,CH_RECVFSK);
				}
				else
				{
					r=DJFsk_SendFSK(i,pChannelInfor[i].senddata,(WORD)pChannelInfor[i].senddatalen,FSK_CH_TYPE_160);
					if(r==1)
					{
						pChannelInfor[i].State=CH_CHECKFSK;
						memset(pChannelInfor[i].senddata,0,sizeof(pChannelInfor[i].senddata));
						pChannelInfor[i].senddatalen=0;
					}
				}
			}
			break;
		case CH_CHECKPLAY:
			{
				if(CheckPlayEnd(i))
				{
					pHdlEvent.event=evtXSSM_Play;
					pHdlEvent.datalen = 0;
					pHdlEvent.DeviceNumber = i*256*256;
					pHdlEvent.sid=SearchSid(i,0);
					pChannelInfor[i].State=CH_DOOTHER;
					ACC_BSendMessage(pHdlEvent);
				}
			}
			break;
		case CH_CHECKFSK:
			{
				if(DJFsk_CheckSendFSKEnd(i,FSK_CH_TYPE_160))
				{
					DJFsk_ResetFskBuffer(i,FSK_CH_TYPE_160);
					pHdlEvent.event=evtXSSM_SendIoData;
					pHdlEvent.datalen = 0;
					pHdlEvent.DeviceNumber = i*256*256;
					pHdlEvent.sid=SearchSid(i,0);
					pChannelInfor[i].State=CH_DOOTHER;
					OutputDebugString("发送完成，状态变为CH_DOOTHER");
					pChannelInfor[i].datalen=0;
					memset(pChannelInfor[i].data,0,sizeof(pChannelInfor[i].data));
					ACC_BSendMessage(pHdlEvent);
					pChannelInfor[i].FBeginRecv=0;
					pChannelInfor[i].dStart=0;
				}
			}
			break;
		case CH_RECVFSK:
			{
				DoAllotherWord(i,CH_RECVFSK);
			}
			break;
		case CH_RECVDTMF:
			{
				DoAllotherWord(i,CH_RECVDTMF);
			}
			break;
		case CH_DOOTHER:
			{
				DoAllotherWord(i,CH_DOOTHER);
			}
			break;
		default:
			break;

		}//end switch

		if(pChannelInfor[i].nType==CHTYPE_TRUNK && pChannelInfor[i].State!=CH_FREE)//外线且正在使用
		{

			if (Sig_CheckBusy(i))//忙音检测,是否挂机
			{
				OutputDebugString("挂机");
				WriteBLog_char1("have hangup");
				ResetChannel(i);
				pHdlEvent.event=evtXSSM_ClearCall;
				pHdlEvent.datalen = 0;
				pHdlEvent.DeviceNumber = i*256*256;
				pHdlEvent.sid=SearchSid(i,0);
				ACC_BSendMessage(pHdlEvent);
			}
		}
		
	}//end for
}
void DoAllotherWord(WORD channelno,int iType)
{
	BYTE data[1024];
	int datalen;
	ACS_Hdl_EVENT pHdlEvent;
	short int code;
	int offset=0;
	BOOL bRun=TRUE;
	int RecvLen=0;
	int looptimes=0;
	int i;
	if((iType==CH_RECVFSK)||(iType==CH_DOOTHER))
	{
		{
			memset(data,0,sizeof(data));
			datalen=DJFsk_GetFSK(channelno,data,FSK_CH_TYPE_160);
			if(datalen>0)
			{	
				if (!pChannelInfor[channelno].FBeginRecv)
				{
					//搜索报文头
					for(i=0;i<datalen;i++)
					{
						if ((data[i] == 0x82) ||(data[i] == 0x87)||(data[i] == 0x83))   //合法报文头
						{
							pChannelInfor[channelno].FBeginRecv = 1;
							pChannelInfor[channelno].FPrevLen = datalen;
							pChannelInfor[channelno].FHeadPos = i;
							pChannelInfor[channelno].dStart = GetTickCount();
							break;
						}
					}
				}

				if (pChannelInfor[channelno].FBeginRecv)
				{
					if (datalen >= 3+pChannelInfor[channelno].FHeadPos) 
					{
						RecvLen = data[pChannelInfor[channelno].FHeadPos+1] * 256 + data[pChannelInfor[channelno].FHeadPos+2];
						if (datalen >= RecvLen+4+pChannelInfor[channelno].FHeadPos)
						{
							OutputDebugString("收 OK\n" );
							BCD_TO_ASCIIString(12,data,RecvLen);
							pHdlEvent.event=evtXSSM_RecvIoData;
							pHdlEvent.datalen = RecvLen+4;
							memcpy(pHdlEvent.ioData,data+pChannelInfor[channelno].FHeadPos,datalen-pChannelInfor[channelno].FHeadPos);
							pHdlEvent.DeviceNumber = channelno*256*256;
							pHdlEvent.sid=SearchSid(channelno,0);
							pChannelInfor[channelno].State=CH_DOOTHER;
							memset(pChannelInfor[channelno].data,0,sizeof(pChannelInfor[channelno].data));
							pChannelInfor[channelno].datalen=0;
							DJFsk_ResetFskBuffer(channelno,FSK_CH_TYPE_160);
							pChannelInfor[channelno].FBeginRecv = 0;
							pChannelInfor[channelno].FPrevLen = 0;
							pChannelInfor[channelno].FHeadPos = 0;
							ACC_BSendMessage(pHdlEvent);
						}
					}

					if (pChannelInfor[channelno].FPrevLen < datalen) 
					{
						pChannelInfor[channelno].FPrevLen = datalen;
						pChannelInfor[channelno].dStart = GetTickCount(); //重置计时器
					}


					if (GetTickCount() - pChannelInfor[channelno].dStart > 3000) //连续300毫秒，无数据，则清空
					{
						OutputDebugString("收 超时\n" );
						BCD_TO_ASCIIString(12,data,datalen);
						pHdlEvent.event=evtXSSM_RecvIoData;
						pHdlEvent.datalen = datalen-pChannelInfor[channelno].FHeadPos;
						memcpy(pHdlEvent.ioData,data+pChannelInfor[channelno].FHeadPos,datalen-pChannelInfor[channelno].FHeadPos);
						pHdlEvent.DeviceNumber = channelno*256*256;
						pHdlEvent.sid=SearchSid(channelno,0);
						pChannelInfor[channelno].State=CH_DOOTHER;
						memset(pChannelInfor[channelno].data,0,sizeof(pChannelInfor[channelno].data));
						pChannelInfor[channelno].datalen=0;
						DJFsk_ResetFskBuffer(channelno,FSK_CH_TYPE_160);
						pChannelInfor[channelno].FBeginRecv = 0;
						pChannelInfor[channelno].FPrevLen = 0;
						pChannelInfor[channelno].FHeadPos = 0;
						ACC_BSendMessage(pHdlEvent);
					}
				}
			}
	
		}

    
	}
	if((iType==CH_RECVDTMF)||(iType==CH_DOOTHER))
	{
		code=GetDtmfCode(channelno);
		if(code!=-1)
		{
			pHdlEvent.event=evtXSSM_RecvIoData;
			pHdlEvent.datalen = 1;
			pHdlEvent.ioData[0]=(BYTE)code;
			pHdlEvent.DeviceNumber = channelno*256*256;
			pHdlEvent.sid=SearchSid(channelno,0);
			pChannelInfor[channelno].State=CH_DOOTHER;
			ACC_BSendMessage(pHdlEvent);
			return;
		}
	}
}
/********************************************************************
函数名称：TrunkHandle
函数功能: 消息处理
参数定义: esrParam：指针地址
返回定义: 无
创建时间: 2008-1-25 10:44
函数作者: 刘定文
注意事项: 无	
*********************************************************************/
void ACC_BSendMessage(ACS_Hdl_EVENT pHdlEvent)
{
	

	TID * tId;
	COPYDATASTRUCT cds;
	tId = GetTID(MID_ACC);
	if(tId!=NULL)
	{
		cds.dwData = 0;
		cds.cbData = sizeof(ACS_Hdl_EVENT);
		cds.lpData = &pHdlEvent;
		SendMessage(tId->hWnd,WM_COPYDATA,0,(LPARAM)&cds);
	}


}
BOOL InitChannelCalledNum()
{
	SECTION *sec;
	INI ini;
	BYTE i;
	char ToneName[32];
	char tmpSection[LENGTH_SECTION];
	int ntotal;
	int callno;
	sprintf(ToneName,"C:\\yfcomm\\ini\\DBDK.ini");
	loadINI(&ini,ToneName);
	/*得到[TONEINDEX]*/
	sprintf(ToneName,"DBDK");
	sec=getSection(&ini,ToneName);
	if(sec==NULL)
	{
		return FALSE;
	}
	{
		ntotal=(WORD)atoi(GetKeyList(sec,0));
	}
	/*得到[KeyNumber]*/
	for(i=0;i<ntotal;i++)
	{
		sprintf(tmpSection,"DBDK%d",i);
		sec=getSection(&ini,tmpSection);
		if(sec==NULL)
		{
			return FALSE;
		}
		{
			callno=atoi(GetKeyList(sec,0));		
			if(callno<TotalChannel)
			{
				strcpy(pChannelInfor[callno].TelNum,GetKeyList(sec,1));
				WriteBLog_char1(pChannelInfor[callno].TelNum);
			}
			
		}
	}
	/*end*/
	freeINI(&ini);
	return TRUE;
}
void GetVoicePath()
{
	char FileName[100];
	GetWindowsDirectory(FileName,100);
	strcat(FileName,"\\tc08a-v.ini");
	GetPrivateProfileString("SYSTEM","InstallDir",NULL,VoicePath,100,FileName);
	strcat(VoicePath,"voc\\");
}
