package com.ctbri.ui;

 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ctbri.Constants;
import com.ctbri.R;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.domain.POSTransRequest;
import com.ctbri.domain.QueryNoticeResponse;
import com.ctbri.net.MessageType;
import com.ctbri.ui.epos.BackMoneyActivity;
import com.ctbri.ui.epos.CancelActivity;
import com.ctbri.ui.epos.PurchaseActivity;
import com.ctbri.ui.notice.NoticeListActivity;
import com.ctbri.utils.POSPUtils;
import com.yifeng.skzs.adapter.ImageAdapter;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.widget.CircleFlowIndicator;
import com.yifeng.skzs.widget.ViewFlow;

/**
 * ClassName:MainMenuActivity Description：首页-九宫格（GridView实现）
 * 
 * @author Administrator Date：2012-10-15
 */
public class MainMenuActivity extends BaseActivity {
	private ImageAdapter imageadapter;
	private ViewFlow viewFlow;
	private GridView gridView;
	private List<Map<String, Object>> menus;
	private QueryNoticeResponse qnResponse;//公告信息


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		initView();
		// 首页广告图片显示
		setBanner();

	    this.initBottom();
		this.setFocus(this.bt_bottom_menu1, R.drawable.bottom_menu1_);
	}

	/**
	 * 首页广告图片显示
	 */
	private void setBanner() {
		qnResponse = getIntent().getParcelableExtra(EXTRA_NOTICE); //获取公告信息
		//解析图片地址
		List<String> images = new ArrayList<String>(); 
		if(qnResponse!=null && qnResponse.getNoticInfo()!=null  && !"".equals(qnResponse.getNoticInfo())){
			try {
				JSONArray arr = new JSONArray(qnResponse.getNoticInfo());
				int len = arr.length();
				if(len>0){
					JSONObject json = null;
					String pic = null;
					for(int i=0;i<len;i++){
						json = arr.getJSONObject(i);
						pic = POSPUtils.getPic(json.getString("noticeContent"));
						if(pic!=null && !"".equals(pic))
							images.add(pic);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		imageadapter = new ImageAdapter(MainMenuActivity.this,images);
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(imageadapter);

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);

		viewFlow.setSelection(0); // 设置初始位置
		viewFlow.startAutoFlowTimer();
	}

	private void initView() {

		gridView = (GridView) findViewById(R.id.gridview);

		gridView.setSelector(R.drawable.edit_text);
		//gridView.setBackgroundResource(R.drawable.edit_text);// 设置背景
		gridView.setNumColumns(3);// 设置每行列数
		gridView.setGravity(Gravity.CENTER);// 位置居中
		gridView.setVerticalSpacing(5);// 垂直间隔
		gridView.setHorizontalSpacing(10);// 水平间隔

		loadMenuData();
		SimpleAdapter adapter = new SimpleAdapter(this, menus,
				R.layout.main_menu_item,
				new String[] { "itemImage", "itemText","count" }, new int[] {
						R.id.item_imageImg, R.id.item_textTxt,R.id.count});
		
		gridView.setAdapter(adapter);// 设置菜单Adapter*/

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
							MainMenuActivity.this.baseStartActivity(mIntent);
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
					
				}else if(tag.equals("QUERYBALANCE")){ //查询余额
					
					POSTransRequest  request  = new POSTransRequest();
					request.setMessageType(MessageType.QUERYBALANCE);//查询
					TransActionFactory.getInstance().startAction(MainMenuActivity.this, request);
					
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
		 
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("itemText", "收款");
		map.put("itemImage", R.drawable.lbsk);
		map.put("tag", "lbsk");
    	map.put("count", "");
		Intent lbsk = new Intent(MainMenuActivity.this, PurchaseActivity.class);
		map.put("intent", lbsk);
		menus.add(map); 
		 
		map = new HashMap<String, Object>();
		map.put("itemText", "退款");
		map.put("itemImage", R.drawable.tk);
		map.put("tag", "tk");
    	map.put("count", "");
		Intent tk =  new Intent(MainMenuActivity.this, BackMoneyActivity.class);;
		map.put("intent", tk);
		menus.add(map);

		map = new HashMap<String, Object>();
		map.put("itemText", "交易撤销");
		map.put("itemImage", R.drawable.cxdd);
		map.put("tag", "cxdd");
    	map.put("count", "");
		Intent jycx = new Intent(MainMenuActivity.this, CancelActivity.class);
		map.put("intent", jycx);
		menus.add(map);

		map = new HashMap<String, Object>();
		map.put("itemText", "余额查询");
		map.put("itemImage", R.drawable.yecx);
		map.put("tag", "QUERYBALANCE");
    	map.put("count", "");
		//Intent yecx = new Intent(MainMenuActivity.this, SwipingCardActivity.class); //刷卡界面
		
    	//Intent yecx = new Intent(MainMenuActivity.this, QueryBalanceAcivity.class);
		//map.put("intent", null);
		menus.add(map);
		
		map = new HashMap<String, Object>();
		map.put("itemText", "公告通知");
		map.put("itemImage", R.drawable.ggtz);
		map.put("tag", "ggtz");
		//map.put("news", R.drawable.circle_orange);
    	//map.put("count", "20");
		Intent ggtz = new Intent(MainMenuActivity.this,NoticeListActivity.class);
		map.put("intent", ggtz);
		menus.add(map);
		
		map = new HashMap<String, Object>();
		map.put("itemText", "客服电话");
		map.put("itemImage", R.drawable.kfdh);
		map.put("tag", "kfdh");
		
		map.put("intent",null);
		menus.add(map);   
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.recomd:
			Uri smsToUri = Uri.parse("smsto:");
			Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
					smsToUri);
			mIntent.putExtra("sms_body", getString(R.string.app_name) + "下载地址:"
					+ ConstantUtil.downapk);
			startActivity(mIntent);
			break;
		case R.id.exit:
			this.dialogUtil.doAdvanceExit();
			break;
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
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
	
	protected void onDestroy() {
		super.onDestroy();
	}
}
