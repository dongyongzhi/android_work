package com.yf.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.yf.define.PublicDefine;
import com.yf.define.YFShopAppInfo;
import com.yf.tools.MD5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

@SuppressLint({ "WorldReadableFiles", "DefaultLocale" })
public class DownloadApk extends Thread {

	private URL url; // 下载路径
	private long fileLength; // 下载的文件大小
	private String dir; // 文件的保存路径
	private Handler mHandler;
	private int item;
	private String filename = null;
	//private Context context;
	private final static int TIME_OUT = 20000; // 启动线程
	private String md5;
	private File file;
	private YFShopAppInfo app;

	public DownloadApk(Context context, String md5, URL url, String filePath,
			int item, YFShopAppInfo app, Handler mHandler) {
		this.url = url;
		this.mHandler = mHandler;
		this.item = item;
		filename = url.toString()
				.substring(url.toString().lastIndexOf("/") + 1);
		this.dir = filePath;
	//	this.context = context;
		this.md5 = md5;
		this.app = app;

		file = new File(dir + "/" + filename);
		if (file.exists()) {
			file.delete();
		}
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void run() {
		BufferedInputStream bis = null;
		FileOutputStream bos = null;
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection(); // 建立一个远程连接句柄，此时尚未真正连接
			conn.setConnectTimeout(TIME_OUT); // 设置连接超时时间为5秒
			conn.setReadTimeout(TIME_OUT);
			conn.connect();

			fileLength = conn.getContentLength();
			byte[] buffer = new byte[2048]; // 下载的缓冲池为8KB

			bis = new BufferedInputStream(conn.getInputStream());
			bos = new FileOutputStream(file);
			int downloadLength = 0; // 当前已下载的文件大小
			int bufferLength = 0;

			while ((bufferLength = bis.read(buffer, 0, buffer.length)) != -1
					&& !this.isInterrupted() && app.IsWaitingDownload) {
				bos.write(buffer, 0, bufferLength);
				bos.flush();
				downloadLength += bufferLength;
				mHandler.obtainMessage(PublicDefine.MSG_DOWNLOAD_PROGRESS,
						item, downloadLength, fileLength).sendToTarget();
			}
			if (app.IsWaitingDownload) {
				String md51 = MD5.getfileMd5(this.dir + "/" + filename);
				if (md51.equals(md5)) {
					mHandler.obtainMessage(PublicDefine.THREAD_FINISHED, item,
							1, this.dir + "/" + filename).sendToTarget(); // 发送下载完毕的消息
					app.IsWaitingDownload = false;// 下载完成，不需要继续下载
				} else {
					mHandler.obtainMessage(PublicDefine.MSG_MD5_FAILED, item,
							1, this.dir + "/" + filename).sendToTarget(); // 发送MD5验证失败消息
				}
			}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {

			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.close();
				}
				if (conn != null) {
					conn.disconnect();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			PublicDefine.isDownloading = false;
			mHandler.obtainMessage(PublicDefine.SCANNEADDOWNLOAD, item, 1, 0)
					.sendToTarget();
		}
	}

	public void changmod(String permission, String path) {
		String command = "chmod -R " + permission + " " + path;
		Runtime runtime = Runtime.getRuntime();
		try {
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
