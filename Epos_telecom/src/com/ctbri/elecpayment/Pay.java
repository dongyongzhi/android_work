package com.ctbri.elecpayment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ctbri.ElecException;
import com.ctbri.R;
import com.ctbri.biz.OrderService;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSInfoResponse;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.PayRequest;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.ValidateOrderRequest;
import com.ctbri.domain.ValidateOrderResponse;
import com.ctbri.net.MessageType;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.ElecPosService.POSType;
import com.ctbri.pos.POSException;
import com.ctbri.ui.BaseActivity;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecPOSSelect;

/**
 * 插件 调用 
 * @author qin
 * 
 * 2012-12-16
 */
public class Pay extends BaseActivity implements OnClickListener{
	/**初始化对话框**/
	private Dialog builder;  
	
	/** 设备初始化*/
	private final static int INIT_POS = 1;
	private final static int INIT_POS_SUCCESS = 4; //初始化成功
	
	private final static int POS_PARAMS_INIT = 2;
	private final static int VALIDATE_ORDER = 3;
	
	/**初始化结果*/
	private final static int INIT_SUCESS = 0; //成功
	private final static int POS_FAIL = 1;//pos 未找到
	private final static int VALIDATE_FAIL = 2; //订单验证失败
	
	private TextView  mProgressStatus;
	private View mSettingPOSName,mDeviceSelect;
	
	private OrderService  orderService;
	private ValidateOrderRequest request;
	private PayRequest payRequest;
	
	/**支付信息**/
	private long money;
	private String orderCode;  //支付订单号
	private String orderSignName;
	private String connAddress,connName;
	private POSType posType;
	
	private int messageType;//转换过的交易类型
	private String tradeTypeName;
	
	//初始化信息
	private LoadingTask task;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay);
		findViewById(R.id.btnDoTrade).setOnClickListener(this);
		orderService = ServiceFactory.getInstance().getOrderService(); //订单处理
		
		//获取传过来的信息
		Intent intent = this.getIntent();
		String tradeType = intent.getStringExtra("tradeType"); //交易类型
		String tradeSerialNum = intent.getStringExtra("tradeSerialNum");//流水号
		String companyName = intent.getStringExtra("companyName"); //商户名称
		String companyType = intent.getStringExtra("companyType");//商户类型
		orderCode = intent.getStringExtra("orderCode");//支付订单号
		String orderNum = intent.getStringExtra("orderNum");//商户订单号
		
		String orderExplain = intent.getStringExtra("orderExplain");//订单说明
		String phoneIMSINum = intent.getStringExtra("phoneIMSINum");//手持设备号
		String operatorNum = intent.getStringExtra("operatorNum");//操作工号
		String payMoney = intent.getStringExtra("payMoney");//金额
		String extensionField = intent.getStringExtra("extensionField");//扩展域
		orderSignName  = intent.getStringExtra("checkDigit");//校验域
		
		//check params
		if(tradeType==null || "".equals(tradeType)){
			showError("交易类型不存在！");
			return;
		}
		
		
		if(!parseTradeType(tradeType)){
			showError("交易类型不存在！");
			return;
		}
		
		if(messageType != MessageType.QUERYBALANCE && messageType!=MessageType.CANCEL_PURCHASE){
			if(payMoney == null || payMoney.equals("")){
				this.showError("付款金额不存在！");
				return;
			}
		}
		//获取金额
		if(payMoney!=null && !"".equals(payMoney)){
			if(payMoney.indexOf(".")<0){
				money = Long.parseLong(payMoney);
			}else{
				money = ((Float)(Float.parseFloat(payMoney) * 100)).longValue();
			}
		}
		
		if("04".equals(tradeType) && (orderCode==null || "".equals(orderCode))){
			showError("订单号不存在！");
			return;
		}
		
		//初始化订单验证参数
		if(orderCode!=null && !orderCode.equals("")){
			if(orderSignName==null || "".equals(orderSignName)){
				showError("校验域不存在！");
				return;
			}
			request = new  ValidateOrderRequest();
			request.setCompanyName(companyName);
			request.setCompanyType(companyType);
			request.setOperatorNum(operatorNum);
			request.setOrderCode(orderCode);
			request.setOrderExplain(orderExplain);
			request.setOrderNum(orderNum);
			request.setPhoneIMSINum(phoneIMSINum);
			request.setSignName(orderSignName);
			request.setTradeType(tradeType);
			request.setTradeSerialNum(tradeSerialNum);
		}
		payRequest  = new PayRequest();
		payRequest.setTradeType(tradeType);
		payRequest.setTradeSerialNum(tradeSerialNum);
		payRequest.setCompanyName(companyName);
		payRequest.setCompanyType(companyType);
		payRequest.setOrderCode(orderCode);
		payRequest.setOrderNum(orderNum);
		payRequest.setOrderExplain(orderExplain);
		payRequest.setPhoneIMSINum(phoneIMSINum);
		payRequest.setOperatorNum(operatorNum);
		payRequest.setPayMoney(payMoney);
		payRequest.setExtensionField(extensionField);
		payRequest.setCheckDigit(orderSignName);
		
		//设置显示信息
		((TextView)findViewById(R.id.order_comment)).setText(orderExplain);  //订单说明
		((TextView)findViewById(R.id.merchant_name)).setText(companyName);  //商户名称
		((TextView)findViewById(R.id.should_money)).setText(String.format("%,.2f 元",money/100.00));  //应付金额
		((TextView)findViewById(R.id.order_pay)).setText(tradeTypeName);  //交易类型
		((TextView)findViewById(R.id.oper_num)).setText(operatorNum);  //操作工号
		((TextView)findViewById(R.id.mTradeSerialNum)).setText(tradeSerialNum);  //流水号
		((TextView)findViewById(R.id.orderCode)).setText(orderCode);  //订单号
		((TextView)findViewById(R.id.mCompanyType)).setText(companyType);  //商户类型
	}
	
	//服务绑定完成
	protected void serviceBindComplete(){
		//初始化对话框
		builder = new Dialog(this, R.style.dialog);
		builder.setCancelable(false);
		builder.show();
		builder.setContentView(R.layout.interface_loading);
		 
		mProgressStatus = (TextView)builder.findViewById(R.id.mProgressStatus);

		mSettingPOSName = builder.findViewById(R.id.mSettingPOSName);
		mDeviceSelect = builder.findViewById(R.id.mDeviceSelect);
		mDeviceSelect.setOnClickListener(this);
		
		SharedPreferences settings  = this.getSharedPreferences(STORE_POS_KEY,Context.MODE_PRIVATE);
		String type = settings.getString(STORE_POSTYPE_KEY, "");
		//获取上次连接的名称
		connName = settings.getString(STORE_POS_NAME_KEY, "");
		
		//初化pos机
		if("".equals(connName)){
			selectPOS();
			return;
		}
		if("".equals(type)){
			selectPOS();
			return;
		}
		
		posType = POSType.valueOf(type);  //获取 pos 类型
		task = new LoadingTask();
		task.execute(posType,null,connName);
	}
	
	/**
	 * 选择POS机
	 */
	private void selectPOS(){
		ElecPOSSelect posSelectDialog = new ElecPOSSelect(this,new ElecPOSSelect.OnSelectedListener(){
			@Override
			public void onSelected(String mac,String name,POSType posType) {
				if(task==null || task.isCancelled())
					task = new LoadingTask();
				Pay.this.connAddress = mac;
				Pay.this.connName = name;
				Pay.this.posType = posType;
				
				task = new LoadingTask();
				task.execute(posType,mac,name);
			}
		});
		
		//取消继续连接
		posSelectDialog.setOnCancelListener(new ElecPOSSelect.OnCancelListener(){
			@Override
			public void onCancel() {
				if(Pay.this.getPOS()!=null)
					Pay.this.getPOS().release();
				
				task = new LoadingTask();
				task.execute(posType,null,connAddress);
			}
		});
		
		posSelectDialog.show();
	}
	
	/**
	 * 初始化任务
	 * @author qin
	 * 2012-12-26
	 */
	class LoadingTask extends AsyncTask<Object,Object,Object>{
		
		protected void onPreExecute(){
			mSettingPOSName.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Object doInBackground(Object... params) {
			//获取以前绑定成功的设备类型  (系统默认 pos 设备类型)
			POSType posType = (POSType)params[0];
			String mac = (String)params[1];     //mac地址
			String posName = (String)params[2];  //获取选择的pos 机名称(蓝牙名称)
			
			publishProgress(INIT_POS,posName);
			
			try {
				
				//设备初始化
				ResponseCode result =  ResponseCode.convert(Pay.this.initDevice(posType,mac, posName));
			 
				//初始化成功
				if(result==ResponseCode.INIT_SUCCESS){ 
					if(this.isCancelled()){  //如果己退出，重新选择POS机
						ElecLog.d(getClass(), "====>pos connection  cancelled");
						Pay.this.getPOS().release();
						return null;
					}
					publishProgress(INIT_POS_SUCCESS);//初始化成功
					
					publishProgress(POS_PARAMS_INIT); //获取pos机参数
					ElecPosService pos = Pay.this.getPOS();
					POSInfo posInfo = pos.getPosInfo(); //获取设备参数
					
				    if(posInfo!=null) {
				    	posInfo.setPosName(posName);//设置设备名称
				    	POSInfoResponse posInfoResponse = ServiceFactory.getInstance()
									.getManagerService().queryPosInfo(posInfo.getPosNumber());
				    	
				    	if(posInfoResponse==null){
				    		throw new ElecException("中心：终端未注册！"); 
				    	}
				    	//posInfo.setCustomerNumber(posInfoResponse.getCustomerNumber());
				    	//posInfo.setCustomerName(posInfoResponse.getCustomerName());
				    	posInfo.setSerialNumber(posInfoResponse.getSerialNumber());
				    }
					publishProgress(posInfo);
				}else
					throw new POSException(result,Pay.this);
			
				//验证订单
				if(request!=null){
					publishProgress(VALIDATE_ORDER);
					ValidateOrderResponse resposne = orderService.validate(request);
					if(!resposne.isValidate())  //订单验证失败
						return VALIDATE_FAIL;
				} 
				
			}catch(Exception e){
				e.printStackTrace();
				return e;
			}
			return INIT_SUCESS;
		}
		
		/**
		 * 处理进度
		 */
		protected void onProgressUpdate(Object... objs){
			Object p = objs[0];
			if(p instanceof Number){  //初始化pos
				
				int state = (Integer)p;
				if(state ==INIT_POS)
					mProgressStatus.setText(String.format("正在连接：%s", (String)objs[1]));
				else if(state == POS_PARAMS_INIT)
					mProgressStatus.setText("正在获取POS机参数...");
				else if(state == VALIDATE_ORDER)
					mProgressStatus.setText("正在验证订单...");
				
				else if(state == INIT_POS_SUCCESS){
					mSettingPOSName.setVisibility(View.GONE);
					mDeviceSelect.setOnClickListener(null);
				}
				
			}else if(p instanceof POSInfo){  //pos参数获取完成
				POSInfo posInfo = (POSInfo)p;
				Pay.this.setPOSInfo(posInfo);
			}
		}
		
		/**
		 * 处理完成
		 */
		protected void onPostExecute(Object result){
			builder.dismiss();
			if(result instanceof Exception){ //出现异常
				if(result instanceof POSException){  //pos异常
					Pay.this.showError(((POSException)result).getSpannableString());
				}else{
					Exception e =(Exception)result;
					Pay.this.showError(e.getMessage());
				}
				
			}else if(result instanceof Number){ 
				int state = (Integer)result;
				if(state == POS_FAIL){
					Pay.this.showError("POS终端未找到！");
				}else if(state == VALIDATE_FAIL){
					Pay.this.showError("订单验证失败！");
				}
			}
		}
	}
	
	public void showError(String message){
		MessageBox.showError(this, message, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				//返回内容
				TransActionFactory.getInstance().payResult(Pay.this, null, payRequest);
				finish();
			}
		});
	}

	public void showError(SpannableString message){
		MessageBox.showError(this, message, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				//返回内容
				TransActionFactory.getInstance().payResult(Pay.this, null, payRequest);
				finish();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnDoTrade){  //付款
			POSTransRequest request = new POSTransRequest();
			request.setMessageType(this.messageType);
			request.setMoney(money);
			request.setOrderCode(orderCode);
			request.setSignName(orderSignName);
			request.setPayRequest(payRequest);
			
			TransActionFactory.getInstance().startAction(this, request); 
			
		}else if(v.getId() == R.id.mDeviceSelect){ //连接中选择 设备
			
			if(task!=null && !task.isCancelled()){
				task.cancel(true);//取消任务
			}
			selectPOS();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			TransActionFactory.getInstance().payResult(Pay.this, null, payRequest);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean parseTradeType(String tradeType){
		if(tradeType.equals("01")){
			this.tradeTypeName =  "收款";
			this.messageType = MessageType.PURCHASE;
			return true;
			
		}else if(tradeType.equals("02")){
			tradeTypeName =  "撤销";
			messageType = MessageType.CANCEL_PURCHASE;
			return true;
			
		}else if(tradeType.equals("03")){
			tradeTypeName =  "查询余额";
			messageType = MessageType.QUERYBALANCE;
			return true;
			
		}else if(tradeType.equals("04")){
			tradeTypeName =  "订单支付";
			this.messageType = MessageType.PURCHASE;
			return true;
		}
		return false;
		
	}
}
