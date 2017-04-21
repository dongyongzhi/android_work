package com.hftcom.db;

import java.util.List;
import java.util.Map;

import com.hftcom.domain.Transaction;

import android.content.ContentValues;

public interface IMainMenu {
	public boolean addCache(Transaction item);

	public boolean deleteCache(String whereClause, String[] whereArgs);

	public boolean updateCache(ContentValues values, String whereClause,
			String[] whereArgs);

	public Map<String, String> viewCache(String selection,
			String[] selectionArgs);

	public List<Map<String, String>> listCache(String selection,
			String[] selectionArgs ,String limit);

	public void clearFeedTable();
}