package com.yifengcom.yfpos;

import com.yifengcom.yfpos.service.YFPosService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        startService(new Intent(this, YFPosService.class));
     //   this.finish();
        
    }

	public void open(View v){
		startService(new Intent(this, YFPosService.class));
	}
	
	public void close(View v){
		stopService(new Intent(this, YFPosService.class));
	}
}