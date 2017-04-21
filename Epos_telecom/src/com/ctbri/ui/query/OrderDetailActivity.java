package com.ctbri.ui.query;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.domain.POSInfo;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.QueryPosOrderResponse.Order;
import com.ctbri.domain.QueryPosOrderResponse.OrderState;
import com.ctbri.net.MessageType;
import com.ctbri.ui.BaseActivity;
import com.ctbri.ui.epos.BackMoneyActivity;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;

/**
 * 查询订单详细显示界面.
 * 
 * <p>如果是当天消费则提供撤消功能，否则提供退货功能.</p>
 * 
 * @author qin
 * 
 * @date 2013-2-25
 */
public class OrderDetailActivity extends BaseActivity implements OnClickListener{

	private Button btnAction,back_btn,top_back;
	private Order order;
	private POSInfo posInfo;
	private ACTION action;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_order_detail);
		setTitle(R.string.title_query_order_detail);
		
		//界面按钮
		btnAction = (Button)findViewById(R.id.action_btn);
		btnAction.setOnClickListener(this);
		back_btn = (Button)findViewById(R.id.back_btn);
		back_btn.setOnClickListener(this);
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		//获取订单信息
		order = this.getIntent().getParcelableExtra(QueryOrderActivity.EXTRA_QUERY_ORDER_DETAIL);
		if(order != null){  //显示信息
			
			((TextView)findViewById(R.id.mTextOrderCode)).setText(order.getOrderCode()); //订单号
			((TextView)findViewById(R.id.mTextCardNo)).setText(order.getCardNo()); //卡号后4位
			((TextView)findViewById(R.id.mTextTransTime)).setText(order.getTrxtime()); //交易时间
			((TextView)findViewById(R.id.mTextTransMoney)).setText(String.format("%,.2f 元",order.getMoney()/100.00)); //交易金额
			((TextView)findViewById(R.id.mTextOrderState)).setText(order.getStatus().getMessage()); //订单状态
			
			((TextView)findViewById(R.id.mTextBatchNo)).setText(order.getPosBatch()); //批次号
			((TextView)findViewById(R.id.mTextVoucherNo)).setText(order.getPosRequestId()); //凭证号
			((TextView)findViewById(R.id.mTextReferNo)).setText(order.getExternalid()); //参考号
			
			//电子签名
			if(order.getValidateUrl()!=null && !"".equals(order.getValidateUrl())){
				try {
					new AsyncLoadImageTask().execute(new URL(order.getValidateUrl()));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}else{
				 //隐藏电子签名栏
				findViewById(R.id.signWrapper).setVisibility(View.GONE);
			}
			
			action  = getAction(order); //获取订单可执行的动作。
			
			switch(action){
			case NULL: //如果不是“己支付”状态的订单，则直接 显示“返回”按钮
				btnAction.setVisibility(View.GONE);
				//设置 返回按钮 为 fill_parent
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			    lp.width = LayoutParams.FILL_PARENT;
				back_btn.setLayoutParams(lp);
				break;
			
			case REVOKE:
				btnAction.setText("撤消");
				break;
				
			case PURCHASEREFUND:
				btnAction.setText("退货");
				break;
			}
		 
		}
		
		//获取 pos 终端信息
		posInfo = this.getIntent().getParcelableExtra(EXTRA_POSINFO);
		if(posInfo!=null){
			((TextView)findViewById(R.id.mTextCustomerName)).setText(posInfo.getCustomerName()); //商户名称
			((TextView)findViewById(R.id.mTextCustomerNo)).setText(posInfo.getCustomerNumber()); //商户编号
			((TextView)findViewById(R.id.mTextPosNo)).setText(posInfo.getPosNumber()); //pos编号
		}
		
	}
	
	/**
	 * 加载电子签名图片，节约空间不需缓存图片
	 * @author qin
	 * 
	 */
	class AsyncLoadImageTask extends AsyncTask<URL, Void, Bitmap>{
		@Override
		protected Bitmap doInBackground(URL... params) {
			if(params==null || params[0]==null)
				return null;
			InputStream  is = null;
			Bitmap mBitmap = null;
			try {
				is = params[0].openStream();
				mBitmap = BitmapFactory.decodeStream(is);
				
			} catch (IOException e) {
				 ElecLog.w(getClass(), e.getMessage());
			}finally{
				if(is!=null)
					try {
						is.close();
					} catch (IOException e) {
						 ElecLog.w(getClass(), e.getMessage());
					}
			}
			
			return mBitmap;
		}
		
		protected void onPostExecute(Bitmap result){
			//显示图片
			((ImageView)findViewById(R.id.iv_sign)).setImageBitmap(result);
			super.onPostExecute(result);  
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.back_btn:
		case R.id.top_back:
			finish();
			break;
			
		case R.id.action_btn: 
			
			if(action==ACTION.REVOKE) {  //撤销
			
				MessageBox.showConfirmBox(this, "是否要撤销该订单？", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						//组织交易数据
						POSTransRequest  request  = new POSTransRequest();
						request.setMessageType(MessageType.CANCEL_PURCHASE);//消费
						request.setOriginalSerialNumber(order.getPosRequestId()); //原流水号
						request.setOriginalBatchNumber(order.getPosBatch()); //批次号
						request.setReferenceNumber(order.getExternalid());
						
						request.setCustomerNumber(posInfo.getCustomerNumber());//商户编号 
						request.setPosNumber(posInfo.getPosNumber());//终端编号
						
						TransActionFactory.getInstance().startAction(OrderDetailActivity.this, request);
					}
				});
				
			} else if(action == ACTION.PURCHASEREFUND) {  //退货
				
				Intent mIntent = new Intent(this,BackMoneyActivity.class);
				mIntent.putExtra(EXTRA_ORDER_DETAIL, order); //订单详情
				
				ctbriStartActivityForResult(mIntent, 1); //转至退货界面
			}
			break;
		}
	}
	
	/**
	 * 交易返回
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data){  
		switch (resultCode){  
		case RESULT_OK:
			//获取返回交易类型
			int messageType = data.getIntExtra(EXTRA_TRANS_RESULT, -1);
			
			//返回交易结果给订单查询界面
			Intent intent = new Intent();
			intent.putExtra(EXTRA_TRANS_RESULT, messageType);
			this.setResult(Activity.RESULT_OK, intent);
			this.finish();
			
			break;
		}
	}
	/**
	 * 获取订单可以执行的动作(撤消或退货)
	 * @param order  订单信息
	 * @return
	 */
	private ACTION getAction(Order order){
		if(order.getStatus()!=OrderState.SUCCESS)
		  return ACTION.NULL;
		
		//判断是否是当天消费如果是则撤消 否则 退货
		Date trxTime = new Date(order.getTime());
		DateFormat   df   =   new   SimpleDateFormat("yyyy-MM-dd"); 
		if(df.format(new Date()).equals(df.format(trxTime))){
			return ACTION.REVOKE;
		} else {
			return ACTION.PURCHASEREFUND;
		}
	}
	
	enum ACTION{
		NULL,
		/**
		 * 撤消
		 */
		REVOKE,
		/**
		 * 退货
		 */
		PURCHASEREFUND
	}
}
