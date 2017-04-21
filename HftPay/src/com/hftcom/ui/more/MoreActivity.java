package com.hftcom.ui.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.hftcom.R;
import com.hftcom.ui.BaseActivity;

public class MoreActivity extends BaseActivity implements OnClickListener{
	private Button more_about,more_sbxx,more_bbgx,more_tccx,top_back,setflow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_list);
		
		more_about = (Button)findViewById(R.id.more_about);
		more_about.setOnClickListener(this);
		
		more_sbxx = (Button)findViewById(R.id.more_sbxx);
		more_sbxx.setOnClickListener(this);
		
		more_bbgx = (Button)findViewById(R.id.more_bbgx);
		more_bbgx.setOnClickListener(this);
		
		more_tccx = (Button)findViewById(R.id.more_tccx);
		more_tccx.setOnClickListener(this);
		
		top_back = (Button)findViewById(R.id.top_back);
		top_back.setOnClickListener(this);
		
		
		setflow=(Button)findViewById(R.id.setflowdata);
		setflow.setOnClickListener(this);
		
		
		this.setTitle(R.string.title_help);
		
	}
 
	@Override
	public void onClick(View v) {
		Intent intent=null;
		switch (v.getId()) {
		case R.id.more_about:
			 intent = new Intent(MoreActivity.this,AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.more_sbxx:
			break;
		case R.id.more_bbgx:
			break;
		case R.id.more_tccx:
			this.dialogUtil.doAdvanceExit();
			break;
		case R.id.top_back:
			finish();
			break;
		case R.id.setflowdata:
		    intent = new Intent(MoreActivity.this,SetfTradeFlow.class);
			startActivity(intent);
			break;
			
		default:
			break;
		}
	}
 
 
 
}
