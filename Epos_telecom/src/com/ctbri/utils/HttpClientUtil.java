package com.ctbri.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.ctbri.Constants;
import com.ctbri.ElecException;
import com.ctbri.net.POSPSetting;

public class HttpClientUtil {
	private final static String TAG = HttpClientUtil.class.getName();
	
	private final static String CLIENT_AGREEMENT = "TLS";//使用协议  
	
    /**连接超时*/
	private final static int TIMEOUT = 5000; 
	/**读取数据超时 */
	private final static int READTIMEOUT = 10000;
	
	private final static String BOUNDARY  = "----WebKitFormBoundary6R5wDpBEYhfORwl4";  //定义数据分隔线
	private final static String SORT_BOUNDARY = "--";
	
	private final static String BR = "\r\n"; //换行
	
	private final static Map<String,String> sessionIds = new HashMap<String,String>(); //保存session ID
	
	/**
	 *  Create an class to trust all hosts
	 */
	private static HostnameVerifier hnv = new HostnameVerifier() {
		 public boolean verify(String hostname, SSLSession session) {
	            return true;
	     }
	};
	
	
	public static JSONObject sendSSLPostRequest(String postUrl,Map<String,Object> params) throws ElecException {
		OutputStream os = null;
		InputStream is = null;
		
		try {
			// TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance(CLIENT_AGREEMENT);//("SSL", "SunJSSE");

			// 使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { new POSPX509TrustManager() }, new java.security.SecureRandom());
			 
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hnv);
			URL url = new URL(postUrl);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();//打开连接
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(READTIMEOUT);
			os = conn.getOutputStream();
			is = conn.getInputStream();
			//发送数据
			String post = getJsonParams(params);
			if(Constants.DEBUG) Log.d(TAG, String.format("Post: %s to posp:%s ", post,postUrl));
			os.write(post.getBytes(Constants.CHARSET));
			os.flush();
			
			//读取数据
			StringBuilder sb = new StringBuilder();
			int code = conn.getResponseCode();
			if(code>=200 && code<300){  //返回成功
				BufferedReader rd = new BufferedReader(new InputStreamReader(is,Constants.CHARSET));
				String line = null;
				while ((line = rd.readLine()) != null) { 
			        sb.append(line);
			   } 
			}
			if(Constants.DEBUG) Log.d(TAG, String.format("recv: %s from posp:%s ", sb.toString(),postUrl));
			return new JSONObject( sb.toString()); //返回结果
		
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_RESPONSE_500);  //posp中心返回的数据有问题
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		/*} catch (NoSuchProviderException e) {
			Log.e(TAG, e.getMessage(),e);
			throw new ElecException(ResponseCode.POSP_CLIENT_FAIL);*/
		} catch (KeyManagementException e) {
			Log.e(TAG, e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(),e);
			throw new ElecException(POSPSetting.POSP_CONNECTION_FAIL); //连接失败，己断网、网络无效、posp服务器连接不上？
		} finally {
			if(os!=null)
				try {
					os.close();
				} catch (IOException e1) {
					Log.w(TAG, e1);
				}
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					Log.w(TAG, e);
				}
		}
	}
	
	/**
	 * http 请求 
	 * @param app      站点标识，主要保存sessionID
	 * @param postUrl  url地址
	 * @param params   参数内容
	 * @return  内容
	 * @throws ElecException
	 */
	public static String sendPostRequest(String app,String postUrl,Map<String,String> params){
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();//打开连接
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			//conn.setConnectTimeout(TIMEOUT);
			//conn.setReadTimeout(READTIMEOUT);
			conn.setUseCaches(false);
			conn.setRequestProperty("connection", "Keep-Alive");  
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			if(sessionIds.get(app)!=null)
				conn.setRequestProperty("Cookie", sessionIds.get(app)); //session管理 ，设备服务端session 
			conn.setInstanceFollowRedirects(true);
			os = conn.getOutputStream();
			
			String post = getParams(params); //获取
			ElecLog.d(HttpClientUtil.class, String.format("Post: %s to posp:%s ", post,postUrl));
			
			if(post!=null && !"".equals(post)){
				os.write(post.getBytes()); //发送数据
			}
			os.flush();
			os.close();
			is = conn.getInputStream();
			//获取sessionID
			String cookieValue = conn.getHeaderField("Set-Cookie");
			ElecLog.d(HttpClientUtil.class, String.format("==== cookie value:%s", cookieValue));
		
			//读取数据
			StringBuilder sb = new StringBuilder();
			int code = conn.getResponseCode();
			if(code>=200 && code<300){  //返回成功
				BufferedReader rd = new BufferedReader(new InputStreamReader(is,Constants.CHARSET));
				String line = null;
				while ((line = rd.readLine()) != null) { 
			        sb.append(line);
			    } 
			    rd.close();
			}
			is.close();
			if(cookieValue!=null){
				String sessionId=cookieValue.substring(0, cookieValue.indexOf(";"));
				sessionIds.put(app, sessionId);
			}
			
			conn.disconnect();
			ElecLog.d(HttpClientUtil.class, String.format("recv: %s from posp:%s ", sb.toString(),postUrl));
			return sb.toString();
			
		} catch (MalformedURLException e) {
			ElecLog.e(HttpClientUtil.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		} catch (ProtocolException e) {
			ElecLog.e(HttpClientUtil.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		} catch (IOException e) {
			ElecLog.e(HttpClientUtil.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CONNECTION_FAIL); //连接失败，己断网、网络无效、posp服务器连接不上？
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
				}
			if(os!=null)
				try {
					os.close();
				} catch (IOException e) {
				}
		}
	}
	
	/**
	 * http 请求  带图片上传
	 * 
	 * @param postUrl  url地址
	 * @param params   参数内容
	 * @param formName 图片表单名称
	 * @param fileName 图片文件名
	 * @param jpg      要上传的图片(jpg格式)
	 * 
	 * @return  内容
	 * @throws ElecException
	 */
	public static String sendPostRequest(String postUrl,Map<String,String> params,String formName,String fileName,byte[] jpg){
		OutputStream os = null;
		InputStream is = null;
		try {
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();//打开连接
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(READTIMEOUT);
			conn.setUseCaches(false);
			conn.setRequestProperty("connection", "Keep-Alive");  
	        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)"); 
	        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);  
	        
			os = conn.getOutputStream();
			
			//要上传的参数域
			if(params!=null && params.size()>0){
				StringBuilder txtTag = new StringBuilder();   
				for(Iterator<String> it = params.keySet().iterator(); it.hasNext();){
					String key = it.next();
					String value = params.get(key);
					txtTag.append(SORT_BOUNDARY).append(BOUNDARY).append(BR);
					txtTag.append("Content-Disposition: form-data; name=\""+key+"\"").append(BR).append(BR); //二行
					txtTag.append(value).append(BR);
				}
				os.write(txtTag.toString().getBytes());//写入参数
			}
			//文件域
			if(jpg!=null && jpg.length>0){
				StringBuilder fileTag = new StringBuilder();  
				fileTag.append(SORT_BOUNDARY).append(BOUNDARY).append(BR);
				fileTag.append("Content-Disposition: form-data; name=\""+formName+"\"; filename=\""+fileName+"\"").append(BR);
				fileTag.append("Content-Type: image/pjpeg").append(BR).append(BR);
				os.write(fileTag.toString().getBytes());
				os.write(jpg);//写入文件
				os.write(BR.getBytes());//换行
			}
			
			//结束域
			os.write((SORT_BOUNDARY + BOUNDARY+SORT_BOUNDARY+BR).getBytes());
			 
			os.flush();
			os.close();
			
			is = conn.getInputStream();
			//读取数据
			StringBuilder sb = new StringBuilder();
			int code = conn.getResponseCode();
			if(code>=200 && code<300){  //返回成功
				BufferedReader rd = new BufferedReader(new InputStreamReader(is,Constants.CHARSET));
				String line = null;
				while ((line = rd.readLine()) != null) { 
			        sb.append(line);
			   } 
			}
			is.close();
			ElecLog.d(HttpClientUtil.class, String.format("recv: %s from posp:%s ", sb.toString(),postUrl));
			return sb.toString();
			
		} catch (MalformedURLException e) {
			ElecLog.e(HttpClientUtil.class, e.getMessage()+postUrl);
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		} catch (ProtocolException e) {
			ElecLog.e(HttpClientUtil.class, e.getMessage()+postUrl);
			throw new ElecException(POSPSetting.POSP_CLIENT_FAIL);
		} catch (IOException e) {
			ElecLog.e(HttpClientUtil.class, e.getMessage());
			throw new ElecException(POSPSetting.POSP_CONNECTION_FAIL); //连接失败，己断网、网络无效、posp服务器连接不上？
			
		}finally{
			if(is!=null)
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if(os!=null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * 获取 post json 参数
	 * @param params
	 * @return
	 */
	private static String getJsonParams(Map<String,Object> params){
		if(params!=null){
			JSONObject json = new JSONObject();
			String key;
			Object value;
			try {
				for(Iterator<String> it = params.keySet().iterator(); it.hasNext();){
					key = it.next();
					value = params.get(key);
					if(value!=null)
						json.put(key, value);
				}
				return json.toString();
			} catch (JSONException e) {
				Log.w(TAG, e);
			}
		}
		return null;
	}
	
	/**
	 * 获取 post 参数
	 * @param params  参数集合
	 * @return
	 */
	private static String getParams(Map<String,String> params){
		if(params!=null){
			StringBuilder sb = new StringBuilder();
			String key;
			String value;
			 
			for(Iterator<String> it = params.keySet().iterator(); it.hasNext();){
				key = it.next();
				value = params.get(key);
				if(value!=null && !"".equals(value))
					sb.append(key).append("=").append(value).append("&");
			}
			if(sb.length()>0)
				sb.delete(sb.length()-1, sb.length());
			
			return sb.toString();
		}
		return "";
	}
	
	/**
	 * 证书信认管理 ，默认所有
	 * @author qin
	 * 
	 * 2012-11-16
	 */
	static class POSPX509TrustManager implements X509TrustManager {
		/*
		 * Delegate to the default trust manager.
		 */
		@Override
		public void checkClientTrusted(X509Certificate[] chain,String authType) throws CertificateException {
		}
		/*
		 * Delegate to the default trust manager.
		 */
		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		/*
		 * Merely pass this through.
		 */
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}
}
