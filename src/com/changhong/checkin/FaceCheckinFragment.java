package com.changhong.checkin;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.chni.CHFaceLib;
import com.changhong.faceattendance.R;
import com.changhong.myinfo.MyinfoActivity;
import com.changhong.record.RecordActivity;
import com.changhong.register.FaceRegisterActivity;
import com.changhong.utils.BaseLazyFragment;
import com.changhong.utils.FaceMarkView;
import com.changhong.utils.MyCamera;
import com.changhong.utils.MyConfig;
import com.changhong.utils.MyImageTextButton;
import com.changhong.utils.Notify;

/**
 *@author 作者：雷传涛  E-mail:chuantao.lei@changhong.com
 *date    创建时间：2016-6-7上午11:11:04
 */
public class FaceCheckinFragment extends BaseLazyFragment implements Notify{
	private final static String TAG = "FaceCheckinFragment";
	private boolean isPrepared;
	public static SurfaceView mCaptureView = null;
	private int mScreenWidth;
	private int mScreenHeight;
	private float mScale;
	private int mPreWidth;
	private int mPreHeight;
	private int mPreX;
	private int mPreY;
	public static Camera mCamera0 = null;
	private Button mCheckinButton;
	private FaceMarkView mFaceMarkView;
	private MyImageTextButton[] mButtonViews;
	private MyImageTextButton mButtonView;
	private TextView mFeatureTextView;
	private ViewGroup tabViewGruop;
	int QUEUE_SIZE = 0;
	final int QUEUE_MAX_SIZE = 1;
	
	private String comp_code;
	private String id;
	private String name;
	private static Context activityContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		//setContentView(R.layout.checkin_layout);
		getActivity().getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_checkin);
		activityContext = getActivity();
		
		WindowManager wm = (WindowManager)activityContext.getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();
		mScale = activityContext.getApplicationContext().getResources().getDisplayMetrics().density;
		Log.i(TAG, "window width:"+mScreenWidth+" height:"+mScreenHeight+" density:"+mScale);
		
		
		String fileDir = activityContext.getApplicationContext().getFilesDir().getPath();
		fileDir += CHFaceLib.defaultConfigFile;
		try {
			int ret1 = CHFaceLib.nativeInitFaceEngine(fileDir.getBytes("US-ASCII"));
			if (0 == ret1) {
				Log.e(TAG, "FaceLib ok");
			} else {
				Log.e(TAG, "FaceLib failed");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SharedPreferences mypref = PreferenceManager.getDefaultSharedPreferences(activityContext.getApplicationContext());
		comp_code = mypref.getString("owner_company", "");
		id = mypref.getString("owner_id", "");
		name = mypref.getString("owner_name", "");
		Log.e(TAG, name);
		//init();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return init(inflater);
	}

	private View init(LayoutInflater inflater){
		final View view = inflater.inflate(R.layout.checkin_layout, null, false);
		isPrepared = true;
//		ImageView imageView = (ImageView) view.findViewById(R.id.checkinret2login);
//		imageView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				MyCamera.CloseCamera(mCamera);
//				mCamera = null;
//				getActivity().finish();
//				getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//			}
//		});
		
		mFaceMarkView = (FaceMarkView)view.findViewById(R.id.face_mark_view);
		mCaptureView = (SurfaceView)view.findViewById(R.id.checkin_view);
		mFeatureTextView = (TextView)view.findViewById(R.id.featurefile_text);
		mFeatureTextView.setText("Hello:" + name);
		
		mPreWidth = mScreenWidth*13/14;
		mPreHeight = MyConfig.IMAGE_HEIGHT*100/MyConfig.IMAGE_WIDTH*mPreWidth/100;
		Log.i(TAG, "capture width:"+mPreWidth+" height:"+mPreHeight);	
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mPreWidth, mPreWidth);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.checkin_note_text);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		layoutParams.topMargin = (int)(20*mScale);
		mCaptureView.setLayoutParams(layoutParams);
		mCaptureView.getHolder().setFixedSize(mPreWidth, mPreHeight);
		
		mCaptureView.getHolder().addCallback(new SurfaceHolder.Callback(){

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
//				Camera tmp = MyCamera.OpenCamera();
//				if(null != tmp)
//					mCamera0 = tmp;
				mCamera0 = CheckinActivity.temp;
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				// TODO Auto-generated method stub
				mPreX = (int) mCaptureView.getX();
				mPreY = (int) mCaptureView.getY();
				Log.i(TAG, "surfaceChanged"+" width:"+width+" height:"+height+" format:"+format+" x:"+mPreX+" y:"+mPreY);
				MyCamera.SetupCamera(mCamera0, holder);
				//mCamera.setPreviewCallback(new DetectImageCallback());
            	int x1 = (int) mCheckinButton.getTop();
				int x2 = (int) mCheckinButton.getBottom();
				int w = x2 - x1 ;
				int y1 = (int) mCaptureView.getBottom();
				int y2 = (int) tabViewGruop.getTop();
				//System.out.println("y1------>" + y1);
				//System.out.println("y2------>" + y2);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mScreenWidth*13/14,LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				lp.topMargin = (y2 + y1) / 2 - w/2;
				mCheckinButton.setLayoutParams(lp);
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				mFaceMarkView.clearMark();
				MyCamera.CloseCamera(mCamera0);
				mCamera0 = null;
			}
			
		});
		
		mCheckinButton = (Button)view.findViewById(R.id.startcheckin_btn);
		mCheckinButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(activityContext, CheckinActivity.dis + "", Toast.LENGTH_SHORT).show();
				QUEUE_SIZE = 0;
				mCamera0.setPreviewCallback(new cloud_recognition_face());		
			}
		});
		
		tabViewGruop = (ViewGroup)view.findViewById(R.id.tabviewgroup);
		mButtonViews = new MyImageTextButton[3];
		for(int i=0; i<mButtonViews.length; i++) {
			mButtonView = new MyImageTextButton(activityContext, null);
			int w = mScreenWidth / 3;
			int h = tabViewGruop.getLayoutParams().height;
			Log.i(TAG, "tab width:"+w+" height:"+h);
			mButtonView.setLayoutParams(new LayoutParams(w, h));
			w = (int) ((w - 30*mScale) / 2);
			mButtonView.setPadding(w, 0, w, 0); 
			switch(i) {
			case 0:
				mButtonView.setBackgroundResource(R.drawable.checkicon_pressed);
				mButtonView.setText("签到");
				break;
			case 1:
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
				mButtonView.setBackgroundResource(R.drawable.my_normal);
				mButtonView.setText("我的");
				mButtonView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						StartMyinfoActivity();
					}
				});
				break;
			default:
				break;
			}
			mButtonViews[i] = mButtonView;
			tabViewGruop.addView(mButtonViews[i]);
		}
		return view;
	}
	
	public Handler checkinHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				CHFaceLib.FaceDesc[] fd = new CHFaceLib.FaceDesc[0];
				if (mCamera0 != null) {
					fd = CHFaceLib.FaceDetect((byte[]) (msg.obj), msg.arg1, msg.arg2); // TODO : check uninitialized stat in FaceDetect
					parseDetectResult(fd, mPreX, mPreY);
					if (fd.length > 0) {
						// Log.e(TAG, "Face detected");
						// if QUEUE_SIZE less than QUEUE_MAX_SIZE
						if (QUEUE_SIZE < QUEUE_MAX_SIZE) {
							
							// post request
							QUEUE_SIZE++;
							// data is YUV data
							byte[] yuv = (byte[]) (msg.obj);
							YuvImage img = new YuvImage(yuv, ImageFormat.NV21, msg.arg1, msg.arg2, null);
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							Rect r = new Rect();
							r.top = 0;
							r.left = 0;
							r.right = img.getWidth();
							r.bottom = img.getHeight();
							img.compressToJpeg(r, 100, baos);

							byte[] data = baos.toByteArray();
							Matrix matrix = new Matrix();
							matrix.postRotate((float) -90.0);
							matrix.postScale(-1, 1);
							Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
							Bitmap nbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
							
							new FaceRecThread(FaceCheckinFragment.this, comp_code, id, Bitmap2Bytes(nbmp)).start();
							
							if(mCamera0 != null){
								mCamera0.setPreviewCallback(null);
							}
						}
						
					}
				}
				break;
			case 4:
				mFaceMarkView.clearMark();
				StartRecordActivity();
				break;
			case 5:
				mFaceMarkView.clearMark();
				Toast.makeText(activityContext.getApplicationContext(), 
						"签到失败，未能识别", Toast.LENGTH_LONG).show();
				break;
			case 99:
				mFaceMarkView.clearMark();
				Toast.makeText(activityContext.getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
	
	class cloud_recognition_face implements Camera.PreviewCallback{

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			checkinHandler.sendMessage(
					Message.obtain(checkinHandler,0,camera.getParameters().getPreviewSize().width,
							camera.getParameters().getPreviewSize().height, data));
		}
		
	}
	
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	
	class FaceRecThread extends Thread{
		private Notify notify;
		private String comp_code;
		private String id;
		private byte[] imagefile;
		
		public FaceRecThread(Notify notify, String comp_code, String id, byte[] imagefile){
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
			this.imagefile = imagefile;
		}
		
		public void run(){
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "user_recognition_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				Log.e(TAG, "comp_code:" + this.comp_code + "id:" + this.id);
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("imagefile", new ByteArrayBody(this.imagefile,"tmpfilename1.jpg"));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
				Log.e(TAG, "http client execute");
				MyConfig.SetNetworkTimeout(httpClient, 5000, 10000);
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
					notify.perform("rec", builder);
					Log.e(TAG, builder.toString());
					checkinHandler.sendMessage(Message.obtain(checkinHandler, 4));
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 435) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform("rec", builder);
					Log.e(TAG, builder.toString());
					checkinHandler.sendMessage(Message.obtain(checkinHandler, 5));
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 436) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform("rec", builder);
					Log.e(TAG, builder.toString());
					checkinHandler.sendMessage(Message.obtain(checkinHandler, 5));
				}else{
					Log.e(TAG, "http not extcute");
					checkinHandler.sendMessage(Message.obtain(checkinHandler, 99));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				checkinHandler.sendMessage(Message.obtain(checkinHandler, 99));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				checkinHandler.sendMessage(Message.obtain(checkinHandler, 99));
				e.printStackTrace();
			}
		}
	}
	
	private void StartRecordActivity() {
		Intent intent = new Intent(activityContext.getApplicationContext(), RecordActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private void StartMyinfoActivity() {
		Intent intent = new Intent(activityContext.getApplicationContext(), MyinfoActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	private Rect parseDetectResult(CHFaceLib.FaceDesc[] fd, int offset_x, int offset_y){
		Rect r = new Rect();
		if(fd == null) {
			mFaceMarkView.clearMark();
			return null;
		}

		for (int i = 0; i < fd.length; ++i){
			if(fd[i].x < 0) {
				mFaceMarkView.clearMark();
				continue;
			}
//			Log.i(TAG, "fd x :"+fd[i].x+" y:"+fd[i].y+" w"+fd[i].w+" h"+fd[i].h);
			double scaleX = (double)(mCaptureView.getWidth()) / MyConfig.IMAGE_WIDTH;
			double scaleY = (double)(mCaptureView.getHeight()) / MyConfig.IMAGE_HEIGHT;
			r.left = (int)((MyConfig.IMAGE_WIDTH-fd[i].x-fd[i].w) * scaleX) + offset_x;
			r.top = (int)(fd[i].y * scaleY) + offset_y;
			r.right = (int)((MyConfig.IMAGE_WIDTH-fd[i].x)*scaleX) + offset_x;
			r.bottom = (int)((fd[i].y+fd[i].h)*scaleY) + offset_y;
			mFaceMarkView.markRect(r, Color.YELLOW);
			
			return r;
		}
		
		mFaceMarkView.clearMark();
		return null;
	}
	
	
	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString());
			String status = jsonObject.getString("status");
			String msg = jsonObject.getString("msg");
			Log.v(TAG, "status:" + status + "msg:" + msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void onBackPressed(){
//		MyCamera.CloseCamera(mCamera);
//		mCamera = null;
//		getActivity().finish();
//		getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//	}
	
	@Override
	public void onPause() {
		Log.e(TAG, "OnPause");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		super.onDestroy();
	}
	@Override
	public void onStart() {
		Log.e(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.e(TAG, "onStop");
		super.onStop();
	}
	
	@Override
	public void onResume() {
		Log.e(TAG, "onResume");
		lazyload();
//		Camera tmp = MyCamera.OpenCamera();
//		if(null != tmp)
//			mCamera0 = tmp;
//		mCamera0 = CheckinActivity.temp;
		super.onResume();
	}

	@Override
	protected void lazyload() {
//		// TODO Auto-generated method stub
//		Log.e(TAG, "lazy load 0");
//		if(!isPrepared || !isVisible){
//			return;
//		}
//		mCamera0 = CheckinActivity.temp;
//		
//		isPrepared=false;
	}
}
