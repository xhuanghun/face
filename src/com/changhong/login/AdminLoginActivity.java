package com.changhong.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.admin.AdminActivity;
import com.changhong.faceattendance.R;
import com.changhong.forgetpsd.CaptchaActivity;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
*@author 作者：雷传涛  E-mail:chuantao.lei@changhong.com
*date    创建时间：2016年7月7日下午5:28:24
*/
public class AdminLoginActivity extends Activity implements Notify{
	private static String TAG = "AdminLoginActivity";
	private Button mAdminLoginBtn;
	private Button mForgetPwdBtn;
	private EditText mCompcodeEdt;
	private EditText mPwdEdt;
	private ImageButton mClearCompcode;
	private ImageButton mClearPwd;
	private String mCompCode;
	private String mPwd;
	private String comp_code;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.admin_login_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_adminlogin);
		
		InitUi();
	}
	
	private void InitUi(){
		ImageView imageView = (ImageView)findViewById(R.id.admin_back);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mAdminLoginBtn = (Button)findViewById(R.id.admin_login);
		mAdminLoginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCompCode = mCompcodeEdt.getText().toString();
				mPwd = mPwdEdt.getText().toString();
				if(!mCompCode.isEmpty()){
					if(!mPwd.isEmpty()){
						new AdminLoginThread(AdminLoginActivity.this,mCompCode, mPwd).start();
					}else{
						Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "请输入公司注册码", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		mForgetPwdBtn = (Button)findViewById(R.id.admin_forgetpasswd);
		mForgetPwdBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartForgetpwdActivity();
			}
		});
		
		mCompcodeEdt = (EditText)findViewById(R.id.admin_user_name);
		mCompcodeEdt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCompcodeEdt.setHint(null);
			}
		});
		mCompcodeEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(mCompcodeEdt.getText().length() > 0)
						mClearCompcode.setVisibility(View.VISIBLE);
				}else{
					mClearCompcode.setVisibility(View.INVISIBLE);
				}
				
			}
		});
		mCompcodeEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mCompcodeEdt.getText().length() != 0){
					mClearCompcode.setVisibility(View.VISIBLE);
				}else{
					mClearCompcode.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mPwdEdt = (EditText)findViewById(R.id.admin_pass_word);
		mPwdEdt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPwdEdt.setHint(null);
			}
		});
		mPwdEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(mPwdEdt.getText().length() > 0)
						mClearPwd.setVisibility(View.VISIBLE);
				}else{
					mClearPwd.setVisibility(View.INVISIBLE);
				}
			}
		});
		mPwdEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mPwdEdt.getText().length() != 0){
					mClearPwd.setVisibility(View.VISIBLE);
				}else{
					mClearPwd.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		mClearCompcode = (ImageButton)findViewById(R.id.admin_myclearbutton3);
		mClearCompcode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCompcodeEdt.setText(null);
			}
		});
		
		mClearPwd = (ImageButton)findViewById(R.id.admin_myclearbutton2);
		mClearPwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mPwdEdt.setText(null);
			}
		});
	}
	
	public Handler mAdminLoginHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				Toast.makeText(getApplicationContext(), "网络问题", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				StartAdminActivity();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "公司注册码不存在", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
	class AdminLoginThread extends Thread{
		private Notify notify;
		private String CompCode;
		private String Pwd;
		
		public AdminLoginThread(Notify notify, String CompCode, String Pwd){
			this.notify = notify;
			this.CompCode = CompCode;
			this.Pwd = Pwd;
		}
		
		public void run(){
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "hr_login_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("comp_code", new StringBody(this.CompCode));
				en.addPart("pwd", new StringBody(this.Pwd));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException  e) {
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
				Log.i(TAG, "http client execute");
				//MyConfig.SetNetworkTimeout(httpClient, 5000, 5000);
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
					Log.e(TAG, builder.toString());
					//mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 1));
					notify.perform("getmsg", builder);
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 441){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					Log.e(TAG, builder.toString());
					mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 2));
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 429){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					Log.e(TAG, builder.toString());
					mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 3));
				}
				else{
					mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 0));
				}
			}catch (ClientProtocolException e) {
				mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 0));
				e.printStackTrace();
			} catch (IOException e) {
				mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 0));
				e.printStackTrace();
			}
		}
	}
	
	private void StartForgetpwdActivity(){
		Intent intent = new Intent(getApplicationContext(), CaptchaActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void StartAdminActivity(){
		Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
		intent.putExtra("name", name);
		intent.putExtra("comp_code", comp_code);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void perform(String action, StringBuilder sBuilder) {
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString());
			name = jsonObject.getString("name");
			comp_code = jsonObject.getString("company code");
			name = new String(name.getBytes(),Charset.forName("utf-8"));
			Log.e(TAG, name);
			mAdminLoginHandler.sendMessage(Message.obtain(mAdminLoginHandler, 1));
			//Log.v(TAG, "status:" + status + "msg:" + msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
