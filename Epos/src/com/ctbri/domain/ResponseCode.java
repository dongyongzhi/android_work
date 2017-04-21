package com.ctbri.domain;

import com.yfcomm.pos.bt.device.DeviceComm;

/**
 * 返回码定义
 * @author qc
 *
 */
public enum ResponseCode {
	
	SUCCESS("00","执行成功"),
	
	//========POS 通信===============//
	
	CONNECT_ERROR("01","未连接终端"),
	
	TIMEOUT("02","终端通信超时"),
	
	DATA_ERROR("03", "通讯故障，接收数据不完整或校验错误"),
	
	//==============POS 交易返回 码定义====================//
	
	CANCEL("04","用户取消交易或操作超时"),
	
	
	HARDWARE_ERROR("05","终端硬件错误"),
	
	
	NO_SIGN("06","终端未签到"),
	
	
	REVERSAL("07","终端需要冲正"),
	
	
	DATA_RESENT("08","终端密码输入错误，用户已经重新输入密码"),
	
	
	NOT_REVERSAL("09","终端不需要冲正"),
	
	
	NOEXISTS_POSNO("10","商户号或终端号不正确"),
	
	
	TRANS_ERROR("11","数据有误，终端解析失败"),
	
	
	AUTH_ERROR("12","认证失败"),
	 

	//=========== POSP  返回类  ===============//
	
	POSP_RESPONSE_500("500", "中心故障，通讯失败！"),
	
	POSP_SESSION_NULL("601","交易失败，中心会话失效，请重新协商！"),
	
	POSP_CLIENT_FAIL("602","中心处理失败"),
	
	TRANS_SIGN_FAIL("602","签到失败");
	
	
	private final String code;
	
	private final String name;
	
	ResponseCode(String code,String name){
		this.name = name;
		this.code = code;
	}
	
    public static ResponseCode convert(String code){
    	for(ResponseCode resp:ResponseCode.values()){
    		if(resp.getCode().equals(code))
    			return resp;
    	}
    	return DATA_ERROR;
    }
 
	public String getMessage(){
		return this.name;
	}

	public String getCode() {
		return code;
	}

	/**
	 * 解析 调用 pos 接口返回码
	 * @param deivceResultCode
	 * @return
	 */
	public static ResponseCode resultCode(int deivceResultCode) {
		ResponseCode code;
		switch(deivceResultCode) {
		
		case  DeviceComm.SUCCESS:
			code = ResponseCode.SUCCESS;
			break;
			
		case  DeviceComm.CONNECT_ERROR:
			code = ResponseCode.CANCEL;
			break;
			
		case DeviceComm.TIMEOUT:
			code = ResponseCode.TIMEOUT;
			break;
			
		case DeviceComm.DATA_ERROR:
			code = ResponseCode.DATA_ERROR;
			break;
			
		default:
			code = ResponseCode.DATA_ERROR;
			break;
		}
		return code;
	}
	
	
	/**
	 * 解析打包命令交易返回码
	 * @param src  命令返回数据
	 * @return  code
	 */
	public static ResponseCode packDataResult(byte[] src) {
		if(src.length<2) {
			return ResponseCode.DATA_ERROR;
		}
		//交易返回码
		String code = new String(src,0,2);
		if("00".equals(code)) {
			return ResponseCode.SUCCESS;
			
		} else if(code.equals("01")) {
			//表示用户取消交易或操作超时。
			return ResponseCode.CANCEL;
			
		} else if(code.equals("02")) {
			//终端硬件错误。
			return ResponseCode.HARDWARE_ERROR;
			
		} else if(code.equals("03")) {
			//终端未签到
			return ResponseCode.NO_SIGN;
			
		} else if(code.equals("04")) {
			 //表示PTE终端需要冲正
			return ResponseCode.REVERSAL;
			
		} else if(code.equals("06")) {
			 //表示PTE终端不需要冲正
			return ResponseCode.NOT_REVERSAL;
		
		} else if(code.equals("07")) {
			 //表示商户号或终端号不正确
			return ResponseCode.NOEXISTS_POSNO;
		
		} else if(code.equals("08")) {   
			//表示数据有误，PTE解析失败用于带数据的交易类型
			return ResponseCode.TRANS_ERROR;
		
		} else {
			return ResponseCode.DATA_ERROR;
		}
	}
}
