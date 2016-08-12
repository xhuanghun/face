package com.changhong.admin;

import com.changhong.faceattendance.R;
import com.changhong.utils.MyImageTextButton2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class AdminCompInfoActivity extends Activity{
	private static String TAG = "AdminCompInfoActivity";
	private MyImageTextButton2 mCompInfoName;
	private MyImageTextButton2 mCompInfoConnctor;
	private MyImageTextButton2 mCompInfoTel;
	private MyImageTextButton2 mCompInfoGps;
	private MyImageTextButton2 mCompInfoEmail;
	private MyImageTextButton2 mCompInfoPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.admin_compinfo_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_compinfo);
		Log.e(TAG, "set title");
		
		InitUi();
	}
	
	private void InitUi(){
		ImageView imageView = (ImageView)this.findViewById(R.id.compinfo_back);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mCompInfoName = (MyImageTextButton2)findViewById(R.id.comp_info_name);
		mCompInfoName.setText("名称");
		mCompInfoName.setTextSize(18);
		mCompInfoName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCompInfoConnctor = (MyImageTextButton2)findViewById(R.id.comp_info_connctor);
		mCompInfoConnctor.setText("联系人");
		mCompInfoConnctor.setTextSize(18);
		mCompInfoConnctor.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCompInfoTel = (MyImageTextButton2)findViewById(R.id.comp_info_tel);
		mCompInfoTel.setText("电话");
		mCompInfoTel.setTextSize(18);
		mCompInfoTel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCompInfoGps = (MyImageTextButton2)findViewById(R.id.comp_info_gps);
		mCompInfoGps.setText("GPS");
		mCompInfoGps.setTextSize(18);
		mCompInfoGps.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCompInfoEmail = (MyImageTextButton2)findViewById(R.id.comp_info_email);
		mCompInfoEmail.setText("邮箱");
		mCompInfoEmail.setTextSize(18);
		mCompInfoEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mCompInfoPwd = (MyImageTextButton2)findViewById(R.id.comp_info_pwd);
		mCompInfoPwd.setText("密码");
		mCompInfoPwd.setTextSize(18);
		mCompInfoPwd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
