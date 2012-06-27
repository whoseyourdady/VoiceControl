package com.scut.vc.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.scut.vc.alarm.AlarmService;
import com.scut.vc.alarm.DatabaseHelper;
import com.scut.vc.alarm.MyAlarmAdapter;
import com.scut.vc.alarm.Str2DateTime;
import com.scut.vc.alarm.Str2DateTimeUtil;
import com.scut.vc.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * 该Activity是对整个闹铃的设置的
 * @author Administrator
 *
 */

public class AlarmActivity extends Activity{

	
	ImageButton  voiceBtn;               
	DatabaseHelper mydb;                              //声明一个数据库实例
	private ListView alarmListView;
	private Cursor cursor;
	private List<String> ids;                        //将数据库中所有闹铃事件的id存储在一个链表中
	private List<String> tempTimes;                   //暂存数据库中的时间信息
	private List<String> tempDate;
	private List<String> states;
	private String id ="";                             //单个闹铃的id
	public long times=0;
	AlertDialog builder = null;    
	Calendar c=Calendar.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clock);
		mydb = new DatabaseHelper(AlarmActivity.this);
		refresh();	    
		NewAlarm();
		//StopAlarmService();		    
	}

	/**
	 * 新建一个闹钟事件
	 */
	public void NewAlarm()
	{
		voiceBtn=(ImageButton)findViewById(R.id.clock_voice);
		voiceBtn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				final Intent isAlarmService = new Intent(AlarmActivity.this,AlarmService.class);
				String test = "下午五点开会";
				Str2DateTime s2dt = new Str2DateTime(test);
				String test1 = s2dt.Analysis();
				Str2DateTimeUtil s2dtu = new Str2DateTimeUtil(test1);
				times = s2dtu.str2LongTimes();
				String time2str = s2dtu.getTime2Str();
				String date2str = s2dtu.getDate2Str();
				if(c.getTimeInMillis() > times){
					new AlertDialog.Builder(AlarmActivity.this)
					.setTitle("注意！")
					.setMessage("时间输入错误或已过期")
					.setNegativeButton("确定", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}						
					}).show();
				}else{
					mydb = new DatabaseHelper(AlarmActivity.this);
					ContentValues cv = new ContentValues();
					cv.put(DatabaseHelper.ALARM_STATE, test);
					cv.put(DatabaseHelper.CLOCK_TIME, time2str);
					cv.put(DatabaseHelper.CLOCK_DATE, date2str);
					cv.put(DatabaseHelper.TIMES, times);
					mydb.insertTime(cv);
					cursor = mydb.selectAlarmTime();
					if(cursor.getCount()!=0){
						cursor.moveToFirst();
						while(cursor.getPosition() != cursor.getCount()){
							id = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALARM_ID));
							cursor.moveToNext();

						}		   
					}

					Log.e("Work", "id  " + id );
					//通过intent将设定的时间间距和alarm的id和设定的时间发送给Service
					isAlarmService.putExtra("ID", id);
					isAlarmService.putExtra("Time", times);

					isAlarmService.setAction("SETTING");
					startService(isAlarmService);

					//重新对alarmListView进行适配器适配并刷新
					refresh();

				}
				//通过intent传送id和时间间距给Service就可以了
				Log.d("Work", "Now Time  " + c.getTimeInMillis());
				Log.d("Work", "Now Time  2  " + c.getTime());
				Log.v("Work", "Long Time " + times + " " + time2str + " "+date2str);


			}
		});
	}


	/**
	 * 更新listview并将数据库中的数据填充到listview中
	 */
	public void refresh(){
		cursor = mydb.selectAlarmTime();
		ids = new ArrayList<String>();
		tempTimes = new ArrayList<String>();
		states = new ArrayList<String>();
		tempDate = new ArrayList<String>();
		int count = cursor.getCount();
		//只有数据库中有数据时才能运行
		if(count>0){
			for(int i = 0; i< count; i ++){
				cursor.moveToPosition(i);
				ids.add(cursor.getString(0));
				states.add(cursor.getString(1));
				tempTimes.add(cursor.getString(2));
				tempDate.add(cursor.getString(3));

			}			
		}else{
		}	
		cursor.close();
		alarmListView = (ListView)findViewById(R.id.alarmList);
		alarmListView.setAdapter(new MyAlarmAdapter(AlarmActivity.this, states,tempTimes,tempDate,ids));
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if(keyCode == KeyEvent.KEYCODE_BACK){
			builder = new AlertDialog.Builder(AlarmActivity.this)
			.setIcon(R.drawable.clock)
			.setTitle("温馨提示：")
			.setMessage("您是否要返回到主界面？")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					Intent i = new Intent(AlarmActivity.this,MainActivity.class);
					startActivity(i);
					AlarmActivity.this.finish();
					
				}
			})
			.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int whichButton) {
					builder.dismiss();
				}
			}).show();
		}
		return true;
	}

}


