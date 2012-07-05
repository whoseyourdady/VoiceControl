package com.scut.vc.alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * 该类是数据库的帮助类，对数据库的各项操作都是在此进行的。
 * @author Administrator
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String DB_NAME = "Voice_Control.db";
	static SQLiteDatabase db;
	public final static String ALARM_ID = "_id";
	public final static String ALARM_STATE = "ALARM_STATE";
	public final static String CLOCK_TIME = "CLOCK_TIME";
	public final static String CLOCK_DATE = "CLOCK_DATE";
	public final static String TIMES = "TIMES";
	public final static String ALARM_TABLE = "ALARM_INFO";
	

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, 1);
		// TODO Auto-generated constructor stub
		createTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO 创建数据库后，对数据库的操作  

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO 更改数据库版本的操作

	}

	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
		//每次成功打开数据库后首先被执行    
	}
	/**
	 * 建表。。。。
	 */
	private void createTable(){
//		String ti = "";
//		long ID = Long.parseLong(ti);
		try{
			db = this.getWritableDatabase();
			String TimeTable = "CREATE TABLE IF NOT EXISTS ALARM_INFO " + 
			"(" + ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"ALARM_STATE VARCHAR, CLOCK_TIME VARCHAR, CLOCK_DATE VARCHAR, TIMES LONG);" ;
			db.execSQL(TimeTable);
			Log.e("Database", "Create success");
			db.close();
		}catch(SQLException ex){
			Log.e("Database", "Create error");
			db.close();
			return;
			
			
		}
	}

	/*
	 * 以下皆为对各程序的操作
	 */
	
	/**
	 * 插入一个时间的事件
	 */
	public long insertTime(ContentValues cv) {
		db  = this.getWritableDatabase();
		return db.insert(ALARM_TABLE, null, cv);
		
	}

	/**
	 * 删除某一个alarm
	 * @param idOfAlarm alarm的Id
	 */
	public void deleteTime(String id){
		SQLiteDatabase db = getWritableDatabase();
		String[] args = {id};
		db.delete("ALARM_INFO", ALARM_ID+ "=?", args);
		db.close();
	}


	/**
	 * 更新一个闹钟事件的状态
	 * @param id 该闹钟事件的id
	 * @param cv 该闹钟时间所含的内容
	 * @return 返回即更新表的操作
	 */
	public int updateTime(String id, ContentValues cv){
		db = this.getWritableDatabase();
		String where = ALARM_ID +"=?";
		String[] args = {id};
		return db.update(ALARM_TABLE, cv, where, args);	
	}
	
	/**
	 * 获取单个闹铃事件
	 * @param id 该闹铃的id
	 * @return 返回带有闹铃信息的cursor
	 */
	public Cursor getAlarmTime(String id){
		db = this.getReadableDatabase();
		String where = ALARM_ID + "=?";
		String[] whereValues = {id};
		Cursor cursor = db.query(ALARM_TABLE, null, where, whereValues, null, null, null);
		return cursor;
	}
	
	/**
	 * 获取所有闹铃事件
	 * @return 返回含有所有闹铃信息的cursor
	 */
	public Cursor selectAlarmTime(){
		db = this.getReadableDatabase();
		Cursor cursor = db.query(ALARM_TABLE, null, null, null, null, null, null);
		return cursor;
	}
	
}


