package com.hftcom.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符操作类
 * @author 吴家宏版本1.1
 * 
 */
public class StringHelper {
	/**
	 * 自定义字符串替换函数
	 * 
	 * @param strFrom
	 * @param strTo
	 * @param strTarget
	 * @return
	 */
	public static String eregi_replace(String strFrom, String strTo,
			String strTarget) {
		String strPattern = "(?i)" + strFrom;
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strTarget);
		if (m.find()) {
			return strTarget.replaceAll(strFrom, strTo);
		} else {
			return strTarget;
		}
	}

	/**
	 * 解析替换XML回来前面带空格的数据
	 * 
	 * @param strFrom
	 * @param strTo
	 * @param strTarget
	 * @return
	 */
	@SuppressWarnings("null")
	public static String eregi_replaceXml(String strFrom, String strTo,
			String strTarget) {
		String strPattern = "(?i)" + strFrom;
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strTarget);
		String str = "";
		if (m.find()) {
			str = strTarget.replaceAll(strFrom, strTo);
			if (str != null || !str.equals("")) {
				int index = str.indexOf("<?xml");// 找到XML前面的第一个索引
				str = str.substring(index);
			}
			return str;
		} else {
			str = strTarget;
			if (str != null || !str.equals("")) {
				int index1 = str.indexOf("<?xml");// 找到XML前面的第一个索引
				str = str.substring(index1);
			}
			return str;
		}
	}

	/**
	 * MD5密码加密
	 * 
	 * @param pwd
	 * @return
	 */
	public static String handlePwd(String pwd) {
		try {
			// Digest dg = new Digest(Digest.ALGORITHM_MD5);
			// String md5Pwd = CipherTool.byteToString(dg.getDigest(pwd),
			// false);
			String md5Pwd = MD5.getMD5(pwd);
			return md5Pwd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pwd;
	}

	/**
	 * 清除掉所有特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static String StringFilter(String str) {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String nstr = "";
		try {
			if (!str.equals("")) {
				String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/\\?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(str);
				nstr = m.replaceAll("").trim();
				nstr = nstr.replace("\\", "");
				return nstr;
			} else {
				return nstr;
			}
		} catch (Exception e) {
			return nstr;
		}
	}

	/***
	 * 去掉HTML标记
	 * 
	 * @param str
	 * @return
	 */
	public static String removeHtml(String str) {
		String nstr = "";
		if (!str.equals("")) {
			Pattern pattern = Pattern.compile("<.+?>", Pattern.DOTALL);
			Matcher matcher = pattern.matcher(str);
			nstr = matcher.replaceAll("");
		}
		return nstr;

	}

	/**
	 * 检测特殊字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkString(String str) {
		if (!str.equals("")) {
			String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";
			Pattern pattern = Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}

	/**
	 * 判断是否只能输入数字及字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNumOrEn(String str) {
		if (!str.equals("")) {
			Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}

	/**
	 * 验证邮箱
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkEmail(String str) {
		if (!str.equals("")) {
			Pattern pattern = Pattern.compile(
					"[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+",
					Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(str);
			return matcher.matches();
		} else {
			return false;
		}
	}

	/***
	 * 格式化浮点数
	 * 
	 * @param obj
	 * @return
	 */
	public static String doConvert(Object obj) {
		try {
			DecimalFormat df = new DecimalFormat("#0.00");
			String str = df.format(obj);
			return str;
		} catch (Exception e) {
			return "0";
		}

	}

	/**
	 * 去掉带有控格的EditText
	 * 
	 * @param str
	 * @return
	 */
	public static String doConvertEmpty(String str) {
		String nstr = "";
		try {
			if (!str.equals("") || str != null) {
				nstr = str.replaceAll(" ", "");
			}
		} catch (Exception e) {
			return nstr;
		}
		return nstr;
	}

	/***
	 * 经度转换 11918617 转成119.18617*1E6
	 * @param longitude
	 * @return
	 */
	public static int getLongitude(String longitude){
		String lng="0";
		int returnLng=0;
		try{
		 if(longitude.length()>3){
			 lng=longitude.substring(0,3)+"."+longitude.substring(3);
			 returnLng=(int) (Double.parseDouble(lng)*1E6);
		 }
		}catch(Exception e){
			e.printStackTrace();
			returnLng=0;
		}
		return returnLng;
	}
	
	/***
	 * 纬度转换 3226327 转成32.26327*1E6
	 * @param latitude
	 * @return
	 */
    public static int getLatitude(String latitude){
    	String lng="0";
    	int returnLng=0;
		try{
		 if(latitude.length()>2){
			 lng=latitude.substring(0,2)+"."+latitude.substring(2);
			 returnLng=(int)(Double.parseDouble(lng)*1E6);
		 }
		}catch(Exception e){
			e.printStackTrace();
			returnLng=0;
		}
		return returnLng;
	}
    
}
