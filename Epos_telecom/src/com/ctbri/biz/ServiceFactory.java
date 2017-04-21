package com.ctbri.biz;

import android.content.Context;

import com.ctbri.net.yeepay.MPOSPManagerService;
import com.ctbri.net.yeepay.MPOSPOrderService;
import com.ctbri.net.yeepay.MPOSPTransService;
import com.ctbri.pos.ElecPosService;

public class ServiceFactory {

	private OrderService  orderService =null;
	private ManagerService managerService= null;
	private static ServiceFactory  instance =null;
	private int pospType =0;//posp 平台类型 （易宝-mposp，电信）等
	
	private ServiceFactory(){
		
	};
	
	public static ServiceFactory getInstance(){
		if(instance==null)
			instance = new ServiceFactory();
		return instance;
	}
	
	public  OrderService getOrderService(){
		if(orderService==null)
			orderService = new MPOSPOrderService();
		return orderService;
	}
	
	public  ManagerService getManagerService(){
		if(managerService==null)
			managerService = new MPOSPManagerService();
		return managerService;
	}
	
	public  TransService getTransService(ElecPosService pos,Context context){
		return new MPOSPTransService(pos,context);
	}
	
	public void init(int pospType){
		this.pospType = pospType;
	}
	public int getPOSPType(){
		return this.pospType;
	}
}
