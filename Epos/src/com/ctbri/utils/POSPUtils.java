package com.ctbri.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.yfcomm.pos.utils.ByteUtils;
import com.yifeng.commonutil.Base64;

import android.util.Log;

import com.ctbri.Constants;
import com.ctbri.domain.POSTransResponse;

public final class POSPUtils {

	private final static String TAG = POSPUtils.class.getName();
	private final static String[] PIC_TYPE = new String[]{".jpg",".png"};

	/**
	 * 生成 TPDU
	 * 
	 * @return
	 */
	public static String genTPDU() {
		byte[] tpdu = new byte[5];
		tpdu[0] = 0x60;
		tpdu[1] = 0x00;
		tpdu[2] = 0x03;
		tpdu[3] = 0x00;
		tpdu[4] = 0x00;

		try {
			return new String(tpdu, Constants.PACK_CHARSET);
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 获取 交易参数 用于posp 通信
	 * 
	 * @param req
	 * @return
	 */
	public static Map<String, Object> getTransParams(POSTransResponse resp) {
		Map<String, Object> params = new HashMap<String, Object>(1);
		try {
			String paydata = new String(resp.getData(), 0, resp.getLen(),
					Constants.PACK_CHARSET);
			params.put(Constants.POSP_TRANS_DATA_REQUEST_KEY, paydata);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage(), e);
			e.printStackTrace();
		}
		return params;
	}

	/**
	 * 获取交易数据
	 * 
	 * @param resp
	 * @return
	 */
	public static String getPayData(POSTransResponse resp) {
		return ByteUtils.byteToHex(resp.getData(), 0, resp.getLen()); // 转换成hex
																		// 传至posp
	}

	/**
	 * sha-1 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String digest(String str) {
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA");
			sha.update(str.getBytes());
			return Base64.encodeToString(sha.digest(), Base64.NO_WRAP);
		} catch (NoSuchAlgorithmException e) {
			ElecLog.e(POSPUtils.class, e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * 获取公告里的图片
	 * @param notice
	 * @return
	 */
	public static String getPic(String notice){
		if(notice==null ||"".equals(notice))
			return null;
		
		notice = notice.toLowerCase();
		int offset = notice.indexOf("http");
		if(offset<0)
			return null;
		
		notice = notice.substring(offset);
		int end = 0;
		for(String e : PIC_TYPE){
			end = notice.indexOf(e);
			if(end>0){
				end += e.length();
				break;
			}
		}
		if(end <=0)
			return null;
		
		return notice.substring(0, end);
	}
}
