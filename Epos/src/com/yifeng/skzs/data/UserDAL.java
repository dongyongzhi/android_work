package com.yifeng.skzs.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.content.Context;
import com.yifeng.skzs.entity.DoCancel;
import com.yifeng.skzs.entity.DoPay;
import com.yifeng.skzs.entity.News;
import com.yifeng.skzs.entity.QueryPayOrders;
import com.yifeng.skzs.entity.QueryRefundOrder;
import com.yifeng.skzs.entity.SignIn;
import com.yifeng.skzs.entity.User;
import com.yifeng.skzs.entity.limitAmt;
import com.yifeng.skzs.entity.QueryPayOrder;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.DataConvert;
import com.yifeng.skzs.util.HttpPostGetUtil;
import com.yifeng.skzs.util.JsonReturn;

public class UserDAL<E> extends BaseDAL
{
	private static String username,pwd;
	private static boolean advance=true;
	public UserDAL(Context context)
	{
		super(context);
	}

	/**
	 * 获取用户信息
	 * @param param
	 * @return
	 */
	public User loadUser(Map<String, String> param)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("LoginId",param.get("LoginId"))); 
		list.add(new BasicNameValuePair("LoginPwd",param.get("LoginPwd")));
		username = param.get("LoginId");
		pwd = param.get("LoginPwd");
		String s = "{\"LoginId\":\""+param.get("LoginId")+"\",\"LoginPwd\":\""+param.get("LoginPwd")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/login");
	    HttpPostGetUtil.cookie.clear();
		JsonReturn re = this.doPost(list);

		User user = new User();
		if (re.returnCode == 200){
			try
			{
				Map<String, String> map = DataConvert.toMap(re.json);
				

				if(map.get("RespCode").equals("000")){
					user.setState(ConstantUtil.LOGIN_SUCCESS);
					User.setCustId(map.get("CustId")==null?"":map.get("CustId"));
					User.setSessionId(map.get("SessionId")==null?"":map.get("SessionId"));
					User.setUserName(map.get("UserName")==null?"":map.get("UserName"));
				}else{
					user.setState(ConstantUtil.LOGIN_FAIL);
					user.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
					user.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
				}
				
				if(re.json.equals("error")){
					user.setRespDesc("RespDesc");
				}else{
					user.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
					user.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
				}
			} catch (Exception e)
			{
				user.setState(ConstantUtil.INNER_ERROR);// 解析异常
			}
		}
		else if (re.returnCode == 404){
			
			user.setState(ConstantUtil.SERVER_ERROR);
		}else{
			user.setRespDesc(re.json);
		}
		return user;
	}
	
	
	
	
	/**
	 * 查询绑定
	 * @param param
	 * @return
	 */
	public User QueryBindStat(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("MtId",param.get("MtId"))); 
		list.add(new BasicNameValuePair("CustId",param.get("CustId")));
//		list.add(new BasicNameValuePair("&SessionId",param.get("SessionId")));
		
		String s = "{\"MtId\":\""+param.get("MtId")+"\",\"CustId\":\""+param.get("CustId")+"\"}";
		
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/query/queryBindStat");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		User user = new User();
		Map<String, String> map = DataConvert.toMap(re.json);
		
		if (re.returnCode == 200){
			user.setBindStat(map.get("BindStat")==null?"":map.get("BindStat"));
			user.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			user.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		}else{
			user.setRespDesc(re.json);
		}
		return user;
	}
	
	/**
	 * 进行 绑定
	 * @param param
	 * @return
	 */
	public User Bind(Map<String, String> param)
	{
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("MtId",param.get("MtId"))); 
		list.add(new BasicNameValuePair("CustId",param.get("CustId")));
//		list.add(new BasicNameValuePair("&SessionId",param.get("SessionId")));
		
		String s = "{\"MtId\":\""+param.get("MtId")+"\",\"CustId\":\""+param.get("CustId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/bind/bind");
	    System.out.println(this.getUrl()+"-----------getUrl");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		User user = new User();
		Map<String, String> map = DataConvert.toMap(re.json);
		
		if (re.returnCode == 200){
			user.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			user.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		}else{
			user.setRespDesc(re.json);
		}
		return user;
	}
	
	/**
	 * 解除绑定
	 * @param param
	 * @return
	 */
	public User Release(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("MtId",param.get("MtId"))); 
		list.add(new BasicNameValuePair("CustId",param.get("CustId")));
//		list.add(new BasicNameValuePair("&SessionId",param.get("SessionId")));
		
		String s = "{\"MtId\":\""+param.get("MtId")+"\",\"CustId\":\""+param.get("CustId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/bind/release");

	    JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		User user = new User();
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			user.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			user.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		}else{
			user.setRespDesc(re.json);
		}
		return user;
	}
	
	/**
	 * 账户查询
	 * @param param
	 * @return
	 */
	public User QueryUserInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("CustId",param.get("CustId")));
		String s = "{\"CustId\":\""+param.get("CustId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
//		list.add(new BasicNameValuePair("&SessionId",param.get("SessionId")));
		
		
	    this.setUrl(this.getIpconfig()+"/action/query/mini/queryUserInfo");

	
		
		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		User user = new User();
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			user.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			user.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
			user.setTotalOrdCnt(map.get("TotalOrdCnt")==null?"":map.get("TotalOrdCnt"));
			user.setTotalOrdAmt(map.get("TotalOrdAmt")==null?"":map.get("TotalOrdAmt"));
			user.setCashCardNo(map.get("CashCardNo")==null?"":map.get("CashCardNo"));
			user.setBankId(map.get("BankId")==null?"":map.get("BankId"));
			user.setBindedMtId(map.get("BindedMtId")==null?"":map.get("BindedMtId"));
			user.setAvailCashAmt(map.get("AvailCashAmt")==null?"":map.get("AvailCashAmt"));
			user.setNeedLiqAmt(map.get("NeedLiqAmt")==null?"":map.get("NeedLiqAmt"));
		}else{
			user.setRespDesc(re.json);
		}
		return user;
	}
	
	

	
public List<Map<String,Object>> QueryPayOrder(Map<String, String> param) throws JSONException{
		
		List<Map<String,Object>> returnlist = new ArrayList<Map<String,Object>>();
 		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("CustId",param.get("CustId")));
//		list.add(new BasicNameValuePair("&SessionId",param.get("SessionId")));
		list.add(new BasicNameValuePair("BeginDate",param.get("BeginDate")));
		list.add(new BasicNameValuePair("EndDate",param.get("EndDate")));
		list.add(new BasicNameValuePair("TransStat",param.get("TransStat")));
		list.add(new BasicNameValuePair("PageSize",param.get("PageSize")));
		list.add(new BasicNameValuePair("PageNum",param.get("PageNum")));
		
		String s = "{\"CustId\":\""+param.get("CustId")+
				"\",\"BeginDate\":\""+param.get("BeginDate")+
				"\",\"EndDate\":\""+param.get("EndDate")+
				"\",\"TransStat\":\""+param.get("TransStat")+
				"\",\"PageNum\":\""+param.get("PageNum")+
				"\",\"PageSize\":\""+param.get("PageSize")+"\"}";
		
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/query/mini/queryPayOrders");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		QueryPayOrders quer = new QueryPayOrders();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			map = DataConvert.toMap(re.json);
			if(map.get("OrdersInfo").equals("")){
				return returnlist;
			}
			returnlist  = DataConvert.toConvertObjectList(re.json, "OrdersInfo");
		}else{
			quer.setRespDesc(re.json);
		}
		
		return returnlist;
	}
	

	/**
	 * 交易限额
	 * @param param
	 * @return
	 */
	public limitAmt limitAmtInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		//list.add(new BasicNameValuePair("MtId",param.get("MtId"))); 
		//list.add(new BasicNameValuePair("CustId",param.get("CustId")));
//		list.add(new BasicNameValuePair("&SessionId",param.get("SessionId")));
		String s = "{\"MtId\":\""+param.get("MtId")+"\",\"CustId\":\""+param.get("CustId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
		
	    this.setUrl(this.getIpconfig()+"/action/query/mini/limitAmt");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		limitAmt limi = new limitAmt();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			limi.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			limi.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		
			limi.setOneLimitAmt(map.get("OneLimitAmt")==null?"":map.get("OneLimitAmt"));
			limi.setSumLimitAmt(map.get("SumLimitAmt")==null?"":map.get("SumLimitAmt"));
		}else{
			limi.setRespDesc(re.json);
		}
		
		return limi;
	}
	
	/**
	 * 签到
	 * @param param
	 * @return
	 */
	public SignIn SignInInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		String s = "{\"MtId\":\""+param.get("MtId")+"\",\"CustId\":\""+param.get("CustId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/trade/signIn");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		SignIn sin = new SignIn();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			sin.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			sin.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		
			sin.setOrdId(map.get("OrdId")==null?"":map.get("OrdId"));
			sin.setPinKey(map.get("PinKey")==null?"":map.get("PinKey"));
			sin.setPinKvc(map.get("PinKvc")==null?"":map.get("PinKvc"));
			sin.setPackKey(map.get("PackKey")==null?"":map.get("PackKey"));
			sin.setPackKvc(map.get("PackKvc")==null?"":map.get("PackKvc"));
			sin.setMd5Key(map.get("Md5Key")==null?"":map.get("Md5Key"));
		}else{
			sin.setRespDesc(re.json);
		}
		return sin;
	}
	
	
	/**
	 * 消费
	 * @param param
	 * @return
	 */
	public DoPay DoPayInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		String s = "{\"MtId\":\""+param.get("MtId")+
				"\",\"CustId\":\""+param.get("CustId")+
				"\",\"OrdId\":\""+param.get("OrdId")+
				"\",\"OrdAmt\":\""+param.get("OrdAmt")+
				"\",\"Longitude\":\""+param.get("Longitude")+
				"\",\"Latitude\":\""+param.get("Latitude")+
				"\",\"ESignature\":\""+param.get("ESignature")+
				"\",\"ChkValue\":\""+param.get("ChkValue")+
				"\",\"InfoField\":\""+param.get("InfoField")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    
	    if (advance)  this.setUrl(this.getIpconfig()+"/action/trade//advanced/doPay");
	    else this.setUrl(this.getIpconfig()+"/action/trade/doPay"); 
	    

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		DoPay doPay = new DoPay();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			doPay.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			doPay.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		
			doPay.setOrdId(map.get("OrdId")==null?"":map.get("OrdId"));
			doPay.setOrdAmt(map.get("OrdAmt")==null?"":map.get("OrdAmt"));
			doPay.setTransStat(map.get("TransStat")==null?"":map.get("TransStat"));
			doPay.setChkValue(map.get("ChkValue")==null?"":map.get("ChkValue"));
			if (advance){
				
				doPay.setAcqInstuId(map.get("AcqInstuId")==null?"":map.get("AcqInstuId"));
				doPay.setCardBankId(map.get("CardBankId")==null?"":map.get("CardBankId"));
			doPay.setPosMerId(map.get("PosMerId")==null?"":map.get("PosMerId"));
			doPay.setPosMerName(map.get("PosMerName")==null?"":map.get("PosMerName"));
			doPay.setPnrDevId(map.get("PnrDevId")==null?"":map.get("PnrDevId"));
			doPay.setMerName(map.get("MerName")==null?"":map.get("MerName"));
			doPay.setPosBankId(map.get("PosBankId")==null?"":map.get("PosBankId"));
			doPay.setIssuingBank(map.get("IssuingBank")==null?"":map.get("IssuingBank"));
			doPay.setPosTermId(map.get("PosTermId")==null?"":map.get("PosTermId"));
			doPay.setCardNo(map.get("CardNo")==null?"":map.get("CardNo"));
			doPay.setExpireDate(map.get("ExpireDate")==null?"":map.get("ExpireDate"));
			doPay.setBatchId(map.get("BatchId")==null?"":map.get("BatchId"));
			doPay.setAuthCode(map.get("AuthCode")==null?"":map.get("AuthCode"));
			doPay.setRefNo(map.get("RefNo")==null?"":map.get("RefNo"));
			doPay.setVoucherNo(map.get("VoucherNo")==null?"":map.get("VoucherNo"));
			doPay.setCurrentDate(map.get("CurrentDate")==null?"":map.get("CurrentDate"));
			doPay.setCurrentTime(map.get("CurrentTime")==null?"":map.get("CurrentTime"));
			}
		}else{
			doPay.setRespDesc(re.json);
		}
		
		return doPay;
	}
	
	
	/**
	 * 单笔消费查询
	 * @param param
	 * @return
	 */
	public QueryPayOrder queryPayOrderInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		//list.add(new BasicNameValuePair("CustId",param.get("CustId"))); 
		//list.add(new BasicNameValuePair("OrdId",param.get("OrdId")));
		
		
		String s = "{\"CustId\":\""+param.get("CustId")+
				"\",\"OrdId\":\""+param.get("OrdId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/query/advanced/queryPayOrder");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		QueryPayOrder doPay = new QueryPayOrder();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			doPay.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			doPay.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
			doPay.setRespDesc(map.get("OrdAmt")==null?"":map.get("OrdAmt"));
			doPay.setRespDesc(map.get("PayOrdId")==null?"":map.get("PayOrdId"));
			doPay.setTransStat(map.get("TransStat")==null?"":map.get("TransStat"));
		}else{
			doPay.setRespDesc(re.json);
		}
		
		return doPay;
	}
	
	

	/**
	 * 消费撤销
	 * @param param
	 * @return
	 */
	public DoCancel DoCancelInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
		
		String s = "{\"MtId\":\""+param.get("MtId")+
				"\",\"CustId\":\""+param.get("CustId")+
				"\",\"OrdId\":\""+param.get("OrdId")+
				"\",\"OrdAmt\":\""+param.get("OrdAmt")+
				"\",\"PayOrdId\":\""+param.get("PayOrdId")+
				"\",\"ChkValue\":\""+param.get("ChkValue")+
				"\",\"InfoField\":\""+param.get("InfoField")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
		if (advance)
			this.setUrl(this.getIpconfig()+"/action/trade/advanced/doCancel");
		else
			this.setUrl(this.getIpconfig()+"/action/trade/doCancel");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		DoCancel doCancel = new DoCancel();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			doCancel.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			doCancel.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
			doCancel.setOrdId(map.get("OrdId")==null?"":map.get("OrdId"));
			doCancel.setPayOrdId(map.get("PayOrdId")==null?"":map.get("PayOrdId"));
			doCancel.setOrdAmt(map.get("OrdAmt")==null?"":map.get("OrdAmt"));
			doCancel.setTransStat(map.get("TransStat")==null?"":map.get("TransStat"));
			doCancel.setChkValue(map.get("ChkValue")==null?"":map.get("ChkValue"));
			
			if (advance){
				doCancel.setPosMerId(map.get("PosMerId")==null?"":map.get("PosMerId"));
				doCancel.setPosMerName(map.get("PosMerName")==null?"":map.get("PosMerName"));
				doCancel.setPnrDevId(map.get("PnrDevId")==null?"":map.get("PnrDevId"));
				doCancel.setMerName(map.get("MerName")==null?"":map.get("MerName"));
				doCancel.setPosBankId(map.get("PosBankId")==null?"":map.get("PosBankId"));
				doCancel.setIssuingBank(map.get("IssuingBank")==null?"":map.get("IssuingBank"));
				doCancel.setPosTermId(map.get("PosTermId")==null?"":map.get("PosTermId"));
				doCancel.setCardNo(map.get("CardNo")==null?"":map.get("CardNo"));
				doCancel.setExpireDate(map.get("ExpireDate")==null?"":map.get("ExpireDate"));
				doCancel.setBatchId(map.get("BatchId")==null?"":map.get("BatchId"));
				doCancel.setAuthCode(map.get("AuthCode")==null?"":map.get("AuthCode"));
				doCancel.setRefNo(map.get("RefNo")==null?"":map.get("RefNo"));
				doCancel.setVoucherNo(map.get("VoucherNo")==null?"":map.get("VoucherNo"));
				doCancel.setCurrentDate(map.get("CurrentDate")==null?"":map.get("CurrentDate"));
				doCancel.setCurrentTime(map.get("CurrentTime")==null?"":map.get("CurrentTime"));
			}
		}else{
			doCancel.setRespDesc(re.json);
		}
		
		return doCancel;
	}
	
	

	/**
	 * 单笔消费撤销查询
	 * @param param
	 * @return
	 */
	public QueryRefundOrder QueryRefundOrderInfo(Map<String, String> param){
		List<NameValuePair> list = new ArrayList<NameValuePair>();
//		list.add(new BasicNameValuePair("CustId",param.get("CustId"))); 
//		list.add(new BasicNameValuePair("OrdId",param.get("OrdId")));
		
		
		String s = "{\"CustId\":\""+param.get("CustId")+
				"\",\"OrdId\":\""+param.get("OrdId")+"\"}";
		list.add(new BasicNameValuePair("jsonStr", s));
		
	    this.setUrl(this.getIpconfig()+"/action/query/advanced/queryRefundOrder");

		JsonReturn re = this.doPost(list);
		if (re.returnCode == 404){
			IsFTF();
			re = this.doPost(list);
		}
		
		QueryRefundOrder doPay = new QueryRefundOrder();
		
		
		Map<String, String> map = DataConvert.toMap(re.json);
		if (re.returnCode == 200){
			doPay.setRespCode(map.get("RespCode")==null?"":map.get("RespCode"));
			doPay.setRespDesc(map.get("RespDesc")==null?"":map.get("RespDesc"));
		
			doPay.setTransStat(map.get("TransStat")==null?"":map.get("TransStat"));
		}else{
			doPay.setRespDesc(re.json);
		}
		
		return doPay;
	}
	
	/**
	 * 新闻中心、讯付通聚焦
	 * @param param
	 * @return
	 * @throws JSONException
	 */
public List<Map<String,Object>> News(Map<String, String> param) throws JSONException{
		
		List<Map<String,Object>> returnlist = new ArrayList<Map<String,Object>>();

		String json = this.doGet(param);
		News quer = new News();
		Map<String,String> map = new HashMap<String, String>();
		if(json.equals("error")){
			quer.setState("error");
		}else{
			map = DataConvert.toMap(json);
			if(map.get("newsList").equals("")){
				return returnlist;
			}
			returnlist  = DataConvert.toConvertObjectList(json, "newsList");
			
		}
		return returnlist;
	}
	
	
	/**
	 * 判断是否是404，错误，是调用下面方法
	 */
	private void IsFTF(){
		Map<String, String> ps = new HashMap<String, String>();
		ps.put("LoginPwd", username);
		ps.put("LoginId", pwd);
		loadUser(ps);
		
	}
}
