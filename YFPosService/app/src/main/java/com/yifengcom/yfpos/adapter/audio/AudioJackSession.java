package com.yifengcom.yfpos.adapter.audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.yifengcom.yfpos.DeviceInfo;
import com.yifengcom.yfpos.DeviceSession;
import com.yifengcom.yfpos.codec.DeviceDecoder;
import com.yifengcom.yfpos.listener.DeviceStateChangeListener;
import com.yifengcom.yfpos.listener.ConnectionStateListener;

/**
 * 音频设备会话
 * @author qc
 *
 */
public class AudioJackSession  implements DeviceSession{

	private final static int audioSource = MediaRecorder.AudioSource.MIC;
	//录制频率
	private final static int sampleRate = 44100;
	//录制通道
	private final static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	//录制编码格式  
	private final static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	
	private final static int streamType = AudioManager.STREAM_MUSIC;
	//
	private final static int mode = AudioTrack.MODE_STREAM;
	
	private final static int BUFFER_SIZE = 2048;
	 
	private final AudioRecord audioRecord;
	private final AudioTrack audioTrack;
	private final DeviceDecoder decoder;
	private final Context context;
	private RecvThread recvThread = null;
	
	private boolean isConnected = false;
	private ConnectionStateListener listener;
	
	public AudioJackSession(Context context,DeviceDecoder decoder)  {
		this.context = context;
		this.decoder = decoder;
		
		//录制
		int recordBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
		audioRecord = new AudioRecord(audioSource, sampleRate, AudioFormat.CHANNEL_IN_FRONT, audioFormat, 4 * recordBufSize);
		
		//播放
		int playSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);
		audioTrack = new AudioTrack(streamType,sampleRate, AudioFormat.CHANNEL_IN_LEFT, audioFormat, 2 *playSize , mode);
	}

	@Override
	public void connect(DeviceInfo deviceInfo, ConnectionStateListener listener) {
		this.listener = listener;
	}
	
	public Context getContext() {
		return context;
	}
	
	/**
	 * 设置是否连接
	 * @param isConnected
	 */
	public synchronized void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
		if(this.isConnected) {
			audioTrack.play();
			this.recvThread = new RecvThread();
		}
	}
	
	/**
	 * 关闭设备
	 */
	@Override
	public void close() {
		setConnected(false);
		audioTrack.stop();
		this.recvThread = null;
		this.decoder.reset();
	}
	
	/**
	 * 获取是否连接
	 * @return
	 */
	@Override
	public boolean connected() {
		return this.isConnected;
	}
	
	
	/**
	 * 写入数据
	 * @param data  数据内容
	 * @return
	 */
	@Override
	public boolean write(byte[] data) {
		return this.write(data, 0, data.length);
	}
	
	/**
	 * 写入数据
	 * @param data   数据内容
	 * @param offset 偏移地址
	 * @param count  个数
	 * @return
	 */
	@Override
	public boolean write(byte[] data,int offset,int count) {
		if(this.connected()) {
			return this.audioTrack.write(data, offset, count)>0;
		} else  {
			return false;
		}
	}
	
	public RecvThread getRecvThread() {
		return recvThread;
	}

	/**
	 * 接收数据线程
	 * @author qc
	 *
	 */
	public class RecvThread implements Runnable {
		
		public RecvThread() {
			Thread thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run() {
			//开始录制 
			audioRecord.startRecording(); 
			
			//发送连接成功信息
			if(listener !=null ) {
				listener.onConnected();
			}
			
			byte[] buf = new byte[BUFFER_SIZE];
			
			while(isConnected) {
				int len = audioRecord.read(buf, 0, BUFFER_SIZE);
				//加入内容
				decoder.append(buf, 0, len);
			}
			audioRecord.stop();
			audioRecord.release();
		}
	}

	@Override
	public void setOnDeviceStateChangeListener(
			DeviceStateChangeListener listener) {
		
	}

	@Override
	public void connect(DeviceInfo deviceInfo, long timeout,
			ConnectionStateListener openDeviceListener) {
		
	}

}
