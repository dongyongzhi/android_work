package com.ctbri.ui.query;

import java.util.HashMap;
import java.util.Map;

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
import com.yifeng.skzs.entity.SignIn;
import com.yifeng.skzs.entity.User;

/**
 * 账户查询
 * 
 * @author qin
 * 
 * @date 2013-2-25
 */
public class QueryUserActivity extends BaseActivity implements OnClickListener{
	
	TextView drljjybs,drljjyje,bankId, bankAddress, shebNo,takeMonery,yuE;
	Button backBtn;
	private LinearLayout tip_info, publicloading;
	private User user, users;
	private UserDAL userDAL;
	private SignIn sin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_userl);
		tip_info = (LinearLayout) findViewById(R.id.tip_info);
		publicloading = (LinearLayout) findViewById(R.id.publicloading);
		drljjybs = (TextView) findViewById(R.id.drljjybs);
		drljjyje = (TextView) findViewById(R.id.drljjyje);
		bankId = (TextView) findViewById(R.id.bankId);
		bankAddress = (TextView) findViewById(R.id.bankAddress);
		shebNo = (TextView) findViewById(R.id.shebNo);
		takeMonery = (TextView) findViewById(R.id.takeMonery);
		yuE = (TextView) findViewById(R.id.yuE);
		backBtn= (Button) findViewById(R.id.top_back);
		backBtn.setOnClickListener(this);
		userDAL = new UserDAL(this);
		 this.setTitle(R.string.title_query_acount);
		new Thread(mRunnable).start();
	}
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Message msg = mHandler.obtainMessage();
			    Map<String, String> param = new HashMap<String, String>();
				param.put("CustId", User.getCustId()); 
				param.put("MtId", User.getMtId());
				param.put("SessionId", User.getSessionId());
//				sin = userDAL.SignInInfo(param);
				doQuery();
				if((!users.getRespCode().equals("000"))||users.getRespCode().equals("")||users.getRespDesc().equals("error")){
					dialogUtil.showMsg("错误", users.getRespDesc());
					msg.what = -1;
					mHandler.sendMessage(msg);
					return;
				}else{
					drljjybs.setText(users.getTotalOrdCnt());
					drljjyje.setText(users.getTotalOrdAmt());
					bankId.setText(users.getCashCardNo());
					bankAddress.setText(users.getBankId());
					shebNo.setText(users.getBindedMtId());
					takeMonery.setText(users.getAvailCashAmt());
					yuE.setText(users.getNeedLiqAmt());
					msg.what = 1;
					Thread.sleep(300);
					mHandler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				tip_info.setVisibility(View.GONE);
				publicloading.setVisibility(View.VISIBLE);
				break;
			case 1:
				tip_info.setVisibility(View.VISIBLE);
				publicloading.setVisibility(View.GONE);
				break;

			}
			
		}
	};
	
	private void doQuery(){
		Map<String, String> param = new HashMap<String, String>();
		param.put("CustId", User.getCustId());
		param.put("SessionId", User.getSessionId());

		users = userDAL.QueryUserInfo(param);
		
		
	}
		
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.top_back:
			back();
			break;
		 
		}
	}
	/**
	 * 返回
	 */
	private void back() {
//		// 取消终端命令
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
