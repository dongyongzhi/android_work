package com.ctbri.biz;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;

import com.ctbri.ElecActivity;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.PayRequest;
import com.ctbri.domain.TransResponse;
import com.ctbri.elecpayment.Pay;
import com.ctbri.net.MessageType;
import com.ctbri.ui.MainMenuActivity;
import com.ctbri.ui.collection.SwipingCardActivity;
import com.ctbri.ui.collection.TradeSuccessActivity;
import com.ctbri.ui.epos.QueryBalanceAcivity;
import com.ctbri.utils.ElecLog;

public class TransActionFactory {

	/**外部apk调用 返回 接口*/
	private final static String INTENT_ELECPAYMENT_PAYRESULT = "com.ctbri.elecpayment.payresult";
	
	private static TransActionFactory  instance = null;
	private Activity  prevAcivity = null; //前一个activity;
	
	private TransActionFactory(){}
	
	public static TransActionFactory getInstance(){
		if(instance == null)
			instance = new TransActionFactory();
		return instance;
	}
	
	/**
	 * 开始执行
	 * @param activity  当前 activity
	 * @param request   请求交易的内容
	 */
	public void startAction(Activity activity,POSTransRequest request){
		this.prevAcivity = activity;
		
		//转到刷卡界面
		Intent intent = new Intent(activity,SwipingCardActivity.class);
		intent.putExtra(ElecActivity.EXTRA_TRANS_REQUEST, request);
		activity.startActivity(intent);
		
		//其它apk调用 则直接finish
		if(activity instanceof Pay){
			activity.finish();
		}
	}
	
	/**
	 * 执行成功
	 * @param activity 当前 activity
	 * @param response  交易返回
	 */
	public void actionSuccess(Activity activity,TransResponse response){
		
		POSTransRequest req = this.getPOSTransRequest(activity);
		//外调用 apk
		if(this.prevAcivity!=null && this.prevAcivity instanceof Pay){
			if(response.getMessageType() == MessageType.QUERYBALANCE){  //查询余额，直接直接返回给调用者
				this.payResult(activity, response, req.getPayRequest());
				return; 
			}
		}
		
		//本程序转向
		Intent mIntent;
		if(response.getMessageType() == MessageType.QUERYBALANCE){  //查询余额
			mIntent = new Intent(activity,QueryBalanceAcivity.class);
		}else
			mIntent = new Intent(activity,TradeSuccessActivity.class);
		
		mIntent.putExtra(ElecActivity.EXTRA_TRANS_RESPONSE, (Parcelable)response);
		mIntent.putExtra(ElecActivity.EXTRA_APK_PAY_REQUEST, req.getPayRequest()); //外部apk调用数据
		
		activity.startActivity(mIntent);
		activity.finish(); //本界面结果
		//结束上一个界面
		if(this.prevAcivity!=null){
			if(!(prevAcivity instanceof MainMenuActivity)) {
				//设置返回值 
                Intent intent = new Intent();
                intent.putExtra(ElecActivity.EXTRA_TRANS_RESULT,response.getMessageType());
                prevAcivity.setResult(Activity.RESULT_OK,intent);
				prevAcivity.finish();
			}
			prevAcivity = null;  //help gc
		}
	}
	
	/**
	 * 执行失败
	 * @param activity 当前 activity
	 */
	public void actionFail(Activity activity){
	    if(prevAcivity!=null && prevAcivity == activity)
	    	return;
	    
	     //外调用 apk
		if(this.prevAcivity instanceof Pay){
			POSTransRequest req = this.getPOSTransRequest(activity);
			this.payResult(activity, null, req.getPayRequest());  //直接返回给调用者
		}
		activity.finish();
	}
	
	/**
	 * 获取 请求pos交易信息
	 * @param activity  当前 activity
	 * @return
	 */
	public POSTransRequest getPOSTransRequest(Activity activity){
		Intent intent = activity.getIntent();
		if(intent==null)
			return null;
		return intent.getParcelableExtra(ElecActivity.EXTRA_TRANS_REQUEST);
	}
	
	/**
	 * 获取 交易请求结果
	 * @param activity
	 * @return
	 */
	public TransResponse getTransResult(Activity activity){
		Intent intent = activity.getIntent();
		if(intent==null)
			return null;
		return intent.getParcelableExtra(ElecActivity.EXTRA_TRANS_RESPONSE);
	}
	
	/**
	 * 外调用 apk 返回
	 * @param activity
	 */
	public void payResult(Activity activity,TransResponse response,PayRequest request){
		POSInfo posInfo = ((ElecActivity)activity).getPOSInfo();
		
		Intent intent = new Intent(INTENT_ELECPAYMENT_PAYRESULT);
		intent.putExtra("tradeType", request.getTradeType());
		intent.putExtra("tradeSerialNum", request.getTradeSerialNum());
		intent.putExtra("companyName", request.getCompanyName());
		intent.putExtra("companyType", request.getCompanyType());
		intent.putExtra("orderNum", request.getOrderNum());
		intent.putExtra("orderExplain", request.getOrderExplain());
		intent.putExtra("phoneIMSINum", request.getPhoneIMSINum());
		intent.putExtra("operatorNum", request.getOrderNum());
		intent.putExtra("extensionField", request.getExtensionField());
		intent.putExtra("checkDigit", request.getCheckDigit());
		//交易结果
		if(response==null){
			intent.putExtra("payResult",false);
			intent.putExtra("payMoney",request.getPayMoney());
		}else{
			ElecLog.d(getClass(), "result apk:"+response.getErrCode());
			
			intent.putExtra("payResult", response.getErrCode()== 0 ? true : false);
			intent.putExtra("systemReferNum", response.getTransNumber()); 
			intent.putExtra("payMoney", String.valueOf(response.getMoney()));
		}
		if(posInfo!=null)
			intent.putExtra("tradeTerminalNum", posInfo.getPosNumber());
		try{ 
			((ElecActivity)activity).baseStartActivity(intent);
		}catch(Exception e){
			
		}
		activity.finish();
	}
}
