package com.scut.vc.utility;



import java.util.Calendar;

import com.scut.vc.alarm.AlarmService;
import com.scut.vc.alarm.DatabaseHelper;
import com.scut.vc.alarm.Str2DateTime;
import com.scut.vc.alarm.Str2DateTimeUtil;
import com.scut.vc.ui.AlarmActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;


public class Alarm {

	private Context context;
	private String alarmStr;
	private long alarmTimeCounter = 0;
	private DatabaseHelper mydb ; 
	private Calendar c = Calendar.getInstance();
	private Cursor cursor;
	private String alarmID;
/**
 * 
 * @param context
 * @param alarmStr   传入的字符串，比如：今天晚上九点开会
 */

	public Alarm(Context context, String alarmStr){
		this.context = context;
		this.alarmStr = alarmStr;
	}

	/**
	 * 该函数是将传入的字符串进行解析，并使之存入数据库中，同时激活Service，
	 * 跳转到Alarm界面
	 */
	public void Execute(){
	
		//对字符串进行语义解析
		Str2DateTime s2dt = new Str2DateTime(alarmStr);
		String dateTimeStr = s2dt.Analysis();
		Str2DateTimeUtil s2dtu = new Str2DateTimeUtil(dateTimeStr);
		alarmTimeCounter = s2dtu.str2LongTimes();
		final String time2Str = s2dtu.getTime2Str();
		final String date2Str = s2dtu.getDate2Str();
		if(c.getTimeInMillis() > alarmTimeCounter){
			new AlertDialog.Builder(context)
			.setTitle("注意！")
			.setMessage("时间输入错误或已过期")
			.setNegativeButton("确定", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub

				}			
			}).show();
		}else{
			//将数据存入数据中
			new AlertDialog.Builder(context)
			.setTitle("将设置备忘提醒")
			.setMessage("确定输入：“"+ alarmStr + "”到备忘提醒中?")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					mydb = new DatabaseHelper(context);
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.ALARM_STATE,alarmStr);
					cv.put(DatabaseHelper.CLOCK_TIME, time2Str);
					cv.put(DatabaseHelper.CLOCK_DATE, date2Str);
					cv.put(DatabaseHelper.TIMES,alarmTimeCounter);
					mydb.insertTime(cv);
					cursor = mydb.selectAlarmTime();
					if(cursor.getCount()!=0){
						cursor.moveToFirst();
						while(cursor.getPosition() != cursor.getCount()){
							alarmID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALARM_ID));
							cursor.moveToNext();
						}
					}
					//通过intent将设定的时间间距和alarm的id和设定的时间发送给Service,同时启动AlarmActivity
					 Intent iService = new Intent(context,AlarmService.class);
					 Intent iActivity = new Intent(context,AlarmActivity.class);
					iService.putExtra("ID", alarmID);
					iService.putExtra("Time",alarmTimeCounter);
					iService.setAction("SETTING");
					context.startService(iService);
					context.startActivity(iActivity);
				}			
			}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();
			
									
		}

	}
}
