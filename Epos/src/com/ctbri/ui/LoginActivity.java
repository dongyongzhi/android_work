package com.ctbri.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.domain.CheckDownloadResponse;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.QueryNoticeResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.POSException;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.Md5Thicken;
import com.yifeng.skzs.data.UserDAL;
import com.yifeng.skzs.entity.User;
import com.yifeng.skzs.util.AppContext;
import com.yifeng.skzs.util.AutoUpdate;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.HttpPostGetUtil;

/**
 * Class Name：LoginActivity Explain：登录页面
 * 
 * @author Administrator Date：2013-2-25
 */
public class LoginActivity extends BaseActivity {
	
	public static final String PWD = "PWD";
	/** 设备初始化*/
	private final static int INIT_POS = 1;
	/**初始化pos成功*/
	private final static int INIT_POS_SUCCESS = 4;
	private AutoUpdate autoupdate;
	
	private QueryNoticeResponse qnResponse;//公告信息
	
	private String connAddress,connName;
	private LoadingTask task;  //下载信息
	
	
	private TextView loadTxt;
	private EditText usernameEdt, passwordEdt;
	private Button loginBtn, cancleBtn,exitBtn;
	private CheckBox reg_check;// 记住密码
	private String name, pwd;
	private UserDAL userDAL;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		userDAL = new UserDAL(this);

		initPage();
		
	}

	private void initPage() {
		usernameEdt = (EditText) findViewById(R.id.usernameEdt);
		passwordEdt = (EditText) findViewById(R.id.passwordEdt);

		loadTxt = (TextView) findViewById(R.id.loadTxt);
		MyClick myclick = new MyClick();
		loginBtn = (Button) findViewById(R.id.loginBtn);
		loginBtn.setOnClickListener(myclick);
		exitBtn = (Button) findViewById(R.id.exitBtn);
		exitBtn.setOnClickListener(myclick);

		reg_check = (CheckBox) findViewById(R.id.reg_check);

		SharedPreferences user_n_p = getSharedPreferences(PWD, 0);
		if (user_n_p.getString("loginName", "").equals("")){
			reg_check.setChecked(false);
		}else{
			reg_check.setChecked(true);
		}
		usernameEdt.setText(user_n_p.getString("loginName", ""));
		passwordEdt.setText(user_n_p.getString("loginPwd", ""));
	}

	private class MyClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginBtn:
//				Intent main = new Intent(LoginActivity.this, MainMenuActivity.class);
				doCheckLogin();
				break;
			case R.id.exitBtn:
				dialogUtil.doAdvanceExit();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 登陆检测
	 * 
	 * @return
	 */
	private void doCheckLogin() {
		User us = new User();
		name = commonUtil.doConvertEmpty(usernameEdt.getText().toString());
		us.setName(name);
		pwd = commonUtil.doConvertEmpty(passwordEdt.getText().toString());
		us.setPwd(pwd);
		if (name.equals("") || pwd.equals("")) {
			dialogUtil.showMsg("错误", "账号及密码不能为空请重新输入!");
		} else if (name.length() > 20) {
			dialogUtil.showMsg("错误", "帐号不能大于20位!");
		} else if (pwd.length() > 15) {
			dialogUtil.showMsg("错误", "密码不能大于15位!");
		} else {
			loadTxt.setText("正在验证...");
			new Thread(mRunnable).start();
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
			doLogin();
		}
	};

	private void doLogin() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("LoginPwd", Md5Thicken.getMD5Res(ConstantUtil.MD5K+pwd));
		param.put("LoginId", name);

		//清除会话cookie
		HttpPostGetUtil.cookie.clear();
		SharedPreferences cookies = AppContext.get().getSharedPreferences("COOKIES", 0);
	     cookies.edit() 
			.putString("COOKIES", "")
			.commit();
	    

		user = userDAL.loadUser(param);
		if((!user.getRespCode().equals("000"))
				||user.getRespCode().equals("")
				||user.getRespDesc().equals("error")){
			
			dialogUtil.showToast(user.getRespDesc());
		}
		if (user.getState() == ConstantUtil.SERVER_ERROR) {
			dialogUtil.showToast("服务器连接超时请重试!或服务器己关闭!");
			loadTxt.setText("");
		} else if (user.getState() == ConstantUtil.INNER_ERROR) {
			dialogUtil.showToast("对不起，系统数据解析异常，请重试!");
			loadTxt.setText("");
		} else if (user.getState() == ConstantUtil.LOGIN_FAIL) {
			dialogUtil.showToast("登录失败，用户名或密码有误请重新输入!");
			loadTxt.setText("");
		} else
		{
			loadTxt.setText("");
			
			if(reg_check.isChecked()){
    	        doRememberPwd();//记住密码
    	      }else {
    	    	  doRemovePwd();//不记住密码
    	      }
			
			// 保存到全局Session
			UserSession session = new UserSession(this);
			session.setUser(user);

//			loadTxt.setText("正在获取终端PSAM卡号...");
			
//			
			//获取终端PSAM卡号
			//POSTransRequest req = new  POSTransRequest();
			//req.setMessageType(00);
			ElecPosService pos = this.getPOS();//pos机
			//POSTransResponse responsePSAM = pos.transRequestPSAM(req);
			//String mtID = new String(responsePSAM.getData(),1 ,12);
			//User.setMtId(mtID);
			//Log.i("login", "PSAM卡号:"+mtID);

			
			loadTxt.setText("正在查询终端绑定关系...");
			BindQuery();
			Intent main = new Intent(LoginActivity.this, MainMenuActivitys.class);
			main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(main);
		}
	} 
	
	
	/**
	 * 记住密码
	 * 
	 */
	private void doRememberPwd(){
		
		SharedPreferences rmd=getSharedPreferences(PWD, 0);
		 rmd.edit()
		.putString("loginName", usernameEdt.getText().toString())
		.putString("loginPwd", passwordEdt.getText().toString())
		.commit();
	}
	
	/**
	 *  取消记住密码
	 */
	private void doRemovePwd(){
		SharedPreferences rmd=getSharedPreferences(PWD, 0);
		 rmd.edit()
		.putString("loginName", "")
		.putString("loginPwd", "")
		.commit();
	}
	
	/**
	 * 查询绑定，绑定，解绑
	 */
	 private void BindQuery(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("MtId", User.getMtId());
		params.put("CustId", User.getCustId());
		params.put("SessionId", User.getSessionId());
		User QueryBind = userDAL.QueryBindStat(params);
		if((!QueryBind.getRespCode().equals("000"))||QueryBind.getRespCode().equals("")||QueryBind.getRespDesc().equals("error")){
			dialogUtil.showMsg("错误", QueryBind.getRespDesc());
			return;
		}else{
           if(QueryBind.getBindStat().equals("I")){
				dialogUtil.showToast("终端未初始化密钥"); 
				return;
			}else if(QueryBind.getBindStat().equals("N")){
				dialogUtil.shortToast("终端与用户已绑定");
				Intent main = new Intent(LoginActivity.this, MainMenuActivitys.class);
				main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(main);
				LoginActivity.this.finish();
			}else if(QueryBind.getBindStat().equals("K")){
				dialogUtil.showToast("终端未绑定"); 
				return;
			}else if(QueryBind.getBindStat().equals("O")){
				dialogUtil.showToast("终端已被他人绑定"); 
				return;
			}else if(QueryBind.getBindStat().equals("C")){
				dialogUtil.showToast("终端已解绑"); 
				return;
			}
		}
	 }
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (ConstantUtil.ISEJOB) {
				// 如果是管理版进来，直接返回;
				this.finish();
			} else {
				// 直接退出
				dialogUtil.doAdvanceExit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	int initForName(String name) {
		return 0;
	}
	/**
	 * 设备绑定成功
	 * 参数（绑定蓝牙连接参数）
	 */
	protected void serviceBindComplete(){
		SharedPreferences settings  = this.getSharedPreferences(STORE_POS_KEY,Context.MODE_PRIVATE);
		//获取上次连接的名称
		connName = settings.getString(STORE_POS_NAME_KEY, "");
		task = new LoadingTask();
		task.execute(LoginActivity.this,autoupdate,null,connName);
	}
	
	/**
	 * 启动任务   
	 * @author qin
	 * 2012-12-25
	 */
	class LoadingTask extends AsyncTask<Object,Object,Object>{
		
		protected void onPreExecute(){
		}
		
		protected Object doInBackground(Object... params) {
			//params•	调用对象的excute()时，将启动后台进程，执行doInBackground()的代码。
			//excute()中所传递的参数类型在参数1中描述，属于范式定义
			//params[] = {LoadingActivity.this,autoupdate,posType,null,connName};
			ElecActivity elec = (ElecActivity)params[0];
			CheckDownloadResponse response =null;
			try {
				ElecPosService pos = null;   
				
				//获取以前绑定成功的设备类型  (系统默认 pos 设备类型)
				//POSType posType = (POSType)params[2];
				String mac = (String)params[3];
				String posName = (String)params[4];  //获取选择的pos 机名称(蓝牙名称)
				
				//==========step 1   初始连接pos 设备  =============//
				publishProgress(INIT_POS,posName);
				//ResponseCode result =  ResponseCode.convert(elec.initDevice(posType,mac, posName));
				
				
				
				//己退出
				if(this.isCancelled()){
					ElecLog.d(getClass(), "====>pos connection  cancelled");
					if(elec.getPOS()!=null)
						elec.getPOS().release();
					return null;
				}
				
				//if(result != ResponseCode.INIT_SUCCESS) //未找到pos直接退出
				//	throw new POSException(result,LoginActivity.this);
				//else{
				//	publishProgress(INIT_POS_SUCCESS);
				//}
				
			}catch(Exception e){
				return e;
			}
			return response; //返回版本信息
		}
		
		/**成功返回*/
		protected void onPostExecute(Object result){
			
			}
			
	};
	
}
