package com.example.merchanttool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		WebView webView = (WebView) findViewById(R.id.webView);

		webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setDefaultTextEncodingName("utf-8");// 避免中文乱码
		webView.setScrollBarStyle(0);
		webView.setHorizontalScrollBarEnabled(false);// 水平不显示
		webView.setVerticalScrollBarEnabled(false); // 垂直不显示
		webView.requestFocus();
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setBackgroundColor(Color.TRANSPARENT);// 设置其背景为透明

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setUseWideViewPort(true);// 设置是当前html界面自适应屏幕
		settings.setCacheMode(WebSettings.LOAD_DEFAULT | WebSettings.LOAD_CACHE_ELSE_NETWORK);
		settings.setNeedInitialFocus(false);
		settings.setDomStorageEnabled(true); // 显示全景的问题
		settings.setAllowFileAccess(true);
		settings.setBlockNetworkImage(false);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);
		// settings.setSupportZoom(false);
		// //启用数据库
		// settings.setDatabaseEnabled(true);
		// //设置定位的数据库路径
		// String dir = this.getApplicationContext().getDir("database",
		// this.MODE_PRIVATE).getPath();
		// settings.setGeolocationDatabasePath(dir);
		// 启用地理定位
		settings.setGeolocationEnabled(true);
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onReceivedIcon(WebView view, Bitmap icon) {
				super.onReceivedIcon(view, icon);
			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
				callback.invoke(origin, true, false);
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

			@Override
			public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
				AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
				b.setTitle("Alert");
				b.setMessage(message);
				b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});
				b.setCancelable(false);
				b.create().show();
				return true;
			}

			@Override
			public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
				AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
				b.setTitle("Confirm");
				b.setMessage(message);
				b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				});
				b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				});
				b.create().show();
				return true;
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			// @Override
			// public void onPageFinished(WebView view,String url)
			// {
			// loading.dismiss();
			// }
			// @Override
			// public void onReceivedError(WebView view, int errorCode,String
			// description, String failingUrl) {
			// super.onReceivedError(view, errorCode, description, failingUrl);
			// loading.dismiss();
			// }
		});

		webView.addJavascriptInterface(new Object() {
			@JavascriptInterface
			public void onUseCamera() {
				// use system camera
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			}

		}, "camera");
		webView.addJavascriptInterface(new Object() {

			@JavascriptInterface
			public String read() {
				StringBuffer sb = new StringBuffer();
				try {
					FileInputStream fis = openFileInput(MerContext.DATA_PATH);
					byte[] value = new byte[fis.available()];
					fis.read(value);
					String valueStr = new String(value);
					sb.append(valueStr);
					fis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				// try {
				// InputStream is = getAssets().open(MerContext.DATA_PATH);
				// byte[] value = new byte[is.available()];
				// is.read(value);
				// String valueStr = new String(value);
				// sb.append(valueStr);
				// is.close();
				// } catch (IOException e1) {
				// e1.printStackTrace();
				// }
				// try {
				// BufferedReader br = new BufferedReader(new
				// FileReader(MerContext.DATA_PATH));
				// String line = br.readLine();
				// while (line != null) {
				// sb.append(line);
				// line = br.readLine();
				// }
				// br.close();
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				System.out.println(sb.toString());
				return sb.toString();
			}

			@JavascriptInterface
			public void write(String value) {
				FileOutputStream fos = null;
				try {
					fos = openFileOutput(MerContext.DATA_PATH, MODE_PRIVATE);
					fos.write(value.getBytes());
					fos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
				    if(fos != null){
				    	try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				}
			}

		}, "data");
		webView.loadUrl("file:///android_asset/main.html");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
