package com.yifeng.skzs.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/***
 * 文件读写操作类
 * 
 * @author 吴家宏 2011-04-02
 */
public class FileUtils {
	private String SDPATH;

	private int FILESIZE = 4 * 1024;// 文件大小4M

	public String getSDPATH() {
		return SDPATH;
	}

	/**
	 * 判断存储卡是否存在
	 * 
	 * @return 存在时返回true
	 */
	public boolean checkSDCard() {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * android 1.5,1.6路径不一样 得到当前外部存储设备的目录( /SDCARD )
	 */
	public FileUtils() {
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * 删除文件
	 * 
	 * @param strFileName
	 *            需要删除的文件名
	 * @return 0表示文件不存在，-1删除失败，1删除成功
	 */
	public int delFile(String strFileName) {
		int ret = 0;
		try {
			File myFile = new File(strFileName);
			if (myFile.exists()) {
				myFile.delete();
				ret = 1;
			}
			ret = 0;
		} catch (Exception e) {
			ret = -1;
		}
		return ret;
	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * 在SD卡上创建目录
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * 判断SD卡上的文件夹是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里面的数据写入到SD卡中
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {
		File file = null;
		OutputStream output = null;
		createSDDir(path);
		try {
			file = createSDFile(path + fileName);

			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			int temp;
			while ((temp = input.read(buffer)) != -1) {
				output.write(buffer, 0, temp);
			}
			output.flush();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return file;
	}

	/**
	 * 查找sdcard文件夹下的所有mp3文件,得到一个List 集合
	 * 
	 * @param path
	 * @return
	 */
	public List<Map<String, Object>> getFiles(String path) {
		File file = new File(SDPATH + File.separator + path);
		File[] files = file.listFiles();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < files.length; i++) {
			// 判断是不是mp3结尾的
			if (files[i].getName().endsWith("mp3")) {
				map = new HashMap<String, Object>();
				map.put("fileName", files[i].getName());// 文件名称
				map.put("fileSize", files[i].length());// 文件大小
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 读取源文件内容
	 * 
	 * @param filename
	 *            String 文件路径
	 * @throws IOException
	 * @return byte[] 文件内容
	 */
	public static byte[] readFile(String filename) {
		File file = new File(filename);
		long len = file.length();
		Log.i("userinfo", "file.length():" + len);
		byte[] bytes = new byte[(int) len];

		BufferedInputStream bufferedInputStream = null;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(
					file));

			int r = bufferedInputStream.read(bytes);
			if (r != len) {
				bufferedInputStream.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return bytes;
	}

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现表单提交功能
	 * 
	 * @param actionUrl
	 *            上传路径
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static String post(String actionUrl, Map<String, String> params,
			FormFile[] files) {
		String result = "";

		String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
		String MULTIPART_FORM_DATA = "multipart/form-data";

		URL url;
		DataOutputStream outStream = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(actionUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);

			StringBuilder sb = new StringBuilder();

			// 上传的表单参数部分，格式请参考文章
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据

			// 上传的文件部分，格式请参考文章
			if (files != null) {
				for (FormFile file : files) {
					StringBuilder split = new StringBuilder();
					split.append("--");
					split.append(BOUNDARY);
					split.append("\r\n");
					split.append("Content-Disposition: form-data;name=\""
							+ file.getFormname() + "\";filename=\""
							+ file.getFilname() + "\"\r\n");
					split.append("Content-Type: " + file.getContentType()
							+ "\r\n\r\n");
					outStream.write(split.toString().getBytes());
					outStream.write(file.getData(), 0, file.getData().length);
					outStream.write("\r\n".getBytes());
				}
			}
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			outStream.close();

			int cah = conn.getResponseCode();
			Log.i("userinfo", "" + cah);

			if (cah != 200)
				throw new RuntimeException("请求url失败");
			is = conn.getInputStream();
			isr = new InputStreamReader(is, "utf-8");// 解决乱码问题，保证服务器端返回数据正常显示
			br = new BufferedReader(isr);

			String line = null;
			StringBuffer res = new StringBuffer();

			while ((line = br.readLine()) != null) {
				res.append(line);
			}
			// String result = br.readLine();
			if (res != null) {
				result = res.toString();
			}
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(conn!= null ) {
				conn.disconnect();
			}
		}

		return result;
	}

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现表单提交功能 (包括cookie提交)
	 * 
	 * @param actionUrl
	 *            上传路径
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static String postCookie(String actionUrl, String cookie,
			Map<String, String> params, FormFile[] files) {
		String result = "";

		String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
		String MULTIPART_FORM_DATA = "multipart/form-data";

		URL url;
		DataOutputStream outStream  = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(actionUrl);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", cookie);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);

			StringBuilder sb = new StringBuilder();

			// 上传的表单参数部分，格式请参考文章
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}

			outStream = new DataOutputStream(conn.getOutputStream());
			
			outStream.write(sb.toString().getBytes());// 发送表单字段数据

			// 上传的文件部分，格式请参考文章
			if (files != null) {
				for (FormFile file : files) {
					StringBuilder split = new StringBuilder();
					split.append("--");
					split.append(BOUNDARY);
					split.append("\r\n");
					split.append("Content-Disposition: form-data;name=\""
							+ file.getFormname() + "\";filename=\""
							+ file.getFilname() + "\"\r\n");
					split.append("Content-Type: " + file.getContentType()
							+ "\r\n\r\n");
					outStream.write(split.toString().getBytes());
					outStream.write(file.getData(), 0, file.getData().length);
					outStream.write("\r\n".getBytes());
				}
			}
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			outStream.close();

			int cah = conn.getResponseCode();
			Log.i("userinfo", "" + cah);

			if (cah != 200)
				throw new RuntimeException("请求url失败");
			InputStream is = conn.getInputStream();
			isr = new InputStreamReader(is, "utf-8");// 解决乱码问题，保证服务器端返回数据正常显示
			br = new BufferedReader(isr);

			String line = null;
			StringBuffer res = new StringBuffer();

			while ((line = br.readLine()) != null) {
				res.append(line);
			}

			// String result = br.readLine();
			if (res != null) {
				result = res.toString();
			}
			

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if( br!=null ) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if ( isr!=null ) {
				try {
					isr.close();
				} catch (IOException e) {
				}
			}
			if( outStream!= null ) {
				try {
					outStream.close();
				} catch (IOException e) {
				}
			}
			
			if(conn!= null) {
				conn.disconnect();
			}
		}

		return result;
	}

	/**
	 * 根据给定本地路径读取本地资源图片
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		return BitmapFactory.decodeFile(url);
	}
}
