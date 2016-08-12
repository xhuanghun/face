package com.changhong.register;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.changhong.dataacq.DataAcqActivity;
import com.changhong.faceattendance.R;
import com.changhong.utils.ConnectDialog;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements Notify{
	private static final String TAG = "RegisterActivity";
	private Button mNextButton;
	private ImageView mRetButton;
	private ImageButton mclearusernameButton;
	private ImageButton mclearpasButton;
	private ImageButton mClearCompanyButton;
	private ImageButton mClearEmailButton;
	private ImageButton mClearNameButton;
	private String contactString;
	private String phonenumString;
	private String companyString;
	private String emailString;
	//private String nameString;
	private static final int NETWORK_ERROR = 99;
	private EditText mEmail;
	private EditText mContact;
	private EditText mCompName;
	private EditText mPhoneNum;
	//private EditText mNameEditText;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.register_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_reg);
		
		mNextButton = (Button)this.findViewById(R.id.reg_next);
		mNextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				EditText company = (EditText)findViewById(R.id.reg_company_text);
				EditText email = (EditText)findViewById(R.id.reg_username_text);
				EditText contact = (EditText)findViewById(R.id.reg_password_text);
				EditText phonenum = (EditText)findViewById(R.id.reg_email_text);
				//EditText name  = (EditText)findViewById(R.id.reg_name_text);
				
				companyString = company.getText().toString();
				emailString = email.getText().toString();
				contactString = contact.getText().toString();
				phonenumString = phonenum.getText().toString();
				//nameString = name.getText().toString();
				//Log.e(TAG, nameString);
//				StartACQActivity(); // test
				if(!companyString.isEmpty()){
					if(!contactString.isEmpty()){
						if(!phonenumString.isEmpty()){
							if(!emailString.isEmpty()){
								if(isEmail(emailString)){
										Intent intent = new Intent(getApplicationContext(),CompRegisterActivity.class);
										intent.putExtra("comp_name", companyString);
										intent.putExtra("contact", contactString);
										intent.putExtra("phonenum", phonenumString);
										intent.putExtra("email", emailString);
										//intent.putExtra("name", nameString);
										startActivity(intent);
										overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
								}else{
									Toast.makeText(getApplicationContext(), "请输入正确的邮箱格式", Toast.LENGTH_SHORT).show();
								}
								
							}else{
								Toast.makeText(getApplicationContext(), "请输入邮箱", Toast.LENGTH_SHORT).show();
							}
						}else{
							Toast.makeText(getApplicationContext(), "请输入联系电话", Toast.LENGTH_SHORT).show();
						}
					}else{
						Toast.makeText(getApplicationContext(), "请输入联系人", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(getApplicationContext(), "请输入公司名称", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		mRetButton = (ImageView)this.findViewById(R.id.ret2login);
		mRetButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mclearusernameButton = (ImageButton)findViewById(R.id.myclearbutton3);
		mclearpasButton = (ImageButton)findViewById(R.id.myclearbutton4);
		mClearCompanyButton = (ImageButton)findViewById(R.id.myclearbutton_company);
		mClearEmailButton = (ImageButton)findViewById(R.id.myclearbutton_email);
		mClearNameButton = (ImageButton)findViewById(R.id.myclearbutton_name);
		
		mCompName = (EditText) findViewById(R.id.reg_company_text);
		mCompName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mCompName.setHint(null);
			}
		});
		mCompName.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					if(mCompName.getText().length() > 0)
						mClearCompanyButton.setVisibility(View.VISIBLE);
				}else{
					mClearCompanyButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		mCompName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mCompName.getText().length() != 0){
					mClearCompanyButton.setVisibility(View.VISIBLE);
				}else{
					mClearCompanyButton.setVisibility(View.INVISIBLE);
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
		
		mEmail = (EditText) findViewById(R.id.reg_username_text);
		mEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEmail.setHint(null);
			}
		});
		
		mEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mEmail.getText().length() > 0)
						mclearusernameButton.setVisibility(View.VISIBLE);
				} else {
					mclearusernameButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mEmail.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mEmail.getText().length() != 0){
					mclearusernameButton.setVisibility(View.VISIBLE);
				} else {
					mclearusernameButton.setVisibility(View.INVISIBLE);
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
		
		mContact = (EditText) findViewById(R.id.reg_password_text);
		mContact.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContact.setHint(null);
			}
		});
		
		mContact.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus) {
					if(mContact.getText().length() > 0)
						mclearpasButton.setVisibility(View.VISIBLE);
				} else {
					mclearpasButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		mContact.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mContact.getText().length() != 0){
					mclearpasButton.setVisibility(View.VISIBLE);
				} else {
					mclearpasButton.setVisibility(View.INVISIBLE);
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
		
		mPhoneNum = (EditText)findViewById(R.id.reg_email_text);
		mPhoneNum.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPhoneNum.setHint(null);
			}
		});
		mPhoneNum.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					if(mPhoneNum.getText().length() > 0)
						mClearEmailButton.setVisibility(View.VISIBLE);
				}else{
					mClearEmailButton.setVisibility(View.INVISIBLE);
				}
			}
		});
		mPhoneNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				if(mPhoneNum.getText().length() != 0){
					mClearEmailButton.setVisibility(View.VISIBLE);
				}else{
					mClearEmailButton.setVisibility(View.INVISIBLE);
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
		
//		mNameEditText = (EditText) findViewById(R.id.reg_name_text);
//		mNameEditText.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mNameEditText.setHint(null);
//			}
//		});
//		mNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
//			
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				// TODO Auto-generated method stub
//				if(hasFocus){
//					if(mNameEditText.getText().length() > 0)
//						mClearNameButton.setVisibility(View.VISIBLE);
//				}else{
//					mClearNameButton.setVisibility(View.INVISIBLE);
//				}
//			}
//		});
//		mNameEditText.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				// TODO Auto-generated method stub
//				if(mNameEditText.getText().length() != 0){
//					mClearNameButton.setVisibility(View.VISIBLE);
//				}else{
//					mClearNameButton.setVisibility(View.INVISIBLE);
//				}
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count,
//					int after) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable s) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		mClearCompanyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEmail.setText(null);
			}
		});
		
		mClearEmailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mPhoneNum.setText(null);
			}
		});
		
//		mClearNameButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				mNameEditText.setText(null);
//			}
//		});
		
		mclearusernameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mEmail.setText(null);
//				mclearnameButton.setVisibility(View.INVISIBLE);
			}
		});
		
		mclearpasButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContact.setText(null);
//				mclearpasButton.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	private Handler registerHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ConnectDialog.dismiss();
			switch(msg.what) {
			case 0:
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
	
	private void StartACQActivity() {
		ConnectDialog.dismiss();
		Intent intent = new Intent(getApplicationContext(), DataAcqActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@Override
	public void perform(String action, StringBuilder sBuilder) {
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
}


