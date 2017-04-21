package com.yifeng.skzs.util;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
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

import android.content.SharedPreferences;
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
	public static Map<String,String> cookie;
	private static final String COOKIES="COOKIES";
	public static final String E_SERVIER="500";
	
 
	/**
	 * Get请求 参数请求路径
	 * @param ur
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
	public static JsonReturn doPost(String url, List<NameValuePair> params) {

		/* 建立HTTPPost对象 */
		//url = "http://test.chinapnr.com/mtp/login?jsonStr={%22LoginId%22:%2218611111111%22,%22LoginPwd%22:%22ee8b8fcec73c097905f9734b06a54c5f%22}";
		JsonReturn re = new JsonReturn();
		re.returnCode = 0;
		HttpPost httpRequest = new HttpPost(url);
		setPostCookie(httpRequest);
		
		System.out.println(url+"--------url\n"+httpRequest.toString());

		try {
			/* 添加请求参数到请求对象 */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			
			// 请求超时
			httpRequest.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,ConstantUtil.POST_CONNECTION_TIMEOUT);	

			/*if(TestCookies.getHeaders()!=null)
			httpRequest.setHeaders(TestCookies.getHeaders());*/
			/* 发送请求并等待响应 */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
		  /* Header[] headers=	httpResponse.getAllHeaders();
		   TestCookies.setHeadersheaders);*/
			re.returnCode = httpResponse.getStatusLine().getStatusCode();
			/* 若状态码为200 ok */
			if (re.returnCode == 200) {
				/* 读返回数据 */
				re.json = EntityUtils.toString(httpResponse.getEntity());
				getCookie(httpResponse);

			}else if(httpResponse.getStatusLine().getStatusCode() == 404){
				re.json = "404";
			}
			else {
				re.json = "服务器端异常";
			}
		//	System.out.println(">>>>>>>>>"+httpResponse.toString());
		} catch(UnknownHostException e){
			re.json = "服务器未知异常";
		} catch (Exception e) {
			re.json = "error";
		}

		Log.i("strResult", re.json);
		//System.out.println(">>>>>>>"+strResult);
		return re;
	}
	
	/**
	 * 连接远程服务器设置
	 * @return
	 */
	public static HttpClient getHttpClient() {

		// 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
		if(httpClient==null){
			//初始化
			cookie=new HashMap<String,String>();
			
		

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
		}
		return httpClient;
	}
	
    private  static void  getCookies(){
    	SharedPreferences cookies = AppContext.get().getSharedPreferences(COOKIES, 0);
    	String str=cookies.getString("COOKIES", "");
    	if(str!=null){
    		if(!str.equals("")){
	    		cookie=new HashMap<String,String>();
	    		String[] arg=str.split("\\|");
	    		for (int i = 0; i < arg.length; i++) {
	    			cookie.put("Cookie"+i, arg[i]);
				}
    		}
    	}
    }
    
    /**
     * 设置提交Post 方法cookie到服务器
     * @param httpRequest
     */
    public static void setPostCookie(HttpPost httpRequest){

    	if(cookie.size()==0){
    		getCookies();
    	}
    	if(cookie!=null){
    		for (Iterator iter = cookie.keySet().iterator(); iter.hasNext();) {
    			String key = (String) iter.next();//取键
    			String value=cookie.get(key);//获取值
    			httpRequest.setHeader("Cookie", value);
    		}
    	}
    }
    
    /**
     * 设置提交 Get 方法 cookie到服务器
     * @param httpRequest
     */
    public static void setGetCookie(HttpGet httpRequest){
    	if(cookie.size()==0){
    		getCookies();
    	}
    	if(cookie!=null){
    		for (Iterator iter = cookie.keySet().iterator(); iter.hasNext();) {
    			String key = (String) iter.next();//取键
    			String value=cookie.get(key);//获取值
    			httpRequest.setHeader("Cookie", value);
    		}
    	}
    }
    
    /**
     * 取得cookie头信息
     * @param httpResponse
     * @return
     */
    private static void getCookie(HttpResponse httpResponse){
    	String str="";
    	int ret=0;
    	
    	if(httpResponse!=null){
	    	Header[] headers = httpResponse.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				//只取cookie
				if(headers[i].getName().equals("Set-Cookie")){
					cookie.put("Cookie"+i, headers[i].getValue());
					str+=headers[i].getValue()+"\\|";
					ret++;
				}
				//System.out.println("头信息==="+headers[i].getName()+"值=====:"+headers[i].getValue());
			}
//			String cookies="";
//			for(int i = 0; i < cookie.size(); i++){
//				cookies+=cookie.get(i)+"||";
//			}
//			
//			saveCookieToDB(cookies);
		}
    	
    	if(ret>0){
    		setCookies(str);
    	}
    	
    }
    
    private  static void  setCookies(String cookie){
    	SharedPreferences rmd=AppContext.get().getSharedPreferences(COOKIES, 0);
		 rmd.edit()
		.putString("COOKIES", cookie)
		.commit();
    }

}
