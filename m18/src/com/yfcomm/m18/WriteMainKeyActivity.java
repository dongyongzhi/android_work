package com.yfcomm.m18;

import com.yfcomm.R;
import com.yfcomm.mpos.api.SwiperController;
import com.yfcomm.mpos.utils.ByteUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
 

/**
 * 写入主密钥
 * 
 */
public class WriteMainKeyActivity extends BaseActivity implements OnClickListener {

	private ProgressDialog progressDialog;
	private SwiperController swiper;
	
	private EditText mainKey;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writemainkey);
		mainKey = (EditText)this.findViewById(R.id.mainKey);
		
		this.findViewById(R.id.btnWriteKey).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
		
		 //初始化刷卡器类
	    swiper = new SwiperController(this,swiperListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 取消
		case R.id.btnCancel:
			this.finish();
			break;

		// 写入
		case R.id.btnWriteKey:
			if (this.mainKey.getText().length() == 0 || this.mainKey.getText().length() % 2!=0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("输入的主密钥有误").setPositiveButton("确定", null)
						.show();
				return;
			}
			
			progressDialog = ProgressDialog.show(this, null, "正在执行...", false);
			progressDialog.show();
			
			swiper.writeMainKey(ByteUtils.hexToByte(mainKey.getText().toString()));
			break;
		}
	}
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			new AlertDialog.Builder(WriteMainKeyActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		@Override
		public void onResultSuccess(int nType) {
			progressDialog.dismiss();
			Toast.makeText(WriteMainKeyActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
		}
	};
}
