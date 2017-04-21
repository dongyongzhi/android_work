package com.yifeng.iso8583.schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class IsoXmlParse {
	private static final String ISO8583_FILE = "iso8583.xml";
	private static final String XML_CHARSET = "UTF-8";
	
	/** 所有域 128 */
	public static Map<Integer,Field> fields = new HashMap<Integer,Field>(128);
	/** mac 参数计算的域 */
	public static Map<Integer,Integer> macFields = new HashMap<Integer,Integer>();  
	
	static{
		InputStream is =null;
		try {
			is = IsoXmlParse.class.getResourceAsStream(ISO8583_FILE);
			if(is!=null)
				parse(is);
		}finally{
			try {
				if(is!=null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 解析 iso8583 文件
	 * @param is
	 */
	public static void parse(InputStream is){
		 
		try {
		    //android pull 解析器
		    XmlPullParser parser = Xml.newPullParser();  
	        parser.setInput(is, XML_CHARSET); 
	        int event = parser.getEventType();//产生第一个事件  
	        
	        //获取内容
	        Field field = null;
	        int level = 1; //层级
	        Map<Integer,Field> tmp = new HashMap<Integer,Field>(6); //最大6级
	        while(event!=XmlPullParser.END_DOCUMENT){  
	        	 switch(event){  
	        	 	case XmlPullParser.START_TAG:   // start tag
	        	 		//配置列表获取
	        	 		if("field".equals(parser.getName())){
							  if(level==1){
								  field = getField(parser);
								  fields.put(field.getIndex(), field);
								  tmp.clear();
								  tmp.put(level, field);  //临时缓存
							  }else {
								  field = getField(parser);
								  tmp.get(level-1).getFields().add(field);  //取上一级
								  tmp.put(level, field);
							  }
							  level ++;
						  }
	        	 		break;
	        		
	        	 	case XmlPullParser.END_TAG:  //end tag
	        	 		if(!parser.getName().equals("field"))
	        	 			break;
						level--;
						break;
	        	 }
	        	 event = parser.next();//进入下一个元素并触发相应事件  
	        }
	        tmp.clear();
	        
	 
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static Field getField(XmlPullParser parser){
		  Field field = new Field();
		  field.setIndex(Integer.parseInt(parser.getAttributeValue(0)));
		  String length = parser.getAttributeValue(null,"length");
		  String maxlen =parser.getAttributeValue(null,"maxlen");
		  field.setLength(length!=null ? Integer.parseInt(length) : 0);
		  field.setMaxlen(maxlen!=null ? Integer.parseInt(maxlen) : 0);
		  field.setName(parser.getAttributeValue(null,"name"));
		  try{
			  field.setType(parser.getAttributeValue(null,"type"));
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		 return field;
	}
}
