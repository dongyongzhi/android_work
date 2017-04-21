package com.yfcomm.m18;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.yfcomm.R;
import com.yfcomm.mpos.api.SwiperController;
import com.yfcomm.mpos.model.syn.WorkKey;
import com.yfcomm.mpos.utils.ByteUtils;

public class WriteKeyActivity extends BaseActivity   implements OnClickListener{

	private EditText tdk,tdkIndex,tdkCheckValue;
	private EditText pik,pikIndex,pikCheckValue;
	private EditText mak,makIndex,makCheckValue;
	
	private Button btnWriteKey,btnCancel;
	
	private ProgressDialog progressDialog;
	private SwiperController swiper;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writekey);
		
		tdk = (EditText)this.findViewById(R.id.tdk);
		tdkIndex = (EditText)this.findViewById(R.id.tdkIndex);
		tdkCheckValue = (EditText)this.findViewById(R.id.tdkCheckValue);
		
		pik = (EditText)this.findViewById(R.id.pik);
		pikIndex = (EditText)this.findViewById(R.id.pikIndex);
		pikCheckValue = (EditText)this.findViewById(R.id.pikCheckValue);
		
		mak = (EditText)this.findViewById(R.id.mak);
		makIndex = (EditText)this.findViewById(R.id.makIndex);
		makCheckValue = (EditText)this.findViewById(R.id.makCheckValue);
		
		btnWriteKey = (Button)this.findViewById(R.id.btnWriteKey);
		btnCancel = (Button)this.findViewById(R.id.btnCancel);
		
		this.btnWriteKey.setOnClickListener(this);
		this.btnCancel.setOnClickListener(this);
		
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
		case R.id.btnWriteKey:
			
			WorkKey wk = new WorkKey();
			
			//TDK 处理
			if(tdkIndex.getText().length()>0) {
				wk.setTdkIndex((byte)Integer.parseInt(tdkIndex.getText().toString()));
			}
			if(tdk.getText().length()> 0 && tdk.getText().length()!=32) {
				new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
				.setMessage("TDK密钥长度应为32个HEX字符")
				.setPositiveButton("确定", null) 
				.show();
				return;
			}else {
				wk.setTdk(ByteUtils.hexToByte(tdk.getText().toString()));
			}
			if(tdkCheckValue.getText().length()>0 && tdkCheckValue.getText().length()!=8) {
				new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
				.setMessage("TDK CheckValue长度应为8个HEX字符")
				.setPositiveButton("确定", null) 
				.show();
				return;
			}else {
				wk.setTdkCheckValue(ByteUtils.hexToByte(tdkCheckValue.getText().toString()));
			}
			
			//PIK 处理
			if(pikIndex.getText().length()>0) {
				wk.setPikIndex((byte)Integer.parseInt(pikIndex.getText().toString()));
			}
			if(pik.getText().length()> 0 && pik.getText().length()!=32) {
				new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
				.setMessage("PIK密钥长度应为32个HEX字符")
				.setPositiveButton("确定", null) 
				.show();
				return;
			}else {
				wk.setPik(ByteUtils.hexToByte(pik.getText().toString()));
			}
			if(pikCheckValue.getText().length()>0 && pikCheckValue.getText().length()!=8) {
				new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
				.setMessage("PIK CheckValue长度应为8个HEX字符")
				.setPositiveButton("确定", null) 
				.show();
				return;
			}else {
				wk.setPikCheckValue(ByteUtils.hexToByte(pikCheckValue.getText().toString()));
			}
			
			//MAK处理
			if(makIndex.getText().length()>0) {
				wk.setMakIndex((byte)Integer.parseInt(makIndex.getText().toString()));
			}
			if(mak.getText().length()> 0 && mak.getText().length()!=32) {
				new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
				.setMessage("MAK密钥长度应为32个HEX字符")
				.setPositiveButton("确定", null) 
				.show();
				return;
			}else {
				wk.setMak(ByteUtils.hexToByte(mak.getText().toString()));
			}
			if(makCheckValue.getText().length()>0 && makCheckValue.getText().length()!=8) {
				new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
				.setMessage("MAK CheckValue长度应为8个HEX字符")
				.setPositiveButton("确定", null) 
				.show();
				return;
			}else {
				wk.setMakCheckValue(ByteUtils.hexToByte(makCheckValue.getText().toString()));
			}
			
			//导入密钥
			progressDialog = ProgressDialog.show(this, null, "正在写入", false);
			this.swiper.writeWorkKey(wk);
			break;
		}
	}
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			
			new AlertDialog.Builder(WriteKeyActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		@Override
		public void onResultSuccess(int nType) {
			progressDialog.dismiss();
		}
	};
}
