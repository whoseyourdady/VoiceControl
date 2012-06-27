package com.scut.vc.alarm;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
/**
 * 激活闹铃
 * @author Administrator
 *
 */
public class CallAlarm extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {
	  /*
	   * 获取传过来的闹铃的id，再将之传送给AlarmAlert，使AlarmAlert能够根据
	   * 闹铃的id获取相对应的事件
	   */
	int id  = intent.getIntExtra("ID", -1);
    Intent i = new Intent(context, AlarmAlert.class);         
    Bundle bundleRet = new Bundle();
    bundleRet.putString("STR_CALLER", "");
    i.putExtras(bundleRet);
    i.putExtra("ID", id);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(i);
  }
}
