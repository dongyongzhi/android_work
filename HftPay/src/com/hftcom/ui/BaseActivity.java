package com.hftcom.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import com.hftcom.R;
import com.hftcom.db.DBUtil;
import com.hftcom.domain.Transaction;
import com.hftcom.domain.VersionInfo;
import com.hftcom.utils.ActivityStackControlUtil;
import com.hftcom.utils.CommonUtil;
import com.hftcom.utils.Config;
import com.hftcom.utils.DialogUtil;
import com.yifengcom.yfpos.print.Print;
import com.yifengcom.yfpos.print.PrintPackage;

public class BaseActivity extends Activity {
	protected ProgressDialog progressDialog;
	public CommonUtil commonUtil;
	public DialogUtil dialogUtil;
	private TelephonyManager telMgr;
	private String imsi = ""; 
	PowerManager manager;
	WakeLock lock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.manager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.lock = this.manager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
		commonUtil = new CommonUtil(this);
		dialogUtil = new DialogUtil(this);
		
		telMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (telMgr.getSimState() == TelephonyManager.SIM_STATE_READY) {
			if (telMgr.getSubscriberId() != null) {
				imsi = telMgr.getSubscriberId();
			}
		}
		ActivityStackControlUtil.add(this);
		if(!commonUtil.checkNetWork()){
			dialogUtil.alertNetError();
			return;
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.lock.acquire();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.lock.release();
	}


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
   

	public VersionInfo getVersion(){
		VersionInfo info = new VersionInfo();
		PackageManager manager = this.getPackageManager();
		try {
			PackageInfo pi = manager.getPackageInfo(this.getPackageName(), 0);
			info.setVersionName(pi.versionName);
			info.setVersionCode(pi.versionCode);
		} catch (NameNotFoundException e) {
			info.setVersionName("");
			info.setVersionCode(1);
		}
		return info;
	}
   
   /**
    * 返回
    */
   protected void  onBack(){
	   if(this instanceof MainMenuActivity){
		   //退出？
	   }else {
	   	   this.finish();
	   }
   }

	

	
	public void showToast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public String getCurrentTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		return df.format(new Date());
	}	

	public String getImsi() {
		return imsi;
	}
	
	public String convertICData(String icData){
		String newIcData = "";
		if(icData == null || icData.equals("")){
			return newIcData;
		}
		String[] strs = Config.icReverseData.split(",");
		for (int i = 0; i < strs.length; i++) {
			int position = icData.indexOf(strs[i]);
			if(position == -1){
				continue;
			}
			try{
				int len = position + strs[i].length();
				String str = icData.substring(len, len+2);
				int dataLen = Integer.parseInt(str,16);
				String newStr = icData.substring(position, (len+2+ dataLen*2));
				newIcData += newStr;
			}catch (Exception e) {
				continue;
			}
		}
		return newIcData;
	}
	
	public Transaction checkLastOrder() {
		Transaction transaction = null;
		Map<String, String> map = DBUtil.getInstance(this).getLastOrder(null,
				null, null, null, "[_id] asc", null);
		if (map.size() != 0) {
			if(map.get("result_code") != null && map.get("result_msg") != null){
				if(!map.get("result_code").trim().equals("") && !map.get("result_msg").trim().equals("")){
					return null;
				}
			}
			
			transaction = new Transaction();
			transaction.set_id(map.get("_id") != null ? map.get("_id") : "");
			transaction.setSerial_no(map.get("serial_no") != null ? map.get("serial_no") : "");
			transaction.setSend_time(map.get("send_time") != null ? map.get("send_time") : "");
			transaction.setSend_result(map.get("send_result") != null ? map.get("send_result") : "");
			transaction.setCard_type(map.get("card_type") != null ? map.get("card_type") : "");
			transaction.setCorrect_num(map.get("correct_num") != null ? map.get("correct_num") : "");
			transaction.setTrading_time(map.get("trading_time") != null ? map.get("trading_time") : "");
			transaction.setResult_code(map.get("result_code") != null ? map.get("result_code") : "");
			transaction.setResult_msg(map.get("result_msg") != null ? map.get("result_msg") : "");
			transaction.setMac(map.get("mac") != null ? map.get("mac") : "");
			transaction.setPrint_data(map.get("print_data") != null ? map.get("print_data") : "");
			transaction.setIc_data(map.get("ic_data") != null ? map.get("ic_data") : "");
			transaction.setMoney(map.get("money") != null ? map.get("money") : "");
			return transaction;
		}
		return transaction;
	}
	
	
	public String hexStr2Str(String hexStr)    
	{      
	    String str = "0123456789ABCDEF";      
	    char[] hexs = hexStr.toCharArray();      
	    byte[] bytes = new byte[hexStr.length() / 2];      
	    int n;      
	  
	    for (int i = 0; i < bytes.length; i++)    
	    {      
	        n = str.indexOf(hexs[2 * i]) * 16;      
	        n += str.indexOf(hexs[2 * i + 1]);      
	        bytes[i] = (byte) (n & 0xff);      
	    }      
	    return new String(bytes);      
	}
	
	public byte[] getPrintBody(ArrayList<String> strs,boolean isSign) {
		byte[] sendbuf = new byte[1024];
		try {
			Print printinfor = new Print();
			printinfor.PRINT_clear();
			// ---------增加走纸步进--------
			printinfor.PRINT_Add_setp((short) (100));
			// ----------增加字符信息-------
			for (int j = 0; j < strs.size(); j++) {
				String print = strs.get(j);
				printinfor.PRINT_Add_character((byte) 0, Print.PNT_24X24,
						print.getBytes("gb2312"),
						(short) (print.getBytes("gb2312").length));
			}
			
			if(isSign){
				printinfor.PRINT_Add_picture((byte)0, (byte)0);
			}
			
			// ---------增加走纸步进--------
//			printinfor.PRINT_Add_setp((short) (100));

			short sendLen = printinfor.PRINT_packages(sendbuf);
			byte[] sendbuf1 = new byte[sendLen];

			System.arraycopy(sendbuf, 0, sendbuf1, 0, sendLen);

			PrintPackage package1 = new PrintPackage(sendbuf1);
			if (package1 != null) {
				return package1.getPackData();
			}
		} catch (Exception e) {

		}
		return null;
	}
	
	public void closeDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
//			progressDialog = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityStackControlUtil.remove(this);
	}
}
