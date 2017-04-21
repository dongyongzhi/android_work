package com.yifeng.skzs.entity;
/**
 * 消费撤销接口
 * 
 */
public class QueryRefundOrder {
	
	private String RespCode="";   	// 应答码	定长3位	必须	000表示成功，其余表示失败	详细解释请见附录5.1	   
	private String OrdId;		//消费撤销订单号	最大20位	必须		   
	private String RespDesc;	//应答码描述	变长	最大128位	必须		   
	private String TransStat;	//订单状态	固定1位	必须	S – 成功	F – 失败	I – 初始化	   
	 
	private String OrdAmt;	//订单金额	最大14位	必须		   
	private String PayOrdId;	//原支付订单号	最大20位	必须		 

	public String getRespCode() {
		return RespCode;
	}
	public void setRespCode(String respCode) {
		RespCode = respCode;
	}
	public String getOrdId() {
		return OrdId;
	}
	public void setOrdId(String ordId) {
		OrdId = ordId;
	}
	 
	public String getRespDesc() {
		return RespDesc;
	}
	public void setRespDesc(String respDesc) {
		RespDesc = respDesc;
	}
	 
	public String getTransStat() {
		return TransStat;
	}
	public void setTransStat(String transStat) {
		TransStat = transStat;
	}
	public String getOrdAmt() {
		return OrdAmt;
	}
	public void setOrdAmt(String ordAmt) {
		OrdAmt = ordAmt;
	}
	public String getPayOrdId() {
		return PayOrdId;
	}
	public void setPayOrdId(String payOrdId) {
		PayOrdId = payOrdId;
	}
	 
	
}
