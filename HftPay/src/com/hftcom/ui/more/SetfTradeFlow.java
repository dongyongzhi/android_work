package com.hftcom.ui.more;

import com.hftcom.R;
import com.hftcom.YFApp;
import com.hftcom.ui.BaseActivity;
import com.hftcom.utils.Config;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetfTradeFlow extends BaseActivity {

	private Button setflow, setcancel, top_back;
	private EditText editeText, curmflow_num;
	private TextView activityTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.btn_setflow);

		activityTitle = (TextView) findViewById(R.id.activityTitle);
		activityTitle.setText("设置交易流水号");
		top_back = (Button) findViewById(R.id.top_back);
		top_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		setflow = (Button) findViewById(R.id.setflow_ok);
		setflow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editeText.getText().toString().isEmpty()) {
					Toast.makeText(SetfTradeFlow.this, "请输入交易流水号", Toast.LENGTH_SHORT).show();
				} else {
					YFApp.getApp().writeSDCard("currentSequence=" + Integer.parseInt(editeText.getText().toString()),
							Config.sName);
					String flownum = YFApp.getApp().loadSequence(Config.sName).get("currentSequence");
					if (flownum != null
							&& Integer.parseInt(flownum) == Integer.parseInt(editeText.getText().toString())) {
						curmflow_num.setText(flownum);
						curmflow_num.setEnabled(false);
						curmflow_num.setFocusable(false);
						Toast.makeText(SetfTradeFlow.this, "设置成功", Toast.LENGTH_SHORT).show();
					
					} else {
						Toast.makeText(SetfTradeFlow.this, "设置失败", Toast.LENGTH_SHORT).show();
					}
				}

			}
		});
		setcancel = (Button) findViewById(R.id.setflow_cancel);
		setcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		editeText = (EditText) findViewById(R.id.mflow_num);

		curmflow_num = (EditText) findViewById(R.id.curmflow_num);
		curmflow_num.setText(YFApp.getApp().loadSequence(Config.sName).get("currentSequence"));
		curmflow_num.setEnabled(false);
		curmflow_num.setFocusable(false);

	}

}
