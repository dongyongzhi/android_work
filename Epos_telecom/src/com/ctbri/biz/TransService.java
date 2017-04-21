package com.ctbri.biz;

import com.ctbri.ElecException;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.TransResponse;

/**
 * 交易管理
 * @author qin
 * 
 * 2012-12-8
 */
public interface TransService {
	/**
	 * 向pos 终端发起交易请求<br/>
	 * 
	 * 如果有冲正或签到则函数自动会做
	 * @param req  请求数据
	 * @return  8583包数据
	 */
	POSTransResponse posTransRequest(POSTransRequest req);
	
	/**
	 * 向 posp  平台发起交易请求
	 * @param resp          pos终端返回响应报文
	 * @return  交易结果
	 */
	TransResponse pospTransRequest(POSTransResponse response);
	
	/**
	 * 签到
	 * @return           是否成功
	 */
	boolean sign();
	
	/**
	 * 签到 
	 * @param response  pos 返回数据
	 * @return  是否成功
	 */ 
	boolean sign(POSTransResponse response);
	
	/**
	 * 冲正
	 * @param resp 终端返回冲正数据
	 */
	void reversal(POSTransResponse resp);
	
	/**
	 * 查询余额
	 * @return
	 */
	TransResponse queryBalance();
	
	/**
	 * 消费
	 * @param  money      金额 单位分
	 * @param  orderCode  订单编号
	 * @param  signName   签名 
	 * @return 
	 */
	TransResponse purchase(long money,String orderCode,String signName);
	
	/**
	 * 消费退款
	 * @param srcSerNo         原凭证号
	 * @param srcBatchNo       原批次号
	 * @param srcReferenceNo   原参考号
	 * @param money            退货金额
	 * @return
	 */
	TransResponse  purchaseRefund(String srcSerNo,String srcBatchNo,String srcReferenceNo,long money);
	
	/**
	 * 消费撤消<br/>
	 * 
	 * 只能撤消当天的交易
	 * 
	 * @param srcSerNo         原凭证号
	 * @param srcBatchNo       原批次号
	 * @param srcReferenceNo   原参考号
	 * @return
	 * @throws ElecException
	 */
	TransResponse revoke(String srcSerNo,String srcBatchNo,String srcReferenceNo);
}
