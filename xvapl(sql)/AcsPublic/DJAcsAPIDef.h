#ifndef _DJITPAPIDLL_H
#define _DJITPAPIDLL_H

#include "ITPCom.h"
#include "DJAcsDataDef.h"

#ifndef WIN32
#define WINAPI
#endif

#ifdef __cplusplus
extern "C" 
{
#endif

#ifndef WIN32
	RetCode_t  XMS_acsDllInit();

	RetCode_t  XMS_acsDllLUnInit(); 
#endif

	RetCode_t WINAPI XMS_acsOpenStream(ACSHandle_t * acsHandle,ServerID_t *serverID,DJ_U8 u8AppID,
										DJ_U32 u32SendQSize,DJ_U32 u32RecvQSize,DJ_S32 s32DebugOn,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_acsCloseStream(ACSHandle_t acsHandle,PrivateData_t * privateData);
			
	RetCode_t WINAPI XMS_acsSetESR(ACSHandle_t acsHandle,EsrFunc esr,DJ_U32 esrParam,BOOL notifyAll);	
		
	RetCode_t WINAPI XMS_acsGetDeviceList(ACSHandle_t acsHandle,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_acsSetTimer(ACSHandle_t acsHandle,DJ_U32 u32Elapse);

	RetCode_t WINAPI XMS_ctsOpenDevice(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsCloseDevice(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsResetDevice(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsSetDevTimer(ACSHandle_t acsHandle,DeviceID_t * deviceID,DJ_U32 u32Elapse);
	
	RetCode_t WINAPI XMS_ctsGetDevState(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);
			
	RetCode_t WINAPI XMS_ctsSetDevGroup(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);	

	RetCode_t WINAPI XMS_ctsMakeCallOut(ACSHandle_t acsHandle,DeviceID_t * deviceID,CallNum_t * callingID,
										CallNum_t * calledID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsSetParam(ACSHandle_t acsHandle,DeviceID_t * deviceID,DJ_U16 u16ParamCmdType,DJ_U16 u16ParamDataSize,DJ_Void * paramData);

	RetCode_t WINAPI XMS_ctsGetParam(ACSHandle_t acsHandle,DeviceID_t * deviceID,DJ_U16 u16ParamCmdType,DJ_U16 u16ParamDataSize,DJ_Void * paramData);
	
	RetCode_t WINAPI XMS_ctsAlertCall(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsAnswerCallIn(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsLinkDevice(ACSHandle_t acsHandle,DeviceID_t * srcDeviceID,DeviceID_t * destDeviceID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsUnlinkDevice(ACSHandle_t acsHandle,DeviceID_t * srcDeviceID,DeviceID_t * destDeviceID,PrivateData_t * privateData);
		
	RetCode_t WINAPI XMS_ctsClearCall(ACSHandle_t acsHandle, DeviceID_t * deviceID,DJ_S32 s32ClearCause,PrivateData_t * privateData);
	
	RetCode_t  WINAPI XMS_ctsJoinToConf(ACSHandle_t acsHandle,DeviceID_t * confDeviceID,DeviceID_t * mediaDeviceID, CmdParamData_Conf_t * confParam, PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsLeaveFromConf(ACSHandle_t acsHandle,DeviceID_t * confDeviceID,DeviceID_t * mediaDeviceID,CmdParamData_Conf_t * confParam,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsClearConf(ACSHandle_t acsHandle,DeviceID_t * confDeviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsPlay(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								PlayProperty_t * playProperty,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsInitPlayIndex(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsBuildPlayIndex(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,PlayProperty_t * playProperty,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsControlPlay(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								ControlPlay_t * controlPlay,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsRecord(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								RecordProperty_t * recordProperty,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsControlRecord(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								ControlRecord_t * controlRecord,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsSendFax(ACSHandle_t acsHandle,DeviceID_t * faxDeviceID,DeviceID_t * mediaDeviceID,
							    DJ_S8 * s8TiffFile,DJ_S8 * s8LocalID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsStopSendFax(ACSHandle_t acsHandle,DeviceID_t * faxDeviceID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsReceiveFax(ACSHandle_t acsHandle,DeviceID_t * faxDeviceID,DeviceID_t * mediaDeviceID,
							    DJ_S8 * s8TiffFile,DJ_S8 * s8LocalID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsStopReceiveFax(ACSHandle_t acsHandle,DeviceID_t * faxDeviceID,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsChangeMonitorFilter(ACSHandle_t acsHandle,DeviceID_t * deviceID,DJ_U32 monitorFilter,PrivateData_t * privateData);	

	RetCode_t WINAPI XMS_ctsSendIOData(ACSHandle_t acsHandle,DeviceID_t * deviceID,DJ_U16 u16IoType,DJ_U16 u16IoDataLen,DJ_Void * ioData);

	RetCode_t WINAPI XMS_ctsSendSignalMsg(ACSHandle_t acsHandle,DeviceID_t * deviceID,DJ_U16 u16SignalMsgType);

	RetCode_t WINAPI XMS_acsOpenStreamExt(ACSHandle_t acsHandle,ServerID_t *serverID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_acsCloseStreamExt(ACSHandle_t acsHandle,ServerID_t *serverID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsPlayCSP(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,CSPPlayProperty_t *  cspPlayProperty,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsSendCSPData(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,CSPPlayDataInfo_t *  cspDataInfo,DJ_Void * cspData,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsControlPlayCSP(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,ControlPlay_t * controlPlay,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsRecordCSP(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								RecordCSPProperty_t * recCSPProperty,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsControlRecordCSP(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								ControlRecord_t * controlRecord,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsInitPlay3gppIndex(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsBuildPlay3gppIndex(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
										   Play3gppProperty_t * playProperty,PrivateData_t * privateData);

    RetCode_t WINAPI XMS_cts3gppPlay(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								Play3gppProperty_t * playProperty,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctsControl3gppPlay(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								Control3gppPlay_t * controlPlay,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_cts3gppRecord(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								Record3gppProperty_t * recordProperty,PrivateData_t * privateData);
	
	RetCode_t WINAPI XMS_ctsControl3gppRecord(ACSHandle_t acsHandle,DeviceID_t * mediaDeviceID,
								Control3gppRecord_t * controlRecord,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_acsQueryLicense(ACSHandle_t acsHandle,DeviceID_t * deviceID,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctxRegister(ACSHandle_t acsHandle,DJ_S8 * s8AppRegName,DJ_U8 u8RegType,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctxLink(ACSHandle_t acsHandle,DeviceID_t * srcDev,DeviceID_t * destDev,DJ_U8 u8Option,PrivateData_t * privateData);

	RetCode_t WINAPI XMS_ctxSendAppData(ACSHandle_t acsHandle,DJ_S8 * s8SrcAppRegName,DJ_S8 * s8DestAppRegName,
									DJ_U8 u8AppReqType,DJ_Void * pData,DJ_U32 u32DataSize,
									DJ_U8 u8SrcAppUnitID,DJ_U8 u8DestAppUnitID,
									DeviceID_t * srcDevice,PrivateData_t * privateData);

	
#ifdef __cplusplus
}
#endif

#endif