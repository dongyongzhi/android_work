package com.yfcomm.businesshall;

import java.io.InputStream;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

import com.yfcomm.public_define.OperterData;

import android.util.Xml;

public class AppXmltran {

	public static int backcolor = 0;

	public static OperterData Read(InputStream xml) {
		OperterData operdata = null;
		try {

			XmlPullParser pullParser = Xml.newPullParser();
			pullParser.setInput(xml, "UTF-8");
			int event = pullParser.getEventType();

			while (event != XmlPullParser.END_DOCUMENT) {

				switch (event) {

				case XmlPullParser.START_DOCUMENT:

					operdata = new OperterData();
					operdata.plannames = new ArrayList<OperterData.Operplannames>(); // 套餐信息
					operdata.numberpool = new ArrayList<OperterData.numberpool>();// 号码池
					break;

				case XmlPullParser.START_TAG:

					if ("package".equals(pullParser.getName())) {
						
						OperterData.Operplannames plan= new OperterData.Operplannames();
						
						plan.prcie  =pullParser.getAttributeValue(0);  //套餐价格
						plan.note =pullParser.getAttributeValue(1);    //套餐详细说明
						plan.planname=pullParser.getAttributeValue(2); //套餐名
						operdata.plannames.add(plan);
						
					} else if ("telephone".equals(pullParser.getName())) {
						
						OperterData.numberpool  pool =new OperterData.numberpool();
						pool.isUsed=false;
						pool.phonenum=pullParser.getAttributeValue(0);
						pool.prcie=pullParser.getAttributeValue(1);
						operdata.numberpool.add(pool);
					}
					break;

				case XmlPullParser.END_TAG:

					break;
				}
				event = pullParser.next();
			}
			OperterData.numberpool morenumberpool = new OperterData.numberpool();
			morenumberpool.phonenum="more>>";
			
			operdata.numberpool.add(morenumberpool);
			OperterData.Operplannames moreplans= new OperterData.Operplannames();
			operdata.plannames.add(moreplans);
			moreplans.planname="more>>";
			
			return operdata;
		} catch (Exception e) {

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