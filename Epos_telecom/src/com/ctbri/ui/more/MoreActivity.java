package com.ctbri.ui.more;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.ctbri.Constants;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.domain.CheckDownloadResponse;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.VersionInfo;
import com.ctbri.ui.BaseActivity;
import com.ctbri.widget.ElecProgressDialog;
import com.yifeng.skzs.util.AutoUpdate;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.HttpDownloader;

/**
 * 
 * @comment:更多
 * @author:Zhu
 * @Date:2012-11-19
 */
public class MoreActivity extends BaseActivity implements OnClickListener{
	private Button more_about,more_sbxx,more_bbgx,more_tccx;
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
		
		posInfo  = this.getPOSInfo();
		//initView();

		this.initBottom();
		this.setFocus(this.bt_bottom_menu4, R.drawable.bestpay_menu_2);
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
			startActivity(intent);
			break;
		case R.id.more_sbxx:
			intent = new Intent(MoreActivity.this,EquipmentInfoActivity.class);
			startActivity(intent);
			break;
		case R.id.more_bbgx:
			task = new CheckDownLoadTask().execute();  //检查下载新版本
			break;
		case R.id.more_tccx:
			this.dialogUtil.doAdvanceExit();
			break;
		default:
			break;
		}
	}
	
	private void doCheckSystem(CheckDownloadResponse response) {
		try {
			//ConstantUtil.downapk  = response.getUrl(); //设置下载地址
			autoupdate.setStrURL(response.getUrl());
			autoupdate.setMsg("新版本下载");
			autoupdate.check();// 下载远程apk文件
		} catch (Exception e) {
			Log.i("检测试版本", "检测试版本出错!" + e);
			dialogUtil.shortToast("检测试版本出错!");
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
					posInfo = MoreActivity.this.getPOS().getPosInfo(); //获取pos终端参数
					if(posInfo.getErrCode()!=0){ //获取参数失败
						return ElecResponse.getErrorResponse(CheckDownloadResponse.class, posInfo.getErrCode(), posInfo.getErrMsg());
					}
				}
				
				CheckDownloadResponse response =  ServiceFactory.getInstance().getManagerService()
						   .checkDownload(String.valueOf(version.getVersionCode()),
								   Constants.SOFT_TYPE, 
								   posInfo.getPosNumber(), 
								   version.getVersionName());
				return response;
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
			
			if(result instanceof CheckDownloadResponse){
				//中心返回数据
				CheckDownloadResponse response = (CheckDownloadResponse)result;
				if(response.getErrCode()!=0){
					Toast.makeText(MoreActivity.this,response.getErrMsg() , Toast.LENGTH_SHORT).show();
				}else if(response.getUrl()==null || "".equals(response.getUrl()))
					Toast.makeText(MoreActivity.this,"没有新版本" , Toast.LENGTH_SHORT).show();
				else{ //下载版本
					MoreActivity.this.doCheckSystem(response);// 检测新版本
				}
			}
		}
	};
}
