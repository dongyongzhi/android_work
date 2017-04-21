package com.hftcom.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.hftcom.R;
 
public class LoadingActivity extends Activity{

	private ImageView imageView;
	private AnimationDrawable animDrawable;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.loading);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		imageView = (ImageView) findViewById(R.id.frameview);
		imageView.post(new Runnable() {
			@Override
			public void run() {
				animDrawable = (AnimationDrawable) imageView.getBackground();
				animDrawable.start();
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try{
					animDrawable.stop();
				}catch (Exception e) {
				}
				goPageHome();
			}
		}, 2000);
	}
	
	private void goPageHome() {
		Intent main = new Intent(LoadingActivity.this, MainMenuActivity.class);
		startActivity(main);
		this.finish();
	}

}