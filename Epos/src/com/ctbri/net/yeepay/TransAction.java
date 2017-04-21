package com.ctbri.net.yeepay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
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
import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.utils.ByteUtils;

/**
 * 交易请求
 * @author qin
 * 
 * 2012-12-7
 */
public class TransAction {
	
	private static final YFLog logger = YFLog.getLog(TransAction.class);
	
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
		if(responseData==null || "".equals(responseData)) {
			//服务器返回数据异常 为NULL 或者 空字符串
			throw new ElecException(ResponseCode.POSP_CLIENT_FAIL.getMessage());
		}
		
		if(responseData.equals(SESSIONNULL)){ 
			resetSessionId();
			//服务器session 失效，需要重新协商 获取公钥
			return ElecResponse.getErrorResponse(MPOSPResponse.class,ResponseCode.POSP_SESSION_NULL);
		}
		
		if(responseData.equals(EXCEPTION)){  
			//服务器出错
			throw new ElecException(ResponseCode.POSP_RESPONSE_500.getMessage());
		}
		
		//获取data=xxx
		if (responseData.indexOf("=") == -1) {
			throw new ElecException(ResponseCode.POSP_CLIENT_FAIL.getMessage());
		} else {
			responseData = responseData.split("=")[1];
			if(responseData==null || "".equals(responseData))
				throw new ElecException(ResponseCode.POSP_CLIENT_FAIL.getMessage());
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
			
			//中心返回业务错误
			if(result.has(KEY_ERRCODE) && result.getInt(KEY_ERRCODE)!=0){
				throw new ElecException("中心:"+result.getString("errormsg"));
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
			if(resp.isSuccess()){
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
				throw new ElecException(ResponseCode.POSP_CLIENT_FAIL.getMessage());
			}
		}
		ElecResponse resp = doPOSPAction(url,paydata,otherParams,key);
		
		//服务器 session  失效，需要重新协商
		if(resp.getErrCode()==ResponseCode.POSP_SESSION_NULL.getCode()){
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
	

	/**
	 * 发送数据至交易平台
	 * @param data
	 * @param len
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public byte[] sendTransData(byte[] data,int offset,int len) {
		
		try {
	      //与服务端建立连接  
	      Socket client = new Socket("180.166.199.109", 30002);  
	      //建立连接后就可以往服务端写数据了  
	      OutputStream os = client.getOutputStream(); 
	      InputStream in = client.getInputStream();
	      
	      logger.d(">>>>>>> 发数据至平台");
	      logger.d(ByteUtils.printBytes(data, offset, len));
	      
	      
	      os.write(data, offset, len);
	      os.flush();
	      //读取数据
	      ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
	      //写入2个字节平台返回数据包总长度
	      out.write(new byte[2]);
	      
	      byte[] buf = new byte[1300];
	      //平台8583数据包长度
	      int dataLen = 0; 
	      while(true) {
	    	  int count = in.read(buf, 0, buf.length);
	    	  if(count<=0 ) {
	    		  break;
	    	  }
	    	  
	    	  out.write(buf, 0, count);
	    	  if(dataLen ==0 && count>=2) {
	    		  //解析前2个字节数据长度
	    		  dataLen = ByteUtils.byteToInt(buf[0], buf[1]);
	    		  if(dataLen + 2 <=out.size()-2) {
	    			  break;
	    		  }
	    	  }
	      }
	      //关闭数据
	      client.close();  
	      //总的数据长度
	      int total = dataLen + 2;
	      
	      byte[] result = out.toByteArray();
	      result[0] =(byte)(total>>8);
	      result[1] = (byte)total;		  
	      
	      logger.d("<<<<<<< 接收平台数据");
	      logger.d(ByteUtils.printBytes(result));
	      
	      return result;
	      
		}catch(Exception ex) {
			logger.e("平台通讯处理错误", ex);
			//ex.printStackTrace();
			return null;
		}
	}
}
