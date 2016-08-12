package com.changhong.myinfo;

import java.io.BufferedReader;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.changhong.faceattendance.R;
import com.changhong.utils.ConnectDialog;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FeedbackActivity extends Activity implements Notify{
	private static final String TAG = "FeedbackActivity";
	private EditText mfeedbackEditText;
	private Button   mfeedbackcommitBtn;
	private static final int NETWORK_ERROR = 99;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.feedback_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_feedback);
		ImageView imageView = (ImageView)findViewById(R.id.feedback);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mfeedbackEditText = (EditText)findViewById(R.id.feedback_edit);
		mfeedbackEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mfeedbackEditText.setHint(null);
				
			}
		});
		
		mfeedbackcommitBtn = (Button)findViewById(R.id.feedback_btn);
		mfeedbackcommitBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String msg = mfeedbackEditText.getText().toString();
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String comp_code = pref.getString("owner_company", "default");
				String userId = pref.getString("owner_id", "default");
				if(msg.isEmpty()) {
					Toast.makeText(getApplicationContext(), "请输入您的反馈意见", Toast.LENGTH_SHORT).show();
					return;
				}else{
					ConnectDialog.showDialog(FeedbackActivity.this, "正在提交您的反馈意见，请稍后！");
					new feedbackThread(FeedbackActivity.this, comp_code, userId, msg).start();
				}
			}
		});
	}
	
	private Handler feedbackHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			ConnectDialog.dismiss();
			switch (msg.what) {
			case 0:
				//if(msg.obj.toString().compareTo("135") == 0){
				    Toast.makeText(getApplicationContext(), 
						"您的反馈意见已经提交", Toast.LENGTH_LONG).show();
				    mfeedbackEditText.setText(null);
				//}
				break;
				
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break; 

			default:
				break;
			}
		}
	};
	
	class feedbackThread extends Thread{
		private Notify notify = null;
		private String comp_code;
		private String id;
		private String msg;
		
		public feedbackThread(Notify notify, String comp_code, String id, String msg){
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
			this.msg = msg;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "feedback_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("message_text", new StringBody(this.msg));
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
					notify.perform("feedback", builder);
					Log.e(TAG, builder.toString());
					feedbackHandler.sendMessage(Message.obtain(feedbackHandler, 0));
				} else {
					// TODO:notify register fail
					feedbackHandler.sendMessage(Message.obtain(feedbackHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				feedbackHandler.sendMessage(Message.obtain(feedbackHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				feedbackHandler.sendMessage(Message.obtain(feedbackHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObject = new JSONObject(sBuilder.toString());
			Log.i(TAG, jsonObject.toString());
			String status = jsonObject.getString("status");
			String reason = jsonObject.getString("reason");
//			if(action.compareTo("feedback") == 0){
//				feedbackHandler.sendMessage(Message.obtain(feedbackHandler, 0, status));
//			}
		}catch (JSONException e) {
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
