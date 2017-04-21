package com.yf.Interfaces;

import android.graphics.Bitmap;


public interface FreedomCallback {
    /**
     * @Title: imageLoaded
     * @Description: TODO
     * @param imageDrawable 传回的bitmap对象
     * @param tag 用于listView查找控件
     * @throws
     */
    public void imageLoaded(Bitmap imageDrawable, Object tag);
}