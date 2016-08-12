package com.changhong.forgetpsd;

import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class ModifySuccessActivity extends Activity{
	private static final String TAG = "ModifySuccessActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.psdmodifysuccess_layout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar_modifysuccess);
	    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pref.edit();
		editor.remove("owner_id");
		editor.remove("owner_pwd");
		editor.commit();
		
		new Thread(new LoginThreadShow()).start();
	}
	
	@SuppressWarnings("static-access")
	private void StartLoginActivity(){
		Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
		intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		overridePendingTransition(R.anim.rotate_fade_in, R.anim.rotate_fade_out);
	}
	
	class LoginThreadShow implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(2000);
				StartLoginActivity();
//				finishAffinity(); //after 4.1 supported
			} catch (InterruptedException e) {
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
