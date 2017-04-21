package com.ctbri;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;

import com.ctbri.domain.POSInfo;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.VersionInfo;
import com.ctbri.elecpayment.Pay;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.ElecPosService.POSType;
import com.ctbri.pos.StartDevService;
import com.ctbri.pos.YFPOSService;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecProgressDialog;
import com.yifeng.hd.IDeviceService;
import com.yifeng.hd.YFDeviceService;
import com.yifeng.start.IStartDevService;
import com.yifeng.start.StartDeviceService;

/**
 * 服务绑定
 * 
 * @author qin
 * 
 *         2012-11-20
 */
public class ElecActivity extends Activity {

	private boolean isExists = false; //是否退出
	private final static String EXTRE_EXTERNAL = "extre_external";
	
	public final static String STORE_POS_KEY = "store_pos_key";
 	public final static String STORE_POSTYPE_KEY = "POSTYPE";
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
    
	private IDeviceService yfpos;        //ys pos 操作接口
	private IStartDevService startpos;   //实达 pos 操作接口
	
	private ElecPosService  pos;   //pos接口
	private POSInfo  posInfo;
	private POSType posType;
	
	private  ElecProgressDialog mProgressDialog; //处理状态显示
	
	private AsyncTask<Activity,Object,Void> task = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制竖屏
		
		//获取pos设备类型
		String type = this.getIntent().getStringExtra(STORE_POSTYPE_KEY);
		posType = type == null || "".equals(type) ? POSType.YF :  POSType.valueOf(type); 
		
		//服务绑定YF POS 设备
		Intent yfService = new Intent(this,YFDeviceService.class);
		if(this instanceof Pay){//外部apk调用
			yfService.putExtra(EXTRE_EXTERNAL, "1");
		}
		ElecLog.d(getClass(), "服务绑定YF POS 设备...");
		this.bindService(yfService, yfConn, Context.BIND_AUTO_CREATE);
		
		//绑定 实达机具
		Intent startService = new Intent(this, StartDeviceService.class);
		if(this instanceof Pay){//外部apk调用
			startService.putExtra(EXTRE_EXTERNAL, "1");
		}
		this.bindService(startService, startConn, Context.BIND_AUTO_CREATE);
		
		//获取 pos信息
		posInfo =	this.getIntent().getParcelableExtra(EXTRA_POSINFO);
	}
	
	/**
	 * 初始化设备
	 * @return
	 */
	public final int initDevice(POSType type,String address,String deviceName){
		int result;
		try {
			switch(type){
			case YF:
				
				if(address!=null && !"".equals(address))
					result = yfpos.initForMAC(address);
				else
					result = yfpos.initForName(deviceName);
				
				if(result == ResponseCode.INIT_SUCCESS.getValue()){
					pos = new YFPOSService(yfpos); 
					setPOSType(POSType.YF,deviceName);
				}
				return result;
				
			default:
				
				if(address!=null && !"".equals(address))
					result = startpos.initForMAC(address);
				else
					result =  startpos.initForName(deviceName);
				
				if(result == ResponseCode.INIT_SUCCESS.getValue()){
					pos = new StartDevService(startpos); 
					setPOSType(POSType.START,deviceName);
				}
				return result;
			}
		} catch (RemoteException e) {
			return ResponseCode.TERM_NOT_FOUND.getValue();
		}
	}
	
    /**
     * pos设备绑定完成
     */
	protected void serviceBindComplete(){}
 
	
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
					publishProgress(elec,result); //返回成功
					
				}catch(Exception e){
					ElecLog.e(getClass(),String.format(" do aysn serivce:%s", e.getMessage()) );
					if(elec.isExists)
						return null;
					//elec.onServiceError(e);
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
	
	public void startActivity(Intent intent){
		//pos参数
		if(intent!=null){
			intent.putExtra(STORE_POSTYPE_KEY, this.posType.name());//传递 pos 设备类型
			if(posInfo!=null)
				intent.putExtra(EXTRA_POSINFO, (Parcelable)posInfo);//传递 pos 设备参数
		}
		super.startActivity(intent);
	}
	
	/**
	 * 跳转带返回
	 * @param intent
	 * @param requestCode
	 */
	public void ctbriStartActivityForResult(Intent intent,int requestCode){
		//pos参数
		if(intent!=null){
			intent.putExtra(STORE_POSTYPE_KEY, this.posType.name());//传递 pos 设备类型
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
		//取消终端命令
		/**
		new AsyncTask<ElecPosService,Void,Void>(){
			@Override
			protected Void doInBackground(ElecPosService... params) {
				ElecPosService pos = params[0];
				if(pos==null)
					return null;
				pos.endPosCmd();
				return null;
			}
		}.execute(getPOS());
		*/
		//if(posType== POSType.NULL || posType == POSType.YF)
			this.unbindService(yfConn);
		
		//if(posType== POSType.NULL || posType == POSType.START)
			this.unbindService(startConn);
		
		isExists = true;
	}
	
	/**
	 * 设置 pos 类型
	 * @param type
	 * @param deviceName 设备名称
	 */
	public void setPOSType(POSType type,String deviceName){
		SharedPreferences settings  = this.getSharedPreferences(STORE_POS_KEY,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(STORE_POSTYPE_KEY, type.name());
		editor.putString(STORE_POS_NAME_KEY, deviceName);
		editor.commit();
		this.posType = type;
	}
	
    /**
     * 获取 pos 类型
     * @return
     */
	public POSType getPOSType(){
		return posType;
	}
	
	public boolean isExists(){
		return this.isExists;
	}
	
	/**
	 * 服务连接
	 * 怡丰机具连接
	 */
	private  ServiceConnection yfConn = new  ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			yfpos =   IDeviceService.Stub.asInterface(binder);
			if( posType == POSType.YF)
				pos = new YFPOSService(yfpos); 
			ElecLog.d(getClass(), "服务绑定YF POS 成功！");
		}
		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			yfpos = null;
		}
	};
	
	/**
	 * 实达pos 服务连接
	 */
	private ServiceConnection startConn = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			startpos = IStartDevService.Stub.asInterface(service);
			if(posType == POSType.START)
				pos = new StartDevService(startpos); 
			
			serviceBindComplete();
			ElecLog.d(getClass(), "服务绑定START  POS 成功！");
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {
			startpos = null;
		}
	};
}
