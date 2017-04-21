#ifndef __ITP_SIGNAL_MONITOR_H__
#define __ITP_SIGNAL_MONITOR_H__

///////////////////////////////////////////////////////////////////////////////
enum ITP_SIGMonitor_EventType        // 
{	
	SMON_EVT_Call_Generate  =  0x01, // 
    SMON_EVT_Call_Connect   =  0x02, // 
	SMON_EVT_Call_Disconnect=  0x03, // 
	SMON_EVT_Call_Info      =  0x04, // 
	SMON_Evt_Gen_Signal_Info=  0x05, // 
	SMON_Evt_Raw_Signal_Info=  0x06, // 
};


enum ITP_SIGMonitor_Protocol         // 
{	
	SMON_Protocol_ISDN      = 0x01,  // 
	SMON_Protocol_SS7       = 0x02,  // 
	SMON_Protocol_N64K      = 0x03,  // N64K
};


enum ITP_SIGMonitor_SI               // 
{	
	SMON_SI_SCCP = 0x03,             // SCCP
    SMON_SI_TUP  = 0x04,             // TUP
	SMON_SI_ISUP = 0x05,             // ISUP
};


enum ITP_SIGMonitor_ISUP_MsgType     // 
{	
	SMON_ISUP_ACM = 0x06,		     // 
	SMON_ISUP_ANM = 0x09,		     // 
	
	SMON_ISUP_CON = 0x07,		     // 
	
	SMON_ISUP_INF = 0x04,		     // 
	SMON_ISUP_IAM = 0x01,		     // 
	
	SMON_ISUP_REL = 0x0C,		     // 
	SMON_ISUP_RLC = 0x10,		     // 
	
	SMON_ISUP_SAM = 0x02,		     // 
	
	//
	SMON_ISUP_CPG = 0x2C,		     // 
	SMON_ISUP_CQR = 0x2B,		     // 
	SMON_ISUP_GRA = 0x29,		     // 
	SMON_ISUP_CFN = 0x2F,		     // 
	
	SMON_ISUP_COT = 0x05,		     // 
	SMON_ISUP_FRJ = 0x21,		     // 
	
	SMON_ISUP_INR = 0x03,		     // 
	
	SMON_ISUP_USR = 0x2D,		     // 
	SMON_ISUP_FOT = 0x08,		     // 
	
	SMON_ISUP_RES = 0x0E,		     // 
	SMON_ISUP_SUS = 0x0D,		     // 
	
	SMON_ISUP_BLO = 0x13,		     // 
	SMON_ISUP_BLA = 0x15,		     // 
	SMON_ISUP_CCR = 0x11,		     // 
	SMON_ISUP_LPA = 0x24,		     // 
	SMON_ISUP_OLM = 0x30,		     // 
	SMON_ISUP_RSC = 0x12,		     // 
	SMON_ISUP_UBL = 0x14,		     // 
	SMON_ISUP_UBA = 0x16,		     // 
	SMON_ISUP_UCIC= 0x2E,		     // 
	
	SMON_ISUP_CGB = 0x18,		     // 
	SMON_ISUP_CGBA= 0x1A,		     // 
	SMON_ISUP_CGU = 0x19,		     // 
	SMON_ISUP_CGUA= 0x1B,		     // 
	
	SMON_ISUP_GRS = 0x17,		     // 
	SMON_ISUP_CQM = 0x2A,		     // 
	
	SMON_ISUP_FAA = 0x20,		     // 
	SMON_ISUP_FAR = 0x1F,		     // 
	
	SMON_ISUP_PAM = 0x28,		     // 
	
	SMON_ISUP_UPT = 0x34,		     // 
	SMON_ISUP_UPA = 0x35,		     // 
	
	SMON_ISUP_FAC = 0x33,		     // 
	SMON_ISUP_NRM = 0x32,		     // 
	SMON_ISUP_IDR = 0x36,		     // 
	SMON_ISUP_IRS = 0x37,		     // 
	SMON_ISUP_SGM = 0x38,		     // 
	
	SMON_ISUP_CRG = 0x31,		     // 
	SMON_ISUP_OPR = 0xFE,		     // 
	SMON_ISUP_MPM = 0xFD,		     // 
	SMON_ISUP_CCL = 0xFC,		     // 
};


enum ITP_SIGMonitor_TUP_MsgType      // 
{	
	SMON_TUP_IAM = 0x11,             // 
	SMON_TUP_IAI = 0x21,             // 
	SMON_TUP_SAM = 0x31,             // 
	SMON_TUP_SAO = 0x41,             // 
	
	SMON_TUP_ACM = 0x14,             // 
	
	SMON_TUP_SEC = 0x15,             // 
	SMON_TUP_CGC = 0x25,             // 
	SMON_TUP_NNC = 0x35,             // 
	SMON_TUP_ADI = 0x45,             // 
	SMON_TUP_CFL = 0x55,             // 
	SMON_TUP_SSB = 0x65,             // 
	SMON_TUP_UNN = 0x75,             // 
	SMON_TUP_LOS = 0x85,             // 
	SMON_TUP_SST = 0x95,             // 
	SMON_TUP_ACB = 0xA5,             // 
	SMON_TUP_DPN = 0xB5,             // 
	SMON_TUP_MPR = 0xC5,             // 
	
	SMON_TUP_SLB = 0x1E,             // 
	SMON_TUP_STB = 0x2E,             // 
	
	SMON_TUP_ANU = 0x06,             // 
	SMON_TUP_ANC = 0x16,             // 
	SMON_TUP_ANN = 0x26,             // 
	SMON_TUP_CBK = 0x36,             // 
	SMON_TUP_CLF = 0x46,             // 
	
	SMON_TUP_RLG = 0x17,             // 
	
	SMON_TUP_GSM = 0x12,             // 
	
	//
	SMON_TUP_COT = 0x32,             // 
	SMON_TUP_CCF = 0x42,             // 
		
	SMON_TUP_GRQ = 0x13,             // 
	
	SMON_TUP_CHG = 0x24,             // 
		
	SMON_TUP_EUM = 0xF5,             // 
			
	SMON_TUP_RAN = 0x56,             // 
	SMON_TUP_FOT = 0x66,             // 
	SMON_TUP_CCL = 0x76,             // 
	
	SMON_TUP_BLO = 0x27,             // 
	SMON_TUP_BLA = 0x37,             // 
	SMON_TUP_UBL = 0x47,             // 
	SMON_TUP_UBA = 0x57,             // 
	SMON_TUP_CCR = 0x67,             // 
	SMON_TUP_RSC = 0x77,             // 
		
	SMON_TUP_MGB = 0x18,             // 
	SMON_TUP_MBA = 0x28,             // 
	SMON_TUP_MGU = 0x38,             // 
	SMON_TUP_MUA = 0x48,             // 
	SMON_TUP_HGB = 0x58,             // 
	SMON_TUP_HBA = 0x68,             // 
	SMON_TUP_HGU = 0x78,             // 
	SMON_TUP_HUA = 0x88,             // 
	SMON_TUP_GRS = 0x98,             // 
	SMON_TUP_GRA = 0xA8,             // 
	SMON_TUP_SGB = 0xB8,             // 
	SMON_TUP_SBA = 0xC8,             // 
	SMON_TUP_SGU = 0xD8,             // 
	SMON_TUP_SUA = 0xE8,             // 
		
	SMON_TUP_ACC = 0x1A,             // 
		
	SMON_TUP_MPM = 0x2C,             // 
		
	SMON_TUP_OPR = 0x1D,             // 
	
	SMON_TUP_MAL = 0x1F,             // 
};


enum ITP_SIGMonitor_ISDN_MsgType     // 
{
	SMON_ISDN_Alerting        = 0x01,// 
	SMON_ISDN_Call_Proceeding = 0x02,// 
	SMON_ISDN_Connect         = 0x07,// 
	SMON_ISDN_Connect_Ack     = 0x0F,// 
	SMON_ISDN_Progress        = 0x03,// 
	SMON_ISDN_Setup           = 0x05,// 
	SMON_ISDN_Setup_Ack       = 0x0D,// 
	
	SMON_ISDN_Resume          = 0x26,//
	SMON_ISDN_Resume_Ack      = 0x2E,//
	SMON_ISDN_Resume_Reject   = 0x22,//
	SMON_ISDN_Suspend         = 0x25,//
	SMON_ISDN_Suspend_Ack     = 0x2D,//
	SMON_ISDN_Suspend_Reject  = 0x21,//
	SMON_ISDN_User_Information= 0x20,//
	
	SMON_ISDN_Disconnect      = 0x45,//
	SMON_ISDN_Release         = 0x4D,//
	SMON_ISDN_Release_Complete= 0x5A,//
	SMON_ISDN_Restart         = 0x46,//
	SMON_ISDN_Restart_Ack     = 0x4E,//
	
	SMON_ISDN_Segment         = 0x60,//
	SMON_ISDN_Congestion_Ctrl = 0x79,//
	SMON_ISDN_Information     = 0x7B,//
	SMON_ISDN_Notify          = 0x6E,//
	SMON_ISDN_Status          = 0x7D,//
	SMON_ISDN_Status_Enquiry  = 0x75,//
};


///////////////////////////////////////////////////////////////////////////////
#define SMON_Evt_Len_But_Raw_Data 128//

typedef struct                       //
{	
	DJ_U8   EventType;               //
	DJ_U8   Protocol;                //
	DJ_U8   SI;                      //
	DJ_U8   MsgType;                 //
	
	DJ_S8   Caller_ID[32];           //
	DJ_S8   Called_ID[32];           //
	DJ_S8   OriCalled[32];           //
	
	DJ_U8   DPC[3];                  //
	DJ_U8   OPC[3];                  //
	DJ_U8   Pcm;                     // CIC_Hig7
	DJ_U8   Chn;                     // CIC_Low5
	
	DJ_U8   ReleaseReason;           //
	DJ_U8   Reserved[14];            //
	DJ_U8   Unit_ID;                 //
	DJ_U8   Port_ID;                 //
	DJ_U8   TS;                      //
	DJ_U16  DataLen;                 //
	
	DJ_U8   RAWdata[300];            //
} SMON_EVENT, *PSMON_EVENT;

#endif
