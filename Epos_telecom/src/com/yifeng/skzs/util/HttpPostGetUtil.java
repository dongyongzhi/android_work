package com.yifeng.skzs.util;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;


/**
 * Http请求
 * @author 吴家宏
 * 2011年03月19日
 *
 */
public class HttpPostGetUtil {
	private static HttpClient httpClient;
	private static HttpParams httpParams;

	/**
	 * Get请求 参数请求路径
	 * @param url
	 * @param params
	 * @return
	 */
	 public static String doGet(String url, Map params) {

	        /* 建立HTTPGet对象 */

	        String paramStr = "";
	        if(params != null) {
		        Iterator iter = params.entrySet().iterator();
		        while (iter.hasNext()) {
		            Map.Entry entry = (Map.Entry) iter.next();
		            Object key = entry.getKey();
		            Object val = entry.getValue();
		            paramStr += paramStr = "&" + key + "=" + val;
		        }
	
		        if (!paramStr.equals("")) {
		            paramStr = paramStr.replaceFirst("&", "?");
		            url += paramStr;
		        }
	        }
	        HttpGet httpRequest = new HttpGet(url);
	      
	        String strResult = "error";

	        try {

	        	//请求超时
	            //httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 60000);
	            //读取超时
	            //httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
	            
	            //HttpClient
	            //HttpClient httpClient=new HttpClient(); 
	            //链接超时
	            //httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(60000); 
	            //读取超时
	            //httpClient.getHttpConnectionManager().getParams().setSoTimeout(60000
	            
	        	/* 发送请求并等待响应 */
	        	//doTybaowen, doCxbaowen doZybaowen
	        	//if(url.indexOf("doTybaowen"))
	        	//httpClient超时默认为10秒,现统一改为30秒
	        	//Object aa = httpClient.getParams().getParameter(CoreConnectionPNames.CONNECTION_TIMEOUT);
	        	httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
	            HttpResponse httpResponse = httpClient.execute(httpRequest);
	            /* 若状态码为200 ok */
	            if (httpResponse.getStatusLine().getStatusCode() == 200)  {
	                /* 读返回数据 */
	                strResult = EntityUtils.toString(httpResponse.getEntity());

	            } else {
	                strResult = "error";
	            }
	        } catch (ClientProtocolException e) {
	        	strResult = "error";
	        } catch (IOException e) {
	        	strResult = "error";
	        } catch (Exception e) {
	        	strResult = "error";
	        }

	        Log.v("strResult", strResult);

	        return strResult;
	    }



	/**
	 * POST请求服务器
	 * 
	 * @param list提交参数
	 * @param strTo
	 * @param strTarget
	 * @return
	 */
	public static String doPost(String url, List<NameValuePair> params) {

		/* 建立HTTPPost对象 */
		HttpPost httpRequest = new HttpPost(url);
		String strResult = "error";
		try {
			/* 添加请求参数到请求对象 */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			/* 若状态码为200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* 读返回数据 */
				strResult = EntityUtils.toString(httpResponse.getEntity());

			} else {
				strResult = "error";
			}
		} catch (ClientProtocolException e) {
			strResult = "error";
		} catch (IOException e) {
			strResult = "error";
		} catch (Exception e) {
			strResult = "error";
		}

		Log.v("strResult", strResult);

		return strResult;
	}
	
	/**
	 * 连接远程服务器设置
	 * @return
	 */
	public static HttpClient getHttpClient() {

		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）

		httpParams = new BasicHttpParams();

		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小

		HttpConnectionParams.setConnectionTimeout(httpParams, 10 * 1000);

		HttpConnectionParams.setSoTimeout(httpParams, 10 * 3000);

		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);

		// 设置重定向，缺省为 true

		HttpClientParams.setRedirecting(httpParams, true);
		
		// 检测代理设置
		/**
		String proxyHost = Proxy.getHost(context);   
		int proxyPort = Proxy.getPort(context); 
		
		if (!TextUtils.isEmpty(proxyHost)) {   
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);   
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}   
		 */
		//HttpHost proxy = new HttpHost("132.234.6.85", 809);   
		//httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

		// 设置 user agent

		String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
		HttpProtocolParams.setUserAgent(httpParams, userAgent);

		// 创建一个 HttpClient 实例

		// 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient

		// 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient

		httpClient = new DefaultHttpClient(httpParams);

		return httpClient;
	}

}
