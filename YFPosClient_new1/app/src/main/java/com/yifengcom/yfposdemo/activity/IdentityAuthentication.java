package com.yifengcom.yfposdemo.activity;

import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctsi.idcertification.constant.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.yifengcom.yfposdemo.Des33;
import com.yifengcom.yfposdemo.IDCard;
import com.yifengcom.yfposdemo.R;
import com.yifengcom.yfposdemo.YFApp;

import org.json.XML;

public class IdentityAuthentication extends Activity {

	private TextView tv_result;
	private TextView tv_name;
	private TextView tv_gender;
	private TextView tv_nation;
	private TextView tv_birth;
	private TextView tv_certAddress;
	private TextView tv_certNumber;
	private TextView tv_certOrg;
	private TextView tv_date;
	private ImageView image_identityPic;
	// private static final String TAG = "IdentityAuthentication";
	String appSecret_3des = "D34AE719CE3246E40729411452759F86D34AE719CE3246E4"; // appId对应的加密密钥
	private ProgressDialog progressDialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			String content_decryp = null;
			switch (what) {
			case 0:
				@SuppressWarnings("rawtypes")
				Map resultMap = (Map) msg.obj;
				int resultCode = Integer.valueOf(resultMap.get(Constant.RESULT_MAP_KEY_FLAG).toString());
				if (resultCode == Constant.RESULT_OK) {
					String content = (String) resultMap.get(Constant.RESULT_MAP_KEY_CONTENT);
					try {
						content_decryp = Des33.decode1(content, appSecret_3des);
						updateResultToUI(content_decryp);
						ReadComplete();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					tv_result.setText(resultMap.get(Constant.STEP_MAP_KEY_FLAG) + "**********" + "resultFlag:"
							+ String.valueOf(resultCode) + "\n" + "errorMsg: "
							+ ((String) resultMap.get(Constant.RESULT_MAP_KEY_ERRORMESSAGE)));
				}
				break;
			case 1:
				Toast.makeText(IdentityAuthentication.this, "连接蓝牙失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		}

	};

	private void ReadComplete() {
		if (progressDialog != null) {
			progressDialog.cancel();
			progressDialog = null;
		}
		YFApp.getApp().getIdentityActivity().readComplete();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_identi);
		findViewByID();//
		progressDialog = new ProgressDialog(this);
		try {
			YFApp.getApp().iService.cancel();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (progressDialog != null) {
			progressDialog.setMessage("正在读身份证...");
			// progressDialog.setCancelable(false);
			progressDialog.show();
		}
		YFApp.getApp().getIdentityActivity().ReadNfcIdentityAuthentication(this, handler);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			YFApp.getApp().iService.cancel();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		YFApp.getApp().getIdentityActivity().readComplete();

	}

	private void findViewByID() {
	
		tv_result = (TextView) this.findViewById(R.id.tv_result);
		this.tv_birth = (TextView) this.findViewById(R.id.tv_birthday);
		this.tv_certAddress = (TextView) this.findViewById(R.id.tv_address);
		this.tv_certNumber = (TextView) this.findViewById(R.id.tv_number);
		this.tv_nation = (TextView) this.findViewById(R.id.tv_ehtnic);
		this.tv_name = (TextView) this.findViewById(R.id.tv_name);
		this.tv_gender = (TextView) this.findViewById(R.id.tv_sex);
		this.image_identityPic = (ImageView) this.findViewById(R.id.iv_photo);
		this.tv_certOrg = (TextView) this.findViewById(R.id.tv_org);
		this.tv_date = (TextView) this.findViewById(R.id.tv_date);
		
	}

	private void updateResultToUI(String paramString) {
		
		JSONObject jsonObj = null;
		String content = null;

		try {
			jsonObj = XML.toJSONObject(paramString);
			content = jsonObj.getString("certificate");
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Gson builder = new Gson();
		IDCard idcard = builder.fromJson(content, IDCard.class);
		this.tv_birth.setText(idcard.bornDay);
		this.tv_certAddress.setText(idcard.certAddress);
		this.tv_certNumber.setText(idcard.certNumber);

		this.tv_gender.setText((idcard.gender.equals("0")) ? "女" : "男");
		this.tv_name.setText(idcard.partyName);
		this.tv_nation.setText(idcard.nation);
		this.tv_certOrg.setText(idcard.certOrg);
		this.tv_date.setText(idcard.effDate + " - " + idcard.expDate);
		Bitmap pic = StringToPic(idcard.identityPic);
		if (pic != null) {
			this.image_identityPic.setImageBitmap(pic);
		}
		
		YFApp.getApp().getIdentityActivity().saveBitmapFile(pic);
	}

	private Bitmap StringToPic(String picString) {
		{
			try {
				byte[] arrayOfByte = Base64.decode(picString, 0);
				Bitmap localBitmap = BitmapFactory.decodeByteArray(arrayOfByte, 0, arrayOfByte.length);
				return localBitmap;
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			return null;
		}
	}

}
