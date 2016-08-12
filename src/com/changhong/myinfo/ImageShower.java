package com.changhong.myinfo;

import com.bumptech.glide.Glide;
import com.changhong.faceattendance.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;


public class ImageShower extends Activity {
	private final String TAG = "ImageShower";
	private String mUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageshower);
		
		Intent intent = getIntent();
		mUrl = intent.getStringExtra("URL");
		Log.e(TAG, mUrl);
		ImageView imageView = (ImageView)findViewById(R.id.img_loading);
		if(mUrl != null){
			Glide.with(ImageShower.this).load(mUrl).into(imageView);
		}
		final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				dialog.dismiss();
			}
		}, 1000 * 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		finish();
		//overridePendingTransition(R.anim.head_in, R.anim.head_out);
		return true;
	}

}
