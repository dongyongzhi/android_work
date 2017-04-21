package com.yfcomm.yf_desk;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;


public class AppProhibitXml{

	public static List<String> Read(XmlPullParser pullParser) {
		List<String> names = null;
		try {
			int event = pullParser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {

				case XmlPullParser.START_DOCUMENT:

					names = new ArrayList<String>();

					break;

				case XmlPullParser.START_TAG:

					if ("package".equals(pullParser.getName())) {

						String name = pullParser.getAttributeValue(0);
						names.add(name);
					}
					break;

				case XmlPullParser.END_TAG:

					break;
				}
				event = pullParser.next();
			}
			return names;

		} catch (Exception e) {

		}
		return null;
	}
}
