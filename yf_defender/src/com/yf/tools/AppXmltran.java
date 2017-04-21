package com.yf.tools;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.yf.define.PublicDefine;
import com.yf.define.YFShopAppInfo;

import android.util.Xml;

public class AppXmltran {

	public List<YFShopAppInfo> Read(InputStream xml) {

		List<YFShopAppInfo> AppIs = null;
		try {

			YFShopAppInfo app_i = null;
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(xml, "UTF-8"); // 为Pull解释器设置要解析的XML数据
			int event = pullParser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {

				case XmlPullParser.START_DOCUMENT:
					AppIs = new ArrayList<YFShopAppInfo>();
					break;

				case XmlPullParser.START_TAG:

					if ("package".equals(pullParser.getName())) {

						app_i = new YFShopAppInfo();
						app_i.packname = pullParser.getAttributeValue(0);
						String ver=pullParser.getAttributeValue(1);
						if(ver==null || ver.isEmpty()){
							app_i.vercode=0;
						}else{
						    app_i.vercode=Integer.parseInt(pullParser.getAttributeValue(1));
						}
						app_i.version = pullParser.getAttributeValue(2);
						app_i.title = pullParser.getAttributeValue(3);
						app_i.apkdir=pullParser.getAttributeValue(4);
						
					} else if ("updateTime".equals(pullParser.getName())) {
						app_i.updatetime = pullParser.nextText();
					} else if ("icon".equals(pullParser.getName())) {
						app_i.icondir = pullParser.nextText();
					} else if ("certMD5".equals(pullParser.getName())) {
						app_i.certmd5 = pullParser.nextText();
					} else if ("fileMD5".equals(pullParser.getName())) {
						app_i.filemd5 = pullParser.nextText();
					} else if ("privilege".equals(pullParser.getName())) {
						app_i.permisson = pullParser.nextText();
					} else if ("note".equals(pullParser.getName())) {
						app_i.note = pullParser.nextText();
					} else if ("fileSize".equals(pullParser.getName())) {
						app_i.fileszie = getFileSzie(pullParser.nextText());

					}
					break;

				case XmlPullParser.END_TAG:
					if ("package".equals(pullParser.getName())) {
						AppIs.add(app_i);
						app_i = null;
					}
					break;
				}
				event = pullParser.next();
			}
			return AppIs;
		} catch (Exception e) {
			if (AppIs.size() != 0)
				return AppIs;
		}
		return null;
	}

	public String getFileSzie(String filesize) {
		float size = Float.parseFloat(filesize) / (1024 * 1024);
		DecimalFormat decimalFormat = new DecimalFormat("0.00");// 构造方法的字符格式这里如果小数不足2位,会以0补足.
		return decimalFormat.format(size);// format 返回的是字符串

	}
}