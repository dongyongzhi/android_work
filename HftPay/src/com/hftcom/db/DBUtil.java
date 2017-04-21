package com.hftcom.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hftcom.YFApp;
import com.hftcom.domain.Transaction;
import com.hftcom.utils.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil implements IMainMenu {
	private static DBUtil mInstance;
	private Context mContext;
	private DBHelper mSQLHelp;
	private SQLiteDatabase mSQLiteDatabase;
	private DbContext dbContext;

	private DBUtil(Context context) {
		mContext = context;
		dbContext = new DbContext(context);
		mSQLHelp = new DBHelper(dbContext);
		mSQLiteDatabase = mSQLHelp.getReadableDatabase();
	}


	public static DBUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBUtil(context);
		}
		return mInstance;
	}


	public void close() {
		if (mSQLiteDatabase != null && mSQLiteDatabase.isOpen()) {
			mSQLiteDatabase.close();
			mSQLiteDatabase = null;
		}
	}

	/**
	 * 添加数据
	 */
	public void insertData(ContentValues values) {
		mSQLiteDatabase.insert(DBHelper.TABLE_TRANSACTION, null, values);
	}

	/**
	 * 更新数据
	 * 
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 */
	public void updateData(ContentValues values, String whereClause,
			String[] whereArgs) {
		mSQLiteDatabase.update(DBHelper.TABLE_TRANSACTION, values, whereClause,
				whereArgs);
	}

	/**
	 * 删除数据
	 * 
	 * @param whereClause
	 * @param whereArgs
	 */
	public void deleteData(String whereClause, String[] whereArgs) {
		mSQLiteDatabase.delete(DBHelper.TABLE_TRANSACTION, whereClause,
				whereArgs);
	}

	/**
	 * 查询数据
	 * 
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor selectData(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		Cursor cursor = mSQLiteDatabase.query(DBHelper.TABLE_TRANSACTION,
				columns, selection, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}

	public synchronized void insertData(Transaction item) {
		int count = getTotalCount();
		if (count == DBHelper.TOTALCOUNT) {
			mSQLiteDatabase.beginTransaction();
			try {
				int id = getMinId();
				if (id != 0) {
					if (deleteCache("_id=?", new String[] { id + "" })) {
						if (addCache(item)) {
							mSQLiteDatabase.setTransactionSuccessful();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mSQLiteDatabase.endTransaction();
			}
		} else {
			addCache(item);
		}
		YFApp.getApp().writeSDCard("currentSequence="+Integer.parseInt(item.getSerial_no()),Config.sName);
	}

	@Override
	public boolean addCache(Transaction item) {
		boolean flag = false;
		long id = -1;
		try {
			ContentValues values = new ContentValues();
			values.put(DBHelper.SERIAL_NO, item.getSerial_no());
			values.put(DBHelper.SEND_TIME, item.getSend_time());
			values.put(DBHelper.SEND_RESULT, item.getSend_result());
			values.put(DBHelper.CARD_TYPE, item.getCard_type());
			values.put(DBHelper.TRADING_TIME, item.getTrading_time());
			values.put(DBHelper.RESULT_CODE, item.getResult_code());
			values.put(DBHelper.RESULT_MSG, item.getResult_msg());
			values.put(DBHelper.MAC, item.getMac());
			values.put(DBHelper.PRINT_DATA, item.getPrint_data());
			values.put(DBHelper.IC_DATA, item.getIc_data());
			values.put(DBHelper.MONEY, item.getMoney());
			id = mSQLiteDatabase.insert(DBHelper.TABLE_TRANSACTION, null,
					values);
			flag = (id != -1 ? true : false);
		} catch (Exception e) {
		} finally {
		}
		return flag;
	}

	@Override
	public boolean deleteCache(String whereClause, String[] whereArgs) {
		boolean flag = false;
		int count = 0;
		try {
			count = mSQLiteDatabase.delete(DBHelper.TABLE_TRANSACTION,
					whereClause, whereArgs);
			flag = (count > 0 ? true : false);
		} catch (Exception e) {
		} finally {
		}
		return flag;
	}

	@Override
	public boolean updateCache(ContentValues values, String whereClause,
			String[] whereArgs) {
		boolean flag = false;
		int count = 0;
		try {
			count = mSQLiteDatabase.update(DBHelper.TABLE_TRANSACTION, values,
					whereClause, whereArgs);
			flag = (count > 0 ? true : false);
		} catch (Exception e) {

		} finally {
		}
		return flag;
	}

	@Override
	public Map<String, String> viewCache(String selection,
			String[] selectionArgs) {
		Cursor cursor = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			cursor = mSQLiteDatabase.query(true, DBHelper.TABLE_TRANSACTION,
					null, selection, selectionArgs, null, null, null, null);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				for (int i = 0; i < cols_len; i++) {
					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
			}
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return map;
	}

	@Override
	public List<Map<String, String>> listCache(String selection,
			String[] selectionArgs, String limit) {
		String orderBy = "[send_time] desc";
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Cursor cursor = null;
		try {
			cursor = mSQLiteDatabase.query(false, DBHelper.TABLE_TRANSACTION,
					null, selection, selectionArgs, null, null, orderBy, limit);
			int cols_len = cursor.getColumnCount();
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < cols_len; i++) {

					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
				list.add(map);
			}
		} catch (Exception e) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	/**
	 * 返回总条数
	 * 
	 * @return
	 */
	public int getTotalCount() {
		String sql = "select count(*) from " + DBHelper.TABLE_TRANSACTION;
		Cursor cursor = null;
		try {
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToNext())
			{
				return cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	/**
	 * 获取最小时间记录id
	 * 
	 * @return
	 */
	public int getMinId() {
		String sql = "select a.*, min(a.[send_time]) from "
				+ DBHelper.TABLE_TRANSACTION + " a";
		Cursor cursor = null;
		try {
			cursor = mSQLiteDatabase.rawQuery(sql, null);
			if (cursor.moveToNext())
			{
				return cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	/**
	 * 获取最后一笔交易
	 */
	public Map<String, String> getLastOrder(String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		Cursor cursor = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			cursor = mSQLiteDatabase.query(DBHelper.TABLE_TRANSACTION, null,
					selection, selectionArgs, groupBy, having, orderBy, limit);

			int cols_len = cursor.getColumnCount();
			if (cursor.moveToLast()) {
				for (int i = 0; i < cols_len; i++) {
					String cols_name = cursor.getColumnName(i);
					String cols_values = cursor.getString(cursor
							.getColumnIndex(cols_name));
					if (cols_values == null) {
						cols_values = "";
					}
					map.put(cols_name, cols_values);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return map;
	}

	public void clearFeedTable() {
		String sql = "DELETE FROM " + DBHelper.TABLE_TRANSACTION + ";";
		mSQLiteDatabase.execSQL(sql);
		revertSeq();
	}

	private void revertSeq() {
		String sql = "update sqlite_sequence set seq=0 where name='"
				+ DBHelper.TABLE_TRANSACTION + "'";
		mSQLiteDatabase.execSQL(sql);
	}
}