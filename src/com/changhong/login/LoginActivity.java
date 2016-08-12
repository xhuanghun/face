package com.changhong.login;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.changhong.checkin.CheckinActivity;
import com.changhong.checkin.FaceCheckinFragment;
import com.changhong.chni.CHFaceLib;
import com.changhong.faceattendance.R;
import com.changhong.forgetpsd.CaptchaActivity;
import com.changhong.register.RegisterActivity;
import com.changhong.register.UserRegisterActivity;
import com.changhong.utils.ConnectDialog;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

public class LoginActivity extends Activity implements Notify{
	private final static String TAG = "LoginActivity";
	private Button mRegButton;
	private Button mLoginButton;
	private Button mForgetButton;
	private Button mJoinBtn;
	private Button mAdminLoginBtn;
	private ImageButton mclearButton1;
	private ImageButton mclearButton2;
	private ImageButton mClearButton_compcode;
	private String comp_code;
	private String id;
	private String pswd;
	private static final int START_CHECKIN = 3;
	private static final int DOWNFILE_ERROR = 998; 
	private static final int NETWORK_ERROR = 999;
	private boolean mFeaturefile = false;
	private EditText mComp_codeEditText;
	private EditText mUsertEditText;
	private EditText mPswdEditText;
	private LocationManager lm;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		
//		try {
//			LoadFacelibrary();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//getLocations();
		//Log.e(TAG, "get location end");
		mForgetButton = (Button)this.findViewById(R.id.forgetpasswd);
		mForgetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),CaptchaActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		
		mRegButton = (Button)this.findViewById(R.id.company_reg);
		mRegButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		
		mJoinBtn = (Button)this.findViewById(R.id.join_comp);
		mJoinBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		
		mAdminLoginBtn = (Button)this.findViewById(R.id.admin_login);
		mAdminLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		
		mclearButton1 = (ImageButton)findViewById(R.id.myclearbutton1);
		mclearButton2 = (ImageButton)findViewById(R.id.myclearbutton2);
		mClearButton_compcode = (ImageButton)findViewById(R.id.myclearbutton_comp_code);
		
		mComp_codeEditText = (EditText)findViewById(R.id.comp_code);
		mComp_codeEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mComp_codeEditText.setHint(null);
			}
		});
		
		mComp_codeEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					if(mComp_codeEditText.getText().length() > 0)
						mClearButton_compcode.setVisibility(View.VISIBLE);
				}else{
					mClearButton_compcode.setVisibility(View.INVISIBLE);
				}
			}
		});
		mComp_codeEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mComp_codeEditText.getText().length() != 0 ){
					mClearButton_compcode.setVisibility(View.VISIBLE);
				} else {
					mClearButton_compcode.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mUsertEditText = (EditText) findViewById(R.id.user_name);
		mUsertEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUsertEditText.setHint(null);
			}
		});
		
		mUsertEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mUsertEditText.getText().length() > 0)
						mclearButton1.setVisibility(View.VISIBLE);
				} else {
					mclearButton1.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mUsertEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mUsertEditText.getText().length() != 0 ){
					mclearButton1.setVisibility(View.VISIBLE);
				} else {
					mclearButton1.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mPswdEditText = (EditText) findViewById(R.id.pass_word);
		mPswdEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPswdEditText.setHint(null);
			}
		});
		
		mPswdEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mPswdEditText.getText().length() > 0)
						mclearButton2.setVisibility(View.VISIBLE);
				} else {
					mclearButton2.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mPswdEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mPswdEditText.getText().length() != 0){
					mclearButton2.setVisibility(View.VISIBLE);
				} else {
					mclearButton2.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		mClearButton_compcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mComp_codeEditText.setText(null);
			}
		});
		
		mclearButton1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mUsertEditText.setText(null);
//				mclearButton1.setVisibility(View.INVISIBLE);
			}
		});
		
		mclearButton2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPswdEditText.setText(null);
//				mclearButton2.setVisibility(View.INVISIBLE);
			}
		});
		
		SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		comp_code = mypref.getString("owner_company", "");
		id = mypref.getString("owner_id", "");
		pswd = mypref.getString("owner_pwd", "");
		Log.e(TAG, "comp_code" + comp_code + "id" + id + "pswd" + pswd);
		if(!TextUtils.isEmpty(comp_code)){
			mComp_codeEditText.setText(comp_code);
		}
		if(!TextUtils.isEmpty(id)) {
			mUsertEditText.setText(id);
		} 
		if(!TextUtils.isEmpty(pswd)) {
			mPswdEditText.setText(pswd);
		}
		
		mLoginButton = (Button)this.findViewById(R.id.login);
		mLoginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				comp_code = mComp_codeEditText.getText().toString();
				id = mUsertEditText.getText().toString();
				pswd = mPswdEditText.getText().toString();
//				StartCheckinActivity(); // for test
				if(!comp_code.isEmpty()){
					if(!id.isEmpty()) {
						if(pswd.isEmpty()) {
							Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_LONG).show();
							return;
						}					
					} else{
						Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_LONG).show();
						return;
					}
				}else{
					Toast.makeText(getApplicationContext(), "请输入公司代码", Toast.LENGTH_LONG).show();
					return;
				}
				
				ConnectDialog.showDialog(LoginActivity.this, "正在登录，请稍后！");
				//FeatureFileExsit();
				new loginThread(LoginActivity.this,comp_code, id, pswd).start();
				//mLoginButton.setEnabled(false);
			}
		});
		
	} 
	
	private Handler loginHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ConnectDialog.dismiss();
			switch(msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), "您输入的公司代码或者员工编号不对", 
						Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "密码错误", 
						Toast.LENGTH_LONG).show();
				
				break;
			case 2:
//				mFeaturefile = true;
//				CHFaceLib.nativeReleaseFaceEngine();
//				try {
//					LoadFacelibrary();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				loginHandler.sendMessage(Message.obtain(loginHandler, START_CHECKIN));
				break;
			case START_CHECKIN:
				//mLoginButton.setEnabled(true);
				
				StartCheckinActivity();
				break;
			case DOWNFILE_ERROR:
				Toast.makeText(getApplicationContext(), 
						"获取信息失败", Toast.LENGTH_LONG).show();
				String fileDir = getApplicationContext().getFilesDir().getPath();
				fileDir += CHFaceLib.defaultConfigFile;
				File file = new File(fileDir);
				//DeleteFile(file);
				mLoginButton.setEnabled(true);
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				mLoginButton.setEnabled(true);
				break;
			default:
				break;
			}
		};
	};
	
	private void StartCheckinActivity() {
		Log.i(TAG, "startcheckinactivity");
		Intent intent = new Intent(getApplicationContext(), CheckinActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		CHFaceLib.nativeReleaseFaceEngine();
	}
	
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String pswdString = mypref.getString("owner_pwd", "");
		if(pswdString.isEmpty()){
			mPswdEditText.setText("");
		}
	}
	/*
	private boolean FeatureFileExsit() {
		String fileDir = getApplicationContext().getFilesDir().getPath();
		fileDir += CHFaceLib.defaultConfigFile;
		File file = new File(fileDir);
		if(!file.exists()) {
			Log.i(TAG, "no featurefile");
			mFeaturefile = false;
		} else {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			boolean register_done = pref.getBoolean("register_done", false);
			if(register_done) {
				ReloadLib();
				Editor editor = pref.edit();
				editor.putBoolean("register_done", false);
				editor.commit();
			}
			byte[] name;
			int size;
			int[] ids = new int[10];
			size = CHFaceLib.nativeGetAllUserIDs(ids);
			name = CHFaceLib.GetNameById(0);
			try {
				String nameString = new String(name, "UTF-8");
				Log.i(TAG, size+" ids "+ids[0]+" words:"+nameString+" id:"+id);
				if(nameString.compareTo(id) != 0) {
					mFeaturefile = false;
				} else {
					Log.i(TAG, "feature file path:"+fileDir);
					mFeaturefile = true;
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mFeaturefile;
	}
	
	private void DeleteFile(File file) {
		if(file.exists() && file.isFile()) {
			file.delete();
		}
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
	
	private void ReloadLib() {
		CHFaceLib.nativeReleaseFaceEngine();
		try {
			LoadFacelibrary();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
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
					loginHandler.sendMessage(Message.obtain(loginHandler, 0));
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
					loginHandler.sendMessage(Message.obtain(loginHandler, 1));
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
	/*
	class getfeatureurlThread extends Thread {
		private Notify notify = null;
		private String userId;
		private String action;
		
		public getfeatureurlThread(Notify notify, String action, String userId) {
			this.notify = notify;
			this.action = action;
			this.userId = userId;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR);
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("action", new StringBody(this.action));
				en.addPart("_userId", new StringBody(this.userId));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
				MyConfig.SetNetworkTimeout(httpClient, 5000, 5000);
				httpResponse = httpClient.execute(httpPost);
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform(this.action, builder);
				} else {
					// TODO:notify register fail
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
				MyConfig.SetNetworkTimeout(httpClient, 5000,10000);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					InputStream input = httpResponse.getEntity().getContent();

					FileOutputStream output = getApplicationContext().
							openFileOutput(MyConfig.OWNER_FEATURE_FILE_NAME, 0);
					byte b[] = new byte[1024];
					int j = 0;
					while((j=input.read(b)) != -1) {
						output.write(b, 0, j);
					}
					output.flush();
					output.close();
					loginHandler.sendMessage(Message.obtain(loginHandler, 2));
				} else {
					// TODO:notify register fail
					loginHandler.sendMessage(Message.obtain(loginHandler, DOWNFILE_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				loginHandler.sendMessage(Message.obtain(loginHandler, DOWNFILE_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				loginHandler.sendMessage(Message.obtain(loginHandler, DOWNFILE_ERROR));
				e.printStackTrace();
			}
		}
	}
*/
	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString());
			String status = jsonObject.getString("status");
			Log.i(TAG, jsonObject.toString());
			if(action.compareTo("login")==0) {
				String nameString = jsonObject.getString("name");
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				Editor editor = pref.edit();
				editor.putString("owner_company", comp_code);
//				editor.clear();
				editor.putString("owner_id", id);
				editor.putString("owner_pwd", pswd);
				editor.putString("owner_name", nameString);
				editor.commit();
				loginHandler.sendMessage(Message.obtain(loginHandler, START_CHECKIN));
			} 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getLocations() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS是否正常启动
		// if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		// 使用GPS
		if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// Toast.makeText(this, "请开启GPS导航...",
			// Toast.LENGTH_SHORT).show(); //使用GPS
			Toast.makeText(getApplicationContext(), getString(R.string.open_location), Toast.LENGTH_SHORT).show();
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0); // 完成设置后返回请求
			//return;
		}

		// 判断设备是否连接网络
		// 获取当前的网络连接服务
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取活动的网络连接信息
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		// 判断
		if (info == null) {
			// 当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
			Toast.makeText(getApplicationContext(), getString(R.string.open_wifi), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			startActivityForResult(intent, 0);
			return;
		} else {
			Log.i(TAG, getString(R.string.network_available));
			boolean isAlive = info.isAvailable();
			Log.i(TAG, Boolean.toString(isAlive));
			if (!isAlive) {
				Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
				return;
			}
		}

		// 为获取地理位置信息时设置查询条件
		// String bestProvider = lm.getBestProvider(getCriteria(), true);
		String bestProvider = lm.NETWORK_PROVIDER;
		// String bestProvider = lm.GPS_PROVIDER; //使用GPS定位方式
		// 获取位置信息
		// 如果不设置查询要求，getLastKnownLocation方法传入的参数为LocationManager.GPS_PROVIDER
		Location mLocation  = lm.getLastKnownLocation(bestProvider);
		if (mLocation == null) {
			// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener); //使用GPS
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
		while (true) {
			mLocation = lm.getLastKnownLocation(bestProvider);
			if (mLocation != null) {
				Log.d("Location", "Latitude: " + mLocation.getLatitude());
				Log.d("Location", "location: " + mLocation.getLongitude());
				// 长时间的监听位置更新可能导致耗电量急剧上升,一旦获取到位置后，就停止监听
				lm.removeUpdates(locationListener);
				break;
			} else {
				Log.d("Location", "Latitude: " + 0);
				Log.d("Location", "location: " + 0);
				lm.removeUpdates(locationListener);
				break;
			}
		}

		//updateView(mLocation);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	// 位置监听
		private LocationListener locationListener = new LocationListener() {

			/**
			 * 位置信息变化时触发
			 */
			public void onLocationChanged(Location location) {
				
				Log.e(TAG, "时间：" + location.getTime());
				Log.e(TAG, "经度：" + location.getLongitude());
				Log.e(TAG, "纬度：" + location.getLatitude());
				Log.e(TAG, "海拔：" + location.getAltitude());
			}

			/**
			 * Provider状态变化时触发
			 */
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i(TAG, "status changed");

			}

			/**
			 * Provider开启时触发
			 */
			public void onProviderEnabled(String provider) {
				Location location = lm.getLastKnownLocation(provider);
				
			}

			/**
			 * Provider禁用时触发
			 */
			public void onProviderDisabled(String provider) {
				
			}

		};
	
	
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
 