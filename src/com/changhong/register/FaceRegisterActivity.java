package com.changhong.register;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.changhong.chni.CHFaceLib;
import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;
import com.changhong.utils.FaceMarkView;
import com.changhong.utils.MyCamera;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;


public class FaceRegisterActivity extends Activity implements Notify{
	private static final String TAG = "FaceRegisterActivity";
	private Button mRegButton;
	private SurfaceView mFaceRegsSurface;
	private Camera mCamera;
	private int mScreenWidth;
	private int mScreenHeight;
	private int mPreWidth;
	private int mPreHeight;
	private int mPreX;
	private int mPreY;
	int QUEUE_SIZE = 0;
	final int QUEUE_MAX_SIZE = 1;
	private String comp_code;
	private String id;
	private String pwd;
	private String email;
	private String name;
	private String tel;
	private FaceMarkView mFaceMarkView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.facereg_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_dataacq);
		
		Intent intent = getIntent();
		comp_code = intent.getStringExtra("comp_code");
//		id = intent.getStringExtra("id");
//		pwd = intent.getStringExtra("pwd");
		email = intent.getStringExtra("email");
		name = intent.getStringExtra("name");
		tel = intent.getStringExtra("tel");
		Log.e(TAG, name);
		String fileDir = getApplicationContext().getFilesDir().getPath();
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
		
		init();
	}

	private void init() {
		
		ImageView imageView = (ImageView)findViewById(R.id.ret2register);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mRegButton = (Button)findViewById(R.id.facereg_btn);
		mRegButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRegButton.setText("注册中...");
				mRegButton.setEnabled(false);
				QUEUE_SIZE = 0;
				mCamera.setPreviewCallback(new cloud_register_face());
				/*if(flag == 0){
					mType = 0;
					mCamera.setPreviewCallback(new cloud_register_face());
					//mCamera.takePicture(null, null, new cloud_register());
				}else{
					mType = 1;
					mCamera.setPreviewCallback(new cloud_register_face());
				}*/
				//mCamera.setPreviewCallback(new cloud_register_face());
				//mCamera.takePicture(null, null, new cloud_register());
				//new Check_API(FaceRegisterActivity.this).start();  for test
			}
		});
		
		WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();
		float scale = getApplicationContext().getResources().getDisplayMetrics().density;
		Log.i(TAG, "window width:"+mScreenWidth+" height:"+mScreenHeight+" density:"+scale);
		
		mFaceMarkView = (FaceMarkView)findViewById(R.id.reg_face_mark_view);
		mFaceRegsSurface = (SurfaceView)findViewById(R.id.facereg_view);
		mPreWidth = mScreenWidth*4/5;
		mPreHeight = MyConfig.IMAGE_HEIGHT*100/MyConfig.IMAGE_WIDTH*mPreWidth/100;
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mPreWidth, mPreHeight);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.facereg_note_text);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		layoutParams.topMargin = (int)(10*scale);
		mFaceRegsSurface.setLayoutParams(layoutParams);
		
		mFaceRegsSurface.getHolder().addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				MyCamera.CloseCamera(mCamera);
				mCamera = null;
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				Camera tmp = MyCamera.OpenCamera();
				if(null != tmp)
					mCamera = tmp;
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				// TODO Auto-generated method stub
				mPreX = (int) mFaceRegsSurface.getX();
				mPreY = (int) mFaceRegsSurface.getY();
				Log.i(TAG, "surfaceChanged"+" width:"+width+" height:"+height);
				MyCamera.SetupCamera(mCamera, holder);
				
				int x1 = (int)mRegButton.getTop();
				int x2 = (int)mRegButton.getBottom();
				int w  = x2 - x1;
				int y1 = (int)mFaceRegsSurface.getBottom();
		        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.relativelayout);
				int y2 = (int)relativeLayout.getHeight();
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mScreenWidth*4/5,LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
				lp.topMargin = (y1 + y2) / 2 - w / 2;
				mRegButton.setLayoutParams(lp);
			}
		});

	}
	
	public Handler faceregHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case 0:
				CHFaceLib.FaceDesc[] fd = new CHFaceLib.FaceDesc[0];
				if (mCamera != null) {
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
							
							
							mRegButton.setText("开始注册");
							mRegButton.setEnabled(true);
							
							Intent intent = new Intent(getApplicationContext(),ShowPicActivity.class);
//							intent.putExtra("pic", nbmp);
							intent.putExtra("comp_code", comp_code);
							intent.putExtra("email", email);
							intent.putExtra("name", name);
							intent.putExtra("tel", tel);
							intent.putExtra("pic", Bitmap2Bytes(nbmp));
							Log.e(TAG, Bitmap2Bytes(nbmp).length + "");
							startActivity(intent);
							mFaceMarkView.clearMark();
							
//							if(mType == 0){
//							new FaceRegThread(FaceRegisterActivity.this, comp_code, id, pwd, email, name, Bitmap2Bytes(nbmp)).start();
//							}else{
//								new FaceRecThread(FaceRegisterActivity.this, comp_code, id, Bitmap2Bytes(nbmp)).start();
//							}
							if(mCamera != null){
								mCamera.setPreviewCallback(null);
							}
						}
						
					}
				}
				break;
			case 1:
				mFaceMarkView.clearMark();
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				Editor editor = pref.edit();
				editor.putString("owner_company", comp_code);
//				editor.clear();
				editor.putString("owner_id", id);
				editor.putString("owner_pwd", pwd);
				editor.putString("owner_name", name);
				editor.commit();
				Toast.makeText(getApplicationContext(), 
						"注册完成", Toast.LENGTH_LONG).show();
				mRegButton.setText("开始注册");
				mRegButton.setEnabled(true);
//				flag++;
				StartLoginActivity();
				break;
			case 2:
				mFaceMarkView.clearMark();
				Toast.makeText(getApplicationContext(), 
						"公司代码不合法", Toast.LENGTH_LONG).show();
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				break;
			case 3:
				mFaceMarkView.clearMark();
				Toast.makeText(getApplicationContext(), 
						"用户已注册", Toast.LENGTH_LONG).show();
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				break;
			case 4:
				mFaceMarkView.clearMark();
				Toast.makeText(getApplicationContext(), 
						"", Toast.LENGTH_LONG).show();
				break;
			case 5:
				mFaceMarkView.clearMark();
				mRegButton.setText("开始注册");
				mRegButton.setEnabled(true);
				Toast.makeText(getApplicationContext(), 
						"未检测到人脸，请重试", Toast.LENGTH_LONG).show();
				break;
			case 99:
				mFaceMarkView.clearMark();
				mRegButton.setText("开始注册");
				mRegButton.setEnabled(true);
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};
	/*class cloud_register implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			Matrix matrix = new Matrix();
			matrix.postRotate((float) -90.0);
			matrix.postScale(-1, 1);
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			//Log.e(TAG, "register img size: " + bmp.getWidth() + "x" + bmp.getHeight());
			Bitmap nbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
			Log.e(TAG, camera.getParameters().getPictureSize().width + "x"
					+ camera.getParameters().getPictureSize().height);
			new FaceRegThread(FaceRegisterActivity.this, comp_code, id, pwd, email, name, Bitmap2Bytes(nbmp)).start();
			//mCamera.startPreview();
		}
	}*/
	class cloud_register_face implements Camera.PreviewCallback{

		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			faceregHandler.sendMessage(
					Message.obtain(faceregHandler,0,camera.getParameters().getPreviewSize().width,
							camera.getParameters().getPreviewSize().height, data));
		}
		
	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}
	
	class FaceRegThread extends Thread{
		private Notify notify;
		private String action;
		private String comp_code;
		private String id;
		private String pwd;
		private String email;
		private String name;
		private byte[] imagefile;
		
		public FaceRegThread(Notify notify, String comp_code, String id, String pwd, String email, 
				String name, byte[] imagefile){
			this.notify = notify;
			this.action = "face_reg";
			this.comp_code = comp_code;
			this.id = id;
			this.pwd = pwd;
			this.email = email;
			this.name = name;
			this.imagefile = imagefile;
		}
		
		public void run (){
			super.run();
//			String actionString = "user_registration_apk";
//			String serverUrl = MyConfig.SERVER_ADDR + actionString;
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "user_registration_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				Log.e(TAG, "comp_code:" + this.comp_code + "id:" + this.id);
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("pwd", new StringBody(this.pwd));
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
					notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 1));
				} else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 434){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 2));
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 428){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 3));
				}else if(httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 437){
					StringBuilder builder = new StringBuilder();
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(httpResponse.getEntity().getContent()));
					for(String s=bufferedReader.readLine(); s!=null; 
							s=bufferedReader.readLine()) {
						builder.append(s);
					}
					notify.perform(this.action, builder);
					Log.e(TAG, builder.toString());
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 5));
				}else{
					Log.e(TAG, "http not extcute");
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 99));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				faceregHandler.sendMessage(Message.obtain(faceregHandler, 99));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				faceregHandler.sendMessage(Message.obtain(faceregHandler, 99));
				e.printStackTrace();
			}
		}
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
					notify.perform("rec", builder);
					Log.e(TAG, builder.toString());
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 4));
				}else{
					Log.e(TAG, "http not extcute");
					faceregHandler.sendMessage(Message.obtain(faceregHandler, 99));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				faceregHandler.sendMessage(Message.obtain(faceregHandler, 99));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				faceregHandler.sendMessage(Message.obtain(faceregHandler, 99));
				e.printStackTrace();
			}
		}
	}
	
	class Check_API extends Thread{
		private Notify notify;
		
		public Check_API(Notify notify){
			notify = this.notify;
		}
		
		public void run(){
			super.run();
			
			try{ 
				HttpClient httpClient = new DefaultHttpClient(); 
				
				HttpGet httpGet = new HttpGet(MyConfig.SERVER_ADDR + "api_check"); 
				HttpResponse httpResponse = httpClient.execute(httpGet); 
				if(httpResponse.getStatusLine().getStatusCode()==200){
					Log.e(TAG, "http ok");
					String result = EntityUtils.toString(httpResponse.getEntity());
					Log.e(TAG, result);
				} 
				}catch(Exception e){} 
		}
	}
	
	private void StartLoginActivity(){
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
			double scaleX = (double)(mFaceRegsSurface.getWidth()) / MyConfig.IMAGE_WIDTH;
			double scaleY = (double)(mFaceRegsSurface.getHeight()) / MyConfig.IMAGE_HEIGHT;
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		CHFaceLib.nativeReleaseFaceEngine();
	}
	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
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

}

	