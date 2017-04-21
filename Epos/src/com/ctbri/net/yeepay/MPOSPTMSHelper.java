package com.ctbri.net.yeepay;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.ctbri.Constants;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.ResponseCode;
import com.ctbri.net.yeepay.tms.TMSDownResponse;
import com.ctbri.net.yeepay.tms.TMSResponse;
import com.ctbri.utils.ElecLog;
import com.yfcomm.pos.utils.ByteUtils;

/**
 * 易宝 tms 报文接口
 * 
 * @author qin
 * 
 *         2012-12-19
 */
public class MPOSPTMSHelper {

	private final static String TPDU = "6000040000";

	public final static String UPDATEPARAMETER = "1"; // 更新参数
	public final static String DOWNLOAD = "0";// 更新软件

	private final static String TMS_UPDATE_REQUEST = "10"; // POS更新请求
	private final static String TMS_DOWN_REQUEST = "11";// POS下载请求
	
	private final static String SUCCESS = "00";//返回成功
	
	private final static int BUF_SIZE = 1024; //下载缓冲区大小
	
	public final static Map<String,String> errMap = new HashMap<String,String>(9);
	
	static{
		errMap.put("01", "未知终端号");
		errMap.put("02", "软件为最新版本，不需要更新");
		errMap.put("03", "参数为最新版本，不需要更新");
		errMap.put("04", "服务器拒绝更新");
		errMap.put("05", "系统忙");
		errMap.put("06", "找不到下载版本");
		errMap.put("07", "未知通知类型");
		errMap.put("08", "Lrc校验位错误");
		errMap.put("09", "不需要更新");
	}

	public static byte[] requestInitTMS(POSInfo info) {
		if(info==null)
			return null;
		
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		// 二个字节长度
		buf.write(0);
		buf.write(0);
		try {
			// tpdu
			buf.write(ByteUtils.hexToByte(TPDU));
			// 二个字节长度
			buf.write(0);
			buf.write(0);

			buf.write(TMS_UPDATE_REQUEST.getBytes());// 命令码
			// POS终端号
			if(info.getPosNumber()==null)
				return null;
			buf.write(info.getPosNumber().getBytes());
			// 软件版本号
			buf.write(rightAddSpace(info.getSoftVersion(), 20).getBytes());
			// 参数版本号
			buf.write(rightAddSpace(info.getParamVersion(), 20).getBytes());

			return fullRequest(buf);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 初始化应答返回
	 * 
	 * @param source
	 *            数据源
	 * @return 返回内容
	 */
	public static TMSResponse responseInitTMS(byte[] source) {
		
		if(!validateResponse(source))
			return ElecResponse.getErrorResponse(TMSResponse.class,ResponseCode.POSP_CLIENT_FAIL);

		// 响应码
		String responseCode = new String(source, 9, 2);
		if(responseCode==null || !responseCode.equals(SUCCESS))
			return ElecResponse.getErrorResponse(TMSResponse.class, responseCode, errMap.get(responseCode));
			 
		TMSResponse response = new TMSResponse();
		// 软件任务ID
		response.setSoftTaskId(new String(source, 11, 8));
		// 新软件版本号
		response.setSoftVersion(new String(source, 19, 20));
		// 软件长度
		response.setSoftLenght(new String(source, 39, 8));
		// 参数任务ID
		response.setParamTaskId(new String(source, 47, 8));
		// 新参数版本号
		response.setParamVersion(new String(source, 55, 20));
		// 参数长度
		response.setParamLength(new String(source, 75, 8));

		return response;
	}

	/**
	 * tms 下载报文
	 * 
	 * @param pos
	 *            pos信息
	 * @param response
	 *            tms 返回的报文
	 * @param type
	 *            下载数据
	 * @param start
	 *            开始位置
	 */
	public static byte[] requestDownTMS(POSInfo pos, TMSResponse response,String type, int start) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		// 报文长度
		buf.write(0);
		buf.write(0);
		// tpdu
		try {
			buf.write(ByteUtils.hexToByte(TPDU));
			// 二个字节长度
			buf.write(0);
			buf.write(0);

			buf.write(TMS_DOWN_REQUEST.getBytes()); // 命令码
			buf.write(pos.getPosNumber().getBytes());// POS终端号

			if (type.equals(UPDATEPARAMETER)) { // 参数更新
				buf.write(response.getParamTaskId().getBytes());// tms 返回的任务ID
				buf.write(rightAddSpace(pos.getParamVersion(), 20).getBytes());// 旧版本号
				buf.write(response.getParamVersion().getBytes());// tms 返回新版本
				buf.write(UPDATEPARAMETER.getBytes());// 下载类型
				
				buf.write(leftAddZero(start,8).getBytes());//起始位
				buf.write(leftAddZero(BUF_SIZE,4).getBytes());//可下载的最大长度
				
				//返回
				return fullRequest(buf);
				
			} else {
				
				buf.write(response.getSoftTaskId().getBytes());// tms 返回的任务ID
				buf.write(rightAddSpace(pos.getSoftVersion(), 20).getBytes());// 旧版本号
				buf.write(response.getSoftVersion().getBytes());// tms 返回新版本
				buf.write(DOWNLOAD.getBytes());// 下载类型
				
				buf.write(leftAddZero(start,8).getBytes());//起始位
				buf.write(leftAddZero(BUF_SIZE,4).getBytes());//可下载的最大长度
				
				//返回
				return fullRequest(buf);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 下载响应
	 */
	public static TMSDownResponse responseDownTMS(byte[] source){
		/**
		if(!validateResponse(source))
			return ElecResponse.getErrorResponse(TMSDownResponse.class,1,POSPSetting.POSP_CLIENT_FAIL);
		**/
		String responseCode = new String(source, 9, 2);
		//成功
		if(responseCode.equals(SUCCESS)){
			String dataLength = new String(source,11,4);// 数据长度  不够，左补0
			//byte[] data =  new byte[Integer.parseInt(dataLength)];//数据长度
			TMSDownResponse downResponse = new TMSDownResponse();
			downResponse.setLength(Integer.parseInt(dataLength));
			downResponse.setData(new byte[downResponse.getLength()]);
			System.arraycopy(source, 15, downResponse.getData(), 0, downResponse.getLength());
			
			return downResponse;
		}else
			return TMSDownResponse.getErrorResponse(TMSDownResponse.class, responseCode,errMap.get(responseCode));
		
	}
	
	/**
	 * 通知下载完成
	 * @param pos       pos 终端
	 * @param response  初始应答内容
	 * @return
	 */
	public static byte[] requestCompleteTMS(POSInfo pos, TMSResponse response){
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		// 二个字节长度
		buf.write(0);
		buf.write(0);
		try {
			// tpdu
			buf.write(ByteUtils.hexToByte(TPDU));
			// 二个字节长度
			buf.write(0);
			buf.write(0);

			buf.write(TMS_UPDATE_REQUEST.getBytes());// 命令码
			// POS终端号
			buf.write(pos.getPosNumber().getBytes());
			//软件任务ID
			buf.write(response.getSoftTaskId().getBytes());
			//软件版本号
			buf.write(response.getSoftVersion().getBytes());
			//参数任务ID
			buf.write(response.getParamTaskId().getBytes());
			//参数版本号
			buf.write(response.getParamVersion().getBytes());
			
			return fullRequest(buf);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解析 完成报文
	 * @param source
	 * @return
	 */
	public static ElecResponse responseCompleteTMS(byte[] source){
		 
		if(!validateResponse(source))
			return ElecResponse.getErrorResponse(ElecResponse.class,ResponseCode.POSP_CLIENT_FAIL);
	    
		String responseCode = new String(source,9,2);
		if(!responseCode.equals(SUCCESS))
			return ElecResponse.getErrorResponse(ElecResponse.class, responseCode, errMap.get(responseCode));
		else 
			return new ElecResponse();
	}
	
	
	public static POSInfo parse(byte[] buf){
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf),Constants.CHARSET));
			String line = null,name=null,value=null;
			int offset=0;
			POSInfo info = new POSInfo();
			while ((line = rd.readLine()) != null) { 
				ElecLog.d(MPOSPTMSHelper.class, line);
				offset = line.indexOf("=");
				if(offset>0){
					name = line.substring(0, offset);
					value = line.substring(offset+1);
					 
					if(name.equals("商户号")){
						info.setCustomerNumber(value);
					}else if(name.equals("终端号")){
						info.setPosNumber(value);
					}else if(name.equals("商户名称")){
						info.setCustomerName(value);
					}else if(name.equals("终端主密钥密文")){
						info.setMainkey(ByteUtils.hexToByte(value));
					}else if(name.equals("软件版本号")){
						info.setSoftVersion(value);
					}else if(name.equals("参数版本号")){
						info.setParamVersion(value);
					}
				}
		    } 
			return info;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return null;
	}
	
	/**
	 * 验证数据包
	 * @param source  应答报文
	 * @return
	 */
	private static boolean validateResponse(byte[] source){
		if (source == null || source.length < 3)
			return false;

		// 获取报文长度
		int msgLen = (source[0] & 0xFF) << 8 | (source[1] & 0xFF);
		if (source.length - 2 != msgLen) // 长度不对
			return false;

		if (9 > source.length) { // 无数据体
			return false;
		}
		// 数据长度
		int dataLen = (source[7] & 0xFF) << 8 | (source[8] & 0xFF);
		if (dataLen == 0)
			return false;

		// check lrc
		if (source[source.length - 1] != getLRC(source, 9, source.length - 1))
			return false;
		
		return true;
	}
	/**
	 * 填充完整数据 
	 * @param buf   
	 * @return
	 */
	private static byte[] fullRequest(ByteArrayOutputStream buf){

		byte[] dest = buf.toByteArray();
		
		// 数据长度
		int dataLen = dest.length - 9;
		int msgLen = dest.length - 2;
		
		// 计算lrc
		buf.write(getLRC(dest, 9, dest.length));

		byte[] source = buf.toByteArray();
		source[0] = (byte) (msgLen >> 8);
		source[1] = (byte) msgLen;

		// 数据长度
		source[7] = (byte) (dataLen >> 8);
		source[8] = (byte) dataLen;
		
		return source;
	}

	private static String rightAddSpace(String source, int length) {
		if (source == null)
			source = "";

		if (source.length() > length) {
			return source.substring(0, length);
		} else if (source.length() == length) {
			return source;
		} else {
			return String.format(String.format("%%-%ds", length), source); // 字母向左靠，右部多余部分填空格。
		}
	}

	private static String leftAddZero(int number, int length) {
		char[] c = new char[length];
		char[] x = String.valueOf(number).toCharArray();
		if (x.length > length) {
			return String.valueOf(number).substring(0, length);
		}
		int lim = c.length - x.length;
		for (int i = 0; i < lim; i++) { // 数字靠右，左补0
			c[i] = '0';
		}
		System.arraycopy(x, 0, c, lim, x.length);
		return new String(c);
	}
	/**
	 * 获取 lrc
	 * @param source
	 * @param offset
	 * @param end
	 * @return
	 */
	private static byte getLRC(byte[] source, int offset, int end) {
		byte xor = 0;
		for (int i = offset; i < end; i++) {
			xor ^= source[i];
		}
		return xor;
	}
}
