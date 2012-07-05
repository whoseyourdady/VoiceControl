package com.scut.vc.alarm;


import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;


public class AlarmService extends Service{

	/**
	 * 该Service是为了方便对闹铃监控而设置的
	 * @return null
	 */
	private static AlarmService as = null;
	private DatabaseHelper mydb;                //数据库
	private String idOfAlarm ="";                     //闹铃的id
	private long alarmTime = 0;                    //该闹铃设定的时间与当前系统时间的差值
	private String delId = "";                 //删除的闹铃ID

	/*
	 * 设置一个静态方法保留Service实体，方便其他地方可以获取.
	 * private static Service getService as = null; 用来存储自己的实体
	 * 在onCreate()中使用sms = this；来存储实体
	 * 编写一个静态的getService()来返回实体就可以了
	 */
	public static Service getService()
	{
		return as;
	}

	public void onCreate()
	{
		super.onCreate();
		as = this;
		reCountTime();	
	}

	/*
	 * reCountTime()是为了重新计算闹钟的时间，如果实际时间大于
	 * 预定时间，则不会有反应，反正则重新设定闹铃时间
	 */
	public void reCountTime(){
		mydb = new DatabaseHelper(AlarmService.this);
		//alarmList = mydb.getAllAlarmTime();
		Cursor cursor = mydb.selectAlarmTime();
		Calendar cl = Calendar.getInstance();
		int count = cursor.getCount();
		if(count>0){
			for(int i = 0; i< count; i++){
				cursor.moveToPosition(i);
				int alarmId = Integer.parseInt(cursor.getString(0));
				//String states = cursor.getString(1);
				long times = cursor.getLong(4);
				AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
				//只有当设定的时间大于当前系统的时间时，才会发出广播
				if(times > cl.getTimeInMillis()){
					Intent it = new Intent(this,CallAlarm.class);
					it.putExtra("ID", alarmId);
					PendingIntent pit = PendingIntent.getBroadcast
							(this, alarmId, it, 0);
					am.set(AlarmManager.RTC_WAKEUP, 
							times, pit);					
					Log.v("Work", "Alarm id "+ alarmId +" times " + times);
				}else{
					//Toast.makeText(AlarmService.this, "出错了！", Toast.LENGTH_SHORT);
				}
			}
		}	
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 * 每次增加一个alarm时，就会再次调用该函数
	 */
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);		
		Log.v("Work", "Service start!!!");
		Log.v("Work", intent.getAction().equals("DELID")+"");
		//		
		if(intent.getAction().equals("DELID")){

			delId = intent.getStringExtra("DELID"); 
			Log.e("Work", "  ---delID---- " + delId);
			AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);
			Intent it = new Intent(AlarmService.this,CallAlarm.class);
			//it.putExtra(DatabaseHelper.ALARM_ID, delId);
			PendingIntent pi = PendingIntent.getBroadcast
					(this, Integer.parseInt(delId), it, 0);
			am.cancel(pi);
		}else if(intent.getAction().equals("SETTING")){
			Log.e("Work", "Wrong!!!!");
			//获取AlarmManager
			alarmTime = intent.getLongExtra("Time",0);
			idOfAlarm = intent.getStringExtra("ID");
			Log.e("Work", "ID "+ idOfAlarm +" Time "+  alarmTime);
			AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);		
			Intent it = new Intent(AlarmService.this,CallAlarm.class);
			PendingIntent pit = PendingIntent.getBroadcast
					(this, Integer.parseInt(idOfAlarm), it, 0);
			am.set(AlarmManager.RTC_WAKEUP, 
					alarmTime, pit);
			Log.d("Work", "Alarm "+ idOfAlarm +" Start!!");
		}else{
			Log.v("Work", "Service Wrong!");
		}



	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 * 目前还没任何作用
	 */
	public void onDestroy()
	{
		super.onDestroy();
		AlarmManager am = (AlarmManager)getSystemService
				(Service.ALARM_SERVICE);
		Log.v("Work", "Service End");

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}	

}
