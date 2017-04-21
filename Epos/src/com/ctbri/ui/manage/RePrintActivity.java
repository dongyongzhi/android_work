package com.ctbri.ui.manage;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ctbri.R;
import com.ctbri.ui.BaseActivity;
import com.yifeng.skzs.data.UserDAL;
import com.yifeng.skzs.entity.User;
import com.yifeng.skzs.entity.limitAmt;

/**
 * @comment:交易限额
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class RePrintActivity extends BaseActivity implements OnClickListener {
	TextView onelimitAm;
	TextView sumlimitAm;
	UserDAL userDAL;
	limitAmt limiotAmt;
	Button backBtn;
	private LinearLayout jiaoyi_xiane, publicloading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reprint_view);
		jiaoyi_xiane = (LinearLayout) findViewById(R.id.jiaoyi_xiane);
		publicloading = (LinearLayout) findViewById(R.id.publicloading);
		userDAL = new UserDAL(this);
		this.setTitle(R.string.title_query_limit);
		
		
		initView();
		loadData();
	}
	
	private void loadData(){
		new Thread(runnable).start();
	}
	
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				Message msg = handler.obtainMessage();
				Map<String, String> param = new HashMap<String, String>();
				param.put("CustId", User.getCustId()); 
				param.put("MtId", User.getMtId());
				limiotAmt = userDAL.limitAmtInfo(param);
				if((!limiotAmt.getRespCode().equals("000"))||limiotAmt.getRespCode().equals("")||limiotAmt.getRespDesc().equals("error")){
					dialogUtil.showMsg("错误", limiotAmt.getRespDesc());
					msg.what = -1;
					handler.sendMessage(msg);
					return;
				}else{
					if(limiotAmt.getOneLimitAmt().equals("0.00")){
						onelimitAm.setText("无限额");
					}
					if(limiotAmt.getSumLimitAmt().equals("0.00")){
						sumlimitAm.setText("无限额");
					}else{
						onelimitAm.setText(limiotAmt.getOneLimitAmt());
						sumlimitAm.setText(limiotAmt.getSumLimitAmt());
					}
					msg.what = 1;
					Thread.sleep(300);
					handler.sendMessage(msg);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case -1:
				jiaoyi_xiane.setVisibility(View.GONE);
				publicloading.setVisibility(View.VISIBLE);
				break;
			case 1:
				jiaoyi_xiane.setVisibility(View.VISIBLE);
				publicloading.setVisibility(View.GONE);
				break;

			}
		};
	};
			
	
	private void initView(){
		onelimitAm = (TextView)findViewById(R.id.onelimitAm);
		sumlimitAm = (TextView)findViewById(R.id.sumlimitAm);
		backBtn = (Button)findViewById(R.id.top_back);
		backBtn.setOnClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.top_back:
			back();
			break;
		}
	}
	/**
	 * 返回
	 */
	private void back() {
		// 取消终端命令
//		new AsyncTask<ElecPosService, Void, Void>() {
//			@Override
//			protected Void doInBackground(ElecPosService... params) {
//				ElecPosService pos = params[0];
//				if (pos == null)
//					return null;
//				pos.endPosCmd();
//				return null;
//			}
//		}.execute(getPOS());

		finish();
	}
	 
		
}
