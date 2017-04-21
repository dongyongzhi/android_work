package com.yifengcom.yfpos.bank;

public class EposProtocol {
	public static final byte S2C_LINK = (byte)0x81;
	public static final byte S2C_BUSI = (byte)0x84;
	
	public static final byte C2S_LINK = (byte)0x82;
	public static final byte C2S_BUSI = (byte)0x87;
	
	public static final byte C2S = 0x02;//报文类型 终端至平台
	public static final byte S2C = 0X03;//报文类型 平台至终端
	
	public static final byte CMD2_RESERVER					= 1;
	public static final byte CMD2_READ_PSAMID				= 2;
	public static final byte CMD2_READ_CARDID				= 3;
	public static final byte CMD2_READ_TRACK				= 4;
	public static final byte CMD2_READ_PIN				= 5;
	public static final byte CMD2_READ_CAPACITY				= 6;
	public static final byte CMD2_READ_AMOUNT				= 7;
	public static final byte CMD2_READ_BANKNO				= 8;
	public static final byte CMD2_READ_BUSINO				= 9;
	public static final byte CMD2_READ_YYYYMMDD				= 10;
	public static final byte CMD2_READ_YYYYMM				= 11;
	public static final byte CMD2_READ_CUSTOM				= 12;
	public static final byte CMD2_CALC_MAC					= 13;
	public static final byte CMD2_SIGNATURE					= 54;
	public static final byte CMD2_READ_REVERSE				= 15;
	public static final byte CMD2_READ_PROGRAM_VER			= 16;
	public static final byte CMD2_READ_APP_VER				= 17;
	public static final byte CMD2_READ_TERMID				= 18;
	public static final byte CMD2_ENCRYPT					= 19;
	public static final byte CMD2_DENCRYPT					= 20;
	public static final byte CMD2_GET_BILL					= 21;
	public static final byte CMD2_UPDATE_TERM				= 22;
	public static final byte CMD2_UPDATE_PSAM				= 23;
	public static final byte CMD2_UPDATE_MENU				= 24;
	public static final byte CMD2_UPDATE_FUNCTION			= 25;
	public static final byte CMD2_UPDATE_OPERATE			= 26;
	public static final byte CMD2_UPDATE_CAPTION			= 27;
	public static final byte CMD2_UPDATE_PRINT				= 28;
	public static final byte CMD2_UNREGISTER				= 29;
	public static final byte CMD2_STORE_BILL				= 30;
	public static final byte CMD2_WRITE_LOG					= 31;
	public static final byte CMD2_STORE_MESSAGE				= 32;
	public static final byte CMD2_PRINT						= 33;
	public static final byte CMD2_DISPLAY					= 34;
	public static final byte CMD2_CONNECT					= 35;
	public static final byte CMD2_SEND						= 36;
	public static final byte CMD2_RECV						= 37;
	public static final byte CMD2_HANGUP					= 38;
	public static final byte CMD2_VERIFY_PASSWORD			= 39;
	public static final byte CMD2_VERIFY_MAC				= 40;
	public static final byte CMD2_HF_DIAL					= 41;
	public static final byte CMD2_BUSI_AFFIRM				= 42;
	public static final byte CMD2_UPDATE_PROGRAM			= 43;
	public static final byte CMD2_IC_CONTROL				= 44;
	public static final byte CMD2_STORE_CARDID				= 45;
	public static final byte CMD2_UPLOAD_CARDID				= 46;
	public static final byte CMD2_CENTER_MESSAGE			= 47;
	public static final byte CMD2_GET_FLOWCODE				= 48;
	public static final byte CMD2_SHIELD_CALL				= 49;
	public static final byte CMD2_IC_CMD					= 50;
	public static final byte CMD2_UPDATE_KEY				= 51;
	public static final byte CMD2_READ_CARDID1				= 52;
	public static final byte CMD2_UPLOAD_BUSILOG			= 53;
	public static final byte CMD2_UPLOAD_ERRORLOG			= 54;
	public static final byte CMD2_RECV_PC					= 55;
	public static final byte CMD2_SEND_PC					= 56;
	public static final byte CMD2_IC_LOAD					= 57;
	public static final byte CMD2_SELE_MENU  				= 58;
	public static final byte CMD2_IC_WALLET					= 59;
	public static final byte CMD2_UPDATE_APP_VER    		= 62;
	
	public static final byte CMD2_IC_RESPONSE				= 58;
	
	public static final byte CMD_ONE_BYTE					= 2;
	public static final byte CMD_TWO_BYTE					= 0;
	public static final byte CMD_THREE_BYTE					= 3;			
	
	public final static int MESSAGE_RECEIVED 			= 1;
	public final static int SESSION_CLOSED				= 2;
	public final static int SESSION_OPENED				= 3;
	public final static int EXCEPTION_CAUGHT			= 4;
	public final static int MESSAGE_SENT				= 5;
	public final static int SESSION_CREATED				= 6;
	public final static int SESSION_IDLE				= 7;
	
	public final static int MESSAGE_TIMER 				= 8;
	public final static int MESSAGE_LOGIN				= 9;
	
	
}
