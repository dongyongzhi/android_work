package com.ctbri.ui.more;


import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ctbri.Constants;
import com.ctbri.R;
import com.ctbri.domain.CheckDownloadResponse;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.VersionInfo;
import com.ctbri.ui.BaseActivity;
import com.ctbri.widget.ElecProgressDialog;
import com.yifeng.skzs.util.AutoUpdate;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.DataConvert;
import com.yifeng.skzs.util.HttpDownloader;

/**
 * 
 * @comment:更多
 * @author:Zhu
 * @Date:2012-11-19
 */
public class MoreActivity extends BaseActivity implements OnClickListener{
	private Button more_about,more_sbxx,more_bbgx,more_tccx,top_back;
	private AutoUpdate autoupdate;
	HttpDownloader loader = new HttpDownloader();
	private POSInfo posInfo;
	@SuppressWarnings({"rawtypes" })
	private AsyncTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_list);
		
		autoupdate = new AutoUpdate(this);
		
		more_about = (Button)findViewById(R.id.more_about);
		more_about.setOnClickListener(this);
		
		more_sbxx = (Button)findViewById(R.id.more_sbxx);
		more_sbxx.setOnClickListener(this);
		
		more_bbgx = (Button)findViewById(R.id.more_bbgx);
		more_bbgx.setOnClickListener(this);
		
		more_tccx = (Button)findViewById(R.id.more_tccx);
		more_tccx.setOnClickListener(this);
		
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		posInfo  = this.getPOSInfo();
		//initView();

		this.initBottom();
		this.setTitle(R.string.title_help);
		
	}

	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(task!=null && !task.isCancelled()){
			task.cancel(true);
			task = null;
			return super.onKeyDown(keyCode, event);
		}
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (ConstantUtil.ISEJOB) {// 如果是管理版进来，直接返回;
				this.finish();
			} else {// 直接退出
				dialogUtil.doAdvanceExit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_about:
			Intent intent = new Intent(MoreActivity.this,AboutActivity.class);
			//intent.putExtra("versions", posInfo.getSoftVersion());
			startActivity(intent);
			break;
		case R.id.more_sbxx:
			intent = new Intent(MoreActivity.this,EquipmentInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.more_bbgx:
			//task = new CheckDownLoadTask().execute();  //检查下载新版本
			new Thread(myRunnable).start();
			break;
		case R.id.more_tccx:
			this.dialogUtil.doAdvanceExit();
			break;
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}
	
	private Runnable myRunnable = new Runnable() {
		public void run() {
			try {
						String json="";
						if (autoupdate.isNetworkAvailable(MoreActivity.this)) {
							String versionfile = ConstantUtil.downtxt;
							json = loader.download(versionfile);
						}
						
						Message m=new Message();
						Bundle data=m.getData();
					    data.putString("json",json);
					    m.setData(data);
					    handler.sendMessage(m);
			} catch (Exception e) {
				showWarn("检测试版本出错!" + e);
				//Log.i("检测试版本", "检测试版本出错!" + e);
			}
		}
	};
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String json=msg.getData().getString("json");
			doCheckSystem(json);
		}

	};
/***
	 * 检测试版本
	 */
	private void doCheckSystem(String json) {
		// 检测是否有网络
		try{
			if (json.equals("error")) {
				showWarn("检测试版本出错!");
				Log.i("检测试版本", "检测试版本出错!");
			
			} else if (!json.equals(null) || !json.equals("")) {
				Map<String, String> map = DataConvert.toMap(json);
				float webcode = Float.parseFloat(map.get("CODE"));
				float lochostcode = Float.parseFloat(autoupdate.versionName);
				if (webcode > lochostcode) {
					autoupdate.setMsg(map.get("MSG"));
					autoupdate.check();// 下载远程apk文件
				}else{
					//goPageHome();
					showWarn("当前已为最新版本");
					Log.i("检测试版本", "当前已为最新版本" );
				}
			}else{
				//goPageHome();
			}
		}catch (Exception e) {
			showWarn("检测试版本出错!" + e);
			Log.i("检测试版本", "检测试版本出错!"+e);
			//goPageHome();
		}
	}
 

	class CheckDownLoadTask extends AsyncTask<Void,Integer,Object>{
		private ElecProgressDialog progress;
		private VersionInfo version;

		protected void  onPreExecute(){
			//获取版本信息
			version = MoreActivity.this.getVersion();
			
			progress = new ElecProgressDialog(MoreActivity.this);
			progress.setCancelable(true);
			progress.show();
			progress.setMessage("正在检查版本...");
		}
		
		@Override
		protected Object doInBackground(Void... params) {
			try{
				if(posInfo==null){
					this.publishProgress(1);
					//获取pos终端参数
					posInfo = MoreActivity.this.getPOS().getPosInfo(); 
					
					//获取参数失败
					if(!ResponseCode.SUCCESS.getCode().equals(posInfo.getErrCode())){ 
						return ElecResponse.getErrorResponse(CheckDownloadResponse.class, posInfo.getErrCode(), posInfo.getErrMsg());
					}
				}
				return null;
			}catch(Exception e){
				return e;
			}
		}
		
		protected void onProgressUpdate(Integer... arg){
			progress.setMessage("正在获取POS参数...");
		}
		
		protected void onPostExecute(Object result){
			if(progress!=null  && progress.isShowing())
				progress.dismiss();
			
			if(result instanceof Exception){
				Exception e  = (Exception)result;
				Toast.makeText(MoreActivity.this,e.getMessage() , Toast.LENGTH_SHORT).show();
			}
			
			/*if(result instanceof CheckDownloadResponse){
				//中心返回数据
				CheckDownloadResponse response = (CheckDownloadResponse)result;
				if(response.getErrCode()!=0){
					Toast.makeText(MoreActivity.this,response.getErrMsg() , Toast.LENGTH_SHORT).show();
				}else if(response.getUrl()==null || "".equals(response.getUrl()))
					Toast.makeText(MoreActivity.this,"没有新版本" , Toast.LENGTH_SHORT).show();
				else{ //下载版本
					MoreActivity.this.doCheckSystem(response);// 检测新版本
				}
			}*/
		}
	};
}
