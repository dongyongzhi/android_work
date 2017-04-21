package com.yfcomm.pos.utils;

public class ByteUtils {

	private static final char[] HEX={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	
	/**
	 * 字节转换成HEX 字符串
	 * @param data
	 * @return
	 */
	public static String byteToHex(byte data){
		return String.valueOf(HEX[(data & 0xF0) >> 4])+ String.valueOf(HEX[data & 0x0F]);
	}
	/**
	 * 字节转换成HEX 字符串
	 * @param raw
	 * @return
	 */
	public static String byteToHex(byte... raw) {
		if(raw!=null) {
			return byteToHex(raw,0,raw.length);
		} else {
			return null;
		}
	}
	
	/**
	 * 字节转换成HEX 字符串
	 * @param raw
	 * @param offset
	 * @param count
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
			hex.append(HEX[(b & 0xF0) >> 4]).append(HEX[b & 0x0F]);
		}
		return hex.toString();
	}
	
	/**
	 * 打印 内容
	 * @param raw
	 * @param offset
	 * @param count
	 * @return
	 */
	public static String printBytes(byte[] raw,int offset,int count) {
		if (raw == null) {
			return " waring data is null!";
		}
		final StringBuilder hex = new StringBuilder();
		int len = raw.length;
		
		offset = offset>(len-1) ? len-1 : offset;
		
		int end = offset + count;
		end = end >len ? len : end;
		for(int i=offset;i<end; i++){
		    byte b = raw[i];
			hex.append(HEX[(b & 0xF0) >> 4]).append(HEX[b & 0x0F]).append(" ");
		}
		return hex.toString();
	}
	public static String printBytes(byte[] raw){
		if(raw!=null) {
			return printBytes(raw,0,raw.length);
		} else {
			return null;
		}
	}
	
	/**
	 * Hex 字符串转换成字节数组
	 * @param hex
	 * @return
	 */
	public static byte[] hexToByte(String hex){
		if(hex==null || "".equals(hex))
			return null;
		
		hex = hex.replaceAll(" ", "");
		
		int len = hex.length()/2;
		byte[] data = new byte[len];
		int offset=0;
		int at=0;
		int at1=0;
		for(int i=0;i<len;i++){
			offset  = i*2;
			at = asciiToHex(hex.charAt(offset));
			at1 = asciiToHex(hex.charAt(offset+1)) & 0xFF;
			data[i] = (byte)( at<<4 |at1);
		}
		return data;
	}
	
	private static int asciiToHex(int asc) {
		if(asc<=57) {
			return asc-48;
		} else if(asc<=70) {
			return asc-55;
		} else {
			return asc-87;
		}
	}
	
	/**
	 * BCD 转字符串
	 * @param data
	 * @param offset
	 * @param len
	 * @return
	 */
	public static String bcdToString(byte[] data,int offset,int len){
		StringBuilder sb = new StringBuilder(len *2 );
		int end = offset + len;
		for(int i=offset;i<end;i++){
			sb.append((data[i] & 0xF0)>>4).append(data[i] & 0x0F);
 		}
		return sb.toString();
	}
	
	/**
	 * 右靠BCD 
	 * @param value
	 * @return
	 */
	public static byte[] toAlignRightBcd(String value){
		byte[] buf = new byte[Math.round((float)(value.length()/(2.0)))];
		int charpos = 0;
		int bufpos = 0;
		if (value.length() % 2 == 1) {
			buf[0] = (byte)(value.charAt(0) - 48);
			charpos = 1;
			bufpos = 1;
		}
		while (charpos < value.length()) {
			buf[bufpos] = (byte)(((value.charAt(charpos) - 48) << 4)
					| (value.charAt(charpos + 1) - 48));
			charpos += 2;
			bufpos++;
		}
		
		return buf;
	}
	
	/**
	 * 左靠BCD 
	 * @param value
	 * @return
	 */
	public static byte[] toAlignLeftBcd(String value) {
		byte[] buf = new byte[Math.round((float)(value.length()/(2.0)))];
	 
		int len = value.length() / 2;
		for(int i=0; i<len ;i++) {
			buf[i] = (byte)(((value.charAt(i*2) - 48) << 4) | (value.charAt(i*2 + 1) - 48));
		}
		
		if (value.length() % 2 == 1) {
			buf[len] =(byte)( (value.charAt(value.length()-1)  - 48) << 4);
		}
		return buf;
	}
	
	
	/**
	 * 2位byte 转换成 int   高前低后
	 * @param bit1
	 * @param bit2
	 * @return
	 */
	public static int byteToInt(byte bit1,byte bit2) {
		return ((bit1 & 0xFF)<< 8) | (bit2 & 0xFF);
	}
	
	public static int byteToInt(byte value) {
		return value & 0xFF;
	}
	
	/**
	 * 获取 2字节int  高前低后
	 * @param value
	 * @return
	 */
	public static byte[] unsignedShort(int value) {
		byte[] data = new byte[2];
		data[0] = (byte)(value>>8);
		data[1] = (byte)(value);
		return data;
	}
}
