package com.yfcomm.businesshall;





import com.yfcomm.public_define.public_define;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PurchaseActivity extends Activity implements OnClickListener {

	private EditText mEditMoney;
	private Button btnPurchase;
	private Button btnCancel;
	private long amount = 0L;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase);

		mEditMoney = (EditText) this.findViewById(R.id.mEditMoney);
		btnPurchase = (Button) this.findViewById(R.id.btnPurchase);
		btnCancel = (Button) this.findViewById(R.id.btnCancel);

		this.btnPurchase.setOnClickListener(this);
		this.btnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent mIntent;
		switch (v.getId()) {

		case R.id.btnCancel:
			mIntent = new Intent();
			this.setResult(RESULT_CANCELED, mIntent);
			this.finish();
			break;

		case R.id.btnPurchase:
			if (this.mEditMoney.getText().length() == 0) {
				new AlertDialog.Builder(this).setTitle("提示")
						.setMessage("请输入消费金额").setPositiveButton("确定", null)
						.show();
				this.mEditMoney.setFocusable(true);
				return;
			}
			sendResylt();
			break;
		}
	}
	
	public void sendResylt() {

		Intent mIntent = new Intent();
		Bundle arguments = new Bundle();
		arguments.putInt(public_define.SevName, public_define.readmoney);
		arguments.putString(public_define.TRADEMONEY, this.mEditMoney.getText()
				.toString());
		mIntent.putExtra(public_define.SelphonenumInfo, arguments);
		setResult(RESULT_OK, mIntent);
		this.finish();
	}
	
}
