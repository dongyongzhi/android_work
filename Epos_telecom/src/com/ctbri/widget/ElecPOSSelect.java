package com.ctbri.widget;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ctbri.R;
import com.ctbri.pos.ElecPosService.POSType;
import com.ctbri.utils.ElecLog;
import com.ctbri.utils.MessageBox;

/**
 * pos机具选择
 * @author qin
 * 
 * 2012-12-25
 */
public class ElecPOSSelect extends BroadcastReceiver implements OnItemClickListener,OnClickListener {
	
	private Dialog dialog;
	private ListView listPOS;
	private Context context;
	private LinearLayout loadingLayout;
	private EditText mTextName;
	private Button mSelectPOS;
	
	private ArrayList<Map<String, Object>> devices = new ArrayList<Map<String, Object>>();
	
	private SimpleAdapter adapter;
	private OnSelectedListener onSelect;
	private BluetoothAdapter device;
	private String address;  //mac地址
	
	private OnCancelListener  onCancelListener;
	
	public ElecPOSSelect(Context context,OnSelectedListener onSelect){
		this.context = context;
		this.onSelect = onSelect;
		
		LayoutInflater inflater = LayoutInflater.from(context);
		View searchLayout = inflater.inflate(R.layout.bluetooth_found,null);
		loadingLayout = new LinearLayout(context);
		loadingLayout.addView(searchLayout);
		loadingLayout.setGravity(Gravity.CENTER_VERTICAL);
		
		//初始化选择对话框
		dialog = new Dialog(context,R.style.dialog);
		//dialog.setCancelable(false);
		
		// 注册查找 蓝牙设备的intent 并返回结果
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(this, filter);
        
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(this, filter);
        
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(this, filter);
        
        //Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(this, filter);
        
        device = BluetoothAdapter.getDefaultAdapter();
        
        if(device!=null){
	        if(!device.isEnabled())
	        	device.enable();
	        else{
	        	device.cancelDiscovery();
	        	device.startDiscovery();
	        }
        }
        
        adapter = new SimpleAdapter(context, devices, R.layout.pos_list_item ,
        		  new String[] { "name","address"}, new int[] {R.id.mPOSName,R.id.mPOSAddress});
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		ElecLog.d(getClass(), action);
		
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {   //找到一个设备
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            ElecLog.d(getClass(), "found:"+device.getName()+" "+device.getAddress());
            //增加己找到的信息
            Map<String, Object> hd = new HashMap<String,Object>();
            hd.put("name", device.getName());
            hd.put("address", device.getAddress());
            devices.add(hd);
            adapter.notifyDataSetChanged();
            
		}else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {  //查找 状态改变
			ElecLog.d(getClass(), "Started discovery");
			
		}else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){  //查找 完成
			ElecLog.d(getClass(), "Started FINISHED");
			//查找 完成
			listPOS.removeHeaderView(loadingLayout);
			//loadingLayout.findViewById(R.id.mImageSearch).setVisibility(View.VISIBLE);
			//loadingLayout.findViewById(R.id.mSearching).setVisibility(View.INVISIBLE);
			
		}else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {  //蓝牙状态变更
			int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
        	switch (state){
			case BluetoothAdapter.STATE_OFF:
				device.cancelDiscovery();
				break;
			case BluetoothAdapter.STATE_ON:
				device.cancelDiscovery();
				device.startDiscovery();
				break;
        	}
		}
	}
	 
	public void show(){
		
		if(!dialog.isShowing()){
			dialog.show();
			
			dialog.setContentView(R.layout.pos_select_dialog);
			listPOS = (ListView)dialog.findViewById(R.id.mListPOS);
			listPOS.setOnItemClickListener(this);
			listPOS.addHeaderView(loadingLayout);
			
			mTextName = (EditText)dialog.findViewById(R.id.mTextName);
			mSelectPOS = (Button)dialog.findViewById(R.id.mSelectPOS);
			mSelectPOS.setOnClickListener(this);
			//添加数据
			listPOS.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			 
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
				@Override
				public void onCancel(DialogInterface v) {
				    if(device!=null)
				    	device.cancelDiscovery();
				    
					context.unregisterReceiver(ElecPOSSelect.this);
					if(onCancelListener!=null)
						onCancelListener.onCancel();
				}
			});
		}
	}

	public void setOnCancelListener(OnCancelListener onCancelListener){
		this.onCancelListener = onCancelListener;
	}
	
	public void dismiss(){
		dialog.dismiss();
		context.unregisterReceiver(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		//选择己查找到的 pos名称和mac 地址
		TextView mPOSName = (TextView)view.findViewById(R.id.mPOSName);
		if(mPOSName!=null){
			String name    =  mPOSName.getText().toString();
			address = ((TextView)view.findViewById(R.id.mPOSAddress)).getText().toString();
			mTextName.setText(name);
		}
	}
	
	public static interface OnSelectedListener{void onSelected(String mac,String name,POSType posType);}
	
	public static interface OnCancelListener{void onCancel();};

	@Override
	public void onClick(View arg0) {
		if(mTextName.getText().length()==0){
			MessageBox.showError(context, "请输入或选择设备名称！");
			return;
			
		}
		selectPOSTypeDialog();
	}
	
	/**
	 * 选择pos机类型
	 */
	public void selectPOSTypeDialog(){
		Builder builder = new AlertDialog.Builder(context);  
		builder.setTitle("POS所属厂商");
		
		String[] names = new String[POSType.values().length];
		int i = 0;
		for(POSType e : POSType.values()){
			names[i] = e.getName();
			i++;
		}
		
		 /*
		//实达测试
		onSelect.onSelected(address,mTextName.getText().toString(),POSType.START); 
		dismiss(); //主对话框
		*/
		 
		//存在多个POS机具，则要选择
		if(POSType.values().length>1){
			builder.setSingleChoiceItems(names, 0, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//选择事件返回
					
					onSelect.onSelected(address,mTextName.getText().toString(),POSType.values()[which]); 
					dialog.dismiss();
					dismiss(); //主对话框
				}
			}).create().show();  //显示
		}else{
			
			//只有一个机具直接返回
			onSelect.onSelected(address,mTextName.getText().toString(),POSType.values()[0]); 
			dismiss(); //主对话框
		}
	}
}
