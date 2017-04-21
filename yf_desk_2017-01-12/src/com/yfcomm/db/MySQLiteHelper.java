package com.yfcomm.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yfcomm.public_define.AppInfo;
import com.yfcomm.public_define.public_define;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String TAG = "MySQLiteHelper";

	public MySQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table if not exists " + public_define.TBNAME + "(" + "id integer primary key,"
				+ public_define.packetname + " varchar," + public_define.title + " varchar," + public_define.isnull + " integer,"
				+ public_define.icon + " BINARY)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + public_define.TBNAME);
		} else {
			return;
		}
		onCreate(db);
	}

	public void InsertAppData(AppInfo nativeapp) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(public_define.packetname, nativeapp.Packetname);
		values.put(public_define.title, nativeapp.title);
		values.put(public_define.isnull, nativeapp.IsNull);
		
		if (nativeapp.icon != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			((BitmapDrawable) nativeapp.icon).getBitmap().compress(CompressFormat.PNG, 100, baos);
			values.put(public_define.icon, baos.toByteArray());
		}
		if (-1 == db.insert(public_define.TBNAME, null, values)) {
			Log.e(TAG, "InsertAppData insert falied");
		}
		db.close();
	}

	public void UpdateAppData(AppInfo nativeapp, String condtion, String[] arg) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(public_define.packetname, nativeapp.Packetname);
		values.put(public_define.title, nativeapp.title);
		values.put(public_define.isnull, nativeapp.IsNull);
		if (nativeapp.icon != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			((BitmapDrawable) nativeapp.icon).getBitmap().compress(CompressFormat.PNG, 100, baos);
			values.put(public_define.icon, baos.toByteArray());
		}

		if (-1 == db.update(public_define.TBNAME, values, condtion, arg)) {
			Log.e(TAG, "UpdateAppData Update falied");
		}
		db.close();
	}

	public List<AppInfo> GetlistAppData() {

		SQLiteDatabase db = this.getReadableDatabase();
		List<AppInfo> appInfo = new ArrayList<AppInfo>();

		Cursor cursor = db.query(public_define.TBNAME, null, null, null, null, null, "id asc");

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {

			AppInfo napp = new AppInfo();
			napp.title = cursor.getString(cursor.getColumnIndex(public_define.title));
			napp.Packetname = cursor.getString(cursor.getColumnIndex(public_define.packetname));
			napp.IsNull = cursor.getInt(cursor.getColumnIndex(public_define.isnull));
		//	napp.id = cursor.getInt(cursor.getColumnIndex("id"));
			byte[] icon;
			icon = cursor.getBlob(cursor.getColumnIndex(public_define.icon));
			if (icon != null) {
				ByteArrayInputStream bais = null;
				bais = new ByteArrayInputStream(icon);
				napp.icon = Drawable.createFromStream(bais, "icon");
			}
			appInfo.add(napp);
		}
		cursor.close();
		db.close();
		return appInfo;
	}

	public void deleteOneAppinfo(String[] args) {

		SQLiteDatabase db = getWritableDatabase();
		db.delete(public_define.TBNAME, "id=?", args);
		db.close();
	}

	public void DeleteListAppInfo(List<AppInfo> apps) {

		for (AppInfo app : apps) {
		//	deleteOneAppinfo(new String[] {Integer.toString(app.id) });
		}
	}

	public void swappostion(AppInfo srcApp, AppInfo desApp) {

	//	UpdateAppData(srcApp, "id=?", new String[] { Integer.toString(desApp.id) });
	//	UpdateAppData(desApp, "id=?", new String[] { Integer.toString(srcApp.id) });
	}

	public void DelAllData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(public_define.TBNAME, "1", null);
		db.close();
	}

}