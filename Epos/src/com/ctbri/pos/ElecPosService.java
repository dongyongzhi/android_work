package com.ctbri.pos;


import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.PrintResponse;
import com.ctbri.domain.ResponseCode;

public interface ElecPosService {
	
	
	/**
	 * 交易请求
	 * @param operator   操作员
	 * @return           是否成功
	 */
	POSTransResponse transRequest(POSTransRequest req);
	
	
	/**
	 * 交易应答
	 * @return
	 */
	POSTransResponse transResponse(byte[] data);
	
	/**
	 * 获取 pos机参数
	 * @return
	 */
	POSInfo  getPosInfo();
	
	/**
	 * 更新pos参数
	 * @param info  pos内容
	 * @return
	 */
	ResponseCode writePOSParams(POSInfo info);
	
	/**
	 * 更新pos参数  
	 * @param data  tms中心下载的数据内容
	 * @return
	 */
	ResponseCode writePOSParams(byte[] data);
	
  
	void release();
	
	/**
	 * 结束 pos 命令
	 */
	void endCmd();
	
	/**
	 * 打印最后 一笔交易
	 * @return
	 */
	PrintResponse printLastTrans();

}
