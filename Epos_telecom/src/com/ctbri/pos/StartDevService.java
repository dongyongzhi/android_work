package com.ctbri.pos;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.os.RemoteException;

import com.ctbri.Constants;
import com.ctbri.ElecException;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.PrintResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TLVElement;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.ISO8583Util;
import com.ctbri.utils.StringUtil;
import com.yifeng.hd.utils.StringUtils;
import com.yifeng.start.IStartDevService;

public class StartDevService implements ElecPosService {

	private final static int BUF = 100;
	private final static String READ_PARAMS = "F0";  //读取参数
	private final static String WRITE_PARAMS = "F1"; //写入参数
	private final static String WRITE_PARAMS_FILE = "F5";//写入参数文件
	private final static String READ_TRANS_TLV = "F6";//读取末笔交易 tlv
	
	private final static String ORDER_PAY = "16";//订单支付
	
	
	private final IStartDevService pos;

	public StartDevService(IStartDevService pos) {
		this.pos = pos;
		if (pos == null)
			throw new IllegalArgumentException(" not find start pos");
	}

	@Override
	public POSTransResponse transRequest(POSTransRequest req) {
		ElecLog.d(getClass(), ">>>>>>>>>>请求pos机数据");

		POSTransResponse response = new POSTransResponse(); //返回数据
		response.setMessageType(req.getMessageType());
		
		ByteArrayOutputStream inBuf = new ByteArrayOutputStream(BUF);
		
		try {
			String transType = convertTransType(req.getMessageType());
			if(transType==null || "".equals(transType))
				throw new ElecException("POS终端不支持此功能！");
			
			//判断是否订单消费
			if(req.getMessageType()== MessageType.PURCHASE && req.getSignName()!=null &&
					!"".equals(req.getSignName())){
				transType = ORDER_PAY;  //订单消费还签名和订单号
			}
			
			//前37个字节
			inBuf.write(StringUtil.leftAddZero(req.getMoney(),12).getBytes()); //金额
			inBuf.write(StringUtil.rightAddSpace(req.getCustomerNumber(), 15).getBytes());//商户编号
			inBuf.write(StringUtil.rightAddSpace(req.getPosNumber(), 8).getBytes());//终端编号
			inBuf.write(transType.getBytes()); //交易类型
			
			//附加数据
			//=========(订单支付信息)===========
			 
			if(ORDER_PAY.equals(transType)){
				List<TLVElement> elements  = new ArrayList<TLVElement>();
				TLVElement tlv;
				if(req.getOrderCode()!=null && !"".equals(req.getOrderCode())){
					//订单号
					tlv = new TLVElement();
					tlv.setTag(0x9F32);
					tlv.setValue(req.getOrderCode());
					elements.add(tlv);
				}
				if(req.getSignName()!=null && !"".equals(req.getSignName())){
					//签名信息
					tlv = new TLVElement();
					tlv.setTag(0x9F33);
					tlv.setValue(req.getSignName());
					elements.add(tlv);
				}
				//写入支付信息
				byte[] tlvBytes = TLV.encoder(elements);
				if(tlvBytes!=null && tlvBytes.length!=0)
					inBuf.write(tlvBytes);
			} 
			
			//退货业务
			if(req.getMessageType() == MessageType.RETURNS){
				inBuf.write(new byte[8]);//操作员和密码信息不带
				inBuf.write(StringUtil.rightAddSpace(req.getReferenceNumber(), 12).getBytes());//参数号
				//原批次号
				if(req.getOriginalBatchNumber()!=null && !"".equals(req.getOriginalBatchNumber())){
					inBuf.write(req.getOriginalBatchNumber().getBytes());
				}else
					inBuf.write(new byte[6]);
				//原流水号
				if(req.getOriginalSerialNumber()!=null && !"".equals(req.getOriginalSerialNumber())){
					inBuf.write(req.getOriginalSerialNumber().getBytes());
				}else
					inBuf.write(new byte[6]);
				//原交易日期
				if(req.getOriginalTransDate()!=null && !"".equals(req.getOriginalTransDate())){
					inBuf.write(req.getOriginalTransDate().getBytes());
				}else
					inBuf.write(new byte[4]);
			}
			
			//撤销业务  
			if(req.getMessageType() == MessageType.CANCEL_PURCHASE){
				inBuf.write(new byte[8]);//操作员和密码信息不带
				
				//凭证号  即 pos流水
				if(req.getOriginalSerialNumber()!=null && !"".equals(req.getOriginalSerialNumber())){
					inBuf.write(req.getOriginalSerialNumber().getBytes());
				}else
					inBuf.write(new byte[6]);
			}
			
			//输出参数
			byte[] outData = new byte[Constants.OUT_BUFFER];
			
			ElecLog.d(getClass(), " send to pos:"+StringUtils.printBytes(inBuf.toByteArray()));
			
			int result = pos.startDevPackData(inBuf.toByteArray(),outData);
			if(result == 0){ //返回成功
				//获取返回码
				ResponseCode code = converResonseCode(new String(outData,0,2));//交易结果
				response.setState(code.getValue()); //设定返回码
				
				//组包成功
				if(code == ResponseCode.SUCCESS){
					int len = (outData[2] & 0xFF)<<8 | (outData[3] & 0xFF);
					ElecLog.d(getClass(), "======>"+StringUtils.printBytes(outData,4,len));
					
					//组织返回数据
					response.setLen(len);
					response.setData(new byte[len]);
					System.arraycopy(outData, 4, response.getData(), 0, len);
					
				}else if(code== ResponseCode.TRANS_NO_SIGN){ //终端未签到，获取签到数据
					POSTransRequest  dest = new POSTransRequest();
					dest.setMessageType(MessageType.SIGN);
					
					response = transRequest(dest);
					response.setState(code.getValue());//设置未签到 返回码
					
				}else if(code== ResponseCode.TRANS_REVERSAL){ //终端需要冲正
					POSTransRequest  dest = new POSTransRequest();
					dest.setMessageType(MessageType.REVERSAL);
					
					response = transRequest(dest);
					response.setState(code.getValue());//设置冲正 返回码
				}
			}else
				response.setState(ResponseCode.CONNECTION_FAIL.getValue());//执行失败
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**解包数据*/
	@Override
	public POSTransResponse transResponse(byte[] data) {
		ElecLog.d(getClass(), ">>>>>>>>>>8583 交易包 返回 给 pos机");
		 //执行，并返回结果 
		POSTransResponse  resp = new POSTransResponse();
		byte[] outData = new byte[Constants.OUT_BUFFER];
		try {
			//pos返回成功
			if(pos.startDevUnpackData(data, outData)==0){  //返回成功
				ResponseCode code = converResonseCode(new String(outData,0,2));//交易结果
				resp.setState(code.getValue());
				
				if(code == ResponseCode.SUCCESS){
					
					//解析8583包,获取交易信息
	    			TransResponse item = ISO8583Util.parse(data);
	    			
	    			 //银联返回正确 并且不是查询金额、冲正、签到时需要 打印票据
	    			if(item.getErrCode()==0 && item.getMessageType()!= MessageType.QUERYBALANCE
	    					&& item.getMessageType()!=MessageType.SIGN && item.getMessageType()!=MessageType.REVERSAL){ 
	    				TransResponse printResult =	printTransResult(false);
	    				
	    				printResult.setMessageType(item.getMessageType());  //交易类型
	    				printResult.setTransDate(item.getTransDate());      //交易日期
	    				
	    				resp.setTransResponse(printResult);
	    				
	    			}else
	    				resp.setTransResponse(item);
	    			
				}else if(code == ResponseCode.TRANS_DATA_RESENT){  //需重新上传交易数据
					int len = (outData[2] & 0xFF)<<8 | (outData[3] & 0xFF); //数据长度
					resp.setLen(len);
					resp.setData(new byte[len]);
					System.arraycopy(outData, 4, resp.getData(), 0, len);  //返回要上交易的数据
				}
			}else{
				resp.setState(ResponseCode.CONNECTION_FAIL.getValue());//执行失败
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return resp;
	}

	/**读取终端参数*/
	@Override
	public POSInfo getPosInfo() {
		ByteArrayOutputStream inBuf = new ByteArrayOutputStream();
		POSInfo posInfo = new POSInfo();
		try {
			
			inBuf.write(new byte[35]);
			inBuf.write(READ_PARAMS.getBytes()); //读取POS参数
			//inBuf.write(new byte[BUF-37]);
			
			//写入要获取的参数
			for(Params  params : Params.values()){
				if(params!=Params.NULL && params.canRead){
					inBuf.write(params.getValue()>> 8);
					inBuf.write(params.getValue());
				}
			}
			byte[] outData = new byte[Constants.OUT_BUFFER];
			byte[] inData = inBuf.toByteArray();
			ElecLog.d(getClass(), "======>"+StringUtils.printBytes(inData));
			
			int result = pos.startDevPackData(inData, outData);
			
			if(result==0){  //返回成功
				ResponseCode code = converResonseCode(new String(outData,0,2));//交易结果
				if(code==ResponseCode.SUCCESS){
					int len = (outData[2] & 0xFF)<<8 | (outData[3] & 0xFF);
					ElecLog.d(getClass(), "======>"+StringUtils.printBytes(outData,4,len));
					parseParams(4,len,outData,posInfo);//解析tlv 格式参数
					
					//获取 pos机地址
					posInfo.setAddress(pos.getPOSAddress());
					//pos名称
					posInfo.setPosName(Constants.START_POS_MODE);
					posInfo.setRemark("支持磁条卡、IC卡、PIN输入、显示、打印");
					posInfo.setCommType(Constants.POS_COMM_TYPE);
					posInfo.setSupportPrint(true);//是否支持打印
					
				}else{
					posInfo.setErrCode(code.getValue());
					posInfo.setErrMsg(code.getMessage());
				}
			}else{
				posInfo.setErrCode(ResponseCode.CONNECTION_FAIL.getValue());
				posInfo.setErrMsg(ResponseCode.CONNECTION_FAIL.getMessage());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			posInfo.setErrCode(ResponseCode.CONNECTION_FAIL.getValue());
			posInfo.setErrMsg(ResponseCode.CONNECTION_FAIL.getMessage());
			
		} catch (RemoteException e) {
			e.printStackTrace();
			posInfo.setErrCode(ResponseCode.CONNECTION_FAIL.getValue());
			posInfo.setErrMsg(ResponseCode.CONNECTION_FAIL.getMessage());
		}
		return posInfo;
	}

	/***
	 * 写入参数
	 */
	@Override
	public ResponseCode writePOSParams(POSInfo info) {
		ElecLog.d(getClass(), ">>>>>>>>>>写入pos参数");
		if(info==null)
			return ResponseCode.CONNECTION_FAIL;
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try {
			 
			buf.write(new byte[35]);
			buf.write(WRITE_PARAMS.getBytes());
			
			//写入要获取的参数
			byte[] temp;
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
		
		try {
			byte[] data = buf.toByteArray();
			byte[] outData = new byte[Constants.OUT_BUFFER];
			
			ElecLog.d(getClass(), " writePOSParams :"+StringUtils.byteToHex(data));
			
			if(pos.startDevPackData(data, outData)==0){  //写入参数成功
				return converResonseCode(new String(outData,0,2));//交易结果
			}else
				return ResponseCode.CONNECTION_FAIL;//执行失败
			
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new ElecException(ResponseCode.EXEC_CMD_FAIL.getMessage());
		}
	}

	@Override
	public ResponseCode writePOSParams(byte[] data) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		
		try {
			buf.write(new byte[35]);
			buf.write(WRITE_PARAMS_FILE.getBytes());
			
			buf.write(data);
			byte[] outData = new byte[Constants.OUT_BUFFER];
			
			ElecLog.d(getClass(), " writePOSParams :"+StringUtils.byteToHex(buf.toByteArray()));
			
			if(pos.startDevPackData(buf.toByteArray(), outData)==0){  //写入参数成功
				return converResonseCode(new String(outData,0,2));//交易结果
			}else
				return ResponseCode.CONNECTION_FAIL;//执行失败
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new ElecException(ResponseCode.EXEC_CMD_FAIL.getMessage());
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new ElecException(ResponseCode.EXEC_CMD_FAIL.getMessage());
		}
	}
	
	@Override
	public void endPosCmd() {
		
	}

	@Override
	public PrintResponse printLastTrans() {
		PrintResponse response = new PrintResponse();
		TransResponse printResult =	printTransResult(true);
		response.setErrCode(printResult.getErrCode());
		response.setErrMsg(printResult.getErrMsg());
		return response;
	}
	
	/**
	 * 打印交易结果 
	 * @return
	 */
	private TransResponse printTransResult(boolean reprint){
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try {
			ElecLog.d(getClass(), ">>>>>>>>>读取 POS TLV 格式交易返回内容");
			buf.write(new byte[35]);
			buf.write(READ_TRANS_TLV.getBytes());
			
			//获取参数
			buf.write(TLV.request(0x9F01,0x9F02,0x9F03,0x9F04,0x9F05,0x9F07,0x9F08,0x9F0A,0x9F0B,
								  0x9F0C,0x9F0D,0x9F15,0x9F0F,0x9F09,0x9F13));
			
			byte[] inBuf = buf.toByteArray();
			ElecLog.d(getClass(), "======>"+StringUtils.printBytes(inBuf));
			
			byte[] outData = new byte[Constants.OUT_BUFFER];
			int result = pos.startDevPackData(inBuf, outData);
			
			if(result==0){  //返回成功
				ResponseCode code = converResonseCode(new String(outData,0,2));//返回结果
				if(code==ResponseCode.SUCCESS){
					int len = (outData[2] & 0xFF)<<8 | (outData[3] & 0xFF);
					ElecLog.d(getClass(), "======>"+StringUtils.printBytes(outData,4,len));
					
					//解析tlv 格式参数
					TLV tlv = new TLV();
					tlv.decoder(4, len, outData);
					
					//生成打印数据
					ByteArrayOutputStream print = new ByteArrayOutputStream();
					print.write(new byte[2]); //2字节长度
					TransResponse response = StartDevHelper.genPrintPkg(tlv, print,reprint); //获取打印内容
					
					byte[] printBytes = print.toByteArray();
					int printlen = printBytes.length - 2;   //打印数据长度
					printBytes[0] = (byte)(printlen>>8);
					printBytes[1] = (byte)printlen;
					
					//打印数据
					if(printASCII(printBytes)!=0)
						response.setPrint(false);
					else
						response.setPrint(true);
					
					return response;  //返回解析的结果
					
				}else
					return ElecResponse.getErrorResponse(TransResponse.class,code);
				
			}else{
				return ElecResponse.getErrorResponse(TransResponse.class,ResponseCode.CONNECTION_FAIL);
			}
			
		} catch (IOException e) {
			ElecLog.w(TLV.class, e);
		} catch (RemoteException e) {
			ElecLog.w(TLV.class, e);
		}
		return ElecResponse.getErrorResponse(TransResponse.class,ResponseCode.CONNECTION_FAIL);
	}
	
	/**
	 * 打印字符串
	 * @param data  要打印的数据    2个字节长度+ 数据内容
	 * 
	 * @return 0为成功，其它为失败
	 */
	private int printASCII(byte[] data){
		byte[] outBuf = new byte[Constants.OUT_BUFFER];
		try {
			int result = pos.startDevPrint(data, outBuf);
			if(result == 0){
				//取出返回码
				String printCode = new String(outBuf,0,2);
				if(printCode.equals("00"))
					 return 0;
			} 
		} catch (RemoteException e) {
		}
		return 1;
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
				 
			 default:
				 offset++;
				break;
			 }
		}
		offset +=len;
		parseParams(offset,count,data,posInfo); //继续解析
	}
	
	/**
	 * 交易类型转换成实达 pos 交易参数
	 * @param messageType
	 * @return
	 */
	private String convertTransType(int messageType){
		if(messageType == MessageType.SIGN) //联机签到
			return "00";
		else if(messageType == MessageType.PURCHASE) //消费
			return "01";
		else if(messageType == MessageType.REVERSAL) //冲正
			return "02";
		else if(messageType == MessageType.CANCEL_PURCHASE)//撤销
			return "03";
		else if(messageType == MessageType.QUERYBALANCE) //余额查询	
			return "04";
		else if(messageType == MessageType.RETURNS) //退货
			return "12";
 		else if(messageType == MessageType.READPARAMS) //读取POS参数
			return "F0";
 		else if(messageType == MessageType.UPDATEPARAMETER) //写入参数
 			return "F1";
 		else
 			return null;
	}	
	
	/**
	 * 编码转换
	 * @param code  原始数据
	 * @return
	 */
	private ResponseCode converResonseCode(String code){
		if(code==null)
			return ResponseCode.TRANS_HARDWARE;
		
		if(code.equals("00"))        //表示组包成功
			return ResponseCode.SUCCESS;
		else if(code.equals("01"))   //表示用户取消交易或操作超时。
			return ResponseCode.CANCEL;
		else if(code.equals("02"))  //表示PTE终端硬件错误。
			return ResponseCode.TRANS_HARDWARE;
		else if(code.equals("03"))  //表示PTE终端未签到
			return ResponseCode.TRANS_NO_SIGN;
		else if(code.equals("04")) //表示PTE终端需要冲正
			return ResponseCode.TRANS_REVERSAL;
		else if(code.equals("05")) //表示PTE终端密码输入错误，用户已经重新输入密码，需要宿主重新上送数据包（数据包在数据中）。
			return ResponseCode.TRANS_DATA_RESENT ;
		
		else if(code.equals("06")) //表示PTE终端不需要冲正
			return ResponseCode.SUCCESS;
		else if(code.equals("07"))  //表示商户号或终端号不正确
			return ResponseCode.TRANS_NOEXISTS_POSNO;
		else if(code.equals("08"))   //表示数据有误，PTE解析失败用于带数据的交易类型
			return ResponseCode.TRANS_ERROR;
		
		else
			return ResponseCode.TRANS_FAIL;
	}
	
	
	enum Params{
		NULL(0,false),
		
		/**商户名称*/
		CUSTOMER_NAME(0x9F01,true), 
		/**商户编号*/
		CUSTOMER_NUMBER(0x9F02,true),
		/**终端编号*/
		POS_CENTER_NUMBER(0x9F03,true),
		
		/**收单行代码*/
		ACQUIRERCODE(0x9F04,true),
		
		/**终端主密钥密文*/
		MAINKEY(0x9F2A,false),
		
		/**软件版本号*/
		SOFTVERSION(0x9F2B,true),
		/**参数版本号*/
		//PARAMVERSION(0x1A14,true),
		
		/**终端序列号*/
		POS_SERIALNUMBER(0x9F2C,true),
		/**操作员*/
		OPERATOR(0x9F08,true);
		
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


	@Override
	public void release() {
		try {
			pos.close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}


}
