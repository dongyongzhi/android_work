package com.ctbri.ui.collection;

import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.ctbri.ElecException;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.pos.ElecPosService;
import com.ctbri.ui.BaseActivity;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecProgressDialog;
/**
 * @comment:刷卡
 * @author:ZhangYan
 * @Date:2012-11-22
 * 
 * 
 * 用于消费和查询余额<br/>
 * action:  用来标识消费和查询余额
 */
public class SwipingCardActivity extends BaseActivity implements OnClickListener{
	
	private final static int POS_REQUEST = 1;  //pos 请求交易
	private final static int POS_RESPONSE = 2; //向posp发起请求并返回给pos终端
	
	private ImageView imageView;
	private AnimationDrawable animDrawable;
	private POSTransRequest  request;
	private TransService  transService;
	private ElecProgressDialog  mProgressDialog;
	private AsyncTask<Object,Object,Object>  task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swiping_card);
		
		imageView = (ImageView) findViewById(R.id.frameview);
		findViewById(R.id.top_back).setOnClickListener(this);
		
		
		imageView.post(new Runnable(){
			@Override
			public void run() {
				// 取得帧动画
				animDrawable = (AnimationDrawable) imageView.getBackground();
				animDrawable.start();
			}
		});
		//获取业务类型
		request = TransActionFactory.getInstance().getPOSTransRequest(this);
		setTitle(R.string.title_swiping_card);
	}
	
	protected void serviceBindComplete(){
		transService = ServiceFactory.getInstance().getTransService(getPOS(), this);
		task = new TransTask().execute(POS_REQUEST,this.getPOS(),transService,request); //发起任务
	}
	
	/**
	 * pos输入完成
	 * @param response
	 */
	public void posInputComplete(POSTransResponse response){
		if(mProgressDialog!=null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		if(animDrawable!=null && animDrawable.isRunning())
			animDrawable.stop();
		
		mProgressDialog = new ElecProgressDialog(this);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
		
		mProgressDialog.setMessage("pos正在处理数据...");
		task = new TransTask().execute(POS_RESPONSE,transService,response); //发起任务
	}
	
	/**
	 * 交易任务
	 * @author qin
	 * 
	 * 2012-12-13
	 */
	class TransTask extends AsyncTask<Object,Object,Object>{
		
		@Override
		protected Object doInBackground(Object... params) {
			int action = (Integer)params[0];
			
			try{
				//pos发起请求
				if(action == POS_REQUEST){
					ElecPosService pos = (ElecPosService)params[1]; //pos机
					TransService trans  = (TransService)params[2];//交易业务
					POSTransRequest req = (POSTransRequest)params[3];// pos请求内容
		
					if(pos==null)
						return null;
					
					POSTransResponse response = pos.transRequest(req);
					ElecLog.d(getClass(), "===pos 返回数据===");
					if(response.getState()==ResponseCode.TRANS_NO_SIGN){     //pos返回需要签到
						publishProgress(MessageType.SIGN); //通知签到
						if(!trans.sign(response)){ //签到失败
							return TransResponse.getErrorResponse(TransResponse.class, 1,"签到失败！");
						}
						
						//再次通知pos 刷卡和输入密码
						publishProgress(req.getMessageType());
						response = pos.transRequest(req);
						
					}else if(response.getState()==ResponseCode.TRANS_REVERSAL){
						publishProgress(MessageType.REVERSAL); //通知冲正
						trans.reversal(response); //冲正,直到成功
						
						//再次通知pos 刷卡和输入密码
						publishProgress(req.getMessageType());
						response = pos.transRequest(req);
					}
					return response;
					
				}else if(action == POS_RESPONSE){  //向posp发起请求并返回给pos终端
					 
					TransService trans  = (TransService)params[1];//交易业务处理
					POSTransResponse req = (POSTransResponse)params[2];// pos机返回的交易数据
					
					return trans.pospTransRequest(req); //返回交易数据
				}
				
			}catch(Exception e){
				e.printStackTrace();
				return e;  //返回异常信息
			}
			
			return null;
		}
		
		protected void onProgressUpdate(Object... objs){
			Object progress = objs[0];
			if(progress instanceof Number){
				
				switch((Integer)progress){
				case MessageType.SIGN:
					setDialogMessage("正在签到...");
					break;
					
				case MessageType.REVERSAL:
					setDialogMessage("正在冲正...");
					break;
					
				case MessageType.QUERYBALANCE:
				case MessageType.PURCHASE:
					if(animDrawable!=null && !animDrawable.isRunning())
						animDrawable.start();
					
					dialogDismsis();
					break;
				}
			}
		}
		
		/**
		 * 执行返回
		 */
		protected void onPostExecute(Object result){
			if(result==null){
				showError("交易失败！");
				return;
			}
			
			//发生异常
			if(result instanceof Exception){
				Exception e = (Exception)result;
				if(e instanceof ElecException)
					showError(e.getMessage());
				return;
			}
			
			//pos 输入返回
			if(result instanceof POSTransResponse){
				POSTransResponse response = (POSTransResponse)result;
				if(response.getState()!=ResponseCode.SUCCESS){
					showError(response.getState().getMessage());
					return;
				}else{
					posInputComplete(response); //向posp发起交易并返回
					return;
				}
			}
			
			//posp平台返回pos处理返回
			if(result instanceof TransResponse){
				TransResponse response = (TransResponse)result;
				if(response.getErrCode()==0){  //交易成功
					dialogDismsis();
					response.setMessageType(request.getMessageType());//设置交易类型 (8583包解析失败bug)
					TransActionFactory.getInstance().actionSuccess(SwipingCardActivity.this, response);
					
				}else{
					if(response.getSubErrMsg()!=null)
						showError(response.getErrMsg()+"\r\n"+response.getSubErrMsg());
					else
						showError(response.getErrMsg());
					return;
				}
			}
		}
	};
	
	/**
	 * 显示错误信息结果本界面
	 */
	public void showError(String message){
		if(task.isCancelled())
			return;
		
		if(animDrawable!=null && animDrawable.isRunning())
			animDrawable.stop();
		
		if(mProgressDialog!=null && mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		
		 MessageBox.showError(this, message, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				TransActionFactory.getInstance().actionFail(SwipingCardActivity.this);
			}
		 });
	}
	
	/**
	 * 设置进度信息
	 * @param message
	 */
	public void setDialogMessage(String message){
		if(task.isCancelled())
			return;
		
		if(animDrawable!=null && animDrawable.isRunning())
			animDrawable.stop();
		
		if(mProgressDialog!=null){
			if(!mProgressDialog.isShowing())
				mProgressDialog.show();
		}else{
			mProgressDialog = new ElecProgressDialog(this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		mProgressDialog.setMessage(message);
	}
	
	public void dialogDismsis(){
		if(task.isCancelled())
			return;
		
		if(mProgressDialog!=null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		animDrawable.stop();
		super.onDestroy();
		if(task!=null && !task.isCancelled())
			task.cancel(true);
	}
	
	/**
	 * 返回
	 */
	private void back(){
		//取消终端命令
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
		
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			back();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.top_back:
			back();
			break;
		}
	}
}
