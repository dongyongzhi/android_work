package com.yifeng.commonutil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;

public class KeywordsUtil {

	public static String[] getAllKeyWords(Context context) {

		ForData data = new ForData(context);
		data.open();
		Cursor cursor = data.getKeywords();
		String tempString = "";
		while (cursor.moveToNext()) {

			int nameColumnIndex = cursor.getColumnIndex("zhu");
			tempString += cursor.getString(nameColumnIndex) + "|";
		}

		data.close();

		return tempString.split("\\|");

	}

	public static boolean complie(String content, String[] keywords) {
		boolean arg = false;

		for (String oneKey : keywords) {
			Pattern p = Pattern.compile(oneKey);
			Matcher m = p.matcher(content);
			while (m.find()) {

			return true;
			}
//			if (b)
//				return false;
		}
		return arg;

	}

}
