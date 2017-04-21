/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.mfs.util;

/**
 *
 * @author Fredrik
 */


/**
 *
 * @author Fredrik
 */
public class DES {
  
    //craetes a DES box and assign the key
    public DES(long key){
        genRoundKeys(key);
    }
   
     //the 16 roundKeys, stored in roundKeys[1]...roundKeys[16]
    //initiated in the constructor
    private long[] roundKeys;
    
    public long encrypt(long plaintext){
    	long temp, RL, ciphertext;
         //apply the IP table
    	long IPtext = IP(plaintext);
    	//System.out.printf("IP  %x\n",IPtext);
    	
        //initiate L and R
        long L = IPtext>>>32;
        //System.out.printf("L   %x\n",L);
        
        long R = (IPtext<<32)>>>32;
        //System.out.printf("R   %x\n",R);
        
        //run 16 rounds
       for(int i=1 ; i<=16 ; i++){
     	  temp = R ;
    	  R = L ^ f(R, roundKeys[i]);
    	  //System.out.printf("%d   %d\n",i,roundKeys[i]);
    	  L = temp;
       }
        //apply the inverse IP transformation
       //System.out.printf("L   %x\n",L);
      // System.out.printf("R   %x\n",R);
       RL = (R<<32 )+ L ;
       //System.out.printf("RL  %x\n",RL);
       ciphertext = IPinv(RL);
       
        
        //return ciphertext
        return ciphertext;
    }
    
     public long decrypt(long ciphertext){
        
    	long temp, RL, plaintext;
        //apply the IP table
     	long IPtext = IP(ciphertext);
      
       //initiate L and R
       long L = IPtext>>>32;
       long R = IPtext<<32>>>32;
   	
       //run 16 rounds
      for(int i=16 ; i>=1 ; i--){
    	  temp = R ;
    	  R = L ^ f(R, roundKeys[i]);
    	  L = temp;
       
      }
       //apply the inverse IP transformation
      RL = (R<<32) + L ;
      plaintext = IPinv(RL);
      
       
       //return ciphertext
       return plaintext;
    }
    
      
    
     //the function f used in each round in DES
    private long f(long R,long Ki){
        long  valE, XRO, C;
        int  D;
        //apply the expansion E
        valE = E(R);
        //add the roundkey
        XRO = valE ^ Ki;
        //apply the SBoxes
        D = SBox(XRO);
        //apply the permutation P
        C = P(D);
        //output function value
        return C;
    }
      
    //the IP table
    final private int[] IP={58,50,42,34,26,18,10,2,
                            60,52,44,36,28,20,12,4,
                            62,54,46,38,30,22,14,6,
                            64,56,48,40,32,24,16,8,
                            57,49,41,33,25,17,9,1,
                            59,51,43,35,27,19,11,3,
                            61,53,45,37,29,21,13,5,
                            63,55,47,39,31,23,15,7};
    
    //the inverse IP table
    final private int[] IPinv={40,8,48,16,56,24,64,32,
                               39,7,47,15,55,23,63,31,
                               38,6,46,14,54,22,62,30,
                               37,5,45,13,53,21,61,29,
                               36,4,44,12,52,20,60,28,
                               35,3,43,11,51,19,59,27,
                               34,2,42,10,50,18,58,26,
                               33,1,41,9,49,17,57,25};
    
    //the PC1 table used in key scheduling
    final private int[] PC1={57,49,41,33,25,17,9,
                             1,58,50,42,34,26,18,
                             10,2,59,51,43,35,27,
                             19,11,3,60,52,44,36,
                             63,55,47,39,31,23,15,
                             7,62,54,46,38,30,22,
                             14,6,61,53,45,37,29,
                             21,13,5,28,20,12,4};
    
    //the PC2 table used in key scheduling
    final private int[] PC2={14,17,11,24,1,5,3,28,
                            15,6,21,10,23,19,12,4,
                            26,8,16,7,27,20,13,2,
                            41,52,31,37,47,55,30,40,   
                            51,45,33,48,44,49,39,56,    
                            34,53,46,42,50,36,29,32};
    
    //the E table used in the round function f
    final private int[] E={32,1,2,3,4,5,4,5,
                            6,7,8,9,8,9,10,11,
                            12,13,12,13,14,15,16,17,
                            16,17,18,19,20,21,20,21,
                            22,23,24,25,24,25,26,27,
                            28,29,28,29,30,31,32,1};
    
    //the P table used in the round function f
    final private int[] P={16,7,20,21,29,12,28,17,
                            1,15,23,26,5,18,31,10,
                            2,8,24,14,32,27,3,9,
                            19,13,30,6,22,11,4,25};
    
    //SBox1
    final private int[][] SBox1={{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
                                 {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
                                 {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
                                 {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}};
    
     //SBox2
    final private int[][] SBox2={{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
                                {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
                                {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
                                {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}};
    
    //SBox3
    final private int[][] SBox3={{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
                                {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
                                {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
                                {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}};
    
     //SBox4
    final private int[][] SBox4={{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
                                {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
                                {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
                                {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}};
    
    //SBox5
    final private int[][] SBox5={{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
                                {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
                                {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
                                {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}};
    
    //SBox6
    final private int[][] SBox6={{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
                                {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
                                {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
                                {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}};
    
    //SBox7
    final private int[][] SBox7={{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
                                {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
                                {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
                                {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}};
    
    //SBox8
    final private int[][] SBox8={{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
                                {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
                                {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
                                {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}};
    
    //generates the roudkeys from the key
    private void genRoundKeys(long key){
        roundKeys=new long[17];
        int[] CD=PC1(key);
        int C=CD[0];
        int D=CD[1];
        for(int i=1;i<=16;i++){
            //check if we should rotate one or two steps
            if((i==1)||(i==2)||(i==9)||(i==16)){
                //rotate one step
                C=((C<<1)|(C>>27))&0x0fffffff;
                D=((D<<1)|(D>>27))&0x0fffffff;
            }
            else {
                C=((C<<2)|(C>>26))&0x0fffffff;
                D=((D<<2)|(D>>26))&0x0fffffff;
            }
            roundKeys[i]=PC2(C,D);
        }
    }
    
    //permutes the bits in input with the IP table
    private long IPinv(long RL){
        long temp=0;
        
       
        long mask;
        for(int i=0;i<64;i++){
            //left shift temp
            temp<<=1;
            //check if bit at position IPinv[i]==1 
            mask=1l<<(64-IPinv[i]);
            if((RL&mask)!=0)
                temp|=1;
        }
        
        return temp;
    }
    
    //permutes the bits in input with the IP table
    private long IP(long input){
        long temp=0;
        
       
        long mask;
        for(int i=0;i<64;i++){
            //left shift temp
            temp<<=1;
            //check if bit at position IPinv[i]==1 
            mask=1l<<(64-IP[i]);
            if((input&mask)!=0)
                temp|=1;
        }
        
        return temp;
    }
    
   
    
    //apply PC1 to the key and generate C0, D0
    private int[] PC1(long input){
        int[] CD=new int[2];
        CD[0]=0;
        CD[1]=0;
       
        long mask;
        for(int i=0;i<28;i++){
            //left shift L and R
            CD[0]<<=1;
            CD[1]<<=1;
            //check if bit at position IP[i]==1 
            mask=1l<<(64-PC1[i]);
            if((input&mask)!=0)
                CD[0]|=1;
            //check if bit at position IP[i]==1 
            mask=1l<<(64-PC1[i+28]);
            if((input&mask)!=0)
                CD[1]|=1;
        }
        return CD;
    }
    
    //apply PC2 to C and D to get roundKeys
    private long PC2(int C,int D){
        long input=(long)C<<28;
        input|=D;
        long roundKey=0;
       
        long mask;
        for(int i=0;i<48;i++){
            //left shift roundKey
            roundKey<<=1;
            //check if bit at position PC2[i]==1 
            mask=1l<<(56-PC2[i]);
            if((input&mask)!=0)
                roundKey|=1;
        }
        return roundKey;
    }
    
    //expand R to 48-bits using the table E
    private long E(long R){
        long valE=0;
       
        long mask;
        for(int i=0;i<48;i++){
            //left shift roundKey
            valE<<=1;
            //check if bit at position E[i]==1 
            mask=1l<<(32-E[i]);
            if((R&mask)!=0)
                valE|=1;
        }
        return valE;
    }
    
    //Final permutation in the function f
    private long P(int D){
        long valP=0;
       
        long mask;
        for(int i=0;i<32;i++){
            //left shift valP
            valP<<=1;
            //check if bit at position P[i]==1 
            mask=1l<<(32-P[i]);
            if((D&mask)!=0)
                valP|=1;
        }
        return valP;
    }
     
    //calculates the SBox values in DES
    private int SBox(long C){
        int D=0;
        //input to S boxes
        int C8=(int)C&0x3f;
        C>>=6;
        int C7=(int)C&0x3f;
        C>>=6;
        int C6=(int)C&0x3f;
        C>>=6;
        int C5=(int)C&0x3f;
        C>>=6;
        int C4=(int)C&0x3f;
        C>>=6;
        int C3=(int)C&0x3f;
        C>>=6;
        int C2=(int)C&0x3f;
        C>>=6;
        int C1=(int)C&0x3f;
     //   System.out.printf("\n%08x\t%08x\t%08x\t%08x\t%08x\t%08x\t%08x\t%08x\n",C1,C2,C3,C4,C5,C6,C7,C8);
        //process the different Ci to get output
        int r=(C1&1)+2*((C1>>5)&0x1);
        int c=((C1>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D=SBox1[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C2&1)+2*((C2>>5)&0x1);
        c=((C2>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D|=SBox2[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C3&1)+2*((C3>>5)&0x1);
        c=((C3>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D|=SBox3[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C4&1)+2*((C4>>5)&0x1);
        c=((C4>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D|=SBox4[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C5&1)+2*((C5>>5)&0x1);
        c=((C5>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D|=SBox5[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C6&1)+2*((C6>>5)&0x1);
        c=((C6>>1)&0xf);
      //  System.out.println("r,c " + r + " "+ c);
        D|=SBox6[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C7&1)+2*((C7>>5)&0x1);
        c=((C7>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D|=SBox7[r][c];
       // System.out.printf("%08x\n",D);
        D<<=4;
        
        r=(C8&1)+2*((C8>>5)&0x1);
        c=((C8>>1)&0xf);
       // System.out.println("r,c " + r + " "+ c);
        D|=SBox8[r][c];
       // System.out.printf("%08x\n",D);
        return D;
    }
}
