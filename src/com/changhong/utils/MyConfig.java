package com.changhong.utils;

import java.io.File;

import org.apache.http.client.HttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.os.Environment;

public class MyConfig {
//	public static final String SERVER_ADDR = "http://10.3.85.197:20000/api_beta/Attendance.php";
//	public static final String SERVER_ADDR = "http://10.3.41.92:20000/api_beta/Attendance.php";
//	public static final String SERVER_ADDR = "http://202.98.157.104:20000/api_beta/Attendance_demo.php";
//	public static final String SERVER_ADDR = "http://120.25.121.173:20000/api_beta/api.php?rquest=";
	public static final String SERVER_ADDR = "http://120.25.121.173/api.attendance/v2/";
	public static final int IMAGE_WIDTH = 480;
	public static final int IMAGE_HEIGHT = 640;
	public static final int CAPTURE_RATE = 15;
	public static final int JPEG_QUALITY = 95;
	public static final int SAMPLE_COUNT = 10;
	public static final String OTHER_FEATURE_FILE_NAME = "other.bin";
	public static final String OWNER_FEATURE_FILE_NAME = "ff.bin";
	public static final String PICTURE_FILE_NAME = "pic.jpg";
	public static boolean IS_OWNER = true;
	
	public static void SetNetworkTimeout(HttpClient client, int co, int so) {
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, co);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, so);
	}
	
	 /**
     * 删除APK文件
     */
    public static void DeleteApk() {
    	File file = new File(Environment.getExternalStorageDirectory(), "FaceAttendance.apk");
    	if(file.exists()){
    		file.delete();
    		System.out.println("APK文件已经成功删除");
    	}
    }
}
