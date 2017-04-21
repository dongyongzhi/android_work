package net.mfs.util;
import java.math.BigInteger;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author deadknight
 */
public class RC4 {
    
    /** Creates a new instance of RC4 */
    public RC4() {}
    public static byte[] encrypt(byte[] plaintext,byte[] keyBytes)
    {
        byte[] e = null;
        try
        {
            Key key = new SecretKeySpec(keyBytes,"RC4");
            Cipher enCipher = Cipher.getInstance("RC4");
            enCipher.init(Cipher.ENCRYPT_MODE,key);
            e = enCipher.doFinal(plaintext);           
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return e;
    }
    public static byte[] decrypt(byte[] ciphertext,byte[] keyBytes)
    {
        byte de[] = null;
        try
        {
            Key key = new SecretKeySpec(keyBytes,"RC4");
            Cipher deCipher = Cipher.getInstance("RC4");
            deCipher.init(Cipher.DECRYPT_MODE,key);
            de = deCipher.doFinal(ciphertext);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        } 
        return de;
        
    }

    public static Key getKey()
    {
        Key key = null;
        try
        {
            SecureRandom sr = new SecureRandom();
            KeyGenerator kg = KeyGenerator.getInstance("RC4");
            kg.init(128,sr);
            key = kg.generateKey(); 
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return key;
    }
    public static String keyGet()
    {
        Key k = RC4.getKey();
        byte[] b = k.getEncoded();
        BigInteger big = new BigInteger(b);
        String s = big.toString();
        return s;
    }

}
