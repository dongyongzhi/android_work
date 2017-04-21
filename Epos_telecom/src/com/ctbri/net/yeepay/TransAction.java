package com.ctbri.net.yeepay;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONException;
import org.json.JSONObject;

import com.ctbri.ElecException;
import com.ctbri.domain.ElecRequest;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.net.POSPSetting;
import com.ctbri.utils.Base64;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.HttpClientUtil;
import com.ctbri.utils.Rsa;
import com.ctbri.utils.TripleDes;

/**
 * 交易请求
 * @author qin
 * 
 * 2012-12-7
 */
public class TransAction {
	
	private static TransAction mInstance = null; 
	private final static String APP="MPOSP";
	private final static String SESSIONNULL = "sessionNull";
	private final static String EXCEPTION = "Exception";
	public final static String KEY_ERRCODE = "rcode";
	
	
	private String mSessionId = null;
	private String mPubKey = null;
	
	/**交易数据*/
	public final static String PAYDATA = "paydata";
	
	
	private TransAction() {}
	
	public static TransAction getInstance() {
		if (mInstance == null) {
			mInstance = new TransAction();
		}
		return mInstance;
	}
	/**
	 * posp http接口请求
	 * @param url       url地址
	 * @param paydata   交易数据
	 * @param params    其它参数
	 * @param key       密钥
	 * @return   解密过后的 json 数据 
	 */
	private MPOSPResponse doPOSPAction(String url,String paydata,Map<String,String> params,String key){
		//参数检查
		if(key==null || "".equals(key))
			throw new ElecException(POSPSetting.SYSTEM_ERROR);
		
		if(url==null || "".equals(url))
			throw new ElecException(POSPSetting.SYSTEM_ERROR);
		
		if((paydata==null  || "" .equals(paydata)) &&  params==null)
			throw new ElecException(POSPSetting.SYSTEM_ERROR);
		
		if((paydata==null || "".equals(paydata)) && params!=null){ //paydata参数从  params 取出
			paydata = params.get(PAYDATA);
		}
		
		if(params==null)
			params = new HashMap<String,String>();
		
		//存在交易参数时应当加密码参数 
		byte[] byteTmp = null;
		if(paydata!=null && !"".equals(paydata)){
			//加密 paydata 数据
			ElecLog.d(TransAction.class,"加密前数据： " + paydata);
			//进行加密
			byteTmp = TripleDes.getInstance().encryptEBC(key, paydata, TripleDes.MODE_EBC);
			if(byteTmp==null)
				throw new ElecException(POSPSetting.SYSTEM_DES3_ENCRYPTO_ERROR);
			
			String dataTmp = new String(Base64.encode(byteTmp, Base64.NO_WRAP));
			
			//组织要发送给服务器的数据
			params.put(PAYDATA, URLEncoder.encode(dataTmp));
			String rsaEncryptoData = null;
	
			if (key != null && mPubKey != null) {
				//需要对传入的 KEY 进行RSA加密
				try {
					rsaEncryptoData = Rsa.getInstance().encrypt(key, mPubKey);
				} catch (Exception e) {
					ElecLog.e(TransAction.class,e.getMessage(),e);
					throw new ElecException(POSPSetting.SYSTEM_ERROR);
				}
				params.put("key", URLEncoder.encode(rsaEncryptoData));
			}
		}
		
		//获取网络数据
		String responseData = HttpClientUtil.sendPostRequest(APP,url, params);
		if(responseData==null || "".equals(responseData))
			throw new ElecException(POSPSetting.POSP_NET_BAD);//服务器返回数据异常 为NULL 或者 空字符串
		
		if(responseData.equals(SESSIONNULL)){ //服务器session 失效，需要重新协商 获取公钥
			resetSessionId();
			return ElecResponse.getErrorResponse(MPOSPResponse.class,ResponseCode.POSP_SESSION_NULL);
		}
		
		if(responseData.equals(EXCEPTION)){  //服务器出错
			throw new ElecException(POSPSetting.POSP_RESPONSE_500);
		}
		
		//获取data=xxx
		if (responseData.indexOf("=") == -1) {
			throw new ElecException(POSPSetting.POSP_NET_BAD);
		} else {
			responseData = responseData.split("=")[1];
			if(responseData==null || "".equals(responseData))
				throw new ElecException(POSPSetting.POSP_NET_BAD);
		}
		 
		//解密数据
		byte[] responseBytes = Base64.decode(responseData.getBytes(), Base64.NO_WRAP);
		try {
			byteTmp = TripleDes.getInstance().decryptEBC(key.getBytes(), responseBytes, TripleDes.MODE_EBC);
		} catch (Exception e) {
			ElecLog.e(TransAction.class,e.getMessage(),e);
			throw new ElecException(POSPSetting.SYSTEM_DES3_DECRYPTE_ERROR);
		}
		
		if(byteTmp==null)
			throw new ElecException(POSPSetting.SYSTEM_DES3_DECRYPTE_ERROR);
		
		try {
			String decrypt = new String(byteTmp);  //解码后的数据
			ElecLog.d(getClass(), String.format("解码后数据：%s", decrypt));
			JSONObject result =  new JSONObject(decrypt);
			
			if(result.has(KEY_ERRCODE) && result.getInt(KEY_ERRCODE)!=0){
		    	return  ElecResponse.getErrorResponse(MPOSPResponse.class,result.getInt(KEY_ERRCODE), "中心:"+result.getString("errormsg"));
		    }
		    MPOSPResponse resp = new MPOSPResponse();
		    resp.setResult(result);
		    return resp;
		    	
		} catch (JSONException e) {
			ElecLog.e(TransAction.class,e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_NET_BAD);
		}
	}
	
	/**
	 * 协商获取公钥
	 */
	public boolean doConsultAction(){
		ElecLog.d(getClass(), "==========开始协商...");
		MPOSPResponse resp = (MPOSPResponse)this.doPOSPAction(POSPSetting.METHOD_CONSULT,POSPSetting.CONSULTDATA, null, POSPSetting.CONSULTKEY);
		try {
			ElecLog.d(getClass(), String.format("==========协商返回 %s  %s",resp.getErrCode(),resp.getErrMsg()));
			if(resp.getErrCode()==0){
				this.mSessionId = resp.getResult().getString("jsessionid");
				this.mPubKey = resp.getResult().getString("publicKey");
				return true;
			}
			return false;
		} catch (JSONException e) {
			ElecLog.e(TransAction.class,e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_NET_BAD);
		}
	}
	
	/**
	 *  posp 接口请求交易
	 * @param url            url 地址
	 * @param paydata       交易数据  (必须加密)
	 * @param otherParams   其它参数
	 * @param key           3des key
	 * @return              posp中心数据
	 */
	public  ElecResponse doAction(String url,String paydata,Map<String,String> otherParams,String key){
		//如果sessionId不合法，则先去获取sessionId  
		if(mSessionId==null || "".equals(mSessionId)){  
			if(!doConsultAction()){  //协商失败
				throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
			}
		}
		ElecResponse resp = doPOSPAction(url,paydata,otherParams,key);
		
		//服务器 session  失效，需要重新协商
		if(resp.getErrCode()==ResponseCode.POSP_SESSION_NULL.getValue()){
			if(!doConsultAction()){ //协商
				throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
			}
			resp = doPOSPAction(url,paydata,otherParams,key);  //再次请求posp中心
		}
		return resp;
	}
	
	/**
	 * posp 接口请求交易 
	 * @param url             url 地址
	 * @param paydata         json交易数据  (必须加密)
	 * @param otherParams     其它参数 （不必加密）
	 * @param key			  3des key
	 * @return  posp中心数据
	 */
	public  ElecResponse doAction(String url,JSONObject paydata,Map<String,String> otherParams,String key){
		return doAction(url,paydata.toString(),otherParams,key);
	}
	
	/**
	 * posp 接口请求交易
	 * @param url        url 地址
	 * @param paydata    json交易数据  (必须加密)
	 * @param key        3des key
	 * @return     posp中心数据
	 */
	public  ElecResponse doAction(String url,JSONObject paydata,String key){
		return doAction(url,paydata.toString(),null,key);
	}
	/**
	 * posp 接口请求交易
	 * @param url   url 地址
	 * @param req   交易数据 
	 * @param key   3des key
	 * @return
	 */
	public  ElecResponse doAction(String url,ElecRequest req,String key){
		return doAction(url,req.getPayData(),req.getParams(),key);
	}
	
	public  ElecResponse doAction(String url,String paydata,String key){
		return doAction(url,paydata,null,key);
	}
	
	public  ElecResponse doAction(String url,Map<String,String> params,String key){
		return this.doAction(url, "", params, key);
	}
	
	public void resetSessionId() {
		mSessionId = null;
	}
}
