#ifndef __ITP_COM_H__
#define __ITP_COM_H__

#include "ITPMsgPublic.h"
#include "ItpDataDefine.h"

#define ITP_SYSTEM_FLAG 0x49545031  // 

typedef struct
{	
	DJ_S32          m_ItpFlag;       //
	DJ_U8           m_u8ModuleType;  //
	DJ_U8           m_u8MainFuncId;  //
	DJ_U8           m_u8SubFuncId;   //
	DJ_U8           m_u8UnitId;      //
	
	DJ_S32          m_Version;       //
	DJ_S32          m_AckInfo;       //
	
	PKG_HEAD_STRUCT m_PkgHead;       //
	DJ_S8           m_s8Username[32];//
	DJ_S8           m_s8Password[32];//
	
	DJ_U8           m_u8Reserved[36];//
}LICENCE_INFO_STRUCT, *PLICENCE_INFO_STRUCT;

typedef struct
{
	DJ_SOCKET     m_SkForListen;     //
	DJ_SOCKET     m_SkForClient;     //
	DJ_S8         m_szIP[32];        //
	DJ_S32        m_Port;            //

	LICENCE_INFO_STRUCT Licence;	
}CONN_INFO_STRUCT, *PCONN_INFO_STRUCT;


#ifdef __cplusplus
extern "C" 
{
#endif

DJ_S32  TCP_SkOpen(DJ_SOCKET *ps);                                   //
DJ_S32  TCP_SkClose(DJ_SOCKET s);                                    //

DJ_S32  TCP_SkConnect(DJ_SOCKET s, DJ_S8* pszIP, DJ_S32 Port, PLICENCE_INFO_STRUCT pLicence);

DJ_S32  TCP_SkWaitSet(DJ_SOCKET s, DJ_S8* pszIP, DJ_S32 Port);       //
DJ_S32  TCP_SkWaitConnect(DJ_SOCKET s);                              //
DJ_S32  TCP_SkGetConnect(DJ_SOCKET s, PCONN_INFO_STRUCT pConnInfo);  //
DJ_S32  TCP_SkEasyAuthorize(DJ_SOCKET s, DJ_S8* pszUsername,DJ_S8* pszPassword, DJ_U8 f);

DJ_S32  TCP_SkSend(DJ_SOCKET s, PPKG_HEAD_STRUCT pHead);             //
DJ_S32  TCP_SkRecv(DJ_SOCKET s, PPKG_HEAD_STRUCT pHead);             //

DJ_S32  TCP_SkGetLastError();                                        //

DJ_S32  TCP_SkBeginLOGFile(DJ_S8* pszFilename);                      //
DJ_S32  TCP_SkEndLOGFile();                                          //

DJ_S32  TCP_SkStartup();                                             //
DJ_S32  TCP_SkCleanup();                                             //

#ifdef __cplusplus
}
#endif

#endif
