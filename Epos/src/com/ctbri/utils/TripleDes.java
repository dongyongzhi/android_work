package com.ctbri.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;


public class TripleDes {
	
	public static final int MODE_EBC = 0;
	public static final int MODE_CBC = 1;
	private static TripleDes mInstance = null;
	private int mMode = 0;
	private Cipher mCipher = null;
	private SecretKey mSecurekey = null;
	private SecureRandom mSecureRandom = null;
	
	private TripleDes() {
	}
	
	public static TripleDes getInstance() {
		if (mInstance == null) {
			mInstance = new TripleDes();
		}
		return mInstance;
	}
	
	public byte[] encryptEBC(String key, String data, int mode) {
		return encryptEBC(key.getBytes(), data.getBytes(), mode);
	}
	
	public byte[] encryptEBC(byte[] key, byte[] data, int mode) {
		init(key, mode);
		byte[] result = null;
		try {
			mCipher.init(Cipher.ENCRYPT_MODE, mSecurekey, mSecureRandom);
			result = mCipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public byte[] decryptEBC(String key, String data, int mode) {
		return decryptEBC(getKeyAsBytes(key), data.getBytes(), mode);
	}
	
	public byte[] decryptEBC(byte[] key, byte[] data, int mode) {
		init(key, mode);
		byte[] result = null;
		try {
			mCipher.init(Cipher.DECRYPT_MODE, mSecurekey, mSecureRandom);
			result = mCipher.doFinal(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private void init(byte[] key, int mode) {
		try {
			
			mSecureRandom = new SecureRandom();
			DESedeKeySpec dks = new DESedeKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
			mSecurekey =keyFactory.generateSecret(dks);
			if (mMode == MODE_EBC) {
				mCipher  = Cipher.getInstance("DESede");
			} else {
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static byte[] getKeyAsBytes(String key) {
	    byte[] keyBytes = new byte[24]; // a Triple DES key is a byte[24] array

	    for (int i = 0; i < key.length() && i < keyBytes.length; i++) 
	        keyBytes[i] = (byte) key.charAt(i);

	    return keyBytes;
	}
	
}

