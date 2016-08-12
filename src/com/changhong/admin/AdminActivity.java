package com.changhong.admin;

import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;
import com.changhong.utils.MyImageTextButton2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AdminActivity extends Activity{
	private static String TAG = "AdminActivity";
	private MyImageTextButton2 mCompBtn;
	private MyImageTextButton2 mWorkBtn;
	private MyImageTextButton2 mScanBtn;
	private Button mAdminLogoutBtn;
	private TextView mAdminCompName;
	private String mCompCode;
	private String mCompName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.admin_layout);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_admin);
		Intent intent = getIntent();
		mCompName = intent.getStringExtra("name");
		mCompCode = intent.getStringExtra("comp_code");
		
		InitUi();
	}
	
	private void InitUi(){
		mAdminCompName = (TextView)findViewById(R.id.admin_comp_name);
		mAdminCompName.setText(mCompName);
		
		mCompBtn = (MyImageTextButton2)findViewById(R.id.admin_comp);
		mCompBtn.setText(mCompCode);
		mCompBtn.setTextSize(18);
		mCompBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),AdminCompInfoActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
			}
		});
		
		mWorkBtn = (MyImageTextButton2)findViewById(R.id.admin_work);
		mWorkBtn.setText("待完成工作");
		mWorkBtn.setTextSize(18);
		mWorkBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartAdminWorkActivity();
			}
		});
		
		mScanBtn = (MyImageTextButton2)findViewById(R.id.admin_scan);
		mScanBtn.setText("查看已注册人员");
		mScanBtn.setTextSize(18);
		mScanBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mAdminLogoutBtn = (Button)findViewById(R.id.admin_logout_btn);
		mAdminLogoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartLoginActivity();
			}
		});
	}
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	@SuppressWarnings("static-access")
	private void StartLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void StartAdminWorkActivity(){
		Intent intent = new Intent(getApplicationContext(), AdminWorkActivity.class);
		intent.putExtra("compcode", mCompCode);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
