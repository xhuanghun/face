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
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;
import com.changhong.utils.MyConfig;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
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

/**
*@author 作者：雷传涛  E-mail:chuantao.lei@changhong.com
*date    创建时间：2016年7月5日下午2:18:29
*/
public class CompRegisterActivity extends Activity {
	private static String TAG = "CompRegisterActivity";
	private Button mAgreeBtn;
	private Button mCancleBtn;
	private ImageView mBack;
	private String mCompName;
	private String mEmail;
	private String mContact;
	private String mPhoneNum;
	private LocationManager lm;
	private double mLatitude1;
	private double mLongitude1;
	private LocationClient mLocationClient = null;
	private BDLocationListener myListener = new MyLocationListener();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.comp_register_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_compreg);
		
		Intent intent = getIntent();
		mCompName = intent.getStringExtra("comp_name");
		mEmail = intent.getStringExtra("email");
		mContact = intent.getStringExtra("contact");
		mPhoneNum = intent.getStringExtra("phonenum");
		Log.e(TAG, "CompName:" + mCompName + "Email" + mEmail + "Contact" + mContact + "PhoneNum" + mPhoneNum);
		
		mBack = (ImageView)findViewById(R.id.ret_compreg);
		mBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mAgreeBtn = (Button)findViewById(R.id.reg_agree_btn);
		mAgreeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//getLocations();
				mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
				// mLocationClient.setAccessKey("8mrnaFzKu3DoduLnWuB5Lt2w"); //V4.1
				// mLocationClient.setAK("8mrnaFzKu3DoduLnWuB5Lt2w"); //V4.0
				mLocationClient.registerLocationListener(myListener); // 注册监听函数
				setLocationOption();
				mLocationClient.start();// 开始定位
				Log.e(TAG, mLatitude1 + "   " + mLongitude1);
				new CompRegThread(mCompName, mEmail, mLongitude1 + "", mLatitude1 + "", mContact, mPhoneNum).start();
			}
		});
		
		mCancleBtn = (Button)findViewById(R.id.reg_cancle_btn);
		mCancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StartLoginActivity();
				
			}
		});
	}
	
	public Handler compRegHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), "网络问题", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "邮箱已注册", Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "注册成功，相关信息已发送至您的邮箱，请查收", 
						Toast.LENGTH_SHORT).show();
				StartLoginActivity();
				break;
			case 3:
				Toast.makeText(getApplicationContext(), "服务器问题", Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				break;
			}
		}
	};
	
	class CompRegThread extends Thread{
		private String CompName;
		private String emailcompany;
		private String longitude;
		private String latitude;
		private String contactPerson;
		private String contactNumber;
		
		public CompRegThread(String CompName, String emailcompany, String longitude, String latitude, 
				String contactPerson, String contactNumber){
			this.CompName = CompName;
			this.emailcompany = emailcompany;
			this.longitude = longitude;
			this.latitude = latitude;
			this.contactPerson = contactPerson;
			this.contactNumber = contactNumber;
		}
			public void run(){
				super.run();
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "company_registration");
				MultipartEntity en = new MultipartEntity();
				try {
					en.addPart("CompName", new StringBody(this.CompName, Charset.forName("utf-8")));
					en.addPart("emailcompany", new StringBody(this.emailcompany));
					en.addPart("longitude", new StringBody(this.longitude));
					en.addPart("latitude", new StringBody(this.latitude));
					en.addPart("contactPerson", new StringBody(this.contactPerson,Charset.forName("utf-8")));
					en.addPart("contactNumber", new StringBody(this.contactNumber));
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
						Log.e(TAG, builder.toString());
						compRegHandler.sendMessage(Message.obtain(compRegHandler, 2));
					}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 438){
						StringBuilder builder = new StringBuilder();
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(httpResponse.getEntity().getContent()));
						for(String s=bufferedReader.readLine(); s!=null; 
								s=bufferedReader.readLine()) {
							builder.append(s);
						}
						Log.e(TAG, builder.toString());
						compRegHandler.sendMessage(Message.obtain(compRegHandler, 1));
					}
					else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 440){
						StringBuilder builder = new StringBuilder();
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(httpResponse.getEntity().getContent()));
						for(String s=bufferedReader.readLine(); s!=null; 
								s=bufferedReader.readLine()) {
							builder.append(s);
						}
						Log.e(TAG, builder.toString());
						compRegHandler.sendMessage(Message.obtain(compRegHandler, 3));
					}
					else{
						compRegHandler.sendMessage(Message.obtain(compRegHandler, 0));
					}
				}catch (ClientProtocolException e) {
					compRegHandler.sendMessage(Message.obtain(compRegHandler, 0));
					e.printStackTrace();
				} catch (IOException e) {
					compRegHandler.sendMessage(Message.obtain(compRegHandler, 0));
					e.printStackTrace();
				}
			}
	}
	
		/**
		 * 设置相关参数
		 */
		private void setLocationOption() {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
			option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
			int span=1000;
			option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
			option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
			option.setOpenGps(true);//可选，默认false,设置是否使用gps
			option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
			option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
			option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
			option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死  
			option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
			option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
			mLocationClient.setLocOption(option);
		}

		public class MyLocationListener implements BDLocationListener {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null)
					return;
				StringBuffer sb = new StringBuffer(256);
				sb.append("当前时间 : ");
				sb.append(location.getTime());
				sb.append("\n错误码 : ");
				sb.append(location.getLocType());
				sb.append("\n纬度 : ");
				sb.append(location.getLatitude());
				sb.append("\n经度 : ");
				sb.append(location.getLongitude());
				sb.append("\n半径 : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation) {
					sb.append("\n速度 : ");
					sb.append(location.getSpeed());
					sb.append("\n卫星数 : ");
					sb.append(location.getSatelliteNumber());
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					sb.append("\n省份 : ");
					sb.append(location.getProvince());
					sb.append("\n城市 : ");
					sb.append(location.getCity());
					sb.append("\n区/县 : ");
					sb.append(location.getDistrict());
					sb.append("\n街道: ");
					sb.append(location.getStreet());
					sb.append("\n街道号码: ");
					sb.append(location.getStreetNumber());
					sb.append("\n地址 : ");
					sb.append(location.getAddrStr());
				}
				//mText.setText(sb.toString());
				mLatitude1 = location.getLatitude();
				mLongitude1 = location.getLongitude();
				mLocationClient.stop();
//				new GetLocationThread(CheckinActivity.this, comp_code).start();
//				Log.e(TAG, mLatitude1 + "     " + mLongitude1);
				//Log.d(TAG, "onReceiveLocation " + sb.toString());
			}

			
			
		}
	
	@SuppressWarnings("static-access")
	private void StartLoginActivity(){
		Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
