package com.ctbri.ui.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.domain.POSInfo;
import com.ctbri.ui.BaseActivity;
import com.yifeng.skzs.entity.User;

/**
 * @comment:设备信息
 * @author:Zhu
 * @Date:2012-12-1
 */
public class EquipmentInfoActivity extends BaseActivity {
	Button backBtn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_info);
		this.setTitle(R.string.title_pos_info);
		POSInfo posInfo = this.getPOSInfo(); //pos信息
		backBtn= (Button) findViewById(R.id.top_back);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if(posInfo!=null){
			//设备信息
			((TextView)findViewById(R.id.posName)).setText(String.format("设备名称：%s", format(posInfo.getPosName())));
			((TextView)findViewById(R.id.posNo)).setText(String.format("设  备  号：%s", format(User.getMtId())));
			((TextView)findViewById(R.id.posSerialNumber)).setText(String.format("序  列  号：%s", format(posInfo.getSerialNumber())));
			((TextView)findViewById(R.id.posAddress)).setText(String.format("设备地址：%s", format(posInfo.getAddress())));
			((TextView)findViewById(R.id.posRemark)).setText(String.format("设备能力：%s", format(posInfo.getRemark())));
			((TextView)findViewById(R.id.posCommType)).setText(String.format("通信方式：%s", format(posInfo.getCommType())));
			
			//商户信息
			((TextView)findViewById(R.id.customerNo)).setText(String.format("商  户  号：%s", format(User.getName())));//
			((TextView)findViewById(R.id.customerName)).setText(String.format("商户名称：%s", format(User.getCustId())));
			//((TextView)findViewById(R.id.acquirerCode)).setText(String.format("收单行号：%s", format(posInfo.getAcquirerCode())));
		}
	}
	
	private String format(String str){
		if(str==null)
			return "";
		else
			return str;
	}
}
