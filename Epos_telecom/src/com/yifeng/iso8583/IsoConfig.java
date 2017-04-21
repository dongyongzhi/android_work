package com.yifeng.iso8583;

import java.io.InputStream;
import java.util.Map;

import com.yifeng.iso8583.schema.Field;
import com.yifeng.iso8583.schema.IsoXmlParse;

/**
 * iso 8583 配置
 * @author qin
 * 
 * 2012-11-14
 */
public class IsoConfig {
	
	/**
	 * 获取iso 配置信息
	 * @return
	 */
	public static Map<Integer,Field> getConfig(){
		return IsoXmlParse.fields;
	}
	
	/**
	 * 获取 参与mac计算的域配置信息
	 * @return
	 */
	public static Map<Integer,Integer> getMacConfig(){
		return IsoXmlParse.macFields;
	}
	
	/**
	 * 增加或 覆盖己iso 配置域
	 * @param fields   域集合
	 * @return
	 */
	public static Map<Integer,Field> appendOrReplace(Field...fields){
		Map<Integer,Field> maps = IsoXmlParse.fields;
		if(fields!=null)
			for(Field e: fields){
				maps.put(e.getIndex(), e);
			}
		return maps;
	}
	
	/**
	 * 增加或 覆盖己iso 配置域
	 * @param xml  域的配置文件xml
	 * @return
	 */
	public static Map<Integer,Field> appendOrReplace(InputStream xml){
		IsoXmlParse.parse(xml);
		return IsoXmlParse.fields;
	}
	
}
