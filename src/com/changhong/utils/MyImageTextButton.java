package com.changhong.utils;

import com.changhong.faceattendance.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyImageTextButton extends RelativeLayout {
	private ImageView mImageView;
	private TextView mTextView;

	public MyImageTextButton(Context context) {
		// TODO Auto-generated constructor stub
		this(context, null);
	}
	
    public MyImageTextButton(Context context,AttributeSet attributeSet) {
        super(context, attributeSet);
        
        LayoutInflater.from(context).inflate(R.layout.img_text_bt, this,true);
        
        this.mImageView = (ImageView)findViewById(R.id.mybt_img);
        this.mTextView = (TextView)findViewById(R.id.mybt_text);
        
        this.setClickable(true);
        this.setFocusable(true);
    }

    public void setImgResource(int resourceID) {
        this.mImageView.setImageResource(resourceID);
    }
    
    public void setBackgroundResource(int resid) {
    	this.mImageView.setBackgroundResource(resid);
    }
    
    public void setText(String text) {
        this.mTextView.setText(text);
    }
    
    public void setTextColor(int color) {
        this.mTextView.setTextColor(color);
    }
    
    public void setTextSize(float size) {
        this.mTextView.setTextSize(size);
    }
    
}
