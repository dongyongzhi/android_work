package com.ctbri.net.yeepay;


import android.content.Context;

import com.ctbri.ElecException;
import com.ctbri.biz.TransService;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.POSTransRequest;
import com.ctbri.pos.support.POSPurchaseRefundRequest;
import com.ctbri.pos.support.POSPurchaseRequest;
import com.ctbri.pos.support.POSQueryBalanceRequest;
import com.ctbri.pos.support.POSRevokeRequest;
import com.ctbri.pos.support.POSSignRequest;
import com.ctbri.push.NotificationService;
import com.ctbri.utils.ElecLog;

/**
 * 交易管理类
 * @author qin
 * 
 * 2012-12-8
 */
public class MPOSPTransService implements TransService {

	
	
	private final TransAction posp;    // posp 平台
	private ElecPosService  pos;  //pos 终端 
	private NotificationService  notification;  //业务状态变更通知 (只通知交易中状态变更情况)
	
	
	public MPOSPTransService(ElecPosService pos,Context context){
		this.posp = TransAction.getInstance();
		this.pos = pos;
		this.notification = new NotificationService(context);
	}
	
	/**查询余额*/
	public TransResponse queryBalance() {
		ElecLog.d(getClass(), "===========查询余额==========");
		
		notification(NotificationService.STATE_SERVICE_START);
		
		POSTransRequest req = new POSQueryBalanceRequest();
		POSTransResponse resp = posTransRequest(req); //pos机操作，刷卡和输入密码
		return pospTransRequest(resp);
	}
	
	/**签到*/
	public boolean sign(String operNo,String operPwd) {
		
		ElecLog.d(getClass(), "===========签到==========");
		
		notification(NotificationService.STATE_SERVICE_START);
		
		//pos 机交易组包请求
		POSSignRequest req = new POSSignRequest();
		req.setOperNo(operNo);
		req.setOperPwd(operPwd);
		 
		//pos处理 获取签到数据
		POSTransResponse resp = pos.transRequest(req); 
		//己签无签到数据返回，则直接返回成功
		if(resp.getCode()==ResponseCode.SUCCESS && resp.getLen() == 0){ 
			throw new ElecException(ResponseCode.DATA_ERROR.getMessage());
		}
		
		if(resp.getCode() == ResponseCode.REVERSAL){
			 //需要冲正，则调用冲正接口
			doReversal(resp);
			
			//冲正成功，再次获取签到数据
			notification(NotificationService.STATE_REQUEST_POS);
			//再次获取签到数据
			resp = pos.transRequest(req); 
		}
		
		//未签到  或 成功但有签到数据返回，则去签到
		if(resp.getCode() == ResponseCode.NO_SIGN || resp.getCode() == ResponseCode.SUCCESS){ 
			
			//执行签到
			return doSign(resp);
			
		} else {
			throw new ElecException(resp.getCode().getMessage());
		}
	}
 
	/**消费*/
	public TransResponse purchase(long money,String orderCode,String signName) {
		ElecLog.d(getClass(), "===========消费==========");
		
		notification(NotificationService.STATE_SERVICE_START);
		
		POSPurchaseRequest req = new POSPurchaseRequest();
		req.setMoney(money);
		POSTransResponse resp = posTransRequest(req); //pos机操作，刷卡和输入密码
		return pospTransRequest(resp);
		
	}

	/**退货*/
	public TransResponse purchaseRefund(String managerPwd,String srcSerNo, String srcBatchNo,String srcReferenceNo, long money) {
		notification(NotificationService.STATE_SERVICE_START);
		ElecLog.d(getClass(), "===========退货==========");
		
		POSPurchaseRefundRequest req = new POSPurchaseRefundRequest();
		req.setManagerPwd(managerPwd);
		req.setOriginalSerialNumber(srcSerNo);
		req.setOriginalBatchNumber(srcBatchNo);
		req.setReferenceNumber(srcReferenceNo);
 
		POSTransResponse resp = posTransRequest(req); //pos机操作
		return pospTransRequest(resp);
	}

	/** 消费撤销 */
	public TransResponse revoke(String managerPwd,String srcSerNo, String srcBatchNo,String srcReferenceNo) {
		notification(NotificationService.STATE_SERVICE_START);
		ElecLog.d(getClass(), "===========消费撤销==========");
		
		POSRevokeRequest req = new POSRevokeRequest();
		req.setOriginalSerialNumber(srcSerNo);
		req.setManagerPwd(managerPwd);
		
		POSTransResponse resp = posTransRequest(req); //pos机操作
		return pospTransRequest(resp);
	}
	
	/**向pos终端发起交易请求*/
	public POSTransResponse posTransRequest(POSTransRequest req) {
		notification(NotificationService.STATE_REQUEST_POS);
		POSTransResponse response = pos.transRequest(req);
		
		//pos终端组包返回
		
		if(response.getCode()==ResponseCode.NO_SIGN){ 
			
			//未签到直接返回
			return response;
			
		}else if(response.getCode()==ResponseCode.REVERSAL){ 
			//需要冲正
			doReversal(response); 
			//再次向pos请求数据
			notification(NotificationService.STATE_REQUEST_POS);
			//冲正完成，再次请求pos组包数据
			response = pos.transRequest(req);
		}
		return response;
	}

	/**向 posp 平台请求交易*/
	public TransResponse pospTransRequest(POSTransResponse response) {
		
		if(response.getCode() == ResponseCode.SUCCESS){ 
			//返回成功，则进行交易
			if(response.getLen() == 0 || response.getData()==null  || response.getData().length==0){  
				//数据错误直接 返回
				return ElecResponse.getErrorResponse(TransResponse.class,ResponseCode.DATA_ERROR);
			}
			
			notification(NotificationService.STATE_REQUEST_POSP);
			
			
			//ElecResponse resp = posp.doAction(pospUrl, POSPUtils.getPayData(response), POSPSetting.generateKey());  //发起posp中心交易请求
			
			//如果出错则直接返回
		    //if(!resp.isSuccess())
		    //	return ElecResponse.getErrorResponse(TransResponse.class,resp.getErrCode(),resp.getErrMsg());
		    
		    //取出posp中心返回的数据
			byte[] data=posp.sendTransData(response.getData(), 0, response.getLen());
	 
			if(data==null) {
				return ElecResponse.getErrorResponse(TransResponse.class,ResponseCode.POSP_CLIENT_FAIL);
			}
			
			//posp 平台返回数据 处理
			notification(NotificationService.STATE_POSP_RESPONSE);
			//向送给 pos终端解包
			response  = pos.transResponse(data);  
			
			 //成功则直接返回
			if(response.getCode() == ResponseCode.SUCCESS){
				
				return response.decode();
			}
			else if(response.getCode() == ResponseCode.REVERSAL)  {
				//返回冲正时，再次进行交易请求
				doReversal(response);
			}
			 
			return ElecResponse.getErrorResponse(TransResponse.class,response.getCode());  //返回错误码
			 
		}else 
			return ElecResponse.getErrorResponse(TransResponse.class,response.getCode());
	}
	
	/**
	 * 冲正
	 * @param req     要冲正的数据(由pos返回的冲正数据包)
	 * @throws ElecException 
	 */
	private void doReversal(POSTransResponse req){
		notification(NotificationService.STATE_REVERSAL);
		ElecLog.d(this.getClass(), "==================正在冲正==================");
		
		//冲正数据不正确
	    if(req.getLen()==0) {
	    	throw new ElecException(ResponseCode.DATA_ERROR.getMessage());
	    }
		
	    //发起posp中心交易请求
		//ElecResponse response =	posp.doAction(POSPSetting.METHOD_REVERSAL, POSPUtils.getPayData(req), POSPSetting.generateKey());  
		//if(!response.isSuccess()){ 
			//冲正返回错误
		//	throw new ElecException(response.getErrMsg());
			//doReversal(req);//继续冲正
			//return;
		//}
		
		//取出posp中心返回的数据
		byte[] data= posp.sendTransData(req.getData(), 0, req.getLen());
		 
		if(data==null) {
			//失败继续冲正
			doReversal(req);
		}
		 
		//pos解包处理冲正结果
		POSTransResponse resp = pos.transResponse(data); 
		if(resp.getCode()== ResponseCode.SUCCESS){ 
			 //冲正成功直接返回
			return; 
		}else if(resp.getCode() == ResponseCode.REVERSAL){ 
			//返回冲正继续冲正
			doReversal(resp); 
		}else {
			throw new ElecException(resp.getCode().getMessage());
		}
		
		ElecLog.d(this.getClass(), "==================冲正完成==================");
	}
	
	/**
	 * 签到<br/>
	 * 
	 * 可能在别的交易里面发生签到
	 * 
	 * @param messageType 原始请求交易类型 
	 * 
	 * @param data   签到数据(由pos返回的签到数据包)
	 * @return       是否成功
	 * @throws ElecException
	 */
	private boolean doSign(POSTransResponse req) throws ElecException{
		
		notification(NotificationService.STATE_SIGN);
		ElecLog.d(this.getClass(), "==================正在签到==================");
		
		//发起posp中心交易请求
		//ElecResponse response =	posp.doAction(POSPSetting.METHOD_SIGNIN, POSPUtils.getPayData(req), POSPSetting.generateKey()); 
		//if(!response.isSuccess()) {
		//	return false;
		//}
		
		//取出posp中心返回的数据
		byte[] data= posp.sendTransData(req.getData(), 0, req.getLen());
		 
		//签到失败
		if(data==null) {
			return false;
		}
	 
		 //pos处理签到结果
		POSTransResponse resp = pos.transResponse(data); 
		ElecLog.d(this.getClass(), "==================签到完成==================");
		
		if(resp.getCode() == ResponseCode.SUCCESS) {
			//获取平台交易结果
			return resp.decode().isSuccess();
		}
		//签到结果
		return false;
	}
	
	/**
	 * 业务状态通知
	 * @param state  状态
	 */
	private void notification(int state){
		notification.notificationTransStateChange(state);
	}

	public void setPos(ElecPosService pos) {
		this.pos = pos;
	}

}
