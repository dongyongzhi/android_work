package com.yfcomm.pos.callback;

/**
 * 下载回调
 * @author qinchuan
 *
 */
public interface DownloadCallback {

	/**
	 * 下载进度更新事件
	 * @param current  当前完成进度
	 * @param total    总大小 
	 */
	void onDownloadProgress(int current,int total);
	
	/**
	 * 下载错误事件
	 * @param code     错误代码
	 * @param message  错误信息
	 */
	void onDownloadError(String code,String message);
	
	/**
	 * 更新完成
	 */
	void onDownLoadComplete();
	
}
