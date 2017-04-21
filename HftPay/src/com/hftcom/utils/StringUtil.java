package com.hftcom.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class StringUtil {

	/**
	 * 不足length长度时右补空格
	 * 
	 * @param number
	 * @param length
	 * @return
	 */
	public static String leftAddZero(int number, int length) {
		char[] c = new char[length];
		char[] x = String.valueOf(number).toCharArray();
		if (x.length > length) {
			return String.valueOf(number).substring(0, length);
		}
		int lim = c.length - x.length;
		for (int i = 0; i < lim; i++) { // 数字靠右，左补0
			c[i] = '0';
		}
		System.arraycopy(x, 0, c, lim, x.length);
		return new String(c);
	}

	/**
	 * 数据number 不足 length长度时 左边补0
	 * 
	 * @param number
	 * @param length
	 * @return
	 */
	public static String leftAddZero(long number, int length) {
		char[] c = new char[length];
		char[] x = String.valueOf(number).toCharArray();
		if (x.length > length) {
			return String.valueOf(number).substring(0, length);
		}
		int lim = c.length - x.length;
		for (int i = 0; i < lim; i++) { // 数字靠右，左补0
			c[i] = '0';
		}
		System.arraycopy(x, 0, c, lim, x.length);
		return new String(c);
	}

	/**
	 * 字母向左靠，右部多余部分填空格。
	 * 
	 * @param value
	 *            原数据
	 * @param length
	 *            长度
	 * @return
	 */
	public static String rightAddSpace(String value, int length) {
		if (value == null) {
			value = "";
		}

		if (value.length() > length) {
			return value.substring(0, length);
		} else if (value.length() == length) {
			return value;
		} else {
			return String.format(String.format("%%-%ds", length), value); // 字母向左靠，右部多余部分填空格。
		}
	}

	public static String utf8Togb2312(String str) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case '+':
				sb.append(' ');
				break;
			case '%':
				try {
					sb.append((char) Integer.parseInt(
							str.substring(i + 1, i + 3), 16));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException();
				}
				i += 2;
				break;
			default:
				sb.append(c);
				break;
			}
		}
		String result = sb.toString();
		String res = null;
		try {
			byte[] inputBytes = result.getBytes("8859_1");
			res = new String(inputBytes, "UTF-8");
		} catch (Exception e) {
		}
		return res;
	}

	 public static byte[] str2Bcd(String asc) {  
	        int len = asc.length();  
	        int mod = len % 2;  
	        if (mod != 0) {  
	            asc = "0" + asc;  
	            len = asc.length();  
	        }  
	        byte abt[] = new byte[len];  
	        if (len >= 2) {  
	            len = len / 2;  
	        }  
	        byte bbt[] = new byte[len];  
	        abt = asc.getBytes();  
	        int j, k;  
	        for (int p = 0; p < asc.length() / 2; p++) {  
	            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {  
	                j = abt[2 * p] - '0';  
	            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {  
	                j = abt[2 * p] - 'a' + 0x0a;  
	            } else {  
	                j = abt[2 * p] - 'A' + 0x0a;  
	            }  
	            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {  
	                k = abt[2 * p + 1] - '0';  
	            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {  
	                k = abt[2 * p + 1] - 'a' + 0x0a;  
	            } else {  
	                k = abt[2 * p + 1] - 'A' + 0x0a;  
	            }  
	            int a = (j << 4) + k;  
	            byte b = (byte) a;  
	            bbt[p] = b;  
	        }  
	        return bbt;  
	    }  
	
}
