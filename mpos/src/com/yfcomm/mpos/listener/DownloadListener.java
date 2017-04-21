package com.yfcomm.mpos.listener;

/**
 * 更新固件监听
 * @author qc
 *
 */
public interface DownloadListener extends ErrorListener {

	/**
	 * 固件更新进度
	 * @param current 当前完成的贞数
	 * @param total   总贞数
	 */
	void onDownloadProgress(long current,long total);
	
	/**
	 * 更新完成
	 */
	void onDownloadComplete();
}
