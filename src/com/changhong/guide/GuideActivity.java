package com.changhong.guide;

import java.util.ArrayList;

import com.changhong.faceattendance.R;
import com.changhong.login.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity {
	private final static String TAG = "GuideActivity"; 
	public int[] guides = new int[] {R.drawable.guide_page_1,
			R.drawable.guide_page_2, R.drawable.guide_page_3};
	private ViewPager mViewPager;
	private ArrayList<View> mPageList;
	private ImageView[] mImageViews;
	private ViewGroup indicatorViewGroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.guide_main);
		mViewPager = (ViewPager) findViewById(R.id.guideViewPager);
		mPageList = new ArrayList<View>();
		InitUi();
	}
	
	private void InitUi() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View item = inflater.inflate(R.layout.guide_page, null);  
        mPageList.add(item); 
		for(int index : guides) {
			item = inflater.inflate(R.layout.guide_page, null);
			item.setBackgroundResource(index);
			mPageList.add(item);
		}
		Button btn = (Button) item.findViewById(R.id.guide_button);
		btn.setVisibility(View.VISIBLE);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				StartLoginActivity();
				finish();
			}
		});
		item = inflater.inflate(R.layout.guide_page, null);
		mPageList.add(item);
		
		mImageViews = new ImageView[mPageList.size()-2];
		indicatorViewGroup = (ViewGroup) findViewById(R.id.guideBottom);
		ImageView imageView;
		for(int i=0; i<mImageViews.length; i++) {
			imageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30);
			params.setMargins(10, 10, 10, 10);
			imageView.setLayoutParams(params);  
			imageView.setPadding(30, 0, 30, 0);
			if(i == 0) {
				imageView.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				imageView.setBackgroundResource(R.drawable.page_indicator);
			}
			mImageViews[i] = imageView;
			indicatorViewGroup.addView(mImageViews[i]);
		}
		
		MyViewPageAdapter adapter = new MyViewPageAdapter(mPageList);
		mViewPager.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(adapter);
		mViewPager.setCurrentItem(1);
	}
	
	class MyViewPageAdapter extends PagerAdapter implements OnPageChangeListener {
		private ArrayList<View> mViewList;
		
		public MyViewPageAdapter(ArrayList<View> views) {
			mViewList = views;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			for (int i = 0; i < mImageViews.length; i++) {
				mImageViews[i].setBackgroundResource(R.drawable.page_indicator);
			}
			if(position == 0) {
				mViewPager.setCurrentItem(1);
				mImageViews[position].setBackgroundResource(R.drawable.page_indicator_focused);
			} else if(position == mPageList.size()-1) {
				mViewPager.setCurrentItem(position-1);
				mImageViews[mImageViews.length-1].setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				mImageViews[position-1].setBackgroundResource(R.drawable.page_indicator_focused);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mViewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(mViewList.get(position), 0);
			return mViewList.get(position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(mViewList.get(position));
		}
	}
	
	private void StartLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
	
	@Override	
	public void onBackPressed(){
		finish();
		overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}
}
