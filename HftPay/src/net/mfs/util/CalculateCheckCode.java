package net.mfs.util;

public class CalculateCheckCode {

	public static byte xorc(byte[] data, int len){
		byte checksum = 0;
		
		for(int i=0;i<len;i++)
			checksum = (byte) (checksum | data[i]);
		return checksum;
	}
	
	
	public static byte lrc(byte[] data, int len){
		int checksum = 0;
		
		for(int i=0;i<len;i++)
			checksum = ((checksum & 0xff) + (data[i] & 0xff));
		
		return (byte) (256 - (checksum & 0xFF));
	}
}
