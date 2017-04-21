package com.yifeng.skzs.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpConManager {
	private static HttpConManager intance = null;

	public static HttpConManager GetIntance() {
		if (intance == null)
			intance = new HttpConManager();
		return intance;
	}

	/**
	 * http数据通道
	 * 
	 * @param httpurl
	 * @return
	 */
	private HttpURLConnection getHttpconnect(String httpurl) {
		HttpURLConnection con = null;
		try {
			URL url = new URL(httpurl);
			con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);// 使用 URL 连接进行输出
			con.setDoInput(true);// 使用 URL 连接进行输入
			con.setUseCaches(false);// 忽略缓存
			con.setRequestMethod("POST");// 设置URL请求方法
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-type", "application/json");
			con.connect();
			con.setConnectTimeout(1);
			con.setReadTimeout(1);
		} catch (IOException e1) {
			// LogUtil.log("HttpConManager getJsonData IOE", 4,
			// e1.getMessage());
		} catch (Exception e2) {
			// LogUtil.log("HttpConManager getJsonData E", 4, e2.getMessage());
		}
		return con;
	}

	/**
	 * 客户端从服务端接收json格式数据
	 * 
	 * @param httpurl
	 * @return
	 */
	public JSONObject getJsonData(String httpurl) {
		JSONObject jsonData = null;
		InputStream input = null;
		ByteArrayOutputStream output = null;
		try {
			HttpURLConnection con = getHttpconnect(httpurl);
			int rescode = con.getResponseCode();
			if (HttpURLConnection.HTTP_OK == rescode) {
				input = con.getInputStream();
				int inputLength = input.available();
				byte[] buffer = new byte[inputLength];
				int offset = 0;
				int length = 0;
				output = new ByteArrayOutputStream();
				while ((offset = input.read(buffer, 0, inputLength)) != -1)
					length += offset;
				output.write(buffer, 0, length);
				String str = new String(output.toByteArray());
				jsonData = new JSONObject(str);
				buffer = null;
				con.disconnect();
			}
		} catch (IOException e1) {
			// LogUtil.log("HttpConManager getJsonData IOE", 4,
			// e1.getMessage());
		} catch (JSONException e2) {
			// LogUtil.log("HttpConManager getJsonData JSONE", 4,
			// e2.getMessage());
		} catch (Exception e3) {
			// LogUtil.log("HttpConManager getJsonData E", 4, e3.getMessage());
		} finally {
			if(input!=null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(output!=null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonData;
	}

	/**
	 * 客户端从服务端接收String类型数据
	 * 
	 * @param httpurl
	 * @return
	 */
	public String getStringData(String httpurl) {
		String value = "";
		InputStream input  = null;
		ByteArrayOutputStream output = null;
		HttpURLConnection con = null;
		try {
			con = getHttpconnect(httpurl);
			int rescode = con.getResponseCode();
			if (HttpURLConnection.HTTP_OK == rescode) {
				input = con.getInputStream();
				int inputLength = input.available();
				byte[] buffer = new byte[inputLength];
				int offset = 0;
				int length = 0;
				output = new ByteArrayOutputStream();
				while ((offset = input.read(buffer, 0, inputLength)) != -1)
					length += offset;
				output.write(buffer, 0, length);
				value = new String(output.toByteArray());
				output.close();
				buffer = null;
			}
		} catch (IOException e1) {
			// LogUtil.log("HttpConManager getStringData IOE", 4,
			// e1.getMessage());
		} catch (Exception e2) {
			// LogUtil.log("HttpConManager getStringData E", 4,
			// e2.getMessage());
		} finally {
			if (input!=null ) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (output!=null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(con!=null ) {
				con.disconnect();
			}
		}
		return value;
	}

	/**
	 * 客户端发送数据给服务端
	 * 
	 * @param httpurl
	 * @param strkey
	 * @param strValue
	 *            StringBuffer类型
	 */
	public void sendPostData(String httpurl, String strkey,
			StringBuffer strValue) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(httpurl);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(strkey, strValue
					.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户端发送数据给服务端
	 * 
	 * @param httpurl
	 * @param strkey
	 * @param strValue
	 *  字符串
	 */
	public void sendPostData(String httpurl, String strkey, String strValue) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(httpurl);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(strkey, strValue));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpClient.execute(httpPost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * post通信方式并且返回JSON格式的数据
	 * 
	 * @param httpUrl
	 *            访问的URL地址
	 * @param strKey
	 *            相当于&key=中的key
	 * @param strValue
	 *            StringBuffer类型的参数
	 * @return
	 */
	public JSONObject sendPostDataAndResult(String httpUrl, String strKey,
			StringBuffer strValue) {
		JSONObject jsonObj = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(httpUrl);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(strKey, strValue
					.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String str = EntityUtils.toString(httpEntity);
			try {
				jsonObj = new JSONObject(str);
			} catch (JSONException e) {
				// LogUtil.log("sendPostDataAndResult", 4, e.getMessage());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	/***
	 * post通信方式并且返回JSON格式的数据
	 * 
	 * @param httpUrl
	 *            访问的URL地址
	 * @param strKey
	 *            相当于&key=中的key
	 * @param strValue
	 *            String类型的参数
	 * @return
	 */
	public JSONObject sendPostDataAndResult(String httpUrl, String strKey,
			String strValue) {
		JSONObject jsonObj = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(httpUrl);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair(strKey, strValue));
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			String str = EntityUtils.toString(httpEntity);
			try {
				jsonObj = new JSONObject(str);
			} catch (JSONException e) {
				// LogUtil.log("sendPostDataAndResult", 4, e.getMessage());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @return
	 */
	public boolean checkNetwork(String url) {
		HttpURLConnection con = getHttpconnect(url);
		try {
			int rescode = con.getResponseCode();
			if (HttpURLConnection.HTTP_OK == rescode)
				return true;
			else
				return false;
		} catch (IOException e) {
			// LogUtil.log("checkNetwork", 4, e.getMessage());
		}
		return false;
	}

}
