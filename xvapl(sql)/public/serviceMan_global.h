/********************************************************************
公司名称：江苏怡丰通讯有限公司
创建时间: 2008-12-11   10:02
文件名称: E:\XVAPL业务逻辑平台\源代码\PUBLIC\serviceMan_global.h
文件路径: E:\XVAPL业务逻辑平台\源代码\PUBLIC
file base:serviceMan_global
file ext: h
author:	  刘定文

purpose:SPP与ServiceMan接口事件定义	
*********************************************************************/
#ifndef _SERVICEMAN_GLOBAL_H_
#define _SERVICEMAN_GLOBAL_H_

#include "event.h"
#include "xvapl_define.h"


#pragma pack(1)
typedef enum
{
  StopServiceRun  = 1  ,  //  停止业务
  ActiveService   ,  //  激活业务
  LoadService     ,  //  加载业务
  UnloadService   ,  //  卸载业务
  StartTrace      ,  //  启动跟踪
  CancelTrace     ,  //  取消跟踪
  WriteConfig        //  写配置文件
}emSERMAN_OperType;
typedef struct 
{
	DWORD serviceID;//业务健ID
	enum  emSERMAN_OperType operStatus;//操作类型
}stSERMAN_STATUS;//设置业务状态
typedef struct 
{
	DWORD serviceID;// 业务健ID
	WORD  no;//常量编号
	WORD  addr;//起始地址
	WORD  datalen;//数据长度
	BYTE  pValue[255];//值
}stSERMAN_VALUE;//常、变量值


typedef struct
{
 DWORD serviceID;// 业务健ID
}stSERMAN_ASKALLCONSTATTR;//请求获取常量整体属性
typedef struct
{
 DWORD serviceID;// 业务健ID
 WORD  ntotal;//常量个数
}stSERMAN_ASKALLCONSTATTRAck;//常量整体属性回复

typedef struct
{
 DWORD serviceID;// 业务健ID
 WORD  starteId;//常量起始编号
 BYTE    btotal;//读取常量属性个数  
} stSERMAN_ASKCONSTATTR;//读常量属性

typedef struct
{
 DWORD serviceID;// 业务健ID
 WORD  starteId;//常量起始编号
 BYTE    btotal;//读取常量属性个数 
XSCM_CONSTVARIABLE *pConstAttr;//常量属性
} stSERMAN_ASKCONSTATTRAck;//常量属性回复


#pragma pack()

#endif