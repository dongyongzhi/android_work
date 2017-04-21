package com.yfcomm.yf_desk;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.WinInfo.Postion;
import com.yfcomm.public_define.public_define;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppXmltran {
	public static int backcolor = 0;
	private static final String TAG = "AppXmltran";

	public static boolean Read(XmlPullParser pullParser, final List<WinInfo> AppIs, launcher launcher) {

		try {
			WinInfo app_i = null;
			int event = pullParser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {

				case XmlPullParser.START_DOCUMENT:
					break;

				case XmlPullParser.START_TAG:

					if ("package".equals(pullParser.getName())) {

						app_i = new WinInfo();
						app_i.child_view = new ArrayList<Postion>();
						app_i.child_text = new ArrayList<Postion>();
						app_i.type = public_define.CUSTOM;
						app_i.Packetname = pullParser.getAttributeValue(0);
						app_i.color = bytesToInt2(ByteUtils.hexToByte(pullParser.getAttributeValue(1)), 0);
						app_i.left = Integer.parseInt(pullParser.getAttributeValue(2));
						app_i.top = (int) Integer.parseInt(pullParser.getAttributeValue(3));
						app_i.right = Integer.parseInt(pullParser.getAttributeValue(4));
						app_i.bottom = Integer.parseInt(pullParser.getAttributeValue(5));
					//	app_i.isSystem= launcher.getPacktools().GetPackageIsSystem(app_i.Packetname);

					} else if ("icon".equals(pullParser.getName())) {

						Postion icon = new Postion();
						icon.buff = pullParser.getAttributeValue(0);
						icon.icon = GetIconDrawable(launcher, icon.buff);
						icon.left = Integer.parseInt(pullParser.getAttributeValue(1));
						icon.top = (int) Integer.parseInt(pullParser.getAttributeValue(2));
						icon.right = Integer.parseInt(pullParser.getAttributeValue(3));
						icon.bottom = Integer.parseInt(pullParser.getAttributeValue(4));
						app_i.child_view.add(icon);

					} else if ("title".equals(pullParser.getName())) {

						Postion text = new Postion();
						app_i.title = text.buff = pullParser.getAttributeValue(0);
						text.left = Integer.parseInt(pullParser.getAttributeValue(1));
						text.top = (int) Integer.parseInt(pullParser.getAttributeValue(2));
						text.right = Integer.parseInt(pullParser.getAttributeValue(3));
						text.bottom = Integer.parseInt(pullParser.getAttributeValue(4));
						text.diplaymode = Integer.parseInt(pullParser.getAttributeValue(5));
						text.size = Integer.parseInt(pullParser.getAttributeValue(6));
						text.color = bytesToInt2(ByteUtils.hexToByte(pullParser.getAttributeValue(7)), 0);

						app_i.child_text.add(text);
					} else if ("backcolor".equals(pullParser.getName())) {

						backcolor = bytesToInt2(ByteUtils.hexToByte(pullParser.nextText()), 0);
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
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
	}

	private static Drawable GetIconDrawable(launcher launcher, String IconName) {
		if (IconName.equals("shaoyishao.png"))
			return launcher.getResources().getDrawable(R.drawable.shaoyishao, null);
		else if (IconName.equals("fukuan.png"))
			return launcher.getResources().getDrawable(R.drawable.fukuan, null);
		else if (IconName.equals("shuaiyishua.png"))
			return launcher.getResources().getDrawable(R.drawable.shuayishua, null);
		else if (IconName.equals("huiyuanka.png"))
			return launcher.getResources().getDrawable(R.drawable.huiyuanka, null);
		else if (IconName.equals("licai.png"))
			return launcher.getResources().getDrawable(R.drawable.licai, null);
		else if (IconName.equals("syt.png"))
			return launcher.getResources().getDrawable(R.drawable.syt, null);
		else if (IconName.equals("tuangou.png"))
			return launcher.getResources().getDrawable(R.drawable.tuangou, null);
		else if (IconName.equals("kajuan.png"))
			return launcher.getResources().getDrawable(R.drawable.kajuan, null);
		else if (IconName.equals("bq.png"))
			return launcher.getResources().getDrawable(R.drawable.bq, null);
		else if (IconName.equals("ppsh.png"))
			return launcher.getResources().getDrawable(R.drawable.ppsh, null);
		else if (IconName.equals("huiming.png")) {
			return launcher.getResources().getDrawable(R.drawable.huiming, null);
		} else if (IconName.equals("kdb.png")) {
			return launcher.getResources().getDrawable(R.drawable.kdb, null);
		} else if (IconName.equals("hsy.png")) {
			return launcher.getResources().getDrawable(R.drawable.hsy, null);
		} else if (IconName.equals("fc.png")) {
			return launcher.getResources().getDrawable(R.drawable.fc, null);
		} else if (IconName.equals("hjy.png")) {
			return launcher.getResources().getDrawable(R.drawable.hjy, null);
		} else if (IconName.equals("hfthuiyuanka.png")) {
			return launcher.getResources().getDrawable(R.drawable.hfthuiyuanka, null);
		} else if (IconName.equals("busnisshall.png")) {
			return launcher.getResources().getDrawable(R.drawable.busnisshall, null);
		}
		return null;
	}

	public static byte[] intToBytes(int value) {
		byte[] src = new byte[4];
		src[3] = (byte) ((value >> 24) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16)
				| ((src[offset + 3] & 0xFF) << 24));
		return value;
	}

	public static int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (int) (((src[offset] & 0xFF) << 24) | ((src[offset + 1] & 0xFF) << 16) | ((src[offset + 2] & 0xFF) << 8)
				| (src[offset + 3] & 0xFF));
		return value;
	}

}