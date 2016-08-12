package com.changhong.forgetpsd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.faceattendance.R;
import com.changhong.utils.ConnectDialog;
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
import android.widget.ProgressBar;
import android.widget.Toast;

public class CaptchaActivity extends Activity implements Notify{
	private static final String TAG = "CaptchaActivity";
    private Button mRecButton;
    private Button mConButton;
    private ImageButton mclearTelButton;
    private ImageButton mclearRidButton;
    private ImageButton mClearCompCodeButton;
    private EditText mCompCodEditText;
    private EditText mTelEditText;
    private EditText mCapEditText;
    private ProgressBar mProgressBar;
    private String id;
    private String comp_code;
    private static final int NETWORK_ERROR = 99;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.psdcaptcha_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_captcha);
		ImageView imageView = (ImageView)findViewById(R.id.retpassword);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//StartLoginActivity();
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mClearCompCodeButton = (ImageButton)findViewById(R.id.myclearbutton_compcode_edit);
		mclearTelButton = (ImageButton)findViewById(R.id.myclearbutton7);
		mclearRidButton = (ImageButton)findViewById(R.id.myclearbutton8);
		
		mCompCodEditText = (EditText)findViewById(R.id.comp_code_edit);
		mCompCodEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCompCodEditText.setHint(null);
			}
		});
		mCompCodEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					if(mCompCodEditText.getText().length() > 0)
						mClearCompCodeButton.setVisibility(View.VISIBLE);
				}else{
					mClearCompCodeButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		mCompCodEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mCompCodEditText.getText().length() != 0){
					mClearCompCodeButton.setVisibility(View.VISIBLE);
				} else {
					mClearCompCodeButton.setVisibility(View.INVISIBLE);
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
		
		mTelEditText = (EditText)findViewById(R.id.telormail_edit);
		mTelEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTelEditText.setHint(null);
			}
		});
		
		mTelEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mTelEditText.getText().length() > 0)
						mclearTelButton.setVisibility(View.VISIBLE);
				} else {
					mclearTelButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mTelEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mTelEditText.getText().length() != 0){
					mclearTelButton.setVisibility(View.VISIBLE);
				} else {
					mclearTelButton.setVisibility(View.INVISIBLE);
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
		
		mCapEditText = (EditText)findViewById(R.id.receive_id_edit);
		mCapEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCapEditText.setHint(null);
			}
		});
		
		mCapEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mCapEditText.getText().length() > 0)
						mclearRidButton.setVisibility(View.VISIBLE);
				} else {
					mclearRidButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mCapEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mCapEditText.getText().length() != 0){
					mclearRidButton.setVisibility(View.VISIBLE);
				} else {
					mclearRidButton.setVisibility(View.INVISIBLE);
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
		
		mClearCompCodeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCompCodEditText.setText(null);
			}
		});
		
		mclearTelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mTelEditText.setText(null);
//				mclearTelButton.setVisibility(View.INVISIBLE);
			}
		});
		
		mclearRidButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCapEditText.setText(null);
//				mclearRidButton.setVisibility(View.INVISIBLE);
			}
		});
		
		mRecButton = (Button)findViewById(R.id.receive_id_btn);
		mRecButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				comp_code = mCompCodEditText.getText().toString();
				id = mTelEditText.getText().toString();
				if(!comp_code.isEmpty()){
					if(!id.isEmpty()){
						ConnectDialog.showDialog(CaptchaActivity.this, "正在获取验证码，请稍后！");
						new validuserThread(CaptchaActivity.this, comp_code, id).start();
						mRecButton.setEnabled(false);
						mCapEditText.setEnabled(true);
						
						
					}else{
						Toast.makeText(getApplicationContext(), "请输入您的员工编号", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "请输入您的公司代码", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		mConButton = (Button)findViewById(R.id.confirm_btn);
		mConButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String code = mCapEditText.getText().toString();
				if(code.isEmpty()){
					Toast.makeText(getApplicationContext(), "请输入您的验证码", Toast.LENGTH_LONG).show();
					return;
				}else{
					ConnectDialog.showDialog(CaptchaActivity.this, "正在识别您的验证码，请稍后！");
					new checkcodeThread(CaptchaActivity.this, comp_code, id, code).start();
	                //StartConfirmActivity();
				}
				
			}
		});
		
	}
	
	private Handler captchaHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ConnectDialog.dismiss();
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), 
						"验证码已经发生至您的邮箱，请查收", Toast.LENGTH_LONG).show();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), 
						"您输入的公司代码或者员工编号不对", Toast.LENGTH_LONG).show();
				mRecButton.setEnabled(true);
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break; 
			case 3:
				StartConfirmActivity();
				break;
			case 4:
				Toast.makeText(getApplicationContext(), 
						"您输入的验证码不正确", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
	
	private void StartConfirmActivity() {
		Intent intent = new Intent(getApplicationContext(),ConfirmActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("comp_code",comp_code);
		bundle.putString("userId",id);
		intent.putExtras(bundle);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	class validuserThread extends Thread{
		private Notify notify = null;
		private String comp_code;
		private String id;
		
		public validuserThread(Notify notify, String comp_code, String id){
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "forgot_passowrd_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
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
					notify.perform("forgot_password", builder);
					Log.e(TAG, builder.toString());
					captchaHandler.sendMessage(Message.obtain(captchaHandler, 0));
				} else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 428) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform("forgot_password", builder);
					Log.e(TAG, builder.toString());
					captchaHandler.sendMessage(Message.obtain(captchaHandler, 1));
				}else{
					// TODO:notify register fail
					captchaHandler.sendMessage(Message.obtain(captchaHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				captchaHandler.sendMessage(Message.obtain(captchaHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				captchaHandler.sendMessage(Message.obtain(captchaHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
		
	}
	
	class checkcodeThread extends Thread{
		private Notify notify;
		private String comp_code;
		private String id;
		private String code;
		
		public checkcodeThread(Notify notify, String comp_code, String id, String code){
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
			this.code = code;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "check_code_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("code", new StringBody(this.code));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
				
				httpResponse = httpClient.execute(httpPost);
				Log.e(TAG, httpResponse.getStatusLine().getStatusCode() + "");
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					Log.i(TAG, "httpResponse not null ");
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform("check_code", builder);
					Log.e(TAG, builder.toString());
					captchaHandler.sendMessage(Message.obtain(captchaHandler, 3));
				} else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 431) {
					Log.i(TAG, "httpResponse not null ");
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform("check_code", builder);
					Log.e(TAG, builder.toString());
					captchaHandler.sendMessage(Message.obtain(captchaHandler, 4));
				}else{
					// TODO:notify register fail
					captchaHandler.sendMessage(Message.obtain(captchaHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				captchaHandler.sendMessage(Message.obtain(captchaHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				captchaHandler.sendMessage(Message.obtain(captchaHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString());
			Log.i(TAG, jsonObject.toString());
			String status = jsonObject.getString("status");
//			String reason = jsonObject.getString("reason");
//			if(action.compareTo("forgot_password") == 0){
//				captchaHandler.sendMessage(Message.obtain(captchaHandler,0,status));
//			}else if(action.compareTo("check_code") == 0){
//				captchaHandler.sendMessage(Message.obtain(captchaHandler, 1,status));
//			}
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
		