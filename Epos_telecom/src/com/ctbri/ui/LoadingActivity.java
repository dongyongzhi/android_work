package com.ctbri.ui;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.Constants;
import com.ctbri.ElecActivity;
import com.ctbri.ElecException;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.domain.CheckDownloadResponse;
import com.ctbri.domain.LoginResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSInfoResponse;
import com.ctbri.domain.QueryNoticeRequest;
import com.ctbri.domain.QueryNoticeResponse;
import com.ctbri.domain.QueryNoticeResponse.Notice;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.VersionInfo;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.ElecPosService.POSType;
import com.ctbri.pos.POSException;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecPOSSelect;
import com.yifeng.skzs.util.AutoUpdate;

/**
 * comment:闪屏界面
 * 
 * @author:ZhangYan Date:2012-10-24
 */
public class LoadingActivity extends BaseActivity implements OnClickListener {
 
	/** 设备初始化*/
	private final static int INIT_POS = 1;
	/**初始化pos成功*/
	private final static int INIT_POS_SUCCESS = 4;
	/**版本检查*/
	private final static int CHECK_VERSION = 2;
	/**获取公告信息*/
	private final static int GET_NOTICE= 3;
	 
	/**正在签到*/
	private final static int SIGNING = 5;
	/**正在登录*/
	private final static int LOGIN = 6;
	
	private ImageView imageView;
	private AnimationDrawable animDrawable;
	private AutoUpdate autoupdate;
	
	private TextView txtStatus;
	private View mDeviceSelect,mSettingPOSName;
	
	private POSInfo  posInfo; //pos 机具参数
	private QueryNoticeResponse qnResponse;//公告信息
	private POSType posType;//pos 类型
	
	private String connAddress,connName;
	private LoadingTask task;  //下载信息

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		autoupdate = new AutoUpdate(this);
		//loader = new HttpDownloader();

		txtStatus = (TextView) this.findViewById(R.id.txtStatus);
		mDeviceSelect = this.findViewById(R.id.mDeviceSelect);
		mSettingPOSName = this.findViewById(R.id.mSettingPOSName);
		
		imageView = (ImageView) findViewById(R.id.frameview);
	}
	
	/**
	 * 设备绑定成功
	 */
	protected void serviceBindComplete(){
		
		SharedPreferences settings  = this.getSharedPreferences(STORE_POS_KEY,Context.MODE_PRIVATE);
		String type = settings.getString(STORE_POSTYPE_KEY, "");
		//获取上次连接的名称
		connName = settings.getString(STORE_POS_NAME_KEY, "");
		
		if("".equals(connName)){
			selectPOS();
			return;
		}
		if("".equals(type)){
			selectPOS();
			return;
		}
		posType = POSType.valueOf(type);  //获取 pos 类型
		task = new LoadingTask();
		task.execute(LoadingActivity.this,autoupdate,posType,null,connName);
	}
	
	/**
	 * 选择POS机
	 */
	private void selectPOS(){
		ElecPOSSelect posSelectDialog = new ElecPOSSelect(this,new ElecPOSSelect.OnSelectedListener(){
			@Override
			public void onSelected(String mac,String name,POSType posType) {
				if(task==null || task.isCancelled())
					task = new LoadingTask();
				LoadingActivity.this.posType = posType;
				LoadingActivity.this.connAddress = mac;
				LoadingActivity.this.connName = name;
				
				task = new LoadingTask();
				task.execute(LoadingActivity.this,autoupdate,posType,connAddress,connName);
			}
		});
		
		//取消继续连接
		posSelectDialog.setOnCancelListener(new ElecPOSSelect.OnCancelListener(){
			@Override
			public void onCancel() {
				if(LoadingActivity.this.getPOS()!=null)
					LoadingActivity.this.getPOS().release();
				if(posType!=null){
					task = new LoadingTask();
					task.execute(LoadingActivity.this,autoupdate,posType,null,connName);
				}
			}
		});
		posSelectDialog.show();
	}
	
	/**
	 * 启动任务   
	 * @author qin
	 * 
	 * 2012-12-25
	 */
	class LoadingTask extends AsyncTask<Object,Object,Object>{
	
		private String deviceId,imsi;
		private VersionInfo version;
		
		protected void onPreExecute(){
			
			//获取版本号
			version = LoadingActivity.this.getVersion();
			
			mDeviceSelect.setOnClickListener(LoadingActivity.this);
			mDeviceSelect.setBackgroundResource(R.drawable.btn_key_clear_on);
			mSettingPOSName.setVisibility(View.VISIBLE);
			txtStatus.setTextColor(Color.parseColor("#4F9FC9"));
			//获取设备信息
			TelephonyManager tm = (TelephonyManager) LoadingActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
			deviceId = tm.getDeviceId();
			imsi = tm.getSubscriberId();
			
			imageView.post(new Runnable(){
				@Override
				public void run() {
					// 取得帧动画
					animDrawable = (AnimationDrawable) imageView.getBackground();
					animDrawable.start();
				}
			});
		}
		
		protected Object doInBackground(Object... params) {
			ElecActivity elec = (ElecActivity)params[0];
			CheckDownloadResponse response =null;
			try {
				ElecPosService pos = null;   
				
				//获取以前绑定成功的设备类型  (系统默认 pos 设备类型)
				POSType posType = (POSType)params[2];
				String mac = (String)params[3];
				String posName = (String)params[4];  //获取选择的pos 机名称(蓝牙名称)
				
				//==========step 1   初始连接pos 设备  =============//
				publishProgress(INIT_POS,posName);
				ResponseCode result =  ResponseCode.convert(elec.initDevice(posType,mac, posName));
				//己退出
				if(this.isCancelled()){
					ElecLog.d(getClass(), "====>pos connection  cancelled");
					if(elec.getPOS()!=null)
						elec.getPOS().release();
					return null;
				}
				
				if(result != ResponseCode.INIT_SUCCESS) //未找到pos直接退出
					throw new POSException(result,LoadingActivity.this);
				else{
					publishProgress(INIT_POS_SUCCESS);
				}
				
				
				//==========step 2   获取设备参数  =============//
				pos = elec.getPOS();  //pos设备
				POSInfo posInfo = pos.getPosInfo(); 
			    if(posInfo!=null) {
			    	posInfo.setPosName(posName);//设置设备名称
			    	POSInfoResponse posInfoResponse = ServiceFactory.getInstance()
								.getManagerService().queryPosInfo(posInfo.getPosNumber());
			    	
			    	if(posInfoResponse==null){
			    		throw new ElecException("中心：终端未注册！"); 
			    	}
			    	//posInfo.setCustomerNumber(posInfoResponse.getCustomerNumber()); //商户号，并非网点商户号
			    	//posInfo.setCustomerName(posInfoResponse.getCustomerName());
			    	posInfo.setSerialNumber(posInfoResponse.getSerialNumber());
			    }
				publishProgress(posInfo);
				
				//==========step 3   登录. =============//
				publishProgress(LOGIN);
				LoginResponse loginResponse = ServiceFactory.getInstance().getManagerService()
												.login(posInfo.getOperator(), posInfo.getPosNumber(), imsi, deviceId);
			    //登录失败直接退出
				if(loginResponse.getErrCode()!=0)
					throw new ElecException(loginResponse.getErrMsg());
				if(!loginResponse.isIslogin())
					throw new ElecException(Constants.LOGIN_ERROR_MSG);
				
				//==========step 4   版本检查下载  =============//
				publishProgress(CHECK_VERSION);
				AutoUpdate update = (AutoUpdate)params[1];
				if (update.isNetworkAvailable(LoadingActivity.this)) {
					response = ServiceFactory.getInstance().getManagerService()
												.checkDownload(String.valueOf(version.getVersionCode()), 
														Constants.SOFT_TYPE, 
														posInfo.getPosNumber(), 
														version.getVersionName());
				}
				
				//==========step 5   获取紧急公告，如果有直接提示并退出程序  =============//
				publishProgress(GET_NOTICE);
				QueryNoticeRequest request = new QueryNoticeRequest();
				request.setNoticeType("1"); 
				request.setCustomerNumber(posInfo.getCustomerNumber());
				request.setPosCati(posInfo.getPosNumber());
				qnResponse = ServiceFactory.getInstance().getManagerService().queryNotice(request);
				if(qnResponse.getErrCode()==0 && qnResponse.getNotices()!=null && qnResponse.getNotices().length>0 ) //紧急公告
					 return qnResponse; //直接返回并退出程序
				
				//==========step 6  获取一般公告中的广告图片信息(这里不下载图片，在首页上下载并存储)  =============//
				request.setNoticeType("0");
				qnResponse = ServiceFactory.getInstance().getManagerService().queryNotice(request);
				if(qnResponse.getErrCode()==0)
					publishProgress(qnResponse);
				
				
			}catch(Exception e){
				return e;
			}
			return response; //返回版本信息
		}
		
		/**
		 * 处理进度
		 */
		protected void onProgressUpdate(Object... objs){
			Object p = objs[0];
			if(p instanceof Number){  //初始化pos
				int state = (Integer)p;
				if(state ==INIT_POS){
					txtStatus.setText(String.format("正在连接：%s", (String)objs[1]));
				}else if(state == SIGNING){
					txtStatus.setText("正在签到...");
				}else if(state == CHECK_VERSION){
					txtStatus.setText("正在检查版本...");
				}else if(state==GET_NOTICE){
					txtStatus.setText("正在获取公告...");
				}else if(state == LOGIN){
					txtStatus.setText("正在登录...");
				}else if(state==INIT_POS_SUCCESS){  
					//连接pos成功
					mDeviceSelect.setOnClickListener(null);
					mDeviceSelect.setBackgroundDrawable(null);
					mSettingPOSName.setVisibility(View.GONE);
					txtStatus.setTextColor(Color.WHITE);
				}
				
			}else if(p instanceof ResponseCode){ //pos初始化返回
				ResponseCode resp = (ResponseCode)p ;
				txtStatus.setText(String.format("%s",resp.getMessage()));
			}else if(p instanceof POSInfo){
				posInfo = (POSInfo)p;
			}else if(p instanceof QueryNoticeResponse){  //获取公告图片信息
				qnResponse = (QueryNoticeResponse)p;
			}
		}
		
		/**成功返回*/
		protected void onPostExecute(Object result){
			if(animDrawable!=null && animDrawable.isRunning())
				animDrawable.stop();
			
			if(this.isCancelled())
				return;
			
			//返回异常,退出程序
			if(result instanceof Exception){
				if(result instanceof POSException){  //pos异常
					
					MessageBox.showError(LoadingActivity.this,((POSException)result).getSpannableString(), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							LoadingActivity.this.finish();
						}
					});
					
				}else{  //其它异常返回
					
					Exception e = (Exception)result;
					MessageBox.showError(LoadingActivity.this,e.getMessage(), new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
							LoadingActivity.this.finish();
						}
					});
				}
				return;
			}
			
			//紧急公告返回 ,显示并退出程序
			if(result instanceof QueryNoticeResponse){  
				String msg = "";
				for(Notice e : ((QueryNoticeResponse)result).getNotices()){
					msg += e.getNoticeInfo();
				}
				MessageBox.showWarn(LoadingActivity.this, "紧急公告",msg, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
						LoadingActivity.this.finish();
					}
				});
				return;
			}
			
			//检查下载返回
			CheckDownloadResponse response = (CheckDownloadResponse)result;
			
			if(response==null){
				goPageHome();
				return;
			}
			//是否需要下载
			if (response.getErrCode()!=0) {
				Toast.makeText(LoadingActivity.this, response.getErrMsg(), Toast.LENGTH_SHORT).show();
				goPageHome();
			} else {
				if(response.getUrl()!=null && !"".equals(response.getUrl())) //存在下载址
					doCheckSystem(response);// 检测新版本
				else
					goPageHome();
			}
		}
		
		protected void onCancelled(){
			ElecLog.d(getClass(), "======cancel=======");
			if(animDrawable!=null && animDrawable.isRunning())
				animDrawable.stop();
		}
	};
	
 	/***
	 * 检测试版本
	 */
	private void doCheckSystem(CheckDownloadResponse response) {
		try {
			//ConstantUtil.downapk  = "d";//response.getUrl(); //设置下载地址
			autoupdate.setStrURL(response.getUrl());
			autoupdate.setMsg("新版本下载");
			autoupdate.check();// 下载远程apk文件
			
			ElecLog.d(getClass(), "=========>doCheckSystem");
		} catch (Exception e) {
			Log.i("检测试版本", "检测试版本出错!" + e);
			goPageHome();
		}
	}

	/**
	 * 跳转登录
	 */
	private void goPageHome() {
		Intent main = new Intent(LoadingActivity.this, MainMenuActivity.class);
		main.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		main.putExtra(EXTRA_POSINFO, (Parcelable)posInfo);   //pos机参数信息
		main.putExtra(EXTRA_NOTICE, (Parcelable)qnResponse);  //公告图片信息
		
		startActivity(main);
		this.finish();
	}

	/**
	 * 返回
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if(task!=null && !task.isCancelled())
				task.cancel(true);//取消任务
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.mDeviceSelect:  //选择设备
			
			if(task!=null && !task.isCancelled()){
				task.cancel(true);//取消任务
			}
			selectPOS();
			break;
		}
	}
}