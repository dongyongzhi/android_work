package com.ctbri.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.Constants;
import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.biz.TransActionFactory;
import com.ctbri.biz.TransService;
import com.ctbri.pos.support.POSQueryBalanceRequest;
import com.ctbri.ui.epos.CancelActivity;
import com.ctbri.ui.epos.PurchaseActivity;
import com.ctbri.ui.manage.RePrintActivity;
import com.ctbri.ui.more.MoreActivity;
import com.ctbri.ui.notice.NoticeListActivitys;
import com.ctbri.ui.query.QueryHistoryActivitys;
import com.ctbri.utils.MessageBox;
import com.ctbri.widget.ElecDialog;
import com.ctbri.widget.ElecProgressDialog;
import com.ctbri.widget.ElecDialog.Style;
import com.yifeng.skzs.adapter.ImageAdapter1;
import com.yifeng.skzs.util.ConstantUtil;
import com.yifeng.skzs.util.HttpDownloader;
import com.yifeng.skzs.widget.CircleFlowIndicator;
import com.yifeng.skzs.widget.ViewFlow;

public class MainMenuActivitys extends BaseActivity implements OnClickListener{
	
	private ImageAdapter1 imageadapter;
	private ViewFlow viewFlow;
	private GridView gridView;
	private List<Map<String, Object>> menus;
	//private QueryNoticeResponse qnResponse;//公告信息
	private LinearLayout aboutlayout;	//关于我们
	private LinearLayout updateLayout;	//版本更新 
	private LinearLayout infoLayout;	//设备信息
	private LinearLayout exitLayout;	//退出程序

	HttpDownloader loader=new HttpDownloader();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menus);
		initView();
		// 首页广告图片显示
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
		images.add(R.drawable.gg1);
		images.add(R.drawable.gg2);
		images.add(R.drawable.gg4);
		imageadapter = new ImageAdapter1(MainMenuActivitys.this,images);
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
		//gridView.setBackgroundResource(R.drawable.edit_text);// 设置背景
		gridView.setNumColumns(3);// 设置每行列数
		gridView.setGravity(Gravity.CENTER);// 位置居中
		gridView.setVerticalSpacing(5);// 垂直间隔
		gridView.setHorizontalSpacing(10);// 水平间隔
	

		loadMenuData();
		SimpleAdapter adapter = new SimpleAdapter(this, menus,R.layout.main_menu_item,
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
					final Dialog builder = new Dialog(MainMenuActivitys.this, R.style.dialog);
					builder.setContentView(R.layout.confirm_dialog);
					TextView ptitle = (TextView) builder.findViewById(R.id.pTitle);
					TextView pMsg = (TextView) builder.findViewById(R.id.pMsg);
					ptitle.setText(MainMenuActivitys.this.getString(R.string.dialog_title_warn));
					pMsg.setText("确定要拔打"+Constants.CUSTOMER_SERVICE_PHONE+"客服电话吗？");
					Button confirm_btn = (Button) builder.findViewById(R.id.confirm_btn);
					Button cancel_btn = (Button) builder.findViewById(R.id.cancel_btn);
					confirm_btn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							builder.dismiss();
							Intent mIntent = new Intent(Intent.ACTION_CALL ,Uri.parse(String.format("tel:%s",Constants.CUSTOMER_SERVICE_PHONE)));
							MainMenuActivitys.this.baseStartActivity(mIntent);
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
					
				}else if(tag.equals("QUERYBALANCE")){
					 //查询余额
					TransActionFactory.getInstance().startAction(MainMenuActivitys.this, new POSQueryBalanceRequest());
			
				} else if (tag.endsWith("SIGN")) {
					//签到
					doSign();
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
    	intent = new Intent(MainMenuActivitys.this, PurchaseActivity.class);
		map.put("intent", intent);
		menus.add(map); 
		 
		
		map = new HashMap<String, Object>();
		map.put("itemText", "消费撤销");
		map.put("itemImage", R.drawable.cxxf_);
		map.put("tag", "cxdd");
    	map.put("count", "");
    	intent= new Intent(MainMenuActivitys.this, CancelActivity.class);
		map.put("intent", intent);
		menus.add(map);
		
		
		map = new HashMap<String, Object>();
		map.put("itemText", "退款");
		map.put("itemImage", R.drawable.jyed_);
		map.put("tag", "tk");
		map.put("count", "");
		intent =  new Intent(MainMenuActivitys.this, RePrintActivity.class);;
		map.put("intent", intent);
		menus.add(map);
				
		map = new HashMap<String, Object>();
		map.put("itemText", "金额查询");
		map.put("itemImage", R.drawable.zhcx_);
		map.put("tag", "QUERYBALANCE");
		map.put("count", "");
		//intent=  new Intent(MainMenuActivitys.this, QueryUserActivity.class);;
		map.put("intent", null);
		menus.add(map);
		

		map = new HashMap<String, Object>();
		map.put("itemText", "签到");
		map.put("itemImage", R.drawable.xftjj_);
		map.put("tag", "SIGN");		
		map.put("intent", null);
		menus.add(map); 
				
		
		map = new HashMap<String, Object>();
		map.put("itemText", "交易明细");
		map.put("itemImage", R.drawable.jymx_);
		map.put("tag", "tk");
		map.put("count", "");
		intent=  new Intent(MainMenuActivitys.this, QueryHistoryActivitys.class);;
		map.put("intent", intent);
		menus.add(map);
				
		map = new HashMap<String, Object>();
		map.put("itemText", "客服电话");
		map.put("itemImage", R.drawable.kfdh_);
		map.put("tag", "kfdh");
		
		map.put("intent",null);
		menus.add(map);   
		

		map = new HashMap<String, Object>();
		map.put("itemText", "新闻中心");
		map.put("itemImage", R.drawable.xwzx_);
		map.put("tag", "ggtz");
		//map.put("news", R.drawable.circle_orange);
    	//map.put("count", "20");
		intent= new Intent(MainMenuActivitys.this,NoticeListActivitys.class);
		map.put("intent", intent);
		menus.add(map);
		
  
		
		map = new HashMap<String, Object>();
		map.put("itemText", "帮助");
		map.put("itemImage", R.drawable.help_title);
		map.put("tag", "xfpoint");		
		intent= new Intent(MainMenuActivitys.this,MoreActivity.class);
		map.put("intent", intent);
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
		       startActivityForResult(mIntent, 0);
			//startActivity(mIntent);
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

	@Override
	public void onClick(View v) {
		
	}


	/**
	 * 签到
	 */
	public void doSign(){
		 new ElecDialog.Builder(MainMenuActivitys.this, Style.SIGN)
			.setConfirmButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface d,int arg1) {
					 
					Dialog dialog = (Dialog)d;
					EditText mEditUserNo = (EditText)dialog.findViewById(R.id.mEditUserNo);
					EditText mEditUserPwd = (EditText)dialog.findViewById(R.id.mEditUserPwd);
					//验证
					if(mEditUserNo.getText().length()<2){
						MessageBox.showWarn(MainMenuActivitys.this, R.string.warn_OperNo_required);
						return;
					}
					if(mEditUserPwd.getText().length()<4){
						MessageBox.showWarn(MainMenuActivitys.this, R.string.warn_OperPwd_required);
						return;
					}
					dialog.dismiss();
					//发起任务
					new SignTask(MainMenuActivitys.this)
						.execute(mEditUserNo.getText().toString(),mEditUserPwd.getText().toString()); 
				}
			})
			.setCancelButtonListener(new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int arg1) {
					dialog.dismiss();
				}
			})
		.show();
	}

	class SignTask extends AsyncTask<Object,Exception,Boolean>{
		private ElecActivity context;
		private ElecProgressDialog mProgressDialog;
		
		public SignTask(ElecActivity context) {
			this.context = context;
		}
		
		public void onPreExecute(){
			mProgressDialog = new ElecProgressDialog(this.context);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			mProgressDialog.setMessage("正在处理签到...");
		}
		
		@Override
		protected Boolean doInBackground(Object... arg0) {
			//获取参数
			String operNo = (String)arg0[0];
			String operPwd = (String)arg0[1];
			
			if(context.getPOS()!=null) {
				TransService service = ServiceFactory.getInstance().getTransService(context.getPOS(), context);
				try {
					return service.sign(operNo, operPwd);
				} catch(Exception ex) {
					this.publishProgress(ex);
				}
			}
			return false;
		}
		
		protected void onProgressUpdate(Exception... args){
			Exception ex = args[0];
			if(ex!=null) {
				this.context.showError(ex.getMessage());
			}
		}
		
		protected void onPostExecute(Boolean result){
			if(mProgressDialog!=null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			
			if(result!=null && result) {
				Toast.makeText(this.context, "签到成功", Toast.LENGTH_SHORT).show();
			}else {
				this.context.showError("签到失败");
			}
		}
	}
}
