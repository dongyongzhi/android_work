package com.yifeng.skzs.util;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * 字符串处理辅助类
 * 
 * @author CMBR
 * @version 2.0.0.0
 */
public class StringUtil {

	/**
	 * 替换掉字符串所有要替换的字符串
	 * 
	 * @param source
	 * @param replaced
	 *            要替换掉的字符串
	 * @param tarrget
	 *            要替换成的字符串
	 * @return
	 */
	public static String replaceAll(String source, String replaced,
			String target) {
		return replace(source, replaced, target, false);
	}

	private static String replace(String source, String replaced,
			String target, boolean bFirst) {
		if (replaced == null || target == null || source == null) {
			return source;
		}
		StringBuffer buf = new StringBuffer(source);
		int len = buf.length();
		int rLen = replaced.length();
		int tLen = target.length();
		for (int i = 0; i < len;) {
			if (i + rLen <= len) {
				if (buf.substring(i, i + rLen).equals(replaced)) {
					buf.replace(i, i + rLen, target);
					i += tLen;
					len = len + tLen - rLen;
					if (bFirst) {
						break;
					} else {
						continue;
					}
				}
			}
			i++;
		}
		return buf.toString();
	}

	/**
	 * 替换掉字符串所有要替换的字符串(只替换第一满足条件的)
	 * 
	 * @param source
	 * @param start
	 *            起始位置
	 * @param replaced
	 *            要替换掉的字符串
	 * @param tarrget
	 *            要替换成的字符串
	 * @return
	 */
	public static String replaceFirst(String source, int start,
			String replaced, String target) {
		String pre = "";
		String post = source;
		if (source != null) {
			if (start < source.length() && start >= 0) {
				pre = source.substring(0, start);
				post = source.substring(start);
			}
		}
		return pre + replace(post, replaced, target, true);
	}

	/**
	 * 替换掉给定字符串中参数标识
	 * 
	 * @param source
	 *            给定字符串
	 * @param values
	 *            参数值列表
	 * @return 替换后的字符串
	 */
	public static String replaceParams(String source, String[] values) {
		int nCount = values.length;
		String ret = source;
		for (int i = 0; i < nCount; i++) {
			ret = ret.replaceFirst("%s", values[i]);
		}
		return ret;
	}

	/**
	 * 替换字符串中参数值，参数格式 {参数名称}
	 * 
	 * @param source
	 *            目标字符串，
	 * @param params
	 *            参数值列表
	 * @return 替换后的值
	 */
	public static String handleParams(String source, Properties params) {
		if (source != null && params != null) {
			StringBuffer buf = new StringBuffer(source);
			int start = -1;
			int end = -1;
			for (int i = 0; i < buf.length();) {
				if (buf.charAt(i) == '{') {
					start = i;
				}
				if (buf.charAt(i) == '}') {
					end = i;
				}
				if (start >= 0 && end > start) {
					String paramName = buf.substring(start + 1, end);
					buf.delete(start, end + 1);
					String value = params.getProperty(paramName);
					if (value != null) {
						buf.insert(start, value);
						i = start + value.length();
					}
					start = -1;
					end = -1;
				} else {
					i++;
				}
			}
			return buf.toString();
		}
		return source;
	}

	/**
	 * 替换指定区间的字符串
	 * 
	 * @param source
	 *            源字符串
	 * @param start
	 *            替换起始位置
	 * @param end
	 *            替换截止位置
	 * @param value
	 *            替换值
	 * @return 替换后的值
	 */
	public static String replace(String source, int start, int end, String value) {
		String temp = source.substring(0, start);
		temp += value;
		temp += source.substring(end, source.length());
		return temp;
	}

	/**
	 * 替换给定字符串的给定参数
	 * 
	 * @param source
	 *            给定字符串
	 * @param value
	 *            给定参数值
	 * @return 替换后的字符串
	 */
	public static String replaceParam(String source, String value) {
		String[] values = new String[1];
		values[0] = value;
		return replaceParams(source, values);
	}

	/**
	 * 将一字符串添加到已有的字符串数组中去
	 * 
	 * @param target
	 *            已有的字符串数组
	 * @param temp
	 *            待添加的字符串
	 * @return 新的字符串数组
	 */
	public static String[] addStringToArray(String[] target, String temp) {
		int nLength = 0;
		if (target != null) {
			nLength = target.length;
		}
		String[] rets = new String[nLength + 1];
		for (int i = 0; i < nLength; i++) {
			rets[i] = target[i];
		}
		rets[nLength] = temp;
		return rets;
	}

	/**
	 * 检查某一个字符串是否存在于指定数组中
	 * 
	 * @param value
	 *            待检查字符串
	 * @param source
	 *            指定字符数组
	 * @return 如果存在，返回true；否则，返回false
	 */
	public static boolean exists(String value, String[] source) {
		if (source != null) {
			int len = source.length;
			for (int i = 0; i < len; i++) {
				if (source[i].equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 检查某一个字符串是否存在于指定数组中
	 * 
	 * @param value
	 *            待检查字符串
	 * @param source
	 *            指定字符数组
	 * @param caseInsensitive
	 *            是否忽略大小写 true 忽略 false 区分大小写
	 * @return 如果存在，返回true；否则，返回false
	 */
	public static boolean exists(String value, String[] source,
			boolean caseInsensitive) {
		if (source != null) {
			int len = source.length;
			for (int i = 0; i < len; i++) {
				String sTemp = source[i];
				if (value == null) {
					if (sTemp == null) {
						return true;
					}
				} else {
					if (sTemp != null) {
						if (caseInsensitive) {
							if (sTemp.equalsIgnoreCase(value)) {
								return true;
							}
						} else {
							if (sTemp.equals(value)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 判断一个字符串在目标字符串数组中第一次出现的索引号
	 * 
	 * @param value
	 *            待判断字符串
	 * @param source
	 *            目标字符串数组
	 * @return 位置序号，从0开始，如果不存在，则返回-1
	 */
	public static int indexOfArray(String value, String[] source,
			boolean caseInsensitive) {
		if (source != null && value != null) {
			int len = source.length;
			for (int i = 0; i < len; i++) {
				if (source[i] != null) {
					if (caseInsensitive) {
						if (source[i].toUpperCase().equals(value.toUpperCase())) {
							return i;
						}
					} else {
						if (source[i].equals(value)) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	/**
	 * 用指定的字符将一字符串修补到指定长度
	 * 
	 * @param source
	 *            待修补的字符串
	 * @param len
	 *            目标长度
	 * @param patch
	 *            修补字符
	 * @param beforePos
	 *            如果为true，表示在字符串前修补；否则，在字符串后修补
	 * @return 修补后的字符串
	 */
	public static String patchString(String source, int len, char patch,
			boolean beforePos) {
		StringBuffer buf = new StringBuffer(source);
		while (buf.length() < len) {
			if (beforePos) {
				buf.insert(0, patch);
			} else {
				buf.append(patch);
			}
		}
		return buf.toString();
	}

	/**
	 * 从左边除去指定字符串
	 * 
	 * @param source
	 *            源字符串
	 * @param trim
	 *            待除去的字符串
	 * @return 除去后的字符串
	 */
	public static String leftTrim(String source, String trim) {
		StringBuffer buf = new StringBuffer(source);
		if (null == trim) {
			return source;
		}
		while (buf.indexOf(trim) == 0) {
			buf = buf.delete(0, trim.length());
		}
		return buf.toString();
	}

	/**
	 * 从右边除去指定字符串
	 * 
	 * @param source
	 *            源字符串
	 * @param trim
	 *            待除去的字符串
	 * @return 除去后的字符串
	 */
	public static String rightTrim(String source, String trim) {
		StringBuffer buf = new StringBuffer(source);
		if (null == trim) {
			return source;
		}
		int lenTrim = trim.length();
		while (buf.lastIndexOf(trim) == buf.length() - lenTrim) {
			int len = buf.length();
			buf = buf.delete(len - lenTrim, len);
		}
		return buf.toString();
	}

	/**
	 * 在字符串前用指定的字符将一字符串修补到指定长度
	 * 
	 * @param source
	 *            待修补的字符串
	 * @param len
	 *            目标长度
	 * @param patch
	 *            修补字符
	 * @return 修补后的字符串
	 */
	public static String patchStringHead(String source, int len, char patch) {
		return patchString(source, len, patch, true);
	}

	/**
	 * 在字符串后用指定的字符将一字符串修补到指定长度
	 * 
	 * @param source
	 *            待修补的字符串
	 * @param len
	 *            目标长度
	 * @param patch
	 *            修补字符
	 * @return 修补后的字符串
	 */
	public static String patchStringTail(String source, int len, char patch) {
		return patchString(source, len, patch, false);
	}

	/**
	 * 对给定字符串中的参数进行替换
	 * 
	 * @param source
	 *            原字符串
	 * @param paramName
	 *            待替换的参数
	 * @param value
	 *            参数值
	 * @return 替换后的字符串
	 */
	public static String replaceParams(String source, String paramName,
			String value) {
		return replaceParams(source, "%", paramName, "", value);
	}

	/**
	 * 替换字符串中指定参数
	 * 
	 * @param source
	 *            源字符串
	 * @param pre
	 *            参数前缀
	 * @param paramName
	 *            参数名称
	 * @param post
	 *            参数后缀
	 * @param value
	 *            参数值
	 * @return 替换后的字符串
	 */
	public static String replaceParams(String source, String pre,
			String paramName, String post, String value) {
		if (source != null) {
			return source.replaceAll(pre + paramName + post, value);
		} else {
			return source;
		}
	}

	/**
	 * 将指定的字节数字转化为目标编码的字符串
	 * 
	 * @param source
	 *            待转换的字节数组
	 * @param coding
	 *            目标编码
	 * @return 转换后的字符串
	 */
	public static String deCodingString(byte[] source, String coding) {
		try {
			return new String(source, coding);
		} catch (Exception e) {
		}
		return new String(source);
	}

	/**
	 * 将指定的字符串转换为目标编码的字节数组
	 * 
	 * @param source
	 *            待转换的字符串
	 * @param coding
	 *            目标编码
	 * @return 转换后的字节数组
	 */
	public static byte[] enCodingString(String source, String coding) {
		try {
			return source.getBytes(coding);
		} catch (Exception e) {
		}
		return source.getBytes();
	}

	/**
	 * 从ISO-8859-1到GBK编码
	 * 
	 * @param value
	 * @return
	 */
	public static String chgGBK(String value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			try {
				return new String(value.getBytes("ISO-8859-1"), "GBK");
			} catch (Exception e) {
			}
			return null;
		}
	}

	/**
	 * 将字符串从源编码格式转换到目标编码格式
	 * 
	 * @param value
	 *            待转换的字符串
	 * @param sourceCode
	 *            源编码
	 * @param targetCode
	 *            目标编码
	 * @return 转换后的字符串
	 * @throws UnsupportedEncodingException
	 */
	public static String codeTran(String value, String sourceCode,
			String targetCode) throws UnsupportedEncodingException {
		if (sourceCode.equalsIgnoreCase(targetCode)) {
			return value;
		} else if (value == null || value.equals("")) {
			return value;
		} else {
			return new String(value.getBytes(sourceCode), targetCode);
		}
	}

	public static String URLEncoding(String value) {
		try {
			return URLEncoder.encode(value);
		} catch (Exception e) {
		}
		return value;
	}

	public static String URLDecoding(String value) {
		try {
			return URLDecoder.decode(value);
		} catch (Exception e) {

		}
		return value;
	}

	/**
	 * 从字符串中获得参数标识值
	 * 
	 * @param pre
	 *            参数标识前缀
	 * @param last
	 *            参数标识后缀
	 * @param source
	 *            待处理的字符串
	 * @return 参数名称，不含前缀和后缀，如果不存在，则返回空字符串
	 */
	public static String findFirstParam(String pre, String last, String source) {
		String ret = source;
		int nStart = ret.indexOf(pre);
		if (nStart != -1) {
			ret = ret.substring(nStart + 1, ret.length());
		}
		int nEnd = ret.indexOf(last);
		if (nStart != -1 && nEnd != -1) {
			return source.substring(nStart + pre.length(), nEnd + nStart + 1);
		} else {
			return "";
		}
	}

	/**
	 * 从字符串获得参数标识值，其前缀和后缀都为"%"
	 * 
	 * @param value
	 * @return
	 */
	public static String findFirstParam(String value) {
		return findFirstParam("%", "%", value);
	}

	/**
	 * 生成指定长度的随机数字字符串
	 * 
	 * @param nLenth
	 *            指定长度
	 * @return 随机数字字符串
	 */
	public static String getRandomNumbers(int nLength) {
		Random rd = new Random();
		String temp = "";
		for (int i = 0; i < nLength; i++) {
			int ret = rd.nextInt(10);
			if (ret < 0) {
				ret = ret * (-1);
			}
			temp += ret;
		}
		return temp;
	}

	/**
	 * 将整型字符串转化为整型
	 * 
	 * @param value
	 *            整型字符串
	 * @return 整型数字，如果字符串非法，抛出异常
	 */
	public static int toInteger(String value) {
		return Integer.valueOf(value).intValue();
	}

	/**
	 * 将浮点型字符串转化为浮点型数字
	 * 
	 * @param value
	 *            待转化的浮点型字符串
	 * @return 浮点型数字，如果字符串非法，抛出异常
	 */
	public static float toFloat(String value) {
		return Float.valueOf(value).floatValue();
	}

	/**
	 * 将双精度数字字符串转化为双精度数字
	 * 
	 * @param value
	 *            待转化的双精度数字字符串
	 * @return 双精度数字，如果字符串非法，抛出异常
	 */
	public static double toDouble(String value) {
		return Double.valueOf(value).doubleValue();
	}

	/**
	 * 判断两个字符串再全部是大写的情况是否相等
	 * 
	 * @param target
	 *            字符串1
	 * @param source
	 *            字符串2
	 * @return true 相等， false 不等
	 */
	public static boolean equalsIgnoreCase(String target, String source) {
		if (target == null && source == null) {
			return true;
		} else if (target == null || source == null) {
			return false;
		} else {
			return target.equalsIgnoreCase(source);
		}
	}

	/**
	 * 将字符串数组转化为字符串
	 * 
	 * @param array
	 *            代转化的字符串数组
	 * @param splitSign
	 *            转化后的每个字符串之间的分隔符
	 * @return 转化后的字符串，如果给定的字符串数组为nul，则返回null
	 */
	public static String arrayToString(String[] array, String splitSign) {
		if (array != null) {
			int length = array.length;
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < length; i++) {
				buf.append(array[i]);
				if (i < length - 1) {
					buf.append(splitSign);
				}
			}
			return buf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 对字符串作并集
	 * 
	 * @param source
	 *            源数组
	 * @param target
	 *            目标数组
	 * @return
	 */
	public static String[] mergeArray(String[] source, String[] target) {
		List<String> ret = new ArrayList<String>();
		int length = source.length;
		for (int i = 0; i < length; i++) {
			ret.add(source[i]);
		}
		length = target.length;
		for (int i = 0; i < length; i++) {
			String temp = target[i];
			if (!StringUtil.exists(temp, source)) {
				ret.add(temp);
			}
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	/**
	 * 对字符串作交集
	 * 
	 * @param source
	 *            源数组
	 * @param target
	 *            目标数组
	 * @return
	 */
	public static String[] joinArray(String[] source, String[] target) {
		List<String> ret = new ArrayList<String>();
		int length = source.length;
		for (int i = 0; i < length; i++) {
			String temp = source[i];
			if (StringUtil.exists(temp, target)) {
				ret.add(temp);
			}
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	/**
	 * 将stacktrace导出为String
	 * 
	 * @param e
	 * @return
	 */
	public static String throwableToString(Throwable e) {
		CharArrayWriter cW = new CharArrayWriter();
		e.printStackTrace(new PrintWriter(cW));
		return cW.toString();
	}

	/**
	 * 将存在于target而且也存在于source中的字符串从source中剔除
	 * 
	 * @param source
	 *            源数组
	 * @param target
	 *            目标数组
	 * @return
	 */
	public static String[] divideArray(String[] source, String[] target) {
		List<String> ret = new ArrayList<String>();
		int length = source.length;
		for (int i = 0; i < length; i++) {
			String temp = source[i];
			if (!StringUtil.exists(temp, target)) {
				ret.add(temp);
			}
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	public static String upperPrefix(String source, int end) {
		if (source != null) {
			if (end > source.length()) {
				return source.toUpperCase();
			}
			String prefix = source.substring(0, end);
			prefix = prefix.toUpperCase();
			return prefix + source.substring(end);
		} else {
			return null;
		}
	}

	public static void replaceAll(StringBuffer buf, String source, String value) {
		int nIndex = buf.indexOf(source);
		if (nIndex != -1) {
			buf.replace(nIndex, nIndex + source.length(), value);
			replaceAll(buf, source, value);
		}
	}

	private final static String[] hex = { "00", "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B",
			"1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31",
			"32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C",
			"3D", "3E", "3F", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52",
			"53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D",
			"5E", "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68",
			"69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73",
			"74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E",
			"7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94",
			"95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F",
			"A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA",
			"AB", "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5",
			"B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0",
			"C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB",
			"CC", "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6",
			"D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1",
			"E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC",
			"ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7",
			"F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF" };

	private final static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01,
			0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };

	/**
	 * 将HTML内容编码处理
	 * 
	 * @param s
	 * @return
	 */
	public static String escape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') { // ?A?..?Z? : as it was
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') { // ?a?..?z? : as it was
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') { // '0'..'9' : as it was
				sbuf.append((char) ch);
			} else if (ch == '-'
					|| ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '*' || ch == '+' || ch == '@'
					|| ch == '/') {
				sbuf.append((char) ch);
			} else if (ch <= 0x007F) { // other ASCII : map to %XX
				sbuf.append('%');
				sbuf.append(hex[ch]);
			} else { // unicode : map to %uXXXX
				sbuf.append('%');
				sbuf.append('u');
				sbuf.append(hex[(ch >> 8)]);
				sbuf.append(hex[(0x00FF & ch)]);
			}
		}
		return sbuf.toString();
	}

	/**
	 * 将编码后的HTML内容解码
	 * 
	 * @param s
	 * @return
	 */
	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		int len = s.length();
		while (i < len) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') { // 'A'..'Z' : as it was
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') { // 'a'..'z' : as it was
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') { // '0'..'9' : as it was
				sbuf.append((char) ch);
			} else if (ch == '-'
					|| ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '*' || ch == '+' || ch == '@'
					|| ch == '/') {
				sbuf.append((char) ch);
			} else if (ch == '%') {
				int cint = 0;
				if ('u' != s.charAt(i + 1)) { // %XX : map to ascii(XX)
					cint = (cint << 4) | val[s.charAt(i + 1)];
					cint = (cint << 4) | val[s.charAt(i + 2)];
					i += 2;
				} else { // %uXXXX : map to unicode(XXXX)
					cint = (cint << 4) | val[s.charAt(i + 2)];
					cint = (cint << 4) | val[s.charAt(i + 3)];
					cint = (cint << 4) | val[s.charAt(i + 4)];
					cint = (cint << 4) | val[s.charAt(i + 5)];
					i += 5;
				}
				sbuf.append((char) cint);
			}
			i++;
		}
		return sbuf.toString();
	}

	/**
	 * 判断给定的值是否代表真
	 * 
	 * @param value
	 *            true|1|真|是|y
	 * @return
	 */
	public static boolean isTrue(String value) {
		return "true".equalsIgnoreCase(value) || "1".equals(value)
				|| "真".equals(value) || "是".equals(value)
				|| "y".equalsIgnoreCase(value);
	}

	/**
	 * 判断给定的值是否代表假
	 * 
	 * @param value
	 *            false|0|假|否|n|空|null
	 * @return
	 */
	public static boolean isFalse(String value) {
		return null == value || "".equals(value)
				|| "false".equalsIgnoreCase(value) || "0".equals(value)
				|| "假".equals(value) || "否".equals(value)
				|| "n".equalsIgnoreCase(value);
	}

	/**
	 * 将给定的字符串编码为合法的XML格式
	 * 
	 * @param source
	 * @return
	 */
	public static String XMLEncode(String source) {
		if (source != null) {
			StringBuffer buf = new StringBuffer(source);
			for (int i = 0; i < buf.length();) {
				char c = buf.charAt(i);
				if (c == '<') {
					buf.replace(i, i + 1, "&lt;");
					i = i + 3;
				} else if (c == '>') {
					buf.replace(i, i + 1, "&gt;");
				} else if (c == '"') {
					buf.replace(i, i + 1, "&quot;");
					i = i + 6;
				} else {
					i++;
				}
			}
			return buf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 将给定的XML格式字符串解码为普通字符串
	 * 
	 * @param source
	 * @return
	 */
	public static String XMLDecode(String source) {
		if (source != null) {
			StringBuffer buf = new StringBuffer(source);
			int len = buf.length();
			for (int i = 0; i < len;) {
				char c = buf.charAt(i);
				if (c == '&') {
					String temp = buf.substring(i, i + 4);
					if (temp.equals("&lt;")) {
						buf.replace(i, i + 4, "<");
					} else if (temp.equals("&gt;")) {
						buf.replace(i, i + 4, ">");
					} else {
						temp = buf.substring(i, i + 6);
						if (temp.equals("&quot;")) {
							buf.replace(i, i + 6, "\"");
						}
					}
					len = buf.length();
				}
				i++;
			}
			return buf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 将给定的HTML编码字符串为普通的字符串
	 * 
	 * @param source
	 * @return
	 */
	public static String html2plan(String source) {
		if (source != null) {
			StringBuffer buf = new StringBuffer(source);
			for (int i = 0; i < buf.length();) {
				char c = buf.charAt(i);
				if (c == '<') {
					buf.replace(i, i + 1, "&lt;");
					i = i + 3;
				} else if (c == '>') {
					buf.replace(i, i + 1, "&gt;");
				} else if (c == '"') {
					buf.replace(i, i + 1, "&quot;");
					i = i + 6;
				} else if (c == ' ') {
					buf.replace(i, i + 1, "&nbsp;");
					i = i + 6;
				} else if (c == '\n') {
					buf.replace(i, i + 1, "<br>");
					i = i + 4;
				} else {
					i++;
				}
			}
			return buf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 将字符串转化为合法的javascript格式
	 * 
	 * @param source
	 * @return
	 */
	public static String JavaScriptEncode(String source) {
		String ret = source;
		ret = StringUtil.replaceAll(ret, "\\", "\\\\");
		ret = StringUtil.replaceAll(ret, "\"", "\\\"");
		ret = StringUtil.replaceAll(ret, "'", "\\'");
		ret = StringUtil.replaceAll(ret, "\r\n", "\\r\\n");
		ret = StringUtil.replaceAll(ret, "\n", "\\n");
		ret = StringUtil.replaceAll(ret, "\t", "\\t");
		return ret;
	}

	/**
	 * 将给定的普通字符串转化为HTML编码字符串
	 * 
	 * @param source
	 * @return
	 */
	public static String plan2html(String source) {
		if (source != null) {
			StringBuffer buf = new StringBuffer(source);
			for (int i = 0; i < buf.length();) {
				char c = buf.charAt(i);
				if (c == '&') {
					String temp = buf.substring(i, i + 4);
					if (temp.equals("&lt;")) {
						buf.replace(i, i + 4, "<");
					} else if (temp.equals("&gt;")) {
						buf.replace(i, i + 4, ">");
					} else {
						temp = buf.substring(i, i + 6);
						if (temp.equals("&quot;")) {
							buf.replace(i, i + 6, "\"");
						}
					}
					i++;
				} else if (c == ' ') {
					buf.replace(i, i + 1, "&nbsp;");
					i = i + 5;
				} else if (c == '\n') {
					buf.replace(i, i + 1, "<br>");
					i = i + 3;
				} else {
					i++;
				}
			}
			return buf.toString();
		} else {
			return "";
		}
	}

	/**
	 * 按给定标识分割字符串为数组；非最后的空字符串也计算在类，
	 * 
	 * @param value
	 * @param sep
	 * @return
	 */
	public static String[] split(String value, String sep) {
		if (value == null || sep == null) {
			return new String[0];
		}
		if (value.length() == 0) {
			return new String[] { "" };
		}
		List<String> rets = new ArrayList<String>();
		StringBuffer buf = new StringBuffer(value);
		int len = buf.length();
		int sepLen = sep.length();
		boolean endWithSep = buf.toString().endsWith(sep);
		for (int i = 0; i < len;) {
			if (i + sepLen <= len) {
				if (buf.substring(i, i + sepLen).equals(sep)) {
					rets.add(buf.substring(0, i));
					buf.delete(0, i + sepLen);
					len = buf.length();
					i = 0;
					continue;
				}
			}
			i++;
		}
		if (buf.length() > 0) {
			rets.add(buf.toString());
		} else {
			if (endWithSep) {
				rets.add("");
			}
		}
		return (String[]) rets.toArray(new String[rets.size()]);
	}

	/**
	 * 将指定的字符串加载到源目标字符串的指定位置
	 * 
	 * @param specify
	 *            指定的字符串
	 * @param source
	 *            目标(源)字符串
	 * @param index
	 *            要插入目标(源)字符串的位置
	 * @return
	 */
	public static String insertStr2SpecifyIndex(String specify, String source,
			int index) {
		String tag1 = source.substring(0, index);
		String tag2 = source.substring(index, source.length());
		return tag1 + specify + tag2;
	}

	/**
	 * 去除HTML制表符
	 * 
	 * @param source
	 * @return
	 */
	public static String replaceTabs(String source) {
		String ret = source.replaceAll("</?[^>]+>", "");
		ret = ret.replaceAll("\\s*", "");
		ret = ret.replaceAll("\t", "");
		ret = ret.replaceAll("\r", "");
		ret = ret.replaceAll("\n", "");
		ret = ret.replaceAll("&ldquo;", "");
		ret = ret.replaceAll("&nbsp;", "");
		ret = ret.replaceAll("&rdquo;", "");

		return ret;
	}

	public static void main(String[] args) {
		/**
		 * String stest =
		 * "@-_\\=+|~!@#$%^&*(){}:\"<>?MNBVCXZASQWRERFYTGUU  UJJIJJ[];'/.'";
		 * System.out.println(escape(stest)); System.out.println(stest);
		 * System.out.println(unescape(escape(stest)));
		 * System.out.println(JavaScriptEncode(stest)); String enstest =
		 * XMLEncode(stest); System.out.println(enstest);
		 * System.out.println(replaceFirst(stest, 18, "d", "yydyyy"));
		 * System.out.println(XMLDecode(enstest)); String sss = "1111;111144;;";
		 * System.out.println(arrayToString(split(sss, ";"), "#")); Properties
		 * props = new Properties(); props.setProperty("param1", "你好");
		 * props.setProperty("param2", "你好吗！"); String s =
		 * "滴滴嗒嗒打发掉Aadddf（×（8902{param1}DFDFASFDAddDE1地地道道宕{param2}ddfdfa{param1}"
		 * ; System.out.println(handleParams(s, props));
		 */
		System.out
				.println(replaceTabs("<p>1.信息管理系统完善</p>&rdquo;&nbsp;<br/><p>2.苏之旅收客系统客户交流</p>"));
	}

}