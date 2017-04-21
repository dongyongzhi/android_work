/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.mfs.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Fredrik
 */
public class SHA1 {
    
    //internal hash values
    private int[] H;
    //the padded message as 32-bit words
    private int[] M32;
    //the message used in the rounds
    private int[] W;
    //the round constants
    private int[] K;

    public SHA1(){
        W=new int[80];
        H=new int[5];
        K=new int[80];
        for(int i=0;i<20;i++)
            K[i]=0x5a827999;
        for(int i=20;i<40;i++)
            K[i]=0x6ed9eba1;
        for(int i=40;i<60;i++)
            K[i]=0x8f1bbcdc;
        for(int i=60;i<80;i++)
            K[i]=0xca62c1d6;
    }
    
    public byte[] hash(byte[] message){
        //calculates the hash of the message
        byte[] hashValue=new byte[20];
        //pad the message
        initM(message);
        
        //initiate internal hash value
        initH();
        
        //calculate the number of 512-bit block for the padded message 
       int M = M32.length/16;
       
      
        int a,b,c,d,e,T;
        for(int i=0; i<M; i++){
        //repeat for each 512-bit block in M
        	
        		//initW
        		initW(i);
        		
        		//init a,b,c,d,e
        		a = H [0];
        		b = H [1];
        		c = H [2];
        		d = H [3];
        		e = H [4];
            
        		//run 80 iterations of the round function
        		for(int t=0; t<=79; t++){
        				T = ROTL(a,5) + ft(b,c,d,t) + e + K[t] + W[t];
        				e = d;
                        d = c;
                        c = ROTL(b, 30);
                        b = a;
                        a = T;
        		}
        		
        		//update H
        		H[0] += a;
        		H[1] += b;
        		H[2] += c;
        		H[3] += d;
        		H[4] += e;
        		
       }
       
        //use H to form hashvalue
       	for(int i=0; i<5; i++ ){
       		for(int j=0; j<4; j++){
       			hashValue[i*4+j]=  (byte) ((H[i]<<j)>>3);
       		}
    	}
        //for(int i=0;i<20;i++)
          // hashValue[i]=0;
        return hashValue;
    }
    


	private void initM(byte[] message){
        //pad the message and write it to W
        int messageLength8=message.length; //the number of bytes in the message
        //calculates the number of bytes to pad
        int padLength=(64-((messageLength8+8)%64));
        int totalLength8=messageLength8+padLength+8;
        byte[] temp= new byte[totalLength8];
        //pad the message and store in temp
        for(int i=0;i<message.length;i++)
            temp[i]=message[i];
        temp[message.length]=(byte)0x80;
        for(int i=message.length+1;i<totalLength8;i++)
            temp[i]=0;
        //write the length
        long bitLength=message.length*8;
        for(int i=1;i<=8;i++){
            temp[totalLength8-i]=(byte)(bitLength&0xff);
            bitLength>>=8;
        }
       
        //copy temp to M32
        int totalLength32=totalLength8/4;
        M32=new int[totalLength32];
        for(int i=0;i<totalLength32;i++){
            M32[i]=0;
            //copy four bytes and write to 32 bit integer
            for(int j=0;j<4;j++){
                M32[i]<<=8;
                M32[i]|=((int)temp[4*i+j]&0xff);
            }
        }
    }
    
    private void initH(){
        H[0]=0x67452301;
        H[1]=0xefcdab89;
        H[2]=0x98badcfe;
        H[3]=0x10325476;
        H[4]=0xc3d2e1f0;
    }
    
    private int ROTL(int x,int n){
        int temp=((x>>>(32-n))|(x<<n));
        //System.out.printf("ROTL %d\t%08x\t%08x\t%08x\t%08x\n",n,x,(x>>>n),(x<<n),temp);
        return temp;
    }
    
    private void initW(int n){
        for(int i=0;i<16;i++)
            W[i]=M32[n*16+i];
        for(int i=16;i<80;i++){
            int temp=W[i-3]^W[i-8]^W[i-14]^W[i-16];
            W[i]=ROTL(temp,1);
        }
    }
    
    private int ft(int x,int y,int z,int t){
        if(t<20)
            return ((x&y)^(~x&z));
        if(t<40)
            return (x^y^z);
        if(t<60)
            return ((x&y)^(x&z)^(y&z));
        return (x^y^z);
    }

}
