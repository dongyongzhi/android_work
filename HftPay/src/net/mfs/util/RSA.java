/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.mfs.util;
import java.util.Random;
import java.math.BigInteger;
/**
 *
 * @author Fredrik
 */

/**
 *
 * @author Fredrik
 */
public class RSA {
    
    private BigInteger n;
    private BigInteger e;
    private BigInteger d;
    private BigInteger Z;
    //initiate RSA with length of n equal to bitlength 
    public RSA(int bitLength){
        Random rndGen=new Random(1);
        //create p and q, size bitlength/2
        BigInteger p = new BigInteger(128,100,rndGen);
        
        
        BigInteger q = p.nextProbablePrime();
        
        
        //calculate n
        n = p.multiply(q);
        
        //calculate fi(n)=(p-1)(q-1)
         Z = (p.subtract(BigInteger.valueOf(1))).multiply((q.subtract(BigInteger.valueOf(1)))) ;
        
        //create e
        for(;;){        	
        	BigInteger temp = Z.divide(BigInteger.valueOf(2));
        	 e = temp.nextProbablePrime();
        	if(e.compareTo(BigInteger.valueOf(1))==1
        			&&Z.compareTo(e)==1
        			&&(e.gcd(Z)).compareTo(BigInteger.valueOf(1))==0	)
        		break;
        }
        
        //calculate d
         d = calculateD();
        
    }
    
    private BigInteger calculateD(){
    	  BigInteger t0 = BigInteger.valueOf(0),t1 = BigInteger.valueOf(1),t2 = BigInteger.valueOf(-1);
    	  BigInteger r0 = Z, m = r0,r1 = e ,r2 = BigInteger.valueOf(-1);
    	  BigInteger[] t3;
    	  do{    		  
    		  BigInteger q = r0.divide(r1);
    		  r2 = r0.subtract(r1.multiply(q));
    		  if(r2.compareTo(BigInteger.valueOf(0))==0)break;
    		  t2 = t0.subtract(t1.multiply(q)) ;
    		  while(t2.compareTo(BigInteger.valueOf(0))==-1){
    			  t2 = t2.add(m);
    		  }
    		  if(t2.compareTo(m)==1){ 
    			  t3 = t2.divideAndRemainder(m);
    			  t2 = t3[1];
    		  }    
    		  r0 = r1;
    		  r1 = r2;
    		  t0 = t1;
    		  t1 = t2;
    	  }while(r2.compareTo(BigInteger.valueOf(0))!=0);
    	  if(r1.compareTo(BigInteger.valueOf(1))!=0){
    	   return BigInteger.valueOf(0);
    	  }
    	  else{
    	   return t2;
    	  }
    }


	
	public BigInteger encrypt(BigInteger bigtemp){
       
		BigInteger ciphertext;    
      
        	ciphertext= ((bigtemp).modPow(e, n));
        
        
        return ciphertext;
    }
    
    public BigInteger decrypt(BigInteger bigtemp){
       
    	BigInteger plaintext;
    	
    		plaintext= ((bigtemp).modPow(d, n));
    	
    	
        return plaintext;
    }
    
   /* public BigInteger[] getPublicExponent(){
    	BigInteger[] eBytes=null;
        return eBytes;
    }
    
    public BigInteger[] getN(){
    	BigInteger[] nBytes=null;
        return nBytes;
    }*/
    
    

}
