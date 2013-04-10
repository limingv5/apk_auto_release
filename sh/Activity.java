package com.nmi.__BContapk__;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;

import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.KeyEvent;
import android.os.Handler;
import android.os.Message;
import android.graphics.Bitmap;

import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;

import android.widget.EditText;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.BasicHttpParams;

public class __BContActivity__Activity extends Activity {
	private String HOMEPAGE = "__BContURL__";
	private WebView wv;
	private ProgressDialog pd;
	private Timer timer;
	private Thread thread;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case -1	: wv.loadUrl("file:///android_asset/error.html"); break;
				case -2	: wv.loadUrl("file:///android_asset/about.html"); break;
				case 0	: onExit(); break;
				case 1	: pd.show(); break;
				case 2	: pd.hide(); break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		pd = new ProgressDialog(__BContActivity__Activity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("正在努力加载中...");
        
		setContentView(R.layout.main);
		
		wv = (WebView)findViewById(R.id.wv);
		
		wv.getSettings().setRenderPriority(RenderPriority.HIGH);
		wv.getSettings().setSupportZoom(true);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		wv.getSettings().setPluginsEnabled(true);
		wv.getSettings().setAllowFileAccess(true);
		wv.getSettings().setUseWideViewPort(true);
		wv.getSettings().setLoadWithOverviewMode(true);
		wv.getSettings().setDefaultTextEncodingName("utf-8");
		
		wv.requestFocus(View.FOCUS_DOWN);

		wv.setVerticalScrollBarEnabled(false);
		wv.setHorizontalScrollBarEnabled(false);
		wv.setInitialScale(100);
		
		wv.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
		
			}
			
			@Override
			public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle("提示对话框").setMessage(message).setPositiveButton("确定", null);
				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						return true;
					}
				});
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				result.confirm();
				return true;
			}
			
			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle("确认对话框").setMessage(message)
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				})
				.setNeutralButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				});
				builder.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						result.cancel();
					}
				});
				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						return true;
					}
				});
				builder.setCancelable(false);
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
			
			@Override
			public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
				builder.setTitle("输入对话框").setMessage(message);
				final EditText et = new EditText(view.getContext());
				et.setSingleLine();
				et.setText(defaultValue);
				builder.setView(et);
				builder
				.setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm(et.getText().toString());
					}
				})
				.setNeutralButton("取消", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				});

				builder.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						return true;
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});
		
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
				if (url.endsWith("/mp4")) {
					Intent it = new Intent(Intent.ACTION_VIEW);
					it.setDataAndType(Uri.parse(url), "video/mp4");
					startActivity(it);
				}
		        else {
					view.loadUrl(url);
				}
				return true;
			}
			
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				handler.sendEmptyMessage(1);
				
				timer = new Timer();
				TimerTask tt = new TimerTask() {
					@Override
					public void run() {
						if (__BContActivity__Activity.this.wv.getProgress() < 100) {
							handler.sendEmptyMessage(2);
							timer.cancel();
							timer.purge();
						}
					}
				};
				timer.schedule(tt, 5000, 1);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				handler.sendEmptyMessage(2);
				
				timer.cancel();
                timer.purge();
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) { 
				super.onReceivedError(view, errorCode, description, failingUrl);
				handler.sendEmptyMessage(-1);
			}
		});
		
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessage(1);
				
				try {
					BasicHttpParams httpParams = new BasicHttpParams();
					int timeout = 12*1000;
					HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
					HttpConnectionParams.setSoTimeout(httpParams, timeout);
					HttpClient client = new DefaultHttpClient(httpParams);
					
					HttpHead head = new HttpHead(HOMEPAGE);
					HttpResponse resp = client.execute(head);
					
					if (resp.getStatusLine().getStatusCode() >= 400) {
						handler.sendEmptyMessage(-1);
					}
					else {
						wv.loadUrl(HOMEPAGE);
					}
				}
				catch (IOException e) {
					handler.sendEmptyMessage(-1);
				}
			}
		});
		
		thread.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (wv.canGoBack()) {
					wv.goBack();
				}
				else {
					onExit();
				}
				break;
			case KeyEvent.KEYCODE_MENU:
				startActivityForResult(new Intent(__BContActivity__Activity.this, PopupMenu.class), 0);
				break;
		}
		return true;//super.onKeyDown(keyCode, event);
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		handler.sendEmptyMessage(resultCode);
	}
	
	private void onExit() {
		AlertDialog.Builder ad=new AlertDialog.Builder(__BContActivity__Activity.this);
		ad.setTitle("退出确认");
		ad.setMessage("你真的要退出吗？");
		ad.setPositiveButton("是", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				thread.interrupt();
				__BContActivity__Activity.this.finish();
			}
		});
		ad.setNegativeButton("否",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
			
			}
		});
		ad.show();
	}
}
