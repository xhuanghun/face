package com.changhong.welcome;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.checkin.CheckinActivity;
import com.changhong.chni.CHFaceLib;
import com.changhong.faceattendance.R;
import com.changhong.guide.GuideActivity;
import com.changhong.login.LoginActivity;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class myWelcomeActivity extends Activity implements View.OnClickListener, Notify{
	private final static String TAG = "myWelcomeActivity";
	private static final int NETWORK_ERROR = 99;
	private Button btnStart = null;
	private SharedPreferences mPrefs;
	private String uid;
	private String pswd;
	private String comp_code;
	private SharedPreferences mStartId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome_layout);
		
		mStartId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String startId = mStartId.getString("startId", "");
		if(startId.isEmpty() || startId.compareTo("first")!=0) {
			Log.i(TAG, "设置闹钟");
			setAlarm();
			mStartId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = mStartId.edit();
			editor.putString("startId", "first");
			editor.commit();
		}
		
		MyConfig.DeleteApk();
		/*try {
			MyConfig.DeleteApk();
			LoadFacelibrary();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		btnStart = (Button)this.findViewById(R.id.start_button);
		btnStart.setVisibility(View.INVISIBLE);
		btnStart.setOnClickListener(this);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		comp_code = mPrefs.getString("owner_company", "");
		uid = mPrefs.getString("owner_id", "");
		pswd = mPrefs.getString("owner_pwd", "");
		if(TextUtils.isEmpty(uid) || TextUtils.isEmpty(pswd)) {
			new Thread(new ThreadShow()).start();
		} else {
			Log.i(TAG, "uid:"+uid+" pswd:"+pswd);
			new loginThread(this,comp_code, uid, pswd).start();
			
		}
//		new Thread(new ThreadShow()).start(); //test
	}
	
	private boolean FeatureFileExsit() {
		String fileDir = getApplicationContext().getFilesDir().getPath();
		fileDir += CHFaceLib.defaultConfigFile;
		File file = new File(fileDir);
		boolean mFeaturefile = false;
		if(!file.exists()) {
			Log.i(TAG, "no featurefile");
			mFeaturefile = false;
		} else {
			boolean register_done = mPrefs.getBoolean("register_done", true);
			if(register_done) {
				Editor editor = mPrefs.edit();
				editor.putBoolean("register_done", false);
				editor.commit();
				mFeaturefile = false;
				return mFeaturefile;
			}
			byte[] name;
			int size;
			int[] ids = new int[10];
			size = CHFaceLib.nativeGetAllUserIDs(ids);
			name = CHFaceLib.GetNameById(0);
			try {
				String nameString = new String(name, "UTF-8");
				Log.i(TAG, size+" ids "+ids[0]+" words:"+nameString+" id:"+uid);
				if(nameString.compareTo(uid) != 0) {
					mFeaturefile = false;
				} else {
					Log.i(TAG, "feature file path:"+fileDir);
					mFeaturefile = true;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Log.i(TAG, "feature file path:"+fileDir);
//			mFeaturefile = true;
		}
		return mFeaturefile;
	}
	
	private void StartGuideActivity() {
		Intent intent = new Intent(this, GuideActivity.class);
		startActivity(intent);
	}
	
	private void StartLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	private void StartCheckinActivity() {
		Intent intent = new Intent(getApplicationContext(), CheckinActivity.class);
		startActivity(intent);
	}
	
	private Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 0:
				StartCheckinActivity();
				break;
			case NETWORK_ERROR:
				Log.i(TAG, "NETWORK_ERROR");
				StartLoginActivity();
				break;
			default:
				break;
			}
		};
	};
	
	class ThreadShow implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
				//test
				StartGuideActivity();
//				StartLoginActivity();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
//	class LoginThreadShow implements Runnable {
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			try {
//				Thread.sleep(1000);
//				StartLoginActivity();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	class loginThread extends Thread {
		private Notify notify;
		private String action;
		private String comp_code;
		private String id;
		private String pwd;
		
		public loginThread(Notify notify, String comp_code, String id, String pwd) {
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
			this.pwd = pwd;
			action = "login";
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "user_login_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("pwd", new StringBody(this.pwd));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			HttpResponse httpResponse = null;
			try {
				Log.i(TAG, "http client execute");
				MyConfig.SetNetworkTimeout(httpClient, 5000, 5000);
				httpResponse = httpClient.execute(httpPost);
				Log.e(TAG, httpResponse.getStatusLine().getStatusCode() + "");
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					//loginHandler.sendMessage(Message.obtain(loginHandler, START_CHECKIN));
					notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					loginHandler.sendMessage(Message.obtain(loginHandler, 0));
				} else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 428){
					// TODO:notify login fail
					//loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					//notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
				} else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 429){
					// TODO:notify login fail
					//loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					//notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
				}else{
					loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				loginHandler.sendMessage(Message.obtain(loginHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.start_button:
			// TODO:start face attendance activity
			Log.i(TAG,"start");
			//StartLoginActivity();
//			this.finish();
			break;
		default:
			break;
		}
	}
	
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "finish");
		this.finish();
	}
    
    /**
     * 设置闹钟
     */
    private void setAlarm(){
    	Intent intent = new Intent();
    	intent.setAction("android.intent.action.startalarm");
    	PendingIntent sendIntent = PendingIntent.getBroadcast(myWelcomeActivity.this, 0, intent, 0);
    	AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
    	//manager.cancel(sendIntent);
    	manager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTime(28,8), 24*60*60*1000, sendIntent);
    	intent.setAction("android.intent.action.afternoonalarm");
    	PendingIntent sendIntent2 = PendingIntent.getBroadcast(myWelcomeActivity.this, 1, intent, 0);
    	manager.setRepeating(AlarmManager.RTC_WAKEUP,
                getTime(30,17), 24*60*60*1000, sendIntent2);
    }
    
    private long getTime(int min , int hour){
		Calendar calendar = Calendar.getInstance();
		System.out.println("calendar----->" + calendar);
		calendar.setTimeInMillis(System.currentTimeMillis());
		// 这里时区需要设置一下，不然会有8个小时的时间差
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long selectTime = calendar.getTimeInMillis();
		System.out.println("selecttime----->" + selectTime);
		long systemTime = System.currentTimeMillis();
		System.out.println("systemtime----->" + systemTime);
		// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
		if(systemTime > selectTime) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			selectTime = calendar.getTimeInMillis();
		}
		//long time = selectTime - systemTime;
		return selectTime;
	}
    
	private void LoadFacelibrary() throws IOException {
		String fileDir = getApplicationContext().getFilesDir().getPath();
		fileDir += CHFaceLib.defaultConfigFile;
		
		try {
			int ret = CHFaceLib.nativeInitFaceEngine(fileDir.getBytes("US-ASCII"));
			File file = new File(fileDir);
//			FileInputStream fis = new FileInputStream(fileDir);
//			long size = fis.available();
//			Log.i(TAG, fileDir+" size:"+size);
			if(0 == ret) {
				Log.i(TAG, "load lib");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(sBuilder.toString());
			Log.i(TAG, jsonObject.toString());
//			String status = jsonObject.getString("status");
//			if(status.compareTo("100") == 0) {
//				StartCheckinActivity();
//			} else {
//				StartLoginActivity();
//			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
