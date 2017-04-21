package com.yfcomm.m18;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yfcomm.R;
import com.yfcomm.mpos.YFMPos;
import com.yfcomm.mpos.api.SwiperController;
import com.yfcomm.mpos.model.CardModel;
import com.yfcomm.mpos.model.TrxType;
 

/**
 * 消费
 *
*/
public class PurchaseActivity extends BaseActivity implements OnClickListener{
	
	private EditText mEditMoney;
	private Button btnPurchase;
	private Button btnCancel;
	
	private ProgressDialog progressDialog;
	private SwiperController swiper;
	
	private long amount = 0L;
	  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);
		
		mEditMoney = (EditText)this.findViewById(R.id.mEditMoney);
		btnPurchase = (Button)this.findViewById(R.id.btnPurchase);
		btnCancel = (Button)this.findViewById(R.id.btnCancel);
		
		this.btnPurchase.setOnClickListener(this);
		this.btnCancel.setOnClickListener(this);
		progressDialog = new ProgressDialog(this);
		
		
		 //初始化刷卡器类
	    swiper = new SwiperController(this,swiperListener);
	}
 

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			//取消
			case R.id.btnCancel:
				this.finish();
				break;
			
			//消费
			case R.id.btnPurchase:
				if(this.mEditMoney.getText().length() == 0) {
					new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("请输入消费金额")
						.setPositiveButton("确定", null) 
						.show();
					this.mEditMoney.setFocusable(true);
					return;
				}
				
				progressDialog.setMessage("正在刷卡...");
				progressDialog.setCancelable(true);
				progressDialog.setOnCancelListener(new OnCancelListener(){
					@Override
					public void onCancel(DialogInterface dialog) {
						//取消刷卡
						swiper.cancel();
					}
				});
				progressDialog.show();
				
				amount= ((Double)( Double.valueOf(this.mEditMoney.getText().toString()) * 100 )).longValue();
				swiper.startSwiper(120, amount,0, TrxType.PURCHASE);
				break;
		}
	}
	
	public void setText(int resId,String text) {
		((TextView)this.findViewById(resId)).setText(text);
	}
	
	
	private SimpleSwiperListener swiperListener = new SimpleSwiperListener() {
		
		@Override
		public void onError(int code, String messsage) {
			progressDialog.dismiss();
			
			new AlertDialog.Builder(PurchaseActivity.this).setTitle("提示")
			.setMessage("操作失败，返回码："+ code +" 信息:"+messsage)
			.setPositiveButton("确定", null) 
			.show();
		}
		
		
		public void onDetectIc() {
			progressDialog.setMessage("检测到IC卡");
		}
		
		public void onInputPin() {
			progressDialog.setMessage("请输入密码");
		}
		
		@Override
		public void onSwiperSuccess(CardModel cardModel) {
			progressDialog.dismiss();
			
			//数据加密成功返回
			StringBuilder sb = new StringBuilder();
			sb.append("pan:").append(cardModel.getPan()).append("\n");
			sb.append("expireDate:").append(cardModel.getExpireDate()).append("\n");
			
			sb.append("batchNo:").append(cardModel.getBatchNo()).append("\n");
			sb.append("serialNo:").append(cardModel.getSerialNo()).append("\n");
			sb.append("track2:").append(cardModel.getTrack2()).append("\n");
			sb.append("track3:").append(cardModel.getTrack3()).append("\n");
			sb.append("encryTrack2:").append(cardModel.getEncryTrack2()).append("\n");
			sb.append("encryTrack3:").append(cardModel.getEncryTrack3()).append("\n");
			
			sb.append("icData:").append(cardModel.getIcData()).append("\n");
			
			sb.append("mac:").append(cardModel.getMac()).append("\n");
			sb.append("pinBlock:").append(cardModel.getPinBlock()).append("\n");
			
			sb.append("icseq:").append(cardModel.getIcSeq()).append("\n");
			sb.append("random:").append(cardModel.getRandom()).append("\n");
			
			PurchaseActivity.this.setText(R.id.trxResult, sb.toString());
		}

		@Override
		public void onTimeout() {
			progressDialog.dismiss();
			
			new AlertDialog.Builder(PurchaseActivity.this).setTitle("错误")
				.setMessage("刷卡超时")
				.setPositiveButton("确定", null) 
				.show();
		}
		
		@Override
		public void onTradeCancel() {
			progressDialog.dismiss();
			
			new AlertDialog.Builder(PurchaseActivity.this).setTitle("错误")
				.setMessage("取消刷卡")
				.setPositiveButton("确定", null) 
				.show();
		}
	};
}
