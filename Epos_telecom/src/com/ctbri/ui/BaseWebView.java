package com.ctbri.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.ctbri.R;

/**
 * comment:WebView操作父类
 * @author:ZhangYan 
 * Date:2012-8-20
 */
public class BaseWebView extends BaseActivity
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
	}

	protected void showProg()
	{
		// progressDialog = ProgressDialog.show(this, "请稍等...","数据加载中...",true);
		/*progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("请稍等...");
		progressDialog.setMessage("数据加载,请稍后...");
		progressDialog.setIndeterminate(true);// 设置进度条是否为不明确
		progressDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
		progressDialog.show();*/

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
					BaseWebView.this.setTitle(R.string.app_name);
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
}
