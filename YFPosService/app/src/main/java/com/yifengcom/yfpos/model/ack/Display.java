package com.yifengcom.yfpos.model.ack;

import java.io.UnsupportedEncodingException;
import android.view.Gravity;
import com.yifengcom.yfpos.codec.DevicePackage;

/**
 * 屏幕送显
 */
public class Display extends AckPackage {

	private static final long serialVersionUID = -7249768000786065246L;

	private int type = Gravity.CENTER; // 显示类型,默认居中
	private int time; // 显示时间
	private String content = ""; // 显示内容
	private int strLen;
	private byte[] singleData;

	@Override
	public void decode(DevicePackage ack) {
		byte[] body = ack.getBody();
		int type = body[1];
		switch (type) {
		case 0x00:
			this.type = Gravity.CENTER;
			break;
		case 0x01:
			this.type = Gravity.LEFT;
			break;
		case 0x02:
			this.type = Gravity.RIGHT;
			break;
		}
		this.time = body[2];
		strLen = body.length - 3; // 所有内容长度
		singleData = new byte[strLen]; // 存放所有内容
		System.arraycopy(body, 3, singleData, 0, strLen);
		getContent(singleData);
	}

	private void getContent(byte[] body) {
		int len = body[1]; // 取出单条内容长度
		int singleLen = len + 2; 
		String str;
		try {
			str = new String(body, 2, len, "gb2312");
			this.content += str + "\n";

			if ((singleLen) != body.length) {
				strLen = body.length - (singleLen);
				singleData = new byte[strLen];
				System.arraycopy(body, singleLen, singleData, 0, strLen);
				getContent(singleData);
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public String getContent() {
		return content;
	}

	public int getType() {
		return type;
	}

	public int getTime() {
		return time;
	}
}
