/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.mfs.util;


/**
 *
 * @author Fredrik
 */
public class AES {
    
    public AES(byte[] key){
        //creates an AES cipher and initiates the key
        W=new byte[44][4];
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                W[i][j]=key[4*i+(3-j)];
        
        expandKey(); 
        
        S=new byte[4][4];
    }
    
    
    public byte[] encrypt(byte[] plaintext){
    	//initiate S
       for(int i=0;i<4;i++)
    	   for(int j=0;j<4;j++)
    		   S[i][j] = plaintext[i*4+j];

        //add the key before the iterations
       addRoundKey(0);
        
        //repeat 9 iterations
       for(int n=1; n<=9; n++){
    	   subBytes();
    	   shiftRow();
    	   mixColumns();
    	   addRoundKey(n);
       }
        
        //do the final iteration
       subBytes();
	   shiftRow();
	   addRoundKey(10);
       
	   //format the output
	   //printS();
	   
        byte[] ciphertext=new byte[16];
       
        for(int i=0;i<4;i++)
     	   for(int j=0;j<4;j++)
           ciphertext[i*4+j]=S[i][j];
        return ciphertext;
    }
    
     public byte[] decrypt(byte[] ciphertext){
    	 //initiate S
         for(int i=0;i<4;i++)
      	   for(int j=0;j<4;j++)
      		   S[i][j] = ciphertext[i*4+j];
        
        //add the key before the iterations
         addRoundKey(10);
        
        //repeat 9 iterations
         for(int n=9; n>=1; n--){
        	 invShiftRow();
        	 invSubBytes();
        	 addRoundKey(n);
        	 invMixColumns();
         }
        	
         
        //do the final iteration
         invShiftRow();
    	 invSubBytes();
    	 addRoundKey(0);
        
        byte[] plaintext=new byte[16];
        
        for(int i=0;i<4;i++)
      	   for(int j=0;j<4;j++)
      		 plaintext[i*4+j]=S[i][j];
        
        return plaintext;
    }
    
    //adds the round key for round i
    //uses W[4i]...W[4i+3]
    private void addRoundKey(int i){
        for(int r=0;r<4;r++)
            for(int c=0;c<4;c++)
                S[r][c]^=W[4*i+c][3-r];
    }
    
    //apply the SBox to the content of S
    private void subBytes(){
        for(int r=0;r<4;r++)
            for(int c=0;c<4;c++)
                S[r][c]=SBox(S[r][c]);
    }
    
     //apply the invSBox to the content of S
    private void invSubBytes(){
        for(int r=0;r<4;r++)
            for(int c=0;c<4;c++)
                S[r][c]=invSBox(S[r][c]);
    }
    
    //shift each row
    private void shiftRow(){
        byte temp;
        //shift row 1
        temp=S[1][0];
        for(int c=0;c<3;c++)
            S[1][c]=S[1][c+1];
        S[1][3]=temp;
        //shift row 2
        temp=S[2][0];
        S[2][0]=S[2][2];
        S[2][2]=temp;
        temp=S[2][1];
        S[2][1]=S[2][3];
        S[2][3]=temp;
        //shift row 3
        temp=S[3][3];
        for(int c=3;c>0;c--)
            S[3][c]=S[3][c-1];
        S[3][0]=temp;
    }
    
     //inverse to shiftRow()
    private void invShiftRow(){
        byte temp;
        //shift row 3
        temp=S[3][0];
        for(int c=0;c<3;c++)
            S[3][c]=S[3][c+1];
        S[3][3]=temp;
        //shift row 2
        temp=S[2][0];
        S[2][0]=S[2][2];
        S[2][2]=temp;
        temp=S[2][1];
        S[2][1]=S[2][3];
        S[2][3]=temp;
        //shift row 1
        temp=S[1][3];
        for(int c=3;c>0;c--)
            S[1][c]=S[1][c-1];
        S[1][0]=temp;
    }
    
    private void mixColumns(){
        byte[][] Sprime=new byte[4][4];
        for(int c=0;c<4;c++){
            Sprime[0][c]=(byte)(multX(S[0][c])^multX(S[1][c])^S[1][c]^S[2][c]^S[3][c]);
            Sprime[1][c]=(byte)(multX(S[1][c])^multX(S[2][c])^S[2][c]^S[3][c]^S[0][c]);
            Sprime[2][c]=(byte)(multX(S[2][c])^multX(S[3][c])^S[3][c]^S[0][c]^S[1][c]);
            Sprime[3][c]=(byte)(multX(S[3][c])^multX(S[0][c])^S[0][c]^S[1][c]^S[2][c]);
        }
        for(int r=0;r<4;r++)
            for(int c=0;c<4;c++)
                S[r][c]=Sprime[r][c];
    }
    
    private void invMixColumns(){
        byte[][] Sprime=new byte[4][4];
        for(int c=0;c<4;c++){
            Sprime[0][c]=(byte)(multX3(S[0][c])^multX2(S[0][c])^multX(S[0][c])^
                                multX3(S[1][c])^multX(S[1][c])^S[1][c]^
                                multX3(S[2][c])^multX2(S[2][c])^S[2][c]^
                                multX3(S[3][c])^S[3][c]);
            Sprime[1][c]=(byte)(multX3(S[1][c])^multX2(S[1][c])^multX(S[1][c])^
                                multX3(S[2][c])^multX(S[2][c])^S[2][c]^
                                multX3(S[3][c])^multX2(S[3][c])^S[3][c]^
                                multX3(S[0][c])^S[0][c]);
            Sprime[2][c]=(byte)(multX3(S[2][c])^multX2(S[2][c])^multX(S[2][c])^
                                multX3(S[3][c])^multX(S[3][c])^S[3][c]^
                                multX3(S[0][c])^multX2(S[0][c])^S[0][c]^
                                multX3(S[1][c])^S[1][c]);
            Sprime[3][c]=(byte)(multX3(S[3][c])^multX2(S[3][c])^multX(S[3][c])^
                                multX3(S[0][c])^multX(S[0][c])^S[0][c]^
                                multX3(S[1][c])^multX2(S[1][c])^S[1][c]^
                                multX3(S[2][c])^S[2][c]);
        }
        for(int r=0;r<4;r++)
            for(int c=0;c<4;c++)
                S[r][c]=Sprime[r][c];
    }
    
    
    
    //calculates in*x mod mx
    private byte multX(byte in){
        byte temp;
        if((in&0x80)==0)
            temp=(byte)(in<<1);
        else
            temp=(byte)((in<<1)^mx);
        return temp;
    }
    
     //calculates in*x^2 mod mx
    private byte multX2(byte in){
        byte temp;
        temp=multX(in);
        temp=multX(temp);
        return temp;
    }
    
    //calculates in*x^3 mod mx
    private byte multX3(byte in){
        byte temp;
        temp=multX(in);
        temp=multX(temp);
        temp=multX(temp);
        return temp;
    }
    
    
    
    //the irreducieble polynomial in AES
    //x^8+x^4+x^3+x+1
    final byte mx=0x1B;
    
    private byte[][] W; //the round keys
    
    private byte[][] S; //the state used in encryption and decryption
    
    private void expandKey(){
        //temp used when expanding the key
        byte[] temp=new byte[4];
        
        for(int i=4;i<44;i++){
            //init temp
            for(int j=0;j<4;j++)
                temp[j]=W[i-1][j];
            
            //check if i%4==0, then do special update
            if(i%4==0){
                //RotWord()
                byte tempByte=temp[3];
                temp[3]=temp[2];
                temp[2]=temp[1];
                temp[1]=temp[0];
                temp[0]=tempByte;
                
                //SubWord()
                for(int j=0;j<4;j++)
                    temp[j]=SBox(temp[j]);
                //add RCon
                temp[3]^=RCon[i/4];   
            }
            for(int j=0;j<4;j++)
                W[i][j]=(byte)(temp[j]^W[i-4][j]);
        }
    }
    
    //the constant used in key expansion
    final private byte[] RCon={(byte)0x00,(byte)0x01,(byte)0x02,(byte)0x04,
                               (byte)0x08,(byte)0x10,(byte)0x20,(byte)0x40,
                               (byte)0x80,(byte)0x1b,(byte)0x36};
    
    private byte SBox(byte input){
        int pos=input;
        pos&=0xFF;
        return SBox[pos];
    }
    
    private byte invSBox(byte input){
        int pos=input;
        pos&=0xFF;
        return invSBox[pos];
    }

    //the AES SBox        
    final private byte[] SBox=
        {(byte)0x63,(byte)0x7c,(byte)0x77,(byte)0x7b,(byte)0xf2,(byte)0x6b,(byte)0x6f,(byte)0xc5, 
         (byte)0x30,(byte)0x01,(byte)0x67,(byte)0x2b,(byte)0xfe,(byte)0xd7,(byte)0xab,(byte)0x76,
         (byte)0xca,(byte)0x82,(byte)0xc9,(byte)0x7d,(byte)0xfa,(byte)0x59,(byte)0x47,(byte)0xf0, 
         (byte)0xad,(byte)0xd4,(byte)0xa2,(byte)0xaf,(byte)0x9c,(byte)0xa4,(byte)0x72,(byte)0xc0,
         (byte)0xb7,(byte)0xfd,(byte)0x93,(byte)0x26,(byte)0x36,(byte)0x3f,(byte)0xf7,(byte)0xcc,
         (byte)0x34,(byte)0xa5,(byte)0xe5,(byte)0xf1,(byte)0x71,(byte)0xd8,(byte)0x31,(byte)0x15,
         (byte)0x04,(byte)0xc7,(byte)0x23,(byte)0xc3,(byte)0x18,(byte)0x96,(byte)0x05,(byte)0x9a,
         (byte)0x07,(byte)0x12,(byte)0x80,(byte)0xe2,(byte)0xeb,(byte)0x27,(byte)0xb2,(byte)0x75,
         (byte)0x09,(byte)0x83,(byte)0x2c,(byte)0x1a,(byte)0x1b,(byte)0x6e,(byte)0x5a,(byte)0xa0,
         (byte)0x52,(byte)0x3b,(byte)0xd6,(byte)0xb3,(byte)0x29,(byte)0xe3,(byte)0x2f,(byte)0x84,
         (byte)0x53,(byte)0xd1,(byte)0x00,(byte)0xed,(byte)0x20,(byte)0xfc,(byte)0xb1,(byte)0x5b,
         (byte)0x6a,(byte)0xcb,(byte)0xbe,(byte)0x39,(byte)0x4a,(byte)0x4c,(byte)0x58,(byte)0xcf,
         (byte)0xd0,(byte)0xef,(byte)0xaa,(byte)0xfb,(byte)0x43,(byte)0x4d,(byte)0x33,(byte)0x85,
         (byte)0x45,(byte)0xf9,(byte)0x02,(byte)0x7f,(byte)0x50,(byte)0x3c,(byte)0x9f,(byte)0xa8,
         (byte)0x51,(byte)0xa3,(byte)0x40,(byte)0x8f,(byte)0x92,(byte)0x9d,(byte)0x38,(byte)0xf5,
         (byte)0xbc,(byte)0xb6,(byte)0xda,(byte)0x21,(byte)0x10,(byte)0xff,(byte)0xf3,(byte)0xd2,
         (byte)0xcd,(byte)0x0c,(byte)0x13,(byte)0xec,(byte)0x5f,(byte)0x97,(byte)0x44,(byte)0x17,
         (byte)0xc4,(byte)0xa7,(byte)0x7e,(byte)0x3d,(byte)0x64,(byte)0x5d,(byte)0x19,(byte)0x73,
         (byte)0x60,(byte)0x81,(byte)0x4f,(byte)0xdc,(byte)0x22,(byte)0x2a,(byte)0x90,(byte)0x88,
         (byte)0x46,(byte)0xee,(byte)0xb8,(byte)0x14,(byte)0xde,(byte)0x5e,(byte)0x0b,(byte)0xdb,
         (byte)0xe0,(byte)0x32,(byte)0x3a,(byte)0x0a,(byte)0x49,(byte)0x06,(byte)0x24,(byte)0x5c,
         (byte)0xc2,(byte)0xd3,(byte)0xac,(byte)0x62,(byte)0x91,(byte)0x95,(byte)0xe4,(byte)0x79,
         (byte)0xe7,(byte)0xc8,(byte)0x37,(byte)0x6d,(byte)0x8d,(byte)0xd5,(byte)0x4e,(byte)0xa9,
         (byte)0x6c,(byte)0x56,(byte)0xf4,(byte)0xea,(byte)0x65,(byte)0x7a,(byte)0xae,(byte)0x08,
         (byte)0xba,(byte)0x78,(byte)0x25,(byte)0x2e,(byte)0x1c,(byte)0xa6,(byte)0xb4,(byte)0xc6,
         (byte)0xe8,(byte)0xdd,(byte)0x74,(byte)0x1f,(byte)0x4b,(byte)0xbd,(byte)0x8b,(byte)0x8a,
         (byte)0x70,(byte)0x3e,(byte)0xb5,(byte)0x66,(byte)0x48,(byte)0x03,(byte)0xf6,(byte)0x0e,
         (byte)0x61,(byte)0x35,(byte)0x57,(byte)0xb9,(byte)0x86,(byte)0xc1,(byte)0x1d,(byte)0x9e,
         (byte)0xe1,(byte)0xf8,(byte)0x98,(byte)0x11,(byte)0x69,(byte)0xd9,(byte)0x8e,(byte)0x94,
         (byte)0x9b,(byte)0x1e,(byte)0x87,(byte)0xe9,(byte)0xce,(byte)0x55,(byte)0x28,(byte)0xdf,
         (byte)0x8c,(byte)0xa1,(byte)0x89,(byte)0x0d,(byte)0xbf,(byte)0xe6,(byte)0x42,(byte)0x68,
         (byte)0x41,(byte)0x99,(byte)0x2d,(byte)0x0f,(byte)0xb0,(byte)0x54,(byte)0xbb,(byte)0x16};

    //the inverse of AES SBox        
    final private byte[] invSBox=
    {(byte)0x52,(byte)0x09,(byte)0x6a,(byte)0xd5,(byte)0x30,(byte)0x36,(byte)0xa5,(byte)0x38,
     (byte)0xbf,(byte)0x40,(byte)0xa3,(byte)0x9e,(byte)0x81,(byte)0xf3,(byte)0xd7,(byte)0xfb,
     (byte)0x7c,(byte)0xe3,(byte)0x39,(byte)0x82,(byte)0x9b,(byte)0x2f,(byte)0xff,(byte)0x87,
     (byte)0x34,(byte)0x8e,(byte)0x43,(byte)0x44,(byte)0xc4,(byte)0xde,(byte)0xe9,(byte)0xcb,
     (byte)0x54,(byte)0x7b,(byte)0x94,(byte)0x32,(byte)0xa6,(byte)0xc2,(byte)0x23,(byte)0x3d,
     (byte)0xee,(byte)0x4c,(byte)0x95,(byte)0x0b,(byte)0x42,(byte)0xfa,(byte)0xc3,(byte)0x4e,
     (byte)0x08,(byte)0x2e,(byte)0xa1,(byte)0x66,(byte)0x28,(byte)0xd9,(byte)0x24,(byte)0xb2,
     (byte)0x76,(byte)0x5b,(byte)0xa2,(byte)0x49,(byte)0x6d,(byte)0x8b,(byte)0xd1,(byte)0x25,
     (byte)0x72,(byte)0xf8,(byte)0xf6,(byte)0x64,(byte)0x86,(byte)0x68,(byte)0x98,(byte)0x16,
     (byte)0xd4,(byte)0xa4,(byte)0x5c,(byte)0xcc,(byte)0x5d,(byte)0x65,(byte)0xb6,(byte)0x92,
     (byte)0x6c,(byte)0x70,(byte)0x48,(byte)0x50,(byte)0xfd,(byte)0xed,(byte)0xb9,(byte)0xda,
     (byte)0x5e,(byte)0x15,(byte)0x46,(byte)0x57,(byte)0xa7,(byte)0x8d,(byte)0x9d,(byte)0x84,
     (byte)0x90,(byte)0xd8,(byte)0xab,(byte)0x00,(byte)0x8c,(byte)0xbc,(byte)0xd3,(byte)0x0a,
     (byte)0xf7,(byte)0xe4,(byte)0x58,(byte)0x05,(byte)0xb8,(byte)0xb3,(byte)0x45,(byte)0x06,
     (byte)0xd0,(byte)0x2c,(byte)0x1e,(byte)0x8f,(byte)0xca,(byte)0x3f,(byte)0x0f,(byte)0x02,
     (byte)0xc1,(byte)0xaf,(byte)0xbd,(byte)0x03,(byte)0x01,(byte)0x13,(byte)0x8a,(byte)0x6b,
     (byte)0x3a,(byte)0x91,(byte)0x11,(byte)0x41,(byte)0x4f,(byte)0x67,(byte)0xdc,(byte)0xea,
     (byte)0x97,(byte)0xf2,(byte)0xcf,(byte)0xce,(byte)0xf0,(byte)0xb4,(byte)0xe6,(byte)0x73,
     (byte)0x96,(byte)0xac,(byte)0x74,(byte)0x22,(byte)0xe7,(byte)0xad,(byte)0x35,(byte)0x85,
     (byte)0xe2,(byte)0xf9,(byte)0x37,(byte)0xe8,(byte)0x1c,(byte)0x75,(byte)0xdf,(byte)0x6e,
     (byte)0x47,(byte)0xf1,(byte)0x1a,(byte)0x71,(byte)0x1d,(byte)0x29,(byte)0xc5,(byte)0x89,
     (byte)0x6f,(byte)0xb7,(byte)0x62,(byte)0x0e,(byte)0xaa,(byte)0x18,(byte)0xbe,(byte)0x1b,
     (byte)0xfc,(byte)0x56,(byte)0x3e,(byte)0x4b,(byte)0xc6,(byte)0xd2,(byte)0x79,(byte)0x20,
     (byte)0x9a,(byte)0xdb,(byte)0xc0,(byte)0xfe,(byte)0x78,(byte)0xcd,(byte)0x5a,(byte)0xf4,
     (byte)0x1f,(byte)0xdd,(byte)0xa8,(byte)0x33,(byte)0x88,(byte)0x07,(byte)0xc7,(byte)0x31,
     (byte)0xb1,(byte)0x12,(byte)0x10,(byte)0x59,(byte)0x27,(byte)0x80,(byte)0xec,(byte)0x5f,
     (byte)0x60,(byte)0x51,(byte)0x7f,(byte)0xa9,(byte)0x19,(byte)0xb5,(byte)0x4a,(byte)0x0d,
     (byte)0x2d,(byte)0xe5,(byte)0x7a,(byte)0x9f,(byte)0x93,(byte)0xc9,(byte)0x9c,(byte)0xef,
     (byte)0xa0,(byte)0xe0,(byte)0x3b,(byte)0x4d,(byte)0xae,(byte)0x2a,(byte)0xf5,(byte)0xb0,
     (byte)0xc8,(byte)0xeb,(byte)0xbb,(byte)0x3c,(byte)0x83,(byte)0x53,(byte)0x99,(byte)0x61,
     (byte)0x17,(byte)0x2b,(byte)0x04,(byte)0x7e,(byte)0xba,(byte)0x77,(byte)0xd6,(byte)0x26,
     (byte)0xe1,(byte)0x69,(byte)0x14,(byte)0x63,(byte)0x55,(byte)0x21,(byte)0x0c,(byte)0x7d};
}
