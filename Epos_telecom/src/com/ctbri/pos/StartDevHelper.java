package com.ctbri.pos;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import com.ctbri.Constants;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;

/**
 * 实达pos 帮助 类
 * @author qin
 * 
 * 2012-12-22
 */
public class StartDevHelper {
	
	private final static String BR ="\r\n";
	
	
	/**
	 * 生成打印数据
	 * @param elements 获取的交易数据
	 * @param is       输出的内容
	 * @param reprint  是否重打印
	 * @return
	 */
	 public static TransResponse genPrintPkg(TLV tlv,OutputStream os,boolean reprint){
		 
		 TransResponse response = new TransResponse();
		 if(tlv.getElements()==null || tlv.getElements().size()==0 || os ==null) //打印失败
			 return TransResponse.getErrorResponse(TransResponse.class, ResponseCode.PRINT_FAIL);
		 
		 //组织打印 和返回 数据
		 StringBuilder sb = new StringBuilder();
		 
		 sb.append("        翼支付POS签购单").append(BR);
		 sb.append("  商户存根           请妥善保管").append(BR);
		 sb.append("--------------------------------").append(BR);
		 
		 //打印主体部分
		 StringBuilder body = new StringBuilder();
		 
		 //商户名称 
		 String customerName =  tlv.getValue(0x9F01);
		 customerName = customerName==null ? "翼支付" : customerName;
		 body.append("商户名称:").append(customerName).append(BR);
		 body.append("商户编号:").append(tlv.getValue(0x9F02)).append(BR);
		 body.append("终端编号:").append(tlv.getValue(0x9F03)).append(BR);
		 //收单行号
		 response.setAcquirer(tlv.getValue(0x9F04));
		 body.append("收单行号:").append(response.getAcquirer()).append(BR);
		 //发卡行号
		 response.setPushCardNo(tlv.getValue(0x9F05));
		 body.append("发卡行号:").append(response.getPushCardNo()).append(BR);
		 //卡号
		 response.setCard(tlv.getValue(0x9F07));
		 body.append("卡    号:").append(response.getFormatCard()).append(BR);
		 //操作员号
		 body.append("操作员号:").append(format(tlv.getValue(0x9F08))).append(BR);
		 //交易类型
		 
		 String hexTransType = tlv.getValue(0x9F09);
		 //hexTransType = hexTransType==null ? "00" : hexTransType;
		 //int messageType =   (hexTransType.charAt(0)-48) << 4 | (hexTransType.charAt(1)-48);
		 //response.setMessageType(hexTransType);
		 body.append("交易类型:").append(hexTransType).append(BR);
		 //卡有效期
		 response.setCardExpDate(tlv.getValue(0x9F0A));
		 body.append("卡有效期:").append(response.getCardExpDate()).append(BR);
		 //批次号
		 response.setBatchNo(tlv.getValue(0x9F0B));
		 body.append("批 次 号:").append(format(response.getBatchNo())).append(BR);
		 //凭证号
		 response.setVoucherNo(tlv.getValue(0x9F0C));
		 body.append("凭 证 号:").append(response.getVoucherNo()).append(BR);
		 //交易时间
		 response.setTransDatetime(tlv.getValue(0x9F0D));
		 body.append("交易时间:").append(response.getDateTime()).append(BR);
		 //参考号
		 response.setTransNumber(tlv.getValue(0x9F0F));
		 body.append("参 考 号:").append(response.getTransNumber()).append(BR);
		 //总金额
		 String money = tlv.getValue(0x9F13);
		 response.setMoney(Long.parseLong(money));
		 body.append("总 金 额:").append(response.getMoneyString()).append(BR);
		 //备注
		 response.setRemark(tlv.getValue(0x9F15));
		 body.append("备    注:");
		 if(reprint)
			 body.append("重打").append(BR);
		 body.append(format(response.getRemark())).append(BR).append(BR);
		 
		 sb.append(body.toString());
		 sb.append("持卡人签名:").append(BR).append(BR).append(BR);
		 
		 sb.append("本人确认以上交易同意记入本人账户").append(BR).append(BR).append(BR)
		 											.append(BR).append(BR);
		 
		 sb.append("        翼支付POS签购单").append(BR);
		 sb.append("  用户存根          请妥善保管").append(BR);
		 sb.append("--------------------------------").append(BR);
		 sb.append(body.toString());
		 
		 try {
			os.write(sb.toString().getBytes(Constants.CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	 }
	 
	 public static String format(String value){
		 if(value==null)
			 return "";
		 else
			 return value;
	 }
 
}
