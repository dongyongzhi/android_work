package com.ctbri.net.yeepay;

import org.json.JSONException;

import android.content.Context;

import com.ctbri.ElecException;
import com.ctbri.biz.TransService;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.net.POSPSetting;
import com.ctbri.pos.ElecPosService;
import com.ctbri.push.NotificationService;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.POSPUtils;
import com.yifeng.iso8583.StringUtils;

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
		
		notification(MessageType.QUERYBALANCE,NotificationService.STATE_SERVICE_START);
		
		POSTransRequest req = new POSTransRequest();
		req.setMessageType(MessageType.QUERYBALANCE);  //查询交易
		
		POSTransResponse resp = posTransRequest(req); //pos机操作，刷卡和输入密码
		return pospTransRequest(resp);
	}
	
	/**签到*/
	public boolean sign() {
		
		ElecLog.d(getClass(), "===========签到==========");
		
		notification(MessageType.SIGN,NotificationService.STATE_SERVICE_START);
		POSTransRequest req = new POSTransRequest();
		req.setMessageType(MessageType.SIGN); //签到
		
		POSTransResponse resp = pos.transRequest(req); //pos处理 获取签到数据
		//己签无签到数据返回，则直接返回成功
		if(resp.getState()==ResponseCode.SUCCESS && resp.getLen() == 0){ 
			if(resp.getTransResponse().getErrCode()==0)
				return true;
			else
				return false;
		}
		if(resp.getState()==ResponseCode.TRANS_REVERSAL){ //需要冲正
			doReversal(resp);
			
			//冲正成功，再次获取签到数据
			notification(MessageType.SIGN,NotificationService.STATE_REQUEST_POS);
			resp = pos.transRequest(req); //再次获取签到数据
		}
		
		//未签到  或 成功但有签到数据返回，则去签到
		if(resp.getState() == ResponseCode.TRANS_NO_SIGN || resp.getState() == ResponseCode.SUCCESS){ 
			return doSign(MessageType.SIGN,resp);
			
		} else {
			throw new ElecException(resp.getState().getMessage());
		}
	}
	/**签到*/
	public boolean sign(POSTransResponse response) {
		ElecLog.d(getClass(), "===========签到==========");
		notification(MessageType.SIGN,NotificationService.STATE_SERVICE_START);
		return doSign(MessageType.SIGN,response);
	}
	
	/**冲正*/
	public void reversal(POSTransResponse resp) {
		this.doReversal(resp);
	}
	
	/**消费*/
	public TransResponse purchase(long money,String orderCode,String signName) {
		ElecLog.d(getClass(), "===========消费==========");
		
		notification(MessageType.PURCHASE,NotificationService.STATE_SERVICE_START);
		
		POSTransRequest req = new POSTransRequest();
		req.setMessageType(MessageType.PURCHASE);
		req.setMoney(money);
		req.setOrderCode(orderCode);
		req.setSignName(signName);
		
		POSTransResponse resp = posTransRequest(req); //pos机操作，刷卡和输入密码
		return pospTransRequest(resp);
		
	}

	/**退货*/
	public TransResponse purchaseRefund(String srcSerNo, String srcBatchNo,String srcReferenceNo, long money) {
		notification(MessageType.RETURNS,NotificationService.STATE_SERVICE_START);
		ElecLog.d(getClass(), "===========退货==========");
		
		POSTransRequest req = new POSTransRequest();
		req.setMessageType(MessageType.RETURNS);
		req.setMoney(money);
		req.setOriginalSerialNumber(srcSerNo);
		req.setOriginalBatchNumber(srcBatchNo);
		req.setReferenceNumber(srcReferenceNo);
 
		POSTransResponse resp = posTransRequest(req); //pos机操作
		return pospTransRequest(resp);
	}

	/** 消费撤销 */
	public TransResponse revoke(String srcSerNo, String srcBatchNo,String srcReferenceNo) {
		notification(MessageType.RETURNS,NotificationService.STATE_SERVICE_START);
		ElecLog.d(getClass(), "===========消费撤销==========");
		
		POSTransRequest req = new POSTransRequest();
		req.setMessageType(MessageType.CANCEL_PURCHASE);
		req.setOriginalSerialNumber(srcSerNo);
		req.setOriginalBatchNumber(srcBatchNo);
		req.setReferenceNumber(srcReferenceNo);
		
		POSTransResponse resp = posTransRequest(req); //pos机操作
		return pospTransRequest(resp);
	}
	
	/**向pos终端发起交易请求*/
	public POSTransResponse posTransRequest(POSTransRequest req) {
		notification(req.getMessageType(),NotificationService.STATE_REQUEST_POS);
		POSTransResponse response = pos.transRequest(req);
		
		if(response.getState()==ResponseCode.TRANS_NO_SIGN){     //pos返回签到
			if(!doSign(req.getMessageType(),response)){   //处理签到
				response.setState(ResponseCode.TRANS_SIGN_FAIL.getValue()); //签到失败
				response.setData(null);
				return response;
			}
			//再次向pos请求数据
			notification(req.getMessageType(),NotificationService.STATE_REQUEST_POS);
			response = pos.transRequest(req);
			
		}else if(response.getState()==ResponseCode.TRANS_REVERSAL){ //需要冲正
			doReversal(response);//冲正
			//再次向pos请求数据
			notification(req.getMessageType(),NotificationService.STATE_REQUEST_POS);
			response = pos.transRequest(req);
		}
		return response;
	}

	/**向 posp 平台请求交易*/
	public TransResponse pospTransRequest(POSTransResponse response) {
		String pospUrl = getTransUrl(response.getMessageType());//获取 posp url地址
		
		if(response.getState() == ResponseCode.SUCCESS){ //返回成功，则进行交易
			if(response.getLen() == 0 || response.getData()==null  || response.getData().length==0){  //数据错误直接 返回
				return ElecResponse.getErrorResponse(TransResponse.class,ResponseCode.EXEC_CMD_FAIL);
			}
			notification(response.getMessageType(),NotificationService.STATE_REQUEST_POSP);
			ElecResponse resp = posp.doAction(pospUrl, POSPUtils.getPayData(response), POSPSetting.generateKey());  //发起posp中心交易请求
			//如果出错则直接返回
		    if(resp.getErrCode()!=0)
		    	return ElecResponse.getErrorResponse(TransResponse.class,resp.getErrCode(),resp.getErrMsg());
		    
		    //取出posp中心返回的数据
			byte[] data=null;
			try {
				data = StringUtils.hexToBytes(((MPOSPResponse)resp).getResult().getString(POSPSetting.KYE_DATA));
			} catch (JSONException e) {
				ElecLog.e(getClass(), e.getMessage(),e);
				return ElecResponse.getErrorResponse(TransResponse.class,1,POSPSetting.POSP_CLIENT_FAIL);
			}
			if(data==null)
				return ElecResponse.getErrorResponse(TransResponse.class,1,POSPSetting.POSP_CLIENT_FAIL);
			
			//posp 平台返回数据 处理
			notification(response.getMessageType(),NotificationService.STATE_POSP_RESPONSE);
			response  = pos.transResponse(data);  //向送给 pos终端解包
			
			if(response.getState() == ResponseCode.SUCCESS){  //成功则直接返回
				return response.getTransResponse();
			}
			else if(response.getState() == ResponseCode.TRANS_REVERSAL)  //返回冲正时，再次进行交易请求
				doReversal(response);
			 
			return ElecResponse.getErrorResponse(TransResponse.class,response.getState());  //返回错误码
			 
		}else 
			return ElecResponse.getErrorResponse(TransResponse.class,response.getState());
	}
	
	/**
	 * 冲正
	 * @param req     要冲正的数据(由pos返回的冲正数据包)
	 * @throws ElecException 
	 */
	private void doReversal(POSTransResponse req){
		notification(req.getMessageType(),NotificationService.STATE_REVERSAL);
		ElecLog.d(this.getClass(), "==================正在冲正==================");
		
	    if(req.getLen()==0)
	    	throw new ElecException(ResponseCode.TRANS_REVERSAL_DATA_ERROR.getMessage());
		
		ElecResponse response =	posp.doAction(POSPSetting.METHOD_REVERSAL, POSPUtils.getPayData(req), POSPSetting.generateKey());  //发起posp中心交易请求
		if(response.getErrCode()!=0){ //冲正返回错误
			throw new ElecException(response.getErrMsg());
			//doReversal(req);//继续冲正
			//return;
		}
		
		//取出posp中心返回的数据
		byte[] data=null;
		try {
			data = StringUtils.hexToBytes(((MPOSPResponse)response).getResult().getString(POSPSetting.KYE_DATA));
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(),e);
			//失败继续冲正
			doReversal(req);
		}
		if(data==null) //失败继续冲正
			doReversal(req);
		 
		POSTransResponse resp = pos.transResponse(data);  //pos处理冲正结果
		if(resp.getState()== ResponseCode.SUCCESS){  //冲正成功直接返回
			return; 
		}else if(resp.getState() == ResponseCode.TRANS_REVERSAL){ //返回冲正
			doReversal(resp); //继续冲正
		}else
			throw new ElecException(resp.getState().getMessage());
		
		ElecLog.d(this.getClass(), "==================冲正完成==================");
	}
	
	/**
	 * 签到<br/>
	 * 
	 * 可能在别的交易里面发生签到
	 * 
	 * @param messageType 交易类型 
	 * 
	 * @param data   签到数据(由pos返回的签到数据包)
	 * @return       是否成功
	 * @throws ElecException
	 */
	private boolean doSign(int messageType,POSTransResponse req) throws ElecException{
		
		notification(messageType,NotificationService.STATE_SIGN);
		ElecLog.d(this.getClass(), "==================正在签到==================");
		
		
		ElecResponse response =	posp.doAction(POSPSetting.METHOD_SIGNIN, POSPUtils.getPayData(req), POSPSetting.generateKey());  //发起posp中心交易请求
		if(response.getErrCode()!=0)
			return false;
		
		//取出posp中心返回的数据
		byte[] data=null;
		try {
			data = StringUtils.hexToBytes(((MPOSPResponse)response).getResult().getString(POSPSetting.KYE_DATA));
		} catch (JSONException e) {
			ElecLog.e(getClass(), e.getMessage(),e);
			//签到失败
			return false;
		}
		if(data==null) //签到失败
			return false;
		 
		POSTransResponse resp = pos.transResponse(data);  //pos处理签到结果
		ElecLog.d(this.getClass(), "==================签到完成==================");
		
		if(resp.getState()== ResponseCode.SUCCESS){  //签到结果
		    if(resp.getTransResponse().getErrCode()==0)
		    	return true;
		    else
		    	return false;
		}else
			return false;
	}
	
	/**
	 * 获取交易 url
	 * @param messageType  信息类型
	 * @return
	 */
	private String getTransUrl(int messageType){
		if(messageType == MessageType.CANCEL_PURCHASE)
			return POSPSetting.METHOD_REVOKE;
		else if(messageType == MessageType.PURCHASE)
			return POSPSetting.METHOD_PURCHASE;
		else if(messageType == MessageType.QUERYBALANCE)
			return POSPSetting.METHOD_QUERYBALANCE;
		else if(messageType== MessageType.RETURNS)
			return POSPSetting.METHOD_PURCHASEREFUND;
		else if(messageType==MessageType.SIGN)
			return POSPSetting.METHOD_SIGNIN;
		return null;
	}

	/**
	 * 业务状态通知
	 * @param state  状态
	 */
	private void notification(int messageType,int state){
		notification.notificationTransStateChange(messageType,state);
	}

	public void setPos(ElecPosService pos) {
		this.pos = pos;
	}


}
