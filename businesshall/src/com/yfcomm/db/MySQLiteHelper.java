package com.yfcomm.db;

import java.util.ArrayList;
import java.util.List;

import com.yfcomm.public_define.CustomInfo;
import com.yfcomm.public_define.public_define;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = "MySQLiteHelper";

	public MySQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "create table if not exists " + public_define.TBNAME_CUSTOMINFOS + "(" + "id integer primary key,"
				+ public_define.planname + " varchar," + public_define.phonenum + " varchar," + public_define.price
				+ " varchar," + public_define.custom_name + " varchar," + public_define.custom_certno + " varchar,"
				+ public_define.custom_address + " varchar," + public_define.attribute_operters + " integer,"
				+ public_define.acount + " integer" + ")";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + public_define.TBNAME_CUSTOMINFOS);
		} else {
			return;
		}
		onCreate(db);
	}

	public void InsertAppData(CustomInfo newusr) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(public_define.planname, newusr.planname);// 套餐
		values.put(public_define.phonenum, newusr.phonenum);// 手机号
		values.put(public_define.price, newusr.price);// 套餐价格
		values.put(public_define.acount, newusr.acount); // 账户余额

		values.put(public_define.custom_certno, newusr.certno);// 身份证号码
		values.put(public_define.custom_name, newusr.name);// 开户姓名
		values.put(public_define.custom_address, newusr.useraddress); // 开户地址
		values.put(public_define.attribute_operters, newusr.attribute_operters);// 属于哪个运营商

		if (-1 == db.insert(public_define.TBNAME_CUSTOMINFOS, null, values)) {
			Log.e(TAG, "InsertAppData insert falied");
		}
		db.close();
	}

	public void UpdateAppData(CustomInfo oldusr , String condtion, String[] arg) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(public_define.planname, oldusr.planname);// 套餐
		values.put(public_define.phonenum, oldusr.phonenum);// 手机号
		values.put(public_define.price, oldusr.price);// 套餐价格
		values.put(public_define.acount, oldusr.acount); // 账户余额

		values.put(public_define.custom_certno, oldusr.certno);// 身份证号码
		values.put(public_define.custom_name, oldusr.name);// 开户姓名
		values.put(public_define.custom_address, oldusr.useraddress); // 开户地址
		values.put(public_define.attribute_operters, oldusr.attribute_operters);// 属于哪个运营商

		if (-1 == db.update(public_define.TBNAME_CUSTOMINFOS, values, condtion, arg)) {
			Log.e(TAG, "UpdateAppData Update falied");
		}
		db.close();
	}

	public List<CustomInfo> GetUserData(int sel_operter) {

		SQLiteDatabase db = this.getReadableDatabase();
		List<CustomInfo> users = new ArrayList<CustomInfo>();
		
		String[] args =  {String.valueOf(sel_operter)};
	  
		Cursor cursor = db.query(public_define.TBNAME_CUSTOMINFOS, null, public_define.attribute_operters + "=?",
				args, null, null, "id asc");

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {

			CustomInfo user = new CustomInfo();

			user.id = cursor.getInt(cursor.getColumnIndex("id"));
			user.planname = cursor.getString(cursor.getColumnIndex(public_define.planname));
			user.phonenum = cursor.getString(cursor.getColumnIndex(public_define.phonenum));
			user.price = cursor.getString(cursor.getColumnIndex(public_define.price));

			user.name = cursor.getString(cursor.getColumnIndex(public_define.custom_name));
			user.useraddress = cursor.getString(cursor.getColumnIndex(public_define.custom_address));
			user.certno = cursor.getString(cursor.getColumnIndex(public_define.custom_certno));
			user.attribute_operters = cursor.getInt(cursor.getColumnIndex(public_define.attribute_operters));
			user.acount = cursor.getInt(cursor.getColumnIndex(public_define.acount));
			users.add(user);
		}
		cursor.close();
		db.close();
		return users;
	}

	public void deleteOneAppinfo(String Condtion) {

		SQLiteDatabase db = getWritableDatabase();
		db.delete(public_define.TBNAME_CUSTOMINFOS, Condtion, null);
		db.close();
	}

	public void DelAllData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(public_define.TBNAME_CUSTOMINFOS, "1", null);
		db.close();
	}

}