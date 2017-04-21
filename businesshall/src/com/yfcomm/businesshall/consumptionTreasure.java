package com.yfcomm.businesshall;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.yfcomm.public_define.public_define;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class consumptionTreasure extends Activity {

	private TextView tv;
	private GridView myGirdView;
	private PaymentGridAdapter payAdpater = null;
	private List<String> Paynames;
	private String names;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choicenum);

		tv = (TextView) findViewById(R.id.text_v);
		tv.setText("发卡商选择");
		myGirdView = (GridView) findViewById(R.id.gridview);

		init();
	}

	public void init() {

		try {
			InputStream is = getAssets().open("paymentoflicence.xml");
			Paynames = AppXmlPayMentName.Read(is); // 运营商名称.
			is.close();

		} catch (IOException e) {

			return;
		}

		payAdpater = new PaymentGridAdapter(this, Paynames);
		myGirdView.setAdapter(payAdpater);
		myGirdView.setNumColumns(1);
		myGirdView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				PaymentGridAdapter adpter = (PaymentGridAdapter) myGirdView.getAdapter();
				if (adpter.setItemsel(position)) {
				    names = adpter.getItem(position);
					CustomDialog dialog;
					CustomDialog.Builder customBuilder = new CustomDialog.Builder(consumptionTreasure.this);
					customBuilder.setMessage("已选发卡商: " + "\r\n" + names).setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								    sendResylt();
								}
							});
					dialog = customBuilder.create(0);
					Window window = dialog.getWindow();
					window.setGravity(Gravity.BOTTOM);
					dialog.show();
				}
			}
		});
	}
	public void sendResylt() {

		Intent mIntent = new Intent(this,consumptionaddandopen.class);
		mIntent.putExtra(public_define.PaymentOper, names);
		startActivity(mIntent);
	
	}

}
