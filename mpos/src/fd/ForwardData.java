package fd;


import java.util.Timer;
import java.util.TimerTask;



public class ForwardData
{	
	private    int    sendtoapp=0,sendtosdk=0;
	private    byte[] rcvfromapp=  new byte[1024];
	private    byte[] rcvfromsdk=  new byte[1024];
	private    boolean isappdata=false;
    private    boolean issdkdata=false;
    private    boolean isreadappovertime=false;
    private    boolean isreadsdkovertime=false;
    
	private    static  ForwardData  fddata= new ForwardData();
	private   ForwardData (){};
	
	public   static  ForwardData  getInstance(){
		return fddata;
	}
	
    public  int sendtoappdata(byte[] data , int sendlen){
		
		if(sendlen>1024 | sendlen<=0) return 0;
		for(int i=0;i<sendlen;i++)
	      rcvfromsdk[i]=data[i]; 
	    sendtoapp=sendlen;
	    issdkdata=true;
	     
	 //   Log.d("Send SDK->APP",ByteUtils.printBytes(data,0,sendlen));
		return sendlen;
	}
	
   public  int RcvfromAppdata(byte[] data , int rcvlen, int timeout ){
		int len;
		if( rcvlen>1024) return 0;
		
		Timer timer = new Timer(true);
		
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				isreadappovertime=true;
			}
		}, timeout*1000);
		
	    while(true){
	    	if(isreadappovertime){
	    	    timer.cancel();
	    		len=-1;
	    		break;
	    	}
	    	if(isappdata){
	    	  timer.cancel();
		      len=(rcvlen>sendtosdk) ? sendtosdk : rcvlen;
		      for(int i=0; i<len;i++){ 
			     data[i]=rcvfromapp[i];
		       }
			// Log.d("Rcv APPData",ByteUtils.printBytes(data,0,len));
		     break;  
	    	}
	    }
	    isreadappovertime=false;
	    isappdata=false;
	    sendtosdk=0;
		return len;
	}
    public  int sendtosdkdata(byte[] data , int sendlen ){
		
		if(sendlen>1024 | sendlen<=0) return 0;
		for(int i=0;i<sendlen;i++)
	      rcvfromapp[i]=data[i]; 
	    sendtosdk=sendlen;
	    isappdata=true;
	 //   Log.d("APP->SDK",ByteUtils.printBytes(data, 0, sendlen));
		return sendlen;
	}
    
    public  int RcvfromSdkdata(byte[] data , int rcvlen, int timeout ){
		int len;
		if( rcvlen>1024) return 0;
		
		Timer timer = new Timer(true);
		
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				isreadsdkovertime=true;
			}
		}, timeout*1000);
		
	    while(true){
	    	if(isreadsdkovertime){
	    		len=-1;
	    		break;
	    	}
	    	if(issdkdata){
		      len=(rcvlen>sendtoapp) ? sendtoapp : rcvlen;
		      for(int i=0; i<len;i++){ 
			     data[i]=rcvfromsdk[i];
		      }
			// Log.d("Rcv SDKData",ByteUtils.printBytes(data,0,len));
		     break;  
	    	}
	    }
	    timer.cancel();
	    isreadsdkovertime=false;
	    issdkdata=false;
	    sendtoapp=0;
		return len;
	}
}