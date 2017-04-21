package com.yifengcom.yfposdemo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;
import com.yifengcom.yfposdemo.listener.CallBackListener;

/**
 * @Description: 设置数据
 */
public class SetDataActivity extends BaseActivity implements OnClickListener{
	private EditText customerNo,termNo,batchNo,serialNo;
	private TextView tv_info;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setdata);
		setTitleName("设置数据");
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		this.findViewById(R.id.btnSave).setOnClickListener(this);
		this.findViewById(R.id.btnCancel).setOnClickListener(this);
		
		customerNo = (EditText)this.findViewById(R.id.customerNo);
		termNo = (EditText)this.findViewById(R.id.termNo);
		batchNo = (EditText)this.findViewById(R.id.batchNo);
		serialNo = (EditText)this.findViewById(R.id.serialNo);
		 
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
			
			
			
			if (!YFApp.getApp().isBind) {
				showToast("请先绑定");
				return;
			}
			pd = ProgressDialog.show(this, null, "正在执行...", false);
			//写入
			try {
				YFApp.getApp().iService.setDeviceData(customerNo.getText().toString(), termNo.getText().toString(), 
						serialNo.getText().toString(), batchNo.getText().toString(),mCallBack);
			} catch (RemoteException e) {
				e.printStackTrace();
				tv_info.setText("接口访问错误");
				closeDialog();
			}
			
			break;
		}
	}
	
	
	CallBackListener mCallBack = new CallBackListener(){

		@Override
		public void onError(final int errorCode, final String errorMessage)
				throws RemoteException {
			closeDialog();

			runOnUiThread(new Runnable() {
				public void run() {
					tv_info.setText("Error  code:" + errorCode + "  message:"
							+ errorMessage);
				}
			});

		}

		@Override
		public void onTimeout() throws RemoteException {
			
		}

		@Override
		public void onResultSuccess(int ntype) throws RemoteException {
			closeDialog();
			runOnUiThread(new Runnable() {
				public void run() {
					tv_info.setText("写入成功");
				}
			});
		}
		
	};
	
	 
}
