package com.changhong.myinfo;

import java.io.BufferedReader;
import java.io.File;
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
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.changhong.chni.CHFaceLib;
import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;
import com.changhong.record.RecordActivity;
import com.changhong.utils.GlideCircleTransform;
import com.changhong.utils.MyConfig;
import com.changhong.utils.MyImageTextButton;
import com.changhong.utils.MyImageTextButton2;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyinfoActivity extends Activity implements Notify{
	private final static String TAG = "MyinfoActivity";
	private MyImageTextButton mButtonView;
	private int mScreenWidth;
	private float mScale;
	private String mComp_code;
	private String mId;
	private String mName;
	private String mDep;
	private TextView mNametTextView;
	private TextView mDepTextView;
	private Button mLogoutBtn;
	private String url;
	private ImageView mImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.myinfo_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_myinfo);
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mComp_code = pref.getString("owner_company", "default");
		mId = pref.getString("owner_id", "default");
		mName = pref.getString("owner_name", "default");
		mDep = pref.getString("owner_company", "default");
		Log.e(TAG, mName);
		new GetPicThread(MyinfoActivity.this, mComp_code, mId).start();
		InitUi();
	}
	
	private void StartRecordActivity() {
		Intent intent = new Intent(getApplicationContext(), RecordActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@SuppressWarnings("static-access")
	private void StartLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void StartHelpActivity(){
		Intent intent = new Intent(getApplicationContext(),HelpActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void StartAboutActivity(){
		Intent intent = new Intent(getApplicationContext(),AboutActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void ClearInfo() {
		String fileDir = getApplicationContext().getFilesDir().getPath();
		fileDir += CHFaceLib.defaultConfigFile;
		File ftfile = new File(fileDir);
		ftfile.delete();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	private Handler myinfoHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 0:
				Glide.with(MyinfoActivity.this).load(url).transform(new GlideCircleTransform(MyinfoActivity.this)).into(mImageView);
				break;
			case 1:
				mImageView.setBackground(getResources().getDrawable(R.drawable.login_icon));
				break;
			default:
				break;
			}
		};
	};
	
	private void InitUi() {
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScale = getApplicationContext().getResources().getDisplayMetrics().density;
		for(int i=0; i<3; i++) {
			switch(i) {
			case 0:
				mButtonView = (MyImageTextButton)findViewById(R.id.myinfo_checkin_btn);
				mButtonView.setBackgroundResource(R.drawable.checkicon_normal);
				mButtonView.setText("签到");
				mButtonView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						finish();
						overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
					}
				});
				break;
			case 1:
				mButtonView = (MyImageTextButton)findViewById(R.id.myinfo_record_btn);
				mButtonView.setBackgroundResource(R.drawable.record_normal);
				mButtonView.setText("记录");
				mButtonView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						StartRecordActivity();
					}
				});
				break;
			case 2:
				mButtonView = (MyImageTextButton)findViewById(R.id.myinfo_myinfo_btn);
				mButtonView.setBackgroundResource(R.drawable.my_pressed);
				mButtonView.setText("我的");
				break;
			default:
				break;
			}
			int w = mScreenWidth / 3;
			int h = (int) (55*mScale);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, h);
			mButtonView.setLayoutParams(layoutParams);
			w = (int) ((w - 30*mScale) / 2);
			mButtonView.setPadding(w, 0, w, 0); 
		}
		
		MyImageTextButton2 helpBtn = (MyImageTextButton2)findViewById(R.id.myinfo_help);
		helpBtn.setImgResource(R.drawable.help);
		helpBtn.setText("帮助");
		helpBtn.setTextSize(18);
		helpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartHelpActivity();
			}
		});
		
		MyImageTextButton2 aboutBtn = (MyImageTextButton2)findViewById(R.id.myinfo_about);
		aboutBtn.setImgResource(R.drawable.about);
		aboutBtn.setText("关于");
		aboutBtn.setTextSize(18);
		aboutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StartAboutActivity();
			}
		});
		
		ImageView myImageView = (ImageView)findViewById(R.id.myinfo_bg1);
		myImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e(TAG, "onclick");
				Intent intent = new Intent(getApplicationContext(),ImageShower.class);
				intent.putExtra("URL", url);
				startActivity(intent);
				//overridePendingTransition(R.anim.head_in, R.anim.head_out);
			}
		});
		
		mImageView = (ImageView)findViewById(R.id.myinfo_icon);
		//Glide.with(MyinfoActivity.this).load(url).transform(new GlideCircleTransform(MyinfoActivity.this)).into(mImageView);
		mNametTextView = (TextView)findViewById(R.id.myinfo_name);
		mDepTextView = (TextView)findViewById(R.id.myinfo_dep);
		mNametTextView.setText(mName);
		mDepTextView.setText(mDep);
		
		mLogoutBtn = (Button)findViewById(R.id.logout_btn);
		mLogoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ClearInfo();
				StartLoginActivity();
//				finishAffinity(); //after 4.1 supported
			}
		});
	}
	
	
	class GetPicThread extends Thread{
		private Notify notify;
		private String comp_code;
		private String id;
		
		public GetPicThread(Notify notify, String comp_code, String id){
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
		}
		
		public void run(){
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "get_picture_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				//Log.i(TAG, "CheckinThread user id :"+this.id);
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
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
					Log.e(TAG, builder.toString());
					notify.perform("getpic", builder);
					
				} else {
					// TODO:notify register fail
					myinfoHandler.sendMessage(Message.obtain(myinfoHandler,1));
					//Toast.makeText(getApplicationContext(), "网络问题", Toast.LENGTH_SHORT).show();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
		}
	}
	
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		if(action.equals("getpic")){
			try {
				JSONObject jsonObject = new JSONObject(sBuilder.toString());
				String status = jsonObject.getString("status");
				url = jsonObject.getString("msg");
				Log.v(TAG, "status:" + status + "msg:" + url);
				myinfoHandler.sendMessage(Message.obtain(myinfoHandler,0));
				//Glide.with(MyinfoActivity.this).load(url).transform(new GlideCircleTransform(MyinfoActivity.this)).into(mImageView);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

}
