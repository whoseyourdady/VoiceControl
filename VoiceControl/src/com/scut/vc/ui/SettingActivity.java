package com.scut.vc.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.setting);

		/*// 读取数据，用于方便测试
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		System.out.println("0000000000000");
		String selectEngine = sp.getString("voiceEngine", "error~~");
		System.out.println("selectEngine = " + selectEngine);
		System.out.println("111111111111");*/

	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// 读取数据，并提交，用于其他activity进行数据的读取
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		//获取语音识别引擎选项的数据
		String voicetEngine = sp.getString("voiceEngine", "error~~"); // list1是控件的ID
		Editor sharedata_voice = getSharedPreferences("voiceEngine", 0).edit();
		sharedata_voice.putString("voiceEngine", voicetEngine);//voiceEngine是给外部activity的数据id
		sharedata_voice.commit();
		//获取搜索引擎选项的数据
		String searchEngine = sp.getString("searchEngine", "error~~"); // list1是控件的ID
		Editor sharedata_search = getSharedPreferences("searchEngine", 0).edit();
		sharedata_search.putString("searchEngine", searchEngine);//voiceEngine是给外部activity的数据id
		sharedata_search.commit();

		Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
	}
}
