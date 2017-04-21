package com.yifeng.skzs.entity;

/**
 * 消费交易限额
 * 
 */
public class limitAmt {
	private String RespCode="";//	应答码	定长3位	必须	000表示成功，其余表示失败	详细解释请见附录5.1	   
	private String RespDesc;//	应答码描述	变长	最大128位	必须		   
	private String OneLimitAmt;//	单笔消费限额	变长	最大14位	必须	如果为0，表示没有限额	   
	private String SumLimitAmt;//	当日累计消费限额	变长	最大14位	必须	如果为0，表示没有限额；	失败的交易不计入当日累计消费金额	 
	public String getRespCode() {
		return RespCode;
	}
	public void setRespCode(String respCode) {
		RespCode = respCode;
	}
	public String getRespDesc() {
		return RespDesc;
	}
	public void setRespDesc(String respDesc) {
		RespDesc = respDesc;
	}
	public String getOneLimitAmt() {
		return OneLimitAmt;
	}
	public void setOneLimitAmt(String oneLimitAmt) {
		OneLimitAmt = oneLimitAmt;
	}
	public String getSumLimitAmt() {
		return SumLimitAmt;
	}
	public void setSumLimitAmt(String sumLimitAmt) {
		SumLimitAmt = sumLimitAmt;
	}
 
	
	
}
