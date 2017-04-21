package net.mfs.util;

import java.io.UnsupportedEncodingException;
 
public class StringUtil {
	
	//转为byte
    private static int by2int(int b)
    {
        return b & 0xff;
    }

    /*
     * UTF8字符串转为GB2312字符串
     * byte[] b={(byte) 0xE6, (byte) 0xB5, (byte) 0x8B, (byte) 0xE8, (byte) 0xAF, (byte) 0x95}; //UTF8字符串
     * byte[] c = ss.getBytes("GB2312"); //GBK字符串数组
     * 
    */
    public static String UTF82GB2312(byte buf[])
    {
        int len = buf.length;
        StringBuffer sb = new StringBuffer(len/2);
        for(int i =0; i<len;i++)
        {
            if(by2int(buf[i])<=0x7F) sb.append((char)buf[i]);
            else if(by2int(buf[i])<=0xDF && by2int(buf[i])>=0xC0)
            {
              int bh = by2int(buf[i] & 0x1F);
              int bl = by2int(buf[++i] & 0x3F);
              bl = by2int(bh << 6 | bl);
              bh = by2int(bh >> 2);
              int c = bh<<8 | bl;
              sb.append((char)c);
            } else if(by2int(buf[i])<=0xEF && by2int(buf[i])>=0xE0){
              int bh = by2int(buf[i] & 0x0F);
              int bl = by2int(buf[++i] & 0x3F);
              int bll = by2int(buf[++i] & 0x3F);
              bh = by2int(bh << 4 | bl>>2);
              bl = by2int(bl << 6 | bll);

              int c = bh<<8 | bl;
              
        //空格转换为半角
        if(c==58865){
            c = 32;
        }
        	sb.append((char)c);
              
            }
        }
        return sb.toString();
    }
    
    
    
   public static String gbToUtf8(String str) throws UnsupportedEncodingException {    
        StringBuffer sb = new StringBuffer();    
        for (int i = 0; i < str.length(); i++) {    
            String s = str.substring(i, i + 1);    
            if (s.charAt(0) > 0x80) {    
                byte[] bytes = s.getBytes("Unicode");    
                String binaryStr = "";    
                for (int j = 2; j < bytes.length; j += 2) {    
                    // the first byte    
                    String hexStr = charToHexChar(bytes[j + 1]);    
                    String binStr = getBinaryString(Integer.valueOf(hexStr, 16));    
                    binaryStr += binStr;    
                    // the second byte    
                    hexStr = charToHexChar(bytes[j]);    
                    binStr = getBinaryString(Integer.valueOf(hexStr, 16));    
                    binaryStr += binStr;    
                }    
                // convert unicode to utf-8    
                String s1 = "1110" + binaryStr.substring(0, 4);    
                String s2 = "10" + binaryStr.substring(4, 10);    
                String s3 = "10" + binaryStr.substring(10, 16);    
                byte[] bs = new byte[3];    
                bs[0] = Integer.valueOf(s1, 2).byteValue();    
                bs[1] = Integer.valueOf(s2, 2).byteValue();    
                bs[2] = Integer.valueOf(s3, 2).byteValue();    
                String ss = new String(bs, "UTF-8");    
                sb.append(ss);    
            } else {    
                sb.append(s);    
            }    
        }    
        return sb.toString();    
    }    
   
 //获取十六进制字符串
   public static String charToHexChar(byte b[]){
	   String s="";
	   for (byte c: b){
		   s += charToHexChar(c);
	   }
	   return s;
   }
   
   //获取十六进制字符串
    private static String charToHexChar(byte b) {    
        String hexStr = Integer.toHexString(b);    
        int m = hexStr.length();    
        if (m < 2) {    
            hexStr = "0" + hexStr;    
        } else {    
            hexStr = hexStr.substring(m - 2);    
        }    
        return hexStr;    
    }    
   
    //获取二进制字符串数
    public static String getBinaryString(int i) {    
        String binaryStr = Integer.toBinaryString(i);    
        int length = binaryStr.length();    
        for (int l = 0; l < 8 - length; l++) {    
            binaryStr = "0" + binaryStr;    
        }    
        return binaryStr;    
    }
    
    /** *//** 
     * 把16进制字符串转换成字节数组 
     * @param hex 
     * @return 
     */ 
 public static byte[] hexStringToByte(String hex) { 
     int len = (hex.length() / 2); 
     byte[] result = new byte[len]; 
     char[] achar = hex.toCharArray(); 
     for (int i = 0; i < len; i++) { 
      int pos = i * 2; 
      result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1])); 
     } 
     return result; 
 } 

 private static byte toByte(char c) { 
     byte b = (byte) "0123456789ABCDEF".indexOf(c); 
     return b; 
 } 

 /** *//** 
     * 把字节数组转换成16进制字符串 
     * @param bArray 
     * @return 
     */ 
 public static final String bytesToHexString(byte[] bArray) { 
     StringBuffer sb = new StringBuffer(bArray.length); 
     String sTemp; 
     for (int i = 0; i < bArray.length; i++) { 
      sTemp = Integer.toHexString(0xFF & bArray[i]); 
      if (sTemp.length() < 2) 
       sb.append(0); 
      sb.append(sTemp.toUpperCase()); 
     } 
     return sb.toString(); 
 } 



 /** *//** 
     * @函数功能: BCD码转为10进制串(阿拉伯数据) 
     * @输入参数: BCD码 
     * @输出结果: 10进制串 
     */ 
 public static String bcd2Str(byte[] bytes){ 
     StringBuffer temp=new StringBuffer(bytes.length*2); 

     for(int i=0;i<bytes.length;i++){ 
      temp.append((byte)((bytes[i]& 0xf0)>>>4)); 
      temp.append((byte)(bytes[i]& 0x0f)); 
     } 
     return temp.toString().substring(0,1).equalsIgnoreCase("0")?temp.toString().substring(1):temp.toString(); 
 } 

 /** *//** 
     * @函数功能: 10进制串转为BCD码 
     * @输入参数: 10进制串 
     * @输出结果: BCD码 
     */ 
 public static byte[] str2Bcd(String asc) { 
     int len = asc.length(); 
     int mod = len % 2; 

     if (mod != 0) { 
      asc = "0" + asc; 
      len = asc.length(); 
     } 

     byte abt[] = new byte[len]; 
     if (len >= 2) { 
      len = len / 2; 
     } 

     byte bbt[] = new byte[len]; 
     abt = asc.getBytes(); 
     int j, k; 

     for (int p = 0; p < asc.length()/2; p++) { 
      if ( (abt[2 * p] >= '0') && (abt[2 * p] <= '9')) { 
       j = abt[2 * p] - '0'; 
      } else if ( (abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) { 
       j = abt[2 * p] - 'a' + 0x0a; 
      } else { 
       j = abt[2 * p] - 'A' + 0x0a; 
      } 

      if ( (abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) { 
       k = abt[2 * p + 1] - '0'; 
      } else if ( (abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) { 
       k = abt[2 * p + 1] - 'a' + 0x0a; 
      }else { 
       k = abt[2 * p + 1] - 'A' + 0x0a; 
      } 

      int a = (j << 4) + k; 
      byte b = (byte) a; 
      bbt[p] = b; 
     } 
     return bbt; 
 } 
 /** *//** 
     * @函数功能: BCD码转ASC码 
     * @输入参数: BCD串 
     * @输出结果: ASC码 
     */ 
 public static String BCD2ASC(byte[] bytes) {
	 final char[] BToA = "0123456789abcdef".toCharArray() ;
     StringBuffer temp = new StringBuffer(bytes.length * 2); 

     for (int i = 0; i < bytes.length; i++) { 
      int h = ((bytes[i] & 0xf0) >>> 4); 
      int l = (bytes[i] & 0x0f);   
      temp.append(BToA[h]).append(BToA[l]); 
     } 
     return temp.toString() ; 
 } 

 public static String HexCharToChar(String s, String Code)
 {
	 if (s.length()<2) return "";
    if("0x".equals(s.substring(0, 2)))
    {
     s =s.substring(2);
    }
    byte[] baKeyword = new byte[s.length()/2];
    for(int i = 0; i < baKeyword.length; i++)
    {
       try
       {
        baKeyword[i] = (byte)(0xff & Integer.parseInt(s.substring(i*2, i*2+2),16));
       }
       catch(Exception e)
       {
        e.printStackTrace();
       }
    }
     
    try 
    {
     s = new String(baKeyword);//UTF-16le:Not
      
    } 
    catch (Exception e1) 
    {
     e1.printStackTrace();
    } 
    return s;
 }

 
 public static String UTF82GB2312(String value)
 {
 	try {
			return UTF82GB2312(value.getBytes("UTF8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
 }
 



}
