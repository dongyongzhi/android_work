package com.yfcomm.yf_desk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.yfcomm.public_define.WinInfo;
import com.yfcomm.public_define.WinInfo.Postion;
import android.util.Xml;

public class AppXmltran {
	public static int backcolor = 0;
	public static List<WinInfo> Read(InputStream xml) {
		List<WinInfo> AppIs = null;
		try {

			WinInfo app_i = null;
			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(xml, "UTF-8");
			int event = pullParser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {

				case XmlPullParser.START_DOCUMENT:
					AppIs = new ArrayList<WinInfo>();
					break;

				case XmlPullParser.START_TAG:

					if ("package".equals(pullParser.getName())) {

						app_i = new WinInfo();
						app_i.icon = new ArrayList<Postion>();
						app_i.text = new ArrayList<Postion>();
						app_i.packetname = pullParser.getAttributeValue(0);

						app_i.color = bytesToInt2(
								ByteUtils.hexToByte(pullParser
										.getAttributeValue(1)), 0);

						app_i.left = Integer.parseInt(pullParser
								.getAttributeValue(2));
						app_i.top = (int) Integer.parseInt(pullParser
								.getAttributeValue(3));
						app_i.right = Integer.parseInt(pullParser
								.getAttributeValue(4));
						app_i.bottom = Integer.parseInt(pullParser
								.getAttributeValue(5));
					} else if ("icon".equals(pullParser.getName())) {
						Postion icon = new Postion();
						icon.buff = pullParser.getAttributeValue(0);
						icon.left = Integer.parseInt(pullParser
								.getAttributeValue(1));
						icon.top = (int) Integer.parseInt(pullParser
								.getAttributeValue(2));
						icon.right = Integer.parseInt(pullParser
								.getAttributeValue(3));
						icon.bottom = Integer.parseInt(pullParser
								.getAttributeValue(4));
						app_i.icon.add(icon);
					} else if ("title".equals(pullParser.getName())) {
						Postion text = new Postion();
						text.buff = pullParser.getAttributeValue(0);
						text.left = Integer.parseInt(pullParser
								.getAttributeValue(1));
						text.top = (int) Integer.parseInt(pullParser
								.getAttributeValue(2));
						text.right = Integer.parseInt(pullParser
								.getAttributeValue(3));
						text.bottom = Integer.parseInt(pullParser
								.getAttributeValue(4));
						text.diplaymode = Integer.parseInt(pullParser
								.getAttributeValue(5));
						text.size = Integer.parseInt(pullParser
								.getAttributeValue(6));
						text.color = bytesToInt2(ByteUtils.hexToByte(pullParser
								.getAttributeValue(7)), 0);

						app_i.text.add(text);
					} else if ("backcolor".equals(pullParser.getName())) {

						backcolor = bytesToInt2(
								ByteUtils.hexToByte(pullParser.nextText()), 0);
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
		value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
				| ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
		return value;
	}

	public static int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (int) (((src[offset] & 0xFF) << 24)
				| ((src[offset + 1] & 0xFF) << 16)
				| ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF));
		return value;
	}

}