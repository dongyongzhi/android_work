package com.ctbri.ui.consum;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.ui.BaseActivity;
import com.ctbri.utils.Md5Thicken;
import com.yifeng.skzs.data.UserDAL;
import com.yifeng.skzs.entity.DoPay;
import com.yifeng.skzs.entity.SignIn;
import com.yifeng.skzs.entity.User;

/**
 * 消费测试
 * 
 * @author qin
 * 
 * @date 2013-2-25
 */
public class ConsumptionActivity extends BaseActivity implements OnClickListener{
	
	TextView ddNo,ddje,dzQm, gjyXinxi, qm,ddZhuangtai;
	Button top_back;
	
	private DoPay users;
	private UserDAL userDAL;
	private SignIn sin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.consumption);
		ddNo = (TextView) findViewById(R.id.ddNo);
		ddje = (TextView) findViewById(R.id.ddje);
		dzQm = (TextView) findViewById(R.id.dzQm);
		gjyXinxi = (TextView) findViewById(R.id.gjyXinxi);
		qm = (TextView) findViewById(R.id.qm);
		ddZhuangtai = (TextView) findViewById(R.id.ddZhuangtai);
		top_back = (Button) findViewById(R.id.top_back);
		 
		userDAL = new UserDAL(this);
		 
		new Thread(mRunnable).start();
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
			sin = userDAL.SignInInfo(param);
			if(sin.getRespCode().equals("")||sin.getRespDesc().equals("error")){
				dialogUtil.showMsg("平台错误", sin.getRespDesc());
				return;
			}else{
				Map<String, String> ps = new HashMap<String, String>();
				ps.put("CustId", User.getCustId()); 
				ps.put("MtId", User.getMtId());
				ps.put("OrdId", sin.getOrdId());
				ps.put("OrdAmt", "12.00");
				ps.put("InfoField", User.getCustId()+"|"+sin.getOrdId()+"|"+"12.00");
				String md;
				md = sin.getMd5Key()+User.getCustId()+User.getMtId()+sin.getOrdId()+
						"12.00"+(User.getCustId()+"|"+sin.getOrdId()+"12.00");
				ps.put("ChkValue", Md5Thicken.getMD5Res(md));
				
				DoPay dop = userDAL.DoPayInfo(ps);
				if(dop.getTransStat().equals("S")){
					//打印
					System.out.println("sssss"+"---------打印");
				}else if(dop.getTransStat().equals("F")){
					return;
				}
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.top_back:
			ConsumptionActivity.this.finish();
			break;
		 
		}
	}
	
	 
}
