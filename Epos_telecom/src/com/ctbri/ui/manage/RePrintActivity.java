package com.ctbri.ui.manage;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.R;
import com.ctbri.biz.ManagerService;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.domain.PrintResponse;
import com.ctbri.ui.BaseActivity;

/**
 * @comment:POS重打印
 * @author:ZhangYan
 * @Date:2012-11-22
 */
public class RePrintActivity extends BaseActivity implements OnClickListener {
	private TextView loading_text;
	private ManagerService  manager;
	private ImageView reprint_img;
	private AnimationDrawable animDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reprint_view);
		
		setTitle(R.string.title_reprint);
		loading_text = (TextView) findViewById(R.id.loading_text);
		
		reprint_img = (ImageView) findViewById(R.id.reprint_img);
		reprint_img.post(new Runnable(){
			@Override
			public void run() {
				// 取得帧动画
				animDrawable = (AnimationDrawable) reprint_img.getBackground();
				animDrawable.start();
			}
		});
		
		findViewById(R.id.btnPrint).setOnClickListener(this);
		
		Button top_back = (Button) findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				RePrintActivity.this.finish();
			}
		});
	}
	
	/**
	 * pos 绑定完成
	 */
	protected void serviceBindComplete(){
		manager = ServiceFactory.getInstance().getManagerService();
		loading_text.setText("正在打印中...");
	}
	
	/**
	 * 打印
	 */
	@Override
	public Object onExecAsynService(){
	    return manager.printLastTrans(getPOS());
	}
	
	/**
	 * 打印返回
	 */
	public void onServiceSuccess(Object obj){
		if(obj==null){
			loading_text.setText("打印失败！");
			return;
		}
		PrintResponse resp = (PrintResponse)obj;
		if(resp.getErrCode()!=0){
			loading_text.setText(resp.getErrMsg());
			Toast.makeText(this, resp.getErrMsg(), Toast.LENGTH_SHORT).show();
		}else
			loading_text.setText("打印成功！");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(animDrawable!=null && animDrawable.isRunning())
			animDrawable.stop();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnPrint:
			this.startAsynService(); //开始打印
			v.setVisibility(View.INVISIBLE);
			loading_text.setVisibility(View.VISIBLE);
			break;
		}
	}
}
