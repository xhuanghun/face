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
import android.widget.Toast;

public class ConfirmActivity extends Activity implements Notify{
	private static final String TAG = "ConfirmActivity";
	private EditText mNewpsdEditText;
	private EditText mConpsdEditText;
	private ImageButton mclearnewButton;
	private ImageButton mclearconButton;
	private Button   mComButton;
	private String   psdfirstString;
	private String   psdconfirmString;
	private static final int NETWORK_ERROR = 99;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.psdconfirm_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_captcha);
		ImageView imageView = (ImageView)findViewById(R.id.retpassword);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mclearnewButton = (ImageButton)findViewById(R.id.myclearbutton9);
		mclearconButton = (ImageButton)findViewById(R.id.myclearbutton10);
		
		mNewpsdEditText = (EditText)findViewById(R.id.newpassword_edit);
		mNewpsdEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNewpsdEditText.setHint(null);
			}
		});
		
		mNewpsdEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mNewpsdEditText.getText().length() > 0)
						mclearnewButton.setVisibility(View.VISIBLE);
				} else {
					mclearnewButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mNewpsdEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mNewpsdEditText.getText().length() != 0){
					mclearnewButton.setVisibility(View.VISIBLE);
				} else {
					mclearnewButton.setVisibility(View.INVISIBLE);
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
		
		mConpsdEditText = (EditText)findViewById(R.id.confirmpassword_edit);
		mConpsdEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mConpsdEditText.setHint(null);
			}
		});
		
		mConpsdEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mConpsdEditText.getText().length() > 0)
						mclearconButton.setVisibility(View.VISIBLE);
				} else {
					mclearconButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mConpsdEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mConpsdEditText.getText().length() != 0){
					mclearconButton.setVisibility(View.VISIBLE);
				} else {
					mclearconButton.setVisibility(View.VISIBLE);
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
		
		mclearnewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mNewpsdEditText.setText(null);
//				mclearnewButton.setVisibility(View.INVISIBLE);
			}
		});
		
		mclearconButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mConpsdEditText.setText(null);
//				mclearconButton.setVisibility(View.INVISIBLE);
			}
		});
		
		mComButton = (Button)findViewById(R.id.commit_btn);
		mComButton.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				Bundle bundle = intent.getExtras();
				String userId = bundle.getString("userId");
				String comp_code = bundle.getString("comp_code");
			    Log.i(TAG, "comp_code" + comp_code + "userId:" + userId);
				psdfirstString = mNewpsdEditText.getText().toString();
				psdconfirmString = mConpsdEditText.getText().toString();
				if(!psdfirstString.isEmpty()){
					if(psdfirstString.length() < 6 || psdfirstString.length() > 16){
						Toast.makeText(getApplicationContext(), "您的密码长度不符合，请重新输入", Toast.LENGTH_LONG).show();
					}
					else if(!psdconfirmString.isEmpty()){
						     if(psdfirstString.equals(psdconfirmString)){
							     String newPassword = mNewpsdEditText.getText().toString();
							     ConnectDialog.showDialog(ConfirmActivity.this, "正在提交您的新密码，请稍后！");
							     new changepasswordThread(ConfirmActivity.this, comp_code, userId, 
							    		 newPassword).start();
							     //StartModifySuccessActivity();
						     }else{
							     Toast.makeText(getApplicationContext(), "两次输入密码不一致，请重新输入", 
							    		 Toast.LENGTH_LONG).show();
							     return;
						      }
					     }else{
						     Toast.makeText(getApplicationContext(), "请再次输入您的密码", Toast.LENGTH_LONG).show();
						     return;
					      }
				}else{
					Toast.makeText(getApplicationContext(), "请输入您的新密码", Toast.LENGTH_LONG).show();
					return;
				}
			}
		});
		
	}
	
	private Handler cofirmHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ConnectDialog.dismiss();
			switch (msg.what) {
			case 0:
				StartModifySuccessActivity();
				break;
			
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break; 

			default:
				break;
			}
		}
	};
	
	
	
	
	private void StartModifySuccessActivity() {
		Intent intent = new Intent(getApplicationContext(),ModifySuccessActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	class changepasswordThread extends Thread{
		private Notify notify;
		private String comp_code;
		private String id;
		private String newPassword;
		
		public changepasswordThread(Notify notify, String comp_code, String id, String newPassword){
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
			this.newPassword = newPassword;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "set_password_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("new_pwd", new StringBody(this.newPassword));
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
					notify.perform("set_password", builder);
					Log.e(TAG, builder.toString());
				}else{
					// TODO:notify register fail
					cofirmHandler.sendMessage(Message.obtain(cofirmHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				cofirmHandler.sendMessage(Message.obtain(cofirmHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				cofirmHandler.sendMessage(Message.obtain(cofirmHandler, NETWORK_ERROR));
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
			if(action.compareTo("set_password") == 0){
				cofirmHandler.sendMessage(Message.obtain(cofirmHandler, 0, status));
			}
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
