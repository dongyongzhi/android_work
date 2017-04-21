package com.yifeng.skzs.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

/**
 * comment:异步加载
 * @author:ZhangYan 
 * Date:2012-8-2
 */
public class AsyncImageLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache;
	private static Context context;
	private static CommonUtil commonUtil;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public synchronized Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback, final String tag) {
		commonUtil = new CommonUtil(context);

		if (imageCache.containsKey(imageUrl)) {// 判断缓存中是否有
			if (imageCache.get(imageUrl) == null)
				return null;
			Drawable softReference = imageCache.get(imageUrl).get();
			if (softReference != null) {// 判断缓存中是否存在，有是从缓存中读取
				Drawable drawable = softReference;
				if (drawable != null) {
					return drawable;
				}
			} else {
				// 如果为空，需要将其从缓存中删除（其bitmap对象已被回收释放，需要重新加载）
				imageCache.remove(imageUrl);
				// return null;
			}
		} else {
			imageCache.put(imageUrl, null);
		}

		Drawable d = findByNetUrl(imageUrl);// 查找图片
		if (d != null) {
			imageCache.put(imageUrl, new SoftReference<Drawable>(d));
			return d;
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, tag);
			}
		};

		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(imageUrl);
				if (drawable != null) {
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
					System.gc(); // 提醒系统及时回收
				}
			}
		}.start();

		return null;
	}

	/**
	 * 下载图片
	 * @param url
	 * @return
	 */
	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		Drawable d = findByNetUrl(url);//先到SDCard中查询一下
		if (d == null) {
			try {
				m = new URL(url);
				i = (InputStream) m.getContent();

				String fileName = commonUtil.getFixName(url);
				insert(fileName, url, i);// 插入到SDCard中
				File file = new File(ConstantUtil.HOT_EVENT +"/"+fileName);
				if (file.exists()) {
					BitmapFactory.Options opts = new BitmapFactory.Options();
					opts.inSampleSize = 2;
					Bitmap tbm = BitmapFactory.decodeFile(ConstantUtil.HOT_EVENT +"/"+ fileName, opts);
					if (tbm != null)
						d = new BitmapDrawable(tbm);
				}
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (i != null)
					try {
						i.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

	/**
	 * 先到SDCard中查找图片
	 * @param url
	 * @return Drawable
	 */
	private static Drawable findByNetUrl(String url) {
		String fileName = commonUtil.getFixName(url);
		String sdcardUrl = ConstantUtil.HOT_EVENT+"/"+fileName;
		Drawable d = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		Bitmap tbm = BitmapFactory.decodeFile(sdcardUrl, opts);
		if (tbm != null){
			d = new BitmapDrawable(tbm);
		}
		return d;
	}

	/**
	 * 将文件写入到sdcard
	 * @param fileName
	 * @param net_url
	 * @param i
	 */
	private static void insert(String fileName, String net_url, InputStream i) {
		File dir = new File(ConstantUtil.HOT_EVENT);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		FileUtils fUtile = new FileUtils();
		try {
			fUtile.write2SDFromInput(ConstantUtil.Hot_EVENT_DIR, fileName, i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 释放缓存中所有的Bitmap对象，并将缓存清空
	 */
	public void releaseBitmapCache() {
		if (imageCache != null) {
			for (Entry<String, SoftReference<Drawable>> entry : imageCache
					.entrySet()) {
				if (entry.getValue() == null)
					continue;
				Drawable bitmap = entry.getValue().get();

				if (bitmap != null) {
					BitmapDrawable bd = (BitmapDrawable) bitmap;
					Bitmap bm = bd.getBitmap();
					if (bm != null && bm.isRecycled())
						bm.recycle();
					bm = null;
					// bitmap.recycle();// 释放bitmap对象
					bitmap = null;
					System.gc();
				}
			}
			imageCache.clear();
		}
	}

}