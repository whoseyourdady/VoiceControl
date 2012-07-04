package com.scut.vc.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	private ListPreference mVoiceEngine;
	private ListPreference mSearchEngine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting);
		this.getWindow().setBackgroundDrawableResource(R.drawable.mywb);
		/*
		 * // 读取数据，用于方便测试 final SharedPreferences sp = PreferenceManager
		 * .getDefaultSharedPreferences(this);
		 * System.out.println("0000000000000"); String selectEngine =
		 * sp.getString("voiceEngine", "error~~");
		 * System.out.println("selectEngine = " + selectEngine);
		 * System.out.println("111111111111");
		 */

		// 获取选项控件
		mVoiceEngine = (ListPreference) getPreferenceScreen().findPreference(
				"voiceEngine");
		mSearchEngine = (ListPreference) getPreferenceScreen().findPreference(
				"searchEngine");
		// 初始显示时，显示对应的summary

		CharSequence entryVoice = mVoiceEngine.getEntry();
		mVoiceEngine.setSummary(entryVoice);
		System.out.println(entryVoice);

		CharSequence entrySearch = mSearchEngine.getEntry();
		mSearchEngine.setSummary(entrySearch);
		System.out.println(entrySearch);

		/*
		 * System.out.println(mVoiceEngine.getKey());//voiceEngine
		 * System.out.println(mVoiceEngine.getValue());//2
		 * System.out.println(mVoiceEngine.getTitle());//识别语音引擎
		 * System.out.println(mVoiceEngine.getEntry());//谷歌语音
		 */

		// 注册改变监听器
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	protected void onPause() {
		super.onPause();

		// 读取数据，并提交，用于其他activity进行数据的读取
		final SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		// 获取语音识别引擎选项的数据，并提交
		String voicetEngine = sp.getString("voiceEngine", "error~~"); // voiceEngine是控件的ID
		Editor sharedata_voice = getSharedPreferences("voiceEngine", 0).edit();
		sharedata_voice.putString("voiceEngine", voicetEngine);// voiceEngine是给外部activity的数据id
		sharedata_voice.commit();
		// 获取搜索引擎选项的数据，并提交
		String searchEngine = sp.getString("searchEngine", "error~~"); // searchEngine是控件的ID
		Editor sharedata_search = getSharedPreferences("searchEngine", 0)
				.edit();
		sharedata_search.putString("searchEngine", searchEngine);// searchEngine是给外部activity的数据id
		sharedata_search.commit();
		// 获取自启动识别选项的数据，并提交
		boolean startTurn = sp.getBoolean("startTurn", false); // startTurn是控件的ID
		Editor sharedata_start = getSharedPreferences("startTurn", 0)
				.edit();
		sharedata_start.putBoolean("startTurn", startTurn);// startTurn是给外部activity的数据id
		sharedata_start.commit();

		Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// TODO Auto-generated method stub
		if (key.equals("voiceEngine")) {
			CharSequence entry = mVoiceEngine.getEntry();
			mVoiceEngine.setSummary(entry);
			System.out.println(entry);
		}
		if (key.equals("searchEngine")) {
			CharSequence entry = mSearchEngine.getEntry();
			mSearchEngine.setSummary(entry);
			System.out.println(entry);
		}
	}
}
