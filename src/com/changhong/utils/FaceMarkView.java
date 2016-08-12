package com.changhong.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FaceMarkView extends ImageView{

	private Paint mPaint = null;
	private Rect mRect = null;
	
	public FaceMarkView(Context context) {
		this(context,null);
	}
	public FaceMarkView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public FaceMarkView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mPaint = new Paint();
		mRect = new Rect();
		
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(5.0F);
	}
	
	
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		
//		canvas.drawRect(mRect, mPaint);
		DrawCorner(canvas);
	}

	
	public void markRect(Rect rect, int color){
		mPaint.setColor(color); 
		mRect = rect;
		invalidate();
	}
	
	public void clearMark(){
		mPaint.setColor(Color.TRANSPARENT);
		invalidate();
	}
		
	private void DrawCorner(Canvas canvas) {
		if(mRect == null)
			return;
		float startX, startY, stopX, stopY;
		int length = (mRect.width()<mRect.height()?mRect.width():mRect.height()) / 8;
		
		// left-top corner
		startX = mRect.left;
		startY = mRect.top;
		stopX = startX + length;
		stopY = startY;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		stopX = startX;
		stopY = startY + length;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		
		// left-bottom corner
		startX = mRect.left;
		startY = mRect.bottom;
		stopX = startX + length;
		stopY = startY;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		stopX = startX;
		stopY = startY - length;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		
		// right-top corner
		startX = mRect.right;
		startY = mRect.top;
		stopX = startX - length;
		stopY = startY;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		stopX = startX;
		stopY = startY + length;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		
		// right-bottom corner
		startX = mRect.right;
		startY = mRect.bottom;
		stopX = startX - length;
		stopY = startY;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
		stopX = startX;
		stopY = startY - length;
		canvas.drawLine(startX, startY, stopX, stopY, mPaint);
	}
}
