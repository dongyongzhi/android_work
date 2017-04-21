package com.hftcom.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hftcom.R;

/**
 * comment:WebView操作父类
 * @author:ZhangYan 
 * Date:2012-8-20
 */
public class BaseWebView extends Activity
{
	protected String url;
	protected boolean flag = false;// 是否中断
	protected WebView webView;
	protected ProgressDialog progressDialog = null;
	private int timeout = 0;
	protected LinearLayout publicloading;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	protected void showProg()
	{
		publicloading.setVisibility(View.VISIBLE);
	}

	protected void doInitView()
	{
		publicloading = (LinearLayout) findViewById(R.id.publicloading);
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		// webView.getSettings().setBuiltInZoomControls(true);// 显示放大缩小controler
		// webView.getSettings().setSupportZoom(true);// 可以缩放
		webView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(this.getUrl());

		webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setWebChromeClient(new WebChromeClient()
		{
			public void onProgressChanged(WebView view, int progress)
			{

				if (publicloading.getVisibility() == View.GONE)
				{
					showProg();
				}

				BaseWebView.this.setProgress(progress * 100);
				Log.v("webView执行时间:==========", String.valueOf(progress));
				if (progress == 100)
				{
					//BaseWebView.this.setTitle(R.string.app_name);
					publicloading.setVisibility(View.GONE);
				}
				timeout += 1;
				Log.v("webView连接操时测试:==========", String.valueOf(timeout));
			}
		});
		webView.setWebViewClient(new WebViewClient()
		{
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
			{
				flag = true;
			}
		});
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack())
		{
			if (flag)
			{
				progressDialog.dismiss();
				this.finish();
			} else
			{
				webView.goBack();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 设置标题 
	 */
   public void setTitle(int resid){
	   TextView title = (TextView)this.findViewById(R.id.activityTitle);
	   if(title!=null){
		   title.setText(resid);
	   } 
   }
   
}
