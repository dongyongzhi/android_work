package com.ctbri.pos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.ctbri.ElecException;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.PrintResponse;
import com.ctbri.domain.ResponseCode;
import com.yfcomm.pos.YFLog;
import com.yfcomm.pos.bt.device.DeviceComm;
import com.yfcomm.pos.bt.device.DeviceDecoder;
import com.yfcomm.pos.tlv.TLVCollection;
import com.yfcomm.pos.utils.ByteUtils;
 

/**
 * yf pos 业务处理
 * @author qin
 * 
 * 2012-11-17
 */
public class YFPOSService implements ElecPosService {
	private final static YFLog logger = YFLog.getLog(YFPOSService.class);
	
	private final DeviceComm pos;
	
	public YFPOSService(DeviceComm pos){
		this.pos = pos;
		if(pos==null)
			throw new IllegalArgumentException(" not find pos");
	}
 
	@Override
	public PrintResponse printLastTrans() {
		 throw new RuntimeException(" no support print");
	}

	@Override
	public POSTransResponse transRequest(POSTransRequest req){
		logger.d(">>>>>>>>>>请求pos机数据");
		
	    //执行，并返回结果 
	    POSTransResponse response = new POSTransResponse();
	    
	    //获取一个新的空间用于存放终端返回数据
	    byte[] out = DeviceDecoder.newDecoder().getBufferSpace();
	    
    	//====开始组包====
    	TLVCollection tlvs = new TLVCollection();
    	req.encode(tlvs);
    	
    	ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
    	try {
    		//交易类型
			os.write(req.getMessageType().getBytes());
	    	tlvs.encode(os);
	    	 
			//调用设备组包
	    	int resultCode = pos.packData(os.toByteArray(), out);
	    	//解析调用接口返回码
	    	ResponseCode responseCode = ResponseCode.resultCode(resultCode);
	    	logger.d("<<<<<<<<<pos机返回:%s",responseCode.getMessage());
	    	
	    	if(responseCode == ResponseCode.SUCCESS) {
	    		//调用 成功
	    		//解析返回数据
	    		responseCode = ResponseCode.packDataResult(out);
	    		
	    		//获取返回数据包长度
	    		//只有返回成功或需要冲正时才有数据返回
	    		if(responseCode == ResponseCode.SUCCESS || responseCode == ResponseCode.REVERSAL) {
	    			int len = ByteUtils.byteToInt(out[2], out[3]);
	    			if(out.length - 4 >=len) {
	    				response.setLen(len);
						response.setData(new byte[len]);
						System.arraycopy(out, 4, response.getData(), 0, len);
	    			}
	    		}
	    	}
	    	//设置返回码
	    	response.setCode(responseCode);
    	} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public POSTransResponse transResponse(byte[] data){
		logger.d( ">>>>>>>>>>8583 交易包 返回 给 pos机");
		 //执行，并返回结果 
		byte[] out = DeviceDecoder.newDecoder().getBufferSpace();
		
		//pos返回成功
		int resultCode = pos.unpackData(data, out);
		
		//解析调用接口返回码
    	ResponseCode responseCode = ResponseCode.resultCode(resultCode);
    	logger.d("<<<<<<<<<pos机返回:%s",responseCode.getMessage());
    	
    	POSTransResponse response = new POSTransResponse(responseCode);
    	
    	if(responseCode == ResponseCode.SUCCESS) {
    		//调用 成功
    		//解析返回数据
    		responseCode = ResponseCode.packDataResult(out);
    		response.setCode(responseCode);
    		
    		//获取返回数据包长度
    		//只有返回成功或需要冲正、用户重新输入密码时才有数据返回
    		if(responseCode == ResponseCode.SUCCESS || responseCode == ResponseCode.REVERSAL || 
    				responseCode == ResponseCode.DATA_RESENT) {
    			int len = ByteUtils.byteToInt(out[2], out[3]);
    			response.setLen(len);
    			response.setOffset(4);
    			response.setData(out);
    		}
    	}
		return response;
	}
	
	@Override
	public ResponseCode writePOSParams(byte[] data) {
		return null;
	}
	
	/**
	 * 写入pos参数
	 * @param info
	 * @return
	 */
	public ResponseCode writePOSParams(POSInfo info){
		logger.d(">>>>>>>>>>写入pos参数");
		return null;
		 
	}

	/**获取pos机参数
	 * @throws ElecException */
	@Override
	public POSInfo getPosInfo(){
		return null;
	}
	
	@Override
	public void release() {
		pos.close();
	}

	@Override
	public void endCmd() {
		
	}

}
