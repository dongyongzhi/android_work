package com.ctbri.pos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.os.RemoteException;
import android.util.Log;

import com.ctbri.Constants;
import com.ctbri.ElecException;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.PrintResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.net.yeepay.MPOSPTMSHelper;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.ISO8583Util;
import com.yifeng.hd.IDeviceService;
import com.yifeng.hd.TransRequest;
import com.yifeng.hd.utils.StringUtils;

/**
 * yf pos 业务处理
 * @author qin
 * 
 * 2012-11-17
 */
public class YFPOSService implements ElecPosService {

	private final static String TAG = YFPOSService.class.getName();
	
	private final IDeviceService pos;
	
	public YFPOSService(IDeviceService pos){
		this.pos = pos;
		if(pos==null)
			throw new IllegalArgumentException(" not find yf pos");
	}

	@Override
	public void endPosCmd() {
		try {
			pos.endCmd();
		} catch (RemoteException e) {
			Log.d(TAG, "endPosCmd error",e);
		}
	}
	
	@Override
	public PrintResponse printLastTrans() {
		try {
			int result = this.pos.printLastTrans();
			ResponseCode code = ResponseCode.convert(result);
			if(code == ResponseCode.SUCCESS){
				PrintResponse resp = new PrintResponse();
				resp.setSuccess(true);
				return resp;
			}else
				return ElecResponse.getErrorResponse(PrintResponse.class, code);
			
		} catch (RemoteException e) {
			Log.d(TAG, "endPosCmd error",e);
			return ElecResponse.getErrorResponse(PrintResponse.class, 1,"连接pos机失败！");
		}
	}

	@Override
	public POSTransResponse transRequest(POSTransRequest req){
		
		ElecLog.d(getClass(), ">>>>>>>>>>请求pos机数据");
		
	    TransRequest target = new  TransRequest();
	    target.setMessageType(req.getMessageType());
	    target.setMoney(req.getMoney());
	    target.setOrderCode(req.getOrderCode());
	    target.setOriginalBatchNumber(req.getOriginalBatchNumber());
	    target.setOriginalSerialNumber(req.getOriginalSerialNumber());
	    target.setReferenceNumber(req.getReferenceNumber());
	    target.setSignName(req.getSignName());//订单签名
	    target.setAuthNo(req.getAuthNo());
	    target.setOriginalTransDate(req.getOriginalTransDate());//原交易日期
	    //执行，并返回结果 
	    POSTransResponse resp = new POSTransResponse();
	    resp.setMessageType(req.getMessageType());  //交易类型
	    
	    byte[] outData = new byte[Constants.OUT_BUFFER];
	    try {
	    	resp.setState(pos.transRequest(target, outData));
	    	
	    	ElecLog.d(getClass(), "<<<<<<<<<pos机返回");
			//获取数据长度(8583长度)
			if(resp.getState() == ResponseCode.SUCCESS || resp.getState() == ResponseCode.TRANS_NO_SIGN
					|| resp.getState() == ResponseCode.TRANS_REVERSAL){
				resp.setLen((outData[0]& 0xFF)<< 8 | (outData[1]& 0xFF));
				resp.setData(new byte[resp.getLen()]);
				System.arraycopy(outData, 2, resp.getData(), 0, resp.getLen());
			}
		} catch (RemoteException e) {
			Log.d(TAG, "transRequest error",e); 
			e.printStackTrace();
		}
		return resp;
	}

	@Override
	public POSTransResponse transResponse(byte[] data){
		ElecLog.d(getClass(), ">>>>>>>>>>8583 交易包 返回 给 pos机");
		
		 //执行，并返回结果 
		POSTransResponse  resp = new POSTransResponse();
		
	    try {
	    	byte[] outData = new byte[Constants.OUT_BUFFER];
	    	byte[] target = new byte[data.length+2];
	    	target[0] = (byte)(data.length>>8);
	    	target[1] = (byte)(data.length);
	    	System.arraycopy(data, 0, target, 2, data.length);
	    	
	    	int code = pos.transResponse(target, outData);
	    	
	    	ElecLog.d(getClass(), String.format("<<<<<<<<<<pos 机解包返回 ,返回码：%d",code));
	    	resp.setState(code);
	    	
	    	if(code == ResponseCode.SUCCESS.getValue()){  //交易成功
	    		//解析8583包
    			TransResponse item = ISO8583Util.parse(data);
    			if(item.getErrCode()!=0){ //pos机上显示错误信息
    				pos.clearScreen();
    				pos.displayASCII(2, 0, item.getErrMsg().getBytes(Constants.CHARSET));
    			}
    			if(item.getMessageType()==MessageType.QUERYBALANCE){
    				pos.clearScreen();
    				pos.displayASCII(2, 0, String.format("金额：%s", item.getMoneyString()).getBytes(Constants.CHARSET));
    			}
    			//打印返回信息
    			item.setPrint(true);
    			resp.setTransResponse(item);
    			
	    	}else if(code==ResponseCode.TRANS_REVERSAL.getValue()
	    			|| code == ResponseCode.TRANS_NO_SIGN.getValue()){ //冲正 或 签到
	    		
	    		resp.setLen((outData[0] & 0xFF)<< 8  | (outData[1] & 0xFF));
				resp.setData(new byte[resp.getLen()]);
				System.arraycopy(outData, 2, resp.getData(), 0, resp.getLen());
	    	}
	    	
		} catch (RemoteException e) {
			Log.e(TAG, e.getMessage(),e);
			resp.setState(ResponseCode.EXEC_CMD_FAIL.getValue());  //执行失败
		} catch (UnsupportedEncodingException e) {
			ElecLog.e(getClass(), e.getMessage(), e);
		}
		return resp;
	}
	
	@Override
	public ResponseCode writePOSParams(byte[] data) {
		return writePOSParams(MPOSPTMSHelper.parse(data));
	}
	
	/**
	 * 写入pos参数
	 * @param info
	 * @return
	 */
	public ResponseCode writePOSParams(POSInfo info){
		ElecLog.d(getClass(), ">>>>>>>>>>写入pos参数");
		if(info==null)
			return ResponseCode.EXEC_CMD_FAIL;
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		//二字节长度
		buf.write(0x00);
		buf.write(0x00);
		buf.write(1);//写入参数
		
		//写入要获取的参数
		byte[] temp;
		
		try {
			for(Params  params : Params.values()){
				if(params!=Params.NULL){
					switch(params){
					 case CUSTOMER_NUMBER:
						 if(info.getCustomerNumber()!=null && !"".equals(info.getCustomerNumber())){
							 buf.write(params.getValue()>> 8);
							 buf.write(params.getValue());
							 
							 buf.write(info.getCustomerNumber().length());
							
							 buf.write(info.getCustomerNumber().getBytes());
							
						 }
						 break;
					 case CUSTOMER_NAME:
						 
						 if(info.getCustomerName()!=null && !"".equals(info.getCustomerName())){
							 buf.write(params.getValue()>> 8);
							 buf.write(params.getValue());
							 temp = info.getCustomerName().getBytes(Constants.CHARSET);
							 
							 buf.write(temp.length);
							 buf.write(temp);
						 }
						 break;
					 case POS_CENTER_NUMBER:
						 if(info.getPosNumber()!=null && !"".equals(info.getPosNumber())){
							 buf.write(params.getValue()>> 8);
							 buf.write(params.getValue());
							 
							 buf.write(info.getPosNumber().length());
							 buf.write(info.getPosNumber().getBytes());
						 }
						 break;
						 
					 case SOFTVERSION:
						 if(info.getSoftVersion()!=null && !"".equals(info.getSoftVersion())){
							 buf.write(params.getValue()>> 8);
							 buf.write(params.getValue());
							 
							 buf.write(info.getSoftVersion().length());
							 buf.write(info.getSoftVersion().getBytes());
						 }
						 break;
						 
					 case PARAMVERSION:
						 if(info.getParamVersion()!=null && !"".equals(info.getParamVersion())){
							 buf.write(params.getValue()>> 8);
							 buf.write(params.getValue());
							 
							 buf.write(info.getParamVersion().length());
							 buf.write(info.getParamVersion().getBytes());
						 }
						 break;
					 case MAINKEY:
						 if(info.getMainkey()!=null && info.getMainkey().length>0){
							 buf.write(params.getValue()>> 8);
							 buf.write(params.getValue());
							 
							 buf.write(info.getMainkey().length);
							 buf.write(info.getMainkey());
						 }
						 break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] data = buf.toByteArray();
		//计算长度
		int len = data.length-2;
		data[0] = (byte)(len>>8);
		data[1] = (byte)len;
		try {
			int code = pos.transParams(data,new byte[10]);
			return ResponseCode.convert(code);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new ElecException(ResponseCode.EXEC_CMD_FAIL.getMessage());
		}
	}

	/**获取pos机参数
	 * @throws ElecException */
	@Override
	public POSInfo getPosInfo(){
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		//二字节长度
		buf.write(0x00);
		buf.write(0x00);
		buf.write(0);//读取参数
		
		//写入要获取的参数
		for(Params  params : Params.values()){
			if(params!=Params.NULL && params.canRead){
				buf.write(params.getValue()>> 8);
				buf.write(params.getValue());
			}
		}
		byte[] data = buf.toByteArray();
		//计算长度
		int len = data.length-2;
		data[0] = (byte)(len>>8);
		data[1] = (byte)len;
		
		byte[] outData = new byte[Constants.OUT_BUFFER];
    	try {
			int code = pos.transParams(data, outData);
			if(code!=ResponseCode.SUCCESS.getValue())
				throw new ElecException(ResponseCode.convert(code).getMessage());
			//解析数据
			len = (outData[0]& 0xFF) << 8 | (outData[1] & 0xFF);
			ElecLog.d(getClass(), String.format(" recv pos params:%s",StringUtils.printBytes(outData,0,len+2)));
			
			POSInfo posInfo = new POSInfo();
			if(len>0){
				parseParams(2,len+2,outData,posInfo);
			}
			//获取 pos机地址
			posInfo.setAddress(pos.getPOSAddress());
			//pos名称
			posInfo.setPosName(Constants.YF_POSE_MODE);
			posInfo.setRemark("支持磁条卡、IC卡、射频卡、PIN输入、显示、打印");
			posInfo.setCommType(Constants.POS_COMM_TYPE);
			posInfo.setSupportPrint(true);//是否支持打印
			
			return posInfo;
			
		} catch (RemoteException e) {
			ElecLog.e(getClass(), e.getMessage());
			throw new ElecException(ResponseCode.EXEC_CMD_FAIL.getMessage());
		} catch (UnsupportedEncodingException e) {
			ElecLog.e(getClass(), e.getMessage(),e);
			throw new ElecException(ResponseCode.EXEC_CMD_FAIL.getMessage());
		}
	}
	
	/**
	 * 解析 pos机返回的参数
	 * @param offset 数据偏移
	 * @param data   数据内容
	 * @throws UnsupportedEncodingException 
	 */
	private void parseParams(int offset,int count,byte[] data,POSInfo posInfo) throws UnsupportedEncodingException{
		if(offset >=count-1)
			return;
		int tag = (data[offset] & 0xFF) <<8  | (data[++offset] & 0xFF);
		int len = data[++offset] & 0xFF;
		if(len>0){
			//中文字符
			 switch(Params.convert(tag)){
			 case CUSTOMER_NUMBER:
				 posInfo.setCustomerNumber(new String(data,++offset,len));
				 break;
			 case CUSTOMER_NAME:
				 posInfo.setCustomerName(new String(data,++offset,len,Constants.CHARSET));
				 break;
			 case POS_SERIALNUMBER:
				 posInfo.setSerialNumber(new String(data,++offset,len));
				 break;
			 case POS_CENTER_NUMBER:
				 posInfo.setPosNumber(new String(data,++offset,len));
				 break;
			 case OPERATOR:
				 posInfo.setOperator(new String(data,++offset,len));
				 break;
				 
			 case SOFTVERSION:
				 posInfo.setSoftVersion(new String(data,++offset,len));
				 break;
				 
			 case PARAMVERSION:
				 posInfo.setParamVersion(new String(data,++offset,len));
				 break;
			 default:
				 offset++;
				break;
			 }
		}
		offset +=len;
		parseParams(offset,count,data,posInfo); //继续解析
	}
	
	@Override
	public void release() {
		try {
			pos.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	enum Params{
		NULL(0,false),
		
		/**商户名称*/
		CUSTOMER_NAME(0x1A01,true), 
		/**商户编号*/
		CUSTOMER_NUMBER(0x1A02,true),
		/**终端编号*/
		POS_CENTER_NUMBER(0x1A03,true),
		
		/**收单行代码*/
		ACQUIRERCODE(0x1A04,true),
		/**初始流水号*/
		INITSERINO(0x1A06,false),
		/**初始批次号*/
		INITBATHNO(0x1A07,false),
		
		/**终端主密钥密文*/
		MAINKEY(0x1A12,false),
		
		/**软件版本号*/
		SOFTVERSION(0x1A13,true),
		/**参数版本号*/
		PARAMVERSION(0x1A14,true),
		
		/**终端序列号*/
		POS_SERIALNUMBER(0x1A16,true),
		/**操作员*/
		OPERATOR(0x1A05,true);
		
		public static Params convert(int value){
			for(Params e : Params.values())
				if(e.value == value)
					return e;
			return NULL;
		}
		
		private final int value;
		private final boolean canRead;
		
		Params(int value,boolean canRead){
			this.value = value;
			this.canRead = canRead;
		}
		public int getValue() {
			return value;
		}
		public boolean canRead() {
			return canRead;
		}
	}
}
