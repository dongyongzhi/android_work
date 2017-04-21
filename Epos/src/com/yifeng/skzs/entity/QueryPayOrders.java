package com.yifeng.skzs.entity;
/**
 * 消费明细
 */
public class QueryPayOrders {
	
	private String CustId; 		//  用户客户号	最大16位	必须		   
	private String BeginDate;	//	查询起始时间	固定8位	必须	格式: yyyyMMdd，	交易包含BeginDate	   
	private String EndDate;		//	查询结束时间	固定8位	必须	格式: yyyyMMdd，	交易包含EndDate	   
	private String TransStat;	//	订单状态	最大16位	必须	S – 成功	F – 失败	I – 初始化	   
	private String PageSize;	//	查询每页记录数	最大3位	必须	最多100条记录	   
	private String PageNum;		//	查询页数	最大4位	必须	第一页则为1	 	返回数据	参数	定义	长度	非空	说明	   
	private String RespCode="";	//	应答码	定长3位	必须	000表示成功，其余表示失败	详细解释请见附录5.1	   
	private String RespDesc;	//	应答码描述	变长	最大128位	必须		   
	private String CurrentDate;	//	服务器当前日期	固定8位	必须	形如yyyyMMdd	   
	private String OrdersInfo;	//	当日累计交易金额		必须	订单信息集合
		//OrdersInfo中的数据	参数	定义	长度	非空	说明	   
		private String AcctDate;//	订单交易日期	固定8位	必须	形如yyyyMMdd	   
		private String SysSeqId;//	服务器端交易流水号	变长	最大12位	必须		   
		private String OrdId;	//	订单号	变长	最大20位	必须		   
		private String OrdAmt;	//	交易金额	变长	最大14位	必须	小数点后保留2位	   
		private String OrdStat;	//	订单状态	固定1位	必须	S – 成功	F – 失败	I – 初始化	   
		private String MtId;	//	移动终端设备号	固定12位	必须		   
		private String PayCard;	//	交易银行卡号	变长	最大25位	必须		   
		private String SysTime;	//	交易时间	固定6位	必须	形如hhmmss	 

	
//	private String PageSize;//	查询每页记录数	最大3位	必须	最多100条记录	   
//	private String PageNum;//	查询页数	最大4位	必须	第一页则为1	   
	private String TotalNum;//	总记录数		必须	符合条件的记录总数	 


	public String getCustId() {
		return CustId;
	}


	public void setCustId(String custId) {
		CustId = custId;
	}


	public String getBeginDate() {
		return BeginDate;
	}


	public void setBeginDate(String beginDate) {
		BeginDate = beginDate;
	}


	public String getEndDate() {
		return EndDate;
	}


	public void setEndDate(String endDate) {
		EndDate = endDate;
	}


	public String getTransStat() {
		return TransStat;
	}


	public void setTransStat(String transStat) {
		TransStat = transStat;
	}


	public String getPageSize() {
		return PageSize;
	}


	public void setPageSize(String pageSize) {
		PageSize = pageSize;
	}


	public String getPageNum() {
		return PageNum;
	}


	public void setPageNum(String pageNum) {
		PageNum = pageNum;
	}


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


	public String getCurrentDate() {
		return CurrentDate;
	}


	public void setCurrentDate(String currentDate) {
		CurrentDate = currentDate;
	}


	public String getOrdersInfo() {
		return OrdersInfo;
	}


	public void setOrdersInfo(String ordersInfo) {
		OrdersInfo = ordersInfo;
	}


	public String getAcctDate() {
		return AcctDate;
	}


	public void setAcctDate(String acctDate) {
		AcctDate = acctDate;
	}


	public String getSysSeqId() {
		return SysSeqId;
	}


	public void setSysSeqId(String sysSeqId) {
		SysSeqId = sysSeqId;
	}


	public String getOrdId() {
		return OrdId;
	}


	public void setOrdId(String ordId) {
		OrdId = ordId;
	}


	public String getOrdAmt() {
		return OrdAmt;
	}


	public void setOrdAmt(String ordAmt) {
		OrdAmt = ordAmt;
	}


	public String getOrdStat() {
		return OrdStat;
	}


	public void setOrdStat(String ordStat) {
		OrdStat = ordStat;
	}


	public String getMtId() {
		return MtId;
	}


	public void setMtId(String mtId) {
		MtId = mtId;
	}


	public String getPayCard() {
		return PayCard;
	}


	public void setPayCard(String payCard) {
		PayCard = payCard;
	}


	public String getSysTime() {
		return SysTime;
	}


	public void setSysTime(String sysTime) {
		SysTime = sysTime;
	}


	public String getTotalNum() {
		return TotalNum;
	}


	public void setTotalNum(String totalNum) {
		TotalNum = totalNum;
	}

	
	
	
}
