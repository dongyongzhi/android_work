package com.ctbri;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;

import com.ctbri.domain.POSInfo;
import com.ctbri.domain.VersionInfo;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.YFPOSService;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecProgressDialog;
import com.yfcomm.pos.bt.device.DeviceComm;
import com.yfcomm.pos.ui.DeviceServiceActivity;

/**
 * 服务绑定
 * 
 * @author qin
 * 
 *         2012-11-20
 */
public class ElecActivity extends DeviceServiceActivity {

	private boolean isExists = false; //是否退出
	
	public final static String STORE_POS_KEY = "store_pos_key";
	public final static String STORE_POSTYPE_KEY = "key";
 	public final static String STORE_POS_ADDRESS_KEY = "store_pos_address_key";
	public final static String STORE_POS_NAME_KEY ="store_pos_name_key";
	
	public final static String EXTRA_ORDER_DETAIL = "extra_order_detail"; //交易详细信息.
	public final static String EXTRA_TRANS_RESULT = "extra_trans_result";//交易结果
    public final static String EXTRA_TRANS_REQUEST = "extra_trans_request"; //交易请求数据
    public final static String EXTRA_TRANS_RESPONSE = "extra_trans_response";//交易返回数据
    public final static String EXTRA_DETAIL_PAGEURL = "extra_detail_pageurl";//显示订单详细信息url
    public final static String EXTRA_APK_PAY_REQUEST = "extra_apk_pay_request"; //外部apk交易请求数据
	public final static String EXTRA_POSINFO = "extra_posinfo";   //pos参数信息
    public final static String EXTRA_SIGN_DATA = "extra_sign_data"; //电子签名数据
    public final static String EXTRA_NOTICE    = "extra_notice";//公告信息
    
	private ElecPosService  pos;   //pos接口
	private POSInfo posInfo;		//pos终端信息
	
	private  ElecProgressDialog mProgressDialog; //处理状态显示
	
	private AsyncTask<Activity,Object,Void> task = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
	}
	
 
	protected void onDeviceReady(DeviceComm device) {
		super.onDeviceReady(device);
		this.pos = new YFPOSService(device);
		onDeviceReady();
	}
    /**
     * pos设备绑定完成
     */
	protected void onDeviceReady(){
		
	}
 
	
	/**
	 * 执行业务
	 * @param <T>  
	 * @return
	 * @throws ElecException   
	 */
	public Object onExecAsynService(){
		return null;
	}
	/**
	 * 执行成功返回
	 * @param obj
	 */
	public void onServiceSuccess(Object obj){
		if(mProgressDialog!=null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}
	
	/**
	 * 执行失败返回
	 * @param e
	 */
	public void onServiceError(Exception e){
		if(mProgressDialog!=null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		
		if(e!=null && e.getMessage()!=null)
			showError(e.getMessage());
	}
	
	/**
	 * 执行异步业务
	 */
	public final void startAsynService(){
		task = new AsyncTask<Activity,Object,Void>(){
			@Override
			protected Void doInBackground(Activity... arg0) {
				ElecActivity elec = (ElecActivity)arg0[0];
				
				try{
					if(elec.getPOS()==null)
						throw new ElecException("POS服务未找到！");
					if(elec.isExists)
						return null;
					Object result =  elec.onExecAsynService();
					publishProgress(elec, result); //返回成功
					
				}catch(Exception e){
					ElecLog.e(getClass(),String.format(" do aysn serivce:%s", e.getMessage()) );
					if(elec.isExists)
						return null;

					publishProgress(elec,e);
					e.printStackTrace();
				}
				return null;
			}
			
			protected void onProgressUpdate(Object... objs){
				ElecActivity elec = (ElecActivity)objs[0];
				Object result = objs[1];
				//成功返回
				if(elec.isExists)
					return;
				try{
					if(result instanceof Exception){
						elec.onServiceError((Exception)result);
					}else
						elec.onServiceSuccess(result);  //执行返回
					
				}catch(Exception e){
					if(e!=null && e.getMessage()!=null)
						ElecLog.e(getClass(), e.getMessage(),e);
					
					if(mProgressDialog!=null && mProgressDialog.isShowing())
						mProgressDialog.dismiss();
				}
			}
		}.execute(this);
	}
	
	/**
	 * 执行异步业务
	 * @param mProgressDialog   进度条
	 */
	public final void startAsynService(ElecProgressDialog mProgressDialog){
		this.mProgressDialog = mProgressDialog;
		this.mProgressDialog.setPOS(getPOS());
		startAsynService();
	}
	
	/**
	 * 执行异步业务
	 * @param showProgressDialog  是否显示进度对话框
	 */
	public final void startAsynService(boolean showProgressDialog){
		if(showProgressDialog){
			mProgressDialog = new ElecProgressDialog(this);
			mProgressDialog.setPOS(getPOS());
			mProgressDialog.show();
		}
		startAsynService();
	}
	
	public POSInfo getPOSInfo(){
		return this.posInfo;
	}
	
	public void setPOSInfo(POSInfo posInfo){
		this.posInfo = posInfo;
	}

	/**
	 * 跳转带返回
	 * @param intent
	 * @param requestCode
	 */
	public void ctbriStartActivityForResult(Intent intent,int requestCode){
		//pos参数
		if(intent!=null){
			if(posInfo!=null)
				intent.putExtra(EXTRA_POSINFO, (Parcelable)posInfo);//传递 pos 设备参数
		}
		startActivityForResult(intent, requestCode);
	}
	
	
	public void baseStartActivity(Intent intent){
		super.startActivity(intent);
	}
	
	public ElecProgressDialog  getProgressDialog(){
		return this.mProgressDialog;
	}
	
	public ElecPosService getPOS(){
		return this.pos;
	}
	
	/**
	 * 获取版本信息
	 * @return
	 */
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
	 * 警告信息
	 * @param message  信息内容
	 */
	public void showWarn(String message){
		MessageBox.showWarn(this, message);
	}
	/**
	 * 显示错误信息
	 * @param message  错误信息内容
	 */
	public void showError(String message){
		MessageBox.showError(this, message);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//结束线程
		if(task!=null && !task.isCancelled()){
			task.cancel(true);
		}
		isExists = true;
	}
 	
	public boolean isExists(){
		return this.isExists;
	}

}
