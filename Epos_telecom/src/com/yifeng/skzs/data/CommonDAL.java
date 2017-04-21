package com.yifeng.skzs.data;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.DataConvert;
import com.yifeng.skzs.util.DateUtil;

import android.content.Context;

/**
 * ClassName:CommonDAL 
 * Description：公共数据操作类（列表查询）
 * @author Administrator 
 * Date：2012-10-15
 */
public class CommonDAL extends BaseDAL
{
	/**
	 * @param context
	 */
	public CommonDAL(Context context)
	{
		super(context);
	}

	/**
	 * (get请求方式)
	 * @param map
	 * @param url
	 * @return
	 */
	public List<Map<String, String>> doQuery(Map<String, String> map, String url)
	{
		/*this.setUrl(this.getIpconfig() + url);
		String json = this.doGet(map);
		return DataConvert.toArrayList(json);*/
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> temp = new HashMap<String, String>();
		if(map.get("page").equals("0")){
			for(int i = 0;i<10; i++){
				temp = new HashMap<String, String>();
				temp.put("state", "1");
				temp.put("ID", i+"");
				temp.put("NOTICE_TITLE", "江苏公司携手“2008刘若英‘梦游’南京演唱会”");
				temp.put("NOTICE_TIME", "2012-11-22 14:36");
				temp.put("NOTICE_CONTENT", "“2008刘若英‘梦游’南京演唱会”将于3月15日在南京奥体中心体育馆举行。江苏省公用信息有限公司与演唱会主办方江苏综艺频道合作，推出“互联星空”百张门票馈用户，短信、彩铃齐抢票等活动，受到了广泛好评。");
				list.add(temp);
			}
		}else{
			temp = new HashMap<String, String>();
			temp.put("state", "0");
			list.add(temp);
		}
		return list;
	}

	/**
	 * (post请求方式)
	 * @param params
	 * @param url
	 * @return
	 */
	public List<Map<String,Object>> doPostQuery(List<NameValuePair> params, String url)
	{
		this.setUrl(ConstantUtil.ip + url);
		String json = this.doPost(params);
		return DataConvert.toConvertObjectList(json, "list");
	}

}
