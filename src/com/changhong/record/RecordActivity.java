package com.changhong.record;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.changhong.utils.ConnectDialog;
import com.changhong.utils.GlideCircleTransform;
import com.changhong.utils.MyConfig;
import com.changhong.utils.Notify;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class RecordActivity extends Activity implements Notify{
	private static final String TAG = "RecordActivity";
	private final static int GET_RECORD = 0;
	private final static int NETWORK_ERROR = 99;
	private String mComp_code;
	private String mUid;
	private String mName;
	private String mDep;
	private JSONArray mJsonArray = null;
	private Dialog mDialog = null;
	public SharedPreferences mchecktime;
	private ImageView imgView;
	private String url;
	
	private RecordAdapter mAdapter;
	private ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.record_listview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_record);
		/*SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.list_view,
				new String[]{"id", "dep", "name", "time", "img"}, 
				new int[]{R.id.vlist_id, R.id.vlist_dep, R.id.vlist_name,
					R.id.vlist_time, R.id.vlist_img});
		setListAdapter(adapter);*/
		//imgView = (ImageView)findViewById(R.id.vlist_img);
		mListView = (ListView)findViewById(R.id.record_listview);
		mAdapter = new RecordAdapter();
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mComp_code = pref.getString("owner_company", "");
		mUid = pref.getString("owner_id", "default");
		mName = pref.getString("owner_name", "default");
		mDep = pref.getString("owner_dep", "default");
		showLoadingDialog(this, "正在获取数据，请稍后...");
		new GetPicThread(RecordActivity.this, mComp_code, mUid).start();
		Log.e(TAG, "mComp_code" + mComp_code +"mUid" + mUid + "mName" + mName);
		//new GetRecordThread(RecordActivity.this, mComp_code, mUid, "30").start();
		
		
		ImageView imageView = (ImageView) findViewById(R.id.ret2checkin);
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
	}
	
	private void showLoadingDialog(Context context, String msg) {
		if(TextUtils.isEmpty(msg)) {
			msg = ConnectDialog.DIALOG_MSG;
		}
		if(mDialog!=null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
		mDialog = ConnectDialog.createLoadingDialog(context, msg);
		mDialog.show();
	}
	
	private void dismissLoadingDialog() {
		if(mDialog == null)
			return;
		mDialog.dismiss();
		mDialog = null;
	}
	
	/*private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        
        if(mJsonArray == null) {
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("id", "�޼�¼");
	        map.put("dep", " ");
	        map.put("name", " ");
	        map.put("time", " ");
	        map.put("img", R.drawable.my_pressed);
	        list.add(map);
        } else {
        	for(int i=mJsonArray.length()-1; i>=0; i--) {
				try {
					JSONObject jsonObject = mJsonArray.getJSONObject(i);
					String checkintime = jsonObject.getString("checkin");
					if(i == (mJsonArray.length()-1)) {
						mchecktime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						Editor editor = mchecktime.edit();
						editor.putString("mchecktime", checkintime);
						editor.commit();
						Log.i(TAG, "success!"+checkintime);
					}
	        		Map<String, Object> map = new HashMap<String, Object>();
	        		map.put("id", "ID:"+mUid);
	        		map.put("dep", mComp_code);
	        		map.put("name", mName);
	        		map.put("time", checkintime);
	        		map.put("img", R.drawable.my_pressed);
	        		list.add(map);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }

        return list;
	}*/
	
	public Handler getRecordHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//dismissLoadingDialog();
			switch(msg.what) {
			case GET_RECORD:
				dismissLoadingDialog();
				/*SimpleAdapter adapter = new SimpleAdapter(RecordActivity.this, getData(), R.layout.list_view,
						new String[]{"id", "dep", "name", "time", "img"}, 
						new int[]{R.id.vlist_id, R.id.vlist_dep, R.id.vlist_name,
							R.id.vlist_time, R.id.vlist_img});
				setListAdapter(adapter);*/
				mListView.setAdapter(mAdapter);
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), 
						"网络问题", Toast.LENGTH_LONG).show();
				break;
			case 1:
				new GetRecordThread(RecordActivity.this, mComp_code, mUid, "30").start();
				break;
			case 2:
				dismissLoadingDialog();
				Toast.makeText(getApplicationContext(), 
						"没有签到记录", Toast.LENGTH_LONG).show();
//				View view = View.inflate(getApplicationContext(), R.layout.list_view, null);
//				ViewHolder holder = (ViewHolder) view.getTag();
//				holder.id.setText("无记录");
//				holder.company.setText("");
//				holder.name.setText("");
//				holder.time.setText("");
//				Glide.with(RecordActivity.this).load(url).transform(new GlideCircleTransform(RecordActivity.this)).into(holder.img);
				break;
			default:
				break;
			}
		};
	};
	
	class RecordAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mJsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			
			return 0;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String checkintime = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.list_view, null);
				new ViewHolder(convertView);
			}
			ViewHolder holder = (ViewHolder) convertView.getTag();
			Glide.with(RecordActivity.this).load(url).transform(new GlideCircleTransform(RecordActivity.this)).into(holder.img);
			JSONObject jsonObject;
			try {
				jsonObject = mJsonArray.getJSONObject(mJsonArray.length()-1-position);
				checkintime = jsonObject.getString("checkin");
			} catch (JSONException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mchecktime = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = mchecktime.edit();
			editor.putString("mchecktime", checkintime);
			editor.commit();
			//Log.i(TAG, "success!"+checkintime);
			holder.id.setText(mUid);
			holder.company.setText(mComp_code);
			holder.name.setText(mName);
			holder.time.setText(checkintime);
			
			return convertView;
		}
		
	}
	
	class ViewHolder{
		ImageView img;
		TextView id;
		TextView company;
		TextView name;
		TextView time;
		
		public ViewHolder(View view){
			img = (ImageView)view.findViewById(R.id.vlist_img);
			id = (TextView)view.findViewById(R.id.vlist_id);
			company = (TextView)view.findViewById(R.id.vlist_dep);
			name = (TextView)view.findViewById(R.id.vlist_name);
			time = (TextView)view.findViewById(R.id.vlist_time);
			view.setTag(this);
		}
	}
	
	class GetRecordThread extends Thread {
		private Notify notify;
		private String comp_code;
		private String id;
		private String days;
		
		public GetRecordThread(Notify notify, String comp_code, String id, String days) {
			this.notify = notify;
			this.comp_code = comp_code;
			this.id = id;
			this.days = days;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(MyConfig.SERVER_ADDR + "get_record_apk");
			MultipartEntity en = new MultipartEntity();
			try {
				Log.i(TAG, "CheckinThread user id :"+this.id);
				en.addPart("comp_code", new StringBody(this.comp_code));
				en.addPart("id", new StringBody(this.id));
				en.addPart("days", new StringBody(this.days));
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
					notify.perform("getrecord", builder);
				}else if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == 204){
					
					getRecordHandler.sendMessage(Message.obtain(getRecordHandler, 2));
				}
				else {
					// TODO:notify register fail
					getRecordHandler.sendMessage(Message.obtain(getRecordHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				getRecordHandler.sendMessage(Message.obtain(getRecordHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				getRecordHandler.sendMessage(Message.obtain(getRecordHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
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
					getRecordHandler.sendMessage(Message.obtain(getRecordHandler, 1));
					notify.perform("getpicture", builder);
				} else {
					// TODO:notify register fail
					getRecordHandler.sendMessage(Message.obtain(getRecordHandler, NETWORK_ERROR));
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				getRecordHandler.sendMessage(Message.obtain(getRecordHandler, NETWORK_ERROR));
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				getRecordHandler.sendMessage(Message.obtain(getRecordHandler, NETWORK_ERROR));
				e.printStackTrace();
			}
		}
	}
	
	
	@Override
	public void perform(String action, StringBuilder sBuilder) {
		// TODO Auto-generated method stub
		if(action.equals("getpicture")){
			try {
				JSONObject jsonObject = new JSONObject(sBuilder.toString());
				String status = jsonObject.getString("status");
				url = jsonObject.getString("msg");
				Log.v(TAG, "status:" + status + "msg:" + url);
				//Glide.with(RecordActivity.this).load(msg);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}else{
			try {
				Log.i(TAG, "perform"+sBuilder.toString());
				mJsonArray = new JSONArray(sBuilder.toString());
//				JSONObject jsonObject;
//				for(int i=0; i<mJsonArray.length(); i++) {
//					jsonObject = mJsonArray.getJSONObject(i);
//					Log.i(TAG, "item"+i+":"+jsonObject.toString());
//				}
				getRecordHandler.sendMessage(Message.obtain(getRecordHandler, GET_RECORD));
				
				
			} catch (JSONException e) {
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

}
