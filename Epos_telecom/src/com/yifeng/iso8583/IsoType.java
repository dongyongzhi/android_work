package com.yifeng.iso8583;

import java.util.Date;

public enum IsoType {

	/**固定长度    数值，右靠，首位有效数字前充零。若表示金额，则最右二位为角分。 */
	N(true, 0),
	/** 固定长度   字母、数字和/或特殊符号向左靠，右部多余部分填空格 */
	ALPHA(true, 0),
	/** 数值型可变长度类型，（压缩成bcd）*/
	N_LLVAR(false,0),
	
	/** 数值型可变长度类型，（压缩成bcd）*/
	N_LLLVAR(false,0),
	
	/** 二磁道信息*/
	Z_LLVAR(false,0),
	
	/** 三磁道信息*/
	Z_LLLVAR(false,0),
	
	/**ascii 型可变长*/
	ALPHA_LLVAR(false,0),
	
	/**ascii 型可变长*/
	ALPHA_LLLVAR(false,0),
	
	/**金额 固定长度  12 */
	AMOUNT(false, 12),
	/** A date in format MMddHHmmss */
	DATE10(false, 10),
	/** A date in format MMdd */
	DATE4(false, 4),
	/** A date in format yyMM */
	DATE_EXP(false, 4),
	/** Time of day in format HHmmss */
	TIME(false, 6),
	 
	/** 二进制bit 位。 */
	B(true, 0),
	/** Similar to LLVAR but holds byte arrays instead of strings. */
	LLBIN(false, 0),
	/** Similar to LLLVAR but holds byte arrays instead of strings. */
	LLLBIN(false, 0);
	
	private boolean needsLen;
	private int length;

	IsoType(boolean flag, int l) {
		needsLen = flag;
		length = l;
	}

	public boolean needsLength() {
		return needsLen;
	}

	public int getLength() {
		return length;
	}
	
	/**
	 * 格式化时间
	 * @param value  时间
	 * @return
	 */
	public String format(Date value) {
		if (this == DATE10) {
			return String.format("%Tm%<Td%<TH%<TM%<TS", value);
		} else if (this == DATE4) {
			return String.format("%Tm%<Td", value);
		} else if (this == DATE_EXP) {
			return String.format("%Ty%<Tm", value);
		} else if (this == TIME) {
			return String.format("%TH%<TM%<TS", value);
		}
		throw new IllegalArgumentException("Cannot format date as " + this);
	}
	
	/**
	 *  格式化值  
	 * @param value  数据内容
	 * @param length 长度
	 * @return
	 */
	public String format(String value, int length) {
		if (this == ALPHA) {  //定度 字符串
	    	if (value == null) {
	    		value = "";
	    	}
	        if (value.length() > length) {
	            return value.substring(0, length);
	        } else if (value.length() == length) {
	        	return value;
	        } else {
	        	return String.format(String.format("%%-%ds", length), value); //字母向左靠，右部多余部分填空格。
	        }
		} else if (this == N) {  //定长数字类型
	        char[] c = new char[length];
	        char[] x = value.toCharArray();
	        if (x.length > length) {
	        	throw new IllegalArgumentException("Numeric value is larger than intended length: " + value + " LEN " + length);
	        }
	        int lim = c.length - x.length;
	        for (int i = 0; i < lim; i++) {  //数字靠右，左补0  
	            c[i] = '0';
	        }
	        System.arraycopy(x, 0, c, lim, x.length);
	        return new String(c);
		}else if (this == AMOUNT) {   //定长金额 
				return IsoType.N.format(value, 12);
		} else if (this == B) {      //定长 BINARY

	    	if (value == null) {
	    		value = "";
	    	}
	        if (value.length() > length) {
	            return value.substring(0, length);
	        }
	        char[] c = new char[length];
	        int end = value.length();
	        if (value.length() % 2 == 1) {
	        	c[0] = '0';
		        System.arraycopy(value.toCharArray(), 0, c, 1, value.length());
		        end++;
	        } else {
		        System.arraycopy(value.toCharArray(), 0, c, 0, value.length());
	        }
	        for (int i = end; i < c.length; i++) {
	            c[i] = '0';
	        }
	        return new String(c);

		} else{
			return value;
		}
	}
	
}
