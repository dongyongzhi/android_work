package com.ctbri.ui.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ctbri.R;
import com.ctbri.biz.TransService;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.ResponseCode;
import com.ctbri.ui.BaseActivity;
import com.ctbri.ui.query.QueryHistoryActivitys;
import com.ctbri.widget.ElecProgressDialog;
import com.yifeng.skzs.util.ConstantUtil;

public class ManageActivity extends BaseActivity {
	private GridView gridView;
	private List<Map<String, Object>> menus;
	
	private int action;
	private final static int SIGN = 0;  //签到
	private final static int UPDATEPARAMS = 1;//更新参数
	private TransService trans;
	
	private ElecProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_manage);

		initView();

		this.initBottom();
		this.setFocus(this.bt_bottom_menu2, R.drawable.bottom_menu2_);
	}

	private void initView() {

		gridView = (GridView) findViewById(R.id.gridview);

		gridView.setSelector(R.drawable.edit_text);
		// gridView.setBackgroundResource(R.drawable.tabbg);// 设置背景
		gridView.setNumColumns(3);// 设置每行列数
		gridView.setGravity(Gravity.CENTER);// 位置居中
		gridView.setVerticalSpacing(10);// 垂直间隔
		gridView.setHorizontalSpacing(10);// 水平间隔

		loadMenuData();
		SimpleAdapter adapter = new SimpleAdapter(this, menus,
				R.layout.main_grid_item,
				new String[] { "itemImage", "itemText" }, new int[] {
						R.id.item_imageImg, R.id.item_textTxt });
		gridView.setAdapter(adapter);// 设置菜单Adapter*/

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				//处理签到
				if(menus.get(position).get("tag").equals("qd")){
					progressDialog = new ElecProgressDialog(ManageActivity.this);
					progressDialog.setCancelable(false);
					progressDialog.setMessage("正在签到...");
					progressDialog.show();
					action = SIGN;
					ManageActivity.this.startAsynService(progressDialog);
					return;
				}
				
				//更新终端参数
				if(menus.get(position).get("tag").equals("csgx")){
					action = UPDATEPARAMS;
					progressDialog = new ElecProgressDialog(ManageActivity.this);
					progressDialog.setCancelable(false);
					progressDialog.setMessage("正在更新参数...");
					progressDialog.show();
					ManageActivity.this.startAsynService(progressDialog);
					return;
				}
				
				Intent intent = (Intent) menus.get(position).get("intent");
				if (intent != null) {
					startActivity(intent);
				}
			}
		});
	}
	
	
	/**
	 * pos 绑定完成
	 */
	protected void serviceBindComplete(){
	}

	/**
	 * 处理业务
	 */
	@Override
	public Object onExecAsynService(){
		
		if(action==SIGN){  //签到业务
			//return trans.sign();
			
		}else if(action==UPDATEPARAMS){  //更新参数
					}
		return null;
	}
	
	/**
	 * 成功返回
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
		
		if(action == SIGN){  //签到
			if(obj==null || !((Boolean)obj)){
				this.showError("签到失败！");
				
			}else if((Boolean)obj){
				Toast.makeText(this, "签到成功", Toast.LENGTH_SHORT).show();
			}
		}else if(action == UPDATEPARAMS){ //向posp发起更新参数
			if(obj==null){
				this.showError(" 更新参数失败！");
				return;
			}
				
		    ElecResponse resp = (ElecResponse)obj;
		    if(!ResponseCode.SUCCESS.getCode().equals(resp.getErrCode()))
		    	Toast.makeText(this, resp.getErrMsg(), Toast.LENGTH_SHORT).show();
		    else
		    	Toast.makeText(this, "更新参数成功", Toast.LENGTH_SHORT).show();
			 
		}
	}
	
	private void loadMenuData() {
		menus = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		/*
		 * map.put("itemText", "订单查询"); map.put("itemImage", R.drawable.zhmx);
		 * map.put("tag", "zhmx"); Intent zhmx = null; map.put("intent", zhmx);
		 * menus.add(map);
		 * 
		 * map = new HashMap<String, Object>();
		 */
		map.put("itemText", "交易明细");
		map.put("itemImage", R.drawable.jycx);
		map.put("tag", "zhkt");
		Intent zhkt = new Intent(ManageActivity.this,QueryHistoryActivitys.class);
		map.put("intent", zhkt);
		menus.add(map);

		map = new HashMap<String, Object>();
		map.put("itemText", "消费限额");
		map.put("itemImage", R.drawable.zfxe);
		map.put("tag", "mmxg");
		Intent mmxg = new Intent(ManageActivity.this, RePrintActivity.class);
		map.put("intent", mmxg);
		menus.add(map);

//		map = new HashMap<String, Object>();
//		map.put("itemText", "参数更新");
//		map.put("itemImage", R.drawable.cscx);
//		map.put("tag", "csgx");
//		Intent csgx = null;
//		map.put("intent", csgx);
//		menus.add(map);

		map = new HashMap<String, Object>();
		map.put("itemText", "签到");
		map.put("itemImage", R.drawable.qd);
		map.put("tag", "qd");
		Intent qd = null;
		map.put("intent", qd);
		menus.add(map);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (ConstantUtil.ISEJOB) {// 如果是管理版进来，直接返回;
				this.finish();
			} else {// 直接退出
				dialogUtil.doAdvanceExit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
