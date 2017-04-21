package com.yfcomm.mpos;

import android.content.Context;

public abstract class DeviceContext {
	
	private final static String DEFTYPE ="string";
	private final static String PREFIX  = "YF_";
	
	private final Context context;
	
    public DeviceContext(Context context) {
    	this.context = context;
    }
    
    /**
     * 获取错误描述信息
     * @param code  错误代码
     * @return  错误描述信息
     */
	public String getErrorMessage(int code) {
		try {
			ErrorCode error = ErrorCode.convert(code);
			return getErrorMessage(error);
			
		}catch(Exception e) {
			return null;
		}
	}
	 
	/**
	 * 获取错误描述信息
	 * @param error  错误信息
	 * @return 错误描述信息
	 */
	public String getErrorMessage(ErrorCode error) {
		int resId = this.context.getResources().getIdentifier(PREFIX + error.name(), DEFTYPE, this.context.getPackageName());
		if(resId==0) {
			return error.getDefaultMessage();
		} else {
			return this.context.getString(resId);
		}
	}

	public Context getContext() {
		return context;
	}
}
