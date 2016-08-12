package com.changhong.checkin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.changhong.faceattendance.R;
import com.changhong.utils.MyCamera;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

/**
 *@author 作者：雷传涛  E-mail:chuantao.lei@changhong.com
 *date    创建时间：2016-6-20上午9:38:35
 */
public class CheckinActivity extends FragmentActivity implements Notify{
	private final String TAG = "CheckinActivity";
	private ViewPager mViewPager;
	private ArrayList<Fragment> mFragments;
	private int mCurrentIndex = 0;
	private CheckinPageAdapter mAdapter;
	public static Camera temp;
	private LocationManager lm;
	private double mLatitude1;
	private double mLongitude1;
	private double mLatitude2;
	private double mLongitude2;
	public static double dis;
	private String comp_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkin_main);
		
		//temp = MyCamera.OpenCamera();
		getLocations();
		SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		comp_code = mypref.getString("owner_company", "");
		Log.e(TAG, comp_code);
		
		new GetLocationThread(CheckinActivity.this, comp_code).start();
		
		mViewPager = (ViewPager)findViewById(R.id.myviewpager);
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new FaceCheckinFragment());
		mFragments.add(new CheckFragment());
		//mViewPager.setOffscreenPageLimit(0);
		
		FragmentManager fm = getSupportFragmentManager();
    	mAdapter = new CheckinPageAdapter(fm, mFragments);
    	mViewPager.setAdapter(mAdapter);
    	
    	mCurrentIndex = 0;
    	mViewPager.setCurrentItem(mCurrentIndex);
    	mViewPager.setOnPageChangeListener(new CheckinPagerChangeListener());
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// TODO Auto-generated method stub
		return super.onCreateView(name, context, attrs);
	}
	
	class CheckinPageAdapter extends FragmentPagerAdapter{
		private ArrayList<Fragment> mList;
		public CheckinPageAdapter(FragmentManager fm, ArrayList<Fragment> list) {
			super(fm);
			mList = list;
		}

		@Override
		public Fragment getItem(int arg0) {
		
			return mList.get(arg0);
		}

		@Override
		public int getCount() {
		
			return mList.size();
		}	
	}
	
	
	class CheckinPagerChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {

			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

			
		}
		
		@Override
		public void onPageSelected(int arg0) {
			MyCamera.CloseCamera(temp);
			temp = MyCamera.OpenCamera();
			Log.i(TAG, "onPageSelected "+arg0);
			mCurrentIndex = arg0;
			if(mCurrentIndex == 0){
//				MyCamera.CloseCamera(CheckFragment.mCamera1);
//				CheckFragment.mCamera1 = null;
//				Camera tmp = MyCamera.OpenCamera();
//				//FaceCheckinFragment.mCaptureView.getHolder().addCallback(new SurfaceViewCallback());
//				if(null != tmp)
//					FaceCheckinFragment.mCamera0 = tmp;
				FaceCheckinFragment.mCamera0 = temp;
				MyCamera.SetupCamera(FaceCheckinFragment.mCamera0, FaceCheckinFragment.mCaptureView.getHolder());
			}else if(mCurrentIndex == 1){
//				MyCamera.CloseCamera(FaceCheckinFragment.mCamera0);
//				FaceCheckinFragment.mCamera0 = null;
//				Camera tmp = MyCamera.OpenCamera();
//				//CheckFragment.mCaptureView.getHolder().addCallback(new SurfaceViewCallback());
//				if(null != tmp)
//					CheckFragment.mCamera1 = tmp;
				CheckFragment.mCamera1 = temp;
				MyCamera.SetupCamera(CheckFragment.mCamera1, CheckFragment.mCaptureView.getHolder());
			}
		}
		
	}
	
	public Handler checkHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), 
						"获取位置信息失败", Toast.LENGTH_LONG).show();
				break;
			case 1:
				dis = gps2m(mLatitude1, mLongitude1, mLatitude2, mLongitude2);
				Log.e(TAG, "mLatitude1" + mLatitude1 + "mLongitude1" + mLongitude1);
				Log.e(TAG, "mLatitude2" + mLatitude2 + "mLongitude2" + mLongitude2);
				Log.e(TAG, dis +"");
				break;
			default:
				break;
			}
		}
	};
	
	
	@Override
	protected void onDestroy() {
		Log.e(TAG, "on destory");
		MyCamera.CloseCamera(temp);
		temp = null;
		super.onDestroy();
		
	}
	

	@Override
	protected void onResume() {
		Log.e(TAG, "onResume");
		temp = MyCamera.OpenCamera();
		super.onResume();
	}

	@Override
	protected void onStop() {
		Log.e(TAG, "onStop");
		MyCamera.CloseCamera(temp);
		//temp = null;
		super.onStop();
	}

	public void onBackPressed(){
		//	MyCamera.CloseCamera(mCamera);
		//	mCamera = null;
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
	
	private void getLocations() {
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS是否正常启动
		// if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
		// 使用GPS
		if (!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// Toast.makeText(this, "请开启GPS导航...",
			// Toast.LENGTH_SHORT).show(); //使用GPS
			Toast.makeText(getApplicationContext(), getString(R.string.open_location), Toast.LENGTH_SHORT).show();
			// 返回开启GPS导航设置界面
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0); // 完成设置后返回请求
			//return;
		}

		// 判断设备是否连接网络
		// 获取当前的网络连接服务
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获取活动的网络连接信息
		NetworkInfo info = connMgr.getActiveNetworkInfo();
		// 判断
		if (info == null) {
			// 当前没有已激活的网络连接（表示用户关闭了数据流量服务，也没有开启WiFi等别的数据服务）
			Toast.makeText(getApplicationContext(), getString(R.string.open_wifi), Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			startActivityForResult(intent, 0);
			return;
		} else {
			Log.i(TAG, getString(R.string.network_available));
			boolean isAlive = info.isAvailable();
			Log.i(TAG, Boolean.toString(isAlive));
			if (!isAlive) {
				Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
				return;
			}
		}

		// 为获取地理位置信息时设置查询条件
		// String bestProvider = lm.getBestProvider(getCriteria(), true);
		String bestProvider = lm.NETWORK_PROVIDER;
		// String bestProvider = lm.GPS_PROVIDER; //使用GPS定位方式
		// 获取位置信息
		// 如果不设置查询要求，getLastKnownLocation方法传入的参数为LocationManager.GPS_PROVIDER
		Location mLocation  = lm.getLastKnownLocation(bestProvider);
		if (mLocation == null) {
			// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener); //使用GPS
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
		}
		while (true) {
			mLocation = lm.getLastKnownLocation(bestProvider);
			if (mLocation != null) {
				mLatitude1 = mLocation.getLatitude();
				mLongitude1 = mLocation.getLongitude();
				Log.d("Location", "Latitude: " + mLocation.getLatitude());
				Log.d("Location", "location: " + mLocation.getLongitude());
				// 长时间的监听位置更新可能导致耗电量急剧上升,一旦获取到位置后，就停止监听
				lm.removeUpdates(locationListener);
				break;
			} else {
				Log.d("Location", "Latitude: " + 0);
				Log.d("Location", "location: " + 0);
				lm.removeUpdates(locationListener);
				break;
			}
		}

		//updateView(mLocation);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
	
	// 位置监听
		private LocationListener locationListener = new LocationListener() {

			/**
			 * 位置信息变化时触发
			 */
			public void onLocationChanged(Location location) {
				mLatitude1 = location.getLatitude();
				mLongitude1 = location.getLongitude();
				Log.e(TAG, "时间：" + location.getTime());
				Log.e(TAG, "经度：" + location.getLongitude());
				Log.e(TAG, "纬度：" + location.getLatitude());
				Log.e(TAG, "海拔：" + location.getAltitude());
			}

			/**
			 * Provider状态变化时触发
			 */
			public void onStatusChanged(String provider, int status, Bundle extras) {
				Log.i(TAG, "status changed");

			}

			/**
			 * Provider开启时触发
			 */
			public void onProviderEnabled(String provider) {
				Location location = lm.getLastKnownLocation(provider);
				
			}

			/**
			 * Provider禁用时触发
			 */
			public void onProviderDisabled(String provider) {
				
			}

		};
		
		// 计算两点距离
		private final double EARTH_RADIUS = 6371000.0;  
		private double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
		       double radLat1 = (lat_a * Math.PI / 180.0);
		       double radLat2 = (lat_b * Math.PI / 180.0);
		       double a = radLat1 - radLat2;
		       double b = (lng_a - lng_b) * Math.PI / 180.0;
		       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
		              + Math.cos(radLat1) * Math.cos(radLat2)
		              * Math.pow(Math.sin(b / 2), 2)));
		       s = s * EARTH_RADIUS;
		       s = Math.round(s * 10000) / 10000;
		       return s;
		    }
		
		class GetLocationThread extends Thread{
			private Notify notify;
			private String compcode;
			private String action;
			public GetLocationThread(Notify notify , String compcode){
				this.notify = notify;
				this.compcode = compcode;
				this.action = "getlocation";
			}
			
			public void run(){
				super.run();
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "get_location_apk");
				MultipartEntity en = new MultipartEntity();
				try {
					en.addPart("comp_code", new StringBody(this.compcode));
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
						notify.perform(this.action, builder);
						Log.e(TAG, builder.toString());
					}else{
						checkHandler.sendMessage(Message.obtain(checkHandler, 0));
					}
				}catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					checkHandler.sendMessage(Message.obtain(checkHandler, 0));
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					checkHandler.sendMessage(Message.obtain(checkHandler, 0));
					e.printStackTrace();
				}
			}
		}

		@Override
		public void perform(String action, StringBuilder sBuilder) {
			try {
				JSONObject jsonObject = new JSONObject(sBuilder.toString());
				Log.i(TAG, jsonObject.toString());
				if(action.compareTo("getlocation") == 0){
					mLatitude2 = Double.parseDouble(jsonObject.getString("latitude"));
					mLongitude2 = Double.parseDouble(jsonObject.getString("longitude"));
					checkHandler.sendMessage(Message.obtain(checkHandler, 1));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
