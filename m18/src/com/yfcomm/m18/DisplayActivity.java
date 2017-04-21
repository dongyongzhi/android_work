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

public class DisplayActivity extends BaseActivity implements OnClickListener{

	private EditText txtRow,txtCol,txtShowTime,displayText;
	
	private SwiperController swiper;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display);
		txtRow = (EditText)this.findViewById(R.id.row);
		txtCol = (EditText)this.findViewById(R.id.col);
		txtShowTime = (EditText)this.findViewById(R.id.showTime);
		displayText = (EditText)this.findViewById(R.id.displayText);

		this.findViewById(R.id.btnCancel).setOnClickListener(this);
		this.findViewById(R.id.btnDisplay).setOnClickListener(this);
		 
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

		// 执行
		case R.id.btnDisplay:
			
			int row,col,showTime;
			row = txtRow.getText().length()!=0 ? Integer.parseInt(txtRow.getText().toString()) : 1;
			col = txtCol.getText().length()!=0 ? Integer.parseInt(txtCol.getText().toString()) : 1;
			showTime = txtShowTime.getText().length()!=0 ? Integer.parseInt(txtShowTime.getText().toString()) : 1;
		     
		    progressDialog = ProgressDialog.show(this, null, "正在执行...", false);
			progressDialog.show();
			
			//显示
			this.swiper.displayText(row, col, displayText.getText().toString(), showTime);
			break;
		}			
	}
	
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			new AlertDialog.Builder(DisplayActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		@Override
		public void onResultSuccess(int nType) {
			progressDialog.dismiss();
			Toast.makeText(DisplayActivity.this, "显示成功", Toast.LENGTH_SHORT).show();
		}
	};

}
