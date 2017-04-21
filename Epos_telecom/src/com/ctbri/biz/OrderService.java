package com.ctbri.biz;

import com.ctbri.domain.CreateOrderRequest;
import com.ctbri.domain.CreateOrderResponse;
import com.ctbri.domain.QueryPosOrderRequest;
import com.ctbri.domain.QueryPosOrderResponse;
import com.ctbri.domain.ValidateOrderRequest;
import com.ctbri.domain.ValidateOrderResponse;

/**
 * 订单操作
 * @author qin
 * 
 * 2012-12-8
 */
public interface OrderService {
	
	/**
	 * 订单验证
	 * @param req   请求参数
	 * @return  验证结果
	 */
	ValidateOrderResponse validate(ValidateOrderRequest req);
	
	/**
	 * 创建订单
	 * @param req
	 * @return
	 */
	CreateOrderResponse create(CreateOrderRequest req);
	
	/**
	 * 按pos 编号查询订单
	 * @param req
	 * @return
	 */
	QueryPosOrderResponse queryByPos(QueryPosOrderRequest req);
}
