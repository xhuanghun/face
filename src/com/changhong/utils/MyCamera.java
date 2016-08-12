package com.changhong.utils;

import java.io.IOException;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.SurfaceHolder;

public class MyCamera {
	private static String TAG = "MyCamera";
	private static boolean ISOPEN = false;
	public static Camera OpenCamera() {
		if (ISOPEN)
			return null;
		int num = Camera.getNumberOfCameras();
		int id = -1;
		
		CameraInfo info = new CameraInfo();
		for(int i=0; i<num; i++) {
			Camera.getCameraInfo(i, info);
			if(info.facing == CameraInfo.CAMERA_FACING_FRONT) {
				id = i;
				break;
			}
		}
		
		Log.e(TAG, id + "");
		Camera c = null;
		if(id == -1)
			c = Camera.open();
		else
			c = Camera.open(id);
		
		if(c != null)
			Log.i(TAG, "camera open");
		ISOPEN = true;
		return c;
	}
	
	public static void SetupCamera(Camera c, SurfaceHolder holder) {
		if(c == null)
			return;
		Camera.Parameters params = c.getParameters();
		Log.i(TAG, "preview width:"+params.getPreviewSize().width+"preview height:"+params.getPreviewSize().height);
		params.setPreviewSize(MyConfig.IMAGE_HEIGHT, MyConfig.IMAGE_WIDTH);
		params.setPictureSize(MyConfig.IMAGE_HEIGHT, MyConfig.IMAGE_WIDTH);
		params.setPreviewFrameRate(MyConfig.CAPTURE_RATE);
		params.setPictureFormat(PixelFormat.JPEG);
		params.setJpegQuality(MyConfig.JPEG_QUALITY);
//		params.setPictureSize(MyConfig.IMAGE_HEIGHT, MyConfig.IMAGE_WIDTH);
		
		c.setParameters(params);
		c.setDisplayOrientation(90);
		try {
			c.setPreviewDisplay(holder);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		c.startPreview();
	}
	
	public static void CloseCamera(Camera c) {
		if(c == null)
			return;
//		Log.i(TAG, "camera close");
		try {
			c.setPreviewCallback(null);
			c.stopPreview();
			//c.setPreviewCallback(null);
			c.release();
			c = null;
			ISOPEN = false;
			Log.i(TAG, "camera close");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		c.setPreviewCallback(null);
//		c.release();
//		c = null;
	}
}
