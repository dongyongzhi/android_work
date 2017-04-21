package com.yfcomm.mpos.utils;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * des 加解密
 * @author qinchuan
 *
 */
public class DesUtils {

	private final static String DES = "DES";
	
	public final static String CBC = "CBC";
	public final static String ECB = "ECB"; 
	
	private final static String DESede = "DESede";
	
    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] doEncrypt(byte[] data, byte[] key,byte[] lv,String model) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance("DES/"+model+"/NoPadding");
 
        // 用密钥初始化Cipher对象
        if(CBC.equals(model)) {
        	cipher.init(Cipher.ENCRYPT_MODE, securekey,lv == null ? new IvParameterSpec(new byte[8]) : 
        		new IvParameterSpec(lv));
        } else {
        	cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        }
 
        return cipher.doFinal(data);
    }
    
     
    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] doDecrypt(byte[] data, byte[] key,byte[] lv,String model) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();
 
        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);
 
        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/"+model+"/NoPadding");
 
        // 用密钥初始化Cipher对象
        if(CBC.equals(model)) {
        	cipher.init(Cipher.DECRYPT_MODE, securekey,lv == null ? new IvParameterSpec(new byte[8]) : 
        		new IvParameterSpec(lv));
        } else {
        	cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        }
        return cipher.doFinal(data);
    }
    
    /**
     * 加密
     * @param data
     * @param key
     * @param model
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, byte[] key,byte[] lv,String mode) throws Exception {
    	if(key.length == 8) {
    		return doEncrypt(data,key,lv,mode);
    		
    	} else if(key.length == 16) {
    		//前8位密钥
    		byte[] preKey = new byte[8];
    		//后8位密钥
    		byte[] nextKey = new byte[8];
    		
    		System.arraycopy(key, 0, preKey, 0, 8);
    		System.arraycopy(key, 8, nextKey, 0, 8);
    		
    		//前8位加密
    		byte[] sec = doEncrypt(data,preKey,lv,mode);
    		//后8位解密
    		sec = doDecrypt(sec,nextKey,lv,mode);
    		//前8位再加密码
    		sec = doEncrypt(sec,preKey,lv,mode);
    		
    		return sec;
    	
    	} else {
    		return null;
    	}
    }
    
    /**
     * EBC 加密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptECB(byte[] data,byte[] key) throws Exception {
     	return encrypt(data,key,null,ECB);
    }
    
    /**
     * CBC 加密
     * @param data
     * @param key
     * @param lv
     * @return
     * @throws Exception
     */
    public static byte[] encryptCBC(byte[] data,byte[] key,byte[] lv) throws Exception {
    	return encrypt(data,key,lv,CBC);
    }
    
    /**
     * 加密 默认 ECB 模式
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data,byte[] key) throws Exception {
    	return encrypt(data,key,null,ECB);
    }
    
    /**
     * 解密
     * @param data
     * @param key
     * @param model
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key,byte[] lv,String model) throws Exception {
    	if(key.length == 8) {
    		return doDecrypt(data,key,lv,model);
    		
    	} else if(key.length == 16) {
    		//前8位密钥
    		byte[] preKey = new byte[8];
    		//后8位密钥
    		byte[] nextKey = new byte[8];
    		
    		System.arraycopy(key, 0, preKey, 0, 8);
    		System.arraycopy(key, 8, nextKey, 0, 8);
    		
    		//前8位解密
    		byte[] sec = doDecrypt(data,preKey,lv,model);
    		//后8位加密
    		sec = doEncrypt(sec,nextKey,lv,model);
    		//前8位再
    		sec = doDecrypt(sec,preKey,lv,model);
    		
    		return sec;
    	
    	} else {
    		return null;
    	}
    }
    
    public static byte[] decryptECB(byte[] data, byte[] key) throws Exception {
    	return decrypt(data,key,null,ECB);
    }
    
    public static byte[] decryptCBC(byte[] data, byte[] key,byte[] lv) throws Exception {
    	return decrypt(data,key,lv,CBC);
    }
    
    /**
     * 解密 默认 ECB
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
    	return decrypt(data,key,null,ECB);
    }
    
    /**
     * 生成 des 密钥
     * @return
     * @throws Exception
     */
    public static byte[] generateDesKey() throws Exception {
    	SecureRandom sr = new SecureRandom();
    	KeyGenerator kg = KeyGenerator.getInstance(DES);
    	kg.init(sr);
		// 生成密钥
		Key key = kg.generateKey();
		return key.getEncoded();
    }
    
    /**
     * 生成 des 密钥
     * @return
     * @throws Exception
     */
    public static byte[] generateDesKey16() throws Exception {
    	SecureRandom sr = new SecureRandom();
    	KeyGenerator kg = KeyGenerator.getInstance(DESede);
    	kg.init(sr);
		// 生成密钥
		Key key = kg.generateKey();
		byte[] data = key.getEncoded();
		byte[] result = new byte[16];
		System.arraycopy(data, 0, result, 0, 16);
		return result;
    }
}
