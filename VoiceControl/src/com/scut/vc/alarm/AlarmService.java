package com.scut.vc.alarm;


import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service{

	/**
	 * 该Service是为了方便对闹铃监控而设置的
	 * @return null
	 */
	private static AlarmService as = null;
	private DatabaseHelper mydb;                //数据库
	private String idOfAlarm ="";                     //闹铃的
	private long alarmTime = 0;                    //该闹铃设定的时间与当前系统时间的差值
	private int counter = 0;                      //避免在开机启动时误启动onstart（）里面的操作
	private String delId = "";

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
		Log.v("Work", "----1----" + counter);
		if(counter ==0){
			reCountTime();	
			counter = 1;
			Log.v("Work", "----2----" + counter);
		}
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
				if(times > cl.getTimeInMillis()){
					Intent it = new Intent(this,CallAlarm.class);
					it.putExtra("ID", alarmId);
					PendingIntent pit = PendingIntent.getBroadcast
					(this, alarmId, it, 0);
					am.set(AlarmManager.RTC_WAKEUP, 
							times, pit);
				}else{
					Toast.makeText(AlarmService.this, "出错了！", Toast.LENGTH_SHORT);
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
		   
		  //  
			//如果获取的闹铃id为空时，则设定为0；
//			if(idOfAlarm.equals("")){
//				Log.v("Work", "ID  111");
//				idOfAlarm ="0";
//			}
			Log.e("Work", "ID "+ idOfAlarm +" Time "+  alarmTime);
			Log.v("Work", "----2----" + counter);
			if(counter > 1){
				AlarmManager am = (AlarmManager)getSystemService(Service.ALARM_SERVICE);		
				Intent it = new Intent(AlarmService.this,CallAlarm.class);
				PendingIntent pit = PendingIntent.getBroadcast
				(this, Integer.parseInt(idOfAlarm), it, 0);
				am.set(AlarmManager.RTC_WAKEUP, 
						alarmTime, pit);
				Toast.makeText(this, "AlarmSet Finish!", Toast.LENGTH_SHORT).show();		
			}
			counter++;
			Log.v("Work", "----3----" + counter);
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

	}

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return null;
	}	

}
