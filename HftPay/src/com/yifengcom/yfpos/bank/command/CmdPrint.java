package com.yifengcom.yfpos.bank.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import net.mfs.util.StringUtil;

import org.apache.mina.core.buffer.IoBuffer;

import com.yifengcom.yfpos.bank.EposProtocol;
import com.yifengcom.yfpos.utils.ByteUtils;

import android.graphics.Bitmap;
import android.util.Log;

public class CmdPrint extends EposCommand {
	public int Len;

	private Charset charset = Charset.forName("GB2312");

	public ArrayList<String> getPrintList() {
		return printList;
	}

	public void setPrintList(ArrayList<String> printList) {
		this.printList = printList;
	}

	private ArrayList<String> printList = new ArrayList<String>();
	public ArrayList<PrintRecord> printRecords = new ArrayList<PrintRecord>();
	ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
	private HashMap<String, String> modalList = new HashMap<String, String>();

	private HashMap<String, String> rcv_print = new HashMap<String, String>();

	public void addTemplete(int index, String s) {
		modalList.put(String.format("%d", index), s);
	}

	public CmdPrint() {
		command = EposProtocol.CMD2_PRINT;
		addTemplete(0x01, "2D2DD6D0B9FAD2F8C1AABDBBD2D7C6BECCF52D2D");// --中国银联交易凭条--
		addTemplete(0x02, "2D2DD6D0B9FAD2F8C1AAC9CCBBA7B4E6B8F92D2D");// --中国银联商户存根--
		addTemplete(0x03, "2D2DB3D6BFA8C8CBB4E6B8F92D2D");// --持卡人存根--
		addTemplete(0x04, "B3D6BFA8C8CBC7A9D7D6A3BA");// 持卡人签字：
		addTemplete(0x05, "CFB5CDB3B2CEBFBCBAC5A3BA");// 系统参考号：
		addTemplete(0x06, "BDBBD2D7C0E0D0CDA3BA");// 交易类型：
		addTemplete(0x07, "C8D5C6DA2FCAB1BCE4A3BA");// 日期/时间：
		addTemplete(0x08, "BDBBD2D7B2CEBFBCBAC5A3BA");// 交易参考号：
		addTemplete(0x09, "CAB5BCCABDBBD2D7BDF0B6EEA3BA");// 实际交易金额：
		addTemplete(0x0A, "D3E0B6EEA3BA");// 余额：
		addTemplete(0x0B, "BBFDB7D6A3BA");// 积分：
		addTemplete(0x0C, "C7B7B7D1A3BA");// 欠费：
		addTemplete(0x0D, "BFA8BAC5A3BA");// 卡号：
		addTemplete(0x0E, "D7AAC8EBBFA8BAC5A3BA");// 转入卡号：
		addTemplete(0x0F, "D7AAB3F6BFA8BAC5A3BA");// 转出卡号：
		addTemplete(0x10, "B8B6BFEEBFA8BAC5A3BA");// 付款卡号：
		addTemplete(0x11, "B8B6BFEED5CBBAC5A3BA");// 付款账号：
		addTemplete(0x12, "D6D0BDB1D0C5CFA2A3BA");// 中奖信息：
		addTemplete(0x13, "BDC9B7D1BAC5C2EBA3BA");// 缴费号码：
		addTemplete(0x14, "B3E4D6B5BAC5C2EBA3BA");// 充值号码：
		addTemplete(0x15, "CAD6BBFABAC5C2EBA3BA");// 手机号码：
		addTemplete(0x16, "B6A9B5A5BAC5A3BA"); // 订单号：
		addTemplete(0x17, "D7AAC8EBD5CBBAC5A3BA");// 转入账号：
		addTemplete(0x18, "D6D5B6CBBBFABAC5A3BA");// 终端机号：
		addTemplete(0x19, "C5FAB4CEBAC5C2EBA3BA");// 批次号码：
		addTemplete(0x1A, "D3D0D0A7C6DAA3BA");// 有效期：
		addTemplete(0x1B, "B2E9D1AFBAC5A3BA");// 查询号：
		addTemplete(0x1C, "D0F2BAC5A3BA");// 序号：
		addTemplete(0x1D, "CADAC8A8BAC5A3BA");// 授权号：
		addTemplete(0x1E, "CCD8D4BCC9CCBBA7C3FBB3C6A3BA");// 特约商户名称：
		addTemplete(0x1F, "CCD8D4BCC9CCBBA7B1E0BAC5A3BA");// 特约商户编号：
		addTemplete(0x20, "2D2DCDACD2E2D6A7B8B6C9CFCAF6BFEECFEE2D2D");// --同意支付上述款项--
		addTemplete(0x22, "B1B8D7A2A3BA");// 备注：
		addTemplete(0x23, "BFA8C3DC5FBFA8BAC5A3BA");// 卡密_卡号：
		addTemplete(0x24, "BFA8C3DC5FC3DCC2EBA3BA");// 卡密_密码：
		addTemplete(0x25, "BFA8C3DC5FC0E0D0CDA3BA");// 卡密_类型：
		addTemplete(0x26, "BFA8C3DC5FD3D0D0A7C6DAA3BA");// 卡密_有效期：
		addTemplete(0x27, "BDBBD2D7C5FAB4CEBAC5A3BA");// 交易批次号：
		addTemplete(0x28, "D5CBB5A5B1E0BAC5A3BA");// 账单编号：
		addTemplete(0x29, "D3A6D3C3D2D1C9FDBCB6A3ACC7EBCFC2D4D8");// 应用已升级，请下载
		addTemplete(0x2A, "B7FECEF1B7D1A3BA");// 服务费：
		addTemplete(0x2B, "D3C5BBDDBDF0B6EEA3BA");// 优惠金额：
		addTemplete(0x2C, "C9CCC6B7BDF0B6EEA3BA");// 商品金额：
		addTemplete(0x2D, "20");
		addTemplete(0x2E, "BBE1D4B1BAC5A3BA");// 会员号：
		addTemplete(0x2F, "2D2DBDBBD2D7C6BECCF52D2D");// --交易凭条--
		addTemplete(0x30, "2D2DBBE1D4B1B4E6B8F92D2D");// --会员存根--
		addTemplete(0x31, "2D2DC9CCBBA7B4E6B8F92D2D");// --商户存根--
		addTemplete(0x32, "2D2DBBE1D4B1D7A2B2E1C6BECCF52D2D");// --会员注册凭条--
		addTemplete(0x33, "BBE1D4B1C9EDB7DDD6A4A3BA");// 会员身份证：
		addTemplete(0x34, "BBE1D4B1CAD6BBFABAC5A3BA");// 会员手机号：
		addTemplete(0x35, "CFFBB7D1BBFDB7D6A3BA");// 消费积分：
		addTemplete(0x36, "BBE1D4B1C9CCBBA7C3FBB3C6A3BA");// 会员商户名称：
		addTemplete(0x37, "BBE1D4B1C9CCBBA7B1E0BAC5A3BA");// 会员商户编号：
		addTemplete(0x38, "D3C5B1D2CAFDA3BA");// 优币数：
		addTemplete(0x39, "2D2DC0ABBFCDBEABD3A2BDBBD2D7C6BECCF52D2D");// --阔客精英交易凭条--
		addTemplete(0x3A, "C0CFBDD1BDF0C5C6BEC620BDF0C5C6B5C4C6B7D6CA");// 老窖金牌酒
																		// 金牌的品质
		addTemplete(0x3B, "C0ABBFCDBEABD3A2CFFBB7D1");// 阔客精英消费
		addTemplete(0x3C, "2D2DC0ABBFCDC9CCC3B3B4E6B8F92D2D");// --阔客商贸存根--
		addTemplete(0x3D, "2D2DC0ABBFCDC9CCBBA7B4E6B8F92D2D");// --阔客商户存根--
		addTemplete(0x3E, "2D2DC0ABBFCDBBE1D4B1B4E6B8F92D2D");// --阔客会员存根--
		addTemplete(0x3F, "B3D6BFA8C8CBB5E7BBB0A3BA");// 持卡人电话：
		addTemplete(0x40, "C0ABBFCDBEABD3A2B3E4D6B5");// 阔客精英充值
		addTemplete(0x41, "C0ABBFCDBEABD3A2CFFBB7D1B3B7CFFA");// 阔客精英消费撤销
		addTemplete(0x42, "C0ABBFCDBEABD3A2B3E4D6B5B3B7CFFA");// 阔客精英充值撤销
		addTemplete(0x43, "C0ABBFCDBAC5A3BA");// 阔客号：
		addTemplete(0x44, "B3ACBCB6B9ABC5A3D1AAB8C9BAEC20B3ACBCB6B5C4CFEDCADC");// 超级公牛血干红
																				// 超级的享受
		addTemplete(0x45, "CFEAC7E9D7C9D1AF3A303337312D3839393131313139");// 详情咨询:0371-89911119
		addTemplete(0x46, "D6D0C9CCBBDDC3F1207777772E6875696D696E2E636E");// 中商惠民
																			// www.huimin.cn
		addTemplete(0x47, "20C9FABBEEB3ACCAD02CB4A5CAD6BFC9BCB0A1A3");// 生活超市,触手可及。
		addTemplete(0x48, "2D2DCEA2D0C5D6A7B8B6BDBBD2D7C6BECCF52D2D");// --微信支付交易凭条--
		addTemplete(0x49, "CEA2D0C5B5A5BAC5A3BA");// 微信单号：
		addTemplete(0x4A, "BDBBD2D7C0E0D0CDA3BACEA2D0C5D6A7B8B6B3B7CFFA");// 交易类型：微信支付撤销
	}

	public int decodeFromBuffer(IoBuffer in) {

	
		
		Log.e("data",StringUtil.charToHexChar(in.array()));
		
		
		int len = (in.get() & 0xFF) * 256 + (in.get() & 0xFF);

		int pos = 1;
		int printCopys = (byte) (in.get() & 0x0F);

		ArrayList<String> sl = new ArrayList<String>();
		ArrayList<String> printHeader = new ArrayList<String>();
		ArrayList<String> printFooter = new ArrayList<String>();
		for (int i = 0; i < printCopys; i++) {
			printHeader.add("0A");
			printFooter.add("0A");
		}

		while (pos < len) {
			String head = "";
			try {
				head = in.getString(2, charset.newDecoder());
			} catch (CharacterCodingException e) {
				e.printStackTrace();
			}

			byte k = (byte) (in.get() & 0x0F);
			// if ((k>0) && (k<printCopys))

			pos += 4;
			int index = in.get() & 0xFF;

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			while (pos < len) {
				byte b = in.get();

				if (b == 0x00) {
					os.write(0x0a);
					break;
				} else
					os.write(b);
			}
			pos += os.size();

			String s = modalList.get(String.format("%d", index));
			if (head.equals("%B")) {
				if ((k > 0) && (k < printCopys))
					printHeader.set(k - 1, s + StringUtil.charToHexChar(os.toByteArray()));
			} else if (head.equals("%E")) {
				printFooter.set(k - 1, s + StringUtil.charToHexChar(os.toByteArray()));
			} else {
				sl.add(s + StringUtil.charToHexChar(os.toByteArray()));
				rcv_print.put(String.format("%d", index), StringUtil.charToHexChar(os.toByteArray()));
			}

		}

		printList.clear();
		for (int j = 0; j < 1; j++) {
			printList.add(printHeader.get(j));
			for (int k = 0; k < sl.size(); k++) {
				printList.add(sl.get(k));
			}
			printList.add(printFooter.get(j));
			printList.add("0A");
			printList.add("0A");
			printList.add("0A");
			printList.add("0A");
			printList.add("0A");
			printList.add("0A");
			printList.add("0A");
		}

		return 0;
	}

	public static class signData {
		public String MerchantCode; // 商户号
		public String TerminalCode; // 终端号
		public String ReferenceNO; // 系统参考号
		public String Amount; // 金额
		public String Status; // 交易状态
		public String resultMsg; // 返回信息
		public String signPic; // 签名图片信息
		public String Timestamp; // 时间戳
		public String MD5; // md5(MerchantCode+signPicture+ReferenceNO).toUpper()

		public String cardno;// 卡号
	}

	public boolean getSectionPrintValue(signData sign_data) {

		if (sign_data == null) {
			return false;
		}

		try {
			sign_data.MerchantCode = new String(ByteUtils.hexToByte(getSection("31")), "gb2312");
			sign_data.TerminalCode = new String(ByteUtils.hexToByte(getSection("24")), "gb2312");
			sign_data.ReferenceNO = new String(ByteUtils.hexToByte(getSection("8")), "gb2312");
			sign_data.Amount = new String(ByteUtils.hexToByte(getSection("9")), "gb2312");
			sign_data.Timestamp = new String(ByteUtils.hexToByte(getSection("7")), "gb2312");
			sign_data.cardno = new String(ByteUtils.hexToByte(getSection("13")), "gb2312");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
		return true;

	}

	public String getSection(String key) {
		try {
			if (rcv_print != null) {
				String value = rcv_print.get(key);
				return value.substring(0, value.length() - 1);
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Override
	public byte[] getFieldData() {
		byte[] out = new byte[Len + 1];
		out[0] = (byte) Len;
		System.arraycopy(stream1.toByteArray(), 0, out, 1, Len);

		return out;
	}

	public void setPrintTitle(boolean bt, int pageCount, byte[] ttmodelNo, ArrayList<String> tilte) {
		if (bt) {
			Len = 0;
			String spc = pageCount + "";
			try {
				stream1.write(spc.getBytes(charset));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Len++;
		}
		for (int i = 1; i < pageCount + 1; i++) {
			String sctr;
			if (bt)
				sctr = "%B" + i;
			else
				sctr = "%E" + i;
			try {
				stream1.write(sctr.getBytes(charset));

				Len = Len + 3;
				stream1.write(ttmodelNo[i - 1]);
				Len++;
				stream1.write(tilte.get(i - 1).getBytes(charset));
				Len = Len + tilte.get(i - 1).getBytes(charset).length;
				stream1.write(0x0);
				Len++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPrintInfo(int recordCount, byte[] modelNo, ArrayList<String> printstr) {
		String sctr = "%FF";

		for (int i = 0; i < recordCount; i++) {
			try {
				stream1.write(sctr.getBytes(charset));
				Len = Len + 3;
				stream1.write(modelNo[i]);
				Len++;

				stream1.write(printstr.get(i).getBytes(charset));
				Len = Len + printstr.get(i).getBytes(charset).length;
			} catch (IOException e) {
				e.printStackTrace();
			}
			stream1.write(0x0);
			Len++;
		}

	}

	public void printjiaofei(ArrayList<String> printstr) {
		byte[] modelNo = new byte[10];
		modelNo[0] = 1;
		ArrayList<String> printtitle = new ArrayList<String>();
		printtitle.add("");
		setPrintTitle(true, 1, modelNo, printtitle);
		modelNo[0] = 3;
		printstr.add(0, "");
		modelNo[1] = 10;
		modelNo[2] = 6;
		modelNo[3] = 24;
		modelNo[4] = 25;
		modelNo[5] = 12;
		modelNo[6] = 7;
		modelNo[7] = 23;
		modelNo[8] = 2;
		printstr.add("");

		setPrintInfo(9, modelNo, printstr);

		modelNo[0] = 32;
		printtitle.clear();
		printtitle.add("");
		setPrintTitle(false, 1, modelNo, printtitle);
	}

	public void printhuaikuan(ArrayList<String> printstr) {
		byte[] modelNo = new byte[10];
		modelNo[0] = 1;
		ArrayList<String> printtitle = new ArrayList<String>();
		printtitle.add("");
		setPrintTitle(true, 1, modelNo, printtitle);
		modelNo[0] = 2;
		printstr.add(0, "");
		modelNo[1] = 10;
		modelNo[2] = 6;
		modelNo[3] = 37;
		modelNo[4] = 38;
		modelNo[5] = 21;
		modelNo[6] = 39;
		modelNo[7] = 7;
		modelNo[8] = 23;
		modelNo[9] = 2;
		printstr.add("");

		setPrintInfo(10, modelNo, printstr);

		modelNo[0] = 30;
		printtitle.clear();
		printtitle.add("4006686689");
		setPrintTitle(false, 1, modelNo, printtitle);
	}

}
