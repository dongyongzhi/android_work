package com.yf.contentprovider;

import java.io.File;
import java.io.FileNotFoundException;

import com.yf.define.PublicDefine;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;


public class MyProvider extends ContentProvider {
   
	private static final  String TAG="yf_defender_MyProvider";
	@Override
	public String getType(Uri uri) {
		if (uri.toString().endsWith(PublicDefine.save_xml)) {
            return PublicDefine.save_xml;
		}
		return null;
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException{
		if(PublicDefine.save_xml.equals(getType(uri))){
			
			File file= new File(getContext().getFilesDir().getAbsolutePath() + "/",
					PublicDefine.save_xml);
			if(file.exists()){
				return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
			}else{
				Log.e(TAG, "file not exists");
			}
		}
		throw new FileNotFoundException(uri.getPath());
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
	     return null;
	}
	@Override
	public boolean onCreate() {
		return true;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
}