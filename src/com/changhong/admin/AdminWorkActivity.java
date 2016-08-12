package com.changhong.admin;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.changhong.faceattendance.R;
import com.changhong.utils.GlideCircleTransform;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AdminWorkActivity extends Activity implements Notify{
	private static String TAG = "AdminWorkActivity";
	private ListView mListView;
	private RegisterAdapter mAdapter;
	private JSONArray mJsonArray = null;
	private String mCompCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.admin_work_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_adminwork);
		
		Intent intent = getIntent();
		mCompCode = intent.getStringExtra("compcode");
		
		InitUi();
		
		new GetWorkThread(AdminWorkActivity.this, mCompCode).start();
	}
	
	private void InitUi(){
		mListView = (ListView)findViewById(R.id.admin_work);
		mAdapter = new RegisterAdapter();
	}
	
	public Handler AdminHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
			case 0:
				mListView.setAdapter(mAdapter);
				break;
			default:
				break;
			}
		}
	};
	
	class RegisterAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mJsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.admin_work_item, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			JSONObject jsonObject;
			try {
				jsonObject = mJsonArray.getJSONObject(position);
				Glide.with(AdminWorkActivity.this).load(jsonObject.getString("location")).transform(new GlideCircleTransform(AdminWorkActivity.this)).into(holder.imageView);
				holder.regname.setText(jsonObject.getString("name"));
				holder.regemail.setText(jsonObject.getString("email"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			holder.regadd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//TODO : add funcation
					Log.e(TAG, "add onclick" + position);
				}
			});
			holder.regdelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//TODO : delete funcation
					Log.e(TAG, "delete onclick" + position);
				}
			});
			return convertView;
		}
		
	}
	
	class ViewHolder{
		ImageView imageView;
		TextView regname;
		TextView regemail;
		Button regadd;
		Button regdelete;
		
		public ViewHolder(View view){
			imageView = (ImageView)view.findViewById(R.id.register_pic);
			regname = (TextView)view.findViewById(R.id.register_name);
			regemail = (TextView)view.findViewById(R.id.register_email);
			regadd = (Button)view.findViewById(R.id.register_add);
			regdelete = (Button)view.findViewById(R.id.register_delete);
			view.setTag(this);
		}
	}
	
	class GetWorkThread extends Thread{
		private Notify notify;
		private String compCode;
		
		public GetWorkThread(Notify notify, String compCode){
			this.notify = notify;
			this.compCode = compCode;
		}
		
		public void run(){
			super.run();
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "hr_selection_view_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				Log.i(TAG, "GetWorkThread compCode :"+this.compCode);
				en.addPart("comp_code", new StringBody(this.compCode));
				httpPost.setEntity(en);
			} catch (UnsupportedEncodingException e) {
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
					notify.perform("getreg", builder);
				}
				else {
					//TODO
				}
			} catch (ClientProtocolException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}

	@Override
	public void perform(String action, StringBuilder sBuilder) {
		try {
			Log.i(TAG, "perform"+sBuilder.toString());
			mJsonArray = new JSONArray(sBuilder.toString());
			AdminHandler.sendMessage(Message.obtain(AdminHandler, 0));
			
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
