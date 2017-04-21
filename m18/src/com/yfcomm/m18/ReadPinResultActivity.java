package com.yfcomm.m18;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.yfcomm.R;
import com.yfcomm.mpos.listener.ReaderListeners.ReadPinListener;
import com.yfcomm.mpos.model.syn.ReadPin;
import com.yfcomm.mpos.utils.ByteUtils;

public class ReadPinResultActivity  extends BaseActivity  implements OnClickListener,ReadPinListener{

	ProgressDialog pd=null;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.pinshow);
		
		this.findViewById(R.id.btnConfirm).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		//取消
		case R.id.btnCancel:
			this.finish();
			break;
		
		case R.id.btnConfirm:
			EditText random = (EditText)this.findViewById(R.id.random);
			
			if(random.getText().length() !=8) {
				new AlertDialog.Builder(this).setTitle("提示")
					.setMessage("请输入8位随机数")
					.setPositiveButton("确定", null) 
					.show();
				random.setFocusable(true);
				return;
			}
			pd = ProgressDialog.show(this, "", "正在等待输密...");	
			
			ReadPin pin = new ReadPin();
			pin.setRandom(random.getText().toString().getBytes());
			pin.setTimeout((byte)30);
			break;
		}
	}

	@Override
	public void onError(int errorCode, String errorMessage) {
		pd.dismiss();
		
		new AlertDialog.Builder(this).setTitle("提示")
		.setMessage("操作失败，返回码："+ errorCode +" 信息:"+errorMessage)
		.setPositiveButton("确定", null) 
		.show();
	}

	@Override
	public void onTimeout() {
		pd.dismiss();
		
		new AlertDialog.Builder(this).setTitle("错误")
		.setMessage("输密超时")
		.setPositiveButton("确定", null) 
		.show();
	}

	@Override
	public void onReadPinSuccess(int pinNumber, byte[] pinBlock) {
		pd.dismiss();
		
		((TextView)this.findViewById(R.id.pinNumber)).setText("密码个数:"+pinNumber);
		((TextView)this.findViewById(R.id.pinBlock)).setText("PINBLOCK:"+ByteUtils.byteToHex(pinBlock));
	}

}
