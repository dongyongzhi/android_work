package com.example.idcertificationdemo;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jarjar.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

//import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctsi.idcertification.BtReaderClient;
import com.ctsi.idcertification.BtReaderClient.IClientCallBack;
import com.ctsi.idcertification.CloudReaderClient;
import com.ctsi.idcertification.constant.Constant;
import com.example.idcertificationdemo.tool.Des33;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;  
import org.json.JSONObject;  
import org.json.XML;  



public class MainActivity extends Activity {

	private Context mContext;
	private TextView tv_result;
	private Button btn_bt;
	private Button btn_otg;
	private Map resultMap;
	private ExecutorService pool = Executors.newCachedThreadPool();
	private BtReaderClient bt_reader;
	private CloudReaderClient reader;
	private PendingIntent pendingIntent;
	private NfcAdapter nfcAdapter;
	
	
	private TextView tv_name;
	private TextView tv_gender;
	private TextView tv_nation;
	private TextView tv_birth;
	private TextView tv_certAddress;
	private TextView tv_certNumber;
	private TextView tv_certOrg;
	private TextView tv_date;
	private ImageView image_identityPic;
	private boolean isOK=false;

	

	String appSecret_3des = "D34AE719CE3246E40729411452759F86D34AE719CE3246E4"; // appId对应的加密密钥
	String appSecret = "30b5c231a8ea42c09c87f75d22ebc9ea"; // appId对应的加密密钥
	String appId = "1035";
	String timestamp = "";
	String nonce = "jfoiiuylkjljpohi";
	String businessExt = "{\"busiSerial\":\"12345\",\"staffCode\":\"110011\","
			+ "\"channelCode\":\"2001\",\"areaCode\":\"020\",\"teminalType\":\"PC\","
			+ "\"srcSystem\":\"CRM\",\"osType\":\"\",\"browserModel\":\"\","
			+ "\"clientIP\":\"\",\"deviceModel\":\"\",\"deviceSerial\":\"\"}";
	StringBuffer sbData = new StringBuffer();
	String signature = "";
	NfcAdapter.ReaderCallback nfcCallBack;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			String content_decryp = null;
			switch (what) {
			case 0:
				int resultCode = Integer.valueOf(resultMap.get(
						Constant.RESULT_MAP_KEY_FLAG).toString());
				if (resultCode == Constant.RESULT_OK) {
					String content = (String) resultMap
							.get(Constant.RESULT_MAP_KEY_CONTENT);
					try {
						content_decryp = Des33.decode1(content, appSecret_3des);
						updateResultToUI(content_decryp);
						isOK=true;
						btn_otg.setClickable(true);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//tv_result.setText(resultMap.get(Constant.STEP_MAP_KEY_FLAG)
					//		+ "**********" + content_decryp);
				} else {
					tv_result.setText(resultMap.get(Constant.STEP_MAP_KEY_FLAG)
							+ "**********"
							+ "resultFlag:"
							+ String.valueOf(resultCode)
							+ "\n"
							+ "errorMsg: "
							+ ((String) resultMap
									.get(Constant.RESULT_MAP_KEY_ERRORMESSAGE)));
					isOK=false;
					/*
					if(nfcCallBack != null){
						reader.connect(Constant.READER_TYPE_NFC, nfcCallBack);
					}*/
				}
				break; 
			case 1:
				Toast.makeText(MainActivity.this, "连接蓝牙失败", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		findViewByID();
		initData();
		initView();
//		if (nfcAdapter == null) {
//			Toast.makeText(this, "不支持nfc", Toast.LENGTH_LONG).show();
//		} else if (!nfcAdapter.isEnabled()) {
//			Toast.makeText(this, "nfc不可用", Toast.LENGTH_LONG).show();
//
//		}
		initReaderCallback();
	}

	@Override
	protected void onResume() {
		super.onResume();
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		reader.clear();
		bt_reader.clear();

	}

	private void initReaderCallback() {
		try {

			nfcCallBack = new NfcAdapter.ReaderCallback() {
				@Override
				public void onTagDiscovered(final Tag tag) {

				if(!isOK){
					if (tag != null) {
						nfcAdapter
								.disableForegroundDispatch((Activity) mContext);
					}

					pool.execute(new Runnable() {
						@Override
						public void run() {
							signature = getSignature();

							Map resultMap = reader.CloudReadCert(appId,
									timestamp, nonce, businessExt, signature,
									Constant.READER_TYPE_NFC, tag);
							if (resultMap != null) {
								MainActivity.this.resultMap = resultMap;
								handler.sendEmptyMessage(0);
							}
						}
					});
				}
				}
			};
		} catch (NoClassDefFoundError e) {

		}
	}

	private void initData() {
		// TODO Auto-generated method stub

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		reader = new CloudReaderClient(MainActivity.this);
		bt_reader = new BtReaderClient(MainActivity.this);
		bt_reader.setCallBack(new IClientCallBack() {

			@Override
			public void onBtState(boolean bConnected) {
				if (bConnected) {
					pool.execute(new Runnable() {
						@Override
						public void run() {
							signature = getSignature();
							Map resultMap = bt_reader.cloudReadCert(appId,
									timestamp, nonce, businessExt, signature);
							if (resultMap != null) {
								MainActivity.this.resultMap = resultMap;
								handler.sendEmptyMessage(0);
							}
						}
					});

				} else {
					Toast.makeText(mContext, "蓝牙设备已断开！", Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	private String getSignature() {
		// TODO Auto-generated method stub
		timestamp = System.currentTimeMillis() + "";
		Log.i("timestamp", timestamp);
		sbData = new StringBuffer();
		sbData.append(appId).append(appSecret).append(businessExt)
				.append(nonce).append(timestamp);
		return DigestUtils.shaHex(sbData.toString());
	}

	private void findViewByID() {
		// TODO Auto-generated method stub
		btn_bt = (Button) this.findViewById(R.id.btn_bt);
		btn_otg = (Button) this.findViewById(R.id.btn_otg);
		tv_result = (TextView) this.findViewById(R.id.tv_result);
		this.tv_birth = (TextView)this.findViewById(R.id.tv_birthday);
		this.tv_certAddress = (TextView)this.findViewById(R.id.tv_address);
		this.tv_certNumber = (TextView)this.findViewById(R.id.tv_number);
		
		this.tv_nation = (TextView)this.findViewById(R.id.tv_ehtnic);
		this.tv_name = (TextView)this.findViewById(R.id.tv_name);
		this.tv_gender = (TextView)this.findViewById(R.id.tv_sex);
		this.image_identityPic = (ImageView)this.findViewById(R.id.iv_photo);
		this.tv_certOrg = (TextView)this.findViewById(R.id.tv_org);
		this.tv_date = (TextView)this.findViewById(R.id.tv_date);
		//this.tv_effDate = (TextView)this.findViewById(R.id.tv_birthday);
	}

	private void initView() {
		// TODO Auto-generated method stub
		super.onStart();
		btn_bt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				tv_result.setText("");
				bt_reader.disconnectBt();
				Intent serverIntent = new Intent(MainActivity.this,
						DeviceListActivity.class);
				startActivityForResult(serverIntent, 1);

			}

		});
		btn_otg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isOK=false;
				if(nfcCallBack != null){
					reader.connect(Constant.READER_TYPE_NFC, nfcCallBack);
				}
				btn_otg.setClickable(false);
			
				
				/*
				tv_result.setText("");
				pool.execute(new Runnable() {
					@Override
					public void run() {
						signature = getSignature();
						Map resultMap = reader.CloudReadCert(appId, timestamp,
								nonce, businessExt, signature,
								Constant.READER_TYPE_OTG);
						if (resultMap != null) {
							MainActivity.this.resultMap = resultMap;
							handler.sendEmptyMessage(0);
						}
					}

				});*/

			}

		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {

		// 选择蓝牙设备后
		case 1:
			if (resultCode == Activity.RESULT_OK) {

				String blueaddress = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// 设入蓝牙对象
				bt_reader.connectBt(blueaddress);
			}
			break;
		}
	}
	
	private void updateResultToUI(String paramString){

		//String content = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + paramString;
		JSONObject jsonObj = null; 
		String content = null;
		
		try {  
		    jsonObj = XML.toJSONObject(paramString); 
		    content = jsonObj.getString("certificate");
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Gson builder = new Gson();
		IDCard idcard = builder.fromJson(content, IDCard.class);
		this.tv_birth.setText(idcard.bornDay);
		this.tv_certAddress.setText(idcard.certAddress);
		this.tv_certNumber.setText(idcard.certNumber);

		this.tv_gender.setText((idcard.gender.equals("0"))?"女":"男");
		this.tv_name.setText(idcard.partyName);
		this.tv_nation.setText(idcard.nation);
		this.tv_certOrg.setText(idcard.certOrg);
		this.tv_date.setText(idcard.effDate + " - " + idcard.expDate);
		Bitmap pic = StringToPic(idcard.identityPic);
		if(pic != null){
			this.image_identityPic.setImageBitmap(pic);
		}

		//this.tv_certOrg.setText(idcard.certOrg);
		//this.tv_effDate.setText(idcard.effDate);
		//this.tv_expDate.setText(idcard.expDate);
		
		
	}
	
	private Bitmap StringToPic(String picString){
	  {
	    try
	    {
	      byte[] arrayOfByte = Base64.decode(picString, 0);
	      Bitmap localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
	      return localBitmap;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return null;
	  }
	}
}
