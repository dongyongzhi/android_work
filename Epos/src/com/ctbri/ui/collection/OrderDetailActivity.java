package com.ctbri.ui.collection;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.domain.ResponseCode;
import com.ctbri.domain.TransResponse;
import com.ctbri.net.MessageType;
import com.ctbri.pos.support.POSRevokeRequest;
import com.ctbri.ui.BaseActivity;
import com.ctbri.utils.MessageBox;
/**
 * @comment:订单详情
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class OrderDetailActivity extends BaseActivity implements OnClickListener{
	private Button cancel_btn,back_btn,top_back;
	
	private TransResponse transResp;
	
	private TransService trans;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_detail);
		setTitle(R.string.title_order_detail);
		
		cancel_btn = (Button)findViewById(R.id.cancel_btn);
		cancel_btn.setOnClickListener(this);
		back_btn = (Button)findViewById(R.id.back_btn);
		back_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		//获取交易信息
		transResp = this.getIntent().getParcelableExtra(EXTRA_TRANS_RESPONSE);
		//apkPay = getIntent().getParcelableExtra(EXTRA_APK_PAY_REQUEST);
		
		if(transResp!=null){
			if(transResp.getMessageType() == MessageType.CANCEL_PURCHASE ||
					transResp.getMessageType() == MessageType.RETURNS ||
					transResp.getMessageType() == MessageType.CANCEL_PRE_AUTHORIZE){  //如果是撤消交易、退货 则不能再撤消
				cancel_btn.setVisibility(View.INVISIBLE);
				
				//设置 返回按钮 为 fill_parent
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			    lp.width = LayoutParams.FILL_PARENT;
				back_btn.setLayoutParams(lp);
				back_btn.setBackgroundResource(R.drawable.alert_dialog_btn);
				back_btn.setText("确定");
			}
			
			((TextView)findViewById(R.id.mTextPushCardNo)).setText(transResp.getPushCardNo()); //发卡行号
			((TextView)findViewById(R.id.mTextCardNo)).setText(transResp.getCard()); //卡号
			((TextView)findViewById(R.id.mTextTransType)).setText(transResp.getMessageTypeName()); //交易类型
			
			//卡有效期
			String cardExpDate = transResp.getCardExpDate();
			if(cardExpDate!=null && !"".equals(cardExpDate)){
				if(cardExpDate.length()==4){
					cardExpDate = "20"+cardExpDate.substring(0,2)+"/"+cardExpDate.substring(2);
				} 
			    ((TextView)findViewById(R.id.mTextExpDate)).setText(cardExpDate);
			}
			((TextView)findViewById(R.id.mTextBatchNo)).setText(transResp.getBatchNo()); //批次号
			((TextView)findViewById(R.id.mTextVoucherNo)).setText(transResp.getVoucherNo()); //凭证号
			((TextView)findViewById(R.id.mTextAuthNo)).setText(transResp.getAuthNo()); //授权码
			((TextView)findViewById(R.id.mTextReferNo)).setText(transResp.getTransNumber()); //参考号
			((TextView)findViewById(R.id.mTextTransTime)).setText(transResp.getDateTime()); //交易时间
			((TextView)findViewById(R.id.mTextTransMoney)).setText(transResp.getMoneyString()); //交易金额
			
			 
			((TextView)findViewById(R.id.mTextCustomerName)).setText(transResp.getBusiness()); //商户名称
			((TextView)findViewById(R.id.mTextCustomerNo)).setText(transResp.getBusinessNo()); //商户编号
			((TextView)findViewById(R.id.mTextPosNo)).setText(transResp.getTermNo()); //pos编号
			
			//pos机未打印成功显示电子签名
			if(!transResp.isPrint()){ 
				//电子签名
				Bitmap bitmap = getIntent().getParcelableExtra(EXTRA_SIGN_DATA);
				if(bitmap!=null){
					((ImageView)findViewById(R.id.iv_sign)).setImageBitmap(bitmap);
				}
			}else{
				 //隐藏电子签名栏
				findViewById(R.id.signWrapper).setVisibility(View.INVISIBLE);
			}
		}

		//收单行名称？
		//((TextView)findViewById(R.id.mTextOpteratorNo)).setText(posInfo.getOperator()); //操作员号
		 
	}
	
	
	/**
	 * 交易取消
	 */
	public TransResponse onExecAsynService(){
		if(trans==null)
			return null;
		return trans.revoke("",transResp.getVoucherNo(), transResp.getBatchNo(),transResp.getTransNumber());	
	}
	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
		
		if(obj==null){
			this.showWarn("撤销失败！");
			return ;
		}
		
		TransResponse resp = (TransResponse)obj;
		if(!ResponseCode.SUCCESS.getCode().equals(resp.getErrCode()))
			this.showWarn(resp.getErrMsg());
		else{
			 //转至成功界面
			Intent mIntent = new Intent(this,TradeSuccessActivity.class);
			//参数
			Bundle mBundle = new Bundle();
			mBundle.putParcelable(ElecActivity.EXTRA_TRANS_RESPONSE, resp);
			mIntent.putExtras(mBundle);
			this.startActivity(mIntent);
			this.finish();
		}
	} 

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel_btn:
            //撤销消费
			
			MessageBox.showConfirmBox(this, "是否要撤销该订单？", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					//OrderDetailActivity.this.startAsynService(true); //撤销业务
					//组织交易数据
					POSRevokeRequest  request  = new POSRevokeRequest();
					request.setOriginalSerialNumber(transResp.getVoucherNo()); //原流水号
					
					TransActionFactory.getInstance().startAction(OrderDetailActivity.this, request);
				}
			});
			break;
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}

}
