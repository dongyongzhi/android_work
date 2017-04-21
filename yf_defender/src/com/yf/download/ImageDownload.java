package com.yf.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.yf.Interfaces.FreedomCallback;
import com.yf.define.PublicDefine;
import com.yf.define.YFShopAppInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ImageDownload {
	private static final String TAG = "ImageDownload";
	private List<YFShopAppInfo> shopapp;

	public ImageDownload(Context context, List<YFShopAppInfo> store_app) {
		this.shopapp = store_app;
	}

	/**
	 * @Title: loadDrawable @Description: 下载图片 @param imageUrl @param
	 *         simpleName @param imageCallback @throws
	 */
	public void loadDrawable(FreedomCallback imageCallback) {
	  new Thread(new ImageThread(imageCallback)).start();
	//	new FreedomLoadTask(imageCallback).execute();
	}

	/**
	 * @Title: loadImageFromUrl @Description: 根据地址下载图片 @param
	 *         url @return @throws
	 */
	public Bitmap loadImageFromUrl(String url) {

		URL myFileURL;
		Bitmap bitmap = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			myFileURL = new URL(url);
			conn = (HttpURLConnection) myFileURL.openConnection();
			conn.setConnectTimeout(PublicDefine.TIME_OUT);
			conn.setReadTimeout(PublicDefine.TIME_OUT);
			conn.connect();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				is = conn.getInputStream();// 得到数据流
				bitmap = BitmapFactory.decodeStream(is);// 解析得到图片
			}
		} catch (Exception e) {
			Log.e(TAG, "err:"+e.getMessage().toString());
		} finally {

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					Log.e(TAG, "err:"+e.getMessage().toString());
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}
		return bitmap;
	}

	private class ImageThread implements Runnable{
		private FreedomCallback ImageBack;
		public ImageThread(FreedomCallback ImagecallBack){		
			this.ImageBack=ImagecallBack;
		}
		
		@Override
		public void run() {
			
			for (int i = 0; i < shopapp.size(); i++) {
				shopapp.get(i).ImageDownTimes=0;
			}
			while (!CheckLoadIsOk()) {
				for (int i = 0; i < shopapp.size(); i++) {
					if (shopapp.get(i).shopicon == null && shopapp.get(i).ImageDownTimes<3){
						DownLoadImg(i);
					}
				}
			}		
		}
		private void DownLoadImg(int i) {
			shopapp.get(i).ImageDownTimes++;
			shopapp.get(i).shopicon = loadImageFromUrl(PublicDefine.SubDir + shopapp.get(i).icondir);
			ImageBack.imageLoaded(shopapp.get(i).shopicon, shopapp.get(i).tag);
			
		}

		private boolean CheckLoadIsOk() {
			for (int i = 0; i < shopapp.size(); i++) {
				if (shopapp.get(i).shopicon == null && shopapp.get(i).ImageDownTimes<3 )
					return false;
			}
			return true;
		}	
	}
	
	
	class FreedomLoadTask extends AsyncTask<Void, Object, Integer> {

		private FreedomCallback imageCallback;

		public FreedomLoadTask(FreedomCallback imageCallback) {
			this.imageCallback = imageCallback;
		}

		@Override
		protected void onPostExecute(Integer bitmap) { // 下载成功后执行回调接口
			super.onPostExecute(bitmap);
			Log.i(TAG, "DownLoad Finished");	
		}

		@Override
		protected Integer doInBackground(Void... params) {
			
			for (int i = 0; i < shopapp.size(); i++) {
				shopapp.get(i).ImageDownTimes=0;
			}
			while (!CheckLoadIsOk()) {
				for (int i = 0; i < shopapp.size(); i++) {
					if (shopapp.get(i).shopicon == null && shopapp.get(i).ImageDownTimes<3){
						DownLoadImg(i);
					}
				}
			}
			return 1;
		}

		@Override
		protected void onProgressUpdate(Object... args) {
			Integer i = (Integer) args[0];
			imageCallback.imageLoaded(shopapp.get(i).shopicon, shopapp.get(i).tag);
		}

		private void DownLoadImg(int i) {
			shopapp.get(i).ImageDownTimes++;
			shopapp.get(i).shopicon = loadImageFromUrl(PublicDefine.SubDir + shopapp.get(i).icondir);
			this.publishProgress(i);
		}

		private boolean CheckLoadIsOk() {
			for (int i = 0; i < shopapp.size(); i++) {
				if (shopapp.get(i).shopicon == null && shopapp.get(i).ImageDownTimes<3 )
					return false;
			}
			return true;
		}

	}
}