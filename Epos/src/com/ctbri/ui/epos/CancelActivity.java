package com.ctbri.ui.epos;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.net.MessageType;
import com.ctbri.pos.support.POSRevokeRequest;
import com.ctbri.ui.BaseActivity;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.Keyboard;
import com.ctbri.widget.MoneyEditText;
import com.yifeng.skzs.data.UserDAL;
import com.yifeng.skzs.entity.SignIn;
import com.yifeng.skzs.entity.User;

/**
 * 交易取消
 * 
 * @author hjy
 * 
 */
public class CancelActivity extends BaseActivity implements OnClickListener {

	private EditText mEditSerNo;
	private Keyboard mKeyboard;

	private TransService trans;
	private MoneyEditText mEditMoney;

	private UserDAL userDAL;
	private SignIn sin;// 撤销签到
	int count = 0;
	private String s;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cancel_order);

		findViewById(R.id.btnDoTrade).setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);

		mEditSerNo = (EditText) findViewById(R.id.mEditSerNo);
		mEditMoney = (MoneyEditText) this.findViewById(R.id.enter_pay);
		mEditSerNo.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
				20) });

		setTitle(R.string.title_cancel_order);

		userDAL = new UserDAL(this);
		

	}


	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDoTrade:
			s = mEditMoney.getText().toString();
			// 数据验证
			if (mEditSerNo.getText().length() < 20) {
				MessageBox.showWarn(this, R.string.warn_SerNo_required);
				return;
			} else if (mEditMoney.getMoney() == 0) {
				this.showWarn("请输入金额");
				mEditMoney.setFocusable(true);
				return;
			}
			new Thread(mRunnable).start();
			
			break;
		case R.id.top_back:
			onBack();
			break;
		}
	}
	
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(300);
				mHandler.sendMessage(mHandler.obtainMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			Map<String, String> param = new HashMap<String, String>();
			param.put("CustId", User.getCustId());
			param.put("MtId", User.getMtId());
//			param.put("SessionId", User.getSessionId());
			
			if(sin.getRespCode().equals("")||sin.getRespDesc().equals("error")){
				dialogUtil.showMsg("错误", sin.getRespDesc());
				return;
			}else{
				POSRevokeRequest request  = new POSRevokeRequest();
				request.setOriginalSerialNumber(mEditSerNo.getText().toString());//原消费订单号
				 
				//request.setKEYDATA(str);
				TransActionFactory.getInstance().startAction(CancelActivity.this, request);

			}
		}
	};
}
