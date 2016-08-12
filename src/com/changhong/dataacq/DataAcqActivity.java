package com.changhong.dataacq;

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
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.chni.CHFaceLib;
import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;
import com.changhong.utils.ConnectDialog;
import com.changhong.utils.FaceMarkView;
import com.changhong.utils.MyCamera;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataAcqActivity extends Activity implements Notify{
	private final static String TAG = "DataAcqActivity";
	private SurfaceView mCaptureView = null;
	private Camera mCamera;
	private static final int PREVIEW_IMAGE_DISPLAY = 0;
	private static final int SAMPLE_DONE = 1;
	private static final int UPLOAD_DONE = 2;
	private static final int REGISTER_DONE = 3;
	private static final int LOCAL_DETECT = 4;
	private static final int NETWORK_ERROR = 99;
	private int mScreenWidth;
	private int mScreenHeight;
	private int mPreWidth;
	private int mPreHeight;
	private int mPreX;
	private int mPreY;
	private Button mACQButton;
	private ProgressBar mProgressBar;
	private int mSampleNum;
	private int mProgStep;
	private String mUid;
	private String mPswd;
	private FaceMarkView mFaceMarkView;
	private TextView mProgTextView;
	private boolean mACQFlag = true;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.dataacq_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_dataacq);
		
		InitUi();
	}
	
	private void InitUi() {
		ImageView retButton = (ImageView)this.findViewById(R.id.ret2register);
		retButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mFaceMarkView = (FaceMarkView)this.findViewById(R.id.reg_face_mark_view);
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();
		float scale = getApplicationContext().getResources().getDisplayMetrics().density;
		Log.i(TAG, "window width:"+mScreenWidth+" height:"+mScreenHeight+" density:"+scale);
		
		mCaptureView = (SurfaceView)this.findViewById(R.id.dataacq_view);
		mPreWidth = mScreenWidth*4/5;
		mPreHeight = MyConfig.IMAGE_HEIGHT*100/MyConfig.IMAGE_WIDTH*mPreWidth/100;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mPreWidth, mPreHeight);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.dataacq_bar);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		layoutParams.topMargin = (int)(10*scale);
		mCaptureView.setLayoutParams(layoutParams);
		
		mCaptureView.getHolder().addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				mFaceMarkView.clearMark();
				MyCamera.CloseCamera(mCamera);
				mCamera = null;
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				mCamera = MyCamera.OpenCamera();
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				mPreX = (int) mCaptureView.getX();
				mPreY = (int) mCaptureView.getY();
				Log.i(TAG, "surfaceChanged"+" width:"+width+" height:"+height);
				MyCamera.SetupCamera(mCamera, holder);
				//test
//				mCamera.setPreviewCallback(new DetectImageCallback());
				
				int x1 = (int)mACQButton.getTop();
				int x2 = (int)mACQButton.getBottom();
				int w  = x2 - x1;
				int y1 = (int)mCaptureView.getBottom();
		        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativelayout);
				int y2 = (int)relativeLayout.getHeight();
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mScreenWidth*4/5,LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				lp.topMargin = (y1 + y2) / 2 - w / 2;
				mACQButton.setLayoutParams(lp);
			}
		});
		
		mProgressBar = (ProgressBar)findViewById(R.id.dataacq_bar);
		mProgStep = mProgressBar.getMax()/10;
		Log.i(TAG, "progress step:"+mProgStep);
		mProgTextView = (TextView) findViewById(R.id.dataacq_prog_text2);
		
		mACQButton = (Button)findViewById(R.id.startacq_btn);
		mACQButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mACQFlag) {
					int ret = -1;
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					mUid = pref.getString("owner_uid", "default");
					mPswd = pref.getString("owner_pswd", "default");
//					mUid = "123456"; //test
					Log.i(TAG, "username:"+mUid+" pswd:"+mPswd);
					try {
						ret = CHFaceLib.nativeAddUser(mUid.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(ret != 0) {
						// TODO:give notice to user
						Log.i(TAG, "nativeAddUser error");
					} else {
						Log.i(TAG, "start acq");
						mSampleNum = 0;
						mProgressBar.setProgress(mSampleNum);
						mProgTextView.setText(Integer.toString(mSampleNum));
						mCamera.setPreviewCallback(null);
						mCamera.setPreviewCallback(new CaptureImage());
//						mACQButton.setEnabled(false);
						mACQButton.setText("停止采集");
						mACQFlag = false;
					}
				} else {
					mFaceMarkView.clearMark();
					mCamera.setPreviewCallback(null);
					//test
//					mCamera.setPreviewCallback(new DetectImageCallback());
					mACQButton.setText("开始采集");
					mACQFlag = true;
				}
			}
		});
		

	}
	
	private void AddSampleToEngine(byte[] data) {
		CHFaceLib.FaceDesc[] fd = CHFaceLib.AddSampleForUser(data, MyConfig.IMAGE_HEIGHT, MyConfig.IMAGE_WIDTH);
		if(CHFaceLib.statusCode == -4) {
			Log.i(TAG, "add sample failed, too similar");
		}
		Log.i(TAG, "return of add sample:"+CHFaceLib.statusCode);
		if(fd.length == 1) {
			Log.i(TAG, "add sample success");
			mSampleNum++;
			mProgTextView.setText(Integer.toString(mSampleNum));
			mProgressBar.incrementProgressBy(mProgStep);
		}
		//test
		CHFaceLib.FaceDesc[] detctfd = CHFaceLib.FaceDetect(data, MyConfig.IMAGE_HEIGHT, MyConfig.IMAGE_WIDTH);
		parseDetectResult(detctfd, mPreX, mPreY);
		if(mSampleNum >= MyConfig.SAMPLE_COUNT) {
			if(CHFaceLib.GetUserCount() > 0) {
				int r = CHFaceLib.nativeDelUser(0);
				Log.i(TAG, "delete " + r + " to make sure only one registered user");
			}
			Log.i(TAG, "add sample end");
			CHFaceLib.nativeAddUserEnd();
			mFaceMarkView.clearMark();
			mCamera.setPreviewCallback(null);
			//test
//			mCamera.setPreviewCallback(new DetectImageCallback());
			previewHandler.sendMessage(previewHandler.obtainMessage(
					SAMPLE_DONE));
		}
	}
	
	public Handler previewHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			
			switch(msg.what) {
			case PREVIEW_IMAGE_DISPLAY:
				AddSampleToEngine((byte[])(msg.obj));
				break;
			case SAMPLE_DONE:
				Log.e(TAG, "SAMPLE_DONE start reg:u:"+mUid+" p:"+mPswd);
				Toast.makeText(getApplicationContext(), "采集完成", Toast.LENGTH_SHORT).show();
//				mACQButton.setEnabled(true);
				mACQButton.setText("开始采集");
				mACQFlag = true;
				//test
//				UploadFeatureFile();
				ConnectDialog.showDialog(DataAcqActivity.this, "正在上传数据，请稍后！");
				new registerThread(DataAcqActivity.this, "register", mUid, mPswd).start();
				break;
			case UPLOAD_DONE:
//				if(msg.obj.toString().compareTo("110") == 0) {
//					new registerThread(DataAcqActivity.this, "register", mUid, mPswd).start();
//				} else {
//					Toast.makeText(getApplicationContext(), 
//							"注册失败", Toast.LENGTH_LONG).show();
//				}
				break;
			case REGISTER_DONE:
				ConnectDialog.dismiss();
				if(msg.obj.toString().compareTo("103")==0) {
					Toast.makeText(getApplicationContext(), 
							"注册成功", Toast.LENGTH_LONG).show();
					UploadFeatureFile();
					SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
					Editor editor = pref.edit();
					editor.putBoolean("register_done", true);
					editor.commit();
					StartLoginActivity();
//					finishAffinity(); //after 4.1 supported
				}else {
					Toast.makeText(getApplicationContext(), 
							"注册失败", Toast.LENGTH_LONG).show();
				}
				break;
			case LOCAL_DETECT:
				CHFaceLib.FaceDesc[] detect_fd = CHFaceLib.FaceDetect((byte[])(msg.obj), MyConfig.IMAGE_HEIGHT, MyConfig.IMAGE_WIDTH);
				parseDetectResult(detect_fd, mPreX, mPreY);
				break;
			case NETWORK_ERROR:
				ConnectDialog.dismiss();
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
	
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
	
	@SuppressWarnings("static-access")
	private void StartLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.rotate_fade_in, R.anim.rotate_fade_out);
	}
	
	private void UploadFeatureFile() {
		new UploadFeatureFileThread(DataAcqActivity.this, "upload_feature_file", mUid).start();
	}
	
	class UploadFeatureFileThread extends Thread {
		private Notify notify;
		private String userId;
		private String action;
		
		public UploadFeatureFileThread(Notify notify, String action, String userId) {
			this.notify = notify;
			this.action = action;
			this.userId = userId;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR);
			MultipartEntity en = new MultipartEntity();
			try {
				String fileDir = getApplicationContext().getFilesDir().getPath();
				fileDir += CHFaceLib.defaultConfigFile;
				Log.i(TAG, "upload file:"+fileDir);
				File file = new File(fileDir);
				en.addPart("action", new StringBody(this.action));
				en.addPart("_userId", new StringBody(this.userId));
				en.addPart("_file", new FileBody(file));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpPost);
				Log.i(TAG, "httpClient execute");
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform(this.action, builder);
				} else {
					// TODO:notify register fail
					previewHandler.sendMessage(Message.obtain(previewHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				previewHandler.sendMessage(Message.obtain(previewHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				previewHandler.sendMessage(Message.obtain(previewHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	class registerThread extends Thread {
		private Notify notify = null;
		private String userId;
		private String passWord;
		private String action;
		
		public registerThread(Notify notify, String action, String userId, String passWord) {
			this.notify = notify;
			this.action = action;
			this.userId = userId;
			this.passWord = passWord;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR);
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("action", new StringBody(this.action));
				en.addPart("_userId", new StringBody(this.userId));
				en.addPart("_password", new StringBody(this.passWord));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httpPost);
				if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 200) {
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform(this.action, builder);
				} else {
					// TODO:notify register fail
					previewHandler.sendMessage(Message.obtain(previewHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				previewHandler.sendMessage(Message.obtain(previewHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				previewHandler.sendMessage(Message.obtain(previewHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	class CaptureImage implements Camera.PreviewCallback {

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			//test
//			AddSampleToEngine(data);
			previewHandler.sendMessage(previewHandler.obtainMessage(PREVIEW_IMAGE_DISPLAY, data));
		}
		
	};
	
	class DetectImageCallback implements Camera.PreviewCallback {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			previewHandler.sendMessage(Message.obtain(previewHandler, LOCAL_DETECT, data));
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString());
			String status = jsonObject.getString("status");
			String reason = jsonObject.getString("reason");
			Log.i(TAG, "status:"+status+" reason:"+reason);
			if(action.compareTo("register") == 0) {
				previewHandler.sendMessage(Message.obtain(previewHandler, 
						REGISTER_DONE, status));
			} else if(action.compareTo("upload_feature_file") == 0) {
				previewHandler.sendMessage(Message.obtain(previewHandler,
						UPLOAD_DONE, status));
			}
		} catch (JSONException e) {
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
