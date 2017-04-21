package com.yifengcom.yfpos.task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;

import com.yifengcom.yfpos.DefaultDeviceComm;
import com.yifengcom.yfpos.DeviceComm;
import com.yifengcom.yfpos.ErrorCode;
import com.yifengcom.yfpos.codec.DevicePackage;
import com.yifengcom.yfpos.codec.DevicePackage.PackageType;
import com.yifengcom.yfpos.codec.PackageBuilder;
import com.yifengcom.yfpos.exception.MPOSException;
import com.yifengcom.yfpos.listener.DownloadListener;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
 * 文件上传任务
 * @author qc
 *
 */
public class UploadFileTask  extends AsyncTask<Void,Long,Integer>{

	//包大小暂按120 传输,超过800 有可能失败
	private final static int BUFFER_SIZE = 800;
	private final File file;
	private final long total;  //文件总大小
	private final DownloadListener listener;
	private final DeviceComm deviceComm;
	
	/**升级开始**/
	private final static byte START = 0x01;
	/** 升级中，用于传输数据**/
	private final static byte UPLOADING = 0x02;
	/**升级结束**/
	private final static byte COMPLETE = 0x03;
	private final int timeout = 10*1000;
	
	public UploadFileTask(String filePath,DeviceComm dc,DownloadListener listener) {
		this.deviceComm = dc;
		this.file = new File(filePath);
		this.total = this.file.length();
		this.listener = listener;
	}
	
	/**
	 * 开始处理
	 */
	@Override
	protected Integer doInBackground(Void... params) {
		
		this.publishProgress(0L);
		
		byte[] body  = new byte[7];
		//开始下载
		body[0] = (byte)START; 
		//文件长度
		System.arraycopy(ByteUtils.unsignedShortLE(Long.valueOf(this.total).intValue()), 0, body, 5, 2);
		//开始上传程序
		body[5] = 0x02;
		InputStream is = null;
		try{
			ByteArrayOutputStream in = new ByteArrayOutputStream(1200);
			in.write(body);
			//文件总大小
			in.write(ByteUtils.unsignedIntLE(total));
			//请求上传
			this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE,in.toByteArray()),timeout);
			in.reset();
			
			//开下获取文件内容
			is = new FileInputStream(file);
			byte[] buf = new byte[BUFFER_SIZE];
			int len = -1;
			//处理的小大小
			long progressTotal = 0L; 
			DevicePackage ack;
			
			while ((len = is.read(buf, 0, BUFFER_SIZE)) != -1) {
				in.reset();
				body[0] = (byte)UPLOADING;
				//偏移量
				System.arraycopy(ByteUtils.unsignedIntLE(progressTotal), 0, body, 1, 4);
				//当前数据长度
				System.arraycopy(ByteUtils.unsignedShortLE(len), 0, body, 5, 2);
				
				in.write(body);
				//写入文件内容
				in.write(buf, 0, len);
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE,in.toByteArray()),timeout);
				
				//应答升级结束
				if(ack.getPackType() == PackageType.ACK_SUCC.getType() && ack.getBody()[0] == COMPLETE) {
					return ack.getBody()[0] & 0xFF;
				}
				
				//更新处理进度
				progressTotal += len;
				this.publishProgress(progressTotal);
			}
			
			//升级结束
			body[0] = (byte)COMPLETE;
			System.arraycopy(ByteUtils.unsignedIntLE(progressTotal), 0, body, 1, 4);
			this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_UPDATE,body));
			
		} catch (MPOSException e) {
			return e.getErrorCode();
		} catch (FileNotFoundException e) {
			return ErrorCode.FILENOTFOUND.getCode();
		} catch (IOException e) {
			return ErrorCode.FILENOTFOUND.getCode();
		}  finally{
			if(is!=null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ErrorCode.SUCC.getCode();
	}
	
	
	/**
	 * 处理进度
	 */
	protected void onProgressUpdate(Long... args) {
		Long current = (Long)args[0];
		listener.onDownloadProgress(current, total);
	}
	
	/**
	 * 处理完成
	 */
	protected void onPostExecute(Integer code) {
		
		if(code == ErrorCode.SUCC.getCode()) { 
			listener.onDownloadComplete();
		} else {
			listener.onError(code, ((DefaultDeviceComm)deviceComm).getErrorMessage(code));
		}
	}
}
