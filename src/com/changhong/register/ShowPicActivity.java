package com.changhong.register;


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
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ShowPicActivity extends Activity{
	private static String TAG = "ShowPicActivity";
	private Button mBackBtn;
	private Button mCommitBtn;
	private ImageView mShowPicImg;
	private String mComp_code;
	private String mEmail;
	private String mName;
	private String mTel;
	private byte[] mPic;
//	private Bitmap mPic;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.show_pic_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_showpic);
		
		Intent intent = getIntent();
		mComp_code = intent.getStringExtra("comp_code");
		mEmail = intent.getStringExtra("email");
		mName = intent.getStringExtra("name");
		mTel = intent.getStringExtra("tel");
		mPic = intent.getByteArrayExtra("pic");
//		mPic = (Bitmap)(intent.getSerializableExtra("pic"));
		Log.e(TAG, mPic.length + "");
		
		initUi();
	}
	
	private void initUi(){
		mShowPicImg = (ImageView)findViewById(R.id.show_pic_img);
		mShowPicImg.setImageBitmap(Bytes2Bimap(mPic));
		
		mBackBtn = (Button)findViewById(R.id.show_back_btn);
		mBackBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mCommitBtn = (Button)findViewById(R.id.show_ok_btn);
		mCommitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCommitBtn.setText("提交中...");
				mCommitBtn.setEnabled(false);
				new FaceRegThread( mComp_code, mTel, mEmail, mName, mPic).start();
			}
		});
	}
	
	 public Bitmap Bytes2Bimap(byte[] b) {
		 if (b.length != 0) {
		    return BitmapFactory.decodeByteArray(b, 0, b.length);
		 } else {
		    return null;
		 }
	}
	 
	 public Handler mShowPicHandler = new Handler(){
			public void handleMessage(android.os.Message msg){
				mCommitBtn.setText("提交");
				mCommitBtn.setEnabled(true);
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					Toast.makeText(getApplicationContext(), "提交失败，网络问题", Toast.LENGTH_SHORT).show();
					finish();
					overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
					break;
				case 1:
					Toast.makeText(getApplicationContext(), "申请结果将会以邮件的形式告知，请注意查收！", Toast.LENGTH_SHORT).show();
					StartLoginActivity();
					break;
				default:
					break;
				}
			}
		};
	 
	 class FaceRegThread extends Thread{
			private String action;
			private String comp_code;
			private String tel;
			private String email;
			private String name;
			private byte[] imagefile;
			
			public FaceRegThread( String comp_code, String tel,String email, 
					String name, byte[] imagefile){
				this.action = "face_reg";
				this.comp_code = comp_code;
				this.tel = tel;
				this.email = email;
				this.name = name;
				this.imagefile = imagefile;
			}
			
			public void run (){
				super.run();
//				String actionString = "user_registration_apk";
//				String serverUrl = MyConfig.SERVER_ADDR + actionString;
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "pre_registration");
				MultipartEntity en = new MultipartEntity();
				try {
					en.addPart("comp_code", new StringBody(this.comp_code));
					en.addPart("telephone", new StringBody(this.tel));
					en.addPart("email", new StringBody(this.email));
					en.addPart("name", new StringBody(this.name, Charset.forName("utf-8")));
					en.addPart("imagefile", new ByteArrayBody(this.imagefile,"tmpfilename.jpg"));
					httpPost.setEntity(en);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				HttpResponse httpResponse = null;
				try {
					Log.e(TAG, "http client execute");
					//MyConfig.SetNetworkTimeout(httpClient, 5000, 10000);
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
						//notify.perform(this.action, builder);
						Log.e(TAG, builder.toString());
						mShowPicHandler.sendMessage(Message.obtain(mShowPicHandler, 1));
					}else{
						Log.e(TAG, "http not extcute");
						mShowPicHandler.sendMessage(Message.obtain(mShowPicHandler, 0));
					}
				} catch (ClientProtocolException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
		}
	 
	 private void StartLoginActivity(){
			Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
			intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		}
	 
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
