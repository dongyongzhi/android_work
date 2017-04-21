package com.hftcom.utils;

/*
 --------------------------------------------**********--------------------------------------------

 该算法于1977年由美国麻省理工学院MIT(Massachusetts Institute of Technology)的Ronal Rivest，Adi Shamir和Len Adleman三位年轻教授提出，并以三人的姓氏Rivest，Shamir和Adlernan命名为RSA算法，是一个支持变长密钥的公共密钥算法，需要加密的文件快的长度也是可变的!

 所谓RSA加密算法，是世界上第一个非对称加密算法，也是数论的第一个实际应用。它的算法如下：

 1.找两个非常大的质数p和q（通常p和q都有155十进制位或都有512十进制位）并计算n=pq，k=(p-1)(q-1)。

 2.将明文编码成整数M，保证M不小于0但是小于n。

 3.任取一个整数e，保证e和k互质，而且e不小于0但是小于k。加密钥匙（称作公钥）是(e, n)。

 4.找到一个整数d，使得ed除以k的余数是1（只要e和n满足上面条件，d肯定存在）。解密钥匙（称作密钥）是(d, n)。

 加密过程： 加密后的编码C等于M的e次方除以n所得的余数。

 解密过程： 解密后的编码N等于C的d次方除以n所得的余数。

 只要e、d和n满足上面给定的条件。M等于N。

 --------------------------------------------**********--------------------------------------------
 */
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;



public class Rsa {
	/** 指定加密算法为DESede */
	private static String ALGORITHM = "RSA/ECB/PKCS1Padding";
	/** 指定key的大小 */
	private static int KEYSIZE = 1024;
	
	private static Rsa mRsa = null;
	private Rsa() {
		
	}
	
	public static Rsa getInstance() {
		if (mRsa == null) {
			mRsa = new Rsa();
		}
		return mRsa;
	}
	

	/**
	 * 生成密钥对
	 */
	public Map<String, String> generateKeyPair() throws Exception {
		/** RSA算法要求有一个可信任的随机数源 */
		SecureRandom sr = new SecureRandom();
		/** 为RSA算法创建一个KeyPairGenerator对象 */
		KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
		/** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
		kpg.initialize(KEYSIZE, sr);
		/** 生成密匙对 */
		KeyPair kp = kpg.generateKeyPair();
		/** 得到公钥 */
		Key publicKey = kp.getPublic();
		byte[] publicKeyBytes = publicKey.getEncoded();
		String pub = new String(Base64.encode(publicKeyBytes, Base64.NO_WRAP), "GBK");
		/** 得到私钥 */
		Key privateKey = kp.getPrivate();
		byte[] privateKeyBytes = privateKey.getEncoded();
		String pri = new String(Base64.encode(privateKeyBytes, Base64.NO_WRAP), "GBK");
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("publicKey", pub);
		map.put("privateKey", pri);
		return map;
	}
	
	/**
	 * 加密方法 source： 源数据
	 */
	public String encrypt(String source, String publicKey)
			throws Exception {
		Key key = getPublicKey(publicKey);
		/** 得到Cipher对象来实现对源数据的RSA加密 */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] b = source.getBytes();
		/** 执行加密操作 */
		byte[] b1 = cipher.doFinal(b);
		return new String(Base64.encode(b1, Base64.NO_WRAP), "GBK");
	}

	/**
	 * 解密算法 cryptograph:密文
	 */
	public String decrypt(String cryptograph, String privateKey)
			throws Exception {
		Key key = getPrivateKey(privateKey);
		/** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] b1 = Base64.decode(cryptograph.getBytes(), Base64.NO_WRAP);
		/** 执行解密操作 */
		byte[] b = cipher.doFinal(b1);
		return new String(b);
	}
	 /** 
     * 得到公钥 
     * @param key 密钥字符串（经过base64编码） 
     * @throws Exception 
     */  
    public PublicKey getPublicKey(String key) throws Exception {  
          X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decode(key.getBytes(), Base64.NO_WRAP));  
          KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
          PublicKey publicKey = keyFactory.generatePublic(keySpec);  
          //YPLog.e("publicKey : ", publicKey.toString());
          return publicKey;  
    }  
    /** 
     * 得到私钥 
     * @param key 密钥字符串（经过base64编码） 
     * @throws Exception 
     */  
    public PrivateKey getPrivateKey(String key) throws Exception {  
          PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(key.getBytes(), Base64.NO_WRAP));  
          KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
          PrivateKey privateKey = keyFactory.generatePrivate(keySpec);  
          return privateKey;  
    }
    

}
