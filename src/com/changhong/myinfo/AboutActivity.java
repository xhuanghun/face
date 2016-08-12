package com.changhong.myinfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.changhong.faceattendance.R;
import com.changhong.utils.ConnectDialog;
import com.changhong.utils.MyConfig;
import com.changhong.utils.MyImageTextButton2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends Activity{
	private final static String TAG = "AboutActivity";
	long newVerCode = -1;           //从服务器得到的版本信息
	private WebView mWebView;
	private ImageView imageView;
	private TextView textView;
	private static final String APK_ADDR = "http://10.3.41.92/FaceAttendance.apk"; 
	private final static int DOWNFILE_SUCCESS = 97;
	private final static int DOWNFILE_ERROR = 98;
	private final static int NETWORK_ERROR = 99;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.help_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_help);
		
	    textView = (TextView)findViewById(R.id.title_help_text);
		
	    imageView = (ImageView)findViewById(R.id.rethelp);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
//		ImageView aboutImageView = (ImageView)findViewById(R.id.about_icon);
		
		MyImageTextButton2 function_btn = (MyImageTextButton2)findViewById(R.id.function_btn);
		function_btn.setText("功能介绍");
		function_btn.setTextSize(18);
		function_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imageView.setVisibility(View.INVISIBLE);
				textView.setText("功能介绍");
				mWebView.setVisibility(View.VISIBLE);
				//mWebView.loadUrl("file:///android_asset/about_example.html");
				mWebView.loadUrl("http://120.25.121.173:20000/attendance.web/faq/faq/index.html");
			}
		});
		
		MyImageTextButton2 feedback_btn = (MyImageTextButton2)findViewById(R.id.feedback0_btn);
		feedback_btn.setText("意见反馈");
		feedback_btn.setTextSize(18);
		feedback_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartFeedbackActivity();
			}
		});
		
		MyImageTextButton2 update_btn = (MyImageTextButton2)findViewById(R.id.update_btn);
		update_btn.setText("检测新版本");
		update_btn.setTextSize(18);
		update_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ConnectDialog.showDialog(AboutActivity.this, "正在检查，请稍后！");
				new downloadThread(APK_ADDR).start();
			}
		});
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setVisibility(View.INVISIBLE);
	}
	
	public Handler updateHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ConnectDialog.dismiss();
			switch (msg.what) {
			case 0:
				
				break;
			case DOWNFILE_SUCCESS:
//				Toast.makeText(getApplicationContext(), 
//						"下载成功", 2000).show();
				int verCode = getVerCode(getApplicationContext());
				newVerCode = getApkVerCode();
				if(newVerCode > verCode){
					updatedialog();
				}else{
					MyConfig.DeleteApk();
					Toast.makeText(AboutActivity.this, "当前已是最新版本！", 2000).show();
				}
				break;
			case DOWNFILE_ERROR:
				Toast.makeText(getApplicationContext(), 
						"更新失败", Toast.LENGTH_LONG).show();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};
	
	private void StartFeedbackActivity() {
		Intent intent = new Intent(getApplicationContext(),FeedbackActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private int getApkVerCode() {
		int verCode = -1;
		String apkPath = Environment.getExternalStorageDirectory().getPath() + "/FaceAttendance.apk";
		Log.i(TAG, "apk path:"+apkPath);
		PackageInfo info = getPackageManager().
				getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if(info != null) {
			verCode = info.versionCode;
			Log.i(TAG, "apk vercode:"+verCode);
		}
		return verCode;
	}
	
	/**
	 * 清除定时提醒标志
	 * @return true or false
	 */
	private boolean clearAlarmFlag() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor ed = prefs.edit();
		ed.remove("startId");
		return ed.commit();
	}
	
	/**
	 * 获取软件版本号
	 * @param context
	 * @return
	 */
	public  int getVerCode(Context context) {
        int verCode = -1;
        try {
        	//注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分
            verCode = context.getPackageManager().getPackageInfo(
            		"com.changhong.faceattendance", 0).versionCode;
            Log.i(TAG,"verCode:"+verCode);
            
        } catch (NameNotFoundException e) {
        	Log.e("msg",e.getMessage());
        }
        return verCode;
    }
	
	/**
	    * 获取版本名称
	    * @param context
	    * @return
	    */
	public  String getVerName(Context context) {
		String verName = "";
	    try {
	        verName = context.getPackageManager().getPackageInfo(
	      		"com.changhong.faceattendance", 0).versionName;
	        System.out.println("verName----->" + verName);
	    } catch (NameNotFoundException e) {
	    	Log.e("msg",e.getMessage());
	    }
	    return verName;   
	}
	    
	    
	/**
	 * 安装程序
	 */
	private void update() {
	    Intent intent = new Intent(Intent.ACTION_VIEW);
	    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "FaceAttendance.apk")),
	            "application/vnd.android.package-archive");
	    startActivity(intent);
	}
	    
	/**
	 * 版本更新对话框
	 */
	public void updatedialog() {
	 	Dialog dialog = new AlertDialog.Builder(AboutActivity.this).setTitle("人脸考勤版本更新").
	 		setPositiveButton("下次再说", 
				new DialogInterface.OnClickListener() {
						
				@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						MyConfig.DeleteApk();
						dialog.dismiss();
					}
				}).
			setNegativeButton("立马更新",
				new DialogInterface.OnClickListener() {
						
				@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						clearAlarmFlag();
						update();
					}
				}).create();
	 	dialog.setCancelable(false);
		dialog.show();
	}
	    
	/**
	 * 下载APK文件
	 */
	class downloadThread extends Thread {
		private String url;
			
		public downloadThread(String url) {
			this.url = url;
		}
			
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
				
			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);

			try {
				//MyConfig.SetNetworkTimeout(httpClient, 5000,10000);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					InputStream input = httpResponse.getEntity().getContent();
					FileOutputStream output = null;
					if(input != null){
						File file = new File(  
								Environment.getExternalStorageDirectory(),  
		                           "FaceAttendance.apk"); 
						output = new FileOutputStream(file);
						byte b[] = new byte[1024];
						int j = 0;
						while((j=input.read(b)) != -1) {
							output.write(b, 0, j);
						}
					}
					output.flush();
					output.close();
					updateHandler.sendMessage(Message.obtain(updateHandler,DOWNFILE_SUCCESS));
					//Toast.makeText(getApplicationContext(), "下载完成", 2000);
				} else {
					// TODO:notify register fail
					//Toast.makeText(getApplicationContext(), "下载失败", 2000);
					updateHandler.sendMessage(Message.obtain(updateHandler,DOWNFILE_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				updateHandler.sendMessage(Message.obtain(updateHandler,DOWNFILE_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				updateHandler.sendMessage(Message.obtain(updateHandler,DOWNFILE_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(mWebView.getVisibility() == View.VISIBLE) {
				mWebView.setVisibility(View.INVISIBLE);
				imageView.setVisibility(View.VISIBLE);
				textView.setText("关于");
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
