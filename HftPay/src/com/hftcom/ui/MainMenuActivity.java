package com.hftcom.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.hftcom.Constants;
import com.hftcom.R;
import com.hftcom.adapter.ImageAdapter1;
import com.hftcom.ui.more.MoreActivity;
import com.hftcom.ui.mpos.PurchaseActivity;
import com.hftcom.ui.query.TradeListActivity;
import com.hftcom.widget.CircleFlowIndicator;
import com.hftcom.widget.ViewFlow;

public class MainMenuActivity extends BaseActivity implements OnClickListener{
	
	private ImageAdapter1 imageadapter;
	private ViewFlow viewFlow;
	private GridView gridView;
	private List<Map<String, Object>> menus;
	//private QueryNoticeResponse qnResponse;//公告信息
	private LinearLayout aboutlayout;	//关于我们
	private LinearLayout updateLayout;	//版本更新 
	private LinearLayout infoLayout;	//设备信息
	private LinearLayout exitLayout;	//退出程序

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menus);
		initView();
		setBanner();

	    //this.initBottom();
		//this.setFocus(this.bt_bottom_menu1, R.drawable.bottom_menu1_);
	}

	/**
	 * 首页广告图片显示
	 */
	private void setBanner() {
		//解析图片地址
		List<Integer> images = new ArrayList<Integer>(); 
		images.add(R.drawable.yfpos_pic1);
		images.add(R.drawable.yfpos_pic2);
		imageadapter = new ImageAdapter1(MainMenuActivity.this,images);
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(imageadapter);

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);

		viewFlow.setSelection(0); // 设置初始位置
		viewFlow.startAutoFlowTimer();
	}

	private void initView() {
		
		aboutlayout = (LinearLayout)findViewById(R.id.layout1);
		updateLayout = (LinearLayout)findViewById(R.id.layout2);
		infoLayout = (LinearLayout)findViewById(R.id.layout3);
		exitLayout = (LinearLayout)findViewById(R.id.layout4);
	

		aboutlayout.setOnClickListener(this);
		updateLayout.setOnClickListener(this);
		infoLayout.setOnClickListener(this);
		exitLayout.setOnClickListener(this);
		
		gridView = (GridView) findViewById(R.id.gridview);

		gridView.setSelector(R.drawable.edit_text);
		gridView.setNumColumns(3);// 设置每行列数
		gridView.setGravity(Gravity.CENTER);// 位置居中
		gridView.setVerticalSpacing(5);// 垂直间隔
		gridView.setHorizontalSpacing(10);// 水平间隔
	

		loadMenuData();
		SimpleAdapter adapter = new SimpleAdapter(this, menus,R.layout.main_menu_item,
				new String[] { "itemImage", "itemText","count" }, new int[] {
						R.id.item_imageImg, R.id.item_textTxt,R.id.count});
		
		gridView.setAdapter(adapter);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Intent intent = (Intent) menus.get(position).get("intent");
				String tag = menus.get(position).get("tag").toString();
				
				if(tag.equals("kfdh")){  //客服服务
					final Dialog builder = new Dialog(MainMenuActivity.this, R.style.dialog);
					builder.setContentView(R.layout.confirm_dialog);
					TextView ptitle = (TextView) builder.findViewById(R.id.pTitle);
					TextView pMsg = (TextView) builder.findViewById(R.id.pMsg);
					ptitle.setText(MainMenuActivity.this.getString(R.string.dialog_title_warn));
					pMsg.setText("确定要拔打"+Constants.CUSTOMER_SERVICE_PHONE+"客服电话吗？");
					Button confirm_btn = (Button) builder.findViewById(R.id.confirm_btn);
					Button cancel_btn = (Button) builder.findViewById(R.id.cancel_btn);
					confirm_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							builder.dismiss();
							Intent mIntent = new Intent(Intent.ACTION_CALL ,Uri.parse(String.format("tel:%s",Constants.CUSTOMER_SERVICE_PHONE)));
							startActivity(mIntent);
						}
					});

					cancel_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							builder.dismiss();
						}
					});
					builder.setCanceledOnTouchOutside(true);
					builder.show();
					
				}else{
					if (intent != null) {
						startActivity(intent);
					}
				}
			}
		});
	}

	private void loadMenuData() {
		menus = new ArrayList<Map<String, Object>>();
		Intent intent=null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("itemText", "消费");
		map.put("itemImage", R.drawable.xf_);
		map.put("tag", "lbsk");
    	map.put("count", "");
    	intent = new Intent(MainMenuActivity.this, PurchaseActivity.class);
		map.put("intent", intent);
		menus.add(map); 
		 
		
//		map = new HashMap<String, Object>();
//		map.put("itemText", "消费撤销");
//		map.put("itemImage", R.drawable.cxxf_);
//		map.put("tag", "cxdd");
//    	map.put("count", "");
//		map.put("intent", null);
//		menus.add(map);
		
		
//		map = new HashMap<String, Object>();
//		map.put("itemText", "退款");
//		map.put("itemImage", R.drawable.jyed_);
//		map.put("tag", "tk");
//		map.put("count", "");
//		map.put("intent", null);
//		menus.add(map);
				
//		map = new HashMap<String, Object>();
//		map.put("itemText", "金额查询");
//		map.put("itemImage", R.drawable.zhcx_);
//		map.put("tag", "QUERYBALANCE");
//		map.put("count", "");
//		map.put("intent", null);
//		menus.add(map);

//		map = new HashMap<String, Object>();
//		map.put("itemText", "签到");
//		map.put("itemImage", R.drawable.xftjj_);
//		map.put("tag", "SIGN");		
//		map.put("intent", null);
//		menus.add(map); 
		
		map = new HashMap<String, Object>();
		map.put("itemText", "交易明细");
		map.put("itemImage", R.drawable.jymx_);
		map.put("tag", "tk");
		map.put("count", "");
		intent=  new Intent(MainMenuActivity.this, TradeListActivity.class);;
		map.put("intent", intent);
		menus.add(map);
				
//		map = new HashMap<String, Object>();
//		map.put("itemText", "客服电话");
//		map.put("itemImage", R.drawable.kfdh_);
//		map.put("tag", "kfdh");
//		map.put("intent",null);
//		menus.add(map);   
		

//		map = new HashMap<String, Object>();
//		map.put("itemText", "新闻中心");
//		map.put("itemImage", R.drawable.xwzx_);
//		map.put("tag", "xwzx");
//		//map.put("news", R.drawable.circle_orange);
//    	//map.put("count", "20");
//		map.put("intent", null);
//		menus.add(map);
		
		map = new HashMap<String, Object>();
		map.put("itemText", "扫一扫");
		map.put("itemImage", R.drawable.wx);
		map.put("tag", "sys");		
		intent= new Intent(MainMenuActivity.this,PurchaseActivity.class);
		intent.putExtra("wx", true);
		map.put("intent", intent);
		menus.add(map);   
		
		map = new HashMap<String, Object>();
		map.put("itemText", "帮助");
		map.put("itemImage", R.drawable.help_title);
		map.put("tag", "help");		
		intent= new Intent(MainMenuActivity.this,MoreActivity.class);
		map.put("intent", intent);
		menus.add(map);   
		
	}
	
	private long mExitTime = 0;
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(MainMenuActivity.this, "再按一下返回键退出程序！",
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
				return false;
			} else {
				dialogUtil.doExitProcess();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		
	}
}
