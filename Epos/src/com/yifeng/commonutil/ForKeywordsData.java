//package com.yifeng.commonutil;
//
//import java.util.List;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;
//
//public class ForKeywordsData {
//	private static final String DATABASE_NAME = "mfcDatabase.db";
//
//	private static final String DATABASE_TABLE1 = "t_keywords";
//
//	private static final int DATABASE_VERSION = 4;
//
//	// The index (key) column name for use in where clauses.
//
//	public static final String KEY_ID = "id";
//
//	// The name and column index of each column in your database.
//
//	public static final String KEY_NAME = "zhu";
//
//	public static final int NAME_COLUMN = 1;
//

//
//	// SQL Statement to create a new database.
//
//	private static final String DATABASE_CREATE1 = "create table " +
//
//	DATABASE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, "
//			+ KEY_NAME + " text not null)";
//
//	// Variable to hold the database instance
//
//	private SQLiteDatabase db;
//
//	// Context of the application using the database.
//
//	private final Context context;
//
//	// Database open/upgrade helper
//
//	private myDbHelper dbHelper;
//
//	public ForKeywordsData(Context _context) {
//
//		context = _context;
//
//		dbHelper = new myDbHelper(context, DATABASE_NAME, null,
//				DATABASE_VERSION);
//
//	}
//
//	public ForKeywordsData open() throws SQLException {
//
//		db = dbHelper.getWritableDatabase();
//
//		return this;
//
//	}
//
//	public void close() {
//
//		db.close();
//
//	}
//
//	public long insertEntry(String _myObject) {
//
//		ContentValues contentValues = new ContentValues();
//		contentValues.put("zhu", _myObject);
//	 
//	 
//

//
//		return db.insert(DATABASE_TABLE, null, contentValues);
//
//	}
//
//	public boolean removeEntry(String _rowIndex) {
//
//		return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
//
//	}
//
//	public Cursor getAllEntries() {
//
//		return db.query(DATABASE_TABLE, new String[] { KEY_ID,  
//				"zhu" },
//
//		null, null, null, null, null);
//
//	}
//
//
//	public MyObject getById(String id) {
//		MyObject mo = null;
//		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
//				"fb_city", "zhu", "fb_xian", "dd_city", "dd_xian", "content",
//				"type", "phone1", "phone2", "have1", "have2", "have3", "need1",
//				"need2", "need3", "k1", "k2" },
//
//		"id=" + id + "", null, null, null, null);
//
//		List<MyObject> almo = MyObject.getByCursor(cursor);
//
//		if (almo.size() > 0) {
//			mo = almo.get(0);
//		}
//		return mo;
//
//	}
//
//	public MyObject getEntry(long _rowIndex) {
//
//		MyObject objectInstance = new MyObject();
//

//
//		// use the values to populate an instance of MyObject
//
//		return objectInstance;
//
//	}
//
//	public int updateEntry(long _rowIndex, MyObject _myObject) {
//
//		String where = KEY_ID + "=" + _rowIndex;
//
//		ContentValues contentValues = new ContentValues();
//

//
//		return db.update(DATABASE_TABLE, contentValues, where, null);
//
//	}
//
//	private static class myDbHelper extends SQLiteOpenHelper
//
//	{
//
//		public myDbHelper(Context context, String name, CursorFactory factory,
//				int version) {
//
//			super(context, name, factory, version);
//
//		}
//
//		// Called when no database exists in
//
//		// disk and the helper class needs
//
//		// to create a new one.
//
//		@Override
//		public void onCreate(SQLiteDatabase _db) {
//
//			_db.execSQL(DATABASE_CREATE1);
//
//		}
//
//		// Called when there is a database version mismatch meaning that
//
//		// the version of the database on disk needs to be upgraded to
//
//		// the current version.
//
//		@Override
//		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
//				int _newVersion) {
//
//			// Log the version upgrade.
//
//			Log.w("TaskDBAdapter", "Upgrading from version " +
//
//			_oldVersion + " to " + _newVersion +
//
//			", which will destroy all old data");
//
//			// Upgrade the existing database to conform to the new version.
//
//			// Multiple previous versions can be handled by comparing
//
//			// _oldVersion and _newVersion values.
//
//			// The simplest case is to drop the old table and create a
//
//			// new one.
//
//			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
//
//			// Create a new one.
//
//			onCreate(_db);
//
//		}
//
//	}
//
//}
