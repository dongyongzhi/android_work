package com.hftcom.ui.query;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.hftcom.R;
import com.hftcom.YFApp;
import com.hftcom.adapter.CommonAdapter;
import com.hftcom.db.DBUtil;
import com.hftcom.ui.BaseActivity;
import com.hftcom.utils.DialogUtil;
import com.hftcom.utils.MessageBox;
import com.yifengcom.yfpos.bank.mina.EposMessageType;
import com.yifengcom.yfpos.callback.CallBackListener;
import com.yifengcom.yfpos.model.PrintType;
import com.yifengcom.yfpos.utils.ByteUtils;

/**
 * @Description: 交易记录
 * @date 2016-6-28
 */
public class TradeListActivity extends BaseActivity implements OnScrollListener {
	public DialogUtil dialogUtil;
	private ListView listView;
	private EditText edtNum;
	Button backBtn,btnLast,btnSearch;
	LinearLayout queryTrans;
	protected List<Map<String, String>> SURPERDATA;
	protected List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
	private int pageNum = 0, lastItem = 0;;
	private CommonAdapter adapter;
	private boolean isLoading = true;// 标志正在加载数据
	private String keyWord = ""; //搜索流水号
	
	private String currentType = ""; //当前交易类型

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trade_list_activity);
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(false);
		
		edtNum = (EditText) findViewById(R.id.edtNum);
		backBtn = (Button) findViewById(R.id.top_back);
		backBtn.setOnClickListener(onClick);
		
		queryTrans = (LinearLayout) findViewById(R.id.queryTrans);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(onClick);
		
		btnLast = (Button) findViewById(R.id.btnLast);
		btnLast.setOnClickListener(onClick);

		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				reset();
				Map<String, Object> map = list.get(position);
				String num = map.get("serial_no").toString();
				final String print_data = map.get("print_data") != null ? map.get("print_data").toString() : "";
				currentType = map.get("card_type") != null ? map.get("card_type").toString() : "";
				
				MessageBox.showConfirmBox(TradeListActivity.this, "是否重新打印流水号："+num+"交易凭证？", "确定", "取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						if(!print_data.equals("")){
							String[] data = print_data.split(",");
							convert(data);
							onPrint(arrayList1, false);
						}
					}
				}, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				return true;
			}
		});
		listView.setOnScrollListener(this);
		dialogUtil = new DialogUtil(this);
		this.setTitle(R.string.title_query_order_list);

		loadData();
	}
	
	private OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.top_back:
				finish();
				break;
			case R.id.btnLast:
				btnLast.setEnabled(false);
				printLast();
				break;
			case R.id.btnSearch:
				if(isLoading){
					keyWord = edtNum.getText().toString().trim();
					if(!keyWord.equals("") && keyWord.length()!=6){
						showToast("请输入6位交易流水号");
						return;
					}
					reset();
					pageNum = 0;
					loadData();
				}
				break;
			}
		}
	};

	private void loadData() {
		if (pageNum == 0) {
			list.clear();
			listView.removeFooterView(commonUtil.loadingLayout);
			listView.addFooterView(commonUtil.addFootBar());
			adapter = new CommonAdapter(this, list, R.layout.trade_list_item,
					new String[] { "serial_no", "m_show", "send_time",
							"result_msg", "mac" }, new int[] { R.id.text1,
							R.id.text2, R.id.text3, R.id.text4, R.id.tvMac },
					listView);
			listView.setAdapter(adapter);
		}
		if (isLoading) {
			isLoading = false;
			new Thread(mRunnable).start();
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		lastItem = arg1 + arg2 - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		if (lastItem == adapter.count
				&& arg1 == OnScrollListener.SCROLL_STATE_IDLE) {
			loadData();
		}
	}

	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Thread.sleep(100);
				String selection = null;
				String[] selectionArgs = null;
				if(!keyWord.equals("")){
					selection = "serial_no=?";
					selectionArgs = new String[]{keyWord}; 
				}
				SURPERDATA = DBUtil.getInstance(getApplicationContext())
						.listCache(selection, selectionArgs, getLimit());
				Message msg = new Message();
				msg.what = 0;
				mHandler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 0) {
				addData();
			}
			isLoading = true;
		}
	};

	private void addData() {
		pageNum++;
		if (SURPERDATA.size() > 0) {
			if (SURPERDATA.size() < 10)
				listView.removeFooterView(commonUtil.loadingLayout);
			for (Map map : SURPERDATA) {
				try {
					long money;
					String payMoney = map.get("money").toString();
					if (payMoney.indexOf(".") < 0) {
						money = Long.parseLong(payMoney);
					} else {
						money = ((Float) (Float.parseFloat(payMoney) * 100)).longValue();
					}
					map.put("m_show", String.format("%,.2f 元", money / 100.00));
				} catch (Exception e) {
					e.printStackTrace();
				}
				list.add(map);
			}
		} else {
			dialogUtil.showToast("未查询到数据");
			listView.removeFooterView(commonUtil.loadingLayout);
		}
		adapter.count = list.size();
		adapter.notifyDataSetChanged();
	}

	private String getLimit() {
		int nextNum = pageNum * 10;
		return nextNum + ",10";
	}

	private void printLast(){
		Map<String, String> map = DBUtil.getInstance(this).getLastOrder(null,
				null, null, null, "[_id] asc", null);
		if(map.size() != 0){
			String print_data = map.get("print_data") != null ? map.get("print_data") : "";
			currentType = map.get("card_type") != null ? map.get("card_type").toString() : "";
			if(!print_data.equals("")){
				String[] data = print_data.split(",");
				convert(data);
				onPrint(arrayList1, false);
			}
		}else{
			showToast("无交易记录");
		}
		btnLast.setEnabled(true);
	}
	
	private int printCount = 2; //打印次数
	private ArrayList<String> arrayList1;
	private ArrayList<String> arrayList2;
	
	private void convert(String[] list){
		if(list != null){
			arrayList1 = new ArrayList<String>();
			arrayList2 = new ArrayList<String>();
			for (int i = 0; i < list.length; i++) {
				byte[] date = ByteUtils.hexToByte(list[i]);
				String str;
				try {
					str = new String(date, "gb2312");
					if(str.contains("FFFF")){
						if(currentType.equals("2")){
							str = str.replace("FFFF", "微信支付");
						}else{
							str = str.replace("FFFF", "银行卡消费");
						}
					}
					if(str.contains("中国银联交易凭条")){
						str = "     " + str;
					}
					if(str.contains("商户存根")){
						str = "      " + str;
					}
				} catch (UnsupportedEncodingException e) {
					str = "";
				}
				arrayList1.add(str);
				if(str.contains("持卡人签字")){
					continue;
				}
				if(str.trim().equals("")){
					continue;
				}
				if(str.contains("商户存根")){
					arrayList2.add("     " + str.replace(" ", "").replace("商户", "持卡人"));
					continue;
				}
				arrayList2.add(str);
			}
			try {
				String str = new String("     ------重打印凭证------".getBytes("gb2312"), "gb2312");
				arrayList1.add(str);
				arrayList2.add(str);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 打印数据
	 * @param body 打印数据
	 * @param isWait 
	 */
	private void onPrint(ArrayList<String> strs,boolean isWait){
		if(strs == null || strs.size() == 0){
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"打印数据为空").sendToTarget();
			return;
		}
		try {
			byte[] data = getPrintBody(strs,false);
			if(isWait){
				Thread.sleep(2000);
			}
			progressDialog.setMessage("正在打印...");
			if(progressDialog != null && !progressDialog.isShowing()){
				progressDialog.show();
			}
			YFApp.getApp().iService.onPrint(data, mCallBack);
		} catch (RemoteException e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"打印服务异常").sendToTarget();
		} catch (Exception e) {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,"打印异常").sendToTarget();
		}
	}
	
	
	private Handler errorHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
			case EposMessageType.REMOTE_ERROR:
				closeDialog();
				String m = msg.obj + "";
				MessageBox.showError(TradeListActivity.this, m,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				break;
			case EposMessageType.PRINTSUCCESS:
				int type = (Integer) msg.obj;
				String text = PrintType.convert(type).getMesssage();
				if(type == 0){
					printCount --;
					if(printCount == 1){
						onPrint(arrayList2,true);
					}else{
						showPrintDialog(text,type);
					}
				}else{
					showPrintDialog(text,type);
				}
				break;
			case EposMessageType.RESULT_DATA:
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 弹出打印提示框
	 * @param text 显示内容
	 */
	private void showPrintDialog(String text,int type){
		closeDialog();
		if(type == 1){
			MessageBox.showConfirmBox(TradeListActivity.this, text, "确定", "打印数据", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			}, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if(printCount == 2){
						onPrint(arrayList1, false);
					}else if(printCount == 1){
						onPrint(arrayList2, true);
					}
				}
			});
		}else{
			MessageBox.showError(TradeListActivity.this, text,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.dismiss();
						}
					});
		}
	}
	
	
	CallBackListener mCallBack = new CallBackListener() {

		@Override
		public void onError(final int code, final String messsage)
				throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR,
					"操作失败，返回码：" + code + " 信息:" + messsage).sendToTarget();
		}

		@Override
		public void onTimeout() throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.REMOTE_ERROR, "超时")
					.sendToTarget();
		}

		@Override
		public void onResultSuccess(int ntype) throws RemoteException {
			errorHandler.obtainMessage(EposMessageType.PRINTSUCCESS, ntype)
					.sendToTarget();
		}

	};
	
	private void reset(){
		printCount = 2;
		if(arrayList1!= null)
			arrayList1.clear();
		if(arrayList2 != null)
			arrayList2.clear();
	}
}
