package com.yifeng.skzs.data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.ctbri.ui.UserSession;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.HttpPostGetUtil;
import com.yifeng.skzs.util.JsonReturn;
import com.yifeng.skzs.util.StringHelper;
/**
 * 读取远程数据操作父类
 * @author 吴家宏
 * 2011-04-19
 */
public class BaseDAL {
	private String ipconfig;
	public Context context;
	private String url;
	public BaseDAL(Context context){
		this.context=context;
		HttpPostGetUtil.getHttpClient();
		ipconfig=ConstantUtil.ip;
	}
	
	public String getIpconfig() {
		return ipconfig;
	}

	public void setIpconfig(String ipconfig) {
		this.ipconfig = ipconfig;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * get请求
	 * @param params
	 * @return
	 */
	public String doGet(Map<String,String> params){
		if(params==null){
			params=new HashMap<String,String>();
		}
		String data=HttpPostGetUtil.doGet(ConstantUtil.news_ip+"android/news/listNews", params);
		if(data!=null){
			data=StringHelper.eregi_replace("(\r\n|\r|\n|\n\r)", "", data).trim();
	    }
		return data;
	}
	/***
	 * post请求
	 * @param pos_params
	 * @return
	 */
	public JsonReturn doPost(List<NameValuePair> pos_params){
		if(pos_params==null){
			 pos_params=new ArrayList<NameValuePair>();
		}
		UserSession session=new UserSession(this.context);
		pos_params.add(new BasicNameValuePair("key",session.getUser().getKey())); 
		JsonReturn re = HttpPostGetUtil.doPost(this.getUrl(), pos_params);
		 if(re.returnCode==200){
				re.json=StringHelper.eregi_replace("(\r\n|\r|\n|\n\r)", "", re.json).trim();
				
		  }
		 return re;
	}

}
