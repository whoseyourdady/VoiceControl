package com.scut.vc.alarm;

import java.util.List;

import com.scut.vc.ui.R;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;



/**
 * MyAlarmAdapter继承于BaseAdapter，是对各个时间中的自定义设定
 * @author Administrator
 *
 */

public class MyAlarmAdapter extends BaseAdapter {
	private Context context;
	private List<String> items;
	private List<String> values;
	private List<String> date;
	private List<String> ids;
	private LayoutInflater inflater;
	//private ArrayList<AlarmTime> alarms;

	private DatabaseHelper mydb;
	public MyAlarmAdapter(Context context,
			List<String> items,List<String> values,List<String> date , List<String> ids){
		this.context = context;
		this.items = items;
		this.values = values;
		this.date = date;
		this.ids= ids;
		inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return items.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub

		//对自定义的listItem进行自定义
		//AlarmTime alarm = alarms.get(arg0);
		ViewHolder holder = new ViewHolder();
		if(arg1 == null){
			arg1 = inflater.inflate(R.layout.clock_item, null);
			holder.time = (TextView)arg1.findViewById(R.id.clockTime);
			holder.state =  (TextView)arg1.findViewById(R.id.state);
			holder.delBtn = (ImageButton)arg1.findViewById(R.id.deleteTime);
			arg1.setTag(holder);
		}else{
			holder = (ViewHolder)arg1.getTag();
		}
		holder.time.setText(date.get(arg0)+" " + values.get(arg0));
		holder.state.setText(items.get(arg0));
		holder.delBtn.setTag(arg0);
		//删除数据库中的闹铃，并依据闹铃的Id取消该闹铃
		holder.delBtn.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final int position = Integer.parseInt(v.getTag().toString());

				new AlertDialog.Builder(context)
				.setTitle("注意！")
				.setMessage("确定删除？")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						String id = ids.get(position);
						Log.v("Work",position+ "");
						Log.e("Work", "id is  "+ id);
						//依据Id取消闹铃

						
						Intent sendId = new Intent(context,AlarmService.class);
						sendId.setAction("DELID");
						sendId.putExtra("DELID", id);
						context.startService(sendId);
						

						//删除该闹铃
						mydb = new DatabaseHelper(context);
						mydb.deleteTime(id);
						Log.v("Work",position+ "");
						Log.v("Work", "Del " + id);
						//alarms.remove(position);
						ids.remove(position);
						items.remove(position);
						values.remove(position);
						Log.v("Work", "cancle the alarm");
						MyAlarmAdapter.this.notifyDataSetChanged();	
					}
				})
				.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).show();
			}
		});

		return arg1;

	}

	private class ViewHolder{
		private TextView time;
		private TextView state;
		private ImageButton delBtn;
	}


}
