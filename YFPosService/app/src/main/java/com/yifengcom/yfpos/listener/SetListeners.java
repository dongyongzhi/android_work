package com.yifengcom.yfpos.listener;

public interface SetListeners {
	
	/**
	 * 打印成功
	 */
	public interface PrintProcessListener extends ErrorListener{
		/**
		 * 写入成功
		 */
		void onPrintProcessSuccess(byte type);
	}

	/**
	 * 设置终端时间回调
	 * @author qc
	 *
	 */
	public interface SetDateTimeListener extends ErrorListener{
		/**
		 * 设置成功
		 */
		void onSetDateTimeSuccess();
	}
	
	

	/**
	 * 更 新主密钥  监听
	 * @author qc
	 *
	 */
	public interface UpdateMainKeyListener  extends ErrorListener{
		/**
		 * 更新成功
		 */
		void onUpdateMainKeySuccess();
	}
	
	/**
	 * 更 新工作密钥  监听
	 * @author qc
	 *
	 */
	public interface UpdateWorkKeyListener  extends ErrorListener{
		/**
		 * 更新成功
		 */
		void onUpdateWorkKeySuccess();
	}
	
	/**
	 * 开启读卡器
	 * @author qc
	 *
	 */
	public interface OpenReadCardListener extends ErrorListener {
		
		void onOpenReadCardSuccess();
	}
	
	/**
	 * pboc 联机交易结果
	 * @author qc
	 *
	 */
	public interface PBOCOnlineDataProcessListener extends ErrorListener{
		
		/**
		 * 写入成功
		 */
		void onWriteProcessSuccess(byte[] icdata);
	}
	
	/**
	 * 重启回调
	 * @author qc
	 *
	 */
	public interface ResetListener extends ErrorListener {
		
		/**
		 * 重启成功
		 */
		void onResetSuccess();
	}
	 
	/**
	 * 数据加密
	 * @author qc
	 *
	 */
	public interface EncryptListener extends ErrorListener{
		
		/**
		 * 加密成功
		 * @param random      产生的随机数
		 * @param cipher  密文
		 */
		void onEncryptSuccess(byte[] random,byte[] cipher);
	}
	
	/**
	 * 设置数据
	 * @author qc
	 *
	 */
	public interface SetDeviceDataListener  extends ErrorListener{
		
		/**
		 * 写入成功
		 */
		void onSetDeviceDataSuccess();
	} 
	
	public interface UpdateAidListener  extends ErrorListener{
		
		void onUpdateAidSuccess();
	}
	
	public interface UpdatePublicKeyListener  extends ErrorListener{
		
		void onUpdatePublicKeySuccess();
	}
	
	public interface DisplayListener   extends ErrorListener{
		
		void onDisplaySuccess();
	}
	
	public interface CheckSystemListener  extends ErrorListener{
		
		void onCheckSuccess(String sn,String softVersion);
	}
}
