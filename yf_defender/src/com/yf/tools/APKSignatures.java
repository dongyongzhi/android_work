package com.yf.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class APKSignatures{
	
	private static List<String> getSignaturesFromApk(File file) throws IOException {  
        List<String> signatures=new ArrayList<String>();  
        JarFile jarFile=new JarFile(file);  
        try {  
            JarEntry je=jarFile.getJarEntry("AndroidManifest.xml");  
            byte[] readBuffer=new byte[8192];  
            Certificate[] certs=loadCertificates(jarFile, je, readBuffer);  
            if(certs != null) {  
                for(Certificate c: certs) {  
                    String sig=toCharsString(c.getEncoded());  
                    signatures.add(sig);  
                }  
            }  
        } catch(Exception ex) {  
        }  
        return signatures;  
    }   
	
     private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {  
	        try {  
	            InputStream is=jarFile.getInputStream(je);  
	            while(is.read(readBuffer, 0, readBuffer.length) != -1) {  
	            }  
	            is.close();  
	            return je != null ? je.getCertificates() : null;  
	        } catch(IOException e) {  
	        }  
	        return null;  
	    }  
	   
	   private static String toCharsString(byte[] sigBytes) {  
	        byte[] sig=sigBytes;  
	        final int N=sig.length;  
	        final int N2=N * 2;  
	        char[] text=new char[N2];  
	        for(int j=0; j < N; j++) {  
	            byte v=sig[j];  
	            int d=(v >> 4) & 0xf;  
	            text[j * 2]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));  
	            d=v & 0xf;  
	            text[j * 2 + 1]=(char)(d >= 10 ? ('a' + d - 10) : ('0' + d));  
	        }  
	        return new String(text);  
	    }  
    
}