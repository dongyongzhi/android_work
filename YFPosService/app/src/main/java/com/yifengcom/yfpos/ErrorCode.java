package com.yifengcom.yfpos;


/**
 * 错误代码
 * @author qc
 *
 */
public enum ErrorCode {
	
	
	SUCC(0x00,"成功"),
	
	NO_MASTER_KEY(0x01,"主密钥不存在"),
	
	NO_WORK_KEY(0x02,"工作密钥不存在"),
	
	CHECKVALUE_ERROR(0x03,"CHECKVALUE错误"),
	
	ERROR_PARAM(0x04,"参数错误"),
	
	VAR_LEN_ERROR(0x05,"可变数据域长度错误"),
	
	FRAME_FORMAT_ERROR(0x06,"帧格式错误"),
	
	TERM_EXEC_EXCEPTION(0x07,"终端执行异常"),
	
	DB_OPERATION_ERROR(0x08,"数据库操作失败"),
	
	NO_PRINTER(0x09,"无打印机"),
	
	UNKNOWN_CMD(0x0A,"未知指令"),
	
	LRC_CHECK_FAIL(0x0B,"LRC校验失败"),
	
	TIMEOUT(0x0C,"处理超时"),
	
	OTHER(0x0D,"其它"),
	
	TERM_LOCK(0x0E,"终端锁机"),
	
	NOT_SUPPORT_PARAM(0x0F,"暂不支持该参数"),
	
	CANCEL(0x10,"用户取消"),
	
    CMD_EXEC_FAIL(0x11,"命令失败"),
	
    ILLEGAL_CMD_SEQ(0x12,"非法命令序列"),
    
    SWIPER_FAIL(0x13,"刷卡失败"),
    
    SWIPER_TIMEOUT(0x14,"刷卡超时"),
    
    ILLEGAL_DATA(0x15,"非法数据"),
    
    EMPTY_PASSWORD(0x16,"空密码"),
    
    CANCEL_INPUT_PASSWORD(0x17,"取消输密"),
    
    INPUT_PASSWORD_TIMEOUT(0x18,"输密超时"),
    
    
    UPLOAD_SIGIN_ERROR1(0x19,"存储失败"),
    
    UPLOAD_SIGIN_ERROR2(0x20,"堆空间申请失败"),
    
    UPLOAD_SIGIN_ERROR3(0x21,"图片数据超长"),
    
    UPLOAD_SIGIN_ERROR4(0x22,"图片宽度超过384"),
    
    
	DEVICE_CLOSE(0xD0,"设备未打开或已关闭 "),
	
	OPEN_DEVICE_FAIL(0xD1,"打开设备失败"),
	
	SEND_DATA_FAIL(0xD2,"数据发送失败"),
	
	NOT_SUPPORT(0xD3,"不支持"),
	
	INTERRUPTED(0xD4,"执行中断"),
	
	FILENOTFOUND(0xE0,"未找到文件"),
	
	UNKNOWN(0xFF,"未知错误"),
	
	BUSY(0xCC,"正忙，请稍后再试");
	
	
	private final int code;
	private final String defaultMessage;

	ErrorCode(int code,String defaultMessage) {
		this.code = code;
		this.defaultMessage = defaultMessage;
	}
	
	public static ErrorCode convert(int code) {
		for(ErrorCode e : ErrorCode.values()) {
			if(e.getCode() == code) {
				return e;
			}
		}
		return UNKNOWN;
	}

	public int getCode() {
		return code;
	}
	
	public String getDefaultMessage() {
		return defaultMessage;
	}
	
}
