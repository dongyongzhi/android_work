package com.yifeng.skzs.entity;
/**
 * 消费撤销接口
 * 
 */
public class DoCancel {
	
	private String RespCode="";   	// 应答码	定长3位	必须	000表示成功，其余表示失败	详细解释请见附录5.1	   
	private String OrdId;		//消费撤销订单号	最大20位	必须		   
	private String OrdAmt;		//消费撤销订单金额	最大14位	必须	小数点后保留两位，例如：12.00	   
	private String PayOrdId;	//原消费订单号	最大20位	必须		   
	private String InfoField;	//关键域信息		必须		   
	private String ChkValue;	//签名	固定32位	必须		 
	private String RespDesc;	//应答码描述	变长	最大128位	必须		   
	private String RefOrdId;	//退款订单号	最大20位	必须		   
	private String RefOrdAmt;	//订单金额	最大14位	必须		   
	private String TransStat;	//订单状态	固定1位	必须	S – 成功	F – 失败	I – 初始化	   
	
	private String PosMerId;	//特约商户号	固定16位	必须
	public String getPosMerId() {
		return PosMerId;
	}
	public void setPosMerId(String posMerId) {
		PosMerId = posMerId;
	}
	private String PosMerName;	//特约商户名称	固定32位	必须	
	private String PnrDevId;	//汇付终端号	固定8位	必须	
	private String MerName;		//	汇付商户名		必须	
	private String PosBankId;	//收单机构	固定8位	可选	8位数字或4个汉字
	private String IssuingBank;	//发卡银行	固定8位	可选	8位数字或4个汉字
	private String PosTermId;	//银联终端号	固定8位	必须	
	private String CardNo;		//卡号		必须	
	private String ExpireDate;	//卡有效期	固定4位	可选	
	private String BatchId;		//批次号	固定6位	必须	
	private String AuthCode;	//授权号	固定6位	必须	
	private String RefNo;		//参考号	固定12位	必须	
	private String VoucherNo;	//凭证号	固定6位	必须	
	private String CurrentDate;	//服务器当前日期	固定8位	必须	格式：yyyyMMdd
	private String CurrentTime;	//	服务器当前时间	固定6位	必须	格式：hhmmss
	private String AcqInstuId;
	private String CardBankId;
	public String getPosMerName() {
		return PosMerName;
	}
	public void setPosMerName(String posMerName) {
		PosMerName = posMerName;
	}
	public String getPnrDevId() {
		return PnrDevId;
	}
	public void setPnrDevId(String pnrDevId) {
		PnrDevId = pnrDevId;
	}
	public String getMerName() {
		return MerName;
	}
	public void setMerName(String merName) {
		MerName = merName;
	}
	public String getPosBankId() {
		return PosBankId;
	}
	public void setPosBankId(String posBankId) {
		PosBankId = posBankId;
	}
	public String getIssuingBank() {
		return IssuingBank;
	}
	public void setIssuingBank(String issuingBank) {
		IssuingBank = issuingBank;
	}
	public String getPosTermId() {
		return PosTermId;
	}
	public void setPosTermId(String posTermId) {
		PosTermId = posTermId;
	}
	public String getCardNo() {
		return CardNo;
	}
	public void setCardNo(String cardNo) {
		CardNo = cardNo;
	}
	public String getExpireDate() {
		return ExpireDate;
	}
	public void setExpireDate(String expireDate) {
		ExpireDate = expireDate;
	}
	public String getBatchId() {
		return BatchId;
	}
	public void setBatchId(String batchId) {
		BatchId = batchId;
	}
	public String getAuthCode() {
		return AuthCode;
	}
	public void setAuthCode(String authCode) {
		AuthCode = authCode;
	}
	public String getRefNo() {
		return RefNo;
	}
	public void setRefNo(String refNo) {
		RefNo = refNo;
	}
	public String getVoucherNo() {
		return VoucherNo;
	}
	public void setVoucherNo(String voucherNo) {
		VoucherNo = voucherNo;
	}
	public String getCurrentDate() {
		return CurrentDate;
	}
	public void setCurrentDate(String currentDate) {
		CurrentDate = currentDate;
	}
	public String getCurrentTime() {
		return CurrentTime;
	}
	public void setCurrentTime(String currentTime) {
		CurrentTime = currentTime;
	}
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
	public String getInfoField() {
		return InfoField;
	}
	public void setInfoField(String infoField) {
		InfoField = infoField;
	}
	public String getChkValue() {
		return ChkValue;
	}
	public void setChkValue(String chkValue) {
		ChkValue = chkValue;
	}
	public String getRespDesc() {
		return RespDesc;
	}
	public void setRespDesc(String respDesc) {
		RespDesc = respDesc;
	}
	public String getRefOrdId() {
		return RefOrdId;
	}
	public void setRefOrdId(String refOrdId) {
		RefOrdId = refOrdId;
	}
	public String getRefOrdAmt() {
		return RefOrdAmt;
	}
	public void setRefOrdAmt(String refOrdAmt) {
		RefOrdAmt = refOrdAmt;
	}
	public String getTransStat() {
		return TransStat;
	}
	public void setTransStat(String transStat) {
		TransStat = transStat;
	}
	public String getAcqInstuId() {
		return AcqInstuId;
	}
	public void setAcqInstuId(String acqInstuId) {
		AcqInstuId = acqInstuId;
	}
	public String getCardBankId() {
		return CardBankId;
	}
	public void setCardBankId(String cardBankId) {
		CardBankId = cardBankId;
	}

	
}
