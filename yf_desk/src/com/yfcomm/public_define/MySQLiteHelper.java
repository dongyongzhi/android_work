package com.yfcomm.public_define;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private final static int CHILD_VIEW = 0;
	private final static int CHILD_TEXT = 1;

	public MySQLiteHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "create table if not exists " + public_define.TBNAME + "(" + public_define.ID
				+ " integer primary key," + public_define.packetname + " varchar," + public_define.title + " varchar,"
				+ public_define.PageID + " integer," + public_define.Item + " integer," + public_define.left
				+ " integer," + public_define.right + " integer," + public_define.top + " integer,"
				+ public_define.bottom + " integer," + public_define.type + " integer," + public_define.color
				+ " integer," + public_define.icon + " BINARY)";

		String sql_child = "create table if not exists " + public_define.TBNAME_CHILD + "(" + public_define.ID
				+ " integer primary key," + public_define.ParentID + " integer," + public_define.title + " varchar,"
				+ public_define.type + " integer," + public_define.left + " integer," + public_define.right
				+ " integer," + public_define.top + " integer," + public_define.diplaymode + " integer,"
				+ public_define.size + " integer," + public_define.color + " integer," + public_define.bottom
				+ " integer," + public_define.icon + " BINARY)";

		db.execSQL(sql);
		db.execSQL(sql_child);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists " + public_define.TBNAME);
			db.execSQL("drop table if exists " + public_define.TBNAME_CHILD);
		} else {
			return;
		}
		onCreate(db);
	}

	public void BatchImportList(List<WinInfo> winInfos) {

		for (int i = 0; i < winInfos.size(); i++) {
			winInfos.get(i).id=i;
			InsertWinData(winInfos.get(i), i);
		}
	}

	public void InsertChildData(WinInfo win, int Id) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		if (!win.child_view.isEmpty()) {// CHILD VIEW
			for (WinInfo.Postion child : win.child_view) {
				values.put(public_define.left, child.left);
				values.put(public_define.right, child.right);
				values.put(public_define.top, child.top);
				values.put(public_define.bottom, child.bottom);
				values.put(public_define.ParentID, Id);
				values.put(public_define.type, CHILD_VIEW);

				values.put(public_define.diplaymode, child.diplaymode);
				values.put(public_define.size, child.size);
				values.put(public_define.color, child.color);

				if (child.icon != null) {
					baos.reset();
					((BitmapDrawable) child.icon).getBitmap().compress(CompressFormat.PNG, 100, baos);
					values.put(public_define.icon, baos.toByteArray());
				}
				if (-1 == db.insert(public_define.TBNAME_CHILD, null, values)) {
					Log.e(TAG, "InsertAppData insert falied");
				}
			}
		}
		if (!win.child_text.isEmpty()) {// CHILD TEXT
			for (WinInfo.Postion child : win.child_text) {

				values.put(public_define.left, child.left);
				values.put(public_define.right, child.right);
				values.put(public_define.top, child.top);
				values.put(public_define.bottom, child.bottom);
				values.put(public_define.ParentID, Id);
				values.put(public_define.title, child.buff);
				values.put(public_define.type, CHILD_TEXT);
				values.put(public_define.diplaymode, child.diplaymode);
				values.put(public_define.size, child.size);
				values.put(public_define.color, child.color);

				if (-1 == db.insert(public_define.TBNAME_CHILD, null, values)) {
					Log.e(TAG, "InsertAppData insert falied");
				}
			}
		}
		db.close();
	}

	public void InsertWinData(WinInfo win, int ID) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(public_define.ID, ID);
		values.put(public_define.packetname, win.Packetname);
		values.put(public_define.title, win.title);
		values.put(public_define.PageID, win.pageId);
		values.put(public_define.Item, win.item);
		values.put(public_define.left, win.left);
		values.put(public_define.right, win.right);
		values.put(public_define.top, win.top);
		values.put(public_define.bottom, win.bottom);
		values.put(public_define.type, win.type);
		values.put(public_define.color, win.color);

		if (win.icon != null) {
			baos.reset();
			((BitmapDrawable) win.icon).getBitmap().compress(CompressFormat.PNG, 100, baos);
			values.put(public_define.icon, baos.toByteArray());
		}
		if (-1 == db.insert(public_define.TBNAME, null, values)) {

			Log.e(TAG, "InsertAppData insert falied");
		}
		db.close();
		if (win.child_text != null || win.child_view != null) {
			InsertChildData(win, ID);
		}
	}

	public void UpdateWinData(WinInfo win) {
		
		Log.e(TAG,"after title="+win.title+" pageid="+win.pageId+" item="+win.item);
		UpdateAppData(win, public_define.ID + "=?", new String[] { Integer.toString(win.id) });
		/*
		List<WinInfo>wins1=GetlistAppData();
		for(WinInfo winInfo:wins1){
			Log.e(TAG,"after title="+winInfo.title+" item="+winInfo.item);
		}*/
	//	GetlistAppData();
	}

	public void UpdateAppData(WinInfo win, String condtion, String[] arg) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(public_define.packetname, win.Packetname);
		values.put(public_define.title, win.title);
		values.put(public_define.PageID, win.pageId);
		values.put(public_define.Item, win.item);
		values.put(public_define.left, win.left);
		values.put(public_define.right, win.right);
		values.put(public_define.top, win.top);
		values.put(public_define.bottom, win.bottom);

		if (win.icon != null) {
			baos.reset();
			((BitmapDrawable) win.icon).getBitmap().compress(CompressFormat.PNG, 100, baos);
			values.put(public_define.icon, baos.toByteArray());
		}
		if (-1 == db.update(public_define.TBNAME, values, condtion, arg)) {
			Log.e(TAG, "UpdateAppData Update falied");
		}
		db.close();
	}

	private boolean getListChild(WinInfo win) {

		SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(public_define.TBNAME_CHILD, null, public_define.ParentID + "=?",
				new String[] { Integer.toString(win.id) }, null, null, null);

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {

			WinInfo.Postion child = new WinInfo.Postion();

			child.type = cursor.getInt(cursor.getColumnIndex(public_define.type));
			child.left = cursor.getInt(cursor.getColumnIndex(public_define.left));
			child.right = cursor.getInt(cursor.getColumnIndex(public_define.right));
			child.top = cursor.getInt(cursor.getColumnIndex(public_define.top));
			child.bottom = cursor.getInt(cursor.getColumnIndex(public_define.bottom));
			child.diplaymode = cursor.getInt(cursor.getColumnIndex(public_define.diplaymode));
			child.size = cursor.getInt(cursor.getColumnIndex(public_define.size));
			child.color = cursor.getInt(cursor.getColumnIndex(public_define.color));

			if (child.type == CHILD_VIEW) {
				if (win.child_view == null) {
					win.child_view = new ArrayList<WinInfo.Postion>();
				}
				byte[] icon = cursor.getBlob(cursor.getColumnIndex(public_define.icon));
				if (icon != null) {
					ByteArrayInputStream bais = new ByteArrayInputStream(icon);
					child.icon = Drawable.createFromStream(bais, "icon");
					// child.icon=zoomDrawable(child.icon,child.right,child.bottom);

				}
				win.child_view.add(child);
			} else {
				if (win.child_text == null) {
					win.child_text = new ArrayList<WinInfo.Postion>();
				}
				child.buff = cursor.getString(cursor.getColumnIndex(public_define.title));
				win.child_text.add(child);
			}

		}
		cursor.close();
		db.close();
		return true;
	}

	public List<WinInfo> GetlistAppData() {

		SQLiteDatabase db = getReadableDatabase();
		List<WinInfo> WINS = new ArrayList<WinInfo>();

		Cursor cursor = db.query(public_define.TBNAME, null, null, null, null, null, public_define.PageID + " asc");

		for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {

			WinInfo win = new WinInfo();

			win.id = cursor.getInt(cursor.getColumnIndex(public_define.ID));
			win.Packetname = cursor.getString(cursor.getColumnIndex(public_define.packetname));
			win.pageId = cursor.getInt(cursor.getColumnIndex(public_define.PageID));
			win.item = cursor.getInt(cursor.getColumnIndex(public_define.Item));

			win.left = cursor.getInt(cursor.getColumnIndex(public_define.left));
			win.right = cursor.getInt(cursor.getColumnIndex(public_define.right));
			win.top = cursor.getInt(cursor.getColumnIndex(public_define.top));
			win.bottom = cursor.getInt(cursor.getColumnIndex(public_define.bottom));
			win.type = cursor.getInt(cursor.getColumnIndex(public_define.type));
			win.color = cursor.getInt(cursor.getColumnIndex(public_define.color));

			if (win.type == public_define.CUSTOM) {
				getListChild(win);
			} else {
				win.title = cursor.getString(cursor.getColumnIndex(public_define.title));
				byte[] icon = cursor.getBlob(cursor.getColumnIndex(public_define.icon));
				if (icon != null) {
					ByteArrayInputStream bais = new ByteArrayInputStream(icon);
					win.icon = Drawable.createFromStream(bais, "icon");
				}
			}
	
		  // Log.e(TAG,"getlist title="+win.title+" item="+win.item);
			
			
			WINS.add(win);
		}
		cursor.close();
		db.close();
		return WINS;
	}

	public void deleteWinInfo(WinInfo win) {

		SQLiteDatabase db = getWritableDatabase();
		db.delete(public_define.TBNAME, public_define.ID + "=?", new String[] { Integer.toString(win.id) });
		db.close();
	}

	public void DelAllData() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(public_define.TBNAME, "1", null);
		db.delete(public_define.TBNAME_CHILD, "1", null);
		db.close();
	}

}