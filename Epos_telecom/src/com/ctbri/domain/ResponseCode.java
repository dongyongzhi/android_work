package com.ctbri.domain;

public enum ResponseCode {
	  //========POS 返回类===============//
	//2013-02-04  尹国亮修改 EXEC_CMD_FAIL(13,"POS终端执行失败") 到  EXEC_CMD_FAIL(13,"用户超时、取消操作")
	SUCCESS(11,"执行成功"),
	
	RECV_SUCCESS_DATA(12,"收到正确数据"),
	
	EXEC_CMD_FAIL(13,"输入超时、取消操作"),
	
	EXEC_CMD_CODE_ERROR(14,"命令错误"),
	
	DATA_LEN_ERROR(15,"数据长度错误"),
	
	LEC_ERROR(16,"LRC校验错误"),
	
	NO_FUNC(17,"无此硬件功能错误"),
	
	COMM_TIMEOUT(18,"POS终端无响应"),
	
	DSP_ROW_ADDRESS_ERROR(19,"显示列地址错误"),
	
	DSP_COL_ADDRESS_ERROR(20,"显示行地址错误"),
	
	CANCEL(21,"己取消操作"),
	
	INPUT_TIMEOUT(22,"输入超时"),
	
	INPUT_MODE_ERROR(23,"输入方式MODE错误"),
	
	MIN_LEN_ERROR(24,"最小长度MIN错误"),
	
	MAX_LEN_ERROR(25,"最大长度MAX错误"),
	
	CC_FAIL(26,"刷卡失败"),
	
	PRINT_NO_PAPER(27,"打印缺纸"),
	
	PRINT_HOT(28,"打印头过热"),
	
	PRINT_FAIL(29,"打印失败"),
	
	//==============交易类定义 ====================//
	
	TRANS_HARDWARE(31,"设备终端硬件错误"),
	
	TRANS_NO_SIGN(32,"POS终端未签到"),
	
	TRANS_REVERSAL(34,"冲正"),
	
	TRANS_ERROR(35,"POS终端:数据有误,解包失败"),
	
	TRANS_INVALID(36,"无此交易类型"),
	
	TRANS_MONEY_ERROR(37,"交易金额出错"),
	
	TRANS_REVERSAL_FAIL(38,"交易失败，己冲正！"),
	
	TRANS_SIGN_FAIL(39,"签到失败"),
	
	TRANS_NO_SERVICE(40,"无此业务"),
	
	TRANS_FAIL(41,"交易失败！"),
	
	TRANS_NOEXISTS_POSNO(42,"商户号或终端号不正确"),
	
	TRANS_DATA_RESENT(43,"需要重新上传交易数据"),
	
	TRANS_REVERSAL_DATA_ERROR(44,"POS终端返回冲正数据错误"),
	
	//===========自定义初始化值===============//
	
    INIT_SUCCESS(10,"初始化成功"),
   
    BLUETOOTH_NOT_FOUND(-2,"蓝牙设备未找到"),
   
    BLUETOOTH_NOT_OPEN(-3,"蓝牙不能打开"),
   
    TERM_NOT_FOUND(-1,"POS终端未找到！"),
   
    CONNECTION_FAIL(0,"连接失败，设备未在附近"),
	

	//=========== POSP  返回类  ===============//
	
	POSP_SESSION_NULL(601,"交易失败，中心会话失效，请重新协商！");
	
	
	private final int type;
	
	private final String name;
	
	ResponseCode(int type,String name){
		this.name = name;
		this.type = type;
	}
	
    public static ResponseCode convert(int type){
    	for(ResponseCode resp:ResponseCode.values()){
    		if(resp.getValue()==type)
    			return resp;
    	}
    	return EXEC_CMD_FAIL;
    }
 
	public String getMessage(){
		return this.name;
	}

	public int getValue() {
		return type;
	}
}
