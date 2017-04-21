package com.yifeng.commonutil;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ForData {
	private static final String DATABASE_NAME = "mfcDatabase.db";

	private static final String DATABASE_TABLE = "mainTable";
	private static final String DATABASE_TABLE1 = "t_keywords";

	private static final int DATABASE_VERSION = 5;

	// The index (key) column name for use in where clauses.

	public static final String KEY_ID = "id";

	// The name and column index of each column in your database.

	public static final String KEY_NAME = "zhu";

	public static final int NAME_COLUMN = 1;


	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE1 = "create table " +

	DATABASE_TABLE1 + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null)";
	private static final String DATABASE_CREATE = "create table " +

	DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text  ," + "fb_city TEXT  NOT NULL,"
			+ "fb_xian TEXT  NOT NULL," + "dd_city TEXT  NOT NULL ,"
			+ " dd_xian TEXT  NOT NULL," + " type TEXT  NOT NULL," +

			" phone1 TEXT ," + " phone2 TEXT ," + " have1 TEXT ,"
			+ " have2 TEXT ," + " have3 TEXT ," + " need1 TEXT ,"
			+ " need2 TEXT ," + " need3 TEXT ," + " k1 TEXT ," + " k2 TEXT ," +
			"content BLOB    NULL)";

	// Variable to hold the database instance

	private SQLiteDatabase db;

	// Context of the application using the database.

	private final Context context;

	// Database open/upgrade helper

	private myDbHelper dbHelper;

	public ForData(Context _context) {

		context = _context;

		dbHelper = new myDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);

	}

	public ForData open() throws SQLException {

		db = dbHelper.getWritableDatabase();

		return this;

	}

	public void close() {

		db.close();

	}

	public long insertEntry(MyObject _myObject) {

		ContentValues contentValues = new ContentValues();
		contentValues.put("content", _myObject.getContent());
		contentValues.put("fb_city", _myObject.getFb_city());
		contentValues.put("zhu", _myObject.getZhu());
		contentValues.put("fb_xian", _myObject.getFb_xian());
		contentValues.put("dd_city", _myObject.getDd_city());
		contentValues.put("dd_xian", _myObject.getDd_xian());
		contentValues.put("type", _myObject.getType());

		contentValues.put("phone1", _myObject.getPhone1());
		contentValues.put("phone2", _myObject.getPhone2());
		contentValues.put("have1", _myObject.getHave1());
		contentValues.put("have2", _myObject.getHave2());
		contentValues.put("have3", _myObject.getHave3());
		contentValues.put("need1", _myObject.getNeed1());
		contentValues.put("need2", _myObject.getNeed2());
		contentValues.put("need3", _myObject.getNeed3());
		contentValues.put("k1", _myObject.getK1());
		contentValues.put("k2", _myObject.getK2());



		return db.insert(DATABASE_TABLE, null, contentValues);

	}

	public boolean removeEntry(String _rowIndex) {

		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;

	}

	public Cursor getAllEntries() {

		return db.query(DATABASE_TABLE, new String[] { KEY_ID, "fb_city",
				"zhu", "fb_xian", "dd_city", "dd_xian", "content", "type",
				"phone1", "phone2", "have1", "have2", "have3", "need1",
				"need2", "need3", "k1", "k2" },

		null, null, null, null, null);

	}

	public Cursor getAllCar() {

		return db.query(DATABASE_TABLE, new String[] { KEY_ID, "fb_city",
				"zhu", "fb_xian", "dd_city", "dd_xian", "content", "type",
				"phone1", "phone2", "have1", "have2", "have3", "need1",
				"need2", "need3", "k1", "k2" },

		"type='1'", null, null, null, null);

	}

	public Cursor getAllGoods() {

		return db.query(DATABASE_TABLE, new String[] { KEY_ID, "fb_city",
				"zhu", "fb_xian", "dd_city", "dd_xian", "content", "type",
				"phone1", "phone2", "have1", "have2", "have3", "need1",
				"need2", "need3", "k1", "k2" },

		"type='2'", null, null, null, null);

	}
	public Cursor getAllSearch() {

		return db.query(DATABASE_TABLE, new String[] { KEY_ID, "fb_city",
				"zhu", "fb_xian", "dd_city", "dd_xian", "content", "type",
				"phone1", "phone2", "have1", "have2", "have3", "need1",
				"need2", "need3", "k1", "k2" },
//3Ϊ��Դ 4Ϊ��Դ 5Ϊûѡ
		"type='5' or type='4' or type='3'", null, null, null, null);

	}

	public MyObject getById(String id) {
		MyObject mo = null;
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
				"fb_city", "zhu", "fb_xian", "dd_city", "dd_xian", "content",
				"type", "phone1", "phone2", "have1", "have2", "have3", "need1",
				"need2", "need3", "k1", "k2" },

		"id=" + id + "", null, null, null, null);

		List<MyObject> almo = MyObject.getByCursor(cursor);

		if (almo.size() > 0) {
			mo = almo.get(0);
		}
		return mo;

	}

	public Cursor getKeywords() {
		return db.query(DATABASE_TABLE1, new String[] { KEY_ID, "zhu" },

		null, null, null, null, null);

	}
	public boolean removeKeywords(String _rowIndex) {

		return db.delete(DATABASE_TABLE1, KEY_ID + "=" + _rowIndex, null) > 0;

	}
	public long insertKeywords(String _myObject) {

		ContentValues contentValues = new ContentValues();
		contentValues.put("zhu", _myObject);



		return db.insert(DATABASE_TABLE1, null, contentValues);

	}

	public MyObject getEntry(long _rowIndex) {

		MyObject objectInstance = new MyObject();



		// use the values to populate an instance of MyObject

		return objectInstance;

	}

	public int updateEntry(long _rowIndex, MyObject _myObject) {

		String where = KEY_ID + "=" + _rowIndex;

		ContentValues contentValues = new ContentValues();


		return db.update(DATABASE_TABLE, contentValues, where, null);

	}

	private static class myDbHelper extends SQLiteOpenHelper

	{

		public myDbHelper(Context context, String name, CursorFactory factory,
				int version) {

			super(context, name, factory, version);

		}

		// Called when no database exists in

		// disk and the helper class needs

		// to create a new one.

		@Override
		public void onCreate(SQLiteDatabase _db) {

			_db.execSQL(DATABASE_CREATE);

			_db.execSQL(DATABASE_CREATE1);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {

			// Log the version upgrade.

			Log.w("TaskDBAdapter", "Upgrading from version " +

			_oldVersion + " to " + _newVersion +

			", which will destroy all old data");

			// Upgrade the existing database to conform to the new version.

			// Multiple previous versions can be handled by comparing

			// _oldVersion and _newVersion values.

			// The simplest case is to drop the old table and create a

			// new one.

			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

			// Create a new one.

			onCreate(_db);

		}

	}

}
