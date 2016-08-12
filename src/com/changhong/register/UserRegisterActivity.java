package com.changhong.register;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.changhong.faceattendance.R;
import com.changhong.utils.MyConfig;

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
*date    创建时间：2016年7月5日下午5:25:01
*/
public class UserRegisterActivity extends Activity{
	private static String TAG = "UserRegisterActivity";
	private Button mNextBtn;
	private ImageView mBackImv;
	private EditText mCompCodeEdt;
	private EditText mEmailEdt;
	private EditText mNameEdt;
	private EditText mTelEdt;
	private ImageButton mClearCompCode;
	private ImageButton mClearEmail;
	private ImageButton mClearName;
	private ImageButton mClearTel;
	private String mCompCode;
	private String mEmail;
	private String mName;
	private String mTel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.user_reg_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_user_reg);
		
		InitUi();
	}
	
	private void InitUi(){
		mNextBtn = (Button)findViewById(R.id.user_reg_next);
		mNextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCompCode = mCompCodeEdt.getText().toString();
				mEmail = mEmailEdt.getText().toString();
				mName = mNameEdt.getText().toString();
				mTel = mTelEdt.getText().toString();
				
				if(!mCompCode.isEmpty()){
					if(!mEmail.isEmpty()){
						if(isEmail(mEmail)){
							if(!mName.isEmpty()){
								if(!mTel.isEmpty()){
									if(isTel(mTel)){
										new CheckUserExitThread(mCompCode, mEmail, mName, mTel).start();
									}else{
										Toast.makeText(getApplicationContext(), "您输入的手机号不合法", Toast.LENGTH_SHORT).show();
									}
								}else{
									Toast.makeText(getApplicationContext(), "请输入手机号", Toast.LENGTH_SHORT).show();
								}
							}else{
								Toast.makeText(getApplicationContext(), "请输入姓名", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "您输入的邮箱不合法", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "请输入邮箱", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "请输入公司代码", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		mBackImv = (ImageView)findViewById(R.id.user_reg_back);
		mBackImv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mCompCodeEdt = (EditText)findViewById(R.id.user_reg_company_text);
		mCompCodeEdt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCompCodeEdt.setHint(null);
			}
		});
		mCompCodeEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(mCompCodeEdt.getText().length() > 0){
						mClearCompCode.setVisibility(View.VISIBLE);
					}
				}else{
					mClearCompCode.setVisibility(View.INVISIBLE);
				}
				
			}
		});
		mCompCodeEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mCompCodeEdt.getText().length() != 0){
					mClearCompCode.setVisibility(View.VISIBLE);
				}else{
					mClearCompCode.setVisibility(View.INVISIBLE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mEmailEdt = (EditText)findViewById(R.id.user_reg_email_text);
		mEmailEdt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEmailEdt.setHint(null);
			}
		});
		mEmailEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(mEmailEdt.getText().length() > 0){
						mClearEmail.setVisibility(View.VISIBLE);
					}
				}else{
					mClearEmail.setVisibility(View.INVISIBLE);
				}
			}
		});
		mEmailEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mEmailEdt.getText().length() != 0){
					mClearEmail.setVisibility(View.VISIBLE);
				}else{
					mClearEmail.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		mNameEdt = (EditText)findViewById(R.id.user_reg_name_text);
		mNameEdt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mNameEdt.setHint(null);
			}
		});
		mNameEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(mNameEdt.getText().length() > 0){
						mClearName.setVisibility(View.VISIBLE);
					}
				}else{
					mClearName.setVisibility(View.INVISIBLE);
				}
				
			}
		});
		mNameEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mNameEdt.getText().length() != 0){
					mClearName.setVisibility(View.VISIBLE);
				}else{
					mClearName.setVisibility(View.INVISIBLE);
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mTelEdt = (EditText)findViewById(R.id.user_reg_phone_text);
		mTelEdt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTelEdt.setHint(null);
				
			}
		});
		mTelEdt.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					if(mTelEdt.getText().length() > 0){
						mClearTel.setVisibility(View.VISIBLE);
					}
				}else{
					mClearTel.setVisibility(View.INVISIBLE);
				}
				
			}
		});
		mTelEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mTelEdt.getText().length() != 0){
					mClearTel.setVisibility(View.VISIBLE);
				}else{
					mClearTel.setVisibility(View.INVISIBLE);
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			
			}
		});
		
		mClearCompCode = (ImageButton)findViewById(R.id.user_myclearbutton_company);
		mClearCompCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCompCodeEdt.setText(null);
			}
		});
		
		mClearEmail = (ImageButton)findViewById(R.id.user_myclearbutton3);
		mClearEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mEmailEdt.setText(null);
			}
		});
		
		mClearName = (ImageButton)findViewById(R.id.user_myclearbutton4);
		mClearName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mNameEdt.setText(null);
			}
		});
		
		mClearTel = (ImageButton)findViewById(R.id.user_myclearbutton_phone);
		mClearTel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mTelEdt.setText(null);
			}
		});
	}
	
	public Handler mUserHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), "网络问题", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				new CheckUserThread(mCompCode, mEmail, mName, mTel).start();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "公司注册码不存在", Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(getApplicationContext(), "邮箱已注册", Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(getApplicationContext(), "手机号已注册", Toast.LENGTH_SHORT).show();
				break;
			case 5:
				Toast.makeText(getApplicationContext(), "用户已注册", Toast.LENGTH_SHORT).show();
				break;
			case 6:
				StartFaceRegisterActivity();
				break;
			default:
				break;
			}
		}
	};
	
	class CheckUserExitThread extends Thread{
		private String CompCode;
		private String Email;
		private String Name;
		private String Tel;
		
		public CheckUserExitThread (String CompCode, String Email, String Name, String Tel ) {
			this.CompCode = CompCode;
			this.Email = Email;
			this.Name = Name;
			this.Tel = Tel;
		}
		
		public void run(){
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "user_registered");
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("comp_code", new StringBody(this.CompCode));
				en.addPart("email", new StringBody(this.Email));
				en.addPart("name", new StringBody(this.Name));
				en.addPart("telephone", new StringBody(this.Tel));
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
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 1));
				}
				else{
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 5));
				}
			}catch (ClientProtocolException e) {
				mUserHandler.sendMessage(Message.obtain(mUserHandler, 0));
				e.printStackTrace();
			} catch (IOException e) {
				mUserHandler.sendMessage(Message.obtain(mUserHandler, 0));
				e.printStackTrace();
			}
		}
	}
	
	class CheckUserThread extends Thread{
		private String CompCode;
		private String Email;
		private String Name;
		private String Tel;
		
		public CheckUserThread (String CompCode, String Email, String Name, String Tel ) {
			this.CompCode = CompCode;
			this.Email = Email;
			this.Name = Name;
			this.Tel = Tel;
		}
		
		public void run(){
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "user_exist");
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("comp_code", new StringBody(this.CompCode));
				en.addPart("email", new StringBody(this.Email));
				en.addPart("name", new StringBody(this.Name));
				en.addPart("telephone", new StringBody(this.Tel));
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
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 6));
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 441){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					Log.e(TAG, builder.toString());
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 2));
				}
				else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 443){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					Log.e(TAG, builder.toString());
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 3));
				}
				else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 442){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					Log.e(TAG, builder.toString());
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 4));
				}
				else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 500){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					Log.e(TAG, builder.toString());
					//mUserHandler.sendMessage(Message.obtain(mUserHandler, 4));
				}
				else{
					mUserHandler.sendMessage(Message.obtain(mUserHandler, 0));
				}
			}catch (ClientProtocolException e) {
				mUserHandler.sendMessage(Message.obtain(mUserHandler, 0));
				e.printStackTrace();
			} catch (IOException e) {
				mUserHandler.sendMessage(Message.obtain(mUserHandler, 0));
				e.printStackTrace();
			}
		}
	}
	private void StartFaceRegisterActivity(){
		Intent intent = new Intent(getApplicationContext(), FaceRegisterActivity.class);
		intent.putExtra("comp_code", mCompCode);
		intent.putExtra("email", mEmail);
		intent.putExtra("name", mName);
		intent.putExtra("tel", mTel);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	public boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}
	
	public boolean isTel(String tel){
		String str = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(tel);
		
		return m.matches();
	}

}
