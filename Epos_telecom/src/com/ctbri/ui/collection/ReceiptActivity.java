package com.ctbri.ui.collection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ctbri.Constants;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.MessageSendRequest;
import com.ctbri.domain.PayRequest;
import com.ctbri.domain.TransResponse;
import com.ctbri.ui.BaseActivity;
import com.ctbri.widget.ElecProgressDialog;
/**
 * 
 * @comment:接收
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class ReceiptActivity extends BaseActivity implements OnClickListener,OnCheckedChangeListener{
	private Button send_btn,skip_btn,top_back;
	private EditText mEditPhone,mEditEmail,mEditMessage;
	private RadioGroup radioSendType;
	private TransResponse transResp; //交易信息
	private PayRequest apkPay;//外部调用数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.receipt);
		setTitle(R.string.title_receipt);
		
		send_btn = (Button)findViewById(R.id.send_btn);
		send_btn.setOnClickListener(this);
		skip_btn = (Button)findViewById(R.id.skip_btn);
		skip_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		mEditPhone = (EditText)findViewById(R.id.phone_number);
		mEditEmail = (EditText)findViewById(R.id.email_number);
		mEditMessage = (EditText)findViewById(R.id.contentMessage);
		
		//发送类型
		radioSendType = (RadioGroup)findViewById(R.id.radioSendType);
		radioSendType.setOnCheckedChangeListener(this);
		
		//获取交易信息
		transResp = this.getIntent().getParcelableExtra(EXTRA_TRANS_RESPONSE);
		apkPay  = getIntent().getParcelableExtra(EXTRA_APK_PAY_REQUEST);
		
		String orderDetailUrl = getIntent().getStringExtra(EXTRA_DETAIL_PAGEURL);
		//没有详细显示地址时，由服务器下发信息
		if(orderDetailUrl==null || "".equals(orderDetailUrl)){
			findViewById(R.id.mTextLabelMessage).setVisibility(View.INVISIBLE);
			mEditMessage.setVisibility(View.GONE);
			mEditMessage.setText("");
		} else {
		
			//显示要发送的信息内容
			mEditMessage.setText(String.format(Constants.SEND_MESSAGE,
					transResp.getCard().substring(transResp.getCard().length()-4),
					transResp.getTransDate().substring(0, 2),
					transResp.getTransDate().substring(2, 4),
					getPOSInfo().getCustomerName(),
					transResp.getMessageTypeName(),
					transResp.getMoneyString(),
					orderDetailUrl));
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_btn:
			String type,toUser;
			//vlidate
			if(radioSendType.getCheckedRadioButtonId() == R.id.radioPhone){
				if(mEditPhone.getText().length()==0){
					this.showWarn("请输入接收方的手机号！");
					return;
				}
				type = "0";
				toUser = mEditPhone.getText().toString();
			}else{
				if(mEditEmail.getText().length()==0){
					this.showWarn("请输入接收方的邮件地址！");
					return;
				}
				type = "1";
				toUser = mEditEmail.getText().toString();
			}
			
			//发送信息
			sendMessage(type,toUser);
			break;
		case R.id.skip_btn:
			finish();
			break;
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 发送短信
	 * @param phone
	 */
	private void sendMessage(String type,String toUser){
		//组织 发送信息 
		MessageSendRequest request = new MessageSendRequest();
		request.setCutomerOrderNumber(transResp.getTransNumber());
		request.setToUser(toUser);
		request.setType(type);
		request.setMessage(mEditMessage.getText().toString());
		
		(new AsyncTask<Object,Void,ElecResponse>(){
			private ElecProgressDialog progress;
			
			protected void onPreExecute(){
				progress = new ElecProgressDialog(ReceiptActivity.this);
				progress.setCancelable(true);
				progress.show();
				progress.setMessage("正在发送信息...");
			}
			
			@Override
			protected ElecResponse doInBackground(Object... params) {
				MessageSendRequest request = (MessageSendRequest)params[0];
				try {
					return ServiceFactory.getInstance().getManagerService().messageSend(request);
				} catch (Exception e) {
					return ElecResponse.getErrorResponse(ElecResponse.class,1,e.getMessage());
				}
			}
			
			/**成功返回*/
			protected void onPostExecute(ElecResponse response){
				if(progress!=null  && progress.isShowing())
					progress.dismiss();
				
				if(response.getErrCode()!=0){
					showError(response.getErrMsg());
					
				}else{  //发送成功
					Toast.makeText(ReceiptActivity.this,"发送成功" , Toast.LENGTH_SHORT).show();
					//外部apk调用则返回数据
					if(apkPay!=null){
						TransActionFactory.getInstance().payResult(ReceiptActivity.this, transResp, apkPay);
					}
					ReceiptActivity.this.finish();
				}
			}
		}).execute(request);
	}

	/**
	 * 发送类型选择事件
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		if(checkedId==R.id.radioPhone){ //选择发送至手机
			this.findViewById(R.id.typePhoneInput).setVisibility(View.VISIBLE);
			this.findViewById(R.id.typeEmailInput).setVisibility(View.GONE);
		}else{
			this.findViewById(R.id.typePhoneInput).setVisibility(View.GONE);
			this.findViewById(R.id.typeEmailInput).setVisibility(View.VISIBLE);
		}
	}
}
