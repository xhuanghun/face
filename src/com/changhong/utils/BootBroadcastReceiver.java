package com.changhong.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import com.changhong.faceattendance.R;
import com.changhong.welcome.myWelcomeActivity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver{
	private static final String ACTION1 = "android.intent.action.BOOT_COMPLETED";
	private static final String ACTION2 = "android.intent.action.startalarm";
	private static final String ACTION3 = "android.intent.action.afternoonalarm";
	private SharedPreferences mchecktime;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	    if (intent.getAction().equals(ACTION1)) {
			Log.i(ACTION1, "接收到开机广播");
			intent.setAction(ACTION2);
            PendingIntent sendIntent1 = PendingIntent.getBroadcast(context, 0, intent, 0);
            AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            manager.setRepeating(AlarmManager.RTC_WAKEUP,
	                getTime(28,8), 24*60*60*1000, sendIntent1); 
            intent.setAction(ACTION3);
            PendingIntent sendIntent2 = PendingIntent.getBroadcast(context, 1, intent, 0);
            manager.setRepeating(AlarmManager.RTC_WAKEUP,
	                getTime(30,17), 24*60*60*1000, sendIntent2); 
        } else if(intent.getAction().equals(ACTION2)) {
    	   Log.i(ACTION2, "收到上午广播，开始显示通知栏");
    	   recTime(context,5*60*60*1000);
        } else if(intent.getAction().equals(ACTION3)) {
    	   Log.i(ACTION3, "收到下午广播，开始显示通知栏");
    	   recTime(context,60*60*1000);	   
        }
    }
	
	private long getTime(int min , int hour) {
		Calendar calendar = Calendar.getInstance();
		System.out.println("calendar----->" + calendar);
		calendar.setTimeInMillis(System.currentTimeMillis());
		// 这里时区需要设置一下，不然会有8个小时的时间差
		calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		long selectTime = calendar.getTimeInMillis();
		System.out.println("selecttime----->" + selectTime);
		long systemTime = System.currentTimeMillis();
		System.out.println("systemtime----->" + systemTime);
		// 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
		if(systemTime > selectTime) {
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			selectTime = calendar.getTimeInMillis();
		}
		//long time = selectTime - systemTime;
		return selectTime;
	}
	
	private void recTime(Context context, long compTime) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int weekday = cal.get(Calendar.DAY_OF_WEEK);
		if(weekday==1 || weekday==7) {
			Log.i(ACTION2, "weekday:"+weekday);
			return;
		}
		
		long checktime = 0;
		mchecktime = PreferenceManager.getDefaultSharedPreferences(context);
		String timeString = mchecktime.getString("mchecktime", "");
//		Log.i(ACTION2, timeString);
		Date newDate = StrToDate(timeString);
		if(newDate != null){
			checktime = newDate.getTime();
		}
		System.out.println("checktime----->" + checktime);
		long currtime = System.currentTimeMillis();
		//String curString = getCurrentTime(currtime);
		//Log.i(TAG, curString);
		System.out.println("systime----->" + currtime);
		long rectime = currtime - checktime;
		if(rectime>compTime || checktime<=0) {
			NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
 		   	Intent appIntent = new Intent(Intent.ACTION_MAIN);  
 		   	appIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
 		   	appIntent.setClass(context, myWelcomeActivity.class);
 		   	appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式  
 		   	PendingIntent contentIntent =PendingIntent.getActivity(context, 0,appIntent,0);
 		   	Notification notification = new Notification.Builder(context)
                                    .setSmallIcon(R.drawable.smalllogoo)
                                    .setTicker("人脸考勤有新消息了！")
                                    .setWhen(System.currentTimeMillis())
                                    .setContentTitle("人脸考勤")
                                    .setContentText("您该签到了！")
                                    .setContentIntent(contentIntent)
                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo))
                                    .build();
 		   	notification.flags |= Notification.FLAG_AUTO_CANCEL;  //通知栏点击取消
 		   	manager.notify(0, notification);
		}
	}
	
	private Date StrToDate(String str) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

}
