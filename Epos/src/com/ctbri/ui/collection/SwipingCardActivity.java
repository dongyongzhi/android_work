package com.ctbri.ui.collection;
 

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ctbri.ElecActivity;
import com.ctbri.ElecException;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.domain.POSTransResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.pos.ElecPosService;
import com.ctbri.pos.POSTransRequest;
import com.ctbri.pos.support.POSPurchaseRequest;
import com.ctbri.pos.support.POSSignRequest;
import com.ctbri.ui.BaseActivity;
import com.ctbri.ui.MainMenuActivitys;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecDialog;
import com.ctbri.widget.ElecDialog.Style;
import com.ctbri.widget.ElecProgressDialog;
import com.yfcomm.pos.YFLog;
import com.yifeng.skzs.util.DialogUtil;

/**
 * @comment:刷卡
 * @author:ZhangYan
 * @Date:2012-11-22 用于消费和查询余额<br/>
 *                  action: 用来标识消费和查询余额
 */
public class SwipingCardActivity extends BaseActivity implements	OnClickListener {
	
	private static final YFLog logger = YFLog.getLog(SwipingCardActivity.class);
	// pos 请求交易
	private final static int POS_REQUEST = 1;
	// 向posp发起请求并返回给pos终端
	private final static int POS_RESPONSE = 2; 
	
	private ImageView imageView;
	private AnimationDrawable animDrawable;
	private POSTransRequest request;
	private TransService transService;
	private ElecProgressDialog mProgressDialog;
	private AsyncTask<Object, Object, Object> task;
	POSTransResponse response1;
	int count = 0;

	//private TextView swiping_status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swiping_card);
		dialogUtil = new DialogUtil(this);
		imageView = (ImageView) findViewById(R.id.frameview);
		findViewById(R.id.top_back).setOnClickListener(this);
		//swiping_status = (TextView) findViewById(R.id.swiping_status);
		imageView.post(new Runnable() {
			@Override
			public void run() {
				// 取得帧动画
				animDrawable = (AnimationDrawable) imageView.getBackground();
				animDrawable.start();
			}
		});
		// 获取业务类型
		request = TransActionFactory.getInstance().getPOSTransRequest(this);
		setTitle(R.string.title_swiping_card);
		
	}

	protected void onDeviceReady() {
		transService = ServiceFactory.getInstance().getTransService(getPOS(), this);
		 // 发起任务
		task = new TransTask().execute(POS_REQUEST, this.getPOS(),transService, request);
	}

	DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 1:
				SwipingCardActivity.this.finish();
				startActivity(new Intent(SwipingCardActivity.this,
						MainMenuActivitys.class));
				break;
			case 2:
				SwipingCardActivity.this.finish();
				startActivity(new Intent(SwipingCardActivity.this,
						MainMenuActivitys.class));
				break;
			}
		}
	};

	
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
	 * 签到
	 */
	public void doSign(){
		if(animDrawable!=null && animDrawable.isRunning())
			animDrawable.stop();
		
		 new ElecDialog.Builder(SwipingCardActivity.this, Style.SIGN)
			.setConfirmButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d,int arg1) {
					if(mProgressDialog!=null && mProgressDialog.isShowing()){
						mProgressDialog.dismiss();
					}
					Dialog dialog = (Dialog)d;
					EditText mEditUserNo = (EditText)dialog.findViewById(R.id.mEditUserNo);
					EditText mEditUserPwd = (EditText)dialog.findViewById(R.id.mEditUserPwd);
					
					//验证
					if(mEditUserNo.getText().length()<2){
						MessageBox.showWarn(SwipingCardActivity.this, R.string.warn_OperNo_required);
						return;
					}
					if(mEditUserPwd.getText().length()<4){
						MessageBox.showWarn(SwipingCardActivity.this, R.string.warn_OperPwd_required);
						return;
					}
					dialog.dismiss();
					 
					POSSignRequest signRequest = new POSSignRequest();
					signRequest.setOperNo(mEditUserNo.getTag().toString());
					signRequest.setOperPwd(mEditUserPwd.getText().toString());
					
					//签到
					new  AsyncTask<Object,Exception,Boolean>() {
						private ElecActivity context;
						public void onPreExecute(){
							this.context = SwipingCardActivity.this;
							mProgressDialog = new ElecProgressDialog(context);
							mProgressDialog.setCancelable(false);
							mProgressDialog.show();
							mProgressDialog.setMessage("正在处理签到...");
						}
						@Override
						protected Boolean doInBackground(Object... arg0) {
							//获取参数
							String operNo = (String)arg0[0];
							String operPwd = (String)arg0[1];
							
							if(context.getPOS()!=null) {
								TransService service = ServiceFactory.getInstance().getTransService(context.getPOS(), context);
								try {
									return service.sign(operNo, operPwd);
								} catch(Exception ex) {
									this.publishProgress(ex);
								}
							}
							return false;
						}
						protected void onProgressUpdate(Exception... args){
							Exception ex = args[0];
							//签到失败
							if(ex!=null) {
								this.context.showError(ex.getMessage());
								finish();
							}
						}
						protected void onPostExecute(Boolean result){
							if(mProgressDialog!=null && mProgressDialog.isShowing()) {
								mProgressDialog.dismiss();
							}
							if(result!=null && result) {
								Toast.makeText(this.context, "签到成功", Toast.LENGTH_SHORT).show();
								//再次发起交易任务
								task = new TransTask().execute(POS_REQUEST, context.getPOS(),transService, request);
							}
						}
					};
				}
			})
			.setCancelButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0,int arg1) {
					//取消退出
					animDrawable.stop();
					finish();
				}
			})
			.show();
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
					//pos机
					ElecPosService pos = (ElecPosService)params[1];
					//交易业务
					TransService trans  = (TransService)params[2];
					// pos请求内容
					POSTransRequest req = (POSTransRequest)params[3];
		
					if(pos==null)
						return null;
					
					POSTransResponse response = pos.transRequest(req);
					logger.d( "===pos 返回数据===");
					
					if(response.getCode()==ResponseCode.NO_SIGN){    
						 //pos返回需要签到
						 return response;
						
					}else if(response.getCode()==ResponseCode.REVERSAL){
						//通知冲正
						publishProgress(MessageType.REVERSAL); 
						 //冲正,直到成功
						//trans.reversal(req.getMessageType(),response);
						
						//再次通知pos 刷卡和输入密码
						publishProgress(req.getMessageType());
						response = pos.transRequest(req);
					}
					return response;
					
				} else if(action == POS_RESPONSE){
					//向posp发起请求并返回给pos终端
					TransService trans  = (TransService)params[1];//交易业务处理
					POSTransResponse req = (POSTransResponse)params[2];// pos机返回的交易数据
					//返回交易数据
					return trans.pospTransRequest(req); 
				}
				
			}catch(Exception e){
				e.printStackTrace();
				return e;  //返回异常信息
			}
			
			return null;
		}
		
		protected void onProgressUpdate(Object... objs){
			Object progress = objs[0];
			if(progress instanceof String){
				
				if( MessageType.SIGN.equals(progress)) {
					doSign();
					
				} else if (MessageType.REVERSAL.equals(progress)) {
					setDialogMessage("正在冲正...");
					
				}  else if( MessageType.QUERYBALANCE .equals(progress) ||  MessageType.PURCHASE.equals(progress)) {
					if(animDrawable!=null && !animDrawable.isRunning())
						animDrawable.start();
					dialogDismsis();
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
				//向posp发起交易并返回
				if(response.getCode() == ResponseCode.SUCCESS){
					posInputComplete(response); 
					return;
				} else if(response.getCode() == ResponseCode.NO_SIGN) {
					//签到处理
					doSign();
					
				}else{
					//错误处理
					showError(response.getCode().getMessage());
					return;
				}
			}
			
			//posp平台返回pos处理返回
			if(result instanceof TransResponse){
				TransResponse response = (TransResponse)result;
				if(response.isSuccess()){  //交易成功
					dialogDismsis();
					if(request instanceof POSPurchaseRequest) {
						//设置金额
						response.setMoney(((POSPurchaseRequest)request).getMoney());
					}
					response.setMessageType(request.getMessageType());//设置交易类型 (8583包解析失败bug)
					//解析tlv
					
					
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
	public void showError(String message) {
		if (task.isCancelled())
			return;

 		if (animDrawable != null && animDrawable.isRunning())
 			animDrawable.stop();
 
 		if (mProgressDialog != null && mProgressDialog.isShowing())
 			mProgressDialog.dismiss();
 		
 		/*
		Intent intent=	new Intent(SwipingCardActivity.this,TradeFailActivity.class);
		intent.putExtra("message",message)	;
	
		startActivity(intent);
		finish();
		*/

 		MessageBox.showError(this, message,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
				//TransActionFactory.getInstance().actionFail(SwipingCardActivity.this);
				//startActivity(new Intent(SwipingCardActivity.this,MainMenuActivitys.class));
			}
 		});
	}

	/**
	 * 设置进度信息
	 * 
	 * @param message
	 */
	public void setDialogMessage(String message) {
		if (task.isCancelled())
			return;

		if (animDrawable != null && animDrawable.isRunning())
			animDrawable.stop();

		if (mProgressDialog != null) {
			if (!mProgressDialog.isShowing())
				mProgressDialog.show();
		} else {
			mProgressDialog = new ElecProgressDialog(this);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		mProgressDialog.setMessage(message);
	}

	public void dialogDismsis() {
		if (task.isCancelled())
			return;

		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		animDrawable.stop();
		super.onDestroy();
		
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}
	}

	/**
	 * 返回
	 */
	private void doBack() {
		// 取消终端命令
		new AsyncTask<ElecPosService, Void, Void>() {
			@Override
			protected Void doInBackground(ElecPosService... params) {
				ElecPosService pos = params[0];
				if (pos != null) {
					pos.endCmd();
				}
				return null;
			}
		}.execute(getPOS());
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			doBack();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_back:
			doBack();
			break;
		}
	}

}
