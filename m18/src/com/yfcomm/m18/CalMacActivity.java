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
import android.widget.TextView;

public class CalMacActivity extends BaseActivity  implements OnClickListener{

	private EditText packData;
	private TextView txtResult;
	
	private ProgressDialog progressDialog;
	private SwiperController swiper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calmac);
		
		packData = (EditText)this.findViewById(R.id.packData);
		txtResult = (TextView)this.findViewById(R.id.txtResult);
		
		this.findViewById(R.id.btnCalMac).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
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
			
		//计算
		case R.id.btnCalMac:
			if (this.packData.getText().length() == 0 || this.packData.getText().length()%2!=0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("输入的报文有误").setPositiveButton("确定", null)
						.show();
				return;
			}
			progressDialog = ProgressDialog.show(this, null, "正在执行...", false);
			progressDialog.show();
			swiper.calculateMac(ByteUtils.hexToByte(packData.getText().toString()));
			break;
		}
	}

	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			new AlertDialog.Builder(CalMacActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		@Override
		public void onCalculateMacSuccess(byte[] mac) {
			progressDialog.dismiss();
			txtResult.setText("mac:"+ByteUtils.byteToHex(mac));
		}
	};
}
