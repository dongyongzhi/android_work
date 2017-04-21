package com.hftcom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "hftsyt.db";
	public static final String TABLE_TRANSACTION = "trade"; // 交易表
	public static final int TOTALCOUNT = 300; 
	private static final int DATABASE_VERSION = 1;

	public static final String _ID = "_id";
	public static final String SERIAL_NO = "serial_no"; // 流水号
	public static final String SEND_TIME = "send_time"; // 交易发送时间
	public static final String SEND_RESULT = "send_result"; // 交易发送结果（已发送、已接收、已超时、故障）
	public static final String CARD_TYPE = "card_type"; // 卡类型
	public static final String CORRECT_NUM = "correct_num"; // 冲正次数
	public static final String TRADING_TIME = "trading_time"; // 交易日期
	public static final String RESULT_CODE = "result_code"; // 交易结果-编号
	public static final String RESULT_MSG = "result_msg"; // 交易结果-信息
	public static final String MAC = "mac"; // 发送报文的mac
	public static final String PRINT_DATA = "print_data"; // 打印数据
	public static final String IC_DATA = "ic_data"; // ic卡数据
	public static final String MONEY = "money"; // 金额
	
	public DBHelper(Context context) {
		super(context , DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists " + TABLE_TRANSACTION
				+ "([_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
				+ "[serial_no] VARCHAR2 NOT NULL, "
				+ "[send_time] VARCHAR2 NOT NULL, "
				+ "[card_type] VARCHAR2, "
				+ "[send_result] VARCHAR2,  "
				+ "[correct_num] INTEGER NOT NULL DEFAULT 3,  "
				+ "[trading_time] VARCHAR2,  " + " [result_code] VARCHAR2,  "
				+ "[result_msg] VARCHAR2,  " + "[mac] VARCHAR2,  "
				+ "[print_data] VARCHAR2, "+ "[money] VARCHAR2, "
				+ "[ic_data] VARCHAR2)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		onCreate(db);
	}

}
