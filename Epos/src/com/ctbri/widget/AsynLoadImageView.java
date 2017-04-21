package com.ctbri.widget;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.ctbri.utils.ElecLog;


/**
 * 获取远程或本地图片 异步调用 
 * @author qin
 * 
 * 2012-12-13
 */
public class AsynLoadImageView extends ImageView {

	public AsynLoadImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setImageURI(String imageUrl){
//		 
//		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(this);
//		String tag = imageUrl;
//		this.setTag(imageUrl);
//		
//		Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl,new ImageCallback() {
//			public void imageLoaded(Drawable imageDrawable,String imageUrl) {
//				ImageView imageViewByTag = AsynLoadImageView.this;
//				if (imageViewByTag != null) {
//					imageViewByTag.setImageDrawable(imageDrawable);
//					
//				}
//			}
//		}, tag);
//		
//		if (cachedImage == null) {
//			//this.setImageResource(R.drawable.noneimg);
//		} else {
//			this.setImageDrawable(cachedImage);
//		}

	}
	
 
	
	/**
	 * 加载图片
	 * @author qin
	 * 
	 */
	class AsyncLoadImageTask extends AsyncTask<URL, Void, Bitmap>{
		@Override
		protected Bitmap doInBackground(URL... params) {
			if(params==null || params[0]==null)
				return null;
			InputStream  is = null;
			Bitmap mBitmap = null;
			try {
				is = params[0].openStream();
				mBitmap = BitmapFactory.decodeStream(is);
				
			} catch (IOException e) {
				 ElecLog.w(getClass(), e.getMessage());
			}finally{
				if(is!=null)
					try {
						is.close();
					} catch (IOException e) {
						 ElecLog.w(getClass(), e.getMessage());
					}
			}
			
			return mBitmap;
		}
		
		protected void onPostExecute(Bitmap result){
			//显示图片
			AsynLoadImageView.this.setImageBitmap(result);
			super.onPostExecute(result);  
		}
	}
}
