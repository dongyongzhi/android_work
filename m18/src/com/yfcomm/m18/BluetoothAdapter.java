package com.yfcomm.m18;

import java.util.ArrayList;
import java.util.List;

import com.yfcomm.R;
import com.yfcomm.mpos.DeviceInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BluetoothAdapter extends BaseAdapter {

	private final Context context;
	private List<DeviceInfo> deviceInfos;
	
	public BluetoothAdapter(Context context) {
		this.context = context;
		deviceInfos = new ArrayList<DeviceInfo>();
	}
	
	@Override
	public int getCount() {
		return deviceInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceInfos.get(position);
	}
	
	public void addItem(DeviceInfo di) {
		boolean exists = false;
		for(DeviceInfo target : this.deviceInfos) {
			if(target.equals(di)) {
				exists = true;
				break;
			}
		}
		if(!exists) {
			this.deviceInfos.add(di);
			this.notifyDataSetChanged();
		}
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			
			LayoutInflater l = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = l.inflate(R.layout.choose_device_listview_item,null);

			TextView name = (TextView) convertView.findViewById(R.id.deviceName);
			name.setText(deviceInfos.get(position).getName());
			
			TextView address = (TextView) convertView.findViewById(R.id.deviceAddress);
			address.setText(deviceInfos.get(position).getAddress());
			
		} else {
			
			TextView name = (TextView) convertView.findViewById(R.id.deviceName);
			name.setText(deviceInfos.get(position).getName());
			
			TextView address = (TextView) convertView.findViewById(R.id.deviceAddress);
			address.setText(deviceInfos.get(position).getAddress());
		}

		return convertView;
	}

}
