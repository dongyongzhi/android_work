package com.hftcom.db;

import java.io.File;
import java.io.IOException;
import com.hftcom.utils.Config;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;

public class DbContext extends ContextWrapper {

	public DbContext(Context base) {
		super(base);
	}
 
	@Override
	public File getDatabasePath(String name) {
		boolean sdExist = android.os.Environment.MEDIA_MOUNTED
				.equals(android.os.Environment.getExternalStorageState());
		if (!sdExist) { 
			return null;
		} else { 
			String dbDir = Environment.getExternalStorageDirectory()+"/";
			dbDir += Config.appName +"/Database";
			String dbPath = dbDir + "/" + name; 
			File dirFile = new File(dbDir);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			boolean isFileCreateSuccess = false;
			File dbFile = new File(dbPath);
			if (!dbFile.exists()) {
				try {
					isFileCreateSuccess = dbFile.createNewFile(); 
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				isFileCreateSuccess = true;
			}
			if (isFileCreateSuccess) {
				return dbFile;
			} else {
				return null;
			}
		}
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			SQLiteDatabase.CursorFactory factory) {
		SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
				getDatabasePath(name), null);
		return result;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(
				getDatabasePath(name), null);
		return result;
	}
}
