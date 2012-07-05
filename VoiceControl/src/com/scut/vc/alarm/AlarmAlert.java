package com.scut.vc.alarm;

import java.io.IOException;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
/**
 * 闹铃启动的Activity
 * @author Administrator
 *
 */

public class AlarmAlert extends Activity
{
	// MediaPlayer实例   
	private MediaPlayer player; 
	private DatabaseHelper mydb;
	private String stateInfo = "快完成你制定的计划吧!!!";                //提示框中的信息
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);		
		getAlarmState();		
		palyMusic();
		new AlertDialog.Builder(AlarmAlert.this)
		.setIcon(R.drawable.ic_dialog_alert)
		.setTitle("闹钟响了!!")
		.setMessage(stateInfo)
		.setPositiveButton("关掉它",
				new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				AlarmAlert.this.finish();
				stopMusic();
			}
		})
		.show();
	} 
	//播放闹铃
	public void palyMusic(){
		if(player == null)
		{
			Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
			try {
				player = new MediaPlayer();
				player.setDataSource(this, uri);
				final AudioManager audioManager = (AudioManager)this
				.getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
					player.setAudioStreamType(AudioManager.STREAM_ALARM);
					player.setLooping(true);
					player.prepare();
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(!player.isPlaying()) {
            player.start();
        }
	}
	//停止闹铃
	 public void stopMusic() {   
	        if (player != null) { 
	            player.stop();   
	            try {   
	                // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数   
	                player.prepare();   
	            } catch (IOException ex) {   
	                ex.printStackTrace();   
	            }   
	        }
	    }
	 
	 /**
	  * 设置提示框中的信息，默认为"快完成你制定的计划吧!!!"
	  */
	 public void getAlarmState(){
			mydb = new DatabaseHelper(AlarmAlert.this);
			Intent i = getIntent();
			int id  = i.getIntExtra("ID",-1);
			if(id == -1){
				Log.d("Work", "SB    +     id   ");
				stateInfo = "快完成你制定的计划吧!!!";
			}else{
				Log.d("Work", "SB    +     id   " + id);
				Cursor cursor = mydb.getAlarmTime(""+ id);
				if(cursor.moveToFirst()){
					stateInfo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALARM_STATE));	
				}
				
			}
	 }
	
}
