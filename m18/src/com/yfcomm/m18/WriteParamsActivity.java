package com.yfcomm.m18;


import com.yfcomm.R;
import com.yfcomm.mpos.api.SwiperController;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 写入参数
 *
 */
public class WriteParamsActivity extends BaseActivity  implements OnClickListener{

	private EditText customerNo,termNo,batchNo,serialNo;
	private SwiperController swiper;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writeparams);
		
		this.findViewById(R.id.btnSave).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
		
		customerNo = (EditText)this.findViewById(R.id.customerNo);
		termNo = (EditText)this.findViewById(R.id.termNo);
		batchNo = (EditText)this.findViewById(R.id.batchNo);
		serialNo = (EditText)this.findViewById(R.id.serialNo);
		
		 //初始化刷卡器类
	    swiper = new SwiperController(this,swiperListener);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		//取消
		case R.id.btnCancel:
			this.finish();
			break;
		
		//写入
		case R.id.btnSave:
			if(this.customerNo.getText().length() == 0) {
				new AlertDialog.Builder(this).setTitle("提示")
					.setMessage("请输入商户号")
					.setPositiveButton("确定", null) 
					.show();
				this.customerNo.setFocusable(true);
				return;
			}
			if(this.termNo.getText().length() == 0) {
				new AlertDialog.Builder(this).setTitle("提示")
					.setMessage("请输入终端号")
					.setPositiveButton("确定", null) 
					.show();
				this.termNo.setFocusable(true);
				return;
			}
			
			progressDialog = ProgressDialog.show(this, null, "正在执行...", false);
			progressDialog.show();
			//写入
			swiper.setDeviceData(customerNo.getText().toString(), termNo.getText().toString(), 
					serialNo.getText().toString(), batchNo.getText().toString());
			
			break;
		}
	}
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			new AlertDialog.Builder(WriteParamsActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		public void onClose(){
			progressDialog.dismiss();
			new AlertDialog.Builder(WriteParamsActivity.this).setTitle("提示")
			.setMessage("连接己关闭")
			.setPositiveButton("确定", null) 
			.show();
		}
	 
		@Override
		public void onResultSuccess(int ntype) {
			progressDialog.dismiss();
			Toast.makeText(WriteParamsActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
		}
	};
}
