package com.yifeng.start;

interface IStartDevService{

	/**
    * 初始化设备
    */
    int init();
    
    int initForMAC(String mac);
    
    int initForName(String name);
    
    int startDevPackData(in byte[] inBuf,out byte[] outBuf);
    
    int startDevUnpackData(in byte[] inBuf,out byte[] outBuf);
    
    int startDevPrint(in byte[] buf, out byte[] outBuf);
    
    int startDevSetTimeOut(int timeout);
    
    int startDevInitIdle();
    
    boolean release();
    
    void close();
    /**
     * 获取pos地址
     */
	String getPOSAddress();
 
}