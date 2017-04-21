package com.yifengcom.yfpos.task;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 * 图片上传任务
 */
public class UploadSignBitmapTask  extends AsyncTask<Void,Long,Integer>{

	private final static int BUFFER_SIZE = 800;
	private final DownloadListener listener;
	private final DeviceComm deviceComm;
	
	private final static byte START = 0x01;
	private final static byte UPLOADING = 0x02;
	private final static byte COMPLETE = 0x03;
	private final int timeout = 10*1000;
	
	private final String filePath;
	private int total;
	
	public UploadSignBitmapTask(String filePath , DeviceComm dc , DownloadListener listener) {
		this.deviceComm = dc;
		this.listener = listener;
		this.filePath = filePath;
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		this.publishProgress(0L);
		FileInputStream fis;
		ByteArrayOutputStream in;
		DevicePackage ack;
		try{
			fis = new FileInputStream(filePath);
			Bitmap bitmap  = BitmapFactory.decodeStream(fis);
			if(bitmap == null)
				return ErrorCode.FILENOTFOUND.getCode();
			byte[] date = bitmap2Array(bitmap);
			
			total = date.length - 4;

			byte[] body  = new byte[7]; 
			body[0] = (byte)START;
			System.arraycopy(date, 0, body, 3, 4);
			System.arraycopy(ByteUtils.unsignedShortLE(4), 0, body, 1, 2);
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SIGN,body),timeout);
			if(ack.getPackType() == PackageType.ACK_SUCC.getType()) {
				switch (ack.getBody()[0]) {
				case 0x01:
					return ErrorCode.UPLOAD_SIGIN_ERROR1.getCode();
				case 0x02:
					return ErrorCode.UPLOAD_SIGIN_ERROR2.getCode();
				case 0x03:
					return ErrorCode.UPLOAD_SIGIN_ERROR3.getCode();
				case 0x04:
					return ErrorCode.UPLOAD_SIGIN_ERROR4.getCode();
				}
			}
			
			//处理的小大小
			long progressTotal = 4L; 
			//当前数据传输位置
			int currentPos = 4;
			
			while (currentPos != total) {
				//每包默认长度
				int len = BUFFER_SIZE;
				
				if(total - (currentPos + len)  < 0){
					len = total - currentPos;
				}
				byte[] buf = new byte[len];
				//拷贝分包数据
				System.arraycopy(date, currentPos, buf, 0, buf.length);
				currentPos = currentPos + len;
				
				body[0] = (byte)UPLOADING;
				//数据长度
				System.arraycopy(ByteUtils.unsignedShortLE(len+4), 0, body, 1, 2);
				
				in = new ByteArrayOutputStream(body.length+buf.length);
				in.write(body);
				in.write(buf);
				
				ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SIGN,in.toByteArray()),timeout);
				if(ack.getPackType() == PackageType.ACK_SUCC.getType()) {
					switch (ack.getBody()[0]) {
					case 0x01:
						return ErrorCode.UPLOAD_SIGIN_ERROR1.getCode();
					case 0x02:
						return ErrorCode.UPLOAD_SIGIN_ERROR2.getCode();
					case 0x03:
						return ErrorCode.UPLOAD_SIGIN_ERROR3.getCode();
					case 0x04:
						return ErrorCode.UPLOAD_SIGIN_ERROR4.getCode();
					}
				}
				
				//更新处理进度
				progressTotal += len;
				this.publishProgress(progressTotal);
			}
			
			//通知上传完成
			body[0] = (byte)COMPLETE;
			System.arraycopy(ByteUtils.unsignedShortLE(4), 0, body, 1, 2);
			ack = this.deviceComm.execute(PackageBuilder.syn(PackageBuilder.CMD_SIGN,body));
			if(ack.getPackType() == PackageType.ACK_SUCC.getType()) {
				switch (ack.getBody()[0]) {
				case 0x01:
					return ErrorCode.UPLOAD_SIGIN_ERROR1.getCode();
				case 0x02:
					return ErrorCode.UPLOAD_SIGIN_ERROR2.getCode();
				case 0x03:
					return ErrorCode.UPLOAD_SIGIN_ERROR3.getCode();
				case 0x04:
					return ErrorCode.UPLOAD_SIGIN_ERROR4.getCode();
				}
			}
			
		}  catch (MPOSException e) {
			return e.getErrorCode();
		} catch (FileNotFoundException e) {
			return ErrorCode.FILENOTFOUND.getCode();
		} catch (IOException e) {
			return ErrorCode.FILENOTFOUND.getCode();
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
	
	protected byte[] bitmap2Array(Bitmap bitmap){

		  if (bitmap == null) return new byte[]{0x00,0x00,0x00,0x00};
		  
		  int w = (bitmap.getWidth()) , h = bitmap.getHeight();
		  int[] pixels=new int[w*h];
		  bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		  int width = bitmap.getWidth()/8*8;
		  if ((bitmap.getWidth() % 8)>0) width+=8;
		  int height =  bitmap.getHeight();
		  byte out[] = new byte[width/8*height+4];
		  out[0] = (byte)(((width) >> 8) & 0xff);
		  out[1] = (byte)(width &0xff);
		  out[2] = (byte)((height >> 8) & 0xff);
		  out[3] = (byte)(height &0xff);
		  for (int i=0;i<h; i++){
			  byte b = 0;
			  int x,j;
			  for (j=0; j<width/8;j++)
				  out[4+i*width/8+j] = 0;
			  for (j =0; j<w; j++){
				 
				  x = (byte)((pixels[i*w+j] &0xffffff)>0?0:1);
				  b = (byte)(x << (7-(j%8)));
				  out[4+i*width/8+j/8] |= b;
			  }
		  }
		  return out;
		}
}
