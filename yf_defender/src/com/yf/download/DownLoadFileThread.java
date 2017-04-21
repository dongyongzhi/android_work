package com.yf.download;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.PublicKey;

import com.yf.define.PublicDefine;
import com.yf.tools.ByteUtils;
import com.yf.tools.MD5;
import com.yf.tools.RSAUtils;
import android.util.YFComm;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class DownLoadFileThread extends Thread {

	private final String url;
	private final static String TAG = "DownLoadFileThread";
	private Handler handler;
	private boolean Isverify = false;
	private int flag;
	private Context context;
	private File filebak;

	public DownLoadFileThread(String url, int flag, Handler mHandler, Context context) {
		this.url = url;
		this.handler = mHandler;
		this.flag = flag;
		this.context = context;

		File file = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + "/",
				PublicDefine.save_xml);

		filebak = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + "/",
				PublicDefine.save_bakxml);

		if (file.exists()) {
			file.delete();
		}

	}
	public void run() {

		URL fileUrl;
		HttpURLConnection conn = null;
		InputStream is = null;
		FileOutputStream fos = null;

		try {
			Log.i(TAG, "begin download xml data");
			fileUrl = new URL(url);
			conn = (HttpURLConnection) fileUrl.openConnection();
			conn.setConnectTimeout(PublicDefine.TIME_OUT);
			conn.setReadTimeout(PublicDefine.TIME_OUT);
			//conn.setRequestProperty("Accept-Encoding", "identity"); 
			conn.connect();
			//long a=conn.getContentLength();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {

				is = conn.getInputStream();
				ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
				fos = context.openFileOutput(PublicDefine.save_xml, Context.MODE_PRIVATE);
				byte[] buff = new byte[1024]; // buff用于存放循环读取的临时数据
				int rc = 0;

				while ((rc = is.read(buff, 0, buff.length)) > 0) {
					swapStream.write(buff, 0, rc);
					fos.write(buff, 0, rc);
					fos.flush();
				}
				swapStream.close();

				Isverify = YFComm.isYFCommFile(
						context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + PublicDefine.save_xml);

				if (Isverify) {

					File file = new File(context.getApplicationContext().getFilesDir().getAbsolutePath() + "/",
							PublicDefine.save_xml);
					if (IsCoverFile(file, filebak)){
					    Log.i(TAG, PublicDefine.save_xml+"被覆盖");
						copyFiletoBakUp(file, filebak);
						handler.obtainMessage(PublicDefine.YF_Complete, 1, 0).sendToTarget();
					}else{
						handler.obtainMessage(PublicDefine.YF_Complete, 0, 0).sendToTarget();
					}
				} else {
					handler.obtainMessage(PublicDefine.MSG_SIGNED_FAILED, "签名验证失败").sendToTarget();
				}

			}

		} catch (MalformedURLException e) {

			sendErrorMsg(PublicDefine.MSG_NOT_FOUNDFILE, "访问网络文件不存在...");

		} catch (ConnectException e) {

			sendErrorMsg(PublicDefine.MSG_CONNECT_HTTP_ERROR, "网络连接失败,请检查网络设置...");

		} catch (SocketTimeoutException e) {

			sendErrorMsg(PublicDefine.MSG_CONNECT_HTTP_ERROR, "网络连接超时,请检查网络设置...");

		} catch (IOException e) {

			sendErrorMsg(PublicDefine.MSG_DOWNLOAD_ERROR, "网络下载异常，请重新打开...");

		} catch (Exception e) {

			sendErrorMsg(PublicDefine.MSG_DOWNLOAD_ERROR, "网络下载异常，请重新打开...");

		} finally {

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}

	public boolean IsCoverFile(File src, File bak) {

		boolean IsneadCover = true;
		
		if (bak.exists()) {
			if (GetSign(src).equals(GetSign(bak))) {
				IsneadCover = false;
			}
		}
		return IsneadCover;
	}

	public String GetSign(File file) {

		byte[] buffer = new byte[YFComm.YFPACKLENGTH];
		long fileLen = file.length();
		if (fileLen < YFComm.YFPACKLENGTH)
			return null;
		fileLen -= YFComm.YFPACKLENGTH;
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file, "rw");
			rf.seek(fileLen);
			rf.read(buffer, 0, YFComm.YFPACKLENGTH);

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				rf.close();
			} catch (IOException e) {
			}
		}
		return new String(buffer);
	}

	public void copyFiletoBakUp(File src, File des) {

		InputStream inStream;
		FileOutputStream fs;
		try {
			inStream = new FileInputStream(src);
			fs = new FileOutputStream(des);
			byte[] buffer = new byte[1444];
			int byteread;
			while ((byteread = inStream.read(buffer)) != -1) {
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
			fs.close();

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void verify(byte[] xmlData, byte[] signed) {

		String md51 = MD5.getMD5(xmlData).toUpperCase();

		try {
			PublicKey publicKey = RSAUtils.loadPublicKey(PublicDefine.RSApublic_key);

			byte[] sign_one = new byte[256];
			byte[] sign_two = new byte[256];

			System.arraycopy(signed, 0, sign_one, 0, 256);
			System.arraycopy(signed, 256, sign_two, 0, 256);

			String md52 = new String(RSAUtils.decryptData(ByteUtils.hexToByte(new String(sign_one)), publicKey))
					+ new String(RSAUtils.decryptData(ByteUtils.hexToByte(new String(sign_two)), publicKey));

			if (md51.equals(md52)) {
				Isverify = true;
				Log.i(TAG, "verify sucess...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "verify failed...");
		}
	}

	public void sendErrorMsg(int msgId, String msg) {
		handler.obtainMessage(msgId, flag, 1, msg).sendToTarget();
	}
}