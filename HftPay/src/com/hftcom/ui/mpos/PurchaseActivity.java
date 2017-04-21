package com.hftcom.ui.mpos;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import com.hftcom.R;
import com.hftcom.domain.Transaction;
import com.hftcom.ui.BaseActivity;
import com.hftcom.ui.pay.SwipingCardActivity;
import com.hftcom.ui.reversal.ReversalActivity;
import com.hftcom.utils.MessageBox;
import com.hftcom.widget.Keyboard;
import com.hftcom.widget.MoneyEditText;
import com.qrcodescanner.qrcode.QrCodeActivity;

/**
 * 消费
 */
public class PurchaseActivity extends BaseActivity implements OnClickListener{
	private Keyboard mKeyboard;
	private MoneyEditText mEditMoney;
	private boolean isWx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		this.setTitle(R.string.title_enter_money);
		
		isWx = this.getIntent().getBooleanExtra("wx", false);
		
		mKeyboard  = (Keyboard)this.findViewById(R.id.keyboard);
	    mEditMoney = (MoneyEditText)this.findViewById(R.id.enter_pay);
	    mKeyboard.setFocusEditText(mEditMoney);
	        
		findViewById(R.id.start_swip).setOnClickListener(this);
		findViewById(R.id.top_back).setOnClickListener(this);
		findViewById(R.id.start_reversal).setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.start_swip:
			if(mEditMoney.getMoney() == 0){
				MessageBox.showWarn(this, "请输入金额");
				mEditMoney.setFocusable(true);
				return;
			}
			Transaction transaction = new Transaction();
			transaction = checkLastOrder();
			if(transaction != null){
				intent = new Intent(PurchaseActivity.this,ReversalActivity.class);
				intent.putExtra("transaction", transaction);
				startActivity(intent);
			}else{
				if(isWx)
				{
					intent = new Intent(PurchaseActivity.this,QrCodeActivity.class);
					
				}else{
					intent = new Intent(PurchaseActivity.this,SwipingCardActivity.class);
				}
				intent.putExtra("money", mEditMoney.getMoney());
				startActivity(intent);
				this.finish();
			}
			break;
		case R.id.top_back:
			finish();
			break;
		case R.id.start_reversal:
			intent = new Intent(PurchaseActivity.this,ReversalActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
