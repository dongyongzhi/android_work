package com.yf.define;


import android.graphics.Bitmap;

public class YFShopAppInfo {

	public String packname;
	public String version;
	public int    vercode;
	public String title;
	public String apkdir;
	public String updatetime;
	public String icondir;

	public String certmd5;
	public String filemd5;
	public String permisson;
	public String note;

	public String fileszie;
    public int ImageDownTimes=0;
	public Boolean isnaticeApp = false;
	public Boolean isupdate = false;
	public Boolean isInsatll = false;
	public Bitmap shopicon;
	public boolean IsWaitingDownload = false;
	public boolean issetlisten = false;

	public Object tag;
}