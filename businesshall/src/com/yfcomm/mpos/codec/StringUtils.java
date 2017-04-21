package com.yfcomm.mpos.codec;

import java.util.Locale;


public class StringUtils {
	
	public static final String EMPTY = "";
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
			return String.format(Locale.getDefault(), String.format("%%-%ds", length), value); // 字母向左靠，右部多余部分填空格。
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
	
	/**
	 * 是否空字符串
	 * @param value
	 * @return
	 */
	public static boolean isEmpty(String value) {
		return value==null || EMPTY.equals(value);
	}
}
