//*****************************************************************************
//Copyright(c)  :  DONJIN CORPORTAION  All Rights Reserved                       
//FileName      :  ITPMsgPublic.h                                                              
//Version       :  1.1                                                              
//*****************************************************************************

#ifndef _ITPMSGPUBLIC_H
#define _ITPMSGPUBLIC_H


#include "ITPDataDefine.h"

#define PKG_EXTEND_NORMAL     0x00
#define PKG_EXTEND_ERROR      0x01
#define PKG_EXTEND_CONFIG     0x02
#define PKG_EXTEND_INTERFACE  0x03

typedef struct
{
	DJ_U8	m_u8PkgFlag;
	DJ_U8	m_u8PkgExtend;
	
	DJ_U16	m_u16DataLen;

    struct ITP_GUID_tag
	{
	    DJ_U8   m_u8ModuleType;
	    DJ_U8   m_u8MainFuncId;
	    DJ_U8   m_u8SubFuncId; 
	    DJ_U8   m_u8UnitId;  
	    DJ_U16  m_u16ChanId; 
	}ITP_GUID;

	DJ_U16 m_u16Reserve; 

}PKG_HEAD_STRUCT, *PPKG_HEAD_STRUCT;

#define PKG_HEAD_LEN  sizeof(PKG_HEAD_STRUCT)

enum PACKAGE_TYPE
{
	PKG_CMD = 0x5A, 
	PKG_EVT = 0xA5  
};

#define MODULE_INIT     0x01
#define MODULE_START    0x02
#define MODULE_RELATE   0x04
#define MODULE_MONITOR  0x08
#define MODULE_CONNECT  0x10
#define MODULE_STOPPING 0x20
#define MODULE_ISTUNING 0x40

typedef struct 
{
	DJ_U32	m_u32time;
	DJ_U32	m_u32date;
	DJ_U16  m_u16ModuleStates;
	DJ_U16  m_u16Param;
}ITP_HEART_TIME,*PITP_HEART_TIME;


typedef struct
{
	DJ_U8	m_u8ModuleType;
    DJ_U8   m_u8UnitID;    
	DJ_U16  m_u16Port;     
    DJ_U32  m_u32IPAddress;
}ITP_MODULE_ADDR,*PITP_MODULE_ADDR;

typedef struct
{
	DJ_S8         m_s8Username[32];
	DJ_S8         m_s8Password[32];
}ITP_MOD_VALIDITY_DATA,*PITP_MOD_VALIDITY_DATA;

typedef struct
{
	DJ_U8	m_u8ModuleType; 
    DJ_U8   m_u8UnitID;     
	DJ_U16  m_u16ChanID;    
	DJ_U16	m_u16DebugLevel;
    DJ_U16  m_u16UdpPort;   
    DJ_U32  m_u32IPAddress; 
	DJ_U32	m_u32Reserved;  
}ITP_MONITOR_ADDR,*PITP_MONITOR_ADDR;

#define MAX_MACADDR_STR_LEN     0x14
#define MAX_MODULENAME_STR_LEN	0x10
#define MAX_ERRMSG_LEN          0x60 

typedef struct
{
    DJ_U8       m_u8ModuleType;   
    DJ_U8       m_u8UnitID;       
    DJ_U8		m_u8IsEnable;     
    DJ_U8       m_u8DSPType;      
    DJ_U16      m_u16ModuleStates;
    DJ_S8       m_s8ModuleName[MAX_MODULENAME_STR_LEN]; 
    DJ_S8       m_s8pMACAddress[MAX_MACADDR_STR_LEN];
    DJ_U32      m_u32Version;         
    DJ_U32      m_u32IPAddress;      
    DJ_U32      m_u32Port;           
	DJ_U32      m_u32Param;          
}ITP_MODULE_STRUCT,*PITP_MODULE_STRUCT;

typedef struct
{
    DJ_S32      m_s32ErrorCode;
    DJ_S8       m_s8pErrMsg[MAX_ERRMSG_LEN];
}ITP_ERROR_STRUCT,*PITP_ERROR_STRUCT;

//**************************************

#define ITP_ROOT_NODE      0x01
#define NODE_NONE       0x00
#define NODE_PARENT     0x01  
#define NODE_RDONLY     0x02  
#define NODE_RDWR       0x03  
#define NODE_SHOWONLY   0x04  
#define NODE_RDWR_TABLE 0x05  

typedef struct
{
	DJ_U8       m_u8NodeType;
	DJ_S8       m_s8ConfigName[31];
	DJ_S8       m_s8ConfigFileName[32];
	DJ_S32      m_s32NodeMsgCode;
    DJ_S32      m_s32ConfigNum;  
}ITP_INTERFACE_NODE_DATA,*PITP_INTERFACE_NODE_DATA;

typedef struct
{
	DJ_U8       m_u8NodeType;
	DJ_S8       m_s8ConfigName[31];
	DJ_S32      m_s32NodeMsgCode;
}ITP_INTERFACE_SUBNODE_DATA,*PITP_INTERFACE_SUBNODE_DATA;


typedef struct
{
	DJ_S32      m_s32IsWriteini;
	DJ_S32      m_s32NodeMsgCode;
	DJ_U16      m_u16FisrtData;
	DJ_U16      m_u16DataNum;
}ITP_INTERFACE_DONE_DATA,*PITP_INTERFACE_DONE_DATA;


typedef struct
{
	DJ_S32      m_s32NodeMsgCode;
	DJ_S32      m_s32RetCode;
}ITP_INTERFACE_WRDATA_MSG,*PITP_INTERFACE_WRDATA_MSG;

typedef struct
{
	DJ_U8       m_u8NodeType;
	DJ_S8       m_s8ConfigFileName[31];
	DJ_S32      m_s32NodeMsgCode;
	DJ_U16      m_u16FisrtData;
	DJ_U16      m_u16DataNum;
}ITP_INTERFACE_RDATA_MSG,*PITP_INTERFACE_RDATA_MSG;
//**************************************


#define ERASE_PASSWORD 0x87654321
#define MAX_DATA_BLOCK_LENGTH  4000 

typedef struct
{
    DJ_U32 m_u32AddrOffset;
	DJ_U32 m_u32ByteNum;
}ITP_FLASH_READ,*PITP_FLASH_READ;

typedef struct
{
    DJ_U32 m_u32AddrOffset;
	DJ_U32 m_u32ByteNum;
	DJ_U8  m_u8pData[MAX_DATA_BLOCK_LENGTH];
}ITP_FLASH_WRITE,*PITP_FLASH_WRITE;

typedef struct
{
	DJ_U32 m_u32DataLen;
	DJ_U8  m_u8pData[MAX_DATA_BLOCK_LENGTH];
}ITP_FLASH_READ_MSG,*PITP_FLASH_READ_MSG;

typedef struct
{
    DJ_U32 m_u32WriteStates;
	DJ_U32 m_u32CheckSum;
}ITP_FLASH_WRITE_MSG,*PITP_FLASH_WRITE_MSG;
//*******************************************************

#define   MSG_MEDIA						0x3000

#define   MSG_MEDIA_DEV					MSG_MEDIA + 0x100
#define   MSG_MEDIA_DEV_OPEN			MSG_MEDIA_DEV + 0x001
#define   MSG_MEDIA_DEV_CLOSE			MSG_MEDIA_DEV + 0x002
#define   MSG_MEDIA_DEV_STOP			MSG_MEDIA_DEV + 0x003

#define   MSG_MEDIA_PLAYREC				MSG_MEDIA_DEV + 0x100
#define   MSG_MEDIA_PLAYREC_PLAY		0x001
#define   MSG_MEDIA_PLAYREC_RECORD		0x002

#define   MSG_MEDIA_UT					MSG_MEDIA_PLAYREC + 0x100

#define   MSG_MEDIA_FSK					MSG_MEDIA_UT + 0x100

#define   MSG_MEDIA_EC					MSG_MEDIA_FSK + 0x100

#define   MSG_MEDIA_DTMF				MSG_MEDIA_EC + 0x100

#define   MSG_MEDIA_CA					MSG_MEDIA_DTMF + 0x100

#define   MSG_MEDIA_DIAL				MSG_MEDIA_CA + 0x100

#define   MSG_MEDIA_PARM				MSG_MEDIA_DIAL + 0x100

#define   MSG_MEDIA_CONF				MSG_MEDIA_PARM + 0x100

#define   MSG_MEDIA_MS					MSG_MEDIA_CONF + 0x100
#define   MSG_MEDIA_MS_SEND				MSG_MEDIA_MS + 0x001
#define   MSG_MEDIA_MS_RECV				MSG_MEDIA_MS + 0x002
#define   MSG_MEDIA_MS_VLDRSC			MSG_MEDIA_MS + 0x003



#define   MSG_MEDIA_END               0x3FFF
//**************************************************************************

DJ_S32 ITP_Msg_GetDataLen(PPKG_HEAD_STRUCT pPkgHead);
DJ_U8 *ITP_Msg_GetData(PPKG_HEAD_STRUCT pPkgHead);
DJ_S32 ITP_Msg_Init(PPKG_HEAD_STRUCT *ppPkgHead, DJ_S32 s32DataLen);
DJ_Void ITP_Msg_Release(PPKG_HEAD_STRUCT *ppPkgHead);
DJ_S32 ITP_Msg_AttachData(PPKG_HEAD_STRUCT *ppPkgHead,DJ_U8 *u8pData, DJ_S32 s32DataLen);
DJ_Void ITP_Msg_ClearData(PPKG_HEAD_STRUCT pPkgHead);



#endif
