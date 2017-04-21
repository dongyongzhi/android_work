package com.yfcomm.public_define;

import java.io.IOException;
import java.lang.reflect.Method;

import com.yfcomm.pos.SerialPort;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class public_define {

	public static int[] Keyboardcoordinates = new int[4];// 密码键盘坐标数组

	public static final String SelphonenumInfo = "sel_numinfo"; // 选中的号码信息
	public static final String SevName = "choice type";// 业务类型
	public static final int selplan = 1; // 选择套餐
	public static final int readcard = 2;// 读身份证
	public static final int readmoney = 3;// 读金额

	public static final String TRADEMONEY = "money";// 交易金额
	public static final String Tradetype = "TradeType";// 业务类型 1：是话费充值 2：是缴费
														// 3：固话充值 4:开卡放号
	public static final String PaymentOper = "PaymentOper";// 运营商类型

	public static final String yfdb = "CustomInfos.db";
	public static final int ver = 1;
	public static final String TBNAME_CUSTOMINFOS = "CustomInfos";

	public static final String plannote = "plannote"; // 套餐描述
	public static final String planname = "planname"; // 套餐名
	public static final String phonenum = "phonenum"; // 手机号
	public static final String price = "price"; // 套餐价格
	public static final String acount = "acount"; // 账户余额

	public static final String custom_name = "name"; // 用户名
	public static final String custom_certno = "certno";// 身份证号码
	public static final String custom_address = "address";// 用户地址
	public static final String attribute_operters = "belongto";// 属于哪个运营商
	public static final String Cardissuingbank = "Cardissuingbank";// 开卡行

	public static SerialPort serialport = null;
	public final static String TAG = "public_define";

	public static SerialPort getSerialPort(SerialPort.SerialPortListener listener, Context context) throws IOException {

		try {
			forceStopPackage("com.yifengcom.yfpos", context);
			Log.e(TAG, "com.yifengcom.yfpos succ");
		} catch (Exception e) {
			Log.e(TAG, "error:" + e.getMessage());
		}
		if (serialport == null) {

			serialport = new SerialPort("/dev/ttyUSB1", 115200, listener);

		} else {
			serialport.setlistener(listener);
		}
		return serialport;
	}

	public static void close() {

		if (serialport != null) {

			serialport.close();
			serialport = null;
		}
	}

	private static void forceStopPackage(String pkgName, Context context) throws Exception {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
		method.invoke(am, pkgName);
	}

}