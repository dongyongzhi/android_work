package com.ctbri.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.ui.manage.ManageActivity;
import com.ctbri.ui.more.MoreActivity;
import com.ctbri.ui.query.QueryDayActivitys;
import com.ctbri.ui.query.QueryHistoryActivitys;
import com.ctbri.ui.query.QueryMonthActivitys;
import com.yifeng.skzs.util.ActivityStackControlUtil;
import com.yifeng.skzs.util.CommonUtil;
import com.yifeng.skzs.util.DialogUtil;

/**
 * comment:Actvity父类
 * @author:ZhangYan
 * Date:2012-8-17
 */
public class BaseActivity extends ElecActivity {
	public Button bt_bottom_menu1, bt_bottom_menu2, bt_bottom_menu3,bt_bottom_menu4,bt_day,bt_month,bt_history;
	public CommonUtil commonUtil;
	public DialogUtil dialogUtil;
	private TelephonyManager telMgr;
	public Handler BASEHANDLER;
	private String imsi = ""; 
	//public User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/** 全屏显示 使应用程序全屏运行，不使用title bar **/
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);//优化内存管理
		commonUtil = new CommonUtil(this);
		dialogUtil = new DialogUtil(this);
		
		//UserSession session = new UserSession(this);
		//user = session.getUser();
		
		telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (telMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
			if (telMgr.getSubscriberId() != null) {
				imsi = telMgr.getSubscriberId();
				//user.setImsi(imsi);
			}
		}
		ActivityStackControlUtil.add(this);
		if(!commonUtil.checkNetWork()){
			/*dialogUtil.shortToast("请设置网络连接!");
			Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
			startActivity(intent);*/
			dialogUtil.alertNetError();
			return;
		}
	}
	
	public void initBottom() {
		bt_bottom_menu1 = (Button) findViewById(R.id.bt_bottom_menu1);
		bt_bottom_menu2 = (Button) findViewById(R.id.bt_bottom_menu2);
		bt_bottom_menu4 = (Button) findViewById(R.id.bt_bottom_menu4);
	}
	
	public void initTopBtn() {
		bt_day = (Button) findViewById(R.id.bt_day);
		bt_month = (Button) findViewById(R.id.bt_month);
		bt_history = (Button) findViewById(R.id.bt_history);
	}
	
	/**
	 * 设置标题 
	 */
   public void setTitle(int resid){
	   TextView title = (TextView)this.findViewById(R.id.activityTitle);
	   if(title!=null){
		   title.setText(resid);
	   } 
   }
   
   public void setTitle(CharSequence title){
	   TextView mTextView = (TextView)this.findViewById(R.id.activityTitle);
	   if(mTextView!=null){
		   mTextView.setText(title);
	   }
   }
   
   /**
    * 返回
    */
   protected void  onBack(){
	   if(this instanceof MainMenuActivitys){
		   //退出？
	   }else {
	   	   this.finish();
	   }
   }
	public void setFocus(Button menu, int drawable) {
		menu.setEnabled(false);
		menu.setBackgroundResource(drawable);
		// 主界面处理
		bt_bottom_menu1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 交易管理
		bt_bottom_menu2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this,ManageActivity.class);
				startActivity(intent);
				if(!(BaseActivity.this instanceof MainMenuActivitys)){
					finish();
				}
			}
		});
		// 更多
		bt_bottom_menu4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this, MoreActivity.class);
				startActivity(intent);
				if(!(BaseActivity.this instanceof MainMenuActivitys)){
					finish();
				}
			}
		});
	}
	
	public void setTopFocus(Button menu, int drawable) {
		menu.setEnabled(false);
		menu.setBackgroundResource(drawable);
		//当日
		bt_day.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this,
						QueryDayActivitys.class);
				startActivity(intent);
				finish();
			}
		});
		//当月
		bt_month.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this,
						QueryMonthActivitys.class);
				startActivity(intent);
				finish();
			}
		});
		//历史
		bt_history.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(BaseActivity.this,
						QueryHistoryActivitys.class);
				startActivity(intent);
				finish();
			}
		});
	}
	 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(BASEHANDLER!=null){
				BASEHANDLER.sendMessage(BASEHANDLER.obtainMessage());
			}else {
				System.gc();
				this.finish();
//				ActivityStackControlUtil.remove(this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public String getImsi() {
		return imsi;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityStackControlUtil.remove(this);
	}
}
