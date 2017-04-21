package com.yfcomm.m18;


import com.yfcomm.R;
import com.yfcomm.mpos.api.SwiperController;
import com.yfcomm.mpos.model.Action;
import com.yfcomm.mpos.utils.ByteUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
 

/**
 * 更新公钥
 * @author  
 *
 */
public class UpdateMainKeyActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener {
	
	private EditText packData;
	private Action upateAct = Action.ADD;
	 
	private SwiperController swiper;
	private ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatemainkey);
		
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
		this.findViewById(R.id.btnExcute).setOnClickListener(this);
		
		packData = (EditText)this.findViewById(R.id.packData);
		((RadioGroup)findViewById(R.id.actionType)).setOnCheckedChangeListener(this);
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
		case R.id.btnExcute:
			 
		    if(this.upateAct == Action.ADD) {
		    	if (this.packData.getText().length() == 0 || this.packData.getText().length()/2!=0) {
					new AlertDialog.Builder(this).setTitle("提示")
							.setMessage("输入的报文有误").setPositiveButton("确定", null)
							.show();
					return;
				}
		    }
		    progressDialog = ProgressDialog.show(this, null, "正在执行...", false);
			progressDialog.show();
			
		    this.swiper.updateAid(upateAct, ByteUtils.hexToByte(packData.getText().toString()));
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.actionAdd) {
			this.upateAct = Action.ADD;
		} else if(checkedId == R.id.actionDel) {
			this.upateAct = Action.DELETE;
		} else {
			this.upateAct = Action.CLEAR;
		}
	}
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			new AlertDialog.Builder(UpdateMainKeyActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		@Override
		public void onResultSuccess(int nType) {
			progressDialog.dismiss();
			Toast.makeText(UpdateMainKeyActivity.this, "执行成功", Toast.LENGTH_SHORT).show();
		}
	};
}
