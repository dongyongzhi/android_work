package com.ctbri.net.yeepay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ctbri.ElecException;
import com.ctbri.biz.OrderService;
import com.ctbri.domain.CreateOrderRequest;
import com.ctbri.domain.CreateOrderResponse;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.QueryPosOrderRequest;
import com.ctbri.domain.QueryPosOrderResponse;
import com.ctbri.domain.QueryPosOrderResponse.Order;
import com.ctbri.domain.ValidateOrderRequest;
import com.ctbri.domain.ValidateOrderResponse;
import com.ctbri.net.POSPSetting;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.POSPUtils;

public class MPOSPOrderService implements OrderService {

	private final TransAction posp;
	private final static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public MPOSPOrderService(){
		this.posp = TransAction.getInstance();
	}
	
	@Override
	public ValidateOrderResponse validate(ValidateOrderRequest req) {
		//调用  posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_VALIDATEORDER, req, POSPSetting.generateKey());
		if(resp.getErrCode()!=0)
			return ElecResponse.getErrorResponse(ValidateOrderResponse.class, resp);
		else{
			//返回结果 
			ValidateOrderResponse result = new ValidateOrderResponse();
			MPOSPResponse mp = (MPOSPResponse)resp;
			try {
				result.setValidate("1".equals(mp.getResult().getString("isValidate")) ? true : false);
			} catch (JSONException e) {
				ElecLog.e(MPOSPOrderService.class, e.getMessage());
				throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
			}
			return result;
		}
	}
	

	/**创建订单*/
	@Override
	public CreateOrderResponse create(CreateOrderRequest req) {
	 
		//生成签名
		// 商户签名 Rsa（sha－1）签名（商户号＋订单号＋交易额度）验签
		String signName = POSPUtils.digest(req.getCutomerId() + req.getCutomerOrderNumber() + String.valueOf(req.getTrxAmount()));
		if(signName==null || "".equals(signName))
			throw new ElecException(POSPSetting.SYSTEM_ERROR);
		
		req.setCustomerSingName(signName);
		
		//调用  posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_CREATEORDER, req, POSPSetting.generateKey());
		if(resp.getErrCode()!=0)
			return ElecResponse.getErrorResponse(CreateOrderResponse.class, resp);
		else{
			//返回结果 
			CreateOrderResponse result = new CreateOrderResponse();
			MPOSPResponse mp = (MPOSPResponse)resp;
			try {
				result.setOrderNumber(mp.getResult().getString("OrderNumber"));
				result.setSignName(mp.getResult().getString("SignName"));
			} catch (JSONException e) {
				ElecLog.e(MPOSPOrderService.class, e.getMessage());
				throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
			}
			return result;
		}
	}

	/**查询订单*/
	@Override
	public QueryPosOrderResponse queryByPos(QueryPosOrderRequest req) {
		//调用  posp
		ElecResponse resp = posp.doAction(POSPSetting.METHOD_QUERYPOSORDERS, req, POSPSetting.generateKey());
		if(resp.getErrCode()!=0)
			return ElecResponse.getErrorResponse(QueryPosOrderResponse.class, resp);
		
		//解析数据
		QueryPosOrderResponse result = new QueryPosOrderResponse();
		MPOSPResponse mp = (MPOSPResponse)resp;
		try {
			//获取订单列表 
			JSONObject jsonRoot =  mp.getResult() ;//.JSONObject("orderparamList");  
			if(jsonRoot.has("count"))
				result.setCount(jsonRoot.getLong("count"));
			
			JSONArray arr = mp.getResult().getJSONArray("orderparamList");
			if(arr!=null && arr.length()>0){
				
				List<Order> orders = new ArrayList<Order>(arr.length());
				Order order = null;
				JSONObject json = null;
				
				int n = arr.length();
				for(int i=0;i<n;i++){
					json = arr.getJSONObject(i);
					order = new Order();
					//获取交易时间
					order.setTime(json.getJSONObject("trxtime").getLong("time")); //交易原始时间
					order.setTrxtime(df.format(new Date(order.getTime())));
					
					order.setMoney(((Double)(json.getDouble("amount") * 100)).longValue());  //金额
					order.setOrderCode(json.getString("ordercode"));
					order.setStatus(json.getString("status"));
					
					order.setPosBatch(json.getString("posBatch"));//批次号
					order.setPosRequestId(json.getString("posRequestId"));//凭证号
					order.setExternalid(json.getString("externalId"));//流水号
					order.setCardNo(json.getString("pan"));//卡号后四位
					order.setValidateUrl(json.getString("path"));//电子签名地址URL
					
					orders.add(order);
				}
				//对 订单进行时间排序
			    Collections.sort(orders, new Comparator<Order>(){
					@Override
					public int compare(Order order1, Order order2) {
						return order2.getTime().compareTo(order1.getTime());
					}
			    });
				result.setOrders(orders);
			}
			return result;
		} catch (JSONException e) {
			ElecLog.e(MPOSPOrderService.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		}
	}

}
