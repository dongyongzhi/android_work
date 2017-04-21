package com.ctbri.ui.collection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.ElecActivity;
import com.ctbri.R;
import com.ctbri.biz.ManagerService;
import com.ctbri.biz.ServiceFactory;
import com.ctbri.domain.ElecResponse;
import com.ctbri.domain.ElectronicSignResponse;
import com.ctbri.domain.PayRequest;
import com.ctbri.domain.TransResponse;
import com.ctbri.ui.BaseActivity;
import com.ctbri.utils.ElecLog;
import com.ctbri.widget.ElecProgressDialog;

/**
 * @comment:签名
 * @author:Zhu
 * @Date:2012-11-30
 */
public class SignCardNameActivity extends BaseActivity implements OnClickListener {
	private Button ok_btn, cancel_btn, top_back;
	private TextView tvSign;
	private Bitmap mSignBitmap, resizedBitmap;
	private ImageView ivSign;
	private View ivSignWrap;
	
	private byte[] signBytes; // 电子签名内容
	
	private ManagerService  managerService; 
	private ElecProgressDialog progressDialog;
	private TransResponse resp;
	private PayRequest apkPay;
	private ElectronicSignResponse signResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_cardname);
		setTitle(R.string.title_sign_cardname);

		ok_btn = (Button) findViewById(R.id.ok_btn);
		ok_btn.setOnClickListener(this);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		cancel_btn.setOnClickListener(this);
		top_back = (Button) findViewById(R.id.top_back);
		top_back.setOnClickListener(this);

		tvSign = (TextView) findViewById(R.id.tv_sign);
		tvSign.setOnClickListener(signListener);
		ivSign = (ImageView) findViewById(R.id.iv_sign);
		ivSign.setOnClickListener(signListener);
		
		ivSignWrap= findViewById(R.id.ivSignWrap);

		// 获取交易数据
 		resp =   getIntent().getParcelableExtra(ElecActivity.EXTRA_TRANS_RESPONSE);
		apkPay = getIntent().getParcelableExtra(ElecActivity.EXTRA_APK_PAY_REQUEST);
		
		if (resp != null) {
			((TextView) findViewById(R.id.mTextCard)).setText(resp.getCard());
			((TextView) findViewById(R.id.mTextMoney)).setText(resp.getMoneyString());
		}
	}
	
	/**
	 * pos 绑定成功
	 */
	protected void serviceBindComplete(){
		managerService = ServiceFactory.getInstance().getManagerService();
	}
	/**
	 * 上传签名
	 */
	public ElecResponse onExecAsynService(){
		if(managerService!=null && resp!=null){
			return managerService.electronicSign(resp.getTransNumber(), this.signBytes);
		}
		return null;
	}
	
	/**
	 * 上传成功
	 */
	public void onServiceSuccess(Object obj){
		super.onServiceSuccess(obj);
		
		if(obj==null){
			this.showWarn("上传电子签名失败！");
			return;
		}
		
		//获取posp返回结果
		signResponse = (ElectronicSignResponse)obj;
		if(signResponse.getErrCode()!=0)
			this.showWarn("中心："+signResponse.getErrMsg());
		else{
			startNext();
		}
	}
	
	/**
	 * 开始下一个界面
	 */
	public void startNext(){
		 //成功转至订单详细界面
		Intent intent = new Intent(SignCardNameActivity.this,OrderDetailActivity.class);
		intent.putExtra(EXTRA_TRANS_RESPONSE, (Parcelable)resp); //交易信息
		intent.putExtra(EXTRA_SIGN_DATA, resizedBitmap); //电子签名数据
		intent.putExtra(EXTRA_APK_PAY_REQUEST, apkPay);//外部apk调用数据
		

		if(signResponse!=null) {
			intent.putExtra(EXTRA_DETAIL_PAGEURL, signResponse.getPageUrl());//显示订单详细信息的url
		}
		startActivity(intent);
		this.finish();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
			//check
			if(this.signBytes==null || this.signBytes.length==0){
				this.showWarn("请点击签名！");
				return;
			}
			this.progressDialog = new ElecProgressDialog(this);
			progressDialog.setMessage("正在上传电子签名...");
			progressDialog.setCancelable(false);
			progressDialog.show();
			startAsynService(progressDialog); //开始上传至服务器
			
			break;
		case R.id.cancel_btn:
			startNext();
			finish();
			break;
		case R.id.top_back:
			finish();
			break;
		default:
			break;
		}
	}

	
	/**
	 * 签名操作
	 */
	private OnClickListener signListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			WritePadDialog writeTabletDialog = new WritePadDialog(
					SignCardNameActivity.this, new DialogListener() {
						@Override
						public void refreshActivity(Object object) {

							mSignBitmap = (Bitmap) object;
							// 暂时不需要保存,直接 上传至服务器
							createFile();
							
							ivSign.setImageBitmap(resizedBitmap);
							tvSign.setVisibility(View.GONE);
						}
					});
			writeTabletDialog.show();
		}
	};

	/**
	 * 创建手写签名文件
	 * 
	 * @return
	 */
	public void createFile() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		// 获取这个图片的宽和高
		int width = mSignBitmap.getWidth();
		int height = mSignBitmap.getHeight();
		ElecLog.d(getClass(), String.format("return sign bitmap w:%d h:%d",width,height));
		
		// 定义预转换成的图片的宽度和高度
		int newWidth = ivSignWrap.getWidth();
		int newHeight = ivSignWrap.getHeight();
		tvSign.setVisibility(View.INVISIBLE);
		
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		float scale = 0;
		if(scaleWidth >= 1  &&  scaleHeight<1)
			scale = scaleHeight;
		else if(scaleWidth<1 && scaleHeight>=1)
			scale = scaleWidth;
		else
			scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
				
		
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scale, scale);
		// 旋转图片 动作
		// matrix.postRotate(45);
		// 创建新的图片
		resizedBitmap = Bitmap.createBitmap(mSignBitmap, 0, 0, width, height,matrix, true);
		resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		signBytes = baos.toByteArray(); //文件签名内容
		try {
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
