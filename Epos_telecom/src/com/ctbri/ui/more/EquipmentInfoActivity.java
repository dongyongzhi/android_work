package com.ctbri.ui.more;

import android.os.Bundle;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.domain.POSInfo;
import com.ctbri.ui.BaseActivity;

/**
 * @comment:设备信息
 * @author:Zhu
 * @Date:2012-12-1
 */
public class EquipmentInfoActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_info);
		
		POSInfo posInfo = this.getPOSInfo(); //pos信息
		if(posInfo!=null){
			//设备信息
			((TextView)findViewById(R.id.posName)).setText(String.format("设备名称：%s", format(posInfo.getPosName())));
			((TextView)findViewById(R.id.posNo)).setText(String.format("设  备  号：%s", format(posInfo.getPosNumber())));
			((TextView)findViewById(R.id.posSerialNumber)).setText(String.format("序  列  号：%s", format(posInfo.getSerialNumber())));
			((TextView)findViewById(R.id.posAddress)).setText(String.format("设备地址：%s", format(posInfo.getAddress())));
			((TextView)findViewById(R.id.posRemark)).setText(String.format("设备能力：%s", format(posInfo.getRemark())));
			((TextView)findViewById(R.id.posCommType)).setText(String.format("通信方式：%s", format(posInfo.getCommType())));
			
			//商户信息
			((TextView)findViewById(R.id.customerNo)).setText(String.format("商  户  号：%s", format(posInfo.getCustomerNumber())));
			((TextView)findViewById(R.id.customerName)).setText(String.format("商户名称：%s", format(posInfo.getCustomerName())));
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
