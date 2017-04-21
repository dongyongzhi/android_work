package com.yifeng.skzs.util;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * @author 吴家宏
 * 2011-06-02
 * BOX平台及手机端常用数据结果集转换工具类
 * 主要对map,list,json,CommonVO,VOList数据进行转换
 */
public class DataConvert {
	/**
	 * 根据JSON格式字各符串转换成List<Map<String,String>>集合
	 * @param array  state状态 0：表示没有数据，1：表示加载成功，-1:表示返回error字符,2:表示解析失败json格式不正确
	 * @return list
	 */
   public static List<Map<String,String>> toArrayList(String array){
	   List<Map<String,String>> list=new ArrayList<Map<String,String>>();
	   Map<String,String> map=new HashMap<String,String>();
	   if(array.equals("")){
		   map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
	       list.add(map);
		   return list;
	   }
	   else if(StringHelper.eregi_replace("(\r\n|\r|\n|\n\r)", "", array).equals("error")){
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
	       list.add(map);
		   return list;
	   }else if(array.equals("FAIL")){//key失败
		   map.put("state", String.valueOf(ConstantUtil.KEY_ERROR));
	       list.add(map);
		   return list;
	   }else{
	   try {
		JSONArray jdata=new JSONArray(array);
		if(jdata.length()==0){
			map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
			list.add(map);
		}else{
			for(int i=0;i<jdata.length();i++){
					JSONObject obj=jdata.getJSONObject(i);
					Map<String,String> map1=new HashMap<String,String>();
					 map1.put("state", String.valueOf(ConstantUtil.LOGIN_SUCCESS));
					for (@SuppressWarnings("rawtypes")
					Iterator iterator = obj.keys(); iterator.hasNext();) {
						String key = (String)iterator.next();
						if(obj.get(key)instanceof String ){
							map1.put(key, obj.getString(key));
						}
						else if(obj.get(key)instanceof Integer){
							map1.put(key, String.valueOf(obj.getInt(key)));
						}
						else if(obj.get(key)instanceof Long){
							map1.put(key, String.valueOf(obj.getLong(key)));
						}
						else if(obj.get(key)instanceof Double){
							map1.put(key, StringHelper.doConvert(obj.get(key)));
						}
						else{
							map1.put(key,obj.get(key).toString());
						}
					}
					list.add(map1);
				}
				}
			} catch (Exception e) {
				map.put("state", String.valueOf(ConstantUtil.INNER_ERROR));
				list.add(map);
			}
		   return list;
	    }
    }
   
   /**
	 * 根据JSON格式字各符串转换成List<Map<String,Object>>集合
	 * @param array  state状态 0：表示没有数据，1：表示加载成功，-1:表示返回error字符,2:表示解析失败json格式不正确
	 * @return list
	 */
  public static List<Map<String,Object>> toObjectArrayList(String array){
	   List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
	   Map<String,Object> map=new HashMap<String,Object>();
	   if(array.equals("")){
		   map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
	       list.add(map);
		   return list;
	   }
	   else if(StringHelper.eregi_replace("(\r\n|\r|\n|\n\r)", "", array).equals("error")){
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
	       list.add(map);
		   return list;
	   }else if(array.equals("FAIL")){//key失败
		   map.put("state", String.valueOf(ConstantUtil.KEY_ERROR));
	       list.add(map);
		   return list;
	   }else{
	   try {
		JSONArray jdata=new JSONArray(array);
		if(jdata.length()==0){
			map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
			list.add(map);
		}else{
			for(int i=0;i<jdata.length();i++){
					JSONObject obj=jdata.getJSONObject(i);
					Map<String,Object> map1=new HashMap<String,Object>();
					 map1.put("state", String.valueOf(ConstantUtil.LOGIN_SUCCESS));
					for (Iterator iterator = obj.keys(); iterator.hasNext();) {
						String key = (String)iterator.next();
						if(obj.get(key)instanceof String ){
							map1.put(key, obj.getString(key));
						}
						else if(obj.get(key)instanceof Integer){
							map1.put(key, String.valueOf(obj.getInt(key)));
						}
						else if(obj.get(key)instanceof Long){
							map1.put(key, String.valueOf(obj.getLong(key)));
						}
						else if(obj.get(key)instanceof Double){
							map1.put(key, StringHelper.doConvert(obj.get(key)));
						}else if(obj.get(key)==null||obj.get(key).toString().equals("null")){
							map.put(key,"");
						}
						else{
							map1.put(key,obj.get(key).toString());
						}
					}
					list.add(map1);
				}
				}
			} catch (Exception e) {
				map.put("state", String.valueOf(ConstantUtil.INNER_ERROR));
				list.add(map);
			}
		   return list;
	    }
   }
   
   /**
	 * 根据JSON格式字各符串转换成Map<String,String>集合
	 * @param json  state状态 0：表示没有数据，1：表示加载成功，-1:表示返回error字符,2:表示解析失败json格式不正确
	 * @return map
	 */
  public static Map<String,String> toMap(String json){
	   Map<String,String> map=new HashMap<String,String>();
	   if(json.equals("")){
		   map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
		   return map;
	   }
	   else if(StringHelper.eregi_replace("(\r\n|\r|\n|\n\r)", "", json).equals("error")){
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
		   return map;
	   }else if(json.equals("FAIL")){//key失败
		   map.put("state", String.valueOf(ConstantUtil.KEY_ERROR));
		   return map;
	   }else{
	   try {
		 JSONObject jdata=new JSONObject(json);
		if(jdata.length()==0){
			map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
		}else{
				 map.put("state", String.valueOf(ConstantUtil.LOGIN_SUCCESS));
				 for (Iterator iterator = jdata.keys(); iterator.hasNext();) {
							String key = (String)iterator.next();
							if(jdata.get(key)instanceof String ){
								map.put(key, jdata.getString(key));
							}
							else if(jdata.get(key)instanceof Integer){
								map.put(key, String.valueOf(jdata.getInt(key)));
							}
							else if(jdata.get(key)instanceof Long){
								map.put(key, String.valueOf(jdata.getLong(key)));
							}
							else if(jdata.get(key)instanceof Double){
								map.put(key, StringHelper.doConvert(jdata.get(key)));
							}else if(jdata.get(key)==null||jdata.get(key).toString().equals("null")){
								map.put(key,"");
							}
							else{
								map.put(key,jdata.get(key).toString());
							}
							
				 }	
			}
			} catch (Exception e) {
				e.printStackTrace();
			    map.put("state", String.valueOf(ConstantUtil.INNER_ERROR));
			}
	     }
	   return map;
   }
  
  /**
	 * 根据JSON格式字各符串转换成Map<String,String>集合
	 * @param json  state状态 0：表示没有数据，1：表示加载成功，-1:表示返回error字符,2:表示解析失败json格式不正确
	 * @return map
	 */
public static Map<String,Object> toObjectMap(String json){
	   Map<String,Object> map=new HashMap<String,Object>();
	   if(json.equals("")){
		   map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
		   return map;
	   }
	   else if(StringHelper.eregi_replace("(\r\n|\r|\n|\n\r)", "", json).equals("error")){
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
		   return map;
	   }else if(json.equals("FAIL")){//key失败
		   map.put("state", String.valueOf(ConstantUtil.KEY_ERROR));
		   return map;
	   }else{
	   try {
		 JSONObject jdata=new JSONObject(json);
		if(jdata.length()==0){
			map.put("state", String.valueOf(ConstantUtil.IS_EMPTY));
		}else{
				 map.put("state", String.valueOf(ConstantUtil.LOGIN_SUCCESS));
				 map.put("success","true");
				 for (Iterator iterator = jdata.keys(); iterator.hasNext();) {
							String key = (String)iterator.next();
							if(jdata.get(key)instanceof String ){
								map.put(key, jdata.getString(key));
							}
							else if(jdata.get(key)instanceof Integer){
								map.put(key, String.valueOf(jdata.getInt(key)));
							}
							else if(jdata.get(key)instanceof Long){
								map.put(key, String.valueOf(jdata.getLong(key)));
							}
							else if(jdata.get(key)instanceof Double){
								map.put(key, StringHelper.doConvert(jdata.get(key)));
							}
							else{
								map.put(key,jdata.get(key).toString());
							}
							
				 }	
			}
			} catch (Exception e) {
			    map.put("state", String.valueOf(ConstantUtil.INNER_ERROR));
			}
	     }
	   return map;
 }

  
  /***
   * 根据给定的List<Map<String,String>> 转换成json格式数据
   * @param list
   * @return  JSONArray
   */
  public static JSONArray toJSONArray(List<Map<String,String>> list){
	  JSONArray jsonarray=new JSONArray();
	  try{
		  for (Iterator iter = list.iterator(); iter.hasNext();) {
			JSONObject json=new JSONObject();
			Map<String,String> map = (Map<String,String>) iter.next();
				for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
					String key = (String) iterator.next();
					json.put(key, map.get(key));
				}
			 jsonarray.put(json);
		   }
		 return jsonarray;
	  }catch(Exception e){
	}
	  return null;
  }
  
  /**
   * 根据给定的map转换成JSONObject数据
   * @param map
   * @return
   */
  public static JSONObject toJsonObject(Map<String,String> map){
	 try{
	   JSONObject json=new JSONObject();
		 for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			json.put(key, map.get(key));
		}
	    return json;
	}catch(Exception e){
	}
	return null;
  }
  
  /***
   * 给定指定的 json格式和目标名转换成String list
   * @param json
   * @param keyName
   * @return
   */
 public static List<Map<String,String>> toConvertStringList(String json,String keyName){
	  List<Map<String,String>> list=new ArrayList<Map<String,String>>();
	  Map<String,String> map=null;
	  try{
	   map=toMap(json);
	  if(map.get("state").equals(String.valueOf(ConstantUtil.LOGIN_SUCCESS))){
		  if(map.get("success").equals("true")){
			  List<Map<String,String>> nlist=toArrayList(map.get(keyName));
			  for (int i = 0; i < nlist.size(); i++) {
				  Map<String,String> nmap=nlist.get(i);
				  nmap.put("msg", map.get("msg"));
				  list.add(nmap);
			  }
		  }else{
			  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
		  }
	  }else{
		  list.add(map);
	  }
	  }catch (Exception e) {
		   map=new HashMap<String,String>();
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
		   list.add(map);
		   e.printStackTrace();
	}
	  return list;
 }
 
 /***
  * 给定指定的 json格式和目标名转换成Object list
  * @param json
  * @param keyName
  * @return
  */
public static List<Map<String,Object>> toConvertObjectList(String json,String keyName){
	  
	  List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
	  Map<String,Object> map=null;
	  try{
		  map=toObjectMap(json);
		  if(map.get("state").equals(String.valueOf(ConstantUtil.LOGIN_SUCCESS))){
			  if(map.get("success").equals("true")){
				  List<Map<String,Object>> nlist=toObjectArrayList((String)map.get(keyName));
				  for (int i = 0; i < nlist.size(); i++) {
					  Map<String,Object> nmap=nlist.get(i);
					  nmap.put("msg", map.get("msg"));
					  list.add(nmap);
				  }
			  }else{
				  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
			  }
		  }else{
			  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
			  list.add(map);
		  }
	   }catch (Exception e) {
		   map=new HashMap<String,Object>();
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
		   list.add(map);
		   e.printStackTrace();
	}
	  return list;
}
/***
 * 给定指定的 json格式和目标名转换成String Map
 * @param json
 * @param keyName
 * @return
 */
public static Map<String,String> toConvertStringMap(String json,String keyName){
	Map<String,String> map=null; 
	try{ 
		  map=toMap(json);
		  if(map.get("state").equals(String.valueOf(ConstantUtil.LOGIN_SUCCESS))){
			  String msg=map.get("msg");
			  if(map.get("success").equals("true")){
				  String njson=map.get(keyName);
				  map=DataConvert.toMap(njson); 
				  map.put("msg", msg);
			  }else{
				  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
			  }
		  }else{
			  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
			 
		  }
	 }catch (Exception e) {
		   map=new HashMap<String,String>();
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
		   e.printStackTrace();
	}
	  return map;
}

/***
 * 给定指定的 json格式和目标名转换成Object Map
 * @param json
 * @param keyName
 * @return
 */
public static Map<String,Object> toConvertObjectMap(String json,String keyName){
	 Map<String,Object> map=null;
	 try{
		 map=toObjectMap(json);
		  if(map.get("state").equals(String.valueOf(ConstantUtil.LOGIN_SUCCESS))){
			  String msg=map.get("msg")+"";
			  if(map.get("success").equals("true")){
				  String njson=(String)map.get(keyName);
				  map=DataConvert.toObjectMap(njson); 
				  map.put("msg", msg);
			  }else{
				  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
			  }
		  }else{
			  map.put("state", String.valueOf(ConstantUtil.LOGIN_FAIL));
			 
		  } 
	  }catch (Exception e) {
		   map=new HashMap<String,Object>();
		   map.put("state", String.valueOf(ConstantUtil.SERVER_ERROR));
		   e.printStackTrace();
	}
	  return map;
}
}