package com.yifeng.iso8583;

import java.io.UnsupportedEncodingException;

/**
 * 字符串操作
 * 
 * @author Administrator
 * 
 */
public class StringUtils {
	static final String HEXES = "0123456789ABCDEF";

	
	public static String strToHex(String str){
		if(str==null)
			return null;
		byte[] raw = str.getBytes();
		return byteToHex(raw);
	}
	
	public static String byteToHex(byte data){
		return String.valueOf(HEXES.charAt((data & 0xF0) >> 4))+ String.valueOf(HEXES.charAt((data & 0x0F)));
	}
	
	/**
	 * byte[] 转换成 hex 字符串
	 * @param raw
	 * @return
	 */
	public static String byteToHex(byte[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final byte b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	
	/**
	 * byte[] 转换成 hex 字符串
	 * @param raw      数据内容
	 * @param offset   偏移量
	 * @param count    要转换的长度
	 * @return
	 */
	public static String byteToHex(byte[] raw,int offset,int count) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		
		offset = offset>(raw.length-1) ? 0 : offset;
		count = count> raw.length ? raw.length : count;
		int end = offset + count;
		
		for(int i=offset;i<end; i++){
		    byte b = raw[i];
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}
	
	/**
	 * 打印显示 byte <br/>
	 * 用于调试用 
	 * @param raw     数据内容
	 * @param offset  偏移量
	 * @param count   要转换的长度
	 * @return
	 */
	public static String printBytes(byte[] raw,int offset,int count) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder();
		
		offset = offset>(raw.length-1) ? raw.length-1 : offset;
		count = count> raw.length ? raw.length : count;
		int end = offset + count;
		for(int i=offset;i<end; i++){
		    byte b = raw[i];
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F))+" ");
		}
		return hex.toString();
	}
	
 
	public static String printBytes(byte... raw){
		return printBytes(raw,0,raw.length);
	}
	/**
	 * hex 字符串转换成 byte[]
	 * @param hex
	 * @return
	 */
	public static byte[] hexToBytes(String hex){
		if(hex==null || "".equals(hex))
			return null;
		hex = hex.toUpperCase();
		int len = hex.length()/2;
		byte[] data = new byte[len];
		int offset=0;
		for(int i=0;i<len;i++){
			offset  = i*2;
			data[i] = (byte)(((byte)(HEXES.indexOf(hex.charAt(offset)))<<4)  | (byte)HEXES.indexOf(hex.charAt(offset+1)));
		}
		return data;
	}
	
	/**
	 *  字符串转bcd 码   左靠 bcd
	 * @param str
	 * @return
	 */
	public static byte[] strToBCD(String str){
		if(str==null || "".equals(str))
			return null;
		int len =   (str.length() / 2) + (str.length() % 2);
		byte[] bcd = new byte[len];
		int offset=0;
		for(int i=0;i<len;i++){
			offset = i*2;
			if(offset>=(str.length()-1))
				bcd[i] = (byte)(HEXES.indexOf(str.charAt(offset))<<4);
			else
				bcd[i] =(byte)((byte)HEXES.indexOf(str.charAt(offset))<<4 | (byte)HEXES.indexOf(str.charAt(offset+1)));
		}
		return bcd;
	}
	/**
	 * bcd码转换成字符串
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String bcdToString(byte[] data,int offset,int len){
		StringBuilder sb = new StringBuilder(len *2 );
		int end = offset + len;
		for(int i=offset;i<end;i++){
			sb.append(data[i]>>4).append(data[i] & 0x0F);
		}
		return sb.toString();
	}
	
	public static String byteToStr(byte[] data,int offset,int len,String charset){
		int end = offset + len;
		int count = 1; //有效数据个数
		for(int i=offset;i<end;i++){
			if(data[i]=='\0')
				continue;
			 count ++;
		}
		try {
			return new String(data,offset,count,charset);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static String byteToStr(byte[] data,int offset,int len){
		int end = offset + len-1;
		int count = 0; //无效数据个数
	    for(int i=end;i>=offset;i--){
	    	if(data[i]=='\0')
	    		count ++;
	    	else
	    		break;
	    }
		return new String(data,offset,len - count);
	}
}
