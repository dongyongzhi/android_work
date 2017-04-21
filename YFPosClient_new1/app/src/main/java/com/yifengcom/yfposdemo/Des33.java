package com.yifengcom.yfposdemo;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import android.util.Base64;

/**
 * 3DES加密工具类
 * 
 * @author liufeng
 * @date 2012-10-11
 */
public class Des33 {
	// 密钥
	// private final static String secretKey =
	// "F982EF6B71FB0C7DF07EE218C57511FCF982EF6B71FB0C7D";
	// private final static String secretKey =
	// "4bb57add409f4ca28a5235161167ffee4bb57add409f4ca2";
	private final static String secretKey = "6091aab92a16a0808d5243221167ffee6091aab92a16a080";
	// 向量
	private final static byte[] keyiv = { 0, 1, 2, 3, 4, 5, 6, 7 };
	// 加解密统一使用的编码方式
	private final static String encoding = "utf-8";

	// 秘钥
	private static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = (byte) (Integer.decode("0x" + src.substring(i * 2, m)
					+ src.substring(m, n)) & 0xFF);
		}
		return ret;
	}

	/**
	 * 3DES加密
	 * 
	 * @param plainText
	 *            普通文本
	 * @return
	 * @throws Exception
	 */
	public static String encode(String plainText, String secretKey)
			throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(hexStr2Bytes(secretKey));
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede" + "/CBC/NoPadding"); // PKCS5Padding
		IvParameterSpec ips = new IvParameterSpec(keyiv);
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		byte[] encryptData = cipher.doFinal(FormateData(plainText));
		return Base64.encodeToString(encryptData, Base64.DEFAULT);
	}

	/**
	 * 密码加密时，填充字符串为8的倍数。
	 * <p>
	 * （此方法在模式是CBC模式，填充方式为NoPadding方式的情况下，用字节零填充.）
	 * 
	 * @param str
	 *            密码
	 * @return 加密后的密文
	 */
	public static byte[] FormateData(String str)
			throws UnsupportedEncodingException {

		if ((str.length() % 8) == 0) {
			return str.getBytes(encoding);
		}

		byte[] data = str.getBytes(encoding);
		int size = 8 - data.length % 8;
		System.out.println("data.length~~" + data.length);
		System.out.println("需要补~~" + size);
		byte[] arr = new byte[data.length + size];
		System.out.println("arr个~~" + arr.length);
		int i = 0;
		for (; i < data.length; i++) {
			arr[i] = data[i];
		}
		for (int j = 0; j < size; j++, i++) {
			System.out.println("i===" + i);
			arr[i] = new byte[] { 0 }[0];
		}
		return arr;
	}

	/**
	 * 3DES解密
	 * 
	 * @param encryptText
	 *            加密文本
	 * @return
	 * @throws Exception
	 */
	public static String decode(String encryptText) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(hexStr2Bytes(secretKey));
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/CBC/NoPadding");
		IvParameterSpec ips = new IvParameterSpec(keyiv);
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		System.out.println("encryptText~~~" + encryptText);
		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText,
				Base64.DEFAULT));
		String bOut = byte2Ucs2(decryptData, 0, decryptData.length);
		return bOut;
		// return new String(decryptData,encoding);
	}

	/**
	 * 3DES解密
	 * 
	 * @param encryptText
	 *            加密文本
	 * @return
	 * @throws Exception
	 */
	public static String decode1(String encryptText, String secretKey)
			throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(hexStr2Bytes(secretKey));
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/CBC/NoPadding");
		IvParameterSpec ips = new IvParameterSpec(keyiv);
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		byte[] decryptData = cipher.doFinal(Base64.decode(encryptText,
				Base64.DEFAULT));
		return new String(FormateByte(decryptData), encoding);
	}

	/**
	 * 密码解密时，将填充的字节零去掉！
	 * <p>
	 * (此方法只在模式是CBC模式，填充方式为NoPadding方式，用字节零填充 的情况下使用。)
	 * 
	 * @param arr
	 *            密文字节组
	 * 
	 * @return 密码字节组
	 */
	public static byte[] FormateByte(byte[] arr) {

		int i = 0;
		for (; i < arr.length; i++) {
			if (arr[i] == new Byte("0")) {
				break;
			}
		}
		byte[] result = new byte[i];
		for (int j = 0; j < i; j++) {
			result[j] = arr[j];
		}
		return result;
	}

	public static String byte2Ucs2(byte[] data, int iPos, int iLen) {
		try {
			return (new String(data, iPos, iLen, "UTF-16LE")).trim();
		} catch (UnsupportedEncodingException e) {
			System.out.println("转换UTF8失败");
		}
		return "";
	}

}