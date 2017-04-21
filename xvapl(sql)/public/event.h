#ifndef _YFSPP_EVENT_H
#define _YFSPP_EVENT_H

/****************     监控事件     **********************/
#define evInitial               WM_USER
#define evLog                   (WORD) (evInitial + 1)
#define evWarring               (WORD) (evInitial + 2)
#define evError1                (WORD) (evInitial + 3)
#define evError2                (WORD) (evInitial + 4)
#define evError3                (WORD) (evInitial + 5)


#define evStateReport           (WORD) (evInitial + 6)
#define evStateReportAck        (WORD) (evInitial + 7)
#define evTraceStatus           (WORD) (evInitial + 8)

#define evReset                 (WORD) (evInitial + 10)
#define evState                 (WORD) (evInitial + 11)


/****************     定时器事件     **********************/
#define evTimer0                (WORD)(evInitial + 100)//设置基本超时
#define evTimer1                (WORD)(evTimer0 + 1)//设置超时
#define evTimer2                (WORD)(evTimer0 + 2)//设置第三方超时
#define evTimer3                (WORD)(evTimer0 + 3)//设置超时挂机


/****************  数据库访问事件,DBAP  **********************/
#define evDataBase              (WORD)(evInitial  + 150)

#define evDBDInital				(WORD)(evDataBase + 1)/*:数据库初始化DBD*/
#define evDBQInital				(WORD)(evDataBase + 2)/*:数据库初始化DBQ*/

#define evDBDConnection			(WORD)(evDataBase + 3)	/*建立数据库连接DBD*/
#define evDBQConnection			(WORD)(evDataBase + 4)	/*建立数据库连接DBQ*/

#define evDBDConnAvailCheck		(WORD)(evDataBase + 5)	/*数据库连接有效性检查*/
#define evDBDConnAvailCheckAck	(WORD)(evDataBase + 6)	/*数据库连接有效性检查返回*/

#define evDBDAsk				(WORD)(evDataBase + 7)/*:数据库查询请求DBD*/
#define evDBQuery				(WORD)(evDataBase + 8) /*:对DBQ进行数据库查询请求*/






/***************SLP*********************************************************/
#define evSLPTN                        (WORD)(evInitial + 270)
#define evDBQueryAck				   (WORD)(evSLPTN+2)/*数据库查询结果返回*/

#define evDBQueryNak				   (WORD)(evSLPTN+8)/*数据库查询结果异常返回*/
#define evFSKQueryAck				   (WORD)(evSLPTN+65)/*第三方查询结果返回*/
///////SLP->DBD///////////////////////////////////////////////////////////
#define evDBD						  (WORD)(evInitial + 300)            
#define evAckDBD                      (WORD)(evDBD+7) //数据库连接失败直接发送消息给SLP，方向DBD->SLP


/////////SLP<->IPP//////////////////////////////////////////////////////////
#define evIPP                         (WORD)(evInitial+330)

///////////////SPP<->BILL///////////////////////////////////////////////////////////
#define      evBILL             (WORD)(evInitial+350)
#define      evASK_BILL         (WORD)(evBILL+1)       //BILL(计算)模块响应消息事件号
#define      evBILL_ACK         (WORD)(evBILL+2)       //返回消息事件号


///////////////BILL<->CDR///////////////////////////////////////////////////////////

#define      evCDR              (WORD)(evInitial+380)
#define      evASK_CDR          (WORD)(evCDR+1)     //CDR模块响应消息事件号


/////////////////////LOG日志事件/////////////////////////////////////////////////////
#define evLogAsk                                evInitial+500//LOG日志事件
//////////////////////////////////////////////////////////////////////////
#define evTraceAsk                              evInitial+551//记录TRACE跟踪事件
/************************************************************************/
/*  purpose:SPP与ServiceMan接口事件定义	                                */
/************************************************************************/

#define evSerMan_Base                           evInitial+976//该数值由XVAPL统一指定
#define	evSerMan_ActivityTest	      			evSerMan_Base +	1//连接测试
#define	evSerMan_ActivityTestAck   			    evSerMan_Base +	2//测试确认
#define	evSerMan_ErrorReport					evSerMan_Base +	3//错误报告
#define	evSerMan_SetStatus						evSerMan_Base +	10//设置业务状态
#define	evSerMan_SetStatusAck           	    evSerMan_Base +	11//设置业务状态确认

#define evSerMan_SetConstValue					evSerMan_Base +	12//设置常量值
#define	evSerMan_SetConstValueAck           	evSerMan_Base +	13//设置常量值确认

#define evSerMan_GetConstValue					evSerMan_Base +	14//读取常量值
#define	evSerMan_GetConstValueAck           	evSerMan_Base +	15//读取常量值确认

#define evSerMan_GetConstAttr					evSerMan_Base +16//读取常量属性
#define	evSerMan_GetConstAttrAck				evSerMan_Base +17//读取常量属性确认

#define evSerMan_GetAllConstAttr				evSerMan_Base +18//获取常量整体属性属性
#define	evSerMan_GetAllConstAttrAck				evSerMan_Base +19//常量整体属性确认



/************************************************************************/
/* ODI模块消息号                                                        */
/************************************************************************/
#define evODI_Message                           evInitial +1200 //	发送消息到ODI 
#define evODI_Message_Ack                       evInitial +1201 //ODI 应答消息
#define evODI_Access                            evInitial +1202 //ODI发送消息到SLP
#define evODI_Access_Ack                        evInitial +1203 //SLP发送消息到ODI确认

#define evODI_JS_Ask                            evInitial +1204 //结算向ODI发送请求消息
#define evODI_JS_Ack                            evInitial +1205 //ODC向结算发送确认消息




#endif


