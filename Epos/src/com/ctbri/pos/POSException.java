package com.ctbri.pos;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;

import com.ctbri.ElecException;
import com.ctbri.R;
import com.ctbri.domain.ResponseCode;

public class POSException extends ElecException {

	/**  */
	private static final long serialVersionUID = 7148703885480607048L;
	
	private String message;
	private SpannableString  mSpannableString;

	public POSException(String message) {
		super(message);
		this.message = message;
		mSpannableString = new SpannableString(message);
	}
	
	public POSException(ResponseCode errCode,Context context){
		super(errCode.getMessage());
		
		//返回错误码pos终端未找到
		if(errCode == ResponseCode.CONNECT_ERROR){
			//获取帮助信息
			String help = context.getString(R.string.wran_term_not_found_help);
			String errMsg = errCode.getMessage();
			message = String.format("%s%s", errCode.getMessage(),help);
			
			mSpannableString = new SpannableString(message);
			mSpannableString.setSpan(new AbsoluteSizeSpan(15),errMsg.length(),message.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //设置字体大小
			//ss.setSpan(what, start, end, flags)
			
			message = String.format("%s%s", errCode.getMessage(),help);
		}else{
			this.message = errCode.getMessage();
			mSpannableString = new SpannableString(message);
		}
	}
	
	public String getMessage(){
		return this.message;
	}
	
	public SpannableString getSpannableString(){
		return mSpannableString;
	}
}
